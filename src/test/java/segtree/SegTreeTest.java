package segtree;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;

/**
 * https://github.com/atcoder/ac-library/blob/master/test/unittest/segtree_test.cpp をもとに作成
 */
class SegTreeTest {

	/** テスト用デフォルト値 */
	private static final String DEFAULT_VALUE = "$";

	/**
	 * テスト用クラス
	 */
	private abstract static class NaiveSegTree<S> {
		final int n;
		final S[] d;

		abstract S e();

		abstract S op(S a, S b);

		@SuppressWarnings("unchecked")
		public NaiveSegTree(int n) {
			this.n = n;
			d = (S[]) new Object[n];
			Arrays.fill(d, e());
		}

		void set(int p, S x) {
			d[p] = x;
		}

		@SuppressWarnings("unused")
		S get(int p) {
			return d[p];
		}

		S prod(int l, int r) {
			S sum = e();
			for (int i = l; i < r; i++) {
				sum = op(sum, d[i]);
			}
			return sum;
		}

		@SuppressWarnings("unused")
		S allProd() {
			return prod(0, n);
		}

		int maxRight(int l, Predicate<S> f) {
			if (!f.test(e())) {
				throw new IllegalArgumentException("f.test(e()) is " + f.test(e()));
			}
			S sum = e();
			for (int i = l; i < n; i++) {
				sum = op(sum, d[i]);
				if (!f.test(sum)) {
					return i;
				}
			}
			return n;
		}

		int minLeft(int r, Predicate<S> f) {
			if (!f.test(e())) {
				throw new IllegalArgumentException("f.test(e()) is " + f.test(e()));
			}
			S sum = e();
			for (int i = r - 1; i >= 0; i--) {
				sum = op(d[i], sum);
				if (!f.test(sum)) {
					return i + 1;
				}
			}
			return 0;
		}
	}

	private static class Seg extends SegTree<String> {
		Seg() {
			super();
		}

		Seg(int n) {
			super(n);
		}

		@Override
		String e() {
			return DEFAULT_VALUE;
		}

		@Override
		String op(String a, String b) {
			if (!(DEFAULT_VALUE.equals(a) || DEFAULT_VALUE.equals(b) || a.compareTo(b) <= 0)) {
				throw new IllegalArgumentException("a is " + a + ", b is " + b);
			}
			if (DEFAULT_VALUE.equals(a)) {
				return b;
			}
			if (DEFAULT_VALUE.equals(b)) {
				return a;
			}
			return a + b;
		}
	}

	private static class NaiveSeg extends NaiveSegTree<String> {
		NaiveSeg(int n) {
			super(n);
		}

		@Override
		String e() {
			return DEFAULT_VALUE;
		}

		@Override
		String op(String a, String b) {
			if (!(DEFAULT_VALUE.equals(a) || DEFAULT_VALUE.equals(b) || a.compareTo(b) <= 0)) {
				throw new IllegalArgumentException("a is " + a + ", b is " + b);
			}
			if (DEFAULT_VALUE.equals(a)) {
				return b;
			}
			if (DEFAULT_VALUE.equals(b)) {
				return a;
			}
			return a + b;
		}
	}

	@Test
	void zero() {
		Seg s = new Seg(0);
		assertEquals(DEFAULT_VALUE, s.allProd());
		s = new Seg();
		assertEquals(DEFAULT_VALUE, s.allProd());
	}

	@Test
	void assign() {
		@SuppressWarnings("unused")
		Seg seg0 = new Seg();
		seg0 = new Seg(10);
	}

	@Test
	void invalid() {
		assertThrows(IllegalArgumentException.class, () -> new Seg(-1));
		Seg s = new Seg(10);
		assertThrows(IllegalArgumentException.class, () -> s.get(-1));
		assertThrows(IllegalArgumentException.class, () -> s.get(10));

		assertThrows(IllegalArgumentException.class, () -> s.prod(-1, -1));
		assertThrows(IllegalArgumentException.class, () -> s.prod(3, 2));
		assertThrows(IllegalArgumentException.class, () -> s.prod(0, 11));
		assertThrows(IllegalArgumentException.class, () -> s.prod(-1, 11));

		assertThrows(IllegalArgumentException.class, () -> s.maxRight(11, a -> true));
		assertThrows(IllegalArgumentException.class, () -> s.minLeft(-1, a -> true));
		assertThrows(IllegalArgumentException.class, () -> s.maxRight(0, a -> false));
	}

	@Test
	void one() {
		Seg s = new Seg(1);
		assertEquals(DEFAULT_VALUE, s.allProd());
		assertEquals(DEFAULT_VALUE, s.get(0));
		assertEquals(DEFAULT_VALUE, s.prod(0, 1));
		s.set(0, "dummy");
		assertEquals("dummy", s.get(0));
		assertEquals(DEFAULT_VALUE, s.prod(0, 0));
		assertEquals("dummy", s.prod(0, 1));
		assertEquals(DEFAULT_VALUE, s.prod(1, 1));
	}

	@Test
	void compareNaive() {
		for (int n = 0; n < 30; n++) {
			NaiveSeg seg0 = new NaiveSeg(n);
			Seg seg1 = new Seg(n);
			for (int i = 0; i < n; i++) {
				String s = "" + (char) ('a' + i);
				seg0.set(i, s);
				seg1.set(i, s);
			}

			for (int l = 0; l <= n; l++) {
				for (int r = l; r <= n; r++) {
					assertEquals(seg0.prod(l, r), seg1.prod(l, r));
				}
			}

			for (int l = 0; l <= n; l++) {
				for (int r = l; r <= n; r++) {
					String y = seg1.prod(l, r);
					Predicate<String> predicate = new Predicate<>() {
						@Override
						public boolean test(String x) {
							return x.length() <= y.length();
						}
					};
					assertEquals(seg0.maxRight(l, predicate), seg1.maxRight(l, predicate));
					assertEquals(seg0.maxRight(l, predicate), seg1.maxRight(l, x -> x.length() <= y.length()));
				}
			}

			for (int r = 0; r <= n; r++) {
				for (int l = 0; l <= r; l++) {
					String y = seg1.prod(l, r);
					Predicate<String> predicate = new Predicate<>() {
						@Override
						public boolean test(String x) {
							return x.length() <= y.length();
						}
					};
					assertEquals(seg0.minLeft(r, predicate), seg1.minLeft(r, predicate));
					assertEquals(seg0.minLeft(r, predicate), seg1.minLeft(r, x -> x.length() <= y.length()));
				}
			}
		}
	}
}
