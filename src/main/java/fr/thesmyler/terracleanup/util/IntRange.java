package fr.thesmyler.terracleanup.util;

/**
 * An integer range
 * 
 * @author SmylerMC
 *
 */
public class IntRange {
	
	private final int lowerBound, upperBound;
	
	/**
	 * Constructor
	 * 
	 * @param lowerBound of this range, inclusive
	 * @param upperBound of this range, inclusive
	 * 
	 * @throws {@link IllegalArgumentException} if upperBound > lowerBound 
	 */
	public IntRange(int lowerBound, int upperBound) {
		if(upperBound < lowerBound) throw new IllegalArgumentException("lowerBound > upperBound");
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
	}
	
	public int lowerBound() {
		return this.lowerBound;
	}
	
	public int upperBound() {
		return this.upperBound;
	}
	
	/**
	 * @param x
	 * @return true if x is within this range (inclusive)
	 */
	public boolean matches(int x) {
		return x >= this.lowerBound && x <= this.upperBound;
	}
	
	/**
	 * @param x
	 * @return true if x is strictly above this range (exclusive)
	 */
	public boolean above(int x) {
		return x > this.upperBound;
	}
	
	/**
	 * @param x
	 * @return true if x is striclty below this range (exclusive)
	 */
	public boolean below(int x) {
		return x < this.lowerBound;
	}
	
	/**
	 * @param other
	 * @return true if the other range is strictly above this range (if both of its bounds are), false otherwise or if other is null
	 */
	public boolean above(IntRange other) {
		if(other == null) return false;
		return this.above(other.lowerBound) && this.above(other.upperBound);
	}
	
	/**
	 * @param other
	 * @return true if the other range is strictly below this range (if both of its bounds are), false otherwise or if other is null
	 */
	public boolean below(IntRange other) {
		if(other == null) return false;
		return this.below(other.lowerBound) && this.below(other.upperBound);
	}
	
	/**
	 * 
	 * @param other
	 * @return true if the other range intersects with this one (inclusive)
	 */
	public boolean intersects(IntRange other) {
		if(other == null) return false;
		return this.matches(other.lowerBound) || this.matches(other.upperBound) || other.matches(this.lowerBound);
	}
	
	/**
	 * @param amount
	 * @return a new {@link IntRange} that has the same lower bound, but an upper bound increased by amount
	 */
	public IntRange extendUp(int amount) {
		return new IntRange(this.lowerBound, this.upperBound + amount);
	}
	
	/**
	 * @param amount
	 * @return a new {@link IntRange} that has the same upper bound, but a lower bound lowered by amount
	 */
	public IntRange extendDown(int amount) {
		return new IntRange(this.lowerBound - amount, this.upperBound);
	}
	
	/**
	 * @param amount
	 * @return a new {@link IntRange} that has the same lower bound, but an upper bound lowered by amount
	 */
	public IntRange contractUp(int amount) {
		return new IntRange(this.lowerBound, this.upperBound - amount);
	}
	
	/**
	 * @param amount
	 * @return a new {@link IntRange} that has the same upper bound, but a lower bound increased by amount
	 */
	public IntRange contractDown(int amount) {
		return new IntRange(this.lowerBound + amount, this.upperBound);
	}
	
	/**
	/**
	 * @param amount
	 * @return a new {@link IntRange} that has both its lower and upper bounds increased by amount
	 */
	public IntRange shift(int amount) {
		return new IntRange(this.lowerBound + amount, this.upperBound + amount);
	}
	
	/**
	 * @return the center of this range
	 */
	public double center() {
		return (double)(this.lowerBound + this.upperBound) / 2;
	}
	
	/**
	 * @return the length of this range
	 */
	public int size() {
		return this.upperBound - this.lowerBound;
	}

}
