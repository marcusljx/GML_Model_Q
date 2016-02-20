package xjlm.gml_model_Q;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import xjlm.domFunctions.DOM_Algorithms;

import java.util.ArrayList;
import java.util.List;

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
        ArrayList<Element> MajorTagNodes = gmlP.getNodesWithName(MajorTagName);

        // 1.Find Minor Tag Descendants of 1st Major Tag, and find their lowest common ancestor (worst case: LCA is MajorNodeToKeep)
        Element MajorNodeToKeep = MajorTagNodes.get(0);
        ArrayList<Element> MinorTagDescendantsOfMajorNodeToKeep = DOM_Algorithms.getNodesWithName(MajorNodeToKeep, MinorTagName);
        Element LCA = DOM_Algorithms.LowestCommonParent(MinorTagDescendantsOfMajorNodeToKeep);

        System.out.println("LCA = \t" + LCA);

        // 2.Find corresponding LCA-name Nodes (CousinsOfLCA) with same name as LCA in MajorTagNodes[1:]
        ArrayList<Element> CousinsOfLCA = new ArrayList<>();
        for(int i=1; i<MinorTagDescendantsOfMajorNodeToKeep.size(); i++) {
            Element current = MinorTagDescendantsOfMajorNodeToKeep.get(i);
            CousinsOfLCA.addAll( DOM_Algorithms.getNodesWithName(current, DOM_Algorithms.getFullNameOfNode(LCA)) );
        }

        // 3.TODO: Move all children of all CousinsOfLCA to LCA.


//        ArrayList<Element> subNodes = gmlP.get_subnodes_from_branchA(MajorTagsPaths.get(0), 1,MinorTagsPaths.get(0));
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
