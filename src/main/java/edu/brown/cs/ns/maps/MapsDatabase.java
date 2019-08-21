package edu.brown.cs.ns.maps;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handles all database connections to Node and Way tables.
 *
 * @author nicole
 *
 */
public final class MapsDatabase {

  static final double EARTH_RADIUS = 6378000;
  private static Map<String, String> traffic = new HashMap<>();

  /**
   * Constructor for database.
   */
  private MapsDatabase() {
  }

  /**
   * Connect to the passed-in database.
   *
   * @param splitCommand
   *          database
   * @return connection to database
   */
  public static Connection load(String[] splitCommand) {
    // Open connection to database.
    Connection conn = null;
    try {
      try {
        Class.forName("org.sqlite.JDBC");
      } catch (ClassNotFoundException e) {
        System.out.println("ERROR: Class not found.");
        return null;
      }
      String urlToDb = "jdbc:sqlite:" + splitCommand[1];

      // Only open connection if file already exists.
      File file = new File(splitCommand[1]);
      if (file.exists()) {
        conn = DriverManager.getConnection(urlToDb);
        Statement stat = conn.createStatement();
        stat.executeUpdate("PRAGMA foreign_keys = ON");
      } else {
        System.out.printf("ERROR: %s does not exist.%n", splitCommand[1]);
        return null;
      }
    } catch (SQLException e) {
      System.out.println("ERROR: SQL connection could not be made.");
      return null;
    }

    PreparedStatement prep;
    ResultSet rs;

    // Make sure correct tables are included in SQL file. If not, terminate
    // connection.
    try {
      prep = conn
          .prepareStatement("SELECT 1 id, latitude, longitude FROM Node;");
      rs = prep.executeQuery();
      rs.close();
      prep.close();
      prep = conn.prepareStatement("SELECT 1 id, start, end FROM Way;");
      rs = prep.executeQuery();
      rs.close();
      prep.close();
    } catch (SQLException e) {
      System.out.println(
          "ERROR: Database must include Node (id, latitude, longitude), "
              + "and Way (id, start, end) tables.");
      return null;
    }

    return conn;
  }

  /**
   * Populates a node's connections and returns them in a hashmap from ID to
   * way.
   *
   * @param conn
   *          connection
   * @return connections for a vertex
   */

  public static List<Node> generateNodeList(Connection conn) {
    List<Node> nodesInDB = new ArrayList<Node>();
    try {
      PreparedStatement prep = conn
          .prepareStatement("SELECT n.id, n.latitude, n.longitude FROM Node AS n WHERE n.id IN (SELECT end FROM way WHERE type!=? AND type!=?) "
              + "OR n.id IN (SELECT start FROM way WHERE type!=? AND type!=?);");
      prep.setString(1, "unclassified");
      prep.setString(2, "");
      prep.setString(3, "unclassified");
      prep.setString(4, "");
      ResultSet rs = prep.executeQuery();
      while (rs.next()) {
        Node current = new Node(rs.getString(1), rs.getDouble(2),
            rs.getDouble(3), conn);
        nodesInDB.add(current);
      }
      rs.close();
      prep.close();
    } catch (SQLException e) {
      System.out.println("ERROR: SQL Exception occurred");
    }
    return nodesInDB;

  }

  /**
   * Find traversable ways.
   *
   * @param conn
   *          database connection
   * @param node
   *          node to find ways for
   * @return list of way ids
   */
  public static List<String> findTraversableWays(Connection conn, Node node) {
    List<String> travWays = new ArrayList<String>();
    try (PreparedStatement prep = conn
        .prepareStatement("SELECT id FROM way WHERE (end = ? OR start = ?)"
            + " AND type != ? AND type != NULL;")) {
      prep.setString(1, node.getId());
      prep.setString(2, node.getId());
      prep.setString(3, "unclassified");
      try (ResultSet rs = prep.executeQuery()) {
        while (rs.next()) {
          travWays.add(rs.getString(1));
        }
      }
      return travWays;
    } catch (SQLException e) {
      System.out.println("ERROR: SQL Exception occurred.");
      return travWays;
    }
  }

  /**
   * Method to find ways using SQL.
   *
   * @param conn
   *          = db connection
   * @param latOne
   *          = low latitude
   * @param latTwo
   *          = high latitude
   * @param longOne
   *          = low longitude
   * @param longTwo
   *          = high longitude
   * @return set of way id's
   */
  public static List<String> findWays(Connection conn, double latOne,
      double latTwo, double longOne, double longTwo) {
    List<String> ways = new ArrayList<String>();
    Double latLo = 0.0;
    Double latHi = 0.0;
    Double longLo = 0.0;
    Double longHi = 0.0;

    if (latOne > latTwo) {
      latLo = latTwo;
      latHi = latOne;
    } else {
      latLo = latOne;
      latHi = latTwo;
    }

    if (longOne > longTwo) {
      longLo = longTwo;
      longHi = longOne;
    } else {
      longLo = longOne;
      longHi = longTwo;
    }

    try (PreparedStatement prep = conn.prepareStatement(
        "SELECT id FROM Way WHERE end IN (SELECT id FROM Node "
            + "WHERE latitude>=? AND latitude<= ? AND "
            + "longitude>=? and longitude<=?);")) {
      prep.setDouble(1, latLo);
      prep.setDouble(2, latHi);
      prep.setDouble(3, longLo);
      prep.setDouble(4, longHi);
      try (ResultSet rs = prep.executeQuery()) {
        while (rs.next()) {
          ways.add(rs.getString(1));
        }
        return ways;
      }
    } catch (SQLException e) {
      System.out.println("ERROR: SQL Exception occurred");
      return ways;
    }
  }

  /**
   * Method to get connections given node.
   *
   * @param node
   *          = node given
   * @param conn
   *          = db connection
   * @return map of end node to way
   */
  public static HashMap<String, Way> getConnections(Node node,
      Connection conn) {
    HashMap<String, Way> adj = new HashMap<>();

    // First get all the ways the node is in.
    try (PreparedStatement prep = conn.prepareStatement(
        "SELECT id, end, name, type FROM Way WHERE start = ?;")) {
      prep.setString(1, node.getId());

      try (ResultSet rs = prep.executeQuery()) {

        // Iterate through all the ways the node is in and get information about
        // the end nodes.
        while (rs.next()) {
          try (PreparedStatement prep2 = conn.prepareStatement(
              "SELECT latitude, longitude FROM Node WHERE id = ?;")) {
            prep2.setString(1, rs.getString(2));

            try (ResultSet endNode = prep2.executeQuery()) {

              while (endNode.next()) {
                // Create end node and corresponding edge.
                Node end = new Node(rs.getString(2), endNode.getDouble(1),
                    endNode.getDouble(2), conn);

                // Create new corresponding edge with edge multiplier from
                // traffic if relevant.
                double weight = haversine(node, end)
                    * getTrafficMultiplier(rs.getString(1));
                Way way = new Way(weight, node, end, rs.getString(1),
                    rs.getString(3), rs.getString(4));

                // Add connection to adjacent nodes.
                if (adj.containsKey(end.getId())) {
                  if (way.getWeight() < adj.get(end.getId()).getWeight()) {
                    adj.put(end.getId(), way);
                  }
                } else if (!end.getId().equals(node.getId())) {
                  adj.put(end.getId(), way);
                }
              }
            }
          }
        }
      }
      return adj;
    } catch (SQLException e) {
      System.out.println("ERROR:");
      return new HashMap<String, Way>();
    }
  }

  /**
   * Load street names into a corpus for autocorrect.
   *
   * @param conn
   *          connection
   */
  public static void loadStreetCorpus(Connection conn) {
    File streets = new File("data/maps/streets.txt");

    // Create a new file if one does not exist.
    if (!streets.exists()) {
      try {
        streets.createNewFile();
      } catch (IOException e) {
        System.out.println("ERROR: Corpus could not be created.");
        return;
      }
    }

    // Write all street names to file.
    try (PreparedStatement prep = conn
        .prepareStatement("SELECT name FROM Way;")) {
      try (ResultSet rs = prep.executeQuery()) {
        try (FileWriter fw = new FileWriter(streets, false)) {
          try (BufferedWriter bw = new BufferedWriter(fw)) {
            while (rs.next()) {
              bw.write(rs.getString(1));
              bw.newLine();
            }
          }
        } catch (IOException e) {
          System.out.println("ERROR: Corpus could not be written to.");
        }
      }
    } catch (SQLException e) {
      System.out.println("ERROR: SQL Exception.");
      return;
    }
  }

  /**
   * Returns node based on given latitude and longitude.
   *
   * @param conn
   *          current connection
   * @param latitude
   *          latitude
   * @param longitude
   *          longitude
   * @return new node
   */
  public static Node getNode(Connection conn, double latitude,
      double longitude) {
    // Query the database for actor ids.
    try (PreparedStatement prep = conn.prepareStatement(
        "SELECT id FROM Node WHERE latitude = ? AND longitude = ?;")) {
      prep.setDouble(1, latitude);
      prep.setDouble(2, longitude);
      try (ResultSet rs = prep.executeQuery()) {
        return new Node(rs.getString(1), latitude, longitude, conn);
      }
    } catch (SQLException e) {
      return null;
    }
  }

  /**
   * Returns a node where two streets intersect.
   *
   * @param conn
   *          current connection
   * @param street1
   *          street name
   * @param street2
   *          name of cross street
   * @return new node
   */
  public static Node getIntersection(Connection conn, String street1,
      String street2) {
    // Query the database for node where streets intersect.
    try (PreparedStatement prep = conn.prepareStatement(
        "SELECT Node.id, Node.latitude, Node.longitude FROM Node WHERE "
            + "Node.id = (SELECT w1.start FROM Way w1 JOIN Way w2 ON "
            + "w1.start = w2.start OR w1.start = w2.end WHERE w1.name "
            + "= ? AND w2.name = ?) OR Node.id = (SELECT w1.end FROM Way "
            + "w1 JOIN Way w2 ON w1.end = w2.end OR w1.end "
            + "= w2.end WHERE w1.name = ? AND w2.name = ?);")) {
      prep.setString(1, street1);
      prep.setString(2, street2);
      prep.setString(3, street1);
      prep.setString(4, street2);
      try (ResultSet rs = prep.executeQuery()) {
        return new Node(rs.getString(1), rs.getDouble(2), rs.getDouble(3),
            conn);
      }
    } catch (SQLException e) {
      return null;
    }
  }

  /**
   * Returns great-circle distance between two nodes in meters.
   *
   * @param startNode
   *          start of way
   * @param endNode
   *          end of way
   * @return distance between start and end nodes of way
   */
  public static double haversine(Node startNode, Node endNode) {
    double startLat = Math.toRadians(startNode.getLat());
    double endLat = Math.toRadians(endNode.getLat());
    double changeLat = Math.toRadians(startNode.getLat() - endNode.getLat());
    double changeLon = Math.toRadians(startNode.getLong() - endNode.getLong());

    double a = Math.sin(changeLat / 2) * Math.sin(changeLat / 2)
        + Math.cos(startLat) * Math.cos(endLat) * Math.sin(changeLon / 2)
            * Math.sin(changeLon / 2);
    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

    return EARTH_RADIUS * c;
  }

  /**
   * Returns ways for GUI access.
   *
   * @param conn
   *          connection
   * @param wayId
   *          list of way ids
   * @return ways
   */
  public static List<List<String>> getGUIData(Connection conn,
      List<String> wayId) {
    List<String> nodes = new ArrayList<>();
    List<List<String>> ways = new ArrayList<List<String>>();
    for (String id : wayId) {
      List<String> inpt = new ArrayList<>();
      nodes = queryFromId(conn, id);

      if (nodes != null) {
        List<String> start = queryLatLon(conn, nodes.get(1));
        List<String> end = queryLatLon(conn, nodes.get(2));

        inpt.add(id);
        inpt.add(start.get(0));
        inpt.add(start.get(1));
        inpt.add(end.get(0));
        inpt.add(end.get(1));
        ways.add(inpt);
      }
    }
    return ways;
  }

  /**
   * Takes stack and returns list of strings representing the path between two
   * points.
   *
   * @param stack
   *          stack of ways
   * @return list of list of strings, where each inner list is a street
   */
  public static List<List<Double>> getPathFromStack(List<Way> stack) {
    // Iterate through the stack to get path between actors.
    List<List<Double>> path = new ArrayList<>();

    if (stack != null) {
      if (!stack.isEmpty()) {
        int last = stack.size() - 1;
        while (!stack.isEmpty()) {
          Way way = stack.get(last);

          // Populate pass.
          List<Double> pass = new ArrayList<>();
          pass.add(way.getStart().getLat());
          pass.add(way.getStart().getLong());
          pass.add(way.getEnd().getLat());
          pass.add(way.getEnd().getLong());

          path.add(pass);
          stack.remove(last);
          last--;
        }
      }
    }

    return path;
  }

  /**
   * Returns way data for a way ID.
   *
   * @param conn
   *          connection
   * @param id
   *          way id
   * @return list of way name, start node, and end node
   */
  public static List<String> queryFromId(Connection conn, String id) {
    List<String> nodes = new ArrayList<>();
    try (PreparedStatement prep = conn
        .prepareStatement("SELECT name, start, end FROM way WHERE id = ?")) {
      prep.setString(1, id);
      try (ResultSet rs = prep.executeQuery()) {
        while (rs.next()) {
          nodes.add(rs.getString(1));
          nodes.add(rs.getString(2));
          nodes.add(rs.getString(3));
        }
      }
    } catch (SQLException e) {
      nodes = null;
    }
    return nodes;
  }

  /**
   * Returns coordinates of node given an ID.
   *
   * @param conn
   *          connection
   * @param id
   *          node id
   * @return node coordinates
   */
  public static List<String> queryLatLon(Connection conn, String id) {
    List<String> coordinates = new ArrayList<>();
    try (PreparedStatement prep = conn.prepareStatement(
        "SELECT latitude, longitude FROM node WHERE id = ?")) {
      prep.setString(1, id);
      try (ResultSet rs = prep.executeQuery()) {
        while (rs.next()) {
          coordinates.add(rs.getString(1));
          coordinates.add(rs.getString(2));
        }
      }
    } catch (SQLException e) {
      coordinates = null;
    }
    return coordinates;
  }

  /**
   * Sets traffic.
   *
   * @param trafficMap
   *          map of traffic
   */
  public static void setTraffic(Map<String, String> trafficMap) {
    traffic = trafficMap;
  }

  /**
   * Returns traffic multiplier for a given way. If way does not have traffic,
   * default is 2. If no ways have traffic, default is 1.
   *
   * @param wayId
   *          way id
   * @return traffic multiplier
   */
  public static double getTrafficMultiplier(String wayId) {
    if (traffic.get(wayId) != null) {
      return Double.parseDouble(traffic.get(wayId));
    } else if (!traffic.isEmpty()) {
      return 2;
    } else {
      return 1;
    }
  }
}
