/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sourcecoding.pb.business.vacation.entity;

import com.sourcecoding.pb.business.individuals.entity.Individual;
import java.io.Serializable;
import java.util.ArrayList;
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

/**
 *
 * @author Matthias
 */
@Entity
@NamedQueries({
    @NamedQuery(name = VacationYear.findByDate, query = "SELECT o FROM VacationYear o WHERE o.individual = :" + VacationYear.queryParam_individual + " and o.vacationYear = :" + VacationYear.queryParam_year ),
    @NamedQuery(name = VacationYear.findAllByYear, query = "SELECT o FROM VacationYear o WHERE o.vacationYear = :" + VacationYear.queryParam_year ),
})
public class VacationYear implements Serializable {

    public static final String findByDate = "VacationYear.findByYear";
    public static final String findAllByYear = "VacationYear.findAllByYear";
        
    public static final String queryParam_individual = "individual";
    public static final String queryParam_year = "year";
    
    
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private Individual individual;
    private Integer vacationYear;
    @OneToMany(mappedBy = "vacationYear", cascade = CascadeType.ALL)
    List<VacationRecord> vacationRecords = new ArrayList<>();
    private Integer numberOfVacationDays;
    private Integer residualLeaveYearBefore;
    private Integer residualLeave;
    private Integer approvedVacationDays;

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

    public Integer getVacationYear() {
        return vacationYear;
    }

    public void setVacationYear(Integer vacationYear) {
        this.vacationYear = vacationYear;
    }

    public List<VacationRecord> getVacationRecords() {
        return vacationRecords;
    }

    public void setVacationRecords(List<VacationRecord> vacationRecords) {
        this.vacationRecords = vacationRecords;
    }

    public Integer getNumberOfVacationDays() {
        return numberOfVacationDays;
    }

    public void setNumberOfVacationDays(Integer numberOfVacationDays) {
        this.numberOfVacationDays = numberOfVacationDays;
    }

    public Integer getResidualLeaveYearBefore() {
        return residualLeaveYearBefore;
    }

    public void setResidualLeaveYearBefore(Integer residualLeaveYearBefore) {
        this.residualLeaveYearBefore = residualLeaveYearBefore;
    }

    public Integer getResidualLeave() {
        return residualLeave;
    }

    public void setResidualLeave(Integer residualLeave) {
        this.residualLeave = residualLeave;
    }

    public Integer getApprovedVacationDays() {
        return approvedVacationDays;
    }

    public void setApprovedVacationDays(Integer approvedVacationDays) {
        this.approvedVacationDays = approvedVacationDays;
    }
    
    
    
}
