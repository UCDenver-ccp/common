/*
 Copyright (c) 2012, Regents of the University of Colorado
 All rights reserved.

 Redistribution and use in source and binary forms, with or without modification, 
 are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, this 
    list of conditions and the following disclaimer.
   
 * Redistributions in binary form must reproduce the above copyright notice, 
    this list of conditions and the following disclaimer in the documentation 
    and/or other materials provided with the distribution.
   
 * Neither the name of the University of Colorado nor the names of its 
    contributors may be used to endorse or promote products derived from this 
    software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; 
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON 
 ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package edu.ucdenver.ccp.common.sql;

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
	/**
	 * JDBC driver name
	 */
	public static final String DRIVER_NAME = "DRIVER_NAME";
	/**
	 * URL prefix for JDBC access
	 */
	public static final String URL_PREFIX = "URL_PREFIX";
	/**
	 * Host computer for the database
	 */
	public static final String HOST = "HOST";
	/**
	 * Port where database access is available
	 */
	public static final String PORT = "PORT";
	/**
	 * Name of the database
	 */
	public static final String DATABASE = "DATABASE";
	/**
	 * User name
	 */
	public static final String USER = "USER";
	/**
	 * Password
	 */
	public static final String PASSWORD = "PASSWORD";

	/**
	 * Returns a database connection given the specified properties
	 * 
	 * @param properties
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public static Connection getConnection(Properties properties) throws ClassNotFoundException, SQLException {
		String driverName = properties.getProperty(DRIVER_NAME);
		String urlprefix = properties.getProperty(URL_PREFIX);
		String host = properties.getProperty(HOST);
		String port = properties.getProperty(PORT);
		String database = properties.getProperty(DATABASE);
		String user = properties.getProperty(USER);
		String password = properties.getProperty(PASSWORD);

		if (!(urlprefix.endsWith("://") || urlprefix.endsWith(":@//"))) {
			throw new RuntimeException("URL prefix must end with ://");
		}
		return getConnection(driverName, urlprefix, host, port, database, user, password);

	}

	/**
	 * Creates a database connection specific to the input arguments
	 * 
	 * @param driverName
	 * @param urlprefix
	 * @param host
	 * @param port
	 * @param database
	 * @param user
	 * @param password
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public static Connection getConnection(String driverName, String urlprefix, String host, String port,
			String database, String user, String password) throws ClassNotFoundException, SQLException {
		Class.forName(driverName);
		String url = urlprefix + host + ":" + port + "/" + database;
		return DriverManager.getConnection(url, user, password);
	}

}
