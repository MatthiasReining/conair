/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sourcecoding.pb.business;

import com.sourcecoding.pb.business.project.entity.ProjectInformation;
import com.sourcecoding.pb.business.project.entity.WorkPackage;
import com.sourcecoding.pb.business.user.entity.Individual;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;
import java.util.HashSet;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Matthias
 */
public class InitalizeSystem {

    private static final String REST_ROOT = "http://localhost:8080/project-business-time-recording/rest";
    private WebResource webResource;

    @Before
    public void init() {
        ClientConfig clientConfig = new DefaultClientConfig();
        clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);

        Client client = Client.create(clientConfig);
        webResource = client.resource(REST_ROOT);

    }

    @Test
    public void run() {

        createIndividual("Mike");
        createIndividual("Susan");
        createIndividual("Lynette");
        createIndividual("Tom");

        createProject("E227", "DirektLine Deutschland", "Analyse", "Entwicklung");
        createProject("E118", "VIG", "Sprint 1", "Sprint 2", "Sprint 3", "Sprint 4", "Sprint 5", "Sprint 6");

        Individual tom = getIndividual("Tom");
        ProjectInformation e227 = getProject("E227");
        WorkPackage e227Analyse = e227.getWorkPackages().toArray(new WorkPackage[]{})[0];
        WorkPackage e227Entwicklung = e227.getWorkPackages().toArray(new WorkPackage[]{})[1];


//        createTimeRecording(tom, e227, e227Analyse, "doku gelesen", "2012-04-09", 120);
//        createTimeRecording(tom, e227, e227Analyse, "doku gelesen", "2012-04-10", 480);
//        createTimeRecording(tom, e227, e227Analyse, "doku gelesen", "2012-04-11", 120);
//        createTimeRecording(tom, e227, e227Analyse, "doku angefordert und gelesen", "2012-04-12", 360);
//        createTimeRecording(tom, e227, e227Entwicklung, "killer app entwickelt", "2012-04-09", 240);
//        createTimeRecording(tom, e227, e227Entwicklung, "killer app entwickelt", "2012-04-10", 120);
//        createTimeRecording(tom, e227, e227Entwicklung, "killer app entwickelt", "2012-04-11", 330);

    }

    protected Individual createIndividual(String nickname) throws UniformInterfaceException, ClientHandlerException {
        ClientResponse cr = webResource.path("individuals")
                .path(nickname)
                .type("application/json")
                .accept("application/json")
                .put(ClientResponse.class);

        Individual individual = cr.getEntity(Individual.class);
        System.out.println("get individual: " + individual.getId());
        return individual;
    }

    protected Individual getIndividual(String nickname) throws UniformInterfaceException, ClientHandlerException {
        ClientResponse cr = webResource.path("individuals")
                .path(nickname)
                .type("application/json")
                .accept("application/json")
                .get(ClientResponse.class);

        Individual individual = cr.getEntity(Individual.class);
        System.out.println("created individual: " + individual.getId());
        return individual;

    }

    private ProjectInformation createProject(String projectKey, String projectName, String... workPackages) throws ClientHandlerException, UniformInterfaceException {
        ProjectInformation pi = new ProjectInformation();
        pi.setProjectKey(projectKey);
        pi.setName(projectName);

        Set<WorkPackage> s = new HashSet<>();

        pi.setWorkPackages(s);
        for (String workPackage : workPackages) {
            WorkPackage wp = new WorkPackage();
            wp.setProjectInformation(pi);
            wp.setWpName(workPackage);
            s.add(wp);
        }

        ClientResponse cr = webResource.path("projects")
                .path(pi.getProjectKey())
                .type("application/json")
                .accept("application/json")
                .put(ClientResponse.class, pi);

        ProjectInformation piResult = cr.getEntity(ProjectInformation.class);
        System.out.println("create project: " + piResult.getProjectKey() + " " + piResult.getName());
        return piResult;
    }

    private ProjectInformation getProject(String projectKey) throws ClientHandlerException, UniformInterfaceException {

        ClientResponse cr = webResource.path("projects")
                .path(projectKey)
                .type("application/json")
                .accept("application/json")
                .get(ClientResponse.class);

        ProjectInformation piResult = cr.getEntity(ProjectInformation.class);
        System.out.println("get project: " + piResult.getProjectKey() + " " + piResult.getName());
        return piResult;
    }

    private void createTimeRecording(Individual individual, ProjectInformation projectInformation, WorkPackage workingPackage,
            String date, String description, int workingTime) {


        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

   
}