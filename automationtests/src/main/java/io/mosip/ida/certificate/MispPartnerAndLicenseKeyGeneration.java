package io.mosip.ida.certificate;

import java.util.HashMap;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import io.mosip.admin.fw.util.AdminTestUtil;
import io.mosip.authentication.fw.util.RestClient;
import io.mosip.global.utils.GlobalConstants;
import io.mosip.kernel.util.ConfigManager;
import io.mosip.service.BaseTestCase;
import io.restassured.response.Response;

public class MispPartnerAndLicenseKeyGeneration extends AdminTestUtil{
	private static final Logger lOGGER = Logger.getLogger(MispPartnerAndLicenseKeyGeneration.class);
	static String localHostUrl = null;
	
	static String address = "Bangalore";
	static String contactNumber = "8553967572";
	static String emailId = "mosip"+timeStamp+"@gmail.com";
	public static String mispOrganizationName = "mosip-" + timeStamp;
	public static String mispPartnerId = mispOrganizationName;
	public static String mispPartnerType = "Misp_Partner";
	static String getPartnerType = "MISP";
	
	public static String getAndUploadCertificatesAndGenerateMispLicKey() {
		if (localHostUrl == null) {
			localHostUrl = getLocalHostUrl();
		}
		
		mispPartnerGeneration();
		JSONObject certificateValue = getCertificates(mispPartnerId, getPartnerType);
		String mispCACertValue = certificateValue.getString("caCertificate");
		lOGGER.info(mispCACertValue);
		String mispInterCertValue = certificateValue.getString("interCertificate");
		lOGGER.info(mispInterCertValue);
		String mispPartnerCertValue = certificateValue.getString("partnerCertificate");
		lOGGER.info(mispPartnerCertValue);
		
		
		uploadCACertificate(mispCACertValue, "Auth");
		uploadIntermediateCertificate(mispInterCertValue, "Auth");
		
		JSONObject mispSignedcertificateValue = uploadPartnerCertificate(mispPartnerCertValue, "Auth", mispPartnerId);
		
		String mispCertValueSigned = mispSignedcertificateValue.getString("signedCertificateData");
		lOGGER.info(mispCertValueSigned);
		uploadSignedCertificate(mispCertValueSigned, getPartnerType);
		String mispLicKey = generateMispLicKey(mispPartnerId);
		lOGGER.info(mispLicKey);
		
		return mispLicKey;
	}
	
	private static String getLocalHostUrl() {
			return ConfigManager.getAuthDemoServiceUrl() + "/";
	}

	public static void mispPartnerGeneration() { 
		String url = ApplnURI + "/v1/partnermanager/partners";
		
		String token = kernelAuthLib.getTokenByRole(GlobalConstants.PARTNER);
		
		HashMap<String, String> requestBody = new HashMap<>();
		
		requestBody.put("address", address);
		requestBody.put("contactNumber", contactNumber);
		requestBody.put("emailId", emailId);
		requestBody.put("organizationName", mispOrganizationName);
		requestBody.put(GlobalConstants.PARTNERID, mispPartnerId);
		requestBody.put(GlobalConstants.PARTNERTYPE, mispPartnerType);
		requestBody.put("policyGroup", policyGroup);
		
		HashMap<String, Object> body = new HashMap<>();
		
		body.put("id", GlobalConstants.STRING);
		body.put(GlobalConstants.METADATA, new HashMap<>());
		body.put(GlobalConstants.REQUEST, requestBody);
		body.put(GlobalConstants.REQUESTTIME, generateCurrentUTCTimeStamp());
		body.put(GlobalConstants.VERSION, GlobalConstants.STRING);
		
		Response response = RestClient.postRequestWithCookie(url, body, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, GlobalConstants.AUTHORIZATION, token);
		lOGGER.info(response);
		JSONObject responseJson = new JSONObject(response.asString());
		lOGGER.info(responseJson);
		JSONObject responseValue = (JSONObject) (responseJson.get(GlobalConstants.RESPONSE));
		lOGGER.info(responseValue);
	}
	
	public static JSONObject getCertificates(String partnerId, String partnerType) {
		String url = localHostUrl + props.getProperty("getPartnerCertURL");
		
		HashMap<String, String> map = new HashMap<>();
		
		map.put("partnerName", partnerId);
		map.put(GlobalConstants.PARTNERTYPE, partnerType);
		map.put("moduleName", BaseTestCase.certsForModule);
		
		Response response = RestClient.getRequestWithQueryParm(url, map, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON);
		lOGGER.info(response);
		JSONObject responseJson = new JSONObject(response.asString());
		lOGGER.info(responseJson);
		
		return responseJson;
	}
	
	public static void uploadCACertificate(String certValueCA, String partnerDomain) {
		String url = ApplnURI + "/v1/partnermanager/partners/certificate/ca/upload";
		
		String token = kernelAuthLib.getTokenByRole(GlobalConstants.PARTNER);
		
		HashMap<String, String> requestBody = new HashMap<>();
		
		requestBody.put(GlobalConstants.CERTIFICATEDATA, certValueCA);
		requestBody.put(GlobalConstants.PARTNERDOMAIN, partnerDomain);
		
		HashMap<String, Object> body = new HashMap<>();
		
		body.put("id", GlobalConstants.STRING);
		body.put(GlobalConstants.METADATA, new HashMap<>());
		body.put(GlobalConstants.REQUEST, requestBody);
		body.put(GlobalConstants.REQUESTTIME, generateCurrentUTCTimeStamp());
		body.put(GlobalConstants.VERSION, GlobalConstants.STRING);
		
		Response response = RestClient.postRequestWithCookie(url, body, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, GlobalConstants.AUTHORIZATION, token);
		
		JSONObject reponseValue = new JSONObject(response.asString());
		lOGGER.info(reponseValue);
	}
	
	public static void uploadIntermediateCertificate(String certValueIntermediate, String partnerDomain) {
		String url = ApplnURI + "/v1/partnermanager/partners/certificate/ca/upload";
		
		String token = kernelAuthLib.getTokenByRole(GlobalConstants.PARTNER);
		
		HashMap<String, String> requestBody = new HashMap<>();
		
		requestBody.put(GlobalConstants.CERTIFICATEDATA, certValueIntermediate);
		requestBody.put(GlobalConstants.PARTNERDOMAIN, partnerDomain);
		
		HashMap<String, Object> body = new HashMap<>();
		
		body.put("id", GlobalConstants.STRING);
		body.put(GlobalConstants.METADATA, new HashMap<>());
		body.put(GlobalConstants.REQUEST, requestBody);
		body.put(GlobalConstants.REQUESTTIME, generateCurrentUTCTimeStamp());
		body.put(GlobalConstants.VERSION, GlobalConstants.STRING);
		
		Response response = RestClient.postRequestWithCookie(url, body, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, GlobalConstants.AUTHORIZATION, token);
		
		JSONObject reponseValue = new JSONObject(response.asString());
		lOGGER.info(reponseValue);
	}
	
	public static JSONObject uploadPartnerCertificate(String certValuePartner, String partnerDomain, String partnerId) {
		String url = ApplnURI + "/v1/partnermanager/partners/certificate/upload";
		
		String token = kernelAuthLib.getTokenByRole(GlobalConstants.PARTNER);
		
		HashMap<String, String> requestBody = new HashMap<>();
		
		requestBody.put(GlobalConstants.CERTIFICATEDATA, certValuePartner);
		requestBody.put(GlobalConstants.PARTNERDOMAIN, partnerDomain);
		requestBody.put(GlobalConstants.PARTNERID, partnerId);
		
		HashMap<String, Object> body = new HashMap<>();
		
		body.put("id", GlobalConstants.STRING);
		body.put(GlobalConstants.METADATA, new HashMap<>());
		body.put(GlobalConstants.REQUEST, requestBody);
		body.put(GlobalConstants.REQUESTTIME, generateCurrentUTCTimeStamp());
		body.put(GlobalConstants.VERSION, GlobalConstants.STRING);
		
		Response response = RestClient.postRequestWithCookie(url, body, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, GlobalConstants.AUTHORIZATION, token);
		lOGGER.info(response);
		
		JSONObject responseJson = new JSONObject(response.asString());
		lOGGER.info(responseJson);
		
		JSONObject responseValue = (JSONObject) responseJson.get(GlobalConstants.RESPONSE);
		lOGGER.info(responseValue);
		
		return responseValue;
	}
	
	public static void uploadSignedCertificate(String certValueSigned, String partnerType) {
		String url = localHostUrl + props.getProperty("uploadSignedCertificateUrl");
		
		HashMap<String, String> requestBody = new HashMap<>();
		
		requestBody.put("certData", certValueSigned);
		
		HashMap<String, Object> queryParamMap = new HashMap<>();
		
		queryParamMap.put(GlobalConstants.PARTNERTYPE, partnerType);
		queryParamMap.put("moduleName", BaseTestCase.certsForModule);
		
		Response response = RestClient.postRequestWithQueryParamsAndBody(url, requestBody, queryParamMap, MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN);
		
		lOGGER.info(response);
	}
	
	public static String generateMispLicKey(String partnerId) {
		String url = ApplnURI + "/v1/partnermanager/misps";
		
		String token = kernelAuthLib.getTokenByRole(GlobalConstants.PARTNER);
		
		HashMap<String, String> requestBody = new HashMap<>();
		
		requestBody.put("providerId", partnerId);
		
		HashMap<String, Object> body = new HashMap<>();
		
		body.put("id", GlobalConstants.STRING);
		body.put(GlobalConstants.METADATA, new HashMap<>());
		body.put(GlobalConstants.REQUEST, requestBody);
		body.put(GlobalConstants.REQUESTTIME, generateCurrentUTCTimeStamp());
		body.put(GlobalConstants.VERSION, GlobalConstants.STRING);
		
		Response response = RestClient.postRequestWithCookie(url, body, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, GlobalConstants.AUTHORIZATION, token);
		
		lOGGER.info(response);
		JSONObject responseJson = new JSONObject(response.asString());
		lOGGER.info(responseJson);
		JSONObject responseValue = (JSONObject) (responseJson.get(GlobalConstants.RESPONSE));
		lOGGER.info(responseValue);
		String licenseKey = responseValue.getString("licenseKey");
		lOGGER.info(licenseKey);
		
		return licenseKey;
	}
	
}

	
