/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sourcecoding.pb.business.configuration.boundary;

import com.sourcecoding.pb.business.configuration.entity.Configuration;
import java.math.BigDecimal;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Matthias
 */
@Stateless
public class Configurator {

    @PersistenceContext
    EntityManager em;

    public String getValue(String key) {
        return em.createNamedQuery(Configuration.findByConfigKey, Configuration.class)
                .setParameter(Configuration.queryParam_key, key)
                .getSingleResult().getConfigValue();
    }

    public Long getLongValue(String key) {
        return Long.valueOf(getValue(key));
    }

    public Integer getIntegerValue(String key) {
        return Integer.valueOf(getValue(key));
    }
    
    public Double getDoubleValue(String key) {
        return Double.valueOf(getValue(key));
    }

    public BigDecimal getBigDecimalValue(String key) {
        return new BigDecimal(getValue(key));
    }

}
