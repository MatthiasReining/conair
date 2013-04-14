/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sourcecoding.pb.business.workinghours.entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

/**
 *
 * @author Matthias
 */
@NamedQueries({
    @NamedQuery(name = WorkingTime.findWorkingTimeRange, query = "SELECT wt FROM WorkingTime wt WHERE wt.workingDay.user = :" + WorkingTime.findWorkingTimeRange_Param_user + " AND wt.workingDay.workingDay BETWEEN :" + WorkingTime.findWorkingTimeRange_Param_startDate + " AND :" + WorkingTime.findWorkingTimeRange_Param_endDate )
})
@Entity
public class WorkingTime implements Serializable {

    public static final String findWorkingTimeRange = "WorkingTime.findWorkingTimeRange";
    public static final String findWorkingTimeRange_Param_user = "user";
    public static final String findWorkingTimeRange_Param_startDate = "startDate";
    public static final String findWorkingTimeRange_Param_endDate = "endDate";
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private WorkingDay workingDay;
    @ManyToOne
    private WorkPackageDescription workPackageDescription;
    private Integer workingTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public WorkingDay getWorkingDay() {
        return workingDay;
    }

    public void setWorkingDay(WorkingDay workingDay) {
        this.workingDay = workingDay;
    }

    public WorkPackageDescription getWorkPackageDescription() {
        return workPackageDescription;
    }

    public void setWorkPackageDescription(WorkPackageDescription workPackageDescription) {
        this.workPackageDescription = workPackageDescription;
    }

    public Integer getWorkingTime() {
        return workingTime;
    }

    public void setWorkingTime(Integer workingTime) {
        this.workingTime = workingTime;
    }
}
