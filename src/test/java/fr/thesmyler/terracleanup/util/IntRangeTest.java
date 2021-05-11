package fr.thesmyler.terracleanup.util;

import org.junit.Assert;
import org.junit.Test;

public class IntRangeTest {
	
	@Test
	public void testCreate() {
		IntRange range = new IntRange(-1337, 42);
		Assert.assertEquals(range.lowerBound(), -1337);
		Assert.assertEquals(range.upperBound(), 42);
		Assert.assertThrows(IllegalArgumentException.class, () -> new IntRange(1000, -1000));
	}
	
	@Test
	public void testProperties() {
		IntRange range = new IntRange(-42, 54);
		Assert.assertTrue(range.center() == 6D);
		Assert.assertTrue(range.size() == 96);
	}
	
	@Test
	public void testMatch() {
		IntRange range = new IntRange(-42, 54);
		Assert.assertTrue(range.matches(0));
		Assert.assertTrue(range.matches(-42));
		Assert.assertTrue(range.matches(54));
		Assert.assertFalse(range.matches(60));
		Assert.assertFalse(range.matches(-60));
		Assert.assertTrue(range.above(85));
		Assert.assertFalse(range.above(20));
		Assert.assertTrue(range.below(-90));
		Assert.assertFalse(range.below(10));
	}
	
	@Test
	public void testIntersect() {
		IntRange range = new IntRange(-42, 54);
		Assert.assertTrue(range.intersects(new IntRange(0, 40)));
		Assert.assertTrue(range.intersects(new IntRange(40, 60)));
		Assert.assertTrue(range.intersects(new IntRange(-50, 10)));
		Assert.assertTrue(range.intersects(new IntRange(-90, 80)));
		Assert.assertFalse(range.intersects(new IntRange(60, 80)));
		Assert.assertFalse(range.intersects(new IntRange(-60, -50)));
		Assert.assertTrue(range.above(new IntRange(60, 80)));
		Assert.assertTrue(range.below(new IntRange(-70, -60)));
		Assert.assertTrue(range.intersects(new IntRange(54, 60)));
		Assert.assertTrue(range.intersects(new IntRange(-60, -42)));
		Assert.assertFalse(range.above(new IntRange(54, 60)));
		Assert.assertFalse(range.below(new IntRange(-60, -42)));
	}

}
