/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sourcecoding.pb.business.vacation.boundary;

import com.sourcecoding.pb.business.restconfig.DateParameter;
import com.sourcecoding.pb.business.vacation.entity.LegalHoliday;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.GET;

/**
 *
 * @author Matthias
 */
@Stateless
public class LegalHolidayResource {

    @PersistenceContext
    EntityManager em;

    /**
     * Get vacation tasks for approval.
     *
     * @return list of tasks
     */
    @GET
    public List<Map<String, Object>> getList() {

        List<LegalHoliday> list = em.createNamedQuery(LegalHoliday.findAll, LegalHoliday.class).getResultList();


        List<Map<String, Object>> result = new ArrayList<>();

        for (LegalHoliday lh : list) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("legalHolidayState", lh.getLegalHolidayState());
            entry.put("legalHolidayDate", DateParameter.valueOf(lh.getLegalHolidayDate())); //TODO check date converter
            entry.put("legalHolidayName", lh.getLegalHolidyName());

            result.add(entry);
        }

        return result;
    }
}
