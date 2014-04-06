/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sourcecoding.pb.business.travelcosts.entity;

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
    @NamedQuery(name = TravelCostsGroup.findByGroupKey, query = "SELECT o FROM TravelCostsGroup o WHERE o.individual.id = :" + TravelCostsGroup.queryParam_individualId + " AND o.groupKey = :" + TravelCostsGroup.queryParam_groupKey)
})
public class TravelCostsGroup implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String findByGroupKey = "TravelCostsGroup#findByMonth";

    public static final String queryParam_individualId = "individualId";
    public static final String queryParam_groupKey = "groupKey";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private Individual individual;
    private String groupKey;

    @Temporal(TemporalType.DATE)
    private Date travelCostsFrom;

    @Temporal(TemporalType.DATE)
    private Date travelCostsTo;

    @OneToMany(mappedBy = "travelCostsGroup", cascade = CascadeType.ALL, orphanRemoval = true)
    List<TravelCosts> travelCostsList;

    private String travelCostsGroupState;

    private BigDecimal travelCostsGroupSum;

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

    public Date getTravelCostsFrom() {
        return travelCostsFrom;
    }

    public void setTravelCostsFrom(Date travelCostsFrom) {
        this.travelCostsFrom = travelCostsFrom;
    }

    public Date getTravelCostsTo() {
        return travelCostsTo;
    }

    public void setTravelCostsTo(Date travelCostsTo) {
        this.travelCostsTo = travelCostsTo;
    }

    public List<TravelCosts> getTravelCostsList() {
        return travelCostsList;
    }

    public void setTravelCostsList(List<TravelCosts> travelCostsList) {
        this.travelCostsList = travelCostsList;
    }

    public String getTravelCostsGroupState() {
        return travelCostsGroupState;
    }

    public void setTravelCostsGroupState(String travelCostsGroupState) {
        this.travelCostsGroupState = travelCostsGroupState;
    }

    public BigDecimal getTravelCostsGroupSum() {
        return travelCostsGroupSum;
    }

    public void setTravelCostsGroupSum(BigDecimal travelCostsGroupSum) {
        this.travelCostsGroupSum = travelCostsGroupSum;
    }

}
