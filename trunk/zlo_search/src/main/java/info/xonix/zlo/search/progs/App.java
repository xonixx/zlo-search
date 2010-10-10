package info.xonix.zlo.search.progs;

import java.util.Scanner;

/**
 * Author: Vovan
 * Date: 17.06.2008
 * Time: 0:24:01
 */
public class App {
    protected static String getSiteName() {
        System.out.println("Enter site to set correct lastIndexed or e to exit");
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }
}
