package insertDataToDB;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import FuncTest.TestFunc.globals;

public class CSVPusher {
	
	/*public static void main(String[] args) {
		try {
			loadToDBFromCSV("C:\\Users\\shai\\Documents\\team1-bot\\src\\main\\java\\insertDataToDB\\old_server_videos.csv");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/
	
	public static void loadToDBFromCSV(String csvName) throws Exception {
		try (BufferedReader br = new BufferedReader(new FileReader(csvName))) {
		    for (String line = br.readLine(); line != null;) {
		    	Connection connection;
				try {
						if(line.charAt(0) == '#') {
							line = br.readLine();
							continue;
						}
						
						System.out.println(line);
						System.out.println();
					
						connection = DriverManager.getConnection(globals.CONNECTION_STRING);
						PreparedStatement pstmt = connection
								.prepareStatement("INSERT INTO dbo.Videos VALUES(? ,? ,? ,?)");
						
						String courseId = line.substring(0, line.indexOf(','));
						line = line.substring(courseId.length() + 1);
						System.out.println(courseId);
						
						String filmingDate = line.substring(0, line.indexOf(','));
						line = line.substring(filmingDate.length() + 1);
						System.out.println(filmingDate);
						
						String type = line.substring(0, line.indexOf(','));
						line = line.substring(type.length() + 1);
						System.out.println(type);
						
						String link = line;
						System.out.println(link);
						
						pstmt.setInt(1, Integer.valueOf(courseId).intValue());
						pstmt.setString(2, filmingDate);
						pstmt.setString(3, type);
						pstmt.setString(4, link);
						
						System.out.println("================================================");
						
						pstmt.executeUpdate();
						connection.close();
						
						line = br.readLine();
		
				} catch (Exception e1) {
					e1.printStackTrace();
					line = br.readLine();
				}
			}
		}
	}
}
