/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sourcecoding.pb.business.timerecording.control;

import com.sourcecoding.pb.business.project.entity.WorkPackage;
import com.sourcecoding.pb.business.timerecording.entity.TimeRecording;
import com.sourcecoding.pb.business.timerecording.entity.TimeRecordingDTO;
import com.sourcecoding.pb.business.timerecording.entity.TimeRecordingRawDTO;
import com.sourcecoding.pb.business.timerecording.entity.TimeRecordingRawValueDTO;
import com.sourcecoding.pb.business.timerecording.entity.WorkPackageDescription;
import com.sourcecoding.pb.business.user.entity.Individual;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Matthias
 */
public class TimeRecordingStore {

    @PersistenceContext
    EntityManager em;

    public void update(TimeRecordingDTO in) {

        Individual individual = em.find(Individual.class, in.getIndividualId());

        for (TimeRecordingRawDTO trr : in.getTimeRecordingRaw()) {
            WorkPackage wp = em.find(WorkPackage.class, trr.getWorkPackageId());

            String description = trr.getDescription();

            List<WorkPackageDescription> wpdList = em.createNamedQuery(WorkPackageDescription.findByWorkPackageAndDescription, WorkPackageDescription.class)
                    .setParameter(WorkPackageDescription.findByWorkPackageAndDescription_Param_WorkPackage, wp)
                    .setParameter(WorkPackageDescription.findByWorkPackageAndDescription_Param_Description, description)
                    .getResultList();

            WorkPackageDescription wpd = null;
            //TODO there is a snychronize problem(!)
            if (wpdList.isEmpty()) {
                wpd = new WorkPackageDescription();
                wpd.setDescription(description);
                wpd.setWorkPackage(wp);
                wpd = em.merge(wpd);
            } else {
                wpd = wpdList.get(0); //there is (should) always one raw available
            }

            for (TimeRecordingRawValueDTO rv : trr.getTimeRecording()) {
                Date workingDay = rv.getWorkingDay();
                Integer workingTime = rv.getWorkingTime();

                //TODO ensure there is no time value in working day only date value

                List<TimeRecording> trList = em.createNamedQuery(TimeRecording.findUniqueTimeRecording, TimeRecording.class)
                        .setParameter(TimeRecording.findUniqueTimeRecording_Param_workPackageDesciption, wpd)
                        .setParameter(TimeRecording.findUniqueTimeRecording_Param_user, individual)
                        .setParameter(TimeRecording.findUniqueTimeRecording_Param_workingDay, workingDay)
                        .getResultList();
                
                TimeRecording tr = null;
                //TODO there is a snychronize problem(!)
                if (trList.isEmpty()) {
                    tr = new TimeRecording();
                    tr.setUser(individual);
                    tr.setWorkPackageDesciption(wpd);
                    tr.setWorkingDay(workingDay);
                    tr = em.merge(tr);
                } else {
                    tr = trList.get(0); //there is (should) always one raw available
                }
                tr.setWorkingTime(workingTime);

            }

        }

    }
}
