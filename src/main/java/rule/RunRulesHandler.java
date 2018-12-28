package rule;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Optional;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;

import FuncTest.TestFunc.globals;
import FuncTest.TestFunc.utils;
import homework.HomeworkGetter;
import homework.LoginCredentials;
import homework.WrongCredentialsException;
import responses.TableResponse;

public class RunRulesHandler {
	private String username;
	private String password;
	
	public RunRulesHandler(String username, String password) {
		this.username = username;
		this.password = password;
	}
	
	public HttpResponseMessage runHomeworkRules(HttpRequestMessage<Optional<String>> s,
			final ExecutionContext c) {
		Connection connection = null;
		StringBuilder jsonResult = new StringBuilder();
		try {
			connection = DriverManager.getConnection(globals.CONNECTION_STRING);
			PreparedStatement query = connection.prepareStatement("SELECT * FROM Users WHERE username = ? and GrPass = ?");
			query.setString(1, username);
			query.setString(2, password);
			ResultSet rs = query.executeQuery();
			
			if(!rs.isBeforeFirst()) {
				c.getLogger().info("================= WRONG USERNAME & PASSWORD ========================");
				jsonResult.append("Some error has occured.\n Please try again.");
			}
			else {
				c.getLogger().info("================= APPLYING RULE ========================");
				LoginCredentials lc = new LoginCredentials(username, password);
				HomeworkGetter homework = new HomeworkGetter(lc, c);
				
				try {
					c.getLogger().info("================= GETTING HOMEWORK ========================");
					return TableResponse.homeworkTableResponse(s, homework.getUpcomingHomework());
				} catch (WrongCredentialsException e) {
					return utils.createWebhookResponseContent("Wrong credentials, please try again", s);
				}
				
			}
			
			connection.close();
		
		} catch (Exception e) {
			jsonResult.append("Some error has occured.\n Please try again.");
			c.getLogger().info("====================" + e.getMessage() + "======================");
		}
		return utils.createWebhookResponseContent(jsonResult.toString(), s);
	}
}
