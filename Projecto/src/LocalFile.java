import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.FileSystemException;

public class LocalFile implements PCDFile {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int NUM_READERS = 3;
	private int currentReaders = 0;
	private boolean cadeadoEscrita = false;
	private File f;
	private String name;
	
	public LocalFile(File f) {
		this.f = f;
		this.name = f.getName();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public synchronized boolean readLock() {
		while (this.currentReaders == NUM_READERS || this.cadeadoEscrita) {
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		currentReaders++;
		return true;
	}

	@Override
	public synchronized boolean writeLock() {
		while (this.cadeadoEscrita || this.currentReaders != 0) {
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		this.cadeadoEscrita = true;
		return true;
	}

	@Override
	public synchronized void readUnlock() throws IOException {
		this.currentReaders--;
		notifyAll();
	}

	@Override
	public synchronized void writeUnlock() throws IOException {
		this.cadeadoEscrita = false;
		notifyAll();
	}

	@Override
	public boolean exists() throws IOException {
		return f.exists();
	}

	@Override
	public int length() throws FileSystemException, IOException {
		return (int) f.length();
	}

	@Override
	public String read() throws FileSystemException, IOException {
		return new String(read(0, length()));
	}

	public byte[] read(int offset, int length) throws FileSystemException {
		ByteBuffer buffer = null;
		RandomAccessFile aFile = null;
		try {
			aFile = new RandomAccessFile(getFullPath(), "r");
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

	private String getFullPath() {
		return f.getPath();
	}

	@Override
	public void write(String dataToWrite) throws FileSystemException, IOException {
		write(dataToWrite.getBytes(), 0);
	}

	public void write(byte[] dataToWrite, int offset) throws FileSystemException {
		RandomAccessFile aFile = null;
		try {
			aFile = new RandomAccessFile(getFullPath(), "rw");

			FileChannel channel = aFile.getChannel();
			if (offset > aFile.length()) {
				offset = (int) aFile.length();
			}

			ByteBuffer buf = ByteBuffer.wrap(dataToWrite);
			channel.position(offset);

			while (buf.hasRemaining()) {
				channel.write(buf);
			}
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
	}

	public synchronized void deleteFile(String name) {
		if (currentReaders == 0 && !cadeadoEscrita) {
			f.delete();
		} else {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public String toString() {
		return name;
	}

}
