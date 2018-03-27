package com.viewfunction.participantManagement.operation.restfulClient;

import java.util.ArrayList;
import java.util.List;

import com.viewfunction.participantManagement.operation.restful.ParticipantDetailInfoVO;
import com.viewfunction.participantManagement.operation.restful.ParticipantDetailInfoVOsList;
import com.viewfunction.participantManagement.operation.restful.ParticipantDetailInfosQueryVO;

public class RESTClientTestCase {
	public static void main(String[] args){
		ParticipantDetailInfosQueryVO participantDetailInfosQueryVO=new ParticipantDetailInfosQueryVO();
		List<String> userList=new ArrayList<String>();
		userList.add("wangychu");
		userList.add("dongna");		
		participantDetailInfosQueryVO.setParticipantsUserUidList(userList);
		ParticipantDetailInfoVOsList participantDetailInfoVOsList=ParticipantOperationServiceRESTClient.getUsersDetailInfo(participantDetailInfosQueryVO);		
		List<ParticipantDetailInfoVO> participantList=participantDetailInfoVOsList.getParticipantDetailInfoVOsList();
		for(ParticipantDetailInfoVO participantDetailInfoVO:participantList){
			System.out.println(participantDetailInfoVO.getDisplayName());
			System.out.println(participantDetailInfoVO.getTitle());
		}		
	}
}