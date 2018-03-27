package com.viewfunction.participantManagement.operation.restful;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "ParticipantDetailInfosQueryVO")
public class ParticipantDetailInfosQueryVO {
	private List<String> participantsUserUidList;
	private String participantScope;
	
	public List<String> getParticipantsUserUidList() {
		return participantsUserUidList;
	}

	public void setParticipantsUserUidList(List<String> participantsUserUidList) {
		this.participantsUserUidList = participantsUserUidList;
	}

	public String getParticipantScope() {
		return participantScope;
	}

	public void setParticipantScope(String participantScope) {
		this.participantScope = participantScope;
	}
}