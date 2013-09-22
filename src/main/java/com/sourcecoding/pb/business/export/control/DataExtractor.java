/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sourcecoding.pb.business.export.control;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Matthias
 */
public class DataExtractor {
    
    public static String getStringValue(Object dataStructure, String path) {
        
        Object result = getDataValue(dataStructure, path);
        
        if (result == null) {
            return null;
        }
        
        //remove leading and ending \" (JSON)
        String resultText = result.toString();
        if (resultText.startsWith("\"")) {
            resultText = resultText.substring(1);
        }
        if (resultText.endsWith("\"")) {
            resultText = resultText.substring(0, resultText.length() - 1);
        }
        
        System.out.println(result.getClass() + " - " + resultText);
        return resultText;
    }
    
    protected static Object getDataValue(Object dataStructure, String path) {

        //if (jsonStructure.getValueType() != JsonValue.ValueType.OBJECT) {
        //    //no path information available
        //    return null;
        //}

        if (path.contains(".")) {
            String key = path.substring(0, path.indexOf("."));
            String newPath = path.substring(path.indexOf(".") + 1);
            
            if (containsKey(dataStructure, key)) {
                Object newDataStructure = getValue(dataStructure, key);
                return getValue(newDataStructure, newPath);
            }
        }
        
        return getValue(dataStructure, path);
        
    }
    
    private static boolean containsKey(Object obj, String keyPath) {
        if (obj instanceof Map) {
            //get Method for Json and Map            }
            return ((Map) obj).containsKey(keyPath);
        } else {
            try {
                String methodName = "get" + keyPath.substring(0, 1).toUpperCase() + keyPath.substring(1);
                Method m = obj.getClass().getMethod(methodName, new Class[]{});
                return (m != null);
            } catch (NoSuchMethodException ex) {
                Logger.getLogger(DataExtractor.class
                        .getName()).log(Level.SEVERE, null, ex);
                return false;
            }
        }
    }
    
    private static Object getValue(Object obj, String keyName) {
        
        if (obj instanceof Map) {
            //get Method for Json and Map            }
            return ((Map) obj).get(keyName);
        } else {
            try {
                String methodName = "get" + keyName.substring(0, 1).toUpperCase() + keyName.substring(1);
                Method m = obj.getClass().getMethod(methodName, new Class[]{});
                return m.invoke(obj);
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                Logger.getLogger(DataExtractor.class
                        .getName()).log(Level.SEVERE, null, ex);
                return null;
            }
        }
    }
}
