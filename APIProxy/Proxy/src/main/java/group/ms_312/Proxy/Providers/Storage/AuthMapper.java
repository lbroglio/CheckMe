package group.ms_312.Proxy.Providers.Storage;

import com.fasterxml.jackson.annotation.JsonIgnore;
import group.ms_312.Proxy.Providers.MessageProvider;
import jakarta.persistence.*;
import jakarta.persistence.Entity;

import java.util.*;

/**
 * Bidirectional map implementation used for associating tokens and usernames<br/>
 * 
 * Acts as a normal map for methods and return values but allows for backwards access with {@link #getKey(Object)}
 *
 */
@Entity
public class AuthMapper implements Map<String, String> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long ID;

    /**
     * The Hashmap which stores the key -> value mapping for this Bimap
     */
    @ElementCollection
    private Map<String, String> forwardsMapping;

    /**
     * The Hashmap which stores the value -> key mapping for this Bimap
     */
    @ElementCollection
    private final Map<String, String> backwardsMapping;

    /**
     * The TokenBasedProvider which owns this mapping
     */
    @OneToOne
    @JsonIgnore
    private MessageProvider owner;

    /**
     * Default constructor for creating an empty bimap
     */
    public AuthMapper(){
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
    public String get(Object key) {
        return forwardsMapping.get(key);
    }

    /**
     * Return a key based on its associated value
     *
     * @param value The value to get the key for
     */
    public String getKey(Object value){
        return backwardsMapping.get(value);
    }

    @Override
    public String put(String key, String value) {
        backwardsMapping.put(value, key);
        return forwardsMapping.put(key, value);

    }

    @Override
    public String remove(Object key) {
        String val = forwardsMapping.get(key);
        backwardsMapping.remove(val);
        return forwardsMapping.remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ? extends String> m) {
        // Add all items in m to backwards mapping in reverse
        for (String o : m.keySet()) {
            String val = m.get(o);
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
    public Set<String> keySet() {
        return forwardsMapping.keySet();
    }

    @Override
    public Collection<String> values() {
        return forwardsMapping.values();
    }

    @Override
    public Set<Entry<String, String>> entrySet() {
        return forwardsMapping.entrySet();
    }

    /**
     * @return The id assigned to this Bimap by the JPA
     */
    public long getID() {
        return ID;
    }

    /**
     * @param ID Set the id of this object
     */
    private void setID(long ID) {
        this.ID = ID;
    }


}
