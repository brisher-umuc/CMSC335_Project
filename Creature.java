/**
 * File: Creature
 * Date: 1/8/16
 * Author: ben risher
 * Purpose:  Creature class to describe creatures in the Sorcerer's Cave game
 */
import java.util.ArrayList;

public class Creature extends CaveElement {
    //    c:<index>:<type>:<name>:<party>:<empathy>:<fear>:<carrying capacity>
    private String type = "";
    private int party = 0;
    private int empathy = 0;
    private int fear = 0;
    private int capacity = 0;
    private ArrayList<Treasure> treasures = new ArrayList<Treasure>();
    private ArrayList<Artifact> artifacts = new ArrayList<Artifact>();

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

        retval += this.getName();

        if (this.getParty() == 0 && this.treasures.size() > 0) {
            retval += "\n--> " + this.treasures;
        }
        else if (this.treasures.size() > 0) {
            retval += "\n-> " + this.treasures;
        }

        if (this.getParty() == 0 && this.artifacts.size() > 0) {
            retval += "\n--> " + this.artifacts;
        }
        else if (this.artifacts.size() > 0) {
            retval += "\n-> " + this.artifacts;
        }

        retval += "\n";

        return retval;
    }
}
