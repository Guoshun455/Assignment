package com.example.week11.repository;

import com.example.week11.model.SoccerEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
public class Repository<T extends SoccerEntity> {
    protected List<T> items;
    public Repository(){
        this.items = new ArrayList<>();
    }
    public List<T> getAll(){
        return new ArrayList<>(items);
    }
    public void add(T item){
        if (item == null)
            throw new IllegalArgumentException("Cannot add null item");
        if (item.getId() == null)
            throw new IllegalArgumentException("Item ID cannot be null");
        if (items.stream().anyMatch(i -> i.getId().equals(item.getId())))
            throw new IllegalArgumentException("Item with this ID already exists");
        items.add(item);
    }
    public void remove(T item){
        if (item == null)
            throw new IllegalArgumentException("Cannot remove null item");
        items.remove(item);
    }
    public List<T> filter(Predicate<T> predicate){
        if (predicate == null)
            throw new IllegalArgumentException("Predicate cannot be null");
        return items.stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }
    public List<T> sort(java.util.Comparator<T> comparator) {
        if (comparator == null)
            throw new IllegalArgumentException("Comparator cannot be null");
        return items.stream()
                .sorted(comparator)
                .collect(Collectors.toList());
    }
    public T findFirst(Predicate<T> predicate){
        return items.stream()
                .filter(predicate)
                .findFirst()
                .orElse(null);
    }
    public int size(){
        return items.size();
    }
    public void clear(){
        items.clear();
    }
}
