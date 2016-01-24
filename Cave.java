/**
 * File: Cave
 * Date: 1/7/16
 * Author: ben risher
 * Purpose:  Cave class to describe a cave in the Sorcerer's Cave game
 */
import java.util.ArrayList;

public class Cave extends Object {
    private ArrayList<Party> parties = new ArrayList<Party>();
    private ArrayList<CaveElement> elements = new ArrayList<CaveElement>();

    public ArrayList<Party> getParties() {
        return parties;
    }

    public void setParties(ArrayList<Party> parties) {
        this.parties = parties;
    }

    public ArrayList<CaveElement> getElements() {
        return elements;
    }

    public void setElements(ArrayList<CaveElement> elements) {
        this.elements = elements;
    }

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

        if (this.elements.size() > 0) {
            retval += "\n\n\nOrphaned Items and Creatures:\n";
            for (CaveElement element: elements) {
                retval += "-> " + element.toString() + "\n";
            }
        }


        return retval;
    }
}  // end Cave
