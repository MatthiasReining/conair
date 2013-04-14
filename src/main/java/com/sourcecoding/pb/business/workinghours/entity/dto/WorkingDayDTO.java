/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sourcecoding.pb.business.workinghours.entity.dto;

import com.sourcecoding.pb.business.workinghours.entity.WorkingTime;
import java.util.Date;
import java.util.List;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author Matthias
 */
public class WorkingDayDTO {

    private Long individualId;
    private Date workingDay;
    private int status; //TODO change to enum
    
    private List<WorkingTimeDTO> workingTimeList;

    
    public Long getIndividualId() {
        return individualId;
    }

    public void setIndividualId(Long individualId) {
        this.individualId = individualId;
    }

    public Date getWorkingDay() {
        return workingDay;
    }

    public void setWorkingDay(Date workingDay) {
        this.workingDay = workingDay;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<WorkingTimeDTO> getWorkingTimeList() {
        return workingTimeList;
    }

    public void setWorkingTimeList(List<WorkingTimeDTO> workingTimeList) {
        this.workingTimeList = workingTimeList;
    }
    
    
}
