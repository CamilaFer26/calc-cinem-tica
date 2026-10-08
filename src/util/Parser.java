package util;

import javax.swing.JComboBox;
import javax.swing.JTextField;

public class Parser {
	// tenta fazer a leitura de um valor e converter para SI
	public static Double ler(JTextField campo, JComboBox<String> combo, double[] fatores, String rotulo) throws Exception {
		String texto = campo.getText().trim().replace(',', '.');
		
		if (texto.equals("")) {
			return null;
		}
		
		try {
			return Double.parseDouble(texto) * fatores[combo.getSelectedIndex()];
		} catch (NumberFormatException ex) {
			throw new Exception("Valor inválido no campo: " + rotulo);
		}
	}
}
