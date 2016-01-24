/**
 * File: Party
 * Date: 1/7/16
 * Author: ben risher
 * Purpose:  Party class to describe parties in the Sorcerer's Cave game
 */

import java.util.ArrayList;


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

    public String toString() {
        String retval = "";

        for (Creature creature: creatures) {
            retval += creature;
        }

        return retval;
    }
}
