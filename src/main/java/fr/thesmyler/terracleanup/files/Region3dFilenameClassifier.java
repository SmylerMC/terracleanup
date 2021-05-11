package fr.thesmyler.terracleanup.files;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

import fr.thesmyler.terracleanup.api.IClassifier;

public class Region3dFilenameClassifier implements IClassifier<Path, FilenameClassification> {
	
	private static final Pattern PATTERN_3DR = Pattern.compile("(-?[0-9]+\\.){3}3dr"); //TODO make sure no edge cases can exist

	@Override
	public synchronized CompletableFuture<FilenameClassification> classify(Path path) {
		return CompletableFuture.completedFuture(this.isValid(path) ? FilenameClassification.VALID: FilenameClassification.INVALID);
	}
	
	private boolean isValid(Path path) {
		String fname = path.getFileName().toString();
		return PATTERN_3DR.matcher(fname).matches();
	}

	@Override
	public void terminate() {}

	@Override
	public synchronized int queueSize() {
		return 0;
	}

}
