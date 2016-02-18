package xjlm.gml_model_Q;

import org.dom4j.*;
import org.dom4j.io.SAXReader;

import java.io.File;
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

    public void debug_print_GMLTree() {
        debug_print_GMLTree(root, 0);
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
}
