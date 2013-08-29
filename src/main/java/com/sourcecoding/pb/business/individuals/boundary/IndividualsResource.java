/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sourcecoding.pb.business.individuals.boundary;

import com.sourcecoding.pb.business.individuals.entity.JsonIndividual;
import com.sourcecoding.pb.business.individuals.entity.Individual;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.core.Response;

/**
 *
 * @author Matthias
 */
public class IndividualsResource {

    @Inject
    IndividualService individualService;

    @GET
    public Response getIndividuals() {
        List<JsonIndividual> result = new ArrayList<>();

        for (Individual individual : individualService.getAll()) {
            JsonIndividual p = JsonIndividual.create(individual);
            result.add(p);
        }

        return Response.ok().entity(result).build();
    }
}
