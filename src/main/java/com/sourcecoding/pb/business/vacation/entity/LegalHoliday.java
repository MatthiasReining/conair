/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sourcecoding.pb.business.vacation.entity;

import com.sourcecoding.pb.business.project.entity.ProjectInformation;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
    @NamedQuery(name = LegalHoliday.findByDate, query = "SELECT o FROM LegalHoliday o WHERE o.legalHolidayDate = :" + LegalHoliday.queryParam_date),})
public class LegalHoliday implements Serializable {
    
    
    public static final String findByDate = "LegalHoliday.findByDate";
    
    public static final String queryParam_date = "date";
 

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Temporal(TemporalType.DATE)
    private Date legalHolidayDate;
    private String legalHolidyName;
    private String legalHolidayState;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getLegalHolidayDate() {
        return legalHolidayDate;
    }

    public void setLegalHolidayDate(Date legalHolidayDate) {
        this.legalHolidayDate = legalHolidayDate;
    }

    public String getLegalHolidyName() {
        return legalHolidyName;
    }

    public void setLegalHolidyName(String legalHolidyName) {
        this.legalHolidyName = legalHolidyName;
    }

    public String getLegalHolidayState() {
        return legalHolidayState;
    }

    public void setLegalHolidayState(String legalHolidayState) {
        this.legalHolidayState = legalHolidayState;
    }

}
