
import java.util.Random;

/**
 * Hovedklassen  inneholder testprogrammet for å sammenligne
 * to varianter av quicksort-algoritmer:
 * - QuickSort med en pivot
 * - DualPivotQuickSort med to pivot.
 * <p>
 * Programmet tester sortering på ulike datasett:
 * - Tilfeldig genererte data
 * - Datasett med mange duplikater
 * - Allerede sortert data
 * - Baklengs sortert data
 * <p>
 * Tester inkluderer sjekksum- og rekkefølgetester.
 * Tidtaking brukes for å evaluere hastigheten på hver sorteringsalgoritme.
 */
public class Oving3 {

  /**
   * Hovedmetoden for programmet. Genererer datasett og tester
   * sorteringsalgoritmene QuickSort og DualPivotQuickSort.
   */
  public static void main(String[] args) {

    // Antall elementer i datasettet
    int n = 50_000_000;

    // Generer tilfeldige  datasett
    int[] randomArray = generateRandomArray(n);
    int[] duplicateArray = generateDuplicateArray(n);
    int[] sortedArray = generateSortedArray(n);
    int[] reverseArray = generateReverseSortedArray(n);

    // Tester Enkel-QuickSort-implementasjon på ulike datasett
    testSortWithTiming(randomArray.clone(), "QuickSort", "Tilfeldig data");
    testSortWithTiming(duplicateArray.clone(), "QuickSort", "Mange duplikater");
    testSortWithTiming(sortedArray.clone(), "QuickSort", "Sortert fra før");
    testSortWithTiming(reverseArray.clone(), "QuickSort", "Baklengs sortert");

    // Tester Dual-Pivot-QuickSort-implementasjon på ulike datasett
    testSortWithTiming(randomArray.clone(), "DualPivotQuickSort", "Tilfeldig data");
    testSortWithTiming(duplicateArray.clone(), "DualPivotQuickSort", "Mange duplikater");
    testSortWithTiming(sortedArray.clone(), "DualPivotQuickSort", "Sortert fra før");
    testSortWithTiming(reverseArray.clone(), "DualPivotQuickSort", "Baklengs sortert");
  }

  /**
   * Tester sorteringsalgoritme med tidtaking, sjekksum- og rekkefølgetest.
   *
   * @param arr      Arrayet som skal sorteres.
   * @param sortType Typen sortering (QuickSort/DualPivotQuickSort).
   * @param dataType Type data som testes (f.eks "Tilfeldig data").
   */
  public static void testSortWithTiming(int[] arr, String sortType, String dataType) {
    System.out.println("\nTester " + sortType + " på " + dataType + "...");

    // Beregn sjekksum før sortering
    int checksumBefore = calculateChecksum(arr);
    System.out.println("Sjekksum før sortering: " + checksumBefore);

    // Start tidtaking
    long startTime = System.currentTimeMillis();

    // Utfør riktig sorteringsalgoritme
    if (sortType.equals("QuickSort")) {
      quicksort(arr, 0, arr.length - 1);
    } else if (sortType.equals("DualPivotQuickSort")) {
      dualPivotQuickSort(arr, 0, arr.length - 1);
    }

    // Slutt tidtaking
    long endTime = System.currentTimeMillis();
    long duration = endTime - startTime;

    // Beregn sjekksum etter sortering
    int checksumAfter = calculateChecksum(arr);
    System.out.println("Sjekksum etter sortering: " + checksumAfter);

    // Sjekk om sjekksummene stemmer
    if (checksumBefore == checksumAfter) {
      System.out.println("Sjekksummene stemmer. Ingen datatap.");
    } else {
      System.out.println("Sjekksummene stemmer ikke. Det har skjedd en feil.");
    }

    // Sjekk om arrayet er korrekt sortert
    if (isSorted(arr)) {
      System.out.println("Arrayet er korrekt sortert.");
    } else {
      System.out.println("Arrayet er IKKE korrekt sortert.");
    }

    // Tid som det tok å sortere
    System.out.println(sortType + " på " + dataType + " tok " + duration + " millisekunder.");
  }

  /**
   * Beregner sjekksummen for arryet.
   * Dette brukes til å sikre at ingen data har gått tapt under sorteringen.
   *
   * @param arr Arrayet som sjekksummen skal beregnes for.
   * @return Summen av alle elementene i arrayet.
   */
  public static int calculateChecksum(int[] arr) {
    int sum = 0;
    for (int i : arr) {
      sum += i;
    }
    return sum;
  }

  /**
   * Sjekker om et array er korrekt sortert i stigende rekkefølge.
   *
   * @param arr Arrayet som skal sjekkes.
   * @return True hvis arrayet er sortert, ellers false.
   */
  public static boolean isSorted(int[] arr) {
    for (int i = 0; i < arr.length - 1; i++) {
      if (arr[i] > arr[i + 1]) {
        return false;
      }
    }
    return true;
  }

  /**
   * Genererer et array med tilfeldige heltall mellom 0 og n.
   *
   * @param n Antall elementer i arrayet.
   * @return Et array med tilfeldige heltall.
   */
  public static int[] generateRandomArray(int n) {
    Random rand = new Random();
    int[] arr = new int[n];
    for (int i = 0; i < n; i++) {
      arr[i] = rand.nextInt(n);
    }
    return arr;
  }

  /**
   * Genererer et array hvor annenhvert element er en duplikat.
   *
   * @param n Antall elementer i arrayet.
   * @return Et array med mange duplikater.
   */
  public static int[] generateDuplicateArray(int n) {
    int[] arr = new int[n];
    for (int i = 0; i < n; i++) {
      arr[i] = (i % 2 == 0) ? 1 : i;
    }
    return arr;
  }

  /**
   * Genererer et sortert array i stigende rekkefølge.
   *
   * @param n Antall elementer i arrayet.
   * @return Et sortert array.
   */
  public static int[] generateSortedArray(int n) {
    int[] arr = new int[n];
    for (int i = 0; i < n; i++) {
      arr[i] = i;
    }
    return arr;
  }

  /**
   * Genererer et array som er sortert i synkende rekkefølge.
   *
   * @param n Antall elementer i arrayet.
   * @return Et baklengs sortert array.
   */
  public static int[] generateReverseSortedArray(int n) {
    int[] arr = new int[n];
    for (int i = 0; i < n; i++) {
      arr[i] = n - i;
    }
    return arr;
  }

    // ------------------- Enkel-QuickSort-implementasjon -------------------

    /**
     * Implementerer QuickSort med en pivot.
     * <p>
     * Denne metoden deler arrayet ved å bruke en pivot, og sorterer de to delene rekursivt.
     * Hvis antallet elementer i den nåværende delen av arrayet er 3 eller færre, blir
     * {@link #median3sort(int[], int, int)} brukt for å håndtere denne delen.
     *
     * @param t Arrayet som skal sorteres.
     * @param v Den laveste indeksen i delen av arrayet som skal sorteres.
     * @param h Den høyeste indeksen i delen av arrayet som skal sorteres.
     */
    public static void quicksort(int[] t, int v, int h) {
      if (h - v > 2) {
        int delepos = splitt(t, v, h);
        quicksort(t, v, delepos - 1);
        quicksort(t, delepos + 1, h);
      } else {
        median3sort(t, v, h);
      }
    }

    /**
     * Hjelpemetode for å finne medianen av tre elementer og sortere disse.
     * <p>
     * Denne metoden brukes til å forbedre pivotvalg i QuickSort-algoritmen ved å sikre at pivoten
     * er nær medianen av tre elementer: venstre, midt og høyre. Dette gir en bedre pivotvalg og forbedrer ytelsen.
     *
     * @param t Arrayet som inneholder elementene.
     * @param v Den laveste indeksen i delen av arrayet som skal vurderes.
     * @param h Den høyeste indeksen i delen av arrayet som skal vurderes.
     * @return Indeksen til pivot-elementet.
     */
    private static int median3sort(int[] t, int v, int h) {
      int m = (v + h) / 2;
      if (t[v] > t[m]) bytt(t, v, m);
      if (t[m] > t[h]) {
        bytt(t, m, h);
        if (t[v] > t[m]) bytt(t, v, m);
      }
      return m;
    }

    /**
     * Deler arrayet rundt pivoten og returnerer posisjonen til pivoten.
     * <p>
     * Denne metoden bruker pivot-elementet (som er medianen av tre elementer) til å dele arrayet i to
     * deler, hvor alle elementene til venstre for pivoten er mindre enn pivoten og alle elementene til
     * høyre er større. Metoden returnerer posisjonen til pivot-elementet etter deling.
     *
     * @param t Arrayet som skal deles.
     * @param v Den laveste indeksen i delen av arrayet som skal deles.
     * @param h Den høyeste indeksen i delen av arrayet som skal deles.
     * @return Indeksen til pivot-elementet etter deling.
     */
    private static int splitt(int[] t, int v, int h) {
      int iv, ih;
      int m = median3sort(t, v, h);
      int dv = t[m];
      bytt(t, m, h - 1);
      for (iv = v, ih = h - 1;;) {
        while (t[++iv] < dv);
        while (t[--ih] > dv);
        if (iv >= ih) break;
        bytt(t, iv, ih);
      }
      bytt(t, iv, h - 1);
      return iv;
    }

    /**
     * Bytter plass på to elementer i arrayet.
     *
     * @param t Arrayet som inneholder elementene.
     * @param i Indeksen til det første elementet.
     * @param j Indeksen til det andre elementet.
     */
    private static void bytt(int[] t, int i, int j) {
      int k = t[j];
      t[j] = t[i];
      t[i] = k;
    }

    // ------------------- Dual-Pivot-Quicksort-implementasjon -------------------

    /**
     * Implementerer Dual-Pivot QuickSort-algoritmen med to pivoter.
     * <p>
     * Denne metoden bruker to pivoter for å dele arrayet i tre deler og sorterer hver del rekursivt.
     * Algoritmen har potensial til å gi bedre ytelse enn enkel QuickSort ved å redusere antall
     * rekursive kall og forbedre partisjoneringen.
     *
     * @param arr Arrayet som skal sorteres.
     * @param low Den laveste indeksen i delen av arrayet som skal sorteres.
     * @param high Den høyeste indeksen i delen av arrayet som skal sorteres.
     */
    public static void dualPivotQuickSort(int[] arr, int low, int high) {
      if (low < high) {

        //Forbedret pivotvalg for å unngå skjevfordeling
        int pivot1 = low + (high - low) / 3;
        int pivot2 = high - (high - low) / 3;

        swap(arr, low, pivot1);
        swap(arr, high, pivot2);

        int[] piv = partition(arr, low, high);

        //Forbedret ytelse ved å redusere antall rekursive kall ved mange duplikater
        dualPivotQuickSort(arr, low, piv[0] - 1);
        if (arr[piv[0]] != arr[piv[1]]) {
          dualPivotQuickSort(arr, piv[0] + 1, piv[1] - 1);
        }
        dualPivotQuickSort(arr, piv[1] + 1, high);
      }
    }

    /**
     * Deler arrayet ved hjelp av to pivoter og returnerer posisjonene til de to pivotene.
     * <p>
     * Denne metoden bruker to pivoter for å dele arrayet i tre deler:
     * - Elementer mindre enn den første pivoten.
     * - Elementer mellom de to pivotene.
     * - Elementer større enn den andre pivoten.
     * Metoden returnerer indekser til de to pivotene etter deling.
     *
     * @param arr Arrayet som skal deles.
     * @param low Den laveste indeksen i delen av arrayet som skal deles.
     * @param high Den høyeste indeksen i delen av arrayet som skal deles.
     * @return Et array som inneholder posisjonene til de to pivotene.
     */
    private static int[] partition(int[] arr, int low, int high) {
      if (arr[low] > arr[high]) swap(arr, low, high);

      int j = low + 1;
      int g = high - 1, k = low + 1;
      int p = arr[low], q = arr[high];

      while (k <= g) {
        if (arr[k] < p) {
          swap(arr, k, j);
          j++;
        } else if (arr[k] >= q) {
          while (arr[g] > q && k < g) {
            g--;
          }
          swap(arr, k, g);
          g--;

          if (arr[k] < p) {
            swap(arr, k, j);
            j++;
          }
        }
        k++;
      }
      j--;
      g++;

      swap(arr, low, j);
      swap(arr, high, g);

      return new int[]{j, g};
    }

    /**
     * Bytter plass på to elementer i arrayet.
     *
     * @param arr Arrayet som inneholder elementene.
     * @param i Indeksen til det første elementet.
     * @param j Indeksen til det andre elementet.
     */
    private static void swap(int[] arr, int i, int j) {
      int temp = arr[i];
      arr[i] = arr[j];
      arr[j] = temp;
    }
  }