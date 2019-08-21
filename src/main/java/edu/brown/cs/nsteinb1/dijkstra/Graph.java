package edu.brown.cs.nsteinb1.dijkstra;

import com.google.common.collect.ImmutableSet;

import java.util.HashSet;
import java.util.Set;

/**
 * Graph structure used for Dijkstra.
 *
 * @author nicole
 *
 * @param <V>
 *          vertex
 * @param <E>
 *          edge
 */
public class Graph<V extends Vertex<V, E>, E extends Edge<V, E>> {

  private Set<V> vertices;

  /**
   * Takes in vertices and edges to build a graph in an adjacency list.
   *
   * @param vert
   *          set of vertices in graph
   */
  public Graph(Set<V> vert) {
    vertices = new HashSet<>(vert);
  }

  /**
   * Return vertices.
   *
   * @return vertices
   */
  public ImmutableSet<V> getVertices() {
    return ImmutableSet.<V>builder().addAll(vertices).build();
  }

}
