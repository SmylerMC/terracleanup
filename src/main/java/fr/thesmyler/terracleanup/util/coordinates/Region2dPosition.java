package fr.thesmyler.terracleanup.util.coordinates;

import java.nio.file.Path;

import fr.thesmyler.terracleanup.util.IntRange;

/**
 * The position of a 2D CubicChunks region.
 * This is not to be confused with {@link Region3dColumn}
 * 
 * @author SmylerMC
 *
 */
public class Region2dPosition implements IPosition2d {
	
	private static final int SIZE = 512;
	
	private final int x,z;
	
	public Region2dPosition(int x, int z) {
		this.x = x;
		this.z = z;
	}
	
	public Region2dPosition(Path path) {
		String[] parts = path.getFileName().toString().split("\\.");
		this.x = Integer.parseInt(parts[0]);
		this.z = Integer.parseInt(parts[1]);
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
		return new IntRange(this.x * SIZE, (this.x + 1) * SIZE - 1);
	}

	@Override
	public IntRange rangeZ() {
		return new IntRange(this.z * SIZE, (this.z + 1) * SIZE - 1);
	}

	@Override
	public int size() {
		return SIZE;
	}

}
