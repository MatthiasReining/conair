/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sourcecoding.pb.business.accounting.boundary;

import com.sourcecoding.pb.business.accounting.controller.AccountingTimeController;
import java.util.Date;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

/**
 *
 * @author Matthias
 */
@Stateless
@Path("accounting")
public class AccountingTimeService {
    
    @Inject
    AccountingTimeController atc;

    @GET
    @Path("project/{projectId}")
    public Response collectTimeRecords(@PathParam("projectId") Long projectId) {

        Date periodTo = new Date();
        Date periodFrom = null;

        if (periodFrom == null) {
            periodFrom = new Date(0L); //1.1.1970
        }
        
        atc.collectTimeRecords(projectId, periodFrom, periodTo);
        

        return Response.ok().build();
    }
}
