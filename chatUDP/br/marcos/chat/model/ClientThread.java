///////////////////
//MARCOS V////////
/////////////////0 0
////////////////  -

package br.marcos.chat.model;

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ClientThread {

	// ENVIAR OS PACOTES
	public static void enviar(ArrayList<Usuario> outroUser, String msg, DatagramSocket cliente) {
		byte[] data = new byte[Limites.getTamMax()];
		data = msg.getBytes();
		for (int i = 0; i < outroUser.size(); i++) {
			try {
				InetAddress ina = InetAddress.getByName(outroUser.get(i).getIp());
				DatagramPacket enviando = new DatagramPacket(data, data.length, ina, outroUser.get(i).getPorta());
				cliente.send(enviando);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	// ENVIAR MENSAGEM DO USUARIO
	public static void enviarMensagem(JTextField visorPequeno, JTextArea visorGrande, ArrayList<Usuario> outroUser,
			String nome, DatagramSocket cliente) {

		Mensagem msgEncapsulado = new Mensagem(visorPequeno.getText(), nome);
		String msg;

		if (!msgEncapsulado.getTexto().isBlank()) {

			visorGrande.append("tu => " + msgEncapsulado.getTexto() + "\n");

			msg = msgEncapsulado.toString();
			enviar(outroUser, msg, cliente);

			visorPequeno.setText("");
		}
	}

	// ENVIAR MENSAGEM PELOS BOTOES
	public static void enviarMensagem(String texto, JTextArea visorGrande, ArrayList<Usuario> outroUser, String nome,
			DatagramSocket cliente) {
		ArrayList<Usuario> novosUs = new ArrayList<Usuario>();
		ArrayList<Usuario> antigosUs = new ArrayList<Usuario>();
		Usuario usuarioAdd;
		Mensagem msgEncapsulado = new Mensagem(texto, nome);
		String msg;

		novosUs.addAll(outroUser);

		if (!msgEncapsulado.getTexto().isBlank()) {
			if (msgEncapsulado.getTexto().equalsIgnoreCase("sair(6283185307)")) {
				visorGrande.append("--> tu saiste.\n");
				msg = msgEncapsulado.toString();
				enviar(outroUser, msg, cliente);
				novosUs.clear();
			} else if ("trocar()".equals(msgEncapsulado.getTexto())) {
				String contatosTroca;
				String[] entradaUsuarios1;
				boolean jaTem;
				JTextField txtPorta2 = new JTextField();

				JPanel painelPort2 = new JPanel(new BorderLayout());
				painelPort2.add(new JLabel(""), BorderLayout.WEST);
				txtPorta2.setEnabled(true);
				painelPort2.add(txtPorta2, BorderLayout.CENTER);
				txtPorta2.setDisabledTextColor(Color.black);
				txtPorta2.setText("localhost:12346; localhost:12398; localhost:12387");

				JLabel entradaMsg1 = new JLabel("Troca de contatos.");
				JLabel entradaMsg2 = new JLabel("Onde conectar:");
				JLabel entradaMsg3 = new JLabel("digite: IP:porta; IP:porta; ...");

				Object[] texts = { entradaMsg1, entradaMsg2, painelPort2, entradaMsg3 };
				JOptionPane.showMessageDialog(null, texts);

				contatosTroca = txtPorta2.getText();

				antigosUs.clear();
				antigosUs.addAll(outroUser);
				novosUs.clear();
				entradaUsuarios1 = contatosTroca.split("; ");
				for (int i = 0; i < entradaUsuarios1.length; i++) {
					String[] entradaUsuarios2 = entradaUsuarios1[i].split(":");
					try {
						usuarioAdd = new Usuario(entradaUsuarios2[0], Integer.parseInt(entradaUsuarios2[1]));
						novosUs.add(usuarioAdd);
						visorGrande.append("--> Trocando os contatos...\n");
						outroUser.addAll(novosUs);
						visorGrande.append("--> Contatos trocados.\n");
					} catch (Exception e) {
						novosUs.addAll(antigosUs);
					}
				}

				for (int i = 0; i < novosUs.size(); i++) {
					jaTem = false;
					for (int j = 0; j < antigosUs.size(); j++) {
						if (novosUs.get(i).equals(antigosUs.get(j))) {
							jaTem = true;
							break;
						}
					}
					if (!jaTem) {
						enviarMensagemConf("6283185307()", nome, novosUs.get(i).getIp(), novosUs.get(i).getPorta(),
								cliente);
					}
				}

				for (int i = 0; i < antigosUs.size(); i++) {
					jaTem = false;
					for (int j = 0; j < novosUs.size(); j++) {
						if (antigosUs.get(i).equals(novosUs.get(j))) {
							jaTem = true;
							break;
						}
					}
					if (!jaTem) {
						enviarMensagemConf("sair(6283185307)", nome, antigosUs.get(i).getIp(),
								antigosUs.get(i).getPorta(), cliente);
					}
				}
			} else if (msgEncapsulado.getTexto().indexOf("trocar(") == 0) {
				String contatosTroca;
				String[] entradaUsuarios1;
				boolean jaTem;

				contatosTroca = texto;
				contatosTroca = contatosTroca.substring(7, contatosTroca.length() - 1);

				antigosUs.clear();
				antigosUs.addAll(outroUser);
				novosUs.clear();
				entradaUsuarios1 = contatosTroca.split("; ");
				for (int i = 0; i < entradaUsuarios1.length; i++) {
					String[] entradaUsuarios2 = entradaUsuarios1[i].split(":");
					try {
						usuarioAdd = new Usuario(entradaUsuarios2[0], Integer.parseInt(entradaUsuarios2[1]));
						novosUs.add(usuarioAdd);
						outroUser.addAll(novosUs);
					} catch (Exception e) {
						visorGrande.append("--> Entrada dos contatos invalida.");
						novosUs.addAll(antigosUs);
					}
				}

				for (int i = 0; i < novosUs.size(); i++) {
					jaTem = false;
					for (int j = 0; j < antigosUs.size(); j++) {
						if (novosUs.get(i).equals(antigosUs.get(j))) {
							jaTem = true;
							break;
						}
					}
					if (!jaTem) {
						enviarMensagemConf("6283185307()", nome, novosUs.get(i).getIp(), novosUs.get(i).getPorta(),
								cliente);
					}
				}

				for (int i = 0; i < antigosUs.size(); i++) {
					jaTem = false;
					for (int j = 0; j < novosUs.size(); j++) {
						if (antigosUs.get(i).equals(novosUs.get(j))) {
							jaTem = true;
							break;
						}
					}
					if (!jaTem) {
						enviarMensagemConf("sair()", nome, antigosUs.get(i).getIp(), antigosUs.get(i).getPorta(),
								cliente);
					}
				}
			} else if ("arqvs()".equalsIgnoreCase(msgEncapsulado.getTexto())) {
				CThread nct = new CThread(visorGrande, nome, cliente);
				Thread maoInvisivel = new Thread(nct);
				maoInvisivel.start();
			} else {
				msg = msgEncapsulado.toString();
				enviar(outroUser, msg, cliente);
			}
			outroUser.clear();
			outroUser.addAll(novosUs);
		}
	}

	// ENVIAR MENSAGEM DE CONFIRMAÇÃO
	public static void enviarMensagemConf(String texto, String nome, String ip, int porta, DatagramSocket cliente) {
		Mensagem msgEncapsulado = new Mensagem(texto, nome);
		String msg = msgEncapsulado.toString();
		byte[] data = new byte[Limites.getTamMax()];
		data = msg.getBytes();

		if (!texto.isBlank()) {
			try {
				InetAddress ina = InetAddress.getByName(ip);
				DatagramPacket enviando = new DatagramPacket(data, data.length, ina, porta);
				cliente.send(enviando);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	// ENVIAR MENSAGEM DE CONFIRMAÇÃO DA TRANSFERÊNCIA DE ARQUIVOS
	public static void sendAck(int foundLast, DatagramSocket socket, InetAddress address, int port) throws IOException {
		// send acknowledgement
		byte[] ackPacket = new byte[2];
		ackPacket[0] = (byte) (foundLast >> 8);
		ackPacket[1] = (byte) (foundLast);
		DatagramPacket acknowledgement = new DatagramPacket(ackPacket, ackPacket.length, address, port);
		socket.send(acknowledgement);
	}

}
