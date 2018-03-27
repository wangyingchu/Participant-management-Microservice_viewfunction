package com.viewfunction.participantManagement.authentication.restful;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "ParticipantLoginResultVO")
public class ParticipantLoginResultVO {
	private String participantID;
	private String participantDisplayName;
	private String participantScope;
	private boolean loginAuthenticateResult;
	public String getParticipantID() {
		return participantID;
	}
	public void setParticipantID(String participantID) {
		this.participantID = participantID;
	}
	public String getParticipantDisplayName() {
		return participantDisplayName;
	}
	public void setParticipantDisplayName(String participantDisplayName) {
		this.participantDisplayName = participantDisplayName;
	}
	public boolean isLoginAuthenticateResult() {
		return loginAuthenticateResult;
	}
	public void setLoginAuthenticateResult(boolean loginAuthenticateResult) {
		this.loginAuthenticateResult = loginAuthenticateResult;
	}
	public String getParticipantScope() {
		return participantScope;
	}
	public void setParticipantScope(String participantScope) {
		this.participantScope = participantScope;
	}
}