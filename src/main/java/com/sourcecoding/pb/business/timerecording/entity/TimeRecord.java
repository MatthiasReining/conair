/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sourcecoding.pb.business.timerecording.entity;

import com.sourcecoding.pb.business.individuals.entity.Individual;
import com.sourcecoding.pb.business.project.entity.ProjectInformation;
import com.sourcecoding.pb.business.project.entity.WorkPackage;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
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
    @NamedQuery(name = TimeRecord.findTimeRecordsInARange, query = "SELECT wt FROM TimeRecord wt WHERE wt.user = :" + TimeRecord.queryParam_user + " AND wt.workingDay BETWEEN :" + TimeRecord.queryParam_startDate + " AND :" + TimeRecord.queryParam_endDate + " ORDER BY wt.workingDay"),
    @NamedQuery(name = TimeRecord.findTimeRecordsInARangeByProjectAndStatus, query = "SELECT wt FROM TimeRecord wt WHERE wt.project = :" + TimeRecord.queryParam_project + " AND wt.status = :" + TimeRecord.queryParam_status + " AND wt.workingDay BETWEEN :" + TimeRecord.queryParam_startDate + " AND :" + TimeRecord.queryParam_endDate + " ORDER BY wt.workingDay")
})
public class TimeRecord implements Serializable {

    public static final String findTimeRecordsInARange = "TimeRecord.findTimeRecordsInARange";
    public static final String findTimeRecordsInARangeByProjectAndStatus = "TimeRecord.findTimeRecordsInARangeByProjectAndStatus";
    
    public static final String queryParam_user = "user"; 
    public static final String queryParam_startDate = "startDate";
    public static final String queryParam_endDate = "endDate";
    public static final String queryParam_status = "status";
    public static final String queryParam_project = "project";
   
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private Individual user;

    private int status;

    @Temporal(TemporalType.DATE)
    private Date workingDay;
    @ManyToOne
    private ProjectInformation project;
    @ManyToOne
    private WorkPackage workPackage;

    private String describtion; //FIXME tippfehler!
    private Integer workingTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getWorkingDay() {
        return workingDay;
    }

    public void setWorkingDay(Date workingDay) {
        this.workingDay = workingDay;
    }

    public ProjectInformation getProject() {
        return project;
    }

    public void setProject(ProjectInformation project) {
        this.project = project;
    }

    public WorkPackage getWorkPackage() {
        return workPackage;
    }

    public void setWorkPackage(WorkPackage workPackage) {
        this.workPackage = workPackage;
    }

    public String getDescribtion() {
        return describtion;
    }

    public void setDescribtion(String describtion) {
        this.describtion = describtion;
    }

    public Integer getWorkingTime() {
        return workingTime;
    }

    public void setWorkingTime(Integer workingTime) {
        this.workingTime = workingTime;
    }

    public Individual getUser() {
        return user;
    }

    public void setUser(Individual user) {
        this.user = user;
    }

}
