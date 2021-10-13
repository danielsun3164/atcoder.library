package scc;

import java.util.Arrays;

/**
 * https://github.com/atcoder/ac-library/blob/master/atcoder/twosat.hpp をもとに作成
 */
public class TwoSat {
	/** 要素数 */
	final int n;
	/** 回答 */
	final boolean[] answer;
	final SccGraph scc;

	/**
	 * コンストラクター
	 */
	TwoSat() {
		n = 0;
		answer = new boolean[n];
		scc = new SccGraph(2 * n);
	}

	/**
	 * コンストラクター
	 *
	 * @param n
	 */
	TwoSat(int n) {
		this.n = n;
		answer = new boolean[n];
		Arrays.fill(answer, false);
		scc = new SccGraph(2 * n);
	}

	/**
	 * 条件を追加
	 *
	 * @param i
	 * @param f
	 * @param j
	 * @param g
	 */
	void addClause(int i, boolean f, int j, boolean g) {
		if (!((0 <= i) && (i < n))) {
			throw new IllegalArgumentException("i is " + i);
		}
		if (!((0 <= j) && (j < n))) {
			throw new IllegalArgumentException("j is " + j);
		}
		scc.addEdge(2 * i + (f ? 0 : 1), 2 * j + (g ? 1 : 0));
		scc.addEdge(2 * j + (g ? 0 : 1), 2 * i + (f ? 1 : 0));
	}

	/**
	 * @return 全体の条件結果
	 */
	boolean satisfiable() {
		int[] id = scc.sccIds().edges;
		for (int i = 0; i < n; i++) {
			if (id[2 * i] == id[2 * i + 1]) {
				return false;
			}
			answer[i] = id[2 * i] < id[2 * i + 1];
		}
		return true;
	}
}
