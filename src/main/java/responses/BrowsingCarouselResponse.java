package responses;

import java.util.List;
import java.util.Optional;

import org.json.JSONArray;
import org.json.JSONObject;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;

public class BrowsingCarouselResponse {

	public static HttpResponseMessage generate(HttpRequestMessage<Optional<String>> s, String textResponse,
			List<BrowsingCarouselItem> items) {
		JSONArray itemsJSON = itemsToJSONArray(items );

		return s.createResponseBuilder(HttpStatus.OK)
				.body(new JSONObject().put("payload", new JSONObject().put("google", new JSONObject()
						.put("expectUserResponse",
								Boolean.TRUE)
						.put("richResponse", new JSONObject().put("items", new JSONArray()
								.put(new JSONObject().put("simpleResponse",
										new JSONObject().put("textToSpeech", textResponse)))
								.put(new JSONObject().put("carouselBrowse", new JSONObject().put("items", itemsJSON)))))
						.put("userStorage", "{\"data\":{}}"))).toString().getBytes())
				.header("Content-Type", "application/json; charset=UTF-8").header("Accept", "application/json").build();

	}

	private static JSONArray itemsToJSONArray(List<BrowsingCarouselItem> items) {
		JSONArray res = new JSONArray();
		for (BrowsingCarouselItem c : items) {

//			cc.getLogger().info("=========== Before iteration" + " ===========");
			JSONObject o = new JSONObject().put("title", c.getTitle())
					.put("openUrlAction", new JSONObject().put("url", c.getUrl()));
			if (c.getDescription().isPresent())
				o.put("description", c.getDescription().get());
			if (c.getFooter().isPresent())
				o.put("footer", c.getFooter().get());
			if (c.getImgText().isPresent() && c.getImgUrl().isPresent())
				o.put("image",
						new JSONObject().put("url", c.getImgUrl().get()).put("accessibilityText",
								c.getImgText().get()));
			res.put(o);
		}

		return res;
	}

}
