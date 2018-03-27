package com.viewfunction.participantManagement.operation.restful;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "UpdateFacePhotoResultVO")
public class UpdateFacePhotoResultVO {
	private boolean updateFacePhotoResult;
	private String userId;
	public boolean isUpdateFacePhotoResult() {
		return updateFacePhotoResult;
	}
	public void setUpdateFacePhotoResult(boolean updateFacePhotoResult) {
		this.updateFacePhotoResult = updateFacePhotoResult;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
}