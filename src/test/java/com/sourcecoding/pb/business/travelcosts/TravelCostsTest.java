/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sourcecoding.pb.business.travelcosts;

import com.sourcecoding.pb.business.configuration.boundary.Configurator;
import com.sourcecoding.pb.business.export.boundary.XlsExportService;
import com.sourcecoding.pb.business.individuals.entity.Individual;
import com.sourcecoding.pb.business.project.entity.ProjectInformation;
import com.sourcecoding.pb.business.travelcosts.entity.TravelCostsDTO;
import com.sourcecoding.pb.business.travelcosts.entity.TravelCostsGroupDTO;
import com.sourcecoding.pb.business.travelcosts.entity.TravelExpensesRate;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Test;

/**
 *
 * @author Matthias
 */
public class TravelCostsTest {

    @Test
    public void test() {
        String templateUrl = "file://" + this.getClass().getResource("/xls-templates/real-world/travel-costs.xls").getFile();

        System.out.println(templateUrl);
        TravelCostsGroupDTO tcg = null;
        tcg = new TravelCostsGroupDTO();
        tcg.indiviudalId = 2l;
        tcg.sum = 2000d;
        tcg.travelCostsGroupState = null;
        tcg.yearMonth = "2014-04";
        tcg.travelCostsList = new ArrayList<>();

        tcg.travelCostsList.add(new TravelCostsDTO(new Date(2014, 2, 1), "07:00", "22:00", 123l, 222l, true, true, true, true, new BigDecimal("100")));
        tcg.travelCostsList.add(new TravelCostsDTO(new Date(2014, 2, 2), "07:00", "22:00", 123l, 222l, true, true, true, true, new BigDecimal("100")));

        Map<String, Object> payloadMap = new HashMap<>();
        payloadMap.put("travelCostsGroup", tcg);

        Map<Long, Object> travelExpensesRateMap = new HashMap<>();
        TravelExpensesRate ter = new TravelExpensesRate();
        ter.setId(222l);
        ter.setAccommodationExpenses(new BigDecimal("120"));
        ter.setBreakfast(new BigDecimal("6"));
        ter.setCountry("Das Land");
        ter.setLunch(new BigDecimal("6"));
        ter.setRate24h(new BigDecimal("42"));
        ter.setRateFrom8To24(new BigDecimal("22"));
        ter.setTravelYear(2014);
        travelExpensesRateMap.put(222l, ter);

        payloadMap.put("travelExpensesRates", travelExpensesRateMap);

        Map<Long, ProjectInformation> projectMap = new HashMap<>();
        ProjectInformation pi = new ProjectInformation();
        pi.setId(123l);
        pi.setName("Das Land");
        projectMap.put(123l, pi);
        
        payloadMap.put("projects", projectMap);
        Individual individual = new Individual();
        individual.setFirstname("Max");
        individual.setLastname("Mustermann");
        individual.setNickname("Max");
        payloadMap.put("individual", individual);

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        new XlsExportService().generate(templateUrl, payloadMap, os);
        
        /*
        try {
            OutputStream fos = new FileOutputStream(new File("d:/out.xls"));
            os.writeTo(fos);
        } catch (IOException ex) {
            Logger.getLogger(TravelCostsTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        */
        
        
    }
}
