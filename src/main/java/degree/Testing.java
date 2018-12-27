package degree;

import java.sql.SQLException;

import homework.LoginCredentials;

public class Testing {
	
	
	
	public static void main(String[] args) {
		CatalogChecker cc = new CatalogChecker(null, new LoginCredentials("ID", "CODE"));
		try {
			System.out.println(cc.degreeCompletionCompute().toString());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//CourseListGetter clg = new CourseListGetter();
		
		
		
		
	}

}
