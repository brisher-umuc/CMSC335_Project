/**
 * File: Resource
 * Date: 3/2/16
 * Author: ben risher
 * Purpose:
 */

public class Resource {
    private final String name;
    private int count;

    public Resource(String name) {
        this.name = name;
        this.count = 0;
    }

    public String getName() {
        return name;
    }
    public int getCount() {
        return count;
    }
    public void setCount(int count) {
        this.count = count;
    }
    public void incrementCount(int step) {
        this.count += step;
    }
    public void incrementCount() {
        this.count++;
    }

    public void decrementCount(int step) {
        this.count -= step;
    }

}
