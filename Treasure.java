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

    public String toString() { return this.getType(); }
}  // end Treasure
