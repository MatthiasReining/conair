/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sourcecoding.pb.business.timerecording.boundary;

import com.sourcecoding.pb.business.authentication.boundary.CurrentUser;
import com.sourcecoding.pb.business.authentication.entity.User;
import com.sourcecoding.pb.business.project.entity.WorkPackage;
import com.sourcecoding.pb.business.restconfig.DateParameter;
import com.sourcecoding.pb.business.individuals.entity.Individual;
import com.sourcecoding.pb.business.project.entity.ProjectInformation;
import com.sourcecoding.pb.business.timerecording.entity.TimeRecord;
import com.sourcecoding.pb.business.workinghours.control.TimeRecordingLoader;
import java.text.SimpleDateFormat;
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
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author Matthias
 */
@Stateless
@Path("time-recording")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TimeRecordingService {

    @Inject
    TimeRecordingLoader trl;
    @Inject
    @CurrentUser
    User currentUser;
    @PersistenceContext
    EntityManager em;

    @GET
    @Path("range/{weeks}")
    public Map<String, Object> getCurrentWorkingHours(@PathParam("weeks") int weeks, @QueryParam("individualId") Long individualId) {

        if (individualId == null) {
            individualId = currentUser.getId();
        }

        Calendar c = Calendar.getInstance();
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK); //SUN=1, MON=2, ... SAT=7
        c.add(Calendar.DATE, (-1 * dayOfWeek) + 8);
        final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");

        String qEnd = dateFormatter.format(c.getTime());
        c.add(Calendar.DATE, (-7 * weeks) + 1);

        String qStart = dateFormatter.format(c.getTime());

        return getWorkingHoursWithRange(individualId, qStart, qEnd);
    }

    @GET
    @Path("range")
    public Map<String, Object> getWorkingHoursWithRange(
            @QueryParam("individualId") Long individualId,
            @QueryParam("qStart") String qStart,
            @QueryParam("qEnd") String qEnd) {

        if (individualId == null) {
            individualId = currentUser.getId();
        }
        Individual individual = em.find(Individual.class, individualId);

        Date startDate = DateParameter.valueOf(qStart);
        Date endDate = DateParameter.valueOf(qEnd);

        //collect existing time records and build a map
        List<TimeRecord> trList = em.createNamedQuery(TimeRecord.findTimeRecordsInARange, TimeRecord.class)
                .setParameter(TimeRecord.queryParam_user, individual)
                .setParameter(TimeRecord.queryParam_startDate, startDate)
                .setParameter(TimeRecord.queryParam_endDate, endDate)
                .getResultList();

        Map<String, Object> result = new HashMap<>();

        //uaerId
        result.put("userId", individualId);

        //date range        
        Map<String, Integer> wdr = new HashMap<>();
        result.put("workingDayRange", wdr);
        Calendar from = Calendar.getInstance();
        from.setTime(startDate);
        Calendar until = Calendar.getInstance();
        until.setTime(endDate);
        until.add(Calendar.DATE, 1); //from first day to (including) last day
        while (from.before(until)) {
            wdr.put(DateParameter.valueOf(from), 0);
            from.add(Calendar.DATE, 1);
        }

        Map<String, Map<String, Object>> workingHoursMap = new HashMap<>();

        for (TimeRecord tr : trList) {
            String key = tr.getProject().getId() + "-" + tr.getWorkPackage().getId() + "-" + tr.getDescribtion();
            if (!workingHoursMap.containsKey(key)) {
                Map<String, Object> line = new HashMap<>();
                line.put("projectId", tr.getProject().getId());
                line.put("workPackageId", tr.getWorkPackage().getId());
                line.put("description", tr.getDescribtion());
                line.put("days", new HashMap<String, Integer>());
                workingHoursMap.put(key, line);
            }
            Map<String, Object> line = workingHoursMap.get(key);
            //TODO use status!!!
            Map<String, Integer> dateLine = (Map<String, Integer>) line.get("days");
            dateLine.put(DateParameter.valueOf(tr.getWorkingDay()), tr.getWorkingTime());
        }
         //workingHours        
        result.put("workingHours", workingHoursMap.values());

        return result;
    }

    @PUT
    public Response udpateTimeRecording(Map<String, Object> payload, @QueryParam("individualId") Long individualId) {
        if (individualId == null) {
            individualId = currentUser.getId();
        }
        Individual individual = em.find(Individual.class, individualId);

        Map<String, TimeRecord> existedTRs = new HashMap<>();
        Map<String, TimeRecord> newTRs = new HashMap<>();

        Date startDate = null;
        Date endDate = null;
        Map<String, Integer> wdRange = (Map<String, Integer>) payload.get("workingDayRange");
        for (String key : wdRange.keySet()) {
            if (startDate == null) {
                startDate = DateParameter.valueOf(key);
            }
            if (endDate == null) {
                endDate = DateParameter.valueOf(key);
            }

            Date tmp = DateParameter.valueOf(key);
            if (tmp.before(startDate)) {
                startDate = tmp;
            }
            if (tmp.after(endDate)) {
                endDate = tmp;
            }
        }

        //collect existing time records and build a map
        List<TimeRecord> trList = em.createNamedQuery(TimeRecord.findTimeRecordsInARange, TimeRecord.class)
                .setParameter(TimeRecord.queryParam_user, individual)
                .setParameter(TimeRecord.queryParam_startDate, startDate)
                .setParameter(TimeRecord.queryParam_endDate, endDate)
                .getResultList();
        for (TimeRecord tr : trList) {
            String key = getKey(tr);
            existedTRs.put(key, tr);
        }

        //collect new time records and build a map
        ArrayList<Map<String, Object>> workingHours = (ArrayList<Map<String, Object>>) payload.get("workingHours");
        for (Map<String, Object> wh : workingHours) {
            String description = (String) wh.get("description");
            ProjectInformation project = em.find(ProjectInformation.class, ((Integer) wh.get("projectId")).longValue());
            WorkPackage workPackage = em.find(WorkPackage.class, ((Integer) wh.get("workPackageId")).longValue());
            for (Map.Entry<String, Integer> whDays : ((Map<String, Integer>) wh.get("days")).entrySet()) {
                Date workingDay = DateParameter.valueOf(whDays.getKey());
                Integer workingTime = whDays.getValue();

                TimeRecord tr = new TimeRecord();
                tr.setDescribtion(description);
                tr.setProject(project);
                tr.setWorkPackage(workPackage);
                tr.setUser(individual);
                tr.setWorkingDay(workingDay);
                tr.setWorkingTime(workingTime);
                tr.setStatus(0);

                String key = getKey(tr);
                newTRs.put(key, tr);
            }
        }

        //iterate over existedTR: if not exists in newTR -> remove; else update and remove newTR from MAP
        //step 2: iterate over newTRs: create with em.
        for (Map.Entry<String, TimeRecord> existedTR : existedTRs.entrySet()) {
            //getNewData
            TimeRecord newTR = newTRs.get(existedTR.getKey());
            if (newTR == null) {
                em.remove(existedTR.getValue()); //remove existing
            } else {
                TimeRecord tr = existedTR.getValue();
                tr.setWorkingTime(newTR.getWorkingTime());
                newTRs.remove(existedTR.getKey()); //remove, because it's updated
            }
        }
        for (Map.Entry<String, TimeRecord> newTR : newTRs.entrySet()) {
            em.persist(newTR.getValue());
        }

        return Response.ok().build();
    }

    private String getKey(TimeRecord tr) {
        String key = DateParameter.valueOf(tr.getWorkingDay()) + "-"
                + tr.getProject().getId() + "-" + tr.getWorkPackage().getId() + "-" + tr.getDescribtion();
        return key;
    }
}
