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
import com.sourcecoding.pb.business.workinghours.control.JsonMapPersister;
import com.sourcecoding.pb.business.workinghours.control.TimeRecordingLoader;
import com.sourcecoding.pb.business.workinghours.entity.WorkPackageDescription;
import com.sourcecoding.pb.business.workinghours.entity.WorkingDay;
import com.sourcecoding.pb.business.workinghours.entity.WorkingTime;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

    private final String PROEJCT_ID = "projectId";
    private final String WORKPACKAGE_ID = "workPackageId";
    private final String DESCRIPTION = "description";
    private final String DAYS = "days";
    private final String WORKING_TIME_MINUTES = "workingTimeMinutes";
    @Inject
    JsonMapPersister jsonMapPersister;
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
            individualId = currentUser.getId();
        }

        Date from = DateParameter.valueOf(qStart);
        Date until = DateParameter.valueOf(qEnd);

        List<WorkingDay> entites = trl.getWorkingTimePackage(individualId, from, until);

        Map<String, Object> result = jsonMapPersister.buildWorkingTimePackage(entites, from, until, individualId);

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
            String key = DateParameter.valueOf(tr.getWorkingDay()) + "-"
                    + tr.getProject().getId() + "-" + tr.getWorkPackage().getId() + "-" + tr.getDescribtion();
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

                String key = DateParameter.valueOf(tr.getWorkingDay()) + "-"
                        + tr.getProject().getId() + "-" + tr.getWorkPackage().getId() + "-" + tr.getDescribtion();
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
                newTRs.remove( existedTR.getKey()); //remove, because it's updated
            }            
        }
        for (Map.Entry<String, TimeRecord> newTR : newTRs.entrySet()) {
            em.persist(newTR.getValue());
        }
        
        return Response.ok().build();
    }

    private Map<Date, WorkingDay> getWorkingDayListFromMap(Map<String, Object> payload, Individual user) {
        Map<String, Integer> workingDayRange = (Map<String, Integer>) payload.get("workingDayRang");
        Map<Date, WorkingDay> workingDayEntityRange = new HashMap<>();
        for (Map.Entry<String, Integer> entry : workingDayRange.entrySet()) {
            //Init //FIXME load existing data
            Date wdDate = DateParameter.valueOf(entry.getKey());
            WorkingDay wd = new WorkingDay();
            wd.setWorkingDay(wdDate);
            wd.setStatus(entry.getValue());
            wd.setUser(user);
            wd.setWorkingTimeList(new ArrayList<WorkingTime>());
            workingDayEntityRange.put(wdDate, wd);
        }

        List<Map<String, Object>> workingHoursList = (List<Map<String, Object>>) (Map<String, Object>) payload.get("workingHours");
        for (Map<String, Object> workingHourEntry : workingHoursList) {
            String description = (String) workingHourEntry.get("description");
            Long wpId = (Long) workingHourEntry.get("workPackageId");
            WorkPackage wp = em.find(WorkPackage.class, wpId);

            WorkPackageDescription wpd = new WorkPackageDescription();
            wpd.setDescription(description);
            wpd.setWorkPackage(wp);

            Map<String, Integer> days = (Map<String, Integer>) workingHourEntry.get("days");
            for (Map.Entry<String, Integer> dayEntry : days.entrySet()) {
                Date wdDate = DateParameter.valueOf(dayEntry.getKey());
                Integer wdTime = dayEntry.getValue();

                WorkingDay wd = workingDayEntityRange.get(wdDate);

                WorkingTime wt = new WorkingTime();
                wt.setWorkPackage(wp);
                wt.setWorkPackageDescription(wpd);
                wt.setWorkingDay(wd);
                wt.setWorkingTime(wdTime);

                wd.getWorkingTimeList().add(wt);
            }
        }

        return workingDayEntityRange;
    }

    @PUT
    @Path("old")
    public Response oldUdpateTimeRecording(Map<String, Object> payload, @QueryParam("individualId") Long individualId) {

        if (individualId == null) {
            individualId = currentUser.getId();
        }

        Individual user = em.find(Individual.class, individualId);
        List<Map<String, Object>> workingHours = (List<Map<String, Object>>) payload.get("workingHours");
        System.out.println("workingHours:" + workingHours);

        //Map for date -> key -> values
        Map<Date, Map<String, Map<String, Object>>> workingTimeByDate = convertToWorkingTimeByDate(workingHours);

        for (Map.Entry<Date, Map<String, Map<String, Object>>> entry : workingTimeByDate.entrySet()) {

            Date date = entry.getKey();
            Map<String, Map<String, Object>> keyObj = entry.getValue();

            WorkingDay workingDay = getManagedWorkingDay(user, date);

            if (workingDay.getWorkingTimeList() == null) {
                workingDay.setWorkingTimeList(new ArrayList<WorkingTime>());
            }

            //modify and remove existing objects
            List<WorkingTime> wtObjects2Remove = new ArrayList<>();
            for (WorkingTime workingTime : workingDay.getWorkingTimeList()) {
                String mangagedKey = workingTime.getWorkPackage().getId() + "-" + workingTime.getWorkPackageDescription().getDescription();

                Map<String, Object> workingTimeMap = keyObj.get(mangagedKey);
                if (workingTimeMap == null) {
                    wtObjects2Remove.add(workingTime);
                } else {
                    workingTime.setWorkingTime((Integer) workingTimeMap.get(WORKING_TIME_MINUTES));
                    //remove updated value from map
                    keyObj.remove(mangagedKey);
                }
            }
            for (WorkingTime workingTime : wtObjects2Remove) {
                em.remove(workingTime);
            }

            //add new workingtime
            for (Map.Entry<String, Map<String, Object>> wtListMap : keyObj.entrySet()) {
                Map<String, Object> wtMap = wtListMap.getValue();

                Long workPackageId = (Long) wtMap.get(WORKPACKAGE_ID);
                String description = (String) wtMap.get(DESCRIPTION);
                Integer workingTimeMinutes = (Integer) wtMap.get(WORKING_TIME_MINUTES);

                WorkPackage workPackage = em.find(WorkPackage.class, workPackageId);
                //search for existing working package descriptions
                List<WorkPackageDescription> wpdList = em.createNamedQuery(WorkPackageDescription.findByWorkPackageAndDescription, WorkPackageDescription.class)
                        .setParameter(WorkPackageDescription.findByWorkPackageAndDescription_Param_WorkPackage, workPackage)
                        .setParameter(WorkPackageDescription.findByWorkPackageAndDescription_Param_Description, description)
                        .getResultList();
                WorkPackageDescription wpd;
                if (wpdList.isEmpty()) {
                    wpd = new WorkPackageDescription();
                    wpd.setDescription(description);
                    wpd.setWorkPackage(workPackage);
                    wpd = em.merge(wpd);
                } else {
                    wpd = wpdList.get(0);
                }

                WorkingTime wt = new WorkingTime();
                wt.setWorkPackage(workPackage);
                wt.setWorkingDay(workingDay);
                wt.setWorkingTime(workingTimeMinutes);
                wt.setWorkPackageDescription(wpd);

                wt = em.merge(wt);

                workingDay.getWorkingTimeList().add(wt);

            }

        }

        return Response.ok().build();
    }

    private Map<Date, Map<String, Map<String, Object>>> convertToWorkingTimeByDate(List<Map<String, Object>> workingHours) {
        //Map for date -> key -> values
        Map<Date, Map<String, Map<String, Object>>> workingTimeByDate = new HashMap<>();

        for (Map<String, Object> workingHour : workingHours) {
            Long projectId = ((Integer) workingHour.get(PROEJCT_ID)).longValue();
            Long workPackageId = ((Integer) workingHour.get(WORKPACKAGE_ID)).longValue();
            String description = (String) workingHour.get(DESCRIPTION);
            Map<String, Integer> days = (Map<String, Integer>) workingHour.get(DAYS);
            String key = workPackageId + "-" + description;

            for (Map.Entry<String, Integer> dayEntry : days.entrySet()) {
                System.out.println("dayEntry: " + dayEntry);
                Date date = DateParameter.valueOf(dayEntry.getKey());
                Integer workingTimeMinutes = dayEntry.getValue();

                if (workingTimeMinutes == null || workingTimeMinutes == 0) {
                    continue;
                }

                Map<String, Map<String, Object>> dateData = workingTimeByDate.get(date);
                if (dateData == null) {
                    dateData = new HashMap<>();
                    workingTimeByDate.put(date, dateData);
                }
                Map<String, Object> keyData = dateData.get(key);
                if (keyData == null) {
                    keyData = new HashMap<>();
                    dateData.put(key, keyData);
                }

                keyData.put(WORKING_TIME_MINUTES, workingTimeMinutes);
                keyData.put(PROEJCT_ID, projectId);
                keyData.put(WORKPACKAGE_ID, workPackageId);
                keyData.put(DESCRIPTION, description);

            }
        }
        return workingTimeByDate;
    }

    private WorkingDay getManagedWorkingDay(Individual user, Date date) {
        List<WorkingDay> workingDayList = em.createNamedQuery(WorkingDay.findWorkingDayForUser, WorkingDay.class)
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
        return workingDay;
    }
}
