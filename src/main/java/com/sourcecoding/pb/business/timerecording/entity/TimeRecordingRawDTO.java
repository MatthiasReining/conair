/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sourcecoding.pb.business.timerecording.entity;

import java.util.Date;
import java.util.List;
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
    private List<TimeRecordingRawValueDTO> timeRecording;

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

    public List<TimeRecordingRawValueDTO> getTimeRecording() {
        return timeRecording;
    }

    public void setTimeRecording(List<TimeRecordingRawValueDTO> timeRecording) {
        this.timeRecording = timeRecording;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
}
