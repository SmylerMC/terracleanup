package fr.thesmyler.terracleanup.util.coordinates;

import fr.thesmyler.terracleanup.util.IntRange;

/**
 * The 2D coordinates of a 2D world column section
 * 
 * @author SmylerMC
 *
 */
public interface IPosition2d {
	
	/**
	 * @return the X coordinate of this column
	 */
	int x();
	
	/**
	 * @return the Z coordinate of this column
	 */
	int z();
	
	/**
	 * @return the range this column covers along the X axis, in blocks
	 */
	IntRange rangeX();
	
	/**
	 * @return the range this column covers along the Z axis, in blocks
	 */
	IntRange rangeZ();
	
	/**
	 * @return the size of this column, in blocks
	 */
	int size();

}
