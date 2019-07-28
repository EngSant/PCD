import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

public class Client extends Thread {

	private static String IPServidor;
	private GUI GUI;
	private DefaultListModel<String> filesList = new DefaultListModel<String>();
	private Socket socketServidor;
	private ObjectOutputStream outServer;
	private ObjectInputStream inServer;
	private boolean reading = false;
	private boolean writing = false;

	public Client(String IPServidor, int PORTO) {
		try {
			InetAddress endereco = InetAddress.getByName(IPServidor);
			this.socketServidor = new Socket(endereco, PORTO);
			connectToServer(socketServidor);
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					createAndShowGUI();
				}
			});
		} catch (IOException e) {
			try {
				System.out.println("Nãoooo...");
				socketServidor.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
		
	}

	public void connectToServer(Socket socketServidor) {
		try {
			outServer = new ObjectOutputStream(socketServidor.getOutputStream());
			inServer = new ObjectInputStream(socketServidor.getInputStream());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			System.out.println("oi??");
			e1.printStackTrace();
		}
		System.out.println("Estou ligado");
		while (true) {
			Object received = null;
			try {
				received = inServer.readObject();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("dammmm...");
				e.printStackTrace();
			}
			if (received != null) {
				if (received.getClass().toString().endsWith("String")) {
					if (received.toString().contains("FIM")) {
						return;
					}
					filesList.addElement((String) received);
				}
			} else {
				System.out.println("Nao recebi nada");
				return;
			}
		}

	}

	public void getFiles() {
		filesList.clear();
	}

	public void getReadLock(String fileName) throws IOException, ClassNotFoundException {
		Pedido p = Pedido.CADEADOLEITURA;
		Message m1 = new Message(p, fileName);
		outServer.writeObject(m1);
		Boolean a = (Boolean) inServer.readObject();
		if (a) {
			this.reading=true;
			System.out.println("req reading = " + reading);
			String r = (String) inServer.readObject();
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					JFrame textReceived = new JFrame(fileName);
					JTextArea texto = new JTextArea(r);
					textReceived.add(texto);
					JButton close = new JButton("FECHAR");
					close.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e) {
							try {
								Pedido l = Pedido.DESBLOQUEIALEITURA;
								Message m2 = new Message(l, fileName);
								outServer.writeObject(m2);
								reading=false;
								System.out.println("giv reading = " + reading);
								textReceived.dispose();

							} catch (IOException e1) {
								e1.printStackTrace();
							}
						}
					});

					textReceived.add(close, BorderLayout.SOUTH);
					textReceived.setSize(300, 200);
					textReceived.setVisible(true);
					textReceived.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
				}
			});
		}
	}

	public RemoteFile callReadFile(String f) throws IOException {
		outServer.writeObject(f);
		Object o = inServer.read();
		RemoteFile a = null;
		while (o != null) {
			if (o instanceof RemoteFile) {
				a = (RemoteFile) o;
			}
		}
		return a;
	}

	public void getSize(String fileName) {
		Message m1 = new Message(Pedido.TAMANHO, fileName);
		try {
			outServer.writeObject(m1);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void getEditLock(String fileSelected) throws IOException, ClassNotFoundException {
		Message m1 = new Message(Pedido.CADEADOESCRITA, fileSelected);
		outServer.writeObject(m1);
		Boolean a = (Boolean) inServer.readObject();
		if (a) {
			this.writing = true;
			System.out.println("req writing = " + writing);
			String r = (String) inServer.readObject();
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					JFrame textReceived = new JFrame(fileSelected);
					JTextArea texto = new JTextArea(r);
					textReceived.add(texto);
					JButton close = new JButton("GRAVAR");
					close.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e) {

							try {

								Pedido l = Pedido.DESBLOQUEIAESCRITA;
								Message m2 = new Message(l, fileSelected, texto.getText());
								outServer.writeObject(m2);
								writing=false;
								System.out.println("giv writing = " + writing);
								textReceived.dispose();
							} catch (IOException e1) {
								e1.printStackTrace();
							}
						}
					});
					textReceived.add(close, BorderLayout.SOUTH);
					textReceived.setSize(300, 200);
					textReceived.setVisible(true);
					textReceived.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
				}
			});
			return;
		}

	}

	public void deleteFile(String fileSelected) {
		Message m1 = new Message(Pedido.APAGAR, fileSelected);
		try {
			outServer.writeObject(m1);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void createAndShowGUI() {
		this.GUI = new GUI(this);
	}

	public void sendMessage(String message) throws IOException {
		outServer.writeObject(message);
		outServer.flush();
	}

	public DefaultListModel<String> getFilesList() {
		return filesList;
	}

	public static void main(String[] args) {
		IPServidor = "localhost";
		int ServerPort = Integer.parseInt(args[1]);
		Client client = new Client(IPServidor, ServerPort);
		client.start();
		client.interrupt();
		try {
			sleep(5000);
		} catch (InterruptedException e) {		
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
