/**
 * 
 */
package edu.ucdenver.ccp.common.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

import edu.ucdenver.ccp.common.string.StringConstants;

/**
 * Consumes an InputStream and outputs its contents to a specified Logger
 * 
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class InputStreamLogger extends Thread {

	private final InputStream inStream;
	private final Logger logger;
	private final String messagePrefix;
	private final Priority logPriority;

	/**
	 * @param inStream
	 *            InputStream to read
	 * @param logger
	 *            Logger where InputStream contents will be output
	 * @param logPriority
	 *            Log level to use when outputting InputStream contents
	 * @param messagePrefix
	 *            This String will be appended to the front of each line logged
	 */
	public InputStreamLogger(InputStream inStream, Logger logger, Priority logPriority, String messagePrefix) {
		this.inStream = inStream;
		this.logger = logger;
		this.logPriority = logPriority;
		this.messagePrefix = messagePrefix;

	}

	/**
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		BufferedReader br = new BufferedReader(new InputStreamReader(inStream));
		try {
			while (br.ready())
				logger.log(logPriority, messagePrefix + StringConstants.SPACE + br.readLine());
		} catch (IOException e) {
			throw new RuntimeException("Exception while logging output stream...", e);
		}
	}

}
