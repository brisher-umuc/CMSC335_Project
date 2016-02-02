/**
 * File: CaveElement
 * Date: 1/7/16
 * Author: ben risher
 * Purpose:  BaseClass for Artifacts, Creatures, Treasures, and Parties to inherit from
 */
public class CaveElement  {
    private int index = 0;
    String name = "";

    public int getIndex() {
        return index;
    }
    public void setIndex(int index) {
        this.index = index;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String toString() {
        return this.getName();
    }
}
