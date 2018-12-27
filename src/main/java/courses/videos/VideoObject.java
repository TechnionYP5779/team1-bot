package courses.videos;

public class VideoObject {
	int courseNum;

	String filmingDate = null;

	public int getCourseNum() {
		return courseNum;
	}

	public void setCourseNum(int courseNum) {
		this.courseNum = courseNum;
	}

	public String getFilmingDate() {
		return filmingDate;
	}

	public void setFilmingDate(String filmingDate) {
		this.filmingDate = filmingDate;
	}

	public String getCourseType() {
		return courseType;
	}

	public void setCourseType(String courseType) {
		this.courseType = courseType;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	String courseType = null;

	String link;
}
