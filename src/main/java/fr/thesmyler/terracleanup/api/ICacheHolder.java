package fr.thesmyler.terracleanup.api;

/**
 * Any objects that holds a cache internally
 * 
 * @author SmylerMC
 *
 */
public interface ICacheHolder {
	
	/**
	 * Clear the cache
	 */
	void clearCache();
	
	/**
	 * @return the number of objects in the cache
	 */
	int cacheCount();

}
