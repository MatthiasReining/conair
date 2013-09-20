/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sourcecoding.pb.business.vacation.boundary;

import com.sourcecoding.pb.business.export.boundary.XlsExportService;
import com.sourcecoding.pb.business.individuals.entity.Individual;
import com.sourcecoding.pb.business.vacation.control.ResponseBuilder;
import com.sourcecoding.pb.business.vacation.entity.VacationYear;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author Matthias
 */
@Path("vacations")
@Stateless
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class VacationService {

    @PersistenceContext
    EntityManager em;
    @Context
    private ResourceContext resourceContext;
    @Inject
    ResponseBuilder rb;
  
    @GET
    public List<Map<String, Object>> getVacations() {
        //TODO Security check

        List<Map<String, Object>> result = new ArrayList<>();

        Integer currentYear = Calendar.getInstance().get(Calendar.YEAR);

        List<VacationYear> vyList = em.createNamedQuery(VacationYear.findAllByYear, VacationYear.class)
                .setParameter(VacationYear.queryParam_year, currentYear)
                .getResultList();

        for (VacationYear vy : vyList) {
            result.add(rb.buildVacationYear(vy));
        }
        return result;
    }

    @Path("{individualId}")
    public IndividualVacationResource getIndividualVacationResource(@PathParam("individualId") Long individualId) {
        //Call sub resource
        IndividualVacationResource ivr = resourceContext.getResource(IndividualVacationResource.class);

        Individual individual = em.find(Individual.class, individualId);
        
        //FIXME setter for slsb :-( search for a better solution!
        ivr.setIndividual(individual);
        return ivr;
    }

    @Path("tasks")
    public TasksResource getTasks() {
        TasksResource tr = resourceContext.getResource(TasksResource.class);
        return tr;
    }
}
