/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sourcecoding.pb.business.authentication.controller;

import java.lang.reflect.Method;
import javax.inject.Inject;
import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.Provider;


//@Provider
public class AuthenticationFeature implements DynamicFeature {

    @Inject
    LinkedInAuthenticationProvider authenticationProvider;

    @Override
    public void configure(ResourceInfo resourceInfo, FeatureContext context) {
        
        Method method = resourceInfo.getResourceMethod();
        
        System.out.println("in AuthenticationFeature#configure -> " + resourceInfo.getResourceClass().getName() + "#" + method.getName());
        if (!method.isAnnotationPresent(AuthenticationNotRequired.class)) {
            //AuthenticationFilter authenticationFilter
            //        = new AuthenticationFilter(authenticationProvider);
            //context.register(authenticationFilter);
        }
    }

}
