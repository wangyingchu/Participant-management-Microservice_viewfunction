package com.viewfunction.participantManagement.authentication.restful;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "ParticipantLoginResultVO")
public class ParticipantLogoutResultVO {
	private String participantID;
	private boolean logoutAuthenticateResult;
	private String participantScope;
	public String getParticipantID() {
		return participantID;
	}
	public void setParticipantID(String participantID) {
		this.participantID = participantID;
	}
	public boolean isLogoutAuthenticateResult() {
		return logoutAuthenticateResult;
	}
	public void setLogoutAuthenticateResult(boolean logoutAuthenticateResult) {
		this.logoutAuthenticateResult = logoutAuthenticateResult;
	}
	public String getParticipantScope() {
		return participantScope;
	}
	public void setParticipantScope(String participantScope) {
		this.participantScope = participantScope;
	}
}