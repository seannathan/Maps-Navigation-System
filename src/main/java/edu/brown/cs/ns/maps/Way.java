package edu.brown.cs.ns.maps;

import edu.brown.cs.nsteinb1.dijkstra.Edge;

/**
 * Represents way between two nodes on the map. Stores id, start and end nodes,
 * and possibly name and type.
 *
 * @author nicole
 *
 */
public class Way implements Edge<Node, Way> {

  private double weight;
  private Node start;
  private Node end;
  private String id;
  private String name;
  private boolean traversable = true;

  /**
   * Constructor for way.
   *
   * @param edgeWeight
   *          weight of edge
   * @param startNode
   *          start of way
   * @param endNode
   *          end of way
   * @param wayId
   *          the way id
   * @param wayName
   *          way name
   * @param type
   *          type of way
   */
  public Way(double edgeWeight, Node startNode, Node endNode, String wayId,
      String wayName, String type) {
    weight = edgeWeight;
    start = startNode;
    end = endNode;
    id = wayId;
    name = wayName;

    // If way type is unclassified or the empty string, it is not traversable.
    if (type.equals("unclassified") || type.isEmpty()) {
      traversable = false;
    }
  }

  /**
   * Get edge ID.
   *
   * @return id
   */
  public String getId() {
    return id;
  }

  /**
   * Get street name.
   *
   * @return name
   */
  public String getName() {
    return name;
  }

  /**
   * Returns starting node.
   *
   * @return starting vertex
   */
  public Node getStart() {
    return start;
  }

  /**
   * Returns end node.
   *
   * @return ending vertex
   */
  public Node getEnd() {
    return end;
  }

  /**
   * Returns weight of edge.
   *
   * @return weight
   */
  public double getWeight() {
    return weight;
  }

  /**
   * Returns if edge is traversable.
   *
   * @return true/false
   */
  public boolean isTraversable() {
    return traversable;
  }

}
