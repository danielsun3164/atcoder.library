package dsu;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.IntStream;

/**
 * https://github.com/atcoder/ac-library/blob/master/atcoder/dsu.hpp のJava実装
 */
class DisjointSetUnion {
	/** 項目数 */
	final int n;
	/** 親のidかグループのサイズ */
	final int[] parentOrSize;
	/** グループの数 */
	int groupNum;

	/**
	 * コンストラクター
	 *
	 * @param n 項目数
	 */
	DisjointSetUnion(int n) {
		if (!(0 <= n)) {
			throw new IllegalArgumentException("n is " + n);
		}
		this.n = n;
		parentOrSize = new int[n];
		Arrays.fill(parentOrSize, -1);
		groupNum = n;
	}

	/**
	 * aとbを同じグループにマージする
	 *
	 * @param a
	 * @param b
	 * @return マージ後のグループリーダー
	 */
	int merge(int a, int b) {
		if (!((0 <= a) && (a < n))) {
			throw new IllegalArgumentException("a is " + a);
		}
		if (!((0 <= b) && (b < n))) {
			throw new IllegalArgumentException("b is " + b);
		}
		int x = leader(a), y = leader(b);
		if (x == y) {
			return x;
		}
		int max = (-parentOrSize[x] < -parentOrSize[y]) ? y : x;
		int min = (-parentOrSize[x] < -parentOrSize[y]) ? x : y;
		parentOrSize[max] += parentOrSize[min];
		parentOrSize[min] = max;
		groupNum--;
		return max;
	}

	/**
	 * aとbが同じグループに所属しているかを判定する
	 *
	 * @param a
	 * @param b
	 * @return aとbが同じグループに所属しているか
	 */
	boolean same(int a, int b) {
		if (!((0 <= a) && (a < n))) {
			throw new IllegalArgumentException("a is " + a);
		}
		if (!((0 <= b) && (b < n))) {
			throw new IllegalArgumentException("b is " + b);
		}
		return leader(a) == leader(b);
	}

	/**
	 * aのグループリーダーを取得する
	 *
	 * @param a
	 * @return aのグループリーダー
	 */
	int leader(int a) {
		if (!((0 <= a) && (a < n))) {
			throw new IllegalArgumentException("a is " + a);
		}
		if (parentOrSize[a] < 0) {
			return a;
		}
		return parentOrSize[a] = leader(parentOrSize[a]);
	}

	/**
	 * aの所属グループのメンバー数を取得する
	 *
	 * @param a
	 * @return aの所属グループのメンバー数
	 */
	int size(int a) {
		if (!((0 <= a) && (a < n))) {
			throw new IllegalArgumentException("a is " + a);
		}
		return -parentOrSize[leader(a)];
	}

	/**
	 * @return グループの一覧
	 */
	int[][] groups() {
		// leaderBuf[i]はiのリーダー、groupSize[i]はiの所在groupのサイズ
		int[] leaderBuf = new int[n], groupSize = new int[n];
		IntStream.range(0, n).forEach(i -> {
			leaderBuf[i] = leader(i);
			groupSize[leaderBuf[i]]++;
		});
		Set<Integer> leaderSet = new HashSet<>();
		int count = 0;
		// groupNo[i]はiの所在グループの番号、groupLeader[i]はグループiのリーダー
		int[] groupNo = new int[n], groupLeader = new int[groupNum];
		for (int i = 0; i < n; i++) {
			if (!leaderSet.contains(leaderBuf[i])) {
				groupNo[leaderBuf[i]] = count;
				groupLeader[count] = leaderBuf[i];
				count++;
				leaderSet.add(leaderBuf[i]);
			}
			groupNo[i] = groupNo[leaderBuf[i]];
		}
		int[] indexes = new int[groupNum];
		int[][] result = new int[groupNum][];
		for (int i = 0; i < groupNum; i++) {
			result[i] = new int[groupSize[groupLeader[i]]];
		}
		Arrays.fill(indexes, 0);
		for (int i = 0; i < n; i++) {
			result[groupNo[i]][indexes[groupNo[i]]++] = i;
		}
		return result;
	}
}
