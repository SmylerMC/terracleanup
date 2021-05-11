package fr.thesmyler.terracleanup.elevation;

/**
 * This enum is used by {@link ElevationDiscriminator} as a result of a classification
 * 
 * @author SmylerMC
 * 
 */
public enum ElevationClassification {
	
	/** The classification failed */
	FAILED,
	
	/** The section was out of projection bounds */
	OUT_OF_BOUNDS,
	
	/** The section is deep below the surface */
	UNDERGROUND_DEEP,
	
	/**	The section is underground */
	UNDERGROUND,
	
	/** The section intersects with the surface */
	SURFACE,
	
	/** The section is above the ground */
	SKY,
	
	/** The section is high above the ground */
	SKY_HIGH;
	
}
