/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sourcecoding.pb.business.travelcosts.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

/**
 *
 * @author Matthias
 */
@Entity
@NamedQueries({
    @NamedQuery(name = TravelExpensesRate.findByDate, query = "SELECT o FROM TravelExpensesRate o WHERE o.travelYear = :" + TravelExpensesRate.queryParam_travelYear + " ORDER BY o.country")
})
public class TravelExpensesRate implements Serializable {
    
    public static final String findByDate = "TravelExpensesRate#findByDate";
    
     
    public static final String queryParam_travelYear = "travelYear";
  

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    private Integer travelYear;
    private String country;
    @Column(precision = 6, scale = 2)
    private BigDecimal rate24h;
    @Column(precision = 6, scale = 2)
    private BigDecimal rateFrom8To24;
    @Column(precision = 6, scale = 2)
    private BigDecimal accommodationExpenses;
    @Column(precision = 6, scale = 2)
    private BigDecimal breakfast;
    @Column(precision = 6, scale = 2)
    private BigDecimal lunch;
    @Column(precision = 6, scale = 2)
    private BigDecimal dinner;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getTravelYear() {
        return travelYear;
    }

    public void setTravelYear(Integer travelYear) {
        this.travelYear = travelYear;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public BigDecimal getRate24h() {
        return rate24h;
    }

    public void setRate24h(BigDecimal rate24h) {
        this.rate24h = rate24h;
    }

    public BigDecimal getRateFrom8To24() {
        return rateFrom8To24;
    }

    public void setRateFrom8To24(BigDecimal rateFrom8To24) {
        this.rateFrom8To24 = rateFrom8To24;
    }

    public BigDecimal getAccommodationExpenses() {
        return accommodationExpenses;
    }

    public void setAccommodationExpenses(BigDecimal accommodationExpenses) {
        this.accommodationExpenses = accommodationExpenses;
    }

    public BigDecimal getBreakfast() {
        return breakfast;
    }

    public void setBreakfast(BigDecimal breakfast) {
        this.breakfast = breakfast;
    }

    public BigDecimal getLunch() {
        return lunch;
    }

    public void setLunch(BigDecimal lunch) {
        this.lunch = lunch;
    }

    public BigDecimal getDinner() {
        return dinner;
    }

    public void setDinner(BigDecimal dinner) {
        this.dinner = dinner;
    }

    
}
