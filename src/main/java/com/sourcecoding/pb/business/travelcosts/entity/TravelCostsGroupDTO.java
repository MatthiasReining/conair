/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sourcecoding.pb.business.travelcosts.entity;

import java.util.List;

/**
 *
 * @author Matthias
 */
public class TravelCostsGroupDTO {

    public Long indiviudalId;
    public String yearMonth;
    public String travelCostsGroupState;
    public Double sum;
    public List<TravelCostsDTO> travelCostsList;

    
}
