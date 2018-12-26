package postrequsites;

public class Course {
	private String name;
	private int id;
	
	public static Course INVALID_COURSE = new Course("INVALID",-1);
	
	public Course(String name, int id) {
		this.name = name;
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public boolean isInvalid() {
		return id == -1;
	}
	
}
