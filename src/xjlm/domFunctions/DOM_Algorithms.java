package xjlm.domFunctions;

import org.dom4j.Element;

import java.util.ArrayList;

/**
 * Created by marcusljx on 19/02/16.
 *  Library of functions for operating on the Element(Node) Tree-structure defined in the Dom4J library.
 */

public class DOM_Algorithms {
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
}
