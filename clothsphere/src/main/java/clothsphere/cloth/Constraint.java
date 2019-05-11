package clothsphere.cloth;

import java.util.stream.IntStream;

/**
 *  Интерфейс связывания
 */
@FunctionalInterface
public interface Constraint {

    public void solve();

    /**
     * Решенеие связования
     * @param iter кол-во итераций
     */
    public default void solve(int iter){
        IntStream.range(0, iter).parallel().forEach(i-> solve());
    }
}

