/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sourcecoding.pb.business.export.boundary;

import com.sourcecoding.pb.business.export.control.XlsExport;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.WriteException;

/**
 *
 * @author Matthias
 */
public class XlsExportService {

    public void generate(String templateURL, Object payload, OutputStream out) {
        try {
            //fetch template
            System.out.println(templateURL);
            URL url = new URL(templateURL);
            
            XlsExport xls = new XlsExport();
            Workbook template = Workbook.getWorkbook(url.openStream());
            xls.run(payload, template, out);

        } catch (IOException | WriteException | BiffException ex) {
            throw new RuntimeException(ex);
        }
    }
}
