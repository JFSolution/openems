package io.openems.edge.common.worker;

import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractWorker {

	private final Logger log = LoggerFactory.getLogger(AbstractWorker.class);

	private final AtomicBoolean isForceRun = new AtomicBoolean(false);
	private final AtomicBoolean isStopped = new AtomicBoolean(false);

	/**
	 * Initializes the worker and starts the worker thread
	 * 
	 * @param name
	 */
	protected void activate(String name) {
		if (name != null) {
			this.worker.setName(name);
			this.worker.start();
		}
	}

	/**
	 * Stops the worker thread
	 */
	protected void deactivate() {
		this.isStopped.set(true);
	}

	/**
	 * This method is called in a loop forever until the Thread gets interrupted.
	 */
	protected abstract void forever();

	/**
	 * Gets the cycleTime of this worker in [ms].
	 * 
	 * @return
	 */
	protected abstract int getCycleTime();

	/**
	 * Causes the Worker to interrupt sleeping and start again the run() method
	 * immediately
	 */
	public void triggerForceRun() {
		if (!isForceRun.getAndSet(true)) {
			this.worker.interrupt();
		}
	}

	private final Thread worker = new Thread() {
		public void run() {
			long onWorkerExceptionSleep = 1; // seconds
			while (!isStopped.get()) {
				long cycleStart = System.currentTimeMillis();
				try {
					/*
					 * Call forever() forever.
					 */
					forever();
					/*
					 * Wait for next cycle
					 */
					try {
						long sleep = getCycleTime() - (System.currentTimeMillis() - cycleStart);
						if (sleep > 0) {
							Thread.sleep(sleep);
						}
					} catch (InterruptedException e) {
						if (isForceRun.get()) {
							// check if a "forceRun" was triggereed. In that case Thread.sleep is
							// interrupted and run() is
							// starting again immediately
							isForceRun.set(false);
						} else {
							// otherwise forward the exception
							isStopped.set(true);
							throw e;
						}
					}
					// Everything went ok -> reset onWorkerExceptionSleep
					onWorkerExceptionSleep = 1;
				} catch (Throwable e) {
					/*
					 * Handle Bridge-Exceptions
					 */
					log.error("Worker error. " + e.getClass().getSimpleName() + ": " + e.getMessage() //
							+ (e.getCause() != null ? " - Caused by: " + e.getCause().getMessage() : ""));
					e.printStackTrace();
					onWorkerExceptionSleep = onWorkerExceptionSleep(onWorkerExceptionSleep);
				}
			}
		};
	};

	/**
	 * Little helper method: Sleep and don't let yourself interrupt by a
	 * ForceRun-Flag. It is not making sense anyway, because something is wrong with
	 * the setup if we landed here.
	 *
	 * @param duration
	 *            in seconds
	 */
	private long onWorkerExceptionSleep(long duration) {
		if (duration < 60) {
			duration += 1;
		}
		long targetTime = System.currentTimeMillis() + (duration * 1000);
		do {
			try {
				long thisDuration = (targetTime - System.currentTimeMillis()) / 1000;
				if (thisDuration > 0) {
					Thread.sleep(thisDuration);
				}
			} catch (InterruptedException e1) {
				log.warn("WorkerExceptionSleep caused " + e1.getMessage());
			}
		} while (targetTime > System.currentTimeMillis());
		return duration;
	}
}
