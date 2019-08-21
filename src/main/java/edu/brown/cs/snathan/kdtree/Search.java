package edu.brown.cs.snathan.kdtree;

import java.util.Comparator;
import java.util.Iterator;
import java.util.PriorityQueue;

import edu.brown.cs.snathan.stars.Findable;

/**
 *
 * @author Sean Nathan
 * @param <T>
 *          = generic object contained within KDTreeNode.
 */
public class Search<T extends Findable> {

  private double[] target;
  private PriorityQueue<KDTreeNode<T>> neighborsQ;
  private double maxDistance = Integer.MIN_VALUE;
  private KDTreeNode<T> maxDistanceNode;
  private int numDimensions;

  /**
   * @param targetCoords
   *          = coordinates of target to search. Constructor for neighbors
   *          search.
   */
  public Search(double[] targetCoords) {
    neighborsQ = new PriorityQueue<KDTreeNode<T>>(new TargetComparator());
    target = targetCoords;
    numDimensions = targetCoords.length;
  }

  /**
   *
   * @param k
   *          = number of nodes to return.
   * @param currTarget
   *          = target to search from.
   * @param node
   *          = node of KDTree.
   * @param name
   *          = name search or coordinate search boolean
   */
  public void neighborsSearch(int k, double[] currTarget, KDTreeNode<T> node,
      boolean name) {

    if (node == null) {
      return;
    }
    double[] coordinatesNode = node.getObject().getValue();
    double nodeDistance = getDistance(coordinatesNode, currTarget);
    node.setDistance(nodeDistance);
    int size = k;
    if (name) {
      size += 1;
    }
    if (neighborsQ.size() < size) {
      neighborsQ.add(node);
      if (nodeDistance > maxDistance) {
        maxDistance = nodeDistance;
        maxDistanceNode = node;
      }
    } else {
      KDTreeNode<T> nodeToCompare = maxDistanceNode;
      if (nodeDistance < maxDistance) {
        neighborsQ.remove(nodeToCompare);
        neighborsQ.add(node);
        Iterator<KDTreeNode<T>> it = neighborsQ.iterator();
        double maxInQ = Integer.MIN_VALUE;
        while (it.hasNext()) {
          KDTreeNode<T> currentNode = it.next();
          double currentDistance = getDistance(currentNode.getValue(),
              currTarget);
          if (currentDistance > maxInQ) {
            maxInQ = currentDistance;
            maxDistanceNode = currentNode;
          }
        }
        maxDistance = maxInQ;
      }
    }

    int axis = node.getDepth() % numDimensions;

    double coordinatesAxis = node.axisValue(axis);

    if (coordinatesAxis <= currTarget[axis]) {
      neighborsSearch(k, currTarget, node.getRight(), name);
      if (maxDistance > Math.abs(coordinatesAxis - currTarget[axis])) {
        neighborsSearch(k, currTarget, node.getLeft(), name);
      }
    } else if (coordinatesAxis > currTarget[axis]) {
      neighborsSearch(k, currTarget, node.getLeft(), name);
      if (maxDistance > Math.abs(coordinatesAxis - currTarget[axis])) {
        neighborsSearch(k, currTarget, node.getRight(), name);
      }
    }
  }

  /**
   *
   * @param radiusDistance
   *          = max distance to search.
   * @param currTarget
   *          = position to search around
   * @param node
   *          = node to examine.
   */
  public void radiusSearch(double radiusDistance, double[] currTarget,
      KDTreeNode<T> node) {
    if (node == null) {
      return;
    }
    double[] coordinatesNode = node.getObject().getValue();
    double nodeDistance = getDistance(coordinatesNode, currTarget);
    node.setDistance(nodeDistance);
    if (nodeDistance <= radiusDistance) {
      neighborsQ.add(node);
    }

    int axis = node.getDepth() % numDimensions;

    double coordinatesAxis = node.axisValue(axis);

    if (coordinatesAxis <= currTarget[axis]) {
      radiusSearch(radiusDistance, currTarget, node.getRight());
      if (radiusDistance > Math.abs(coordinatesAxis - currTarget[axis])) {
        radiusSearch(radiusDistance, currTarget, node.getLeft());
      }
    } else if (coordinatesAxis > currTarget[axis]) {
      radiusSearch(radiusDistance, currTarget, node.getLeft());
      if (radiusDistance > Math.abs(coordinatesAxis - currTarget[axis])) {
        radiusSearch(radiusDistance, currTarget, node.getRight());
      }
    }

  }

  /**
   *
   * @param nodeCoordinates
   *          = coordinates of node being passed in.
   * @param currTarget
   *          = current position to measure distance from.
   * @return distance from node to target.
   */
  public double getDistance(double[] nodeCoordinates, double[] currTarget) {
    return Math.sqrt(Math.pow((currTarget[0] - nodeCoordinates[0]), 2)
        + Math.pow((currTarget[1] - nodeCoordinates[1]), 2));
  }

  /**
   * Returns the queue of neighbors.
   *
   * @return _neighborsQ.
   */
  public PriorityQueue<KDTreeNode<T>> getPQ() {
    return neighborsQ;
  }

  /**
   * Compares the distance of two nodes from the target.
   *
   * @author Sean Nathan
   *
   */
  class TargetComparator implements Comparator<KDTreeNode<T>> {
    public int compare(KDTreeNode<T> n1, KDTreeNode<T> n2) {
      double[] tarCoords = target;
      double[] node1 = n1.getObject().getValue();
      double[] node2 = n2.getObject().getValue();

      double distance1 = Math.sqrt(Math.pow((node1[0] - tarCoords[0]), 2)
          + Math.pow((node1[1] - tarCoords[1]), 2));
      double distance2 = Math.sqrt(Math.pow((node2[0] - tarCoords[0]), 2)
          + Math.pow((node2[1] - tarCoords[1]), 2));

      if (distance1 < distance2) {
        return -1;
      } else if (distance2 > distance1) {
        return 1;
      } else {
        return 0;
      }
    }
  }

}
