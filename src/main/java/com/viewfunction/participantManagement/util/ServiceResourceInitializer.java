package com.viewfunction.participantManagement.util;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.directory.api.ldap.codec.api.DefaultConfigurableBinaryAttributeDetector;
import org.apache.directory.ldap.client.api.DefaultLdapConnectionFactory;
import org.apache.directory.ldap.client.api.DefaultPoolableLdapConnectionFactory;
import org.apache.directory.ldap.client.api.LdapConnectionConfig;
import org.apache.directory.ldap.client.api.LdapConnectionPool;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.PersistenceConfiguration;
import net.sf.ehcache.config.PersistenceConfiguration.Strategy;
import net.sf.ehcache.store.MemoryStoreEvictionPolicy;

@WebListener
public class ServiceResourceInitializer implements ServletContextListener{
	
	private static String LDAP_SERVER_ADDRESS= PropertyHandler.getPerportyValue(PropertyHandler.LDAP_SERVER_ADDRESS).trim();
	private static String LDAP_SERVER_PORT= PropertyHandler.getPerportyValue(PropertyHandler.LDAP_SERVER_PORT).trim();
	private static String LDAP_ADMIN_DN= PropertyHandler.getPerportyValue(PropertyHandler.LDAP_ADMIN_DN).trim();
	private static String LDAP_ADMIN_PASSWORD= PropertyHandler.getPerportyValue(PropertyHandler.LDAP_ADMIN_PASSWORD).trim();
	private static String AUTHORIZATION_TIMEOUT_IN_MINUTES= PropertyHandler.getPerportyValue(PropertyHandler.AUTHORIZATION_TIMEOUT_IN_MINUTES).trim();
	
	private LdapConnectionPool _LDAPConnectionPool; 
	private CacheManager _CacheManager;
	private static final String PARTICIPANT_BASICINFO_CACHE_NAME="PARTICIPANT_BASICINFO_CACHE_NAME";	
	private static final String PARTICIPANT_DETAILINFO_CACHE_NAME="PARTICIPANT_DETAILINFO_CACHE_NAME";
	private static final String AUTHORIZATION_RECORD_CACHE_NAME="AUTHORIZATION_RECORD_CACHE_NAME";
	
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		//shutdown LDAPConnection pool
		try {
			_LDAPConnectionPool.close();
		} catch (Exception e) {			
			e.printStackTrace();
		}
		//shutdown encache
		_CacheManager.shutdown();
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		//init LDAP Connection pool
		int ldapServerPort=Integer.parseInt(LDAP_SERVER_PORT);
		LdapConnectionConfig config = new LdapConnectionConfig();
		config.setLdapHost(LDAP_SERVER_ADDRESS);
		config.setLdapPort(ldapServerPort);
		config.setName(LDAP_ADMIN_DN);
		config.setCredentials(LDAP_ADMIN_PASSWORD);		
		DefaultConfigurableBinaryAttributeDetector defaultConfigurableBinaryAttributeDetector=new DefaultConfigurableBinaryAttributeDetector();		
		config.setBinaryAttributeDetector(defaultConfigurableBinaryAttributeDetector);

		DefaultLdapConnectionFactory factory = new DefaultLdapConnectionFactory( config );
		// optional, values below are defaults
		GenericObjectPool.Config poolConfig = new GenericObjectPool.Config();
		/*
		poolConfig.lifo = true;
		poolConfig.maxActive = 8;
		poolConfig.maxIdle = 8;
		poolConfig.maxWait = -1L;
		poolConfig.minEvictableIdleTimeMillis = 1000L * 60L * 30L;
		poolConfig.minIdle = 0;
		poolConfig.numTestsPerEvictionRun = 3;
		poolConfig.softMinEvictableIdleTimeMillis = -1L;
		poolConfig.testOnBorrow = false;
		poolConfig.testOnReturn = false;
		poolConfig.testWhileIdle = false;
		poolConfig.timeBetweenEvictionRunsMillis = -1L;
		poolConfig.whenExhaustedAction = GenericObjectPool.WHEN_EXHAUSTED_BLOCK;
		*/
		_LDAPConnectionPool = new LdapConnectionPool(new DefaultPoolableLdapConnectionFactory( factory ), poolConfig );
		_LDAPConnectionPool.setTestOnBorrow( true );
		ServiceResourceHolder.setLdapConnectionPool(_LDAPConnectionPool);		
		//init encache		
		_CacheManager=CacheManager.getInstance();		
		Cache paticipantBasicInfoCache = new Cache(
				  new CacheConfiguration(PARTICIPANT_BASICINFO_CACHE_NAME, 10000)
				    .memoryStoreEvictionPolicy(MemoryStoreEvictionPolicy.LFU)
				    .eternal(false)
				    .timeToLiveSeconds(86400)
				    .timeToIdleSeconds(60000)
				    .diskExpiryThreadIntervalSeconds(0)
				    .persistence(new PersistenceConfiguration().strategy(Strategy.LOCALTEMPSWAP)));		
		_CacheManager.addCache(paticipantBasicInfoCache);
		paticipantBasicInfoCache=_CacheManager.getCache(PARTICIPANT_BASICINFO_CACHE_NAME);
		ServiceResourceHolder.setPaticipantBasicInfoCache(paticipantBasicInfoCache);
		Cache paticipantDetailInfoCache = new Cache(
				  new CacheConfiguration(PARTICIPANT_DETAILINFO_CACHE_NAME, 10000)
				    .memoryStoreEvictionPolicy(MemoryStoreEvictionPolicy.LFU)
				    .eternal(false)
				    .timeToLiveSeconds(86400)
				    .timeToIdleSeconds(60000)
				    .diskExpiryThreadIntervalSeconds(0)
				    .persistence(new PersistenceConfiguration().strategy(Strategy.LOCALTEMPSWAP)));		
		_CacheManager.addCache(paticipantDetailInfoCache);
		paticipantDetailInfoCache=_CacheManager.getCache(PARTICIPANT_DETAILINFO_CACHE_NAME);
		ServiceResourceHolder.setPaticipantDetailInfoCache(paticipantDetailInfoCache);	
		Cache authorizationRecordCache = new Cache(
				  new CacheConfiguration(AUTHORIZATION_RECORD_CACHE_NAME, 10000)
				    .memoryStoreEvictionPolicy(MemoryStoreEvictionPolicy.LFU)
				    .eternal(false)
				    .timeToLiveSeconds(86400)
				    .timeToIdleSeconds(60000)
				    .diskExpiryThreadIntervalSeconds(0)
				    .persistence(new PersistenceConfiguration().strategy(Strategy.LOCALTEMPSWAP)));		
		_CacheManager.addCache(authorizationRecordCache);
		authorizationRecordCache=_CacheManager.getCache(AUTHORIZATION_RECORD_CACHE_NAME);
		ServiceResourceHolder.setAuthorizationRecordCache(authorizationRecordCache);
		
		int authorizationTimeoutInMinuteValue=Integer.parseInt(AUTHORIZATION_TIMEOUT_IN_MINUTES);
		ServiceResourceHolder.setAuthorizationTimeoutInMinutes(authorizationTimeoutInMinuteValue);
	}
}