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