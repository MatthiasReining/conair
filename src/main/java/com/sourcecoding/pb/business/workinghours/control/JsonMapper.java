/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sourcecoding.pb.business.workinghours.control;

import com.sourcecoding.pb.business.project.entity.ProjectInformation;
import com.sourcecoding.pb.business.project.entity.WorkPackage;
import com.sourcecoding.pb.business.user.entity.Individual;
import com.sourcecoding.pb.business.workinghours.entity.WorkPackageDescription;
import com.sourcecoding.pb.business.workinghours.entity.WorkingDay;
import com.sourcecoding.pb.business.workinghours.entity.WorkingTime;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import org.codehaus.jackson.map.ObjectMapper;

/**
 *
 * @author Matthias
 */
public class JsonMapper {

    @PersistenceContext
    EntityManager em;

    public Object buildWorkingHoursEntities(Map<String, Object> data) {


        Long userId = ((Integer) data.get("userId")).longValue();
        System.out.println("userId: " + userId);
        Individual individual = em.find(Individual.class, userId);
        

        Map<String, Map<String, Object>> workingDayMap = (Map<String, Map<String, Object>>) data.get("workingDayMap");
        System.out.println("workingDayMap: " + workingDayMap);

        Map<String, Object> workPackageDescriptionMap = (Map<String, Object>) data.get("workPackageDescription");
        System.out.println("workPackageDescription: " + workPackageDescriptionMap);

        Map<String, WorkPackageDescription> workPackageDescriptionMapByJsonId = createWorkPackageList(workPackageDescriptionMap);
        System.out.println(workPackageDescriptionMapByJsonId);


        createOrUpdateWorkingTime(workingDayMap, individual, workPackageDescriptionMapByJsonId);

        return null;

    }

    public String buildWorkingTimePackage(List<WorkingDay> workingDayList) {

        Map<String, Object> content = new HashMap<>();

        Map<Long, Map<String, Object>> outputWorkPackageDescription = new HashMap<>();
        Map<Long, Map<String, Object>> outputWorkPackage = new HashMap<>();
        Map<Long, Map<String, Object>> outputProjectInformation = new HashMap<>();

        Map<String, Map<String, Object>> outputWorkingDay = new HashMap<>();


        for (WorkingDay wd : workingDayList) {

            Map<String, Object> data;
            Map<String, Object> wdData = new HashMap<>();

            String workingDay = wd.getWorkingDay().toString();

            wdData.put("date", workingDay);
            wdData.put("state", wd.getStatus());
            wdData.put("userId", wd.getUser().getId());
            wdData.put("workingTimeList", new ArrayList<Map<String, Object>>());
            outputWorkingDay.put(workingDay, wdData);

            for (WorkingTime wt : wd.getWorkingTimeList()) {

                data = new HashMap<>();
                data.put("workingTime", wt.getWorkingTime());
                data.put("wpDescriptionId", wt.getWorkPackageDescription().getId());

                List<Map<String, Object>> wtList = (List<Map<String, Object>>) wdData.get("workingTimeList");
                wtList.add(data);

                WorkPackageDescription wpDescr = wt.getWorkPackageDescription();
                if (!outputWorkPackageDescription.containsKey(wpDescr.getId())) {
                    data = new HashMap<>();
                    data.put("id", wpDescr.getId());
                    data.put("description", wpDescr.getDescription());
                    data.put("wpId", wpDescr.getWorkPackage().getId());
                    outputWorkPackageDescription.put(wpDescr.getId(), data);
                }

                WorkPackage wp = wpDescr.getWorkPackage();
                if (!outputWorkPackage.containsKey(wp.getId())) {
                    data = new HashMap<>();
                    data.put("id", wp.getId());
                    data.put("name", wp.getWpName());
                    data.put("projectId", wp.getProjectInformation().getId());
                    outputWorkPackage.put(wp.getId(), data);
                }

                ProjectInformation pi = wp.getProjectInformation();
                if (!outputWorkPackage.containsKey(pi.getId())) {
                    data = new HashMap<>();
                    data.put("id", pi.getId());
                    data.put("key", pi.getProjectKey());
                    data.put("name", pi.getName());
                    outputProjectInformation.put(pi.getId(), data);
                }
            }

        }

        content.put("workPackageDescription", outputWorkPackageDescription);
        content.put("workPackage", outputWorkPackage);
        content.put("projectInformation", outputProjectInformation);

        content.put("workDayList", outputWorkingDay);


        ObjectMapper mapper = new ObjectMapper();

        try {
            return mapper.writeValueAsString(content);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<String, WorkPackageDescription> createWorkPackageList(Map<String, Object> workPackageDescriptionMap) {
        Map<String, WorkPackageDescription> wpList2merge = new HashMap<>();

        for (Map.Entry<String, Object> wp : workPackageDescriptionMap.entrySet()) {
            Map<String, Object> descrObj = (Map<String, Object>) wp.getValue();

            String descrId = wp.getKey();

            String description = (String) descrObj.get("description");
            Long workPackageId = ((Integer) descrObj.get("workPackageId")).longValue();

            WorkPackage workPackage = em.find(WorkPackage.class, workPackageId);
            if (workPackage == null) {
                throw new RuntimeException("There is no work package with id: " + workPackageId);
            }

            WorkPackageDescription wpDescr;
            List<WorkPackageDescription> wpDescrList = em.createNamedQuery(WorkPackageDescription.findByWorkPackageAndDescription, WorkPackageDescription.class)
                    .setParameter(WorkPackageDescription.findByWorkPackageAndDescription_Param_WorkPackage, workPackage)
                    .setParameter(WorkPackageDescription.findByWorkPackageAndDescription_Param_Description, description)
                    .getResultList();
            if (wpDescrList == null || wpDescrList.isEmpty()) {
                //create new one
                wpDescr = new WorkPackageDescription();
                wpDescr.setDescription(description);
                wpDescr.setWorkPackage(workPackage);
            } else {
                wpDescr = wpDescrList.get(0);
            }
            wpDescr = em.merge(wpDescr);
            wpList2merge.put(descrId, wpDescr);

        }

        return wpList2merge;
    }

    private void createOrUpdateWorkingTime(Map<String, Map<String, Object>> workingDayJsonMap, Individual individual, Map<String, WorkPackageDescription> workPackageDescriptionMapByJsonId) {

        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");

        Map<Long, WorkPackageDescription> workPackageDescriptionByEntityId = new HashMap<>();
        for (WorkPackageDescription wp : workPackageDescriptionMapByJsonId.values()) {
            workPackageDescriptionByEntityId.put(wp.getId(), wp);
        }
        
        System.out.println(workingDayJsonMap);
        System.out.println("----");
        System.out.println(workPackageDescriptionByEntityId);
        System.out.println("----");
        System.out.println(workPackageDescriptionMapByJsonId);


        for (Map.Entry<String, Map<String, Object>> workingDayJsonEntry : workingDayJsonMap.entrySet()) {
            System.out.println(workingDayJsonEntry);

            String dateText = workingDayJsonEntry.getKey();
            //TODO state is ignored at the moment: String state = (String)workingDayMap.get("state");
            int state = 0;
            Map<String, Integer> workingTimeMapByJsonWPDescrId = (Map<String, Integer>) workingDayJsonEntry.getValue().get("workingTimeByDescriptionId");
            
            System.out.println("dateText: " + dateText);
            System.out.println("workingTimeMapByJsonWPDescrId: "+ workingTimeMapByJsonWPDescrId);
            
            Map<Long, Integer> workingTimeMapByEntityWPDescrId = new HashMap<>();
            for (Map.Entry<String, Integer> entry : workingTimeMapByJsonWPDescrId.entrySet()) {
                Integer workingTime = entry.getValue();
                String jsonWPDescrId = entry.getKey();
                
                WorkPackageDescription wpd = workPackageDescriptionMapByJsonId.get(jsonWPDescrId);
                workingTimeMapByEntityWPDescrId.put(wpd.getId(), workingTime);
            }
            
            Date workingDate;
            try {
                workingDate = dateFormatter.parse(dateText);
            } catch (ParseException ex) {
                throw new RuntimeException(ex);
            }

            WorkingDay workingDay;
            try {
                workingDay = em.createNamedQuery(WorkingDay.findWorkingDayForUser, WorkingDay.class)
                        .setParameter(WorkingDay.queryParam_user, individual)
                        .setParameter(WorkingDay.queryParam_date, workingDate)
                        .getSingleResult();
            } catch (NoResultException e) {
                workingDay = new WorkingDay();
                workingDay.setUser(individual);
                workingDay.setWorkingDay(workingDate);
            }    
            
            workingDay.setStatus(state);

            workingDay = em.merge(workingDay);

            List<WorkingTime> wtEntityList = workingDay.getWorkingTimeList();
            if (wtEntityList == null) {
                wtEntityList = new ArrayList();
                workingDay.setWorkingTimeList(wtEntityList);
            }

            //update and remove working Time
            //iteratate overall existing time entries
            List<Long> existingWorkingTimeByWPDescrId = new ArrayList<>();
            List<WorkingTime> wt2Remove = new ArrayList<>();
            for (WorkingTime wt : wtEntityList) {
                existingWorkingTimeByWPDescrId.add(wt.getWorkPackageDescription().getId());
                Integer workingTime = workingTimeMapByEntityWPDescrId.get(wt.getWorkPackageDescription().getId());
                
                if (workingTime == null) { //no new one definied - remove existing
                    System.out.println("remove " + dateText + " -> wpid: " + wt.getWorkPackageDescription().getId() + ";time: " + workingTime);
                    em.remove(wt);
                    wt2Remove.add(wt);
                    continue;
                }
                
                if (wt.getWorkingTime().equals(workingTime)) //nothing changed - do nothing
                    continue;
                
                //time has changed - update time
                wt.setWorkingTime(workingTime);
                System.out.println("update " + dateText + " -> wpid: " + wt.getWorkPackageDescription().getId() + ";new-time: " + workingTime);
            }
            wtEntityList.removeAll(wt2Remove);
            
            //create new one
            for (Map.Entry<Long, Integer> entry : workingTimeMapByEntityWPDescrId.entrySet()) {

                if (existingWorkingTimeByWPDescrId.contains(entry.getKey()))
                    continue;

                WorkingTime newWt = new WorkingTime();
                newWt.setWorkingDay(workingDay);
                newWt.setWorkingTime(entry.getValue());
                newWt.setWorkPackageDescription(workPackageDescriptionByEntityId.get(entry.getKey()));

                workingDay.getWorkingTimeList().add(newWt);

                //em.merge(newWt);

            }
        }
    }
}
