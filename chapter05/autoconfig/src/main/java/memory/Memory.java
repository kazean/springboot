package memory;

public class Memory {
    private long max;
    private long used;

    public Memory(long max, long used) {
        this.max = max;
        this.used = used;
    }

    public long getMax() {
        return max;
    }

    public long getUsed() {
        return used;
    }

    @Override
    public String toString() {
        return "Memory{" +
                "max=" + max +
                ", used=" + used +
                '}';
    }
}
