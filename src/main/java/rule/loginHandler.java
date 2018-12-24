package rule;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import FuncTest.TestFunc.globals;

public class loginHandler {
	private String username;
	
	public loginHandler(String username) {
		this.username = username;
	}
	
	public String checkUserNameExists() {
		Connection connection = null;
		StringBuilder jsonResult = new StringBuilder();
		try {
			connection = DriverManager.getConnection(globals.CONNECTION_STRING);
			String selectSql = "SELECT * FROM Users WHERE Username = '"+this.username+"'";
			try (Statement statement = connection.createStatement();
					ResultSet resultSet = statement.executeQuery(selectSql)) {
				if (!resultSet.isBeforeFirst())
					jsonResult.append(globals.UNKNOWN_USERNAME);
				else
					jsonResult.append("Logged in successfuly");
				connection.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jsonResult.toString();
	}
}
