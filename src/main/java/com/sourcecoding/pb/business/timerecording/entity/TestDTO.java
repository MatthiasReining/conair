/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sourcecoding.pb.business.timerecording.entity;

import java.util.Date;
import java.util.Map;

/**
 *
 * @author Matthias
 */
public class TestDTO {
    
    private String name;
    private Date lastAccess;
    private Map<Date, String> tm;

    public Map<Date, String> getTm() {
        return tm;
    }

    public void setTm(Map<Date, String> tm) {
        this.tm = tm;
    }
    
    

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getLastAccess() {
        return lastAccess;
    }

    public void setLastAccess(Date lastAccess) {
        this.lastAccess = lastAccess;
    }
    
    
}
