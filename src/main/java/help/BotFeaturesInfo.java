package help;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.json.JSONObject;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;

import FuncTest.TestFunc.utils;

public class BotFeaturesInfo {

	public static String WELCOME_MESSAGE = "Welcome to the TechnoBot - the first Technion-Bot!\r\n";
	public static String[] AVAILABLE_PREFIXES = { "The TechnoBot can do the following:\r\n", "I can help you with:\r\n",
			"I am capable of the following:\r\n", "I can do plenty:\r\n" };
	private static final String[] FEATURES_ARRAY = {
			"Keep track of homework assignments\r\n\ttry: 'When is my next assigment due'\r\n",
			"Guide to attractions around campus\r\n\ttry: 'when can I go to Junta'\r\n",
			"Search for videos relavant to a course\r\n\ttry:'does course 234319 have a video?'\r\n",
			"Filter courses based on the given info\r\n\ttry: 'filter courses'\r\n",
			"Search for info on courses \r\n\ttry: 'Search class'\r\n",
			"Retrive both prerequisites & postrequisites \r\n\ttry:'Give me the prerequisites of Information Retrieval' or 'What are the postrequisites of Computer Vision?'",
			"Define your own rules & execute them\r\n\ttry:'subscribe to rule homework with username <uname> and password <pwd>' and 'run my rules'", };

	public static String[] AVAILABLE_POSTFIXES = { "If you are having problems, please respond \"support\"",
			"In case you encounter an issue, please respond \"support\"",
			"For further guidance, please respond \"support\"","You can get more info by saying \"support\"" };

	public static HttpResponseMessage returnInfoResponse(JSONObject queryResult, HttpRequestMessage<Optional<String>> s,
			ExecutionContext c) {
		return utils.createWebhookResponseContent(buildInfoText(), s);
	}

	private static String buildInfoText() {
		int prefix_index = new Random().nextInt(AVAILABLE_PREFIXES.length);
		int postfix_index = new Random().nextInt(AVAILABLE_PREFIXES.length);

		List<String> list = new ArrayList<String>();
		for (int i = 0; i < FEATURES_ARRAY.length; i++) {
			list.add(FEATURES_ARRAY[i]);
		}
		java.util.Collections.shuffle(list);
		int count = 1;
		String capabilites = "";
		for (String s : list) {
			capabilites += count + ". " + s;
			count++;
		}
		return WELCOME_MESSAGE + AVAILABLE_PREFIXES[prefix_index] + capabilites + AVAILABLE_POSTFIXES[postfix_index];
	}
}
