/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sourcecoding.pb.business.authentication.controller;

import com.sourcecoding.pb.business.authentication.entity.User;
import java.io.IOException;
import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;

//@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {

    AuthenticationProvider authenticationProvider;

    public AuthenticationFilter(AuthenticationProvider authenticationProvider) {
        this.authenticationProvider = authenticationProvider;
    }

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        User authenticatedUserInfo = this.authenticationProvider.authenticateUser(requestContext);
        System.out.println( "auth user: " + authenticatedUserInfo);
        //requestContext.setProperty(AuthenticatedUserInfo.PROPERTY, authenticatedUserInfo);
    }
}
