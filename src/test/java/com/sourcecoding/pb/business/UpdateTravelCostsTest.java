/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sourcecoding.pb.business;

import java.io.InputStream;
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
public class UpdateTravelCostsTest {

    private static final String REST_ROOT = "http://localhost:8080/conair/rest";
    private WebTarget base;

    @Before
    public void init() {
        Client client = ClientBuilder.newClient();
        base = client.target(REST_ROOT);
    }

    @Test
    public void run() {

        InputStream jsonStream = this.getClass().getResourceAsStream("/perdiem.json");
        String json = convertStreamToString(jsonStream);

        //FIXME wird nicht klappen :-( Entity vs json String
        Response cr = base.path("travel-costs/4/2013-04")
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.json(json), Response.class);


        System.out.println("update per diem 04/2013: " + cr.getStatus());
    }

    public String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}