package degree;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.microsoft.azure.functions.ExecutionContext;

import FuncTest.TestFunc.globals;
import FuncTest.TestFunc.utils;
import homework.LoginCredentials;

public class CatalogChecker {
	
	ExecutionContext c;
	List<Course> myCourses;
	List<Course> coreCourses;
	
	
	public CatalogChecker(ExecutionContext c, LoginCredentials creds) {
		this.c = c;
		CourseListGetter clg = new CourseListGetter(creds);
		myCourses = clg.getCourseList();
	}
	
		
	private List<Course> getListA() throws SQLException{
		List<Course> listA = new ArrayList<Course>();
		String query_getListA = "select * from dbo.CourseRoles where CourseRole = 'listA'";
		StringBuilder jsonResult = new StringBuilder();
		
		//c.getLogger().info("=========== " + query_getMandatory + " ===========");
		
		try (Connection connection = DriverManager.getConnection(globals.CONNECTION_STRING)) {
			PreparedStatement stmt = connection.prepareStatement(query_getListA);
			ResultSet resultSet = stmt.executeQuery();
			if (!resultSet.isBeforeFirst()) {
				//c.getLogger().info("=========== NO RESULTS ===========");
				jsonResult.append(globals.MISSING_MANDATORY_COURSES_ERROR);
			} else {
				//c.getLogger().info("=========== FOUND RESULTS ===========");
				while(resultSet.next()) {
					int courseNumber = resultSet.getInt(1);
					listA.add(new Course(courseNumber, -1));
				}
			}

			connection.close();
			//c.getLogger().info("=========== RETURNING RESULTS ===========");
		}		
		return listA;
	}
	
	private List<Course> getMyListA() throws SQLException {
		List<Course> listA = getListA();
		List<Course> myListA = intersection(listA, myCourses);
		return myListA;
	}

	private List<Course> getListB() throws SQLException{
		List<Course> listB = new ArrayList<Course>();
		String query_getListB = "select * from dbo.CourseRoles where CourseRole = 'listB'";
		StringBuilder jsonResult = new StringBuilder();
		
		//c.getLogger().info("=========== " + query_getMandatory + " ===========");
		
		try (Connection connection = DriverManager.getConnection(globals.CONNECTION_STRING)) {
			PreparedStatement stmt = connection.prepareStatement(query_getListB);
			ResultSet resultSet = stmt.executeQuery();
			if (!resultSet.isBeforeFirst()) {
				//c.getLogger().info("=========== NO RESULTS ===========");
				jsonResult.append(globals.MISSING_MANDATORY_COURSES_ERROR);
			} else {
				//c.getLogger().info("=========== FOUND RESULTS ===========");
				while(resultSet.next()) {
					int courseNumber = resultSet.getInt(1);
					listB.add(new Course(courseNumber, -1));
				}
			}

			connection.close();
			//c.getLogger().info("=========== RETURNING RESULTS ===========");
		}		
		return listB;
	}
	
	private List<Course> getMyListB() throws SQLException {
		List<Course> listB = getListB();
		List<Course> myListB = intersection(listB, myCourses);
		return myListB;
	}

	private List<Course> getProject() throws SQLException {
		List<Course> project = new ArrayList<Course>();
		String query_getProject = "select * from dbo.CourseRoles where CourseRole = 'project'";
		StringBuilder jsonResult = new StringBuilder();
		
		//c.getLogger().info("=========== " + query_getMandatory + " ===========");
		
		try (Connection connection = DriverManager.getConnection(globals.CONNECTION_STRING)) {
			PreparedStatement stmt = connection.prepareStatement(query_getProject);
			ResultSet resultSet = stmt.executeQuery();
			if (!resultSet.isBeforeFirst()) {
				//c.getLogger().info("=========== NO RESULTS ===========");
				jsonResult.append(globals.MISSING_MANDATORY_COURSES_ERROR);
			} else {
				//c.getLogger().info("=========== FOUND RESULTS ===========");
				while(resultSet.next()) {
					int courseNumber = resultSet.getInt(1);
					project.add(new Course(courseNumber, -1));
				}
			}

			connection.close();
			//c.getLogger().info("=========== RETURNING RESULTS ===========");
		}		
		return project;
	}
	
	private List<Course> getMyProject() throws SQLException{
		List<Course> project = getProject();
		return intersection(project, myCourses);
	}
	
	private List<Course> getMandatory() throws SQLException {
		List<Course> mandatory = new ArrayList<Course>();
		String query_getProject = "select * from dbo.CourseRoles where CourseRole = 'mandatory'";
		StringBuilder jsonResult = new StringBuilder();
		
		//c.getLogger().info("=========== " + query_getMandatory + " ===========");
		
		try (Connection connection = DriverManager.getConnection(globals.CONNECTION_STRING)) {
			PreparedStatement stmt = connection.prepareStatement(query_getProject);
			ResultSet resultSet = stmt.executeQuery();
			if (!resultSet.isBeforeFirst()) {
				//c.getLogger().info("=========== NO RESULTS ===========");
				jsonResult.append(globals.MISSING_MANDATORY_COURSES_ERROR);
			} else {
				//c.getLogger().info("=========== FOUND RESULTS ===========");
				while(resultSet.next()) {
					int courseNumber = resultSet.getInt(1);
					mandatory.add(new Course(courseNumber, -1));
				}
			}

			connection.close();
			//c.getLogger().info("=========== RETURNING RESULTS ===========");
		}		
		return mandatory;
	}
	
	private List<Course> getCore() throws SQLException{
		List<Course> core = new ArrayList<Course>();
		String query_getCore = "select * from dbo.CourseRoles where CourseRole = 'core'";
		StringBuilder jsonResult = new StringBuilder();
		
		//c.getLogger().info("=========== " + query_getMandatory + " ===========");
		
		try (Connection connection = DriverManager.getConnection(globals.CONNECTION_STRING)) {
			PreparedStatement stmt = connection.prepareStatement(query_getCore);
			ResultSet resultSet = stmt.executeQuery();
			if (!resultSet.isBeforeFirst()) {
				//c.getLogger().info("=========== NO RESULTS ===========");
				jsonResult.append(globals.MISSING_MANDATORY_COURSES_ERROR);
			} else {
				//c.getLogger().info("=========== FOUND RESULTS ===========");
				while(resultSet.next()) {
					int courseNumber = resultSet.getInt(1);
					core.add(new Course(courseNumber, -1));
				}
			}

			connection.close();
			//c.getLogger().info("=========== RETURNING RESULTS ===========");
		}		
		return core;
	}
	
	private List<Course> getMyCore() throws SQLException{
		List<Course> core = getCore();
		List<Course> myCore = intersection(core, myCourses).stream().limit(3).collect(Collectors.toList());
		return myCore;
	}
	
	public double sumPoints(List<Course> list){
		return list.stream().map(c -> c.getPoints()).reduce(0.0, (x,y) -> x+y);
	}
	
	public List<Course> getMissingMandatory() {
		List<Course> mandatory = null;
		try {
			mandatory = getMandatory();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		List<Course> diff = difference(mandatory, myCourses);
		if(myCourses.contains(new Course(234145, -1))){
			diff.remove(new Course(44145, -1));
		}
		if(myCourses.contains(new Course(44145, -1))){
			diff.remove(new Course(234145, -1));
		}
		return diff;
	}
	
	//A - B
	private List<Course> difference(List<Course> A, List<Course> B) {
		if(A == null || B == null) {
			return null;
		}
	    List<Course> rtnList = new ArrayList<>();
	    for(Course dto : A) {
	        if(!B.contains(dto)) {
	            rtnList.add(dto);
	        }
	    }
	    return rtnList;
	}
	
	private List<Course> intersection(List<Course> A, List<Course> B) {
		if(A == null || B == null) {
			return null;
		}
	    List<Course> rtnList = new ArrayList<>();
	    for(Course dto : A) {
	    	for(Course dtoB : B) {
		        if(dtoB.getCourseNum() == dto.getCourseNum()) {
		            rtnList.add(dtoB);
		            break;
		        }
	    	}
	    }
	    return rtnList;
	}
	
	public List<Course> computeMissingMandatory(List<Course> courses) {
		List<Course> mandatory = getMissingMandatory();
		
		return courses;
	}
	
	StringBuilder degreeCompletionCompute(List<Course> courses) throws SQLException {
		StringBuilder jsonResult = new StringBuilder();
		
		List<Course> missingMandatory = computeMissingMandatory(courses);
		if(missingMandatory.size() != 0) {
			jsonResult.append("It seems that you haven't completed the following mandatory courses:\n");
			for(Course course : missingMandatory) {
				jsonResult.append(course.getCourseNum() + " ");
			}
		} else {
			jsonResult.append("You've completed the mandatory courses");
		}
		
		List<Course> myCore = getMyCore();
		if(myCore.size() < 3) {
			jsonResult.append("It seems that you haven't completed the requirement for core courses\n");
			jsonResult.append("You possess the following core classes:\n");
			for (Course corecourse : myCore) {
				jsonResult.append(corecourse.getCourseNum() + " ");
			}
			
		} else {
			jsonResult.append("You've completed the core courses");
		}
		List<Course> myProject = getMyProject();
		if(myProject.size() == 0) {
			jsonResult.append("It seems that you haven't completed the requirement for a project\n");
		}
		
		List<Course> myListA = getListA();
		double myListAPoints = sumPoints(myListA);
		if(myListAPoints < 15) {
			jsonResult.append("It seems that you haven't completed the requirement for List A, you are missing "+(15 - myListAPoints)+" points\n");
		}
		
		List<Course> myListB = getListB();
		double myListBPoints = sumPoints(myListB);
		if(myListAPoints + myListBPoints < 28.5) {
			jsonResult.append("It seems that you haven't completed the requirement for electives, you are missing "+(28.5 - myListAPoints - myListBPoints)+" points\n");
		}
		
		
		
		
		
		return null;
	}

}
