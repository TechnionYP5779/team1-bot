package responses;

import java.util.List;
import java.util.Optional;
import org.json.JSONArray;
import org.json.JSONObject;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;

import homework.Homework;

public class TableResponse {
	public static HttpResponseMessage homeworkTableResponse(HttpRequestMessage<Optional<String>> s, List<Homework> homeworksList) {
		// create table rows based on input
		JSONArray rowsArray = new JSONArray();
		for(int i=0; i<homeworksList.size(); ++i) {
			JSONArray row = new JSONArray();
			row.put(new JSONObject().put("text", homeworksList.get(i).getCourseNum()));
			row.put(new JSONObject().put("text", homeworksList.get(i).getCourseName()));
			row.put(new JSONObject().put("text", homeworksList.get(i).getDueDate().toString()));
			// insert into the rows array
			rowsArray.put(new JSONObject().put("cells",row).put("dividerAfter", Boolean.TRUE));
		}
		
		// return the response based on the rows created
		return s.createResponseBuilder(HttpStatus.OK)
				.body(new JSONObject().put("payload",
						new JSONObject().put("google", new JSONObject().put("expectUserResponse", Boolean.TRUE)
								.put("richResponse", new JSONObject().put("items", new JSONArray()
										.put(new JSONObject().put("simpleResponse",
												new JSONObject().put("textToSpeech", "The upcoming homework are shown")))
										.put(new JSONObject().put("tableCard", new JSONObject()
												.put("rows", rowsArray)
												.put("columnProperties",
														new JSONArray()
																.put(new JSONObject().put("header", "Course Number"))
																.put(new JSONObject().put("header", "Course Name"))
																.put(new JSONObject().put("header", "Due Date")))))))
								.put("userStorage", "{\"data\":{}}"))).toString().getBytes())
				.header("Content-Type", "application/json; charset=UTF-8").header("Accept", "application/json").build();
		
	}
}
