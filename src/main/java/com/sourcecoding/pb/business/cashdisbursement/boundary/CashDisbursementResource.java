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

        CashDisbursementGroup cdg = getCDByMonth(individualId, yearAndMonth);

        List<CashDisbursement> cd2remove = new ArrayList<>();
        for (CashDisbursement cd : cdg.getCashDisbursementList()) {

            CashDisbursementDTO cdDTO = cashDisbursementGroupDTO.findCashDisbursementDTOById(cd.getId());
            if (cdDTO != null) {
                //update cd
                cd.setAmount(cdDTO.amount);
                cd.setCdCategory(cdDTO.cdCategory);
                cd.setCdDate(cdDTO.cdDate);
                cd.setDescription(cdDTO.description);
                //remove from dto list, it's worked off
                cashDisbursementGroupDTO.cashDisbursementList.remove(cdDTO);
            } else {
                //remove cd
                cd2remove.add(cd);
            }

        }
        cdg.getCashDisbursementList().removeAll(cd2remove);

        for (CashDisbursementDTO cdDTO : cashDisbursementGroupDTO.cashDisbursementList) {
            if (cdDTO.cdDate == null || cdDTO.amount == null)
                continue;
            //create new entity
            CashDisbursement cd = new CashDisbursement();
            cd.setCdDate(cdDTO.cdDate);
            cd.setCashDisbursementGroup(cdg);
            cd.setIndividual(cdg.getIndividual());
            cdg.getCashDisbursementList().add(cd);
            cd.setAmount(cdDTO.amount);
            cd.setCdCategory(cdDTO.cdCategory);
            cd.setDescription(cdDTO.description);
        }

        cdg.setGroupSum(BigDecimal.valueOf(cashDisbursementGroupDTO.sum));
        em.merge(cdg);

        return getCashDisbursementListByMonth(individualId, yearAndMonth);

    }

    private CashDisbursementGroup getCDByMonth(Long individualId, String yearAndMonth) throws RuntimeException {
        List<CashDisbursementGroup> chList = em.createNamedQuery(CashDisbursementGroup.findByGroupKey, CashDisbursementGroup.class)
                .setParameter(CashDisbursementGroup.queryParam_individualId, individualId)
                .setParameter(CashDisbursementGroup.queryParam_groupKey, yearAndMonth)
                .getResultList();

        if (chList.size() > 1)
            throw new RuntimeException("Only one entity for ChargesMonth is allowed for individualId and year-month!");

        CashDisbursementGroup cdg = (chList.isEmpty()) ? null : chList.get(0);

        if (cdg == null) {
            cdg = new CashDisbursementGroup();
            cdg.setGroupKey(yearAndMonth);
            cdg.setGroupSum(BigDecimal.ZERO);
            cdg.setIndividual(em.find(Individual.class, individualId));

            int year = Integer.parseInt(yearAndMonth.split("-")[0]);
            int month = Integer.parseInt(yearAndMonth.split("-")[1]);

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
        }

        if (cdg.getCashDisbursementList()
                == null)
            cdg.setCashDisbursementList(new ArrayList<CashDisbursement>());

        return cdg;
    }

    @GET
    @Path("{individualId}/{yearAndMonth}/xls")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getReimbursementOfExpesesXls(@PathParam("individualId") Long individualId,
            @PathParam("yearAndMonth") String yearAndMonth) throws IOException {

        String templateUrl = configurator.getValue(Configurator.XLS_TEMPLATE_PATH_FOR_REIMBURSEMENT_OF_EXPENSES);

        CashDisbursementGroupDTO cdg = getCashDisbursementListByMonth(individualId, yearAndMonth);
        Individual individual = em.find(Individual.class, individualId);

        Map<String, Object> payloadMap = new HashMap<>();

        payloadMap.put("roe", cdg);
        payloadMap.put("individual", individual);

        ByteArrayOutputStream os = new ByteArrayOutputStream();

        exportService.generate(templateUrl, payloadMap, os);
        String filename = yearAndMonth + "-roe-" + individual.getFirstname().toLowerCase() + ".xls";

        return Response.ok(os.toByteArray(), MediaType.APPLICATION_OCTET_STREAM)
                .header("content-disposition", "attachment; filename = " + filename)
                .build();
    }

}
