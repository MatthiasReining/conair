/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sourcecoding.pb.business.meta.entity;

import java.io.Serializable;

/**
 * 
 */
public class VersionInformation implements Serializable {

	private static final long serialVersionUID = 1L;

	private String manifestVersion;
	private String builtBy;
	private String buildJdk;
	private String createdBy;
	private String implementationVersion;
	private String specificationTitle;
	private String specificationVersion;
	private String buildId;
	private String buildNumber;
	private String buildTag;
	private String buildUrl;
	private String executorNumber;
	private String jenkinsUrl;
	private String jobName;
	private String gitRevision;
        private String gitBranch;

	public String getManifestVersion() {
		return manifestVersion;
	}

	public void setManifestVersion(String manifestVersion) {
		this.manifestVersion = manifestVersion;
	}

	public String getBuiltBy() {
		return builtBy;
	}

	public void setBuiltBy(String builtBy) {
		this.builtBy = builtBy;
	}

	public String getBuildJdk() {
		return buildJdk;
	}

	public void setBuildJdk(String buildJdk) {
		this.buildJdk = buildJdk;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getImplementationVersion() {
		return implementationVersion;
	}

	public void setImplementationVersion(String implementationVersion) {
		this.implementationVersion = implementationVersion;
	}

	public String getSpecificationTitle() {
		return specificationTitle;
	}

	public void setSpecificationTitle(String specificationTitle) {
		this.specificationTitle = specificationTitle;
	}

	public String getSpecificationVersion() {
		return specificationVersion;
	}

	public void setSpecificationVersion(String specificationVersion) {
		this.specificationVersion = specificationVersion;
	}

	public String getBuildId() {
		return buildId;
	}

	public void setBuildId(String buildId) {
		this.buildId = buildId;
	}

	public String getBuildNumber() {
		return buildNumber;
	}

	public void setBuildNumber(String buildNumber) {
		this.buildNumber = buildNumber;
	}

	public String getBuildTag() {
		return buildTag;
	}

	public void setBuildTag(String buildTag) {
		this.buildTag = buildTag;
	}

	public String getBuildUrl() {
		return buildUrl;
	}

	public void setBuildUrl(String buildUrl) {
		this.buildUrl = buildUrl;
	}

	public String getExecutorNumber() {
		return executorNumber;
	}

	public void setExecutorNumber(String executorNumber) {
		this.executorNumber = executorNumber;
	}

	public String getJenkinsUrl() {
		return jenkinsUrl;
	}

	public void setJenkinsUrl(String jenkinsUrl) {
		this.jenkinsUrl = jenkinsUrl;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

    public String getGitRevision() {
        return gitRevision;
    }

    public void setGitRevision(String gitRevision) {
        this.gitRevision = gitRevision;
    }

    public String getGitBranch() {
        return gitBranch;
    }

    public void setGitBranch(String gitBranch) {
        this.gitBranch = gitBranch;
    }


}