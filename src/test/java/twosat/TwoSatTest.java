package twosat;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

/**
 * https://github.com/atcoder/ac-library/blob/master/test/unittest/scc_test.cpp をもとに作成
 */
class TwoSatTest {

	@Test
	void empty() {
		TwoSat ts0 = new TwoSat();
		assertTrue(ts0.satisfiable());
		assertArrayEquals(new boolean[0], ts0.answer);
		TwoSat ts1 = new TwoSat();
		assertTrue(ts1.satisfiable());
		assertArrayEquals(new boolean[0], ts1.answer);
	}

	@Test
	void one1() {
		TwoSat ts = new TwoSat(1);
		ts.addClause(0, true, 0, true);
		ts.addClause(0, false, 0, false);
		assertFalse(ts.satisfiable());
	}

	@Test
	void one2() {
		TwoSat ts = new TwoSat(1);
		ts.addClause(0, true, 0, true);
		assertTrue(ts.satisfiable());
		assertArrayEquals(new boolean[] { true }, ts.answer);
	}

	@Test
	void one3() {
		TwoSat ts = new TwoSat(1);
		ts.addClause(0, false, 0, false);
		assertTrue(ts.satisfiable());
		assertArrayEquals(new boolean[] { false }, ts.answer);
	}

	@Test
	void assign() {
		@SuppressWarnings("unused")
		TwoSat ts;
		ts = new TwoSat(10);
	}

	private static final Random random = new Random();

	@Test
	void stressOk() {
		for (int phase = 0; phase < 10000; phase++) {
			int n = random.nextInt(20) + 1;
			int m = random.nextInt(100) + 1;
			boolean[] expect = new boolean[n];
			IntStream.range(0, n).forEach(i -> expect[i] = random.nextBoolean());
			TwoSat ts = new TwoSat(n);
			int[] xs = new int[m], ys = new int[m], types = new int[m];
			IntStream.range(0, m).forEach(i -> {
				int x = xs[i] = random.nextInt(n);
				int y = ys[i] = random.nextInt(n);
				int type = types[i] = random.nextInt(3);
				if (0 == type) {
					ts.addClause(x, expect[x], y, expect[y]);
				} else if (1 == type) {
					ts.addClause(x, !expect[x], y, expect[y]);
				} else {
					ts.addClause(x, expect[x], y, !expect[y]);
				}
			});
			assertTrue(ts.satisfiable());
			boolean[] actual = ts.answer;
			for (int i = 0; i < m; i++) {
				int x = xs[i], y = ys[i], type = types[i];
				if (0 == type) {
					assertTrue((actual[x] == expect[x]) || (actual[y] == expect[y]));
				} else if (1 == type) {
					assertTrue((actual[x] != expect[x]) || (actual[y] == expect[y]));
				} else {
					assertTrue((actual[x] == expect[x]) || (actual[y] != expect[y]));
				}
			}
		}
	}
}
