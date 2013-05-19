/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sourcecoding.pb.business.perdiemcharges.entity;

import java.io.Serializable;
import java.math.BigDecimal;
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
    @NamedQuery(name = TravelExpensesRate.findByDate, query = "SELECT o FROM TravelExpensesRate o ORDER BY o.country")
})
public class TravelExpensesRate implements Serializable {
    
    public static final String findByDate = "TravelExpensesRate#findByDate";

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String country;
    private BigDecimal rate24h;
    private BigDecimal rateFrom14To24;
    private BigDecimal rateFrom8To14;
    private BigDecimal accommodationExpenses;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public BigDecimal getRateFrom14To24() {
        return rateFrom14To24;
    }

    public void setRateFrom14To24(BigDecimal rateFrom14To24) {
        this.rateFrom14To24 = rateFrom14To24;
    }

    public BigDecimal getRateFrom8To14() {
        return rateFrom8To14;
    }

    public void setRateFrom8To14(BigDecimal rateFrom8To14) {
        this.rateFrom8To14 = rateFrom8To14;
    }

    public BigDecimal getAccommodationExpenses() {
        return accommodationExpenses;
    }

    public void setAccommodationExpenses(BigDecimal accommodationExpenses) {
        this.accommodationExpenses = accommodationExpenses;
    }
}
