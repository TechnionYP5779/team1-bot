package postrequsites;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Optional;

import org.json.JSONObject;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;

import FuncTest.TestFunc.globals;
import FuncTest.TestFunc.utils;
import responses.TableResponse;

public class PostrequisiteHandler {

	private static final String SERVER_ERROR = "A server error ocurred please try again.";

	public static HttpResponseMessage getPostrequisitesByNumber(JSONObject queryResult,
			HttpRequestMessage<Optional<String>> s, ExecutionContext c) {
		try {
			JSONObject parameters = queryResult.getJSONObject("parameters");
			if (!utils.allParametersArePresent(parameters, Arrays.asList("courseNum"))) {
				c.getLogger().info("ERROR::PostrequisiteHandler::Missing parameters");
				return utils.createWebhookResponseContent(SERVER_ERROR, s);
			}
			int courseNum = parameters.getInt("courseNum");
			Course course = extractCourseInfo(courseNum);
			return !course.isInvalid() ? getPostrequisites(s, course)
					: utils.createWebhookResponseContent(
							"There is no course with id " + courseNum + ", please try again.", s);
		} catch (SQLException e) {
			c.getLogger().info("ERROR::PostrequisiteHandler::SQL Exception");
			c.getLogger().info(e.toString());
			return utils.createWebhookResponseContent(SERVER_ERROR, s);

		}
	}

	public static Course extractCourseInfo(int courseNum) throws SQLException {
		try (Connection connection = DriverManager.getConnection(globals.CONNECTION_STRING)) {
			PreparedStatement ps = connection.prepareStatement("Select ID,Name From Courses WHERE Courses.ID = ?;");
			ps.setInt(1, courseNum);
			ResultSet rs = ps.executeQuery();
			return !rs.next() ? Course.INVALID_COURSE : new Course(rs.getString(2), rs.getInt(1));
		}
	}

	public static HttpResponseMessage getPostrequisitesByName(JSONObject queryResult,
			HttpRequestMessage<Optional<String>> s, ExecutionContext c) {

		try {
			JSONObject parameters = queryResult.getJSONObject("parameters");
			if (!utils.allParametersArePresent(parameters, Arrays.asList("courseName"))) {
				c.getLogger().info("ERROR::PostrequisiteHandler::Missing parameters");
				return utils.createWebhookResponseContent(SERVER_ERROR, s);
			}
			String courseName = parameters.getString("courseName");
			Course course = extractCourseInfo(courseName);
			return !course.isInvalid() ? getPostrequisites(s, course)
					: utils.createWebhookResponseContent(
							"There is no course named " + courseName + ", please try again.", s);
		} catch (SQLException e) {
			c.getLogger().info("ERROR::PostrequisiteHandler::SQL Exception");
			c.getLogger().info(e.toString());
			return utils.createWebhookResponseContent(SERVER_ERROR, s);
		}
	}

	private static Course extractCourseInfo(String courseName) throws SQLException {
		try (Connection connection = DriverManager.getConnection(globals.CONNECTION_STRING)) {
			PreparedStatement ps = connection.prepareStatement("Select ID,Name From Courses WHERE Courses.Name = ?");
			ps.setString(1, courseName);
			ResultSet rs = ps.executeQuery();
			return !rs.next() ? Course.INVALID_COURSE : new Course(rs.getString(2), rs.getInt(1));
		}
	}

	private static HttpResponseMessage getPostrequisites(HttpRequestMessage<Optional<String>> s, Course c)
			throws SQLException {
		try (Connection connection = DriverManager.getConnection(globals.CONNECTION_STRING)) {
			PreparedStatement ps = connection.prepareStatement(
					"Select ID,Name From ( SELECT PostrequisiteID FROM Postrequisites WHERE CourseID = ? ) as PostIDs LEFT JOIN Courses ON PostIDs.PostrequisiteID = Courses.ID;");
			ps.setInt(1, c.getId());
			ResultSet rs = ps.executeQuery();
			return rs.isBeforeFirst()
					? TableResponse.quaryTableResponse(s, "The postrequisites for " + c.getName() + " are:", rs)
					: utils.createWebhookResponseContent("There are no postrequisites for " + c.getName() + ".", s);
		}
	}
}
