/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sourcecoding.pb.business.timerecording.entity;

import java.util.Date;
import java.util.Map;

/**
 *
 * @author Matthias
 */
public class TimeRecordingRawDTO {

    
    private Long workPackageId;
    
    private String projectName;
    private String workPackageName;
    
    private String description;
    private Map<Date, Integer> timeRecording;

    public Long getWorkPackageId() {
        return workPackageId;
    }

    public void setWorkPackageId(Long workPackageId) {
        this.workPackageId = workPackageId;
    }

    public String getWorkPackageName() {
        return workPackageName;
    }

    public void setWorkPackageName(String workPackageName) {
        this.workPackageName = workPackageName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<Date, Integer> getTimeRecording() {
        return timeRecording;
    }

    public void setTimeRecording(Map<Date, Integer> timeRecording) {
        this.timeRecording = timeRecording;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
    
    
    
}
