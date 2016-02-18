package xjlm.gml_model_Q;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by marcusljx on 18/02/16.
 */
public class gml_Parser {
    private static Document root;

    public void readGML(String filepath) {
        try {
            // Create DocumentBuilder
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(false);
            DocumentBuilder builder = factory.newDocumentBuilder();

            // Parse GML file
            root = builder.parse(new FileInputStream(new File(filepath)));

        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
    }

    public void debug_print_GMLTree() {
        debug_print_GMLTree(root, 0);
    }

    private void debug_print_GMLTree(Node node, int level) {
        String line = "";
        String spacing = new String( new char[level]).replace("\0", "  ");  // add two spaces for each level

        // Evaluate Name
        String nName = node.getNodeName();
        boolean isTag = (nName.substring(0,1).equals("#")); // T/F

        // Evaluate Value
        String nValue = node.getNodeValue();
        boolean hasValue = ( (nValue != null) && (!nValue.matches("^\\s*$")) ); // T/F

        line += (isTag && !hasValue) ? "" : spacing;

        // Evaluate Children
        NodeList cList = node.getChildNodes();
        boolean isParent = (cList.getLength() > 0); // T/F

        // Formatting
        if(isTag && hasValue) {
            line += "(" + nName + " = " + nValue + ")";
        } else if (!isTag) {
            line += nName;
        }

        System.out.print(line);

        if(isParent) {
            System.out.println(" {");

            //recurse through children
            for(int i=0; i<cList.getLength(); i++) {
                Node child = cList.item(i);
                debug_print_GMLTree(child, level + 1);
            }

            System.out.println("\n" + spacing + "}");
        }
    }


}
