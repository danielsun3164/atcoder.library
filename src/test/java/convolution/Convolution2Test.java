package convolution;

import static org.junit.Assert.assertArrayEquals;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

/**
 * https://github.com/atcoder/ac-library/blob/master/test/unittest/bit_test.cpp をもとに作成
 */
class Convolution2Test {

	private static final int MOD = 998_244_353;

	int[] convNaive(int[] a, int[] b, int v) {
		int n = a.length, m = b.length;
		int[] c = new int[(n + m) - 1];
		Arrays.fill(c, 0);
		IntStream.range(0, n).forEach(i -> IntStream.range(0, m)
				.forEach(j -> c[i + j] = Convolution2.safeMod(c[i + j] + a[i] * (long) b[j], v)));
		return c;
	}

	@Test
	void empty() {
		// convolutionLongがないため、実装しない
		assertArrayEquals(new int[] {}, Convolution2.convolution(new int[] {}, new int[] {}, MOD));
		assertArrayEquals(new int[] {}, Convolution2.convolution(new int[] {}, new int[] { 1, 2 }, MOD));
	}

	@Test
	void mid() {
		int n = 1234, m = 2345;
		Random random = new Random();
		int[] a = new int[n], b = new int[m];

		IntStream.range(0, n).forEach(i -> a[i] = random.nextInt());
		IntStream.range(0, m).forEach(i -> b[i] = random.nextInt());
		assertArrayEquals(convNaive(a, b, MOD), Convolution2.convolution(a, b, MOD));
	}

	@Test
	void simpleSMod() {
		Random random = new Random();
		final int MOD1 = 998_244_353;
		IntStream.range(1, 20).forEach(n -> IntStream.range(1, 20).forEach(m -> {
			int[] a = new int[n], b = new int[m];
			IntStream.range(0, n).forEach(i -> a[i] = random.nextInt());
			IntStream.range(0, m).forEach(i -> b[i] = random.nextInt());
			assertArrayEquals(convNaive(a, b, MOD1), Convolution2.convolution(a, b, MOD1));
		}));
		final int MOD2 = 924_844_033;
		IntStream.range(1, 20).forEach(n -> IntStream.range(1, 20).forEach(m -> {
			int[] a = new int[n], b = new int[m];
			IntStream.range(0, n).forEach(i -> a[i] = random.nextInt());
			IntStream.range(0, m).forEach(i -> b[i] = random.nextInt());
			assertArrayEquals(convNaive(a, b, MOD2), Convolution2.convolution(a, b, MOD2));
		}));
	}

	// 中身はsimpleSModと同じなため、省略
	@Test
	void simpleInt() {
	}

	// Javaにuintがないため、実装しない
	@Test
	void simpleUint() {
	}

	// 中身はsimpleSModと同じなため、省略
	@Test
	void simpleLL() {
	}

	// Javaにullがないため、実装しない
	@Test
	void simpleULL() {
	}

	// Javaにint128の実装がないため、実装しない
	@Test
	void simpleInt128() {
	}

	// Javaにuint128の実装がないため、実装しない
	@Test
	void simpleUInt128() {
	}

	// convolutionLong の実装がないため、実装しない
	@Test
	void convLL() {
	}

	// convolutionLong の実装がないため、実装しない
	@Test
	void convLLBound() {
	}

	@Test
	void conv641() {
		// 641 = 128 * 5 + 1
		final int MOD = 641;
		int n = 64, m = 65;
		Random random = new Random();
		int[] a = new int[n], b = new int[m];
		IntStream.range(0, n).forEach(i -> a[i] = random.nextInt(MOD));
		IntStream.range(0, m).forEach(i -> b[i] = random.nextInt(MOD));
		assertArrayEquals(convNaive(a, b, MOD), Convolution2.convolution(a, b, MOD));
	}

	@Test
	void conv18433() {
		// 18433 = 2048 * 9 + 1
		final int MOD = 18433;
		int n = 1024, m = 1025;
		Random random = new Random();
		int[] a = new int[n], b = new int[m];
		IntStream.range(0, n).forEach(i -> a[i] = random.nextInt(MOD));
		IntStream.range(0, m).forEach(i -> b[i] = random.nextInt(MOD));
		assertArrayEquals(convNaive(a, b, MOD), Convolution2.convolution(a, b, MOD));
	}

	@Test
	void conv2() {
		int[] empty = new int[] {};
		assertArrayEquals(empty, Convolution2.convolution(empty, empty, 2));
	}

	@Test
	void conv257() {
		final int MOD = 257;
		int n = 128, m = 129;
		Random random = new Random();
		int[] a = new int[n], b = new int[m];
		IntStream.range(0, n).forEach(i -> a[i] = random.nextInt(MOD));
		IntStream.range(0, m).forEach(i -> b[i] = random.nextInt(MOD));
		assertArrayEquals(convNaive(a, b, MOD), Convolution2.convolution(a, b, MOD));
	}

	@Test
	void conv2147483647() {
		final int MOD = 2147483647;
		int n = 1, m = 2;
		Random random = new Random();
		int[] a = new int[n], b = new int[m];
		IntStream.range(0, n).forEach(i -> a[i] = random.nextInt(MOD));
		IntStream.range(0, m).forEach(i -> b[i] = random.nextInt(MOD));
		assertArrayEquals(convNaive(a, b, MOD), Convolution2.convolution(a, b, MOD));
	}

	@Test
	void conv2130706433() {
		final int MOD = 2130706433;
		int n = 1024, m = 1024;
		Random random = new Random();
		int[] a = new int[n], b = new int[m];
		IntStream.range(0, n).forEach(i -> a[i] = random.nextInt(MOD));
		IntStream.range(0, m).forEach(i -> b[i] = random.nextInt(MOD));
		assertArrayEquals(convNaive(a, b, MOD), Convolution2.convolution(a, b, MOD));
	}
}
