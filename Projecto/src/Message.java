import java.io.Serializable;

public class Message implements Serializable {

	private Pedido p;
	private String texto;
	private String nomeFicheiro;
	
	public Message (Pedido p, String nomeFicheiro, String texto) {
		this.p = p;
		this.nomeFicheiro = nomeFicheiro;
		this.texto = texto;
	}

	public Message(Pedido p, String fileName) {
		this.p = p;
		this.nomeFicheiro = fileName;
	}

	public Pedido getPedido() {
		return p;
	}

	public void setPedido(Pedido p) {
		this.p = p;
	}

	public String getTexto() {
		return texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}

	public String getNomeFicheiro() {
		return nomeFicheiro;
	}

	public void setNomeFicheiro(String nomeFicheiro) {
		this.nomeFicheiro = nomeFicheiro;
	}
	
	
	
}
