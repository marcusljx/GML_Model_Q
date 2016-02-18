package xjlm.gml_model_Q;

import org.dom4j.DocumentException;

import java.util.ArrayList;

public class Main {
    static gml_Parser gmlP = new gml_Parser();

    //==== default stuff
    public static <T> void print(ArrayList<T> arr)  {
        for(T item : arr) {
            System.out.println(item);
        }
    }



    public static void mergeMajorTag_keepMinorTags(String MajorTagName, String MinorTagName) {
        // Find all Minor Tags in all non-first MajorTag Branches
        ArrayList<String> MajorTagsPaths = gmlP.getListOfPathsEndingWith(MajorTagName);
        ArrayList<String> MinorTagsPaths = gmlP.getListOfPathsEndingWith(MinorTagName);

        System.out.println("Major Tags:");
        print(MajorTagsPaths);
        System.out.println("Minor Tags:");
        print(MinorTagsPaths);


        // Move Minor Tags to 1st MajorTag, where their immediate parent is shared with MinorTags in the 1st MajorTag


    }

    public static void mergeSurfaces() {
        // Combine Outer Blocks from "bldg:consistsOfBuildingPart", keep "bldg:boundedBy" nodes unique.
        mergeMajorTag_keepMinorTags("bldg:consistsOfBuildingPart", "bldg:boundedBy");

        // Add surface id to composite surface
    }

    public static void main(String[] args) throws DocumentException {
        gmlP.readGML("samples/qn1_modela.gml");
        mergeSurfaces();
    }
}
