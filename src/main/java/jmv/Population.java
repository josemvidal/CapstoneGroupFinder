package jmv;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.ListIterator;

/**
 * A Population of Allocations
 */
public class Population {

    public class Item implements Comparable<Item>{
        public Allocation allocation;
        public int value;

        public Item(Allocation allocation, int value) {
            this.allocation = allocation;
            this.value = value;
        }

        @Override
        public int compareTo(Item o) {
            return Integer.compare(value, o.value);
        }
    }

    private ArrayList<Item> population;

    public Population() {
        population = new ArrayList<>();
    }

    public void add(Allocation allocation, int value){
        Item item = new Item(allocation, value);
        population.add(item);
    }

    public int getMinValue(){
        int min = Integer.MAX_VALUE;
        for (Item item: population){
            if (item.value < min)
                min = item.value;
        }
        return min;
    }

    public int getMaxValue(){
        int max = Integer.MIN_VALUE;
        for (Item item : population){
            if (item.value > max)
                max = item.value;
        }
        return max;
    }

    /**
     * Maximum size of the population
     */
    public static final int MAX_SIZE = 100000;

    /**
     * Kill all those that are unfit. Keep the top 10% of the population
     */
    public void killUnfit(){
        Collections.sort(population);
        for (int i = MAX_SIZE/10; i < MAX_SIZE; i++) {
            population.remove(MAX_SIZE/10);
        }
    }

    public void repopulate(Preferences p){
        int lastParent = population.size();
        while (population.size() < MAX_SIZE){
            Item a = population.get(Preferences.random.nextInt(lastParent));
            Item b = population.get(Preferences.random.nextInt(lastParent));
            Allocation baby = a.allocation.reproduceWith(b.allocation);
            p.mutateAllocation(baby, 100);
            Item babyItem = new Item(baby, p.getValue(baby));
            population.add(babyItem);
        }
    }
}
