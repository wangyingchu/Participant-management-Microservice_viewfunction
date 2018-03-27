package com.viewfunction.participantManagement.authentication.restful;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "ParticipantLogoutInfoVO")
public class ParticipantLogoutInfoVO {
	private String participantID;
	private String participantScope;
	
	public String getParticipantID() {
		return participantID;
	}

	public void setParticipantID(String participantID) {
		this.participantID = participantID;
	}

	public String getParticipantScope() {
		return participantScope;
	}

	public void setParticipantScope(String participantScope) {
		this.participantScope = participantScope;
	}
}
