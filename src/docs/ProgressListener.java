package docs;

/**
 * Receives status updates while the master workbook is generated.
 * Wire this to LoadingScreen::setProgress in PokemonXhenos.loadGame.
 * Messages may contain '\n' (LoadingScreen draws each line separately).
 */
@FunctionalInterface
public interface ProgressListener {
	void report(int percent, String message);
}