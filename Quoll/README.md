SWEN90014-2019-BI-Quoll

University of Melbourne


Big Issue Classroom Rostering System


Runs with Java 11. Run au.org.thebigissue.rostering.gui.App to launch application.

A ZIP file is also provided in the /bin directory, containing the JAR file and an EXE file designed for Windows 7 and 10. A user manual and a user tutorial is also included in this ZIP file.


The rostering component relies on OptaPlanner, an open-source constraint solver written in Java, and freely licensed under the Apache Licence, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0).

Some of the files used in this project have been derived from example files provided in the OptaPlanner package. We note that the file /src/java/au/org/thebigissue/rostering/solver/AbstractPersistable.java is not the work of the student team.

The PDF output is handled by Apache PDFBox, an open-source tool. Word and Excel input/output is handled by Apache POI.

The GUI uses the JavaFX toolkit.