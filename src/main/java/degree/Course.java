package degree;

public class Course {
	//Can only work with course numbers for now, because it is with Hebrew in Tadpis
	private final int courseNum;
	private boolean isProject;
	private boolean isListA;
	private boolean isListB;
	private boolean isRequired;
	private double points;
	
	public Course(int courseNum, double points) {
		this.courseNum = courseNum;
		this.points = points;
		this.isListA = false;
		this.isListB = false;
		this.isProject = false;
		this.isRequired = false;
	}
	
	public void setIsListA(boolean flag) {
		this.isListA = flag;
	}
	
	public void setIsListB(boolean flag) {
		this.isListB = flag;
	}
	
	public void setIsProject(boolean flag) {
		this.isProject = flag;
	}
	
	public void setIsRequired(boolean flag) {
		this.isRequired = flag;
	}
	
	public boolean getIsListA() {
		return this.isListA;
	}
	
	public boolean getIsListB() {
		return this.isListB;
	}
	
	public boolean getIsProject() {
		return this.isProject;
	}
	
	public boolean getIsRequired() {
		return this.isRequired;
	}
		
	public int getCourseNum() {
		return this.courseNum;
	}
	
	public double getPoints() {
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
	
	@Override
	public boolean equals(Object obj) {
		return ((Course)obj).getCourseNum() == courseNum;
	}
}
