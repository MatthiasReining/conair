/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sourcecoding.pb.business.timerecording.entity;

import java.util.Date;

/**
 *
 * @author Matthias
 */
public class TimeRecordingRawValueDTO {
    
    public TimeRecordingRawValueDTO() {
    }
    
    public TimeRecordingRawValueDTO(Date workingDay, Integer workingTime) {
        this.workingDay = workingDay;
        this.workingTime = workingTime;
    }
    
    private Date workingDay;
    private Integer workingTime;

    public Date getWorkingDay() {
        return workingDay;
    }

    public void setWorkingDay(Date workingDay) {
        this.workingDay = workingDay;
    }

    public Integer getWorkingTime() {
        return workingTime;
    }

    public void setWorkingTime(Integer workingTime) {
        this.workingTime = workingTime;
    }
    
    
    
}
