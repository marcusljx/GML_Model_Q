package xjlm.domFunctions;

import org.dom4j.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by marcusljx on 19/02/16.
 *  Library of functions for operating on the Element(Node) Tree-structure defined in the Dom4J library.
 */

public class DOM_Algorithms {

    //===============================================
    //=================== STRING ====================
    //===============================================
    public static String getFullNameOfNode(Element node) {
        return (node.getNamespacePrefix().equals("")) ? node.getName() : node.getNamespacePrefix() + ":" + node.getName();
    }

    public String trimNamespaces(String S) {
        return S.replaceAll("([A-Za-z0-9]*:)", "");
    }

    public static ArrayList<String> getListOfPathsEndingWith(Element node, String name) {   // local recursion stops at first found
        ArrayList<String> result = new ArrayList<>();

        String prefix = node.getNamespacePrefix();
        String nName;
        if(!prefix.equals("")) {
            nName = prefix + ":" + node.getName();
        } else {
            nName = node.getName();
        }

        if(nName.equals(name)) {
            result.add("/"+ nName);
        } else {
            // Traverse Children recursively. Append their results together;
            List<Element> children = node.elements();
            for(Element c : children) {
                result.addAll(getListOfPathsEndingWith(c, name));
            }

            // Add own path in front of all results
            for(int i=0; i<result.size(); i++) {
                result.set(i, "/" + nName + result.get(i));
            }
        }

        return result;
    }

    //===============================================
    //================= TREE SEARCH =================
    //===============================================
    public static Element LowestCommonParent(ArrayList<Element> children) {
        ArrayList<Element> result = new ArrayList<>();
        result.addAll(children);
        ArrayList<Element> visited = new ArrayList<>();

        while((result.size() > 1) && (result.size() != 0)) {
            for(int i=0; i<result.size(); i++) {
                Element e = result.get(i);

                if(e.isRootElement()) {
                    visited.add(e);
                } else {
                    if (!visited.contains(e)) {
                        visited.add(e);
                        result.set(i, e.getParent());   // change element to its parent
                    } else {
                        result.remove(i);   // remove element if it has already been visited.
                        i--;
                    }
                }
            }
        }

        return result.get(0);
    }

    public static ArrayList<Element> getNodesWithName(Element node, String name) {
        ArrayList<Element> result = new ArrayList<>();
        if(getFullNameOfNode(node).equals(name)) {
            result.add(node);
        } else {
            List<Element> children = node.elements();
            for(Element e : children) {
                result.addAll(getNodesWithName(e, name));
            }
        }

        return result;
    }

    // returns list of descendants matching a descendantPath from the A-th sibling named branchPath
    public ArrayList<Element> get_subnodes_from_branchA(Element node, String branchPath, int A, String descendantPath) {
        List<Element> parentCandidates = node.selectNodes(trimNamespaces(branchPath));
        if(parentCandidates.size() == 1) {
            A = 1;
        } else if(parentCandidates.size() < 1) {
            return null;
        }

        // Number of candidates > 1
        Element MajorParent = parentCandidates.get(A);

        // Find
        System.out.println(getFullNameOfNode(MajorParent));
//        MajorParent.selectNodes()

        ArrayList<Element> result = new ArrayList<>();
        return result;
    }
}
