package rule;

import java.sql.Connection;
import java.sql.DriverManager;

import FuncTest.TestFunc.globals;

public class subscribeHandler {
	private String username;
	private String password;
	
	public subscribeHandler(String username, String password) {
		this.username = username;
		this.password = password;
	}
	
	public String subscribe() {
		Connection connection = null;
		StringBuilder jsonResult = new StringBuilder();
		try {
			connection = DriverManager.getConnection(globals.CONNECTION_STRING);
			connection.prepareStatement(
					"INSERT INTO Users(Username,GRPass) VALUES ('" + this.username + "','" + this.password + "')").executeUpdate();
/*			connection = DriverManager.getConnection(globals.CONNECTION_STRING);
			String selectSql = "INSERT INTO Users(Username,GRPass) VALUES ('"+this.username+"','"+this.password+"')";
			try (Statement statement = connection.createStatement();
					ResultSet resultSet = statement.executeQuery(selectSql)) {
*/
			jsonResult.append("Rule saved successfuly");
			connection.close();
		} catch (Exception e) {
			jsonResult.append("Some error has occured and the subscription has failed.\n Please try again.");
			e.printStackTrace();
		}
		return jsonResult.toString();
	}
}
