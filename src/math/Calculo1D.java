package math;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Calculo1D {
	// 0=x0 1=xf 2=Δx 3=v0 4=vf 5=vm 6=Δv 7=a 8=am 9=t0 10=tf 11=Δt 12=g
	static String[] simb = {"x0", "xf", "Δx", "v0", "vf", "vm", "Δv", "a", "am", "t0", "tf", "Δt", "g"};
	static String obs = "";
	
	// Estrutura para armazenar cada passo do cálculo e suas dependências
	static class PassoCalculo {
		int varCalculada;
		List<Integer> deps;
		String formula;

		public PassoCalculo(int varCalculada, List<Integer> deps, String formula) {
			this.varCalculada = varCalculada;
			this.deps = deps;
			this.formula = formula;
		}
	}

	static List<PassoCalculo> historicoPassos = new ArrayList<>();

	public static String calcular1D(Double[] x, int alvo, String target, double fator, String unidade, String si) {
		if (x[alvo] != null) {
			return "Deixe vazio o campo que deseja calcular!";
		}
	 
		double[] val = new double[13];
		boolean[] sab = new boolean[13];
		for (int i = 0; i < 13; i++) {
			if (x[i] != null) {
				val[i] = x[i];
				sab[i] = true;
			}
		}
	 
		historicoPassos.clear();
		obs = "";
	 
		// Δx, v0, vf, a, Δt
		int[] nucleo = {2, 3, 4, 7, 11};
	 
		// Repete até descobrir o objetivo
		boolean mudou = true;
		while (mudou && !sab[alvo]) {
			mudou = regrasSimples(val, sab);
			for (int k = 0; k < 5 && !sab[alvo]; k++) {
				if (!sab[nucleo[k]]) {
					if (equacaoCinematica(val, sab, nucleo[k])) {
						mudou = true;
					}
				}
			}
		}
	 
		if (!sab[alvo]) {
			return String.format("Não existe fórmula que calcule %s\ncom os dados informados", target);
		}
	 
		// Obtém apenas os passos essenciais para o alvo
		List<String> passosUteis = reconstruirPassosUteis(alvo);
		StringBuilder passosTexto = new StringBuilder();
		for (String p : passosUteis) {
			passosTexto.append("  ").append(p).append("\n");
		}

		String texto = "Fórmulas usadas:\n" + passosTexto.toString() + "\n";
		texto += target + " = " + String.format("%.4f", val[alvo] / fator) + " " + unidade + "\n";
		texto += "(em unidades do SI: " + String.format("%.6f %s", val[alvo], si) + ")\n";
		if (!obs.equals("")) {
			texto += "\n" + obs;
		}
		return texto;
	}

	// Filtra o histórico do fim para o início mantendo apenas o caminho do alvo
	static List<String> reconstruirPassosUteis(int alvo) {
		Set<Integer> necessarios = new HashSet<>();
		necessarios.add(alvo);

		List<String> passosFiltrados = new ArrayList<>();

		// Percorre os passos gravados de trás para frente
		for (int i = historicoPassos.size() - 1; i >= 0; i--) {
			PassoCalculo p = historicoPassos.get(i);
			if (necessarios.contains(p.varCalculada)) {
				necessarios.remove(p.varCalculada);
				necessarios.addAll(p.deps);
				passosFiltrados.add(0, p.formula); // Adiciona no início da lista final
			}
		}
		return passosFiltrados;
	}

	// Guarda o valor e as dependências da fórmula
	static boolean aprender(double[] val, boolean[] sab, int var, double r, String formula, int[] dependencias) {
		if (Double.isNaN(r) || Double.isInfinite(r)) {
			return false;
		}
		val[var] = r;
		sab[var] = true;

		List<Integer> deps = new ArrayList<>();
		if (dependencias != null) {
			for (int d : dependencias) {
				deps.add(d);
			}
		}
		historicoPassos.add(new PassoCalculo(var, deps, formula));
		return true;
	}

	// Relação d = f − i e inversas
	static boolean diferenca(double[] val, boolean[] sab, int d, int f, int i) {
		boolean m = false;
		if (!sab[d] && sab[f] && sab[i]) {
			m |= aprender(val, sab, d, val[f] - val[i], simb[d] + " = " + simb[f] + " − " + simb[i], new int[]{f, i});
		}
		if (!sab[f] && sab[d] && sab[i]) {
			m |= aprender(val, sab, f, val[i] + val[d], simb[f] + " = " + simb[i] + " + " + simb[d], new int[]{i, d});
		}
		if (!sab[i] && sab[d] && sab[f]) {
			m |= aprender(val, sab, i, val[f] - val[d], simb[i] + " = " + simb[f] + " − " + simb[d], new int[]{f, d});
		}
		return m;
	}

	// Relação q = n / d e inversas
	static boolean razao(double[] val, boolean[] sab, int q, int n, int d) {
		boolean m = false;
		if (!sab[q] && sab[n] && sab[d]) {
			m |= aprender(val, sab, q, val[n] / val[d], simb[q] + " = " + simb[n] + " / " + simb[d], new int[]{n, d});
		}
		if (!sab[n] && sab[q] && sab[d]) {
			m |= aprender(val, sab, n, val[q] * val[d], simb[n] + " = " + simb[q] + "·" + simb[d], new int[]{q, d});
		}
		if (!sab[d] && sab[q] && sab[n]) {
			m |= aprender(val, sab, d, val[n] / val[q], simb[d] + " = " + simb[n] + " / " + simb[q], new int[]{n, q});
		}
		return m;
	}

	// Relações diretas
	static boolean regrasSimples(double[] val, boolean[] sab) {
		boolean m = false;
		m |= diferenca(val, sab, 2, 1, 0);    // Δx = xf − x0
		m |= diferenca(val, sab, 6, 4, 3);    // Δv = vf − v0
		m |= diferenca(val, sab, 11, 10, 9);  // Δt = tf − t0
		m |= razao(val, sab, 5, 2, 11);       // vm = Δx / Δt
		m |= razao(val, sab, 8, 6, 11);       // am = Δv / Δt

		// vm = (v0 + vf) / 2 (aceleração constante)
		if (!sab[5] && sab[3] && sab[4]) {
			m |= aprender(val, sab, 5, (val[3] + val[4]) / 2, "vm = (v0 + vf) / 2", new int[]{3, 4});
		}
		if (!sab[3] && sab[5] && sab[4]) {
			m |= aprender(val, sab, 3, 2 * val[5] - val[4], "v0 = 2·vm − vf", new int[]{5, 4});
		}
		if (!sab[4] && sab[5] && sab[3]) {
			m |= aprender(val, sab, 4, 2 * val[5] - val[3], "vf = 2·vm − v0", new int[]{5, 3});
		}

		// aceleração constante: a = am
		if (!sab[7] && sab[8]) {
			m |= aprender(val, sab, 7, val[8], "a = am (aceleração constante)", new int[]{8});
		}
		if (!sab[8] && sab[7]) {
			m |= aprender(val, sab, 8, val[7], "am = a (aceleração constante)", new int[]{7});
		}

		// queda livre: a = g (eixo positivo para baixo)
		if (!sab[7] && sab[12]) {
			m |= aprender(val, sab, 7, val[12], "a = g (queda livre)", new int[]{12});
		}
		if (!sab[12] && sab[7]) {
			m |= aprender(val, sab, 12, val[7], "g = a (queda livre)", new int[]{7});
		}
		return m;
	}

	// Menor raiz positiva de A·t² + B·t + C = 0
	static double raizTempo(double A, double B, double C) {
		if (A == 0) {
			if (B == 0) {
				return Double.NaN;
			}
			double t = -C / B;
			if (t >= 0) {
				return t;
			}
			return Double.NaN;
		}
		double d = B * B - 4 * A * C;
		if (d < 0) {
			return Double.NaN;
		}
		double t1 = (-B - Math.sqrt(d)) / (2 * A);
		double t2 = (-B + Math.sqrt(d)) / (2 * A);
		double menor = Math.min(t1, t2);
		double maior = Math.max(t1, t2);
		if (menor >= 0) {
			return menor;
		}
		if (maior >= 0) {
			return maior;
		}
		return Double.NaN;
	}

	// Sinal da velocidade final ao usar v² = v0² + 2·a·Δx
	static double sinalFinal(double v0, double dx) {
		if (v0 == 0) {
			if (dx < 0) return -1;
			return 1;
		}
		if (dx == 0 || v0 * dx < 0) {
			if (v0 > 0) return -1;
			return 1;
		}
		if (v0 > 0) return 1;
		return -1;
	}

	// Tenta calcular a variável "alvo" com uma das 5 equações cinemáticas
	static boolean equacaoCinematica(double[] val, boolean[] sab, int alvo) {
		int[][] formulas = {
				{4, 3, 7, 11}, // v = v0 + a·t
				{2, 3, 4, 11}, // Δx = (v0 + v)·t / 2
				{2, 3, 11, 7}, // Δx = v0·t + a·t²/2
				{2, 4, 11, 7}, // Δx = v·t − a·t²/2
				{4, 3, 7, 2}   // v² = v0² + 2·a·Δx
		};
		String[] nomes = {"vf = v0 + a·Δt", "Δx = (v0 + vf)·Δt / 2", "Δx = v0·Δt + a·Δt²/2",
				"Δx = vf·Δt − a·Δt²/2", "vf² = v0² + 2·a·Δx"};

		for (int k = 0; k < 5; k++) {
			boolean contemAlvo = false;
			boolean tudoConhecido = true;
			List<Integer> deps = new ArrayList<>();

			for (int j = 0; j < 4; j++) {
				int var = formulas[k][j];
				if (var == alvo) {
					contemAlvo = true;
				} else if (!sab[var]) {
					tudoConhecido = false;
				} else {
					deps.add(var);
				}
			}
			if (!contemAlvo || !tudoConhecido) {
				continue;
			}

			double dx = val[2];
			double v0 = val[3];
			double v = val[4];
			double a = val[7];
			double t = val[11];
			double r = Double.NaN;

			if (k == 0) {
				if (alvo == 4) r = v0 + a * t;
				else if (alvo == 3) r = v - a * t;
				else if (alvo == 7) r = (v - v0) / t;
				else r = (v - v0) / a;
			} else if (k == 1) {
				if (alvo == 2) r = (v0 + v) * t / 2;
				else if (alvo == 3) r = 2 * dx / t - v;
				else if (alvo == 4) r = 2 * dx / t - v0;
				else r = 2 * dx / (v0 + v);
			} else if (k == 2) {
				if (alvo == 2) r = v0 * t + a * t * t / 2;
				else if (alvo == 3) r = (dx - a * t * t / 2) / t;
				else if (alvo == 7) r = 2 * (dx - v0 * t) / (t * t);
				else r = raizTempo(a / 2, v0, -dx);
			} else if (k == 3) {
				if (alvo == 2) r = v * t - a * t * t / 2;
				else if (alvo == 4) r = (dx + a * t * t / 2) / t;
				else if (alvo == 7) r = 2 * (v * t - dx) / (t * t);
				else r = raizTempo(a / 2, -v, dx);
			} else {
				if (alvo == 4) {
					r = sinalFinal(v0, dx) * Math.sqrt(v0 * v0 + 2 * a * dx);
					avisar();
				} else if (alvo == 3) {
					r = Math.sqrt(v * v - 2 * a * dx);
					if (v < 0) r = -r;
					avisar();
				} else if (alvo == 7) {
					r = (v * v - v0 * v0) / (2 * dx);
				} else {
					r = (v * v - v0 * v0) / (2 * a);
				}
			}

			if (alvo == 11 && r < 0) {
				r = Double.NaN;
			}

			int[] depsArr = deps.stream().mapToInt(i -> i).toArray();
			if (aprender(val, sab, alvo, r, nomes[k], depsArr)) {
				return true;
			}
		}
		return false;
	}

	static void avisar() {
		String msg = "Obs.: onde há raiz quadrada existem duas soluções (±);\nfoi adotado o sinal "
				+ "coerente com a direção do movimento.\n";
		if (!obs.contains(msg)) {
			obs += msg;
		}
	}
}