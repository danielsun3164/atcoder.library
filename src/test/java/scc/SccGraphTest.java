package scc;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * https://github.com/atcoder/ac-library/blob/master/test/unittest/scc_test.cpp をもとに作成
 */
class SccGraphTest {

	@Test
	void empty() {
		SccGraph graph0 = new SccGraph();
		assertArrayEquals(new List[0], graph0.scc());
		SccGraph graph1 = new SccGraph(0);
		assertArrayEquals(new List[0], graph1.scc());
	}

	@Test
	void assign() {
		@SuppressWarnings("unused")
		SccGraph graph = new SccGraph();
		graph = new SccGraph(10);
	}

	@Test
	void simple() {
		SccGraph graph = new SccGraph(2);
		graph.addEdge(0, 1);
		graph.addEdge(1, 0);
		List<Integer>[] scc = graph.scc();
		assertEquals(1, scc.length);
		assertArrayEquals(new List[] { Arrays.asList(1, 0) }, scc);
	}

	@Test
	void selfLoop() {
		SccGraph graph = new SccGraph(2);
		graph.addEdge(0, 0);
		graph.addEdge(0, 0);
		graph.addEdge(1, 1);
		List<Integer>[] scc = graph.scc();
		assertEquals(2, scc.length);
		assertArrayEquals(new List[] { Arrays.asList(1), Arrays.asList(0) }, scc);
	}

	@Test
	void invalid() {
		SccGraph graph = new SccGraph(2);
		assertThrows(IllegalArgumentException.class, () -> graph.addEdge(0, 10));
	}
}
