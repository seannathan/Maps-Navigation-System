package edu.brown.cs.nsteinb1.main;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;

import edu.brown.cs.ns.maps.MapsCommands;
import edu.brown.cs.ns.maps.MapsDatabase;
import edu.brown.cs.ns.maps.Node;
import edu.brown.cs.nsteinb1.repl.Repl;
import edu.brown.cs.snathan.kdtree.KDTreeNode;
import freemarker.template.Configuration;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import spark.ExceptionHandler;
import spark.ModelAndView;
import spark.QueryParamsMap;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;
import spark.TemplateViewRoute;
import spark.template.freemarker.FreeMarkerEngine;

/**
 * The Main class of our project. This is where execution begins.
 *
 * @author nsteinb1
 */
public final class Main {

  private static final int DEFAULT_PORT = 4568;
  private static Repl repl;
  private static boolean mapDrawn = false;
  private static MapsCommands maps;
  private static final Gson GSON = new Gson();

  /**
   * The initial method called when execution begins.
   *
   * @param args
   *          An array of command line arguments
   */
  public static void main(String[] args) {
    new Main(args).run();
  }

  private String[] args;

  private Main(String[] args) {
    maps = new MapsCommands();
    this.args = args;
  }

  private void run() {
    // Parse command line arguments
    OptionParser parser = new OptionParser();
    parser.accepts("gui");
    parser.accepts("port").withRequiredArg().ofType(Integer.class)
        .defaultsTo(DEFAULT_PORT);
    OptionSet options = parser.parse(args);

    repl = new Repl();

    if (options.has("gui")) {
      runSparkServer((int) options.valueOf("port"));
      maps.setPort((int) options.valueOf("port"));
      maps.closeThread();
    } else {
      repl.start();
    }
  }

  private static FreeMarkerEngine createEngine() {
    Configuration config = new Configuration();
    File templates = new File("src/main/resources/spark/template/freemarker");
    try {
      config.setDirectoryForTemplateLoading(templates);
    } catch (IOException ioe) {
      System.out.printf("ERROR: Unable use %s for template loading.%n",
          templates);
      System.exit(1);
    }
    return new FreeMarkerEngine(config);
  }

  private void runSparkServer(int port) {
    Spark.port(port);
    Spark.externalStaticFileLocation("src/main/resources/static");
    Spark.exception(Exception.class, new ExceptionPrinter());

    FreeMarkerEngine freeMarker = createEngine();

    // Setup Spark Routes
    Spark.get("/maps", new FrontHandler(), freeMarker);
    Spark.post("/getWaysInBox", new WaysHandler());
    Spark.post("/getRoute", new RouteHandler());
    Spark.post("/getTraffic", new TrafficUpdateHandler());
    Spark.post("/getNearest", new NearHandler());
    Spark.post("/loadDb", new DatabaseHandler());
    Spark.post("/getIntersection", new IntersectionHandler());

    Spark.post("/ac1", new AutocorrectHandler());
    Spark.post("/ac2", new AutocorrectHandler());
    Spark.post("/ac3", new AutocorrectHandler());
    Spark.post("/ac4", new AutocorrectHandler());

  }

  /**
   * Java doc.
   *
   */
  private static class FrontHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      if (!mapDrawn) {
        String command = "map data/maps/maps.sqlite3";
        mapDrawn = true;
        try {
          maps.processCommands(command);
        } catch (Exception e) {
          System.out.println("ERROR: Loading page failed!");
        }
      }
      Map<String, Object> variables = ImmutableMap.of("title", "Maps");
      return new ModelAndView(variables, "draw.ftl");
    }
  }

  /**
   * Java doc.
   *
   */
  private static class WaysHandler implements Route {
    @Override
    public String handle(Request req, Response res) {
      QueryParamsMap qm = req.queryMap();
      Map<String, Object> variables = null;

      List<String> ways = MapsDatabase.findWays(maps.getConnection(),
          Double.parseDouble(qm.value("a")), Double.parseDouble(qm.value("c")),
          Double.parseDouble(qm.value("b")), Double.parseDouble(qm.value("d")));

      List<List<String>> temp = new ArrayList<List<String>>();
      temp = MapsDatabase.getGUIData(maps.getConnection(), ways);

      variables = ImmutableMap.of("ways", temp);
      return GSON.toJson(variables);
    }
  }

  /**
   * Java doc.
   *
   */
  private static class RouteHandler implements Route {
    @Override
    public String handle(Request req, Response res) {
      QueryParamsMap qm = req.queryMap();
      Map<String, Object> variables = null;
      try {
        String command = "route " + qm.value("a") + " " + qm.value("b") + " "
            + qm.value("c") + " " + qm.value("d");
        List<List<Double>> temp = MapsDatabase
            .getPathFromStack(maps.route(command.split(" ")));
        variables = ImmutableMap.of("ways", temp);
      } catch (Exception e) {
        System.out.println("ERROR: Route finding failed!");
      }
      return GSON.toJson(variables);
    }
  }

  /**
   * Java doc.
   *
   */
  private static class IntersectionHandler implements Route {
    @Override
    public String handle(Request req, Response res) {
      QueryParamsMap qm = req.queryMap();
      Map<String, Object> variables = null;
      try {
        Node intersection = MapsDatabase.getIntersection(maps.getConnection(),
            qm.value("st1"), qm.value("st2"));
        List<Double> coord = new ArrayList<>();

        if (intersection != null) {
          coord.add(intersection.getLat());
          coord.add(intersection.getLong());
        }

        variables = ImmutableMap.of("point", coord);
      } catch (Exception e) {
        System.out.println("ERROR: Finding intersection failed!");
      }
      return GSON.toJson(variables);
    }
  }

  /**
   * Finds nearest traversable node.
   */
  private static class NearHandler implements Route {
    @Override
    public String handle(Request req, Response res) {
      QueryParamsMap qm = req.queryMap();
      Map<String, Object> variables = null;
      try {
        List<KDTreeNode<Node>> list = maps.neighborsSearch("1", qm.value("lat"),
            qm.value("lon"), maps.getRoot());

        if (!list.isEmpty()) {
          Node returned = list.get(0).getObject();

          List<Double> coord = new ArrayList<>();
          coord.add(returned.getLat());
          coord.add(returned.getLong());
          variables = ImmutableMap.of("point", coord);
        }
      } catch (Exception e) {
        System.out.println("ERROR: Finding nearest node failed!");
        System.out.println(e);
      }
      return GSON.toJson(variables);
    }
  }

  /**
   * Java doc.
   *
   */
  private static class AutocorrectHandler implements Route {
    @Override
    public String handle(Request req, Response res) {

      QueryParamsMap qm = req.queryMap();
      String input = qm.value("input");
      if (input == null || input.equals("")) {
        return null;
      }

      int trailing = 0;
      if (input.charAt(input.length() - 1) == ' ') {
        trailing = 1;
      }

      input = "suggest " + input;

      List<String> suggestions = maps.getAcCommands()
          .autocorrect(input.split(" "), trailing);

      if (suggestions.size() > 2) {
        suggestions = suggestions.subList(0, 2);
      }

      Map<String, Object> variables = ImmutableMap.of("suggestions",
          suggestions);
      return GSON.toJson(variables);
    }
  }

  /**
   * Java doc.
   *
   */
  private static class TrafficUpdateHandler implements Route {
    @Override
    public String handle(Request req, Response res) {
      Map<String, String> variables = maps.getTrafficMap();
      MapsDatabase.setTraffic(variables);
      return GSON.toJson(variables);
    }
  }

  /**
   * Java doc.
   *
   */
  private static class DatabaseHandler implements Route {
    @Override
    public String handle(Request req, Response res) {
      QueryParamsMap qm = req.queryMap();
      String path = qm.value("db");
      path = "map " + path;
      maps.processCommands(path);
      Map<String, Object> variables = ImmutableMap.of("db", path);
      return GSON.toJson(variables);
    }
  }

  /**
   * Display an error page when an exception occurs in the server.
   *
   * @author jj
   */
  private static class ExceptionPrinter implements ExceptionHandler {
    @Override
    public void handle(Exception e, Request req, Response res) {
      res.status(500);
      StringWriter stacktrace = new StringWriter();
      try (PrintWriter pw = new PrintWriter(stacktrace)) {
        pw.println("<pre>");
        e.printStackTrace(pw);
        pw.println("</pre>");
      }
      res.body(stacktrace.toString());
    }
  }

}
