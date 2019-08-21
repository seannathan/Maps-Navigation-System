package edu.brown.cs.nsteinb1.dijkstra;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import edu.brown.cs.ns.maps.MapsDatabase;
import edu.brown.cs.ns.maps.Node;
import edu.brown.cs.ns.maps.Way;

public class DijkstraTest {

  @Test
  // Tests constructor.
  public void testNew() {
    Dijkstra<Node, Way> di = new Dijkstra<>();
    assertTrue(di != null);
  }

  @Test
  // Tests search.
  public void testSearch() {
    // Set up graph.
    Node node1 = new Node("/n/0", 41.2, -73.5, null);
    Node node2 = new Node("/n/1", 41.2, -73.5, null);
    Node node3 = new Node("/n/2", 41.3, -74.5, null);
    Way way1 = new Way(MapsDatabase.haversine(node1, node2), node1, node2,
        "/w/0", "Waterman St", "residential");
    Way way2 = new Way(MapsDatabase.haversine(node2, node3), node2, node3,
        "/w/1", "Thayer St", "residential");

    node1.addAdj(node2.getId(), way1);
    node2.addAdj(node1.getId(), way1);
    node2.addAdj(node3.getId(), way2);
    node3.addAdj(node2.getId(), way2);

    Set<Node> vertices = new HashSet<>();
    vertices.add(node1);
    vertices.add(node2);
    vertices.add(node2);

    Dijkstra<Node, Way> di = new Dijkstra<>();
    List<Way> path = di.search(node1, node3);

    assertEquals(path.get(0), way2);
    assertEquals(path.get(1), way1);
  }

  @Test
  // Tests shortest path is found.
  public void testShortestPath() {
    // Set up graph.
    Node node1 = new Node("/n/1", 41.2, -73.5, null);
    Node node2 = new Node("/n/2", 60.2, -33.5, null);
    Node node3 = new Node("/n/3", 41.3, -74.5, null);
    Way way1 = new Way(MapsDatabase.haversine(node1, node2), node1, node2,
        "/w/1", "Waterman St", "residential");
    Way way2 = new Way(MapsDatabase.haversine(node2, node3), node2, node3,
        "/w/2", "Thayer St", "residential");
    Way way3 = new Way(MapsDatabase.haversine(node1, node3), node1, node3,
        "/w/3", "Prospect St", "residential");

    node1.addAdj(node2.getId(), way1);
    node2.addAdj(node3.getId(), way2);
    node1.addAdj(node3.getId(), way3);

    Node node4 = new Node("/n/4", 42.1, -73.7, null);
    Way way4 = new Way(MapsDatabase.haversine(node3, node4), node3, node4,
        "/w/4", "43rd St", "residential");
    node3.addAdj(node4.getId(), way4);

    Set<Node> vertices = new HashSet<>();
    vertices.add(node1);
    vertices.add(node2);
    vertices.add(node3);
    vertices.add(node4);

    Dijkstra<Node, Way> di = new Dijkstra<>();
    List<Way> path = di.search(node1, node4);

    assertEquals(path.get(0), way4);
    assertEquals(path.get(1), way3);
  }

  @Test
  // Tests what happens if no path exists.
  public void testNoPath() {
    // Set up graph.
    Node node1 = new Node("/n/1", 41.2, -73.5, null);
    Node node2 = new Node("/n/2", 41.4, -73.5, null);
    Way way1 = new Way(MapsDatabase.haversine(node1, node2), node1, node2,
        "/w/1", "Waterman St", "residential");

    node1.addAdj(node2.getId(), way1);
    node2.addAdj(node1.getId(), way1);

    Set<Node> vertices = new HashSet<>();
    vertices.add(node1);
    vertices.add(node2);

    Node node3 = new Node("/n/3", 41.3, -74.5, null);

    Dijkstra<Node, Way> di = new Dijkstra<>();
    List<Way> path = di.search(node1, node3);

    assertTrue(path.isEmpty());

    vertices.add(node3);
    path = di.search(node1, node3);
    assertTrue(path.isEmpty());
  }

  @Test
  // Tests what happens if multiple edges exist between actors.
  public void testMultEdges() {
    // // Set up graph.
    // Node node1 = new Node("/n/1", 41.2, -73.5, null);
    // Node node2 = new Node("/n/2", 41.4, -73.5, null);
    // Way movie1 = new Way(0.3, actor1, actor2, "13", "4");
    // Way movie2 = new Way(0.1, actor1, actor2, "13", "4");
    //
    // actor1.addAdj(actor2.getId(), movie1);
    // actor2.addAdj(actor1.getId(), movie1);
    // actor1.addAdj(actor2.getId(), movie2);
    // actor2.addAdj(actor1.getId(), movie2);
    //
    // Way movie3 = new Way(0.2, actor1, actor2, "13", "4");
    // actor1.addAdj(actor2.getId(), movie3);
    // actor2.addAdj(actor1.getId(), movie3);
    //
    // Set<Node> vertices = new HashSet<>();
    // vertices.add(actor1);
    // vertices.add(actor2);
    //
    // Dijkstra<Node, Way> di = new Dijkstra<>();
    // List<Way> path = di.search(actor1, actor2);
    //
    // assertTrue(path.get(0).equals(movie2));
    // assertTrue(actor1.getWeight(actor2.getId()) == 0.1);
  }

}
