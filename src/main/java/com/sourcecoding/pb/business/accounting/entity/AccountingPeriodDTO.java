/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sourcecoding.pb.business.accounting.entity;

import com.sourcecoding.pb.business.individuals.entity.Individual;
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

    public static AccountingPeriodDTO create(AccountingPeriod ap, boolean details) {
        AccountingPeriodDTO dto = new AccountingPeriodDTO();
        dto.id = ap.getId();
        dto.accountingNumber = ap.getAccountingNumber();
        dto.periodFrom = ap.getPeriodFrom();
        dto.periodTo = ap.getPeriodTo();
        dto.price = ap.getPrice();
        dto.taxRate = ap.getTaxRate();
        dto.accoutingStatus = ap.getAccountingStatus();
        dto.accountingCurrency = ap.getAccountingCurrency();

        if (details) {
            for (AccountingTimeDetail atd : ap.getAccoutingTimeDetails()) {
                AccountingTimeDetailDTO atdDTO = dto.new AccountingTimeDetailDTO();
                dto.accountingTimeDetails.add(atdDTO);
                
                atdDTO.description = atd.getDescription();
                atdDTO.price = atd.getPrice();
                atdDTO.priceHour = atd.getPriceHour();
                atdDTO.status = atd.getStatus();
                //atdDTO.user = atd.getUser();
                atdDTO.workPackageId = atd.getWorkPackage().getId();
                atdDTO.workingDay = atd.getWorkingDay();
                atdDTO.workingTime = atd.getWorkingTime();
                
            }
        }
        return dto;
    }

    public static List<AccountingPeriodDTO> create(List<AccountingPeriod> apList) {
        List<AccountingPeriodDTO> result = new ArrayList<>();

        for (AccountingPeriod ap : apList) {
            AccountingPeriodDTO dto = create(ap, false);
            result.add(dto);
        }
        return result;
    }

    public Long id;

    @XmlJavaTypeAdapter(JsonDateTimeAdapter.class)
    public Date periodFrom;
    @XmlJavaTypeAdapter(JsonDateTimeAdapter.class)
    public Date periodTo;

    public BigDecimal taxRate;
    public BigDecimal price;
    public String accountingNumber;
    public String accoutingStatus;
    public String accountingCurrency;
    public List<AccountingTimeDetailDTO> accountingTimeDetails = new ArrayList<>();

    public class AccountingTimeDetailDTO {

        public Individual user;
        public String status;
        @XmlJavaTypeAdapter(JsonDateTimeAdapter.class)
        public Date workingDay;
        
        
        public Long workPackageId;
        public String description;
        public Integer workingTime;

        public BigDecimal price;
        public BigDecimal priceHour;
    }

}
