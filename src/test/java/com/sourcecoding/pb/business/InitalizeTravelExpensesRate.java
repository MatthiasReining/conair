/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sourcecoding.pb.business;

import com.sourcecoding.pb.business.perdiemcharges.entity.TravelExpensesRate;
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
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Matthias
 */
public class InitalizeTravelExpensesRate {

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
    public void run() throws IOException {
        List<TravelExpensesRate> data = new ArrayList<>();

        URL resource = this.getClass().getResource("/per-diem-2013.csv");
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
            ter.setRateFrom14To24(new BigDecimal(values[2]));
            ter.setRateFrom8To14(new BigDecimal(values[3]));
            ter.setAccommodationExpenses(new BigDecimal(values[4]));
        }

        ClientResponse cr = webResource.path("per-diem/travel-expenses-rates")
                .type("application/json")
                .accept("application/json")
                .put(ClientResponse.class, data);

        System.out.println("Upload status: " + cr.getStatus());
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