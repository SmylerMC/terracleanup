package fr.thesmyler.terracleanup.api;

import java.util.concurrent.CompletableFuture;

/**
 * An object that classifies data asynchronously
 * 
 * @author SmylerMC
 *
 * @param <T> the type of object this classifier operates on
 * @param <E> the classification enum
 */
public interface IClassifier<T, E extends Enum<?>> {
	
	/**
	 * @param object to classify
	 * @return a {@link CompletableFuture} that will hold the result of the classification once completed
	 */
	CompletableFuture<E> classify(T object);
	
	/**
	 * Forces this classifier to terminate all operations and cleanup used resources.
	 * Running and awaiting classification tasks are discarded and will not complete.
	 * The classifier should be considered unusable once this has returned.
	 */
	void terminate();
	
	/**
	 * @return the number of futures that are not yet done processing
	 */
	int queueSize();

}
