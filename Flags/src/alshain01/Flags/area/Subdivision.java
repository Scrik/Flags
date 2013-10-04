package alshain01.Flags.area;

/**
 * Interface that defines area types that can be subdivided into parent/child subdivisions.
 * 
 * @author Alshain01
  */
public interface Subdivision {
	/**
	 * Determines whether or not the area is a subdivision.
	 * 
	 * @return True if the are is a subdivision of another area.
	 */
	public boolean isSubdivision();

	/**
	 * Retrieves the id of the subdivision.
	 * 
	 * @return The subdivision ID, null if not a subdivision
	 */
	public String getSystemSubID();
	
	/**
	 * Determines if the subdivision is inheriting flags from it's parent
	 * 
	 * @return True if the area is inheriting.
	 */
	public boolean isInherited();
	
	/**
	 * Changes whether or not a subdivision is inheriting from it's parent
	 * 
	 * @param value True if the area should inherit from the parent area.
	 * @return True if successfully set.
	 */
	public boolean setInherited(Boolean value);
}
