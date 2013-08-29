/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sourcecoding.pb.business.vacation.entity;

import com.sourcecoding.pb.business.individuals.entity.Individual;
import java.io.Serializable;
import java.util.Date;
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
 *
 * @author Matthias
 */
@Entity
@NamedQueries({
    @NamedQuery(name = VacationRecord.findByApprovalState, query = "SELECT o FROM VacationRecord o WHERE o.approvalState = :" + VacationRecord.queryParam_approvalState + " and o.individual.vacationManager = :" + VacationRecord.queryParam_vacationManager)
})
public class VacationRecord implements Serializable {

    public static final String findByApprovalState = "VacationRecord.findByApprovalState";
    public static final String queryParam_approvalState = "approvalState";
    public static final String queryParam_vacationManager = "vacationManager";
    
    public static final int APPROVAL_STATE_NEW = 0;
    public static final int APPROVAL_STATE_FOR_APPROVAL = 1;
    public static final int APPROVAL_STATE_APPROVED = 5;
    public static final int APPROVAL_STATE_REJECTED = 9;
    
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private Individual individual;
    @ManyToOne
    private VacationYear vacationYear;
    private Integer numberOfDays;
    @Temporal(TemporalType.DATE)
    private Date vacationFrom;
    @Temporal(TemporalType.DATE)
    private Date vacationUntil;
    private Integer approvalState;
    
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

    public Integer getNumberOfDays() {
        return numberOfDays;
    }

    public void setNumberOfDays(Integer numberOfDays) {
        this.numberOfDays = numberOfDays;
    }

    public Date getVacationFrom() {
        return vacationFrom;
    }

    public void setVacationFrom(Date vacationFrom) {
        this.vacationFrom = vacationFrom;
    }

    public Date getVacationUntil() {
        return vacationUntil;
    }

    public void setVacationUntil(Date vacationUntil) {
        this.vacationUntil = vacationUntil;
    }

    public Integer getApprovalState() {
        return approvalState;
    }

    public void setApprovalState(Integer approvalState) {
        this.approvalState = approvalState;
    }

    public VacationYear getVacationYear() {
        return vacationYear;
    }

    public void setVacationYear(VacationYear vacationYear) {
        this.vacationYear = vacationYear;
    }
}
