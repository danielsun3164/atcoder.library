package convolution;

import java.util.Arrays;

/**
 * https://github.com/atcoder/ac-library/blob/master/atcoder/convolution.hpp をもとに作成
 */
public class Convolution {
	static int MOD = -1;
	private static int SIZE = 30;
	private static boolean first = true;
	// sum_e[i] = ies[0] * ... * ies[i - 1] * es[i]
	private static long[] sum_e = new long[SIZE];
	private static boolean invFirst = true;
	// sum_ie[i] = es[0] * ... * es[i - 1] * ies[i]
	private static long[] sum_ie = new long[SIZE];
	private static int g;

	private static void butterfly(long[] a) {
		g = primitiveRoot(MOD);
		int n = a.length, h = ceilPow2(n);

		if (first) {
			first = false;
			Arrays.fill(sum_e, 0L);
			// es[i]^(2^(2+i)) == 1
			int cnt2 = bsf(MOD - 1);
			long[] es = new long[SIZE], ies = new long[SIZE];
			long e = powMod(g, (MOD - 1) >> cnt2), ie = invMod(e);
			for (int i = cnt2; i >= 2; i--) {
				// e^(2^i) == 1
				es[i - 2] = e;
				ies[i - 2] = ie;
				e = safeMod(e * e);
				ie = safeMod(ie * ie);
			}
			long now = 1L;
			for (int i = 0; i <= (cnt2 - 2); i++) {
				sum_e[i] = safeMod(es[i] * now);
				now = safeMod(now * ies[i]);
			}
		}
		for (int ph = 1; ph <= h; ph++) {
			int w = 1 << (ph - 1), p = 1 << (h - ph);
			long now = 1L;
			for (int s = 0; s < w; s++) {
				int offset = s << ((h - ph) + 1);
				for (int i = 0; i < p; i++) {
					long l = a[i + offset];
					long r = safeMod(a[i + offset + p] * now);
					a[i + offset] = safeMod(l + r);
					a[i + offset + p] = safeMod(l - r);
				}
				now = safeMod(now * (sum_e[bsf(~s & Integer.MAX_VALUE)]));
			}
		}
	}

	private static void butterflyInv(long[] a) {
		g = primitiveRoot(MOD);
		int n = a.length, h = ceilPow2(n);

		if (invFirst) {
			invFirst = false;
			Arrays.fill(sum_ie, 0L);
			// es[i]^(2^(2+i)) == 1
			int cnt2 = bsf(MOD - 1);
			long[] es = new long[SIZE], ies = new long[SIZE];
			long e = powMod(g, (MOD - 1) >> cnt2), ie = invMod(e);
			for (int i = cnt2; i >= 2; i--) {
				// e^(2^i) == 1
				es[i - 2] = e;
				ies[i - 2] = ie;
				e = safeMod(e * e);
				ie = safeMod(ie * ie);
			}
			long now = 1L;
			for (int i = 0; i <= (cnt2 - 2); i++) {
				sum_ie[i] = safeMod(ies[i] * now);
				now = safeMod(now * es[i]);
			}
		}

		for (int ph = h; ph >= 1; ph--) {
			int w = 1 << (ph - 1), p = 1 << (h - ph);
			long inow = 1L;
			for (int s = 0; s < w; s++) {
				int offset = s << ((h - ph) + 1);
				for (int i = 0; i < p; i++) {
					long l = a[i + offset];
					long r = a[i + offset + p];
					a[i + offset] = safeMod(l + r);
					a[i + offset + p] = safeMod(safeMod(l - r) * inow);
				}
				inow = safeMod(inow * sum_ie[bsf(~s & Integer.MAX_VALUE)]);
			}
		}
	}

	private static long[] convolutionNaive(long[] a, long[] b) {
		int n = a.length, m = b.length;
		long[] ans = new long[(n + m) - 1];
		if (n < m) {
			for (int j = 0; j < m; j++) {
				for (int i = 0; i < n; i++) {
					ans[i + j] = safeMod(ans[i + j] + safeMod(a[i] * b[j]));
				}
			}
		} else {
			for (int i = 0; i < n; i++) {
				for (int j = 0; j < m; j++) {
					ans[i + j] = safeMod(ans[i + j] + safeMod(a[i] * b[j]));
				}
			}
		}
		return ans;
	}

	private static long[] convolutionFft(long[] a, long[] b) {
		int n = a.length, m = b.length;
		int z = 1 << ceilPow2((n + m) - 1);
		a = Arrays.copyOf(a, z);
		butterfly(a);
		b = Arrays.copyOf(b, z);
		butterfly(b);
		for (int i = 0; i < z; i++) {
			a[i] = safeMod(a[i] * b[i]);
		}
		butterflyInv(a);
		a = Arrays.copyOf(a, (n + m) - 1);
		long iz = invMod(z);
		for (int i = 0; i < ((n + m) - 1); i++) {
			a[i] = safeMod(a[i] * iz);
		}
		return a;
	}

	private static long[] convolution(long[] a, long[] b) {
		int n = a.length, m = b.length;
		if ((0 == n) || (0 == m)) {
			return new long[0];
		}
		if (Math.min(n, m) <= 60) {
			return convolutionNaive(a, b);
		} else {
			return convolutionFft(a, b);
		}
	}

	/**
	 * 畳み込みを mod m で計算します。a,b の少なくとも一方が空配列の場合は空配列を返します。
	 *
	 * @param a
	 * @param b
	 * @param m
	 * @return 計算した結果配列
	 */
	static long[] convolution(long[] a, long[] b, int m) {
		if (MOD != m) {
			MOD = m;
			first = true;
			invFirst = true;
		}
		return convolution(a, b);
	}

	private static final long MOD1 = 754_974_721L; // 2^24
	private static final long MOD2 = 167_772_161L; // 2^25
	private static final long MOD3 = 469_762_049L; // 2^26
	private static final long M2M3 = MOD2 * MOD3;
	private static final long M1M3 = MOD1 * MOD3;
	private static final long M1M2 = MOD1 * MOD2;
	private static final long M1M2M3 = MOD1 * MOD2 * MOD3;

	private static final long i1 = invGcd(MOD2 * MOD3, MOD1)[1];
	private static final long i2 = invGcd(MOD1 * MOD3, MOD2)[1];
	private static final long i3 = invGcd(MOD1 * MOD2, MOD3)[1];

	/**
	 * 畳み込みを計算します。a,b の少なくとも一方が空配列の場合は空配列を返します。
	 *
	 * @param a
	 * @param b
	 * @return 計算した結果配列
	 */
	static long[] convolutionLong(long[] a, long[] b) {
		int n = a.length, m = b.length;
		if ((0 == n) || (0 == m)) {
			return new long[0];
		}

		long[] c1 = convolution(a, b, (int) MOD1);
		long[] c2 = convolution(a, b, (int) MOD2);
		long[] c3 = convolution(a, b, (int) MOD3);

		long[] c = new long[(n + m) - 1];
		for (int i = 0; i < ((n + m) - 1); i++) {
			long x = 0L;
			x += ((c1[i] * i1) % MOD1) * M2M3;
			x += ((c2[i] * i2) % MOD2) * M1M3;
			x += ((c3[i] * i3) % MOD3) * M1M2;
			int diff = (int) safeMod(c1[i] - safeMod(x, MOD1), MOD1);
			long[] offset = { 0L, 0L, M1M2M3, 2 * M1M2M3, 3 * M1M2M3 };
			x -= offset[diff % 5];
			c[i] = x;
		}

		return c;
	}

	// 以下 https://github.com/atcoder/ac-library/blob/master/atcoder/internal_math.hpp を参考に作成
	/**
	 * x^n mod MOD
	 *
	 * @param x
	 * @param n
	 * @return x^n mod MOD を計算する
	 */
	static long powMod(long x, long n) {
		return powMod(x, n, MOD);
	}

	/**
	 * x^n mod m を計算する
	 *
	 * @param x
	 * @param n
	 * @param m
	 * @return x^n mod m
	 */
	static long powMod(long x, long n, long m) {
		if (!((0L <= n) || (1L <= m))) {
			throw new IllegalArgumentException("n is " + n + ", m is " + m);
		}
		if (1L == m) {
			return 0L;
		}
		long r = 1L, y = safeMod(x, m);
		while (n > 0L) {
			if (1L == (n & 1L)) {
				r = safeMod(r * y, m);
			}
			y = safeMod(y * y, m);
			n >>= 1;
		}
		return r;
	}

	/**
	 * x mod MOD を安全に計算する
	 *
	 * @param x
	 * @return x mod MOD
	 */
	private static long safeMod(long x) {
		return safeMod(x, MOD);
	}

	/**
	 * x mod m を安全に計算する
	 *
	 * @param x
	 * @param m
	 * @return x mod m
	 */
	static long safeMod(long x, long m) {
		x %= m;
		if (x < 0) {
			x += m;
		}
		return x;
	}

	static long invMod(long x) {
		return invMod(x, MOD);
	}

	/**
	 * xy≡1(mod m) なる y のうち、0≤y<m を満たすものを返します。
	 *
	 * @param x
	 * @param m
	 * @return xy≡1 (mod m) なる y のうち、0≤y<m を満たすもの
	 */
	static long invMod(long x, long m) {
		if (!(1 <= m)) {
			throw new IllegalArgumentException("m is " + m);
		}
		long[] z = invGcd(x, m);
		if (1L != z[0]) {
			throw new IllegalArgumentException("z[0] is " + z[0]);
		}
		return z[1];
	}

	/**
	 * @param a
	 * @param b `1 <= b`
	 * @return {g, x} s.t. g = gcd(a, b), x a = g (mod b), 0 <= x < b/g
	 */
	static long[] invGcd(long a, long b) {
		a = safeMod(a, b);
		if (a == 0) {
			return new long[] { b, 0 };
		}

		long s = b, t = a;
		long m0 = 0L, m1 = 1L;

		while (t != 0) {
			long u = s / t;
			s -= t * u;
			m0 -= m1 * u;

			long tmp = s;
			s = t;
			t = tmp;
			tmp = m0;
			m0 = m1;
			m1 = tmp;
		}
		if (m0 < 0) {
			m0 += b / s;
		}
		return new long[] { s, m0 };
	}

	static int primitiveRoot(int m) {
		if (2 == m) {
			return 1;
		}
		if (167772161 == m) {
			return 3;
		}
		if (469762049 == m) {
			return 3;
		}
		if (754974721 == m) {
			return 11;
		}
		if (998244353 == m) {
			return 3;
		}
		int[] divs = new int[20];
		Arrays.fill(divs, 0);
		divs[0] = 2;
		int cnt = 1;
		int x = (m - 1) / 2;
		while (0 == (x & 1)) {
			x >>= 1;
		}
		for (int i = 3; ((long) (i) * i) <= x; i += 2) {
			if (0 == (x % i)) {
				divs[cnt++] = i;
				while (0 == (x % i)) {
					x /= i;
				}
			}
		}
		if (x > 1) {
			divs[cnt++] = x;
		}
		for (int g = 2;; g++) {
			boolean ok = true;
			for (int i = 0; i < cnt; i++) {
				if (1L == powMod(g, (m - 1) / divs[i], m)) {
					ok = false;
					break;
				}
			}
			if (ok) {
				return g;
			}
		}
	}

	/**
	 *
	 * @param n `0 <= n`
	 * @return minimum non-negative `x` s.t. `n <= 2**x`
	 */
	static int ceilPow2(int n) {
		if (!(0 <= n)) {
			throw new IllegalArgumentException("n is " + n);
		}
		int x = 0;
		while ((1L << x) < n) {
			x++;
		}
		return x;
	}

	/**
	 *
	 * @param n `1 <= n`
	 * @return minimum non-negative `x` s.t. `(n & (1 << x)) != 0`
	 */
	static int bsf(long n) {
		if (!(1L <= n)) {
			throw new IllegalArgumentException("n is " + n);
		}
		int x = 0;
		while ((0 == (n & (1L << x))) && (x < 31)) {
			x++;
		}
		return x;
	}
}
