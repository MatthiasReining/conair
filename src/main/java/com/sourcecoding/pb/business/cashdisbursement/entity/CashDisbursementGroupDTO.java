/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sourcecoding.pb.business.cashdisbursement.entity;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Matthias
 */
public class CashDisbursementGroupDTO {
    
    public CashDisbursementGroupDTO() {        
    }
    
    public CashDisbursementGroupDTO( CashDisbursementGroup cdGroup ) {
        this.indiviudalId = cdGroup.getIndividual().getId();
        this.yearMonth = cdGroup.getGroupKey();
        this.groupState = cdGroup.getGroupState();
        this.sum = cdGroup.getGroupSum().doubleValue();
        this.cashDisbursementList = new ArrayList<>();
        for (CashDisbursement cd : cdGroup.getCashDisbursementList()) {            
            this.cashDisbursementList.add( new CashDisbursementDTO(cd) );
        }
    }

    public Long indiviudalId;
    public String yearMonth;
    public String groupState;
    public Double sum;
    public List<CashDisbursementDTO> cashDisbursementList;

    
}
