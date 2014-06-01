/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sourcecoding.pb.business.cashdisbursement.boundary;

import com.sourcecoding.pb.business.cashdisbursement.entity.CashDisbursement;
import com.sourcecoding.pb.business.cashdisbursement.entity.CashDisbursementDTO;
import com.sourcecoding.pb.business.configuration.boundary.Configurator;
import com.sourcecoding.pb.business.export.boundary.XlsExportService;
import com.sourcecoding.pb.business.individuals.entity.Individual;
import com.sourcecoding.pb.business.project.entity.ProjectInformation;
import com.sourcecoding.pb.business.cashdisbursement.entity.CashDisbursementGroup;
import com.sourcecoding.pb.business.cashdisbursement.entity.CashDisbursementGroupDTO;
import com.sourcecoding.pb.business.travelcosts.entity.TravelExpensesRate;
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
@Stateless
@Path("cash-disbursement")
public class CashDisbursementResource {

    @PersistenceContext
    EntityManager em;

    @Inject
    XlsExportService exportService;

    @Inject
    Configurator configurator;

    @GET
    @Path("{individualId}/{yearAndMonth}")
    public CashDisbursementGroupDTO getCashDisbursementListByMonth(@PathParam("individualId") Long individualId,
            @PathParam("yearAndMonth") String yearAndMonth) {

        CashDisbursementGroup cdg = getCDByMonth(individualId, yearAndMonth);
        CashDisbursementGroupDTO cdgDTO = new CashDisbursementGroupDTO(cdg);

        return cdgDTO;
    }


    @PUT
    @Path("{individualId}")
    public CashDisbursementGroupDTO upcateChargesByMonth(CashDisbursementGroupDTO cashDisbursementGroupDTO,
            @PathParam("individualId") Long individualId) {
        String yearAndMonth = cashDisbursementGroupDTO.yearMonth;
        int year = Integer.parseInt(yearAndMonth.split("-")[0]);
        int month = Integer.parseInt(yearAndMonth.split("-")[1]);

        CashDisbursementGroup cdg = getCDByMonth(individualId, yearAndMonth);

        Calendar day = Calendar.getInstance();
        day.setTimeInMillis(0);
        day.set(year, month - 1, 0, 0, 0, 0); //start one day before, incr in loop
        Calendar untilDay = Calendar.getInstance();
        untilDay.setTime(day.getTime());
        untilDay.add(Calendar.MONTH, 1);

        Calendar fromDate = Calendar.getInstance();
        fromDate.setTime(day.getTime());
        fromDate.add(Calendar.DATE, 1);
        cdg.setGroupFrom(fromDate.getTime());
        cdg.setGroupTo(untilDay.getTime());
        cdg.setGroupSum(BigDecimal.valueOf(cashDisbursementGroupDTO.sum));

        while (day.before(untilDay)) {
            day.add(Calendar.DATE, 1);

            CashDisbursement tc = null;
            for (CashDisbursement tcLoop : cdg.getCashDisbursementList()) {
                if (day.getTime().equals(tcLoop.getCdDate())) {
                    tc = tcLoop;
                    break;
                }
            }
            CashDisbursementDTO tcIn = null;
            for (CashDisbursementDTO tcLoop : cashDisbursementGroupDTO.cashDisbursementList) {
                if (day.getTime().equals(tcLoop.cdDate)) {
                    tcIn = tcLoop;
                    break;
                }
            }

            if (tcIn == null && tc == null)
                continue; //nothing todo

            if (tcIn == null && tc != null) {
                cdg.getCashDisbursementList().remove(tc); //remove
                //em.remove(tc);
                continue;
            }

            if (tcIn == null)
                throw new RuntimeException("Cash disbursement group is null - this case should not possible!");

            //tcIn is now always != null            
            if (tc == null) {
                tc = new CashDisbursement();
                tc.setCdDate(day.getTime());

                tc.setCashDisbursementGroup(cdg);

                tc.setIndividual(cdg.getIndividual());

                cdg.getCashDisbursementList().add(tc);
            }
            tc.setAmount(tcIn.amount);
            tc.setCdCategory(tcIn.cdCategory);
            tc.setDescription(tcIn.description);
            tc.setId(tcIn.id);
        }

        em.merge(cdg);

        return getCashDisbursementListByMonth(individualId, yearAndMonth);

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

    private CashDisbursementGroup getCDByMonth(Long individualId, String yearAndMonth) throws RuntimeException {
        List<CashDisbursementGroup> chList = em.createNamedQuery(CashDisbursementGroup.findByGroupKey, CashDisbursementGroup.class)
                .setParameter(CashDisbursementGroup.queryParam_individualId, individualId)
                .setParameter(CashDisbursementGroup.queryParam_groupKey, yearAndMonth)
                .getResultList();
        if (chList.size() > 1) {
            throw new RuntimeException("Only one entity for ChargesMonth is allowed for individualId and year-month!");
        }
        CashDisbursementGroup tcg = (chList.isEmpty()) ? null : chList.get(0);

        if (tcg == null) {
            tcg = new CashDisbursementGroup();
            tcg.setGroupKey(yearAndMonth);
            tcg.setGroupSum(BigDecimal.ZERO);
            tcg.setIndividual(em.find(Individual.class, individualId));
        }
        if (tcg.getCashDisbursementList() == null)
            tcg.setCashDisbursementList(new ArrayList<CashDisbursement>());

        return tcg;
    }

    @GET
    @Path("{individualId}/{yearAndMonth}/xls")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getVacationsXls(@PathParam("individualId") Long individualId,
            @PathParam("yearAndMonth") String yearAndMonth) throws IOException {

        String templateUrl = configurator.getValue(Configurator.XLS_TEMPLATE_PATH_FOR_TRAVEL_COSTS);

        System.out.println(templateUrl);
        CashDisbursementGroupDTO tcg = getCashDisbursementListByMonth(individualId, yearAndMonth);
        Individual individual = em.find(Individual.class, individualId);

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
