
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
 */package edu.ucdenver.ccp.common.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

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
	private final Level logLevel;

	/**
	 * @param inStream
	 *            InputStream to read
	 * @param logger
	 *            Logger where InputStream contents will be output
	 * @param logLevel
	 *            Log level to use when outputting InputStream contents
	 * @param messagePrefix
	 *            This String will be appended to the front of each line logged
	 */
	public InputStreamLogger(InputStream inStream, Logger logger, Level logLevel, String messagePrefix) {
		this.inStream = inStream;
		this.logger = logger;
		this.logLevel = logLevel;
		this.messagePrefix = messagePrefix;

	}

	/**
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		BufferedReader br = new BufferedReader(new InputStreamReader(inStream));
		try {
			while (br.ready()) {
				logger.log(logLevel, messagePrefix + StringConstants.SPACE + br.readLine());
			}
		} catch (IOException e) {
			throw new RuntimeException("Exception while logging output stream...", e);
		}
	}

}
