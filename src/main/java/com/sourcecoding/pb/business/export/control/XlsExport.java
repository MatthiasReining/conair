/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sourcecoding.pb.business.export.control;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;
import jxl.Cell;
import jxl.CellType;
import jxl.Workbook;
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

    private Map<String, JsonArray> scopedCollections = new HashMap<>();

    protected JsonValue getJsonValue(JsonValue jsonStructure, String path) {

        if (jsonStructure.getValueType() != JsonValue.ValueType.OBJECT) {
            //no path information available
            return null;
        }

        JsonObject jsonObj = (JsonObject) jsonStructure;
        if (path.contains(".")) {
            String key = path.substring(0, path.indexOf("."));
            String newPath = path.substring(path.indexOf(".") + 1);

            if (jsonObj.containsKey(key)) {
                JsonValue newJsonStructure = jsonObj.get(key);
                return getJsonValue(newJsonStructure, newPath);
            }
        }
        if (jsonObj.containsKey(path)) {
            return jsonObj.get(path);
        }

        return null;
    }

    public void run(JsonObject json, Workbook template, OutputStream out) throws IOException, WriteException, BiffException {
        WritableWorkbook workbook = Workbook.createWorkbook(out, template);
        WritableSheet sheet = workbook.getSheet(0);

        int replaceRowStart = 0;
        int replaceRowEnd = sheet.getRows();
        replaceRows(replaceRowStart, replaceRowEnd, sheet, json);

        workbook.write();
        workbook.close();
    }

    private void replaceRows(int replaceRowStart, int replaceRowEnd, WritableSheet sheet, JsonObject json) throws WriteException {
        boolean loopMode = false;
        String currentLoopScope = null;

        List<Cell[]> loopRows = null;

        for (int currentRowNumber = replaceRowStart; currentRowNumber < replaceRowEnd; currentRowNumber++) {

            System.out.println("Row: " + convertToString(sheet.getRow(currentRowNumber)));

            if (loopMode) {
                //copy rows
                loopRows.add(sheet.getRow(currentRowNumber));
            }
            for (Cell cell : sheet.getRow(currentRowNumber)) {
                if (cell.getContents().startsWith("{{")) {
                    String formula = cell.getContents();


                    if (formula.startsWith("{{repeat:")) {
                        //repeat
                        String expression = formula.substring("{{{repeat:".length());
                        expression = expression.replace("}}", "").trim();
                        String param[] = expression.split(" ");
                        String scopeName = param[0];
                        String pathName = param[2];
                        JsonArray jsonArray = (JsonArray) getJsonValue(json, pathName);
                        scopedCollections.put(scopeName, jsonArray);
                        currentLoopScope = scopeName;
                        //sheet.removeRow(rowNumber);
                        loopMode = true;
                        loopRows = new ArrayList<>();
                        sheet.removeRow(currentRowNumber);
                        currentRowNumber--;
                    } else if (formula.startsWith("{{end-of-repeat")) {
                        loopMode = false;
                        loopRows.remove(loopRows.size() - 1);
                        int numberOfRows2Remove = loopRows.size();
                        int definitionLoopRowNumber = currentRowNumber - numberOfRows2Remove;


                        //print Data
                        JsonArray entries = scopedCollections.get(currentLoopScope);

                        System.out.println("entries: " + entries);
                        for (int i = 0; i < entries.size(); i++) {
                            JsonObject entry = entries.getJsonObject(i);

                            //copy row block
                            copyRows(sheet, loopRows, currentRowNumber);

                            //replace data in block
                            JsonObject scopeObj = Json.createObjectBuilder()
                                    .add(currentLoopScope, entry)
                                    .build();
                            replaceRows(currentRowNumber, currentRowNumber + loopRows.size(), sheet, scopeObj);

                            currentRowNumber = currentRowNumber + loopRows.size();
                        }

                        sheet.removeRow(currentRowNumber); //line {{end-of-repeat
                        //remove existing rows
                        System.out.println("remove " + numberOfRows2Remove + " lines from " + definitionLoopRowNumber);
                        for (int i = 0; i < numberOfRows2Remove; i++) {
                            sheet.removeRow(definitionLoopRowNumber);
                        }

                    } else {
                        //replace
                        if (loopMode == false) {
                            String fieldPath = formula.replace("{{", "");
                            fieldPath = fieldPath.replace("}}", "");

                            System.out.println("excel value detected: " + fieldPath);
                            String value = getJsonValue(json, fieldPath).toString();
                            System.out.println(value);
                            WritableCell modifyCell = sheet.getWritableCell(cell.getColumn(), cell.getRow());

                            if (modifyCell.getType() == CellType.LABEL) {
                                Label l = (Label) cell;
                                l.setString(value);
                            }
                        }
                    }
                }
            }
        }
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
                System.out.println("cellSource: " + cellSource.getRow() + "/" + cellSource.getColumn() + " = " + cellSource.getContents());
                WritableCell newCell = cellSource.copyTo(cellcounter, insertRowNumber);
                System.out.println("newCell: " + newCell.getRow() + "/" + newCell.getColumn() + " = " + newCell.getContents());
                sheet.addCell(newCell);
                cellcounter++;
            }
            insertRowNumber++;
        }
    }
}
