package edu.brown.cs.nsteinb1.dijkstra;

import com.google.common.collect.ImmutableMap;

/**
 * Vertex interface for graphs.
 *
 * @param <V>
 *          vertex
 * @param <E>
 *          edge
 * @author nicole
 *
 */
public interface Vertex<V extends Vertex<V, E>, E extends Edge<V, E>> {

  /**
   * Return vertex id.
   *
   * @return id
   */
  String getId();

  /**
   * Return vertex name.
   *
   * @return name
   */
  String getName();

  /**
   * Return current shortest distance from start node to vertex.
   *
   * @return distance
   */
  double shortestDist();

  /**
   * Return previous pointer.
   *
   * @return edge
   */
  E getIncomingEdge();

  /**
   * Set previous pointer.
   *
   * @param previous
   *          incoming edge
   */
  void setIncomingEdge(E previous);

  /**
   * Get adjacent nodes.
   *
   * @return adjacent nodes map
   */
  ImmutableMap<String, E> getAdj();

  /**
   * Add adjacent.
   *
   * @param dest
   *          destination
   * @param edge
   *          edge
   */
  void addAdj(String dest, E edge);

  /**
   * Set distance.
   *
   * @param distance
   *          shortest distance
   */
  void setDist(double distance);
}
