/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sourcecoding.pb.business.vacation.boundary;

import com.sourcecoding.pb.business.authentication.boundary.CurrentUser;
import com.sourcecoding.pb.business.authentication.entity.User;
import com.sourcecoding.pb.business.individuals.entity.Individual;
import com.sourcecoding.pb.business.vacation.control.ResponseBuilder;
import com.sourcecoding.pb.business.vacation.control.VacationCalculator;
import com.sourcecoding.pb.business.vacation.entity.VacationRecord;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

/**
 *
 * @author Matthias
 */
@Stateless
public class TasksResource {

    @PersistenceContext
    EntityManager em;
    @Inject
    @CurrentUser
    User currentUser;
    @Inject
    ResponseBuilder rb;
    @Inject
    VacationCalculator vacationCalculator;

    /**
     * Get vacation tasks for approval.
     *
     * @return list of tasks
     */
    @GET
    public List<Object> getTasks() {
        Long individualId = currentUser.getId();
        Individual individual = em.find(Individual.class, individualId);

        List<VacationRecord> vrList = em.createNamedQuery(VacationRecord.findByApprovalState, VacationRecord.class)
                .setParameter(VacationRecord.queryParam_vacationManager, individual)
                .setParameter(VacationRecord.queryParam_approvalState, VacationRecord.APPROVAL_STATE_FOR_APPROVAL)
                .getResultList();

        List<Object> result = new ArrayList<>();

        if (vrList.isEmpty()) {
            return result;
        }

        for (VacationRecord vr : vrList) {
            Map<String, Object> taskEntry = rb.buildVacationRecordEntry(vr);
            result.add(taskEntry);
            System.out.println("worked off " + vr.getId());
        }


        return result;
    }

    @PUT
    @Path("{vacationRecordId}/approve")
    public Map<String, Object> approveRequestForTimeOff(@PathParam("vacationRecordId") Long vacationRecordId, Map<String, Object> payload) {
        VacationRecord vr = em.find(VacationRecord.class, vacationRecordId);

        vr.setApprovalState(VacationRecord.APPROVAL_STATE_APPROVED);
        vr.setNumberOfDays((Integer) payload.get("numberOfDays"));        
        vr.setVacationComment((String)payload.get("message"));        
        
        vacationCalculator.calculateAllVacationDays(vr.getVacationYear());

        return null;
    }

    @PUT
    @Path("{vacationRecordId}/reject")
    public Map<String, Object> rejectRequestForTimeOff(@PathParam("vacationRecordId") Long vacationRecordId, Map<String, Object> payload) {
        VacationRecord vr = em.find(VacationRecord.class, vacationRecordId);

        vr.setApprovalState(VacationRecord.APPROVAL_STATE_REJECTED);

        return null;
    }

    @GET
    @Path("{vacationRecordId}")
    public Map<String, Object> getVacationForApprovalByVacationRecordId(
            @PathParam("vacationRecordId") Long vacationRecordId) {
        VacationRecord vr = em.find(VacationRecord.class, vacationRecordId);

        Map<String, Object> taskEntry = rb.buildVacationRecordEntry(vr);

        return taskEntry;
    }
}
