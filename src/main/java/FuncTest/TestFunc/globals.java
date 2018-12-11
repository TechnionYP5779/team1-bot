package FuncTest.TestFunc;

public class globals {
	// ===================================
	// IMPORTANT VARIABLES
	// intent names
	public static final String BUSINESS_HOUR_BY_DAY_INTENT_NAME = "technion.businesses.getSpecific";
	public static final String BUSINESS_HOUR_WEEK_INTENT_NAME = "technion.businesses.fullweek";
	public static final String HOMEWORK_GET_UPCOMING_INTENT_NAME = "technion.homework.getUpcomingWithContext";
	public static final String COURSE_GET_POSTREQUISITES_BY_NAME_INTENT_NAME = "technion.postrequisites.getByCourseName";
	public static final String COURSE_GET_POSTREQUISITES_BY_NUMBER_INTENT_NAME = "technion.postrequisites.getByCourseNumber";
	public static final String TEST_INTENT = "testIntent";
	// other globals
	public static final String NO_BUSINESS_FOUND_ERROR = "No such business found. If this is an error please contact technionbot1@gmail.com";
	public static final String MISSING_BUSINESS_PARAM = "Please choose a business to look into\n";
	public static final String MISSING_DAY_PARAM = "Please specify a day\n";
	public static final String CONNECTION_STRING = String.format(
			"jdbc:sqlserver://%s:1433;database=%s;user=%s;password=%s;encrypt=true;"
					+ "hostNameInCertificate=*.database.windows.net;loginTimeout=30;",
			"technobotserver.database.windows.net", "technobot", "techazure2@technobotserver", "TechnionBot1234");
}
