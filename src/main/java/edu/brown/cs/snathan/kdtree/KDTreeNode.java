package edu.brown.cs.snathan.kdtree;

import edu.brown.cs.snathan.stars.Findable;

/**
 *
 * @author Sean Nathan
 *
 * @param <T>
 *          = Object contained by KDTreeNode.
 *
 *          KDTreeNode class that encompasses the node and its functionality.
 */
public class KDTreeNode<T extends Findable> {

  private T current;
  private KDTreeNode<T> left = null;
  private KDTreeNode<T> right = null;
  private int depth;
  private double nodeDistance;

  /**
   *
   * @param currentNode
   *          = object contained by node.
   * @param nodeDepth
   *          = depth of node in tree. Constructor for KDTreeNode given only
   *          object and depth.
   */
  public KDTreeNode(T currentNode, int nodeDepth) {
    current = currentNode;
    depth = nodeDepth;
  }

  /**
   * Method to set the object of the node.
   *
   * @param entry
   *          = object given
   */
  public void setObject(T entry) {
    current = entry;
  }

  /**
   * Method that gets the object held by the node.
   *
   * @return object held by KDTreeNode
   */
  public T getObject() {
    return current;
  }

  /**
   * Method that sets the node depth.
   *
   * @param newDepth
   *          = new depth of node
   */
  public void setDepth(int newDepth) {
    depth = newDepth;
  }

  /**
   *
   * @return depth of node. Method that returns the depth of the node.
   */
  public int getDepth() {
    return depth;
  }

  /**
   * Method that adds node to right of current.
   *
   * @param newRight
   *          = node to be added to the right of current node
   * @return node to the right of current node
   */
  public KDTreeNode<T> addRight(KDTreeNode<T> newRight) {
    if (right != null) {
      System.out.println("Right child already exists for this node");
      return null;
    }
    right = newRight;
    return newRight;
  }

  /**
   * Method that adds node to left of current.
   *
   * @param newLeft
   *          = node to be added to the left of current node
   * @return node added to the left
   */
  public KDTreeNode<T> addLeft(KDTreeNode<T> newLeft) {
    if (left != null) {
      System.out.println("Left child already exists for this node");
      return null;
    }
    left = newLeft;
    return newLeft;
  }

  /**
   *
   * @return right node Method that returns right node of current node.
   */
  public KDTreeNode<T> getRight() {
    return right;
  }

  /**
   *
   * @return left node Method that returns left node of current node.
   */
  public KDTreeNode<T> getLeft() {
    return left;
  }

  /**
   * Method that returns the value of the axis coordinate.
   *
   * @param axis
   *          = axis to get coordinate value.
   * @return value of coordinate on this specific access.
   */
  public double axisValue(int axis) {
    return current.axisValue(axis);
  }

  /**
   * Method that gets the coordinates of object inside node.
   *
   * @return double array of coordinate values
   */
  public double[] getValue() {
    return (current.getValue());
  }

  /**
   * Method that gets the distance of node object from target.
   *
   * @return double of nodeDistance
   */
  public double getDistance() {
    return nodeDistance;
  }

  /**
   *
   * @param distance
   *          = distance of node from target. Method that sets node distance.
   */
  public void setDistance(double distance) {
    nodeDistance = distance;
  }

}
