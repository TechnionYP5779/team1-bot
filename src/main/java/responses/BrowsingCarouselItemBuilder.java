package responses;

public class BrowsingCarouselItemBuilder {
	private String title;
	private String description;
	private String footer;
	private String imgUrl;
	private String imgText;
	private String url;
	public void setTitle(String title) {
		this.title = title;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public void setFooter(String footer) {
		this.footer = footer;
	}
	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}
	public void setImgText(String imgText) {
		this.imgText = imgText;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	public BrowsingCarouselItem generate() {
		return new  BrowsingCarouselItem( title,  description,  footer,  imgUrl,  imgText,
				   url);
	}
}
