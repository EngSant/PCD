import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.util.RandomAccess;

public class RemoteFile implements PCDFile {

	private static final long serialVersionUID = 1L;
	private File ficheiro;
	private String name;
	private boolean readlock = false;
	private boolean writelock = false;
	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		this.name = ficheiro.getName();
		return name;
	}

	@Override
	public boolean readLock() throws IOException {
		// TODO Auto-generated method stub
		return !(this.readlock);
	}

	@Override
	public boolean writeLock() throws IOException {
		// TODO Auto-generated method stub
		return !(this.writelock);
	}

	@Override
	public void readUnlock() throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeUnlock() throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean exists() throws IOException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int length() throws FileSystemException, IOException {
		// TODO Auto-generated method stub
		int length = (int) ficheiro.getTotalSpace();
		return length;
	}

	@Override
	public String read() throws FileSystemException, IOException {
		// TODO Auto-generated method stub
		return new String(read(0, length()));
	}

	@Override
	public void write(String dataToWrite) throws FileSystemException, IOException {
		// TODO Auto-generated method stub

	}
	
	public byte[] read(int offset, int length) throws FileSystemException {
		ByteBuffer buffer = null;
		RandomAccessFile aFile = null;
		try {
			aFile = new RandomAccessFile(getName(), "r");
			FileChannel channel = aFile.getChannel();

			if (offset > aFile.length())
				return null;

			if (offset + length > aFile.length()) {
				length = (int) aFile.length() - offset;

			}

			channel.position(offset);
			buffer = ByteBuffer.allocate(length);
			channel.read(buffer);
		} catch (IOException e) {
			throw new FileSystemException(e.getMessage());

		} finally {
			if (aFile != null)
				try {
					aFile.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return buffer.array();
	}

}
