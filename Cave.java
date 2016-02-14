/**
 * File: Cave
 * Date: 1/7/16
 * Author: ben risher
 * Purpose:  Cave class to describe a cave in the Sorcerer's Cave game
 */
import java.util.ArrayList;

public class Cave {
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

        for (Party party: parties) {
            retval += party.toString();
        }

        if (this.elements.size() > 0) {
            retval += "Orphaned Items and Creatures:\n";
            for (CaveElement element: elements) {
                retval += element.toString() + "\n";
            }
        }
        return retval;
    }
}  // end Cave
