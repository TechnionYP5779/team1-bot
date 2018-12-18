package FuncTest.TestFunc;

import java.util.Optional;

import org.json.JSONObject;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class VideoAnswers {

	public static HttpResponseMessage checkExists(JSONObject queryResult, HttpRequestMessage<Optional<String>> s,
			ExecutionContext c) {
		c.getLogger().info("=========== GET VIDEO EXISTENCE ===========");
		JSONObject params = queryResult.getJSONObject("parameters");
		
		if(!params.has("coursenumber")) 
			return utils.createWebhookResponseContent("I'm sorry I don't know what course you meant\n", s);
		
		String query_checkVideo = "select * from dbo.Videos where ID = " + params.getString("coursenumber");
		StringBuilder jsonResult = new StringBuilder();
		
		try (Connection connection = DriverManager.getConnection(globals.CONNECTION_STRING)) {
			ResultSet resultSet = connection.createStatement().executeQuery(query_checkVideo);
			if (!resultSet.isBeforeFirst()) {
				c.getLogger().info("=========== NO RESULTS ===========");
				jsonResult.append(globals.NO_VIDEO_FOUND_ERROR);
			} else {
				c.getLogger().info("=========== FOUND RESULTS ===========");
				jsonResult.append("Here's what I found:\n");
				jsonResult = parseVideoResults(resultSet, jsonResult, c);
			}

			connection.close();
			c.getLogger().info("=========== RETURNING RESULTS ===========");
			return utils.createWebhookResponseContent(jsonResult.toString(), s);

		} catch (Exception e) {
			c.getLogger().info("=========== " + e.getMessage() + " ===========");
			throw new RuntimeException();
		}
	}

	private static StringBuilder parseVideoResults(ResultSet resultSet, StringBuilder jsonResult, ExecutionContext c) {
		c.getLogger().info("=========== BUILDING RESULT TEXT ===========");
		try {
			while(resultSet.next()) 
				jsonResult.append("a " + resultSet.getString(3) + " video for course " + resultSet.getInt(1)
				 + " was filmed in semester " + resultSet.getString(2) + "\n");
			
		} catch (SQLException e) {
			c.getLogger().info("=========== " + e.getMessage() + " ===========");
			throw new RuntimeException();
		}

		c.getLogger().info("=========== RESULTS: " + jsonResult.toString() +  " ===========");
		return jsonResult;

	}

}
