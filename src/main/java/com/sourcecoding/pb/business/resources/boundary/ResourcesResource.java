/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sourcecoding.pb.business.resources.boundary;

import com.sourcecoding.pb.business.individuals.boundary.IndividualsResource;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

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

    @Path("individuals")
    public IndividualsResource getPeople() {
        return resourceContext.getResource(IndividualsResource.class);
    }
}
