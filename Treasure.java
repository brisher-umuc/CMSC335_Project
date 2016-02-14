/**
 * File: Treasure
 * Date: 1/8/16
 * Author: ben risher
 * Purpose:  Treasure class to describe treasures in the Sorcerer's Cave game
 */
public class Treasure extends CaveElement {
    //    t:<index>:<type>:<creature>:<weight>:<value>
    private String type = "";
    private int creature = 0;
    private double weight = 0;
    private int value = 0;

    public Treasure(String _type) {
        this.type = _type;
        this.name = _type;
    }

    public Treasure(String _type, int _index, int _creature, double _weight, int _value) {
        setIndex(_index);
        setName(_type);
        type = _type;
        creature = _creature;
        weight = _weight;
        value = _value;
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public int getCreature() {
        return creature;
    }
    public void setCreature(int creature) {
        this.creature = creature;
    }
    public double getWeight() {
        return weight;
    }
    public void setWeight(double weight) {
        this.weight = weight;
    }
    public int getValue() {
        return value;
    }
    public void setValue(int value) {
        this.value = value;
    }

    public double getW() {return weight;}
    public int getV() {return value;}

    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("    Treasure: ");
        sb.append(this.name);
        sb.append("\n");
        sb.append("      Index: ");
        sb.append(this.getIndex());
        sb.append("\n");
        sb.append("      Weight: ");
        sb.append(this.weight);
        sb.append("\n");
        sb.append("      Value: ");
        sb.append(this.value);
        sb.append("\n");

        return sb.toString();
    }
}  // end Treasure
