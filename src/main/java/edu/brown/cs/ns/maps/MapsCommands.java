package edu.brown.cs.ns.maps;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import edu.brown.cs.nsteinb1.ac.AcCommands;
import edu.brown.cs.nsteinb1.bacon.BaconCommands;
import edu.brown.cs.nsteinb1.repl.Commands;
import edu.brown.cs.snathan.kdtree.KDTreeBuilder;
import edu.brown.cs.snathan.kdtree.KDTreeNode;
import edu.brown.cs.snathan.kdtree.Search;

/**
 * Includes and executes all REPL commands that pertain to Maps.
 *
 * @author nicole
 *
 */
public class MapsCommands implements Commands {

  private AcCommands acCommands = new AcCommands();
  private BaconCommands baconCommands = new BaconCommands();
  private Connection conn;
  private KDTreeNode<Node> root;
  private ConcurrentMap<String, String> traffic;
  private static final int ONESEC = 1000;
  private static final int LONGTIME = 10000;
  private boolean stopThread = false;
  private String portUrl = "";

  /**
   * Constructor for MapsCommands.
   */
  public MapsCommands() {
    traffic = new ConcurrentHashMap<String, String>();

  }

  /**
   * Return if the command given is covered by Maps.
   *
   * @param command
   *          - command
   * @return boolean
   */
  public boolean isCommand(String command) {
    return command.equals("ways") || command.equals("map")
        || command.equals("nearest") || command.equals("route")
        || command.equals("suggest");
  }

  /**
   * Process user input.
   *
   * @param command
   *          - A string from the command line.
   */
  public void processCommands(String command) {
    int trailing = 0;
    if (command.charAt(command.length() - 1) == ' ') {
      trailing = 1;
    }

    // Split command into individual strings.
    String[] splitCommand = command.split(" ");

    switch (splitCommand[0]) {
      case "map":
        baconCommands.loadDatabase(splitCommand);
        acCommands = new AcCommands();
        acCommands.parseCorpus("corpus data/maps/streets.txt".split(" "));
        List<Node> nodeList = new ArrayList<Node>();
        if (baconCommands.getConnection() != null) {
          conn = baconCommands.getConnection();
          nodeList = MapsDatabase.generateNodeList(conn);
          KDTreeBuilder<Node> builder = new KDTreeBuilder<>(2,
              createNodeList(nodeList));
          root = builder.getTree();
        }
        break;
      case "ways":
        if (splitCommand.length != 5) {
          System.out.println(
              "ERROR: Correct format is 'ways <lat1> <lat2> <lat3> <lat4>");
          return;
        }

        List<String> ways = findWays(splitCommand[1], splitCommand[2],
            splitCommand[3], splitCommand[4]);
        Collections.sort(ways);
        for (String way : ways) {
          System.out.println(way);
        }
        break;
      case "nearest":
        if (splitCommand.length != 3) {
          System.out.println("ERROR: Correct format is 'nearest <lat1> <lat2>");
          return;
        }
        printID(neighborsSearch("1", splitCommand[1], splitCommand[2], root));
        break;
      case "route":
        List<Way> stack = this.route(splitCommand);
        this.printStack(stack);
        break;
      case "suggest":
        List<String> suggestions = acCommands.autocorrect(splitCommand,
            trailing);
        for (String s : suggestions) {
          System.out.println(s);
        }
        break;
      default:
        System.out.printf("ERROR: '%s' is not a valid command.%n",
            splitCommand[0]);
    }
  }

  private List<KDTreeNode<Node>> createNodeList(List<Node> nodes) {
    List<KDTreeNode<Node>> nodeList = new ArrayList<KDTreeNode<Node>>();
    for (Node node : nodes) {
      KDTreeNode<Node> current = new KDTreeNode<Node>(node, 0);
      nodeList.add(current);
    }
    return nodeList;
  }

  /**
   *
   * @param nNearest
   *          is number of neighbors
   * @param x
   *          is x coord
   * @param y
   *          is y coord
   * @param rootNode
   *          is root node
   * @return List of finalStars in the search. Method to execute neighbors
   *         search for coordinates case.
   */
  public List<KDTreeNode<Node>> neighborsSearch(String nNearest, String x,
      String y, KDTreeNode<Node> rootNode) {
    List<KDTreeNode<Node>> finalNodes = new ArrayList<KDTreeNode<Node>>();
    try {
      int nNearestNeighbors = Integer.parseInt(nNearest);
      double xCoord = Double.parseDouble(x);
      double yCoord = Double.parseDouble(y);
      double[] target = new double[] {
          xCoord, yCoord
      };
      Search<Node> kn = new Search<>(target);
      kn.neighborsSearch(nNearestNeighbors, target, root, false);
      finalNodes = returnFinalList(kn.getPQ());
      return finalNodes;
    } catch (NumberFormatException e) {
      System.out.println("ERROR: Input is not valid. Please retry.");
      return finalNodes;
    }
  }

  private List<KDTreeNode<Node>> returnFinalList(
      PriorityQueue<KDTreeNode<Node>> pq) {
    List<KDTreeNode<Node>> finalStars = new ArrayList<KDTreeNode<Node>>(pq);
    Collections.sort(finalStars, new NodeComparator());
    return finalStars;
  }

  /**
   * Print the ids of nodes in a list.
   *
   * @param finalNodes
   *          nodes
   */
  private void printID(List<KDTreeNode<Node>> finalNodes) {
    for (KDTreeNode<Node> node : finalNodes) {
      System.out.println(node.getObject().getId());
    }
  }

  /**
   * Find ways between two points.
   *
   * @param lat1
   *          latitude 1
   * @param long1
   *          longitude 1
   * @param lat2
   *          latitude 2
   * @param long2
   *          longitude 2
   * @return set of ways ids
   */
  private List<String> findWays(String lat1, String long1, String lat2,
      String long2) {
    List<String> ways = new ArrayList<String>();
    try {
      Double latOne = Double.parseDouble(lat1);
      Double latTwo = Double.parseDouble(lat2);
      Double longOne = Double.parseDouble(long1);
      Double longTwo = Double.parseDouble(long2);
      ways = MapsDatabase.findWays(conn, latOne, latTwo, longOne, longTwo);
      return ways;
    } catch (NumberFormatException e) {
      System.out.println("ERROR: Input lat/longs not valid. Please retry.");
      return ways;
    }
  }

  /**
   * Class that compares the distances of two nodes.
   *
   */
  class NodeComparator implements Comparator<KDTreeNode<Node>> {
    @Override
    public int compare(KDTreeNode<Node> a, KDTreeNode<Node> b) {
      if (a.getDistance() < b.getDistance()) {
        return -1;
      } else if (a.getDistance() > b.getDistance()) {
        return 1;
      } else {
        return 0;
      }
    }
  }

  /**
   * Returns the shortest path using the route command.
   *
   * @param splitCommand
   *          command line input
   * @return path
   */
  public List<Way> route(String[] splitCommand) {
    char quote = '"';
    if (splitCommand[1].charAt(0) == quote) {
      splitCommand = baconCommands.parseQuotes(splitCommand);
    }

    // Make sure you're connected to a database.
    if (conn == null) {
      System.out.println("ERROR: Database not loaded.");
      return null;
    }

    // route has four arguments (four street names).
    if (splitCommand.length != 5) {
      System.out
          .printf("ERROR: Command format is 'route \"<street1>\" \"<cross1>\""
              + " \"<street2>\" \"<cross2>\"' or 'route <lat1> <lon1>"
              + " <lat2> <lon2>'.%n");
      return null;
    }

    // If route uses lat and long, find the nearest traversable node and add to
    // the list.
    List<Node> nearest = new ArrayList<>();
    if (splitCommand[1].charAt(0) != quote
        || splitCommand[4].charAt(splitCommand[4].length() - 1) != quote) {
      try {
        Double.parseDouble(splitCommand[1]);
        Double.parseDouble(splitCommand[2]);
        Double.parseDouble(splitCommand[3]);
        Double.parseDouble(splitCommand[4]);
      } catch (Exception e) {
        System.out
            .printf("ERROR: Command format is 'route \"<street1>\" \"<cross1>\""
                + " \"<street2>\" \"<cross2>\"' or 'route <lat1> <lon1>"
                + " <lat2> <lon2>'.%n");
        return null;
      }

      nearest.add(neighborsSearch("1", splitCommand[1], splitCommand[2], root)
          .get(0).getObject());
      nearest.add(neighborsSearch("1", splitCommand[3], splitCommand[4], root)
          .get(0).getObject());
    }

    List<Way> stack = baconCommands.connectNodes(splitCommand, nearest);

    if (stack == null) {
      return null;
    }

    return stack;
  }

  /**
   * Prints stack returned from route.
   *
   * @param stack
   *          stack
   */
  public void printStack(List<Way> stack) {
    if (stack == null) {
      return;
    }

    // Print the path if there is one.
    int last = stack.size() - 1;
    while (!stack.isEmpty()) {
      Way way = stack.get(last);
      System.out.printf("%s -> %s : %s%n", way.getStart().getName(),
          way.getEnd().getName(), way.getId());
      stack.remove(last);
      last--;
    }
  }

  /**
   * Return connection for GUI.
   *
   * @return conn
   */
  public Connection getConnection() {
    return conn;
  }

  /**
   * Return root for GUI access.
   *
   * @return root
   */
  public KDTreeNode<Node> getRoot() {
    return root;
  }

  /**
   * Return AcCommands for GUI access.
   *
   * @return acCommands
   */
  public AcCommands getAcCommands() {
    return acCommands;
  }

  /**
   * Return BaconCommands for GUI access.
   *
   * @return baconCommands
   */
  public BaconCommands getBaconCommands() {
    return baconCommands;
  }

  /**
   * Method to return traffic Map.
   *
   * @return traffic dictionary
   */
  public Map<String, String> getTrafficMap() {
    Map<String, String> returnTraffic = new HashMap<String, String>(traffic);
    return returnTraffic;
  }

  /**
   * Close thread.
   */
  public void closeThread() {
    stopThread = true;
  }

  /**
   * Set the port of where to look for the traffic server.
   *
   * @param port
   */
  public void setPort(int port) {
    String portString = Integer.toString(port);
    portUrl = "http://localhost:" + portString + "/?last=";
    TrafficUpdater tUpdate = new TrafficUpdater("Thread-1");
    tUpdate.start();
  }

  /**
   * Traffic updater class.
   *
   * @author snathan
   */
  class TrafficUpdater implements Runnable {
    private Thread thread;
    private String threadID;

    TrafficUpdater(String id) {
      threadID = id;
    }

    /**
     * Get HTML.
     *
     * @param urlRead
     * @throws IOException
     *           when HTML not found Method to get traffic data from HTML and
     *           put in ConcurrentMap of traffic data.
     */
    public void getHTML(String urlRead) throws IOException {
      StringBuilder res = new StringBuilder();
      URL url = new URL(urlRead);
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("GET");
      BufferedReader br = new BufferedReader(
          new InputStreamReader(connection.getInputStream()));
      String line;
      while ((line = br.readLine()) != null) {
        res.append(line);
      }
      br.close();
      String read = res.toString();
      read = read.replaceAll("\\[", "").replaceAll("\\]", "")
          .replaceAll("\"", "").replaceAll(" ", "");
      String[] splits = read.split(",");
      if (splits.length >= 2) {
        for (int i = 0; i < splits.length; i += 2) {
          traffic.putIfAbsent(splits[i], splits[i + 1]);
        }
      }
    }

    /**
     * Function to start multi-threading.
     */
    public void start() {
      if (thread == null) {
        thread = new Thread(this, threadID);
        thread.start();
      }
    }

    /**
     * Method to run threading class.
     */
    public void run() {
      try {
        long time = 0;
        for (int i = LONGTIME; i > 0; i--) {
          if (!stopThread && !portUrl.equals("")) {
            getHTML(portUrl + time);
            time = Instant.now().getEpochSecond();
            Thread.sleep(ONESEC);
          }
        }
      } catch (Exception e) {
        System.out.println("ERROR: Traffic server not accessible.");
      }
    }
  }
}
