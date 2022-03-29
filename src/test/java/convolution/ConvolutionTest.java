package convolution;

import static org.junit.Assert.assertArrayEquals;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

/**
 * https://github.com/atcoder/ac-library/blob/master/test/unittest/bit_test.cpp をもとに作成
 */
class ConvolutionTest {

	private static final int MOD = 998_244_353;

	long[] convLongNaive(long[] a, long[] b) {
		int n = a.length, m = b.length;
		long[] c = new long[(n + m) - 1];
		Arrays.fill(c, 0L);
		IntStream.range(0, n).forEach(i -> IntStream.range(0, m).forEach(j -> c[i + j] += a[i] * b[j]));
		return c;
	}

	long[] convNaive(long[] a, long[] b, int v) {
		int n = a.length, m = b.length;
		long[] c = new long[(n + m) - 1];
		Arrays.fill(c, 0L);
		IntStream.range(0, n).forEach(
				i -> IntStream.range(0, m).forEach(j -> c[i + j] = Convolution.safeMod(c[i + j] + a[i] * b[j], v)));
		return c;
	}

	@Test
	void empty() {
		assertArrayEquals(new long[] {}, Convolution.convolutionLong(new long[] {}, new long[] {}));
		assertArrayEquals(new long[] {}, Convolution.convolutionLong(new long[] {}, new long[] { 1L, 2L }));
		assertArrayEquals(new long[] {}, Convolution.convolutionLong(new long[] { 1L, 2L }, new long[] {}));
		assertArrayEquals(new long[] {}, Convolution.convolutionLong(new long[] { 1L }, new long[] {}));

		assertArrayEquals(new long[] {}, Convolution.convolution(new long[] {}, new long[] {}, MOD));
		assertArrayEquals(new long[] {}, Convolution.convolution(new long[] {}, new long[] { 1L, 2L }, MOD));
	}

	@Test
	void mid() {
		int n = 1234, m = 2345;
		Random random = new Random();
		long[] a = new long[n], b = new long[m];

		IntStream.range(0, n).forEach(i -> a[i] = random.nextInt());
		IntStream.range(0, m).forEach(i -> b[i] = random.nextInt());
		assertArrayEquals(convNaive(a, b, MOD), Convolution.convolution(a, b, MOD));
	}

	@Test
	void simpleSMod() {
		Random random = new Random();
		final int MOD1 = 998_244_353;
		IntStream.range(1, 20).forEach(n -> IntStream.range(1, 20).forEach(m -> {
			long[] a = new long[n], b = new long[m];
			IntStream.range(0, n).forEach(i -> a[i] = random.nextInt());
			IntStream.range(0, m).forEach(i -> b[i] = random.nextInt());
			assertArrayEquals(convNaive(a, b, MOD1), Convolution.convolution(a, b, MOD1));
		}));
		final int MOD2 = 924_844_033;
		IntStream.range(1, 20).forEach(n -> IntStream.range(1, 20).forEach(m -> {
			long[] a = new long[n], b = new long[m];
			IntStream.range(0, n).forEach(i -> a[i] = random.nextInt());
			IntStream.range(0, m).forEach(i -> b[i] = random.nextInt());
			assertArrayEquals(convNaive(a, b, MOD2), Convolution.convolution(a, b, MOD2));
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

	@Test
	void convLL() {
		Random random = new Random();
		IntStream.range(1, 20).forEach(n -> IntStream.range(1, 20).forEach(m -> {
			long[] a = new long[n], b = new long[m];
			IntStream.range(0, n).forEach(i -> a[i] = (random.nextInt() % 1_000_000L) - 500_000L);
			IntStream.range(0, m).forEach(i -> b[i] = (random.nextInt() % 1_000_000L) - 500_000L);
			assertArrayEquals(convLongNaive(a, b), Convolution.convolutionLong(a, b));
		}));
	}

	@Test
	void convLLBound() {
		final long MOD1 = 469762049; // 2^26
		final long MOD2 = 167772161; // 2^25
		final long MOD3 = 754974721; // 2^24
		final long M2M3 = MOD2 * MOD3;
		final long M1M3 = MOD1 * MOD3;
		final long M1M2 = MOD1 * MOD2;
		IntStream.range(-1000, 1000).forEach(i -> {
			long[] a = { (0L - M1M2 - M1M3 - M2M3) + i };
			long[] b = { 1L };
			assertArrayEquals(a, Convolution.convolutionLong(a, b));
		});
		IntStream.range(0, 1000).forEach(i -> {
			long[] a = { Long.MIN_VALUE + i };
			long[] b = { 1L };
			assertArrayEquals(a, Convolution.convolutionLong(a, b));
		});
		IntStream.range(0, 1000).forEach(i -> {
			long[] a = { Long.MAX_VALUE - i };
			long[] b = { 1L };
			assertArrayEquals(a, Convolution.convolutionLong(a, b));
		});
	}

	@Test
	void conv641() {
		// 641 = 128 * 5 + 1
		final int MOD = 641;
		int n = 64, m = 65;
		Random random = new Random();
		long[] a = new long[n], b = new long[m];
		IntStream.range(0, n).forEach(i -> a[i] = random.nextInt(MOD));
		IntStream.range(0, m).forEach(i -> b[i] = random.nextInt(MOD));
		assertArrayEquals(convNaive(a, b, MOD), Convolution.convolution(a, b, MOD));
	}

	@Test
	void conv18433() {
		// 18433 = 2048 * 9 + 1
		final int MOD = 18433;
		int n = 1024, m = 1025;
		Random random = new Random();
		long[] a = new long[n], b = new long[m];
		IntStream.range(0, n).forEach(i -> a[i] = random.nextInt(MOD));
		IntStream.range(0, m).forEach(i -> b[i] = random.nextInt(MOD));
		assertArrayEquals(convNaive(a, b, MOD), Convolution.convolution(a, b, MOD));
	}

	@Test
	void conv2() {
		long[] empty = new long[] {};
		assertArrayEquals(empty, Convolution.convolution(empty, empty, 2));
	}

	@Test
	void conv257() {
		final int MOD = 257;
		int n = 128, m = 129;
		Random random = new Random();
		long[] a = new long[n], b = new long[m];
		IntStream.range(0, n).forEach(i -> a[i] = random.nextInt(MOD));
		IntStream.range(0, m).forEach(i -> b[i] = random.nextInt(MOD));
		assertArrayEquals(convNaive(a, b, MOD), Convolution.convolution(a, b, MOD));
	}

	@Test
	void conv2147483647() {
		final int MOD = 2147483647;
		int n = 1, m = 2;
		Random random = new Random();
		long[] a = new long[n], b = new long[m];
		IntStream.range(0, n).forEach(i -> a[i] = random.nextInt(MOD));
		IntStream.range(0, m).forEach(i -> b[i] = random.nextInt(MOD));
		assertArrayEquals(convNaive(a, b, MOD), Convolution.convolution(a, b, MOD));
	}

	@Test
	void conv2130706433() {
		final int MOD = 2130706433;
		int n = 1024, m = 1024;
		Random random = new Random();
		long[] a = new long[n], b = new long[m];
		IntStream.range(0, n).forEach(i -> a[i] = random.nextInt(MOD));
		IntStream.range(0, m).forEach(i -> b[i] = random.nextInt(MOD));
		assertArrayEquals(convNaive(a, b, MOD), Convolution.convolution(a, b, MOD));
	}
}
