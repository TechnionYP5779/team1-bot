package degree;

import java.util.ArrayList;
import java.util.List;

import homework.LoginCredentials;
import parsing.CatalogParser;

public class Testing {
	
	
	
	public static void main(String[] args) {
		double sum = 0;
		CatalogChecker cc = new CatalogChecker(null, new LoginCredentials("<ID>", "<8-digit-code>"));
		List<Course> courses = cc.getMissingMandatory();
		for(Course c : courses) {
			System.out.println(c.getCourseNum());
		}
		System.out.println(courses.size());
		//CourseListGetter clg = new CourseListGetter();
		
		
		
		
	}

}
