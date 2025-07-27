package tech.insight.spring;

/**
 *  
 */
public class Util {
    private static boolean debug = false;

    public static void Dprintf(String args) {
        if (debug) {
            System.out.println(args);
        }
    }
}
