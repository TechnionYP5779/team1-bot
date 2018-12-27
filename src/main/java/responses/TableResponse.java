package responses;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;

import homework.Homework;

public class TableResponse {

	public static HttpResponseMessage businessHoursTableResponse(HttpRequestMessage<Optional<String>> s,
			String businessName, Map<String, String> businessHours, String imgUrl) {
		// create table rows based on input
		JSONArray rowsArray = new JSONArray();
		businessHours.forEach((day, hours) -> {
			JSONArray row = new JSONArray();
			row.put(new JSONObject().put("text", day));
			row.put(new JSONObject().put("text", hours));
			rowsArray.put(new JSONObject().put("cells", row).put("dividerAfter", Boolean.TRUE));
		});

		// return the response based on the rows created
		return s.createResponseBuilder(HttpStatus.OK)
				.body(new JSONObject()
						.put("payload",
								new JSONObject().put("google", new JSONObject().put("expectUserResponse", Boolean.TRUE)
										.put("richResponse", new JSONObject().put("items", new JSONArray()
												.put(new JSONObject().put("simpleResponse",
														new JSONObject().put("textToSpeech",
																"The business hours for " + businessName
																		+ " are shown")))
												.put(new JSONObject().put("tableCard",
														new JSONObject()
																.put("title", businessName + "'s Business hours")
																.put("image", new JSONObject().put("url", imgUrl).put(
																		"accessibilityText", "Image alternate text"))
																.put("rows", rowsArray)
																.put("columnProperties", new JSONArray()
																		.put(new JSONObject().put("header", "Day"))
																		.put(new JSONObject().put("header",
																				"Business Hours")))))))
										.put("userStorage", "{\"data\":{}}")))
						.toString().getBytes())
				.header("Content-Type", "application/json; charset=UTF-8").header("Accept", "application/json").build();

	}

	public static HttpResponseMessage homeworkTableResponse(HttpRequestMessage<Optional<String>> s,
			List<Homework> homeworksList) {
		// create table rows based on input
		JSONArray rowsArray = new JSONArray();
		for (int i = 0; i < homeworksList.size(); ++i) {
			JSONArray row = new JSONArray();
			row.put(new JSONObject().put("text", homeworksList.get(i).getCourseNum()));
			row.put(new JSONObject().put("text", homeworksList.get(i).getCourseName()));
			row.put(new JSONObject().put("text", homeworksList.get(i).getDueDate().toString()));
			// insert into the rows array
			rowsArray.put(new JSONObject().put("cells", row).put("dividerAfter", Boolean.TRUE));
		}

		// return the response based on the rows created
		return s.createResponseBuilder(HttpStatus.OK).body(new JSONObject().put("payload",
				new JSONObject().put("google", new JSONObject().put("expectUserResponse", Boolean.TRUE)
						.put("richResponse", new JSONObject().put("items", new JSONArray()
								.put(new JSONObject().put("simpleResponse",
										new JSONObject().put("textToSpeech", "The upcoming homework are shown")))
								.put(new JSONObject().put("tableCard",
										new JSONObject().put("rows", rowsArray).put("columnProperties",
												new JSONArray().put(new JSONObject().put("header", "Course Number"))
														.put(new JSONObject().put("header", "Course Name"))
														.put(new JSONObject().put("header", "Due Date")))))))
						.put("userStorage", "{\"data\":{}}")))
				.toString().getBytes()).header("Content-Type", "application/json; charset=UTF-8")
				.header("Accept", "application/json").build();

	}

	public static HttpResponseMessage quaryTableResponse(HttpRequestMessage<Optional<String>> s, String textResponse,
			ResultSet rs) {
		// create table rows based on input
		JSONArray rowsArray = new JSONArray();
		JSONArray columns = new JSONArray();
		try {
			int columnCount = rs.getMetaData().getColumnCount();
			for (int i = 1; i <= columnCount; ++i)
				columns.put(new JSONObject().put("header", rs.getMetaData().getColumnName(i)));
			while (rs.next()) {
				JSONArray row = new JSONArray();
				for (int i = 1; i <= columnCount; ++i)
					row.put(new JSONObject().put("text", rs.getString(i)));
				rowsArray.put(new JSONObject().put("cells", row).put("dividerAfter", Boolean.TRUE));
			}
		} catch (JSONException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// return the response based on the rows created
		return s.createResponseBuilder(HttpStatus.OK)
				.body(new JSONObject().put("payload", new JSONObject().put("google", new JSONObject()
						.put("expectUserResponse",
								Boolean.TRUE)
						.put("richResponse", new JSONObject().put("items", new JSONArray()
								.put(new JSONObject().put("simpleResponse",
										new JSONObject().put("textToSpeech", textResponse)))
								.put(new JSONObject().put("tableCard",
										new JSONObject().put("rows", rowsArray).put("columnProperties", columns)))))
						.put("userStorage", "{\"data\":{}}"))).toString().getBytes())
				.header("Content-Type", "application/json; charset=UTF-8").header("Accept", "application/json").build();

	}
}
