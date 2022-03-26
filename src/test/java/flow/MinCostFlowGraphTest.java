package flow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import flow.MinCostFlowGraph.Edge;
import flow.MinCostFlowGraph.Result;

/**
 * https://github.com/atcoder/ac-library/blob/master/test/unittest/mincostflow_test.cpp をもとに作成
 */
class MinCostFlowGraphTest {

	@Test
	void zero() {
		new MinCostFlowGraph();
		new MinCostFlowGraph(0);
	}

	private void edgeEquals(Edge expect, Edge actual) {
		assertEquals(expect.from, actual.from);
		assertEquals(expect.to, actual.to);
		assertEquals(expect.cap, actual.cap);
		assertEquals(expect.flow, actual.flow);
		assertEquals(expect.cost, actual.cost);
	}

	private void resultEquals(Result expect, Result actual) {
		assertEquals(expect.cap, actual.cap);
		assertEquals(expect.cost, actual.cost);
	}

	@Test
	void simple() {
		MinCostFlowGraph g = new MinCostFlowGraph(4);
		assertEquals(0, g.addEdge(0, 1, 1, 1));
		assertEquals(1, g.addEdge(0, 2, 1, 1));
		assertEquals(2, g.addEdge(1, 3, 1, 1));
		assertEquals(3, g.addEdge(2, 3, 1, 1));
		assertEquals(4, g.addEdge(1, 2, 1, 1));

		List<Result> result = g.slope(0, 3, 10);
		assertEquals(2, result.size());
		Result[] expected = new Result[] { new Result(0, 0), new Result(2, 4) };
		IntStream.range(0, result.size()).forEach(i -> resultEquals(expected[i], result.get(i)));

		edgeEquals(new Edge(0, 1, 1, 1, 1), g.getEdge(0));
		edgeEquals(new Edge(0, 2, 1, 1, 1), g.getEdge(1));
		edgeEquals(new Edge(1, 3, 1, 1, 1), g.getEdge(2));
		edgeEquals(new Edge(2, 3, 1, 1, 1), g.getEdge(3));
		edgeEquals(new Edge(1, 2, 1, 0, 1), g.getEdge(4));
	}

	@Test
	void usage() {
		{
			MinCostFlowGraph g = new MinCostFlowGraph(2);
			g.addEdge(0, 1, 1, 2);
			resultEquals(new Result(1, 2), g.flow(0, 1));
		}
		{
			MinCostFlowGraph g = new MinCostFlowGraph(2);
			g.addEdge(0, 1, 1, 2);
			List<Result> result = g.slope(0, 1);
			assertEquals(2, result.size());
			Result[] expected = new Result[] { new Result(0, 0), new Result(1, 2) };
			IntStream.range(0, result.size()).forEach(i -> resultEquals(expected[i], result.get(i)));
		}
	}

	@Test
	void assign() {
		@SuppressWarnings("unused")
		MinCostFlowGraph g = new MinCostFlowGraph();
		g = new MinCostFlowGraph(10);
	}

	@Test
	void outOfRange() {
		MinCostFlowGraph g = new MinCostFlowGraph(10);
		assertThrows(IllegalArgumentException.class, () -> g.slope(-1, 3));
		assertThrows(IllegalArgumentException.class, () -> g.slope(3, 3));
	}

	@Test
	void selfLoop() {
		MinCostFlowGraph g = new MinCostFlowGraph(3);
		assertEquals(0, g.addEdge(0, 0, 100, 123));
		edgeEquals(new Edge(0, 0, 100, 0, 123), g.getEdge(0));
	}

	@Test
	void sameCostPaths() {
		MinCostFlowGraph g = new MinCostFlowGraph(3);
		assertEquals(0, g.addEdge(0, 1, 1, 1));
		assertEquals(1, g.addEdge(1, 2, 1, 0));
		assertEquals(2, g.addEdge(0, 2, 2, 1));
		List<Result> result = g.slope(0, 2);
		assertEquals(2, result.size());
		Result[] expected = new Result[] { new Result(0, 0), new Result(3, 3) };
		IntStream.range(0, result.size()).forEach(i -> resultEquals(expected[i], result.get(i)));
	}

	@Test
	void invalid() {
		MinCostFlowGraph g = new MinCostFlowGraph(2);
		assertThrows(IllegalArgumentException.class, () -> g.addEdge(0, 0, -1, 0));
		assertThrows(IllegalArgumentException.class, () -> g.addEdge(0, 0, 0, -1));
	}

	@Test
	void stress() {
		Random random = new Random();
		for (int phase = 0; phase < 1000; phase++) {
			int n = random.nextInt(19) + 2;
			int m = random.nextInt(100) + 1;
			int s, t;
			s = t = random.nextInt(n);
			while (s == t) {
				t = random.nextInt(n);
			}
			if (random.nextBoolean()) {
				int tmp = s;
				s = t;
				t = tmp;
			}
			MaxFlowGraph mfG = new MaxFlowGraph(n);
			MinCostFlowGraph g = new MinCostFlowGraph(n);
			for (int i = 0; i < m; i++) {
				int u = random.nextInt(n);
				int v = random.nextInt(n);
				long cap = random.nextInt(11);
				long cost = random.nextInt(10001);
				g.addEdge(u, v, cap, cost);
				mfG.addEdge(u, v, cap);
			}
			Result result = g.flow(s, t);
			assertEquals(result.cap, mfG.flow(s, t));

			long cost2 = 0;
			long[] vCap = new long[n];
			Arrays.fill(vCap, 0L);
			for (Edge e : g.edges()) {
				vCap[e.from] -= e.flow;
				vCap[e.to] += e.flow;
				cost2 += e.flow * e.cost;
			}
			assertEquals(result.cost, cost2);

			for (int i = 0; i < n; i++) {
				if (i == s) {
					assertEquals(-result.cap, vCap[i]);
				} else if (i == t) {
					assertEquals(result.cap, vCap[i]);
				} else {
					assertEquals(0L, vCap[i]);
				}
			}

			long[] dist = new long[n];
			Arrays.fill(dist, 0L);
			while (true) {
				boolean update = false;
				for (Edge e : g.edges()) {
					if (e.flow < e.cap) {
						long ndist = dist[e.from] + e.cost;
						if (ndist < dist[e.to]) {
							update = true;
							dist[e.to] = ndist;
						}
					}
					if (0L != e.flow) {
						long ndist = dist[e.to] - e.cost;
						if (ndist < dist[e.from]) {
							update = true;
							dist[e.from] = ndist;
						}
					}
				}
				if (!update) {
					break;
				}
			}
		}
	}
}
