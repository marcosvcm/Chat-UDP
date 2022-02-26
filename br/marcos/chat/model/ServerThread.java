///////////////////
//MARCOS V////////
/////////////////0 0
////////////////  -

package br.marcos.chat.model;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import javax.swing.JTextArea;
import java.util.ArrayList;

public class ServerThread implements Runnable {
	DatagramSocket socket;
	JTextArea visorGrande;
	ArrayList<Usuario> usersAceitaveis = new ArrayList<Usuario>();
	String nome;

	public ServerThread(DatagramSocket socket, JTextArea visorGrande, ArrayList<Usuario> usersAceitaveis, String nome) {
		this.socket = socket;
		this.visorGrande = visorGrande;
		this.usersAceitaveis = usersAceitaveis;
		this.nome = nome;
	}

	public Mensagem receberMensagem(DatagramSocket socket, ArrayList<Usuario> usersAceitaveis)
			throws UnknownHostException {
		String recebido = null;
		byte[] dados = new byte[Limites.getTamMax()];
		DatagramPacket bufferRecibo = null;
		Mensagem retorno;
		boolean passa = false;
		String ipOutro;
		int conta;

		// RECEBENDO MENSAGEM
		try {
			bufferRecibo = new DatagramPacket(dados, Limites.getTamMax());
			socket.receive(bufferRecibo);
			recebido = new String(bufferRecibo.getData(), 0, bufferRecibo.getLength());
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (!usersAceitaveis.isEmpty()) {
			retorno = Mensagem.fromString(recebido);

			// VENDO SE PODE ACEITAR MENSAGEM DO REMETENTE
			conta = 0;
			for (int i = 0; i < usersAceitaveis.size(); i++) {

				ipOutro = InetAddress.getByName(usersAceitaveis.get(i).getIp()).toString();

				if (ipOutro.contains(bufferRecibo.getAddress().toString())
						&& bufferRecibo.getPort() == usersAceitaveis.get(i).getPorta()) {
					passa = true;
					break;
				}
				conta++;
			}

			if (passa) {
				retorno = Mensagem.fromString(recebido);

				// ENVIANDO MENSAGEM DE CONFIRMAÇÃO, RESPOSTA
				if (retorno.getTexto().equals("6283185307()")) {
					ClientThread.enviarMensagemConf("3141592654()", nome, usersAceitaveis.get(conta).getIp(),
							bufferRecibo.getPort(), socket);
				} else if (retorno.getTexto().indexOf("2718281828(2718281828)--") == 0) {
					try {
						DatagramSocket socket2 = new DatagramSocket();
						ClientThread.enviarMensagemConf("2718281828(oi)", nome, usersAceitaveis.get(conta).getIp(),
								Integer.valueOf(retorno.getTexto().substring(24)), socket2);

						SThread nst = new SThread(socket2, visorGrande, nome);
						Thread segundaMao = new Thread(nst);
						segundaMao.start();
					} catch (SocketException e) {
						e.printStackTrace();
					}
				}
			} else {
				retorno = null;
			}
		} else {
			retorno = null;
		}
		return retorno;
	}

	@Override
	public void run() {
		Mensagem msg;
		while (true) {
			try {
				msg = receberMensagem(socket, usersAceitaveis);
			} catch (UnknownHostException e) {
				e.printStackTrace();
				msg = null;
			}

			// TRATAMENTO DA MENSAGEM RECEBIDA
			if (msg != null) {
				if ("sair(6283185307)".equals(msg.getTexto())) {
					visorGrande.append(msg.getNome() + " saiu.\n");
				} else if (msg.getTexto().equals("6283185307()")) {
					visorGrande.append("-->" + msg.getNome() + " entrou.\n");
				} else if (msg.getTexto().equals("3141592654()")) {
					visorGrande.append("-->" + msg.getNome() + " está aqui.\n");
				} else if (msg.getTexto().indexOf("2718281828(2718281828)--") == 0) {
					visorGrande.append("");
				} else {
					visorGrande.append(msg.getNome() + " => " + msg.getTexto() + "\n");
				}
			}
		}
	}

}
