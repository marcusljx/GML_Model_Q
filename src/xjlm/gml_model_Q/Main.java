package xjlm.gml_model_Q;

import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;
import xjlm.domFunctions.DOM_Algorithms;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    static gml_Parser qn1_modelA = new gml_Parser();
    static gml_Parser qn2_modelA = new gml_Parser();

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

    //===============================================
    //================ Q1 FUNCTIONS =================
    //===============================================
    public static void mergeMajorTag_keepMinorTags(gml_Parser parsedDoc, String MajorTagName, String MinorTagName) {
        // Find all Minor Tags in all non-first MajorTag Branches
        ArrayList<Element> MajorTagNodes = parsedDoc.getNodesWithName(MajorTagName);
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

    public static void Qn1() {
        String MajorTagName = "bldg:consistsOfBuildingPart";
        String MinorTagName = "bldg:boundedBy";

        // Combine Tags from MajorTagName up until MinorTagName. Keep MinorTagName nodes unique.
        mergeMajorTag_keepMinorTags(qn1_modelA, MajorTagName, MinorTagName);
    }

    //===============================================
    //================ Q2 FUNCTIONS =================
    //===============================================
    // if fullString contains 5 sets of XYZ (dimensions==3) values (ie. 15 values), and if setIndex==1, extract the 2nd set of XYZ values (ie. 4th,5th,6th values)
    public static String extractCoordinateString(String fullString, int setIndex, int dimensions) throws ArrayIndexOutOfBoundsException {
        String result = "";
        String[] values = fullString.split(" +");

        result += values[setIndex*dimensions] + " ";
        result += values[(setIndex*dimensions)+1] + " ";
        result += values[(setIndex*dimensions)+2];

        return result;
    }

    public static void generateGroundSurfaceTree(Element parent, String id, int srsDimension, String coordinateData) {
        parent  .addElement("bldg:boundedBy")
                .addElement("bldg:GroundSurface")
                .addElement("bldg:lod2MultiSurface")
                .addElement("gml:MultiSurface")
                .addElement("gml:surfaceMember")
                .addElement("gml:Polygon")
                    .addAttribute("gml:id", id)        // Add id attribute to Polygon tag
                .addElement("gml:exterior")
                .addElement("gml:LinearRing")
                .addElement("gml:posList")
                    .addAttribute("srsDimension", String.valueOf(srsDimension)) // Add srsDimension attribute and text data to posList
                    .addText(coordinateData);
    }

    public static void deriveGroundSurfaceFromWallSurfaces(gml_Parser parsedDoc, String familyTagName, String surfaceID) throws ArrayIndexOutOfBoundsException {
        ArrayList<Element> Surfaces = parsedDoc.getNodesWithName(familyTagName);
        ArrayList<Element> wallSurfaces = new ArrayList<>();

        // Find all WallSurfaces
        for(Element S : Surfaces) {
            List<Element> children = S.elements();
            if(children.get(0).getQualifiedName().equals("bldg:WallSurface")) {
                wallSurfaces.add(S);
            }
        }

        int srsDimensions = 3;  // default
        // For Each WallSurface, take the 2nd set of XYZ coordinates, add it to GroundSurfaceCoordinates
        String groundSurfacePosListText = "";
        for(int i=0; i<=wallSurfaces.size(); i++) { // using <= because have to loop back to get first wallSurface again
            Element W = wallSurfaces.get( i % wallSurfaces.size() );    // cyclic index

            Element posListNode = DOM_Algorithms.getNodesWithName(W, "gml:posList").get(0);
            String posList = posListNode.getText();
            srsDimensions = Integer.parseInt(posListNode.attribute("srsDimension").getValue());

            groundSurfacePosListText += extractCoordinateString(posList, 1, srsDimensions) + " ";   // extract the 2nd set of XYZ values
        }
        groundSurfacePosListText = groundSurfacePosListText.trim();

        // Add Ground Surface to the parent of the wall surfaces
        Element parent = wallSurfaces.get(0).getParent();
        generateGroundSurfaceTree(parent, surfaceID, srsDimensions, groundSurfacePosListText);

        // Add ground surface id to composite surface
        Element compositeSurface = parsedDoc.getNodesWithName("gml:CompositeSurface").get(0);
        compositeSurface.addElement("gml:surfaceMember")
                .addAttribute("xlink:href", "#"+surfaceID);
    }

    public static void Qn2() {
        String FamilyTagName = "bldg:boundedBy";
        deriveGroundSurfaceFromWallSurfaces(qn2_modelA, FamilyTagName, "gs_BM_p32767_0");
    }

    //===============================================
    //=================== MAIN ======================
    //===============================================
    public static void main(String[] args) throws DocumentException, IOException {
        if(args.length != 2) {
            System.err.println("Usage: GML_Model_Q.java <Q1 | Q2> <outputFilePath>");
            System.exit(-1);
        }
        String QuestionNumber = args[0];
        String outputFilePath = args[1];

        if(QuestionNumber.equals("Q1")) {
            qn1_modelA.readGML("samples/qn1_modela.gml");
            Qn1();
            qn1_modelA.writeXMLtoFile(outputFilePath);

        } else if(QuestionNumber.equals("Q2")) {
            qn2_modelA.readGML("samples/qn2_modela.gml");
            Qn2();
            qn2_modelA.writeXMLtoFile(outputFilePath);

        } else {
            System.err.println("ERROR: Question Number should match one of the following: Q1 | Q2");
            System.exit(-1);
        }
    }
}
