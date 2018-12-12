package course.info;


import org.junit.*;


public class WebInfoExtractorTest {
  
	@Ignore @Test @SuppressWarnings("static-method") public void test1() {
	  WebInfoExtractor.writeCsvFile("courseInfo.csv", WebInfoExtractor.getCourseData());
  }
  
  @Ignore @Test @SuppressWarnings("static-method") public void test2() {
	  WebInfoExtractor.getCourseData();
  }
}
