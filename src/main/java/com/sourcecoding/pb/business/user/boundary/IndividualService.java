/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sourcecoding.pb.business.user.boundary;

import com.sourcecoding.pb.business.user.entity.Individual;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author Matthias
 */
@Stateless
@Path("individuals")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class IndividualService {

    @PersistenceContext
    EntityManager em;

    @PUT
    @Path("{nickname}")
    public Individual createOrUpdate(@PathParam("nickname") String nickname, @QueryParam("linkedInId") String linkedinId) {

        List<Individual> iList = em.createNamedQuery(Individual.findByNickname, Individual.class)
                .setParameter(Individual.queryParam_nickname, nickname)
                .getResultList();
        Individual individual;

        if (iList.isEmpty()) {
            individual = new Individual();
            individual.setNickname(nickname);
            individual.setLinkedInId(linkedinId);
            individual = em.merge(individual);
        } else {
            individual = iList.get(0);
            individual.setLinkedInId(linkedinId);
        }

        return individual;
    }

    @GET
    @Path("{nickname}")
    public Individual read(@PathParam("nickname") String nickname) {
        return em.createNamedQuery(Individual.findByNickname, Individual.class)
                .setParameter(Individual.queryParam_nickname, nickname)
                .getSingleResult();
    }
    
    @GET
    public List<Individual> getAll() {
         return em.createNamedQuery(Individual.findAll, Individual.class)
                .getResultList();
    }
    
    public Individual loginBySocialNetId(String socialNetId) {
        List<Individual> result = em.createNamedQuery(Individual.findByLinkedInId, Individual.class)
                .setParameter(Individual.queryParam_socialNetId, socialNetId)
                .getResultList();
        if (result.size() != 1) {
            return null;
        }
        Individual individual = result.get(0);        
        individual.setLastLogin(new Date());
        
        return individual;
    }
}
