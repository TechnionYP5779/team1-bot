package FuncTest.TestFunc;

import java.util.Optional;

import org.json.JSONObject;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class VideoAnswers {

	public static HttpResponseMessage checkExists(JSONObject queryResult, HttpRequestMessage<Optional<String>> s,
			ExecutionContext c) {
		c.getLogger().info("=========== GET VIDEO EXISTENCE ===========");
		JSONObject params = queryResult.getJSONObject("parameters");
		
		if(!params.has("coursenumber")) 
			return utils.createWebhookResponseContent("I'm sorry I don't know what course you meant\n", s);
		
		String query_checkVideo = "select * from dbo.Videos where ID = ?";
		
		StringBuilder jsonResult = new StringBuilder();
		
		c.getLogger().info("=========== " + query_checkVideo + " ===========");
		
		try (Connection connection = DriverManager.getConnection(globals.CONNECTION_STRING)) {
			PreparedStatement stmt = connection.prepareStatement(query_checkVideo);
			stmt.setString(1, params.getString("coursenumber"));
			
			ResultSet resultSet = stmt.executeQuery();
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
			c.getLogger().info("=========== ERROR MSG2:" + e.getMessage() + " ===========");
			throw new RuntimeException();
		}
	}

	private static StringBuilder parseVideoResults(ResultSet resultSet, StringBuilder jsonResult, ExecutionContext c) {
		c.getLogger().info("=========== BUILDING RESULT TEXT ===========");
		try {
			for (; resultSet.next(); jsonResult.append("\n")) {
				c.getLogger().info("=========== APPENDING RESULT ===========");
				int courseNum = resultSet.getInt(1);
				
				String filmingDate = trimQuotes(resultSet.getString(2));
				
				String courseType = trimQuotes(resultSet.getString(3));
				
				String link = trimQuotes(resultSet.getString(4));
				
				c.getLogger().info("=========== filmingDate: " + filmingDate + " ===========");
					
				jsonResult.append(
						"I found a video for course " + courseNum + " at " + link + ". ");
				
				if (!courseType.equals(null) && !"NULL".equals(courseType) && !"'NULL'".equals(courseType))
					jsonResult.append("Also, The type of the video is " + courseType + ".");
				
				if (!filmingDate.equals(null) && !"NULL".equals(filmingDate) && !"'NULL'".equals(filmingDate))
					jsonResult.append("And it was filmed at semester " + filmingDate + ".");
			}
			
		} catch (SQLException e) {
			c.getLogger().info("=========== ERROR MSG: " + e.getMessage() + " ===========");
			throw new RuntimeException();
		}

		c.getLogger().info("=========== RESULTS: " + jsonResult.toString() +  " ===========");
		return jsonResult;

	}

	private static String trimQuotes(String str) {
		if(str == null || str.length() == 0) return str;
		
		String newStr = str.charAt(0) != '\'' ? str : str.substring(1);
		
		if(str.charAt(str.length()-1) == '\'') 
			newStr = newStr.substring(0, newStr.length()-1);
		
		return newStr;
	}

}
