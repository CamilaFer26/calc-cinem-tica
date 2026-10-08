package math;

public class Calculo1D {
	public static String calcular1D(Double[] x, int alvo, String target) {
		if (x[alvo] != null) {
			return "Deixe vazio o campo que deseja calcular!";
		}

		// Fórmulas e as variáveis que contém (0=Δx, 1=v0, 2=v, 3=a, 4=t)
		int[][] formulas = {
				{2, 1, 3, 4}, // v = v0 + a·t
				{0, 1, 2, 4}, // Δx = (v0 + v)·t / 2
				{0, 1, 3, 4}, // Δx = v0·t + a·t²/2
				{0, 2, 3, 4}, // Δx = v·t − a·t²/2
				{0, 1, 2, 3} // v² = v0² + 2·a·Δx
				};

		String[] nomesFormulas = {"v = v0 + a·t", "Δx = (v0 + v)·t / 2", "Δx = v0·t + a·t²/2", "Δx = v·t − a·t²/2", "v² = v0² + 2·a·Δx"};

		// Escolhe a primeira fórmula que contém o objetivo e cujas outras variáveis são conhecidas
		int usada = -1;
		for (int i = 0; i < 5 && usada == -1; i++) {
			boolean contemAlvo = false;
			boolean tudoConhecido = true;
			
			for (int j = 0; j < 4; j++) {
				int var = formulas[i][j];
				if (var == alvo) {
					contemAlvo = true;
				} else if (x[var] == null) {
					tudoConhecido = false;
				}
			}
			if (contemAlvo && tudoConhecido) {
				usada = i;
				break;
			}
		}

		if (usada == -1) {
			return String.format("Não existe fórmula que calcule %s com os dados informados.", target);
		}

		double dx = x[0] != null ? x[0] : 0;
		double v0 = x[1] != null ? x[1] : 0;
		double v = x[2] != null ? x[2] : 0;
		double a = x[3] != null ? x[3] : 0;
		double t = x[4] != null ? x[4] : 0;
		double r = Double.NaN;
		
		String obs = "";

		switch(usada){
		case 0:
			if (alvo == 2) r = v0 + a * t;
			else if (alvo == 1) r = v - a * t;
			else if (alvo == 3) r = (v - v0) / t;
			else r = (v - v0) / a;
			break;
		case 1:
			if (alvo == 0) r = (v0 + v) * t / 2;
			else if (alvo == 1) r = 2 * dx / t - v;
			else if (alvo == 2) r = 2 * dx / t - v0;
			else r = 2 * dx / (v0 + v);
			break;
		}
		
		if (Double.isNaN(r) || Double.isInfinite(r)) {
			return "Não foi possível calcular: os dados são inconsistentes ou levam a divisão por zero.";
		}

		  
		String texto = "Fórmula usada: " + nomesFormulas[usada] + "\n\n";
		texto += target + " = " + r + "\n";
		texto += "(em unidades do SI: " + String.format("%.6f", r) + ")\n";

		if (!obs.equals("")) {
			texto += "\n" + obs + "\n";
		}
		return texto;
	}
}
