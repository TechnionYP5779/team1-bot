package degree;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

import homework.LoginCredentials;
import homework.WrongCredentialsException;

public class CourseListGetter {
	private List<Course> courseList = new ArrayList<>();
	
	public CourseListGetter(LoginCredentials creds) {
		java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
		try {
			this.courseList = getCourseList(creds);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private List<Course> getCourseList(LoginCredentials c) throws IOException{
		try(WebClient webClient = new WebClient()) {
			HtmlPage page = webClient.getPage("http://ug3.technion.ac.il/Tadpis.html");
			HtmlForm form = page.getFormByName("SignonForm");
			HtmlSubmitInput submitButton = form.getInputByName("submit");
			HtmlTextInput usernameField = form.getInputByName("userid");
			HtmlPasswordInput passwordField = form.getInputByName("password");
			usernameField.type(c.getUsername());
			passwordField.type(c.getPassword());
			HtmlPage page2 = submitButton.click();
			if(page2.getUrl() == page2.getUrl()) {
				throw new WrongCredentialsException();
			}
			List<HtmlTable> semesterTables = page2.getByXPath("//table[@class='tab']");
			return extractCourseListFromTables(semesterTables);
		}				
	}
	
	private List<Course> extractCourseListFromTables(List<HtmlTable> semesterTables){
		List<Course> courseList = new ArrayList<>();
		for(HtmlTable table : semesterTables) {
			courseList.addAll(extractCourseListFromTable(table));
		}
		return courseList;
	}
	
	private List<Course> extractCourseListFromTable(HtmlTable semesterTable){
		List<Course> courseList = new ArrayList<>();
		List<HtmlTableRow> courseTableRows = semesterTable.getRows();
		for(HtmlTableRow courseRow : courseTableRows) {
			courseList.add(extractCourseFromRow(courseRow));
		}
		return courseList;	
	}
	
	private Course extractCourseFromRow(HtmlTableRow courseRow) {
		String coursePoints = courseRow.getCell(1).asText();
		String courseName = courseRow.getCell(2).asText();
		String courseNumber = extractCourseNumber(courseName);
		if(courseNumber.equals("")) {
			return null;
		}
		return new Course(courseNumber, Double.valueOf(coursePoints));
	}
	
	private String extractCourseNumber(final String in) {
		Pattern p = Pattern.compile("(\\d{6})");
		Matcher m = p.matcher(in);
		if (m.find()) {
			return m.group(0);
		}
		return "";
	}
}
