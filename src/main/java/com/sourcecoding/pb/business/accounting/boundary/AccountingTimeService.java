/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sourcecoding.pb.business.accounting.boundary;

import com.sourcecoding.pb.business.accounting.controller.AccountingTimeController;
import com.sourcecoding.pb.business.accounting.entity.AccountingContainer;
import com.sourcecoding.pb.business.accounting.entity.AccountingPeriod;
import com.sourcecoding.pb.business.accounting.entity.AccountingPeriodDTO;
import com.sourcecoding.pb.business.export.boundary.XlsExportService;
import com.sourcecoding.pb.business.individuals.entity.Individual;
import com.sourcecoding.pb.business.project.entity.ProjectInformation;
import com.sourcecoding.pb.business.restconfig.DateParameter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
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
    @Path("{projectKey}")
    public Response getAccountingPeriods(@PathParam("projectKey") String projectKey) {

        ProjectInformation project = em.createNamedQuery(ProjectInformation.findByKey, ProjectInformation.class)
                .setParameter(ProjectInformation.queryParam_projectKey, projectKey)
                .getSingleResult();

        List<AccountingPeriod> apList = em.createNamedQuery(AccountingPeriod.findByProject, AccountingPeriod.class)
                .setParameter(AccountingPeriod.queryParam_project, project)
                .getResultList();

        List<AccountingPeriodDTO> payload = AccountingPeriodDTO.create(apList);

        return Response.ok(payload).build();
    }

    @GET
    @Path("{projectKey}/periods/{apId}")
    public Response getAccountingPeriodDetails(@PathParam("projectKey") String projectKey,
            @PathParam("apId") Long apId) {

        AccountingPeriod ap = em.find(AccountingPeriod.class, apId);

        AccountingPeriodDTO payload = AccountingPeriodDTO.create(ap, true);
        return Response.ok(payload).build();
    }

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

        AccountingContainer ac = new AccountingContainer();

        ac.accountingDate = new Date();
        ac.accountingNumber = "2";

        ac.accountingPeriodFrom = DateParameter.valueOf(ap.getPeriodFrom());
        ac.accountingPeriodTo = DateParameter.valueOf(ap.getPeriodTo());
        ac.projectKey = ap.getProjectInformation().getProjectKey();
        ac.projectName = ap.getProjectInformation().getName();
        ac.taxRate = ap.getTaxRate().longValue();

        List<Object[]> result = em.createQuery("SELECT pm.title, atd.user, atd.priceHour, SUM(atd.workingTime), SUM(atd.price) FROM AccountingTimeDetail atd, ProjectMember pm WHERE atd.user = pm.individual AND atd.accountingPeriod = :period GROUP BY pm.title, atd.user, atd.priceHour")
                .setParameter("period", ap)
                .getResultList();

        for (Object[] o : result) {
            String title = (String) o[0];
            Individual individual = (Individual) o[1];
            BigDecimal priceHour = (BigDecimal) o[2];
            Long workingTime = (Long) o[3];
            BigDecimal price = (BigDecimal) o[4];

            ac.addPerson(ac.new Person(
                    title, "TODO_lastname", individual.getNickname(),
                    workingTime / 60.0, workingTime / 60.0 / 8.0, priceHour, price));
            //important to use double values 60.0, 8.0
        }

        ByteArrayOutputStream os = new ByteArrayOutputStream();

        exportService.generate(templateName, ac, os);
        String filename = "accounting-" + projectId + "-" + periodId + ".xls";
        return Response.ok(os.toByteArray(), MediaType.APPLICATION_OCTET_STREAM)
                .header("content-disposition", "attachment; filename = " + filename)
                .build();
    }

}
