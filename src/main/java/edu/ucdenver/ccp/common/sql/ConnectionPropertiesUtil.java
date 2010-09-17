/* Copyright (C) 2010 Center for Computational Pharmacology, University of Colorado School of Medicine
 * 
 * This file is part of the CCP common library.
 * The CCP common library is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
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
		
		if (!urlprefix.endsWith("://")) {
			throw new RuntimeException("URL prefix must end with ://");
		}
		return getConnection(driverName, urlprefix, host, port, database, user, password);

	}

	public static Connection getConnection(String driverName, String urlprefix, String host, String port,
			String database, String user, String password) throws ClassNotFoundException, SQLException {
		Class.forName(driverName);
		String url = urlprefix + host + ":" + port + "/" + database;
		return DriverManager.getConnection(url, user, password);
	}

}
