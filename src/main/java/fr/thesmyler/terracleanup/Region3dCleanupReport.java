package fr.thesmyler.terracleanup;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

import fr.thesmyler.terracleanup.elevation.ElevationClassification;
import fr.thesmyler.terracleanup.util.Formatting;

public class Region3dCleanupReport {
	
	private ConcurrentMap<ElevationClassification, Queue<Path>> result = new ConcurrentHashMap<>();
	private ConcurrentMap<ElevationClassification, AtomicLong> resultSizes = new ConcurrentHashMap<>();
	
	public Region3dCleanupReport() {
		for(ElevationClassification classification: ElevationClassification.values()) {
			result.put(classification, new ConcurrentLinkedQueue<Path>());
			resultSizes.put(classification, new AtomicLong(0));
		}
	}
	
	public synchronized void addPath(Path path, ElevationClassification classification) {
		this.result.get(classification).add(path);
		try {
			resultSizes.get(classification).addAndGet(Files.size(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public synchronized int getCount(ElevationClassification classification) {
		return this.result.get(classification).size();
	}
	
	public synchronized long getSize(ElevationClassification classification) {
		return this.resultSizes.get(classification).longValue();
	}
	
	public void printTo(PrintStream out) {
		for(ElevationClassification c: ElevationClassification.values()) {
			out.println(String.format("## %s: %d (%s) ##", c.toString(), this.result.get(c).size(), Formatting.humanReadableByteCountBin(this.resultSizes.get(c).get())));
			for(Path p: this.result.get(c)) {
				out.println(p);
			}
			out.println();
		}
	}

}
