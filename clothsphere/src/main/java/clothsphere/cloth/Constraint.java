package clothsphere.cloth;

import java.util.stream.IntStream;

public interface Constraint {
    public void solve();

    public default void solve(int iter){
        IntStream.range(0, iter).parallel().forEach(i ->solve());
    }
}
