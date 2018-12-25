package rule;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import FuncTest.TestFunc.globals;

public class loginHandler {
	private String username;
	private String password;
	
	public loginHandler(String username, String password) {
		this.username = username;
		this.password = password;
	}
	
	public String checkUserNameExists() {
		Connection connection = null;
		StringBuilder jsonResult = new StringBuilder();
		try {
			connection = DriverManager.getConnection(globals.CONNECTION_STRING);
			String selectSql = "SELECT * FROM Users WHERE Username = ? and GrPass = ?";
			
			try (PreparedStatement statement = connection.prepareStatement(selectSql)) {
				
				statement.setString(1, username);
				statement.setString(2, password);
				ResultSet resultSet = statement.executeQuery();
				
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
