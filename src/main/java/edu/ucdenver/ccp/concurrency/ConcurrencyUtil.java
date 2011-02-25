package edu.ucdenver.ccp.concurrency;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Concurrency utilities
 */
public class ConcurrencyUtil {

	/**
	 * Current thread will wait for pool tasks to complete.
	 * Pool shutdown command will be issued preventing it
	 * from accepting new tasks.  
	 * 
	 * @param pool task/thread pool
	 */
	public static void awaitTermination(ExecutorService pool) {
		pool.shutdown(); // necessary before awaiting termination
		try {
			while (!pool.awaitTermination(100, TimeUnit.MILLISECONDS))
				continue;
		} catch (InterruptedException e) {
			e.printStackTrace();
			pool.shutdownNow();
			Thread.currentThread().interrupt(); // Preserve interrupt status
		}
	}

}
