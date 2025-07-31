import  java.util.Random;

public class BestTid {
  public static void main(String[] args) {

    // initialiserer en array med forandringer i aksjekursen
    int[] forandringer = {-1, 3, -9, 2, 2, -1, 2, -1, -5};

    // kaller metoden finnBestDag og lagrer resultatet i en array
    int[] res = finnBestDag(forandringer);

    System.out.println("Beste kjøpsdag: " + res[0]);
    System.out.println("Beste salgsdag: " + res[1]);
    System.out.println("Maksimal fortjeneste: " + res[2]);

    // tester
    testMethod(10_000, 5);
    testMethod(100_000, 5);
    testMethod(1_000_000, 5);
    testMethod(10_000_000, 5);
    testMethod(100_000_000, 5);
    testMethod(1_000_000_000, 5);
  }

  // metode som tar inn en array med forandringer
  // og returnerer en array med beste kjøps- og salgsdag
  // samt  best fortjeneste
  public static int[] finnBestDag(int[] forandringer) {

    // deklarerer og initialiserer variabler
    int minPrisIndeks = 0;
    int maksProfit = 0;
    int kjopsDag = 0;
    int salgsDag = 0;
    int currentPris = 0;
    int minPris = 0;

    // itererer gjennom arrayen, oppdaterer den akkumelerte prisen
    // ved å legge til dagens forandring
    // sjekker om akkumulert pris er mindre enn minste pris
    // oppdaterer i så fall minste pris og indeks
    for (int i = 0; i < forandringer.length; i++) {
      currentPris += forandringer[i];
      if (currentPris < minPris) {
        minPris = currentPris;
        minPrisIndeks = i;
      }

      // kalkulerer profit ved å trekke fra minste pris fra akkumulert pris
      // dersom profit er større enn maksProfit oppdateres maksProfit, kjøpsdag og salgsdag
      int profit = currentPris - minPris;
      if (profit > maksProfit) {
        maksProfit = profit;
        kjopsDag = minPrisIndeks + 1;
        salgsDag = i + 1;
      }
    }
    return new int[]{kjopsDag, salgsDag, maksProfit};
  }

  public static void testMethod(int N, int iterations) {
    Random r = new Random();
    int[] forandringer = new int[N];
    for (int i = 0; i < N; i++) {
      forandringer[i] = r.nextInt(11) - 5; // random number between -5 and 5
    }

    long averageTime = 0;
    for (int i = 0; i < iterations; i++) {
      long startTime = System.nanoTime();
      finnBestDag(forandringer);
      long endTime = System.nanoTime();
      averageTime += endTime - startTime;
    }
    averageTime /= iterations;
    System.out.printf("Time for N=%d: %d ns%n", N, averageTime);
  }
}