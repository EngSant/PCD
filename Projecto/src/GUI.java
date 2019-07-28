import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class GUI {

	private Client client;
	private JFrame frame;

	public GUI(Client client) {
		this.client = client;
		frame = new JFrame("Cliente");
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		frame.setSize(600, 300);
		addFrameContent();
		frame.pack();
		frame.setVisible(true);
	}

	private void addFrameContent() {
		JTextField text = new JTextField();
		text.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					client.sendMessage(text.getText());
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		frame.add(text, BorderLayout.NORTH);
		DefaultListModel<String> l = client.getFilesList();
		JList<String> lista = new JList<String>(l);
		frame.add(lista, BorderLayout.CENTER);
		JPanel buttonpanel = new JPanel();
		JButton size = new JButton("Tamanho");
		size.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				String f = lista.getSelectedValue();
				client.getSize(f);
			}
		});
		buttonpanel.add(size);
		JButton show = new JButton("Exibir");
		show.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String fileName = lista.getSelectedValue();
				try {
					client.getReadLock(fileName);
				} catch (ClassNotFoundException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}

		});
		buttonpanel.add(show);
		JButton edit = new JButton("Editar");
		edit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String fileSelected = lista.getSelectedValue();
				try {
					client.getEditLock(fileSelected);
				} catch (ClassNotFoundException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		buttonpanel.add(edit);
		JButton novo = new JButton("Novo");
		novo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String nomeficheiro = JOptionPane.showInputDialog("Escreve o nome de ficheiro");
				JFrame frameCriar = new JFrame(nomeficheiro);
				File file = new File((System.getProperty("user.dir") + "\\Drive\\") + nomeficheiro + ".txt");
				System.out.println(file);
				if (!file.exists()) {
					try {
						file.createNewFile();
					} catch (IOException e1) {
						e1.printStackTrace();
						System.out.println("Já existe o ficheiro com este nome!");
					}
				} else {
					JOptionPane.showMessageDialog(null, "Já existe o ficheiro com este nome! Tenta novamente!");
					return;
				}
				JTextArea text = new JTextArea();
				text.setLineWrap(true);
				JScrollPane listPane = new JScrollPane(text);
				frameCriar.add(listPane);
				System.out.println(text.getText());

				JButton gravar = new JButton("Gravar");
				gravar.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						System.out.println("O conteudo de ficheiro é: " + text.getText());
						File file = new File((System.getProperty("user.dir") + "\\Drive\\") + nomeficheiro + ".txt");
						FileWriter fw = null;
						try {
							fw = new FileWriter(file);
						} catch (IOException e1) {
							e1.printStackTrace();
						}
						String str = text.getText();
						for (int i = 0; i < str.length(); i++) {
							if (str.charAt(i) == 10) {
								try {
									fw.write(13);
								} catch (IOException e1) {
									e1.printStackTrace();
								}
								try {
									fw.write(10);
								} catch (IOException e1) {
									e1.printStackTrace();
								}
							} else {
								try {
									fw.write(str.charAt(i));
								} catch (IOException e1) {
									e1.printStackTrace();
								}
							}
						}
						try {
							fw.close();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
						frameCriar.dispose();
					}

				});
				frameCriar.add(gravar, BorderLayout.SOUTH);
				frameCriar.setSize(300, 300);
				frameCriar.setLocation(500, 400);
				frameCriar.setLocationRelativeTo(frame);
				frameCriar.setVisible(true);
				frameCriar.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

			}
		});
		buttonpanel.add(novo);
		JButton delete = new JButton("Apagar");
		delete.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String t = lista.getSelectedValue();
				client.deleteFile(t);
			}
		});
		buttonpanel.add(delete);
		frame.add(buttonpanel, BorderLayout.SOUTH);
	}


}
