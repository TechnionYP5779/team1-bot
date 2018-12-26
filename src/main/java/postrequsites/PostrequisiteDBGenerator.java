package postrequsites;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;

import FuncTest.TestFunc.globals;

public class PostrequisiteDBGenerator {
	private static final String courseInfoDBName = "dbo.Courses";
	private static final String postrequisitesDBName = "dbo.Postrequisites";

	private static String genImportCoursesDBQuery() {
		return "select * from " + courseInfoDBName;
	}

	private static List<Pair<String, String>> parsePrerequisitesAsPostrequisitePairs(String course,
			String prerequisitesString) {
		List<Pair<String, String>> postrequisitePairs = new ArrayList<>();
		if (prerequisitesString == null)
			return postrequisitePairs;
		String[] prereqs = prerequisitesString.split("(&|\\|)");
		List<String> prerequisites = prereqs == null ? new ArrayList<>()
				: Arrays.stream(prereqs).distinct().collect(Collectors.toList());
		prerequisites.forEach(prerequisite -> postrequisitePairs.add(Pair.of(prerequisite, course)));
		return postrequisitePairs;
	}

	private static Set<Pair<String, String>> getAllCoursesPostrequisites() throws SQLException {
		try (Connection connection = DriverManager.getConnection(globals.CONNECTION_STRING)) {
			ResultSet resultSet = connection.createStatement().executeQuery(genImportCoursesDBQuery());
			Set<Pair<String, String>> postrequisitePairs = new HashSet<>();
			while (resultSet.next())
				postrequisitePairs.addAll(parsePrerequisitesAsPostrequisitePairs(resultSet.getString("Name"),
						resultSet.getString(resultSet.findColumn("Prereq"))));
			return postrequisitePairs;
		}
	}

	private static void addPairToBatchInsert(PreparedStatement s, Pair<String, String> postreqPair) {
		try {
			s.setInt(1, Integer.parseInt(postreqPair.getLeft()));
			s.setInt(2, Integer.parseInt(postreqPair.getRight()));
			s.addBatch();
		} catch (SQLException | NumberFormatException e) {
			e.printStackTrace();
		}

	}

	public static void generate() {
		try {
			clearPostrequisitesDB();
			Set<Pair<String, String>> coursesPostrequisites = getAllCoursesPostrequisites();
			System.out.println(coursesPostrequisites);
			try (Connection connection = DriverManager.getConnection(globals.CONNECTION_STRING)) {
				PreparedStatement ps = connection
						.prepareStatement("INSERT INTO " + postrequisitesDBName + " VALUES ( ? , ? );");
				coursesPostrequisites.forEach(pair -> addPairToBatchInsert(ps, pair));
				ps.executeBatch();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static void clearPostrequisitesDB() throws SQLException {
		String clearQuery = "DELETE FROM " + postrequisitesDBName + ";";
		try (Connection connection = DriverManager.getConnection(globals.CONNECTION_STRING)) {
			connection.createStatement().executeUpdate(clearQuery);
		}
	}
}
