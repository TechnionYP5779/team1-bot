package responses;

import java.util.Optional;

import org.json.JSONArray;
import org.json.JSONObject;

import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;

public class CardResponse {

	public static HttpResponseMessage generate(HttpRequestMessage<Optional<String>> s, String textResponse, Card card) {
		// return the response based on the rows created

		JSONObject jsonCard = new JSONObject().put("title", card.getTitle()).put("formattedText", card.getText());
		if (card.getSubtitle().isPresent()) {
			jsonCard.put("subtitle", card.getSubtitle());
		}
		if (card.getImgUrl().isPresent()) {
			jsonCard.put("image", new JSONObject().put("url", card.getImgUrl().get()).put("accessibilityText",
					"Image alternate text"));
		}

		return s.createResponseBuilder(HttpStatus.OK)
				.body(new JSONObject().put("payload", new JSONObject().put("google", new JSONObject()
						.put("expectUserResponse",
								Boolean.TRUE)
						.put("richResponse",
								new JSONObject().put("items",
										new JSONArray()
												.put(new JSONObject().put("simpleResponse",
														new JSONObject().put("textToSpeech", textResponse)))
												.put(new JSONObject().put("basicCard", jsonCard))))
						.put("userStorage", "{\"data\":{}}"))).toString().getBytes())
				.header("Content-Type", "application/json; charset=UTF-8").header("Accept", "application/json").build();

	}
}
