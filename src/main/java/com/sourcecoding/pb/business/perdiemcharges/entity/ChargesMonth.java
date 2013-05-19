/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sourcecoding.pb.business.perdiemcharges.entity;

import com.sourcecoding.pb.business.project.entity.ProjectInformation;
import com.sourcecoding.pb.business.user.entity.Individual;
import java.io.Serializable;
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
    @NamedQuery(name = ChargesMonth.findByMonth, query = "SELECT o FROM ChargesMonth o WHERE o.individual.id = :" + ChargesMonth.queryParam_individualId + " AND o.chargesMonth = :" + ChargesMonth.queryParam_yearMonth)
})
public class ChargesMonth implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public static final String findByMonth = "ChargesMonth#findByMonth";
    
    public static final String queryParam_individualId = "individualId";
    public static final String queryParam_yearMonth = "yearMonth";
    
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private Individual individual;
    private String chargesMonth;
    
    @OneToMany(mappedBy = "chargesMonth", cascade = CascadeType.ALL)
    List<PerDiem> perDiems;    
    private String chargesState;

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

    public List<PerDiem> getPerDiems() {
        return perDiems;
    }

    public void setPerDiems(List<PerDiem> perDiems) {
        this.perDiems = perDiems;
    }

    public String getChargesState() {
        return chargesState;
    }

    public void setChargesState(String chargesState) {
        this.chargesState = chargesState;
    }

    public String getChargesMonth() {
        return chargesMonth;
    }

    public void setChargesMonth(String chargesMonth) {
        this.chargesMonth = chargesMonth;
    }
    
    
}
