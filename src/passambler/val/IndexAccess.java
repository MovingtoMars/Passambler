package passambler.val;

public interface IndexAccess {
    public Val getIndex(int index);
    
    public void setIndex(int index, Val value);
    
    public int getIndexCount();
}