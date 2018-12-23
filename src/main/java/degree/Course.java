package degree;

public class Course {
	//Can only work with course numbers for now, because it is with Hebrew in Tadpis
	private final String courseNum;
	private boolean isProject;
	private boolean isListA;
	private boolean isListB;
	private boolean isRequired;
	private double points;
	
	public Course(String courseNum, double points) {
		this.courseNum = courseNum;
		this.points = points;
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
		
	private String getCourseNum() {
		return this.courseNum;
	}
	
	private double getPoints() {
		return this.points;
	}
	
	@Override
	public String toString() {
		StringBuilder string = new StringBuilder();
		string.append(courseNum);
		string.append(" - ");
		string.append(points);
		return string + "";	
	}	
}
