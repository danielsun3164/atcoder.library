package maxflow;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Random;

import org.junit.jupiter.api.Test;

import maxflow.MaxFlowGraph.Edge;

/**
 * https://github.com/atcoder/ac-library/blob/master/test/unittest/maxflow_test.cpp をもとに作成
 */
class MaxFlowGraphTest {

	@Test
	void zero() {
		new MaxFlowGraph();
		new MaxFlowGraph(0);
	}

	@Test
	void Assign() {
		@SuppressWarnings("unused")
		MaxFlowGraph g = new MaxFlowGraph();
		g = new MaxFlowGraph(10);
	}

	private void edgeEquals(Edge expect, Edge actual) {
		assertEquals(expect.from, actual.from);
		assertEquals(expect.to, actual.to);
		assertEquals(expect.cap, actual.cap);
		assertEquals(expect.flow, actual.flow);
	}

	@Test
	void simple() {
		MaxFlowGraph g = new MaxFlowGraph(4);
		assertEquals(0, g.addEdge(0, 1, 1));
		assertEquals(1, g.addEdge(0, 2, 1));
		assertEquals(2, g.addEdge(1, 3, 1));
		assertEquals(3, g.addEdge(2, 3, 1));
		assertEquals(4, g.addEdge(1, 2, 1));
		assertEquals(2, g.flow(0, 3));

		edgeEquals(new Edge(0, 1, 1, 1), g.getEdge(0));
		edgeEquals(new Edge(0, 2, 1, 1), g.getEdge(1));
		edgeEquals(new Edge(1, 3, 1, 1), g.getEdge(2));
		edgeEquals(new Edge(2, 3, 1, 1), g.getEdge(3));
		edgeEquals(new Edge(1, 2, 1, 0), g.getEdge(4));

		assertArrayEquals(new boolean[] { true, false, false, false }, g.minCut(0));
	}

	@Test
	void notSimple() {
		MaxFlowGraph g = new MaxFlowGraph(2);
		assertEquals(0, g.addEdge(0, 1, 1));
		assertEquals(1, g.addEdge(0, 1, 2));
		assertEquals(2, g.addEdge(0, 1, 3));
		assertEquals(3, g.addEdge(0, 1, 4));
		assertEquals(4, g.addEdge(0, 1, 5));
		assertEquals(5, g.addEdge(0, 0, 6));
		assertEquals(6, g.addEdge(1, 1, 7));
		assertEquals(15, g.flow(0, 1));

		edgeEquals(new Edge(0, 1, 1, 1), g.getEdge(0));
		edgeEquals(new Edge(0, 1, 2, 2), g.getEdge(1));
		edgeEquals(new Edge(0, 1, 3, 3), g.getEdge(2));
		edgeEquals(new Edge(0, 1, 4, 4), g.getEdge(3));
		edgeEquals(new Edge(0, 1, 5, 5), g.getEdge(4));

		assertArrayEquals(new boolean[] { true, false }, g.minCut(0));
	}

	@Test
	void cut() {
		MaxFlowGraph g = new MaxFlowGraph(3);
		assertEquals(0, g.addEdge(0, 1, 2));
		assertEquals(1, g.addEdge(1, 2, 1));
		assertEquals(1, g.flow(0, 2));

		edgeEquals(new Edge(0, 1, 2, 1), g.getEdge(0));
		edgeEquals(new Edge(1, 2, 1, 1), g.getEdge(1));

		assertArrayEquals(new boolean[] { true, true, false }, g.minCut(0));
	}

	@Test
	void twice() {
		MaxFlowGraph g = new MaxFlowGraph(3);
		assertEquals(0, g.addEdge(0, 1, 1));
		assertEquals(1, g.addEdge(0, 2, 1));
		assertEquals(2, g.addEdge(1, 2, 1));

		assertEquals(2, g.flow(0, 2));

		edgeEquals(new Edge(0, 1, 1, 1), g.getEdge(0));
		edgeEquals(new Edge(0, 2, 1, 1), g.getEdge(1));
		edgeEquals(new Edge(1, 2, 1, 1), g.getEdge(2));

		g.changeEdge(0, 100, 10);
		edgeEquals(new Edge(0, 1, 100, 10), g.getEdge(0));

		assertEquals(0, g.flow(0, 2));
		assertEquals(90, g.flow(0, 1));

		edgeEquals(new Edge(0, 1, 100, 100), g.getEdge(0));
		edgeEquals(new Edge(0, 2, 1, 1), g.getEdge(1));
		edgeEquals(new Edge(1, 2, 1, 1), g.getEdge(2));

		assertEquals(2, g.flow(2, 0));

		edgeEquals(new Edge(0, 1, 100, 99), g.getEdge(0));
		edgeEquals(new Edge(0, 2, 1, 0), g.getEdge(1));
		edgeEquals(new Edge(1, 2, 1, 0), g.getEdge(2));
	}

	@Test
	void bound() {
		long INF = Long.MAX_VALUE;
		MaxFlowGraph g = new MaxFlowGraph(3);
		assertEquals(0, g.addEdge(0, 1, INF));
		assertEquals(1, g.addEdge(1, 0, INF));
		assertEquals(2, g.addEdge(0, 2, INF));

		assertEquals(INF, g.flow(0, 2));

		edgeEquals(new Edge(0, 1, INF, 0), g.getEdge(0));
		edgeEquals(new Edge(1, 0, INF, 0), g.getEdge(1));
		edgeEquals(new Edge(0, 2, INF, INF), g.getEdge(2));
	}

	// Javaにはuintがないため、、実装しない
	@Test
	void boundUint() {
	}

	@Test
	void selfLoop() {
		MaxFlowGraph g = new MaxFlowGraph(3);
		assertEquals(0, g.addEdge(0, 0, 100));

		edgeEquals(new Edge(0, 0, 100, 0), g.getEdge(0));
	}

	@Test
	void invalid() {
		MaxFlowGraph g = new MaxFlowGraph(2);
		assertThrows(IllegalArgumentException.class, () -> g.flow(0, 0));
		assertThrows(IllegalArgumentException.class, () -> g.flow(0, 0, 0));
	}

	@Test
	void stress() {
		Random random = new Random();
		for (int phase = 0; phase < 10000; phase++) {
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

			MaxFlowGraph g = new MaxFlowGraph(n);
			for (int i = 0; i < m; i++) {
				int u = random.nextInt(n);
				int v = random.nextInt(n);
				int c = random.nextInt(10001);
				g.addEdge(u, v, c);
			}
			long flow = g.flow(s, t);
			int dual = 0;
			boolean[] cut = g.minCut(s);
			int[] v_flow = new int[n];
			for (Edge e : g.edges()) {
				v_flow[e.from] -= e.flow;
				v_flow[e.to] += e.flow;
				if (cut[e.from] && !cut[e.to]) {
					dual += e.cap;
				}
			}
			assertEquals(flow, dual);
			assertEquals(-flow, v_flow[s]);
			assertEquals(flow, v_flow[t]);
			for (int i = 0; i < n; i++) {
				if ((i == s) || (i == t)) {
					continue;
				}
				assertEquals(0, v_flow[i]);
			}
		}
	}
}
