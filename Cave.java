/**
 * File: Cave
 * Date: 1/7/16
 * Author: ben risher
 * Purpose:  Cave class to describe a cave in the Sorcerer's Cave game
 */
import java.util.ArrayList;

public class Cave extends Object {
    ArrayList<Party> parties = new ArrayList<Party>();
    ArrayList<CaveElement> elements = new ArrayList<CaveElement>();

    public String toString() {
        String retval = "";

        for (Party party: parties) {
            retval += party.toString() + "\n";
        }

        retval += "Unheld Items:\n";

        for (CaveElement element: elements) {
            retval += "--> " + element.toString() + "\n";
        }

        return retval;
    }
}  // end Cave
