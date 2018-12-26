package FuncTest.TestFunc;

public class globals {
	// ===================================
	// IMPORTANT VARIABLES
	// intent names
	public static final String BUSINESS_HOUR_BY_DAY_INTENT_NAME = "technion.businesses.getSpecific";
	public static final String BUSINESS_HOUR_WEEK_INTENT_NAME = "technion.businesses.fullweek";
	public static final String FILTER_COURSES_INTENT_NAME = "technion.courses.filter - examA";
	public static final String FINISH_DEGREE_INTENT_NAME = "technion.degree.helpFinish";
	public static final String HOMEWORK_GET_UPCOMING_INTENT_NAME = "technion.homework.getUpcomingWithContext";
	public static final String VIDEOS_CHECK_EXISTS_INTENT_NAME = "technion.courses.video.checkexistance";
	public static final String PREREQUISITES_GET_BY_NAME_INTENT_NAME = "technion.prerequisites.getByCourseName";
	public static final String PREREQUISITES_GET_BY_NUMBER_INTENT_NAME = "technion.prerequisites.getByCourseNumber";
	public static final String COURSE_GET_POSTREQUISITES_BY_NAME_INTENT_NAME = "technion.postrequisites.getByCourseName";
	public static final String COURSE_GET_POSTREQUISITES_BY_NUMBER_INTENT_NAME = "technion.postrequisites.getByCourseNumber";
	public static final String TEST_INTENT = "testIntent";
	public static final String HELP_INTENT_NAME = "technion.help";
	// other globals
	public static final String NO_BUSINESS_FOUND_ERROR = "No such business found. If this is an error please contact technionbot1@gmail.com";
	public static final String MISSING_BUSINESS_PARAM = "Please choose a business to look into\n";
	public static final String MISSING_MANDATORY_COURSES_ERROR = "No mandatory courses found\n";
	public static final String MISSING_DAY_PARAM = "Please specify a day\n";
	public static final String GENERIC_ERR_MSG = "I'm sorry, something wen\'t wrong";
	//globals for course filter
	public static final String NO_COURSES_FOUND_ERROR = "I'm Sorry, I couldn't find any courses matching your request";
	//connection string
	public static final String CONNECTION_STRING = String.format(
			"jdbc:sqlserver://%s:1433;database=%s;user=%s;password=%s;encrypt=true;"
					+ "hostNameInCertificate=*.database.windows.net;loginTimeout=30;",
			"technobotserver.database.windows.net", "technobot", "techazure2@technobotserver", "TechnionBot1234");

	public static final Object NO_VIDEO_FOUND_ERROR = "No videos where found. It could be that the course-number does represent an actual course"
			+ ", or perhaps no video was recorded";
	public static final int COURSE_FILTER_LIMIT = 15;

}
