package com.viewfunction.participantManagement.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class PropertyHandler {
	private static Properties _properties;		
	public static String LDAP_SERVER_ADDRESS="LDAP_SERVER_ADDRESS";
	public static String LDAP_SERVER_PORT="LDAP_SERVER_PORT";
	public static String LDAP_ADMIN_DN="LDAP_ADMIN_DN";
	public static String LDAP_ADMIN_PASSWORD="LDAP_ADMIN_PASSWORD";	
	public static String LDAP_PARTICIPANT_SEARCHBASE_DN="LDAP_PARTICIPANT_SEARCHBASE_DN";
	public static String LDAP_PARTICIPANT_SEARCHNODE_OU="LDAP_PARTICIPANT_SEARCHNODE_OU";
	public static String BINARYFILE_TEMPSTORAGE_FOLDER="BINARYFILE_TEMPSTORAGE_FOLDER";
	public static String BINARYFILE_PARTICIPANT_FACEPHOTO_FOLDER="BINARYFILE_PARTICIPANT_FACEPHOTO_FOLDER";
	public static String BINARYFILE_UPLOADING_TEMPFILE_FOLDER="BINARYFILE_UPLOADING_TEMPFILE_FOLDER";	
	public static String BUSINESS_BINARYFILE_FOLDER="BUSINESS_BINARYFILE_FOLDER";
	public static String DEFAULT_USERFACEPHOTO_BLUE="DEFAULT_USERFACEPHOTO_BLUE";
	public static String DEFAULT_USERFACEPHOTO_GREEN="DEFAULT_USERFACEPHOTO_GREEN";
	//participant properties
	public static String PARTICIPANT_PROPERTY_UID="PARTICIPANT_PROPERTY_UID";
	public static String PARTICIPANT_PROPERTY_ROLETYPE="PARTICIPANT_PROPERTY_ROLETYPE";
	public static String PARTICIPANT_PROPERTY_DESCRIPTION="PARTICIPANT_PROPERTY_DESCRIPTION";
	public static String PARTICIPANT_PROPERTY_DISPLAYNAME="PARTICIPANT_PROPERTY_DISPLAYNAME";
	public static String PARTICIPANT_PROPERTY_EMAILADDRESS="PARTICIPANT_PROPERTY_EMAILADDRESS";
	public static String PARTICIPANT_PROPERTY_MOBILEPHONE="PARTICIPANT_PROPERTY_MOBILEPHONE";
	public static String PARTICIPANT_PROPERTY_POSTALCODE="PARTICIPANT_PROPERTY_POSTALCODE";
	public static String PARTICIPANT_PROPERTY_ADDRESS="PARTICIPANT_PROPERTY_ADDRESS";
	public static String PARTICIPANT_PROPERTY_FIXEDPHONE="PARTICIPANT_PROPERTY_FIXEDPHONE";
	public static String PARTICIPANT_PROPERTY_TITLE="PARTICIPANT_PROPERTY_TITLE";
	public static String PARTICIPANT_PROPERTY_FACEPHOTO="PARTICIPANT_PROPERTY_FACEPHOTO";
	public static String PARTICIPANT_PROPERTY_PASSWORD="PARTICIPANT_PROPERTY_PASSWORD";
	public static String PARTICIPANT_PROPERTY_USERSTATUS="PARTICIPANT_PROPERTY_USERSTATUS";	
	public static String PARTICIPANT_PROPERTY_FEATURECATEGORIES="PARTICIPANT_PROPERTY_FEATURECATEGORIES";	
	//authorization properties
	public static String AUTHORIZATION_TIMEOUT_IN_MINUTES="AUTHORIZATION_TIMEOUT_IN_MINUTES";	
	//System data sync
	public static String SYNC_ADDNEW_PARTICIPANT_IN_ACTIVITYSPACE="SYNC_ADDNEW_PARTICIPANT_IN_ACTIVITYSPACE";
	
	public static String getPerportyValue(String resourceFileName){	
		if(_properties==null){
			_properties=new Properties();
			try {			
				_properties.load(new FileInputStream(RuntimeEnvironmentHandler.getApplicationRootPath()+"ServiceConfig.properties"));
			} catch (FileNotFoundException e) {
				e.printStackTrace();			
			} catch (IOException e) {
				e.printStackTrace();
			}		
		}		
		return _properties.getProperty(resourceFileName);
	}	
}