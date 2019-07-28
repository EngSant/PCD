import java.io.IOException;
import java.io.Serializable;
import java.nio.file.FileSystemException;

public interface PCDDirectory extends Serializable {

	public abstract boolean fileExists(String name) throws IOException;
	
	public abstract PCDFile newFile(String name) throws FileSystemException, IOException;
	
	public abstract void delete(String name) throws FileSystemException, IOException;
	
	public abstract String[] getDirectoryList() throws FileSystemException, IOException;
	
	public abstract PCDFile getFile(String name) throws FileSystemException, IOException;
	
}
