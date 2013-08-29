/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sourcecoding.pb.business.workinghours.control;

import com.sourcecoding.pb.business.authentication.entity.User;
import com.sourcecoding.pb.business.project.entity.ProjectInformation;
import com.sourcecoding.pb.business.project.entity.WorkPackage;
import com.sourcecoding.pb.business.restconfig.DateParameter;
import com.sourcecoding.pb.business.individuals.entity.Individual;
import com.sourcecoding.pb.business.workinghours.entity.WorkPackageDescription;
import com.sourcecoding.pb.business.workinghours.entity.WorkingDay;
import com.sourcecoding.pb.business.workinghours.entity.WorkingTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Matthias
 */
public class JsonMapPersister {

    @PersistenceContext
    EntityManager em;

    public void update(Map<String, Object> data) {

        Long userId = ((Integer) data.get("userId")).longValue();
        Individual individual = em.find(Individual.class, userId);


        Map<String, Map<String, Object>> workingDayMapByDay = (Map<String, Map<String, Object>>) data.get("workingDayMap");
        Map<String, Object> workPackageDescriptionMapByJsonId = (Map<String, Object>) data.get("workPackageDescription");


        Map<String, WorkPackageDescription> workPackageDescriptionEntiyMapByJsonId = createWorkPackageDescriptionEntityMap(workPackageDescriptionMapByJsonId);

        createOrUpdateWorkingTime(individual, workingDayMapByDay, workPackageDescriptionEntiyMapByJsonId);

    }

    public Map<String, Object> buildWorkingTimePackage(List<WorkingDay> workingDayList, Date fromDate, Date untilDate, Long individualId) {

        Map<String, Object> content = new HashMap<>();

        //uaerId
        content.put("userId", individualId);

        //date range        
        Map<String, Integer> wdr = new HashMap<>();
        content.put("workingDayRange", wdr);
        Calendar from = Calendar.getInstance();
        from.setTime(fromDate);
        Calendar until = Calendar.getInstance();
        until.setTime(untilDate);
        while (from.before(until)) {
            wdr.put(DateParameter.valueOf(from), 0);
            from.add(Calendar.DATE, 1);
        }

        //workingHours        
        List<Map<String, Object>> workingHours = new ArrayList<>();
        content.put("workingHours", workingHours);

        Map<WorkPackageDescription, Map<String, Integer>> daysByWpd = new HashMap<>();

        for (WorkingDay wd : workingDayList) {
            String workingDay = DateParameter.valueOf(wd.getWorkingDay());

            //set working day status
            wdr.put(workingDay, wd.getStatus());

            for (WorkingTime wt : wd.getWorkingTimeList()) {
                Long wpdId = wt.getWorkPackageDescription().getId();

                if (!daysByWpd.containsKey(wt.getWorkPackageDescription())) {
                    daysByWpd.put(wt.getWorkPackageDescription(), new HashMap<String, Integer>());
                }
                Map<String, Integer> days = daysByWpd.get(wt.getWorkPackageDescription());
                days.put(workingDay, wt.getWorkingTime());
            }
        }

        for (Map.Entry<WorkPackageDescription, Map<String, Integer>> entry : daysByWpd.entrySet()) {
            WorkPackageDescription wpd = entry.getKey();
            Map<String, Object> workingHour = new HashMap<>();
            workingHour.put("projectId", wpd.getWorkPackage().getProjectInformation().getId());
            workingHour.put("workPackageId", wpd.getWorkPackage().getId());
            workingHour.put("description", wpd.getDescription());
            workingHour.put("days", entry.getValue());

            workingHours.add(workingHour);
        }

        return content;
    }

    private Map<String, WorkPackageDescription> createWorkPackageDescriptionEntityMap(Map<String, Object> workPackageDescriptionMap) {
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

    private void createOrUpdateWorkingTime(Individual individual,
            Map<String, Map<String, Object>> workingDayMapByDay,
            Map<String, WorkPackageDescription> workPackageDescriptionEntiyMapByJsonId) {


        Map<Long, WorkPackageDescription> workPackageDescriptionByEntityId = new HashMap<>();
        for (WorkPackageDescription wp : workPackageDescriptionEntiyMapByJsonId.values()) {
            workPackageDescriptionByEntityId.put(wp.getId(), wp);
        }


        for (Map.Entry<String, Map<String, Object>> workingDayJsonEntry : workingDayMapByDay.entrySet()) {
            String dateText = workingDayJsonEntry.getKey();
            //TODO state is ignored at the moment: String state = (String)workingDayMap.get("state");
            int state = 0;
            Map<String, Integer> workingTimeMapByJsonWPDescrId = (Map<String, Integer>) workingDayJsonEntry.getValue().get("workingTimeByDescriptionId");
            Map<Long, Integer> workingTimeMapByEntityWPDescrId = buildWorkingTimeMapByEntityWPDescrId(workingTimeMapByJsonWPDescrId, workPackageDescriptionEntiyMapByJsonId);

            Date workingDate = DateParameter.valueOf(dateText);

            System.out.println("wtTimeDebug: " + workingTimeMapByJsonWPDescrId);
            System.out.println("wtTimeDebug1: " + workingTimeMapByJsonWPDescrId.size());

            WorkingDay workingDay;
            try {
                workingDay = em.createNamedQuery(WorkingDay.findWorkingDayForUser, WorkingDay.class)
                        .setParameter(WorkingDay.queryParam_user, individual)
                        .setParameter(WorkingDay.queryParam_date, workingDate)
                        .getSingleResult();
            } catch (NoResultException e) {
                //if no dataset exists and no new one is available -> continue
                if (workingTimeMapByJsonWPDescrId.isEmpty()) {
                    continue;
                }
                workingDay = new WorkingDay();
                workingDay.setUser(individual);
                workingDay.setWorkingDay(workingDate);
            }

            workingDay.setStatus(state);
            workingDay = em.merge(workingDay); //cascade.ALL - so WorkingTime has not to be saved separately

            List<Long> existingWorkingTimeByWPDescrId = updateAndRemoveExistingWorkingTimeEntities(workingDay.getWorkingTimeList(), workingTimeMapByEntityWPDescrId);

            createWorkingTimeEntities(workingTimeMapByEntityWPDescrId, existingWorkingTimeByWPDescrId, workingDay, workPackageDescriptionByEntityId);
        }
    }

    private Map<Long, Integer> buildWorkingTimeMapByEntityWPDescrId(Map<String, Integer> workingTimeMapByJsonWPDescrId, Map<String, WorkPackageDescription> workPackageDescriptionEntiyMapByJsonId) {
        Map<Long, Integer> workingTimeMapByEntityWPDescrId = new HashMap<>();
        for (Map.Entry<String, Integer> entry : workingTimeMapByJsonWPDescrId.entrySet()) {
            Integer workingTime = entry.getValue();
            String jsonWPDescrId = entry.getKey();

            WorkPackageDescription wpd = workPackageDescriptionEntiyMapByJsonId.get(jsonWPDescrId);
            workingTimeMapByEntityWPDescrId.put(wpd.getId(), workingTime);
        }
        return workingTimeMapByEntityWPDescrId;
    }

    private List<Long> updateAndRemoveExistingWorkingTimeEntities(List<WorkingTime> wtEntityList, Map<Long, Integer> workingTimeMapByEntityWPDescrId) {
        //update and remove working Time
        //iteratate overall existing time entries
        List<Long> existingWorkingTimeByWPDescrId = new ArrayList<>();
        List<WorkingTime> wt2Remove = new ArrayList<>();
        for (WorkingTime wt : wtEntityList) {
            existingWorkingTimeByWPDescrId.add(wt.getWorkPackageDescription().getId());
            Integer workingTime = workingTimeMapByEntityWPDescrId.get(wt.getWorkPackageDescription().getId());

            if (workingTime == null) { //no new one definied - remove existing
                em.remove(wt);
                wt2Remove.add(wt);
                continue;
            }

            if (wt.getWorkingTime().equals(workingTime)) //nothing changed - do nothing
            {
                continue;
            }

            //time has changed - update time
            wt.setWorkingTime(workingTime);
        }
        wtEntityList.removeAll(wt2Remove);
        return existingWorkingTimeByWPDescrId;
    }

    private void createWorkingTimeEntities(Map<Long, Integer> workingTimeMapByEntityWPDescrId, List<Long> existingWorkingTimeByWPDescrId, WorkingDay workingDay, Map<Long, WorkPackageDescription> workPackageDescriptionByEntityId) {
        //create new one
        for (Map.Entry<Long, Integer> entry : workingTimeMapByEntityWPDescrId.entrySet()) {

            if (existingWorkingTimeByWPDescrId.contains(entry.getKey())) {
                continue;
            }

            WorkingTime newWt = new WorkingTime();
            newWt.setWorkingDay(workingDay);
            newWt.setWorkingTime(entry.getValue());
            newWt.setWorkPackageDescription(workPackageDescriptionByEntityId.get(entry.getKey()));
            newWt.setWorkPackage(workPackageDescriptionByEntityId.get(entry.getKey()).getWorkPackage());
            workingDay.getWorkingTimeList().add(newWt);

            //em.merge(newWt);

        }
    }
}
