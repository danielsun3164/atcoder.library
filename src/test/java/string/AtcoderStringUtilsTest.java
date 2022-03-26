package string;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

/**
 * https://github.com/atcoder/ac-library/blob/master/test/unittest/string_test.cpp をもとに作成
 */
class AtcoderStringUtilsTest {

	private int[] saNaive(int[] s) {
		int n = s.length;
		return IntStream.range(0, n).boxed().sorted((l, r) -> {
			int[] ls = Arrays.copyOfRange(s, l, n);
			int[] rs = Arrays.copyOfRange(s, r, n);
			return Arrays.compare(ls, rs);
		}).mapToInt(Integer::intValue).toArray();
	}

	private int[] lcpNaive(int[] s, int[] sa) {
		int n = s.length;
		int[] lcp = new int[n - 1];
		Arrays.fill(lcp, 0);
		IntStream.range(0, n - 1).forEach(i -> {
			int l = sa[i], r = sa[i + 1];
			while (l + lcp[i] < n && r + lcp[i] < n && s[l + lcp[i]] == s[r + lcp[i]]) {
				lcp[i]++;
			}
		});
		return lcp;
	}

	private int[] zNaive(int[] s) {
		int n = s.length;
		int[] z = new int[n];
		Arrays.fill(z, 0);
		IntStream.range(0, n).forEach(i -> {
			while (i + z[i] < n && s[z[i]] == s[i + z[i]]) {
				z[i]++;
			}
		});
		return z;
	}

	@Test
	void empty() {
		int[] expected = new int[] {};
		assertArrayEquals(expected, AtcoderStringUtils.suffixArray(""));
		assertArrayEquals(expected, AtcoderStringUtils.suffixArray(new int[] {}));

		assertArrayEquals(expected, AtcoderStringUtils.zAlgorithm(""));
		assertArrayEquals(expected, AtcoderStringUtils.zAlgorithm(new int[] {}));
	}

	@Test
	void saLcpNaive() {
		IntStream.rangeClosed(1, 5).forEach(n -> {
			int m = IntStream.range(0, n).reduce(1, (s, j) -> s * 4);
			IntStream.range(0, m).forEach(f -> {
				int[] s = new int[n];
				int g = f, maxC = 0;
				for (int i = 0; i < n; i++) {
					s[i] = g % 4;
					maxC = Math.max(maxC, s[i]);
					g /= 4;
				}
				int[] sa = saNaive(s);
				assertArrayEquals(sa, AtcoderStringUtils.suffixArray(s));
				assertArrayEquals(sa, AtcoderStringUtils.suffixArray(s, maxC));
				assertArrayEquals(lcpNaive(s, sa), AtcoderStringUtils.lcpArray(s, sa));
			});
		});
		IntStream.rangeClosed(1, 10).forEach(n -> {
			int m = IntStream.range(0, n).reduce(1, (s, j) -> s * 2);
			IntStream.range(0, m).forEach(f -> {
				int[] s = new int[n];
				int g = f, maxC = 0;
				for (int i = 0; i < n; i++) {
					s[i] = g % 2;
					maxC = Math.max(maxC, s[i]);
					g /= 2;
				}
				int[] sa = saNaive(s);
				assertArrayEquals(sa, AtcoderStringUtils.suffixArray(s));
				assertArrayEquals(sa, AtcoderStringUtils.suffixArray(s, maxC));
				assertArrayEquals(lcpNaive(s, sa), AtcoderStringUtils.lcpArray(s, sa));
			});
		});
	}

	@Test
	void internalSaNaiveNaive() {
		IntStream.rangeClosed(1, 5).forEach(n -> {
			int m = IntStream.range(0, n).reduce(1, (s, j) -> s * 4);
			IntStream.range(0, m).forEach(f -> {
				int[] s = new int[n];
				int g = f;
				for (int i = 0; i < n; i++) {
					s[i] = g % 4;
					g /= 4;
				}
				assertArrayEquals(saNaive(s), AtcoderStringUtils.saNaive(s));
			});
		});
		IntStream.rangeClosed(1, 10).forEach(n -> {
			int m = IntStream.range(0, n).reduce(1, (s, j) -> s * 2);
			IntStream.range(0, m).forEach(f -> {
				int[] s = new int[n];
				int g = f;
				for (int i = 0; i < n; i++) {
					s[i] = g % 2;
					g /= 2;
				}
				assertArrayEquals(saNaive(s), AtcoderStringUtils.saNaive(s));
			});
		});
	}

	@Test
	void internalSaDoublingNaive() {
		IntStream.rangeClosed(1, 5).forEach(n -> {
			int m = IntStream.range(0, n).reduce(1, (s, j) -> s * 4);
			IntStream.range(0, m).forEach(f -> {
				int[] s = new int[n];
				int g = f;
				for (int i = 0; i < n; i++) {
					s[i] = g % 4;
					g /= 4;
				}
				assertArrayEquals(saNaive(s), AtcoderStringUtils.saDoubling(s));
			});
		});
		IntStream.rangeClosed(1, 10).forEach(n -> {
			int m = IntStream.range(0, n).reduce(1, (s, j) -> s * 2);
			IntStream.range(0, m).forEach(f -> {
				int[] s = new int[n];
				int g = f;
				for (int i = 0; i < n; i++) {
					s[i] = g % 2;
					g /= 2;
				}
				assertArrayEquals(saNaive(s), AtcoderStringUtils.saDoubling(s));
			});
		});
	}

	@Test
	void internalSaIsNaive() {
		IntStream.rangeClosed(1, 5).forEach(n -> {
			int m = IntStream.range(0, n).reduce(1, (s, j) -> s * 4);
			IntStream.range(0, m).forEach(f -> {
				int[] s = new int[n];
				int g = f, maxC = 0;
				for (int i = 0; i < n; i++) {
					s[i] = g % 4;
					maxC = Math.max(maxC, s[i]);
					g /= 4;
				}
				assertArrayEquals(saNaive(s), AtcoderStringUtils.saIs(s, maxC, -1, -1));
			});
		});
		IntStream.rangeClosed(1, 10).forEach(n -> {
			int m = IntStream.range(0, n).reduce(1, (s, j) -> s * 2);
			IntStream.range(0, m).forEach(f -> {
				int[] s = new int[n];
				int g = f, maxC = 0;
				for (int i = 0; i < n; i++) {
					s[i] = g % 2;
					maxC = Math.max(maxC, s[i]);
					g /= 2;
				}
				assertArrayEquals(saNaive(s), AtcoderStringUtils.saIs(s, maxC, -1, -1));
			});
		});
	}

	@Test
	void saAllATest() {
		IntStream.rangeClosed(1, 100).forEach(n -> {
			int[] s = new int[n];
			Arrays.fill(s, 10);
			int[] sa = saNaive(s);
			assertArrayEquals(sa, AtcoderStringUtils.suffixArray(s));
			assertArrayEquals(sa, AtcoderStringUtils.suffixArray(s, 10));
			assertArrayEquals(sa, AtcoderStringUtils.suffixArray(s, 12));
		});
	}

	@Test
	void saAllABTest() {
		IntStream.rangeClosed(1, 100).forEach(n -> {
			int[] s = IntStream.range(0, n).map(i -> 1 & i).toArray();
			int[] sa = saNaive(s);
			assertArrayEquals(sa, AtcoderStringUtils.suffixArray(s));
			assertArrayEquals(sa, AtcoderStringUtils.suffixArray(s, 3));
		});
		IntStream.rangeClosed(1, 100).forEach(n -> {
			int[] s = IntStream.range(0, n).map(i -> 1 - (1 & i)).toArray();
			int[] sa = saNaive(s);
			assertArrayEquals(sa, AtcoderStringUtils.suffixArray(s));
			assertArrayEquals(sa, AtcoderStringUtils.suffixArray(s, 3));
		});
	}

	@Test
	void sa() {
		String s = "missisippi";
		int[] sa = AtcoderStringUtils.suffixArray(s);
		List<String> answer = Arrays.asList("i", // 9
				"ippi", // 6
				"isippi", // 4
				"issisippi", // 1
				"missisippi", // 0
				"pi", // 8
				"ppi", // 7
				"sippi", // 5
				"sisippi", // 3
				"ssisippi"); // 2
		assertEquals(answer.size(), sa.length);
		IntStream.range(0, sa.length).forEach(i -> assertEquals(answer.get(i), s.substring(sa[i])));
	}

	@Test
	void saSingle() {
		int[] expected = new int[] { 0 };
		assertArrayEquals(expected, AtcoderStringUtils.suffixArray(new int[] { 0 }));
		assertArrayEquals(expected, AtcoderStringUtils.suffixArray(new int[] { -1 }));
		assertArrayEquals(expected, AtcoderStringUtils.suffixArray(new int[] { 1 }));
		assertArrayEquals(expected, AtcoderStringUtils.suffixArray(new int[] { Integer.MIN_VALUE }));
		assertArrayEquals(expected, AtcoderStringUtils.suffixArray(new int[] { Integer.MAX_VALUE }));
	}

	@Test
	void lcp() {
		String s = "aab";
		int[] sa = AtcoderStringUtils.suffixArray(s);
		assertArrayEquals(new int[] { 0, 1, 2 }, sa);
		int[] lcp = AtcoderStringUtils.lcpArray(s, sa);
		assertArrayEquals(new int[] { 1, 0 }, lcp);

		assertArrayEquals(lcp, AtcoderStringUtils.lcpArray(new int[] { 0, 0, 1 }, sa));
		assertArrayEquals(lcp, AtcoderStringUtils.lcpArray(new int[] { -100, -100, 100 }, sa));
		assertArrayEquals(lcp,
				AtcoderStringUtils.lcpArray(new int[] { Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE }, sa));
		assertArrayEquals(lcp,
				AtcoderStringUtils.lcpArray(new long[] { Long.MIN_VALUE, Long.MIN_VALUE, Long.MAX_VALUE }, sa));
		// Javaでは unsigned long long と unsigned int の実装がないため未実装
	}

	@Test
	void zAlgorithm() {
		String s = "abab";
		assertArrayEquals(new int[] { 4, 0, 2, 0 }, AtcoderStringUtils.zAlgorithm(s));
		assertArrayEquals(new int[] { 4, 0, 2, 0 }, AtcoderStringUtils.zAlgorithm(new int[] { 1, 10, 1, 10 }));
		int[] zero = new int[] { 0, 0, 0, 0, 0, 0, 0 };
		assertArrayEquals(zNaive(zero), AtcoderStringUtils.zAlgorithm(zero));
	}

	@Test
	void zNaive() {
		IntStream.rangeClosed(1, 5).forEach(n -> {
			int m = IntStream.range(0, n).reduce(1, (s, j) -> s * 4);
			IntStream.range(0, m).forEach(f -> {
				int[] s = new int[n];
				int g = f;
				for (int i = 0; i < n; i++) {
					s[i] = g % 4;
					g /= 4;
				}
				assertArrayEquals(zNaive(s), AtcoderStringUtils.zAlgorithm(s));
			});
		});
		IntStream.rangeClosed(1, 10).forEach(n -> {
			int m = IntStream.range(0, n).reduce(1, (s, j) -> s * 2);
			IntStream.range(0, m).forEach(f -> {
				int[] s = new int[n];
				int g = f;
				for (int i = 0; i < n; i++) {
					s[i] = g % 2;
					g /= 2;
				}
				assertArrayEquals(zNaive(s), AtcoderStringUtils.zAlgorithm(s));
			});
		});
	}
}
