import java.io.BufferedReader;
import java.io.FileReader;
import java.util.StringTokenizer;
import java.util.Stack;
import java.util.ArrayList;
import java.util.List;

/**
 * Oving5-klassen representerer en direkte graf og inneholder metoder
 * for å finne sterkt sammenkoblede komponenter (SCC) ved bruk av Kosaraju's algoritme.
 * Den skriver også ut grafen i form av en naboliste med piler.
 * Vi har her 3 datatyper, kanttypen, nodetypen og graftypen, som skildres hver for seg.
 */
public class Oving5 {
  int N, K;
  Node[] node;

  /**
   * Kanttypen, som et listeelement. Felter for hvilken node kanten går til, og neste kant i listen.
   */
  class Kant {
    Kant neste;
    Node til;

    /**
     * Konstruktor for Kant.
     *
     * @param n   Destinasjonen til kanten.
     * @param nst Neste kant i listen.
     */
    public Kant(Node n, Kant nst) {
      til = n;
      neste = nst;
    }
  }

  /**
   * Nodetypen, som et listeelement. Felt for første kant fra noden, data og indeks.
   */
  class Node {
    Kant kant1;
    Object d;
    int index;

    /**
     * Konstruktor for Node.
     *
     * @param index Nodens indeks.
     */
    public Node(int index) {
      this.index = index;
    }
  }

  /**
   * Metode for å lese  graf fra fil.
   *
   * @param br BufferedReader for å lese fra fil.
   * @throws Exception dersom det oppstår en feil under lesing.
   */
  public void ny_ugraf(BufferedReader br) throws Exception {
    StringTokenizer st = new StringTokenizer(br.readLine());
    N = Integer.parseInt(st.nextToken());
    node = new Node[N];
    for (int i = 0; i < N; ++i) {
      node[i] = new Node(i);
    }
    K = Integer.parseInt(st.nextToken());
    for (int i = 0; i < K; ++i) {
      st = new StringTokenizer(br.readLine());
      int fra = Integer.parseInt(st.nextToken());
      int til = Integer.parseInt(st.nextToken());
      node[fra].kant1 = new Kant(node[til], node[fra].kant1);
    }
  }

  /**
   * Forgj-klassen representerer forgjengerinformasjonen som brukes under DFS-traversering.
   */

  class Forgj {
    int dist;
    Node forgj;
    static int uendelig = 1000000000;

    /**
     * Konstruktør for Forgj.
     */
    public Forgj() {
      dist = uendelig;
    }
  }

  /**
   * Dfs_forgj-klassen utvider Forgj og legger til funnet- og ferdigstid som brukes i DFS.
   */

  class Dfs_forgj extends Forgj {
    int funnet_tid, ferdig_tid;
    static int tid;

    /**
     * Resetter den globale tellevariabelen.
     */
    static void null_tid() {
      tid = 0;
    }

    /**
     * Inkrementerer og returnerer den globale tellevariabelen.
     *
     * @return Oppdatert tid
     */
    static int les_tid() {
      return ++tid;
    }
  }

  /**
   * Initialiserer noder for DFS traversal.
   */
  public void dfs_init() {
    for (int i = N; i-- > 0;) {
      node[i].d = new Dfs_forgj();
    }
    Dfs_forgj.null_tid();
  }

  /**
   * Utfører DFS på en gitt node og legger noden til en stack etter at DFS er ferdig.
   *
   * @param n     Noden som DFS skal utføres på.
   * @param stack Stack for å lagre noder i rekkefølge av ferdigstid.
   */
  public void df_sok(Node n, Stack<Node> stack) {
    Dfs_forgj nd = (Dfs_forgj) n.d;
    nd.funnet_tid = Dfs_forgj.les_tid();
    for (Kant k = n.kant1; k != null; k = k.neste) {
      Dfs_forgj md = (Dfs_forgj) k.til.d;
      if (md.funnet_tid == 0) {
        md.forgj = n;
        md.dist = nd.dist + 1;
        df_sok(k.til, stack);
      }
    }
    nd.ferdig_tid = Dfs_forgj.les_tid();
    stack.push(n);
  }

  /**
   * Transponerer grafen og returnerer en ny graf med reverserte kanter.
   *
   * @return ny graf med reverserte kanter.
   */
  public Node[] transposeGraph() {
    Node[] transposed = new Node[N];
    for (int i = 0; i < N; i++) {
      transposed[i] = new Node(i);
    }

    for (int i = 0; i < N; i++) {
      for (Kant k = node[i].kant1; k != null; k = k.neste) {
        transposed[k.til.index].kant1 = new Kant(transposed[i], transposed[k.til.index].kant1);
      }
    }
    return transposed;
  }

  /**
   * Utfører DFS på den transponerte grafen og samler sterkt sammenkoblede komponenter (SCC).
   *
   * @param n         Noden som DFS skal utføres på.
   * @param component Listen som skal inneholde nodene i den sterkt sammenkoblede komponenten.
   */
  public void df_sok_scc(Node n, List<Node> component) {
    Dfs_forgj nd = (Dfs_forgj) n.d;
    nd.funnet_tid = Dfs_forgj.les_tid();
    component.add(n);
    for (Kant k = n.kant1; k != null; k = k.neste) {
      Dfs_forgj md = (Dfs_forgj) k.til.d;
      if (md.funnet_tid == 0) {
        df_sok_scc(k.til, component);
      }
    }
  }

  /**
   * Finner og skriver ut alle sterkt sammenkoblede komponenter (SCC) i grafen.
   */
  public void findSCCs() {
    Stack<Node> stack = new Stack<>();
    dfs_init();

    // Steg 1: Kjør DFS på den originale grafen og legg noder på stacken i rekkefølge av synkende ferdigstid
    for (int i = 0; i < N; i++) {
      Dfs_forgj nd = (Dfs_forgj) node[i].d;
      if (nd.funnet_tid == 0) {
        df_sok(node[i], stack);
      }
    }

    // Steg 2: Transponer grafen (reverser alle kanter)
    Node[] transposedGraph = transposeGraph();

    // Skriv ut den transponerte grafen med piler
    printTransposedGraphWithArrows(transposedGraph);

    // Steg 3: Kjør DFS på den transponerte grafen i rekkefølge av synkende ferdigstid
    List<List<Node>> sccs = new ArrayList<>();
    Dfs_forgj.null_tid();
    for (int i = 0; i < N; i++) {
      transposedGraph[i].d = new Dfs_forgj();
    }

    while (!stack.isEmpty()) {
      Node n = stack.pop();
      Dfs_forgj nd = (Dfs_forgj) transposedGraph[n.index].d;
      if (nd.funnet_tid == 0) {
        List<Node> component = new ArrayList<>();
        df_sok_scc(transposedGraph[n.index], component);
        sccs.add(component);
      }
    }

    // Steg 4: Skriv ut alle SCCs
    System.out.println("Grafen har " + sccs.size() + " sterkt sammenhengende komponenter.");
    for (int i = 0; i < sccs.size(); i++) {
      System.out.print("Komponent " + (i + 1) + ": ");
      for (Node n : sccs.get(i)) {
        System.out.print(n.index + " ");
      }
      System.out.println();
    }
    System.out.println();
  }

  /**
   * Skriv ut nabolista til grafen.
   */
  public void printGraphWithArrows() {
    System.out.println("Graf (naboliste):");
    for (int i = 0; i < N; i++) {
      System.out.print(i + ": ");
      for (Kant k = node[i].kant1; k != null; k = k.neste) {
        System.out.print(" -> " + k.til.index);
      }
      System.out.println();
    }
    System.out.println();
  }

  /**
   * Skriver ut nabolista representasjonen av den transponerte grafen med piler.
   *
   * @param transposedGraph Den transponerte grafen.
   */
  public void printTransposedGraphWithArrows(Node[] transposedGraph) {
    System.out.println("Transponert graf (naboliste):");
    for (int i = 0; i < N; i++) {
      System.out.print(i + ": ");
      for (Kant k = transposedGraph[i].kant1; k != null; k = k.neste) {
        System.out.print(" -> " + k.til.index);
      }
      System.out.println();
    }
    System.out.println();
  }

  public static void main(String[] args) throws Exception {
    BufferedReader br = new BufferedReader(new FileReader("ø5g1.txt"));
    Oving5 graph = new Oving5();
    graph.ny_ugraf(br);

    graph.printGraphWithArrows();

    graph.findSCCs();
  }
}
