import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;
import java.util.HashMap;
import java.util.Date;

public class Oving4 {
  public static void main(String[] args) {
    System.out.println("---------------- Del 1: ----------------\n");
    del1();
    System.out.println("\n--------------- Del 2:-----------------\n");
    del2();
  }

  private static void del1() {
    // Path to the 'navn.txt' file
    String filePath = "navn.txt";
    int size = 127; // Prime number
    int names = 0;

    HashTableString hashTable = new HashTableString(size);

    try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
      String name;
      // Read file line by line
      while ((name = br.readLine()) != null) {
        hashTable.add(name.trim()); // Add each name to the hash table, trimming any extra spaces
        names++;
      }
    } catch (IOException e) {
      System.err.println("Error reading file: " + e.getMessage());
    }

    // hashTable.printTable();
    // Check if names exist in table
    System.out.println("HashTable contains Sander Sandvik Nessa: " + hashTable.contains("Sander Sandvik Nessa", true));
    System.out.println("HashTable contains Garv Sood: " + hashTable.contains("Garv Sood", true));
    System.out.println("HashTable contains Usman Ghafoorzai: " + hashTable.contains("Usman Ghafoorzai", true));
    System.out.println("HashTable contains negativeTest: " + hashTable.contains("negativeTest", true));
    System.out.println("Collisions: " + hashTable.getCollisionCounter());
    System.out.println("Load factor: " + (float) names / size);
    System.out.println("Collisions per name: " + (float) hashTable.getCollisionCounter() / names);
  }

  private static void del2() {

    int[] table = new int[10_000_000]; 
    Random rand = new Random();
  
    // Fill the table with random numbers
    for(int i = 0; i < table.length; i++) {
      table[i] = rand.nextInt(Integer.MAX_VALUE);
    }
  
    // Create a hash table
    HashTableDel2 ht = new HashTableDel2();
  
    // Time measurement for insert the numbers into our hash table
    long startTime = new Date().getTime();
    for(int number : table) {
      ht.insert(number);
    }
    long endTime = new Date().getTime();
    System.out.println("Tid for innsetting i egen hashtabell: " + (endTime - startTime) + " ms");
  
    // Load factor and collisions
    System.out.println("Last faktor: " + ht.loadFactor());
    System.out.println("Antall kollisjoner: " + ht.getCollisions());
    System.out.println("Gjennomsnittlig antall kollisjoner per innsetting: " + (double) ht.getCollisions() / table.length);
  
    // Time measurement for insert the numbers into Java's HashMap
    HashMap<Integer, Integer> hm = new HashMap<>(10_999_997); // Using initial size equal to ht's size
    startTime = new Date().getTime();
    for(int number : table) {
      hm.put(number, number);
    }
    endTime = new Date().getTime();
    System.out.println("Tid for innsetting i Javas HashMap: " + (endTime - startTime) + " ms");
  }
}

class Node<T> {
  private T value;
  private Node<T> next;

  public Node(T value) {
    this.value = value;
    this.next = null;
  }

  public Node(T value, Node<T> next) {
    this.value = value;
    this.next = next;
  }

  public T getValue() {
    return value;
  }

  public void setValue(T value) {
    this.value = value;
  }

  public Node<T> getNext() {
    return next;
  }

  public void setNext(Node<T> next) {
    this.next = next;
  }
}

class LinkedListGeneric<T> {
  private Node<T> head;
  private int size;

  public Node<T> getHead() {
    return head;
  }

  public int getSize() {
    return size;
  }

  public void addInFront(T value) {
    head = new Node<T>(value, head);
    size++;
  }

  public void addInBack(T value) {
    if (head != null) {
      Node<T> currentNode = head;
      while (currentNode.getNext() != null) {
        currentNode = currentNode.getNext();
      }
      currentNode.setNext(new Node<T>(value));
    } else {
      head = new Node<T>(value);
    }
    size++;
  }

  public Node<T> remove(Node<T> n) {
    Node<T> prevNode = null;
    Node<T> currentNode = head;
    while (currentNode != null && currentNode != n) {
      prevNode = currentNode;
      currentNode = currentNode.getNext();
    }
    if (currentNode != null) {
      if (prevNode != null) {
        prevNode.setNext(currentNode.getNext());
      } else {
        head = currentNode.getNext();
      }
      currentNode.setNext(null);
      size--;
      return currentNode;
    }
    return null; // Node does not exist
  }

  public Node<T> getNodeNr(int nr) {
    Node<T> currentNode = head;
    if (nr < size) {
      for (int i = 0; i < nr; i++) {
        currentNode = currentNode.getNext();
      }
      return currentNode;
    }
    return null; // Node does not exist
  }

  // Overloading contains to have not print collisions be the standard
  public boolean contains(T value) {
    return contains(value, false);
  }

  public boolean contains(T value, boolean printCollisions) {
    Node<T> currentNode = head;
    boolean collisionPrinted = false;

    while (currentNode != null) {
      // Only prints a collision if value isn't in the head(first) and it hasn't
      // already found a collision
      if (printCollisions && !collisionPrinted && currentNode != head) {
        // Print out the first collision when traversing the list
        System.out.println("Collision during search: " + head.getValue() + " and " + value);
        collisionPrinted = true;
      }

      if (currentNode.getValue().equals(value)) {
        return true;
      }
      currentNode = currentNode.getNext();
    }
    return false; // Value not found
  }

  public boolean isEmpty() {
    return head == null;
  }

  public void clearList() {
    head = null;
    size = 0;
  }
}

class HashTableString {
  private LinkedListGeneric<String>[] array;
  private int size;
  private int collisionCounter;

  @SuppressWarnings("unchecked")
  public HashTableString(int size) {
    this.size = size;
    array = new LinkedListGeneric[size];
    for (int i = 0; i < size; i++) {
      array[i] = new LinkedListGeneric<>();
    }
    collisionCounter = 0;
  }

  public int HashFunc(String value) {
    int hash = 0;
    int prime = 31;

    for (int i = 0; i < value.length(); i++) {
      // Compute the hash incrementally
      hash = hash * prime + value.charAt(i);
    }

    // Ensure the result is non-negative and fits within the table size
    return Math.abs(hash) % size;
  }

  public void add(String value) {
    int index = HashFunc(value); // Get the index using the hash function
    LinkedListGeneric<String> list = array[index]; // Get the linked list at that index

    // Only add if the value is not already present
    if (!list.contains(value)) {
      if (!list.isEmpty()) {
        System.out.println(
            "Collision during insertion: " + list.getHead().getValue() + " and " + value + " at index " + index);
        collisionCounter++;
      }
      list.addInFront(value); // Add the value to the linked list
    }
  }

  // Overloading contains to have not print collisions be the standard
  public boolean contains(String value) {
    return contains(value, false);
  }

  public boolean contains(String value, boolean printCollisions) {
    int index = HashFunc(value); // Get the index using the hash function
    LinkedListGeneric<String> list = array[index]; // Get the linked list at that index
    return list.contains(value, printCollisions); // Check if the value is in the list
  }

  public void printTable() {
    for (int i = 0; i < size; i++) {
      LinkedListGeneric<String> list = array[i];
      System.out.print("Index " + i + ": ");

      if (list.getSize() == 0) {
        System.out.println("Empty");
      } else {
        Node<String> currentNode = list.getHead();
        while (currentNode != null) {
          System.out.print(currentNode.getValue());

          // Move to the next node
          currentNode = currentNode.getNext();

          // Print the arrow only if there is another node
          if (currentNode != null) {
            System.out.print(" --> ");
          }
        }
        // New line after each index
        System.out.println();
      }
    }
  }

  public int getCollisionCounter() {
    return collisionCounter;
  }
}

class HashTableDel2 {
  private final int size = 10_999_997; // 10_999_997 is a prime number less than 35% more than 10 million
  private int[] table;
  private int noOfElements = 0;   // Keeps track of the number of elements in the hash table
  private int collisions = 0;      // Keeps track of the number of collisions in the hash table

  public HashTableDel2() {
    table = new int[size];
  }

  private int hash1(int key) {
    return key % size;
  }

  private int hash2(int key) {
    return 1 + (key % (size - 1));
  }

  public void insert(int number) {
    int pos = hash1(number);    // Calculate the initial position
    if(table[pos] == 0) {
      table[pos] = number;    // No collision, insert the number
    } else {
      // Double hashing
      collisions++;
      int h2 = hash2(number);
      while(table[pos] != 0) {
        collisions++;
        pos = (pos + h2) % size;
      }
      table[pos] = number;
    }
    noOfElements++;
  }

  public double loadFactor() {
    return (double) noOfElements / size;
  }

  public int getCollisions() {
    return collisions;
  }
}