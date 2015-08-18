/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sourcecoding.pb.business.vacation.boundary;

import com.sourcecoding.pb.business.configuration.boundary.Configurator;
import com.sourcecoding.pb.business.export.boundary.XlsExportService;
import com.sourcecoding.pb.business.export.control.DataExtractor;
import com.sourcecoding.pb.business.individuals.entity.Individual;
import com.sourcecoding.pb.business.vacation.control.ResponseBuilder;
import com.sourcecoding.pb.business.vacation.control.VacationCalculator;
import com.sourcecoding.pb.business.vacation.entity.VacationRecord;
import com.sourcecoding.pb.business.vacation.entity.VacationYear;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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
    @Inject
    XlsExportService exportService;
    @Inject
    VacationCalculator vacationCalculator;

    @Inject
    Configurator configurator;

    @GET
    @Path("xls")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getVacationsXls(@QueryParam("year") Integer year) throws IOException {

        String templateUrl = configurator.getValue(Configurator.XLS_TEMPLATE_PATH_FOR_VACATION_OVERVIEW);
        System.out.println(templateUrl);

        if (year == null)
            year = Calendar.getInstance().get(Calendar.YEAR);

        Map<String, Object> vacationMap = new HashMap<>();
        vacationMap.put("vacations", getVacations(year));
        vacationMap.put("vacationYear", year.toString());

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        exportService.generate(templateUrl, vacationMap, os);
        String filename = "vacations-" + DataExtractor.getStringValue(vacationMap, "vacationYear") + ".xls";
        return Response.ok(os.toByteArray(), MediaType.APPLICATION_OCTET_STREAM)
                .header("content-disposition", "attachment; filename = " + filename)
                .build();
    }

    @GET
    public List<Map<String, Object>> getVacations(@QueryParam("year") Integer year) {
        //TODO Security check

        List<Map<String, Object>> result = new ArrayList<>();

        if (year == null)
            year = Calendar.getInstance().get(Calendar.YEAR);

        List<VacationYear> vyList = em.createNamedQuery(VacationYear.findAllByYear, VacationYear.class)
                .setParameter(VacationYear.queryParam_year, year)
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
        return ivr;
    }

    @Path("jobs/takeover-residual-leave/{year}")
    @PUT
    public void takeoverResidualLeave(@PathParam("year") Integer year) {
        System.out.println("year: " + year);

        List<VacationYear> vyList = em.createNamedQuery(VacationYear.findAllByYear, VacationYear.class)
                .setParameter(VacationYear.queryParam_year, year)
                .getResultList();
        Integer newYear = year;
        newYear++;
        System.out.println("newYear: " + newYear);

        for (VacationYear vy : vyList) {

            System.out.println(vy.getIndividual().getFirstname() + " - " + vy.getResidualLeave());
            VacationYear newVy;
            try {
                newVy = em.createNamedQuery(VacationYear.findByDate, VacationYear.class)
                        .setParameter(VacationYear.queryParam_individual, vy.getIndividual())
                        .setParameter(VacationYear.queryParam_year, newYear)
                        .getSingleResult();
            } catch (NoResultException e) {
                newVy = new VacationYear();
                newVy.setIndividual(vy.getIndividual());
                newVy.setVacationYear(newYear);
                newVy.setVacationRecords(new ArrayList<VacationRecord>());
                newVy.setNumberOfVacationDays(vy.getIndividual().getVacationDaysPerYear());
                em.persist(newVy);
                newVy = em.merge(newVy);
            }
            newVy.setResidualLeaveYearBefore(vy.getResidualLeave());
            newVy.setNumberOfVacationDays(vy.getIndividual().getVacationDaysPerYear());

            vacationCalculator.calculateAllVacationDays(newVy); 
        }
    }

    @Path("tasks")
    public TasksResource getTasks() {
        TasksResource tr = resourceContext.getResource(TasksResource.class);
        return tr;
    }
}
