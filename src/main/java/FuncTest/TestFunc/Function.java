package FuncTest.TestFunc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import com.microsoft.azure.functions.annotation.*;
import com.microsoft.azure.functions.*;
import org.json.*;
import FuncTest.TestFunc.globals;
import FuncTest.TestFunc.utils;
import homework.HomeworkGetter;
import homework.LoginCredentials;
import homework.WrongCredentialsException;
import responses.TableResponse;
import rule.RunRulesHandler;
import rule.loginHandler;
import rule.subscribeHandler;

/**
 * Azure Functions with HTTP Trigger.
 */
@SuppressWarnings("static-method")
public class Function {
	@FunctionName("CoolFunc")
	public HttpResponseMessage run(
			@HttpTrigger(name = "req", methods = { HttpMethod.GET,
					HttpMethod.POST }, authLevel = AuthorizationLevel.FUNCTION) HttpRequestMessage<Optional<String>> s,
			final ExecutionContext c) {
		c.getLogger().info("=========== WEBHOOK INVOKED ===========");
		JSONObject queryResult = new JSONObject(s.getBody().get().toString()).getJSONObject("queryResult");
		switch (queryResult.getJSONObject("intent").getString("displayName")) {
			case globals.BUSINESS_HOUR_BY_DAY_INTENT_NAME:
				return getHourByDay(queryResult, s, c);
			case globals.BUSINESS_HOUR_WEEK_INTENT_NAME:
				return getHourByWeek(queryResult, s, c);
			case globals.HOMEWORK_GET_UPCOMING_INTENT_NAME:
				return getUpcomingHomework(queryResult, s, c);
			case globals.LOGIN_INTENT:
				return checkLoginName(queryResult, s, c);
			case globals.SUBSCRIBE_INTENT:
				return subscribeToSystem(queryResult,s,c);
			case globals.FILTER_COURSES_INTENT_NAME:
				return getMatchingCoursesResponse(queryResult, s, c);
			case globals.RUN_RULES_INTENT:
				return applyRules(queryResult, s, c);
		}
		return utils.createWebhookResponseContent("what is this intent?", s);
	}
	
	private HttpResponseMessage applyRules(JSONObject queryResult, HttpRequestMessage<Optional<String>> s,
			ExecutionContext c) {
		// TODO Auto-generated method stub
		String uname = utils.getStringUserParamFromContext(queryResult, "username");
		String passwd = utils.getStringUserParamFromContext(queryResult, "password");
		
		c.getLogger().info("================ username = " + uname + "================");
		c.getLogger().info("================ password = " + passwd + "================");
		
		return new RunRulesHandler(uname, passwd).runHomeworkRules(s,c);
	}

	private HttpResponseMessage subscribeToSystem(JSONObject queryResult, HttpRequestMessage<Optional<String>> s,
			final ExecutionContext c) {
		JSONObject parameters = queryResult.getJSONObject("parameters");
		List<String> requiredParameterNames = Arrays.asList("username", "password");
		
		if (!utils.allParametersArePresent(parameters, requiredParameterNames)) 
			return utils.createWebhookResponseContent("Please enter user credentials to subscribe with.", s);
		
		return utils.createWebhookResponseContent(
				(new subscribeHandler(parameters.getString("username"),parameters.getString("password"))).subscribe(c), s);
	}
	
	private HttpResponseMessage checkLoginName(JSONObject queryResult, HttpRequestMessage<Optional<String>> s,
			final ExecutionContext c)
	{
		JSONObject parameters = queryResult.getJSONObject("parameters");
		List<String> requiredParameterNames = Arrays.asList("username", "password");
		
		if (!utils.allParametersArePresent(parameters, requiredParameterNames)) 
			return utils.createWebhookResponseContent("Missing username. Please choose a user to log in", s);
		
		return utils.createWebhookResponseContent(
				(new loginHandler(parameters.getString("username"), parameters.getString("password"))).checkUserNameExists(), s);
	}

	private HttpResponseMessage getMatchingCoursesResponse(JSONObject queryResult,
			HttpRequestMessage<Optional<String>> s, ExecutionContext c) {
		c.getLogger().info("=========== FILTER COURSES BY PARAMS ===========");

		String facultyName = utils.getStringUserParamFromContext(queryResult, "Faculty");
		Integer lectureHours = utils.getIntUserParamFromContext(queryResult, "lectureHours");
		Integer tutorialHours = utils.getIntUserParamFromContext(queryResult, "tutorialHours");
		String dateA[] = utils.getDateRange(queryResult, "date-period");
		
		String query = utils.buildFilteringQuery(facultyName, lectureHours, tutorialHours, dateA);
		StringBuilder jsonResult = new StringBuilder();

		c.getLogger().info("=========== FACULTY IS " + facultyName + " ===========");
		c.getLogger().info("=========== lectureHours IS " + lectureHours + " ===========");
		c.getLogger().info("=========== tutorialHours IS " + tutorialHours + " ===========");
		c.getLogger().info("=========== dateRangeA IS " + dateA[0] + " to " + dateA[1] + " ===========");
		c.getLogger().info("=========== QUERY IS " + query + " ===========");

		try (Connection connection = DriverManager.getConnection(globals.CONNECTION_STRING)) {
			ResultSet resultSet = connection.createStatement().executeQuery(query);
			if (!resultSet.isBeforeFirst()) {
				c.getLogger().info("=========== NO RESULTS ===========");
				jsonResult.append(globals.NO_COURSES_FOUND_ERROR);
			} else {
				c.getLogger().info("=========== FOUND RESULTS ===========");
				jsonResult.append("Here's what I found:\n");
				jsonResult = parseFilterResults(resultSet, jsonResult, c);
			}
			
			connection.close();
			c.getLogger().info("=========== RETURNING RESULTS ===========");
			return utils.createWebhookResponseContent(jsonResult.toString(), s);
			
		} catch (Exception e) {
			c.getLogger().info("=========== " + e.getMessage() + " ===========");
			throw new RuntimeException();
		}
	}

	private StringBuilder parseFilterResults(ResultSet resultSet, StringBuilder jsonResult, ExecutionContext c) {
		c.getLogger().info("=========== MAKING RESULTS ===========");
		try {
			for (int count = 1; resultSet.next();) {
				jsonResult.append(count + " - " + resultSet.getString(1) + " (" +
			resultSet.getString(2) + ")\n");
				++count;
			}
		} catch (SQLException e) {
			c.getLogger().info("=========== " + e.getMessage() + " ===========");
			throw new RuntimeException();
		}
		
		c.getLogger().info("=========== RESULTS: " + jsonResult.toString() +  " ===========");
		return jsonResult;

	}

	private HttpResponseMessage getHourByWeek(JSONObject queryResult, HttpRequestMessage<Optional<String>> s,
			final ExecutionContext c) {
		JSONObject parameters = queryResult.getJSONObject("parameters");
		String bname = "";
		if (!parameters.has("Business"))
			return utils.createWebhookResponseContent(globals.MISSING_BUSINESS_PARAM, s);
		bname = parameters.getString("Business");
		Connection connection = null;
		StringBuilder jsonResult = new StringBuilder();
		try {
			connection = DriverManager.getConnection(globals.CONNECTION_STRING);
			String selectSql = "SELECT Sunday,Monday,Tuesday,Wednesday,Thursday,Friday,Saturday FROM Businesses"
					+ " WHERE CHARINDEX('" + bname + "',BusinessName) != 0";
			try (Statement statement = connection.createStatement();
					ResultSet resultSet = statement.executeQuery(selectSql)) {
				if (!resultSet.isBeforeFirst())
					jsonResult.append(globals.NO_BUSINESS_FOUND_ERROR);
				else {
					jsonResult.append(bname + " is open on:\n");
					ResultSetMetaData rsmd = resultSet.getMetaData();
					for (int columnsNumber = rsmd.getColumnCount(); resultSet.next();)
						for (int i = 1; i <= columnsNumber; ++i) {
							String columnValue = resultSet.getString(i);
							if (!"N\\A".equals(columnValue)) {
								if (i > 1)
									jsonResult.append("\n");
								jsonResult.append("Between " + columnValue + " on " + rsmd.getColumnName(i) + "s");
							}
						}
				}
				connection.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return utils.createWebhookResponseContent(jsonResult.toString(), s);
	}

	private HttpResponseMessage getHourByDay(JSONObject queryResult, HttpRequestMessage<Optional<String>> s,
			final ExecutionContext c) {
		c.getLogger().info("=========== GET BUSINESS HOURS BY DAY ===========");
		JSONObject parameters = queryResult.getJSONObject("parameters");
		String bname = "", day = "";
		if (!parameters.has("Business"))
			return utils.createWebhookResponseContent(globals.MISSING_BUSINESS_PARAM, s);
		bname = parameters.getString("Business");
		if (!parameters.has("DayOfWeek"))
			return utils.createWebhookResponseContent(globals.MISSING_DAY_PARAM, s);
		day = utils.dayValidation(parameters.getString("DayOfWeek"));
		if ("".equals(day))
			return utils.createWebhookResponseContent("I'm sorry I don't know what day you mean\n", s);

		Connection connection = null;
		StringBuilder jsonResult = new StringBuilder();
		try {
			connection = DriverManager.getConnection(globals.CONNECTION_STRING);
			String selectSql = "SELECT BusinessName, " + day + " FROM BUSINESSES WHERE CHARINDEX('" + bname
					+ "', BusinessName) != 0";
			try (Statement statement = connection.createStatement();
					ResultSet resultSet = statement.executeQuery(selectSql)) {
				if (!resultSet.isBeforeFirst())
					jsonResult.append(globals.NO_BUSINESS_FOUND_ERROR);
				else
					while (resultSet.next())
						jsonResult.append(("N\\A".equals(resultSet.getString(2))
								? resultSet.getString(1) + " is not open on " + day
								: "The " + resultSet.getString(1) + " is open between " + resultSet.getString(2)
										+ " on " + day)
								+ "s");
				connection.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return utils.createWebhookResponseContent(jsonResult.toString(), s);
	}

	private HttpResponseMessage getUpcomingHomework(JSONObject queryResult, HttpRequestMessage<Optional<String>> s,
			final ExecutionContext c) {

		JSONObject parameters = queryResult.getJSONObject("parameters");
		List<String> requiredParameterNames = new ArrayList<>();
		requiredParameterNames.add("username");
		requiredParameterNames.add("password");
		if (!utils.allParametersArePresent(parameters, requiredParameterNames)) 
			return utils.createWebhookResponseContent("Missing parameters. Please report this", s);
		
		LoginCredentials lc = new LoginCredentials(parameters.getString("username"), parameters.getString("password"));
		HomeworkGetter homework = new HomeworkGetter(lc);
		try {
			return TableResponse.homeworkTableResponse(s, homework.getUpcomingHomework());
		} catch (WrongCredentialsException e) {
			return utils.createWebhookResponseContent("Wrong credentials, please try again", s);
		}
	}

}