package xjlm.gml_model_Q;

public class Main {
    static gml_Parser gmlP = new gml_Parser();

    public static void mergeMajorTag_keepMinorTags(String MajorTagName, String MinorTagName) {
        /*todo:
            - Merge >1x of <bldg:consistsOfBuildingPart> into topmost instance
            - Overwrite all (use all factors up until)

         */
    }

    public static void mergeSurfaces() {
        // Combine Outer Blocks from "bldg:consistsOfBuildingPart", keep "bldg:boundedBy" nodes unique.
        mergeMajorTag_keepMinorTags("bldg:consistsOfBuildingPart", "bldg:boundedBy");
    }

    public static void main(String[] args) {
        gmlP.readGML("samples/qn1_modela.gml");
//        gmlP.readGML("samples/sampleNote.xml");

        gmlP.debug_print_GMLTree();
    }
}
