/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sourcecoding.pb.business.export.control;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import org.junit.Test;
import jxl.*;
import jxl.read.biff.BiffException;
import jxl.write.WriteException;
import org.junit.Before;

/**
 *
 * @author Matthias
 */
public class XlsExportLoopListTest {

    Map<String, Object> personMap;

    @Before
    public void init() {

        personMap = new HashMap<>();

        personMap.put("created", "2013-11-23");
        personMap.put("from", "Matthias Reining");

        List<Map<String, Object>> persons = new ArrayList<>();
        personMap.put("persons", persons);
        Map<String, Object> person = new HashMap<>();
        person.put("firstname", "Mickey");
        person.put("lastname", "Mouse");
        List<Map<String, Object>> addresses = new ArrayList<>();
        Map<String, Object> address = new HashMap<>();
        address.put("zip", "12345");
        address.put("city", "Disneyhausen");
        addresses.add(address);
        address = new HashMap<>();
        address.put("zip", "33223");
        address.put("city", "Walt-D-City");
        addresses.add(address);
        person.put("addresses", addresses);
        persons.add(person);
        
        
        person = new HashMap<>();
        person.put("firstname", "Mini");
        person.put("lastname", "Mouse");
        addresses = new ArrayList<>();
        address = new HashMap<>();
        address.put("zip", "44221");
        address.put("city", "Comic-Rulez-City");
        addresses.add(address);
        address = new HashMap<>();
        address.put("zip", "22122");
        address.put("city", "Minihausen");
        addresses.add(address);
        person.put("addresses", addresses);
        persons.add(person);
        
        
        persons.add(person);
        persons.add(person);
    }

    @Test
    public void run() throws WriteException, BiffException, IOException {
        
        //OutputStream out = new FileOutputStream(new File("d:\\out.xls"));
        OutputStream out = new ByteArrayOutputStream();
        Workbook template = Workbook.getWorkbook(this.getClass().getResourceAsStream("/xls-templates/loop-list-test.xls"));
        
        XlsExport xlsExport = new XlsExport();
        xlsExport.run(personMap, template, out);

        out.flush();
        out.close();

    }
}
