/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sourcecoding.pb.business.vacation.control;

import com.sourcecoding.pb.business.restconfig.DateParameter;
import com.sourcecoding.pb.business.user.entity.Individual;
import com.sourcecoding.pb.business.vacation.entity.LegalHoliday;
import com.sourcecoding.pb.business.vacation.entity.VacationRecord;
import com.sourcecoding.pb.business.vacation.entity.VacationYear;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Matthias
 */
public class VacationCalculator {

    @PersistenceContext
    EntityManager em;
    
    public void calculateAllVacationDays(VacationYear vy) {
        System.out.println(vy.getResidualLeaveYearBefore() + " - " + vy.getNumberOfVacationDays());
        int total = vy.getResidualLeaveYearBefore() + vy.getNumberOfVacationDays();
        int approvedVacationDays = 0;
        for (VacationRecord vr : vy.getVacationRecords()) {
            if (vr.getApprovalState() == VacationRecord.APPROVAL_STATE_APPROVED) {
                approvedVacationDays += vr.getNumberOfDays();
            }
        }
        vy.setApprovedVacationDays(approvedVacationDays);
        vy.setResidualLeave(total - approvedVacationDays);
    }

    public Map<String, Object> calcVacationDays(Individual individual, String vacationFrom, String vacationUntil) {
        Calendar from = DateParameter.calendarValueOf(vacationFrom); //payload.get(PARAM__VACATION_FROM));
        Calendar until = DateParameter.calendarValueOf(vacationUntil); //payload.get(PARAM__VACATION_UNTIL));

        if (until.before(from)) {
            System.out.println("until before from");
            return null;
        }

        Map<String, Object> result = new HashMap<>();
        result.put("legalHolidays", new ArrayList());
        result.put("vacationDayDate", new ArrayList<String>());

        int duration = 0;
        int workDaysWithoutLegalHoliday = 0;
        while (from.getTimeInMillis() <= until.getTimeInMillis()) {
            boolean sunday = (from.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY);
            boolean saturday = (from.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY);
            List<LegalHoliday> legalHolidayList = em.createNamedQuery(LegalHoliday.findByDate, LegalHoliday.class)
                    .setParameter(LegalHoliday.queryParam_date, from.getTime())
                    .getResultList();
            Map<String, String> legalHoliday = new HashMap<>();
            if (!legalHolidayList.isEmpty()) {
                legalHoliday.put("date", DateParameter.valueOf(from));
                legalHoliday.put("name", legalHolidayList.get(0).getLegalHolidyName());
                ((List) result.get("legalHolidays")).add(legalHoliday);
            }
            if (!saturday && !sunday) {
                workDaysWithoutLegalHoliday++;
            }
            if (!saturday && !sunday && legalHoliday.isEmpty()) {
                System.out.println(from.getTime() + " arbeitstag ");
                ((List<String>) result.get("vacationDayDate")).add(DateParameter.valueOf(from));
                duration++;
            }

            from.add(Calendar.DATE, 1);
        }

        int vacationWeeks = workDaysWithoutLegalHoliday / 5;
        int reduceVacationDaysPerWeek = 5 - individual.getWorkdaysPerWeek();
        int reduceVacationDays = vacationWeeks * reduceVacationDaysPerWeek;

        duration = duration - reduceVacationDays;

        result.put("vacationDays", duration);

        return result;
    }
}
