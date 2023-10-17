package group.ms_312.Proxy.Resources;

import jakarta.persistence.Entity;

import java.util.*;

/**
 * Bidirectional map implementation <br/>
 * 
 * Acts as a normal map for methods and  return values but allows for backwards access with {@link #getKey(Object)}
 *
 * @param <k> The type of the key for the map
 * @param <v> The type of the value for the map
 */
public class Bimap<k, v> implements Map<k, v> {

    /**
     * The Hashmap which stores the key -> value mapping for this Bimap
     */
    private final HashMap<k,v> forwardsMapping;

    /**
     * The Hashmap which stores the value -> key mapping for this Bimap
     */
    private final HashMap<v,k> backwardsMapping;

    /**
     * Default constructor for creating an empty bimap
     */
    public Bimap (){
        forwardsMapping = new HashMap<>();
        backwardsMapping = new HashMap<>();
    }

    @Override
    public int size() {
        // Stored implicitly in the backing maps
        return forwardsMapping.size();
    }

    @Override
    public boolean isEmpty() {
        return forwardsMapping.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return forwardsMapping.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return forwardsMapping.containsValue(value);
    }

    @Override
    public v get(Object key) {
        return forwardsMapping.get(key);
    }

    /**
     * Return a key based on its associated value
     *
     * @param value The value to get the key for
     */
    public k getKey(Object value){
        return backwardsMapping.get(value);
    }

    @Override
    public v put(k key, v value) {
        backwardsMapping.put(value, key);
        return forwardsMapping.put(key, value);

    }

    @Override
    public v remove(Object key) {
        v val = forwardsMapping.get(key);
        backwardsMapping.remove(val);
        return forwardsMapping.remove(key);
    }

    @Override
    public void putAll(Map<? extends k, ? extends v> m) {
        // Add all items in m to backwards mapping in reverse
        for (k o : m.keySet()) {
            v val = m.get(o);
            backwardsMapping.put(val, o);
        }
        // Add to forwards map
        forwardsMapping.putAll(m);

    }

    @Override
    public void clear() {
        forwardsMapping.clear();
        backwardsMapping.clear();
    }

    @Override
    public Set<k> keySet() {
        return forwardsMapping.keySet();
    }

    @Override
    public Collection<v> values() {
        return forwardsMapping.values();
    }

    @Override
    public Set<Entry<k, v>> entrySet() {
        return forwardsMapping.entrySet();
    }
}
