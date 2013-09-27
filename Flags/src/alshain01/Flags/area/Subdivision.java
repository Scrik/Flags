package alshain01.Flags.area;

public interface Subdivision {
	
	/**
	 * @return True if the are is a subdivision of another area.
	 */
	public boolean isSubdivision();

	/**
	 * @return The subdivision ID
	 */
	public String getSystemSubID();
	
	/**
	 * @return True if the area is inheriting flags from the parent area.
	 */
	public boolean isInherited();
	
	/**
	 * @param value True if the area should inherit from the parent area.
	 * @return True if successfully set.
	 */
	public boolean setInherited(Boolean value);
}
