import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.FileSystemException;
import java.util.ArrayList;

import javax.swing.JOptionPane;

public class Server {

	public int PORTO;
	private File[] files;
	private String[] fileList;
	private LocalDirectory listaFicheiros;
	private ThreadPool pool;
	private ArrayList<Semaforo> sem;
	private ArrayList<FilaBloqueante> fila;

	public Server(String path, int PORTO) {
		this.PORTO = PORTO;
		this.files = new File(System.getProperty("user.dir") + "/" + path).listFiles();
		this.listaFicheiros = new LocalDirectory(this);
		getFileList();
		pool = new ThreadPool (10);
/*		this.sem = new ArrayList<Semaforo>();
		this.fila = new ArrayList<FilaBloqueante>();
		for (LocalFile lf : listaFicheiros.getFicheiros()) {
			this.sem.add(new Semaforo(1, lf));
			this.fila.add(new FilaBloqueante(3, lf));
		}
		sem = new Semaforo(1);
		fila = new FilaBloqueante(3);*/
	}
	

	public void startServing() throws IOException {
		ServerSocket s = new ServerSocket(PORTO);
		System.out.println("Servidor ligado!");
		Socket t;
		try {
			while (true) {
				t = s.accept();
				System.out.println("Ligação aceite: " + t);
				new DealWithClient(t, this).start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public String[] getFileList() {
		this.fileList = new String[files.length];
		for (int i = 0; i < files.length; i++) {
			System.out.println(files[i].getName());
			listaFicheiros.addLocalFile(new LocalFile(files[i]));
			fileList[i] = files[i].getName();
		}
		return fileList;
	}

	public File[] getFiles() {
		return files;
	}

	public void setFiles(File[] files) {
		this.files = files;
	}

	public boolean fileExists(String fileName) {
		try {
			return listaFicheiros.fileExists(fileName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public LocalFile getFile(String name) {
		try {
			return (LocalFile) listaFicheiros.getFile(name);
		} catch (FileSystemException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	

	public Semaforo getSem(LocalFile lf) {
		for (Semaforo sm : sem) {
			if (sm.getLf().equals(lf)) {
				return sm;
			}
		}
		return null;
	}
	
	

	public FilaBloqueante getFila(LocalFile lf) {
		for (FilaBloqueante fb : fila) {
			if (fb.getLf().equals(lf)) {
				return fb;
			}
		}
		return null;
	}


	public ThreadPool getPool() {
		return pool;
	}
	
	public static boolean deleteFile(String fileName) {
		File file = new File((System.getProperty("user.dir") + "\\Drive\\") + fileName);
		System.out.println(file);
		if (file.exists() && file.isFile()) {
			if (file.delete()) {
				System.out.println(fileName + " Sucesso!");
				return true;
			} else {
				System.out.println(fileName + " Falha!");
				return false;
			}
		} else {
			System.out.println(fileName + " Não existe!");
			return false;
		}
	}

	public void getFileSize(String fileName) {
		File file = new File((System.getProperty("user.dir") + "\\Drive\\") + fileName);
		if (file.exists() && file.isFile()) {
			String str = new String("Fcheiro " + fileName + " tem tamanho de: " + file.length());
			JOptionPane.showMessageDialog(null, str);
		}
	}

	public LocalDirectory getLocalDirectory() {
		return listaFicheiros;
	}

	public static void main(String[] args) {

		int PORTO = Integer.parseInt(args[1]);
		Server servidor = new Server(args[0], PORTO);
		try {
			servidor.startServing();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}



}
