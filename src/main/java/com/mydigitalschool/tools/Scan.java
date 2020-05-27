package com.mydigitalschool.tools;

import java.util.InputMismatchException;
import java.util.Scanner;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Scan {

  private final Scanner sc;

  private Scan(){
    sc = new Scanner(System.in);
  }

  private static Scan INSTANCE = null;

  public static Scan getInstance()
  {
      if (INSTANCE == null)
      {
          synchronized(Scan.class)
          {
              if (INSTANCE == null)
              {   INSTANCE = new Scan();
              }
          }
      }
      return INSTANCE;
  }

  public Integer selectInt(final String msg, final Object[] choices, final Integer min, final Integer max) {
    Integer result = null;
    do {
      System.out.println(msg);
      for (Object choice : choices) {
        System.out.println(choice.toString());
      }
      try {
        result = this.sc.nextInt();
      } catch (InputMismatchException e) {
        System.err.println(e.getMessage());
      }

    } while (result < min || result > max);

    return result;
  }

  public String selectString(final String msg, final Object[] choices, final List<String> selections) {
    String result = null;
    do {
      System.out.println(msg);
      for (Object choice : choices) {
        System.out.println(choice.toString());
      }
      try {
        result = this.sc.nextLine();
      } catch (InputMismatchException e) {
        System.err.println(e.getMessage());
      }

    } while (!selections.contains(result));

    return result;
  }

  public String selectStringFromIntChoice(final String msg, final Object[] choices, final Map<Integer,String> selections) {
    Integer selected = null;

    do {
      System.out.println(msg);
      for (Object choice : choices) {
        System.out.println(choice.toString());
      }
      try {
        selected = this.sc.nextInt();
      } catch (InputMismatchException e) {
        System.err.println(e.getMessage());
      }

    } while (!selections.containsKey(selected));

    return selections.get(selected);
  }

  public String inputString() {
    if (this.sc.hasNextLine()) {
      this.sc.nextLine();
    }

    return this.sc.nextLine();
  }

  public Integer inputInt() {
    Integer result = null;

    try {
      result = this.sc.nextInt();
    } catch (Exception e) {
      System.err.println(e.getMessage());
      result = inputInt();
    }

    return result;
  }
}