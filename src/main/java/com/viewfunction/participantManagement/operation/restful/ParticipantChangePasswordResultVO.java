package com.viewfunction.participantManagement.operation.restful;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "ParticipantChangePasswordResultVO")
public class ParticipantChangePasswordResultVO {
	private boolean changePasswordResult;
	private String returnMessage;
	public boolean isChangePasswordResult() {
		return changePasswordResult;
	}
	public void setChangePasswordResult(boolean changePasswordResult) {
		this.changePasswordResult = changePasswordResult;
	}
	public String getReturnMessage() {
		return returnMessage;
	}
	public void setReturnMessage(String returnMessage) {
		this.returnMessage = returnMessage;
	}
}