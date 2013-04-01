/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sourcecoding.pb.business;

import com.sourcecoding.pb.business.project.entity.ProjectInformation;
import com.sourcecoding.pb.business.project.entity.WorkPackage;
import com.sourcecoding.pb.business.timerecording.entity.TimeRecordingDTO;
import com.sourcecoding.pb.business.timerecording.entity.TimeRecordingRowDTO;
import com.sourcecoding.pb.business.timerecording.entity.TimeRecordingRowValueDTO;
import com.sourcecoding.pb.business.user.entity.Individual;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
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

        Individual individual = createIndividual("MickeyMouse2");
        ProjectInformation projectInfo = createProject();

        createTimeRecording(individual, projectInfo);

        TimeRecordingDTO tr = search(individual.getId());

        for (TimeRecordingRowDTO row : tr.getTimeRecordingRow()) {
            System.out.print(row.getProjectName() + " " + row.getWorkPackageName() + " " + row.getDescription());
            for (TimeRecordingRowValueDTO value : row.getTimeRecording()) {
                System.out.print(value.getWorkingDay() + " / " + value.getWorkingTime());
            }
            System.out.println();
        }



    }

    protected TimeRecordingDTO search(Long individualId) throws UniformInterfaceException, ClientHandlerException {

        ClientResponse cr = webResource.path("time-acquisition")
                .path(String.valueOf(individualId))
                .path("search")
                .queryParam("qStart", "2012-03-01")
                .queryParam("qEnd", "2012-03-31")
                .type("application/json")
                .accept("application/json")
                .get(ClientResponse.class);

        TimeRecordingDTO tr = cr.getEntity(TimeRecordingDTO.class);
        System.out.println(tr.getIndividualId());
        return tr;

    }

    protected Individual createIndividual(String nickname) throws UniformInterfaceException, ClientHandlerException {
        ClientResponse cr = webResource.path("individuals")
                .path(nickname)
                .type("application/json")
                .accept("application/json")
                .put(ClientResponse.class);

        Individual individual = cr.getEntity(Individual.class);
        System.out.println(individual.getId());
        return individual;

    }

    private ProjectInformation createProject() throws ClientHandlerException, UniformInterfaceException {
        ProjectInformation pi = new ProjectInformation();
        pi.setProjectKey("E225");
        pi.setName("Direkt Line Deutschland");

        Set<WorkPackage> s = new HashSet<>();

        pi.setWorkPackages(s);
        WorkPackage wp = new WorkPackage();
        wp.setProjectInformation(pi);
        wp.setWpName("Analyse");
        s.add(wp);
        wp = new WorkPackage();
        wp.setWpName("Entwicklung");
        wp.setProjectInformation(pi);
        s.add(wp);

        ClientResponse cr = webResource.path("projects")
                .path(pi.getProjectKey())
                .type("application/json")
                .accept("application/json")
                .put(ClientResponse.class, pi);

        ProjectInformation piResult = cr.getEntity(ProjectInformation.class);
        System.out.println(piResult.getId());
        return piResult;

    }

    private TimeRecordingDTO createTimeRecording(Individual user, ProjectInformation projectInfo) {
        TimeRecordingDTO timeRecording = new TimeRecordingDTO();
        timeRecording.setIndividualId(user.getId());

        WorkPackage[] wp = projectInfo.getWorkPackages().toArray(new WorkPackage[]{});

        List<TimeRecordingRowDTO> rowList = new ArrayList<>();
        timeRecording.setTimeRecordingRow(rowList);

        TimeRecordingRowDTO row = new TimeRecordingRowDTO();
        rowList.add(row);
        row.setWorkPackageId(wp[0].getId());
        row.setProjectName(projectInfo.getName());
        row.setWorkPackageName(wp[0].getWpName());

        List<TimeRecordingRowValueDTO> tr = new ArrayList<>();
        row.setTimeRecording(tr);
        Calendar c = Calendar.getInstance();
        c.set(2012, 2, 11, 0, 0, 0);
        c.set(Calendar.MILLISECOND, 0);
        tr.add(new TimeRecordingRowValueDTO(c.getTime(), 510));
        c.add(Calendar.DATE, 1);
        tr.add(new TimeRecordingRowValueDTO(c.getTime(), 480));

        row = new TimeRecordingRowDTO();
        rowList.add(row);
        row.setWorkPackageId(wp[1].getId());
        row.setProjectName(projectInfo.getName());
        row.setWorkPackageName(wp[1].getWpName());
        tr = new ArrayList<>();
        row.setTimeRecording(tr);
        c = Calendar.getInstance();
        c.set(2012, 2, 13, 0, 0, 0);
        c.set(Calendar.MILLISECOND, 0);
        c.add(Calendar.DATE, 2);
        tr.add(new TimeRecordingRowValueDTO(c.getTime(), 480));
        c.add(Calendar.DATE, 1);
        tr.add(new TimeRecordingRowValueDTO(c.getTime(), 480));


        ClientResponse cr = webResource.path("time-acquisition")
                .type("application/json")
                .accept("application/json")
                .put(ClientResponse.class, timeRecording);

        TimeRecordingDTO trResult = cr.getEntity(TimeRecordingDTO.class);

        return trResult;
    }
}