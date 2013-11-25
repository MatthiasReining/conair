/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sourcecoding.pb.business.project.entity;

import com.sourcecoding.pb.business.individuals.entity.Individual;
import com.sourcecoding.pb.business.restconfig.JsonDateAdapter;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 *
 * @author Matthias
 */
@Entity
@NamedQueries({
    @NamedQuery(name = ProjectInformation.findAllValidProjects, query = "SELECT pi FROM ProjectInformation pi"),
    @NamedQuery(name = ProjectInformation.findByKey, query = "SELECT pi FROM ProjectInformation pi WHERE pi.projectKey= :" + ProjectInformation.queryParam_projectKey),})
public class ProjectInformation implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final String findAllValidProjects = "ProjectInformation#findAllValidProjects";
    public static final String findByKey = "ProjectInformation#findByKey";
    public static final String queryParam_projectKey = "key";
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String projectKey;
    private String name;

    @Temporal(TemporalType.DATE)
    @XmlJavaTypeAdapter(JsonDateAdapter.class)
    private Date projectStart;

    @Temporal(TemporalType.DATE)
    @XmlJavaTypeAdapter(JsonDateAdapter.class)
    private Date projectEnd;
    private Integer limitForWorkingHours;

    @OneToMany(mappedBy = "projectInformation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectMember> members;

    /**
     * TODO link to user
     */
    private Individual projectManager;
    @OneToMany(mappedBy = "projectInformation", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<WorkPackage> workPackages;

    public ProjectMember getProjectMemberByIndividual(Individual individual) {
        for (ProjectMember pm : members) {
            if (pm.getIndividual().getId() == individual.getId())
                return pm;
        }
        return null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProjectKey() {
        return projectKey;
    }

    public void setProjectKey(String projectKey) {
        this.projectKey = projectKey;
    }

    public Set<WorkPackage> getWorkPackages() {
        return workPackages;
    }

    public void setWorkPackages(Set<WorkPackage> workPackages) {
        this.workPackages = workPackages;
    }

    public Date getProjectStart() {
        return projectStart;
    }

    public void setProjectStart(Date projectStart) {
        this.projectStart = projectStart;
    }

    public Date getProjectEnd() {
        return projectEnd;
    }

    public void setProjectEnd(Date projectEnd) {
        this.projectEnd = projectEnd;
    }

    public Individual getProjectManager() {
        return projectManager;
    }

    public void setProjectManager(Individual projectManager) {
        this.projectManager = projectManager;
    }

    public Integer getLimitForWorkingHours() {
        return limitForWorkingHours;
    }

    public void setLimitForWorkingHours(Integer limitForWorkingHours) {
        this.limitForWorkingHours = limitForWorkingHours;
    }

    public List<ProjectMember> getMembers() {
        return members;
    }

    public void setMembers(List<ProjectMember> members) {
        this.members = members;
    }

}
