package com.viewfunction.participantManagement.authentication.restful;

public class AuthorizationRecordVO  implements java.io.Serializable{
	
	private static final long serialVersionUID = 4327080345754832346L;
	
	private String loginId;
	private String loginIP;
	private long loginTimeStamp;
	private String participantScope;
	public String getLoginId() {
		return loginId;
	}
	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}
	public String getLoginIP() {
		return loginIP;
	}
	public void setLoginIP(String loginIP) {
		this.loginIP = loginIP;
	}
	public long getLoginTimeStamp() {
		return loginTimeStamp;
	}
	public void setLoginTimeStamp(long loginTimeStamp) {
		this.loginTimeStamp = loginTimeStamp;
	}
	public String getParticipantScope() {
		return participantScope;
	}
	public void setParticipantScope(String participantScope) {
		this.participantScope = participantScope;
	}
}
