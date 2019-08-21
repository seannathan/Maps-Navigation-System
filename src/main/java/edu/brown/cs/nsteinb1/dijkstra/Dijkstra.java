package edu.brown.cs.nsteinb1.dijkstra;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableMap;

/**
 * Implements Dijkstra's algorithm.
 *
 * @param <E>
 *          edge
 * @param <V>
 *          vertex
 * @author nicole
 *
 */
public class Dijkstra<V extends Vertex<V, E>, E extends Edge<V, E>> {

  /**
   * Constructor.
   */
  public Dijkstra() {

  }

  /**
   * Dijsktra search.
   *
   * @param startNode
   *          starting vertex
   * @param endNode
   *          target vertex
   * @return shortest path
   */
  public List<E> search(V startNode, V endNode) {
    // Create set of vertices to visit, and a set of vertices to never visit
    // again.
    Set<V> toVisit = new HashSet<>();
    Set<String> visited = new HashSet<>();

    // Initialize start node distance with 0.
    startNode.setDist(0);
    toVisit.add(startNode);

    V u = null;

    // Keep iterating until we've visited all nodes or if we've reached the
    // target.
    while (!toVisit.isEmpty()) {
      u = lowestDistanceNode(toVisit);

      // Mark u as visited.
      toVisit.remove(u);
      visited.add(u.getId());

      // If we've found the target, stop searching.
      if (u == null || u.getId().equals(endNode.getId())) {
        break;
      } else {
        ImmutableMap<String, E> adjVerts = u.getAdj();

        // Iterate through all of u's adjacent vertices and update their
        // shortest distances.
        for (String vertId : adjVerts.keySet()) {
          V vert = adjVerts.get(vertId).getEnd();

          double altDist = u.shortestDist() + adjVerts.get(vertId).getWeight();

          // Update distance and previous pointer.
          if (altDist < vert.shortestDist() && !visited.contains(vertId)) {
            vert.setDist(altDist);

            // Set incoming edge.
            vert.setIncomingEdge(adjVerts.get(vertId));
            toVisit.add(vert);
          }
        }
      }
    }

    // Now let's return the shortest path between the start and end nodes.
    V node = u;
    List<E> returnStack = new ArrayList<>();

    if (node == null) {
      return returnStack;
    } else if (!node.getId().equals(endNode.getId())) {
      return returnStack;
    }

    // If vertex is reachable...
    if (node.getIncomingEdge() != null
        || node.getId().equals(startNode.getId())) {
      while (node.getIncomingEdge() != null) {
        E edge = node.getIncomingEdge();
        returnStack.add(edge);
        node = edge.getStart();
      }
    }

    if (!node.getId().equals(startNode.getId())) {
      return new ArrayList<>();
    }

    // Return stack of edges - last edge in stack will be start of list.
    return returnStack;

  }

  /**
   * Get lowest distance node in unvisited set.
   *
   * @param unvisited
   *          unvisited set
   * @return lowest distance node
   */
  public V lowestDistanceNode(Set<V> unvisited) {
    V lowestNode = null;
    double minDist = Double.MAX_VALUE;

    for (V vert : unvisited) {
      double newDist = vert.shortestDist();
      if (newDist < minDist) {
        minDist = newDist;
        lowestNode = vert;
      }
    }
    return lowestNode;
  }

}
