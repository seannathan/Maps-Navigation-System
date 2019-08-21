package edu.brown.cs.nsteinb1.dijkstra;

/**
 * Edge interface for graphs.
 *
 * @param <V>
 *          vertex
 * @param <E>
 *          edge
 *
 * @author nicole
 *
 */
public interface Edge<V extends Vertex<V, E>, E extends Edge<V, E>> {

  /**
   * Get edge ID.
   *
   * @return id
   */
  String getId();

  /**
   * Returns weight of edge.
   *
   * @return weight
   */
  double getWeight();

  /**
   * Returns starting node.
   *
   * @return starting vertex
   */
  V getStart();

  /**
   * Returns end node.
   *
   * @return ending vertex
   */
  V getEnd();
}
