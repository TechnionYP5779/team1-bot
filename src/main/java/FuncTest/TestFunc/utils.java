package FuncTest.TestFunc;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;
import org.json.JSONArray;
import org.json.JSONObject;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;

public class utils {
	protected static String dayValidation(String day) {
		// ===================================
		// check day conform supported days
		String[] days_of_week = new String[] { "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday",
				"Saturday", "Today", "Tomorrow", "Now" };
		String new_day = day.substring(0, 1).toUpperCase() + day.substring(1).toLowerCase();
		if (!Arrays.asList(days_of_week).contains(new_day))
			return "";
		// ===================================
		// handle special days
		if ("Now".equals(new_day) || "Today".equals(new_day)) // question regarding this day
		{
			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			cal.add(Calendar.HOUR_OF_DAY, 2);
			new_day = fromIntToDay(cal.get(Calendar.DAY_OF_WEEK));
		} else if ("Tomorrow".equals(new_day))// question regarding the next day
		{
			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			cal.add(Calendar.HOUR_OF_DAY, 2);
			cal.add(Calendar.DATE, 1);
			new_day = fromIntToDay(cal.get(Calendar.DAY_OF_WEEK));
		}
		return new_day;
	}

	protected static String fromIntToDay(int i) {
		return (new String[] { "zeroDay", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" })[i];
	}
	
	protected static boolean allParametersArePresent(JSONObject parameters, Collection<String> paremeterNames) {
		return paremeterNames.stream().allMatch(paramName -> parameters.has(paramName));
	}

	protected static HttpResponseMessage createWebhookResponseContent(String resultText,
			HttpRequestMessage<Optional<String>> s) {
		return s.createResponseBuilder(
				HttpStatus.OK).body(
						new JSONObject().put("fulfillmentText", resultText)
								.put("fulfillmentMessages", new JSONArray().put(new JSONObject().put("simpleResponses",
										new JSONObject().put("simpleResponses",
												new JSONArray().put(new JSONObject().put("displayText", "display text")
														.put("textToSpeech", "display text"))))))
								.put("payload",
										new JSONObject().put("google",
												new JSONObject().put("expectUserResponse", Boolean.TRUE)))
								.toString().getBytes())
				.header("Content-Type", "application/json; charset=UTF-8").header("Accept", "application/json").build();
	}
	
	private static String quote(String s) {
		return "\'" + s + "\'";
	}

	protected static String buildPrerequisitesQueryByName(String courseName) {
		String q = "select prerequisites from dbo.Courses";		
		if(courseName != null) q += " where name = " + courseName;
		if(courseName != null) q += " where name = " + quote(courseName);
		return q;
	}
	
	protected static String buildPrerequisitesQueryByNumber(String courseNumber) {
		String q = "select prerequisites from dbo.Courses";		
		if(courseNumber != null) q += " where number = " + courseNumber;
		if(courseNumber != null) q += " where number = " + quote(courseNumber);
		return q;
	}
}
