/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sourcecoding.pb.business.authentication.controller;

import com.sourcecoding.pb.business.authentication.boundary.CurrentUser;
import com.sourcecoding.pb.business.authentication.entity.User;
import com.sourcecoding.pb.business.individuals.entity.Individual;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Matthias
 */
@SessionScoped
public class UserFactory implements Serializable {

    private static final long serialVersionUID = 1L;

    @PersistenceContext
    EntityManager em;

    User currentUser;

    /**
     * Produces an instance of the current logged in User.<br>
     * The Scope is defined as <code>@RequestScoped</code>(!),
     * <code>@SessionScoped</code> is not suitable, because JAX-RS and servlet
     * beans caches the injected bean, so a modification at the object during
     * the current session would not possible.
     *
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
        Individual individual = loginBySocialNetId(user);
        if (individual == null) {
            throw new SecurityException("User " + user.getFirstName() + " " + user.getLastName() + "(social net id: " + user.getSocialNetId() + ") has no account on this platform! Please contact the platform admin.");
            //individual = new Individual();
            //individual.setId(123L);

        }
        user.setId(individual.getId());

        System.out.println("userFactory#loginUser: " + this);
        System.out.println("user: " + user);
        currentUser = user;
    }

    private Individual loginBySocialNetId(User user) {
        String socialNetId = user.getSocialNetId();
        List<Individual> result = em.createNamedQuery(Individual.findByLinkedInId, Individual.class)
                .setParameter(Individual.queryParam_socialNetId, socialNetId)
                .getResultList();
        if (result.size() != 1) {
            return null;
        }
        Individual individual = result.get(0);
        individual.setLastLogin(new Date());
        individual.setEmailAddress(user.getEmailAddress());

        return individual;
    }
}
