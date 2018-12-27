package responses;

import java.util.Optional;

public class BrowsingCarouselItem {
	private String title;
	private Optional<String> description;
	private Optional<String> footer;
	private Optional<String> imgUrl;
	private Optional<String> imgText;
	private String url;

	public BrowsingCarouselItem(String title, String description, String footer, String imgUrl, String imgText,
			String url) {
		this.title = title;
		this.description = Optional.ofNullable(description);
		this.footer = Optional.ofNullable(footer);
		this.imgUrl = Optional.ofNullable(imgUrl);
		this.imgText = Optional.ofNullable(imgText);
		this.url = url;
	}

	public String getTitle() {
		return title;
	}

	public Optional<String> getDescription() {
		return description;
	}

	public Optional<String> getFooter() {
		return footer;
	}

	public Optional<String> getImgUrl() {
		return imgUrl;
	}

	public Optional<String> getImgText() {
		return imgText;
	}

	public String getUrl() {
		return url;
	}
}
