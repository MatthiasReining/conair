/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sourcecoding.pb.business.export.boundary;

import com.sourcecoding.pb.business.export.control.XlsExport;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.json.JsonObject;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.WriteException;

/**
 *
 * @author Matthias
 */
public class XlsExportService {

    public void generate(String templateName, Object payload, OutputStream out) {
        try {
            InputStream templateStream = this.getClass().getResourceAsStream("/xls-templates/" + templateName + ".xls");

            XlsExport xls = new XlsExport();
            Workbook template = Workbook.getWorkbook(templateStream);
            xls.run(payload, template, out);
            
        } catch (IOException | WriteException | BiffException ex) {
            throw new RuntimeException(ex);
        }
    }
}
