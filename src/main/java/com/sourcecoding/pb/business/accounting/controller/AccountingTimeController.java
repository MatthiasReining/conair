/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sourcecoding.pb.business.accounting.controller;

import com.sourcecoding.pb.business.accounting.entity.AccountingPeriod;
import com.sourcecoding.pb.business.accounting.entity.AccountingTimeDetail;
import com.sourcecoding.pb.business.project.entity.ProjectInformation;
import com.sourcecoding.pb.business.timerecording.entity.TimeRecord;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Matthias
 */
public class AccountingTimeController {

    @PersistenceContext
    EntityManager em;

    public void collectTimeRecords(Long projectId, Date periodFrom, Date periodTo) {

        ProjectInformation projectInformation = em.find(ProjectInformation.class, projectId);
        
        AccountingPeriod ap = new AccountingPeriod();
        ap.setPeriodFrom(periodFrom);
        ap.setPeriodTo(periodTo);
        ap.setProjectInformation(projectInformation);
        ap = em.merge(ap);

        List<TimeRecord> trList = em.createNamedQuery(TimeRecord.findTimeRecordsInARangeByProjectAndStatus, TimeRecord.class)
                .setParameter(TimeRecord.queryParam_project, projectInformation)
                .setParameter(TimeRecord.queryParam_status, 0)
                .setParameter(TimeRecord.queryParam_startDate, periodFrom)
                .setParameter(TimeRecord.queryParam_endDate, periodTo)
                .getResultList();
        for (TimeRecord tr : trList) {
            //tr.setStatus(ap.getId());
            AccountingTimeDetail adt = new AccountingTimeDetail(tr);
            adt.setAccountingPeriod(ap);
            adt.setStatus("N");
            ap.getAccoutingTimeDetails().add(adt);            
        }

    }

}
