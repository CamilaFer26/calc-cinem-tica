
package view;

import javax.swing.*;
import java.awt.*;
import util.Colors;
import util.Parser;
import util.Units;

import math.Calculo2D;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class View2D extends JPanel implements ActionListener {

    private Units u = new Units();

    String[] rotulos = {"Posição inicial X (x0)", "Posição final X (xf)", "Deslocamento X (Δx)", "Velocidade inicial X (v0x)", "Velocidade final X (vfx)", "Velocidade média X (vmx)",
        "Variação velocidade X (Δvx)", "Aceleração X (ax)", "Aceleração média X (amx)", "Tempo inicial (t0)", "Tempo final (tf)", "Tempo (Δt)",
        "Gravidade (g)", "Posição inicial Y (y0)", "Posição final Y (yf)", "Deslocamento Y (Δy)", "Velocidade inicial Y (v0y)", "Velocidade final Y (vfy)", "Ângulo inicial (θ0)" };

    private String[][] nomes = {u.Comprimento, u.Comprimento, u.Comprimento,u.Velocidade, u.Velocidade, u.Velocidade, u.Velocidade,
        u.Aceleracao, u.Aceleracao, u.Tempo, u.Tempo, u.Tempo, u.Aceleracao, u.Comprimento, u.Comprimento, u.Comprimento, u.Velocidade, u.Velocidade, {"°"} };

    private double[][] fat = {u.fComprimento, u.fComprimento, u.fComprimento, u.fVelocidade, u.fVelocidade, u.fVelocidade, u.fVelocidade,
        u.fAceleracao, u.fAceleracao, u.fTempo, u.fTempo, u.fTempo, u.fAceleracao, u.fComprimento, u.fComprimento, u.fComprimento, u.fVelocidade, u.fVelocidade,{1.0}};

    private int[] padrao = {2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 1, 1, 0};

    JTextField[] campos = new JTextField[19];
    JComboBox<String>[] combos = new JComboBox[19];
    JComboBox<String> alvo;
    JTextArea resultado = new JTextArea();
    JButton botaoCalc = new JButton("Calcular");
    JButton botaoLimpar = new JButton("Limpar");

    public View2D() {

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

        for (int i = 0; i < 19; i++) {
            campos[i] = new JTextField();
            campos[i].setFont(new Font("Monospaced", Font.PLAIN, 11));

            combos[i] = new JComboBox<String>(nomes[i]);
            combos[i].setSelectedIndex(padrao[i]);
            campos[i].setFont(new Font("Monospaced", Font.PLAIN, 11));

            JLabel rotulo = new JLabel(rotulos[i]);
			rotulo.setFont(new Font("Monospaced", Font.PLAIN, 11));
			
			grade.add(rotulo);
			grade.add(campos[i]);
			grade.add(combos[i]);
        }

        alvo = new JComboBox<String>(rotulos);
		alvo.setFont(new Font("Monospaced", Font.PLAIN, 11));
		JLabel label = new JLabel("Quero calcular:");
		label.setFont(new Font("Monospaced", Font.PLAIN, 11));
		grade.add(label);
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
		botaoCalc.setFont(new Font("Monospaced", Font.PLAIN, 11));
		botoes.add(botaoCalc);
		botaoLimpar.setFont(new Font("Monospaced", Font.PLAIN, 11));
		botoes.add(botaoLimpar);

		botaoCalc.addActionListener(this);
		botaoLimpar.addActionListener(this);

		panel.add(new JScrollPane(grade),BorderLayout.CENTER);
        panel.add(botoes, BorderLayout.CENTER);        


        JScrollPane scrollGrade = new JScrollPane(grade);
        scrollGrade.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollGrade.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        panel.add(scrollGrade, BorderLayout.CENTER);
        panel.add(botoes, BorderLayout.SOUTH);
        grade.setPreferredSize(new Dimension(400, 700));  
  
        
        JLabel lblNewLabel = new JLabel("Resultado:");
		lblNewLabel.setFont(new Font("Monospaced", Font.BOLD, 20));
		lblNewLabel.setBounds(506, 145, 166, 25);
		lblNewLabel.setForeground(Colors.get2());
		add(lblNewLabel);
        
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        Object origem = e.getSource();

        if (origem == botaoCalc) {
            try {
                // Lê as 19 variáveis
                Double[] x = new Double[19];

                for (int i = 0; i < 19; i++) {
                    x[i] = Parser.ler(campos[i], combos[i], fat[i], rotulos[i]);
                }

                int alvoidx = alvo.getSelectedIndex();

                String target = rotulos[alvoidx];

                double fator =
                    fat[alvoidx][combos[alvoidx].getSelectedIndex()];

                String unidade =
                    (String) combos[alvoidx].getSelectedItem();

                resultado.setText(
                    Calculo2D.calcular2D(x, alvoidx, target, fator, unidade));

            } catch (Exception e1) {
                resultado.setText("");

                JOptionPane.showMessageDialog(this, e1.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }

        } else if (origem == botaoLimpar) {

            for (int i = 0; i < 19; i++) {
                campos[i].setText("");
            }

            alvo.setSelectedIndex(0);
            resultado.setText("");
        }
    }
}
