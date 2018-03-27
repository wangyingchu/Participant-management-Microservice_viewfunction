package com.viewfunction.participantManagement.authentication.restful;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "ParticipantLoginInfoVO")
public class ParticipantLoginInfoVO {
	private String participantID;
	private String participantPWD;
	private String participantScope;
	public String getParticipantID() {
		return participantID;
	}
	public void setParticipantID(String participantID) {
		this.participantID = participantID;
	}
	public String getParticipantPWD() {
		return participantPWD;
	}
	public void setParticipantPWD(String participantPWD) {
		this.participantPWD = participantPWD;
	}
	public String getParticipantScope() {
		return participantScope;
	}
	public void setParticipantScope(String participantScope) {
		this.participantScope = participantScope;
	}
}