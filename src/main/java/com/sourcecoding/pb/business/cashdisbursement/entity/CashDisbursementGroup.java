/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sourcecoding.pb.business.cashdisbursement.entity;

import com.sourcecoding.pb.business.individuals.entity.Individual;
import java.io.Serializable;
import java.math.BigDecimal;
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
import javax.persistence.OrderBy;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author Matthias
 */
@Entity
@NamedQueries({
    @NamedQuery(name = CashDisbursementGroup.findByGroupKey, query = "SELECT o FROM CashDisbursementGroup o WHERE o.individual.id = :" + CashDisbursementGroup.queryParam_individualId + " AND o.groupKey = :" + CashDisbursementGroup.queryParam_groupKey)
})
public class CashDisbursementGroup implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String findByGroupKey = "CashDisbursementGroup#findByMonth";

    public static final String queryParam_individualId = "individualId";
    public static final String queryParam_groupKey = "groupKey";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private Individual individual;
    private String groupKey;

    @Temporal(TemporalType.DATE)
    private Date groupFrom;

    @Temporal(TemporalType.DATE)
    private Date groupTo;

    @OrderBy("cdDate ASC")
    @OneToMany(mappedBy = "cashDisbursementGroup", cascade = CascadeType.ALL, orphanRemoval = true)
    List<CashDisbursement> cashDisbursementList;

    private String groupState;

    @Column(precision = 10, scale = 2)
    private BigDecimal groupSum;

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

    public String getGroupKey() {
        return groupKey;
    }

    public void setGroupKey(String groupKey) {
        this.groupKey = groupKey;
    }

    public List<CashDisbursement> getCashDisbursementList() {
        return cashDisbursementList;
    }

    public void setCashDisbursementList(List<CashDisbursement> cashDisbursementList) {
        this.cashDisbursementList = cashDisbursementList;
    }

    public Date getGroupFrom() {
        return groupFrom;
    }

    public void setGroupFrom(Date groupFrom) {
        this.groupFrom = groupFrom;
    }

    public Date getGroupTo() {
        return groupTo;
    }

    public void setGroupTo(Date groupTo) {
        this.groupTo = groupTo;
    }

    public String getGroupState() {
        return groupState;
    }

    public void setGroupState(String groupState) {
        this.groupState = groupState;
    }

    public BigDecimal getGroupSum() {
        return groupSum;
    }

    public void setGroupSum(BigDecimal groupSum) {
        this.groupSum = groupSum;
    }

}
