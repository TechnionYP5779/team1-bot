package responses;

import java.util.Optional;

public class Card {
	private String title;
	private Optional<String> subtitle;
	private String text;
	private Optional<String> imgUrl;
	private Optional<String> button_title;
	private Optional<String> button_url;

	public Card(String title, String subtitle, String text, String imgUrl) {
		this.title = title;
		this.subtitle = Optional.ofNullable(subtitle);
		this.text = text;
		this.imgUrl = Optional.ofNullable(imgUrl);
	}

	public Card(String title, String subtitle, String text, String imgUrl, String button_title, String button_url) {
		this.title = title;
		this.subtitle = Optional.ofNullable(subtitle);
		this.text = text;
		this.imgUrl = Optional.ofNullable(imgUrl);
		this.button_title = Optional.ofNullable(button_title);
		this.button_url = Optional.ofNullable(button_url);
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

	public Optional<String> getButton_title() {
		return button_title;
	}

	public Optional<String> getButton_url() {
		return button_url;
	}
	
}
