/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sourcecoding.pb.business.vacation.boundary;

import com.sourcecoding.pb.business.authentication.boundary.CurrentUser;
import com.sourcecoding.pb.business.authentication.entity.User;
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
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author Matthias
 */
@Path("vacations")
@Stateless
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class VacationService {

    @PersistenceContext
    EntityManager em;
    @Inject
    @CurrentUser
    User currentUser;

    @GET
    public Map<String, Object> getVactions(@QueryParam("individualId") Long individualId) {
        Map<String, Object> result = new HashMap<>();

        if (individualId == null) {
            individualId = currentUser.getId();
        }
        Individual individual = em.find(Individual.class, individualId);
        Integer year = Calendar.getInstance().get(Calendar.YEAR);

        System.out.println("vor select");
        List<VacationYear> vyList = em.createNamedQuery(VacationYear.findByDate, VacationYear.class)
                .setParameter(VacationYear.queryParam_individual, individual)
                .setParameter(VacationYear.queryParam_year, year)
                .getResultList();
        if (vyList.isEmpty()) {
            return result;
        }
        System.out.println("nach select");

        VacationYear vy = vyList.get(0);
        result.put("individualId", vy.getIndividual().getId());
        result.put("numberOfVacationDays", vy.getNumberOfVacationDays());
        result.put("residualLeaveYearBefore", vy.getResidualLeaveYearBefore());
        result.put("residualLeave", vy.getResidualLeave());
        result.put("vacationYear", vy.getVacationYear());
        result.put("vacationYearBefore", vy.getVacationYear() - 1);
        List<Map<String, Object>> vacationRecords = new ArrayList<>();
        result.put("vacationRecords", vacationRecords);
        List<String> vacationDays = new ArrayList<>();
        result.put("vacationDays", vacationDays);


        int vacationDaysThisYear = 0;
        for (VacationRecord vr : vy.getVacationRecords()) {
            Map<String, Object> vrMap = new HashMap<>();
            vacationRecords.add(vrMap);
            vrMap.put("approvalState", vr.getApprovalState());
            vrMap.put("numberOfDays", vr.getNumberOfDays());
            vacationDaysThisYear += vr.getNumberOfDays();
            String vacationFrom = DateParameter.valueOf(vr.getVacationFrom());
            String vacationUntil = DateParameter.valueOf(vr.getVacationUntil());
            vrMap.put("vacationFrom", vacationFrom);
            vrMap.put("vacationUntil", vacationUntil);
            vrMap.put("id", vr.getId());

            Map<String, Object> vacationDaysMap = calculateVacationDays(individualId, vacationFrom, vacationUntil);
            List<String> vacationDayDates = (List<String>) vacationDaysMap.get("vacationDayDate");
            vacationDays.addAll(vacationDayDates);
        }

        int numberOfVacationDaysTotal = vy.getResidualLeaveYearBefore() + vy.getNumberOfVacationDays();
        result.put("numberOfVacationDaysTotal", numberOfVacationDaysTotal);
        result.put("vacationDaysThisYear", vacationDaysThisYear);


        return result;
    }

    @Path("{id}")
    @DELETE
    @Consumes(MediaType.WILDCARD)
    public void deleteVacationRecord(@PathParam("id") String idText) {

        Long id = Long.valueOf(idText);

        VacationRecord vr = em.find(VacationRecord.class, id);
        VacationYear vy = vr.getVacationYear();
        vy.getVacationRecords().remove(vr);
        em.remove(vr);
        calculateAllVacationDays(vy);

    }

    @Path("{individualId}/calculateVacationDays")
    @GET
    public Map<String, Object> calculateVacationDays(@PathParam("individualId") Long individualId,
            @QueryParam("vacationFrom") String vacationFrom,
            @QueryParam("vacationUntil") String vacationUntil) {
        Calendar from = DateParameter.calendarValueOf(vacationFrom); //payload.get(PARAM__VACATION_FROM));
        Calendar until = DateParameter.calendarValueOf(vacationUntil); //payload.get(PARAM__VACATION_UNTIL));

        if (until.before(from)) {
            System.out.println("until before from");
            return null;
        }

        Individual individual = em.find(Individual.class, individualId);


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
            if (!saturday && !sunday)
                workDaysWithoutLegalHoliday++;
            if (!saturday && !sunday && legalHoliday.isEmpty()) {
                System.out.println(from.getTime() + " arbeitstag ");
                ((List<String>) result.get("vacationDayDate")).add(DateParameter.valueOf(from));
                duration++;
            }

            from.add(Calendar.DATE, 1);
        }

        int vacationWeeks = workDaysWithoutLegalHoliday / 5;
        int reduceVacationDaysPerWeek = 5- individual.getWorkdaysPerWeek();
        int reduceVacationDays = vacationWeeks * reduceVacationDaysPerWeek;
        
        duration = duration - reduceVacationDays;

        result.put("vacationDays", duration);

        return result;
    }

    @POST
    public Map<String, Object> issueRequestForTimeOff(Map<String, Object> payload) {
        Long individualId = ((Integer) payload.get("individualId")).longValue();
        if (individualId == 0) {
            individualId = currentUser.getId();
        }

        Individual individual = em.find(Individual.class, individualId);

        Calendar from = DateParameter.calendarValueOf(payload.get("vacationFrom").toString());
        Integer year = from.get(Calendar.YEAR);

        List<VacationYear> vyList = em.createNamedQuery(VacationYear.findByDate, VacationYear.class)
                .setParameter(VacationYear.queryParam_individual, individual)
                .setParameter(VacationYear.queryParam_year, year)
                .getResultList();
        VacationYear vy;
        if (vyList.isEmpty()) {
            vy = new VacationYear();
            vy.setIndividual(individual);
            System.out.println("vacation days per year: " + individual.getVacationDaysPerYear());
            vy.setNumberOfVacationDays(individual.getVacationDaysPerYear());
            vy.setResidualLeaveYearBefore(0);
            vy.setResidualLeave(vy.getNumberOfVacationDays() + vy.getResidualLeaveYearBefore());
            vy.setVacationYear(year);
            vy = em.merge(vy);

        } else {
            vy = vyList.get(0);
        }
        if (vy.getVacationRecords() == null) {
            vy.setVacationRecords(new ArrayList<VacationRecord>());
        }
        List<VacationRecord> vrList = vy.getVacationRecords();


        VacationRecord vr = new VacationRecord();
        vr.setVacationYear(vy);
        vr.setIndividual(individual);
        vr.setNumberOfDays((Integer) payload.get("numberOfDays"));
        vr.setVacationFrom(DateParameter.valueOf(payload.get("vacationFrom").toString()));
        vr.setVacationUntil(DateParameter.valueOf(payload.get("vacationUntil").toString()));
        vr.setApprovalState(0);

        vrList.add(vr);


        calculateAllVacationDays(vy);

        return null;
    }

    private void calculateAllVacationDays(VacationYear vy) {
        System.out.println(vy.getResidualLeaveYearBefore() + " - " + vy.getNumberOfVacationDays());
        int total = vy.getResidualLeaveYearBefore() + vy.getNumberOfVacationDays();
        int vacationDays = 0;
        for (VacationRecord vr : vy.getVacationRecords()) {
            vacationDays += vr.getNumberOfDays();
        }
        vy.setResidualLeave(total - vacationDays);
    }
}
