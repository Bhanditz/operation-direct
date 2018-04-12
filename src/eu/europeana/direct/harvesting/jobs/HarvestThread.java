package eu.europeana.direct.harvesting.jobs;


import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class HarvestThread {
	private final int nThreads;
	private final PoolWorker[] threads;
	private BlockingQueue<Runnable> queue;			

	public HarvestThread(int nThreads) {
		this.nThreads = nThreads;
		queue = new LinkedBlockingQueue<Runnable>(nThreads);
		threads = new PoolWorker[nThreads];

		for (int i = 0; i < nThreads; i++) {
			threads[i] = new PoolWorker();
			threads[i].start();
		}
	}

	
	
	public BlockingQueue<Runnable> getQueue() {
		return queue;
	}



	public void execute(Runnable r) throws InterruptedException {
		try {
			queue.put(r);
		} catch (InterruptedException e) {
			throw e;
		}
	}

	private class PoolWorker extends Thread {
		public void run() {
			Runnable r = null;

			while (true) {
				try {					
					r = (Runnable) queue.take();					
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				// If we don't catch RuntimeException,
				// the pool could leak threads
				try {
					r.run();
				} catch (RuntimeException e) {

				}
			}
		}
	}
}
