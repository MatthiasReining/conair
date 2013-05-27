/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sourcecoding.pb.business.authentication.controller;

import com.sourcecoding.pb.business.authentication.boundary.CurrentUser;
import com.sourcecoding.pb.business.authentication.entity.User;
import com.sourcecoding.pb.business.user.boundary.IndividualService;
import com.sourcecoding.pb.business.user.entity.Individual;
import java.io.Serializable;
import javax.enterprise.context.Dependent;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Matthias
 */
@SessionScoped
public class UserFactory implements Serializable {

    @Inject
    private IndividualService individualService;

    @PersistenceContext
    EntityManager em;
    private static final long serialVersionUID = 1L;
    
    User currentUser;

    /**
     * Produces an instance of the current logged in User.<br>
     * The Scope is defined as
     * <code>@RequestScoped</code>(!), <code>@SessionScoped</code> is not
     * suitable, because JAX-RS and servlet beans caches the injected bean, so a
     * modification at the object during the current session would not possible.
     * @return Current logged in user.
     */
    @Produces
    @CurrentUser
    @RequestScoped
    User getCurrentUser() {
        if (currentUser == null) {
            throw new SecurityException("No user is logged in");
        }
        return currentUser;
    }

    public void loginUser(User user) {
        Individual individual = individualService.loginBySocialNetId(user.getSocialNetId());
        if (individual == null) {
            throw new SecurityException("User " + user.getFirstName() + " " + user.getLastName() + "(social net id: " + user.getSocialNetId() + ") has no account on this platform! Please contact the platform admin.");
            //individual = new Individual();
            //individual.setId(123L);
                    
        }
        user.setId(String.valueOf(individual.getId()));


        System.out.println("userFactory#oginUser: " + this);
        System.out.println("user: " + user);
        currentUser = user;
    }
}
