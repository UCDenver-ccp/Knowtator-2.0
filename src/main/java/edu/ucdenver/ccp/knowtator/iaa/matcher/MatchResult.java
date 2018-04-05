package edu.ucdenver.ccp.knowtator.iaa.matcher;

public class MatchResult {
//	public static final int MATCH_RESULT_UNASSIGNED = -1;

	public static final int NONTRIVIAL_MATCH = 0;

	public static final int TRIVIAL_MATCH = 1;

	public static final int NONTRIVIAL_NONMATCH = 2;

	public static final int TRIVIAL_NONMATCH = 3;

	private int result = -1;

	public MatchResult() {
	}

	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}
}
