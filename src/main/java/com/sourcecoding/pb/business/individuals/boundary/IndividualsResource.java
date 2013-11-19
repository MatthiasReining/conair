/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sourcecoding.pb.business.individuals.boundary;

import com.sourcecoding.pb.business.individuals.entity.JsonIndividual;
import com.sourcecoding.pb.business.individuals.entity.Individual;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author Matthias
 */
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Stateless
public class IndividualsResource {

    @PersistenceContext
    EntityManager em;

    @GET
    public Response getIndividuals() {
        List<JsonIndividual> result = new ArrayList<>();

        List<Individual> individuals = em.createNamedQuery(Individual.findAll, Individual.class)
                .getResultList();

        for (Individual individual : individuals) {
            JsonIndividual p = JsonIndividual.create(individual);
            result.add(p);
        }

        return Response.ok().entity(result).build();
    }

    @PUT
    public Response updateOrCreateIndividual(JsonIndividual jsonIndividual) {
        Individual individual = null;
        System.out.println("id: " + jsonIndividual.id);
        if (jsonIndividual.id <= 0) {
            //create new Individual
            individual = new Individual();
            individual = em.merge(individual);

        } else {
            //load existing individual
            individual = em.find(Individual.class, jsonIndividual.id);
            System.out.println("individual: " + individual.getId());
        }

        individual.setLinkedInId(jsonIndividual.linkedInId);
        individual.setNickname(jsonIndividual.nickname);
        individual.setVacationDaysPerYear(jsonIndividual.vacationDaysPerYear);
        individual.setWorkdaysPerWeek(jsonIndividual.workdaysPerWeek);
        individual.setVacationManager(em.find(Individual.class, jsonIndividual.vacationManagerId));

        //TODO set Roles
        //TODO set Working Manager
        return Response.ok().build();
    }
}
