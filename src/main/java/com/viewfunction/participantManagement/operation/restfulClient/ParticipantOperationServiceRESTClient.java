package com.viewfunction.participantManagement.operation.restfulClient;

import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.client.WebClient;

import com.viewfunction.participantManagement.operation.restful.ParticipantDetailInfoVOsList;
import com.viewfunction.participantManagement.operation.restful.ParticipantDetailInfosQueryVO;

public class ParticipantOperationServiceRESTClient {
	public static ParticipantDetailInfoVOsList getUsersDetailInfo(ParticipantDetailInfosQueryVO participantDetailInfosQueryVO){
		WebClient client = WebClient.create(RESTClientConfigUtil.getREST_baseURLValue());
		client.path("participantOperationService/usersDetailInfo/");
		client.type("application/xml").accept("application/xml");
		Response response = client.post(participantDetailInfosQueryVO);		
		ParticipantDetailInfoVOsList participantDetailInfoVOsList=response.readEntity(ParticipantDetailInfoVOsList.class);
		return participantDetailInfoVOsList;
	}	
}