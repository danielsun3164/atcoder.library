package dsu;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

/**
 * https://github.com/atcoder/ac-library/blob/master/test/unittest/dsu_test.cpp をもとに作成
 */
class DisjointSetUnionTest {

	@Test
	void zero() {
		DisjointSetUnion uf = new DisjointSetUnion(0);
		assertArrayEquals(new int[0][0], uf.groups(), "array is not same");
	}

	@Test
	void empty() {
		// 引数なしのコンストラクターを提供しないため、テスト対象外
	}

	@Test
	void assign() {
		@SuppressWarnings("unused")
		DisjointSetUnion uf = new DisjointSetUnion(0);
		uf = new DisjointSetUnion(10);
	}

	@Test
	void simple() {
		DisjointSetUnion uf = new DisjointSetUnion(2);
		assertFalse(uf.same(0, 1));
		int x = uf.merge(0, 1);
		assertEquals(x, uf.leader(0));
		assertEquals(x, uf.leader(1));
		assertTrue(uf.same(0, 1));
		assertEquals(2, uf.size(0));
	}

	@Test
	void line() {
		int n = 500000;
		DisjointSetUnion uf = new DisjointSetUnion(n);
		IntStream.range(0, n - 1).forEach(i -> uf.merge(i, i + 1));
		assertEquals(n, uf.size(0));
		assertEquals(1, uf.groups().length);
		assertEquals(1, uf.groupNum);
	}

	@Test
	void LineReverse() {
		int n = 500000;
		DisjointSetUnion uf = new DisjointSetUnion(n);
		for (int i = n - 2; i >= 0; i--) {
			uf.merge(i, i + 1);
		}
		assertEquals(n, uf.size(0));
		int[][] groups = uf.groups();
		assertEquals(1, groups.length);
		assertEquals(1, uf.groupNum);
		int[][] expected = new int[1][];
		expected[0] = IntStream.range(0, n).toArray();
		assertArrayEquals(expected, groups);
	}

	@Test
	void testGroups() {
		DisjointSetUnion uf = new DisjointSetUnion(3);
		uf.merge(1, 2);
		assertArrayEquals(new int[][] { { 0 }, { 1, 2 } }, uf.groups(), "array is not the same");
	}
}
