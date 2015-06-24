package jmv;

import au.com.bytecode.opencsv.CSVReader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * The set of everyone's preferences for which group they want to be in.
 */
public class Preferences {

    /**
     * Number of preferences each person submits
     */
    public static final int NUM_PREFERENCES = 5;

    public static void main(String[] args){
        System.out.println("Hello World");
        Preferences p = new Preferences();
        p.readFromFile();
        p.hillClimbingSearch();
    }

    /**
     * preferences[i] = [1,5,3,2] means student 1 prefers group 1, then group 5, then 3, then 2.
     */
    private int[][] preferences = null;
    private String[] emails = null;

    public Preferences() {
    }

    public void set(int[][] preferences){
        this.preferences = preferences;
        for (int[] p : preferences) {
            if (p.length != NUM_PREFERENCES)
                throw new RuntimeException("Wrong number of preferences " + p.length);
        }
        setNumGroups();
    }

    public void readFromFile(){
        try {
            CSVReader reader = new CSVReader(new FileReader("project_preferences.csv"));
            List myEntries = reader.readAll();
            int numStudents = myEntries.size();
            preferences = new int[numStudents][NUM_PREFERENCES];
            emails = new String[numStudents];
            Iterator<String []> iterator = myEntries.iterator();
            int i = 0;
            while (iterator.hasNext()){
                String [] row = iterator.next();
                emails[i] = row[10];
                for (int j = 0; j < NUM_PREFERENCES; j++) {
                    preferences[i][j] = Integer.parseInt(row[11 + j]);
                }
                i++;
            }
            setNumGroups();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getNumStudents(){
        return preferences.length;
    }

    private int numGroups;

    public int getNumGroups(){
        return numGroups;
    }

    /**
     * Sets the number of groups. Assumes they are positive consecutive integers.
     */
    private void setNumGroups(){
        int max = 0;
        for (int[] p: preferences){ //just find the biggest number
            for (int g :p){
                if (g > max)
                    max = g;
            }
        }
        numGroups = max + 1;
    }
    /**
     * Return the index of value in array, or -1 if not there.
     * @param array
     * @param value
     */
    private int getIndex(int[] array, int value){
        for (int i = 0; i < array.length; i++) {
            if (array[i] == value)
                return i;
        }
        return -1;
    }

    /**
     * Calculate the value of a given our current preferences. 0 is perfect, bigger is worse.
     * @param a an Allocation
     * @return
     */
    public int getValuePreferencesOf(Allocation a){
        int value = 0;
        for (int student = 0; student < a.NUM_STUDENTS; student++) {
            int studentAssignment = a.getGroup(student);
            int pos = getIndex(preferences[student], studentAssignment);
            value += (pos == -1) ? 6 : (1 + pos);
        }
        return value;
    }

    /**
     * Creates and returns this preferences' most preferred allocation, which might not be legal.
     * @return preferred allocation, might not be legal
     */
    public Allocation getPreferredAllocation(){
        int[] allocation = new int[getNumStudents()];
        for (int i = 0; i < preferences.length; i++){
            allocation[i] = preferences[i][0];
        }
        return new Allocation(allocation,numGroups);
    }

    /**
     * Creates and return a random allocation, but with all the groups assigned being in each student's
     * list of preferences.
     * @return allocation, might not be legal.
     */
    public Allocation getRandomAllocation(){
        int[] allocation = new int[getNumStudents()];
        for (int i = 0; i < preferences.length; i++){
            int c = (int)(Math.random()*NUM_PREFERENCES);
            allocation[i] = preferences[i][c];
        }
        return new Allocation(allocation,numGroups);
    }

    /**
     * Calculate the value of this allocation. 0 is best. Lower is better.
     * @param a
     * @return
     */
    public int getValue(Allocation a){
        int groupError = a.getMemberNumberError();
        if (groupError == 0) //its legal
            return getValuePreferencesOf(a);
        return 50 * groupError + getValuePreferencesOf(a);
    }

    /**
     * Find an allocation that is better than a by only changing one person's group.
     * @param a
     * @return
     */
    public Allocation hillClimb(Allocation a){
        int bestValue = getValue(a);
        ArrayList<Allocation> bestAllocations = new ArrayList<>();
        bestAllocations.add(a.clone());
        for (int personIndex = 0; personIndex < a.NUM_STUDENTS; personIndex++) {
            int oldGroup = a.getGroup(personIndex);
            //Try all the groups for this personIndex
            for (int prefIndex = 0; prefIndex < NUM_PREFERENCES; prefIndex++) {
                a.set(personIndex, preferences[personIndex][prefIndex]);
                int value = getValue(a);
                if (value < bestValue) {
                    bestAllocations.clear();
                    bestAllocations.add(a.clone());
                    bestValue = value;
                }
                else if (value == bestValue) {
                    bestAllocations.add(a.clone());
                }
            }
            a.set(personIndex,oldGroup); //set to old value
        }
        return bestAllocations.get((int)Math.random()*bestAllocations.size());
    }

    /**
     * Starting at a, keep hillClimb-ing until we get stuck.
     * @param a
     * @return the allocation at which we got stuckn
     */
    public Allocation continuousHillClimb(Allocation a){
        int currentValue = getValue(a);
        Allocation next = hillClimb(a);
        int nextValue = getValue(next);
        while (nextValue < currentValue) {
            currentValue = nextValue;
            a = next;
            next = hillClimb(a);
            nextValue = getValue(next);
        }
        return a;
    }

    public Allocation hillClimbingSearch(){
        Allocation bestAllocation = getRandomAllocation();
        int bestValue = getValue(bestAllocation);
        System.out.println(bestValue);
        Allocation next = bestAllocation;
        int nextValue;
        for (int i = 0; i < 100000; i++) {
            next = continuousHillClimb(next);
            nextValue = getValue(next);
            if  (nextValue < bestValue) {
                bestValue = nextValue;
                bestAllocation = next.clone();
                System.out.println(bestValue);
                System.out.println(bestAllocation);
            }
            next = getRandomAllocation();
        }
        return bestAllocation;
    }
}
