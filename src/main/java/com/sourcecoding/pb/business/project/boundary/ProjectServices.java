/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sourcecoding.pb.business.project.boundary;

import com.sourcecoding.pb.business.project.entity.ProjectInformation;
import com.sourcecoding.pb.business.project.entity.ProjectMember;
import com.sourcecoding.pb.business.project.entity.WorkPackage;
import com.sourcecoding.pb.business.restconfig.DateParameter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author Matthias
 */
@Path("projects")
@Stateless
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProjectServices {

    @PersistenceContext
    EntityManager em;

    @PUT
    @Path("{key}")
    public Map<String, Object> update(@PathParam("key") String key, Map<String, Object> map) {
        System.out.println("in upate...");

        Long id = null;
        if (map.containsKey("id")) {
            id = Long.valueOf(String.valueOf(map.get("id")));
        }

        ProjectInformation pi;
        if (id == null) {
            pi = new ProjectInformation();
        } else {
            pi = em.find(ProjectInformation.class, id);
        }

        //ProjectInformation pi = em.createNamedQuery(ProjectInformation.findByKey, ProjectInformation.class)
        //        .setParameter(ProjectInformation.findByKey_Param_Key, key)
        //        .getSingleResult();
        pi.setProjectKey(String.valueOf(map.get("projectKey")));
        pi.setName(String.valueOf(map.get("name")));
        pi.setProjectManager(String.valueOf(map.get("projectManager")));

        //projectStart
        String dateText = String.valueOf(map.get("projectStart"));
        Date dateValue = (dateText == null || dateText.isEmpty() || "null".equals(dateText)) ? null : DateParameter.valueOf(dateText);
        pi.setProjectStart(dateValue);

        //projectEnd
        dateText = String.valueOf(map.get("projectEnd"));
        dateValue = (dateText == null || dateText.isEmpty() || "null".equals(dateText)) ? null : DateParameter.valueOf(dateText);
        pi.setProjectEnd(dateValue);

        if (pi.getWorkPackages() == null) {
            pi.setWorkPackages(new HashSet<WorkPackage>());
        }
        Set<WorkPackage> wpEntityList = pi.getWorkPackages();
        Map<Long, WorkPackage> wpEntityMap = new HashMap<>();
        for (WorkPackage wpEntity : pi.getWorkPackages()) {
            wpEntityMap.put(wpEntity.getId(), wpEntity);
        }

        List<Long> newWpIdExistingList = new ArrayList<>();
        List<Map<String, Object>> wpList = (List<Map<String, Object>>) map.get("workPackages");
        if (wpList != null) {
            for (Map<String, Object> wp : wpList) {

                WorkPackage wpEntity;

                if (wp.containsKey("id")) {
                    Long wpId = Long.valueOf(String.valueOf(wp.get("id")));
                    newWpIdExistingList.add(wpId);
                    wpEntity = wpEntityMap.get(wpId);
                } else {
                    wpEntity = new WorkPackage();
                    pi.getWorkPackages().add(wpEntity);
                }
                wpEntity.setProjectInformation(pi);
                wpEntity.setWpName(String.valueOf(wp.get("wpName")));
                //TODO add wpEntity.setLimitForWorkingHours(...);
            }
        }

        //remove entities
        for (Map.Entry<Long, WorkPackage> entries : wpEntityMap.entrySet()) {
            Long existingId = entries.getKey();

            if (!newWpIdExistingList.contains(existingId)) {
                //entry has to be removed
                WorkPackage wp2Remove = entries.getValue();
                pi.getWorkPackages().remove(wp2Remove);
            }
        }

        pi = em.merge(pi);
        return convertProjectInformation2Map(pi);
    }

    @GET
    @Path("{key}")
    public Map<String, Object> getProject(@PathParam("key") String key) {
        ProjectInformation pi = em.createNamedQuery(ProjectInformation.findByKey, ProjectInformation.class)
                .setParameter(ProjectInformation.findByKey_Param_Key, key)
                .getSingleResult();
        return convertProjectInformation2Map(pi);
    }

    @GET
    public Map<String, Object> getProjectList() {

        List<ProjectInformation> projects = em.createNamedQuery(ProjectInformation.findAllValidProjects, ProjectInformation.class)
                .getResultList();

        Map<String, Object> result = new HashMap<>();
        for (ProjectInformation projectEntity : projects) {
            Map<String, Object> project = new HashMap<>();

            project.put("id", projectEntity.getId());
            project.put("name", projectEntity.getName());
            project.put("key", projectEntity.getProjectKey());

            result.put(String.valueOf(projectEntity.getId()), project);

            Map<String, Map> workPackages = new HashMap<>();
            project.put("workPackages", workPackages);
            for (WorkPackage wpEntity : projectEntity.getWorkPackages()) {
                Map<String, Object> wp = new HashMap<>();
                System.out.println(wpEntity.getWpName());
                workPackages.put(String.valueOf(wpEntity.getId()), wp);
                //XXX maybe a key will be added
                wp.put("id", wpEntity.getId());
                wp.put("name", wpEntity.getWpName());
            }
        }
        return result;
    }

    private Map<String, Object> convertProjectInformation2Map(ProjectInformation pi) {
        Map<String, Object> result = new HashMap<>();
        result.put("projectKey", pi.getProjectKey());
        result.put("name", pi.getName());
        result.put("id", pi.getId());
        result.put("projectStart", DateParameter.valueOf(pi.getProjectStart()));
        result.put("projectEnd", DateParameter.valueOf(pi.getProjectEnd()));
        result.put("projectManager", pi.getProjectManager());

        if (pi.getWorkPackages() != null) {
            List<Map<String, Object>> workPackages = new ArrayList<>();
            result.put("workPackages", workPackages);

            for (WorkPackage wp : pi.getWorkPackages()) {
                Map<String, Object> workPackage = new HashMap<>();
                workPackages.add(workPackage);
                workPackage.put("id", wp.getId());
                workPackage.put("wpName", wp.getWpName());
                workPackage.put("bookableFrom", wp.getBookabelFrom());
                workPackage.put("bookableTo", wp.getBookableTo());
                workPackage.put("limitForWorkingHours", wp.getLimitForWorkingHours());
            }
        }
        
        if (pi.getMembers() != null) {
            List<Map<String, Object>> members = new ArrayList<>();
            result.put("members", members);

            for (ProjectMember pm : pi.getMembers()) {
                Map<String, Object> member = new HashMap<>();
                members.add(member);
                member.put("id", pm.getId());
                member.put("individual-nickname", pm.getIndividual().getNickname());
                member.put("individual-id", pm.getIndividual().getId());
                member.put("memberFrom", DateParameter.valueOf(pm.getMemberFrom()));
                member.put("memberTo", DateParameter.valueOf(pm.getMemberTo()));
                member.put("priceHour", pm.getPriceHour());
            }
        }

        return result;
    }
}
