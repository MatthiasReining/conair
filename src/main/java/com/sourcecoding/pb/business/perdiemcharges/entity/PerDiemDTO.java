/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sourcecoding.pb.business.perdiemcharges.entity;

import com.sourcecoding.pb.business.restconfig.JsonDateTimeAdapter;
import java.math.BigDecimal;
import java.util.Date;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 *
 * @author Matthias
 */
public class PerDiemDTO {

    public PerDiemDTO() {
    }

    public PerDiemDTO(Date perDiemDate, String timeFrom, String timeTo, long projectId, long travelExpenseRateId, boolean breakfast, boolean lunch, boolean dinner, boolean taxable, BigDecimal charges) {
        this.perDiemDate = perDiemDate;
        this.timeFrom = timeFrom;
        this.timeTo = timeTo;
        this.projectId = projectId;
        this.travelExpenseRateId = travelExpenseRateId;
        this.breakfast = breakfast;
        this.lunch = lunch;
        this.dinner = dinner;
        this.taxable = taxable;
        this.charges = charges;
    }

    @XmlJavaTypeAdapter(JsonDateTimeAdapter.class)
    public Date perDiemDate;
    public String timeFrom;
    public String timeTo;
    public Long projectId;
    public Long travelExpenseRateId;
    public BigDecimal charges;
    public boolean breakfast;
    public boolean lunch;
    public boolean dinner;
    public boolean taxable;
}
