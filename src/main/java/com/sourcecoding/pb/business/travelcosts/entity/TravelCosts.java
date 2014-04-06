/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sourcecoding.pb.business.travelcosts.entity;

import com.sourcecoding.pb.business.project.entity.ProjectInformation;
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
public class TravelCosts implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private Individual individual;
    @ManyToOne
    private TravelCostsGroup travelCostsGroup;
    @Temporal(TemporalType.DATE)
    private Date travelCostsDate;
    private Double timeFrom;
    private Double timeTo;
    @ManyToOne
    private ProjectInformation project;
    @ManyToOne
    private TravelExpensesRate travelExpensesRate;

    private boolean breakfast;
    private boolean lunch;
    private boolean dinner;

    private boolean taxable;

    @Column(precision = 6, scale = 2)
    private BigDecimal charges;

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

    public TravelCostsGroup getTravelCostsGroup() {
        return travelCostsGroup;
    }

    public void setTravelCostsGroup(TravelCostsGroup travelCostsGroup) {
        this.travelCostsGroup = travelCostsGroup;
    }

    public Date getTravelCostsDate() {
        return travelCostsDate;
    }

    public void setTravelCostsDate(Date travelCostsDate) {
        this.travelCostsDate = travelCostsDate;
    }

    public Double getTimeFrom() {
        return timeFrom;
    }

    public void setTimeFrom(Double timeFrom) {
        this.timeFrom = timeFrom;
    }

    public Double getTimeTo() {
        return timeTo;
    }

    public void setTimeTo(Double timeTo) {
        this.timeTo = timeTo;
    }

    public ProjectInformation getProject() {
        return project;
    }

    public void setProject(ProjectInformation project) {
        this.project = project;
    }

    public TravelExpensesRate getTravelExpensesRate() {
        return travelExpensesRate;
    }

    public void setTravelExpensesRate(TravelExpensesRate travelExpensesRate) {
        this.travelExpensesRate = travelExpensesRate;
    }

    public boolean isBreakfast() {
        return breakfast;
    }

    public void setBreakfast(boolean breakfast) {
        this.breakfast = breakfast;
    }

    public boolean isLunch() {
        return lunch;
    }

    public void setLunch(boolean lunch) {
        this.lunch = lunch;
    }

    public boolean isDinner() {
        return dinner;
    }

    public void setDinner(boolean dinner) {
        this.dinner = dinner;
    }

    public boolean isTaxable() {
        return taxable;
    }

    public void setTaxable(boolean taxable) {
        this.taxable = taxable;
    }

    public BigDecimal getCharges() {
        return charges;
    }

    public void setCharges(BigDecimal charges) {
        this.charges = charges;
    }

}
