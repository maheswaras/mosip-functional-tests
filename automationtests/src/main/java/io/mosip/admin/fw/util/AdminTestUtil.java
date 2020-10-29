package io.mosip.admin.fw.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TimeZone;
import java.util.stream.Collectors;

import javax.ws.rs.core.MediaType;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.Reporter;
import org.yaml.snakeyaml.Yaml;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import io.mosip.authentication.fw.util.ReportUtil;
import io.mosip.authentication.fw.util.RestClient;
import io.mosip.authentication.fw.util.RunConfigUtil;
import io.mosip.kernel.util.KernelDataBaseAccess;
import io.mosip.service.BaseTestCase;
import io.mosip.testrunner.MosipTestRunner;
import io.restassured.response.Response;


/**
 * @author Ravi Kant
 *
 */
public class AdminTestUtil extends BaseTestCase{
	
	private static final Logger logger = Logger.getLogger(AdminTestUtil.class);
	static KernelDataBaseAccess masterDB = new KernelDataBaseAccess();
	String token = null;
	String adminAutoGeneratedIdPropFileName = "/admin/autoGeneratedId.properties";
	String preregAutoGeneratedIdPropFileName = "/preReg/autoGeneratedId.properties";
	String partnerAutoGeneratedIdPropFileName = "/partner/autoGeneratedId.properties";
	
	
	
	/**
	 * This method will hit post request and return the response
	 * @param url
	 * @param jsonInput
	 * @param cookieName
	 * @param role
	 * @return Response
	 */
	protected Response postWithBodyAndCookie(String url, String jsonInput, String cookieName, String role, String testCaseName) {
		Response response=null;
		String inputJson = inputJsonKeyWordHandeler(jsonInput, testCaseName);
		token = kernelAuthLib.getTokenByRole(role);
		logger.info("******Post request Json to EndPointUrl: " + url + " *******");
		Reporter.log("<pre>" + ReportUtil.getTextAreaJsonMsgHtml(inputJson) + "</pre>");
		try {
			  response = RestClient.postRequestWithCookie(url, inputJson, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, cookieName, token);
			  Reporter.log("<b><u>Actual Response Content: </u></b>(EndPointUrl: " + url + ") <pre>"
						+ ReportUtil.getTextAreaJsonMsgHtml(response.asString()) + "</pre>");
			return response;
		} catch (Exception e) {
			logger.error("Exception " + e);
			return response;
		}
	}
	
	protected Response postWithBodyAndCookieForAutoGeneratedId(String url, String jsonInput, String cookieName, String role, String testCaseName, String idKeyName) {
		Response response=null;
		String inputJson = inputJsonKeyWordHandeler(jsonInput, testCaseName);
		token = kernelAuthLib.getTokenByRole(role);
		logger.info("******Post request Json to EndPointUrl: " + url + " *******");
		Reporter.log("<pre>" + ReportUtil.getTextAreaJsonMsgHtml(inputJson) + "</pre>");
		try {
			  response = RestClient.postRequestWithCookie(url, inputJson, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, cookieName, token);
			  Reporter.log("<b><u>Actual Response Content: </u></b>(EndPointUrl: " + url + ") <pre>"
						+ ReportUtil.getTextAreaJsonMsgHtml(response.asString()) + "</pre>");
			  
			  if(testCaseName.toLowerCase().contains("_sid"))
			  {
				  writeAutoGeneratedId(response, idKeyName, testCaseName);
			  }
				  
			return response;
		} catch (Exception e) {
			logger.error("Exception " + e);
			return response;
		}
	}
	
	protected Response postWithFormPathParamAndFile(String url, String jsonInput, String cookieName, String role, String testCaseName) {
		Response response=null;
		String inputJson = inputJsonKeyWordHandeler(jsonInput, testCaseName);
		JSONObject req = new JSONObject(inputJson);
		HashMap<String, String> formParams = new HashMap<String, String>();
		HashMap<String, String> pathParams = new HashMap<String, String>();
		
		File filetoUpload = null;
		String fileKeyName = null;
		if(req.has("filePath") && req.has("fileKeyName") ) {
		 filetoUpload = new File(RunConfigUtil.getResourcePath() + req.get("filePath").toString());
		 req.remove("filePath");
		 fileKeyName = req.get("fileKeyName").toString();
		 req.remove("fileKeyName");
		}
		else 
			logger.error("request doesn't contanin filePath and fileKeyName: "+inputJson);
		pathParams.put("preRegistrationId", req.get("preRegistrationId").toString());
		req.remove("preRegistrationId");
		formParams.put("Document request", req.toString());
		token = kernelAuthLib.getTokenByRole(role);
		logger.info("******Post request Json to EndPointUrl: " + url + " *******");
		Reporter.log("<pre>" + ReportUtil.getTextAreaJsonMsgHtml(inputJson) + "</pre>");
		try {
			response = RestClient.postWithFormPathParamAndFile(url, formParams, pathParams, filetoUpload, fileKeyName,
						MediaType.MULTIPART_FORM_DATA, token);
			  Reporter.log("<b><u>Actual Response Content: </u></b>(EndPointUrl: " + url + ") <pre>"
						+ ReportUtil.getTextAreaJsonMsgHtml(response.asString()) + "</pre>");
			return response;
		} catch (Exception e) {
			logger.error("Exception " + e);
			return response;
		}
	}
	
	/**
	 * This method will hit put request and return the response
	 * @param url
	 * @param jsonInput
	 * @param cookieName
	 * @param role
	 * @return Response
	 */
	protected Response putWithBodyAndCookie(String url, String jsonInput, String cookieName, String role, String testCaseName) {
		Response response=null;
		String inputJson = inputJsonKeyWordHandeler(jsonInput, testCaseName);
		token = kernelAuthLib.getTokenByRole(role);
		logger.info("******Put request Json to EndPointUrl: " + url + " *******");
		Reporter.log("<pre>" + ReportUtil.getTextAreaJsonMsgHtml(inputJson) + "</pre>");
		try {
			  response = RestClient.putRequestWithCookie(url, inputJson, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, cookieName, token);
			  Reporter.log("<b><u>Actual Response Content: </u></b>(EndPointUrl: " + url + ") <pre>"
						+ ReportUtil.getTextAreaJsonMsgHtml(response.asString()) + "</pre>");
			return response;
		} catch (Exception e) {
			logger.error("Exception " + e);
			return response;
		}
	}
	
	protected Response putWithPathParamAndCookie(String url, String jsonInput, String cookieName, String role, String testCaseName) {
		Response response=null;
		String inputJson = inputJsonKeyWordHandeler(jsonInput, testCaseName);
		HashMap<String, String> map = null;
		try {
			map = new Gson().fromJson(jsonInput, new TypeToken<HashMap<String, String>>(){}.getType());
		} catch (Exception e) {
			logger.error("Not able to convert jsonrequet to map: "+jsonInput+" Exception: "+e.getMessage());
		}
		token = kernelAuthLib.getTokenByRole(role);
		logger.info("******Put request Json to EndPointUrl: " + url + " *******");
		Reporter.log("<pre>" + ReportUtil.getTextAreaJsonMsgHtml(inputJson) + "</pre>");
		try {
			  response = RestClient.putRequestWithParm(url, map, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, cookieName, token);
			  Reporter.log("<b><u>Actual Response Content: </u></b>(EndPointUrl: " + url + ") <pre>"
						+ ReportUtil.getTextAreaJsonMsgHtml(response.asString()) + "</pre>");
			return response;
		} catch (Exception e) {
			logger.error("Exception " + e);
			return response;
		}
	}
	
	protected Response putWithPathParamsBodyAndCookie(String url, String jsonInput, String cookieName, String role, String testCaseName, String pathParams) {
		Response response=null;
		String inputJson = inputJsonKeyWordHandeler(jsonInput, testCaseName);
		JSONObject req = new JSONObject(inputJson);
		HashMap<String, String> pathParamsMap = new HashMap<String, String>();
		String params[] = pathParams.split(",");
		for(String param: params)
		{
			if(req.has(param)) {
				 pathParamsMap.put(param, req.get(param).toString());
				 req.remove(param);
			}
			else 
			logger.error("request doesn't contanin param: "+param+" in: "+inputJson);
		}
		
		token = kernelAuthLib.getTokenByRole(role);
		logger.info("******put request Json to EndPointUrl: " + url + " *******");
		Reporter.log("<pre>" + ReportUtil.getTextAreaJsonMsgHtml(inputJson) + "</pre>");
		try {
			response = RestClient.putWithPathParamsBodyAndCookie(url, pathParamsMap, req.toString(), MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, cookieName, token);
			  Reporter.log("<b><u>Actual Response Content: </u></b>(EndPointUrl: " + url + ") <pre>"
						+ ReportUtil.getTextAreaJsonMsgHtml(response.asString()) + "</pre>");
			return response;
		} catch (Exception e) {
			logger.error("Exception " + e);
			return response;
		}
	}
	
	/**
	 * This method will hit get request and return the response
	 * @param url
	 * @param jsonInput
	 * @param cookieName
	 * @param role
	 * @return Response
	 */
	protected Response getWithPathParamAndCookie(String url, String jsonInput, String cookieName, String role, String testCaseName) {
			Response response=null;
			jsonInput = inputJsonKeyWordHandeler(jsonInput, testCaseName);
			HashMap<String, String> map = null;
			try {
				map = new Gson().fromJson(jsonInput, new TypeToken<HashMap<String, String>>(){}.getType());
			} catch (Exception e) {
				logger.error("Not able to convert jsonrequet to map: "+jsonInput+" Exception: "+e.getMessage());
			}
		
		token = kernelAuthLib.getTokenByRole(role);
		logger.info("******get request to EndPointUrl: " + url + " *******");
		Reporter.log("<pre>" + ReportUtil.getTextAreaJsonMsgHtml(jsonInput) + "</pre>");
		try {
			  response = RestClient.getRequestWithCookieAndPathParm(url, map, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, cookieName, token);
			  Reporter.log("<b><u>Actual Response Content: </u></b>(EndPointUrl: " + url + ") <pre>"
						+ ReportUtil.getTextAreaJsonMsgHtml(response.asString()) + "</pre>");
			return response;
		} catch (Exception e) {
			logger.error("Exception " + e);
			return response;
		}
	}
	
	protected Response getWithQueryParamAndCookie(String url, String jsonInput, String cookieName, String role, String testCaseName) {
		Response response=null;
		jsonInput = inputJsonKeyWordHandeler(jsonInput, testCaseName);
		HashMap<String, String> map = null;
		try {
			map = new Gson().fromJson(jsonInput, new TypeToken<HashMap<String, String>>(){}.getType());
		} catch (Exception e) {
			logger.error("Not able to convert jsonrequet to map: "+jsonInput+" Exception: "+e.getMessage());
		}
	
	token = kernelAuthLib.getTokenByRole(role);
	logger.info("******get request to EndPointUrl: " + url + " *******");
	Reporter.log("<pre>" + ReportUtil.getTextAreaJsonMsgHtml(jsonInput) + "</pre>");
	try {
		  response = RestClient.getRequestWithCookieAndQueryParm(url, map, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, cookieName, token);
		  Reporter.log("<b><u>Actual Response Content: </u></b>(EndPointUrl: " + url + ") <pre>"
					+ ReportUtil.getTextAreaJsonMsgHtml(response.asString()) + "</pre>");
		return response;
	} catch (Exception e) {
		logger.error("Exception " + e.getMessage());
		return response;
	}
}
	
	void writeAutoGeneratedId(Response response, String idKeyName, String testCaseName)
	{
		String fileName = getAutoGenIdFileName(testCaseName);
		String IdentifierKeyName = getAutogenIdKeyName(testCaseName);
		JSONObject responseJson = null;
		FileOutputStream outputStrem = null;
		FileInputStream inputStrem = null;
		Properties props = new Properties();
		try {
			
			responseJson = new JSONObject(response.getBody().asString()).getJSONObject("response");
			inputStrem = new FileInputStream(RunConfigUtil.getResourcePath()+fileName);
			props.load(inputStrem);
			if(responseJson!=null && responseJson.has(idKeyName)) {
				props.put(IdentifierKeyName, responseJson.get(idKeyName));
				outputStrem = new FileOutputStream(RunConfigUtil.getResourcePath()+fileName);
				props.store(outputStrem, "autogenerated ids");
				logger.info("added autogenerated id to property: "+RunConfigUtil.getResourcePath()+fileName);
			}
			else {
				logger.info("Response doesn't contain autogenerated fields to write- Response : "+response.asString());
			}
		} catch (JSONException | IOException e) {
			logger.error("Exception while getting autogenerated id and writing in property file:" + e.getMessage());
		}

	}
	
	public String getAutogenIdKeyName(String testCaseName)
	{
		if(testCaseName == null) return null;
		int indexof2nd_= testCaseName.indexOf("_");
		String autogenIdKeyName = testCaseName.substring(indexof2nd_+1);
		if(autogenIdKeyName==null)
			logger.error("Didn't find the key to store ID for testCase: "+testCaseName);
		logger.info("key for testCase: "+testCaseName+" : "+autogenIdKeyName);
		return autogenIdKeyName;
	}
	
	public static String getGlobalResourcePath() {
		return MosipTestRunner.getGlobalResourcePath();
	}
	
	public static void initiateAdminTest() {
		copyAdminTestResource();
	}
	
	public static void copyAdminTestResource() {
		try {
			File source = new File(RunConfigUtil.getGlobalResourcePath() + "/admin");
			File destination = new File(RunConfigUtil.getGlobalResourcePath() + "/"+RunConfigUtil.resourceFolderName);
			FileUtils.copyDirectoryToDirectory(source, destination);
			logger.info("Copied the admin test resource successfully");
		} catch (Exception e) {
			logger.error("Exception occured while copying the file: "+e.getMessage());
		}
	}
	public static void initiateKernelTest()
	{
		try {
			File source = new File(RunConfigUtil.getGlobalResourcePath() + "/kernel");
			File destination = new File(RunConfigUtil.getGlobalResourcePath() + "/"+RunConfigUtil.resourceFolderName);
			FileUtils.copyDirectoryToDirectory(source, destination);
			logger.info("Copied the kernel test resource successfully");
		} catch (Exception e) {
			logger.error("Exception occured while copying the file: "+e.getMessage());
		}
	}
	
	public Object[] getYmlTestData(String ymlPath){
		String testType = testLevel;
		final ObjectMapper mapper = new ObjectMapper();
		List<TestCaseDTO> testCaseDTOList = new LinkedList<TestCaseDTO>();
		Map<String, Map<String, Map<String, String>>> scriptsMap = loadyaml(ymlPath);
		for (String key : scriptsMap.keySet()) {
			Map<String, Map<String, String>> testCases = scriptsMap.get(key);
			if(testType.equalsIgnoreCase("smoke")){
				testCases = testCases.entrySet().stream().filter(mapElement -> mapElement.getKey().toLowerCase().contains("smoke")).collect(Collectors.toMap(mapElement -> mapElement.getKey(), mapElement -> mapElement.getValue()));
			}
			for (String testCase : testCases.keySet()) {
				TestCaseDTO testCaseDTO = mapper.convertValue(testCases.get(testCase), TestCaseDTO.class);
						testCaseDTO.setTestCaseName(testCase);
						testCaseDTOList.add(testCaseDTO);
			}
		}
		return testCaseDTOList.toArray();
	}
	
	@SuppressWarnings("unchecked")
	protected Map<String,Map<String, Map<String, String>>> loadyaml(String path) {
		Map<String,Map<String, Map<String, String>>> scriptsMap = null;
		try {
			Yaml yaml = new Yaml();
			InputStream inputStream = new FileInputStream(
					new File(RunConfigUtil.getResourcePath() + path).getAbsoluteFile());
		scriptsMap = (Map<String,Map<String, Map<String, String>>>) yaml.load(inputStream);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		}
		return scriptsMap;
	}	
	
	protected String getJsonFromTemplate(String input, String template)
	{
		String resultJson = null;
		try {
			Handlebars handlebars = new Handlebars();
			Gson gson = new Gson();
			Type type = new TypeToken<Map<String, Object>>(){}.getType();
			Map<String, Object> map = gson.fromJson(input, type);   
			Template compiledTemplate = handlebars.compile(template);
			Context context = Context.newBuilder(map).build();
			resultJson = compiledTemplate.apply(context);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultJson;
	}

	String inputJsonKeyWordHandeler(String jsonString, String testCaseName)
	{
		if(jsonString==null)
		{
			logger.info(" Request Json String is :"+jsonString);
			return jsonString;
		}
		if(jsonString.contains("$TIMESTAMP$"))
			jsonString = jsonString.replace("$TIMESTAMP$", generateCurrentUTCTimeStamp());
		if(jsonString.contains("$TIMESTAMPL$"))
			jsonString = jsonString.replace("$TIMESTAMPL$", generateCurrentLocalTimeStamp());
		if(jsonString.contains("$REMOVE$")) 
			jsonString = removeObject(new JSONObject(jsonString));
		if(jsonString.contains("$ID:")) {
			String autoGenIdFileName = getAutoGenIdFileName(testCaseName);
			jsonString = replaceIdWithAutogeneratedId(jsonString, "$ID:", autoGenIdFileName);
		}
		return jsonString;
	}
	
	public String getAutoGenIdFileName(String testCaseName)
	{
		if(testCaseName == null) return null;
		if(testCaseName.toLowerCase().startsWith("admin"))
			return adminAutoGeneratedIdPropFileName;
		else if(testCaseName.toLowerCase().startsWith("prereg"))
			return preregAutoGeneratedIdPropFileName;
		else if(testCaseName.toLowerCase().startsWith("partner"))
			return partnerAutoGeneratedIdPropFileName;
		return null;
	}
	
	private String generateCurrentUTCTimeStamp() {
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		return dateFormat.format(date);
	}
	private String generateCurrentLocalTimeStamp()
	{
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		return dateFormat.format(date);
	}
	private String replaceIdWithAutogeneratedId(String jsonString, String idKey, String autoGenIdFileName)
	{
		if(!jsonString.contains(idKey))
			return jsonString;
		else {
			String keyForIdProperty = StringUtils.substringBetween(jsonString, idKey, "$");
			String keyToReplace = idKey+keyForIdProperty+"$";
			Properties props = new Properties();
				try {
					FileInputStream inputStrem = new FileInputStream(RunConfigUtil.getResourcePath()+autoGenIdFileName);
					props.load(inputStrem);
				} catch (IOException e) {
					logger.error("Exception while loading the autogenerated id: "+e.getMessage());
				}
				jsonString = jsonString.replace(keyToReplace, props.getProperty(keyForIdProperty));
				replaceIdWithAutogeneratedId(jsonString, idKey, autoGenIdFileName);
		}
		return jsonString;
	}
	
	public String removeObject(JSONObject object) {
		Iterator<String> keysItr = object.keys();
		while (keysItr.hasNext()) {
			String key = keysItr.next();
			Object value = object.get(key);
			if (value instanceof JSONArray) {
				JSONArray array = (JSONArray) value;
				String finalarrayContent = "";
				if (array.length() != 0) {
					for (int i = 0; i < array.length(); ++i) {
						if (!array.toString().contains("{") && !array.toString().contains("}")) {
							Set<String> arr = new HashSet<String>();
							for (int k = 0; k < array.length(); k++) {
								arr.add(array.getString(k));
							}
							finalarrayContent = removObjectFromArray(arr);
						} else {
							String arrayContent = removeObject(new JSONObject(array.get(i).toString()),finalarrayContent);
							if (!arrayContent.equals("{}"))
								finalarrayContent = finalarrayContent + "," + arrayContent;
						}
					}
					finalarrayContent = finalarrayContent.substring(1, finalarrayContent.length());
					object.put(key, new JSONArray("[" + finalarrayContent + "]"));
				} else
					object.put(key, new JSONArray("[]"));

			} else if (value instanceof JSONObject) {
				String objectContent = removeObject(new JSONObject(value.toString()));
				object.put(key, new JSONObject(objectContent));
			}
			if (value.toString().equals("$REMOVE$")) {
				object.remove(key);
				keysItr = object.keys();
			}
		}
		return object.toString();
	}
	private String removeObject(JSONObject object, String tempArrayContent) {
		Iterator<String> keysItr = object.keys();
		while (keysItr.hasNext()) {
			String key = keysItr.next();
			Object value = object.get(key);
			if (value instanceof JSONArray) {
				JSONArray array = (JSONArray) value;
				for (int i = 0; i < array.length(); ++i) {
					String arrayContent = removeObject(new JSONObject(array.get(i).toString()));
					object.put(key, new JSONArray("[" + arrayContent + "]"));
				}
			} else if (value instanceof JSONObject) {
				String objectContent = removeObject(new JSONObject(value.toString()));
				object.put(key, new JSONObject(objectContent));
			}
			if (value.toString().equals("$REMOVE$")) {
				object.remove(key);
				keysItr = object.keys();
			}
		}
		return object.toString();
	}
	private String removObjectFromArray(Set<String> content) {
		String array = "[";
		for (String str : content) {
			if (!str.contains("$REMOVE$"))
				array = array + '"' + str + '"' + ",";
		}
		array = array.substring(0, array.length() - 1);
		array = array + "]";
		return array;
	}
	
	protected Response postWithOnlyPathParamAndCookie(String url, String jsonInput, String cookieName, String role, String testCaseName) {
		Response response=null;
		jsonInput = inputJsonKeyWordHandeler(jsonInput, testCaseName);
		HashMap<String, String> map = null;
		try {
			map = new Gson().fromJson(jsonInput, new TypeToken<HashMap<String, String>>(){}.getType());
		} catch (Exception e) {
			logger.error("Not able to convert jsonrequet to map: "+jsonInput+" Exception: "+e.getMessage());
		}
	
	token = kernelAuthLib.getTokenByRole(role);
	logger.info("******get request to EndPointUrl: " + url + " *******");
	Reporter.log("<pre>" + ReportUtil.getTextAreaJsonMsgHtml(jsonInput) + "</pre>");
	try {
		  response = RestClient.postRequestWithCookieAndOnlyPathParm(url, map, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, cookieName, token);
		  Reporter.log("<b><u>Actual Response Content: </u></b>(EndPointUrl: " + url + ") <pre>"
					+ ReportUtil.getTextAreaJsonMsgHtml(response.asString()) + "</pre>");
		return response;
	} catch (Exception e) {
		logger.error("Exception " + e);
		return response;
	}
}
}
