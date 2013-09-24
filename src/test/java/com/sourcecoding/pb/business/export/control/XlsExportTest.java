/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sourcecoding.pb.business.export.control;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import org.junit.Test;
import jxl.*;
import jxl.read.biff.BiffException;
import jxl.write.WriteException;
import org.junit.Ignore;

/**
 *
 * @author Matthias
 */
@Ignore
/**
 * FIXME: wirft bei Cloudbees folgende Fehlermeldung
 * java.lang.ArrayIndexOutOfBoundsException: null
 * [INFO] 	at java.lang.System.arraycopy(Native Method)
 * [INFO] 	at jxl.biff.StringHelper.getBytes(StringHelper.java:127)
 * [INFO] 	at jxl.write.biff.WriteAccessRecord.<init>(WriteAccessRecord.java:59)
 * [INFO] 	at jxl.write.biff.WritableWorkbookImpl.write(WritableWorkbookImpl.java:726)
 * [INFO] 	at com.sourcecoding.pb.business.export.control.XlsExport.run(XlsExport.java:40)
 * [INFO] 	at com.sourcecoding.pb.business.export.control.XlsExportTest.run(XlsExportTest.java:34)
 */
public class XlsExportTest {

    @Test 
    public void run() throws WriteException, BiffException, IOException {
        JsonReader reader = Json.createReader(this.getClass().getResourceAsStream("/vacation.json"));
        JsonObject json = (JsonObject) reader.read();

        //File f = new File("d:\\out.xls");
        OutputStream out = new ByteArrayOutputStream();
        Workbook template = Workbook.getWorkbook(this.getClass().getResourceAsStream("/vacation-template-test.xls"));

        XlsExport xlsExport = new XlsExport();
        xlsExport.run(json, template, out);

        out.flush();
        out.close();

    }
}
