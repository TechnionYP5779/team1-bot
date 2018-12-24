package degree;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
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
	private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.106 Safari/537.36";
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
			webClient.setAjaxController(new NicelyResynchronizingAjaxController());
			webClient.getOptions().setRedirectEnabled(true);
			webClient.getOptions().setJavaScriptEnabled(true);
			webClient.getOptions().setCssEnabled(false);
			webClient.getCookieManager().setCookiesEnabled(true);
			webClient.getOptions().setThrowExceptionOnScriptError(false);
			webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
			//there is a problem with the header returned, unable to redirect because of it, so need to handle this manually
			String redirectURL = getRedirectURL("http://techmvs.technion.ac.il/cics/wmn/wmngrad?ORD=1");
			//Now can use the redirected url
			if("".equals(redirectURL))
				return null;
			HtmlPage page = webClient.getPage(redirectURL);
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
		
	private String getRedirectURL(String url) throws IOException {
		HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
		con.setRequestMethod("GET");
		con.setRequestProperty("User-Agent", USER_AGENT);
		if(con.getResponseCode() == HttpURLConnection.HTTP_OK)
			return url;
		return con.getResponseCode() != HttpURLConnection.HTTP_MOVED_TEMP ? "" : con.getHeaderField("Location");
	}
	
	public List<Course> getCourseList(){
		return this.courseList;
	}
}
