/**
 * 
 */
package edu.ucdenver.ccp.common.sge;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import edu.ucdenver.ccp.common.calendar.CalendarUtil;
import edu.ucdenver.ccp.common.file.CharacterEncoding;
import edu.ucdenver.ccp.common.file.FileUtil;
import edu.ucdenver.ccp.common.file.reader.FileLine;
import edu.ucdenver.ccp.common.file.reader.FileLineIterator;
import edu.ucdenver.ccp.common.file.reader.Line;
import edu.ucdenver.ccp.common.file.reader.StreamLineIterator;

/**
 * Monitors active SGE processes. Checks logs for "BUILD FAILURE".
 * 
 * @author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class SgeJobMonitor {
	private static final Logger logger = Logger.getLogger(SgeJobMonitor.class);

	private final File logDirectory;

	private final long MONITOR_FREQUENCY_IN_MINUTES = 2;

	/**
	 * @param logDirectory
	 * @param loadScriptDirectory
	 *            if not null, then this class will send load commands to AG
	 */
	public SgeJobMonitor(File logDirectory) {
		this.logDirectory = logDirectory;
		try {
			FileUtil.validateDirectory(logDirectory);
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
		for (File logFile : logDirectory.listFiles()) {
			FileLineIterator lineIter = null;
			for (lineIter = new FileLineIterator(logFile, CharacterEncoding.UTF_8); lineIter.hasNext();) {
				FileLine line = lineIter.next();
				if (line.getText().contains("BUILD FAILURE")) {
					logger.error("SGE process failed. See log file: " + logFile.getAbsolutePath());
					failureLogs.add(logFile);
				}
			}
			lineIter.close();
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
	 *            args[0] = the directory that contains the log files<br>
	 */
	public static void main(String[] args) {
		BasicConfigurator.configure();
		File logDirectory = new File(args[0]);
		SgeJobMonitor monitor = new SgeJobMonitor(logDirectory);
		try {
			monitor.monitor();
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
}
