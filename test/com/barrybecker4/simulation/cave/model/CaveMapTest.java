package com.barrybecker4.simulation.cave.model;


import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for CaveMap
 */
public class CaveMapTest {

	@Test
	public void test2by2Construction() {
		CaveMap cave = new CaveMap(2, 2);
		assertEquals("2 by 2 construction", "  \n0 \n", cave.toString());
	}

	@Test
	public void test3by3Construction() {
		CaveMap cave = new CaveMap(3, 3);
		assertEquals("3 by 3 construction", "   \n0  \n 0 \n", cave.toString());
	}

	@Test
	public void test5by5Construction() {
		CaveMap cave = new CaveMap(5, 5);
		assertEquals("5 by 5 construction", " 00  \n0 00 \n  0  \n  0  \n     \n", cave.toString());
	}

	@Test
	public void test4by1Construction() {
		CaveMap cave = new CaveMap(4, 1);
		assertEquals("4 by 1 construction", " 0  \n", cave.toString());
	}

	@Test
	public void test1by4Construction() {
		CaveMap cave = new CaveMap(1, 4);
		assertEquals("1 by 4 construction", " \n0\n \n \n", cave.toString());
	}

	/** assertEquals that we get correct neighbor counts for different positions within the 5x5 cave. */
	@Test
	public void testNeighborCount() {
		CaveMap cave = new CaveMap(5, 5);

		assertEquals("count for 0,0", "7", "" + cave.neighborCount(0, 0));
		assertEquals("count for 1,0", "6", "" + cave.neighborCount(1, 0));
		assertEquals("count for 1,1", "5", "" + cave.neighborCount(1, 1));
		assertEquals("count for 4,3", "3", "" + cave.neighborCount(4, 3));
	}

	@Test
	public void testNextPhase() {
		CaveMap cave = new CaveMap(5, 5);
		cave.nextPhase();
		assertEquals("5 by 5 nextPhase", "0  00\n 0  0\n00 00\n0 0 0\n00000\n", cave.toString());
	}

}