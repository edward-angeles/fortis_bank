package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
	public static Connection getConnection() throws SQLException {
		Connection         myConnection = null;
		String user, password, service, url;		
		user = "c##edward";
		password = "123" ;
		service = "localhost:1521/XE" ;		
		url = "jdbc:oracle:thin:";
		myConnection = DriverManager.getConnection(url + user + "/" + password + "@" + service);	
		
		return myConnection;
	}
}
