package xjlm.gml_model_Q;

import org.dom4j.*;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by marcusljx on 18/02/16.
 */
public class gml_Parser {
    private static Document document;
    private static Element root;

    public void readGML(String filepath) throws DocumentException {
        SAXReader reader = new SAXReader();
        document = reader.read(new File(filepath));
        root = document.getRootElement();
    }

    private String getAttributeString(Element node) {     // returns a string of the nodes attributes and their values
        String output = "";

        List<Attribute> attr = node.attributes();
        for(Attribute A : attr) {
            output += " [";

            // Add namespace prefix if it exists
            if(!A.getNamespacePrefix().equals("")) {
                output += A.getNamespacePrefix() + ":";
            }

            output += A.getName() + " = \"" + A.getValue() + "\"]";
        }
        return output;
    }

    private void debug_print_GMLTree(Element node, int level) {
        String indent = new String(new char[level]).replace("\0", "  ");
        String currentLine = indent + node.getName();

        // Check Kids
        List<Element> children = node.elements();
        boolean hasKids = (children.size() > 0);

        // Check Attributes
        currentLine += getAttributeString(node);

        // Fill in Data (text data)
        if(node.isTextOnly()) {
            currentLine += " (" + node.getText() + ")";
        }

        // Print full line
        System.out.print( currentLine );

        // Print block
        if(hasKids) {
            System.out.println(" {");

            // Recurse over children
            for(Element c : children) {
                debug_print_GMLTree(c, level + 1);
            }

            System.out.println(indent + "}");
        } else {
            System.out.println();
        }
    }

    public String trimNamespaces(String S) {
        return S.replaceAll("([A-Za-z0-9]*:)", "");
    }

    //=================== GETTERS
    public Element getRoot() {
        return root;
    }

    public String getFullNameOfNode(Element node) {
        return (node.getNamespacePrefix().equals("")) ? node.getName() : node.getNamespacePrefix() + ":" + node.getName();
    }

    public Document getDocument() {
        return document;
    }

    public ArrayList<String> getListOfPathsEndingWith(Element node, String name) {   // local recursion stops at first found
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

    public ArrayList<Element> getNodesWithName(Element node, String name) {
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
    public ArrayList<Element> get_subnodes_from_branchA(String branchPath, int A, String descendantPath) {
        List<Element> parentCandidates = root.selectNodes(trimNamespaces(branchPath));
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

    //==================== OVERRIDES
    public void debug_print_GMLTree() {
        debug_print_GMLTree(root, 0);
    }

    public ArrayList<Element> getNodesWithName(String name) {
        return getNodesWithName(root, name);
    }

    public ArrayList<String> getListOfPathsEndingWith(String name) {
        return getListOfPathsEndingWith(root, name);
    }
}
