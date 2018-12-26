package course.info;

import java.sql.Connection;
import java.sql.DriverManager;
//import java.sql.ResultSet;
//import java.sql.Statement;
//import java.sql.Connection;
import java.sql.PreparedStatement;
//import java.sql.ResultSet;
import java.sql.SQLException;
//import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import FuncTest.TestFunc.globals;
import Utils.JsonReader;

public class DataToDBLoader {

	public static void loadToDBFromJson(String jsonName) {

		try {
			Connection connection = DriverManager.getConnection(globals.CONNECTION_STRING);
			PreparedStatement pstmt = connection
					.prepareStatement("INSERT INTO Courses VALUES(? ,? ,? ,? ,? ,?, ? ,? ,? ,? ,? ,?)");
			JSONArray courses = JsonReader.ReadJsonFromFile(jsonName);
			for (int c = 0; c < courses.length(); ++c) {
				JSONObject course = courses.getJSONObject(c);
				String facultyName = course.getString("faculty");
				String courseName = course.getString("name");
				Integer courseId = Integer.valueOf(course.getString("id"));
				Integer academicPoints = Integer.valueOf(course.getString("academicPoints"));
				Integer lectureHours = Integer.valueOf(course.getString("lectureHours"));
				Integer tutorialHours = Integer.valueOf(course.getString("tutorialHours"));
				Integer labHours = Integer.valueOf(course.getString("labHours"));
				Integer projectHours = Integer.valueOf(course.getString("projectHours"));
				String examA = (course.getString("examA"));
				String examB = (course.getString("examB"));
				String prequstiteCourses = course.getString("prerequisite courses");
				String linkedCourses = course.getString("linked courses");
				/*
				 * "faculty": "Civil and Environmental Engineering", "name": "Statistics", "id":
				 * "14003", "academicPoints": "3", "lectureHours": "0", "tutorialHours": "2",
				 * "labHours": "0", "projectHours": "0", "examA": "6.2.2019", "examB":
				 * "6.3.2019", "prequstite courses": "104004", "linked courses": ""
				 */

				try {

					pstmt.setString(1, facultyName);
					pstmt.setString(2, courseName);
					pstmt.setInt(3, courseId.intValue());
					pstmt.setInt(4, academicPoints.intValue());
					pstmt.setInt(5, lectureHours.intValue());
					pstmt.setInt(6, tutorialHours.intValue());
					pstmt.setInt(7, labHours.intValue());
					pstmt.setInt(8, projectHours.intValue());
					pstmt.setDate(9, examA.isEmpty() ? null : java.sql.Date.valueOf(examA));
					pstmt.setDate(10, examB.isEmpty() ? null : java.sql.Date.valueOf(examB));
					pstmt.setString(11, prequstiteCourses.isEmpty() ? null : prequstiteCourses);
					pstmt.setString(12, linkedCourses.isEmpty() ? null : linkedCourses);
					System.out.println(courseName);
					pstmt.addBatch();

				} catch (SQLException e1) {
					e1.printStackTrace();
				}

			}
			pstmt.executeBatch();
			connection.commit();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
//
//	//coverts 6.2.2019 to 2019-2-6
//	private static String transformDate(String s) {
//		if (s.isEmpty()) {
//			return s;
//		}
//		return s.split("\\.")[2] + "-" + s.split("\\.")[1] + "-"+s.split("\\.")[0];
//	}
}
