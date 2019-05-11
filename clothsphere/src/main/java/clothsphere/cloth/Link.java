package clothsphere.cloth;

import clothsphere.helpers.Vector4;

/**
 * Свзяь между точками ткани
 */
public class Link implements Constraint {

    private final double distance, stiffness;

    private final ClothPoint p1, p2;

    public Link(ClothPoint p1, ClothPoint p2, double distance, double stiffness) {
        this.p1 = p1;
        this.p2 = p2;
        this.distance = distance;
        this.stiffness = stiffness;
    }


    /**
     * Решение для связывания точек ткани, основанное на разности масс
     */
    @Override
    public void solve() {

        //расчет растояния между двумя точками масс
        Vector4 diff = new Vector4(
                p1.position.x - p2.position.x,
                p1.position.y - p2.position.y,
                p1.position.z - p2.position.z
        );

        double d = diff.magnitude();

        double difference = (distance - d) / d;

        double im1 = 1 / p1.mass;
        double im2 = 1 / p2.mass;
        double scalarP1 = (im1 / (im1 + im2)) * stiffness;
        double scalarP2 = stiffness - scalarP1;

        synchronized (this) {
            p1.position.x += ((float) (diff.x * scalarP1 * difference));
            p1.position.y += ((float) (diff.y * scalarP1 * difference));
            p1.position.z += ((float) (diff.z * scalarP1 * difference));

            p2.position.x -= ((float) (diff.x * scalarP2 * difference));
            p2.position.y -= ((float) (diff.y * scalarP2 * difference));
            p2.position.z -= ((float) (diff.z * scalarP2 * difference));
        }

    }

}