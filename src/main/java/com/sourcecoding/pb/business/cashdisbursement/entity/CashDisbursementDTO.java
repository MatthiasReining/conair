/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sourcecoding.pb.business.cashdisbursement.entity;

import com.sourcecoding.pb.business.restconfig.JsonDateAdapter;
import com.sourcecoding.pb.business.restconfig.JsonDateTimeAdapter;
import java.math.BigDecimal;
import java.util.Date;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 *
 * @author Matthias
 */
public class CashDisbursementDTO {

    public CashDisbursementDTO() {
    }

    public CashDisbursementDTO(CashDisbursement cd) {
        this.id = cd.getId();
        this.cdCategory = cd.getCdCategory();
        this.cdDate = cd.getCdDate();
        this.description = cd.getDescription();
        this.amount = cd.getAmount();
    }

    public Long id;

    @XmlJavaTypeAdapter(JsonDateAdapter.class)
    public Date cdDate;

    public String description;
    public String cdCategory;
    public BigDecimal amount;

}
