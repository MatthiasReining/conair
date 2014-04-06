/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sourcecoding.pb.business;

import com.sourcecoding.pb.business.travelcosts.entity.TravelExpensesRate;
import com.sourcecoding.pb.business.project.entity.ProjectInformation;
import com.sourcecoding.pb.business.project.entity.WorkPackage;
import com.sourcecoding.pb.business.individuals.entity.Individual;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
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
@Deprecated
@Ignore
public class InitalizeTravelExpensesRate {

    private static final String REST_ROOT = "http://localhost:8080/conair/rest";
    private WebTarget base;

    @Before
    public void init() {

        Client client = ClientBuilder.newClient();
        base = client.target(REST_ROOT);


        //ClientConfig clientConfig = new DefaultClientConfig();
        //clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);

        //Client client = Client.create(clientConfig);
        //webResource = client.resource(REST_ROOT);

    }

    @Test
    public void run() throws IOException {
        List<TravelExpensesRate> data = new ArrayList<>();

        URL resource = this.getClass().getResource("/travel-costs-2013.csv");
        System.out.println(resource);
        BufferedReader in = new BufferedReader(new InputStreamReader(
                new FileInputStream(resource.getFile()), "UTF8"));


        int count = 0;
        String line = null;
        while ((line = in.readLine()) != null) {
            count++;
            if (count == 1) {
                continue; //skip first line
            }

            String[] values = line.split(";"); //only west european format

            TravelExpensesRate ter = new TravelExpensesRate();
            data.add(ter);

            ter.setCountry(values[0]);
            ter.setRate24h(new BigDecimal(values[1])); //only without decimal places
            //ter.setRateFrom14To24(new BigDecimal(values[2]));
            //ter.setRateFrom8To14(new BigDecimal(values[3]));
            ter.setAccommodationExpenses(new BigDecimal(values[4]));
        }

        Response cr = base.path("travel-costs/travel-expenses-rates")
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.json(data), Response.class);

        System.out.println("Upload status: " + cr.getStatus());
    }

    protected Individual createIndividual(String nickname)  {
        Response cr = base.path("individuals")
                .path(nickname)
                .request(MediaType.APPLICATION_JSON)
                .put(null, Response.class);

        Individual individual = cr.readEntity(Individual.class);
        System.out.println("get individual: " + individual.getId());
        return individual;
    }

    protected Individual getIndividual(String nickname) {
        Response cr = base.path("individuals")
                .path(nickname)
                .request(MediaType.APPLICATION_JSON)
                .get();

        Individual individual = cr.readEntity(Individual.class);
        System.out.println("created individual: " + individual.getId());
        return individual;

    }

    private ProjectInformation getProject(String projectKey) {

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