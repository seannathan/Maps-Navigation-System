package edu.brown.cs.ns.maps;

import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.util.HashMap;

import org.junit.Test;

public class MapsDatabaseTest {

  /**
   * Tests that database returns correct connection.
   */
  @Test
  public void testLoadConnection() {
    // Loading in nonexistent file returns null.
    assertTrue(MapsDatabase.load("map lala.sqlite3".split(" ")) == null);

    // Loading in empty file...
    assertTrue(
        MapsDatabase.load("map data/bacon/empty.sqlite3".split(" ")) == null);

    // Loading in file with wrong tables.
    assertTrue(MapsDatabase
        .load("map data/bacon/smallBacon.sqlite3".split(" ")) == null);

    // Loading in good file.
    assertTrue(MapsDatabase
        .load("map data/maps/smallMaps.sqlite3".split(" ")) != null);
  }

  /**
   * Tests that street corpus loads correctly.
   */
  @Test
  public void testLoadStreets() {
    Connection conn = MapsDatabase
        .load("map data/maps/smallMaps.sqlite3".split(" "));
    MapsDatabase.loadStreetCorpus(conn);

    File streets = new File("data/maps/streets.txt");
    assertTrue(streets.exists());

    try (BufferedReader br = new BufferedReader(new FileReader(streets))) {
      assertTrue(br.readLine().equals("Chihiro Ave"));
    } catch (Exception f) {
      return;
    }

    // Make sure not written to the end - should be overwritten.
    Connection conn2 = MapsDatabase
        .load("mdb data/maps/maps.sqlite3".split(" "));
    MapsDatabase.loadStreetCorpus(conn2);

    try (BufferedReader br = new BufferedReader(new FileReader(streets))) {
      assertTrue(br.readLine().equals(""));
    } catch (Exception f) {
      return;
    }
  }

  /**
   * Tests that getting connections works correctly.
   */
  @Test
  public void testGetConnections() {
    Connection conn = MapsDatabase
        .load("map data/maps/smallMaps.sqlite3".split(" "));
    Node node = new Node("/n/0", 41.82, -71.4, conn);
    HashMap<String, Way> conns = MapsDatabase.getConnections(node, conn);
    assertTrue(conns.containsKey("/n/1"));
    assertTrue(conns.get("/n/1").getName().equals("Chihiro Ave"));

    // No connections are returned with nonexistent node.
    node = new Node("hiya", 120, 32, conn);
    conns = MapsDatabase.getConnections(node, conn);
    assertTrue(conns.isEmpty());
  }

  /**
   * Tests that correct node is returned given lat and long.
   */
  @Test
  public void testGetNode() {
    Connection conn = MapsDatabase
        .load("map data/maps/smallMaps.sqlite3".split(" "));
    Node node = MapsDatabase.getNode(conn, 41.82, -71.4);
    assertTrue(node.getId().equals("/n/0"));

    node = MapsDatabase.getNode(conn, 4, 30);
    assertTrue(node == null);
  }

  /**
   * Tests haversine distance.
   */
  @Test
  public void testHaversine() {
    Connection conn = MapsDatabase
        .load("map data/maps/smallMaps.sqlite3".split(" "));
    Node node = MapsDatabase.getNode(conn, 41.82, -71.4);
    Node node2 = MapsDatabase.getNode(conn, 41.8203, -71.4);

    double dist = MapsDatabase.haversine(node, node2);
    assert (dist > 33);
    assert (dist < 34);
  }

  /**
   * Tests getIntersection.
   */
  @Test
  public void testGetIntersection() {
    Connection conn = MapsDatabase
        .load("map data/maps/smallMaps.sqlite3".split(" "));

    Node node = MapsDatabase.getIntersection(conn, "Chihiro Ave",
        "Radish Spirit Blvd");
    assertTrue(node.getId().equals("/n/0"));

    node = MapsDatabase.getIntersection(conn, "Radish Spirit Blvd",
        "Kamaji Pl");
    assertTrue(node == null);

  }

}
