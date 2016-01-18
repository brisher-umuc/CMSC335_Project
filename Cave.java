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

        int i = 0;
        for (Party party: parties) {
            if (i == 0) {
                retval += "Party: " + party.getName() + "\n";
            }
            else {
                // i have a regex replacing \n\n with \n
                //this just tricks it into letting me have 2 newlines when i want them
                retval += "\n\n\nParty: " + party.getName() + "\n";
            }
            retval += party.toString();
            ++i;
        }

        retval += "Unheld Items:\n";

        for (CaveElement element: elements) {
            retval += "-> " + element.toString() + "\n";
        }

        return retval;
    }
}  // end Cave
