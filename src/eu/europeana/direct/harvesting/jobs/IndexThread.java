package eu.europeana.direct.harvesting.jobs;

import eu.europeana.direct.legacy.index.LuceneIndexing;

public class IndexThread extends Thread {

	private boolean running = true;
	
	@Override
	public void run() {
		LuceneIndexing.getInstance().commitIndex(true);
	}

	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}
	
}
