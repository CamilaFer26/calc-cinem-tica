package util;

public class Units {
	public static String[] Comprimento = {"mm", "cm", "m", "km", "in", "ft", "mi"};
	public static double[] fComprimento = {0.001, 0.01, 1, 1000, 0.0254, 0.3048, 1609.344};

	public static String[] Tempo = {"ms", "s", "min", "h"};
	public static double[] fTempo = {0.001, 1, 60, 3600};

	public static String[] Velocidade = {"cm/s", "m/s", "km/h", "mph", "ft/s"};
	public static double[] fVelocidade = {0.01, 1, 1 / 3.6, 0.44704, 0.3048};

	public static String[] Aceleracao = {"cm/s²", "m/s²", "km/h²", "ft/s²", "g"};
	public static double[] fAceleracao = {0.01, 1, 1000.0 / (3600.0 * 3600.0), 0.3048, 9.80665};

	public static String[] Angulo = {"graus", "rad"};
	public static double[] fAngulo = {Math.PI / 180, 1};
}
