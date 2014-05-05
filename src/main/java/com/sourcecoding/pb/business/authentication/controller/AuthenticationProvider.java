/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sourcecoding.pb.business.authentication.controller;

import com.sourcecoding.pb.business.authentication.entity.User;
import javax.ws.rs.container.ContainerRequestContext;

/**
 *
 * @author Matthias
 */
public interface AuthenticationProvider {
    
    User authenticateUser(ContainerRequestContext requestContext);
}
