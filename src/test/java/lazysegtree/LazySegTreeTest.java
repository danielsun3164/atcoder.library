package lazysegtree;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

/**
 * https://github.com/atcoder/ac-library/blob/master/test/unittest/lazysegtree_test.cpp
 * https://github.com/atcoder/ac-library/blob/master/test/unittest/lazysegtree_stress_test.cpp をもとに作成
 */
class LazySegTreeTest {

	/** テスト用デフォルト値 */
	private static final int DEFAULT_VALUE = -1_000_000_000;

	/**
	 * テスト用クラス
	 */
	private static class ArraySegTree extends LazySegTree<Integer, Integer> {
		public ArraySegTree() {
			super();
		}

		public ArraySegTree(int n) {
			super(n);
		}

		public ArraySegTree(Integer[] v) {
			super(v);
		}

		@Override
		Integer op(Integer a, Integer b) {
			return Math.max(a, b);
		}

		@Override
		Integer e() {
			return DEFAULT_VALUE;
		}

		@Override
		Integer mapping(Integer a, Integer b) {
			return a + b;
		}

		@Override
		Integer composition(Integer a, Integer b) {
			return a + b;
		}

		@Override
		Integer id() {
			return 0;
		}
	}

	@Test
	void zero() {
		LazySegTree<Integer, Integer> s = new ArraySegTree(0);
		assertEquals(DEFAULT_VALUE, s.allProd());

		s = new ArraySegTree();
		assertEquals(DEFAULT_VALUE, s.allProd());

		s = new ArraySegTree(10);
		assertEquals(DEFAULT_VALUE, s.allProd());
	}

	@Test
	void assign() {
		@SuppressWarnings("unused")
		LazySegTree<Integer, Integer> seg0 = new ArraySegTree();
		seg0 = new ArraySegTree(10);
	}

	@Test
	void invalid() {
		assertThrows(IllegalArgumentException.class, () -> new ArraySegTree(-1));
		LazySegTree<Integer, Integer> s = new ArraySegTree(10);
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
			LazySegTree<Integer, Integer> seg = new ArraySegTree(n);
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
		Integer[] v = new Integer[10];
		Arrays.fill(v, 0);
		LazySegTree<Integer, Integer> seg = new ArraySegTree(v);
		assertEquals(0, seg.allProd());
		seg.apply(0, 3, 5);
		assertEquals(5, seg.allProd());
		seg.apply(2, -10);
		assertEquals(-5, seg.prod(2, 3));
		assertEquals(0, seg.prod(2, 4));
	}

	private static class TimeManager {
		int[] v;

		TimeManager(int n) {
			v = new int[n];
			Arrays.fill(v, -1);
		}

		void action(int l, int r, int time) {
			Arrays.fill(v, l, r, time);
		}

		int prod(int l, int r) {
			return IntStream.range(l, r).map(i -> v[i]).max().orElse(-1);
		}
	}

	private static class S {
		int l, r, time;

		S(int l, int r, int time) {
			super();
			this.l = l;
			this.r = r;
			this.time = time;
		}
	}

	private static class T {
		int newTime;

		T(int newTime) {
			super();
			this.newTime = newTime;
		}
	}

	private static class TestSegTree extends LazySegTree<S, T> {

		TestSegTree(int n) {
			super(n);
		}

		@Override
		S op(S l, S r) {
			if (-1 == l.l) {
				return r;
			}
			if (-1 == r.l) {
				return l;
			}
			if (!(l.r == r.l)) {
				throw new IllegalArgumentException("l.r=" + l.r + ", r.l=" + r.l);
			}
			return new S(l.l, r.r, Math.max(l.time, r.time));
		}

		@Override
		S e() {
			return new S(-1, -1, -1);
		}

		@Override
		S mapping(T l, S r) {
			if (-1 == l.newTime) {
				return r;
			}
			if (!(r.time < l.newTime)) {
				throw new IllegalArgumentException("r.time=" + r.time + ", l.newTime=" + l.newTime);
			}
			return new S(r.l, r.r, l.newTime);
		}

		@Override
		T composition(T l, T r) {
			if (-1 == l.newTime) {
				return r;
			}
			if (-1 == r.newTime) {
				return l;
			}
			if (!(l.newTime > r.newTime)) {
				throw new IllegalArgumentException("l.newTime=" + l.newTime + ", r.newTime=" + r.newTime);
			}
			return l;
		}

		@Override
		T id() {
			return new T(-1);
		}
	}

	@Test
	void nativeTest() {
		Random random = new Random();
		for (int n = 1; n <= 30; n++) {
			for (int ph = 0; ph < 10; ph++) {
				LazySegTree<S, T> seg0 = new TestSegTree(n);
				TimeManager tm = new TimeManager(n);
				for (int i = 0; i < n; i++) {
					seg0.set(i, new S(i, i + 1, -1));
				}
				int now = 0;
				for (int q = 0; q < 3000; q++) {
					int ty = random.nextInt(4);
					int a, b;
					a = b = random.nextInt(n + 1);
					while (a == b) {
						b = random.nextInt(n + 1);
					}
					int l = Math.min(a, b), r = Math.max(a, b);
					if (0 == ty) {
						S res = seg0.prod(l, r);
						assertEquals(l, res.l);
						assertEquals(r, res.r);
						assertEquals(tm.prod(l, r), res.time);
					} else if (1 == ty) {
						S res = seg0.get(l);
						assertEquals(l, res.l);
						assertEquals(l + 1, res.r);
						assertEquals(tm.prod(l, l + 1), res.time);
					} else if (2 == ty) {
						now++;
						seg0.apply(l, r, new T(now));
						tm.action(l, r, now);
					} else if (3 == ty) {
						now++;
						seg0.apply(l, new T(now));
						tm.action(l, l + 1, now);
					} else {
						fail();
					}
				}
			}
		}
	}

	@Test
	void maxRightTest() {
		Random random = new Random();
		for (int n = 1; n <= 30; n++) {
			for (int ph = 0; ph < 10; ph++) {
				LazySegTree<S, T> seg0 = new TestSegTree(n);
				TimeManager tm = new TimeManager(n);
				for (int i = 0; i < n; i++) {
					seg0.set(i, new S(i, i + 1, -1));
				}
				int now = 0;
				for (int q = 0; q < 1000; q++) {
					int ty = random.nextInt(3);
					int a, b;
					a = b = random.nextInt(n + 1);
					while (a == b) {
						b = random.nextInt(n + 1);
					}
					int l = Math.min(a, b), r = Math.max(a, b);
					if (0 == ty) {
						assertEquals(r, seg0.maxRight(l, s -> {
							if (s.l == -1) {
								return true;
							}
							if (!(s.l == l)) {
								throw new IllegalArgumentException("s.l=" + s.l + ", l=" + l);
							}
							if (!(s.time == tm.prod(l, s.r))) {
								throw new IllegalArgumentException(
										"s.time=" + s.time + ", tm.prod(l,s.r)=" + tm.prod(l, s.r));
							}
							return s.r <= r;
						}));
					} else {
						now++;
						seg0.apply(l, r, new T(now));
						tm.action(l, r, now);
					}
				}
			}
		}
	}

	@Test
	void minLeftTest() {
		Random random = new Random();
		for (int n = 1; n <= 30; n++) {
			for (int ph = 0; ph < 10; ph++) {
				LazySegTree<S, T> seg0 = new TestSegTree(n);
				TimeManager tm = new TimeManager(n);
				for (int i = 0; i < n; i++) {
					seg0.set(i, new S(i, i + 1, -1));
				}
				int now = 0;
				for (int q = 0; q < 1000; q++) {
					int ty = random.nextInt(3);
					int a, b;
					a = b = random.nextInt(n + 1);
					while (a == b) {
						b = random.nextInt(n + 1);
					}
					int l = Math.min(a, b), r = Math.max(a, b);
					if (ty == 0) {
						assertEquals(l, seg0.minLeft(r, s -> {
							if (s.l == -1) {
								return true;
							}
							if (!(s.r == r)) {
								throw new IllegalArgumentException("s.r=" + s.r + ", r=" + r);
							}
							if (!(s.time == tm.prod(s.l, r))) {
								throw new IllegalArgumentException(
										"s.time=" + s.time + ", tm.prod(s.l,r)=" + tm.prod(s.l, r));
							}
							return l <= s.l;
						}));
					} else {
						now++;
						seg0.apply(l, r, new T(now));
						tm.action(l, r, now);
					}
				}
			}
		}
	}
}
