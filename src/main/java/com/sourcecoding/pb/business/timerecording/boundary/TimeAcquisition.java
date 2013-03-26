/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sourcecoding.pb.business.timerecording.boundary;

import com.sourcecoding.pb.business.timerecording.entity.TimeRecordingDTO;
import com.sourcecoding.pb.business.timerecording.entity.TimeRecordingRawDTO;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author Matthias
 */
@Stateless
@Path("time-acquisition")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TimeAcquisition {

    @GET
    public String isAlive() {
        return "bin da : " + new Date();
    }

    @PUT
    public TimeRecordingDTO createOrReplaceTimeRecording(TimeRecordingDTO timeRecording) {
        return null;
    }

    @GET
    @Path("dummy")
    public TimeRecordingDTO createOrReplaceTimeRecording() {

        TimeRecordingDTO timeRecording = new TimeRecordingDTO();
        timeRecording.setIndividualId(123L);

        List<TimeRecordingRawDTO> rawList = new ArrayList<>();
        timeRecording.setTimeRecordingRaw(rawList);

        TimeRecordingRawDTO raw = new TimeRecordingRawDTO();
        rawList.add(raw);
        raw.setWorkPackageId(234L);
        raw.setProjectName("Project X");
        raw.setWorkPackageName("Module A");
        Map<Date, Integer> tr = new HashMap<>();
        Calendar c = Calendar.getInstance();
        c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        c.set(Calendar.MILLISECOND, 0);
        tr.put(c.getTime(), 510);
        c.add(Calendar.DATE, 1);
        tr.put(c.getTime(), 480);
        raw.setTimeRecording(tr);


        raw = new TimeRecordingRawDTO();
        rawList.add(raw);
        raw.setWorkPackageId(434L);
        raw.setProjectName("Project X");
        raw.setWorkPackageName("Module C");
        tr = new HashMap<>();
        c = Calendar.getInstance();
        c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        c.set(Calendar.MILLISECOND, 0);
        c.add(Calendar.DATE, 2);
        tr.put(c.getTime(), 480);
        c.add(Calendar.DATE, 1);
        tr.put(c.getTime(), 480);
        raw.setTimeRecording(tr);


        return timeRecording;
    }
}
