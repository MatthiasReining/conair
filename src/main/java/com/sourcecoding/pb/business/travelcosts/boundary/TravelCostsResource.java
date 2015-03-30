/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sourcecoding.pb.business.travelcosts.boundary;

import com.sourcecoding.pb.business.configuration.boundary.Configurator;
import com.sourcecoding.pb.business.export.boundary.XlsExportService;
import com.sourcecoding.pb.business.travelcosts.entity.TravelCostsGroup;
import com.sourcecoding.pb.business.travelcosts.entity.TravelCosts;
import com.sourcecoding.pb.business.travelcosts.entity.TravelExpensesRate;
import com.sourcecoding.pb.business.project.entity.ProjectInformation;
import com.sourcecoding.pb.business.individuals.entity.Individual;
import com.sourcecoding.pb.business.travelcosts.entity.TravelCostsDTO;
import com.sourcecoding.pb.business.travelcosts.entity.TravelCostsGroupDTO;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author Matthias
 */
@Path("travel-costs")
@Stateless
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TravelCostsResource {

    @PersistenceContext
    EntityManager em;

    @Inject
    XlsExportService exportService;

    @Inject
    Configurator configurator;

    @GET
    @Path("{individualId}/{yearAndMonth}")
    public TravelCostsGroupDTO getTravelCostsListByMonth(@PathParam("individualId") Long individualId,
            @PathParam("yearAndMonth") String yearAndMonth) {
        int year = Integer.parseInt(yearAndMonth.split("-")[0]);
        int month = Integer.parseInt(yearAndMonth.split("-")[1]);
        TravelCostsGroup tcg = getChargesMonth(individualId, yearAndMonth);

        TravelCostsGroupDTO tcGroupDTO = new TravelCostsGroupDTO();
        tcGroupDTO.indiviudalId = individualId;
        tcGroupDTO.travelCostsGroupState = tcg.getTravelCostsGroupState();
        tcGroupDTO.yearMonth = yearAndMonth;
        tcGroupDTO.sum = tcg.getTravelCostsGroupSum().doubleValue();

        tcGroupDTO.travelCostsList = new ArrayList<>();

        Calendar day = Calendar.getInstance();
        day.setTimeInMillis(0);
        day.set(year, month - 1, 0, 0, 0, 0); //start one day before, incr in loop
        Calendar untilDay = Calendar.getInstance();
        //untilDay.setTime(day.getTime());
        //untilDay.add(Calendar.MONTH, 1);
        untilDay.setTimeInMillis(0);
        untilDay.set(year, month, 0, 0, 0, 0);
        
        while (day.before(untilDay)) {
            day.add(Calendar.DATE, 1);

            TravelCosts tc = getTravelCostsByDate(tcg.getTravelCostsList(), day.getTime());
            TravelCostsDTO tcDTO = new TravelCostsDTO();
            tcDTO.travelCostsDate = day.getTime();
            if (tc != null) {
                tcDTO.charges = tc.getCharges();
                tcDTO.breakfast = tc.isBreakfast();
                tcDTO.lunch = tc.isLunch();
                tcDTO.dinner = tc.isDinner();
                tcDTO.taxable = tc.isTaxable();
                tcDTO.projectId = tc.getProject().getId();
                tcDTO.timeFrom = (tc.getTimeFrom() == null ? "" : tc.getTimeFrom().toString());
                tcDTO.timeTo = (tc.getTimeTo() == null ? "" : tc.getTimeTo().toString());
                tcDTO.travelExpenseRateId = tc.getTravelExpensesRate().getId();
            }
            tcGroupDTO.travelCostsList.add(tcDTO);
        }
        return tcGroupDTO;
    }

    private TravelCosts getTravelCostsByDate(List<TravelCosts> travelCostsList, Date travelCostsDate) {
        for (TravelCosts tc : travelCostsList) {
            if (tc.getTravelCostsDate().equals(travelCostsDate))
                return tc;
        }
        return null;
    }

    @PUT
    @Path("{individualId}")
    public TravelCostsGroupDTO updateChargesByMonth(TravelCostsGroupDTO travelCostsGroupDTO,
            @PathParam("individualId") Long individualId) {
        String yearAndMonth = travelCostsGroupDTO.yearMonth;
        int year = Integer.parseInt(yearAndMonth.split("-")[0]);
        int month = Integer.parseInt(yearAndMonth.split("-")[1]);

        TravelCostsGroup tcg = getChargesMonth(individualId, yearAndMonth);

        Calendar day = Calendar.getInstance();
        day.setTimeInMillis(0);
        day.set(year, month - 1, 0, 0, 0, 0); //start one day before, incr in loop
        Calendar untilDay = Calendar.getInstance();
        untilDay.setTime(day.getTime());
        untilDay.add(Calendar.MONTH, 1);

        Calendar fromDate = Calendar.getInstance();
        fromDate.setTime(day.getTime());
        fromDate.add(Calendar.DATE, 1);
        tcg.setTravelCostsFrom(fromDate.getTime());
        tcg.setTravelCostsTo(untilDay.getTime());
        tcg.setTravelCostsGroupSum(BigDecimal.valueOf(travelCostsGroupDTO.sum));

        while (day.before(untilDay)) {
            day.add(Calendar.DATE, 1);

            TravelCosts tc = null;
            for (TravelCosts tcLoop : tcg.getTravelCostsList()) {
                if (day.getTime().equals(tcLoop.getTravelCostsDate())) {
                    tc = tcLoop;
                    break;
                }
            }
            TravelCostsDTO tcIn = null;
            for (TravelCostsDTO tcLoop : travelCostsGroupDTO.travelCostsList) {
                if (day.getTime().equals(tcLoop.travelCostsDate)) {
                    tcIn = tcLoop;
                    break;
                }
            }
            if (tcIn != null
                    && (tcIn.projectId == null || tcIn.timeFrom == null
                    || tcIn.timeTo == null || tcIn.travelExpenseRateId == null))
                tcIn = null; //set to null

            if (tcIn == null && tc == null)
                continue; //nothing todo

            if (tcIn == null && tc != null) {
                tcg.getTravelCostsList().remove(tc); //remove
                //em.remove(tc);
                continue;
            }

            if (tcIn == null)
                throw new RuntimeException("Per Diem is null - this case should not possible!");

            //tcIn is now always != null            
            if (tc == null) {
                tc = new TravelCosts();
                tc.setTravelCostsDate(day.getTime());
                tc.setTravelCostsGroup(tcg);
                tc.setIndividual(tcg.getIndividual());
                tcg.getTravelCostsList().add(tc);
            }
            tc.setCharges(tcIn.charges);
            tc.setBreakfast(tcIn.breakfast);
            tc.setLunch(tcIn.lunch);
            tc.setDinner(tcIn.dinner);
            tc.setTaxable(tcIn.taxable);
            tc.setTimeFrom(Double.parseDouble(tcIn.timeFrom));
            tc.setTimeTo(Double.parseDouble(tcIn.timeTo));
            tc.setProject(em.find(ProjectInformation.class, tcIn.projectId));
            tc.setTravelExpensesRate(em.find(TravelExpensesRate.class, tcIn.travelExpenseRateId));

        }

        em.merge(tcg);

        return getTravelCostsListByMonth(individualId, yearAndMonth);

    }

    @PUT
    @Path("travel-expenses-rates")
    public Response uploadTravelExpensesRate(List<TravelExpensesRate> terList) {

        for (TravelExpensesRate ter : terList) {
            em.merge(ter);
        }
        return Response.ok().build();
    }

    @GET
    @Path("travel-expenses-rates/{travelYear}")
    public List<TravelExpensesRate> getTravelExpensesRate(@PathParam("travelYear") Integer travelYear) {
        return em.createNamedQuery(TravelExpensesRate.findByDate, TravelExpensesRate.class)
                .setParameter(TravelExpensesRate.queryParam_travelYear, travelYear)
                .getResultList();
    }

    private TravelCostsGroup getChargesMonth(Long individualId, String yearAndMonth) throws RuntimeException {
        List<TravelCostsGroup> chList = em.createNamedQuery(TravelCostsGroup.findByGroupKey, TravelCostsGroup.class)
                .setParameter(TravelCostsGroup.queryParam_individualId, individualId)
                .setParameter(TravelCostsGroup.queryParam_groupKey, yearAndMonth)
                .getResultList();
        if (chList.size() > 1) {
            throw new RuntimeException("Only one entity for ChargesMonth is allowed for individualId and year-month!");
        }
        TravelCostsGroup tcg = (chList.isEmpty()) ? null : chList.get(0);

        if (tcg == null) {
            tcg = new TravelCostsGroup();
            tcg.setGroupKey(yearAndMonth);
            tcg.setTravelCostsGroupSum(BigDecimal.ZERO);
            tcg.setIndividual(em.find(Individual.class, individualId));
        }
        if (tcg.getTravelCostsList() == null)
            tcg.setTravelCostsList(new ArrayList<TravelCosts>());

        return tcg;
    }

    @GET
    @Path("{individualId}/{yearAndMonth}/xls")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getVacationsXls(@PathParam("individualId") Long individualId,
            @PathParam("yearAndMonth") String yearAndMonth) throws IOException {

        String templateUrl = configurator.getValue(Configurator.XLS_TEMPLATE_PATH_FOR_TRAVEL_COSTS);

        System.out.println(templateUrl);
        TravelCostsGroupDTO tcg = getTravelCostsListByMonth(individualId, yearAndMonth);
        Individual individual = em.find(Individual.class, individualId);

        List<TravelCostsDTO> removePD = new ArrayList<>();
        for (TravelCostsDTO tc : tcg.travelCostsList) {
            if (tc.travelExpenseRateId == null)
                removePD.add(tc);
            else {
                tc.timeFrom = tc.timeFrom.replace(".0", ":00");
                tc.timeFrom = tc.timeFrom.replace(".5", ":30");
                tc.timeTo = tc.timeTo.replace(".0", ":00");
                tc.timeTo = tc.timeTo.replace(".5", ":30");
            }
        }
        tcg.travelCostsList.removeAll(removePD);

        Map<String, Object> payloadMap = new HashMap<>();
        payloadMap.put("travelCostsGroup", tcg);

        Map<Long, Object> travelExpensesRateMap = new HashMap<>();
        int year = Integer.parseInt(yearAndMonth.split("-")[0]);
        for (TravelExpensesRate ter : getTravelExpensesRate(year)) {
            travelExpensesRateMap.put(ter.getId(), ter);
        }
        payloadMap.put("travelExpensesRates", travelExpensesRateMap);

        Map<Long, ProjectInformation> projectMap = new HashMap<>();
        for (ProjectInformation pi : em.createNamedQuery(ProjectInformation.findAllValidProjects, ProjectInformation.class).getResultList()) {
            projectMap.put(pi.getId(), pi);
        }
        payloadMap.put("projects", projectMap);
        payloadMap.put("individual", individual);

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        exportService.generate(templateUrl, payloadMap, os);
        String filename = "travel-costs-" + individual.getFirstname().toLowerCase() + "-" + yearAndMonth + ".xls";
        return Response.ok(os.toByteArray(), MediaType.APPLICATION_OCTET_STREAM)
                .header("content-disposition", "attachment; filename = " + filename)
                .build();
    }
}
