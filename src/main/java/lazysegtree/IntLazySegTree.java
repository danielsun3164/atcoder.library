package lazysegtree;

import java.util.Arrays;
import java.util.function.IntBinaryOperator;
import java.util.function.IntSupplier;
import java.util.function.Predicate;

/**
 * https://github.com/atcoder/ac-library/blob/master/atcoder/lazysegtree.hpp を参考に作成
 *
 * int,int を使用するLazySegTree
 */
public class IntLazySegTree {

	final int n, size, log;
	final int[] d;
	final int[] lz;
	final IntBinaryOperator op, mapping, composition;
	final IntSupplier e, id;

	/**
	 * コンストラクター
	 *
	 * @param op
	 * @param e
	 * @param mapping
	 * @param composition
	 * @param id
	 */
	public IntLazySegTree(IntBinaryOperator op, IntSupplier e, IntBinaryOperator mapping, IntBinaryOperator composition,
			IntSupplier id) {
		this(0, op, e, mapping, composition, id);
	}

	/**
	 * コンストラクター
	 *
	 * @param n
	 * @param op
	 * @param e
	 * @param mapping
	 * @param composition
	 * @param id
	 */
	public IntLazySegTree(int n, IntBinaryOperator op, IntSupplier e, IntBinaryOperator mapping,
			IntBinaryOperator composition, IntSupplier id) {
		this.n = n;
		this.op = op;
		this.e = e;
		this.mapping = mapping;
		this.composition = composition;
		this.id = id;
		size = bitCeil(n);
		log = countrZero(size);
		d = new int[size << 1];
		Arrays.fill(d, e.getAsInt());
		lz = new int[size];
		Arrays.fill(lz, id.getAsInt());
		for (int i = size - 1; i >= 1; i--) {
			update(i);
		}
	}

	/**
	 * コンストラクター
	 *
	 * @param v
	 * @param op
	 * @param e
	 * @param mapping
	 * @param composition
	 * @param id
	 */
	public IntLazySegTree(int[] v, IntBinaryOperator op, IntSupplier e, IntBinaryOperator mapping,
			IntBinaryOperator composition, IntSupplier id) {
		n = v.length;
		this.op = op;
		this.e = e;
		this.mapping = mapping;
		this.composition = composition;
		this.id = id;
		size = bitCeil(n);
		log = countrZero(size);
		d = new int[size << 1];
		Arrays.fill(d, e.getAsInt());
		lz = new int[size];
		Arrays.fill(lz, id.getAsInt());
		System.arraycopy(v, 0, d, size, n);
		for (int i = size - 1; i >= 1; i--) {
			update(i);
		}
	}

	/**
	 * a[p] = x と設定する
	 *
	 * @param p
	 * @param x
	 */
	void set(int p, int x) {
		if (!(0 <= p && p < n)) {
			throw new IllegalArgumentException("p is " + p);
		}
		p += size;
		pushTo(p);
		d[p] = x;
		updateFrom(p);
	}

	/**
	 * a[p] を返す
	 *
	 * @param p
	 * @return a[p]
	 */
	int get(int p) {
		if (!(0 <= p && p < n)) {
			throw new IllegalArgumentException("p is " + p);
		}
		p += size;
		pushTo(p);
		return d[p];
	}

	/**
	 * op(a[l], ..., a[r - 1]) を、モノイドの性質を満たしていると仮定して計算する。l==r のときは e() を返す。
	 *
	 * @param l
	 * @param r
	 * @return op(a[l], ..., a[r - 1])
	 */
	int prod(int l, int r) {
		if (!(0 <= l && l <= r && r <= n)) {
			throw new IllegalArgumentException("l is " + l + ", r is " + r);
		}
		if (l == r) {
			return e.getAsInt();
		}

		l += size;
		r += size;
		for (int i = log; i >= 1; i--) {
			if (((l >> i) << i) != l) {
				push(l >> i);
			}
			if (((r >> i) << i) != r) {
				push((r - 1) >> i);
			}
		}

		int sml = e.getAsInt(), smr = e.getAsInt();
		while (l < r) {
			if ((l & 1) > 0) {
				sml = op.applyAsInt(sml, d[l++]);
			}
			if ((r & 1) > 0) {
				smr = op.applyAsInt(d[--r], smr);
			}
			l >>= 1;
			r >>= 1;
		}
		return op.applyAsInt(sml, smr);
	}

	/**
	 * op(a[0], ..., a[n-1]) を計算する。n==0 のときは e() を返す。
	 *
	 * @return op(a[0], ..., a[n-1])
	 */
	int allProd() {
		return d[1];
	}

	/**
	 * a[p] = f(a[p]) と設定する
	 *
	 * @param p
	 * @param f
	 */
	void apply(int p, int f) {
		if (!(0 <= p && p < n)) {
			throw new IllegalArgumentException("p is " + p);
		}
		p += size;
		pushTo(p);
		d[p] = mapping.applyAsInt(f, d[p]);
		updateFrom(p);
	}

	/**
	 * i = l..r-1についてa[i] = f(a[i]) と設定する
	 *
	 * @param l
	 * @param r
	 * @param f
	 */
	void apply(int l, int r, int f) {
		if (!(0 <= l && l <= r && r <= n)) {
			throw new IllegalArgumentException("l is " + l + ", r is " + r);
		}
		if (l == r) {
			return;
		}

		l += size;
		r += size;
		for (int i = log; i >= 1; i--) {
			if (((l >> i) << i) != l) {
				push(l >> i);
			}
			if (((r >> i) << i) != r) {
				push((r - 1) >> i);
			}
		}

		{
			int l2 = l, r2 = r;
			while (l2 < r2) {
				if ((l2 & 1) > 0) {
					allApply(l2++, f);
				}
				if ((r2 & 1) > 0) {
					allApply(--r2, f);
				}
				l2 >>= 1;
				r2 >>= 1;
			}
		}

		for (int i = 1; i <= log; i++) {
			if (((l >> i) << i) != l) {
				update(l >> i);
			}
			if (((r >> i) << i) != r) {
				update((r - 1) >> i);
			}
		}
	}

	/**
	 * 以下の条件を両方満たす r を(いずれか一つ)返します。<br/>
	 * r = l もしくは g(op(a[l], a[l + 1], ..., a[r - 1])) = true <br/>
	 * r = n もしくは g(op(a[l], a[l + 1], ..., a[r])) = false <br/>
	 * gが単調だとすれば、g(op(a[l], a[l + 1], ..., a[r - 1])) = true となる最大の r、と解釈することが可能です。
	 *
	 * @param l
	 * @param g
	 * @return 条件を両方満たす r を(いずれか一つ)
	 */
	int maxRight(int l, Predicate<Integer> g) {
		if (!(0 <= l && l <= n)) {
			throw new IllegalArgumentException("l is " + l);
		}
		if (!g.test(e.getAsInt())) {
			throw new IllegalArgumentException("g.test(e()) is " + g.test(e.getAsInt()));
		}
		if (l == n) {
			return n;
		}
		l += size;
		pushTo(l);
		int sm = e.getAsInt();
		do {
			while (0 == (l & 1)) {
				l >>= 1;
			}
			if (!g.test(op.applyAsInt(sm, d[l]))) {
				while (l < size) {
					push(l);
					l = (2 * l);
					if (g.test(op.applyAsInt(sm, d[l]))) {
						sm = op.applyAsInt(sm, d[l]);
						l++;
					}
				}
				return l - size;
			}
			sm = op.applyAsInt(sm, d[l]);
			l++;
		} while ((l & -l) != l);
		return n;
	}

	/**
	 * 以下の条件を両方満たす l を(いずれか一つ)返します。<br/>
	 * l = r もしくは g(op(a[l], a[l + 1], ..., a[r - 1])) = true <br/>
	 * l = 0 もしくは g(op(a[l - 1], a[l], ..., a[r - 1])) = false <br/>
	 * gが単調だとすれば、g(op(a[l], a[l + 1], ..., a[r - 1])) = true となる最小の l、と解釈することが可能です。
	 *
	 * @param r
	 * @param g
	 * @return 条件を両方満たす l を(いずれか一つ)
	 */
	int minLeft(int r, Predicate<Integer> g) {
		if (!(0 <= r && r <= n)) {
			throw new IllegalArgumentException("r is " + r);
		}
		if (!g.test(e.getAsInt())) {
			throw new IllegalArgumentException("g.test(e()) is " + g.test(e.getAsInt()));
		}
		if (0 == r) {
			return 0;
		}
		r += size;
		for (int i = log; i >= 1; i--) {
			push((r - 1) >> i);
		}
		int sm = e.getAsInt();
		do {
			r--;
			while (r > 1 && (r & 1) > 0) {
				r >>= 1;
			}
			if (!g.test(op.applyAsInt(d[r], sm))) {
				while (r < size) {
					push(r);
					r = (2 * r + 1);
					if (g.test(op.applyAsInt(d[r], sm))) {
						sm = op.applyAsInt(d[r], sm);
						r--;
					}
				}
				return r + 1 - size;
			}
			sm = op.applyAsInt(d[r], sm);
		} while ((r & -r) != r);
		return 0;
	}

	private void update(int k) {
		d[k] = op.applyAsInt(d[k << 1], d[k << 1 | 1]);
	}

	private void allApply(int k, int f) {
		d[k] = mapping.applyAsInt(f, d[k]);
		if (k < size) {
			lz[k] = composition.applyAsInt(f, lz[k]);
		}
	}

	private void push(int k) {
		allApply(k << 1, lz[k]);
		allApply(k << 1 | 1, lz[k]);
		lz[k] = id.getAsInt();
	}

	private void pushTo(int p) {
		for (int i = log; i >= 1; i--) {
			push(p >> i);
		}
	}

	private void updateFrom(int p) {
		while (p > 1) {
			p >>= 1;
			update(p);
		}
	}

	/**
	 * n以上最小の2^xの数字を計算する
	 *
	 * @param n
	 * @return n以上最小の2^xの数字
	 */
	private static int bitCeil(int n) {
		if (!(0 <= n)) {
			throw new IllegalArgumentException("n is " + n);
		}
		int x = 1;
		while (x < n) {
			x <<= 1;
		}
		return x;
	}

	/**
	 * 入力数値を2進で表した場合に、右から連続した0のビットを数える
	 *
	 * @param n 数値
	 * @return 2進で表した場合に、右から連続した0のビット
	 */
	private static int countrZero(int n) {
		return Integer.numberOfTrailingZeros(n);
	}
}
