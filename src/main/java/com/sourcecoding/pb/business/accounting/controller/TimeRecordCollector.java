/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sourcecoding.pb.business.accounting.controller;

import com.sourcecoding.pb.business.accounting.entity.AccountingPeriod;
import com.sourcecoding.pb.business.accounting.entity.AccountingTimeDetail;
import com.sourcecoding.pb.business.project.entity.ProjectInformation;
import com.sourcecoding.pb.business.project.entity.ProjectMember;
import com.sourcecoding.pb.business.timerecording.entity.TimeRecord;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Matthias
 */
public class TimeRecordCollector {

    @PersistenceContext
    EntityManager em;

    public AccountingPeriod collectTimeRecords(Long projectId, Date periodFrom, Date periodTo) {

        ProjectInformation projectInformation = em.find(ProjectInformation.class, projectId);
        
        AccountingPeriod ap = new AccountingPeriod();
        ap.setPeriodFrom(periodFrom);
        ap.setPeriodTo(periodTo);
        ap.setProjectInformation(projectInformation);
        ap.setAccountingStatus(0);
                
        ap.setTaxRate(new BigDecimal("19")); //FIXME configure
        ap.setAccountingNumber("2-fix"); //FIXME number generator
        ap.setAccountingCurrency("EUR"); //FIXME currency is hard coded
        
        
        ap = em.merge(ap);
        
        BigDecimal apPrice = new BigDecimal("0.0");

        List<TimeRecord> trList = em.createNamedQuery(TimeRecord.findTimeRecordsInARangeByProjectAndStatus, TimeRecord.class)
                .setParameter(TimeRecord.queryParam_project, projectInformation)
                .setParameter(TimeRecord.queryParam_status, 0L)
                .setParameter(TimeRecord.queryParam_startDate, periodFrom)
                .setParameter(TimeRecord.queryParam_endDate, periodTo)
                .getResultList();
        for (TimeRecord tr : trList) {
            
            tr.setStatus(ap.getId());
            
            AccountingTimeDetail adt = new AccountingTimeDetail(tr);
            adt.setAccountingPeriod(ap);
            adt.setStatus("N");
            
            //TODO check if select is to slow for ever entry... date is changed for every iteration
            ProjectMember pm = em.createNamedQuery(ProjectMember.findByDateRange, ProjectMember.class)
                    .setParameter(ProjectMember.queryParam_individual, tr.getUser())
                    .setParameter(ProjectMember.queryParam_project, projectInformation)
                    .setParameter(ProjectMember.queryParam_date, tr.getWorkingDay())
                    .getSingleResult();
            
            BigDecimal priceHour = pm.getPriceHour();
            System.out.println( pm.getPriceHour());
            //use minutes for calculation
            //
            BigDecimal price = new BigDecimal( priceHour.doubleValue() / 60 * tr.getWorkingTime() ).setScale(2, RoundingMode.HALF_UP);
            adt.setPriceHour(priceHour);
            adt.setPrice(price);
            ap.getAccoutingTimeDetails().add(adt);  
            
            apPrice = apPrice.add(price);            
        }
        ap.setPrice(apPrice);

        return ap;
    }

}
