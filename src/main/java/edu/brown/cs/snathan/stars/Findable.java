package edu.brown.cs.snathan.stars;

/**
 *
 * @author Sean Nathan Interface for connecting object to KDTree generics.
 *
 */
public interface Findable {
  /**
   * Interface method to getValue of Object.
   *
   * @return whatever gets returned in overridden method.
   */
  double[] getValue();

  /**
   *
   * @param axis
   *          = axis of Object coordinate.
   * @return double of coordinate value.
   */
  double axisValue(int axis);

}
