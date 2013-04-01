/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sourcecoding.pb.business.timerecording.boundary;

import com.sourcecoding.pb.business.restconfig.DateParameter;
import com.sourcecoding.pb.business.timerecording.control.TimeRecordingStore;
import com.sourcecoding.pb.business.timerecording.entity.TimeRecordingDTO;
import com.sourcecoding.pb.business.timerecording.entity.TimeRecordingRowDTO;
import com.sourcecoding.pb.business.timerecording.entity.TimeRecordingRowValueDTO;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
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

    @GET
    @Path("{individualId}/search")
    public TimeRecordingDTO getTimeRecords(
            @PathParam("individualId") Long individualId,
            @QueryParam("qStart") String qStart,
            @QueryParam("qEnd") String qEnd) {

        Date startDate = DateParameter.valueOf(qStart);
        Date endDate = DateParameter.valueOf(qEnd);

        return trs.get(individualId, startDate, endDate);
    }

    @GET
    public TimeRecordingDTO getDummy() {

        TimeRecordingDTO timeRecording = new TimeRecordingDTO();
        timeRecording.setIndividualId(123L);

        List<TimeRecordingRowDTO> rowList = new ArrayList<>();
        timeRecording.setTimeRecordingRow(rowList);

        TimeRecordingRowDTO row = new TimeRecordingRowDTO();
        rowList.add(row);
        row.setWorkPackageId(234L);
        row.setProjectName("Project X");
        row.setWorkPackageName("Module A");

        List<TimeRecordingRowValueDTO> tr = new ArrayList<>();
        row.setTimeRecording(tr);
        Calendar c = Calendar.getInstance();
        c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        c.set(Calendar.MILLISECOND, 0);
        tr.add(new TimeRecordingRowValueDTO(c.getTime(), 510));
        c.add(Calendar.DATE, 1);
        tr.add(new TimeRecordingRowValueDTO(c.getTime(), 480));

        row = new TimeRecordingRowDTO();
        rowList.add(row);
        row.setWorkPackageId(434L);
        row.setProjectName("Project X");
        row.setWorkPackageName("Module C");
        tr = new ArrayList<>();
        row.setTimeRecording(tr);
        c = Calendar.getInstance();
        c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        c.set(Calendar.MILLISECOND, 0);
        c.add(Calendar.DATE, 2);
        tr.add(new TimeRecordingRowValueDTO(c.getTime(), 480));
        c.add(Calendar.DATE, 1);
        tr.add(new TimeRecordingRowValueDTO(c.getTime(), 480));



        return timeRecording;
    }
}
