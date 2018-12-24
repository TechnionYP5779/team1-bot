package FuncTest.TestFunc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
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
		}
		return utils.createWebhookResponseContent("what is this intent?", s);
	}
	
	private HttpResponseMessage subscribeToSystem(JSONObject queryResult, HttpRequestMessage<Optional<String>> s,
			final ExecutionContext c) {
		JSONObject parameters = queryResult.getJSONObject("parameters");
		List<String> requiredParameterNames = new ArrayList<>();
		requiredParameterNames.add("username");
		requiredParameterNames.add("password");
		if (!utils.allParametersArePresent(parameters, requiredParameterNames)) 
			return utils.createWebhookResponseContent("Please enter user credentials to subscribe with.", s);
		return utils.createWebhookResponseContent(
				(new subscribeHandler(parameters.getString("username"),parameters.getString("password"))).subscribe(), s);
	}
	
	private HttpResponseMessage checkLoginName(JSONObject queryResult, HttpRequestMessage<Optional<String>> s,
			final ExecutionContext c)
	{
		JSONObject parameters = queryResult.getJSONObject("parameters");
		List<String> requiredParameterNames = new ArrayList<>();
		requiredParameterNames.add("username");
		if (!utils.allParametersArePresent(parameters, requiredParameterNames)) 
			return utils.createWebhookResponseContent("Missing username. Please choose a user to log in", s);
		return utils.createWebhookResponseContent(
				(new loginHandler(parameters.getString("username"))).checkUserNameExists(), s);
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
			return utils.createWebhookResponseContent("Missing parametrs. Please report this", s);
		
		LoginCredentials lc = new LoginCredentials(parameters.getString("username"), parameters.getString("password"));
		HomeworkGetter homework = new HomeworkGetter(lc);
		try {
			return TableResponse.homeworkTableResponse(s, homework.getUpcomingHomework());
		} catch (WrongCredentialsException e) {
			return utils.createWebhookResponseContent("Wrong credentials, please try again", s);
		}
	}

	

}