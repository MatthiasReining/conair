/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sourcecoding.pb.business.perdiemcharges.boundary;

import com.sourcecoding.pb.business.configuration.boundary.Configurator;
import com.sourcecoding.pb.business.export.boundary.XlsExportService;
import com.sourcecoding.pb.business.export.control.DataExtractor;
import com.sourcecoding.pb.business.perdiemcharges.entity.PerDiemGroup;
import com.sourcecoding.pb.business.perdiemcharges.entity.PerDiem;
import com.sourcecoding.pb.business.perdiemcharges.entity.TravelExpensesRate;
import com.sourcecoding.pb.business.project.entity.ProjectInformation;
import com.sourcecoding.pb.business.individuals.entity.Individual;
import com.sourcecoding.pb.business.perdiemcharges.entity.PerDiemDTO;
import com.sourcecoding.pb.business.perdiemcharges.entity.PerDiemGroupDTO;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
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

    @Inject
    XlsExportService exportService;

    @Inject
    Configurator configurator;

    @GET
    @Path("{individualId}/{yearAndMonth}")
    public PerDiemGroupDTO getPerDiemListByMonth(@PathParam("individualId") Long individualId,
            @PathParam("yearAndMonth") String yearAndMonth) {
        int year = Integer.parseInt(yearAndMonth.split("-")[0]);
        int month = Integer.parseInt(yearAndMonth.split("-")[1]);
        PerDiemGroup pdg = getChargesMonth(individualId, yearAndMonth);

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
                pdDTO.breakfast = pd.isBreakfast();
                pdDTO.lunch = pd.isLunch();
                pdDTO.dinner = pd.isDinner();
                pdDTO.taxable = pd.isTaxable();
                pdDTO.projectId = pd.getProject().getId();
                pdDTO.timeFrom = (pd.getTimeFrom() == null ? "" : pd.getTimeFrom().toString());
                pdDTO.timeTo = (pd.getTimeTo() == null ? "" : pd.getTimeTo().toString());
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

    @PUT
    @Path("{individualId}")
    public PerDiemGroupDTO updateChargesByMonth(PerDiemGroupDTO perDiemGroupDTO,
            @PathParam("individualId") Long individualId) {
        String yearAndMonth = perDiemGroupDTO.yearMonth;
        int year = Integer.parseInt(yearAndMonth.split("-")[0]);
        int month = Integer.parseInt(yearAndMonth.split("-")[1]);

        PerDiemGroup pdg = getChargesMonth(individualId, yearAndMonth);

        Calendar day = Calendar.getInstance();
        day.setTimeInMillis(0);
        day.set(year, month - 1, 0, 0, 0, 0); //start one day before, incr in loop
        Calendar untilDay = Calendar.getInstance();
        untilDay.setTime(day.getTime());
        untilDay.add(Calendar.MONTH, 1);

        Calendar fromDate = Calendar.getInstance();
        fromDate.setTime(day.getTime());
        fromDate.add(Calendar.DATE, 1);
        pdg.setPerDiemFrom(fromDate.getTime());
        pdg.setPerDiemTo(untilDay.getTime());

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
                    && (pdIn.projectId == null || pdIn.timeFrom == null
                    || pdIn.timeTo == null || pdIn.travelExpenseRateId == null))
                pdIn = null; //set to null

            if (pdIn == null && pd == null)
                continue; //nothing todo

            if (pdIn == null && pd != null) {
                pdg.getPerDiemList().remove(pd); //remove
                //em.remove(pd);
                continue;
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
            pd.setBreakfast(pdIn.breakfast);
            pd.setLunch(pdIn.lunch);
            pd.setDinner(pdIn.dinner);
            pd.setTaxable(pdIn.taxable);
            pd.setTimeFrom(Double.parseDouble(pdIn.timeFrom));
            pd.setTimeTo(Double.parseDouble(pdIn.timeTo));
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
        PerDiemGroup pdg = (chList.isEmpty()) ? null : chList.get(0);

        if (pdg == null) {
            pdg = new PerDiemGroup();
            pdg.setGroupKey(yearAndMonth);
            pdg.setIndividual(em.find(Individual.class, individualId));
        }
        if (pdg.getPerDiemList() == null)
            pdg.setPerDiemList(new ArrayList<PerDiem>());

        return pdg;
    }

    @GET
    @Path("{individualId}/{yearAndMonth}/xls")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getVacationsXls(@PathParam("individualId") Long individualId,
            @PathParam("yearAndMonth") String yearAndMonth) throws IOException {

        //String templateUrl = configurator.getValue(Configurator.XLS_TEMPLATE_PATH_FOR_VACATION_OVERVIEW);
        String templateUrl = "file:///d:/per-diem.xls";
        System.out.println(templateUrl);
        PerDiemGroupDTO pdg = getPerDiemListByMonth(individualId, yearAndMonth);

        List<PerDiemDTO> removePD = new ArrayList<>();
        for (PerDiemDTO pd : pdg.perDiemList) {
            if (pd.travelExpenseRateId == null)
                removePD.add(pd);
        }
        pdg.perDiemList.removeAll(removePD);

        Map<String, Object> payloadMap = new HashMap<>();
        payloadMap.put("perDiemGroup", pdg);

        Map<Long, Object> travelExpensesRateMap = new HashMap<>();
        int year = 2014;//FIXME hard coded year - Integer.parseInt(yearAndMonth.split("-")[0]);        
        for (TravelExpensesRate ter : getTravelExpensesRate(year)) {
            travelExpensesRateMap.put(ter.getId(), ter);
        }
        payloadMap.put("travelExpensesRates", travelExpensesRateMap);
        //{{travelExpensesRates[pd.travelExpenseRateId]}}
        
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        exportService.generate(templateUrl, payloadMap, os);
        String filename = "vacations-" + DataExtractor.getStringValue(payloadMap, "vacationYear") + ".xls";
        return Response.ok(os.toByteArray(), MediaType.APPLICATION_OCTET_STREAM)
                .header("content-disposition", "attachment; filename = " + filename)
                .build();
    }
}
