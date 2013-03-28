/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sourcecoding.pb.business.timerecording.entity;

import com.sourcecoding.pb.business.user.entity.Individual;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author Matthias
 */
@Entity
@NamedQueries({
    @NamedQuery(name = TimeRecording.findUniqueTimeRecording, query = "SELECT tr FROM TimeRecording tr WHERE tr.workPackageDesciption = :"+ TimeRecording.findUniqueTimeRecording_Param_workPackageDesciption+" AND tr.user = :"+TimeRecording.findUniqueTimeRecording_Param_user+" AND tr.workingDay = :" + TimeRecording.findUniqueTimeRecording_Param_workingDay)
})
public class TimeRecording implements Serializable {

    public static final String findUniqueTimeRecording = "TimeRecording.findUniqueTimeRecording";
    public static final String findUniqueTimeRecording_Param_workPackageDesciption = "workPackageDesciption";
    public static final String findUniqueTimeRecording_Param_user = "user";
    public static final String findUniqueTimeRecording_Param_workingDay = "workingDay";
    
    
            
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    private WorkPackageDescription workPackageDesciption;
    @ManyToOne
    private Individual user;
    /**
     * workingTime in minutes!
     */
    private Integer workingTime;
    @Temporal(TemporalType.DATE)
    private Date workingDay;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public WorkPackageDescription getWorkPackageDesciption() {
        return workPackageDesciption;
    }

    public void setWorkPackageDesciption(WorkPackageDescription workPackageDesciption) {
        this.workPackageDesciption = workPackageDesciption;
    }

    public Individual getUser() {
        return user;
    }

    public void setUser(Individual user) {
        this.user = user;
    }

    public Integer getWorkingTime() {
        return workingTime;
    }

    public void setWorkingTime(Integer workingTime) {
        this.workingTime = workingTime;
    }

    public Date getWorkingDay() {
        return workingDay;
    }

    public void setWorkingDay(Date workingDay) {
        this.workingDay = workingDay;
    }
}
