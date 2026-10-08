package view;

import javax.swing.*;
import java.awt.*;
import util.Colors;
import util.Parser;
import util.Units;

import javax.swing.BoxLayout;

import math.Calculo1D;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class View1D extends JPanel implements ActionListener{
	private Units u = new Units();

	String[] rotulos = {"Deslocamento (Δx)", "Velocidade inicial (v0)", "Velocidade final (v)", "Aceleração (a)", "Tempo (t)"};
	private String[][] nomes = {u.Comprimento, u.Velocidade, u.Velocidade, u.Aceleracao, u.Tempo};
	private double[][] fat = {u.fComprimento, u.fVelocidade, u.fVelocidade, u.fAceleracao, u.fTempo};
	private int[] padrao = {2, 1, 1, 1, 1};
	
	JTextField[] campos = new JTextField[5];
	JComboBox<String>[] combos = new JComboBox[5];
	JComboBox<String> alvo;
	JTextArea resultado = new JTextArea();
	JButton botaoCalc = new JButton("Calcular");
	JButton botaoLimpar = new JButton("Limpar");
		
	public View1D() {
		setBackground(Colors.get4());
		this.setSize(830, 580);
		setLayout(null);
		
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBounds(10, 11, 436, 558);
		panel.setBackground(Colors.get1());
		add(panel);

		JPanel grade = new JPanel(new GridLayout(0, 3, 8, 8));
		grade.setBackground(Colors.get1());
		grade.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		for (int i = 0; i < 5; i++) {
			campos[i] = new JTextField();
			combos[i] = new JComboBox<String>(nomes[i]);
			combos[i].setSelectedIndex(padrao[i]);
			
			grade.add(new JLabel(rotulos[i]));
			grade.add(campos[i]);
			grade.add(combos[i]);

		}
		
		alvo = new JComboBox<String>(rotulos);
		grade.add(new JLabel("Quero calcular:"));
		grade.add(alvo);
		grade.add(new JLabel(""));
		  

		resultado.setEditable(false);
		resultado.setBackground(Colors.get5());
		resultado.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
		resultado.setForeground(Colors.get3());
	

		JPanel panel2 = new JPanel(new BorderLayout());
		panel2.setBackground(Colors.get5());
		panel2.setBounds(456, 172, 359, 241);
		add(panel2);

		JPanel botoes = new JPanel();
		botoes.setBackground(Colors.get1());
		botoes.add(botaoCalc);
		botoes.add(botaoLimpar);

		botaoCalc.addActionListener(this);
		botaoLimpar.addActionListener(this);

		  
		panel.add(grade, BorderLayout.NORTH);
		panel2.add(new JScrollPane(resultado), BorderLayout.CENTER);
		panel.add(botoes, BorderLayout.CENTER);
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object origem = e.getSource();
		if (origem == botaoCalc) {
			try {
				Double[] x = new Double[5];

				for (int i = 0; i < 5; i++) {
					x[i] = Parser.ler(campos[i], combos[i], fat[i], rotulos[i]);
				}

				int alvoidx = alvo.getSelectedIndex();
				String target = rotulos[alvoidx];
				
				resultado.setText(Calculo1D.calcular1D(x, alvoidx, target));
				
			} catch(Exception e1) {
				
			}
				
		} else if (origem == botaoLimpar) {
			for (int i = 0; i < 5; i++) {
				campos[i].setText("");
			}
			resultado.setText("");
		}
	}
	
	
}
