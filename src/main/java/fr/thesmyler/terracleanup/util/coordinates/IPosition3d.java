package fr.thesmyler.terracleanup.util.coordinates;

import fr.thesmyler.terracleanup.util.IntRange;

/**
 * The 3D coordinates of a world section
 * 
 * @author SmylerMC
 *
 * @param <T> a corresponding column type
 */
public interface IPosition3d<T extends IPosition2d> {

	/**
	 * @return the x coordinate of this section
	 */
	int x();
	
	/**
	 * @return the y coordinate of this section
	 */
	int y();
	
	/**
	 * @return the z coordinate of this section
	 */
	int z();
	
	/**
	 * @return the range this section covers along the X axis, in blocks
	 */
	IntRange rangeX();
	
	/**
	 * @return the range this section covers along the Y axis, in blocks
	 */
	IntRange rangeY();
	
	/**
	 * @return the range this section covers along the Z axis, in blocks
	 */
	IntRange rangeZ();
	
	/**
	 * @return the size of this column, in blocks
	 */
	int size();
	
	/**
	 * @return the column that contains this section
	 */
	T column();
	
}
