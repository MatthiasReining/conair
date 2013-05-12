/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sourcecoding.pb.business.project.boundary;

import com.sourcecoding.pb.business.project.entity.ProjectInformation;
import com.sourcecoding.pb.business.project.entity.WorkPackage;
import com.sourcecoding.pb.business.restconfig.DateParameter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import javax.ws.rs.core.Response;

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
    @Path("v0.1/{key}")
    public ProjectInformation createOrUpdate(@PathParam("key") String key, ProjectInformation pi) {

        pi = em.merge(pi);
        return pi;
    }

    @GET
    @Path("v0.1/{key}")
    public ProjectInformation getProjectv2(@PathParam("key") String key) {
        return em.createNamedQuery(ProjectInformation.findByKey, ProjectInformation.class)
                .setParameter(ProjectInformation.findByKey_Param_Key, key)
                .getSingleResult();
    }

    @PUT
    @Path("{key}")
    public Map<String, Object> update(@PathParam("key") String key, Map<String, Object> map) {

        ProjectInformation pi = em.createNamedQuery(ProjectInformation.findByKey, ProjectInformation.class)
                .setParameter(ProjectInformation.findByKey_Param_Key, key)
                .getSingleResult();
        pi.setName(String.valueOf(map.get("name")));
        pi.setProjectManager(String.valueOf(map.get("projectManager")));

        //projectStart
        String dateText = String.valueOf(map.get("projectStart"));
        Date dateValue = (dateText == null || dateText.isEmpty()) ? null : DateParameter.valueOf(dateText);
        pi.setProjectStart(dateValue);

        //projectEnd
        dateText = String.valueOf(map.get("projectEnd"));
        dateValue = (dateText == null || dateText.isEmpty()) ? null : DateParameter.valueOf(dateText);
        pi.setProjectEnd(dateValue);


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
    public Map<String, Object> getProjects() {

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

    /**
     * used for project-list.html
     *
     * @return
     */
    @GET
    @Path("list")
    public List<Object> getProjectList() {

        List<ProjectInformation> projects = em.createNamedQuery(ProjectInformation.findAllValidProjects, ProjectInformation.class)
                .getResultList();

        List<Object> result = new ArrayList<>();

        for (ProjectInformation projectEntity : projects) {
            Map<String, Object> project = new HashMap<>();

            project.put("id", projectEntity.getId());
            project.put("name", projectEntity.getName());
            project.put("key", projectEntity.getProjectKey());
            result.add(project);
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
        return result;
    }
}
