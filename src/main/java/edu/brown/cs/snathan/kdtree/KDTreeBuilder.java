package edu.brown.cs.snathan.kdtree;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import edu.brown.cs.snathan.stars.Findable;

/**
 *
 * @author Sean Nathan
 *
 * @param <T>
 *          = generic Object that interfaces with KDTree classes. KDTreeBuilder
 *          class that builds the KDTree based on number of dimensions.
 */
public class KDTreeBuilder<T extends Findable> {
  private int numDimensions;
  private int axis;
  private int nodeDepth;
  private int numNodes = 0;
  private List<KDTreeNode<T>> nodeList;
  private KDTreeNode<T> tree;

  /**
   * Constructor for KDTree.
   *
   * @param numDim
   *          = number of dimensions of tree.
   * @param nodes
   *          = list of nodes in tree
   */
  public KDTreeBuilder(int numDim, List<KDTreeNode<T>> nodes) {
    numDimensions = numDim;
    nodeList = nodes;
    nodeDepth = 0;
    tree = buildTree(nodeList, nodeDepth);
  }

  /**
   * Method that builds the KDTree and returns the root of the tree.
   *
   * @param nodes
   *          = list of nodes.
   * @param depth
   *          = current depth.
   * @return root of tree.
   */
  public KDTreeNode<T> buildTree(List<KDTreeNode<T>> nodes, int depth) {
    if (nodes.size() == 0) {
      return null;
    }
    int currentDepth = depth;
    axis = currentDepth % numDimensions;
    KDTreeNodeComparator comparator = new KDTreeNodeComparator();
    Collections.sort(nodes, comparator);

    KDTreeNode<T> rootNode = new KDTreeNode<T>(
        nodes.get(nodes.size() / 2).getObject(), currentDepth);
    rootNode.addLeft(
        this.buildTree(nodes.subList(0, nodes.size() / 2), currentDepth + 1));
    rootNode.addRight(this.buildTree(
        nodes.subList((nodes.size() / 2) + 1, nodes.size()), currentDepth + 1));

    rootNode.setDepth(currentDepth);
    numNodes++;
    return rootNode;

  }

  /**
   * Method that setsDimensions of tree.
   *
   * @param d
   *          = number of dimensions to be set.
   */
  public void setDimensions(int d) {
    this.numDimensions = d;
  }

  /**
   * Method to get the dimensions of tree.
   *
   * @return numberOfDimensions
   */
  public int getDimensions() {
    return numDimensions;
  }

  /**
   * Method that returns our KDTree object.
   *
   * @return root of tree.
   */
  public KDTreeNode<T> getTree() {
    return this.tree;
  }

  /**
   * Method to get the number of nodes in the tree.
   *
   * @return number of nodes in tree.
   */
  public int getNumNodes() {
    return this.numNodes;
  }

  /**
   * Comparator for axis value of node1 versus node2.
   *
   */
  public class KDTreeNodeComparator implements Comparator<KDTreeNode<T>> {

    @Override
    public int compare(KDTreeNode<T> n1, KDTreeNode<T> n2) {
      if (n1.axisValue(axis) < n2.axisValue(axis)) {
        return -1;
      } else if (n1.axisValue(axis) > n2.axisValue(axis)) {
        return 1;
      } else {
        return 0;
      }
    }

  }

}
