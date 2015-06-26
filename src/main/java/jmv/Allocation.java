package jmv;

/**
 * An allocation of students to groups.
 */
public class Allocation implements Cloneable{

    /**
     * allocation[i] = group of student i.
     */
    private int[] allocation;

    /**
     * member[i] = number of people in group i.
     */
    private int[] member;

    public final int NUM_STUDENTS;

    public Allocation(int numStudents, int numGroups){
        NUM_STUDENTS = numStudents;
        allocation = new int[numStudents];
        member = new int[numGroups];
        setGroupMembership();
    }

    public Allocation (int[] allocation, int numGroups){
        NUM_STUDENTS = allocation.length;
        this.allocation = allocation;
        member = new int[numGroups];
        setGroupMembership();
    }

    /**
     * Make a copy.
     * @return
     */
    public Allocation clone(){
        Allocation a = new Allocation(allocation.length, member.length );
        for (int i = 0; i < allocation.length; i++) {
            a.allocation[i] = allocation[i];
        }
        for (int i = 0; i < member.length; i++) {
            a.member[i] = member[i];
        }
        return a;
    }

    private void setGroupMembership(){
        for (int i = 0; i < member.length; i++) { //reset to 0
            member[i] = 0;
        }
        for (int g: allocation){ //then count
            member[g]++;
        }
    }

    /**
     * Get the group number that studentIndex is in.
     * @param studentIndex
     * @return group number
     */
    public int getGroup(int studentIndex){
        return allocation[studentIndex];
    }

    /**
     * Is this allocation legal? That is, does it assign everyone? and do all groups have either 4 or 5 students?
     * @return legal?
     */
    public boolean isLegal(){
        for (int count : member){
            if (! (count == 0 || count == 4 || count == 5)) //each group must have 0 or 4 or 5.
                return false;
        }
        return true;
    }

    /**
     * Calculate the total number of missing/needed people to make this allocation legal.
     * @return the number, bigger is worse, 0 means its legal.
     */
    public int getMemberNumberError(){
        int error = 0;
        for (int count : member){
            if (count != 0 && count != 4 && count != 5) //each group must have 0 or 4 or 5.
                if (count < 4)
                    error +=  4 - count;
                else if (count > 5)
                    error += count - 5;
        }
        return error;
    }

    /**
     * Change person person to group newGroup
     * @param person the person
     * @param newGroup the new group
     */
    public void set(int person, int newGroup){
        member[allocation[person]]--; //one fewer person in old group.
        allocation[person] = newGroup;
        member[newGroup]++; //one more in new group
    }

    public String toString(){
        StringBuilder result = new StringBuilder();
        for (int g: allocation){
            result.append(g);
            result.append(",");
        }
        return result.toString();
    }

    public boolean equals(Object other){
        if (! (other instanceof Allocation)) return false;
        Allocation o = (Allocation)other;
        if (allocation.length != o.allocation.length)
            return false;
        for (int i = 0; i < allocation.length; i++) {
            if (allocation[i] != o.allocation[i])
                return false;
        }
        return true;
    }

    public int hashCode() {
        assert false : "hashCode not designed";
        return 42; // any arbitrary constant will do
    }

}
