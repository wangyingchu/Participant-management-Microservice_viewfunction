package com.viewfunction.participantManagement.util;

import net.sf.ehcache.Cache;

import org.apache.directory.ldap.client.api.LdapConnectionPool;

public class ServiceResourceHolder {
	private static LdapConnectionPool ldapConnectionPool;
	private static Cache paticipantBasicInfoCache;
	private static Cache paticipantDetailInfoCache;
	private static Cache authorizationRecordCache;
	private static int authorizationTimeoutInMinutes=720;
	
	public static LdapConnectionPool getLdapConnectionPool() {
		return ldapConnectionPool;
	}

	public static void setLdapConnectionPool(LdapConnectionPool ldapConnectionPool) {
		ServiceResourceHolder.ldapConnectionPool = ldapConnectionPool;
	}

	public static Cache getPaticipantBasicInfoCache() {
		return paticipantBasicInfoCache;
	}

	public static void setPaticipantBasicInfoCache(Cache paticipantBasicInfoCache) {
		ServiceResourceHolder.paticipantBasicInfoCache = paticipantBasicInfoCache;
	}

	public static Cache getPaticipantDetailInfoCache() {
		return paticipantDetailInfoCache;
	}

	public static void setPaticipantDetailInfoCache(Cache paticipantDetailInfoCache) {
		ServiceResourceHolder.paticipantDetailInfoCache = paticipantDetailInfoCache;
	}

	public static Cache getAuthorizationRecordCache() {
		return authorizationRecordCache;
	}

	public static void setAuthorizationRecordCache(Cache authorizationRecordCache) {
		ServiceResourceHolder.authorizationRecordCache = authorizationRecordCache;
	}

	public static int getAuthorizationTimeoutInMinutes() {
		return authorizationTimeoutInMinutes;
	}

	public static void setAuthorizationTimeoutInMinutes(int authorizationTimeoutInMinutes) {
		ServiceResourceHolder.authorizationTimeoutInMinutes = authorizationTimeoutInMinutes;
	}	
}