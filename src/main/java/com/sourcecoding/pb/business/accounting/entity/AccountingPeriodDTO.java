/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sourcecoding.pb.business.accounting.entity;

import com.sourcecoding.pb.business.restconfig.JsonDateTimeAdapter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 *
 * @author Matthias
 */
public class AccountingPeriodDTO {
    
    public static List<AccountingPeriodDTO> create(List<AccountingPeriod> apList) {
        List<AccountingPeriodDTO> result = new ArrayList<>();
        
        for(AccountingPeriod ap : apList) {
            AccountingPeriodDTO dto = new AccountingPeriodDTO();
            dto.accountingNumber = ap.getAccountingNumber();
            dto.periodFrom = ap.getPeriodFrom();
            dto.periodTo = ap.getPeriodTo();
            dto.price = ap.getPrice();
            dto.taxRate = ap.getTaxRate();
            dto.accoutingStatus = ap.getAccountingStatus();
            dto.accountingCurrency = ap.getAccountingCurrency();
            result.add(dto);
        }
        
        return result;
    }

    @XmlJavaTypeAdapter(JsonDateTimeAdapter.class)
    public Date periodFrom;
    @XmlJavaTypeAdapter(JsonDateTimeAdapter.class)
    public Date periodTo;

    public BigDecimal taxRate;
    public BigDecimal price;
    public String accountingNumber;
    public String accoutingStatus;
    public String accountingCurrency;
    

}
