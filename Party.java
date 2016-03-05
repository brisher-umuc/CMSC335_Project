/**
 * File: Party
 * Date: 1/7/16
 * Author: ben risher
 * Purpose:  Party class to describe parties in the Sorcerer's Cave game
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;


public class Party extends CaveElement {
    //    p:<index>:<name>
    private ArrayList <Creature> creatures = new ArrayList<Creature>();
    private ResourcePool pool = new ResourcePool();

    public Party(String _name) {
        this.name = _name;
    }

    public ResourcePool getPool() {
        return pool;
    }
    public void setPool(ResourcePool pool) {
        this.pool = pool;
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
        StringBuilder sb = new StringBuilder();

        sb.append("Party: ");
        sb.append(this.name);
        sb.append(" (");
        sb.append(this.getIndex());
        sb.append(")");
        sb.append("\n");

        for (Creature creature: creatures) {
            sb.append(creature);
            sb.append("\n");
        }

        return sb.toString();
    }
}
