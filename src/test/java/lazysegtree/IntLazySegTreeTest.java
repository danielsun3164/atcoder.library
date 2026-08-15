package lazysegtree;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.function.IntBinaryOperator;
import java.util.function.IntSupplier;

import org.junit.jupiter.api.Test;

/**
 * https://github.com/atcoder/ac-library/blob/master/test/unittest/lazysegtree_test.cpp
 * https://github.com/atcoder/ac-library/blob/master/test/unittest/lazysegtree_stress_test.cpp をもとに作成
 */
class IntLazySegTreeTest {

	/** テスト用デフォルト値 */
	private static final int DEFAULT_VALUE = -1_000_000_000;

	/**
	 * テスト用クラス
	 */
	private static class ArraySegTree extends IntLazySegTree {
		private static final IntBinaryOperator OP = (a, b) -> Math.max(a, b);
		private static final IntSupplier E = () -> DEFAULT_VALUE;
		private static final IntBinaryOperator MAPPING = (a, b) -> a + b;
		private static final IntBinaryOperator COMPOSITION = (a, b) -> a + b;
		private static final IntSupplier ID = () -> 0;

		public ArraySegTree() {
			super(OP, E, MAPPING, COMPOSITION, ID);
		}

		public ArraySegTree(int n) {
			super(n, OP, E, MAPPING, COMPOSITION, ID);
		}

		public ArraySegTree(int[] v) {
			super(v, OP, E, MAPPING, COMPOSITION, ID);
		}
	}

	@Test
	void zero() {
		IntLazySegTree s = new ArraySegTree(0);
		assertEquals(DEFAULT_VALUE, s.allProd());

		s = new ArraySegTree();
		assertEquals(DEFAULT_VALUE, s.allProd());

		s = new ArraySegTree(10);
		assertEquals(DEFAULT_VALUE, s.allProd());
	}

	@Test
	void assign() {
		@SuppressWarnings("unused")
		IntLazySegTree seg0 = new ArraySegTree();
		seg0 = new ArraySegTree(10);
	}

	@Test
	void invalid() {
		assertThrows(IllegalArgumentException.class, () -> new ArraySegTree(-1));
		IntLazySegTree s = new ArraySegTree(10);
		assertThrows(IllegalArgumentException.class, () -> s.get(-1));
		assertThrows(IllegalArgumentException.class, () -> s.get(10));

		assertThrows(IllegalArgumentException.class, () -> s.prod(-1, -1));
		assertThrows(IllegalArgumentException.class, () -> s.prod(3, 2));
		assertThrows(IllegalArgumentException.class, () -> s.prod(0, 11));
		assertThrows(IllegalArgumentException.class, () -> s.prod(-1, 11));
	}

	@Test
	void naiveProd() {
		for (int n = 0; n <= 50; n++) {
			IntLazySegTree seg = new ArraySegTree(n);
			int[] p = new int[n];
			for (int i = 0; i < n; i++) {
				p[i] = (i * i + 100) % 31;
				seg.set(i, p[i]);
			}
			for (int l = 0; l <= n; l++) {
				for (int r = l; r <= n; r++) {
					int e = DEFAULT_VALUE;
					for (int i = l; i < r; i++) {
						e = Math.max(e, p[i]);
					}
					assertEquals(e, seg.prod(l, r));
				}
			}
		}
	}

	@Test
	void usage() {
		int[] v = new int[10];
		Arrays.fill(v, 0);
		IntLazySegTree seg = new ArraySegTree(v);
		assertEquals(0, seg.allProd());
		seg.apply(0, 3, 5);
		assertEquals(5, seg.allProd());
		seg.apply(2, -10);
		assertEquals(-5, seg.prod(2, 3));
		assertEquals(0, seg.prod(2, 4));
	}
}
