# GML_Model_Q
Small programs to work with GML files.

## How To Use
Compile the program to Working Directory:
```
javac -cp 'lib/*' -sourcepath . src/gml_model_repair/*.java -d .
```

Run the program using:
```
java -cp 'lib/*:.' gml_model_repair.Main <Q1 | Q2> <inputFilePath> <outputFilePath>
```

For example, running
```
java -cp 'lib/*:.' gml_model_repair.Main Q1 samples/qn1_modela.gml testOutput.gml
```
will perform the repair operation requested in Question 1 on a CityGML file (`samples/qn1_modela.gml`) and write the repaired file to `testOutput.gml`.

## Methodology 
### Q1
The program represents GML files as a tree structure using the Dom4J library. For the following explanations, the terms `tag` and `subtree` are equivalent from a functional point of view. The term `node` refers to a `tag` by itself (ie. without its children). `DOM` refers to the entire GML tree structure as a whole.

The problem represented in Question 1 is a GML file which has two `bldg:consistsOfBuildingPart` subtrees containing different surfaces, all of which are encapsulated within `bldg:boundedBy` tags. The surfaces are to be merged into the top-most `bldg:consistsOfBuildingPart` tag, with each `bldg:boundedBy` subtree remaining unique. The `gml:id` of each added surface is also added to the `gml:CompositeSurface` subtree of the top-most `bldg:consistsOfBuildingPart` subtree, as a `gml:surfaceMember` tag.

To achieve this, the program:

1. Searches the entire DOM for `bldg:consistsOfBuildingPart` tags.
2. In the 1st `bldg:consistsOfBuildingPart` subtree, search for the Lowest Common Ancestor of all `bldg:boundedBy` nodes. Let this be called `LCA`. Also search for the `gml:CompositeSurface` node.
3. For all remaining `bldg:consistsOfBuildingPart` subtrees, search for all nodes with the same tag name as `LCA`. Let these nodes be called `LCA_Cousins`.
4. For all nodes in `LCA_Cousins`, detach all their children nodes that are called `bldg:boundedBy` and attach them (the children nodes) to `LCA`. 
5. For each detach-and-attach operation, search for the `gml:Polygon` tag and extract its `gml:id`. Create a `gml:surfaceMember` tag with this id-attribute, and add it as a child tag of the `gml:CompositeSurface` found in step [2].
6. Detach from the DOM all `bldg:consistsOfBuildingPart` except the top-most one.
