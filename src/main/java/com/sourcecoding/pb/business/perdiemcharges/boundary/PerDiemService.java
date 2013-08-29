/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sourcecoding.pb.business.perdiemcharges.boundary;

import com.sourcecoding.pb.business.perdiemcharges.entity.ChargesMonth;
import com.sourcecoding.pb.business.perdiemcharges.entity.PerDiem;
import com.sourcecoding.pb.business.perdiemcharges.entity.TravelExpensesRate;
import com.sourcecoding.pb.business.project.entity.ProjectInformation;
import com.sourcecoding.pb.business.restconfig.DateParameter;
import com.sourcecoding.pb.business.individuals.entity.Individual;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
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

    @PUT
    @Path("{individualId}/{year}-{month}")
    public Map<String, Object> createOrUpdateChargesByMonth(
            @PathParam("individualId") Long individualId,
            @PathParam("year") String year, @PathParam("month") String month, Map<String, Object> chargesDTMap) {
        String byMonth = year + "-" + month;
        Individual individual = em.find(Individual.class, individualId);

        ChargesMonth cm = getChargesMonth(individualId, byMonth);
        if (cm == null) {
            cm = new ChargesMonth();
            cm.setChargesMonth(byMonth);
            cm.setIndividual(individual);
            cm.setChargesState("OK");
            cm.setPerDiems(new ArrayList<PerDiem>());
        }


        Map<String, PerDiem> pdEntities = new HashMap<>();
        for (PerDiem pd : cm.getPerDiems()) {
            pdEntities.put(DateParameter.valueOf(pd.getPerDiemDate()), pd);
        }


        for (Map<String, Object> pdJson : ((List<Map<String, Object>>) chargesDTMap.get("perDiems"))) {
            String day = String.valueOf(pdJson.get("day"));
            String chargesText = String.valueOf(pdJson.get("charges"));
            System.out.println("chargesText: " + chargesText);
            double charges = (chargesText == null || chargesText.isEmpty()) ? 0 : Double.valueOf(chargesText);
            PerDiem pd = pdEntities.get(day);
            if (charges == 0) {
                if (pd != null) {
                    em.remove(pd);
                    cm.getPerDiems().remove(pd);
                }
                continue;
            }
            if (pd == null) {
                pd = new PerDiem();
                cm.getPerDiems().add(pd);
                pd.setChargesMonth(cm);
                pd.setPerDiemDate(DateParameter.valueOf(day));
                pd.setIndividual(individual);
            }

            pd.setCharges(new BigDecimal(charges));
            //FIXME use also inServiceFrom/to
            String inServiceFrom = String.valueOf(pdJson.get("inServiceFrom"));
            String inServiceTo = String.valueOf(pdJson.get("inServiceTo"));

            System.out.println("fullTime: " + pdJson.get("fullTime"));
            Object ftObj = pdJson.get("fullTime");
            if (String.valueOf(ftObj).isEmpty()) pdJson.put("fullTime", false);
            
            pd.setFulltime((Boolean)pdJson.get("fullTime"));

            String projectId = String.valueOf(pdJson.get("projectId"));
            pd.setProject(em.find(ProjectInformation.class, Long.valueOf(projectId)));
            String travelExpensesRateId = String.valueOf(pdJson.get("travelExpensesRateId"));
            pd.setTravelExpensesRate(em.find(TravelExpensesRate.class, Long.valueOf(travelExpensesRateId)));

        }

        cm = em.merge(cm);
        return buildDTMap(individualId, byMonth, cm);
    }

    @GET
    @Path("{individualId}/{year}-{month}")
    public Map<String, Object> getChargesByMonth(@PathParam("individualId") Long individualId,
            @PathParam("year") String year, @PathParam("month") String month) {
        String byMonth = year + "-" + month;
        ChargesMonth cm = getChargesMonth(individualId, byMonth);
        Map<String, Object> result = buildDTMap(individualId, byMonth, cm);

        return result;

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
    @Path("travel-expenses-rates")
    public List<TravelExpensesRate> getTravelExpensesRate() {
        return em.createNamedQuery(TravelExpensesRate.findByDate, TravelExpensesRate.class).getResultList();
    }

    private Map<String, Object> buildDTMap(Long individualId, String byMonth, ChargesMonth cm) {
        String year = byMonth.split("-")[0];
        String month = byMonth.split("-")[1];

        Map<String, Object> result = new HashMap<>();
        result.put("individualId", individualId);
        result.put("chargesMonth", byMonth);
        result.put("chargesState", "OK"); //FIXME state is hard coded!


        Calendar cal = new GregorianCalendar(Integer.valueOf(year), Integer.valueOf(month) - 1, 1, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);


        LinkedHashMap<String, Object> days = new LinkedHashMap<>();
        while ((Integer.valueOf(month) - 1) == cal.get(Calendar.MONTH)) {
            String date = DateParameter.valueOf(cal.getTime());
            Map<String, Object> dayValues = new HashMap<>();
            dayValues.put("day", date);
            dayValues.put("inServiceFrom", "");
            dayValues.put("inServiceTo", "");
            dayValues.put("fullTime", "");
            dayValues.put("projectId", "");
            dayValues.put("travelExpensesRateId", "");
            dayValues.put("charges", "");

            days.put(date, dayValues);

            cal.add(Calendar.DATE, 1);
        }


        if (cm != null) {
            for (PerDiem pd : cm.getPerDiems()) {
                System.out.println(DateParameter.valueOf(pd.getPerDiemDate()));
                System.out.println(days);
                Map<String, Object> dayValues = (Map<String, Object>) days.get(DateParameter.valueOf(pd.getPerDiemDate()));
                if (dayValues == null) {
                    throw new RuntimeException("Month match failed! (" + pd.getPerDiemDate() + ")");
                }
                dayValues.put("inServiceFrom", pd.getInServiceFrom());
                dayValues.put("inServiceTo", pd.getInServiceTo());
                dayValues.put("fullTime", pd.getFulltime());
                dayValues.put("projectId", pd.getProject().getId());
                dayValues.put("projectKey", pd.getProject().getProjectKey());
                dayValues.put("projectName", pd.getProject().getName());
                dayValues.put("travelExpensesRateId", pd.getTravelExpensesRate().getId());
                dayValues.put("charges", pd.getCharges());
            }
        }

        result.put("perDiems", days.values());

        return result;
    }

    private ChargesMonth getChargesMonth(Long individualId, String byMonth) throws RuntimeException {
        List<ChargesMonth> chList = em.createNamedQuery(ChargesMonth.findByMonth, ChargesMonth.class)
                .setParameter(ChargesMonth.queryParam_individualId, individualId)
                .setParameter(ChargesMonth.queryParam_yearMonth, byMonth)
                .getResultList();
        if (chList.size() > 1) {
            throw new RuntimeException("Only one entity for ChargesMonth is allowed for individualId and year-month!");
        }
        ChargesMonth cm = (chList.isEmpty()) ? null : chList.get(0);
        return cm;
    }
}
