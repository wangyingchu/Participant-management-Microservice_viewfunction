package com.viewfunction.participantManagement.operation.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.ehcache.Element;

import org.apache.directory.api.ldap.model.cursor.SearchCursor;
import org.apache.directory.api.ldap.model.entry.Attribute;
import org.apache.directory.api.ldap.model.entry.DefaultAttribute;
import org.apache.directory.api.ldap.model.entry.DefaultEntry;
import org.apache.directory.api.ldap.model.entry.DefaultModification;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.entry.Modification;
import org.apache.directory.api.ldap.model.entry.ModificationOperation;
import org.apache.directory.api.ldap.model.entry.Value;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.api.ldap.model.message.Response;
import org.apache.directory.api.ldap.model.message.SearchRequest;
import org.apache.directory.api.ldap.model.message.SearchRequestImpl;
import org.apache.directory.api.ldap.model.message.SearchResultEntry;
import org.apache.directory.api.ldap.model.message.SearchScope;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.api.ldap.model.password.PasswordUtil;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.directory.ldap.client.api.LdapNetworkConnection;

import com.viewfunction.participantManagement.operation.restful.ParticipantBasicInfoVO;
import com.viewfunction.participantManagement.operation.restful.ParticipantChangePasswordResultVO;
import com.viewfunction.participantManagement.operation.restful.ParticipantChangePasswordVO;
import com.viewfunction.participantManagement.operation.restful.ParticipantDetailInfoVO;
import com.viewfunction.participantManagement.util.PerportyHandler;
import com.viewfunction.participantManagement.util.RuntimeEnvironmentHandler;
import com.viewfunction.participantManagement.util.ServiceResourceHolder;
import com.viewfunction.vfmab.restful.userManagement.UserBasicInfoVO;
import com.viewfunction.vfmab.restfulClient.UserManagementServiceRESTClient;

public class ParticipantOperationUtil {
	
	private static String LDAP_SERVER_ADDRESS=PerportyHandler.getPerportyValue(PerportyHandler.LDAP_SERVER_ADDRESS).trim();
	private static String LDAP_SERVER_PORT=PerportyHandler.getPerportyValue(PerportyHandler.LDAP_SERVER_PORT).trim();
	private static String LDAP_ADMIN_DN=PerportyHandler.getPerportyValue(PerportyHandler.LDAP_ADMIN_DN).trim();
	private static String LDAP_ADMIN_PASSWORD=PerportyHandler.getPerportyValue(PerportyHandler.LDAP_ADMIN_PASSWORD).trim();
	private static String LDAP_PARTICIPANT_SEARCHBASE_DN=PerportyHandler.getPerportyValue(PerportyHandler.LDAP_PARTICIPANT_SEARCHBASE_DN).trim();	
	private static String LDAP_PARTICIPANT_SEARCHNODE_OU=PerportyHandler.getPerportyValue(PerportyHandler.LDAP_PARTICIPANT_SEARCHNODE_OU).trim();	
	private static String BINARYFILE_TEMPSTORAGE_FOLDER=PerportyHandler.getPerportyValue(PerportyHandler.BINARYFILE_TEMPSTORAGE_FOLDER).trim();
	private static String BINARYFILE_PARTICIPANT_FACEPHOTO_FOLDER=PerportyHandler.getPerportyValue(PerportyHandler.BINARYFILE_PARTICIPANT_FACEPHOTO_FOLDER).trim();
	private static String BUSINESS_BINARYFILE_FOLDER=PerportyHandler.getPerportyValue(PerportyHandler.BUSINESS_BINARYFILE_FOLDER).trim();
	private static String DEFAULT_USERFACEPHOTO_GREEN=PerportyHandler.getPerportyValue(PerportyHandler.DEFAULT_USERFACEPHOTO_GREEN).trim();	
	private static String defaultUserFacePhotoPath=RuntimeEnvironmentHandler.getApplicationRootPath()+BUSINESS_BINARYFILE_FOLDER+"/"+
			DEFAULT_USERFACEPHOTO_GREEN;
	//participant properties
	private static String PARTICIPANT_PROPERTY_UID=PerportyHandler.getPerportyValue(PerportyHandler.PARTICIPANT_PROPERTY_UID).trim();
	private static String PARTICIPANT_PROPERTY_ROLETYPE=PerportyHandler.getPerportyValue(PerportyHandler.PARTICIPANT_PROPERTY_ROLETYPE).trim();
	private static String PARTICIPANT_PROPERTY_DESCRIPTION=PerportyHandler.getPerportyValue(PerportyHandler.PARTICIPANT_PROPERTY_DESCRIPTION).trim();
	private static String PARTICIPANT_PROPERTY_DISPLAYNAME=PerportyHandler.getPerportyValue(PerportyHandler.PARTICIPANT_PROPERTY_DISPLAYNAME).trim();
	private static String PARTICIPANT_PROPERTY_EMAILADDRESS=PerportyHandler.getPerportyValue(PerportyHandler.PARTICIPANT_PROPERTY_EMAILADDRESS).trim();
	private static String PARTICIPANT_PROPERTY_MOBILEPHONE=PerportyHandler.getPerportyValue(PerportyHandler.PARTICIPANT_PROPERTY_MOBILEPHONE).trim();
	private static String PARTICIPANT_PROPERTY_POSTALCODE=PerportyHandler.getPerportyValue(PerportyHandler.PARTICIPANT_PROPERTY_POSTALCODE).trim();
	private static String PARTICIPANT_PROPERTY_ADDRESS=PerportyHandler.getPerportyValue(PerportyHandler.PARTICIPANT_PROPERTY_ADDRESS).trim();
	private static String PARTICIPANT_PROPERTY_FIXEDPHONE=PerportyHandler.getPerportyValue(PerportyHandler.PARTICIPANT_PROPERTY_FIXEDPHONE).trim();
	private static String PARTICIPANT_PROPERTY_TITLE=PerportyHandler.getPerportyValue(PerportyHandler.PARTICIPANT_PROPERTY_TITLE).trim();
	private static String PARTICIPANT_PROPERTY_FACEPHOTO=PerportyHandler.getPerportyValue(PerportyHandler.PARTICIPANT_PROPERTY_FACEPHOTO).trim();
	private static String PARTICIPANT_PROPERTY_PASSWORD=PerportyHandler.getPerportyValue(PerportyHandler.PARTICIPANT_PROPERTY_PASSWORD).trim();	
	private static String PARTICIPANT_PROPERTY_USERSTATUS=PerportyHandler.getPerportyValue(PerportyHandler.PARTICIPANT_PROPERTY_USERSTATUS).trim();
	private static String PARTICIPANT_PROPERTY_FEATURECATEGORIES=PerportyHandler.getPerportyValue(PerportyHandler.PARTICIPANT_PROPERTY_FEATURECATEGORIES).trim();
	////System data sync
	private static String SYNC_ADDNEW_PARTICIPANT_IN_ACTIVITYSPACE=PerportyHandler.getPerportyValue(PerportyHandler.SYNC_ADDNEW_PARTICIPANT_IN_ACTIVITYSPACE).trim();
	private static String facePhotoDivStr="_DIV_";
	public static String USER_STATUS_ACTIVE="ACTIVE";
	public static String USER_STATUS_DISABLED="INACTIVE";
	
	public static ParticipantDetailInfoVO updateParticipantDetailInfo(ParticipantDetailInfoVO participantDetailInfoVO){
		String participantId=participantDetailInfoVO.getUserId();
		String participantScope=participantDetailInfoVO.getParticipantScope();
		try{
			boolean isPoolConnection;
			LdapConnection currentConnection;
			if(ServiceResourceHolder.getLdapConnectionPool()!=null){			
				currentConnection= ServiceResourceHolder.getLdapConnectionPool().getConnection();
				isPoolConnection=true;				
			}else{
				currentConnection=getLdapConnection();
				isPoolConnection=false;				
			}					
			SearchRequest req = new SearchRequestImpl();
			req.setScope( SearchScope.ONELEVEL );
			req.addAttributes( "*" );
			req.setTimeLimit(0);
			req.setBase( new Dn(LDAP_PARTICIPANT_SEARCHNODE_OU+"o="+participantScope.trim()+","+LDAP_PARTICIPANT_SEARCHBASE_DN) );
			req.setFilter( "("+PARTICIPANT_PROPERTY_UID+"="+participantId.trim()+")" );
			// Process the request
			SearchCursor searchCursor = currentConnection.search(req);				
			//searchCursor.						
			while (searchCursor.next()){
			    Response response = searchCursor.get();
			    // process the SearchResultEntry
			    if (response instanceof SearchResultEntry){
			    	Entry resultEntry = ((SearchResultEntry)response).getEntry();			    	
			    	Modification modification;
			    	
					Attribute description=resultEntry.get(PARTICIPANT_PROPERTY_DESCRIPTION);					
					if(description!=null){					
						if(!participantDetailInfoVO.getDescription().equals(description.getString())){
							if(participantDetailInfoVO.getDescription().equals("")){
								modification=new DefaultModification(ModificationOperation.REPLACE_ATTRIBUTE,PARTICIPANT_PROPERTY_DESCRIPTION," ");
							}else{
								modification=new DefaultModification(ModificationOperation.REPLACE_ATTRIBUTE,PARTICIPANT_PROPERTY_DESCRIPTION,participantDetailInfoVO.getDescription());
							}							
							currentConnection.modify(resultEntry.getDn(), modification);
						}						
					}else{
						resultEntry.add(PARTICIPANT_PROPERTY_DESCRIPTION, participantDetailInfoVO.getDescription());		
					}				
					
					Attribute displayName=resultEntry.get(PARTICIPANT_PROPERTY_DISPLAYNAME);
					if(displayName!=null){					
						if(!participantDetailInfoVO.getDisplayName().equals(displayName.getString())){
							modification=new DefaultModification(ModificationOperation.REPLACE_ATTRIBUTE,PARTICIPANT_PROPERTY_DISPLAYNAME,participantDetailInfoVO.getDisplayName());
							currentConnection.modify(resultEntry.getDn(), modification);
						}						
					}else{
						resultEntry.add(PARTICIPANT_PROPERTY_DISPLAYNAME, participantDetailInfoVO.getDisplayName());		
					}					
					
					Attribute email=resultEntry.get(PARTICIPANT_PROPERTY_EMAILADDRESS);
					if(email!=null){					
						if(!participantDetailInfoVO.getEmailAddress().equals(email.getString())){
							if(participantDetailInfoVO.getEmailAddress().equals("")){
								modification=new DefaultModification(ModificationOperation.REPLACE_ATTRIBUTE,PARTICIPANT_PROPERTY_EMAILADDRESS," ");
							}else{
								modification=new DefaultModification(ModificationOperation.REPLACE_ATTRIBUTE,PARTICIPANT_PROPERTY_EMAILADDRESS,participantDetailInfoVO.getEmailAddress());
							}							
							currentConnection.modify(resultEntry.getDn(), modification);
						}						
					}else{
						resultEntry.add(PARTICIPANT_PROPERTY_EMAILADDRESS, participantDetailInfoVO.getEmailAddress());		
					}					
										
					Attribute mobile=resultEntry.get(PARTICIPANT_PROPERTY_MOBILEPHONE);
					if(mobile!=null){					
						if(!participantDetailInfoVO.getMobilePhone().equals(mobile.getString())){
							if(participantDetailInfoVO.getMobilePhone().equals("")){
								modification=new DefaultModification(ModificationOperation.REPLACE_ATTRIBUTE,PARTICIPANT_PROPERTY_MOBILEPHONE,"0");
							}else{
								modification=new DefaultModification(ModificationOperation.REPLACE_ATTRIBUTE,PARTICIPANT_PROPERTY_MOBILEPHONE,participantDetailInfoVO.getMobilePhone());
							}							
							currentConnection.modify(resultEntry.getDn(), modification);
						}						
					}else{
						resultEntry.add(PARTICIPANT_PROPERTY_MOBILEPHONE, participantDetailInfoVO.getMobilePhone());		
					}						
					
					Attribute postalCode=resultEntry.get(PARTICIPANT_PROPERTY_POSTALCODE);
					if(postalCode!=null){					
						if(!participantDetailInfoVO.getPostalCode().equals(postalCode.getString())){
							if(participantDetailInfoVO.getPostalCode().equals("")){
								modification=new DefaultModification(ModificationOperation.REPLACE_ATTRIBUTE,PARTICIPANT_PROPERTY_POSTALCODE," ");
							}else{
								modification=new DefaultModification(ModificationOperation.REPLACE_ATTRIBUTE,PARTICIPANT_PROPERTY_POSTALCODE,participantDetailInfoVO.getPostalCode());
							}							
							currentConnection.modify(resultEntry.getDn(), modification);
						}						
					}else{
						resultEntry.add(PARTICIPANT_PROPERTY_POSTALCODE, participantDetailInfoVO.getPostalCode());		
					}						
					
					Attribute street=resultEntry.get(PARTICIPANT_PROPERTY_ADDRESS);
					if(street!=null){					
						if(!participantDetailInfoVO.getAddress().equals(street.getString())){
							if(participantDetailInfoVO.getAddress().equals("")){
								modification=new DefaultModification(ModificationOperation.REPLACE_ATTRIBUTE,PARTICIPANT_PROPERTY_ADDRESS," ");
							}else{
								modification=new DefaultModification(ModificationOperation.REPLACE_ATTRIBUTE,PARTICIPANT_PROPERTY_ADDRESS,participantDetailInfoVO.getAddress());
							}							
							currentConnection.modify(resultEntry.getDn(), modification);
						}						
					}else{
						resultEntry.add(PARTICIPANT_PROPERTY_ADDRESS, participantDetailInfoVO.getAddress());		
					}						
					
					Attribute telephoneNumber=resultEntry.get(PARTICIPANT_PROPERTY_FIXEDPHONE);
					if(telephoneNumber!=null){					
						if(!participantDetailInfoVO.getFixedPhone().equals(telephoneNumber.getString())){
							if(participantDetailInfoVO.getFixedPhone().equals("")){
								modification=new DefaultModification(ModificationOperation.REPLACE_ATTRIBUTE,PARTICIPANT_PROPERTY_FIXEDPHONE,"0");
							}else{
								modification=new DefaultModification(ModificationOperation.REPLACE_ATTRIBUTE,PARTICIPANT_PROPERTY_FIXEDPHONE,participantDetailInfoVO.getFixedPhone());
							}							
							currentConnection.modify(resultEntry.getDn(), modification);
						}						
					}else{
						resultEntry.add(PARTICIPANT_PROPERTY_FIXEDPHONE, participantDetailInfoVO.getFixedPhone());		
					}						
					
					Attribute title=resultEntry.get(PARTICIPANT_PROPERTY_TITLE);
					if(title!=null){					
						if(!participantDetailInfoVO.getTitle().equals(title.getString())){
							if(participantDetailInfoVO.getTitle().equals("")){
								modification=new DefaultModification(ModificationOperation.REPLACE_ATTRIBUTE,PARTICIPANT_PROPERTY_TITLE," ");
							}else{
								modification=new DefaultModification(ModificationOperation.REPLACE_ATTRIBUTE,PARTICIPANT_PROPERTY_TITLE,participantDetailInfoVO.getTitle());
							}							
							currentConnection.modify(resultEntry.getDn(), modification);
						}						
					}else{
						resultEntry.add(PARTICIPANT_PROPERTY_TITLE, participantDetailInfoVO.getTitle());		
					}					
					Attribute photo=resultEntry.get(PARTICIPANT_PROPERTY_FACEPHOTO);
					if(photo!=null){
						participantDetailInfoVO.setHasFacePhoto(true);
					}else{
						participantDetailInfoVO.setHasFacePhoto(false);
					}								
					Attribute status=resultEntry.get(PARTICIPANT_PROPERTY_USERSTATUS);
					if(status!=null){
						if(status.getString().equals(USER_STATUS_ACTIVE)){
							participantDetailInfoVO.setActiveUser(true);
						}else{
							participantDetailInfoVO.setActiveUser(false);
						}
					}else{
						participantDetailInfoVO.setActiveUser(false);
					}			
					Attribute roleType=resultEntry.get(PARTICIPANT_PROPERTY_ROLETYPE);	
					if(roleType!=null){
						participantDetailInfoVO.setRoleType(roleType.getString());
					}
					List<String> allowedFeatureCategoriesList=new ArrayList<String>();
					Attribute allowedFeatureCategories=resultEntry.get(PARTICIPANT_PROPERTY_FEATURECATEGORIES);
					if(allowedFeatureCategories!=null){
						Iterator<Value<?>> valueItet=allowedFeatureCategories.iterator();
						if(valueItet!=null){
							while(valueItet.hasNext()){
								Value<?> currentValue=valueItet.next();
								allowedFeatureCategoriesList.add(currentValue.getString());
							}
						}
					}
					participantDetailInfoVO.setAllowedFeatureCategories(allowedFeatureCategoriesList);
					
					String cacheKeyString=participantScope+facePhotoDivStr+participantId.trim();
					ServiceResourceHolder.getPaticipantDetailInfoCache().put(new Element(cacheKeyString, participantDetailInfoVO));
					ServiceResourceHolder.getPaticipantBasicInfoCache().remove(cacheKeyString);				
			    	break;
			    }
			}				
			searchCursor.close();		
			if(!isPoolConnection){
				currentConnection.unBind();
				currentConnection.close();			
			}else{
				ServiceResourceHolder.getLdapConnectionPool().releaseConnection(currentConnection);
			}				
			return participantDetailInfoVO;				
		}catch(LdapException e){
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}
		return participantDetailInfoVO;
	}	
	
	public static ParticipantDetailInfoVO updateParticipantRoleType(ParticipantDetailInfoVO participantDetailInfoVO){
		String participantId=participantDetailInfoVO.getUserId();
		String participantScope=participantDetailInfoVO.getParticipantScope();
		try{
			boolean isPoolConnection;
			LdapConnection currentConnection;
			if(ServiceResourceHolder.getLdapConnectionPool()!=null){			
				currentConnection= ServiceResourceHolder.getLdapConnectionPool().getConnection();
				isPoolConnection=true;				
			}else{
				currentConnection=getLdapConnection();
				isPoolConnection=false;				
			}					
			SearchRequest req = new SearchRequestImpl();
			req.setScope( SearchScope.ONELEVEL );
			req.addAttributes( "*" );
			req.setTimeLimit(0);
			req.setBase( new Dn(LDAP_PARTICIPANT_SEARCHNODE_OU+"o="+participantScope.trim()+","+LDAP_PARTICIPANT_SEARCHBASE_DN) );
			req.setFilter( "("+PARTICIPANT_PROPERTY_UID+"="+participantId.trim()+")" );
			// Process the request
			SearchCursor searchCursor = currentConnection.search(req);				
			//searchCursor.						
			while (searchCursor.next()){
			    Response response = searchCursor.get();
			    // process the SearchResultEntry
			    if (response instanceof SearchResultEntry){
			    	Entry resultEntry = ((SearchResultEntry)response).getEntry();			    	
			    	Modification modification;
			    	if(participantDetailInfoVO.getRoleType()!=null){
				    	Attribute roleType=resultEntry.get(PARTICIPANT_PROPERTY_ROLETYPE);	
						if(roleType!=null){
							if(!participantDetailInfoVO.getRoleType().equals(roleType.getString())){
								modification=new DefaultModification(ModificationOperation.REPLACE_ATTRIBUTE,PARTICIPANT_PROPERTY_ROLETYPE,participantDetailInfoVO.getRoleType());
								currentConnection.modify(resultEntry.getDn(), modification);
							}
						}else{
							resultEntry.add(PARTICIPANT_PROPERTY_ROLETYPE, participantDetailInfoVO.getRoleType());
						}
						String cacheKeyString=participantScope+facePhotoDivStr+participantId.trim();
				    	Element detailInfoCacheObj=ServiceResourceHolder.getPaticipantDetailInfoCache().get(cacheKeyString);
				    	if(detailInfoCacheObj!=null){
				    		ParticipantDetailInfoVO cachedParticipantDetailInfoVO=(ParticipantDetailInfoVO)detailInfoCacheObj.getObjectValue();	
				    		cachedParticipantDetailInfoVO.setRoleType( participantDetailInfoVO.getRoleType());
				    	}
				    	ServiceResourceHolder.getPaticipantBasicInfoCache().remove(cacheKeyString);		
			    	}
			    	break;
			    }
			}				
			searchCursor.close();		
			if(!isPoolConnection){
				currentConnection.unBind();
				currentConnection.close();			
			}else{
				ServiceResourceHolder.getLdapConnectionPool().releaseConnection(currentConnection);
			}				
			return getParticipantDetailInfo(participantScope,participantId);				
		}catch(LdapException e){
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}
		return getParticipantDetailInfo(participantScope,participantId);
	}	
	
	public static ParticipantDetailInfoVO updateParticipantAllowedFeatureCategories(ParticipantDetailInfoVO participantDetailInfoVO){
		String participantId=participantDetailInfoVO.getUserId();
		String participantScope=participantDetailInfoVO.getParticipantScope();
		try{
			boolean isPoolConnection;
			LdapConnection currentConnection;
			if(ServiceResourceHolder.getLdapConnectionPool()!=null){			
				currentConnection= ServiceResourceHolder.getLdapConnectionPool().getConnection();
				isPoolConnection=true;				
			}else{
				currentConnection=getLdapConnection();
				isPoolConnection=false;				
			}					
			SearchRequest req = new SearchRequestImpl();
			req.setScope( SearchScope.ONELEVEL );
			req.addAttributes( "*" );
			req.setTimeLimit(0);
			req.setBase( new Dn(LDAP_PARTICIPANT_SEARCHNODE_OU+"o="+participantScope.trim()+","+LDAP_PARTICIPANT_SEARCHBASE_DN) );
			req.setFilter( "("+PARTICIPANT_PROPERTY_UID+"="+participantId.trim()+")" );
			// Process the request
			SearchCursor searchCursor = currentConnection.search(req);				
			//searchCursor.						
			while (searchCursor.next()){
			    Response response = searchCursor.get();
			    // process the SearchResultEntry
			    if (response instanceof SearchResultEntry){
			    	Entry resultEntry = ((SearchResultEntry)response).getEntry();			    	
			    	Modification modification;
			    	Attribute allowedFeatureCategories=resultEntry.get(PARTICIPANT_PROPERTY_FEATURECATEGORIES);
					if(allowedFeatureCategories!=null){
						modification=new DefaultModification(ModificationOperation.REMOVE_ATTRIBUTE,PARTICIPANT_PROPERTY_FEATURECATEGORIES);
						currentConnection.modify(resultEntry.getDn(), modification);
					}
					if(participantDetailInfoVO.getAllowedFeatureCategories()!=null&&participantDetailInfoVO.getAllowedFeatureCategories().size()>0){
						List<String> persistedCategories=new ArrayList<String>();
						for(String currentAllowedFeature:participantDetailInfoVO.getAllowedFeatureCategories()){
							if(!currentAllowedFeature.equals("")){
								Attribute currentFeatureAttribute = new DefaultAttribute(PARTICIPANT_PROPERTY_FEATURECATEGORIES, currentAllowedFeature);
								modification=new DefaultModification(ModificationOperation.ADD_ATTRIBUTE,currentFeatureAttribute);
								currentConnection.modify(resultEntry.getDn(), modification);
								persistedCategories.add(currentAllowedFeature);
							}
						}
					}
					String cacheKeyString=participantScope+facePhotoDivStr+participantId.trim();
			    	ServiceResourceHolder.getPaticipantDetailInfoCache().remove(cacheKeyString);	
			    	ServiceResourceHolder.getPaticipantBasicInfoCache().remove(cacheKeyString);	
			    	break;
			    }
			}				
			searchCursor.close();		
			if(!isPoolConnection){
				currentConnection.unBind();
				currentConnection.close();			
			}else{
				ServiceResourceHolder.getLdapConnectionPool().releaseConnection(currentConnection);
			}				
			return getParticipantDetailInfo(participantScope,participantId);			
		}catch(LdapException e){
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}
		return getParticipantDetailInfo(participantScope,participantId);
	}	
	
	public static boolean updateParticipantFacePhoto(String participantScope,String userId,File photoFile){
		String participantId=userId.trim();
		try{
			boolean isPoolConnection;
			LdapConnection currentConnection;
			if(ServiceResourceHolder.getLdapConnectionPool()!=null){			
				currentConnection= ServiceResourceHolder.getLdapConnectionPool().getConnection();
				isPoolConnection=true;				
			}else{
				currentConnection=getLdapConnection();
				isPoolConnection=false;				
			}					
			SearchRequest req = new SearchRequestImpl();
			req.setScope( SearchScope.ONELEVEL );
			req.addAttributes( "*" );
			req.setTimeLimit(0);
			req.setBase( new Dn(LDAP_PARTICIPANT_SEARCHNODE_OU+"o="+participantScope.trim()+","+LDAP_PARTICIPANT_SEARCHBASE_DN) );
			req.setFilter( "("+PARTICIPANT_PROPERTY_UID+"="+participantId.trim()+")" );
			// Process the request
			SearchCursor searchCursor = currentConnection.search(req);				
			//searchCursor.						
			while (searchCursor.next()){
			    Response response = searchCursor.get();
			    // process the SearchResultEntry
			    if (response instanceof SearchResultEntry){
			    	Entry resultEntry = ((SearchResultEntry)response).getEntry();
			    	BufferedInputStream in = new BufferedInputStream(new FileInputStream(photoFile));        
			        ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
			        byte[] temp = new byte[1024];        
			        int size = 0;        
			        while ((size = in.read(temp)) != -1) {        
			            out.write(temp, 0, size);        
			        }        
			        in.close();  
			        byte[] fileContent = out.toByteArray(); 
			    	Modification modification;	
					Attribute photo=resultEntry.get(PARTICIPANT_PROPERTY_FACEPHOTO);					
					if(photo!=null){	
						modification=new DefaultModification(ModificationOperation.REPLACE_ATTRIBUTE,PARTICIPANT_PROPERTY_FACEPHOTO,fileContent);						
					}else{						
						modification=new DefaultModification(ModificationOperation.ADD_ATTRIBUTE,PARTICIPANT_PROPERTY_FACEPHOTO, fileContent);
					}	
					currentConnection.modify(resultEntry.getDn(), modification);
					photoFile.delete();									
					String userFacePhotoStorePath=RuntimeEnvironmentHandler.getApplicationRootPath()+BINARYFILE_TEMPSTORAGE_FOLDER+"/"+
							BINARYFILE_PARTICIPANT_FACEPHOTO_FOLDER+"/";		
					String userFacePhotoFileName=participantScope+facePhotoDivStr+userId.trim();
					String targetPhotoURI=userFacePhotoStorePath+userFacePhotoFileName;					
					File orgFacePhotoFile=new File(targetPhotoURI);					
					if(orgFacePhotoFile.exists()){
						orgFacePhotoFile.delete();						
					}
					String cacheKeyString=participantScope+facePhotoDivStr+participantId.trim();					
					Element cachedParticipantDetailInfoObj = ServiceResourceHolder.getPaticipantDetailInfoCache().get(cacheKeyString);
					if(cachedParticipantDetailInfoObj!=null){
						ParticipantDetailInfoVO participantDetailInfoVO=(ParticipantDetailInfoVO)cachedParticipantDetailInfoObj.getObjectValue();
						participantDetailInfoVO.setHasFacePhoto(true);						
						ServiceResourceHolder.getPaticipantDetailInfoCache().put(new Element(cacheKeyString, participantDetailInfoVO));
					}					
					Element cachedParticipantBasicInfoObj = ServiceResourceHolder.getPaticipantBasicInfoCache().get(cacheKeyString);
					if(cachedParticipantBasicInfoObj!=null){
						ParticipantBasicInfoVO participantBasicInfoVO=(ParticipantBasicInfoVO)cachedParticipantBasicInfoObj.getObjectValue();
						participantBasicInfoVO.setHasFacePhoto(true);						
						ServiceResourceHolder.getPaticipantBasicInfoCache().put(new Element(cacheKeyString, participantBasicInfoVO));
					}	
			    	break;
			    }
			}				
			searchCursor.close();		
			if(!isPoolConnection){
				currentConnection.unBind();
				currentConnection.close();			
			}else{
				ServiceResourceHolder.getLdapConnectionPool().releaseConnection(currentConnection);
			}				
			return true;				
		}catch(LdapException e){
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}		
		return false;
	}
	
	public static ParticipantChangePasswordResultVO updateParticipantPassword(ParticipantChangePasswordVO participantChangePasswordVO){
		String participantId=participantChangePasswordVO.getUserId().trim();
		ParticipantChangePasswordResultVO participantChangePasswordResultVO=new ParticipantChangePasswordResultVO();
		participantChangePasswordResultVO.setChangePasswordResult(false);
		participantChangePasswordResultVO.setReturnMessage("更新用户  "+participantId+"密码");
		String participantScope=participantChangePasswordVO.getParticipantScope();
		try{
			boolean isPoolConnection;
			LdapConnection currentConnection;
			if(ServiceResourceHolder.getLdapConnectionPool()!=null){			
				currentConnection= ServiceResourceHolder.getLdapConnectionPool().getConnection();
				isPoolConnection=true;				
			}else{
				currentConnection=getLdapConnection();
				isPoolConnection=false;				
			}					
			SearchRequest req = new SearchRequestImpl();
			req.setScope( SearchScope.ONELEVEL );
			req.addAttributes( "*" );
			req.setTimeLimit(0);
			req.setBase( new Dn(LDAP_PARTICIPANT_SEARCHNODE_OU+"o="+participantScope.trim()+","+LDAP_PARTICIPANT_SEARCHBASE_DN) );
			req.setFilter( "("+PARTICIPANT_PROPERTY_UID+"="+participantId.trim()+")" );
			// Process the request
			SearchCursor searchCursor = currentConnection.search(req);				
			//searchCursor.						
			while (searchCursor.next()){
			    Response response = searchCursor.get();
			    // process the SearchResultEntry
			    if (response instanceof SearchResultEntry){
			    	Entry resultEntry = ((SearchResultEntry)response).getEntry();
			    	Modification modification;
			    	Attribute password=resultEntry.get(PARTICIPANT_PROPERTY_PASSWORD);			    	
					if(password!=null){						
						byte[] passedInPlainPwd = participantChangePasswordVO.getCurrentPassword().getBytes();						
						byte[] currentPassword=password.getBytes();
						boolean currentPasswordCheckResult=PasswordUtil.compareCredentials( passedInPlainPwd, currentPassword);
						try{
							if(currentPasswordCheckResult){
								modification=new DefaultModification(ModificationOperation.REPLACE_ATTRIBUTE,PARTICIPANT_PROPERTY_PASSWORD,participantChangePasswordVO.getNewPassword());
								currentConnection.modify(resultEntry.getDn(), modification);
								participantChangePasswordResultVO.setChangePasswordResult(true);
								participantChangePasswordResultVO.setReturnMessage("密码更新成功");
							}else{
								participantChangePasswordResultVO.setReturnMessage("当前密码输入错误");
							}
						}catch(org.apache.directory.api.ldap.model.exception.LdapInvalidAttributeValueException e){							
							participantChangePasswordResultVO.setChangePasswordResult(false);
							participantChangePasswordResultVO.setReturnMessage("密码更新操作失败<br/>"+e.getMessage());
						}
					}else{
						resultEntry.add(PARTICIPANT_PROPERTY_PASSWORD, participantChangePasswordVO.getNewPassword());		
					}	
			    	break;
			    }
			}				
			searchCursor.close();		
			if(!isPoolConnection){
				currentConnection.unBind();
				currentConnection.close();			
			}else{
				ServiceResourceHolder.getLdapConnectionPool().releaseConnection(currentConnection);
			}			
		}catch(LdapException e){
			e.printStackTrace();
			participantChangePasswordResultVO.setReturnMessage("密码更新操作失败");
		}catch(Exception e){
			e.printStackTrace();
			participantChangePasswordResultVO.setReturnMessage("密码更新操作失败");
		}		
		return participantChangePasswordResultVO;
	}		
	
	public static ParticipantDetailInfoVO getParticipantDetailInfo(String participantScope,String participantUid){		
		String cacheKeyString=participantScope+facePhotoDivStr+participantUid.trim();
		Element cachedParticipantDetailInfoObj = ServiceResourceHolder.getPaticipantDetailInfoCache().get(cacheKeyString);
		if(cachedParticipantDetailInfoObj!=null){
			ParticipantDetailInfoVO participantDetailInfoVO=(ParticipantDetailInfoVO)cachedParticipantDetailInfoObj.getObjectValue();			
			return participantDetailInfoVO;
		}else{						
			try{
				boolean isPoolConnection;
				LdapConnection currentConnection;
				if(ServiceResourceHolder.getLdapConnectionPool()!=null){			
					currentConnection= ServiceResourceHolder.getLdapConnectionPool().getConnection();
					isPoolConnection=true;				
				}else{
					currentConnection=getLdapConnection();
					isPoolConnection=false;
				}							
				SearchRequest req = new SearchRequestImpl();
				req.setScope( SearchScope.ONELEVEL );
				req.addAttributes( "*" );
				req.setTimeLimit(0);
				req.setBase( new Dn(LDAP_PARTICIPANT_SEARCHNODE_OU+"o="+participantScope.trim()+","+LDAP_PARTICIPANT_SEARCHBASE_DN) );
				req.setFilter( "("+PARTICIPANT_PROPERTY_UID+"="+participantUid.trim()+")" );
				// Process the request
				SearchCursor searchCursor = currentConnection.search(req);				
				//searchCursor.			
				ParticipantDetailInfoVO participantDetailInfoVO=null;
				while (searchCursor.next()){
				    Response response = searchCursor.get();
				    // process the SearchResultEntry
				    if (response instanceof SearchResultEntry){
				    	Entry resultEntry = ((SearchResultEntry)response).getEntry();					
				    	participantDetailInfoVO=new ParticipantDetailInfoVO();
						Attribute userRole=resultEntry.get(PARTICIPANT_PROPERTY_ROLETYPE);						
						if(userRole!=null){
							participantDetailInfoVO.setRoleType(userRole.getString());
						}
						List<String> allowedFeatureCategoriesList=new ArrayList<String>();
						Attribute allowedFeatureCategories=resultEntry.get(PARTICIPANT_PROPERTY_FEATURECATEGORIES);
						if(allowedFeatureCategories!=null){
							Iterator<Value<?>> valueItet=allowedFeatureCategories.iterator();
							if(valueItet!=null){
								while(valueItet.hasNext()){
									Value<?> currentValue=valueItet.next();
									allowedFeatureCategoriesList.add(currentValue.getString());
								}
							}
						}
						participantDetailInfoVO.setAllowedFeatureCategories(allowedFeatureCategoriesList);
						Attribute description=resultEntry.get(PARTICIPANT_PROPERTY_DESCRIPTION);
						if(description!=null){
							participantDetailInfoVO.setDescription(description.getString());	
						}										
						Attribute displayName=resultEntry.get(PARTICIPANT_PROPERTY_DISPLAYNAME);
						if(displayName!=null){
							participantDetailInfoVO.setDisplayName(displayName.getString());
						}
						Attribute email=resultEntry.get(PARTICIPANT_PROPERTY_EMAILADDRESS);
						if(email!=null){
							participantDetailInfoVO.setEmailAddress(email.getString());
						}						
						Attribute mobile=resultEntry.get(PARTICIPANT_PROPERTY_MOBILEPHONE);
						if(mobile!=null){
							participantDetailInfoVO.setMobilePhone(mobile.getString());
						}											
						Attribute postalCode=resultEntry.get(PARTICIPANT_PROPERTY_POSTALCODE);
						if(postalCode!=null){
							participantDetailInfoVO.setPostalCode(postalCode.getString());
						}						
						Attribute street=resultEntry.get(PARTICIPANT_PROPERTY_ADDRESS);
						if(street!=null){
							participantDetailInfoVO.setAddress(street.getString());
						}						
						Attribute telephoneNumber=resultEntry.get(PARTICIPANT_PROPERTY_FIXEDPHONE);
						if(telephoneNumber!=null){
							participantDetailInfoVO.setFixedPhone(telephoneNumber.getString());
						}						
						Attribute title=resultEntry.get(PARTICIPANT_PROPERTY_TITLE);
						if(title!=null){
							participantDetailInfoVO.setTitle(title.getString());		
						}	
						Attribute status=resultEntry.get(PARTICIPANT_PROPERTY_USERSTATUS);
						if(status!=null){
							if(status.getString().equals(USER_STATUS_ACTIVE)){
								participantDetailInfoVO.setActiveUser(true);
							}else{
								participantDetailInfoVO.setActiveUser(false);
							}
						}else{
							participantDetailInfoVO.setActiveUser(false);
						}				
						Attribute photo=resultEntry.get(PARTICIPANT_PROPERTY_FACEPHOTO);						
						if(photo!=null&&photo.getBytes()!=null){							
							String userFacePhotoStorePath=RuntimeEnvironmentHandler.getApplicationRootPath()+BINARYFILE_TEMPSTORAGE_FOLDER+"/"+
									BINARYFILE_PARTICIPANT_FACEPHOTO_FOLDER+"/";
							String facePhotoFileName=participantScope+facePhotoDivStr+participantUid.trim();
							String targetPhotoURI=userFacePhotoStorePath+facePhotoFileName;								
							File targetFile=new File(targetPhotoURI);
							if(!targetFile.exists()){	
								byte[] bFile = (byte[])photo.get().getBytes();			 
							    try {
									//convert array of bytes into file				        	
									FileOutputStream fileOuputStream =new FileOutputStream(targetPhotoURI); 
									fileOuputStream.write(bFile);
									fileOuputStream.close();						   
							    }catch(Exception e){
							    	e.printStackTrace();
							    } 								
							}								
							participantDetailInfoVO.setHasFacePhoto(true);
						}else{
							participantDetailInfoVO.setHasFacePhoto(false);
						}						
						participantDetailInfoVO.setUserId(participantUid.trim());	
						participantDetailInfoVO.setParticipantScope(participantScope);
						ServiceResourceHolder.getPaticipantDetailInfoCache().put(new Element(cacheKeyString, participantDetailInfoVO));
				    	break;	
				    }
				}				
				searchCursor.close();
				if(!isPoolConnection){
					currentConnection.unBind();
					currentConnection.close();			
				}else{
					ServiceResourceHolder.getLdapConnectionPool().releaseConnection(currentConnection);
				}				
				return participantDetailInfoVO;				
			}catch(LdapException e){
				e.printStackTrace();
			}catch(Exception e){
				e.printStackTrace();
			}
			return null;
		}
	}	
	
	public static List<ParticipantDetailInfoVO> getParticipantScopeParticipantsDetailInfo(String participantScope){
		List<ParticipantDetailInfoVO> participantDetailInfoVOList=new ArrayList<ParticipantDetailInfoVO>();
		try{
			boolean isPoolConnection;
			LdapConnection currentConnection;
			if(ServiceResourceHolder.getLdapConnectionPool()!=null){			
				currentConnection= ServiceResourceHolder.getLdapConnectionPool().getConnection();
				isPoolConnection=true;				
			}else{
				currentConnection=getLdapConnection();
				isPoolConnection=false;
			}							
			SearchRequest req = new SearchRequestImpl();
			req.setScope( SearchScope.ONELEVEL );
			req.addAttributes( "*" );
			req.setTimeLimit(0);
			req.setBase( new Dn(LDAP_PARTICIPANT_SEARCHNODE_OU+"o="+participantScope.trim()+","+LDAP_PARTICIPANT_SEARCHBASE_DN) );	
			req.setFilter( "("+PARTICIPANT_PROPERTY_UID+"=*)" );
			// Process the request
			SearchCursor searchCursor = currentConnection.search(req);				
			//searchCursor.			
			ParticipantDetailInfoVO participantDetailInfoVO=null;
			while (searchCursor.next()){
				Response response = searchCursor.get();
				// process the SearchResultEntry
				if (response instanceof SearchResultEntry){
					Entry resultEntry = ((SearchResultEntry)response).getEntry();					
				    participantDetailInfoVO=new ParticipantDetailInfoVO();
					Attribute roleType=resultEntry.get(PARTICIPANT_PROPERTY_ROLETYPE);						
					if(roleType!=null){
						participantDetailInfoVO.setRoleType(roleType.getString());
					}
					List<String> allowedFeatureCategoriesList=new ArrayList<String>();
					Attribute allowedFeatureCategories=resultEntry.get(PARTICIPANT_PROPERTY_FEATURECATEGORIES);
					if(allowedFeatureCategories!=null){
						Iterator<Value<?>> valueItet=allowedFeatureCategories.iterator();
						if(valueItet!=null){
							while(valueItet.hasNext()){
								Value<?> currentValue=valueItet.next();
								allowedFeatureCategoriesList.add(currentValue.getString());
							}
						}
					}
					participantDetailInfoVO.setAllowedFeatureCategories(allowedFeatureCategoriesList);
					Attribute description=resultEntry.get(PARTICIPANT_PROPERTY_DESCRIPTION);
					if(description!=null){
						participantDetailInfoVO.setDescription(description.getString());	
					}										
					Attribute displayName=resultEntry.get(PARTICIPANT_PROPERTY_DISPLAYNAME);
					if(displayName!=null){
						participantDetailInfoVO.setDisplayName(displayName.getString());
					}
					Attribute email=resultEntry.get(PARTICIPANT_PROPERTY_EMAILADDRESS);
					if(email!=null){
						participantDetailInfoVO.setEmailAddress(email.getString());
					}						
					Attribute mobile=resultEntry.get(PARTICIPANT_PROPERTY_MOBILEPHONE);
					if(mobile!=null){
						participantDetailInfoVO.setMobilePhone(mobile.getString());
					}											
					Attribute postalCode=resultEntry.get(PARTICIPANT_PROPERTY_POSTALCODE);
					if(postalCode!=null){
						participantDetailInfoVO.setPostalCode(postalCode.getString());
					}						
					Attribute street=resultEntry.get(PARTICIPANT_PROPERTY_ADDRESS);
					if(street!=null){
						participantDetailInfoVO.setAddress(street.getString());
					}						
					Attribute telephoneNumber=resultEntry.get(PARTICIPANT_PROPERTY_FIXEDPHONE);
					if(telephoneNumber!=null){
						participantDetailInfoVO.setFixedPhone(telephoneNumber.getString());
					}						
					Attribute title=resultEntry.get(PARTICIPANT_PROPERTY_TITLE);
					if(title!=null){
						participantDetailInfoVO.setTitle(title.getString());		
					}						
					Attribute status=resultEntry.get(PARTICIPANT_PROPERTY_USERSTATUS);
					if(status!=null){
						if(status.getString().equals(USER_STATUS_ACTIVE)){
							participantDetailInfoVO.setActiveUser(true);
						}else{
							participantDetailInfoVO.setActiveUser(false);
						}
					}else{
						participantDetailInfoVO.setActiveUser(false);
					}				
					
					Attribute uid=resultEntry.get(PARTICIPANT_PROPERTY_UID);
					String uidStr=uid.getString();
					participantDetailInfoVO.setUserId(uidStr.trim());
						
					Attribute photo=resultEntry.get(PARTICIPANT_PROPERTY_FACEPHOTO);						
					if(photo!=null&&photo.getBytes()!=null){
						String userFacePhotoStorePath=RuntimeEnvironmentHandler.getApplicationRootPath()+BINARYFILE_TEMPSTORAGE_FOLDER+"/"+
								BINARYFILE_PARTICIPANT_FACEPHOTO_FOLDER+"/";
						String facePhotoFileName=participantScope+facePhotoDivStr+uidStr.trim();
						String targetPhotoURI=userFacePhotoStorePath+facePhotoFileName;								
						File targetFile=new File(targetPhotoURI);
						if(!targetFile.exists()){	
							byte[] bFile = (byte[])photo.get().getBytes();			 
							   try {
								//convert array of bytes into file				        	
								FileOutputStream fileOuputStream =new FileOutputStream(targetPhotoURI); 
								fileOuputStream.write(bFile);
								fileOuputStream.close();						   
							}catch(Exception e){
								e.printStackTrace();
							} 								
						}								
						participantDetailInfoVO.setHasFacePhoto(true);
					}else{
						participantDetailInfoVO.setHasFacePhoto(false);
					}					
					participantDetailInfoVO.setParticipantScope(participantScope);	
					String cacheKeyString=participantScope+facePhotoDivStr+uidStr.trim();	
					Element cachedParticipantDetailInfoObj = ServiceResourceHolder.getPaticipantDetailInfoCache().get(cacheKeyString);
					if(cachedParticipantDetailInfoObj!=null){
						ServiceResourceHolder.getPaticipantDetailInfoCache().remove(cacheKeyString);
					}					
					ServiceResourceHolder.getPaticipantDetailInfoCache().put(new Element(cacheKeyString, participantDetailInfoVO));
					participantDetailInfoVOList.add(participantDetailInfoVO);
				}
			}				
			searchCursor.close();
			if(!isPoolConnection){
				currentConnection.unBind();
				currentConnection.close();			
			}else{
				ServiceResourceHolder.getLdapConnectionPool().releaseConnection(currentConnection);
			}				
			return participantDetailInfoVOList;				
		}catch(LdapException e){
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;		
	}
	
	public static ParticipantBasicInfoVO getParticipantBasicInfo(String participantScope,String participantUid){
		String cacheKeyString=participantScope+facePhotoDivStr+participantUid.trim();
		Element cachedParticipantBasicInfoObj = ServiceResourceHolder.getPaticipantBasicInfoCache().get(cacheKeyString);
		if(cachedParticipantBasicInfoObj!=null){
			ParticipantBasicInfoVO participantBasicInfoVO=(ParticipantBasicInfoVO)cachedParticipantBasicInfoObj.getObjectValue();			
			return participantBasicInfoVO;
		}else{			
			try{
				boolean isPoolConnection;
				LdapConnection currentConnection;
				if(ServiceResourceHolder.getLdapConnectionPool()!=null){			
					currentConnection= ServiceResourceHolder.getLdapConnectionPool().getConnection();
					isPoolConnection=true;				
				}else{
					currentConnection=getLdapConnection();
					isPoolConnection=false;				
				}							
				SearchRequest req = new SearchRequestImpl();
				req.setScope( SearchScope.ONELEVEL );
				req.addAttributes( "*" );
				req.setTimeLimit(0);
				req.setBase( new Dn(LDAP_PARTICIPANT_SEARCHNODE_OU+"o="+participantScope.trim()+","+LDAP_PARTICIPANT_SEARCHBASE_DN) );
				req.setFilter( "("+PARTICIPANT_PROPERTY_UID+"="+participantUid.trim()+")" );
				// Process the request
				SearchCursor searchCursor = currentConnection.search(req);				
				//searchCursor.			
				ParticipantBasicInfoVO participantBasicInfoVO=null;
				while (searchCursor.next()){
				    Response response = searchCursor.get();
				    // process the SearchResultEntry
				    if (response instanceof SearchResultEntry){
				    	Entry resultEntry = ((SearchResultEntry)response).getEntry();					
						participantBasicInfoVO=new ParticipantBasicInfoVO();
						Attribute businessCategory=resultEntry.get(PARTICIPANT_PROPERTY_ROLETYPE);						
						if(businessCategory!=null){
							participantBasicInfoVO.setRoleType(businessCategory.getString());
						}
						Attribute description=resultEntry.get(PARTICIPANT_PROPERTY_DESCRIPTION);
						if(description!=null){
							participantBasicInfoVO.setDescription(description.getString());	
						}										
						Attribute displayName=resultEntry.get(PARTICIPANT_PROPERTY_DISPLAYNAME);
						if(displayName!=null){
							participantBasicInfoVO.setDisplayName(displayName.getString());
						}
						Attribute email=resultEntry.get(PARTICIPANT_PROPERTY_EMAILADDRESS);
						if(email!=null){
							participantBasicInfoVO.setEmailAddress(email.getString());
						}						
						Attribute mobile=resultEntry.get(PARTICIPANT_PROPERTY_MOBILEPHONE);
						if(mobile!=null){
							participantBasicInfoVO.setMobilePhone(mobile.getString());
						}											
						Attribute postalCode=resultEntry.get(PARTICIPANT_PROPERTY_POSTALCODE);
						if(postalCode!=null){
							participantBasicInfoVO.setPostalCode(postalCode.getString());
						}						
						Attribute street=resultEntry.get(PARTICIPANT_PROPERTY_ADDRESS);
						if(street!=null){
							participantBasicInfoVO.setAddress(street.getString());
						}						
						Attribute telephoneNumber=resultEntry.get(PARTICIPANT_PROPERTY_FIXEDPHONE);
						if(telephoneNumber!=null){
							participantBasicInfoVO.setFixedPhone(telephoneNumber.getString());
						}						
						Attribute title=resultEntry.get(PARTICIPANT_PROPERTY_TITLE);
						if(title!=null){
							participantBasicInfoVO.setTitle(title.getString());		
						}								
						Attribute photo=resultEntry.get(PARTICIPANT_PROPERTY_FACEPHOTO);						
						if(photo!=null&&photo.getBytes()!=null){							
							String userFacePhotoStorePath=RuntimeEnvironmentHandler.getApplicationRootPath()+BINARYFILE_TEMPSTORAGE_FOLDER+"/"+
									BINARYFILE_PARTICIPANT_FACEPHOTO_FOLDER+"/";
							String facePhotoFileName=participantScope+facePhotoDivStr+participantUid.trim();
							String targetPhotoURI=userFacePhotoStorePath+facePhotoFileName;							
							File targetFile=new File(targetPhotoURI);
							if(!targetFile.exists()){	
								byte[] bFile = (byte[])photo.get().getBytes();			 
							    try {
									//convert array of bytes into file				        	
									FileOutputStream fileOuputStream =new FileOutputStream(targetPhotoURI); 
									fileOuputStream.write(bFile);
									fileOuputStream.close();						   
							    }catch(Exception e){
							    	e.printStackTrace();
							    } 								
							}								
							participantBasicInfoVO.setHasFacePhoto(true);
						}else{
							participantBasicInfoVO.setHasFacePhoto(false);
						}						
						participantBasicInfoVO.setUserId(participantUid.trim());						
						ServiceResourceHolder.getPaticipantBasicInfoCache().put(new Element(cacheKeyString, participantBasicInfoVO));
				    	break;	
				    }
				}				
				searchCursor.close();
				if(!isPoolConnection){
					currentConnection.unBind();
					currentConnection.close();			
				}else{
					ServiceResourceHolder.getLdapConnectionPool().releaseConnection(currentConnection);
				}				
				return participantBasicInfoVO;				
			}catch(LdapException e){
				e.printStackTrace();
			}catch(Exception e){
				e.printStackTrace();
			}
			return null;
		}
	}	
	
	public static ParticipantDetailInfoVO enableParticipant(String participantScope,String participantUid){		
		String cacheKeyString=participantScope+facePhotoDivStr+participantUid.trim();							
		try{
			boolean isPoolConnection;
			LdapConnection currentConnection;	
			if(ServiceResourceHolder.getLdapConnectionPool()!=null){			
				currentConnection= ServiceResourceHolder.getLdapConnectionPool().getConnection();
				isPoolConnection=true;				
			}else{
				currentConnection=getLdapConnection();
				isPoolConnection=false;
			}							
			SearchRequest req = new SearchRequestImpl();
			req.setScope( SearchScope.ONELEVEL );
			req.addAttributes( "*" );
			req.setTimeLimit(0);
			req.setBase( new Dn(LDAP_PARTICIPANT_SEARCHNODE_OU+"o="+participantScope.trim()+","+LDAP_PARTICIPANT_SEARCHBASE_DN) );
			req.setFilter( "("+PARTICIPANT_PROPERTY_UID+"="+participantUid.trim()+")" );
			// Process the request
			SearchCursor searchCursor = currentConnection.search(req);
				
			//searchCursor.			
			ParticipantDetailInfoVO participantDetailInfoVO=null;
			while (searchCursor.next()){
			    Response response = searchCursor.get();
			    // process the SearchResultEntry
			    if (response instanceof SearchResultEntry){
			    	Entry resultEntry = ((SearchResultEntry)response).getEntry();		
			    	Modification modification; 				    	
					Attribute statusToChange=resultEntry.get(PARTICIPANT_PROPERTY_USERSTATUS);
					if(statusToChange!=null){							
						modification=new DefaultModification(ModificationOperation.REPLACE_ATTRIBUTE,PARTICIPANT_PROPERTY_USERSTATUS,USER_STATUS_ACTIVE);
						currentConnection.modify(resultEntry.getDn(), modification);							
					}else{							
						resultEntry.add(PARTICIPANT_PROPERTY_USERSTATUS, USER_STATUS_ACTIVE);							
					}	
					
			    	participantDetailInfoVO=new ParticipantDetailInfoVO();
			    	participantDetailInfoVO.setActiveUser(true);
					Attribute roleType=resultEntry.get(PARTICIPANT_PROPERTY_ROLETYPE);						
					if(roleType!=null){
						participantDetailInfoVO.setRoleType(roleType.getString());
					}
					List<String> allowedFeatureCategoriesList=new ArrayList<String>();
					Attribute allowedFeatureCategories=resultEntry.get(PARTICIPANT_PROPERTY_FEATURECATEGORIES);
					if(allowedFeatureCategories!=null){
						Iterator<Value<?>> valueItet=allowedFeatureCategories.iterator();
						if(valueItet!=null){
							while(valueItet.hasNext()){
								Value<?> currentValue=valueItet.next();
								allowedFeatureCategoriesList.add(currentValue.getString());
							}
						}
					}
					participantDetailInfoVO.setAllowedFeatureCategories(allowedFeatureCategoriesList);
					Attribute description=resultEntry.get(PARTICIPANT_PROPERTY_DESCRIPTION);
					if(description!=null){
						participantDetailInfoVO.setDescription(description.getString());	
					}										
					Attribute displayName=resultEntry.get(PARTICIPANT_PROPERTY_DISPLAYNAME);
					if(displayName!=null){
						participantDetailInfoVO.setDisplayName(displayName.getString());
					}
					Attribute email=resultEntry.get(PARTICIPANT_PROPERTY_EMAILADDRESS);
					if(email!=null){
						participantDetailInfoVO.setEmailAddress(email.getString());
					}						
					Attribute mobile=resultEntry.get(PARTICIPANT_PROPERTY_MOBILEPHONE);
					if(mobile!=null){
						participantDetailInfoVO.setMobilePhone(mobile.getString());
					}											
					Attribute postalCode=resultEntry.get(PARTICIPANT_PROPERTY_POSTALCODE);
					if(postalCode!=null){
						participantDetailInfoVO.setPostalCode(postalCode.getString());
					}						
					Attribute street=resultEntry.get(PARTICIPANT_PROPERTY_ADDRESS);
					if(street!=null){
						participantDetailInfoVO.setAddress(street.getString());
					}						
					Attribute telephoneNumber=resultEntry.get(PARTICIPANT_PROPERTY_FIXEDPHONE);
					if(telephoneNumber!=null){
						participantDetailInfoVO.setFixedPhone(telephoneNumber.getString());
					}						
					Attribute title=resultEntry.get(PARTICIPANT_PROPERTY_TITLE);
					if(title!=null){
						participantDetailInfoVO.setTitle(title.getString());		
					}	
					Attribute photo=resultEntry.get(PARTICIPANT_PROPERTY_FACEPHOTO);						
					if(photo!=null&&photo.getBytes()!=null){							
						String userFacePhotoStorePath=RuntimeEnvironmentHandler.getApplicationRootPath()+BINARYFILE_TEMPSTORAGE_FOLDER+"/"+
								BINARYFILE_PARTICIPANT_FACEPHOTO_FOLDER+"/";
						String facePhotoFileName=participantScope+facePhotoDivStr+participantUid.trim();
						String targetPhotoURI=userFacePhotoStorePath+facePhotoFileName;							
						File targetFile=new File(targetPhotoURI);
						if(!targetFile.exists()){	
							byte[] bFile = (byte[])photo.get().getBytes();			 
						    try {
								//convert array of bytes into file				        	
								FileOutputStream fileOuputStream =new FileOutputStream(targetPhotoURI); 
								fileOuputStream.write(bFile);
								fileOuputStream.close();						   
						    }catch(Exception e){
						    	e.printStackTrace();
						    } 								
						}								
						participantDetailInfoVO.setHasFacePhoto(true);
					}else{
						participantDetailInfoVO.setHasFacePhoto(false);
					}				
					participantDetailInfoVO.setUserId(participantUid.trim());	
					participantDetailInfoVO.setParticipantScope(participantScope);
					ServiceResourceHolder.getPaticipantDetailInfoCache().put(new Element(cacheKeyString, participantDetailInfoVO));
			    	break;	
			    }
			}				
			searchCursor.close();
			if(!isPoolConnection){
				currentConnection.unBind();
				currentConnection.close();			
			}else{
				ServiceResourceHolder.getLdapConnectionPool().releaseConnection(currentConnection);
			}				
			return participantDetailInfoVO;				
		}catch(LdapException e){
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;		
	}	
	
	public static ParticipantDetailInfoVO disableParticipant(String participantScope,String participantUid){		
		String cacheKeyString=participantScope+facePhotoDivStr+participantUid.trim();						
		try{
			boolean isPoolConnection;
			LdapConnection currentConnection;	
			if(ServiceResourceHolder.getLdapConnectionPool()!=null){			
				currentConnection= ServiceResourceHolder.getLdapConnectionPool().getConnection();
				isPoolConnection=true;				
			}else{
				currentConnection=getLdapConnection();
				isPoolConnection=false;
			}				
			SearchRequest req = new SearchRequestImpl();
			req.setScope( SearchScope.ONELEVEL );
			req.addAttributes( "*" );
			req.setTimeLimit(0);
			req.setBase( new Dn(LDAP_PARTICIPANT_SEARCHNODE_OU+"o="+participantScope.trim()+","+LDAP_PARTICIPANT_SEARCHBASE_DN) );
			req.setFilter( "("+PARTICIPANT_PROPERTY_UID+"="+participantUid.trim()+")" );
			// Process the request
			SearchCursor searchCursor = currentConnection.search(req);
				
			//searchCursor.			
			ParticipantDetailInfoVO participantDetailInfoVO=null;
			while (searchCursor.next()){
			    Response response = searchCursor.get();
			    // process the SearchResultEntry
			    if (response instanceof SearchResultEntry){
			    	Entry resultEntry = ((SearchResultEntry)response).getEntry();		
			    	Modification modification; 				    	
					Attribute statusToChange=resultEntry.get(PARTICIPANT_PROPERTY_USERSTATUS);
					if(statusToChange!=null){							
						modification=new DefaultModification(ModificationOperation.REPLACE_ATTRIBUTE,PARTICIPANT_PROPERTY_USERSTATUS,USER_STATUS_DISABLED);
						currentConnection.modify(resultEntry.getDn(), modification);							
					}else{							
						resultEntry.add(PARTICIPANT_PROPERTY_USERSTATUS, USER_STATUS_DISABLED);							
					}						
			    	participantDetailInfoVO=new ParticipantDetailInfoVO();
			    	participantDetailInfoVO.setActiveUser(false);
			    	
					Attribute roleType=resultEntry.get(PARTICIPANT_PROPERTY_ROLETYPE);						
					if(roleType!=null){
						participantDetailInfoVO.setRoleType(roleType.getString());
					}
					List<String> allowedFeatureCategoriesList=new ArrayList<String>();
					Attribute allowedFeatureCategories=resultEntry.get(PARTICIPANT_PROPERTY_FEATURECATEGORIES);
					if(allowedFeatureCategories!=null){
						Iterator<Value<?>> valueItet=allowedFeatureCategories.iterator();
						if(valueItet!=null){
							while(valueItet.hasNext()){
								Value<?> currentValue=valueItet.next();
								allowedFeatureCategoriesList.add(currentValue.getString());
							}
						}
					}
					participantDetailInfoVO.setAllowedFeatureCategories(allowedFeatureCategoriesList);
					Attribute description=resultEntry.get(PARTICIPANT_PROPERTY_DESCRIPTION);
					if(description!=null){
						participantDetailInfoVO.setDescription(description.getString());	
					}										
					Attribute displayName=resultEntry.get(PARTICIPANT_PROPERTY_DISPLAYNAME);
					if(displayName!=null){
						participantDetailInfoVO.setDisplayName(displayName.getString());
					}
					Attribute email=resultEntry.get(PARTICIPANT_PROPERTY_EMAILADDRESS);
					if(email!=null){
						participantDetailInfoVO.setEmailAddress(email.getString());
					}						
					Attribute mobile=resultEntry.get(PARTICIPANT_PROPERTY_MOBILEPHONE);
					if(mobile!=null){
						participantDetailInfoVO.setMobilePhone(mobile.getString());
					}											
					Attribute postalCode=resultEntry.get(PARTICIPANT_PROPERTY_POSTALCODE);
					if(postalCode!=null){
						participantDetailInfoVO.setPostalCode(postalCode.getString());
					}						
					Attribute street=resultEntry.get(PARTICIPANT_PROPERTY_ADDRESS);
					if(street!=null){
						participantDetailInfoVO.setAddress(street.getString());
					}						
					Attribute telephoneNumber=resultEntry.get(PARTICIPANT_PROPERTY_FIXEDPHONE);
					if(telephoneNumber!=null){
						participantDetailInfoVO.setFixedPhone(telephoneNumber.getString());
					}						
					Attribute title=resultEntry.get(PARTICIPANT_PROPERTY_TITLE);
					if(title!=null){
						participantDetailInfoVO.setTitle(title.getString());		
					}								
					Attribute photo=resultEntry.get(PARTICIPANT_PROPERTY_FACEPHOTO);						
					if(photo!=null&&photo.getBytes()!=null){							
						String userFacePhotoStorePath=RuntimeEnvironmentHandler.getApplicationRootPath()+BINARYFILE_TEMPSTORAGE_FOLDER+"/"+
								BINARYFILE_PARTICIPANT_FACEPHOTO_FOLDER+"/";
						String facePhotoFileName=participantScope+facePhotoDivStr+participantUid.trim();
						String targetPhotoURI=userFacePhotoStorePath+facePhotoFileName;							
						File targetFile=new File(targetPhotoURI);
						if(!targetFile.exists()){	
							byte[] bFile = (byte[])photo.get().getBytes();			 
						    try {
								//convert array of bytes into file				        	
								FileOutputStream fileOuputStream =new FileOutputStream(targetPhotoURI); 
								fileOuputStream.write(bFile);
								fileOuputStream.close();						   
						    }catch(Exception e){
						    	e.printStackTrace();
						    } 								
						}								
						participantDetailInfoVO.setHasFacePhoto(true);
					}else{
						participantDetailInfoVO.setHasFacePhoto(false);
					}				
					participantDetailInfoVO.setUserId(participantUid.trim());	
					participantDetailInfoVO.setParticipantScope(participantScope);
					ServiceResourceHolder.getPaticipantDetailInfoCache().put(new Element(cacheKeyString, participantDetailInfoVO));						
			    	break;	
			    }
			}				
			searchCursor.close();
			if(!isPoolConnection){
				currentConnection.unBind();
				currentConnection.close();			
			}else{
				ServiceResourceHolder.getLdapConnectionPool().releaseConnection(currentConnection);
			}				
			return participantDetailInfoVO;				
		}catch(LdapException e){
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;		
	}	
	
	public static File getParticipantFacePhoto(String participantScope,String participantUid){			
		String userFacePhotoStorePath=RuntimeEnvironmentHandler.getApplicationRootPath()+BINARYFILE_TEMPSTORAGE_FOLDER+"/"+
				BINARYFILE_PARTICIPANT_FACEPHOTO_FOLDER+"/";				
		String userFacePhotoFileName=participantScope+facePhotoDivStr+participantUid.trim();		
		String targetPhotoURI=userFacePhotoStorePath+userFacePhotoFileName;
		File targetFile=new File(targetPhotoURI);		
		if(targetFile.exists()&&targetFile.isFile()){
			return targetFile;			
		}else{			
			ParticipantBasicInfoVO participantBasicInfoVO=getParticipantBasicInfo(participantScope,participantUid);			
			if(participantBasicInfoVO==null||!participantBasicInfoVO.getHasFacePhoto()){
				return new File(defaultUserFacePhotoPath);				
			}			
			try{
				boolean isPoolConnection;
				LdapConnection currentConnection;
				if(ServiceResourceHolder.getLdapConnectionPool()!=null){			
					currentConnection= ServiceResourceHolder.getLdapConnectionPool().getConnection();
					isPoolConnection=true;				
				}else{
					currentConnection=getLdapConnection();
					isPoolConnection=false;				
				}
				//currentConnection.loadSchema();			
				SearchRequest req = new SearchRequestImpl();
				req.setScope( SearchScope.ONELEVEL );
				req.addAttributes( "*" );
				req.setTimeLimit(0);
				req.setBase( new Dn(LDAP_PARTICIPANT_SEARCHNODE_OU+"o="+participantScope.trim()+","+LDAP_PARTICIPANT_SEARCHBASE_DN) );
				req.setFilter( "("+PARTICIPANT_PROPERTY_UID+"="+participantUid.trim()+")" );
				// Process the request
				SearchCursor searchCursor = currentConnection.search(req);
				while (searchCursor.next()){
				    Response response = searchCursor.get();
				    // process the SearchResultEntry
				    if(response instanceof SearchResultEntry){
				        Entry resultEntry = ((SearchResultEntry)response).getEntry();	
				        Attribute photoAttribute=resultEntry.get(PARTICIPANT_PROPERTY_FACEPHOTO);
				        if(photoAttribute!=null){
				        	byte[] bFile = (byte[])photoAttribute.get().getBytes();			 
						    try {
								//convert array of bytes into file				        	
								FileOutputStream fileOuputStream =new FileOutputStream(targetPhotoURI); 
								fileOuputStream.write(bFile);
								fileOuputStream.close();						   
						    }catch(Exception e){
						    	e.printStackTrace();
						    } 
				        }
				        break;
				    }   
				}
				searchCursor.close();
				if(!isPoolConnection){
					currentConnection.unBind();
					currentConnection.close();			
				}else{
					ServiceResourceHolder.getLdapConnectionPool().releaseConnection(currentConnection);
				}
				targetFile=new File(targetPhotoURI);
				if(targetFile.exists()&targetFile.isFile()){					
					return targetFile;			
				}
			}catch(LdapException e){
				e.printStackTrace();
			}catch (IOException e) {				
				e.printStackTrace();
			}catch(Exception e){
				e.printStackTrace();
			}			
			return null;			
		}				
	}	
	
	public static ParticipantDetailInfoVO addNewParticipant(ParticipantDetailInfoVO participantDetailInfoVO){
		String participantId=participantDetailInfoVO.getUserId();
		String participantScope=participantDetailInfoVO.getParticipantScope();		
		if(participantId==null||participantScope==null){
			return null;
		}		
		try{
			boolean isPoolConnection;
			LdapConnection currentConnection;
			if(ServiceResourceHolder.getLdapConnectionPool()!=null){			
				currentConnection= ServiceResourceHolder.getLdapConnectionPool().getConnection();
				isPoolConnection=true;				
			}else{
				currentConnection=getLdapConnection();
				isPoolConnection=false;				
			}
			String parentDN=LDAP_PARTICIPANT_SEARCHNODE_OU+"o="+participantScope.trim()+","+LDAP_PARTICIPANT_SEARCHBASE_DN;
			String newParticipantDN="cn="+participantId+","+parentDN;			
			//String dn="cn=wyc,ou=person,o=viewfunction_inc,dc=example,dc=com";		
			DefaultEntry newParticipantEntry= new DefaultEntry( 
					newParticipantDN,      // The Dn
		            "ObjectClass: top",
		            "ObjectClass: userPropertyCollection",
		            "cn", participantId,   // Note : there is no ':' when using a variable
		            "sn", participantId
		            ) ;
			
			Attribute userPasswordAttribute = new DefaultAttribute(PARTICIPANT_PROPERTY_PASSWORD, participantId);			
			newParticipantEntry.add(userPasswordAttribute);	
			Attribute userStatusAttribute = new DefaultAttribute(PARTICIPANT_PROPERTY_USERSTATUS, USER_STATUS_ACTIVE);			
			newParticipantEntry.add(userStatusAttribute);	
			
			participantDetailInfoVO.setActiveUser(true);
			participantDetailInfoVO.setHasFacePhoto(false);
			
			Attribute userAddressAttribute;
			if(participantDetailInfoVO.getAddress()!=null&&!participantDetailInfoVO.getAddress().equals("")){
				userAddressAttribute = new DefaultAttribute(PARTICIPANT_PROPERTY_ADDRESS, participantDetailInfoVO.getAddress());				
			}else{
				userAddressAttribute = new DefaultAttribute(PARTICIPANT_PROPERTY_ADDRESS, " ");
			}			
			newParticipantEntry.add(userAddressAttribute);				
			
			Attribute userDescriptionAttribute;
			if(participantDetailInfoVO.getDescription()!=null&&!participantDetailInfoVO.getDescription().equals("")){
				userDescriptionAttribute = new DefaultAttribute(PARTICIPANT_PROPERTY_DESCRIPTION, participantDetailInfoVO.getDescription());
					
			}else{
				userDescriptionAttribute = new DefaultAttribute(PARTICIPANT_PROPERTY_DESCRIPTION, " ");
			}			
			newParticipantEntry.add(userDescriptionAttribute);			
			
			Attribute userDisplayNameAttribute;
			if(participantDetailInfoVO.getDisplayName()!=null&&!participantDetailInfoVO.getDisplayName().equals("")){
				userDisplayNameAttribute = new DefaultAttribute(PARTICIPANT_PROPERTY_DISPLAYNAME, participantDetailInfoVO.getDisplayName());					
			}else{
				userDisplayNameAttribute = new DefaultAttribute(PARTICIPANT_PROPERTY_DISPLAYNAME, " ");
			}
			newParticipantEntry.add(userDisplayNameAttribute);			
			
			Attribute userEmailAttribute;
			if(participantDetailInfoVO.getEmailAddress()!=null&&!participantDetailInfoVO.getEmailAddress().equals("")){
				userEmailAttribute = new DefaultAttribute(PARTICIPANT_PROPERTY_EMAILADDRESS, participantDetailInfoVO.getEmailAddress());				
			}else{
				userEmailAttribute = new DefaultAttribute(PARTICIPANT_PROPERTY_EMAILADDRESS, " ");
			}				
			newParticipantEntry.add(userEmailAttribute);				
			
			Attribute userFixedPhoneAttribute ;
			if(participantDetailInfoVO.getFixedPhone()!=null&&!participantDetailInfoVO.getFixedPhone().equals("")){
				userFixedPhoneAttribute = new DefaultAttribute(PARTICIPANT_PROPERTY_FIXEDPHONE, participantDetailInfoVO.getFixedPhone());					
			}else{
				userFixedPhoneAttribute = new DefaultAttribute(PARTICIPANT_PROPERTY_FIXEDPHONE, "0");
			}
			newParticipantEntry.add(userFixedPhoneAttribute);			
			
			Attribute userMobilePhoneAttribute ;
			if(participantDetailInfoVO.getMobilePhone()!=null&&!participantDetailInfoVO.getMobilePhone().equals("")){
				userMobilePhoneAttribute = new DefaultAttribute(PARTICIPANT_PROPERTY_MOBILEPHONE, participantDetailInfoVO.getMobilePhone());					
			}else{
				userMobilePhoneAttribute = new DefaultAttribute(PARTICIPANT_PROPERTY_MOBILEPHONE, "0");
			}
			newParticipantEntry.add(userMobilePhoneAttribute);
			
			Attribute userPostcodeAttribute;
			if(participantDetailInfoVO.getPostalCode()!=null&&!participantDetailInfoVO.getPostalCode().equals("")){
				userPostcodeAttribute = new DefaultAttribute(PARTICIPANT_PROPERTY_POSTALCODE, participantDetailInfoVO.getPostalCode());				
			}else{
				userPostcodeAttribute = new DefaultAttribute(PARTICIPANT_PROPERTY_POSTALCODE, "000000");
			}
			newParticipantEntry.add(userPostcodeAttribute);	
			
			Attribute userRoleTypeAttribute;
			if(participantDetailInfoVO.getRoleType()!=null&&!participantDetailInfoVO.getRoleType().equals("")){
				userRoleTypeAttribute = new DefaultAttribute(PARTICIPANT_PROPERTY_ROLETYPE, participantDetailInfoVO.getRoleType());					
			}else{
				userRoleTypeAttribute = new DefaultAttribute(PARTICIPANT_PROPERTY_ROLETYPE, " ");
			}
			newParticipantEntry.add(userRoleTypeAttribute);		
			
			if(participantDetailInfoVO.getAllowedFeatureCategories()!=null){
				for(String currentAllowedFeature:participantDetailInfoVO.getAllowedFeatureCategories()){
					if(!currentAllowedFeature.equals("")){
						Attribute currentFeatureAttribute = new DefaultAttribute(PARTICIPANT_PROPERTY_FEATURECATEGORIES, currentAllowedFeature);
						newParticipantEntry.add(currentFeatureAttribute);	
					}
				}
			}
			
			Attribute userTitleAttribute ;
			if(participantDetailInfoVO.getTitle()!=null&&!participantDetailInfoVO.getTitle().equals("")){
				userTitleAttribute = new DefaultAttribute(PARTICIPANT_PROPERTY_TITLE, participantDetailInfoVO.getTitle());					
			}else{
				userTitleAttribute = new DefaultAttribute(PARTICIPANT_PROPERTY_TITLE, " ");
			}
			newParticipantEntry.add(userTitleAttribute);
			
			currentConnection.add( newParticipantEntry);	
			if(!isPoolConnection){
				currentConnection.unBind();
				currentConnection.close();			
			}else{
				ServiceResourceHolder.getLdapConnectionPool().releaseConnection(currentConnection);
			}	
			
			boolean syncAddNewParticipantinActivitySpaceFlag=Boolean.parseBoolean(SYNC_ADDNEW_PARTICIPANT_IN_ACTIVITYSPACE);
			if(syncAddNewParticipantinActivitySpaceFlag){
				UserBasicInfoVO targetUserBasicInfoVO=new UserBasicInfoVO();
				targetUserBasicInfoVO.setUserId(participantDetailInfoVO.getUserId());
				targetUserBasicInfoVO.setUserDisplayName(participantDetailInfoVO.getDisplayName());
				UserManagementServiceRESTClient.syncAddNewParticipant(participantScope, targetUserBasicInfoVO);
				
			}
			return participantDetailInfoVO;				
		}catch(LdapException e){
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}
		return participantDetailInfoVO;
	}
	
	private static LdapConnection getLdapConnection(){
		int ldapServerPort=Integer.parseInt(LDAP_SERVER_PORT);
		LdapConnection connection = new LdapNetworkConnection( LDAP_SERVER_ADDRESS, ldapServerPort);
		try {
			connection.bind(LDAP_ADMIN_DN,LDAP_ADMIN_PASSWORD);
			return connection;
		} catch (LdapException e) {			
			e.printStackTrace();
		}
		return null;
	}
}