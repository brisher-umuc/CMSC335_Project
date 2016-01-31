/**
 * File: Party
 * Date: 1/7/16
 * Author: ben risher
 * Purpose:  Party class to describe parties in the Sorcerer's Cave game
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class Party extends CaveElement {
    //    p:<index>:<name>
    private ArrayList <Creature> creatures = new ArrayList<Creature>();

    public Party(String _name) {
        this.name = _name;
    }

    public ArrayList<Creature> getCreatures() {
        return creatures;
    }

    public void setCreatures(ArrayList<Creature> creatures) {
        this.creatures = creatures;
    }

    public void sortCreatures(String compareType) {
        Comparator<Creature> comparator;

        switch (compareType) {
            case "Name":
                comparator = Comparator.comparing(e -> e.getName());
                Collections.sort(this.getCreatures(), comparator);
                break;
            case "Age":
                comparator = Comparator.comparing(e -> e.getAge());
                Collections.sort(this.getCreatures(), comparator);
                break;
            case "Height":
                comparator = Comparator.comparing(e -> e.getHeight());
                Collections.sort(this.getCreatures(), comparator);
                break;
            case "Weight":
                comparator = Comparator.comparing(e -> e.getWeight());
                Collections.sort(this.getCreatures(), comparator);
                break;
            case "Empathy":
                comparator = Comparator.comparing(e -> e.getEmpathy());
                Collections.sort(this.getCreatures(), comparator);
                break;
            case "Fear":
                comparator = Comparator.comparing(e -> e.getFear());
                Collections.sort(this.getCreatures(), comparator);
                break;
            case "Carrying Capacity":
                comparator = Comparator.comparing(e -> e.getCapacity());
                Collections.sort(this.getCreatures(), comparator);
                break;

        }
    }

    public String toString() {
        String retval = "";

        for (Creature creature: creatures) {
            retval += creature;
        }

        return retval;
    }
}
