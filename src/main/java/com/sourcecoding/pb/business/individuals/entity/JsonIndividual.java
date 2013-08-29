/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sourcecoding.pb.business.individuals.entity;

import com.sourcecoding.pb.business.restconfig.JsonDateTimeAdapter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * Used as JSON object
 *
 * @author Matthias
 */
public class JsonIndividual {

    public Long id;
    public String nickname;
    public String emailAddress;
    public String linkedInId;
    @XmlJavaTypeAdapter(JsonDateTimeAdapter.class)
    public Date lastLogin;
    public Integer vacationDaysPerYear;
    public Integer workdaysPerWeek;
    public String vacationManagerNickName;
    public List<String> roles;

    public static JsonIndividual create(Individual individual) {
        JsonIndividual p = new JsonIndividual();
        p.id = individual.getId();
        p.nickname = individual.getNickname();
        p.emailAddress = individual.getEmailAddress();
        p.linkedInId = individual.getLinkedInId();
        p.lastLogin = individual.getLastLogin();
        p.vacationDaysPerYear = individual.getVacationDaysPerYear();
        p.workdaysPerWeek = individual.getWorkdaysPerWeek();
        p.vacationManagerNickName = individual.getVacationManager().getNickname();
        p.roles = new ArrayList<>();
        for (IndividualRole ir : individual.getRoles())
            p.roles.add(ir.getRoleName());
        
        return p;
    }
}
