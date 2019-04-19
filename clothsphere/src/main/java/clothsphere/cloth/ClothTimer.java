package clothsphere.cloth;

import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.util.Duration;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class ClothTimer extends ScheduledService<Void> {

    //переменный для учета времени
    private long startTime, previousTime;
    private double deltaTime;

    //Периодичность таймера, в мс
    private double fixedDeltaTime = 16;

    /**
     * таймер на паузе
     */
    private boolean paused;

    /**
     * ткань которой принадлежит таймер
     */
    private Cloth parent;

    /**
     * Конструктор таймера
     * @param cloth
     */
    public ClothTimer(Cloth cloth) {
        super();
        this.parent = cloth;
        //Задаем периодичность вызова таймера в милисекндах
        this.setPeriod(Duration.millis(fixedDeltaTime));

        //Создаем и назначем поток таймера
        NanoThreadFactory tf = new NanoThreadFactory();
        this.setExecutor(Executors.newSingleThreadExecutor(tf));
    }

    /**
     *
     * @return возвращает время от запуска
     */
    public long getTime() {
        return System.nanoTime() - startTime;
    }

    /**
     *
     * @return возвращает разницу времени
     */
    public double getDeltaTime() {
        return deltaTime;
    }

    /**
     *
     * @return обновляет значения таймера
     */
    private void updateTimer() {

        long ONE_NANO = 1000000000L;

        if(previousTime <= 0) {
            deltaTime = fixedDeltaTime/100.0f;
        } else
            deltaTime = (getTime() - previousTime) * (10.0f / ONE_NANO);

        previousTime = getTime();
    }


    /**
     * Включаем таймер или снимаеи с паузы
     */
    public void dostart() {
        paused = false;
        if (!isRunning()) {
            start();
        }

    }

    /**
     * Установка таймепа на паузу
     */
    protected void pause() {
        paused = true;
    }

    /**
     * Задача таймера - работа
     * @return
     */
    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {

                //если сейчас пауза не делаем работу
                if(paused) return null;

                //обновляем значения таймера
                updateTimer();

                //в паралельном потоке расчитываем связи, чем больше раз расчитваем тем точнее связи, но и больше нагрузка
                for (int i = 0; i < parent.constraitsAccuracy; i++) {
                    parent.points.parallelStream().forEach(ClothPoint::solveConstraints);
                }

                //в паралельно потоке для каждой точке вызываем расчет физики на дельту времени
                parent.points.parallelStream().forEach(p -> {

                    p.updatePhysics((float)getDeltaTime(), 1);
                });

                return null;
            }
        };
    }

    @Override
    protected void failed() {
        getException().printStackTrace(System.err);
    }

    @Override
    protected void succeeded() {
        super.succeeded();
        parent.updatePoints();
    }

    @Override
    protected void cancelled() {
        super.cancelled();
        reset();
    }

    @Override
    public void start() {
        super.start();
        if (isRunning()) {
            return;
        }
        if (startTime <= 0) {
            startTime = System.nanoTime();
        }
    }

    @Override
    public void reset() {
        super.reset();
        if (!paused) {
            startTime = System.nanoTime();
            previousTime = getTime();
        }
    }

    /**
     * Поток для таймера
     */
    private class NanoThreadFactory implements ThreadFactory {

        public NanoThreadFactory() {
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r, "ClothTimerThread");
            t.setDaemon(true);
            return t;
        }

    }
}