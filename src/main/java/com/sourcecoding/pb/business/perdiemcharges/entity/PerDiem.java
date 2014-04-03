/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sourcecoding.pb.business.perdiemcharges.entity;

import com.sourcecoding.pb.business.project.entity.ProjectInformation;
import com.sourcecoding.pb.business.individuals.entity.Individual;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.CascadeType;
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
public class PerDiem implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private Individual individual;
    @ManyToOne
    private PerDiemGroup perDiemGroup;
    @Temporal(TemporalType.DATE)
    private Date perDiemDate;
    private Double timeFrom;
    private Double timeTo;
    @ManyToOne
    private ProjectInformation project;
    @ManyToOne
    private TravelExpensesRate travelExpensesRate;
    @Column(precision = 6, scale = 2)
    private BigDecimal charges; //FIXME decimal places!

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

    public BigDecimal getCharges() {
        return charges;
    }

    public void setCharges(BigDecimal charges) {
        this.charges = charges;
    }

    public Date getPerDiemDate() {
        return perDiemDate;
    }

    public void setPerDiemDate(Date perDiemDate) {
        this.perDiemDate = perDiemDate;
    }

    public PerDiemGroup getPerDiemGroup() {
        return perDiemGroup;
    }

    public void setPerDiemGroup(PerDiemGroup perDiemGroup) {
        this.perDiemGroup = perDiemGroup;
    }

}
