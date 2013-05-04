/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sourcecoding.pb.business.workinghours.boundary;

import com.sourcecoding.pb.business.restconfig.DateParameter;
import com.sourcecoding.pb.business.workinghours.control.JsonMapPersister;
import com.sourcecoding.pb.business.workinghours.control.TimeRecordingLoader;
import com.sourcecoding.pb.business.workinghours.entity.WorkingDay;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
@Path("working-hours")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class WorkingHours {

    @PersistenceContext
    EntityManager em;
    @Inject
    JsonMapPersister jsonMapPersister;
    @Inject
    TimeRecordingLoader trl;

    @GET
    @Path("{individualId}/current-range/{weeks}")
    public Response getCurrentWorkingHours(@PathParam("individualId") Long individualId, @PathParam("weeks") int weeks) {

        Calendar c = Calendar.getInstance();
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        c.add(Calendar.DATE, (-1 * dayOfWeek)+7);
        final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        
        String qEnd = dateFormatter.format(c.getTime());
        c.add(Calendar.DATE, -7*weeks);


        String qStart = dateFormatter.format(c.getTime());
        
        return getWorkingHoursWithRange(individualId, qStart, qEnd);
    }

    @GET
    @Path("{individualId}/range")
    public Response getWorkingHoursWithRange(
            @PathParam("individualId") Long individualId,
            @QueryParam("qStart") String qStart,
            @QueryParam("qEnd") String qEnd) {

        Date from = DateParameter.valueOf(qStart);
        Date until = DateParameter.valueOf(qEnd);

        List<WorkingDay> entites = trl.getWorkingTimePackage(individualId, from, until);

        String json = jsonMapPersister.buildWorkingTimePackage(entites);
        return Response.ok(json, MediaType.APPLICATION_JSON).build();
    }

    @PUT
    public Response createOrReplaceWorkingHours(HashMap<String, Object> data) {


        System.out.println(data);
        jsonMapPersister.update(data);
        //FIXME trs.update(timeRecording);
        //return timeRecording;
        return Response.ok().build();
    }
}
