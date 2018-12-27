package responses;

public class CardBuilder {

	private String title;
	private String subtitle;
	private String text;
	private String imgUrl;

	private String button_url;
	private String button_title;

	public CardBuilder() {
		this.title = ""; // Mandatory
		this.subtitle = null;
		this.text = "";// Mandatory
		this.imgUrl = null;
		this.button_title = "Click to view";
		this.button_url =null;
	}

	public Card build() {
		if (button_url == null) {
			return new Card(title, subtitle, text, imgUrl);
		}else {
			return new Card(title, subtitle, text, imgUrl,button_title,button_url);
		}
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}

	public void setText(String text) {
		this.text = text;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public String getButton_url() {
		return button_url;
	}

	public void setButton_url(String button_url) {
		this.button_url = button_url;
	}

	public String getButton_title() {
		return button_title;
	}

	public void setButton_title(String button_title) {
		this.button_title = button_title;
	}

}
