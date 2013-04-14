/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sourcecoding.pb.business.workinghours.control;

import com.sourcecoding.pb.business.workinghours.entity.WorkingDay;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Matthias
 */
public class TimeRecordingStore {

    @PersistenceContext
    EntityManager em;


    public List<WorkingDay> getWorkingTimePackage(Long individualId, Date from, Date until) {

        List<WorkingDay> workingDayList = em.createNamedQuery(WorkingDay.findWorkingDayRange, WorkingDay.class)
                .setParameter(WorkingDay.findWorkingDayRange_Param_user, individualId)
                .setParameter(WorkingDay.findWorkingDayRange_Param_startDate, from)
                .setParameter(WorkingDay.findWorkingDayRange_Param_endDate, until)
                .getResultList();

        return workingDayList;

    }
}
