/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sourcecoding.pb.business.authentication.boundary;

import com.sourcecoding.pb.business.authentication.entity.User;
import com.sourcecoding.pb.business.meta.boundary.VersionService;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author Matthias
 */
@Path("auth")
@Stateless
public class AuthService {

    @Inject
    @CurrentUser
    User user;
    @Inject
    VersionService versionService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserInfo() throws IOException, ClassNotFoundException {
        Map<String, Object> result = new HashMap<>();
        result.put("version", versionService.getVersionInformation());

        try {
            System.out.println("auth/user: " + user);

            User userCopy = new User();
            userCopy.setFirstName(user.getFirstName());
            userCopy.setLastName(user.getLastName());
            userCopy.setSocialNetId(user.getSocialNetId());
            userCopy.setId(user.getId());
            userCopy.setTitle(user.getTitle());
            userCopy.setPictureUrl(user.getPictureUrl());

            result.put("user", userCopy);
            return Response.ok().entity(result).build();

        } catch (SecurityException e) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(result).build();
        }

    }
}
