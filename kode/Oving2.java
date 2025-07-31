public class Oving2 {

  // Tidskompelkitet θ(n) - lineær rekursjon
  private static double metode1(double x, int n) {
    if (n == 1) {
      return x;
    } else  {
      return x * metode1(x, n - 1);
    }
  }
  public  static double Metode1(double x, int n) {
    return metode1(x, n);
  }

  // Tidskompleksitet θ(log n) - eksponentiell rekursjon
  private  static double metode2(double x, int n) {
    if (n == 1) {
      return x;
    } else if (n % 2 == 0) {
      return metode2(x, n / 2) * metode2(x, n / 2);
    } else {
      return x * metode2(x, n - 1);
    }
  }
  public static double Metode2(double x, int n) {
    return metode2(x, n);
  }

  // Tidskompleksitet θ(1) - konstant tid
  private  static double metode3(double x, int n) {
    return Math.pow(x, n);
  }
  public static double Metode3(double x, int n) {
    return metode3(x, n);
  }

  public static void main(String[] args) {
   System.out.println("Metode 1: " + Metode1(5, 11));
   System.out.println("Metode 2: " + Metode2(5, 11));
   System.out.println("Metode 3: " + Metode3(5, 11));

    double x = 1.02;
    int[] nValues = {5000};
    int iterations = 10000;

    for (int n : nValues) {
      System.out.println("\nFor n = " + n + ":");

      long totalTid1 = 0, totalTid2 = 0, totalTid3 = 0;
      for (int i = 0; i < iterations; i++) {
        long startTid = System.nanoTime();
        Metode1(x, n);
        long sluttTid = System.nanoTime();
        totalTid1 += sluttTid - startTid;

        startTid = System.nanoTime();
        Metode2(x, n);
        sluttTid = System.nanoTime();
        totalTid2 += sluttTid - startTid;

        startTid = System.nanoTime();
        Metode3(x, n);
        sluttTid = System.nanoTime();
        totalTid3 += sluttTid - startTid;
      }

      System.out.println("Gjennomsnittlig kjøretid for Metode 1: " + (totalTid1 / iterations) + " nanosekunder");
      System.out.println("Gjennomsnittlig kjøretid for Metode 2: " + (totalTid2 / iterations) + " nanosekunder");
      System.out.println("Gjennomsnittlig kjøretid for Metode 3: " + (totalTid3 / iterations) + " nanosekunder");
    }
  }
}