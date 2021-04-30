package fenwicktree;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigInteger;

import org.junit.jupiter.api.Test;

/**
 * https://github.com/atcoder/ac-library/blob/master/test/unittest/fenwicktree_test.cpp のJava実装
 */
class FenwickTreeTest {

	@Test
	void empty() {
		// 引数なしのコンストラクターを提供しないため、テスト対象外
	}

	@Test
	void assign() {
		@SuppressWarnings("unused")
		FenwickTree fw = new FenwickTree(0);
		fw = new FenwickTree(10);
	}

	@Test
	void zero() {
		FenwickTree fw = new FenwickTree(0);
		assertEquals(0L, fw.sum(0, 0));
	}

	@Test
	void overFlowLong() {
		// Javaではunsigned long longがないため、longでテスト
		FenwickTree fw = new FenwickTree(10);
		for (int i = 0; i < 10; i++) {
			fw.add(i, (1L << 63) + i);
		}
		for (int i = 0; i <= 10; i++) {
			for (int j = i; j <= 10; j++) {
				long sum = 0;
				for (int k = i; k < j; k++) {
					sum += k;
				}
				assertEquals((1 == (j - i) % 2) ? (1L << 63) + sum : sum, fw.sum(i, j));
			}
		}
	}

	@Test
	void naiveTest() {
		for (int n = 0; n <= 50; n++) {
			FenwickTree fw = new FenwickTree(n);
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
	void Invalid() {
		assertThrows(IllegalArgumentException.class, () -> new FenwickTree(-1));
		FenwickTree fw = new FenwickTree(10);
		assertThrows(IllegalArgumentException.class, () -> fw.add(-1, 0));
		assertThrows(IllegalArgumentException.class, () -> fw.add(10, 0));
		assertThrows(IllegalArgumentException.class, () -> fw.sum(-1, 3));
		assertThrows(IllegalArgumentException.class, () -> fw.sum(3, 11));
		assertThrows(IllegalArgumentException.class, () -> fw.sum(5, 3));
	}

	@Test
	void bound() {
		// 内部実装はlongであるため、intでのテストを実施しない
	}

	@Test
	void boundLL() {
		FenwickTree fw = new FenwickTree(10);
		fw.add(3, Long.MAX_VALUE);
		fw.add(5, Long.MIN_VALUE);
		assertEquals(-1L, fw.sum(0, 10));
		assertEquals(-1L, fw.sum(3, 6));
		assertEquals(Long.MAX_VALUE, fw.sum(3, 4));
		assertEquals(Long.MIN_VALUE, fw.sum(4, 10));
	}

	@Test
	void overFlow() {
		// longで実装したため、BigIntegerとlongで確認する
		FenwickTree fw = new FenwickTree(20);
		BigInteger[] a = new BigInteger[20];
		for (int i = 0; i < 10; i++) {
			long x = Long.MAX_VALUE;
			a[i] = BigInteger.valueOf(x);
			fw.add(i, x);
		}
		for (int i = 10; i < 20; i++) {
			long x = Long.MIN_VALUE;
			a[i] = BigInteger.valueOf(x);
			fw.add(i, x);
		}
		a[5] = a[5].add(BigInteger.valueOf(11111));
		fw.add(5, 11111);

		for (int l = 0; l <= 20; l++) {
			for (int r = l; r <= 20; r++) {
				BigInteger sum = BigInteger.ZERO;
				for (int i = l; i < r; i++) {
					sum = sum.add(a[i]);
				}
				BigInteger dif = sum.subtract(BigInteger.valueOf(fw.sum(l, r)));
				assertEquals(BigInteger.ZERO, dif.mod(BigInteger.ONE.shiftLeft(64)));
			}
		}
	}
}
