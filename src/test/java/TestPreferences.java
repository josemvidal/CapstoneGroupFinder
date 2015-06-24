import jmv.Allocation;
import jmv.Preferences;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

/**
 *
 */
public class TestPreferences {

    @Test
    public void testgetPreferredAllocation(){
        Preferences p = new Preferences();
        int [][] prefs = new int[][] {
                {1,2,3,4,5},
                {1,2,3,4,5},
                {1,2,3,4,5},
                {1,2,3,4,5}};
        p.set(prefs);
        Allocation bestAlloc = p.getPreferredAllocation();
        for (int i = 0; i < bestAlloc.NUM_STUDENTS; i++) {
            assertEquals(1, bestAlloc.getGroup(i));
        }
    }

    @Test
    public void testgetPreferredAllocation2(){
        Preferences p = new Preferences();
        int [][] prefs = new int[][] {
                {1,2,3,4,5},
                {2,1,3,4,5},
                {3,1,2,4,5},
                {4,1,3,2,5}};
        p.set(prefs);
        Allocation bestAlloc = p.getPreferredAllocation();
        for (int i = 0; i < bestAlloc.NUM_STUDENTS; i++) {
            assertEquals(i+1, bestAlloc.getGroup(i));
        }
    }

    @Test
    public void testGetValuePreferencesOf(){
        Preferences p = new Preferences();
        int [][] prefs = new int[][] {
                {1,2,3,4,5},
                {1,2,3,4,5},
                {1,2,3,4,5},
                {1,2,3,4,5}};
        p.set(prefs);
        Allocation bestAlloc = p.getPreferredAllocation();
        assertEquals(4,p.getValuePreferencesOf(bestAlloc));
    }

    @Test
    public void testGetValue(){
        Preferences p = new Preferences();
        int [][] prefs = new int[][] {
                {1,2,3,4,5},
                {1,2,3,4,5},
                {1,2,3,4,5},
                {1,2,3,4,5}};
        p.set(prefs);
        Allocation a = p.getPreferredAllocation();
        assertEquals(4,p.getValue(a));
    }

    @Test
    public void testGetValue2(){
        Preferences p = new Preferences();
        int [][] prefs = new int[][] {
                {1,2,3,4,5},
                {1,2,3,4,5},
                {1,2,3,4,5},
                {1,2,3,4,5},
                {1,2,3,4,5}};
        p.set(prefs);
        Allocation a = p.getPreferredAllocation();
        assertEquals(5,p.getValue(a));
    }

    @Test
    public void testGetValue3(){
        Preferences p = new Preferences();
        int [][] prefs = new int[][] {
                {1,2,3,4,5},
                {1,2,3,4,5},
                {1,2,3,4,5},
                {1,2,3,4,5},
                {1,2,3,4,5}};
        p.set(prefs);
        int[] allocation = {1,1,1,1,2};
        Allocation a = new Allocation(allocation,3);
        assertEquals(50*(4-1) + 6,p.getValue(a));
    }

    @Test
    public void testGetRandom(){
        Preferences p = new Preferences();
        int [][] prefs = new int[][] {
                {1,2,3,4,5},
                {1,2,3,4,5},
                {1,2,3,4,5},
                {1,2,3,4,5}};
        p.set(prefs);
        for (int i = 0; i < 100; i++) {
            Allocation a = p.getRandomAllocation();
            //At worst, everyone gets their least preferred group
            assert (p.getValuePreferencesOf(a) <= prefs.length * Preferences.NUM_PREFERENCES);
            //At best, everyone gets their most preferred
            assert (prefs.length <= p.getValuePreferencesOf(a));
        }
    }

    @Test
    public void testGetRandom2(){
        Preferences p = new Preferences();
        int [][] prefs = new int[][] {
                {1,2,3,4,5},
                {1,2,3,4,5},
                {1,2,3,4,5},
                {1,2,3,4,5},
                {5,2,3,4,1},
                {4,2,3,1,5}
        };
        p.set(prefs);
        for (int i = 0; i < 100; i++) {
            Allocation a = p.getRandomAllocation();
            //At worst, everyone gets their least preferred group
            assert (p.getValuePreferencesOf(a) <= prefs.length * Preferences.NUM_PREFERENCES);
            //At best, everyone gets their most preferred
            assert (prefs.length <= p.getValuePreferencesOf(a));
        }
    }

    @Test
    public void testHillClimbing1(){
        Preferences p = new Preferences();
        int [][] prefs = new int[][] {
                {1,2,3,4,5},
                {1,2,3,4,5},
                {1,2,3,4,5},
                {1,2,3,4,5},
                {1,2,3,4,5}};
        p.set(prefs);
        int[] allocation = {1,1,1,1,5};
        Allocation a = new Allocation(allocation,6);
        Allocation next = p.hillClimb(a);
        int[] answerA = {1,1,1,1,1};
        Allocation answer = new Allocation(answerA, 6);
        assert(answer.equals(next));
    }

    @Test
    public void testHillClimbing2(){
        Preferences p = new Preferences();
        int [][] prefs = new int[][] {
                {1,2,3,4,5},
                {1,2,3,4,5},
                {1,2,3,4,5},
                {1,2,3,4,5},
                {1,2,3,4,5}};
        p.set(prefs);
        int[] allocation = {1,2,5,1,2};
        Allocation a = new Allocation(allocation,6);
        Allocation next = p.hillClimb(a);
        int[] answerA = {1,2,1,1,2};
        Allocation answer = new Allocation(answerA, 6);
        assert(answer.equals(next));
    }


    @Test
    public void testHillClimbing3(){
        Preferences p = new Preferences();
        int [][] prefs = new int[][] {
                {1,2,3,4,5},
                {1,2,3,4,5},
                {1,2,3,4,5},
                {1,2,3,4,5},
                {5,2,3,4,1},
                {5,2,3,4,1},
                {5,2,3,4,1},
                {3,2,5,4,1}
        };
        p.set(prefs);
        int[] allocation = {1,1,1,1,5,5,5,3}; //not legal, so bad
        Allocation a = new Allocation(allocation,6);
        Allocation next = p.hillClimb(a);
        int[] answerA = {1,1,1,1,5,5,5,5};
        Allocation answer = new Allocation(answerA, 6);
        assert(answer.equals(next));
    }

    @Test
    public void testContinousHillClimbing1(){
        Preferences p = new Preferences();
        int [][] prefs = new int[][] {
                {1,2,3,4,5},
                {1,2,3,4,5},
                {1,2,3,4,5},
                {1,2,3,4,5},
                {1,2,3,4,5}};
        p.set(prefs);
        int[] allocation = {1,2,3,4,5};
        Allocation a = new Allocation(allocation,6);
        Allocation next = p.continuousHillClimb(a);
        int[] answerA = {1,1,1,1,1};
        Allocation answer = new Allocation(answerA, 6);
        assert(answer.equals(next));
    }

    @Test
    public void testContinousHillClimbing2(){
        Preferences p = new Preferences();
        int [][] prefs = new int[][] {
                {1,2,3,4,5},
                {1,2,3,4,5},
                {1,2,3,4,5},
                {1,2,3,4,5},
                {1,2,3,4,5}};
        p.set(prefs);
        int[] allocation = {3,3,3,3,3};
        Allocation a = new Allocation(allocation,6);
        Allocation next = p.continuousHillClimb(a);
        int[] answerA = {3,3,3,3,3};
        Allocation answer = new Allocation(answerA, 6);
        assert(answer.equals(next));
    }

    @Test
    public void testContinousHillClimbing3(){
        Preferences p = new Preferences();
        int [][] prefs = new int[][] {
                {1,2,3,4,5},
                {1,2,3,4,5},
                {1,2,3,4,5},
                {1,2,3,4,5},
                {5,4,3,2,1},
                {5,4,3,2,1},
                {5,4,3,2,1},
                {5,4,3,2,1} };
        p.set(prefs);
        int[] allocation = {3,3,3,3,5,5,4,4};
        Allocation a = new Allocation(allocation,6);
        Allocation next = p.continuousHillClimb(a);
        int[] answerA = {3,3,3,3,5,5,5,5}; //{1,1,1,1,5,5,5,5} requires more than 1 move
        Allocation answer = new Allocation(answerA, 6);
        assert(answer.equals(next));
    }


}
