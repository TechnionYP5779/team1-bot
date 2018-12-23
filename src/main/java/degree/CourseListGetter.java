package degree;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

import homework.LoginCredentials;

public class CourseListGetter {
	private static String DID_NOT_COMPLETE = "לא השלים";
	private static String DID_NOT_DO = "-";
	private List<Course> courseList = new ArrayList<>();
	
	public CourseListGetter(LoginCredentials creds) {
		try {
			java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
			this.courseList = getCourseList(creds);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private List<Course> getCourseList(LoginCredentials c) throws IOException{
		List<HtmlTable> semesterTables;
		try(WebClient webClient = new WebClient(BrowserVersion.CHROME)) {
			webClient.getOptions().setUseInsecureSSL(true);
			webClient.getOptions().setRedirectEnabled(true);
			webClient.getOptions().setJavaScriptEnabled(true);
			webClient.getCookieManager().setCookiesEnabled(true);
			webClient.getOptions().setThrowExceptionOnScriptError(false);
			HtmlPage page = webClient.getPage("https://techmvs.technion.ac.il/cics/wmn/wmngrad?afiflzrc&ORD=1");
			//If not logged in
			try {
				HtmlForm form = page.getFormByName("SignonForm");
				HtmlSubmitInput submitButton = form.getInputByValue("Signon");
				HtmlTextInput usernameField = form.getInputByName("userid");
				HtmlPasswordInput passwordField = form.getInputByName("password");
				usernameField.type(c.getUsername());
				passwordField.type(c.getPassword());
				semesterTables = ((DomNode) submitButton.click()).getByXPath("//table[@class='tab']");
			}catch(ElementNotFoundException e) {
				semesterTables = page.getByXPath("//table[@class='tab']");
			}
			return extractCourseListFromTables(semesterTables);
		}
	}
	
	private boolean checkCourseAlreadyExist(Course toCheck, List<Course> courseList) {
		for(Course course : courseList)
			if (course.getCourseNum().equals(toCheck.getCourseNum()))
				return true;
		return false;
	}
	
	private List<Course> extractCourseListFromTables(List<HtmlTable> semesterTables){
		List<Course> courseList = new ArrayList<>();
		for(HtmlTable table : semesterTables)
			courseList.addAll(extractCourseListFromTable(table));
		return courseList;
	}
	
	private List<Course> extractCourseListFromTable(HtmlTable semesterTable){
		List<Course> courseList = new ArrayList<>();
		List<HtmlTableRow> courseTableRows = semesterTable.getRows();
		for(HtmlTableRow courseRow : courseTableRows) {
			Course course = extractCourseFromRow(courseRow);
			if(course != null && !checkCourseAlreadyExist(course, courseList))
				courseList.add(course);
		}
		return courseList;	
	}
	
	private Course extractCourseFromRow(HtmlTableRow courseRow) {
		if (courseRow.getCells().size() != 3)
			return null;
		System.out.println(courseRow.asText());
		String courseGrade = courseRow.getCell(0).asText();
		if (courseGrade.equals(DID_NOT_DO) || courseGrade.equals(DID_NOT_COMPLETE))
			return null;
		String courseName = courseRow.getCell(2).asText(),
				courseNumber = extractCourseNumber(courseName);
		return "".equals(courseNumber) ? null : new Course(courseNumber, Double.valueOf(courseRow.getCell(1).asText()));
	}
	
	private String extractCourseNumber(final String s) {
		Matcher m = Pattern.compile("(\\d{6})").matcher(s);
		return !m.find() ? "" : m.group(0);
	}
	
	public List<Course> getCourseList(){
		return this.courseList;
	}
}
