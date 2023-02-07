package string;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.IntStream;

/**
 * https://github.com/atcoder/ac-library/blob/master/atcoder/string.hpp を参考に作成
 */
public class AtcoderString {
	static int[] saNaive(int[] s) {
		int n = s.length;
		return IntStream.range(0, n).boxed().sorted((l, r) -> {
			if (Objects.equals(l, r)) {
				return 0;
			}
			while (l < n && r < n) {
				if (s[l] != s[r]) {
					return Integer.compare(s[l], s[r]);
				}
				l++;
				r++;
			}
			return (l == n) ? -1 : 1;
		}).mapToInt(Integer::intValue).toArray();
	}

	static int[] saDoubling(int[] s) {
		int n = s.length;
		Integer[] sa = IntStream.range(0, n).boxed().toArray(Integer[]::new);
		int[] rnk2 = Arrays.copyOf(s, n), tmp = new int[n];
		for (int k2 = 0; (1 << k2) < n; k2++) {
			int[] rnk = rnk2;
			int k = 1 << k2;
			Arrays.sort(sa, (x, y) -> {
				if (rnk[x] != rnk[y]) {
					return Integer.compare(rnk[x], rnk[y]);
				}
				int rx = (x + k < n) ? rnk[x + k] : -1;
				int ry = (y + k < n) ? rnk[y + k] : -1;
				return Integer.compare(rx, ry);
			});
			tmp[sa[0]] = 0;
			for (int i = 1; i < n; i++) {
				tmp[sa[i]] = tmp[sa[i - 1]] + (cmp(sa[i - 1], sa[i], n, k, rnk) ? 1 : 0);
			}
			int[] tmp2 = tmp;
			tmp = rnk2;
			rnk2 = tmp2;
		}
		return Arrays.stream(sa).mapToInt(Integer::intValue).toArray();
	}

	private static boolean cmp(final int x, final int y, final int n, final int k, final int[] rnk) {
		if (rnk[x] != rnk[y]) {
			return rnk[x] < rnk[y];
		}
		int rx = (x + k < n) ? rnk[x + k] : -1;
		int ry = (y + k < n) ? rnk[y + k] : -1;
		return rx < ry;
	}

	private static final int THRESHOLD_NAIVE = 10;
	private static final int THRESHOLD_DOUBLING = 40;

	static int[] saIs(int[] s, int upper) {
		return saIs(s, upper, THRESHOLD_NAIVE, THRESHOLD_DOUBLING);
	}

	static int[] saIs(int[] s, int upper, final int naive, final int doubling) {
		if (null == s) {
			return new int[] {};
		}
		int n = s.length;
		if (0 == n) {
			return new int[] {};
		}
		if (1 == n) {
			return new int[] { 0 };
		}
		if (2 == n) {
			if (s[0] < s[1]) {
				return new int[] { 0, 1 };
			} else {
				return new int[] { 1, 0 };
			}
		}
		if (n < naive) {
			return saNaive(s);
		}
		if (n < doubling) {
			return saDoubling(s);
		}
		int[] sa = new int[n];
		boolean[] ls = new boolean[n];
		for (int i = n - 2; i >= 0; i--) {
			ls[i] = (s[i] == s[i + 1]) ? ls[i + 1] : (s[i] < s[i + 1]);
		}
		int[] sumL = new int[upper + 1], sumS = new int[upper + 1];
		IntStream.range(0, n).forEach(i -> {
			if (!ls[i]) {
				sumS[s[i]]++;
			} else {
				sumL[s[i] + 1]++;
			}
		});
		IntStream.rangeClosed(0, upper).forEach(i -> {
			sumS[i] += sumL[i];
			if (i < upper) {
				sumL[i + 1] += sumS[i];
			}
		});
		int[] lmsMap = new int[n + 1];
		Arrays.fill(lmsMap, -1);
		int m = 0;
		for (int i = 1; i < n; i++) {
			if (!ls[i - 1] && ls[i]) {
				lmsMap[i] = m++;
			}
		}
		int[] lms = IntStream.range(1, n).filter(i -> !ls[i - 1] && ls[i]).toArray();
		induce(n, s, upper, sa, ls, sumL, sumS, lms);

		if (0 != m) {
			int[] sortedLms = Arrays.stream(sa).filter(v -> lmsMap[v] != -1).toArray();
			int[] recS = new int[m];
			int recUpper = 0;
			recS[lmsMap[sortedLms[0]]] = 0;
			for (int i = 1; i < m; i++) {
				int l = sortedLms[i - 1], r = sortedLms[i];
				int endL = (lmsMap[l] + 1 < m) ? lms[lmsMap[l] + 1] : n;
				int endR = (lmsMap[r] + 1 < m) ? lms[lmsMap[r] + 1] : n;
				boolean same = true;
				if (endL - l != endR - r) {
					same = false;
				} else {
					while (l < endL) {
						if (s[l] != s[r]) {
							break;
						}
						l++;
						r++;
					}
					if ((l == n) || (s[l] != s[r])) {
						same = false;
					}
				}
				if (!same) {
					recUpper++;
				}
				recS[lmsMap[sortedLms[i]]] = recUpper;
			}
			int[] recSa = saIs(recS, recUpper);

			IntStream.range(0, m).forEach(i -> {
				sortedLms[i] = lms[recSa[i]];
			});
			induce(n, s, upper, sa, ls, sumL, sumS, sortedLms);
		}
		return sa;
	}

	private static void induce(int n, int[] s, int upper, int[] sa, boolean[] ls, int[] sumL, int[] sumS, int[] lms) {
		Arrays.fill(sa, -1);
		int[] buf = new int[upper + 1];
		System.arraycopy(sumS, 0, buf, 0, upper + 1);
		for (int d : lms) {
			if (d != n) {
				sa[buf[s[d]]++] = d;
			}
		}
		System.arraycopy(sumL, 0, buf, 0, upper + 1);
		sa[buf[s[n - 1]]++] = n - 1;
		IntStream.range(0, n).forEach(i -> {
			int v = sa[i];
			if ((v >= 1) && !ls[v - 1]) {
				sa[buf[s[v - 1]]++] = v - 1;
			}
		});
		System.arraycopy(sumL, 0, buf, 0, upper + 1);
		for (int i = n - 1; i >= 0; i--) {
			int v = sa[i];
			if ((v >= 1) && ls[v - 1]) {
				sa[--buf[s[v - 1] + 1]] = v - 1;
			}
		}
	}

	/**
	 * 配列sのSuffix Arrayを計算する
	 *
	 * @param s     配列
	 * @param upper sの最大値
	 * @return 配列sのSuffix Array
	 */
	static int[] suffixArray(int[] s, int upper) {
		if (!(0 <= upper)) {
			throw new IllegalArgumentException("upper is " + upper);
		}
		for (int d : s) {
			if (!(0 <= d && d <= upper)) {
				throw new IllegalArgumentException("upper is " + upper + ", d is " + d);
			}
		}
		return saIs(s, upper);
	}

	/**
	 * 配列sのSuffix Arrayを計算する
	 *
	 * @param s 配列
	 * @return 配列sのSuffix Array
	 */
	static int[] suffixArray(int[] s) {
		int n = s.length;
		int[] idx = IntStream.range(0, n).boxed().sorted((a, b) -> Integer.compare(s[a], s[b])).mapToInt(i -> i)
				.toArray();
		int[] s2 = new int[n];
		int now = 0;
		for (int i = 0; i < n; i++) {
			if ((i > 0) && (s[idx[i - 1]] != s[idx[i]])) {
				now++;
			}
			s2[idx[i]] = now;
		}
		return saIs(s2, now);
	}

	/**
	 * 文字列sのSuffix Arrayを計算する
	 *
	 * @param s 文字列
	 * @return 文字列sのSuffix Array
	 */
	static int[] suffixArray(String s) {
		int[] s2 = IntStream.range(0, s.length()).map(i -> s.charAt(i)).toArray();
		return saIs(s2, 255);
	}

	/**
	 * 配列sのLCP Arrayを計算する
	 *
	 * @param s  配列
	 * @param sa sのSuffix Array
	 * @return 配列sのLCP Array，i番目の要素は s[sa[i]..n), s[sa[i+1]..n) の LCP(Longest Common Prefix) の長さ。
	 */
	static int[] lcpArray(int[] s, int[] sa) {
		int n = s.length;
		if (!(n >= 1)) {
			throw new IllegalArgumentException("n is " + n);
		}
		int[] rnk = new int[n];
		IntStream.range(0, n).forEach(i -> {
			rnk[sa[i]] = i;
		});
		int[] lcp = new int[n - 1];
		int h = 0;
		for (int i = 0; i < n; i++) {
			if (h > 0) {
				h--;
			}
			if (0 == rnk[i]) {
				continue;
			}
			int j = sa[rnk[i] - 1];
			for (; (j + h < n) && (i + h < n); h++) {
				if (s[j + h] != s[i + h]) {
					break;
				}
			}
			lcp[rnk[i] - 1] = h;
		}
		return lcp;
	}

	/**
	 * 配列sのLCP Arrayを計算する
	 *
	 * @param s  配列
	 * @param sa sのSuffix Array
	 * @return 配列sのLCP Array，i番目の要素は s[sa[i]..n), s[sa[i+1]..n) の LCP(Longest Common Prefix) の長さ。
	 */
	static int[] lcpArray(long[] s, int[] sa) {
		int n = s.length;
		if (!(n >= 1)) {
			throw new IllegalArgumentException("n is " + n);
		}
		int[] rnk = new int[n];
		IntStream.range(0, n).forEach(i -> {
			rnk[sa[i]] = i;
		});
		int[] lcp = new int[n - 1];
		int h = 0;
		for (int i = 0; i < n; i++) {
			if (h > 0) {
				h--;
			}
			if (0 == rnk[i]) {
				continue;
			}
			int j = sa[rnk[i] - 1];
			for (; (j + h < n) && (i + h < n); h++) {
				if (s[j + h] != s[i + h]) {
					break;
				}
			}
			lcp[rnk[i] - 1] = h;
		}
		return lcp;
	}

	/**
	 * 文字列sのLCP Arrayを計算する
	 *
	 * @param s  文字列
	 * @param sa sのSuffix Array
	 * @return 文字列sのLCP Array，i番目の要素は s[sa[i]..n), s[sa[i+1]..n) の LCP(Longest Common Prefix) の長さ。
	 */
	static int[] lcpArray(String s, int[] sa) {
		int[] s2 = IntStream.range(0, s.length()).map(i -> s.charAt(i)).toArray();
		return lcpArray(s2, sa);
	}

	/**
	 * @param s 配列
	 * @return 長さnの配列。 i番目の要素は s[0..n)とs[i..n)のLCP(Longest Common Prefix)の長さ。
	 */
	static int[] zAlgorithm(int[] s) {
		int n = s.length;
		if (0 == n) {
			return new int[] {};
		}
		int[] z = new int[n];
		z[0] = 0;
		for (int i = 1, j = 0; i < n; i++) {
			z[i] = (j + z[j] <= i) ? 0 : Math.min(j + z[j] - i, z[i - j]);
			while (i + z[i] < n && s[z[i]] == s[i + z[i]]) {
				z[i]++;
			}
			if (j + z[j] < i + z[i]) {
				j = i;
			}
		}
		z[0] = n;
		return z;
	}

	/**
	 * @param s 配列
	 * @return 長さnの配列。 i番目の要素は s[0..n)とs[i..n)のLCP(Longest Common Prefix)の長さ。
	 */
	static int[] zAlgorithm(char[] s) {
		int n = s.length;
		if (0 == n) {
			return new int[] {};
		}
		int[] z = new int[n];
		z[0] = 0;
		for (int i = 1, j = 0; i < n; i++) {
			z[i] = (j + z[j] <= i) ? 0 : Math.min(j + z[j] - i, z[i - j]);
			while (i + z[i] < n && s[z[i]] == s[i + z[i]]) {
				z[i]++;
			}
			if (j + z[j] < i + z[i]) {
				j = i;
			}
		}
		z[0] = n;
		return z;
	}

	/**
	 * @param s 文字列
	 * @return 長さnの配列。 i番目の要素は s[0..n)とs[i..n)のLCP(Longest Common Prefix)の長さ。
	 */
	static int[] zAlgorithm(String s) {
		return zAlgorithm(s.toCharArray());
	}
}
