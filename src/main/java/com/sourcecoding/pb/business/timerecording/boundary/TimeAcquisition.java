/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sourcecoding.pb.business.timerecording.boundary;

import com.sourcecoding.pb.business.timerecording.control.TimeRecordingStore;
import com.sourcecoding.pb.business.timerecording.entity.TimeRecordingDTO;
import com.sourcecoding.pb.business.timerecording.entity.TimeRecordingQueryDTO;
import com.sourcecoding.pb.business.timerecording.entity.TimeRecordingRawDTO;
import com.sourcecoding.pb.business.timerecording.entity.TimeRecordingRawValueDTO;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
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
    
    @Inject
    TimeRecordingStore trs;

    @PUT
    public TimeRecordingDTO createOrReplaceTimeRecording(TimeRecordingDTO timeRecording) {
        System.out.println(timeRecording);
        trs.update(timeRecording);
        return timeRecording;
    }
    
    @PUT
    @Path("/search")
    public TimeRecordingDTO getTimeRecords(TimeRecordingQueryDTO query) {
        return trs.get(query);
    }

    @GET
    public TimeRecordingDTO getDummy() {

        TimeRecordingDTO timeRecording = new TimeRecordingDTO();
        timeRecording.setIndividualId(123L);

        List<TimeRecordingRawDTO> rawList = new ArrayList<>();
        timeRecording.setTimeRecordingRaw(rawList);

        TimeRecordingRawDTO raw = new TimeRecordingRawDTO();
        rawList.add(raw);
        raw.setWorkPackageId(234L);
        raw.setProjectName("Project X");
        raw.setWorkPackageName("Module A");

        List<TimeRecordingRawValueDTO> tr = new ArrayList<>();
        raw.setTimeRecording(tr);
        Calendar c = Calendar.getInstance();
        c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        c.set(Calendar.MILLISECOND, 0);
        tr.add(new TimeRecordingRawValueDTO(c.getTime(), 510));
        c.add(Calendar.DATE, 1);
        tr.add(new TimeRecordingRawValueDTO(c.getTime(), 480));

        raw = new TimeRecordingRawDTO();
        rawList.add(raw);
        raw.setWorkPackageId(434L);
        raw.setProjectName("Project X");
        raw.setWorkPackageName("Module C");
        tr = new ArrayList<>();
        raw.setTimeRecording(tr);
        c = Calendar.getInstance();
        c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        c.set(Calendar.MILLISECOND, 0);
        c.add(Calendar.DATE, 2);
        tr.add(new TimeRecordingRawValueDTO(c.getTime(), 480));
        c.add(Calendar.DATE, 1);
        tr.add(new TimeRecordingRawValueDTO(c.getTime(), 480));



        return timeRecording;
    }
}
