package xjlm.gml_model_Q;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import xjlm.domFunctions.DOM_Algorithms;

import java.awt.*;
import java.io.IOException;
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

    public static <T> void print(List<T> arr)  {
        for(T item : arr) {
            System.out.println(item);
        }
    }




    public static void mergeMajorTag_keepMinorTags(String MajorTagName, String MinorTagName) {
        // Find all Minor Tags in all non-first MajorTag Branches
        ArrayList<Element> MajorTagNodes = gmlP.getNodesWithName(MajorTagName);
        Element MajorNodeToKeep = MajorTagNodes.get(0);

        //=============== SEGMENT: ADDING SURFACE IDs TO 1ST COMPOSITE SURFACE
        // 1.Find Node storing CompositeSurface in 1stMajorTag
        ArrayList<Element> temp = DOM_Algorithms.getNodesWithName(MajorNodeToKeep, "gml:CompositeSurface");
        if(temp.size() != 1) { System.exit(-1); }
        Element CompositeSurfaceNode = temp.get(0);

        // 2. Search (2nd->Onwards) MajorTagNodes for all Children of all "CompositeSurface" nodes
        ArrayList<Element> newSurfacesToAdd = new ArrayList<>();
        for(int i=1; i< MajorTagNodes.size(); i++) {
            ArrayList<Element> csNodes = DOM_Algorithms.getNodesWithName(MajorTagNodes.get(i), "gml:CompositeSurface"); // example only shows 1 csNode, but general case may be >1
            for(Element c : csNodes) {
                newSurfacesToAdd.addAll(c.elements());
            }
        }
        CompositeSurfaceNode.content().addAll(newSurfacesToAdd);    // 1stCompositeSurfaceNode -> add all children of other compositeSurfaceNodes

        //=============== SEGMENT: MOVING SURFACE NODES TO 1ST MAJOR TAG NODE
        // 1.Find Minor Tag Descendants of 1st Major Tag, and find their lowest common ancestor (worst case: LCA is MajorNodeToKeep)
        ArrayList<Element> MinorTagDescendantsOfMajorNodeToKeep = DOM_Algorithms.getNodesWithName(MajorNodeToKeep, MinorTagName);
        Element LCA = DOM_Algorithms.LowestCommonParent(MinorTagDescendantsOfMajorNodeToKeep);

        // 2.Find corresponding LCA-name Nodes (CousinsOfLCA) with same name as LCA in MajorTagNodes[1:]
        ArrayList<Element> CousinsOfLCA = new ArrayList<>();
        for(int i=1; i<MajorTagNodes.size(); i++) {
            Element current = MajorTagNodes.get(i);

            // Add surfaceID to composite surface of 1stMajorTag.

            CousinsOfLCA.addAll( DOM_Algorithms.getNodesWithName(current, LCA.getQualifiedName()) );
        }

        // 3.Move all children of all CousinsOfLCA, whose name matches MinorTagName, to LCA.
        for(Element e : CousinsOfLCA) {
            List<Element> children = e.elements();
            for(Element C : children) {
                if(C.getQualifiedName().equals(MinorTagName)) {
                    C.detach();
                    LCA.content().add(C);
                }
            }
        }

        //=============== SEGMENT: CLEANING UP ALL SECONDARY MAJOR TAG NODES
        for(int i=1; i< MajorTagNodes.size(); i++) {
            MajorTagNodes.get(i).detach();
        }
    }

    public static void mergeSurfaces() {
        String MajorTagName = "bldg:consistsOfBuildingPart";
        String MinorTagName = "bldg:boundedBy";



        // Combine Outer Blocks from "bldg:consistsOfBuildingPart", keep "bldg:boundedBy" nodes unique.
        mergeMajorTag_keepMinorTags(MajorTagName, MinorTagName);

        // Add surface id to composite surface

        // Remove secondary Major Tags
    }

    public static void main(String[] args) throws DocumentException, IOException {
        gmlP.readGML("samples/qn1_modela.gml");
        mergeSurfaces();

        gmlP.writeXMLtoFile("out/testOutput.gml");
    }
}
