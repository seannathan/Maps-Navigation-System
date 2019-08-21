package edu.brown.cs.ns.maps;

import java.sql.Connection;
import java.util.HashMap;

import com.google.common.collect.ImmutableMap;

import edu.brown.cs.nsteinb1.dijkstra.Vertex;
import edu.brown.cs.snathan.stars.Findable;

/**
 * Represents actor in a graph of bacon passing. Stores id, name, and current
 * shortest distance.
 *
 * @author nicole
 *
 */
public class Node implements Vertex<Node, Way>, Findable {

  private String id;
  private double dist;
  private HashMap<String, Way> adj = new HashMap<>();

  private double lat;
  private double lon;
  private Way prev;
  private Connection conn;

  /**
   * Constructor for Node.
   *
   * @param iden
   *          node ID
   * @param latitude
   *          latitude
   * @param longitude
   *          longitude
   * @param connection
   *          connection to database
   */
  public Node(String iden, double latitude, double longitude,
      Connection connection) {
    id = iden;
    lat = latitude;
    lon = longitude;
    dist = Double.MAX_VALUE;
    prev = null;
    conn = connection;
  }

  /**
   * Returns unique id of actor.
   *
   * @return id
   */
  public String getId() {
    return id;
  }

  /**
   * Returns current shortest distance from start node to vertex.
   *
   * @return distance
   */
  public double shortestDist() {
    return dist;
  }

  /**
   * Return previous pointer.
   *
   * @return prev
   */
  public Way getIncomingEdge() {
    return prev;
  }

  /**
   * Set previous pointer.
   *
   * @param previous
   *          incoming edge
   */
  public void setIncomingEdge(Way previous) {
    prev = previous;
  }

  /**
   * Get adjacent nodes.
   *
   * @return adjacent nodes
   */
  public ImmutableMap<String, Way> getAdj() {
    // If we want to retrieve adjacent nodes from a database, do so.
    if (conn != null && adj.isEmpty()) {
      ImmutableMap<String, Way> adjVerts = ImmutableMap.<String, Way>builder()
          .putAll(MapsDatabase.getConnections(this, conn)).build();
      return adjVerts;
    }

    return ImmutableMap.<String, Way>builder().putAll(adj).build();
  }

  /**
   * Returns weight of edge between the actors.
   *
   * @param vertex
   *          destination node
   * @return current weight of connecting edge
   */
  public double getWeight(String vertex) {
    return adj.get(vertex).getWeight();
  }

  /**
   * Add adjacent node and corresponding edge to hashmap only if edge of lesser
   * weight does not already exist.
   *
   * @param dest
   *          adj node
   * @param edge
   *          edge that connects them
   */
  public void addAdj(String dest, Way edge) {
    if (adj.containsKey(dest)) {
      if (edge.getWeight() < adj.get(dest).getWeight()) {
        adj.put(dest, edge);
      }
    } else {
      adj.put(dest, edge);
    }
  }

  /**
   * Returns latitude of node.
   *
   * @return lat
   */
  public double getLat() {
    return lat;
  }

  /**
   * Returns longitude of node.
   *
   * @return long
   */
  public double getLong() {
    return lon;
  }

  /**
   * Returns id of node.
   *
   * @return id
   */
  public String getName() {
    return id;
  }

  /**
   * Update shortest distance from start node.
   *
   * @param distance
   *          shortest distance
   */
  public void setDist(double distance) {
    dist = distance;
  }

  /**
   * Method to get lat, long of node.
   */
  @Override
  public double[] getValue() {
    return new double[] {
        getLat(), getLong()
    };
  }

  /**
   * @param axis
   *          = dimensional axis being searched.
   * @return the coordinate of the axis being passed in.
   */
  @Override
  public double axisValue(int axis) {
    double value = Integer.MAX_VALUE;
    if (axis == 0) {
      value = getLat();
    } else if (axis == 1) {
      value = getLong();
    }
    return value;
  }
}
