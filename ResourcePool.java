import java.util.HashMap;
import java.util.Map;

/**
 * File: ResourcePool
 * Date: 3/2/16
 * Author: ben risher
 * Purpose:
 */
public class ResourcePool {
    private HashMap<String, Resource> properties = new HashMap<>();
    private Party party;

    public Party getParty() {
        return party;
    }
    public void setParty(Party party) {
        this.party = party;
    }
    public Map<String, Resource> getProperties() {
        return properties;
    }
    public void setProperties(HashMap<String, Resource> properties) {
        this.properties = properties;
    }
    public Resource getResource(String key) {
        return properties.get(key);
    }
    public void setResource(String key, Resource resource) {
        properties.put(key, resource);
    }

    public String toString() {
        String s = "";

        s += party.getName() + " -> ";
        for (Map.Entry<String, Resource> entry: properties.entrySet()) {
            s += entry.getKey() + ": " + entry.getValue().getCount() + " - ";
        }
        s = s.substring(0, s.length() - 2);
        s += "\n";
        return s;
    }

}
