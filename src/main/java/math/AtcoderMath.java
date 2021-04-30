package math;

import java.util.Arrays;

/**
 * クラス名はjava.lang.Mathと混同しないためにAtcoderMathとする
 *
 * https://github.com/atcoder/ac-library/blob/master/atcoder/math.hpp<br/>
 * https://github.com/atcoder/ac-library/blob/master/atcoder/internal_math.hpp
 * https://github.com/atcoder/ac-library/blob/master/atcoder/internal_bit.hpp をもとに作成
 */
public class AtcoderMath {

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
	 * 同じ長さの配列 r, m に対して、この配列の長さをnとした時、<br/>
	 * x≡r[i](modm[i]),∀i∈{0,1,⋯,n−1} <br/>
	 * を解きます。答えは(存在するならば) y,z(0≤y<z=lcm(m[i])) を用いて x≡y(mod z) の形で書けることが知られており、<br/>
	 * この (y,z) を配列として返します。答えがない場合は (0,0) を返します。
	 *
	 * @param r
	 * @param m
	 * @return
	 */
	static long[] crt(long[] r, long[] m) {
		if (r.length != m.length) {
			throw new IllegalArgumentException("r.length is " + r.length + ", m.length is " + m.length);
		}
		int n = r.length;
		long r0 = 0L, m0 = 1L;
		for (int i = 0; i < n; i++) {
			if (m[i] < 1L) {
				throw new IllegalArgumentException("m[" + i + "] is " + m[i]);
			}
			long r1 = safeMod(r[i], m[i]), m1 = m[i];
			if (m0 < m1) {
				// swap r0,r1 m0,m1
				long tmp = r0;
				r0 = r1;
				r1 = tmp;
				tmp = m0;
				m0 = m1;
				m1 = tmp;
			}
			if (0L == (m0 % m1)) {
				if ((r0 % m1) != r1) {
					return new long[] { 0L, 0L };
				}
				continue;
			}

			long[] gcd = invGcd(m0, m1);
			long g = gcd[0], im = gcd[1];

			long u1 = m1 / g;
			if (((r1 - r0) % g) != 0L) {
				return new long[] { 0L, 0L };
			}

			long x = ((((r1 - r0) / g) % u1) * im) % u1;
			r0 += x * m0;
			m0 *= u1;
			if (r0 < 0L) {
				r0 += m0;
			}
		}
		return new long[] { r0, m0 };
	}

	/**
	 * ∑i=0 n−1 floor(a×i+b/m) を返します。
	 *
	 * @param n
	 * @param m
	 * @param a
	 * @param b
	 * @return ∑i=0 n−1 floor(a×i+b/m)
	 */
	static long floorSum(long n, long m, long a, long b) {
		if (!((0 <= n) && (n < (1L << 32)))) {
			throw new IllegalArgumentException("n is " + n);
		}
		if (!((1 <= m) && (m < (1L << 32)))) {
			throw new IllegalArgumentException("m is " + m);
		}
		long ans = 0;
		if (a < 0) {
			long a2 = safeMod(a, m);
			ans -= ((n * (n - 1)) / 2L) * ((a2 - a) / m);
			a = a2;
		}
		if (b < 0) {
			long b2 = safeMod(b, m);
			ans -= n * ((b2 - b) / m);
			b = b2;
		}
		return ans + floorSumUnsigned(n, m, a, b);
	}

	private static long floorSumUnsigned(long n, long m, long a, long b) {
		long ans = 0L;
		while (true) {
			if (a >= m) {
				ans += (((n - 1) * n) / 2L) * (a / m);
				a %= m;
			}
			if (b >= m) {
				ans += n * (b / m);
				b %= m;
			}

			long yMax = (a * n) + b;
			if (yMax < m) {
				break;
			}
			n = yMax / m;
			b = yMax % m;
			// swap m,a
			long tmp = a;
			a = m;
			m = tmp;
		}
		return ans;
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

	/**
	 * nが素数かどうかを判定する
	 *
	 * @param n
	 * @return nが素数かどうか
	 */
	static boolean isPrime(long n) {
		if (n <= 1L) {
			return false;
		}
		if ((2L == n) || (7L == n) || (61L == n)) {
			return true;
		}
		if (0L == (n & 1L)) {
			return false;
		}
		long d = n - 1L;
		while (0L == (d & 1L)) {
			d >>= 1;
		}
		long[] bases = new long[] { 2L, 7L, 61L };
		for (long a : bases) {
			long t = d;
			long y = powMod(a, t, n);
			while ((t != (n - 1)) && (1L != y) && (y != (n - 1))) {
				y = (y * y) % n;
				t <<= 1;
			}
			if ((y != (n - 1)) && (0 == (t & 1))) {
				return false;
			}
		}
		return true;
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
