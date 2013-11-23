/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sourcecoding.pb.business.accounting.boundary;

import com.sourcecoding.pb.business.accounting.controller.AccountingTimeController;
import com.sourcecoding.pb.business.accounting.entity.AccountingPeriod;
import com.sourcecoding.pb.business.export.boundary.XlsExportService;
import com.sourcecoding.pb.business.export.control.DataExtractor;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
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

    @GET
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
        accountingMap.put("accountingDate", "2013-11-23");

        Map<String, Object> peopleByRole = new LinkedHashMap<>();
        accountingMap.put("peopleByRole", peopleByRole);

        List<Map<String, Object>> personList = new ArrayList<>();

        peopleByRole.put("Senior Consultant", personList);

        Map<String, Object> person = new HashMap<>();
        personList.add(person);
        person.put("lastname", "Reichert");
        person.put("firstname", "Jürgen");
        person.put("totalHours", "152");
        person.put("totalDays", "19");
        person.put("pricePerHour", "118,75");
        person.put("totalPrice", "18050,00");

        person = new HashMap<>();
        personList.add(person);
        person.put("lastname", "Mustermann");
        person.put("firstname", "Max");
        person.put("totalHours", "120");
        person.put("totalDays", "15");
        person.put("pricePerHour", "118,75");
        person.put("totalPrice", "14000,00");

        ByteArrayOutputStream os = new ByteArrayOutputStream();

        exportService.generate(templateName, accountingMap, os);
        String filename = "accounting-" + projectId + "-" + periodId + ".xls";
        return Response.ok(os.toByteArray(), MediaType.APPLICATION_OCTET_STREAM)
                .header("content-disposition", "attachment; filename = " + filename)
                .build();
    }
}
