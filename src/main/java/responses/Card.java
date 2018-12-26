package responses;

import java.util.Optional;

public class Card {
	private String title;
	private Optional<String> subtitle;
	private String text;
	private Optional<String> imgUrl;

	public Card(String title, String subtitle, String text, String imgUrl) {
		this.title = title;
		this.subtitle = Optional.ofNullable(subtitle);
		this.text = text;
		this.imgUrl = Optional.ofNullable(imgUrl);
	}

	public String getTitle() {
		return title;
	}

	public Optional<String> getSubtitle() {
		return subtitle;
	}

	public String getText() {
		return text;
	}

	public Optional<String> getImgUrl() {
		return imgUrl;
	}
}
