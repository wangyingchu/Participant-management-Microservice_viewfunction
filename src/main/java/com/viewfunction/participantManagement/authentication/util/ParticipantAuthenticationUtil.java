package com.viewfunction.participantManagement.authentication.util;

import java.util.Date;

import net.sf.ehcache.Element;

import org.apache.directory.api.ldap.model.cursor.SearchCursor;
import org.apache.directory.api.ldap.model.entry.Attribute;
import org.apache.directory.api.ldap.model.entry.Entry;
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

import com.viewfunction.participantManagement.authentication.restful.AuthorizationRecordVO;
import com.viewfunction.participantManagement.authentication.restful.ParticipantLoginInfoVO;
import com.viewfunction.participantManagement.authentication.restful.ParticipantLoginResultVO;
import com.viewfunction.participantManagement.operation.restful.ParticipantBasicInfoVO;
import com.viewfunction.participantManagement.operation.util.ParticipantOperationUtil;
import com.viewfunction.participantManagement.util.PerportyHandler;
import com.viewfunction.participantManagement.util.ServiceResourceHolder;

public class ParticipantAuthenticationUtil {
	private static String LDAP_SERVER_ADDRESS=PerportyHandler.getPerportyValue(PerportyHandler.LDAP_SERVER_ADDRESS).trim();
	private static String LDAP_SERVER_PORT=PerportyHandler.getPerportyValue(PerportyHandler.LDAP_SERVER_PORT).trim();
	private static String LDAP_ADMIN_DN=PerportyHandler.getPerportyValue(PerportyHandler.LDAP_ADMIN_DN).trim();
	private static String LDAP_ADMIN_PASSWORD=PerportyHandler.getPerportyValue(PerportyHandler.LDAP_ADMIN_PASSWORD).trim();
	private static String LDAP_PARTICIPANT_SEARCHBASE_DN=PerportyHandler.getPerportyValue(PerportyHandler.LDAP_PARTICIPANT_SEARCHBASE_DN).trim();	
	private static String LDAP_PARTICIPANT_SEARCHNODE_OU=PerportyHandler.getPerportyValue(PerportyHandler.LDAP_PARTICIPANT_SEARCHNODE_OU).trim();	
	//participant properties
	private static String PARTICIPANT_PROPERTY_UID=PerportyHandler.getPerportyValue(PerportyHandler.PARTICIPANT_PROPERTY_UID).trim();
	private static String PARTICIPANT_PROPERTY_PASSWORD=PerportyHandler.getPerportyValue(PerportyHandler.PARTICIPANT_PROPERTY_PASSWORD).trim();	
	private static String PARTICIPANT_PROPERTY_USERSTATUS=PerportyHandler.getPerportyValue(PerportyHandler.PARTICIPANT_PROPERTY_USERSTATUS).trim();
	private static String USER_STATUS_ACTIVE="ACTIVE";
	
	public static ParticipantLoginResultVO participantPasswordVerify(ParticipantLoginInfoVO participantLoginInfoVO){
		String participantId=participantLoginInfoVO.getParticipantID().trim();
		String participantScope=participantLoginInfoVO.getParticipantScope();
		ParticipantLoginResultVO participantLoginResultVO=new ParticipantLoginResultVO();		
		participantLoginResultVO.setLoginAuthenticateResult(false);
		participantLoginResultVO.setParticipantID(participantLoginInfoVO.getParticipantID());
		participantLoginResultVO.setParticipantDisplayName("NOTLOGINYET");		
		participantLoginResultVO.setParticipantScope(participantLoginInfoVO.getParticipantScope());
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
			    	Attribute password=resultEntry.get(PARTICIPANT_PROPERTY_PASSWORD);			    	
					if(password!=null){						
						byte[] passedInPlainPwd = participantLoginInfoVO.getParticipantPWD().getBytes();						
						byte[] currentPassword=password.getBytes();
						boolean currentPasswordCheckResult=PasswordUtil.compareCredentials( passedInPlainPwd, currentPassword);
						if(currentPasswordCheckResult){
							Attribute status=resultEntry.get(PARTICIPANT_PROPERTY_USERSTATUS);
							if(status!=null){
								if(status.getString().equals(USER_STATUS_ACTIVE)){
									participantLoginResultVO.setLoginAuthenticateResult(true);
									ParticipantBasicInfoVO participantBasicInfoVO=ParticipantOperationUtil.getParticipantBasicInfo(participantLoginInfoVO.getParticipantScope(),participantLoginInfoVO.getParticipantID());
									if(participantBasicInfoVO!=null){
										participantLoginResultVO.setParticipantDisplayName(participantBasicInfoVO.getDisplayName());	
									}
								}else{
									participantLoginResultVO.setLoginAuthenticateResult(false);
								}
							}else{
								participantLoginResultVO.setLoginAuthenticateResult(false);
							}	
						}else{
							participantLoginResultVO.setLoginAuthenticateResult(false);
						}
					}else{
						participantLoginResultVO.setLoginAuthenticateResult(false);		
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
			participantLoginResultVO.setLoginAuthenticateResult(false);	
		}catch(Exception e){
			e.printStackTrace();
			participantLoginResultVO.setLoginAuthenticateResult(false);	
		}		
		return participantLoginResultVO;
	}	
	
	public static boolean setAuthenticateRecord(AuthorizationRecordVO authorizationRecordVO){
		String longinId=authorizationRecordVO.getLoginId();
		String loginIp=authorizationRecordVO.getLoginIP();
		String participantScope=authorizationRecordVO.getParticipantScope();
		authorizationRecordVO.setLoginTimeStamp(new Date().getTime());
		
		String cacheKeyString=participantScope+"_"+longinId+" "+loginIp;
		Element cachedAuthenticateRecordObj = ServiceResourceHolder.getAuthorizationRecordCache().get(cacheKeyString);
		
		if(cachedAuthenticateRecordObj!=null){
			ServiceResourceHolder.getAuthorizationRecordCache().remove(cacheKeyString);
		}
		ServiceResourceHolder.getAuthorizationRecordCache().put(new Element(cacheKeyString, authorizationRecordVO));
		return true;
	}
	
	public static boolean removeAuthenticateRecord(AuthorizationRecordVO authorizationRecordVO){
		String longinId=authorizationRecordVO.getLoginId();
		String loginIp=authorizationRecordVO.getLoginIP();
		String participantScope=authorizationRecordVO.getParticipantScope();
		
		String cacheKeyString=participantScope+"_"+longinId+" "+loginIp;
		Element cachedAuthenticateRecordObj = ServiceResourceHolder.getAuthorizationRecordCache().get(cacheKeyString);		
		if(cachedAuthenticateRecordObj!=null){
			return ServiceResourceHolder.getAuthorizationRecordCache().remove(cacheKeyString);
		}else{
			return true;
		}	
	}
	
	public static boolean verifyAuthenticateRecord(AuthorizationRecordVO authorizationRecordVO){
		String longinId=authorizationRecordVO.getLoginId();
		String loginIp=authorizationRecordVO.getLoginIP();
		String participantScope=authorizationRecordVO.getParticipantScope();
		
		String cacheKeyString=participantScope+"_"+longinId+" "+loginIp;
		Element cachedAuthenticateRecordObj = ServiceResourceHolder.getAuthorizationRecordCache().get(cacheKeyString);
		if(cachedAuthenticateRecordObj==null){
			return false;
		}else{
			AuthorizationRecordVO cachedAuthorizationRecordVO=(AuthorizationRecordVO)cachedAuthenticateRecordObj.getObjectValue();
			long recordSetTime = cachedAuthorizationRecordVO.getLoginTimeStamp();
			long currentTime=new Date().getTime();
			long AUTHORIZATION_TIMEOUT_IN_MINUTES_mSecondValue=ServiceResourceHolder.getAuthorizationTimeoutInMinutes()*60*1000;
			if(currentTime-recordSetTime<AUTHORIZATION_TIMEOUT_IN_MINUTES_mSecondValue){
				return true;
			}else{
				return false;
			}			
		}
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
