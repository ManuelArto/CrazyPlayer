package mnkgame.CrazyPlayer.model;

import mnkgame.MNKCell;

import java.util.Comparator;

public class MNKCellEstimate extends MNKCell {
	private double estimate;

	public MNKCellEstimate(int i, int j, double estimate) {
		super(i, j);
		this.estimate = estimate;
	}

	public void setEstimate(double estimate) {
		this.estimate = estimate;
	}

	public double getEstimate() {
		return estimate;
	}

	@Override
	public String toString() {
		return super.toString() + " - " + this.estimate;
	}

	public static Comparator<MNKCellEstimate> getCellComparator(boolean asc) {
		return new Comparator<MNKCellEstimate>() {
			@Override
			public int compare(MNKCellEstimate c1, MNKCellEstimate c2) {
				if (c1.getEstimate() - c2.getEstimate() == 0.0) return 1;    // TreeSet non contiene "duplicati"
				return (int) (asc ?
						(c1.getEstimate() - c2.getEstimate()) :
						(c2.getEstimate() - c1.getEstimate()));
			}
		};
	}

}
