/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sourcecoding.pb.business.perdiemcharges.entity;

import com.sourcecoding.pb.business.individuals.entity.Individual;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
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
    @NamedQuery(name = PerDiemGroup.findByGroupKey, query = "SELECT o FROM PerDiemGroup o WHERE o.individual.id = :" + PerDiemGroup.queryParam_individualId + " AND o.groupKey = :" + PerDiemGroup.queryParam_groupKey)
})
public class PerDiemGroup implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String findByGroupKey = "PerDiemGroup#findByMonth";

    public static final String queryParam_individualId = "individualId";
    public static final String queryParam_groupKey = "groupKey";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private Individual individual;
    private String groupKey;

    @Temporal(TemporalType.DATE)
    private Date perDiemFrom;

    @Temporal(TemporalType.DATE)
    private Date perDiemTo;

    @OneToMany(mappedBy = "perDiemGroup", cascade = CascadeType.ALL, orphanRemoval = true)
    List<PerDiem> perDiemList;

    private String perDiemGroupState;

    private BigDecimal perDiemGroupSum;

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

    public Date getPerDiemFrom() {
        return perDiemFrom;
    }

    public void setPerDiemFrom(Date perDiemFrom) {
        this.perDiemFrom = perDiemFrom;
    }

    public Date getPerDiemTo() {
        return perDiemTo;
    }

    public void setPerDiemTo(Date perDiemTo) {
        this.perDiemTo = perDiemTo;
    }

    public List<PerDiem> getPerDiemList() {
        return perDiemList;
    }

    public void setPerDiemList(List<PerDiem> perDiemList) {
        this.perDiemList = perDiemList;
    }

    public String getPerDiemGroupState() {
        return perDiemGroupState;
    }

    public void setPerDiemGroupState(String perDiemGroupState) {
        this.perDiemGroupState = perDiemGroupState;
    }

    public BigDecimal getPerDiemGroupSum() {
        return perDiemGroupSum;
    }

    public void setPerDiemGroupSum(BigDecimal perDiemGroupSum) {
        this.perDiemGroupSum = perDiemGroupSum;
    }

}
