package help;

import java.util.Optional;

import org.json.JSONObject;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;

import FuncTest.TestFunc.utils;

public class BotFeaturesInfo {
	
	public static String WELCOME_MESSAGE = "Welcome to the TechnoBot - the first Technion-Bot!\r\n";
	public static String AVAILABLE_ACTIONS = "The TechnoBot can do the following:\r\n" + 
			"1. Keep track of homework assignments\r\n\ttry: 'When is my next assigment due'\r\n" + 
			"2. Guide to attractions around campus\r\n\ttry: 'when can I go to Junta'\r\n" +
			"3. Search for videos relavant to a course\r\n\ttry:'does course 234319 have a video?'\r\n" +
			"5. Filter courses based on the given info\r\n\ttry: 'filter courses'\r\n" + 
			"6. Search for info on courses \r\n\ttry: 'Search class'\r\n" + 
			"7. Retrive both prerequisites & postrequisites \r\n\ttry:'Give me the prerequisites of Information Retrieval' or 'What are the postrequisites of Computer Vision?'"+
			"8. Define your own rules & execute them\r\n\ttry:'subscribe to rule homework with username <uname> and password <pwd>' and 'run my rules'"+
			"If you are having problems, please respond \"support\"";

	public static HttpResponseMessage returnInfoResponse(JSONObject queryResult, HttpRequestMessage<Optional<String>> s,
			ExecutionContext c) {
		return utils.createWebhookResponseContent(buildInfoText(), s);
	}

	private static String buildInfoText() {
		return WELCOME_MESSAGE+AVAILABLE_ACTIONS;
	}
}
