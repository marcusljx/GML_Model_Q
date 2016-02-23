# GML_Model_Q
Small functions to repair/modify GML files. Uses the Dom4J library.

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
The program represents GML files as a tree structure using the Dom4J library. For the following explanations, the terms `tag` and `subtree` are equivalent from a functional point of view. The term `node` refers to a `tag` by itself (ie. without its children). `DOM` refers to the entire GML tree structure as a whole.

### Q1
The problem represented in Question 1 is a GML file which has two `bldg:consistsOfBuildingPart` subtrees containing different surfaces, all of which are encapsulated within `bldg:boundedBy` tags. The surfaces are to be merged into the top-most `bldg:consistsOfBuildingPart` tag, with each `bldg:boundedBy` subtree remaining unique. The `gml:id` of each added surface is also added to the `gml:CompositeSurface` subtree of the top-most `bldg:consistsOfBuildingPart` subtree, as a `gml:surfaceMember` tag.

To achieve this, the program:

1. Searches the entire DOM for `bldg:consistsOfBuildingPart` tags.
2. In the 1st `bldg:consistsOfBuildingPart` subtree, search for the Lowest Common Ancestor of all `bldg:boundedBy` nodes. Let this be called `LCA`. Also search for the `gml:CompositeSurface` node.
3. For all remaining `bldg:consistsOfBuildingPart` subtrees, search for all nodes with the same tag name as `LCA`. Let these nodes be called `LCA_Cousins`.
4. For all nodes in `LCA_Cousins`, detach all their children nodes that are called `bldg:boundedBy` and attach them (the children nodes) to `LCA`. 
5. For each detach-and-attach operation, search for the `gml:Polygon` tag and extract its `gml:id`. Create a `gml:surfaceMember` tag with this id-attribute, and add it as a child tag of the `gml:CompositeSurface` found in step [2].
6. Detach from the DOM all `bldg:consistsOfBuildingPart` except the top-most one.


### Q2
The program determines the coordinates of a `GroundSurface` from the existing coordinates of `WallSurface`s, and generates a subtree with them. The `id` of the newly-generated `GroundSurface` is hard-coded to follow the example in Question 1.

The basic concept here is how a surface is "drawn" in CityGML files. A surface is drawn using a sequence of "lines", which has two points (beginning and end). A rectangular surface is thus drawn with 4 lines, which means the `gml:posList` must accommodate 5 sets of coordinates (ie. point1, point2, point3, point4, point1). Failing to include the final "point1" coordinate will mean that the line between point4 and point1 will not be drawn (incomplete surface). The `srsDimension` attribute determines the dimensionality of each coordinate. For example, a rectangular surface with `srsDimenstion=3` will have `(4+1)*3 = 15` values in its `posList`, while a triangular surface with `srsDimension=2` will have `(3+1)*2 = 8` values.

To find the coordinates of a `GroundSurface`, the program:

1. Reads through the `DOM`'s `bldg:boundedBy` tags.
2. For each `bldg:boundedBy` subtree that has an immediate child node called `bldg:WallSurface`, find its `gml:posList` descendant and extract the data. Add these coordinates to a `ListOfPositions`. Find the `srsDimension` attribute value.
3. The number of sides of the `GroundSurface`, `N` == number of items in `ListOfPositions`.
3. For `N+1` times, take each `posList` in `ListOfPositions`, extract the second set of coordinate values based on the `srsDimension`. Add these values to a single `GroundSurfacePosList`. In the sample file, `srsDimension=3`, so the program extracts the 4th, 5th, and 6th value in each `posList`. When the loop reaches the `N+1`-th time, use the 1st `posList` again (cycle behaviour).
4. Generate a `bldg:boundedBy` subtree that represents a `GroundSurface`, with the `GroundSurfacePosList`. Attach this `bldg:boundedBy` tag to the parent of the `DOM`'s other `bldg:boundedBy` tags.
