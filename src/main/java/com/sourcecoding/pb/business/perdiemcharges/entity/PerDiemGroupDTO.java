/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sourcecoding.pb.business.perdiemcharges.entity;

import java.util.List;

/**
 *
 * @author Matthias
 */
public class PerDiemGroupDTO {

    public Long indiviudalId;
    public String yearMonth;
    public String perDiemGroupState;
    public Double sum;
    public List<PerDiemDTO> perDiemList;

    
}
