/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sourcecoding.pb.business.cashdisbursement.entity;

import com.sourcecoding.pb.business.individuals.entity.Individual;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author Matthias
 */
@Entity
public class CashDisbursement implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private Individual individual;
    @ManyToOne
    private CashDisbursementGroup cashDisbursementGroup;
    @Temporal(TemporalType.DATE)
    private Date cdDate;
    private String description;
    private String cdCategory;
    @Column(precision = 10, scale = 2)
    private BigDecimal amount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Individual getIndividual() {
        return individual;
    }

    public void setIndividual(Individual individual) {
        this.individual = individual;
    }

    public CashDisbursementGroup getCashDisbursementGroup() {
        return cashDisbursementGroup;
    }

    public void setCashDisbursementGroup(CashDisbursementGroup cashDisbursementGroup) {
        this.cashDisbursementGroup = cashDisbursementGroup;
    }

    public Date getCdDate() {
        return cdDate;
    }

    public void setCdDate(Date cdDate) {
        this.cdDate = cdDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCdCategory() {
        return cdCategory;
    }

    public void setCdCategory(String cdCategory) {
        this.cdCategory = cdCategory;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

}
