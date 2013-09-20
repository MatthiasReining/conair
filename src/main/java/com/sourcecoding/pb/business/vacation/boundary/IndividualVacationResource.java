/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sourcecoding.pb.business.vacation.boundary;

import com.sourcecoding.pb.business.export.boundary.XlsExportService;
import com.sourcecoding.pb.business.restconfig.DateParameter;
import com.sourcecoding.pb.business.individuals.entity.Individual;
import com.sourcecoding.pb.business.vacation.control.ResponseBuilder;
import com.sourcecoding.pb.business.vacation.control.VacationCalculator;
import com.sourcecoding.pb.business.vacation.entity.VacationRecord;
import com.sourcecoding.pb.business.vacation.entity.VacationYear;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.codehaus.jackson.map.ObjectMapper;

/**
 *
 * @author Matthias
 */
@Stateless
public class IndividualVacationResource {

    @PersistenceContext
    EntityManager em;
    @Inject
    ResponseBuilder rb;
    @Inject
    VacationCalculator vacationCalculator;
    @Inject
    XlsExportService exportService;
    private Individual individual;

    public void setIndividual(Individual individual) {
        this.individual = individual;
    }

    @GET
    @Path("xls")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getVacationXls() throws IOException {

        String templateName = "vacation-template";

        Map<String, Object> vacationMap = getVaction();
        ObjectMapper om = new ObjectMapper();
        String payloadText = om.writeValueAsString(vacationMap);
        System.out.println("payloadText");
        System.out.println(payloadText);
        JsonObject payload = Json.createReader(new StringReader(payloadText)).readObject();
        payload = Json.createObjectBuilder().add("vacation", payload).build();
        
        System.out.println("payload");
        System.out.println(payload);
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        exportService.generate(templateName, payload, os);
        String filename = "vacation-yyyy-" + individual.getNickname() + ".xls";
        return Response.ok(os.toByteArray(), MediaType.APPLICATION_OCTET_STREAM)
                .header("content-disposition", "attachment; filename = " + filename)
                .build();
    }

    @GET
    public Map<String, Object> getVaction() {
        Map<String, Object> result = new HashMap<>();

        Integer year = Calendar.getInstance().get(Calendar.YEAR);

        List<VacationYear> vyList = em.createNamedQuery(VacationYear.findByDate, VacationYear.class)
                .setParameter(VacationYear.queryParam_individual, individual)
                .setParameter(VacationYear.queryParam_year, year)
                .getResultList();
        VacationYear vy;
        if (vyList.isEmpty()) {
            vy = createNewVacationYear(individual, year);
        } else {
            vy = vyList.get(0);
        }

        rb.buildVacationYear(vy, result);

        List<Map<String, Object>> vacationRecords = new ArrayList<>();
        result.put("vacationRecords", vacationRecords);
        List<String> vacationDays = new ArrayList<>();
        result.put("vacationDays", vacationDays);


        int vacationDaysThisYear = 0;
        for (VacationRecord vr : vy.getVacationRecords()) {
            Map<String, Object> vrMap = new HashMap<>();
            vacationRecords.add(vrMap);
            vrMap.put("approvalState", vr.getApprovalState());
            vrMap.put("numberOfDays", vr.getNumberOfDays());
            if (vr.getApprovalState() == VacationRecord.APPROVAL_STATE_APPROVED) {
                vacationDaysThisYear += vr.getNumberOfDays();
            }
            String vacationFrom = DateParameter.valueOf(vr.getVacationFrom());
            String vacationUntil = DateParameter.valueOf(vr.getVacationUntil());
            vrMap.put("vacationFrom", vacationFrom);
            vrMap.put("vacationUntil", vacationUntil);
            vrMap.put("id", vr.getId());

            Map<String, Object> vacationDaysMap = calculateVacationDays(vacationFrom, vacationUntil);
            List<String> vacationDayDates = (List<String>) vacationDaysMap.get("vacationDayDate");
            vacationDays.addAll(vacationDayDates);
        }

        int numberOfVacationDaysTotal = vy.getResidualLeaveYearBefore() + vy.getNumberOfVacationDays();
        result.put("numberOfVacationDaysTotal", numberOfVacationDaysTotal);
        result.put("vacationDaysThisYear", vacationDaysThisYear);


        return result;
    }

    @Path("{id}")
    @DELETE
    @Consumes(MediaType.WILDCARD)
    public void deleteVacationRecord(@PathParam("id") String idText) {

        Long id = Long.valueOf(idText);

        VacationRecord vr = em.find(VacationRecord.class, id);
        VacationYear vy = vr.getVacationYear();
        vy.getVacationRecords().remove(vr);
        em.remove(vr);
        vacationCalculator.calculateAllVacationDays(vy);
    }

    @Path("calculateVacationDays")
    @GET
    public Map<String, Object> calculateVacationDays(
            @QueryParam("vacationFrom") String vacationFrom,
            @QueryParam("vacationUntil") String vacationUntil) {

        return vacationCalculator.calcVacationDays(individual, vacationFrom, vacationUntil);
    }

    @POST
    public Response issueRequestForTimeOff(Map<String, Object> payload) {
        //validation
        Integer numberOfDays = (Integer) payload.get("numberOfDays");
        Date vacationFrom = DateParameter.valueOf(payload.get("vacationFrom").toString());
        Date vacationUntil = DateParameter.valueOf(payload.get("vacationUntil").toString());

        Map<String, String> errors = new HashMap<>();
        if (numberOfDays <= 0) {
            errors.put("numberOfDays", "Die Anzahl der Urlaubstage muss > 0 sein.");
        }
        if (vacationFrom.after(vacationUntil)) {
            errors.put("vacationFrom", "'Urlaub von' muss vor 'Urlaub bis' liegen.");
        }

        if (errors.size() > 0) {
            return Response.status(Response.Status.PRECONDITION_FAILED).entity(errors).build();
        }


        Calendar from = DateParameter.calendarValueOf(payload.get("vacationFrom").toString());
        Integer year = from.get(Calendar.YEAR);
        VacationYear vy = getVacationYear(year, individual);
        if (vy.getVacationRecords() == null) {
            vy.setVacationRecords(new ArrayList<VacationRecord>());
        }
        List<VacationRecord> vrList = vy.getVacationRecords();


        VacationRecord vr = new VacationRecord();
        vr.setVacationYear(vy);
        vr.setIndividual(individual);
        vr.setNumberOfDays(numberOfDays);
        vr.setVacationFrom(vacationFrom);
        vr.setVacationUntil(vacationUntil);
        vr.setApprovalState(VacationRecord.APPROVAL_STATE_FOR_APPROVAL);

        vrList.add(vr);

        vacationCalculator.calculateAllVacationDays(vy);


        return Response.ok().build();
    }

    private VacationYear createNewVacationYear(Individual individual, Integer year) {
        VacationYear vy = new VacationYear();
        vy.setIndividual(individual);
        System.out.println("vacation days per year: " + individual.getVacationDaysPerYear());
        vy.setNumberOfVacationDays(individual.getVacationDaysPerYear());
        //FIXME select vacationyear from year before!!!
        vy.setResidualLeaveYearBefore(0);
        vy.setApprovedVacationDays(0);
        vy.setResidualLeave(vy.getNumberOfVacationDays() + vy.getResidualLeaveYearBefore());
        vy.setVacationYear(year);
        vy = em.merge(vy);
        return vy;
    }

    @PUT
    @Path("admin/{year}/change-number-of-vacation-days")
    public Response changeNumberOfVacationDays(@PathParam("year") Integer year, Map<String, Object> payload) {

        Integer newNumberOfVacationDays = (Integer) payload.get("newNumberOfVacationDays");

        VacationYear vy = getVacationYear(year, individual);
        vy.setNumberOfVacationDays(newNumberOfVacationDays);

        vacationCalculator.calculateAllVacationDays(vy);

        return Response.ok().build();
    }

    private VacationYear getVacationYear(Integer year, Individual individual) {
        List<VacationYear> vyList = em.createNamedQuery(VacationYear.findByDate, VacationYear.class)
                .setParameter(VacationYear.queryParam_individual, individual)
                .setParameter(VacationYear.queryParam_year, year)
                .getResultList();
        VacationYear vy;
        System.out.println("vyList: " + vyList);
        if (vyList.isEmpty()) {
            vy = createNewVacationYear(individual, year);
            vy = em.merge(vy);
        } else {
            vy = vyList.get(0);
        }
        return vy;
    }
}
