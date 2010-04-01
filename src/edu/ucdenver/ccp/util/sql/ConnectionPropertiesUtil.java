package edu.ucdenver.ccp.util.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Adapted from the class of the same name in the research/nlp/util CVS project. Originally authored
 * by Philip Ogren. <br>
 * <br>
 * Example:<br>
 * DRIVER_NAME=com.mysql.jdbc.Driver<br>
 * URL_PREFIX=jdbc:mysql://<br>
 * HOST=noname.ucdenver.edu<br>
 * PORT=3306<br>
 * DATABASE=test<br>
 * USER=usr<br>
 * PASSWORD=pword<br>
 * 
 * @author Bill Baumgartner
 * @author Philip Ogren
 */
public class ConnectionPropertiesUtil {
	public static final String DRIVER_NAME = "DRIVER_NAME";
	public static final String URL_PREFIX = "URL_PREFIX";
	public static final String HOST = "HOST";
	public static final String PORT = "PORT";
	public static final String DATABASE = "DATABASE";
	public static final String USER = "USER";
	public static final String PASSWORD = "PASSWORD";

	public static Connection getConnection(Properties properties) throws ClassNotFoundException, SQLException {
		String driverName = properties.getProperty(DRIVER_NAME);
		String urlprefix = properties.getProperty(URL_PREFIX);
		String host = properties.getProperty(HOST);
		String port = properties.getProperty(PORT);
		String database = properties.getProperty(DATABASE);
		String user = properties.getProperty(USER);
		String password = properties.getProperty(PASSWORD);

		return getConnection(driverName, urlprefix, host, port, database, user, password);

	}

	public static Connection getConnection(String driverName, String urlprefix, String host, String port,
			String database, String user, String password) throws ClassNotFoundException, SQLException {
		Class.forName(driverName);
		String url = urlprefix + host + ":" + port + "/" + database;
		return DriverManager.getConnection(url, user, password);
	}

}
