/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sourcecoding.pb.business.resources.boundary;

import com.sourcecoding.pb.business.user.entity.JsonPeople;
import com.sourcecoding.pb.business.user.boundary.IndividualService;
import com.sourcecoding.pb.business.user.boundary.PeopleResource;
import com.sourcecoding.pb.business.user.entity.Individual;
import com.sourcecoding.pb.business.vacation.boundary.TasksResource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author Matthias
 */
@Stateless
@Path("resources")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ResourcesResource {

    @Context
    private ResourceContext resourceContext;

    @Path("people")
    public PeopleResource getPeople() {
        return resourceContext.getResource(PeopleResource.class);
    }
}
