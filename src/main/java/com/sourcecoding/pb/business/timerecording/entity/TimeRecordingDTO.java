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
    
    private List<TimeRecordingRowDTO> timeRecordingRow;

    public Long getIndividualId() {
        return individualId;
    }

    public void setIndividualId(Long individualId) {
        this.individualId = individualId;
    }

    public List<TimeRecordingRowDTO> getTimeRecordingRow() {
        return timeRecordingRow;
    }

    public void setTimeRecordingRow(List<TimeRecordingRowDTO> timeRecordingRow) {
        this.timeRecordingRow = timeRecordingRow;
    }
    
    
    
    
   
    
}
