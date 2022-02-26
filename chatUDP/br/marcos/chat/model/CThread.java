///////////////////
//MARCOS V////////
/////////////////0 0
////////////////  -

package br.marcos.chat.model;

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JOptionPane;

public class CThread implements Runnable {

	private JTextArea visorGrande;
	private String nome;
	private DatagramSocket cliente;

	public CThread(JTextArea visorGrande, String nome, DatagramSocket cliente) {
		this.cliente = cliente;
		this.nome = nome;
		this.visorGrande = visorGrande;
	}

	// ESCOLHER O ARQUIVO A SER ENVIADO E ENVIAR SEU NOME
	public static void ready(JTextArea visorGrande, String nome, DatagramSocket cliente) {
		String host, fileName;
		int port, portaArq;
		InetAddress address;
		JTextField txtPorta = new JTextField();
		JTextField txtIp = new JTextField();
		while (true) {
			JPanel painelPort = new JPanel(new BorderLayout());
			painelPort.add(new JLabel("Porta:"), BorderLayout.WEST);
			txtPorta.setEnabled(true);
			painelPort.add(txtPorta, BorderLayout.CENTER);
			txtPorta.setDisabledTextColor(Color.black);
			txtPorta.setText("12346");

			JPanel painelIp = new JPanel(new BorderLayout());
			painelIp.add(new JLabel("IP:"), BorderLayout.WEST);
			txtIp.setEnabled(true);
			painelIp.add(txtIp, BorderLayout.CENTER);
			txtIp.setDisabledTextColor(Color.black);
			txtIp.setText("127.0.0.1");

			JLabel entradaMsg1 = new JLabel("Enviar para:");
			Object[] texts = { entradaMsg1, painelPort, painelIp };
			JOptionPane.showMessageDialog(null, texts);

			host = txtIp.getText();
			port = Integer.parseInt(txtPorta.getText());

			try {
				address = InetAddress.getByName(host);
				break;
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		// receber a nova porta da outra pessoa
		DatagramSocket socket = null;
		try {
			byte[] dados = new byte[Limites.getTamMax()];
			socket = new DatagramSocket();
			DatagramPacket dgp = new DatagramPacket(dados, Limites.getTamMax());

			ClientThread.enviarMensagemConf("2718281828(2718281828)--" + String.valueOf(socket.getLocalPort()), nome,
					host, port, cliente);
			socket.receive(dgp);
			portaArq = dgp.getPort();

			JFileChooser jfc = new JFileChooser(); // ESCOLHENDO ARQUIVO
			jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
			if (jfc.isMultiSelectionEnabled()) {
				jfc.setMultiSelectionEnabled(false);
			}

			int r = jfc.showOpenDialog(null);
			if (r == JFileChooser.APPROVE_OPTION) {
				File f = jfc.getSelectedFile();
				fileName = f.getName();

				System.out.println(fileName);

				byte[] fileNameBytes = fileName.getBytes();
				DatagramPacket fileStatPacket = new DatagramPacket(fileNameBytes, fileNameBytes.length, address,
						portaArq);
				socket.send(fileStatPacket); // ENVIANDO NOME DO ARQUIVO

				byte[] fileByteArray = readFileToByteArray(f); // ARRAY DE BYTES DO ARQUIVO
				if (fileByteArray.length <= 20000000) { // VENDO O TAMANHO DO ARQUIVO
					visorGrande.append("--> Enviando arquivo.\n--> Aguarde.\n");
					sendFile(socket, fileByteArray, address, portaArq, visorGrande);
				} else {
					visorGrande.append("--> Arquivo muito grande.\n");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		socket.close();
	}

	private static void sendFile(DatagramSocket socket, byte[] fileByteArray, InetAddress address, int portaArq,
			JTextArea visorGrande) throws IOException {
		int sequenceNumber = 0;
		boolean flag;
		int ackSequence = 0;
		int numReenvs = 0;

		for (int i = 0; i < fileByteArray.length; i = i + 1021) {
			sequenceNumber += 1;

			// OS PRIMEIROS DOIS BITS DA MENSAGEM SÃO PARA CONTROLE (INTEGRIDADE E ORDEM)
			byte[] message = new byte[1024];
			message[0] = (byte) (sequenceNumber >> 8);
			message[1] = (byte) (sequenceNumber);

			if ((i + 1021) >= fileByteArray.length) {
				flag = true;
				message[2] = (byte) (1);
			} else {
				flag = false;
				message[2] = (byte) (0);
			}

			if (!flag) {
				System.arraycopy(fileByteArray, i, message, 3, 1021);
			} else {
				System.arraycopy(fileByteArray, i, message, 3, fileByteArray.length - i);
			}

			DatagramPacket sendPacket = new DatagramPacket(message, message.length, address, portaArq);
			socket.send(sendPacket);

			boolean ackRec;
			numReenvs = 0;

			while (true) {
				byte[] ack = new byte[2];
				DatagramPacket ackpack = new DatagramPacket(ack, ack.length);

				try {
					socket.setSoTimeout(50);
					socket.receive(ackpack);
					ackSequence = ((ack[0] & 0xff) << 8) + (ack[1] & 0xff);
					ackRec = true;
				} catch (SocketTimeoutException e) {
					ackRec = false;
				}

				if ((ackSequence == sequenceNumber) && (ackRec)) {
					break;
				} else {
					if (numReenvs < Limites.getErrMax()) {
						socket.send(sendPacket);
					} else {
						numReenvs++;
						break;
					}
					numReenvs++;
				}
				if (numReenvs >= Limites.getErrMax()) {
					break;
				}
			}
			if (numReenvs >= Limites.getErrMax()) {
				visorGrande.append("--> Erro ao enviar arquivo.\n");
				break;
			}
			if (flag) {
				visorGrande.append("--> Arquivo enviado com sucesso.\n");
			}
		}
	}

	private static byte[] readFileToByteArray(File file) {
		FileInputStream fis = null;
		byte[] bArray = new byte[(int) file.length()];
		try {
			fis = new FileInputStream(file);
			fis.read(bArray);
			fis.close();

		} catch (IOException ioExp) {
			ioExp.printStackTrace();
		}
		return bArray;
	}

	@Override
	public void run() {
		ready(visorGrande, nome, cliente);
	}

}
