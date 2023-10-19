package group.ms_312.Proxy.Messages;

import com.fasterxml.jackson.annotation.JsonIgnore;
import group.ms_312.Proxy.Messages.Message;
import group.ms_312.Proxy.Providers.MessageProvider;
import jakarta.persistence.*;
import org.springframework.lang.NonNull;

import java.util.*;

/**
 * A {@link List} which stores Messages associated with a certain username or other string in one Collection.
 */
@Entity
public class MessageBucket implements List<Message> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long ID;

    @OneToMany(cascade = CascadeType.ALL)
    private final List<Message> backingList;

    // The provider this Bucket stores Messages for
    @ManyToOne
    @JsonIgnore
    private MessageProvider owner;


    /**
     * Create a new MessageBucket
     */
    public MessageBucket(){
        backingList = new ArrayList<>();
    }

    /**
     * Create a new MessageBucket with the given initial capacity
     *
     * @param initialCapacity int holding the starting capacity of this List
     */
    public MessageBucket(int initialCapacity){
        backingList = new ArrayList<>(initialCapacity);
    }

    @Override
    public int size() {
        return backingList.size();
    }

    @Override
    public boolean isEmpty() {
        return backingList.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return backingList.contains(o);
    }

    @Override
    public Iterator<Message> iterator() {
        return backingList.iterator();
    }

    @Override
    public Object[] toArray() {
        return backingList.toArray();
    }

    @Override
    public boolean add(Message o) {
        return backingList.add(o);
    }

    @Override
    public boolean remove(Object o) {
        return backingList.remove(o);
    }

    @Override
    public boolean addAll(@NonNull Collection<? extends Message> c) {
        return backingList.addAll(c);
    }

    @Override
    public boolean addAll(int index, @NonNull Collection<? extends Message> c) {
        return backingList.addAll(index, c);
    }

    @Override
    public void clear() {
        backingList.clear();
    }

    @Override
    public Message get(int index) {
        return backingList.get(index);
    }

    @Override
    public Message set(int index, Message element) {
        return backingList.set(index, element);
    }

    @Override
    public void add(int index, Message element) {
        backingList.add(index, element);
    }

    @Override
    public Message remove(int index) {
        return backingList.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return backingList.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return backingList.lastIndexOf(o);
    }

    @Override
    public ListIterator<Message> listIterator() {
        return backingList.listIterator();
    }

    @Override
    public ListIterator<Message> listIterator(int index) {
        return backingList.listIterator(index);
    }

    @Override
    public List<Message> subList(int fromIndex, int toIndex) {
        return backingList.subList(fromIndex, toIndex);
    }

    @Override
    public boolean retainAll(@NonNull Collection<?> c) {
        return backingList.retainAll(c);
    }

    @Override
    public boolean removeAll(@NonNull Collection<?> c) {
        return backingList.removeAll(c);
    }

    @Override
    public boolean containsAll(@NonNull Collection<?> c) {
        return backingList.containsAll(c);
    }

    @Override
    public <K> K[] toArray(@NonNull K[] a) {
        return backingList.toArray(a);
    }

    /**
     * @return The ID assigned to this MessageBucket by the JPA
     */
    public long getId(){
        return ID;
    }

    /**
     * Set the ID used by the JPA of this MessageBucket
     * Method is private and is only meant to be used by the JPA
     *
     * @param id The id to for this List
     */
    private void setId(long id){
        this.ID = id;
    }


}
