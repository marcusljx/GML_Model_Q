package gml_model_repair;

import javafx.geometry.Point3D;

import java.util.ArrayList;

/**
 * Created by marcusljx on 24/02/16.
 */
public class gml_PolygonSurface {
    private ArrayList<Point3D> posList = new ArrayList<>();

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

            posList.add(new Point3D(x,y,z));
        }
        return this;
    }

    @Override
    public String toString() {
        String result = "[" + posList.size() + " points]\n";
        for(Point3D p : posList) {
            result += p.toString() + "\n";
        }
        return result;
    }
}
