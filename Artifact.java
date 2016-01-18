/**
 * File: Artifact
 * Date: 1/8/16
 * Author: ben risher
 * Purpose:  Artifact class to describe artifacts in the Sorcerer's Cave game
 */
public class Artifact extends CaveElement {
    //    a:<index>:<type>:<creature>[:<name>]
    int creature = 0;
    String type = "";

    public Artifact(String _name) {
        this.name = _name;
    }
    public int getCreature() {
        return creature;
    }
    public void setCreature(int creature) {
        this.creature = creature;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String toString() {
        return this.getName();
    }
}  // end Artifact
