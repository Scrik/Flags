package alshain01.Flags.data;

public interface SQLDataStore extends DataStore {
	public boolean isConnected();
	
	public void close();
}
