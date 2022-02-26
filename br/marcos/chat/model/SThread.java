///////////////////
//MARCOS V////////
/////////////////0 0
////////////////  -

package br.marcos.chat.model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import javax.swing.JTextArea;

public class SThread implements Runnable {

	DatagramSocket socket;
	JTextArea visorGrande;
	String nome;

	public SThread(DatagramSocket socket, JTextArea visorGrande, String nome) {
		this.socket = socket;
		this.visorGrande = visorGrande;
		this.nome = nome;
	}

	public static void createFile(DatagramSocket socket, String serverRoute, JTextArea visorGrande) {
		try {
			byte[] receiveFileName = new byte[1024];
			DatagramPacket receiveFileNamePacket = new DatagramPacket(receiveFileName, receiveFileName.length);
			socket.receive(receiveFileNamePacket);
			byte[] data = receiveFileNamePacket.getData();
			String fileName = new String(data, 0, receiveFileNamePacket.getLength());

			System.out.println(fileName);

			File f = new File(serverRoute + fileName);
			FileOutputStream outToFile = new FileOutputStream(f);

			receiveFile(outToFile, socket, visorGrande, fileName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void receiveFile(FileOutputStream outToFile, DatagramSocket socket, JTextArea visorGrande,
			String nomeArqv) throws IOException {
		boolean flag;
		int sequenceNumber = 0;
		int foundLast = 0;
		int contador = 0;

		visorGrande.append("--> Recebendo arquivo.\n--> Aguarde.\n");

		while (true) {
			byte[] message = new byte[1024];
			byte[] fileByteArray = new byte[1021];

			DatagramPacket receivedPacket = new DatagramPacket(message, message.length);
			socket.receive(receivedPacket);
			message = receivedPacket.getData();

			InetAddress address = receivedPacket.getAddress();
			int port = receivedPacket.getPort();

			sequenceNumber = ((message[0] & 0xff) << 8) + (message[1] & 0xff);
			flag = (message[2] & 0xff) == 1;

			if (sequenceNumber == (foundLast + 1)) {

				foundLast = sequenceNumber;

				System.arraycopy(message, 3, fileByteArray, 0, 1021);

				outToFile.write(fileByteArray);

				// ENVIAR ACK
				ClientThread.sendAck(foundLast, socket, address, port);

				contador = 0;
			} else {
				// REENVIAR ACK
				ClientThread.sendAck(foundLast, socket, address, port);
				contador++;
				if (contador > Limites.getErrMax() - 5) {
					visorGrande.append("--> Falha ao receber arquivo.\n");
					break;
				}
			}

			if (flag) {
				outToFile.close();
				visorGrande.append("--> Arquivo recebido: " + nomeArqv + "\n");
				break;
			}
		}
	}

	@Override
	public void run() {
		createFile(socket, "", visorGrande);
		socket.close();
	}

}
