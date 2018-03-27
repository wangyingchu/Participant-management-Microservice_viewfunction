package com.viewfunction.participantManagement.operation.restful;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "ParticipantDetailInfoVOsList")
public class ParticipantDetailInfoVOsList {
	private List<ParticipantDetailInfoVO> participantDetailInfoVOsList;

	public List<ParticipantDetailInfoVO> getParticipantDetailInfoVOsList() {
		return participantDetailInfoVOsList;
	}

	public void setParticipantDetailInfoVOsList(
			List<ParticipantDetailInfoVO> participantDetailInfoVOsList) {
		this.participantDetailInfoVOsList = participantDetailInfoVOsList;
	}

}