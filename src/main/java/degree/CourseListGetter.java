package degree;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.plaf.synth.SynthSpinnerUI;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.FrameWindow;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
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
		try {
			java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
			this.courseList = getCourseList(creds);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private List<Course> getCourseList(LoginCredentials c) throws IOException{
		try(WebClient webClient = new WebClient(BrowserVersion.CHROME)) {
			webClient.getOptions().setUseInsecureSSL(true);
			webClient.getOptions().setRedirectEnabled(true);
			webClient.getOptions().setJavaScriptEnabled(true);
			webClient.getCookieManager().setCookiesEnabled(true);
			webClient.getOptions().setThrowExceptionOnScriptError(false);
			HtmlPage page = webClient.getPage("https://techmvs.technion.ac.il/cics/wmn/wmngrad?afiflzrc&ORD=1");
			//HtmlPage formPage = (HtmlPage) page.getFrameByName("UGTadpis").getEnclosedPage();
			HtmlForm form = page.getFormByName("SignonForm");
			HtmlSubmitInput submitButton = form.getInputByValue("Signon");
			HtmlTextInput usernameField = form.getInputByName("userid");
			HtmlPasswordInput passwordField = form.getInputByName("password");
			usernameField.type(c.getUsername());
			passwordField.type(c.getPassword());
			HtmlPage page2 = submitButton.click();
			System.out.println(page2.asText());
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
			Course course = extractCourseFromRow(courseRow);
			if(course != null) {
				courseList.add(course);
			}
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
	
	private String extractCourseNumber(final String s) {
		Pattern p = Pattern.compile("(\\d{6})");
		Matcher m = p.matcher(s);
		if (m.find()) {
			return m.group(0);
		}
		return "";
	}
	
	public List<Course> getCourseList(){
		return this.courseList;
	}
}
