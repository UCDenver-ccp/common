package edu.ucdenver.ccp.common.sge;

/*
 * #%L
 * Colorado Computational Pharmacology's common module
 * %%
 * Copyright (C) 2012 - 2014 Regents of the University of Colorado
 * %%
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * 3. Neither the name of the Regents of the University of Colorado nor the names of its contributors
 *    may be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.DefaultConfiguration;

import edu.ucdenver.ccp.common.calendar.CalendarUtil;
import edu.ucdenver.ccp.common.file.CharacterEncoding;
import edu.ucdenver.ccp.common.file.FileUtil;
import edu.ucdenver.ccp.common.file.reader.Line;
import edu.ucdenver.ccp.common.file.reader.StreamLineIterator;

/**
 * Monitors active SGE processes. Checks logs for "BUILD FAILURE".
 * 
 * @author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class SgeJobMonitor {
	private static final Logger logger = LogManager.getLogger(SgeJobMonitor.class);

	private final Collection<File> logDirectories;

	private final long MONITOR_FREQUENCY_IN_MINUTES = 2;

	/**
	 * @param logDirectory
	 * @param loadScriptDirectory
	 *            if not null, then this class will send load commands to AG
	 */
	public SgeJobMonitor(Collection<File> logDirectories) {
		this.logDirectories = logDirectories;
		try {
			for (File logDirectory : logDirectories) {
				FileUtil.validateDirectory(logDirectory);
			}
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Monitors the log directory specified in the constructor. The directory is checked
	 * periodically according to the MONITOR_FREQUENCY_IN_MINUTES constant. If an error is found in
	 * one of the logs a RuntimeException is thrown at the completion of processing.
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void monitor() throws IOException, InterruptedException {
		logger.info("Monitoring log directories: " + logDirectories.toString());
		Collection<File> failureLogs;
		int activeProcessCount = -1;
		/*
		 * keep looping as long as there are remaining active qsub processes, i.e. qstat has output
		 */
		while ((activeProcessCount = qsubQueueActiveProcessCount()) > 0) {
			logger.info("================================================================\nChecking logs ["
					+ CalendarUtil.getTimeStamp() + "]... Active processes: " + activeProcessCount);
			failureLogs = checkSgeLogs();
			logger.info("================================================================");
			Thread.sleep(MONITOR_FREQUENCY_IN_MINUTES * 60 * 1000);
		}
		failureLogs = checkSgeLogs();
		if (!failureLogs.isEmpty()) {
			logger.info("SGE PROCESS FAILURE. See log file(s): " + failureLogs.toString());
			throw new RuntimeException("SGE PROCESS FAILURE. See log file(s): " + failureLogs.toString());
		}
		logger.info("SGE PROCESS COMPLETE AND SUCCESSFUL.");
	}

	/**
	 * @return
	 * @throws IOException
	 */
	private Collection<File> checkSgeLogs() throws IOException {
		Collection<File> failureLogs;
		failureLogs = new ArrayList<File>();
		for (File logDirectory : logDirectories) {
			for (File logFile : logDirectory.listFiles()) {
				StreamLineIterator lineIter = null;
				try {
					lineIter = new StreamLineIterator(logFile, CharacterEncoding.UTF_8); 
					while (lineIter.hasNext()) {
						Line line = lineIter.next();
						if (line.getText().contains("BUILD FAILURE")) {
							logger.error("SGE process failed. See log file: " + logFile.getAbsolutePath());
							failureLogs.add(logFile);
						}
					}
				}
				finally {
					if (lineIter != null) {
						lineIter.close();
					}
				}
			}
		}
		if (failureLogs.size() == 0) {
			logger.info("Logs are clean.");
		}
		return failureLogs;
	}

	/**
	 * Sample qstat output:
	 * 
	 * <pre>
	 * job-ID  prior   name       user         state submit/start at     queue                          slots ja-task-ID 
	 * -----------------------------------------------------------------------------------------------------------------
	 *     838 0.55500 ice-build  hudson       r     02/07/2012 10:11:13 all.q@amc-colfaxnd2.ucdenver.p     1 10
	 *     838 0.55500 ice-build  hudson       r     02/07/2012 10:11:13 all.q@amc-colfaxnd5.ucdenver.p     1 11
	 * 
	 * </pre>
	 * 
	 * @return true if the qsub queue is empty or if the only jobs remaining on the queue are in an
	 *         error state
	 * @throws IOException
	 */
	private int qsubQueueActiveProcessCount() throws IOException {
		Runtime runtime = Runtime.getRuntime();
		Process qstatProcess = runtime.exec("qstat");

		StreamLineIterator lineIter = new StreamLineIterator(qstatProcess.getInputStream(), CharacterEncoding.UTF_8,
				"------");
		/* skip the header line */
		if (lineIter.hasNext())
			lineIter.next();
		int remainingProcessesCount = 0;
		while (lineIter.hasNext()) {
			Line qstatLine = lineIter.next();
			if (!inErrorState(qstatLine))
				remainingProcessesCount++;
		}
		try {
			int exitValue = qstatProcess.waitFor();
			if (exitValue != 0)
				throw new RuntimeException("qstat process exited with error code: " + exitValue);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		lineIter.close();
		// logger.info("Active qstat process count: " + remainingProcessesCount);
		return remainingProcessesCount;
	}

	/**
	 * If the state field contains an "E" then the job is in an error state.
	 * 
	 * @param qstatLine
	 * @return true if the state field contains an "E"
	 */
	private static boolean inErrorState(Line qstatLine) {
		String[] toks = qstatLine.getText().split("\\s+");
		String stateField = toks[4];
		return stateField.contains("E");
	}

	/**
	 * @param args
	 *            args[0+] = the directories that contains the log files<br>
	 */
	public static void main(String[] args) {
		Configurator.initialize(new DefaultConfiguration());
	    Configurator.setRootLevel(Level.INFO);
		Collection<File> logDirectories = new ArrayList<File>();
		for (int i = 0; i < args.length; i++) {
			logDirectories.add(new File(args[i]));
		}

		SgeJobMonitor monitor = new SgeJobMonitor(logDirectories);
		try {
			monitor.monitor();
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
}
