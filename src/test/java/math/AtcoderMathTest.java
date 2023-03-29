package math;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * https://github.com/atcoder/ac-library/blob/master/test/unittest/math_test.cpp
 * https://github.com/atcoder/ac-library/blob/master/test/unittest/internal_math_test.cpp
 * https://github.com/atcoder/ac-library/blob/master/test/unittest/bit_test.cpp をもとに作成
 */
class AtcoderMathTest {

	private static long gcd(long a, long b) {
		if (!((0 <= a) && (0 <= b))) {
			throw new IllegalArgumentException("a is " + a + ", b is " + b);
		}
		return (0L == b) ? a : gcd(b, a % b);
	}

	private static long floorSumNaive(long n, long m, long a, long b) {
		long sum = 0L;
		for (long i = 0L; i < n; i++) {
			long z = (a * i) + b;
			sum += (z - AtcoderMath.safeMod(z, m)) / m;
		}
		return sum;
	}

	private static boolean isPrimeNaive(long n) {
		if (!((0 <= n) && (n <= Integer.MAX_VALUE))) {
			throw new IllegalArgumentException("n is " + n);
		}
		if ((0L == n) || (1L == n)) {
			return false;
		}
		for (long i = 2L; (i * i) <= n; i++) {
			if (0 == (n % i)) {
				return false;
			}
		}
		return true;
	}

	private static int[] factors(int m) {
		List<Integer> result = new ArrayList<>();
		for (int i = 2; ((long) (i) * i) <= m; i++) {
			if (0 == (m % i)) {
				result.add(i);
				while (0 == (m % i)) {
					m /= i;
				}
			}
		}
		if (m > 1) {
			result.add(m);
		}
		return result.stream().mapToInt(i -> i).toArray();
	}

	boolean isPrimitiveRoot(int m, int g) {
		if (!((1 <= g) && (g < m))) {
			throw new IllegalArgumentException("m is " + m + ", g is " + g);
		}
		int[] prs = factors(m - 1);
		for (int x : prs) {
			if (1L == AtcoderMath.powMod(g, (m - 1) / x, m)) {
				return false;
			}
		}
		return true;
	}

	@Test
	void powMod() {
		class Local {
			long powModNaive(long x, long n, long mod) {
				long y = AtcoderMath.safeMod(x, mod);
				long z = 1 % mod;
				for (long i = 0L; i < n; i++) {
					z = (z * y) % mod;
				}
				return z;
			}
		}
		Local local = new Local();
		for (int a = -100; a <= 100; a++) {
			for (int b = 0; b <= 100; b++) {
				for (int c = 1; c <= 100; c++) {
					assertEquals(local.powModNaive(a, b, c), AtcoderMath.powMod(a, b, c));
				}
			}
		}
	}

	@Test
	void invBoundHand() {
		assertEquals(AtcoderMath.invMod(-1L, Long.MAX_VALUE), AtcoderMath.invMod(Long.MIN_VALUE, Long.MAX_VALUE));
		assertEquals(1L, AtcoderMath.invMod(Long.MAX_VALUE, Long.MAX_VALUE - 1));
		assertEquals(Long.MAX_VALUE - 1L, AtcoderMath.invMod(Long.MAX_VALUE - 1, Long.MAX_VALUE));
		assertEquals(2L, AtcoderMath.invMod((Long.MAX_VALUE >> 1) + 1L, Long.MAX_VALUE));
	}

	@Test
	void invMod() {
		for (int a = -100; a <= 100; a++) {
			for (int b = 1; b <= 1000; b++) {
				if (1L != gcd(AtcoderMath.safeMod(a, b), b)) {
					continue;
				}
				long c = AtcoderMath.invMod(a, b);
				assertTrue(0 <= c, "c is" + c);
				assertTrue(c < b, "c is " + c + ", b is " + b);
				assertEquals(1 % b, (((a * c) % b) + b) % b);
			}
		}
	}

	@Test
	void invModZero() {
		assertEquals(0L, AtcoderMath.invMod(0L, 1L));
		for (int i = 0; i < 10; i++) {
			assertEquals(0L, AtcoderMath.invMod(i, 1L));
			assertEquals(0L, AtcoderMath.invMod(-i, 1L));
			assertEquals(0L, AtcoderMath.invMod(Long.MIN_VALUE + i, 1L));
			assertEquals(0L, AtcoderMath.invMod(Long.MAX_VALUE - i, 1L));
		}
	}

	@Test
	void floorSum() {
		for (int n = 0; n < 20; n++) {
			for (int m = 1; m < 20; m++) {
				for (int a = -20; a < 20; a++) {
					for (int b = -20; b < 20; b++) {
						assertEquals(floorSumNaive(n, m, a, b), AtcoderMath.floorSum(n, m, a, b));
					}
				}
			}
		}
	}

	@Test
	void crtHand() {
		long[] res = AtcoderMath.crt(new long[] { 1, 2, 1 }, new long[] { 2, 3, 2 });
		assertEquals(5L, res[0]);
		assertEquals(6L, res[1]);
	}

	@Test
	void crt2() {
		for (int a = 1; a <= 20; a++) {
			for (int b = 1; b <= 20; b++) {
				for (int c = -10; c <= 10; c++) {
					for (int d = -10; d <= 10; d++) {
						long[] res = AtcoderMath.crt(new long[] { c, d }, new long[] { a, b });
						if (0L == res[1]) {
							for (int x = 0; x < ((a * b) / gcd(a, b)); x++) {
								assertTrue(((x % a) != c) || ((x % b) != d));
							}
							continue;
						}
						assertEquals((a * b) / gcd(a, b), res[1]);
						assertEquals(AtcoderMath.safeMod(c, a), res[0] % a);
						assertEquals(AtcoderMath.safeMod(d, b), res[0] % b);
					}
				}
			}
		}
	}

	@Test
	void crt3() {
		for (int a = 1; a <= 5; a++) {
			for (int b = 1; b <= 5; b++) {
				for (int c = 1; c <= 5; c++) {
					for (int d = -5; d <= 5; d++) {
						for (int e = -5; e <= 5; e++) {
							for (int f = -5; f <= 5; f++) {
								long[] res = AtcoderMath.crt(new long[] { d, e, f }, new long[] { a, b, c });
								long lcm = (a * b) / gcd(a, b);
								lcm = (lcm * c) / gcd(lcm, c);
								if (0L == res[1]) {
									for (int x = 0; x < lcm; x++) {
										assertTrue(((x % a) != d) || ((x % b) != e) || ((x % c) != f));
									}
									continue;
								}
								assertEquals(lcm, res[1]);
								assertEquals(AtcoderMath.safeMod(d, a), res[0] % a);
								assertEquals(AtcoderMath.safeMod(e, b), res[0] % b);
								assertEquals(AtcoderMath.safeMod(f, c), res[0] % c);
							}
						}
					}
				}
			}
		}
	}

	@Test
	void crtOverflow() {
		long r0 = 0L;
		long r1 = 1_000_000_000_000L - 2L;
		long m0 = 900577;
		long m1 = 1_000_000_000_000L;
		long[] res = AtcoderMath.crt(new long[] { r0, r1 }, new long[] { m0, m1 });
		assertEquals(m0 * m1, res[1]);
		assertEquals(r0, res[0] % m0);
		assertEquals(r1, res[0] % m1);
	}

	@Test
	void crtBound() {
		long INF = Long.MAX_VALUE;
		int j = 0;
		long[] pred = new long[23];
		for (int i = 1; i <= 10; i++) {
			pred[j++] = i;
			pred[j++] = INF - (i - 1);
		}
		pred[j++] = 998_244_353L;
		pred[j++] = 1_000_000_007L;
		pred[j++] = 1_000_000_009L;

		for (long[] ab : new long[][] { { INF, INF }, { 1, INF }, { INF, 1 }, { 7, INF }, { INF / 337, 337 },
				{ 2, (INF - 1) / 2 } }) {
			long a = ab[0], b = ab[1];
			for (int ph = 0; ph < 2; ph++) {
				for (long ans : pred) {
					long[] res = AtcoderMath.crt(new long[] { ans % a, ans % b }, new long[] { a, b });
					long lcm = (a / gcd(a, b)) * b;
					assertEquals(lcm, res[1]);
					assertEquals(ans % lcm, res[0]);
				}
				// swap a,b
				long tmp = a;
				a = b;
				b = tmp;
			}
		}
		long[] factorInf = { 49, 73, 127, 337, 92737, 649657 };
		do {
			for (long ans : pred) {
				int jr = 0, jm = 0;
				long[] r = new long[factorInf.length], m = new long[factorInf.length];
				for (long f : factorInf) {
					r[jr++] = ans % f;
					m[jm++] = f;
				}
				long[] res = AtcoderMath.crt(r, m);
				assertEquals(ans % INF, res[0]);
				assertEquals(INF, res[1]);
			}
		} while (nextPermutation(factorInf));
		long[] factorInfN1 = { 2, 3, 715827883, 2147483647 };
		do {
			for (long ans : pred) {
				int jr = 0, jm = 0;
				long[] r = new long[factorInfN1.length], m = new long[factorInfN1.length];
				for (long f : factorInfN1) {
					r[jr++] = ans % f;
					m[jm++] = f;
				}
				long[] res = AtcoderMath.crt(r, m);
				assertEquals(ans % (INF - 1), res[0]);
				assertEquals(INF - 1, res[1]);
			}
		} while (nextPermutation(factorInfN1));
	}

	/**
	 * c++のnext_permutationの実装
	 *
	 * @param a 入力配列
	 * @return 次の順列が存在するかどうか
	 */
	private static boolean nextPermutation(long[] a) {
		int pivotPos = -1;
		long pivot = Long.MAX_VALUE;
		for (int i = a.length - 2; i >= 0; i--) {
			if (a[i] < a[i + 1]) {
				pivotPos = i;
				pivot = a[i];
				break;
			}
		}
		if ((-1 == pivotPos) && (Long.MAX_VALUE == pivot)) {
			return false;
		}
		int left = pivotPos + 1, right = a.length - 1;
		int minPos = -1;
		long min = Long.MAX_VALUE;
		for (int i = right; i >= left; i--) {
			if ((pivot < a[i]) && (a[i] < min)) {
				min = a[i];
				minPos = i;
			}
		}

		// swap a[pivotPos], a[minPos]
		long tmp = a[pivotPos];
		a[pivotPos] = a[minPos];
		a[minPos] = tmp;
		Arrays.sort(a, left, right + 1);
		return true;
	}

	/**
	 * Barrettの実装がないため、テストしない
	 */
	@Test
	void barrett() {
	}

	/**
	 * Barrettの実装がないため、テストしない
	 */
	@Test
	void barrettBorder() {
	}

	@Test
	void isPrime() {
		assertFalse(AtcoderMath.isPrime(121));
		assertFalse(AtcoderMath.isPrime(11 * 13));
		assertTrue(AtcoderMath.isPrime(1_000_000_007));
		assertFalse(AtcoderMath.isPrime(1_000_000_008));
		assertTrue(AtcoderMath.isPrime(1_000_000_009));
		for (int i = 0; i <= 10000; i++) {
			assertEquals(isPrimeNaive(i), AtcoderMath.isPrime(i));
		}
		for (int i = 0; i <= 10000; i++) {
			int x = Integer.MAX_VALUE - i;
			assertEquals(isPrimeNaive(x), AtcoderMath.isPrime(x));
		}
	}

	@Test
	void safeMod() {
		long[] preds = new long[505];
		int j = 0;
		for (int i = 0; i <= 100; i++) {
			preds[j++] = i;
			preds[j++] = -i;
			preds[j++] = i;
			preds[j++] = Long.MIN_VALUE + i;
			preds[j++] = Long.MAX_VALUE - i;
		}

		for (long a : preds) {
			for (long b : preds) {
				if (b <= 0L) {
					continue;
				}
				BigInteger bigB = BigInteger.valueOf(b);
				long ans = BigInteger.valueOf(a).mod(bigB).add(bigB).mod(bigB).longValue();
				assertEquals(ans, AtcoderMath.safeMod(a, b));
			}
		}
	}

	@Test
	void invGcdBound() {
		long[] pred = new long[138];
		int j = 0;
		for (int i = 0; i <= 10; i++) {
			pred[j++] = i;
			pred[j++] = -i;
			pred[j++] = Long.MIN_VALUE + i;
			pred[j++] = Long.MAX_VALUE - i;

			pred[j++] = (Long.MIN_VALUE >> 1) + i;
			pred[j++] = (Long.MIN_VALUE >> 1) - i;
			pred[j++] = (Long.MAX_VALUE >> 1) + i;
			pred[j++] = (Long.MAX_VALUE >> 1) - i;

			pred[j++] = (Long.MIN_VALUE / 3) + i;
			pred[j++] = (Long.MIN_VALUE / 3) - i;
			pred[j++] = (Long.MAX_VALUE / 3) + i;
			pred[j++] = (Long.MAX_VALUE / 3) - i;
		}
		pred[j++] = 998_244_353L;
		pred[j++] = 1_000_000_007L;
		pred[j++] = 1_000_000_009L;
		pred[j++] = -998_244_353L;
		pred[j++] = -1_000_000_007L;
		pred[j++] = -1_000_000_009L;

		for (long a : pred) {
			for (long b : pred) {
				if (b <= 0L) {
					continue;
				}
				long a2 = AtcoderMath.safeMod(a, b);
				long[] eg = AtcoderMath.invGcd(a, b);
				long g = gcd(a2, b);
				assertEquals(g, eg[0]);
				assertTrue(0 <= eg[1]);
				assertTrue(eg[1] <= (b / eg[0]));
				assertEquals(g % b, BigInteger.valueOf(eg[1]).multiply(BigInteger.valueOf(a2))
						.mod(BigInteger.valueOf(b)).longValue());
			}
		}
	}

	@Test
	void primitiveRootTestNaive() {
		for (int m = 2; m <= 10000; m++) {
			if (!AtcoderMath.isPrime(m)) {
				continue;
			}
			int n = AtcoderMath.primitiveRoot(m);
			assertTrue(1 <= n);
			assertTrue(n < m);
			int x = 1;
			for (int i = 1; i <= (m - 2); i++) {
				x = (int) (((long) (x) * n) % m);
				// x == n^i
				assertNotEquals(1, x);
			}
			x = (int) (((long) (x) * n) % m);
			assertEquals(1, x);
		}
	}

	@Test
	void primitiveRootTemplateTest() {
		assertTrue(isPrimitiveRoot(2, (AtcoderMath.primitiveRoot(2))));
		assertTrue(isPrimitiveRoot(3, AtcoderMath.primitiveRoot(3)));
		assertTrue(isPrimitiveRoot(5, AtcoderMath.primitiveRoot(5)));
		assertTrue(isPrimitiveRoot(7, AtcoderMath.primitiveRoot(7)));
		assertTrue(isPrimitiveRoot(11, AtcoderMath.primitiveRoot(11)));
		assertTrue(isPrimitiveRoot(998244353, AtcoderMath.primitiveRoot(998244353)));
		assertTrue(isPrimitiveRoot(1000000007, AtcoderMath.primitiveRoot(1000000007)));

		assertTrue(isPrimitiveRoot(469762049, AtcoderMath.primitiveRoot(469762049)));
		assertTrue(isPrimitiveRoot(167772161, AtcoderMath.primitiveRoot(167772161)));
		assertTrue(isPrimitiveRoot(754974721, AtcoderMath.primitiveRoot(754974721)));
		assertTrue(isPrimitiveRoot(324013369, AtcoderMath.primitiveRoot(324013369)));
		assertTrue(isPrimitiveRoot(831143041, AtcoderMath.primitiveRoot(831143041)));
		assertTrue(isPrimitiveRoot(1685283601, AtcoderMath.primitiveRoot(1685283601)));
	}

	@Test
	void primitiveRootTest() {
		for (int i = 0; i < 1000; i++) {
			int x = Integer.MAX_VALUE - i;
			if (!AtcoderMath.isPrime(x)) {
				continue;
			}
			assertTrue(isPrimitiveRoot(x, AtcoderMath.primitiveRoot(x)));
		}
	}

	@Test
	void ceilPow2() {
		assertEquals(0, AtcoderMath.ceilPow2(0));
		assertEquals(0, AtcoderMath.ceilPow2(1));
		assertEquals(1, AtcoderMath.ceilPow2(2));
		assertEquals(2, AtcoderMath.ceilPow2(3));
		assertEquals(2, AtcoderMath.ceilPow2(4));
		assertEquals(3, AtcoderMath.ceilPow2(5));
		assertEquals(3, AtcoderMath.ceilPow2(6));
		assertEquals(3, AtcoderMath.ceilPow2(7));
		assertEquals(3, AtcoderMath.ceilPow2(8));
		assertEquals(4, AtcoderMath.ceilPow2(9));
		assertEquals(30, AtcoderMath.ceilPow2(1 << 30));
		assertEquals(31, AtcoderMath.ceilPow2((1 << 30) + 1));
		assertEquals(31, AtcoderMath.ceilPow2(Integer.MAX_VALUE));
	}

	@Test
	void bitCeil() {
		assertEquals(1, AtcoderMath.bitCeil(0));
		assertEquals(1, AtcoderMath.bitCeil(1));
		assertEquals(2, AtcoderMath.bitCeil(2));
		assertEquals(4, AtcoderMath.bitCeil(3));
		assertEquals(4, AtcoderMath.bitCeil(4));
		assertEquals(8, AtcoderMath.bitCeil(5));
		assertEquals(8, AtcoderMath.bitCeil(6));
		assertEquals(8, AtcoderMath.bitCeil(7));
		assertEquals(8, AtcoderMath.bitCeil(8));
		assertEquals(16, AtcoderMath.bitCeil(9));
		assertEquals(1 << 30, AtcoderMath.bitCeil(1 << 30));
		assertEquals(1L << 31, AtcoderMath.bitCeil((1L << 30) + 1));
		assertEquals(1L << 31, AtcoderMath.bitCeil((1L << 31) - 1));
		assertEquals(1L << 31, AtcoderMath.bitCeil((long) Integer.MAX_VALUE));
	}

	@Test
	void countrZero() {
		assertEquals(0, AtcoderMath.countrZero(1));
		assertEquals(1, AtcoderMath.countrZero(2));
		assertEquals(0, AtcoderMath.countrZero(3));
		assertEquals(2, AtcoderMath.countrZero(4));
		assertEquals(0, AtcoderMath.countrZero(5));
		assertEquals(1, AtcoderMath.countrZero(6));
		assertEquals(0, AtcoderMath.countrZero(7));
		assertEquals(3, AtcoderMath.countrZero(8));
		assertEquals(0, AtcoderMath.countrZero(9));
		assertEquals(30, AtcoderMath.countrZero(1 << 30));
		assertEquals(0, AtcoderMath.countrZero((1 << 31) - 1));
		assertEquals(31, AtcoderMath.countrZero(1 << 31));
		assertEquals(0, AtcoderMath.countrZero(Integer.MAX_VALUE));
	}

	@Test
	void countrZeroConstexpr() {
		assertEquals(0, AtcoderMath.countrZeroConstexpr(1));
		assertEquals(1, AtcoderMath.countrZeroConstexpr(2));
		assertEquals(0, AtcoderMath.countrZeroConstexpr(3));
		assertEquals(2, AtcoderMath.countrZeroConstexpr(4));
		assertEquals(0, AtcoderMath.countrZeroConstexpr(5));
		assertEquals(1, AtcoderMath.countrZeroConstexpr(6));
		assertEquals(0, AtcoderMath.countrZeroConstexpr(7));
		assertEquals(3, AtcoderMath.countrZeroConstexpr(8));
		assertEquals(0, AtcoderMath.countrZeroConstexpr(9));
		assertEquals(30, AtcoderMath.countrZeroConstexpr(1 << 30));
		assertEquals(0, AtcoderMath.countrZeroConstexpr((1 << 31) - 1));
		assertEquals(31, AtcoderMath.countrZeroConstexpr(1 << 31));
		assertEquals(0, AtcoderMath.countrZeroConstexpr(Integer.MAX_VALUE));
	}
}
