package util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class PokemonUtil {
	public static double round(double value, int decimals) {
		return BigDecimal.valueOf(value).setScale(decimals, RoundingMode.HALF_UP).doubleValue();
	}
}
