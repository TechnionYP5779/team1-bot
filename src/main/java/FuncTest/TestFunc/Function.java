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
import help.BotFeaturesInfo;
import homework.HomeworkGetter;
import homework.LoginCredentials;
import homework.WrongCredentialsException;
import postrequsites.PostrequisiteHandler;
import responses.TableResponse;

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
      case globals.FILTER_COURSES_INTENT_NAME:
        return getMatchingCoursesResponse(queryResult, s, c);
      case globals.HOMEWORK_GET_UPCOMING_INTENT_NAME:
        return getUpcomingHomework(queryResult, s, c);
      case globals.PREREQUISITES_GET_BY_NAME_INTENT_NAME:
			  return getCoursesPrerequisitesByName(queryResult, s, c);
		  case globals.PREREQUISITES_GET_BY_NUMBER_INTENT_NAME:
			  return getCoursesPrerequisitesByNumber(queryResult, s, c);
      case globals.COURSE_GET_POSTREQUISITES_BY_NAME_INTENT_NAME:
        return PostrequisiteHandler.getPostrequisitesByName(queryResult, s, c);
      case globals.COURSE_GET_POSTREQUISITES_BY_NUMBER_INTENT_NAME:
        return PostrequisiteHandler.getPostrequisitesByNumber(queryResult, s, c);
      case globals.VIDEOS_CHECK_EXISTS_INTENT_NAME:
			  return VideoAnswers.checkExists(queryResult, s, c);
      case globals.HELP_INTENT_NAME:
          return BotFeaturesInfo.returnInfoResponse(queryResult, s, c);
		}
		
		return utils.createWebhookResponseContent("what is this intent?", s);
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

	private StringBuilder parseFilterResults(ResultSet s, StringBuilder jsonResult, ExecutionContext c) {
		c.getLogger().info("=========== MAKING RESULTS ===========");
		try {
			for (int count = 1; s.next() & count <= globals.COURSE_FILTER_LIMIT;) {
				jsonResult.append(count + " - " + s.getString(1) + " (" +
			s.getString(2) + ")\n");
				++count;
			}
			
			if(s.next()) //more answers to be read after reading limit
				jsonResult.append("(only showing first " + globals.COURSE_FILTER_LIMIT + " results."
						+ "To narrow your search please add parameters)");
			
			
		} catch (SQLException e) {
			c.getLogger().info("=========== " + e.getMessage() + " ===========");
			throw new RuntimeException();
		}
		
		c.getLogger().info("=========== RESULTS: " + jsonResult.toString() +  " ===========");
		return jsonResult;

	}
	
	private HttpResponseMessage getCoursesPrerequisitesByName(JSONObject queryResult,
			HttpRequestMessage<Optional<String>> s, ExecutionContext c) {
		c.getLogger().info("=========== GET COURSES PREREQUISITES BY NAME ===========");
		String courseName = utils.getUserParam(queryResult, "courseName"),
				query = utils.buildPrerequisitesQueryByName(courseName);
		c.getLogger().info("=========== COURSE NAME IS " + courseName + " ===========");
		c.getLogger().info("=========== QUERY IS " + query + " ===========");
		try (Connection connection = DriverManager.getConnection(globals.CONNECTION_STRING)) {
			StringBuilder jsonResult = new StringBuilder();
			ResultSet resultSet = connection.createStatement().executeQuery(query);
			if (resultSet.isBeforeFirst())
				parsePrerequisitesResults(resultSet, jsonResult, c);
			else
				jsonResult.append(globals.NO_COURSES_FOUND_ERROR);
			connection.close();
			return utils.createWebhookResponseContent(jsonResult.toString(), s);
		} catch (Exception e) {
			throw new RuntimeException();
		}
	}
	
	private HttpResponseMessage getCoursesPrerequisitesByNumber(JSONObject queryResult,
			HttpRequestMessage<Optional<String>> s, ExecutionContext c) {
		c.getLogger().info("=========== GET COURSES PREREQUISITES BY NUMBER ===========");
		Integer courseNumber = Integer.valueOf(utils.getUserParam(queryResult, "courseNumber"));
		c.getLogger().info("=========== NUMBER IS " + courseNumber + " ===========");
		String query = utils.buildPrerequisitesQueryByNumber(courseNumber);
		c.getLogger().info("=========== QUERY IS " + query + " ===========");
		try (Connection connection = DriverManager.getConnection(globals.CONNECTION_STRING)) {
			StringBuilder jsonResult = new StringBuilder();
			ResultSet resultSet = connection.createStatement().executeQuery(query);
			if (resultSet.isBeforeFirst())
				parsePrerequisitesResults(resultSet, jsonResult, c);
			else
				jsonResult.append(globals.NO_COURSES_FOUND_ERROR);
			connection.close();
			return utils.createWebhookResponseContent(jsonResult.toString(), s);
		} catch (Exception e) {
			throw new RuntimeException();
		}
	}
	
	private void parsePrerequisitesResults(ResultSet s, StringBuilder jsonResult, ExecutionContext c) {
		try {
			s.next();
			String pre = s.getString(1);
			c.getLogger().info(pre);
			int count = 1;
			String allOptions[] = pre.split("(\\|)");
			for(String opt : allOptions) {
				String anOption[] = opt.split("(&)");
				jsonResult.append(count + ") ");
				for(String course : anOption)
					jsonResult.append(course + " AND ");
				jsonResult.delete(jsonResult.length() - 5, jsonResult.length() - 1);
				jsonResult.append("\nOR\n");
				++count;
			}
			jsonResult.delete(jsonResult.length() - 4, jsonResult.length() - 1);
			c.getLogger().info(jsonResult.toString());
			assert !s.next();
		} catch (SQLException e) {
			throw new RuntimeException();
		}
	}

	private HttpResponseMessage getHourByWeek(JSONObject queryResult, HttpRequestMessage<Optional<String>> s,
			final ExecutionContext c) {
		c.getLogger().info("=========== GET HOUR BY WEEK ===========");
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