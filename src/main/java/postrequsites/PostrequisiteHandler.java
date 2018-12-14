package postrequsites;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.json.JSONObject;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;

import FuncTest.TestFunc.globals;
import FuncTest.TestFunc.utils;
import responses.TableResponse;

public class PostrequisiteHandler {


	public static HttpResponseMessage getPostrequisitesByNumber(JSONObject queryResult,
			HttpRequestMessage<Optional<String>> s, ExecutionContext c) {
		try {
			JSONObject parameters = queryResult.getJSONObject("parameters");
			List<String> requiredParameterNames = new ArrayList<>();
			requiredParameterNames.add("courseNum");
			if (!utils.allParametersArePresent(parameters, requiredParameterNames))
				return utils.createWebhookResponseContent("Missing parametrs. Please report this", s);
			int courseNum = parameters.getInt("courseNum");
			Course course = extractCourseInfo(courseNum);
			if(course.isInvalid()) 
				return utils.createWebhookResponseContent("There is no course with id " +courseNum + ", please try again." , s);
			return getPostrequisites(s, course);
		} catch (SQLException e) {
			e.printStackTrace();
			return utils.createWebhookResponseContent("SQL Exception. Please report this", s);

		}
	}
	
	private static Course extractCourseInfo(int courseNum) throws SQLException {
		try (Connection connection = DriverManager.getConnection(globals.CONNECTION_STRING)) {
			PreparedStatement ps = connection.prepareStatement("Select ID,Name From Courses WHERE Courses.ID = ?;");
			ps.setInt(1, courseNum);
			ResultSet rs =  ps.executeQuery();
			if(!rs.next())
				return Course.INVALID_COURSE;
			return new Course(rs.getString(2),rs.getInt(1));
		}
	}
	
//	private static Course extractCourseWithSimilarNames(String courseName) throws SQLException {
//		try (Connection connection = DriverManager.getConnection(globals.CONNECTION_STRING)) {
//			PreparedStatement ps = connection.prepareStatement("Select ID,Name From Courses WHERE Courses.Name LIKE '%?%';");
//			ps.setString(1, courseName);
//			ResultSet rs = ps.executeQuery();
//			if(!rs.next())
//				return Course.INVALID_COURSE;
//			return new Course(rs.getString(2),rs.getInt(1));
//		}
//	}

	public static HttpResponseMessage getPostrequisitesByName(JSONObject queryResult,
			HttpRequestMessage<Optional<String>> s, ExecutionContext c) {

		try {
			JSONObject parameters = queryResult.getJSONObject("parameters");
			List<String> requiredParameterNames = new ArrayList<>();
			requiredParameterNames.add("courseName");
			if (!utils.allParametersArePresent(parameters, requiredParameterNames))
				return utils.createWebhookResponseContent("Missing parametrs. Please report this", s);
			String courseName = parameters.getString("courseName");
			Course course = extractCourseInfo(courseName);
			if(course.isInvalid()) 
				return utils.createWebhookResponseContent("There is no course named " +courseName+ ", please try again." , s);

			return getPostrequisites(s, course);
		} catch(SQLException e) {
			e.printStackTrace();
			return utils.createWebhookResponseContent("SQL Exception. Please report this", s);
		}
	}
	
	private static Course extractCourseInfo(String courseName) throws SQLException {
		try (Connection connection = DriverManager.getConnection(globals.CONNECTION_STRING)) {
			PreparedStatement ps = connection.prepareStatement("Select ID,Name From Courses WHERE Courses.Name = ?");
			ps.setString(1, courseName);
			ResultSet rs = ps.executeQuery();
			if(!rs.next())
				return Course.INVALID_COURSE;
			return new Course(rs.getString(2),rs.getInt(1));
		}
	}

	private static HttpResponseMessage getPostrequisites(HttpRequestMessage<Optional<String>> s, Course c) throws SQLException {
		try (Connection connection = DriverManager.getConnection(globals.CONNECTION_STRING)) {
			PreparedStatement ps = connection.prepareStatement("Select ID,Name From ( SELECT PostrequisiteID FROM Postrequisites WHERE CourseID = ? ) as PostIDs LEFT JOIN Courses ON PostIDs.PostrequisiteID = Courses.ID;");
			ps.setInt(1, c.getId());
			ResultSet rs = ps.executeQuery();
			if(!rs.isBeforeFirst()) 
				return utils.createWebhookResponseContent("There are no postrequisites for " +c.getName()+ "." , s);
			return TableResponse.quaryTableResponse(s, "The postrequisites for " + c.getName() +" are:", rs);
		}
	}
}
