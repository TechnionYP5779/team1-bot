package degree;

public class Course {
	private final String courseName;
	private final String courseNum;
	private boolean isProject;
	private boolean isListA;
	private boolean isListB;
	private boolean isRequired;
	
	public Course(String courseNum, String courseName) {
		this.courseName = courseName;
		this.courseNum = courseNum;
		this.isListA = false;
		this.isListB = false;
		this.isProject = false;
		this.isRequired = false;
	}
	
	private void setIsListA(boolean flag) {
		this.isListA = flag;
	}
	
	private void setIsListB(boolean flag) {
		this.isListB = flag;
	}
	
	private void setIsProject(boolean flag) {
		this.isProject = flag;
	}
	
	private void setIsRequired(boolean flag) {
		this.isRequired = flag;
	}
	
	private boolean getIsListA() {
		return this.isListA;
	}
	
	private boolean getIsListB() {
		return this.isListB;
	}
	
	private boolean getIsProject() {
		return this.isProject;
	}
	
	private boolean getIsRequired() {
		return this.isRequired;
	}
	
	private String getCourseName() {
		return this.courseName;
	}
	
	private String getCourseNum() {
		return this.courseNum;
	}
	
}
