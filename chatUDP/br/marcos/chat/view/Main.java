///////////////////
//MARCOS V////////
/////////////////0 0
////////////////  -

package br.marcos.chat.view;

import br.marcos.chat.model.*;

import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.net.DatagramSocket;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.util.ArrayList;

public class Main extends JFrame implements KeyListener {
	private static final long serialVersionUID = 1L;

	private static final JTextArea visorGrande = new JTextArea();
	private final JTextField visorPequeno = new JTextField();

	private JTextField txtPorta = new JTextField();
	private JTextField txtPorta2 = new JTextField();
	private JTextField nomeUser = new JTextField();

	private static String nome;
	private static int porta;
	private static DatagramSocket socket;
	private static ArrayList<Usuario> outroUsuario = new ArrayList<Usuario>();

	public Main() {
		super("MARCOS - MSG2");
		montaEntrada();
		montaJanela();
	}

	private void montaEntrada() {
		JPanel painelPort = new JPanel(new BorderLayout());
		painelPort.add(new JLabel("Sua porta:"), BorderLayout.WEST);
		txtPorta.setEnabled(true);
		painelPort.add(txtPorta, BorderLayout.CENTER);
		txtPorta.setDisabledTextColor(Color.black);
		txtPorta.setText("12345");

		JPanel painelPort2 = new JPanel(new BorderLayout());
		painelPort2.add(new JLabel(""), BorderLayout.WEST);
		txtPorta2.setEnabled(true);
		painelPort2.add(txtPorta2, BorderLayout.CENTER);
		txtPorta2.setDisabledTextColor(Color.black);
		txtPorta2.setText("localhost:12346; localhost:12398; localhost:12387");

		JPanel painelNome = new JPanel(new BorderLayout());
		painelNome.add(new JLabel("Nome:"), BorderLayout.WEST);
		nomeUser.setEnabled(true);
		painelNome.add(nomeUser, BorderLayout.CENTER);
		nomeUser.setDisabledTextColor(Color.black);
		nomeUser.setText("Jason Borne");

		JLabel entradaMsg1 = new JLabel("Configuração");
		JLabel entradaMsg2 = new JLabel("Onde conectar:");
		JLabel entradaMsg3 = new JLabel("digite: IP:porta; IP:porta; ...");

		Object[] texts = { entradaMsg1, painelNome, painelPort, entradaMsg2, painelPort2, entradaMsg3 };
		JOptionPane.showMessageDialog(null, texts);
	}

	private void montaJanela() {

		// Nome Calculadora
		JLabel painelNomeC = new JLabel("MARCOS - MSG2");

		// Criando um painel com o visorGrande
		JPanel painelVisorGrande = new JPanel(new BorderLayout());
		painelVisorGrande.add(new JLabel("Menssagens: "), BorderLayout.WEST);
		visorGrande.setEnabled(false);
		visorGrande.setDisabledTextColor(Color.black);
		painelVisorGrande.add(new JScrollPane(visorGrande), BorderLayout.CENTER);
		visorGrande.setText("Olá.\n");
		painelVisorGrande.setBackground(Color.cyan);

		// Criando um painel com o visorPequeno
		JPanel painelVPequeno = new JPanel(new BorderLayout());
		painelVPequeno.add(new JLabel("Entrada: "), BorderLayout.WEST);
		visorPequeno.setEnabled(true);
		painelVPequeno.add(visorPequeno, BorderLayout.CENTER);
		visorPequeno.setDisabledTextColor(Color.black);
		visorPequeno.addKeyListener(this);
		painelVPequeno.setBackground(Color.red);

		// Criando um painel que contem o nome e os visores
		JPanel painelVisores = new JPanel(new BorderLayout());
		painelVisores.add(painelNomeC, BorderLayout.NORTH);
		painelVisores.add(painelVisorGrande, BorderLayout.CENTER);
		painelVisores.add(painelVPequeno, BorderLayout.SOUTH);

		// Criando um painel com os botões sob a lista
		JPanel painelBotoes = new JPanel(new GridLayout(1, 4));
		JButton botaoEnter = new JButton("enviar");
		JButton botaoSair = new JButton("sair");
		JButton botaoArqv = new JButton("arquivo");
		JButton botaoTroca = new JButton("trocar");
		painelBotoes.add(botaoTroca);
		painelBotoes.add(botaoSair);
		painelBotoes.add(botaoArqv);
		painelBotoes.add(botaoEnter);
		botaoEnter.setToolTipText("enviar mensagem");
		botaoArqv.setToolTipText("enviar arquivo");
		botaoTroca.setToolTipText("trocar os contatos");
		botaoSair.setToolTipText("sair");

		// Explicacao de coisas
		String textoExp = "para enviar mensagens aperte enter.\n"
				+ "enviar: envia mensagem.\ntrocar: troca os contatos.\n"
				+ "arquivo: envia arquivo para uma pessoa.\nsair: desconecta dos contatos.";
		JTextPane painelExplica = new JTextPane();
		painelExplica.setText(textoExp);
		painelExplica.setEnabled(false);
		painelExplica.setDisabledTextColor(Color.BLACK);
		painelExplica.setBackground(Color.YELLOW);

		// Criando painel com Botoes e expicações
		JPanel painelBE = new JPanel(new BorderLayout());
		painelBE.add(painelBotoes, BorderLayout.CENTER);
		painelBE.add(painelExplica, BorderLayout.SOUTH);

		JPanel painelTudo = new JPanel(new BorderLayout());
		painelTudo.add(painelVisores, BorderLayout.CENTER);
		painelTudo.add(painelBE, BorderLayout.SOUTH);

		// Configurando os listeners/ OQ ACONTECE QND APERTA OS BOTÕES
		botaoEnter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ClientThread.enviarMensagem(visorPequeno, visorGrande, outroUsuario, nome, socket);
			}
		});
		botaoArqv.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ClientThread.enviarMensagem("arqvs()", visorGrande, outroUsuario, nome, socket);
			}
		});
		botaoSair.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ClientThread.enviarMensagem("sair(6283185307)", visorGrande, outroUsuario, nome, socket);
			}
		});
		botaoTroca.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ClientThread.enviarMensagem("trocar()", visorGrande, outroUsuario, nome, socket);
			}
		});

		this.setContentPane(painelTudo);

		// Configurando a janela
		this.pack();
		this.setSize(500, 600);
		this.setLocationRelativeTo(null);
		setTitle(nomeUser.getText());
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	// Para enviar msg qnd apertar enter
	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			try {
				ClientThread.enviarMensagem(visorPequeno, visorGrande, outroUsuario, nome, socket);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	public static void main(String[] args) {
		Main peer = new Main();
		peer.setVisible(true);

		while (true) {
			nome = peer.nomeUser.getText();
			porta = Integer.parseInt(peer.txtPorta.getText());
			try {
				socket = new DatagramSocket(porta);
				break;
			} catch (Exception e) {
				e.printStackTrace();
				peer.montaEntrada();
			}
		}

		ServerThread st = new ServerThread(socket, visorGrande, outroUsuario, nome);
		Thread tarefa = new Thread(st);
		tarefa.start();
		Thread tarefa2 = new Thread(st);
		tarefa2.start();
		ClientThread.enviarMensagem("trocar(" + peer.txtPorta2.getText() + ")", visorGrande, outroUsuario, nome,
				socket);
	}

}
