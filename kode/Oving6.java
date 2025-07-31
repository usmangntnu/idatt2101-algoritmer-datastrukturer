import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.util.*;

/**
 * Oving6-klassen implementerer LZW-komprimering og -dekomprimering.
 * Klassen håndterer koding, dekoding, filoperasjoner, og tilbyr et terminalgrensesnitt.
 */
public class Oving6 {

  /**
   * Initialiserer ordboken for koding med ASCII-tegn (0-255).
   * @param ordbokStorrelse Startstørrelsen på ordboken.
   * @return En Map hvor hvert ASCII-tegn er koblet til en heltallskode.
   */
  public static Map<String, Integer> initierKodeOrdbok(int ordbokStorrelse) {
    Map<String, Integer> ordbok = new HashMap<>();
    for (int i = 0; i < ordbokStorrelse; i++) {
      ordbok.put(String.valueOf((char) i), i);
    }
    return ordbok;
  }

  /**
   * Initialiserer ordboken for dekoding med ASCII-tegn (0-255).
   * @param ordbokStorrelse Startstørrelsen på ordboken.
   * @return En Map hvor hver heltallskode er koblet til et ASCII-tegn.
   */
  public static Map<Integer, String> initierDekodeOrdbok(int ordbokStorrelse) {
    Map<Integer, String> ordbok = new HashMap<>();
    for (int i = 0; i < ordbokStorrelse; i++) {
      ordbok.put(i, String.valueOf((char) i));
    }
    return ordbok;
  }

  /**
   * Koder en gitt tekststreng ved bruk av LZW-komprimering.
   * @param input Tekststrengen som skal komprimeres.
   * @return En ArrayList med kodet data som inneholder både heltall og strenger.
   */
  public static ArrayList<Object> kodTekst(String input) {
    int ordbokStorrelse = 256;
    Map<String, Integer> ordbok = initierKodeOrdbok(ordbokStorrelse);
    ArrayList<Object> resultat = new ArrayList<>();
    StringBuilder sekvens = new StringBuilder();

    for (char tegn : input.toCharArray()) {
      sekvens.append(tegn);
      String novarendeStreng = sekvens.toString();

      if (!ordbok.containsKey(novarendeStreng)) {
        String tidligereStreng = novarendeStreng.substring(0, novarendeStreng.length() - 1);

        if (tidligereStreng.length() == 1 && tidligereStreng.charAt(0) > 127) {
          resultat.add(tidligereStreng);
        } else {
          resultat.add(ordbok.get(tidligereStreng));
        }

        if (novarendeStreng.length() > 1) {
          ordbok.put(novarendeStreng, ordbokStorrelse++);
        }

        sekvens = new StringBuilder(String.valueOf(tegn));
      }
    }

    if (!sekvens.isEmpty()) {
      String sisteStreng = sekvens.toString();
      if (sisteStreng.length() == 1 && sisteStreng.charAt(0) > 127) {
        resultat.add(sisteStreng);
      } else {
        resultat.add(ordbok.get(sisteStreng));
      }
    }

    return resultat;
  }

  /**
   * Dekoder en gitt kodet input ved bruk av LZW-dekomprimering.
   * @param kodetInput Kodet data som en ArrayList med både heltall og strenger.
   * @return Den originale, ukomprimerte strengen.
   */
  public static String dekodTekst(ArrayList<Object> kodetInput) {
    int ordbokStorrelse = 256;
    Map<Integer, String> ordbok = initierDekodeOrdbok(ordbokStorrelse);

    StringBuilder resultat = new StringBuilder();
    String forrigeInngang = null;

    for (Object kodeEllerTegn : kodetInput) {
      String novarendeInngang;
      if (kodeEllerTegn instanceof Integer) {
        int kode = (Integer) kodeEllerTegn;
        if (ordbok.containsKey(kode)) {
          novarendeInngang = ordbok.get(kode);
        } else if (kode == ordbokStorrelse) {
          novarendeInngang = forrigeInngang + forrigeInngang.charAt(0);
        } else {
          throw new IllegalArgumentException("Ugyldig kodet input: kode ikke i ordbok");
        }
      } else {
        novarendeInngang = (String) kodeEllerTegn;
      }

      resultat.append(novarendeInngang);

      if (forrigeInngang != null && !novarendeInngang.isEmpty()) {
        ordbok.put(ordbokStorrelse++, forrigeInngang + novarendeInngang.charAt(0));
      }

      forrigeInngang = novarendeInngang;
    }

    return resultat.toString();
  }

  /**
   * Skriver den kodede dataen til en fil med støtte for ulike datatyper.
   * @param filnavn Filnavnet som skal skrives til.
   * @param kodetData Den kodede dataen som en ArrayList med både heltall og strenger.
   * @throws IOException Hvis en I/O-feil oppstår.
   */
  public static void skrivKodetFil(String filnavn, ArrayList<Object> kodetData) throws IOException {
    try (DataOutputStream output = new DataOutputStream(new FileOutputStream(filnavn))) {
      boolean brukerInt = false;

      for (Object kodeEllerTegn : kodetData) {
        if (kodeEllerTegn instanceof Integer) {
          int kode = (Integer) kodeEllerTegn;
          if (kode > 65535 && !brukerInt) {
            output.writeByte(2);
            brukerInt = true;
          }
          output.writeByte(0);
          if (brukerInt) {
            output.writeInt(kode);
          } else {
            output.writeShort(kode);
          }
        } else if (kodeEllerTegn instanceof String) {
          output.writeByte(1);
          output.writeUTF((String) kodeEllerTegn);
        }
      }
    }
  }

  /**
   * Leser kodet data fra en fil med støtte for ulike datatyper.
   * @param filnavn Filnavnet som skal leses fra.
   * @return En ArrayList som inneholder den kodede dataen med både heltall og strenger.
   * @throws IOException Hvis en I/O-feil oppstår.
   */
  public static ArrayList<Object> lesKodetFil(String filnavn) throws IOException {
    ArrayList<Object> kodetData = new ArrayList<>();
    boolean brukerInt = false;

    try (DataInputStream input = new DataInputStream(new FileInputStream(filnavn))) {
      while (input.available() > 0) {
        byte flagg = input.readByte();
        if (flagg == 0) {
          if (brukerInt) {
            kodetData.add(input.readInt());
          } else {
            kodetData.add(input.readShort() & 0xFFFF);
          }
        } else if (flagg == 1) {
          kodetData.add(input.readUTF());
        } else if (flagg == 2) {
          brukerInt = true;
        } else {
          throw new IOException("Ugyldig flagg i kodet fil");
        }
      }
    } catch (FileNotFoundException e) {
      System.out.println("\"" + filnavn + "\"" + " ble ikke funnet. Sørg for at filen finnes i samme katalog, og prøv igjen.");
    }
    return kodetData;
  }

  /**
   * Leser innholdet av en fil som en UTF-8-streng.
   * @param filnavn Filen som skal leses.
   * @return Filinnholdet som en streng.
   */
  private static String lesFilSomStreng(String filnavn) {
    try {
      return Files.readString(new File(filnavn).toPath(), StandardCharsets.UTF_8);
    } catch (NoSuchFileException e) {
      return null;
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * Skriver en streng til en fil i UTF-8-koding.
   * @param filnavn Filnavnet som skal skrives.
   * @param data Strengdataen som skal skrives.
   * @throws IOException Hvis en I/O-feil oppstår.
   */
  private static void skrivTilFil(String filnavn, String data) throws IOException {
    Files.writeString(new File(filnavn).toPath(), data, StandardCharsets.UTF_8);
  }

  /**
   * Starter terminalgrensesnittet for komprimerings- og dekomprimeringsalternativer.
   * @throws IOException Hvis en I/O-feil oppstår.
   */
  public static void terminalGrensesnitt() throws IOException {
    Scanner sc = new Scanner(System.in);

    while (true) {
      visMeny();
      String valg = sc.nextLine();

      switch (valg) {
        case "1":
          utforKomprimering(sc);
          break;
        case "2":
          utforDekomprimering(sc);
          break;
        case "3":
          System.out.println("Takk for at du brukte våre tjenester.");
          System.exit(0);
          return;
        default:
          System.out.println("Ugyldig valg, prøv igjen.");
          break;
      }
    }
  }

  /**
   * Viser menyen med alternativer for brukeren.
   */
  private static void visMeny() {
    System.out.println("Hva ønsker du å gjøre?");
    System.out.println("1. Komprimere en fil");
    System.out.println("2. Pakke ut en fil");
    System.out.println("3. Avslutt");
  }

  /**
   * Hovedmetoden som starter programmet.
   * @param args Kommandolinjeargumenter.
   */
  public static void main(String[] args) {
    try {
      terminalGrensesnitt();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Håndterer komprimering ved å lese, kode, og lagre resultatet i en fil.
   * @param sc Scanner for brukerinput.
   * @throws IOException Hvis en I/O-feil oppstår.
   */
  private static void utforKomprimering(Scanner sc) throws IOException {
    System.out.println("Hva er navnet på filen du ønsker å komprimere?");
    String filnavn = sc.nextLine();
    String filInnhold = lesFilSomStreng(filnavn);
    if (filInnhold == null) {
      System.out.println("Kunne ikke lese fil eller fil ikke funnet. Prøv igjen.");
      return;
    }
    ArrayList<Object> kodet = kodTekst(filInnhold);
    skrivKodetFil("komprimert.txt", kodet);
    System.out.println("Kodet data skrevet til komprimert.txt");
  }

  /**
   * Håndterer dekomprimering ved å lese kodet data, dekode den og lagre resultatet.
   * @param sc Scanner for brukerinput.
   * @throws IOException Hvis en I/O-feil oppstår.
   */
  private static void utforDekomprimering(Scanner sc) throws IOException {
    System.out.println("Hva er navnet på filen du ønsker å pakke ut?");
    String filnavn = sc.nextLine();
    ArrayList<Object> kodetData = lesKodetFil(filnavn);
    String dekodetTekst = dekodTekst(kodetData);
    skrivTilFil("utpakket.txt", dekodetTekst);
    System.out.println("Dekodet data skrevet til utpakket.txt");
  }
}
