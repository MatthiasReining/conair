/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sourcecoding.pb.business.user.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Matthias
 */
@Entity
@NamedQueries({
    @NamedQuery(name = Individual.findByNickname, query = "SELECT i FROM Individual i WHERE i.nickname= :" + Individual.queryParam_nickname),
    @NamedQuery(name = Individual.findAll, query = "SELECT i FROM Individual i"),
    @NamedQuery(name = Individual.findByLinkedInId, query = "SELECT i FROM Individual i WHERE i.linkedInId = :" + Individual.queryParam_socialNetId)
})
public class Individual implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final String findByNickname = "Individual#findByNickname";
    public static final String findByLinkedInId = "Individual#findByLinkedInId";    
    public static final String findAll = "Individual#findAll";
    
    public static final String queryParam_nickname = "nickname";
    public static final String queryParam_socialNetId = "socialNetId";
    
   
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String nickname;
    private String linkedInId;
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastLogin;
    
    private Integer vacationDaysPerYear;
    private Integer workdaysPerWeek;
    
    @XmlTransient
    @OneToOne
    private Individual vacationManager;
    
    @XmlTransient
    @ManyToMany(mappedBy = "individual", cascade = CascadeType.ALL)
    private List<IndividualRole> roles;
    
    private String emailAddress;
    
    
    public boolean hasRole(String roleName) {
        for (IndividualRole ir : getRoles())
            if (ir.getRoleName().equals(roleName)) return true;
        return false;
    }
    public boolean hasRole(IndividualRole individualRole) {
        for (IndividualRole ir : getRoles())
            if (ir.getId().equals(individualRole.getId())) return true;
        return false;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getLinkedInId() {
        return linkedInId;
    }

    public void setLinkedInId(String linkedInId) {
        this.linkedInId = linkedInId;
    }

    public Date getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }

    public Integer getVacationDaysPerYear() {
        return vacationDaysPerYear;
    }

    public void setVacationDaysPerYear(Integer vacationDaysPerYear) {
        this.vacationDaysPerYear = vacationDaysPerYear;
    }

    public Integer getWorkdaysPerWeek() {
        return workdaysPerWeek;
    }

    public void setWorkdaysPerWeek(Integer workdaysPerWeek) {
        this.workdaysPerWeek = workdaysPerWeek;
    }

    public List<IndividualRole> getRoles() {
        return roles;
    }

    public void setRoles(List<IndividualRole> roles) {
        this.roles = roles;
    }

    public Individual getVacationManager() {
        return vacationManager;
    }

    public void setVacationManager(Individual vacationManager) {
        this.vacationManager = vacationManager;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
    
    
}
