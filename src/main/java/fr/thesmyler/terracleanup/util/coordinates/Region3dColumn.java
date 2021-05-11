package fr.thesmyler.terracleanup.util.coordinates;

import fr.thesmyler.terracleanup.util.IntRange;

/**
 * The 2D position of a vertical column of 3D CubicChunks regions.
 * This is not to be confused with {@link Region2dPosition}
 * 
 * @author SmylerMC
 *
 */
public class Region3dColumn implements IPosition2d {
	
	private static final int SIZE = 256;
	
	private final int x, z;

	public Region3dColumn(int x, int z) {
		this.x = x;
		this.z = z;
	}

	@Override
	public int x() {
		return this.x;
	}

	@Override
	public int z() {
		return this.z;
	}

	@Override
	public IntRange rangeX() {
		return new IntRange(x*SIZE, (x + 1)*SIZE - 1);
	}

	@Override
	public IntRange rangeZ() {
		return new IntRange(z*SIZE, (z + 1)*SIZE - 1);
	}

	@Override
	public int size() {
		return SIZE;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + z;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Region3dColumn other = (Region3dColumn) obj;
		if (x != other.x)
			return false;
		if (z != other.z)
			return false;
		return true;
	}

}
