import java.io.PrintWriter;
import java.net.Socket;

public class FilesTransferThread extends Thread {

	private Socket s;
	private PrintWriter out;
	private Server t;
	// private LocalFile f;

	public FilesTransferThread(PrintWriter out, Socket s, Server t) {
		// TODO Auto-generated constructor stub
		out = out;
		this.s = s;
		this.t = t;
	}

	@Override
	public void run() {
		System.out.println("Vou enviar os ficheiros");
		String[] n = t.getFileList();
		for (String i : n) {
			out.println(i);
			System.out.println(i);
		}
		out.println("FIM");
	}

}
