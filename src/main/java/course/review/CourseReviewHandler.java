package course.review;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Optional;
import java.util.Random;

import org.json.JSONObject;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;

import FuncTest.TestFunc.globals;
import FuncTest.TestFunc.utils;
import postrequsites.Course;
import postrequsites.PostrequisiteHandler;

public class CourseReviewHandler {

	public static final int MAX_DIFFICULTY = 5;
	public static final int MIN_DIFFICULTY = 1;
	public static final String COURSE_REVIEWS_DB_NAME = "dbo.CourseReviews";

	public static HttpResponseMessage addCourseReviewByNumber(JSONObject queryResult,
			HttpRequestMessage<Optional<String>> s, ExecutionContext c) {
		try {
			JSONObject parameters = queryResult.getJSONObject("parameters");
			if (!utils.allParametersArePresent(parameters, Arrays.asList("courseNum", "difficulty"))) {
				c.getLogger().info("ERROR::CourseReviewHandler::Missing parameters");
				return utils.createWebhookResponseContent(globals.SERVER_ERROR, s);
			}
			// todo: find how to identify repeating users
			int userId = new Random().nextInt();
			int courseNum = parameters.getInt("courseNum");
			int difficulty = parameters.getInt("difficulty");
			Course course = PostrequisiteHandler.extractCourseInfo(courseNum);
			return !course.isInvalid() ? addReview(s, userId, course, difficulty)
					: utils.createWebhookResponseContent(
							"There is no course with id " + courseNum + ", please try again.", s);
		} catch (SQLException e) {
			c.getLogger().info("ERROR::CourseReviewHandler::SQL Exception");
			c.getLogger().info(e.toString());
			return utils.createWebhookResponseContent(globals.SERVER_ERROR, s);
		}
	}

	private static HttpResponseMessage addReview(HttpRequestMessage<Optional<String>> s, int userId, Course course,
			int difficulty) throws SQLException {
		try (Connection connection = DriverManager.getConnection(globals.CONNECTION_STRING)) {
			PreparedStatement ps = connection
					.prepareStatement("INSERT INTO " + COURSE_REVIEWS_DB_NAME + " VALUES ( ? , ? , ? );");
			ps.setInt(1, course.getId());
			ps.setInt(2, userId);
			ps.setInt(3, difficulty);
			ps.execute();
		}
		return utils.createWebhookResponseContent("Thank you for reviewing the course!", s);
	}

}
