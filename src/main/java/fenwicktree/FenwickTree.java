package fenwicktree;

import java.util.Arrays;

/**
 * https://github.com/atcoder/ac-library/blob/master/atcoder/fenwicktree.hpp のJava実装
 *
 * 実行速度を重視するため、Genericsには対応しない
 */
class FenwickTree {
	/** 項目数 */
	final int n;
	/** データ */
	final long[] data;

	/**
	 * コンストラクター
	 *
	 * @param n 項目数
	 */
	FenwickTree(int n) {
		if (!(0 <= n)) {
			throw new IllegalArgumentException("n is " + n);
		}
		this.n = n;
		data = new long[n];
		Arrays.fill(data, 0L);
	}

	/**
	 * インデックスpの値にxを加算する
	 *
	 * @param p 0-index
	 * @param x
	 */
	void add(int p, long x) {
		if (!((0 <= p) && (p < n))) {
			throw new IllegalArgumentException("p is " + p);
		}
		p++;
		while (p <= n) {
			data[p - 1] += x;
			p += p & -p;
		}
	}

	/**
	 * dataの[l,r)の範囲の合計値を計算する
	 *
	 * @param l 0-index
	 * @param r 0-index
	 * @return dataの[l,r)の範囲の合計値
	 */
	long sum(int l, int r) {
		if (!((0 <= l) && (l <= r) && (r <= n))) {
			throw new IllegalArgumentException("l is " + l + ", r is " + r);
		}
		return sum(r) - sum(l);
	}

	/**
	 * インデックスがrより小さいのdataの合計値を計算する
	 *
	 * @param r 0-index
	 * @return インデックスがrより小さいのdataの合計値
	 */
	private long sum(int r) {
		long s = 0;
		while (r > 0) {
			s += data[r - 1];
			r -= r & -r;
		}
		return s;
	}
}
