/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sourcecoding.pb.business.workinghours.entity;

import com.sourcecoding.pb.business.project.entity.WorkPackage;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

/**
 *
 * @author Matthias
 */
@Entity
@NamedQueries({
    @NamedQuery(name = WorkPackageDescription.findByWorkPackageAndDescription, query = "SELECT wpd FROM WorkPackageDescription wpd WHERE wpd.workPackage = :" + WorkPackageDescription.findByWorkPackageAndDescription_Param_WorkPackage + " AND wpd.description = :" + WorkPackageDescription.findByWorkPackageAndDescription_Param_Description)
})
public class WorkPackageDescription implements Serializable {

    public static final String findByWorkPackageAndDescription = "WorkPackageDescription#findByWorkPackageAndDescription";
    public static final String findByWorkPackageAndDescription_Param_WorkPackage = "workPackage";
    public static final String findByWorkPackageAndDescription_Param_Description = "description";
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private WorkPackage workPackage;
    private String description;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public WorkPackage getWorkPackage() {
        return workPackage;
    }

    public void setWorkPackage(WorkPackage workPackage) {
        this.workPackage = workPackage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    
}
