/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sourcecoding.pb.business.export.control;

import com.sourcecoding.pb.business.travelcosts.entity.TravelCostsDTO;
import com.sourcecoding.pb.business.travelcosts.entity.TravelCostsGroupDTO;
import com.sourcecoding.pb.business.travelcosts.entity.TravelExpensesRate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import junit.framework.Assert;
import org.junit.Test;

/**
 *
 * @author Matthias
 */
public class DataExtractorTest {

    @Test
    public void shouldExtractJson() {
        JsonReader reader = Json.createReader(this.getClass().getResourceAsStream("/data-extractor-test.json"));
        JsonObject json = (JsonObject) reader.read();

        String expected = "2013";
        Object actual = DataExtractor.getStringValue(json, "vacation.vacationYear");
        System.out.println(actual.getClass());
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void shouldExtractJsonArray() {
        JsonReader reader = Json.createReader(this.getClass().getResourceAsStream("/data-extractor-test.json"));
        JsonObject json = (JsonObject) reader.read();

        String expected = "403";
        Object actual = DataExtractor.getStringValue(json, "vacation.vacationRecords[0].id");
        System.out.println(actual.getClass());
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void shouldExtractMapStructure() {
        TravelCostsGroupDTO pdg = new TravelCostsGroupDTO();
        pdg.indiviudalId = 2L;
        pdg.sum = 444D;
        pdg.travelCostsList = new ArrayList<>();
        TravelCostsDTO pd = new TravelCostsDTO();
        pd.travelExpenseRateId=123L;
        pd.travelCostsDate=new Date();
        pdg.travelCostsList.add(pd);
        
        Map<Object, Object> travelExpensesRateMap = new HashMap<>();
        TravelExpensesRate ter = new TravelExpensesRate();
        ter.setId(123L);
        ter.setCountry("Deutschland");
        travelExpensesRateMap.put(ter.getId(), ter);

        Map<String, Object> payloadMap = new HashMap<>();
        payloadMap.put("perDiemGroup", pdg);
        payloadMap.put("travelExpensesRates", travelExpensesRateMap);
        
        Map<String,Object> data = new HashMap<>();
        data.put("travelExpenseRateId", 123L);
        payloadMap.put("pd", data);
        
        
        //{{travelExpensesRates[pd.travelExpenseRateId]}}
        
        String expected = "Deutschland";
        Object actual = DataExtractor.getStringValue(payloadMap, "travelExpensesRates[pd.travelExpenseRateId].country");        
        Assert.assertEquals(expected, actual);
        
    }

    @Test
    public void shouldExtractPoJo() {
        TestObj obj = new TestObj();

        String expected = obj.getValue1();
        String actual = DataExtractor.getStringValue(obj, "value1");

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void shouldExtractPoJoDependency() {
        TestObj obj = new TestObj();

        String expected = obj.getObj2().getValue2();
        String actual = DataExtractor.getStringValue(obj, "obj2.value2");

        Assert.assertEquals(expected, actual);

    }

    private class TestObj {

        public String getValue1() {
            return "value1";
        }

        public TestObj2 getObj2() {
            return new TestObj2();
        }
    }

    private class TestObj2 {

        public String getValue2() {
            return "value2";
        }
    }
}
