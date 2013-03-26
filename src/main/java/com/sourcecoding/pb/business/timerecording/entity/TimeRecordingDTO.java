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
public class TimeRecordingDTO {
    
    private Long individualId;
    
    private List<TimeRecordingRawDTO> timeRecordingRaw;

    public Long getIndividualId() {
        return individualId;
    }

    public void setIndividualId(Long individualId) {
        this.individualId = individualId;
    }

    public List<TimeRecordingRawDTO> getTimeRecordingRaw() {
        return timeRecordingRaw;
    }

    public void setTimeRecordingRaw(List<TimeRecordingRawDTO> timeRecordingRaw) {
        this.timeRecordingRaw = timeRecordingRaw;
    }
    
    
    
    
   
    
}
