package edu.brown.cs.ns.maps;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.google.common.collect.ImmutableMap;

public class GraphStructureTest {

  /**
   * Tests that initial node fields are as expected.
   */
  @Test
  public void testNewNode() {
    Node node = new Node("node", -32, 21.3, null);
    assertTrue(node != null);
    assertEquals(node.getId(), "node");
    assertEquals(node.getName(), "node");
    assertTrue(node.getAdj().isEmpty());
    assertTrue(node.shortestDist() == Double.MAX_VALUE);
    assertTrue(node.getIncomingEdge() == null);
  }

  /**
   * Tests adding adjacent nodes to node.
   */
  @Test
  public void testAdj() {
    Node node1 = new Node("0", 0, 0, null);
    Node node2 = new Node("1", 1, 1, null);
    Node node3 = new Node("2", 2, 2, null);

    Way way1 = new Way(0.1, node1, node2, "w1", "hhhh", "ye");
    Way way2 = new Way(0.1, node1, node3, "w1", "hhhh", "untraversable");

    assertTrue(node1.getAdj().isEmpty());

    node1.addAdj("1", way1);
    node1.addAdj("2", way2);
    node2.addAdj("0", way1);
    node3.addAdj("0", way2);

    ImmutableMap<String, Way> adj = node1.getAdj();
    assertTrue(adj.get(node2.getId()).equals(way1));
    assertTrue(adj.get(node3.getId()).equals(way2));
  }

  /**
   * Tests incoming edges.
   */
  @Test
  public void testIncoming() {
    Node node1 = new Node("0", 0, 0, null);
    Node node2 = new Node("1", 1, 1, null);
    Node node3 = new Node("2", 2, 2, null);

    Way way1 = new Way(0.1, node1, node2, "w1", "hhhh", "ye");
    Way way2 = new Way(0.1, node2, node3, "w1", "hhhh", "untraversable");

    // Say we're looking for the way from node1 to node3.
    node2.setIncomingEdge(way1);
    node2.setDist(way1.getWeight());
    node3.setIncomingEdge(way2);
    node3.setDist(way1.getWeight() + way2.getWeight());

    assertEquals(node2.getIncomingEdge().getStart(), node1);
    assertEquals(node1.getIncomingEdge(), null);
    assertEquals(node3.getIncomingEdge().getStart(), node2);
    assertTrue(node2.shortestDist() == 0.1);
    assertTrue(node3.shortestDist() == 0.2);

  }

  /**
   * Tests that initial way fields are as expected.
   */
  @Test
  public void testNewWay() {
    Node node1 = new Node("node1", -32, 21.3, null);
    Node node2 = new Node("node2", -32, 21.4, null);
    Way way = new Way(MapsDatabase.haversine(node1, node2), node1, node2, "way",
        "Waterman St", "hi");
    assertTrue(way != null);
    assertTrue(way.getWeight() >= 943);
    assertTrue(way.getStart() == node1);
    assertTrue(way.getEnd() == node2);
    assertEquals(way.getId(), "way");
    assertEquals(way.getName(), "Waterman St");
  }
}
