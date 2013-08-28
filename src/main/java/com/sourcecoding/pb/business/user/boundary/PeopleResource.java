/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sourcecoding.pb.business.user.boundary;

import com.sourcecoding.pb.business.user.entity.JsonPeople;
import com.sourcecoding.pb.business.user.entity.Individual;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.core.Response;

/**
 *
 * @author Matthias
 */
public class PeopleResource {

    @Inject
    IndividualService individualService;

    @GET
    public Response getPeople() {
        List<JsonPeople> result = new ArrayList<>();

        for (Individual individual : individualService.getAll()) {
            JsonPeople p = JsonPeople.create(individual);
            result.add(p);
        }

        return Response.ok().entity(result).build();
    }
}
