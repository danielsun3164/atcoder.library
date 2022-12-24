package flow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * https://github.com/atcoder/ac-library/blob/master/atcoder/maxflow.hpp をもとに作成
 */
public class MaxFlowGraph {
	/** ノード数 */
	final int n;
	/** 辺の一覧 */
	final List<InternalEdge>[] g;
	final List<int[]> pos;

	/**
	 * コンストラクター
	 */
	MaxFlowGraph() {
		this(0);
	}

	/**
	 * コンストラクター
	 *
	 * @param n グラフのノード数
	 */
	@SuppressWarnings("unchecked")
	MaxFlowGraph(int n) {
		super();
		this.n = n;
		g = new List[n];
		IntStream.range(0, n).forEach(i -> g[i] = new ArrayList<>());
		pos = new ArrayList<>();
	}

	/**
	 * グラフに辺を追加する
	 *
	 * @param from
	 * @param to
	 * @param cap
	 * @return 追加する前の辺の数
	 */
	int addEdge(int from, int to, long cap) {
		if (!((0 <= from) && (from < n))) {
			throw new IllegalArgumentException("from is " + from);
		}
		if (!((0 <= to) && (to < n))) {
			throw new IllegalArgumentException("to is " + to);
		}
		if (!(0L <= cap)) {
			throw new IllegalArgumentException("cap is " + cap);
		}
		int m = pos.size();
		int fromId = g[from].size(), toId = g[to].size();
		pos.add(new int[] { from, fromId });
		if (from == to) {
			toId++;
		}
		g[from].add(new InternalEdge(to, toId, cap));
		g[to].add(new InternalEdge(from, fromId, 0L));
		return m;
	}

	Edge getEdge(int i) {
		if (!((0 <= i) && (i < pos.size()))) {
			throw new IllegalArgumentException("i is " + i + ", pos.size()=" + pos.size());
		}
		int[] js = pos.get(i);
		InternalEdge e = g[js[0]].get(js[1]);
		InternalEdge re = g[e.to].get(e.rev);
		return new Edge(js[0], e.to, e.cap + re.cap, re.cap);
	}

	/**
	 * すべてのパスを取得する
	 *
	 * @return すべてのパス
	 */
	List<Edge> edges() {
		return IntStream.range(0, pos.size()).mapToObj(i -> getEdge(i)).collect(Collectors.toList());
	}

	/**
	 * 辺を変更する
	 *
	 * @param i
	 * @param newCap
	 * @param newFlow
	 */
	void changeEdge(int i, long newCap, long newFlow) {
		if (!((0 <= i) && (i < pos.size()))) {
			throw new IllegalArgumentException("i is " + i + ", pos.size()=" + pos.size());
		}
		if (!((0 <= newFlow) && (newFlow < newCap))) {
			throw new IllegalArgumentException("newCap is " + newCap + ", newFlow is " + newFlow);
		}
		int[] js = pos.get(i);
		InternalEdge e = g[js[0]].get(js[1]);
		InternalEdge re = g[e.to].get(e.rev);
		e.cap = newCap - newFlow;
		re.cap = newFlow;
	}

	long flow(int s, int t) {
		return flow(s, t, Long.MAX_VALUE);
	}

	long flow(int s, int t, long flowLimit) {
		if (!((0 <= s) && (s < n))) {
			throw new IllegalArgumentException("s is " + s);
		}
		if (!((0 <= t) && (t < n))) {
			throw new IllegalArgumentException("t is " + t);
		}
		if (!(s != t)) {
			throw new IllegalArgumentException("s is " + s + ", t is " + t);
		}

		int[] level = new int[n], iter = new int[n];
		int[] que = new int[n];

		long flow = 0;
		while (flow < flowLimit) {
			bfs(s, t, level, que);
			if (-1 == level[t]) {
				break;
			}
			Arrays.fill(iter, 0);
			long f = dfs(s, t, level, iter, flowLimit - flow);
			if (0L == f) {
				break;
			}
			flow += f;
		}
		return flow;
	}

	private void bfs(int s, int t, int[] level, int[] que) {
		Arrays.fill(level, -1);
		level[s] = 0;
		int head = 0, tail = 0;
		que[tail++] = s;
		while (head < tail) {
			int v = que[head++];
			for (InternalEdge e : g[v]) {
				if ((0 == e.cap) || (level[e.to] >= 0)) {
					continue;
				}
				level[e.to] = level[v] + 1;
				if (t == e.to) {
					return;
				}
				que[tail++] = e.to;
			}
		}
	}

	private long dfs(int s, int v, int[] level, int[] iter, long up) {
		if (v == s) {
			return up;
		}
		long res = 0;
		int levelV = level[v];
		for (int i = iter[v]; i < g[v].size(); i++) {
			InternalEdge e = g[v].get(i);
			if ((levelV <= level[e.to]) || (0 == g[e.to].get(e.rev).cap)) {
				continue;
			}
			long d = dfs(s, e.to, level, iter, Math.min(up - res, g[e.to].get(e.rev).cap));
			if (d <= 0) {
				continue;
			}
			g[v].get(i).cap += d;
			g[e.to].get(e.rev).cap -= d;
			res += d;
			if (res == up) {
				break;
			}
		}
		level[v] = n;
		return res;
	}

	/**
	 * 最小カット問題を解く
	 *
	 * @param s
	 * @return 最小カット問題の回答
	 */
	boolean[] minCut(int s) {
		if (!((0 <= s) && (s < n))) {
			throw new IllegalArgumentException("s is " + s);
		}
		boolean[] visited = new boolean[n];
		int[] que = new int[n];
		int head = 0, tail = 0;
		que[tail++] = s;
		while (head < tail) {
			int p = que[head++];
			visited[p] = true;
			for (InternalEdge e : g[p]) {
				if ((0L != e.cap) && !visited[e.to]) {
					visited[e.to] = true;
					que[tail++] = e.to;
				}
			}
		}
		return visited;
	}

	/**
	 * グラフの内部で使用する辺のクラス
	 */
	private static class InternalEdge {
		/** 宛先 */
		int to;
		/** 戻る辺は宛先の何番目の辺か */
		int rev;
		/** 容量 */
		long cap;

		InternalEdge(int to, int rev, long cap) {
			super();
			this.to = to;
			this.rev = rev;
			this.cap = cap;
		}
	}

	/**
	 * グラフのパスを表すクラス
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

		Edge(int from, int to, long cap, long flow) {
			super();
			this.from = from;
			this.to = to;
			this.cap = cap;
			this.flow = flow;
		}
	}
}
