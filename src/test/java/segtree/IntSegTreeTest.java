package segtree;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.function.IntBinaryOperator;
import java.util.function.IntPredicate;
import java.util.function.IntSupplier;

import org.junit.jupiter.api.Test;

/**
 * https://github.com/atcoder/ac-library/blob/master/test/unittest/segtree_test.cpp をもとに作成
 */
class IntSegTreeTest {

	/** 0 */
	private static final int DEFAULT_VALUE = 0;

	/**
	 * テスト用クラス
	 */
	private abstract static class IntNaiveSegTree {
		final int n;
		final int[] d;
		final IntBinaryOperator op;
		final IntSupplier e;

		IntNaiveSegTree(int n, IntBinaryOperator op, IntSupplier e) {
			this.n = n;
			this.op = op;
			this.e = e;
			d = new int[n];
			Arrays.fill(d, e.getAsInt());
		}

		void set(int p, int x) {
			d[p] = x;
		}

		@SuppressWarnings("unused")
		int get(int p) {
			return d[p];
		}

		int prod(int l, int r) {
			int sum = e.getAsInt();
			for (int i = l; i < r; i++) {
				sum = op.applyAsInt(sum, d[i]);
			}
			return sum;
		}

		@SuppressWarnings("unused")
		int allProd() {
			return prod(0, n);
		}

		int maxRight(int l, IntPredicate f) {
			if (!f.test(e.getAsInt())) {
				throw new IllegalArgumentException("f.test(e()) is " + f.test(e.getAsInt()));
			}
			int sum = e.getAsInt();
			for (int i = l; i < n; i++) {
				sum = op.applyAsInt(sum, d[i]);
				if (!f.test(sum)) {
					return i;
				}
			}
			return n;
		}

		int minLeft(int r, IntPredicate f) {
			if (!f.test(e.getAsInt())) {
				throw new IllegalArgumentException("f.test(e()) is " + f.test(e.getAsInt()));
			}
			int sum = e.getAsInt();
			for (int i = r - 1; i >= 0; i--) {
				sum = op.applyAsInt(d[i], sum);
				if (!f.test(sum)) {
					return i + 1;
				}
			}
			return 0;
		}
	}

	private static IntBinaryOperator OP = (a, b) -> a + b;
	private static IntSupplier E = () -> DEFAULT_VALUE;

	private static class Seg extends IntSegTree {
		Seg() {
			super(OP, E);
		}

		Seg(int n) {
			super(n, OP, E);
		}
	}

	private static class NaiveSeg extends IntNaiveSegTree {
		NaiveSeg(int n) {
			super(n, OP, E);
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
		s.set(0, 111);
		assertEquals(111, s.get(0));
		assertEquals(DEFAULT_VALUE, s.prod(0, 0));
		assertEquals(111, s.prod(0, 1));
		assertEquals(DEFAULT_VALUE, s.prod(1, 1));
	}

	@Test
	void compareNaive() {
		for (int n = 0; n < 30; n++) {
			NaiveSeg seg0 = new NaiveSeg(n);
			Seg seg1 = new Seg(n);
			for (int i = 0; i < n; i++) {
				seg0.set(i, i);
				seg1.set(i, i);
			}

			for (int l = 0; l <= n; l++) {
				for (int r = l; r <= n; r++) {
					assertEquals(seg0.prod(l, r), seg1.prod(l, r));
				}
			}

			for (int l = 0; l <= n; l++) {
				for (int r = l; r <= n; r++) {
					int y = seg1.prod(l, r);
					IntPredicate predicate = new IntPredicate() {
						@Override
						public boolean test(int x) {
							return x <= y;
						}
					};
					assertEquals(seg0.maxRight(l, predicate), seg1.maxRight(l, predicate));
					assertEquals(seg0.maxRight(l, predicate), seg1.maxRight(l, x -> x <= y));
				}
			}

			for (int r = 0; r <= n; r++) {
				for (int l = 0; l <= r; l++) {
					int y = seg1.prod(l, r);
					IntPredicate predicate = new IntPredicate() {
						@Override
						public boolean test(int x) {
							return x <= y;
						}
					};
					assertEquals(seg0.minLeft(r, predicate), seg1.minLeft(r, predicate));
					assertEquals(seg0.minLeft(r, predicate), seg1.minLeft(r, x -> x <= y));
				}
			}
		}
	}
}
