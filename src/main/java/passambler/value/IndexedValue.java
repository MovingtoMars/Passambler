package passambler.value;

public interface IndexedValue {
    public Value getIndex(Value key);

    public void setIndex(Value key, Value value);

    public int getIndexCount();
}
