/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sourcecoding.pb.business.meta.boundary;

import com.sourcecoding.pb.business.meta.entity.VersionInformation;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

/**
 * provides VersionInformation about the application
 *
 * @author cot
 *
 */
@Path("/version")
@ApplicationScoped
public class VersionService {

    private VersionInformation vi;
    @Context
    ServletContext servletContext;
    
    @PostConstruct
    void createVersionInformation() {
        try {
            vi = new VersionInformation();
            vi.setBuildId("Build Information are not available!");
            vi.setSpecificationTitle("no title information available");
            vi.setSpecificationVersion("no version information available");

            InputStream manifestStream = getManifestStream();

            Manifest mf = new Manifest(manifestStream);
            Attributes attr = mf.getMainAttributes();

            vi.setManifestVersion(attr.getValue("Manifest-Version"));
            vi.setBuiltBy(attr.getValue("Built-By"));
            vi.setBuildJdk(attr.getValue("Build-Jdk"));
            vi.setCreatedBy(attr.getValue("Created-By"));
            vi.setImplementationVersion(attr.getValue("Implementation-Version"));
            vi.setSpecificationVersion(attr.getValue("Specification-Version"));
            vi.setSpecificationTitle(attr.getValue("Specification-Title"));
            vi.setBuildId(attr.getValue("buildId"));
            vi.setBuildNumber(attr.getValue("buildNumber"));
            vi.setBuildTag(attr.getValue("buildTag"));
            vi.setBuildUrl(attr.getValue("buildUrl"));
            vi.setExecutorNumber(attr.getValue("executorNumber"));
            vi.setJenkinsUrl(attr.getValue("jenkinsUrl"));
            vi.setJobName(attr.getValue("jobName"));
            vi.setGitRevision(attr.getValue("gitRevision"));
            vi.setGitRevision(attr.getValue("gitBranch"));
        } catch (IOException ex) {
            Logger.getLogger(VersionService.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    protected InputStream getManifestStream() {
        InputStream manifestStream;
        if (FacesContext.getCurrentInstance() != null) // JSF Call
        {
            manifestStream = FacesContext.getCurrentInstance().getExternalContext()
                    .getResourceAsStream("/META-INF/MANIFEST.MF");
        } else {
            manifestStream = servletContext.getResourceAsStream("/META-INF/MANIFEST.MF");
        }
        return manifestStream;
    }

    /**
     * returns the versionInformation
     *
     * @return the versionInformation
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public VersionInformation getVersionInformation() {
        return vi;
    }
}