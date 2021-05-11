package fr.thesmyler.terracleanup.elevation;

import java.util.concurrent.ExecutionException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import fr.thesmyler.terracleanup.util.coordinates.Region3dColumn;
import fr.thesmyler.terracleanup.util.coordinates.Region3dPosition;
import net.buildtheearth.terraminusminus.generator.EarthGeneratorSettings;

public class ElevationClassifierTest {

	private ElevationClassifier<Region3dColumn, Region3dPosition> classifier;

	@Before
	public void prepare() {
		EarthGeneratorSettings bte = EarthGeneratorSettings.parse(EarthGeneratorSettings.BTE_DEFAULT_SETTINGS);
		this.classifier = new ElevationClassifier<>(bte, 8, 1000, 1000, 1);
	}

	@Test
	public void classify() throws InterruptedException, ExecutionException {

		// Paris
		Assert.assertTrue(this.classifier.classify(new Region3dPosition(11139, 0, -19726)).get() == ElevationClassification.SURFACE);
		Assert.assertTrue(this.classifier.classify(new Region3dPosition(11139, 1, -19726)).get() == ElevationClassification.SKY);
		Assert.assertTrue(this.classifier.classify(new Region3dPosition(11139, -1, -19726)).get() == ElevationClassification.UNDERGROUND);
		Assert.assertTrue(this.classifier.classify(new Region3dPosition(11139, 50, -19726)).get() == ElevationClassification.SKY_HIGH);
		Assert.assertTrue(this.classifier.classify(new Region3dPosition(11139, -50, -19726)).get() == ElevationClassification.UNDERGROUND_DEEP);

		// Mount Everest
		Assert.assertTrue(this.classifier.classify(new Region3dPosition(39985, 34, -13694)).get() == ElevationClassification.SURFACE);
		Assert.assertTrue(this.classifier.classify(new Region3dPosition(39985, 36, -13694)).get() == ElevationClassification.SKY);
		Assert.assertTrue(this.classifier.classify(new Region3dPosition(39985, 32, -13694)).get() == ElevationClassification.UNDERGROUND);
		Assert.assertTrue(this.classifier.classify(new Region3dPosition(39985, 45, -13694)).get() == ElevationClassification.SKY_HIGH);
		Assert.assertTrue(this.classifier.classify(new Region3dPosition(39985, 28, -13694)).get() == ElevationClassification.UNDERGROUND_DEEP);
		
		// Point 0 - Ocean test, what we case about is the surface, not the ocean floor
		Assert.assertTrue(this.classifier.classify(new Region3dPosition(3270, 0, 1772)).get() == ElevationClassification.SURFACE);
		Assert.assertTrue(this.classifier.classify(new Region3dPosition(3270, 1, 1772)).get() == ElevationClassification.SKY);
		Assert.assertTrue(this.classifier.classify(new Region3dPosition(3270, -1, 1772)).get() == ElevationClassification.UNDERGROUND);
		Assert.assertTrue(this.classifier.classify(new Region3dPosition(3270, 5, 1772)).get() == ElevationClassification.SKY_HIGH);
		Assert.assertTrue(this.classifier.classify(new Region3dPosition(3270, -5, 1772)).get() == ElevationClassification.UNDERGROUND_DEEP);

	}

	@After
	public void cleanup() {
		this.classifier.terminate();
	}

}
