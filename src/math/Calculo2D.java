
package math;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Calculo2D {

    // 0=x0 1=xf 2=Δx 3=v0x 4=vfx 5=vmx 6=Δvx 7=ax 8=amx 9=t0 10=tf 11=Δt 12=g 13=y0 14=yf 15=Δy 16=v0y 17=vfy 18=θ0

    static String[] simb = { "x0", "xf", "Δx", "v0x", "vfx", "vmx", "Δvx", "ax", "amx", "t0", "tf", "Δt", "g",
        "y0", "yf", "Δy", "v0y", "vfy", "θ0"};
    static String obs = "";

    // Estrutura para armazenar cada passo do cálculo e suas dependências
    static class PassoCalculo {
        int varCalculada;
        List<Integer> deps;
        String formula;

        public PassoCalculo(
                int varCalculada,
                List<Integer> deps,
                String formula) {

            this.varCalculada = varCalculada;
            this.deps = deps;
            this.formula = formula;
        }
    }

    static List<PassoCalculo> historicoPassos = new ArrayList<>();

    public static String calcular2D(Double[] x, int alvo, String target, double fator, String unidade) {

        if (x[alvo] != null) {
            return "Deixe vazio o campo que deseja calcular!";
        }

        double[] val = new double[19];
        boolean[] sab = new boolean[19];

        for (int i = 0; i < 19; i++) {
            if (x[i] != null) {
                val[i] = x[i];
                sab[i] = true;
            }
        }

        historicoPassos.clear();
        obs = "";

        boolean mudou = true;
        int tentativas = 0;

        while (mudou && !sab[alvo] && tentativas < 50) {
            mudou = false;
            tentativas++;

            mudou |= regrasSimples(val, sab);

            // Equações cinemáticas do eixo X
            if (!sab[alvo]) {
                mudou |= equacaoEixo(val, sab, alvo, 2, 3, 4, 7, 11, false);
            }

            // Equações cinemáticas do eixo Y
            // O eixo Y é positivo para cima: ay = -g
            if (!sab[alvo]) {
                mudou |= equacaoEixo(val, sab, alvo, 15, 16, 17, 12, 11, true);
            }

            // Relação entre as posições inicial e final no eixo Y
            if (!sab[alvo]) {
                mudou |= diferenca(val, sab, 15, 14, 13);
            }

            // Ângulo inicial calculado pelas componentes da velocidade
            if (!sab[alvo] && alvo == 18 && sab[3] && sab[16]) {

                double angulo = Math.toDegrees(Math.atan2(val[16], val[3]));

                mudou |= aprender(val, sab, 18, angulo, "θ0 = atan2(v0y, v0x)", new int[]{16, 3});
            }
        }

        if (!sab[alvo]) {
            return String.format("Não existe fórmula que calcule %s com os dados informados.",target);
        }

        // Reconstrói apenas as fórmulas necessárias para o resultado
        List<String> passosUteis = reconstruirPassosUteis(alvo);

        StringBuilder passosTexto = new StringBuilder();

        for (String p : passosUteis) {
            passosTexto.append("  ").append(p).append("\n");
        }

        String texto = "Fórmulas usadas:\n" + passosTexto + "\n";

        texto += target + " = " + String.format("%.4f", val[alvo] / fator) + " " + unidade + "\n";

        texto += "(em unidades do SI: " + String.format("%.6f", val[alvo]) + ")\n";

        if (!obs.equals("")) {
            texto += "\n" + obs;
        }

        return texto;
    }

    // Mantém somente os passos que contribuíram para o resultado
    static List<String> reconstruirPassosUteis(int alvo) {

        Set<Integer> necessarios = new HashSet<>();
        necessarios.add(alvo);

        List<String> passosFiltrados = new ArrayList<>();

        for (int i = historicoPassos.size() - 1; i >= 0; i--) {
            PassoCalculo p = historicoPassos.get(i);

            if (necessarios.contains(p.varCalculada)) {
                necessarios.remove(p.varCalculada);
                necessarios.addAll(p.deps);
                passosFiltrados.add(0, p.formula);
            }
        }

        return passosFiltrados;
    }

    // Guarda o valor calculado, a fórmula e suas dependências
    static boolean aprender(double[] val, boolean[] sab, int var, double r, String formula, int[] dependencias) {

        if (sab[var] || Double.isNaN(r) || Double.isInfinite(r)) {
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

        historicoPassos.add(
            new PassoCalculo(var, deps, formula)
        );

        return true;
    }

    // Relação d = f - i e inversas
    static boolean diferenca( double[] val, boolean[] sab, int d, int f, int i) {

        boolean m = false;

        if (!sab[d] && sab[f] && sab[i]) {
            m |= aprender(val, sab, d, val[f] - val[i], simb[d] + " = " + simb[f] + " − " + simb[i], new int[]{f, i});
        }

        if (!sab[f] && sab[d] && sab[i]) {
            m |= aprender( val, sab, f, val[i] + val[d], simb[f] + " = " + simb[i] + " + " + simb[d], new int[]{i, d});
        }

        if (!sab[i] && sab[d] && sab[f]) {
            m |= aprender( val, sab, i, val[f] - val[d], simb[i] + " = " + simb[f] + " − " + simb[d], new int[]{f, d});
        }

        return m;
    }

    // Relação q = n / d e inversas
    static boolean razao( double[] val, boolean[] sab, int q, int n, int d) {

        boolean m = false;

        if (!sab[q] && sab[n] && sab[d] && val[d] != 0) {
            m |= aprender(val, sab, q, val[n] / val[d], simb[q] + " = " + simb[n] + " / " + simb[d], new int[]{n, d});
        }

        if (!sab[n] && sab[q] && sab[d]) {
            m |= aprender( val, sab, n, val[q] * val[d], simb[n] + " = " + simb[q] + "·" + simb[d], new int[]{q, d});
        }

        if (!sab[d] && sab[q] && sab[n] && val[q] != 0) {
            m |= aprender( val, sab, d, val[n] / val[q], simb[d] + " = " + simb[n] + " / " + simb[q], new int[]{n, q});
        }

        return m;
    }

    // Relações diretas e médias do eixo X
    static boolean regrasSimples(double[] val, boolean[] sab) {

        boolean m = false;

        m |= diferenca(val, sab, 2, 1, 0);
        m |= diferenca(val, sab, 6, 4, 3);
        m |= diferenca(val, sab, 11, 10, 9);

        // vmx = Δx / Δt
        m |= razao(val, sab, 5, 2, 11);

        // amx = Δvx / Δt
        m |= razao(val, sab, 8, 6, 11);

        // vmx = (v0x + vfx) / 2
        if (!sab[5] && sab[3] && sab[4]) {
            m |= aprender( val, sab, 5, (val[3] + val[4]) / 2, "vmx = (v0x + vfx) / 2", new int[]{3, 4});
        }

        if (!sab[3] && sab[5] && sab[4]) {
            m |= aprender( val, sab, 3, 2 * val[5] - val[4], "v0x = 2·vmx − vfx", new int[]{5, 4});
        }

        if (!sab[4] && sab[5] && sab[3]) {
            m |= aprender( val, sab, 4, 2 * val[5] - val[3], "vfx = 2·vmx − v0x", new int[]{5, 3});
        }

        // Aceleração constante: ax = amx
        if (!sab[7] && sab[8]) {
            m |= aprender( val, sab, 7, val[8], "ax = amx (aceleração constante)", new int[]{8} );
        }

        if (!sab[8] && sab[7]) {
            m |= aprender( val, sab, 8, val[7], "amx = ax (aceleração constante)", new int[]{7} );
        }

        return m;
    }

    // Menor raiz não negativa de A·t² + B·t + C = 0
    static double raizTempo(double A, double B, double C) {

        if (A == 0) {
            if (B == 0) {
                return Double.NaN;
            }

            double t = -C / B;

            return t >= 0 ? t : Double.NaN;
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

    // Equações cinemáticas para os eixos X e Y
    static boolean equacaoEixo(double[] val, boolean[] sab, int alvo, int desloc, int v0Indice, int vfIndice, int acelIndice, int tempoIndice, boolean vertical) {

        int[][] formulas = {{vfIndice, v0Indice, acelIndice, tempoIndice}, {desloc, v0Indice, vfIndice, tempoIndice},
            {desloc, v0Indice, tempoIndice, acelIndice}, {desloc, vfIndice, tempoIndice, acelIndice}, {vfIndice, v0Indice, acelIndice, desloc} };

        String eixo = vertical ? "Y" : "X";
        String[] nomes = {"vf = v0 + a·Δt", "Δr = (v0 + vf)·Δt / 2", "Δr = v0·Δt + a·Δt² / 2", "Δr = vf·Δt − a·Δt² / 2", "vf² = v0² + 2·a·Δr" };

        double dx = val[desloc];
        double v0 = val[v0Indice];
        double vf = val[vfIndice];
        double t = val[tempoIndice];

        // Na vertical, a aceleração física é -g
        double a = vertical ? -val[acelIndice] : val[acelIndice];

        for (int k = 0; k < formulas.length; k++) {

            boolean contemAlvo = false;
            boolean tudoConhecido = true;

            List<Integer> deps = new ArrayList<>();

            for (int j = 0; j < formulas[k].length; j++) {
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

            // Atualiza os valores após verificar os dados conhecidos
            dx = val[desloc];
            v0 = val[v0Indice];
            vf = val[vfIndice];
            t = val[tempoIndice];
            a = vertical ? -val[acelIndice] : val[acelIndice];

            double r = Double.NaN;

            if (k == 0) {
                if (alvo == vfIndice) {
                    r = v0 + a * t;
                } else if (alvo == v0Indice) {
                    r = vf - a * t;
                } else if (alvo == acelIndice) {
                    r = (vf - v0) / t;
                } else if (alvo == tempoIndice) {
                    r = (vf - v0) / a;
                }

            } else if (k == 1) {
                if (alvo == desloc) {
                    r = (v0 + vf) * t / 2;
                } else if (alvo == v0Indice) {
                    r = 2 * dx / t - vf;
                } else if (alvo == vfIndice) {
                    r = 2 * dx / t - v0;
                } else if (alvo == tempoIndice) {
                    if (v0 + vf != 0) {
                        r = 2 * dx / (v0 + vf);
                    }
                }

            } else if (k == 2) {
                if (alvo == desloc) {
                    r = v0 * t + a * t * t / 2;
                } else if (alvo == v0Indice) {
                    r = (dx - a * t * t / 2) / t;
                } else if (alvo == acelIndice) {
                    r = 2 * (dx - v0 * t) / (t * t);
                } else if (alvo == tempoIndice) {
                    r = raizTempo(a / 2, v0, -dx);
                }

            } else if (k == 3) {
                if (alvo == desloc) {
                    r = vf * t - a * t * t / 2;
                } else if (alvo == vfIndice) {
                    r = (dx + a * t * t / 2) / t;
                } else if (alvo == acelIndice) {
                    r = 2 * (vf * t - dx) / (t * t);
                } else if (alvo == tempoIndice) {
                    r = raizTempo(a / 2, -vf, dx);
                }

            } else {
                if (alvo == vfIndice) {
                    double raiz = v0 * v0 + 2 * a * dx;

                    if (raiz >= 0) {
                        r = Math.sqrt(raiz);

                        if (v0 < 0) {
                            r = -r;
                        }
                    }

                    avisar();

                } else if (alvo == v0Indice) {
                    double raiz = vf * vf - 2 * a * dx;

                    if (raiz >= 0) {
                        r = Math.sqrt(raiz);

                        if (vf < 0) {
                            r = -r;
                        }
                    }

                    avisar();

                } else if (alvo == acelIndice) {
                    if (dx != 0) {
                        r = (vf * vf - v0 * v0) / (2 * dx);
                    }

                } else if (alvo == desloc) {
                    if (a != 0) {
                        r = (vf * vf - v0 * v0) / (2 * a);
                    }
                }
            }

            // Converte a aceleração física para g, se for o eixo Y
            if (vertical && alvo == acelIndice) {
                r = -r;
            }

            // Registra a fórmula com os índices realmente utilizados
            String formula = nomes[k];

            if (vertical) {
                formula = formula.replace("vf", "vfy").replace("v0", "v0y").replace("Δr", "Δy").replace("a·", "(-g)·").replace("a =", "(-g) =");
            } else {
                formula = formula.replace("vf", "vfx").replace("v0", "v0x").replace("Δr", "Δx").replace("a", "ax");
            }

            int[] depsArr = deps.stream().mapToInt(i -> i).toArray();

            if (aprender(
                    val, sab, alvo, r, formula, depsArr)) {
                return true;
            }
        }

        return false;
    }

    static void avisar() {
        String msg = "Obs.: na equação de Torricelli existem duas soluções " + "possíveis para a velocidade; foi escolhido um sinal com base " + "na velocidade conhecida.\n";

        if (!obs.contains(msg)) {
            obs += msg;
        }
    }
}
