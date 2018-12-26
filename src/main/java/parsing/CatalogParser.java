package parsing;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;


public class CatalogParser {
	
	public static String CATALOG_SOFTWARE_ENGINEERING = "   . המסלול להנדסת תוכנה3\r\n";
	private String CATALOG_SOFTWARE_ENGINEERING_END = " . המסלול להנדסת מחשבים4\r\n";
	

	private String entireCatalog;
	private String semsterToken="\\dסמסטר";
	private String ignoreSpringCatalogToken="אביב:דיהם בסמסטר ולסטודנטים אשר התחילו לימ\r\n";
	
	public void getWhatNeeded(String catalogName) {
		List<String> semesters = getSemesters(getCatalog(catalogName)), requiredCourses = getRequiredCourses(semesters);
		for(String course : requiredCourses)
			System.out.println(course);
		System.out.println(requiredCourses.size());
	}
	
	public List<String> getRequiredCourses(List<String> semesters){
		ArrayList<String> courses = new ArrayList<>();
		for(String semester : semesters)
			courses.addAll(getRequiredCoursesInSemester(semester));
		return courses;
	}
	
	public List<String> getRequiredCoursesInSemester(String semester){
		ArrayList<String> courses = new ArrayList<>(), dupCourses = new ArrayList<>();
		Matcher matcher = Pattern.compile("(\\d{6}/)").matcher(semester);
		while(matcher.find()) {
			String courseNum = matcher.group();
			dupCourses.add(courseNum.substring(0, courseNum.length()-1));
		}
		matcher = Pattern.compile("(\\d{6})").matcher(semester);
		while(matcher.find())
			courses.add(matcher.group());
		courses.removeAll(dupCourses);
		return courses;
	}
	
	public List<String> getSemesters(String catalogDetails) {
		ArrayList<String> semesters = new ArrayList<>();
		String[] tokens = catalogDetails.split(semsterToken);
		for(int i = 1; i < tokens.length; ++i)
			semesters.add(tokens[i]);
		return semesters;
	}
	
	public String getCatalog(String catalogName) {
		return entireCatalog.split(catalogName)[1].split(getEndTokenOfCatalog(catalogName))[0].split(ignoreSpringCatalogToken)[0];
	}
	
	public CatalogParser(String filePath) {
		java.util.logging.Logger.getLogger("org.apache.pdfbox").setLevel(java.util.logging.Level.OFF);
		entireCatalog = convertPDFToTxt(filePath);
	}
	
	public static String convertPDFToTxt(String filePath) {
		try {
			PDDocument pdDoc = PDDocument.load(readFileAsBytes(filePath));
			String pageText = new PDFTextStripper().getText(pdDoc);
			pdDoc.close();
			return pageText;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static byte[] readFileAsBytes(String filePath) throws IOException {
		return IOUtils.toByteArray(new FileInputStream(filePath));
	}
	
	private String getEndTokenOfCatalog(String catalog) {
		return catalog != CatalogParser.CATALOG_SOFTWARE_ENGINEERING ? null : CATALOG_SOFTWARE_ENGINEERING_END;
	}
	
	
	
	

}
