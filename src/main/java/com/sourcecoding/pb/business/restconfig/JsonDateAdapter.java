/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sourcecoding.pb.business.restconfig;

import java.util.Date;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 *
 * @author Matthias
 */
public class JsonDateAdapter extends XmlAdapter<String, Date> {

    @Override
    public Date unmarshal(String v) throws Exception {
        return DateParameter.valueOf(v);
    }

    @Override
    public String marshal(Date v) throws Exception {
        return DateParameter.valueOf(v);
    }
}
