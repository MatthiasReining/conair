/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sourcecoding.pb.business.perdiemcharges.entity;

import com.sourcecoding.pb.business.project.entity.ProjectInformation;
import com.sourcecoding.pb.business.user.entity.Individual;
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
public class PerDiem implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private Individual individual;
    @ManyToOne
    private ChargesMonth chargesMonth;
    @Temporal(TemporalType.DATE)
    private Date perDiemDate;
    @Temporal(TemporalType.TIME)
    private Date inServiceFrom;
    @Temporal(TemporalType.TIME)
    private Date inServiceTo;
    private Boolean fulltime;
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

    public Date getInServiceFrom() {
        return inServiceFrom;
    }

    public void setInServiceFrom(Date inServiceFrom) {
        this.inServiceFrom = inServiceFrom;
    }

    public Date getInServiceTo() {
        return inServiceTo;
    }

    public void setInServiceTo(Date inServiceTo) {
        this.inServiceTo = inServiceTo;
    }

    public Boolean getFulltime() {
        return fulltime;
    }

    public void setFulltime(Boolean fulltime) {
        this.fulltime = fulltime;
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

    public ChargesMonth getChargesMonth() {
        return chargesMonth;
    }

    public void setChargesMonth(ChargesMonth chargesMonth) {
        this.chargesMonth = chargesMonth;
    }
}
