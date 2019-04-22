package clothsphere.cloth;

import clothsphere.helpers.Vector4;

import java.util.HashMap;


/**
 * Точка ткани
 */
public class ClothPoint {

    /**
     * Ткань которой пренадлежит точка
     */
    private final Cloth parent;

    public double mass = 0,  //масса точки
            initMass = 0;  //начальная масса точки

    public Vector4
            position,     //положение точки
            oldPosition,  //старое положение точки
            initPosition, //начальное положение точки
            force;        //вектор силы

    /**
     * Ассоциативный массив, точка ткани и связи с ней
     */
    private final HashMap<ClothPoint, Constraint> constraints = new HashMap<>();


    /**
     * Конструктор точки
     * @param parent ткань
     * @param mass масса точки
     * @param x коодинаты x
     * @param y коодинаты y
     * @param z коодинаты z
     */
    public ClothPoint(Cloth parent, double mass, double x, double y, double z) {

        this.position = new Vector4((float) x, (float) y, (float) z);
        this.oldPosition = new Vector4((float) x, (float) y, (float) z);
        this.force = new Vector4(0, 0, 0);
        this.initPosition = new Vector4((float) x, (float) y, (float) z);
        this.initMass = mass;

        this.parent = parent;
        this.mass = mass;
    }

    /**
     * Сброс точки в начальное положение и массу
     */
    public final void reset() {
        this.oldPosition.x = this.initPosition.x;
        this.oldPosition.y = this.initPosition.y;
        this.oldPosition.z = this.initPosition.z;

        mass = initMass;

        this.position.x = this.initPosition.x;
        this.position.y = this.initPosition.y;
        this.position.z = this.initPosition.z;
    }

    /**
     * Установить текущую позицию
     * @param pos координаты точки
     */
    public void setPosition(Vector4 pos) {
        position.x = pos.x;
        position.y = pos.y;
        position.z = pos.z;
    }

    /**
     * Установить старую позицию
     * @param oldPosition координаты точки
     */
    public void setOldPosition(Vector4 oldPosition) {
        this.oldPosition.x = oldPosition.x;
        this.oldPosition.y = oldPosition.y;
        this.oldPosition.z = oldPosition.z;
    }

    /**
     * Установить вектор силы
     * @param p
     */
    private void setForce(Vector4 p) {
        this.force = p;
    }

    /**
     * Очистить силу
     */
    public void clearForces() {
        setForce(new Vector4(0, 0, 0));
    }

    /**
     * Присоединить к
     * @param other другая точка
     * @param linkDistance растояние
     * @param stiffness жесткость
     */
    public final void attatchTo(ClothPoint other, double linkDistance, double stiffness) {
        attatchTo(this, other, linkDistance, stiffness);
    }

    public final void attatchTo(ClothPoint self, ClothPoint other, double linkDistance, double stiffness) {
        Link pl = new Link(self, other, linkDistance, stiffness);
        this.constraints.put(other, (Constraint) pl);
    }

    /**
     * Применяем вектор силы
     * @param force
     */

    public void applyForce(Vector4 force) {
        this.force.x += force.x;
        this.force.y += force.y;
        this.force.z += force.z;
    }
    /**
     * Решеаем связи в паралельной потоковой обработке
     */
    public void solveConstraints() {
        constraints.values().parallelStream().forEach(Constraint::solve);
    }

    /**
     * Реализация физики падения ткани
     */
    public void updatePhysics(double dt, double t) {

        //Выбполням синхронно с другими потоками
        synchronized (this) {

            //Высчитваем разнизу межу позициями, старой и текущей
            Vector4 vel = new Vector4(
                    (position.x - oldPosition.x),
                    (position.y - oldPosition.y),
                    (position.z - oldPosition.z)
            );

            //Квадрат времени
            float dtSq = (float) (dt * dt);

            //Гравитация
            double GRAVITY = -0.98;

            //Направляем гравитацию по оси Z
            applyForce(new Vector4(0,0, (float) GRAVITY));

            //Вектор ускорения
            Vector4 acceleration = new Vector4(0,0,0);

            //Если масса точки больше 0, делим вектор силы на массу.
            if(mass > 0) {
                acceleration = force.divide((float) mass);
            }

            //Вычесляем следующую позицию используя алгоритм Verlet Integration
            Vector4 next = position.add(vel.multiply((float)dt)).add(acceleration.multiply((float) (dtSq)));

            //Масштаб радиуса сферы
            float scaleRatio = 1.06f;

            //Делаем проверяемый радиус чуть больше чем реальный, чтобы ткань не просвечивала
            float sphereRadius = (float) (parent.sphere.getRadius() * scaleRatio);

            //Флаг дальнейшего движения точки
            boolean movable = true;

            //скольжение
            boolean slide = false;

            //Если позиция точки в сфере
            if(position.squaredLength() < sphereRadius*sphereRadius) {


                if(slide) {
                    //нормализуем вектор позиции
                    Vector4 normal = position.normalize();
                    //инвертируем
                    Vector4 neg = normal.multiply(-1);

                    float vertical = Math.max(force.dotProduct(neg),  0 );

                    //если он не вертиальный
                    if (vertical != 0) {
                        //добавляем небольшой случайный боковой коеффициент силы
                        //случайность зададим у самой ткани а не для каждой точки
                        force.y += 3-parent.ky;
                        force.x += 3-parent.kx;

                        if (force.length() > 0 || vertical == 0)
                            movable = true;
                        else
                            movable = false;

                    }
                } else {
                    //очищаем силы
                    clearForces();
                    //запрещаем передвижение
                    movable = false;
                }
            }

            if(movable) {

                //Если сила гравитации слишком большая ограничиваем движение чтобы ткань не порвалась.
                if(force.z < -350) {
                    force.z = -350;

                    //Если отключено скольжение фиксируем точки с большой силой чтобы ограничить тряску
                    if(!slide) {
                        setPosition(oldPosition);
                        setOldPosition(oldPosition);
                    }
                }

                next = position.add(vel.multiply((float) dt)).add(acceleration.multiply((float) (dtSq)));

            }

            //если скольжение то делаем имтацию трения
            if(slide)
                if(next.squaredLength() < sphereRadius*sphereRadius) {

                    //смещаем позиции вдоль радиуса сферы
                    next = next.normalize().multiply(sphereRadius);

                    //увиличиваем массу чтобы имитировать трение
                    mass *= 1 + dt/9.1;
                } else {

                    if(mass > 2) {
                        // если точка освободилась а масса > 2, возвращаем в исходное состояние
                        mass = initMass;
                    }
                }

            //Проверяем коснулась ли ткань пола
            if(next.z<-500.5f) {
                //Если да фиксируем ось Z
                next.z = -500.5f;
            }

            //если почти уже пол, убираем силу с некотрых точек, чтобы оставить ткань лежать не идеально ровно
            if(next.z < -450f) clearForces();

            //смещаем вектора позиции
            if(movable) {
                setOldPosition(position);
                //if(movable)
                setPosition(next);
            }else {
                setPosition(oldPosition);
                setOldPosition(oldPosition);
            }

        }
    }

}



