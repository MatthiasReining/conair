/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sourcecoding.pb.business.perdiemcharges.boundary;

import com.sourcecoding.pb.business.perdiemcharges.entity.PerDiemGroup;
import com.sourcecoding.pb.business.perdiemcharges.entity.PerDiem;
import com.sourcecoding.pb.business.perdiemcharges.entity.TravelExpensesRate;
import com.sourcecoding.pb.business.project.entity.ProjectInformation;
import com.sourcecoding.pb.business.restconfig.DateParameter;
import com.sourcecoding.pb.business.individuals.entity.Individual;
import com.sourcecoding.pb.business.perdiemcharges.entity.PerDiemDTO;
import com.sourcecoding.pb.business.perdiemcharges.entity.PerDiemGroupDTO;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author Matthias
 */
@Path("per-diem")
@Stateless
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PerDiemService {

    @PersistenceContext
    EntityManager em;

    @GET
    @Path("{individualId}/{yearAndMonth}")
    public PerDiemGroupDTO getPerDiemListByMonth(@PathParam("individualId") Long individualId,
            @PathParam("yearAndMonth") String yearAndMonth) {
        int year = Integer.parseInt(yearAndMonth.split("-")[0]);
        int month = Integer.parseInt(yearAndMonth.split("-")[1]);
        PerDiemGroup pdg = getChargesMonth(individualId, yearAndMonth);

        if (pdg == null) {
            pdg = new PerDiemGroup(); //will not be persisted
            pdg.setPerDiemGroupState("");
            pdg.setPerDiemList(new ArrayList<PerDiem>());
        }

        PerDiemGroupDTO pdGroupDTO = new PerDiemGroupDTO();
        pdGroupDTO.indiviudalId = individualId;
        pdGroupDTO.perDiemGroupState = pdg.getPerDiemGroupState();
        pdGroupDTO.yearMonth = yearAndMonth;

        pdGroupDTO.perDiemList = new ArrayList<>();

        Calendar day = Calendar.getInstance();
        day.setTimeInMillis(0);
        day.set(year, month - 1, 0, 0, 0, 0); //start one day before, incr in loop
        Calendar untilDay = Calendar.getInstance();
        untilDay.setTime(day.getTime());
        untilDay.add(Calendar.MONTH, 1);
        while (day.before(untilDay)) {
            day.add(Calendar.DATE, 1);

            PerDiem pd = getPerDiemByDate(pdg.getPerDiemList(), day.getTime());
            PerDiemDTO pdDTO = new PerDiemDTO();
            pdDTO.perDiemDate = day.getTime();
            if (pd != null) {
                pdDTO.charges = pd.getCharges();
                pdDTO.projectId = pd.getProject().getId();
                pdDTO.timeFrom = pd.getTimeFrom();
                pdDTO.timeTo = pd.getTimeTo();
                pdDTO.travelExpenseRateId = pd.getTravelExpensesRate().getId();
            }
            pdGroupDTO.perDiemList.add(pdDTO);
        }
        return pdGroupDTO;
    }

    private PerDiem getPerDiemByDate(List<PerDiem> perDiemList, Date perDiemDate) {
        for (PerDiem pd : perDiemList) {
            if (pd.getPerDiemDate().equals(perDiemDate))
                return pd;
        }
        return null;
    }

    @POST
    @Path("{individualId}")
    public PerDiemGroupDTO updateChargesByMonth(PerDiemGroupDTO perDiemGroupDTO) {
        String yearAndMonth = perDiemGroupDTO.yearMonth;
        int year = Integer.parseInt(yearAndMonth.split("-")[0]);
        int month = Integer.parseInt(yearAndMonth.split("-")[1]);
        Long individualId = perDiemGroupDTO.indiviudalId;

        PerDiemGroup pdg = getChargesMonth(individualId, yearAndMonth);

        Calendar day = Calendar.getInstance();
        day.setTimeInMillis(0);
        day.set(year, month - 1, 0, 0, 0, 0); //start one day before, incr in loop
        Calendar untilDay = Calendar.getInstance();
        untilDay.setTime(day.getTime());
        untilDay.add(Calendar.MONTH, 1);
        while (day.before(untilDay)) {
            day.add(Calendar.DATE, 1);

            PerDiem pd = null;
            for (PerDiem pdLoop : pdg.getPerDiemList()) {
                if (day.getTime().equals(pdLoop.getPerDiemDate())) {
                    pd = pdLoop;
                    break;
                }
            }
            PerDiemDTO pdIn = null;
            for (PerDiemDTO pdLoop : perDiemGroupDTO.perDiemList) {
                if (day.getTime().equals(pdLoop.perDiemDate)) {
                    pdIn = pdLoop;
                    break;
                }
            }
            if (pdIn != null
                    && (pdIn.projectId == 0 || pdIn.timeFrom == null
                    || pdIn.timeTo == null || pdIn.travelExpenseRateId == 0))
                pdIn = null; //set to null

            if (pdIn == null && pd == null)
                break; //nothing todo

            if (pdIn == null && pd != null) {
                pdg.getPerDiemList().remove(pd); //remove
                em.remove(pd);
                break;
            }

            if (pdIn == null)
                throw new RuntimeException("Per Diem is null - this case should not possible!");

            //pdIn is now always != null            
            if (pd == null) {
                pd = new PerDiem();
                pd.setPerDiemDate(day.getTime());
                pd.setPerDiemGroup(pdg);
                pd.setIndividual(pdg.getIndividual());
                pdg.getPerDiemList().add(pd);
            }
            pd.setCharges(pdIn.charges);
            pd.setTimeFrom(pdIn.timeFrom);
            pd.setTimeTo(pdIn.timeTo);
            pd.setProject(em.find(ProjectInformation.class, pdIn.projectId));
            pd.setTravelExpensesRate(em.find(TravelExpensesRate.class, pdIn.travelExpenseRateId));

        }

        em.merge(pdg);

        return getPerDiemListByMonth(individualId, yearAndMonth);

    }

    @PUT
    @Path("travel-expenses-rates")
    public Response uploadTravelExpensesRate(List<TravelExpensesRate> terList) {

        for (TravelExpensesRate ter : terList) {
            em.merge(ter);
        }
        return Response.ok().build();
    }

    @GET
    @Path("travel-expenses-rates/{travelYear}")
    public List<TravelExpensesRate> getTravelExpensesRate(@PathParam("travelYear") Integer travelYear) {
        return em.createNamedQuery(TravelExpensesRate.findByDate, TravelExpensesRate.class)
                .setParameter(TravelExpensesRate.queryParam_travelYear, travelYear)
                .getResultList();
    }

    private PerDiemGroup getChargesMonth(Long individualId, String yearAndMonth) throws RuntimeException {
        List<PerDiemGroup> chList = em.createNamedQuery(PerDiemGroup.findByGroupKey, PerDiemGroup.class)
                .setParameter(PerDiemGroup.queryParam_individualId, individualId)
                .setParameter(PerDiemGroup.queryParam_groupKey, yearAndMonth)
                .getResultList();
        if (chList.size() > 1) {
            throw new RuntimeException("Only one entity for ChargesMonth is allowed for individualId and year-month!");
        }
        PerDiemGroup cm = (chList.isEmpty()) ? null : chList.get(0);
        return cm;
    }
}
