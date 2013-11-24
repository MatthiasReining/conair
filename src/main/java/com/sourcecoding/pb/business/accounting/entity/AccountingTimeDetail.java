/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sourcecoding.pb.business.accounting.entity;

import com.sourcecoding.pb.business.individuals.entity.Individual;
import com.sourcecoding.pb.business.project.entity.ProjectMember;
import com.sourcecoding.pb.business.project.entity.WorkPackage;
import com.sourcecoding.pb.business.timerecording.entity.TimeRecord;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * SELECT pm.title, atd.user_ID, atd.pricehour, sum(atd.workingtime/60.0),
 * sum(atd.workingtime/60.0/8.0), sum(atd.price) FROM APP.ACCOUNTINGTIMEDETAIL
 * atd JOIN APP.PROJECTMEMBER pm ON atd.USER_ID = pm.INDIVIDUAL_ID WHERE
 * atd.ACCOUNTINGPERIOD_ID = 1601 GROUP BY pm.TITLE, atd.user_ID, atd.pricehour
 */
/**
 *
 * @author Matthias
 */
@Entity
public class AccountingTimeDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String groupByTitleAndUser = "AccountingTimeDetail#groupByTitleAndUser";
    public static final String queryParam_accountingPeriod = "accountingPeriod";
    
    public AccountingTimeDetail() {
    }

    public AccountingTimeDetail(TimeRecord tr) {
        this.describtion = tr.getDescribtion();
        this.user = tr.getUser();
        this.workPackage = tr.getWorkPackage();
        this.workingDay = tr.getWorkingDay();
        this.workingTime = tr.getWorkingTime();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private Individual user;

    private String status;

    @Temporal(TemporalType.DATE)
    private Date workingDay;

    @ManyToOne
    private AccountingPeriod accountingPeriod;
    @ManyToOne
    private WorkPackage workPackage;

    private String describtion;
    private Integer workingTime;

    @Column(precision = 10, scale = 2)
    private BigDecimal price;
    @Column(precision = 10, scale = 2)
    private BigDecimal priceHour;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Individual getUser() {
        return user;
    }

    public void setUser(Individual user) {
        this.user = user;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getWorkingDay() {
        return workingDay;
    }

    public void setWorkingDay(Date workingDay) {
        this.workingDay = workingDay;
    }

    public AccountingPeriod getAccountingPeriod() {
        return accountingPeriod;
    }

    public void setAccountingPeriod(AccountingPeriod accountingPeriod) {
        this.accountingPeriod = accountingPeriod;
    }

    public WorkPackage getWorkPackage() {
        return workPackage;
    }

    public void setWorkPackage(WorkPackage workPackage) {
        this.workPackage = workPackage;
    }

    public String getDescribtion() {
        return describtion;
    }

    public void setDescribtion(String describtion) {
        this.describtion = describtion;
    }

    public Integer getWorkingTime() {
        return workingTime;
    }

    public void setWorkingTime(Integer workingTime) {
        this.workingTime = workingTime;
    }

    
    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getPriceHour() {
        return priceHour;
    }

    public void setPriceHour(BigDecimal priceHour) {
        this.priceHour = priceHour;
    }

}
