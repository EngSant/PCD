import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystemException;
import java.util.ArrayList;

public class LocalDirectory implements PCDDirectory {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private ArrayList<LocalFile> ficheiros = new ArrayList<LocalFile>();
	private String[] lista;
	private Server s;
	
	public LocalDirectory (Server s) {
		this.s = s;
	}

	@Override
	public boolean fileExists(String name) throws IOException {
		for (LocalFile f :ficheiros) {
			if (f.getName().equals(name)) {
				System.out.println("Ficheiro existe");
				return true;
			}
		}
		return false;
	}

	@Override
	public PCDFile newFile(String name) throws FileSystemException, IOException {
		return null;
	}

	@Override
	public void delete(String name) throws FileSystemException, IOException {
		LocalFile f = s.getFile(name);
		f.deleteFile(name);
	}

	@Override
	public String[] getDirectoryList() throws FileSystemException, IOException {
		String[] lista = new String [ficheiros.size()];
		int i = 0;
		for (LocalFile l : ficheiros) {
			lista[i] = l.getName();
			i++;
		}
		return lista;
	}
	
	

	@Override
	public PCDFile getFile(String name) throws FileSystemException, IOException {
		for (PCDFile f : ficheiros) {
			if (f.getName().equalsIgnoreCase(name)) {
				return f;
			}
		}
		return null;
	}
	
	public void addLocalFile (LocalFile f) {
		ficheiros.add(f);
	}
	
	public void clearList () {
		this.ficheiros.clear();
	}

	@Override
	public String toString() {
		return "LocalDirectory [ficheiros=" + ficheiros + "]";
	}

	public ArrayList<LocalFile> getFicheiros() {
		return ficheiros;
	}

	
	
	
	
}
