/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sourcecoding.pb.business.timerecording.boundary;

import com.sourcecoding.pb.business.authentication.boundary.CurrentUser;
import com.sourcecoding.pb.business.authentication.entity.User;
import com.sourcecoding.pb.business.restconfig.DateParameter;
import com.sourcecoding.pb.business.user.entity.Individual;
import com.sourcecoding.pb.business.workinghours.control.JsonMapPersister;
import com.sourcecoding.pb.business.workinghours.control.TimeRecordingLoader;
import com.sourcecoding.pb.business.workinghours.entity.WorkingDay;
import com.sourcecoding.pb.business.workinghours.entity.WorkingTime;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
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
    JsonMapPersister jsonMapPersister;
    @Inject
    TimeRecordingLoader trl;
    @Inject
    @CurrentUser
    User user;
    @PersistenceContext
    EntityManager em;

    @GET
    @Path("range/{weeks}")
    public Map<String, Object> getCurrentWorkingHours(@PathParam("weeks") int weeks, @QueryParam("individualId") Long individualId) {

        if (individualId == null) {
            individualId = user.getId();
        }

        Calendar c = Calendar.getInstance();
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        c.add(Calendar.DATE, (-1 * dayOfWeek) + 7);
        final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");

        String qEnd = dateFormatter.format(c.getTime());
        c.add(Calendar.DATE, -7 * weeks);


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
            individualId = user.getId();
        }


        Date from = DateParameter.valueOf(qStart);
        Date until = DateParameter.valueOf(qEnd);

        List<WorkingDay> entites = trl.getWorkingTimePackage(individualId, from, until);

        Map<String, Object> result = jsonMapPersister.buildWorkingTimePackage(entites, from, until, individualId);

        return result;
    }

    @PUT
    public Response udpateTimeRecording(Map<String, Object> payload) {
        Long userId = ((Integer) payload.get("userId")).longValue();
        System.out.println(userId);

        Individual user = em.find(Individual.class, userId);

        List<Map<String, Object>> workingHours = (List<Map<String, Object>>) payload.get("workingHours");
        for (Map<String, Object> workingHour : workingHours) {
            Long projectId = ((Integer) workingHour.get("projectId")).longValue();
            Long workPackageId = ((Integer) workingHour.get("workPackageId")).longValue();
            String description = (String) workingHour.get("description");
            Map<String, Integer> days = (Map<String, Integer>) workingHour.get("days");

            for (Map.Entry<String, Integer> dayEntry : days.entrySet()) {

                Date date = DateParameter.valueOf(dayEntry.getKey());

                List<WorkingDay> workingDayList = em.createNativeQuery(WorkingDay.findWorkingDayForUser, WorkingDay.class)
                        .setParameter(WorkingDay.queryParam_user, user)
                        .setParameter(WorkingDay.queryParam_date, date)
                        .getResultList();
                WorkingDay workingDay;
                if (workingDayList.isEmpty()) {
                    workingDay = new WorkingDay();
                    workingDay.setUser(user);
                    workingDay.setWorkingDay(date);
                    workingDay = em.merge(workingDay);
                } else {
                    workingDay = workingDayList.get(0);
                }
                
                //FIXME set status workingDay.setStatus();
                
                List<WorkingTime> workingTimeList = workingDay.getWorkingTimeList();
                if (workingTimeList == null) workingTimeList = new ArrayList<>();
                
                //FIXME hier gehts weiter

            }

        }
        return Response.ok().build();
    }
}
