package DBConnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBHandler extends Configs{

	Connection dbconnection;
	
	public Connection getConnection() {
		String connectionString = "jdbc:mysql://localhost:3306/dbvoting?allowPublicKeyRetrieval=true&useSSL=false";
		
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		try {
			dbconnection = DriverManager.getConnection(connectionString, "root", "1234");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return dbconnection;
	}
	
}
