///////////////////
//MARCOS V////////
/////////////////0 0
////////////////  -

package br.marcos.chat.model;

public class Mensagem {
	private String texto, nome;

	public Mensagem(String texto, String deQuem) {
		this.texto = texto;
		this.nome = deQuem;
	}

	public String getNome() {
		return this.nome;
	}

	public String getTexto() {
		return this.texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}

	public String toString() {
		String retorno = this.nome + "--0--" + this.texto;
		return retorno;
	}

	static Mensagem fromString(String texto) {
		String palavras, pessoa;
		int separacao;
		Mensagem retorno;

		separacao = texto.indexOf("--0--");

		if (separacao > 0) {
			pessoa = texto.substring(0, separacao);
			palavras = texto.substring(separacao + 5);
			retorno = new Mensagem(palavras, pessoa);
		} else {
			retorno = new Mensagem(texto, texto);
		}
		return retorno;
	}
}
