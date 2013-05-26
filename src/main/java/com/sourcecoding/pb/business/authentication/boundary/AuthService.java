/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sourcecoding.pb.business.authentication.boundary;

import com.sourcecoding.pb.business.authentication.entity.User;
import java.io.IOException;
import java.io.Serializable;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author Matthias
 */
@Path("auth")
@Stateless
public class AuthService implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Inject
    @CurrentUser
    User user;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public User getUserInfo() throws IOException, ClassNotFoundException {
        System.out.println("auth/user: " + user);
        
        User userCopy = new User();
        userCopy.setFirstName(user.getFirstName());
        userCopy.setLastName(user.getLastName());
        userCopy.setSocialNetId(user.getSocialNetId());
        userCopy.setId(user.getId());
        userCopy.setTitle(user.getTitle());
        userCopy.setPictureUrl(user.getPictureUrl());
        return userCopy;
    }
}
