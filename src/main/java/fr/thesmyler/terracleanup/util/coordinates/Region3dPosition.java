package fr.thesmyler.terracleanup.util.coordinates;

import java.nio.file.Path;

import fr.thesmyler.terracleanup.util.IntRange;

/**
 * The position of a 3D CubicChunks region in the world
 * 
 * @author SmylerMC
 *
 */
public class Region3dPosition implements IPosition3d<Region3dColumn> {
	
	public static final int SIZE = 256;
	
	private final int x, y, z;

	public Region3dPosition(Path path) {
		String[] parts = path.getFileName().toString().split("\\.");
		this.x = Integer.parseInt(parts[0]);
		this.y = Integer.parseInt(parts[1]);
		this.z = Integer.parseInt(parts[2]);
	}
	
	public Region3dPosition(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public int x() {
		return this.x;
	}
	
	public int y() {
		return this.y;
	}
	
	public int z() {
		return this.z;
	}
	
	public int size() {
		return SIZE;
	}
	
	public IntRange rangeX() {
		return new IntRange(x*SIZE, (x + 1)*SIZE - 1);
	}
	
	public IntRange rangeY() {
		return new IntRange(y*SIZE, (y + 1)*SIZE - 1);
	}
	
	public IntRange rangeZ() {
		return new IntRange(z*SIZE, (z + 1)*SIZE - 1);
	}

	public Region3dColumn column() {
		return new Region3dColumn(this.x, this.z);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
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
		Region3dPosition other = (Region3dPosition) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		if (z != other.z)
			return false;
		return true;
	}
	
}
