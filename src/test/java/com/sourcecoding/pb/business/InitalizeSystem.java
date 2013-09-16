/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sourcecoding.pb.business;

import com.sourcecoding.pb.business.project.entity.ProjectInformation;
import com.sourcecoding.pb.business.project.entity.WorkPackage;
import com.sourcecoding.pb.business.individuals.entity.Individual;
import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author Matthias
 */
@Ignore
public class InitalizeSystem {

    private static final String REST_ROOT = "http://localhost:8080/conair/rest";
    private WebTarget base;

    @Before
    public void init() {

        Client client = ClientBuilder.newClient();
        base = client.target(REST_ROOT);


        //clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);

    }

    @Test
    public void run() {

        createIndividual("Pierre", "M0vVQEOr07");
        createIndividual("Hans", "35CXEuiaFs");
        createIndividual("Matthias", "P1ExxuV6JL");

        createProject("E227", "DirektLine Deutschland", "Analyse", "Entwicklung");
        createProject("E118", "VIG", "Sprint 1", "Sprint 2", "Sprint 3", "Sprint 4", "Sprint 5", "Sprint 6");

        Individual tom = getIndividual("Matthias");
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

    protected Individual createIndividual(String nickname, String linkedInId) {

        Response response = base.path("individuals")
                .path(nickname)
                .queryParam("linkedInId", linkedInId)
                .request(MediaType.APPLICATION_JSON)
                .put(null, Response.class);


        Individual individual = response.readEntity(Individual.class);
        System.out.println("get individual: " + individual.getId());
        return individual;
    }

    protected Individual getIndividual(String nickname)  {
        Response cr = base.path("individuals")
                .path(nickname)
                .request(MediaType.APPLICATION_JSON)
                .get();

        Individual individual = cr.readEntity(Individual.class);
        System.out.println("created individual: " + individual.getId());
        return individual;

    }

    private ProjectInformation createProject(String projectKey, String projectName, String... workPackages) {
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

        Response cr = base.path("projects/v0.1")
                .path(pi.getProjectKey())
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.json(pi), Response.class);

        ProjectInformation piResult = cr.readEntity(ProjectInformation.class);
        System.out.println("create project: " + piResult.getProjectKey() + " " + piResult.getName());
        return piResult;
    }

    private ProjectInformation getProject(String projectKey)  {

        Response cr = base.path("projects")
                .path(projectKey)
                .request(MediaType.APPLICATION_JSON)
                .get();

        ProjectInformation piResult = cr.readEntity(ProjectInformation.class);
        System.out.println("get project: " + piResult.getProjectKey() + " " + piResult.getName());
        return piResult;
    }

    private void createTimeRecording(Individual individual, ProjectInformation projectInformation, WorkPackage workingPackage,
            String date, String description, int workingTime) {


        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}