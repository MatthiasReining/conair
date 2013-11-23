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
public class XlsExportLoopMapTest {

    Map<String, Object> accountingMap;

    @Before
    public void init() {

        accountingMap = new HashMap<>();
        accountingMap.put("accountingDate", "2013-11-23");

        Map<String, Object> peopleByRole = new LinkedHashMap<>();
        accountingMap.put("peopleByRole", peopleByRole);

        List<Map<String, Object>> personList = new ArrayList<>();

        peopleByRole.put("Senior Consultant", personList);

        Map<String, Object> person = new HashMap<>();
        personList.add(person);
        person.put("lastname", "Reichert");
        person.put("firstname", "Jürgen");
        person.put("totalHours", "152");
        person.put("totalDays", "19");
        person.put("pricePerHour", "118,75");
        person.put("totalPrice", "18050,00");

        person = new HashMap<>();
        personList.add(person);
        person.put("lastname", "Mustermann");
        person.put("firstname", "Max");
        person.put("totalHours", "120");
        person.put("totalDays", "15");
        person.put("pricePerHour", "118,75");
        person.put("totalPrice", "14000,00");

    }

    @Test
    public void run() throws WriteException, BiffException, IOException {

        OutputStream out = new FileOutputStream(new File("d:\\out.xls"));
        //OutputStream out = new ByteArrayOutputStream();
        Workbook template = Workbook.getWorkbook(this.getClass().getResourceAsStream("/xls-templates/accounting-test.xls"));

        XlsExport xlsExport = new XlsExport();
        xlsExport.run(accountingMap, template, out);

        out.flush();
        out.close();

    }
}
