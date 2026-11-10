package docs;

/**
 * Maps per-section progress onto one global percent range (e.g. 65-98) and
 * forwards it to a ProgressListener.
 *
 * Usage (from MasterDoc):
 *   Progress p = new Progress(listener, 65, 98, totalWeight);
 *   p.begin("Writing Pokemon...", 40);        // section weight
 *   p.update(i, total, "Pokemon " + i + "/" + total + "\n" + name);
 *
 * Percent is monotonic (never goes backwards) and updates are throttled so
 * a tight loop over thousands of rows doesn't flood the loading screen.
 */
public class Progress {
	private static final long MIN_REPORT_INTERVAL_MS = 100;

	private final ProgressListener listener;
	private final int startPercent;
	private final int endPercent;
	private final int totalWeight;

	private int completedWeight = 0;   // weight of sections already finished
	private int currentWeight = 0;     // weight of the section in progress
	private String currentLabel = "";
	private int lastPercent = -1;
	private long lastReportTime = 0;

	public Progress(ProgressListener listener, int startPercent, int endPercent, int totalWeight) {
		this.listener = listener;
		this.startPercent = startPercent;
		this.endPercent = endPercent;
		this.totalWeight = Math.max(1, totalWeight);
	}

	/** Finishes the previous section (if any) and starts a new one. */
	public synchronized void begin(String label, int weight) {
		completedWeight += currentWeight;
		currentWeight = weight;
		currentLabel = label;
		emit(percentFor(0.0), label, true);
	}

	/**
	 * Progress within the current section. `detail` is appended on a second
	 * line under the section label; pass null to show only the label.
	 */
	public synchronized void update(int done, int total, String detail) {
		double fraction = total <= 0 ? 1.0 : Math.min(1.0, done / (double) total);
		String message = detail == null ? currentLabel : currentLabel + "\n" + detail;
		emit(percentFor(fraction), message, done >= total);
	}

	/** Marks everything done and reports the end percent. */
	public synchronized void finish(String message) {
		completedWeight = totalWeight;
		currentWeight = 0;
		emit(endPercent, message, true);
	}

	private int percentFor(double sectionFraction) {
		double overall = (completedWeight + currentWeight * sectionFraction) / totalWeight;
		overall = Math.max(0.0, Math.min(1.0, overall));
		return startPercent + (int) Math.round((endPercent - startPercent) * overall);
	}

	private void emit(int percent, String message, boolean force) {
		if (listener == null) return;
		long now = System.currentTimeMillis();
		if (!force && now - lastReportTime < MIN_REPORT_INTERVAL_MS) return;
		lastReportTime = now;
		lastPercent = Math.max(lastPercent, percent);
		listener.report(lastPercent, message);
	}
}