package gml_model_repair;

import com.sun.javafx.geom.Vec3d;

import java.util.ArrayList;

/**
 * Created by marcusljx on 24/02/16.
 */
public class gml_PolygonSurface {
    private ArrayList<Vec3d> posList = new ArrayList<>();

    public gml_PolygonSurface() {}
    public gml_PolygonSurface(String str_posList) {
        set_PositionList(str_posList);
    }

    public gml_PolygonSurface set_PositionList(String str_posList) {
        String[] values = str_posList.split(" +");

        for(int i=0; i<values.length; i+=3) {
            double x = Double.parseDouble(values[i]);
            double y = Double.parseDouble(values[i+1]);
            double z = Double.parseDouble(values[i+2]);

            posList.add(new Vec3d(x,y,z));
        }
        return this;
    }

    public Vec3d get_surfaceNormal() {
        // Use 1st 3 coordinates
        Vec3d U = new Vec3d(posList.get(1));
        U.sub(posList.get(0));

        Vec3d V = new Vec3d(posList.get(2));
        V.sub(posList.get(0));

        Vec3d result = new Vec3d();
        result.cross(U, V);
        result.normalize();

        return result;
    }

    public String get_surfaceType() {
        Vec3d normal = get_surfaceNormal();
        long rounded_z = Math.round(normal.z);

        if (rounded_z == 1) {
            return "RoofSurface";
        } else if (rounded_z == -1) {
            return "GroundSurface";
        } else {
            return "WallSurface";
        }
    }


    @Override
    public String toString() {
        String result = "[" + posList.size() + " points]\n";
        for(Vec3d p : posList) {
            result += p.toString() + "\n";
        }
        return result;
    }
}
