/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sourcecoding.pb.business.timerecording.entity;

import java.util.List;

/**
 *
 * @author Matthias
 */
public class TimeRecordingRowDTO {

    private Long workPackageId;
    private String projectName;
    private String workPackageName;
    private String description;
    private List<TimeRecordingRowValueDTO> timeRecording;

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

    public List<TimeRecordingRowValueDTO> getTimeRecording() {
        return timeRecording;
    }

    public void setTimeRecording(List<TimeRecordingRowValueDTO> timeRecording) {
        this.timeRecording = timeRecording;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
}
