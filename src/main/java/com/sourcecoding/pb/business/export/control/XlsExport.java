/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sourcecoding.pb.business.export.control;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import jxl.Cell;
import jxl.CellType;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableCell;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

/**
 *
 * @author Matthias
 */
public class XlsExport {

    private static final String ROOT_KEY = "_root_";
    private final Map<String, Object> scopedCollections = new HashMap<>();

    public void run(Object json, Workbook template, OutputStream out) throws IOException, WriteException, BiffException {
        WorkbookSettings settings = new WorkbookSettings();
        settings.setWriteAccess("rw");
        settings.setLocale(new Locale("de", "DE"));

        //TODO write blog - without setting writeAccess on a linux machine an exception is thrown:
        /*
         java.lang.ArrayIndexOutOfBoundsException
         at java.lang.System.arraycopy(Native Method)
         at jxl.biff.StringHelper.getBytes(StringHelper.java:127)
         at jxl.write.biff.WriteAccessRecord.<init>(WriteAccessRecord.java:59)
         at jxl.write.biff.WritableWorkbookImpl.write(WritableWorkbookImpl.java:726)
         at com.sourcecoding.pb.business.export.control.XlsExport.run(XlsExport.java:40)
 
         */
        WritableWorkbook workbook = Workbook.createWorkbook(out, template, settings);
        WritableSheet sheet = workbook.getSheet(0);

        int replaceRowStart = 0;
        int replaceRowEnd = sheet.getRows();

        scopedCollections.put(ROOT_KEY, json);

        replaceRows(replaceRowStart, replaceRowEnd, sheet);

        workbook.write();
        workbook.close();
    }

    private int replaceRows(int replaceRowStart, int replaceRowEnd, WritableSheet sheet) throws WriteException {
        int additionalLines = 0;

        System.out.println("-->replaceRows: replaceRowStart: " + replaceRowStart + "; replaceRowEnd: " + replaceRowEnd);

        for (int currentRowNumber = replaceRowStart; currentRowNumber < replaceRowEnd; currentRowNumber++) {

            System.out.println("work on row " + currentRowNumber + "  -> " + convertToString(sheet.getRow(currentRowNumber)));

            for (Cell cell : sheet.getRow(currentRowNumber)) {
                if (cell.getContents().startsWith("{{")  && cell.getContents().endsWith("}}")) {
                    String formula = cell.getContents();

                    if (formula.startsWith("{{repeat:")) {
                        //repeat
                        String expression = formula.substring("{{repeat: ".length());
                        expression = expression.replace("}}", "").trim();
                        
                        String pathName = expression.substring(expression.indexOf("in") + 3).trim();
                        String scopeName = expression.substring(0, expression.indexOf("in")).trim();
                        

                        System.out.println("  -->loop (variable: " + scopeName + ")! " + formula);

                        //find end of repeat!
                        int loopMetaInfoStartLine = currentRowNumber;
                        List<Cell[]> loopContent = new ArrayList<>();

                        boolean endTagAvailable = false;
                        for (int loopRowNumber = currentRowNumber + 1; loopRowNumber < replaceRowEnd; loopRowNumber++) {
                            for (Cell loopCell : sheet.getRow(loopRowNumber)) {
                                if (loopCell.getContents().startsWith("{{end-of-repeat: " + scopeName)) {
                                    endTagAvailable = true;
                                    break;
                                }
                            }
                            if (endTagAvailable)
                                break;
                            loopContent.add(sheet.getRow(loopRowNumber));
                        }

                        if (!endTagAvailable)
                            throw new RuntimeException("no end-of-repeat found ({{end-of-repeat: " + scopeName + "}})");

                        Object listValues = DataExtractor.getDataValue(scopedCollections.get(ROOT_KEY), pathName);
                        if (listValues == null)
                            listValues = DataExtractor.getDataValue(scopedCollections, pathName);

                        if (listValues == null)
                            throw new RuntimeException("no data for path '" + pathName + "' found");

                        Iterable c = null;
                        if (listValues instanceof Collection) {
                            c = (Iterable) ((Collection) listValues);
                        } else if (listValues instanceof Map) {
                            c = (Iterable) ((Map) listValues).entrySet();
                        } else {
                            throw new RuntimeException("repeat is only possible for Collections and Maps! (" + scopeName + ")");
                        }

                        int loopBlockLines = loopContent.size();

                        currentRowNumber = currentRowNumber + loopBlockLines + 2; //two lines meta information 'repeat'

                        for (Object loopValue : c) {
                            //copy row block
                            System.out.println("  copy block (" + loopContent.size() + " lines; at row " + currentRowNumber + ")");
                            copyRows(sheet, loopContent, currentRowNumber);
                            additionalLines += loopBlockLines;

                            System.out.println("put into scopedCollection: " + scopeName + " : " + loopValue);

                            System.out.println( "loopValue type: "+ loopValue.getClass());
                            if (loopValue instanceof Map.Entry) {
                                scopeName = scopeName.replace("(", "");
                                scopeName = scopeName.replace(")", "");
                                System.out.println("scopeName: " + scopeName);
                                String scopeKeyName = scopeName.split(",")[0].trim();
                                String scopeValueName = scopeName.split(",")[1].trim();
                                System.out.println("scopedMap key: " + scopeKeyName + "/" + scopeValueName);
                                Map.Entry<String, Object> entry = (Map.Entry<String, Object>)loopValue;
                                scopedCollections.put(scopeKeyName, entry.getKey());
                                scopedCollections.put(scopeValueName, entry.getValue());
                            } else {
                                scopedCollections.put(scopeName, loopValue);
                            }

                            int newLines = replaceRows(currentRowNumber, (currentRowNumber + loopBlockLines), sheet);
                            additionalLines += newLines;

                            System.out.println("newLines: " + newLines);

                            currentRowNumber += loopBlockLines + newLines;
                            replaceRowEnd += loopBlockLines + newLines;

                        }

                        for (int i = 0; i < (loopBlockLines + 2); i++) {
                            sheet.removeRow(loopMetaInfoStartLine);
                            additionalLines--;
                            currentRowNumber--;
                        }
                        currentRowNumber--;

                    } else {
                        //replace

                        System.out.println("  field found: " + formula);
                        String fieldPath = formula.replace("{{", "");
                        fieldPath = fieldPath.replace("}}", "");

                        String value = DataExtractor.getStringValue(scopedCollections.get(ROOT_KEY), fieldPath);
                        if (value == null) {
                            System.out.println("lookup for " + fieldPath + " in " + scopedCollections );
                            value = DataExtractor.getStringValue(scopedCollections, fieldPath);
                        }
                        if (value == null) {
                            //skip
                            System.out.println("  no value found for field {{" + fieldPath + "}}");
                            continue;
                        }
                        System.out.println("  field {{" + fieldPath + "}} is replaced with value: " + value);

                        WritableCell modifyCell = sheet.getWritableCell(cell.getColumn(), cell.getRow());

                        if (modifyCell.getType() == CellType.LABEL) {
                            Label l = (Label) cell;
                            l.setString(value);
                        }
                    }
                }
            }
        }
        return additionalLines;
    }

    private String convertToString(Cell[] cells) {
        StringBuilder sb = new StringBuilder();

        for (Cell cell : cells) {
            sb.append("Cell(").append(cell.getRow()).append("|").append(cell.getColumn()).append("):").append(cell.getContents()).append("; ");
        }

        return sb.toString();
    }

    private void copyRows(WritableSheet sheet, List<Cell[]> rows, int insertRowNumber) throws WriteException {
        for (Cell[] cells : rows) {
            sheet.insertRow(insertRowNumber);

            int cellcounter = 0;
            for (Cell readCell : cells) {
                WritableCell cellSource = sheet.getWritableCell(readCell.getColumn(), readCell.getRow());
                WritableCell newCell = cellSource.copyTo(cellcounter, insertRowNumber);
                sheet.addCell(newCell);
                cellcounter++;
            }
            insertRowNumber++;
        }
    }
}
