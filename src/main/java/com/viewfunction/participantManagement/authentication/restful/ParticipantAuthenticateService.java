package com.viewfunction.participantManagement.authentication.restful;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import org.apache.cxf.jaxrs.ext.MessageContext;
import javax.ws.rs.core.Context;

import com.viewfunction.participantManagement.authentication.util.ParticipantAuthenticationUtil;

import org.springframework.stereotype.Service;

@Service
@Path("/participantAuthenticateService")  
@Produces("application/json")
public class ParticipantAuthenticateService {
	
	@Context 
	private MessageContext messageContext;
	
	@POST
    @Path("/participantLogin/")
    @Produces("application/json") 
	public ParticipantLoginResultVO participantLogin(ParticipantLoginInfoVO participantLoginInfoVO){	
		ParticipantLoginResultVO participantLoginResultVO=ParticipantAuthenticationUtil.participantPasswordVerify(participantLoginInfoVO);
		if(participantLoginResultVO.isLoginAuthenticateResult()){
			AuthorizationRecordVO currentUserAuthorizeRecordVO = new AuthorizationRecordVO();
			currentUserAuthorizeRecordVO.setLoginId(participantLoginInfoVO.getParticipantID());
			currentUserAuthorizeRecordVO.setParticipantScope(participantLoginInfoVO.getParticipantScope());
			HttpServletRequest request = messageContext.getHttpServletRequest();
			if(request.getRemoteAddr()!=null){
				currentUserAuthorizeRecordVO.setLoginIP(request.getRemoteAddr());
			}else{
				currentUserAuthorizeRecordVO.setLoginIP("0.0.0.0");
			}
			ParticipantAuthenticationUtil.setAuthenticateRecord(currentUserAuthorizeRecordVO);
		}
		return participantLoginResultVO;
	}
	
	@POST
    @Path("/participantLogout/")
    @Produces("application/json") 
	public ParticipantLogoutResultVO participantLogout(ParticipantLogoutInfoVO participantLogoutInfoVO){
		AuthorizationRecordVO currentUserAuthorizeRecordVO = new AuthorizationRecordVO();
		currentUserAuthorizeRecordVO.setLoginId(participantLogoutInfoVO.getParticipantID());
		currentUserAuthorizeRecordVO.setParticipantScope(participantLogoutInfoVO.getParticipantScope());
		HttpServletRequest request = messageContext.getHttpServletRequest();
		if(request.getRemoteAddr()!=null){
			currentUserAuthorizeRecordVO.setLoginIP(request.getRemoteAddr());
		}else{
			currentUserAuthorizeRecordVO.setLoginIP("0.0.0.0");
		}
		boolean removeAuthRecordResult=ParticipantAuthenticationUtil.removeAuthenticateRecord(currentUserAuthorizeRecordVO);
		ParticipantLogoutResultVO participantLogoutResultVO=new ParticipantLogoutResultVO();		
		participantLogoutResultVO.setLogoutAuthenticateResult(removeAuthRecordResult);
		participantLogoutResultVO.setParticipantID(participantLogoutInfoVO.getParticipantID());				
		return participantLogoutResultVO;
	}
	
	@GET
    @Path("/participantLoginVerify/{participantScope}/{userUid}/")
    @Produces("application/json")
	public ParticipantLoginResultVO participantLoginVerify(@PathParam("participantScope") String participantScope,@PathParam("userUid") String userUid){
		AuthorizationRecordVO currentUserAuthorizeRecordVO = new AuthorizationRecordVO();
		currentUserAuthorizeRecordVO.setLoginId(userUid);
		currentUserAuthorizeRecordVO.setParticipantScope(participantScope);
		currentUserAuthorizeRecordVO.setLoginTimeStamp(new Date().getTime());
		HttpServletRequest request = messageContext.getHttpServletRequest();
		if(request.getRemoteAddr()!=null){
			currentUserAuthorizeRecordVO.setLoginIP(request.getRemoteAddr());
		}else{
			currentUserAuthorizeRecordVO.setLoginIP("0.0.0.0");
		}
		boolean verifyResult = ParticipantAuthenticationUtil.verifyAuthenticateRecord(currentUserAuthorizeRecordVO);
		ParticipantLoginResultVO participantLoginResultVO=new ParticipantLoginResultVO();
		participantLoginResultVO.setLoginAuthenticateResult(verifyResult);
		participantLoginResultVO.setParticipantID(userUid);				
		return participantLoginResultVO;		
	}
}