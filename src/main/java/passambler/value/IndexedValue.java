package passambler.value;

public interface IndexedValue {
    public Value getIndex(int index);

    public void setIndex(int index, Value value);

    public int getIndexCount();
}
