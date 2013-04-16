/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sourcecoding.pb.business.workinghours.boundary;

import com.sourcecoding.pb.business.restconfig.DateParameter;
import com.sourcecoding.pb.business.workinghours.control.JsonMapper;
import com.sourcecoding.pb.business.workinghours.control.TimeRecordingStore;
import com.sourcecoding.pb.business.workinghours.entity.WorkingDay;
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
    JsonMapper jsonMapper;
    @Inject
    TimeRecordingStore trs;

  

    @GET
    @Path("{individualId}/range")
    public Response getWorkingHoursWithRange(
            @PathParam("individualId") Long individualId,
            @QueryParam("qStart") String qStart,
            @QueryParam("qEnd") String qEnd) {

        Date from = DateParameter.valueOf(qStart);
        Date until = DateParameter.valueOf(qEnd);

        List<WorkingDay> entites = trs.getWorkingTimePackage(individualId, from, until);

        String json = jsonMapper.buildWorkingTimePackage(entites);
        return Response.ok(json, MediaType.APPLICATION_JSON).build();
    }

    @PUT
    public Response createOrReplaceWorkingHours(HashMap<String,Object> data) {
        
        System.out.println( data );
        jsonMapper.buildWorkingHoursEntities(data);
        //FIXME trs.update(timeRecording);
        //return timeRecording;
        return Response.ok().build();
    }
}
