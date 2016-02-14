/**
 * File: Creature
 * Date: 1/8/16
 * Author: ben risher
 * Purpose:  Creature class to describe creatures in the Sorcerer's Cave game
 */
import java.util.*;

public class Creature extends CaveElement {
    //    c:<index>:<type>:<name>:<party>:<empathy>:<fear>:<carrying capacity>
    private String type = "";
    private int party = 0;
    private int empathy = 0;
    private int fear = 0;
    private int capacity = 0;
    private double weight = 0;
    private double height = 0;
    private double age = 0;
    private ArrayList<Treasure> treasures = new ArrayList<Treasure>();
    private ArrayList<Artifact> artifacts = new ArrayList<Artifact>();
    private ArrayList<String> jobsListing = new ArrayList<>();

    public boolean busyFlag = false;
    public Creature(String _name) {
        this.name = _name;
    }

    public Creature(String _name, int _index, String _type, int _party, int _empathy, int _fear, int _capacity) {
        setIndex(_index);
        setName(_name);
        type = _type;
        party = _party;
        empathy = _empathy;
        fear = _fear;
        capacity = _capacity;
    }

    public double getWeight() {
        return weight;
    }
    public void setWeight(double weight) {
        this.weight = weight;
    }
    public double getHeight() {
        return height;
    }
    public void setHeight(double height) {
        this.height = height;
    }
    public double getAge() {
        return age;
    }
    public void setAge(double age) {
        this.age = age;
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

    public ArrayList<String> getJobsListing() {
        return jobsListing;
    }

    public void setJobsListing(ArrayList<String> jobsListing) {
        this.jobsListing = jobsListing;
    }

    public boolean isBusyFlag() {
        return busyFlag;
    }

    public void setBusyFlag(boolean busyFlag) {
        this.busyFlag = busyFlag;
    }

    // helpers for easy reflection
    public double getW() {return weight;}
    public double getH() {return height;}
    public double getA() {return age;}
    public int getE() {return empathy;}
    public int getF() {return fear;}
    public int getC() {return capacity;}

    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("  Creature: ");
        sb.append(this.name);
        sb.append("\n");
        sb.append("    Index: ");
        sb.append(this.getIndex());
        sb.append("\n");
        sb.append("    Weight: ");
        sb.append(this.weight);
        sb.append("\n");
        sb.append("    Height: ");
        sb.append(this.height);
        sb.append("\n");
        sb.append("    Age: ");
        sb.append(this.age);
        sb.append("\n");
        sb.append("    Fear: ");
        sb.append(this.fear);
        sb.append("\n");
        sb.append("    Empathy: ");
        sb.append(this.empathy);
        sb.append("\n");
        sb.append("    Carrying Capacity: ");
        sb.append(this.capacity);
        sb.append("\n");

        for (Treasure t: this.treasures) {
            sb.append(t);
            sb.append("\n");
        }
        for (Artifact a: this.artifacts) {
            sb.append(a);
            sb.append("\n");
        }

        return sb.toString();
    }

    public void sortTreasures(String compareType) {
        Comparator<Treasure> comparator;

        if (compareType.equals("Weight")) {
            comparator = Comparator.comparing(e -> e.getWeight());
        }
        else {
            comparator = Comparator.comparing(e -> e.getValue());
        }

        Collections.sort(this.treasures, comparator);
    }
}
