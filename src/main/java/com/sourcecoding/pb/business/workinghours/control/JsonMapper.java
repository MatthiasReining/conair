/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sourcecoding.pb.business.workinghours.control;

import com.sourcecoding.pb.business.project.entity.ProjectInformation;
import com.sourcecoding.pb.business.project.entity.WorkPackage;
import com.sourcecoding.pb.business.workinghours.entity.WorkPackageDescription;
import com.sourcecoding.pb.business.workinghours.entity.WorkingDay;
import com.sourcecoding.pb.business.workinghours.entity.WorkingTime;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
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

        List<Map<String, Object>> workingDayList = (List<Map<String, Object>>) data.get("workingDayList");
        System.out.println("workingDayList: " + workingDayList);

        Map<String, Object> workPackageDescriptionMap = (Map<String, Object>) data.get("workPackageDescription");
        System.out.println("workPackageDescription: " + workPackageDescriptionMap);

        Map<String, WorkPackageDescription> workPackageList = createWorkPackageList(workPackageDescriptionMap);
        System.out.println(workPackageList);


        for (Map<String, Object> workingDay : workingDayList) {
            System.out.println(workingDay);
        }

//            JSONArray workingDays = json.names();
//            for (int i=0; i<workingDays.length(); i++) {
//                JSONObject workingDay = workingDays.getJSONObject(i);
//                
//            }

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
            wpList2merge.put(descrId, wpDescr);

        }

        return wpList2merge;
    }
}
