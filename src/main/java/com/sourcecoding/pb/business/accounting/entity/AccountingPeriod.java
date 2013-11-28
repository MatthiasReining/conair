/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sourcecoding.pb.business.accounting.entity;

import com.sourcecoding.pb.business.project.entity.ProjectInformation;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author Matthias
 */
@Entity
@NamedQueries({
    @NamedQuery(name = AccountingPeriod.findByProject, query = "SELECT ap FROM AccountingPeriod ap WHERE ap.projectInformation = :" + AccountingPeriod.queryParam_project)
})
public class AccountingPeriod implements Serializable {

    public static final String findByProject = "AccountingPeriod.findByProject";
    public static final String queryParam_project = "proejct";

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private ProjectInformation projectInformation;

    @Temporal(TemporalType.DATE)
    private Date periodFrom;

    @Temporal(TemporalType.DATE)
    private Date periodTo;

    @Column(precision = 6, scale = 2)
    private BigDecimal taxRate;

    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    private String accountingNumber;

    private int accountingStatus;
    private String accountingCurrency;

    @OneToMany(mappedBy = "accountingPeriod", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AccountingTimeDetail> accoutingTimeDetails = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ProjectInformation getProjectInformation() {
        return projectInformation;
    }

    public void setProjectInformation(ProjectInformation projectInformation) {
        this.projectInformation = projectInformation;
    }

    public List<AccountingTimeDetail> getAccoutingTimeDetails() {
        return accoutingTimeDetails;
    }

    public void setAccoutingTimeDetails(List<AccountingTimeDetail> accoutingTimeDetails) {
        this.accoutingTimeDetails = accoutingTimeDetails;
    }

    public Date getPeriodFrom() {
        return periodFrom;
    }

    public void setPeriodFrom(Date periodFrom) {
        this.periodFrom = periodFrom;
    }

    public Date getPeriodTo() {
        return periodTo;
    }

    public void setPeriodTo(Date periodTo) {
        this.periodTo = periodTo;
    }

    public BigDecimal getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(BigDecimal taxRate) {
        this.taxRate = taxRate;
    }

    public String getAccountingNumber() {
        return accountingNumber;
    }

    public void setAccountingNumber(String accountingNumber) {
        this.accountingNumber = accountingNumber;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getAccountingCurrency() {
        return accountingCurrency;
    }

    public void setAccountingCurrency(String accountingCurrency) {
        this.accountingCurrency = accountingCurrency;
    }

    public int getAccountingStatus() {
        return accountingStatus;
    }

    public void setAccountingStatus(int accountingStatus) {
        this.accountingStatus = accountingStatus;
    }

}
