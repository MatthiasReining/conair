/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sourcecoding.pb.business.export.control;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import org.junit.Test;
import jxl.*;
import jxl.read.biff.BiffException;
import jxl.write.WriteException;
import org.codehaus.jackson.map.ObjectMapper;

/**
 *
 * @author Matthias
 */
public class XlsExportTest {

    @Test
    public void run() throws WriteException, BiffException, IOException {
        JsonReader reader = Json.createReader(this.getClass().getResourceAsStream("/vacation.json"));
        JsonObject json = (JsonObject) reader.read();
        
        String s = json.toString();
        System.out.println( s );
        JsonObject jo = (JsonObject) Json.createReader(new StringReader(s)).read();
        System.out.println( jo );
        
        Map<String, Object> test = new HashMap<>();
        test.put("xxx", "yyy");
        test.put("123", "qwer");
        System.out.println( test.toString() );
        
        ObjectMapper om = new ObjectMapper();
        String x = om.writeValueAsString(test);
        System.out.println( x );
        if (true) return;
        
        

        File f = new File("d:\\out.xls");
        OutputStream out = new FileOutputStream(f);
        Workbook template = Workbook.getWorkbook(this.getClass().getResourceAsStream("/vacation-template-test.xls"));

        XlsExport xlsExport = new XlsExport();
        xlsExport.run(json, template, out);

        out.flush();
        out.close();

    }
}
