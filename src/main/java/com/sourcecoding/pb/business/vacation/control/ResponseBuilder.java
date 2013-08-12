/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sourcecoding.pb.business.vacation.control;

import com.sourcecoding.pb.business.restconfig.DateParameter;
import com.sourcecoding.pb.business.vacation.entity.VacationRecord;
import com.sourcecoding.pb.business.vacation.entity.VacationYear;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;

/**
 *
 * @author Matthias
 */
public class ResponseBuilder {
    
    @Inject
    VacationCalculator vacationCalculator;
    
    public Map<String, Object> buildVacationRecordEntry(VacationRecord vr) {


        Map<String, Object> taskEntry = buildVacationYear(vr.getVacationYear());
        taskEntry.put("processName", "vacation-approval");

        taskEntry.put("approvalState", vr.getApprovalState());
        taskEntry.put("numberOfDays", vr.getNumberOfDays());
        String vacationFrom = DateParameter.valueOf(vr.getVacationFrom());
        String vacationUntil = DateParameter.valueOf(vr.getVacationUntil());
        taskEntry.put("vacationFrom", vacationFrom);
        taskEntry.put("vacationUntil", vacationUntil);
        taskEntry.put("id", vr.getId());


        Map<String, Object> calcDays = vacationCalculator.calcVacationDays(vr.getIndividual(), vacationFrom, vacationUntil);
        taskEntry.put("calcDays", calcDays);


        return taskEntry;
    }
    
    public Map<String, Object> buildVacationYear(VacationYear vy, Map<String, Object>... dataIn) {
        Map<String, Object> data;
        if (dataIn.length > 0) {
            data = dataIn[0];
        } else {
            data = new HashMap<>();
        }

        data.put("individualId", vy.getIndividual().getId());
        data.put("individualNickName", vy.getIndividual().getNickname());
        data.put("vacationManager", vy.getIndividual().getVacationManager().getNickname());

        data.put("vacationYear", vy.getVacationYear());
        data.put("numberOfVacationDays", vy.getNumberOfVacationDays());
        data.put("workDaysPerWeek", vy.getIndividual().getWorkdaysPerWeek());
        data.put("vacationYearBefore", vy.getVacationYear() - 1);

        data.put("residualLeaveYearBefore", vy.getResidualLeaveYearBefore());

        data.put("approvedVacationDays", vy.getApprovedVacationDays());
        data.put("residualLeave", vy.getResidualLeave());


        return data;
    }

    
}
