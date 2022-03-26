package flow;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Stack;
import java.util.stream.IntStream;

/**
 * https://github.com/atcoder/ac-library/blob/master/atcoder/mincostflow.hpp をもとに作成
 */
public class MinCostFlowGraph {
	/** ノード数 */
	final int n;
	/** 各ノードからの辺の一覧 */
	private final List<Edge> edges;

	/**
	 * コンストラクター
	 */
	MinCostFlowGraph() {
		this(0);
	}

	/**
	 * コンストラクター
	 *
	 * @param n ノード数
	 */
	MinCostFlowGraph(int n) {
		super();
		this.n = n;
		edges = new ArrayList<>();
	}

	/**
	 * 辺を追加
	 *
	 * @param from 始点
	 * @param to   終点
	 * @param cap  容量
	 * @param cost コスト
	 * @return 辺のインデックス
	 */
	int addEdge(int from, int to, long cap, long cost) {
		if (!((0 <= from) && (from < n))) {
			throw new IllegalArgumentException("from is " + from);
		}
		if (!((0 <= to) && (to < n))) {
			throw new IllegalArgumentException("to is " + to);
		}
		if (!(0 <= cap)) {
			throw new IllegalArgumentException("cap is " + cap);
		}
		if (!(0 <= cost)) {
			throw new IllegalArgumentException("cost is " + cost);
		}
		int m = edges.size();
		edges.add(new Edge(from, to, cap, 0L, cost));
		return m;
	}

	/**
	 * i番目の辺を取得する
	 *
	 * @param i
	 * @return i番目の辺
	 */
	Edge getEdge(int i) {
		if (!((0 <= i) && (i < edges.size()))) {
			throw new IllegalArgumentException("i is " + i + ", edges.size() is " + edges.size());
		}
		return edges.get(i);
	}

	/**
	 * パスの一覧を取得する
	 *
	 * @return パスの一覧
	 */
	List<Edge> edges() {
		return edges;
	}

	/**
	 * s から t へ流せるだけ流し、その流量とコストを返す。
	 *
	 * @param s
	 * @param t
	 * @return 流量とコストのクラス
	 */
	Result flow(int s, int t) {
		return flow(s, t, Long.MAX_VALUE);
	}

	/**
	 * s から t へ流せるだけ流し、その流量とコストを返す。
	 *
	 * @param s
	 * @param t
	 * @param flowLimit 流量の最大値
	 * @return 流量とコストのクラス
	 */
	Result flow(int s, int t, long flowLimit) {
		List<Result> result = slope(s, t, flowLimit);
		return result.get(result.size() - 1);
	}

	/**
	 * s から t へ流せる min_cost_slope を計算する
	 *
	 * @param s
	 * @param t
	 * @return min_cost_slope
	 */
	List<Result> slope(int s, int t) {
		return slope(s, t, Long.MAX_VALUE);
	}

	/**
	 * s から t へ流せる min_cost_slope を計算する
	 *
	 * @param s
	 * @param t
	 * @param flowLimit 流量の最大値
	 * @return min_cost_slope
	 */
	List<Result> slope(int s, int t, long flowLimit) {
		if (!((0 <= s) && (s < n))) {
			throw new IllegalArgumentException("s is " + s);
		}
		if (!((0 <= t) && (t < n))) {
			throw new IllegalArgumentException("t is " + t);
		}
		if (!(s != t)) {
			throw new IllegalArgumentException("s is " + s + ", t is " + t);
		}

		int m = edges.size();
		int[] edgeIndex = new int[m];
		Arrays.fill(edgeIndex, 0);

		Csr<InternalEdge> g = calcCsr(edgeIndex);
		List<Result> result = slope(g, s, t, flowLimit);
		IntStream.range(0, m).forEach(i -> {
			InternalEdge e = g.elist[edgeIndex[i]];
			edges.get(i).flow = edges.get(i).cap - e.cap;
		});
		return result;
	}

	/**
	 * s から t へ流せる min_cost_slope を計算する
	 *
	 * @param g
	 * @param s
	 * @param t
	 * @param flowLimit 流量の最大値
	 * @return min_cost_slope
	 */
	private List<Result> slope(Csr<InternalEdge> g, int s, int t, long flowLimit) {
		long[] dual = new long[n], dist = new long[n];
		Arrays.fill(dual, 0L);
		int[] prevE = new int[n];
		boolean[] vis = new boolean[n];

		Stack<Integer> queMin = new Stack<>();
		Queue<Q> que = new PriorityQueue<>();

		long flow = 0L, cost = 0L, prevCostPerFlow = -1L;
		List<Result> result = new ArrayList<>();
		result.add(new Result(0L, 0L));
		while (flow < flowLimit) {
			if (!dualRef(g, s, t, dual, dist, prevE, vis, queMin, que)) {
				break;
			}
			long c = flowLimit - flow;
			for (int v = t; v != s; v = g.elist[prevE[v]].to) {
				c = Math.min(c, g.elist[g.elist[prevE[v]].rev].cap);
			}
			for (int v = t; v != s; v = g.elist[prevE[v]].to) {
				InternalEdge e = g.elist[prevE[v]];
				e.cap += c;
				g.elist[e.rev].cap -= c;
			}
			long d = -dual[s];
			flow += c;
			cost += c * d;
			if (prevCostPerFlow == d) {
				result.remove(result.size() - 1);
			}
			result.add(new Result(flow, cost));
			prevCostPerFlow = d;
		}
		return result;
	}

	/**
	 * Csrクラスを計算する
	 *
	 * @param edgeIndex
	 * @return Csrクラス
	 */
	private Csr<InternalEdge> calcCsr(int[] edgeIndex) {
		int m = edges.size();
		int[] degree = new int[n], redgeIndex = new int[m];
		Arrays.fill(degree, 0);
		Arrays.fill(redgeIndex, 0);
		int[] indexes = new int[2 * m];
		InternalEdge[] inEdges = new InternalEdge[m * 2];
		IntStream.range(0, m).forEach(i -> {
			Edge e = edges.get(i);
			edgeIndex[i] = degree[e.from]++;
			redgeIndex[i] = degree[e.to]++;
			indexes[i * 2] = e.from;
			inEdges[i * 2] = new InternalEdge(e.to, -1, e.cap - e.flow, e.cost);
			indexes[i * 2 + 1] = e.to;
			inEdges[i * 2 + 1] = new InternalEdge(e.from, -1, e.flow, -e.cost);
		});
		Csr<InternalEdge> g = new Csr<>(n, indexes, inEdges, InternalEdge.class);
		IntStream.range(0, m).forEach(i -> {
			Edge e = edges.get(i);
			edgeIndex[i] += g.start[e.from];
			redgeIndex[i] += g.start[e.to];
			g.elist[edgeIndex[i]].rev = redgeIndex[i];
			g.elist[redgeIndex[i]].rev = edgeIndex[i];
		});
		return g;
	}

	private boolean dualRef(Csr<InternalEdge> g, int s, int t, long[] dual, long[] dist, int[] prevE, boolean[] vis,
			Stack<Integer> queMin, Queue<Q> que) {
		Arrays.fill(dist, Long.MAX_VALUE);
		Arrays.fill(vis, false);
		queMin.clear();
		que.clear();
		dist[s] = 0L;
		queMin.add(s);
		while (!queMin.isEmpty() || !que.isEmpty()) {
			int v = (!queMin.isEmpty()) ? queMin.pop() : que.poll().to;
			if (vis[v]) {
				continue;
			}
			vis[v] = true;
			if (v == t) {
				break;
			}
			IntStream.range(g.start[v], g.start[v + 1]).forEach(i -> {
				InternalEdge e = g.elist[i];
				if (e.cap != 0L) {
					long cost = e.cost - dual[e.to] + dual[v];
					if (dist[e.to] - dist[v] > cost) {
						long distTo = dist[v] + cost;
						dist[e.to] = distTo;
						prevE[e.to] = e.rev;
						if (distTo == dist[v]) {
							queMin.add(e.to);
						} else {
							que.add(new Q(distTo, e.to));
						}
					}
				}
			});
		}
		if (!vis[t]) {
			return false;
		}
		IntStream.range(0, n).forEach(v -> {
			if (vis[v]) {
				dual[v] -= dist[t] - dist[v];
			}
		});
		return true;
	}

	/**
	 * 内部用の辺を表すクラス
	 */
	private static class InternalEdge {
		/** 終点 */
		int to;
		/** 戻る辺は宛先の何番目の辺か */
		int rev;
		/** 容量 */
		long cap;
		/** コスト */
		long cost;

		/**
		 * コンストラクター
		 *
		 * @param to
		 * @param rev
		 * @param cap
		 * @param cost
		 */
		InternalEdge(int to, int rev, long cap, long cost) {
			super();
			this.to = to;
			this.rev = rev;
			this.cap = cap;
			this.cost = cost;
		}
	}

	private static class Q implements Comparable<Q> {
		long key;
		int to;

		Q(long key, int to) {
			super();
			this.key = key;
			this.to = to;
		}

		@Override
		public int compareTo(Q q) {
			return Long.compare(key, q.key);
		}
	}

	private static class Csr<E> {
		final int[] start;
		final E[] elist;

		@SuppressWarnings("unchecked")
		Csr(int n, int[] indexes, E[] inEdges, Class<E> clazz) {
			start = new int[n + 1];
			Arrays.fill(start, 0);
			int m = inEdges.length;
			elist = (E[]) Array.newInstance(clazz, m);

			Arrays.stream(indexes).forEach(index -> start[index + 1]++);
			IntStream.rangeClosed(1, n).forEach(i -> start[i] += start[i - 1]);
			int[] counter = Arrays.copyOf(start, start.length);
			IntStream.range(0, m).forEach(i -> elist[counter[indexes[i]]++] = inEdges[i]);
		}
	}

	/**
	 * 辺を表すクラス
	 */
	static class Edge {
		/** 始点 */
		int from;
		/** 終点 */
		int to;
		/** 容量 */
		long cap;
		/** フロー */
		long flow;
		/** コスト */
		long cost;

		/**
		 * コンストラクター
		 *
		 * @param from
		 * @param to
		 * @param cap
		 * @param flow
		 * @param cost
		 */
		Edge(int from, int to, long cap, long flow, long cost) {
			super();
			this.from = from;
			this.to = to;
			this.cap = cap;
			this.flow = flow;
			this.cost = cost;
		}
	}

	/**
	 * 計算結果を表すクラス
	 */
	static class Result {
		/** 容量 */
		long cap;
		/** コスト */
		long cost;

		/**
		 * コンストラクター
		 *
		 * @param cap
		 * @param cost
		 */
		Result(long cap, long cost) {
			super();
			this.cap = cap;
			this.cost = cost;
		}
	}
}
