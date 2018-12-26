package responses;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.json.JSONArray;
import org.json.JSONObject;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;

public class SuggestionChips {
	private static int maxChipsNum = 8;
	private static int maxChipLength = 20;
	private static List<String> moreSuggestions;
	
	public static HttpResponseMessage filterSuggestionChips(HttpRequestMessage<Optional<String>> s, String[] suggestions, int numSuggestions,String displayText) {
		boolean needMore = ( numSuggestions > maxChipsNum ) ? true : false;
		int possibleChipsHint = ( numSuggestions > maxChipsNum ) ? maxChipsNum : numSuggestions; 
		moreSuggestions = new ArrayList<>();
		JSONArray suggestionsArray = new JSONArray();
		for(int i=0; i < possibleChipsHint; i++) {
			if(suggestions[i].length() > maxChipLength)// can't display this as hint
			{
				moreSuggestions.add(suggestions[i]);
				possibleChipsHint += 1;
			}
			else
				suggestionsArray.put(new JSONObject().put("title",suggestions[i]));
		}
		
		for(int i = possibleChipsHint; i < numSuggestions; i++) { //we'll save this for later
			moreSuggestions.add(suggestions[i]);
		}
		
		if(needMore) {
			suggestionsArray.put(new JSONObject().put("title", "allOptions"));
		}
		
		JSONArray items = new JSONArray()
				.put(new JSONObject()
						.put("simpleResponse", new JSONObject()
								.put("textToSpeech",displayText)
								.put("displayText",displayText)));
		JSONObject inputPrompt = new JSONObject().put("richInitialPrompt", new JSONObject()
				.put("items", items)
				.put("suggestions",suggestionsArray));
		JSONArray expectedInputs = new JSONArray();
		expectedInputs.put(new JSONObject().put("inputPrompt", inputPrompt).put("possibleIntents", new JSONArray().put(new JSONObject().put("intent", "actions.intent.TEXT"))));
		
		// return the response based on the chips created
		return s.createResponseBuilder(HttpStatus.OK)
				.body(new JSONObject().put("conversationToken", "")
						.put("expectUserResponse", Boolean.TRUE)
						.put("expectedInputs", expectedInputs).toString().getBytes())
				.header("Content-Type", "application/json; charset=UTF-8").header("Accept", "application/json").build();
	}
}
