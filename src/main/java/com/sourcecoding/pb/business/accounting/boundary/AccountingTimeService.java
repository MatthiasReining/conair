/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sourcecoding.pb.business.accounting.boundary;

import com.sourcecoding.pb.business.accounting.controller.AccountingTimeController;
import com.sourcecoding.pb.business.accounting.entity.AccountingPeriod;
import com.sourcecoding.pb.business.accounting.entity.AccountingTimeDetail;
import com.sourcecoding.pb.business.export.boundary.XlsExportService;
import com.sourcecoding.pb.business.individuals.entity.Individual;
import com.sourcecoding.pb.business.restconfig.DateParameter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.GET;
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
@Path("accounting")
public class AccountingTimeService {

    @Inject
    AccountingTimeController atc;

    @PersistenceContext
    EntityManager em;

    @Inject
    XlsExportService exportService;

    @GET //TODO change to post or put
    @Path("projects/{projectId}")
    public Response collectTimeRecords(@PathParam("projectId") Long projectId) {

        Date periodTo = new Date();
        Date periodFrom = null;

        if (periodFrom == null) {
            periodFrom = new Date(0L); //1.1.1970
        }

        atc.collectTimeRecords(projectId, periodFrom, periodTo);

        return Response.ok().build();
    }

    @GET
    @Path("projects/{projectId}/periods/{periodId}/xls")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getAccountingPeriodXLS(@PathParam("projectId") Long projectId, @PathParam("periodId") Long periodId) throws IOException {

        System.out.println("periodId:" + periodId);
        AccountingPeriod ap = em.find(AccountingPeriod.class, periodId);

        String templateName = "accounting-template";

        Map<String, Object> accountingMap = new HashMap<>();
        accountingMap.put("accountingDate", DateParameter.valueOf(new Date()));

        accountingMap.put("accountingPeriodFrom", DateParameter.valueOf(ap.getPeriodFrom()));
        accountingMap.put("accountingPeriodTo", DateParameter.valueOf(ap.getPeriodTo()));
        accountingMap.put("projectKey", ap.getProjectInformation().getProjectKey());
        accountingMap.put("projectName", ap.getProjectInformation().getName());
        accountingMap.put("accountingNumber", "2");

        List<Object[]> result = em.createQuery("SELECT pm.title, atd.user, atd.priceHour, SUM(atd.workingTime), SUM(atd.price) FROM AccountingTimeDetail atd, ProjectMember pm WHERE atd.user = pm.individual AND atd.accountingPeriod = :period GROUP BY pm.title, atd.user, atd.priceHour")
                .setParameter("period", ap)
                .getResultList();
        System.out.println("--> nach query: " + result);

        Map<String, Map<String, Object>> peopleByRole = new LinkedHashMap<>();
        accountingMap.put("peopleByRole", peopleByRole);
        
        BigDecimal totalNet = new BigDecimal("0.0");

        for (Object[] o : result) {
            String title = (String) o[0];
            Individual individual = (Individual) o[1];
            BigDecimal priceHour = (BigDecimal) o[2];
            Long workingTime = (Long) o[3];
            BigDecimal price = (BigDecimal) o[4];

            Map<String, Object> peopleByRoleContent;
            List<Map<String, Object>> personList;
            if (peopleByRole.containsKey(title)) {
                peopleByRoleContent = peopleByRole.get(title);
            } else {
                peopleByRoleContent = new HashMap<>();
                personList = new ArrayList<>();
                peopleByRoleContent.put("people", personList);
                peopleByRoleContent.put("sum", new BigDecimal("0.0"));
                peopleByRole.put(title, peopleByRoleContent);
            }
            personList = (List<Map<String, Object>>) peopleByRoleContent.get("people");
            
            Map<String, Object> person = new HashMap<>();
            personList.add(person);
            person.put("lastname", "TODO_lastname");
            person.put("firstname", individual.getNickname());
            person.put("totalHours", workingTime / 60.0); //important to use double values 60.0
            person.put("totalDays", workingTime / 60.0 / 8.0); //important to use double values 8.0
            person.put("pricePerHour", priceHour);
            person.put("totalPrice", price);
            
            BigDecimal sum = (BigDecimal) peopleByRoleContent.get("sum");
            peopleByRoleContent.put("sum", sum.add(price));
            
            totalNet = totalNet.add(price);

        }

        double taxFactor = ap.getTaxRate().doubleValue() / 100;
        
        accountingMap.put("taxRate", ap.getTaxRate().longValue());
        accountingMap.put("accountingNet", totalNet);
        accountingMap.put("accountingTax", totalNet.doubleValue() * taxFactor);
        accountingMap.put("accountingGross", totalNet.doubleValue() * (1+taxFactor));


        ByteArrayOutputStream os = new ByteArrayOutputStream();

        exportService.generate(templateName, accountingMap, os);
        String filename = "accounting-" + projectId + "-" + periodId + ".xls";
        return Response.ok(os.toByteArray(), MediaType.APPLICATION_OCTET_STREAM)
                .header("content-disposition", "attachment; filename = " + filename)
                .build();
    }
}
