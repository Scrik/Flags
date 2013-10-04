package alshain01.Flags.area;

/**
 * Interface that defines area types that can be removed from the server
 * by practical non-administrative means.
 * 
 * @author Alshain01
  */
public interface Removable {
	/**
	 * Permanently removes the area from the data store
	 * USE CAUTION!
	 */
	public void remove();
}
