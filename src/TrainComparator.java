import java.util.Comparator;

public class TrainComparator implements Comparator<Train> {
    @Override
    public int compare(Train t1, Train t2) {
        return t1.getNumber() > t2.getNumber() ? 1 : (t1.getNumber() < t2.getNumber()) ? -1 : 0;
    }
}
