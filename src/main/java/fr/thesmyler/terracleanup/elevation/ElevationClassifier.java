package fr.thesmyler.terracleanup.elevation;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import fr.thesmyler.terracleanup.api.ICacheHolder;
import fr.thesmyler.terracleanup.api.IClassifier;
import fr.thesmyler.terracleanup.util.IntRange;
import fr.thesmyler.terracleanup.util.coordinates.IPosition2d;
import fr.thesmyler.terracleanup.util.coordinates.IPosition3d;
import net.buildtheearth.terraminusminus.dataset.scalar.MultiresScalarDataset;
import net.buildtheearth.terraminusminus.generator.EarthGeneratorPipelines;
import net.buildtheearth.terraminusminus.generator.EarthGeneratorSettings;
import net.buildtheearth.terraminusminus.projection.GeographicProjection;
import net.buildtheearth.terraminusminus.projection.OutOfProjectionBoundsException;

/**
 * Classifies 3D world sections based on their altitude relative to ground elevation.
 * 
 * @author SmylerMC
 *
 * @param <C> the column type that corresponds to the 3D section type {@link P}
 * @param <P> the 3D section type that will be classified
 */
public class ElevationClassifier<C extends IPosition2d, P extends IPosition3d<C>> implements IClassifier<P, ElevationClassification>, ICacheHolder {
	
	private final ExecutorService lookupPool;
	private final ExecutorService elevationComputePool;
	
	private final ConcurrentMap<C, Future<IntRange>> cache = new ConcurrentHashMap<>();
	
	private final MultiresScalarDataset heights;
	private final GeographicProjection projection;
	
	private final int highSkyStart;
	private final int deepStart;
	private final int stepSize;
	
	private final AtomicInteger toProcess = new AtomicInteger(0);
	
	/**
	 * Default constructor
	 * 
	 * @param settings the generation settings of the world to classify for
	 * @param stepSize the interval at which to sample ground elevation for, in blocks
	 * @param deepBelow depth under which a section will be classified as {@link ElevationClassification#UNDERGROUND_DEEP}
	 * @param highAbove height above which a section will be classified as {@link ElevationClassification#SKY_HIGH}
	 * @param workerCount number of threads to use when classification. This probably shouldn't be more than 4
	 */
	public ElevationClassifier(EarthGeneratorSettings settings, int stepSize, int deepBelow, int highAbove, int workerCount) {
		this.highSkyStart = highAbove;
		this.deepStart = highAbove;
		this.stepSize = stepSize;
		this.lookupPool = Executors.newFixedThreadPool(workerCount);
		this.elevationComputePool = Executors.newFixedThreadPool(workerCount);
		this.heights = (MultiresScalarDataset)EarthGeneratorPipelines.datasets(settings).get(EarthGeneratorPipelines.KEY_DATASET_HEIGHTS);
		this.projection = settings.projection();
	}
	
	/**
	 * Submits a classification task
	 * 
	 * @param position a 3d world section position of type {@link P}
	 * @return a {@link CompletableFuture} than will hold a {@link ElevationClassification} once completed
	 */
	public synchronized CompletableFuture<ElevationClassification> classify(P position){
		this.toProcess.incrementAndGet();
		return CompletableFuture.completedFuture(position).thenApplyAsync(p -> {
			try {
				return this.classifyFromRange(p.rangeY(), this.getElevationRange(p.column()));
			} catch (Exception e) {
				e.printStackTrace();
				return ElevationClassification.FAILED;
			} finally {
				this.toProcess.decrementAndGet();
			}
		}, this.lookupPool);
		
	}
	
	private IntRange getElevationRange(C column) throws InterruptedException, ExecutionException {
		Future<IntRange> future;
		synchronized(this.cache) {
			if(this.cache.containsKey(column)) {
				future = this.cache.get(column);
			}
			else {
				future = this.elevationComputePool.submit(() -> computeElevationRange(column));
				this.cache.put(column, future);
			}
		}
		return future.get();
	}
	
	private IntRange computeElevationRange(C column) throws InterruptedException, ExecutionException {
		int sampleStep = this.stepSize;
		IntRange xs = column.rangeX();
		IntRange zs = column.rangeZ();
		double minElevation = Integer.MAX_VALUE;
		double maxElevation = Integer.MIN_VALUE;
		for(int x = xs.lowerBound() + sampleStep / 2; x <= xs.upperBound(); x += sampleStep) {
			for(int z = zs.lowerBound() + sampleStep / 2; z <= zs.upperBound(); z += sampleStep) {
				try {
					double[] geo = this.projection.toGeo(x, z);
					double elevation = this.heights.getAsync(geo[0], geo[1]).get();
					minElevation = Math.min(minElevation, elevation);
					maxElevation = Math.max(maxElevation, elevation);
				} catch (OutOfProjectionBoundsException silenced) {}
			}
		}
		if(minElevation == Integer.MAX_VALUE || maxElevation == Integer.MIN_VALUE) return null;
		return new IntRange((int)Math.round(minElevation), (int)Math.round(maxElevation));
	}
	
	private ElevationClassification classifyFromRange(IntRange yRange, IntRange elevationRange) {
		if(elevationRange == null) return ElevationClassification.OUT_OF_BOUNDS;
		if(elevationRange.above(0)) elevationRange = new IntRange(0, 0); // Special case for oceans: we care about the surface, not the floor
		if(elevationRange.intersects(yRange)) return ElevationClassification.SURFACE;
		IntRange sky = elevationRange.extendUp(this.highSkyStart);
		if(sky.intersects(yRange)) return ElevationClassification.SKY;
		if(sky.above(yRange)) return ElevationClassification.SKY_HIGH;
		IntRange underground = elevationRange.extendDown(this.deepStart);
		if(underground.intersects(yRange)) return ElevationClassification.UNDERGROUND;
		return ElevationClassification.UNDERGROUND_DEEP;
	}

	@Override
	public void clearCache() {
		this.cache.clear();
	}
	
	@Override
	public int cacheCount() {
		return this.cache.size();
	}
	
	@Override
	public synchronized int queueSize() {
		return this.toProcess.get();
	}
	
	@Override
	public synchronized void terminate() {
		this.elevationComputePool.shutdownNow();
		this.clearCache();
	}

}
