package com.viewfunction.participantManagement.operation.restful;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.activation.DataHandler;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;

import com.viewfunction.participantManagement.operation.util.ParticipantOperationUtil;
import com.viewfunction.participantManagement.util.PerportyHandler;
import com.viewfunction.participantManagement.util.RuntimeEnvironmentHandler;
import org.springframework.stereotype.Service;

@Service
@Path("/participantOperationService")  
@Produces("application/json")
public class ParticipantOperationService {
	
	private static String BINARYFILE_TEMPSTORAGE_FOLDER=PerportyHandler.getPerportyValue(PerportyHandler.BINARYFILE_TEMPSTORAGE_FOLDER).trim();
	private static String BINARYFILE_UPLOADING_TEMPFILE_FOLDER=PerportyHandler.getPerportyValue(PerportyHandler.BINARYFILE_UPLOADING_TEMPFILE_FOLDER).trim();
	
	@GET
    @Path("/userInfo/facePhoto/{participantScope}/{userUid}/")
    @Produces("application/json")    
    public Response getUserFacePhoto(@PathParam("participantScope") String participantScope,@PathParam("userUid") String userUid) {         
		File file=ParticipantOperationUtil.getParticipantFacePhoto(participantScope,userUid);
 		return Response.ok(file, MediaType.APPLICATION_OCTET_STREAM).
 			header("content-disposition", "attachment; filename =" + file.getName()).build(); 		
    }
	
	@GET
    @Path("/userInfo/basicInfo/{participantScope}/{userUid}/")
    @Produces("application/json")    
    public ParticipantBasicInfoVO getUserBasicInfo(@PathParam("participantScope") String participantScope,@PathParam("userUid") String userUid) {         
		ParticipantBasicInfoVO participantBasicInfoVO=ParticipantOperationUtil.getParticipantBasicInfo(participantScope,userUid); 			
		return participantBasicInfoVO;
    }
	
	@GET
    @Path("/userInfo/detailInfo/{participantScope}/{userUid}/")
    @Produces("application/json")    
    public ParticipantDetailInfoVO getUserDetailInfo(@PathParam("participantScope") String participantScope,@PathParam("userUid") String userUid) {
		ParticipantDetailInfoVO participantDetailInfoVO=ParticipantOperationUtil.getParticipantDetailInfo(participantScope,userUid); 			
		return participantDetailInfoVO;
    }
	
	@POST
    @Path("/userInfo/detailInfo/")
    @Produces("application/json")    
    public ParticipantDetailInfoVO updateUserDetailInfo(ParticipantDetailInfoVO participantDetailInfoVO) {     
		ParticipantDetailInfoVO participantDetailInfoVO_result=ParticipantOperationUtil.updateParticipantDetailInfo(participantDetailInfoVO); 			
		return participantDetailInfoVO_result;		
    }
	
	@POST
    @Path("/userInfo/updateRoleType/")
    @Produces("application/json")    
    public ParticipantDetailInfoVO updateUserRoleType(ParticipantDetailInfoVO participantDetailInfoVO) {     
		ParticipantDetailInfoVO participantDetailInfoVO_result=ParticipantOperationUtil.updateParticipantRoleType(participantDetailInfoVO); 			
		return participantDetailInfoVO_result;		
    }
	
	@POST
    @Path("/userInfo/updateAllowedFeatureCategories/")
    @Produces("application/json")    
    public ParticipantDetailInfoVO updateUserAllowedFeatureCategories(ParticipantDetailInfoVO participantDetailInfoVO) {     
		ParticipantDetailInfoVO participantDetailInfoVO_result=ParticipantOperationUtil.updateParticipantAllowedFeatureCategories(participantDetailInfoVO); 			
		return participantDetailInfoVO_result;		
    }
	
	@POST
    @Path("/userInfo/addUser/")
    @Produces("application/json")    
    public ParticipantDetailInfoVO addUser(ParticipantDetailInfoVO participantDetailInfoVO) { 		
		String participantId=participantDetailInfoVO.getUserId();
		String participantScope=participantDetailInfoVO.getParticipantScope();		
		ParticipantDetailInfoVO targetUser=ParticipantOperationUtil.getParticipantDetailInfo(participantScope, participantId);
		if(targetUser!=null){
			return null;
		}else{
			ParticipantDetailInfoVO participantDetailInfoVO_result=ParticipantOperationUtil.addNewParticipant(participantDetailInfoVO); 			
			return participantDetailInfoVO_result;	
		}			
    }	
	
	@POST
	@Path("/userInfo/updateFacePhoto/{participantScope}/{userUid}/")
	@Consumes("multipart/form-data")
	public Response updateFacePhoto(@PathParam("participantScope") String participantScope,@PathParam("userUid") String userUid,MultipartBody body){
		String uploadingFileTempStorePath=RuntimeEnvironmentHandler.getApplicationRootPath()+BINARYFILE_TEMPSTORAGE_FOLDER+"/"+
				BINARYFILE_UPLOADING_TEMPFILE_FOLDER+"/";		
		Attachment attachment=body.getRootAttachment();		
		//String fileType=attachment.getContentType().toString();		
		//String fileName=attachment.getContentDisposition().getParameter("filename");
		long tempFileTimeStamp=new Date().getTime();
		String tempFileName="uploadingFile_"+tempFileTimeStamp;		
		DataHandler dataHandler=attachment.getDataHandler();		
		try {
			InputStream fileInputStream=dataHandler.getInputStream();
			File f=new File(uploadingFileTempStorePath+tempFileName);			
			OutputStream out=new FileOutputStream(f);
			byte buf[]=new byte[1024];
			int len;
			while((len=fileInputStream.read(buf))>0){
				out.write(buf,0,len);
			}
			out.close();			
			fileInputStream.close();	
			ParticipantOperationUtil.updateParticipantFacePhoto(participantScope,userUid, f);
		} catch (IOException e) {			
			e.printStackTrace();			
		}			
		UpdateFacePhotoResultVO updateFacePhotoResultVO=new UpdateFacePhotoResultVO();
		updateFacePhotoResultVO.setUpdateFacePhotoResult(true);
		updateFacePhotoResultVO.setUserId(userUid);		
		return Response.ok().build();
	}
	
	@POST
    @Path("/userInfo/changePassword/")
    @Produces("application/json")    
    public ParticipantChangePasswordResultVO updateUserPassword(ParticipantChangePasswordVO participantChangePasswordVO) {
		ParticipantChangePasswordResultVO participantChangePasswordResultVO=ParticipantOperationUtil.updateParticipantPassword(participantChangePasswordVO);		
		return participantChangePasswordResultVO;		
    }
	
	@POST
    @Path("/usersDetailInfo/")
	@Produces({"application/xml", "application/json"}) 
    public ParticipantDetailInfoVOsList getUsersDetailInfo(ParticipantDetailInfosQueryVO participantDetailInfosQueryVO) {     
		List<String> participantsIdList=participantDetailInfosQueryVO.getParticipantsUserUidList();
		List<ParticipantDetailInfoVO> participantDetailInfoVOsList=new ArrayList<ParticipantDetailInfoVO>();
		String participantScope=participantDetailInfosQueryVO.getParticipantScope();
		if(participantsIdList!=null&&participantScope!=null){
			for(String currentUid:participantsIdList){		
				ParticipantDetailInfoVO participantDetailInfoVO=ParticipantOperationUtil.getParticipantDetailInfo(participantScope,currentUid);			
				participantDetailInfoVOsList.add(participantDetailInfoVO);
			}	
		}			
		ParticipantDetailInfoVOsList participantDetailInfoVOsListResult=new ParticipantDetailInfoVOsList();
		participantDetailInfoVOsListResult.setParticipantDetailInfoVOsList(participantDetailInfoVOsList);
		return participantDetailInfoVOsListResult;
    }
	
	@GET
    @Path("/usersInfo/detailInfo/{participantScope}/")
    @Produces("application/json")    
    public List<ParticipantDetailInfoVO> getUsersDetailInfoOfParticipantSpace(@PathParam("participantScope") String participantScope) {         
		List<ParticipantDetailInfoVO> participantDetailInfoVOList=ParticipantOperationUtil.getParticipantScopeParticipantsDetailInfo(participantScope); 			
		return participantDetailInfoVOList;
    }
	
	@GET
    @Path("/disableUser/{participantScope}/{userUid}")
    @Produces("application/json")    
    public ParticipantDetailInfoVO disableUser(@PathParam("participantScope") String participantScope,@PathParam("userUid") String userUid) { 
		return ParticipantOperationUtil.disableParticipant(participantScope, userUid);
    }
	
	@GET
    @Path("/enableUser/{participantScope}/{userUid}")
    @Produces("application/json")    
    public ParticipantDetailInfoVO enableUser(@PathParam("participantScope") String participantScope,@PathParam("userUid") String userUid) { 
		return ParticipantOperationUtil.enableParticipant(participantScope, userUid);
    }	
}