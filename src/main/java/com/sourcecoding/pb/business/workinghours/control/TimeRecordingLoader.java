/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sourcecoding.pb.business.workinghours.control;

import com.sourcecoding.pb.business.project.entity.WorkPackage;
import com.sourcecoding.pb.business.workinghours.entity.WorkPackageDescription;
import com.sourcecoding.pb.business.workinghours.entity.WorkingDay;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Matthias
 */
public class TimeRecordingLoader {

    @PersistenceContext
    EntityManager em;


    public List<WorkingDay> getWorkingTimePackage(Long individualId, Date from, Date until) {

        List<WorkingDay> workingDayList = em.createNamedQuery(WorkingDay.findWorkingDayRange, WorkingDay.class)
                .setParameter(WorkingDay.queryParam_user, individualId)
                .setParameter(WorkingDay.queryParam_startDate, from)
                .setParameter(WorkingDay.queryParam_endDate, until)
                .getResultList();

        return workingDayList;

    }
}
