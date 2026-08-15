package fenwicktree;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * https://github.com/atcoder/ac-library/blob/master/test/unittest/fenwicktree_test.cpp をもとに作成
 */
class IntFenwickTreeTest {

	@Test
	void empty() {
		IntFenwickTree fw = new IntFenwickTree();
		assertEquals(0, fw.sum(0, 0));
		// 以降modintの実装がないため、テスト対象外
	}

	@Test
	void assign() {
		@SuppressWarnings("unused")
		IntFenwickTree fw = new IntFenwickTree(0);
		fw = new IntFenwickTree(10);
	}

	@Test
	void zero() {
		IntFenwickTree fw = new IntFenwickTree(0);
		assertEquals(0, fw.sum(0, 0));
	}

	@Test
	void overFlowInt() {
		IntFenwickTree fw = new IntFenwickTree(10);
		for (int i = 0; i < 10; i++) {
			fw.add(i, (1 << 31) + i);
		}
		for (int i = 0; i <= 10; i++) {
			for (int j = i; j <= 10; j++) {
				long sum = 0;
				for (int k = i; k < j; k++) {
					sum += k;
				}
				assertEquals((1 == ((j - i) & 1)) ? (1 << 31) + sum : sum, fw.sum(i, j));
			}
		}
	}

	@Test
	void naiveTest() {
		for (int n = 0; n <= 50; n++) {
			IntFenwickTree fw = new IntFenwickTree(n);
			for (int i = 0; i < n; i++) {
				fw.add(i, i * i);
			}
			for (int l = 0; l <= n; l++) {
				for (int r = l; r <= n; r++) {
					long sum = 0;
					for (int i = l; i < r; i++) {
						sum += i * i;
					}
					assertEquals(sum, fw.sum(l, r));
				}
			}
		}
	}

	@Test
	void sMintTest() {
		// static modint の実装がないため、テスト対象外
	}

	@Test
	void mintTest() {
		// modint の実装がないため、テスト対象外
	}

	@Test
	void invalid() {
		assertThrows(IllegalArgumentException.class, () -> new IntFenwickTree(-1));
		IntFenwickTree fw = new IntFenwickTree(10);
		assertThrows(IllegalArgumentException.class, () -> fw.add(-1, 0));
		assertThrows(IllegalArgumentException.class, () -> fw.add(10, 0));
		assertThrows(IllegalArgumentException.class, () -> fw.sum(-1, 3));
		assertThrows(IllegalArgumentException.class, () -> fw.sum(3, 11));
		assertThrows(IllegalArgumentException.class, () -> fw.sum(5, 3));
	}

	@Test
	void bound() {
		IntFenwickTree fw = new IntFenwickTree(10);
		fw.add(3, Integer.MAX_VALUE);
		fw.add(5, Integer.MIN_VALUE);
		assertEquals(-1, fw.sum(0, 10));
		assertEquals(-1, fw.sum(3, 6));
		assertEquals(Integer.MAX_VALUE, fw.sum(3, 4));
		assertEquals(Integer.MIN_VALUE, fw.sum(4, 10));
	}

	@Test
	void boundLL() {
		// 内部実装はintであるため、longでのテストを実施しない
	}

	@Test
	void overFlow() {
		// intで実装したため、longで確認する
		IntFenwickTree fw = new IntFenwickTree(20);
		long[] a = new long[20];
		for (int i = 0; i < 10; i++) {
			int x = Integer.MAX_VALUE;
			a[i] = x;
			fw.add(i, x);
		}
		for (int i = 10; i < 20; i++) {
			int x = Integer.MIN_VALUE;
			a[i] = x;
			fw.add(i, x);
		}
		a[5] += 11111;
		fw.add(5, 11111);

		for (int l = 0; l <= 20; l++) {
			for (int r = l; r <= 20; r++) {
				long sum = 0L;
				for (int i = l; i < r; i++) {
					sum += a[i];
				}
				long dif = sum - fw.sum(l, r);
				assertEquals(0L, dif % (1L << 32));
			}
		}
	}
}
