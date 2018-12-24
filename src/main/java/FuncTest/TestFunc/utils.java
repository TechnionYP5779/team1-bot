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
		return (new String[] { "zeroDay", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday",
				"Saturday" })[i];
	}

	public static boolean allParametersArePresent(JSONObject parameters, Collection<String> paremeterNames) {
		return paremeterNames.stream().allMatch(paramName -> parameters.has(paramName));
	}

	public static HttpResponseMessage createWebhookResponseContent(String resultText,
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

	//returns null is the parameter is missing
	protected static String getUserParam(JSONObject queryResult, String paramName) {
		JSONObject parameters = queryResult.getJSONObject("parameters");
		return !parameters.has(paramName) ? null : parameters.getString(paramName);
	}

	
	protected static String buildFilteringQuery(String facultyName, Integer lectureHours
			, Integer tutorialHours, String dateRangeA[]) {
		String q = "select name, ID from dbo.Courses where 1 = 1";
		
		if(isValidFaculty(facultyName)) q += " and faculty = " + quote(facultyName);
		if(isValidNumber(lectureHours)) q += " and lectureHours = " + lectureHours;
		if(isValidNumber(tutorialHours)) q += " and tutorialHours = " + tutorialHours;
		if(isValidDateRange(dateRangeA)) q += " and examA between '" + dateRangeA[0] + "' and '"+ dateRangeA[1] +"'";
		return q;
	}
	
	private static boolean isValidDateRange(String[] dateRange) {
		// TODO Auto-generated method stub
		return (dateRange != null);
	}

	private static boolean isValidNumber(Integer lectureHours) {
		return lectureHours != null && lectureHours.intValue() >= 0;
	}

	private static boolean isValidFaculty(String facultyName) {
		return facultyName != null && !"None".equals(facultyName);
	}

	private static String quote(String s) {
		return "\'" + s + "\'";
	}

	//param must be string!
	public static String getStringUserParamFromContext(JSONObject queryResult, String paramName) {
		JSONArray outputContexts = queryResult.getJSONArray("outputContexts");
		JSONObject parameters = outputContexts.getJSONObject(0).getJSONObject("parameters");
		return !parameters.has(paramName) ? null : parameters.getString(paramName);
	}
	
	//param must be a number!
	public static Integer getIntUserParamFromContext(JSONObject queryResult, String paramName) {
		JSONArray outputContexts = queryResult.getJSONArray("outputContexts");
		JSONObject parameters = outputContexts.getJSONObject(0).getJSONObject("parameters");
		return !parameters.has(paramName) ? null : parameters.getInt(paramName);
	}
	
	//paramName is probably date-period
	public static String[] getDateRange(JSONObject queryResult, String paramName){
		JSONArray outputContexts = queryResult.getJSONArray("outputContexts");
		JSONObject parameters = outputContexts.getJSONObject(0).getJSONObject("parameters");
		if(!parameters.has(paramName)) return null;
		
		JSONObject dateJSON = parameters.getJSONObject(paramName);
		String from = dateJSON.getString("startDate");
		String to = dateJSON.getString("endDate");
		
		return new String[] {from.substring(0, from.indexOf("T")),
				to.substring(0, to.indexOf("T"))};
		
	}
		
	
}
