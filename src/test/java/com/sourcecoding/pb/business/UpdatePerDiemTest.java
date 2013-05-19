/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sourcecoding.pb.business;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;
import java.io.InputStream;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author Matthias
 */
public class UpdatePerDiemTest {

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
    
        InputStream jsonStream = this.getClass().getResourceAsStream("/perdiem.json");
        String json = convertStreamToString(jsonStream);
        
        ClientResponse cr = webResource.path("per-diem/4/2013-04")
                .type("application/json")
                .accept("application/json")
                .put(ClientResponse.class, json);

        System.out.println( "update per diem 04/2013: " + cr.getStatus());
    }

    public String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}