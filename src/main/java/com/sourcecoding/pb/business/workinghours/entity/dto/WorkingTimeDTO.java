/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sourcecoding.pb.business.workinghours.entity.dto;

import com.sourcecoding.pb.business.workinghours.entity.WorkPackageDescription;

/**
 *
 * @author Matthias
 */
public class WorkingTimeDTO {

    private Long wpDescriptionId;
    private Integer workingTime;

    public Integer getWorkingTime() {
        return workingTime;
    }

    public void setWorkingTime(Integer workingTime) {
        this.workingTime = workingTime;
    }

    public Long getWpDescriptionId() {
        return wpDescriptionId;
    }

    public void setWpDescriptionId(Long wpDescriptionId) {
        this.wpDescriptionId = wpDescriptionId;
    }
}
