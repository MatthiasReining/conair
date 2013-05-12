/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sourcecoding.pb.business.project.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 *
 * @author Matthias
 */
@Entity
public class WorkPackage implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String wpName;
    @Temporal(TemporalType.DATE)
    private Date bookabelFrom;
    @Temporal(TemporalType.DATE)
    private Date bookableTo;
    private Integer limitForWorkingHours;
    
    //FIXME add costs for every hour booked on a work package (temporal compontent=
    @JsonIgnore
    @ManyToOne
    private ProjectInformation projectInformation;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWpName() {
        return wpName;
    }

    public void setWpName(String wpName) {
        this.wpName = wpName;
    }

    public ProjectInformation getProjectInformation() {
        return projectInformation;
    }

    public void setProjectInformation(ProjectInformation projectInformation) {
        this.projectInformation = projectInformation;
    }

    public Date getBookabelFrom() {
        return bookabelFrom;
    }

    public void setBookabelFrom(Date bookabelFrom) {
        this.bookabelFrom = bookabelFrom;
    }

    public Date getBookableTo() {
        return bookableTo;
    }

    public void setBookableTo(Date bookableTo) {
        this.bookableTo = bookableTo;
    }

    public Integer getLimitForWorkingHours() {
        return limitForWorkingHours;
    }

    public void setLimitForWorkingHours(Integer limitForWorkingHours) {
        this.limitForWorkingHours = limitForWorkingHours;
    }
}
