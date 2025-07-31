import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Dijkstra {
  public static long startTime;
  public static long endTime;
  static public Set<Integer> visited = new HashSet<>();
  static public Map<Integer, Node> nodesMap = new HashMap<>();
  static public Set<Attraction> attractionSet = new HashSet<>();
  static public Map<Integer, List<Vertice>> adjacencyList = new HashMap<>();
  static public Map<String, Integer> differentAttractions = new HashMap<>();
  static public String attractionName;
  static String[] felt = new String[20]; // Max 20 felt i en linje
  public static class Vertice {
    Node from;
    Node to;
    long time;  // Changed from length to time

    Vertice(Node from, Node to, long time) {
      this.from = from;
      this.to = to;
      this.time = time;
    }
  }

  public static class Node implements Comparable<Node> {
    int node;
    double shortestTime;
    Node previousNode;

    Node(int node) {
      this.node = node;
      this.previousNode = null;
      this.shortestTime = Double.POSITIVE_INFINITY;
    }

    public void setShortestTime(double newValue) {
      this.shortestTime = newValue;
    }

    public void setPreviousNode(Node prevNode) {
      this.previousNode = prevNode;
    }

    // For priority queue to sort nodes by shortest time
    @Override
    public int compareTo(Node other) {
      return Double.compare(this.shortestTime, other.shortestTime);
    }
  }

  public static class Attraction {
    public int nodeNumber;
    public int infoCode;
    public String name;

    public Attraction(int nodeNumber, int infoCode, String name) {
      this.nodeNumber = nodeNumber;
      this.infoCode = infoCode;
      this.name = name;
    }

    public boolean checkAttractionType(int infoCode) {
      return (this.infoCode & infoCode) != 0;
    }
  }

  public static void readVerticesFromFile(String pathname) throws FileNotFoundException {
    File file = new File(pathname);
    Scanner sc = new Scanner(file);
    String firstLine = sc.nextLine();

    // Reading edges from the file, without knowing the number of nodes beforehand
    while(sc.hasNextLine()) {
      String nextLine = sc.nextLine();
      hsplit(nextLine,3);
      int from = Integer.parseInt(felt[0]);
      int to = Integer.parseInt(felt[1]);
      long time = Long.parseLong(felt[2]);  // Changed from length to time

      // Create nodes only if they do not exist in the map
      nodesMap.putIfAbsent(from, new Node(from));
      nodesMap.putIfAbsent(to, new Node(to));

      // Create an edge (vertice) and add to the adjacency list
      Node fromNode = nodesMap.get(from);
      Node toNode = nodesMap.get(to);
      Vertice edge = new Vertice(fromNode, toNode, time);

      // Initialize the adjacency list for the 'from' node if it does not exist
      adjacencyList.computeIfAbsent(from, k -> new ArrayList<>());
      adjacencyList.get(from).add(edge);
    }
  }

  public static void readInteressepunktFromFile(String pathname) throws FileNotFoundException {
    File file = new File(pathname);
    Scanner sc = new Scanner(file);
    sc.nextLine();

    differentAttractions.put("Stedsnavn", 1);
    differentAttractions.put("Bensinstasjon", 2);
    differentAttractions.put("Ladestasjon", 4);
    differentAttractions.put("Spisested", 8);
    differentAttractions.put("Drikkested", 16);
    differentAttractions.put("Overnattingssted", 32);
    int attractionCode;
    try {
      attractionCode = differentAttractions.get(attractionName);
    }
    catch (Error e) {
      System.out.println("Cannot find that attraction place");
      return;
    }

    while(sc.hasNextLine()) {
      String nextLine = sc.nextLine();
      hsplit(nextLine,3);
      int nodeNr = Integer.parseInt(felt[0]);
      int infoCode = Integer.parseInt(felt[1]);
      String name = felt[2];
      Attraction attractionObj = new Attraction(nodeNr, infoCode, name);
      // Create attraction only if it doesn't exist in the hashmap
      if (attractionObj.checkAttractionType(attractionCode)) {
        attractionSet.add(attractionObj);
      }
    }
  }

  public static void findClosestAttractions(int start) {
    for (Node node : nodesMap.values()) {
      node.shortestTime = Double.POSITIVE_INFINITY;
    }
    // Set visited to be empty
    visited = new HashSet<>();
    Node[] top4 = new Node[4];
    for(Attraction attraction : attractionSet) {
      findShortestTime(start, attraction.nodeNumber);

      for (int k = 0; k < top4.length; k++) {
        if (top4[k] == null) {
          top4[k] = nodesMap.get(attraction.nodeNumber);
          break;
        }
        else if (nodesMap.get(attraction.nodeNumber).shortestTime < top4[k].shortestTime) {
          replaceFurthest(top4, nodesMap.get(attraction.nodeNumber));
          break;
        }
      }
    }
    System.out.println("Closest " + attractionName + " to " + start + " are");
    for (Node node : top4) {
      if (node != null) {
        // Find the attraction that corresponds to this node
        Attraction matchingAttraction = attractionSet.stream()
            .filter(attraction -> attraction.nodeNumber == node.node)
            .findFirst()
            .orElse(null);

        if (matchingAttraction != null) {
          System.out.println(matchingAttraction.name);
        } else {
          System.out.println("Attraction Name: Unknown");
        }

        System.out.println("Node: " + node.node);
        printPathInfo(node);
      }
    }

  }

  public static void replaceFurthest(Node[] top4, Node currentNode) {
    int furthestIndex = 0;
    double maxTime = top4[0].shortestTime;

    // Find the index of the furthest attraction
    for (int i = 1; i < top4.length; i++) {
      if (top4[i] != null && top4[i].shortestTime > maxTime) {
        furthestIndex = i;
        maxTime = top4[i].shortestTime;
      }
    }

    // Replace the furthest attraction with the new one
    top4[furthestIndex] = currentNode;
  }

  public static void findShortestTime(int start, int end) {
    PriorityQueue<Node> priorityQueue = new PriorityQueue<>();

    // Initialize the start node
    Node startNode = nodesMap.get(start);
    startNode.setShortestTime(0);
    priorityQueue.add(startNode);

    while (!priorityQueue.isEmpty()) {
      Node currentNode = priorityQueue.poll();

      // If we reached the target node, we can stop
      if (currentNode.node == end) break;

      // Mark node as visited
      if (visited.contains(currentNode.node)) continue;
      visited.add(currentNode.node);

      // Iterate only over the edges of the current node
      List<Vertice> edges = adjacencyList.get(currentNode.node);
      if (edges != null) {
        for (Vertice edge : edges) {
          Node neighbor = edge.to;
          if (visited.contains(neighbor.node)) continue;

          double newTime = currentNode.shortestTime + edge.time;  // Changed from distance to time
          if (newTime < neighbor.shortestTime) {
            neighbor.setShortestTime(newTime);
            neighbor.setPreviousNode(currentNode);
            priorityQueue.add(neighbor);
          }
        }
      }
    }
  }

  // Method to count the number of nodes in the path and display the total time
  public static void printPathInfo(Node target) {
    if (target == null) {
      System.out.println("No path found.");
      return;
    }

    // Calculate the number of nodes in the path
    int nodeCount = 0;
    Node current = target;
    while (current != null) {
      nodeCount++;
      current = current.previousNode;
    }

    // Convert the total time in hundredths of a second to hours, minutes, and seconds
    convertHundredthsToHoursMinutesSeconds((long) (target.shortestTime));

    System.out.println("Number of nodes in the shortest path: " + nodeCount + '\n');
  }

  // Method to convert hundredths of a second to hours, minutes, and seconds
  public static void convertHundredthsToHoursMinutesSeconds(long hundredths) {
    // Convert hundredths of a second to total seconds
    double totalSeconds = hundredths / 100.0;

    // Convert total seconds to hours
    int hours = (int) (totalSeconds / 3600);

    // Calculate remaining seconds after extracting hours
    double remainingSeconds = totalSeconds % 3600;

    // Convert remaining seconds to minutes
    int minutes = (int) (remainingSeconds / 60);

    // Calculate remaining seconds after extracting minutes
    int seconds = (int) remainingSeconds % 60;

    // Print the result
    System.out.println("Time taken: " + hours + " hours, " + minutes + " minutes, and " + seconds + " seconds.");
  }
  public static void hsplit(String linje, int antall) {
    int j = 0;
    int lengde = linje.length();
    for (int i = 0; i < antall; ++i) {
      if (i != 2) {
        // Hopp over innledende blanke, finn starten på ordet
        while (linje.charAt(j) <= ' ') ++j;
        int ordstart = j;
        // Finn slutten på ordet, hopp over ikke-blanke
        while (j < lengde && linje.charAt(j) > ' ') ++j;
        felt[i] = linje.substring(ordstart, j);
      }
      else {
        while (linje.charAt(j) <= '\n') ++j;
        int ordstart = j;
        while (j < lengde && linje.charAt(j) > '\n') ++j;
        felt[i] = linje.substring(ordstart, j);
      }
    }
  }


  public static void main(String[] args) throws FileNotFoundException {
    readVerticesFromFile("kanterOppgave.txt");  // Sett inn kant fil
    System.out.println("File read completed.");

    int startNode = 2800567; // Set start node here
    int endNode = 7705656;   // Set end node here
    int fromNode = 2001238;  // Set node we want to search for attractions near
    attractionName = "Drikkested"; // Set the name of the attractions you want to search for

    startTime = System.currentTimeMillis();
    findShortestTime(startNode, endNode);
    endTime = System.currentTimeMillis();

    System.out.println("Time taken for Dijkstras algorithm: " + (endTime-startTime)/1000.0 + " seconds");

    System.out.println("Shortest path from node " + startNode + " to " + endNode + ":");
    printPathInfo(nodesMap.get(endNode));
    readInteressepunktFromFile("interessepktNorden.txt"); // Sett inn interessepunkter fil her
    findClosestAttractions(fromNode);
  }
}
