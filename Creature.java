import java.util.ArrayList;

/**
 * File: Creature
 * Date: 1/8/16
 * Author: ben risher
 * Purpose:  Creature class to describe creatures in the Sorcerer's Cave game
 */
public class Creature extends CaveElement {
    //    c:<index>:<type>:<name>:<party>:<empathy>:<fear>:<carrying capacity>
    String type = "";
    int party = 0;
    int empathy = 0;
    int fear = 0;
    int capacity = 0;
    ArrayList<Treasure> treasures = new ArrayList<Treasure>();
    ArrayList<Artifact> artifacts = new ArrayList<Artifact>();

    public Creature(String _name) {
        this.name = _name;
    }

    public ArrayList<Treasure> getTreasures() {
        return treasures;
    }
    public void setTreasures(ArrayList<Treasure> treasures) {
        this.treasures = treasures;
    }
    public ArrayList<Artifact> getArtifacts() {
        return artifacts;
    }
    public void setArtifacts(ArrayList<Artifact> artifacts) {
        this.artifacts = artifacts;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public int getParty() {
        return party;
    }
    public void setParty(int party) {
        this.party = party;
    }
    public int getEmpathy() {
        return empathy;
    }
    public void setEmpathy(int empathy) {
        this.empathy = empathy;
    }
    public int getFear() {
        return fear;
    }
    public void setFear(int fear) {
        this.fear = fear;
    }
    public int getCapacity() {
        return capacity;
    }
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String toString() {
        String retval = "";

        retval += "--> " + this.getName() + "\n";

        for (Treasure treasure: treasures) {
            retval += "----> " + treasure + "\n";
        }

        for (Artifact artifact: artifacts) {
            retval += "----> " + artifact + "\n";
        }

        return retval;
    }
}
