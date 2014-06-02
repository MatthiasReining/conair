/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sourcecoding.pb.business.export.control;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Locale;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import org.junit.Test;
import jxl.*;
import jxl.read.biff.BiffException;
import jxl.read.biff.File;
import jxl.write.WriteException;
import org.junit.Ignore;

/**
 *
 * @author Matthias
 */
@Ignore
public class XlsExportTest {

    @Test
    public void run() throws WriteException, BiffException, IOException {
        JsonReader reader = Json.createReader(this.getClass().getResourceAsStream("/vacation.json"));
        JsonObject json = (JsonObject) reader.read();

        java.io.File f = new java.io.File("d:\\out.xls");
        OutputStream out = new FileOutputStream(f);
        //OutputStream out = new ByteArrayOutputStream();
        WorkbookSettings settings = new WorkbookSettings();
        settings.setEncoding("Cp1252");
        Workbook template = Workbook.getWorkbook(this.getClass().getResourceAsStream("/vacation-template-test.xls"), settings);

        XlsExport xlsExport = new XlsExport();
        xlsExport.run(json, template, out);

        out.flush();
        out.close();

    }
}
