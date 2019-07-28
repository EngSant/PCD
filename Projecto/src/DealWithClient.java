
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketException;
import java.nio.file.FileSystemException;

public class DealWithClient extends Thread {

	private Socket s;
	private Server t;
	private ObjectOutputStream outClient;
	private ObjectInputStream inClient;

	public DealWithClient(Socket s, Server t) throws IOException {
		this.s = s;
		this.t = t;
		try {
			doConnections(s);
		} catch (IOException e) {
			e.printStackTrace();
			interrupted();
			s.close();
			return;
		}
	}

	public void doConnections(Socket s) throws IOException {
		inClient = new ObjectInputStream(s.getInputStream());
		outClient = new ObjectOutputStream(s.getOutputStream());
	}

	@Override
	public void run() {
		try {
			sendFiles();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		while (true) {
			try {
				System.out.println("Estou à escuta");
				Message m = (Message) inClient.readObject();
				if (m instanceof Message) {
					//responseToRequest(m);
					t.getPool().addTarefa(new Tarefa(m, inClient, outClient, t));
				}
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Fui interrompido");
				interrupted();
				return;
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	public void responseToRequest(Message m) {
		Pedido p = m.getPedido();
		String fileName = m.getNomeFicheiro();
		switch (p) {
		case CADEADOLEITURA:
			try {
				LocalFile f = t.getFile(fileName);
				t.getFila(f).filaWait();
				if (t.fileExists(fileName)) {
					
					if (f.readLock()) {
						outClient.writeObject(true);
						System.out.print("Cadeado dado\n");
						outClient.writeObject(f.read());
						outClient.flush();
						
					} 
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		case APAGAR:
			String name = m.getNomeFicheiro();
			LocalDirectory ld = t.getLocalDirectory();
			try {
				ld.delete(name);
			} catch (FileSystemException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		case CADEADOESCRITA:
			try {
				LocalFile f = t.getFile(fileName);
				t.getSem(f).semWait();
				if (t.fileExists(fileName)) {
					
					System.out.println("Enviar o ficheiro " + f.toString() + " com cadeado " + f.getClass());

					if (f.writeLock()) {
						outClient.writeObject(true);
						System.out.println("Pode escrever\n");
						outClient.writeObject(f.read());
						outClient.flush();
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		case CRIAR:
			break;
		case TAMANHO:
			if (t.fileExists(fileName)) {
				t.getFileSize(fileName);

			}
			break;
		case DESBLOQUEIALEITURA:
			LocalFile f = t.getFile(fileName);
			try {
				f.readUnlock();
				t.getFila(f).filaPost();
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		case DESBLOQUEIAESCRITA:
			System.out.println("Recebi o pedido " + p);
			LocalFile l = t.getFile(fileName);
			try {
				String j = m.getTexto();
				l.write(j);
				l.writeUnlock();
				t.getSem(l).semPost();
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;

		default:
			break;
		}
	}

	public void sendFiles() throws IOException {
		for (File f : t.getFiles()) {
			outClient.writeObject(f.getName());
		}
		outClient.writeObject("FIM");
	}
}
