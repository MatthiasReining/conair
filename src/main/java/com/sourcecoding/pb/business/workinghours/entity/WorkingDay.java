/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sourcecoding.pb.business.workinghours.entity;

import com.sourcecoding.pb.business.individuals.entity.Individual;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author Matthias
 */
@Entity
@NamedQueries({
    @NamedQuery(name = WorkingDay.findWorkingDayRange, query = "SELECT wd FROM WorkingDay wd WHERE wd.user = :" + WorkingDay.queryParam_user + " AND wd.workingDay BETWEEN :" + WorkingDay.queryParam_startDate + " AND :" + WorkingDay.queryParam_endDate + " ORDER BY wd.workingDay"),
    @NamedQuery(name = WorkingDay.findWorkingDayForUser, query = "SELECT wd FROM WorkingDay wd WHERE wd.user = :"+ WorkingDay.queryParam_user + " AND wd.workingDay = :" + WorkingDay.queryParam_date )
})
public class WorkingDay implements Serializable {

    public static final String findWorkingDayRange = "WorkingDay.findWorkingDayRange";
    public static final String findWorkingDayForUser = "WorkingDay.findWorkingDayForUser";
    
    public static final String queryParam_user = "user";
    public static final String queryParam_startDate = "startDate";
    public static final String queryParam_endDate = "endDate";
    public static final String queryParam_date = "date";
    
    
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private Individual user;
    private int status; //TODO change to enum and rename to state
    @OneToMany(mappedBy = "workingDay", cascade = CascadeType.ALL)
    private List<WorkingTime> workingTimeList;
    @Temporal(TemporalType.DATE)
    private Date workingDay;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Individual getUser() {
        return user;
    }

    public void setUser(Individual user) {
        this.user = user;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<WorkingTime> getWorkingTimeList() {
        return workingTimeList;
    }

    public void setWorkingTimeList(List<WorkingTime> workingTimeList) {
        this.workingTimeList = workingTimeList;
    }

    public Date getWorkingDay() {
        return workingDay;
    }

    public void setWorkingDay(Date workingDay) {
        this.workingDay = workingDay;
    }
}
