import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.FileSystemException;

public class Tarefa implements Runnable {

	private Message m;
	private ObjectOutputStream outClient;
	private ObjectInputStream inClient;
	private Server s;

	public Tarefa(Message m, ObjectInputStream inClient, ObjectOutputStream outClient, Server s) {
		this.m = m;
		this.inClient = inClient;
		this.outClient = outClient;
		this.s = s;
	}
	

	@Override
	public void run() {
		switch (m.getPedido()) {
		case APAGAR:
			deleteFile(m.getPedido());
			break;
		case CADEADOESCRITA:
			writeText(m.getPedido());
			break;
		case CADEADOLEITURA:
			sendText(m.getPedido());
			break;
		case CRIAR:
			break;
		case DESBLOQUEIAESCRITA:
			unlockWriting(m.getPedido());
			break;
		case DESBLOQUEIALEITURA:
			unlockReading(m.getPedido());
			break;
		case TAMANHO:
			sendSize(m.getPedido());
			break;
		default:
			break;

		}

	}

	private void deleteFile(Pedido pedido) {
		String name = m.getNomeFicheiro();
		LocalDirectory ld = s.getLocalDirectory();
		try {
			ld.delete(name);
		} catch (FileSystemException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void unlockReading(Pedido pedido) {	
		String name = m.getNomeFicheiro();
		LocalFile f = s.getFile(name);
		try {
			f.readUnlock();
			//s.getFila(f).filaPost();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendSize(Pedido p) {
		if (p.equals(Pedido.TAMANHO)) {
			String name = m.getNomeFicheiro();
			if (s.fileExists(name)) {
				s.getFileSize(name);
			}
		}
	}

	public void sendText(Pedido p) {
		
		String name = m.getNomeFicheiro();
		LocalFile f = s.getFile(name);
		//s.getFila(f).filaWait();
		if (s.fileExists(name)) {			
			try {
				if (f.readLock()) {
					outClient.writeObject(true);
					outClient.writeObject(f.read());
					outClient.flush();
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void writeText (Pedido p) {
		
		String name = m.getNomeFicheiro();
		LocalFile f = s.getFile(name);
		//s.getSem(f).semWait();
		if (s.fileExists(name)) {
			
			try {
				if (f.writeLock()) {
					outClient.writeObject(true);
					outClient.writeObject(f.read());
					outClient.flush();
				}
			} catch (FileSystemException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void unlockWriting(Pedido p) {
		String name = m.getNomeFicheiro();
		LocalFile l = s.getFile(name);
		try {
			String j = m.getTexto();
			l.write(j);
			l.writeUnlock();
			//s.getSem(l).semPost();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

}
