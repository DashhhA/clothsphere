package clothsphere.cloth;

import clothsphere.helpers.Vector4;

import java.util.HashMap;

public class ClothPoint {
    private final Cloth parent;
    public double mass = 0,
            initMass = 0;

    public Vector4 position,
                oldPosition,
                initPosition,
                force;

    private final HashMap<ClothPoint, Constraint> constrains = new HashMap<>();

    public ClothPoint(Cloth parent, double mass, double x, double y, double z){
        this.position = new Vector4((float)x, (float) y, (float) z);
        this.oldPosition = new Vector4((float)x, (float) y, (float) z);
        this.force = new Vector4(0, 0, 0);
        this.initPosition = new Vector4((float)x, (float) y, (float) z);
        this.initMass = mass;
        this.parent = parent;
        this.mass = mass;
    }

    public  final void reset(){
        this.oldPosition.x = this.initPosition.x;
        this.oldPosition.y = this.initPosition.y;
        this.oldPosition.z = this.initPosition.z;

        mass = initMass;

        this.position.x = this.initPosition.x;
        this.position.y = this.initPosition.y;
        this.position.z = this.initPosition.z;
    }

    public void setPosition(Vector4 pos){
        position.x = pos.x;
        position.y = pos.y;
        position.z = pos.z;
    }

    public void setOldPosition(Vector4 oldPosition){
        this.oldPosition.x = oldPosition.x;
        this.oldPosition.y = oldPosition.y;
        this.oldPosition.z = oldPosition.z;
    }

    //https://www.programcreek.com/java-api-examples/?code=FXyz/FXyzLib/FXyzLib-master/src/org/fxyz/shapes/complex/cloth/WeightedPoint.java
    public final void attatchTo(ClothPoint other, double linkDistance, double stiffness) {
        attatchTo(this, other, linkDistance, stiffness);
    }

    public final void attatchTo(ClothPoint self, ClothPoint other, double linkDistance, double stiffness) {
        Link pl = new Link(self, other, linkDistance, stiffness);
        this.constrains.put(other, (Constraint) pl);
    }

    public void solveConstraints(){
        constrains.values().parallelStream().forEach(Constraint::solve);
    }

    public  void updatePhysics(double dt, double t){
        synchronized (this){

        }
    }

}
