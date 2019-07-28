import java.io.IOException;
import java.io.Serializable;
import java.nio.file.FileSystemException;

public interface PCDFile extends Serializable {
	
	public String getName();
	public boolean readLock() throws IOException;
	public boolean writeLock() throws IOException;
	public void readUnlock() throws IOException;
	public void writeUnlock() throws IOException;
	public boolean exists() throws IOException;
	public abstract int length() throws FileSystemException, IOException;
	public abstract String read() throws FileSystemException, IOException;
	public abstract void write(String dataToWrite) throws FileSystemException, IOException;
	
}
