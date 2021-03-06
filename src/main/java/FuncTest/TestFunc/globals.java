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
	public static final String LOGIN_INTENT = "technion.login";
	public static final String SUBSCRIBE_INTENT = "technion.rules.subscribe";
	public static final String RUN_RULES_INTENT = "technion.rules.run";
	public static final String VIDEOS_CHECK_EXISTS_INTENT_NAME = "technion.courses.video.checkexistance";
	public static final String PREREQUISITES_GET_BY_NAME_INTENT_NAME = "technion.prerequisites.getByCourseName";
	public static final String PREREQUISITES_GET_BY_NUMBER_INTENT_NAME = "technion.prerequisites.getByCourseNumber";
	public static final String COURSE_GET_POSTREQUISITES_BY_NAME_INTENT_NAME = "technion.postrequisites.getByCourseName";
	public static final String COURSE_GET_POSTREQUISITES_BY_NUMBER_INTENT_NAME = "technion.postrequisites.getByCourseNumber";
	public static final String COURSE_GET_RECOMMENDED_BY_QUERY = "technion.recommend.index";
	public static final String COURSE_GET_RECOMMENDED_BY_COURSE_NUMBER = "technion.recommend.similarCourses";
	public static final String TEST_INTENT = "testIntent";
	public static final String HELP_INTENT_NAME = "technion.help";
	// other globals
	public static final String NO_BUSINESS_FOUND_ERROR = "No such business found. If this is an error please contact technionbot1@gmail.com";
	public static final String MISSING_BUSINESS_PARAM = "Please choose a business to look into\n";
	public static final String UNKNOWN_USERNAME = "No user with this username was found. Please retry.";
	public static final String MISSING_DAY_PARAM = "Please specify a day\n";
	public static final String GENERIC_ERR_MSG = "I'm sorry, something went wrong";
	public static final String SERVER_ERROR = "Server error, please try again at a later date";
	//globals for course filter
	public static final String NO_COURSES_FOUND_ERROR = "I'm Sorry, I couldn't find any courses matching your request";
	//globals for degreeHelper
	public static final String MISSING_MANDATORY_COURSES_ERROR = "No mandatory courses found\n";
	public static final String MISSING_LISTA_COURSES_ERROR = "No list A courses found\n";
	public static final String MISSING_LISTB_COURSES_ERROR = "No list B courses found\n";
	public static final String MISSING_PROJECT_COURSES_ERROR = "No project courses found\n";
	public static final String MISSING_CORE_COURSES_ERROR = "No core courses found\n";
	//connection string
	public static final String CONNECTION_STRING = String.format(
			"jdbc:sqlserver://%s:1433;database=%s;user=%s;password=%s;encrypt=true;"
					+ "hostNameInCertificate=*.database.windows.net;loginTimeout=30;",
			"technobotserver.database.windows.net", "technobot", "techazure2@technobotserver", "TechnionBot1234");

	public static final Object NO_VIDEO_FOUND_ERROR = "No videos were found. It could be that the course-number does not correspond to an actual course"
			+ ", or perhaps no video was recorded for it\n";
	public static final int COURSE_FILTER_LIMIT = 15;

}
