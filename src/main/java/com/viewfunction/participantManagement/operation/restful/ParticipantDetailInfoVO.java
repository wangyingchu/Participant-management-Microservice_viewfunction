package com.viewfunction.participantManagement.operation.restful;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "ParticipantBasicInfoVO")
public class ParticipantDetailInfoVO implements java.io.Serializable{	
	private static final long serialVersionUID = 6188789992844215975L;
	private String roleType;
	private String description;
	private String userId;
	private String displayName;
	private String emailAddress;
	private String mobilePhone;
	private String fixedPhone;
	private String address;
	private String postalCode;
	private String title;	
	private boolean hasFacePhoto;
	private String participantScope;
	private boolean activeUser;
	private List<String> allowedFeatureCategories;
	public String getRoleType() {
		return roleType;
	}
	public void setRoleType(String roleType) {
		this.roleType = roleType;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public String getEmailAddress() {
		return emailAddress;
	}
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}
	public String getMobilePhone() {
		return mobilePhone;
	}
	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}
	public String getFixedPhone() {
		return fixedPhone;
	}
	public void setFixedPhone(String fixedPhone) {
		this.fixedPhone = fixedPhone;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getPostalCode() {
		return postalCode;
	}
	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}	
	public boolean getHasFacePhoto() {
		return hasFacePhoto;
	}
	public void setHasFacePhoto(boolean hasFacePhoto) {
		this.hasFacePhoto = hasFacePhoto;
	}
	public String getParticipantScope() {
		return participantScope;
	}
	public void setParticipantScope(String participantScope) {
		this.participantScope = participantScope;
	}
	public boolean isActiveUser() {
		return activeUser;
	}
	public void setActiveUser(boolean activeUser) {
		this.activeUser = activeUser;
	}
	public List<String> getAllowedFeatureCategories() {
		return allowedFeatureCategories;
	}
	public void setAllowedFeatureCategories(List<String> allowedFeatureCategories) {
		this.allowedFeatureCategories = allowedFeatureCategories;
	}
}
