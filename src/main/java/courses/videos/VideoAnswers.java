package courses.videos;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.json.JSONObject;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;

import FuncTest.TestFunc.globals;
import FuncTest.TestFunc.utils;
import responses.BrowsingCarouselItem;
import responses.BrowsingCarouselItemBuilder;
import responses.BrowsingCarouselResponse;
import responses.CardBuilder;
import responses.CardResponse;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class VideoAnswers {

	private static final String VIDEO_IMAGE_URL = "https://res-1.cloudinary.com/crunchbase-production/image/upload/c_lpad,h_256,w_256,f_auto,q_auto:eco/v1415786418/wglyb8lwnrycvmqmbswx.jpg";

	public static HttpResponseMessage checkExists(JSONObject queryResult, HttpRequestMessage<Optional<String>> s,
			ExecutionContext c) {
		c.getLogger().info("=========== GET VIDEO EXISTENCE ===========");
		JSONObject params = queryResult.getJSONObject("parameters");
		
		if(!params.has("coursenumber")) 
			return utils.createWebhookResponseContent("I'm sorry I don't know what course you meant\n", s);
		
		String query_checkVideo = "select * from dbo.Videos where ID = ?";
		
		StringBuilder jsonResult = new StringBuilder();
		
//		c.getLogger().info("=========== " + query_checkVideo + " ===========");
		List<VideoObject> results = new ArrayList<>();
		try (Connection connection = DriverManager.getConnection(globals.CONNECTION_STRING)) {
			PreparedStatement stmt = connection.prepareStatement(query_checkVideo);
			stmt.setString(1, params.getString("coursenumber"));
			
			ResultSet resultSet = stmt.executeQuery();
			if (!resultSet.isBeforeFirst()) {
//				c.getLogger().info("=========== NO RESULTS ===========");
				jsonResult.append(globals.NO_VIDEO_FOUND_ERROR);
				return utils.createWebhookResponseContent(jsonResult.toString(), s);
			} else {
//				c.getLogger().info("=========== FOUND RESULTS ===========");
//				jsonResult.append("Here's what I found:\n");
				results = parseVideoResults(resultSet, c);
			}

			connection.close();
			return createAppropriateResponse(s,results);

//

//			return utils.createWebhookResponseContent(jsonResult.toString(), s);

		} catch (Exception e) {
			c.getLogger().info("=========== ERROR MSG2:" + e.getMessage() + " ===========");
			throw new RuntimeException();
		}
	}

	private static HttpResponseMessage createAppropriateResponse(HttpRequestMessage<Optional<String>> s, List<VideoObject> results ) {

		if (results.size() == 1) {
			VideoObject v = results.get(0);
			CardBuilder cb = new CardBuilder();
			cb.setButton_url(v.getLink());
			cb.setButton_title("click to visit the video webpage");
			cb.setImgUrl(VIDEO_IMAGE_URL);
			cb.setTitle("Video results for "+v.courseNum);
			cb.setSubtitle(v.courseType == null ? null : "Type : "+v.courseType);
			cb.setText(v.filmingDate);
			return CardResponse.generateWithButton(s, "Hooray! I found a video result", cb.build());
		}
		else {
			List<BrowsingCarouselItem> items = new ArrayList<>();
			for (VideoObject v : results) {
				BrowsingCarouselItemBuilder cb = new BrowsingCarouselItemBuilder();
				cb.setDescription(v.courseType == null ? null : "Type : "+v.courseType);
				cb.setFooter(v.filmingDate);
				cb.setImgText("");
				cb.setImgUrl(VIDEO_IMAGE_URL);
				cb.setUrl(v.getLink());
				cb.setTitle("Video results for "+v.courseNum);
				items.add(cb.generate());
			}

			return BrowsingCarouselResponse.generate(s,"Hooray! I found some video results", items );
		}
	}

	private static List<VideoObject> parseVideoResults(ResultSet resultSet, ExecutionContext c) {
		c.getLogger().info("=========== BUILDING RESULT TEXT ===========");

		List<VideoObject> response= new ArrayList<>(); 
		try {
			for (; resultSet.next();) {
				VideoObject o = new VideoObject();
				c.getLogger().info("=========== APPENDING RESULT ===========");
				int courseNum = resultSet.getInt(1);
				
				String filmingDate = trimQuotes(resultSet.getString(2));
				
				String courseType = trimQuotes(resultSet.getString(3));
				
				String link = trimQuotes(resultSet.getString(4));
				
				c.getLogger().info("=========== filmingDate: " + filmingDate + " ===========");
				o.setCourseNum(courseNum);
				o.setLink(link);
				c.getLogger().info("=========== link: " + link+  " ===========");

				if (!courseType.equals(null) && !"NULL".equals(courseType) && !"'NULL'".equals(courseType))
					o.setCourseType(courseType);
				if (!filmingDate.equals(null) && !"NULL".equals(filmingDate) && !"'NULL'".equals(filmingDate))
					o.setFilmingDate(filmingDate);
				
				response.add(o);
			}
			
		} catch (SQLException e) {
			c.getLogger().info("=========== ERROR MSG: " + e.getMessage() + " ===========");
			throw new RuntimeException();
		}

//		c.getLogger().info("=========== RESULTS: " + jsonResult.toString() +  " ===========");
		return response;

	}

	private static String trimQuotes(String str) {
		if(str == null || str.length() == 0) return str;
		
		String newStr = str.charAt(0) != '\'' ? str : str.substring(1);
		
		if(str.charAt(str.length()-1) == '\'') 
			newStr = newStr.substring(0, newStr.length()-1);
		
		return newStr;
	}

}
