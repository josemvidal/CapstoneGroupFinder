import jmv.Allocation;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by jmvidal on 6/23/15.
 */
public class TestAllocation {

    @Test
    public void testGetMemberNumberError(){
        int[] allocation = {1,1,1,1};
        Allocation a = new Allocation(allocation,3);
        assertEquals(0, a.getMemberNumberError());
    }

    @Test
    public void testGetMemberNumberErrorB(){
        int[] allocation = {1,1,1,1};
        Allocation a = new Allocation(allocation,10);
        assertEquals(0, a.getMemberNumberError());
    }

    @Test
    public void testGetMemberNumberError2(){
        int[] allocation = {1,1,1,1,2};
        Allocation a = new Allocation(allocation,5);
        assertEquals(3, a.getMemberNumberError());
    }
}
