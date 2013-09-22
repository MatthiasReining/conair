/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sourcecoding.pb.business.export.control;

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

        JsonReader reader = Json.createReader(this.getClass().getResourceAsStream("/vacation.json"));
        JsonObject json = (JsonObject) reader.read();

        String expected = "2013";
        Object actual = DataExtractor.getStringValue(json, "vacation.vacationYear");
        System.out.println(actual.getClass());
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
