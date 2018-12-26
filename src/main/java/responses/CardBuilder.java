package responses;

public class CardBuilder {

	private String title;
	private String subtitle;
	private String text;
	private String imgUrl;

	public CardBuilder() {
		this.title = ""; // Mandatory
		this.subtitle = null;
		this.text = "";// Mandatory
		this.imgUrl = null;
	}

	public Card build() {
		return new Card(title, subtitle, text, imgUrl);
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

}
