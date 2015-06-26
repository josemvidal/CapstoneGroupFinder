package jmv;

import au.com.bytecode.opencsv.CSVReader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * The set of everyone's preferences for which group they want to be in.
 */
public class Preferences implements Runnable{


    /**
     * Number of preferences each person submits
     */
    public static final int NUM_PREFERENCES = 5;

    public static void main(String[] args){
        System.out.println("Starting threads");
        for (int i = 0; i < 4; i++) {
            Preferences p = new Preferences();
            p.readFromFile();
            Thread t = new Thread(p);
            t.start();
        }
    }

    /**
     * preferences[i] = [1,5,3,2] means student 1 prefers group 1, then group 5, then 3, then 2.
     */
    private int[][] preferences = null;
    private String[] emails = null;
    private static final Random random = new Random();

    /**
     * Inverted preferences.
     * preferencesIndex[i][groupNum] = the order in which student i places group Num,
     *   Its value is 0,1,..NUM_PREFERENCES-1 or -1 if groupNum is not in i's preferences.
     */
    private int[][] preferencesIndex = null;

    /**
     * Create the preferencesIndex.
     */
    private void setPreferencesIndex (){
        preferencesIndex = new int[getNumStudents()][numGroups];
        for (int i = 0; i < preferences.length; i++) {
            for (int j = 0; j < numGroups; j++) {
                preferencesIndex[i][j] = -1;
            }
            int[] prefs = preferences[i];
            for (int groupIndex = 0; groupIndex < NUM_PREFERENCES; groupIndex++) {
                preferencesIndex[i][prefs[groupIndex]] = groupIndex;
            }
        }
    }

    /**
     * Get the ordered preference of student for group
     * @param student the student id
     * @param group the group id
     * @return the order, 0,1..NUM_PREFERENCES-1 or -1 if group is not present in student's preferences
     */
    public int getPreference(int student, int group){
        return preferencesIndex[student][group];
    }

    public Preferences() {
    }

    public void set(int[][] preferences){
        this.preferences = preferences;
        for (int[] p : preferences) {
            if (p.length != NUM_PREFERENCES)
                throw new RuntimeException("Wrong number of preferences " + p.length);
        }
        setNumGroups();
        setPreferencesIndex();
    }

    public void readFromFile(){
        try {
            CSVReader reader = new CSVReader(new FileReader("project_preferences.csv"));
            List myEntries = reader.readAll();
            int numStudents = myEntries.size();
            preferences = new int[numStudents][NUM_PREFERENCES];
            emails = new String[numStudents];
            Iterator iterator = myEntries.iterator();
            int i = 0;
            while (iterator.hasNext()){
                String [] row = (String[]) iterator.next();
                emails[i] = row[10];
                for (int j = 0; j < NUM_PREFERENCES; j++) {
                    preferences[i][j] = Integer.parseInt(row[11 + j]);
                }
                i++;
            }
            setNumGroups();
            setPreferencesIndex();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getNumStudents(){
        return preferences.length;
    }

    private int numGroups;

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
     * Calculate the value of a given our current preferences. 0 is perfect, bigger is worse.
     * @param a an Allocation
     * @return the value
     */
    public int getValuePreferencesOf(Allocation a){
        int value = 0;
        for (int student = 0; student < a.NUM_STUDENTS; student++) {
            int studentAssignment = a.getGroup(student);
            int pos = preferencesIndex[student][studentAssignment];
            value += (pos == -1) ? (NUM_PREFERENCES + 1) : (1 + pos);
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
            int c = (int)(random.nextInt(NUM_PREFERENCES));
            allocation[i] = preferences[i][c];
        }
        return new Allocation(allocation,numGroups);
    }

    /**
     * Calculate the value of this allocation. 0 is best. Lower is better.
     * @param a the allocation
     * @return the value
     */
    public int getValue(Allocation a){
        int groupError = a.getMemberNumberError();
        if (groupError == 0) //its legal
            return getValuePreferencesOf(a);
        return 50 * groupError + getValuePreferencesOf(a);
    }

    /**
     * Find an allocation that is better than a by only changing one person's group.
     * @param a the allocation
     * @return a new allocation
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
        return bestAllocations.get(random.nextInt(bestAllocations.size()));
    }

    /**
     * Starting at a, keep hillClimb-ing until we get stuck. Return
     * @param a the allocation
     * @return a new allocation, the allocation at which we got stuck on.
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

    /**
     * Returns a modified version of a
     * @param allocation the start
     * @param numChanges the max number of changes
     * @return modified allocation
     */
    public Allocation mutateAllocation(Allocation allocation, int numChanges){
        for (int i = 0; i < random.nextInt(numChanges); i++) {
            int index = random.nextInt(allocation.NUM_STUDENTS); //of course, we could end up picking the same one multiple times
            allocation.set(index, preferences[index][random.nextInt(NUM_PREFERENCES)]);
        }
        return allocation;
    }


    /**
     * Hillclimb until stuck, then try to get out of rut with some noise. If that works then repeat. If not, we give up.
     * It works much better than simple hill climbing.
     *
     * @param a seed
     * @return new allocation.
     */
    public Allocation continuousHillClimbWithNoise(Allocation a){
        boolean foundBetterMutation;
        Allocation bestAllocation;
        int bestValue;
        Allocation next = continuousHillClimb(a);
        bestAllocation = next;
        bestValue = getValue(next);
        do {
            foundBetterMutation = false;
            for (int i = 0; i < 100; i++) {
                next = continuousHillClimb(mutateAllocation(bestAllocation.clone(), 10)); //5 works well
                int nextValue = getValue(next);
                if (nextValue < bestValue) {
                    bestValue = nextValue;
                    bestAllocation = next;
                    foundBetterMutation = true;
                    break;
                }
            }
        } while (foundBetterMutation);
        return bestAllocation;
    }

    /**
     * Perform a the search
     * @return the best allocation found.
     */
    public Allocation hillClimbingSearch(){
        Allocation bestAllocation = getRandomAllocation();
        int bestValue = getValue(bestAllocation);
        System.out.println(bestValue);
        Allocation next = bestAllocation;
        int nextValue;
        for (int i = 0; i < 1000; i++) {
//            next = continuousHillClimb(next);
            next = continuousHillClimbWithNoise(next);
            nextValue = getValue(next);
            if  (nextValue < bestValue) {
                bestValue = nextValue;
                bestAllocation = next.clone();
                System.out.println(bestValue - getNumStudents());
                System.out.println(bestAllocation);
            }
            next = getRandomAllocation();
        }
        return bestAllocation;
    }

    @Override
    public void run() {
        System.out.println("Running");
        hillClimbingSearch();
    }
}
