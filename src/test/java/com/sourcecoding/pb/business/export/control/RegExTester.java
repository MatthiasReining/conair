/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sourcecoding.pb.business.export.control;

import java.util.List;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Matthias
 */
public class RegExTester {

    @Test
    public void bracketTester1() {
        XlsExport exporter = new XlsExport();

        String test = "{{p.firstname}} in {{p.lastname}}";

        List<String> result = exporter.getFields(test);
        
        Assert.assertEquals("{{p.firstname}}", result.get(0));
        Assert.assertEquals("{{p.lastname}}", result.get(1));
    }
    
     @Test
    public void bracketTester2() {
        XlsExport exporter = new XlsExport();

        String test = "blub {{p.firstname}} in {{p.lastname}}";

        List<String> result = exporter.getFields(test);
        
        Assert.assertEquals("{{p.firstname}}", result.get(0));
        Assert.assertEquals("{{p.lastname}}", result.get(1));
       
    }

}
