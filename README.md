# Data Flow Analysis for Asynchronous Systems (DFAS)

This is a prototype implementation of the intra-procedural data flow analysis algorithms for asynchronous message-passing systems proposed as part of my PhD.  Asynchronous message-passing systems are employed frequently to implement distributed mechanisms, protocols, and processes. My work addressed the problem of precise data flow analysis for such systems. To obtain good precision, data flow analysis of message-passing systems needs to somehow skip  execution paths that read more messages than the number of messages sent so far in the path, as such
 paths are infeasible at run time. Current data flow analysis techniques for such systems either compromise on precision by traversing infeasible paths in addition to feasible paths, or traverse only feasible paths but admit only finite abstract analysis domains. 
 
 We propose two approaches, which elide a large class of infeasible paths, and generalize upon the state of art by admitting infinite abstract analysis domains. Our first approach, ForwardDFAS, is a conservative algorithm that admits any infinite abstract data domain. The second approach, BackwardDFAS is a precise algorithm and admits a more restricted class of data domains. More details can be found in the [thesis](https://tinyurl.com/y8zglqpe).
 
 
 We have implemented our approaches, and have analyzed their performance on a set of 14 benchmarks and 3 abstract domains. We are making the benchmarks and their models available along with the tool source. Four of these benchmarks -- bartlett, leader, lynch, and peterson, are Promela models for the [Spin model-checker](www.imm.dtu.dk/~albl/promela.html). Three benchmarks -- boundedAsync, receive1, and replicatingStorage, are taken from the [P language repository](www.github.com/p-org), while two -- server and chameneos, are taken from the [Basset repository](www.github.com/SoftwareEngineeringToolDemos/FSE-2010-Basset), which contains actor-based examples for Java
PathFinder. Four benchmarks -- event\_bus\_test, jobqueue\_test, nursery\_test and bookCollectionStore are  real world [Go programs](www.github.com/avelino/awesome-go). There is one toy example "mutualExclusionUnbounded", for ensuring mutual exclusion, via blocking receive messages, that we have made ourselves. Some of the benchmarks use procedures, but none of them use recursion, so we have manually inlined the procedure calls. 

 
 
 ## Input to the Tool
 Our DFAS implementations expect the asynchronous system to be specified as an XML file. We have developed a custom XML schema for this, closely based on the Promela modeling language used in Spin~\cite{holzmann2004spin}. The schema *SPECForward.dtd* is available in the *dat*  folder. The XML format allowed us to evaluate our approach on examples from different languages. 
 
 We have manually translated each benchmark into an XML file, which we call a **model**. Details about the modeling decisions are present in the thesis. The folder *benchmark-sources* contains the original source code of the XML models. Users can compare the code of Promela models like *bartlett.pml* to the corresponding model *bartlett.xml* to understand the modeling language better.
 

 ## Downloading and Building the Tool
 
 The tool is a Maven Project. It can be built using the command-line or can be imported as an Eclipse project, and then built using the Maven tool in Eclipse. For both the methods, [Maven](https://maven.apache.org/install.html) should be installed on your system. To begin with, clone the tool or download the source. We will call the directory *DFAS\AsyncDataAnalyzer\AsyncDataAnayzer* as the root directory.
 
 ### Building the Tool Using Command-Line
 
 To build the tool using command-line, go to folder `DFAS -> AsyncDataAnalyzer -> AsyncDataAnalyzer` and run `mvn clean install`. This will download all the necessary libraries automatically and create a folder *target* in the root directory.
 
 ### Building the Tool Using Eclipse
 
 Import the tool in Eclipse using `Right-Click -> Import -> Maven -> Existing Maven Projects` and selecting the downloaded *DFAS* as the root directory. Eclipse will detect the project and the *pom.xml* file. Click on **Finish**. Then, right-click on the *pom.xml* file, select `Run As -> Maven build`. A new dialog box will open for a new run configuration. Enter **clean install** in the Goals field and click on **Run**. Maven will download the dependencies and create the *target* folder in the root directory.
 
  
 ### *target* Folder Structure
 
 Besides generated sources and other Maven artifacts, the *target* folder has four important items. The *models* directory, the *dat* directory, the runnable jar file `DFAS-1.0.RELEASE-jar-with-dependencies.jar`, and the *log4j.properties* file. The *models* directory contains all the models and is a copy of the *models* folder present in the root folder. The *dat* folder contains the DTD file *SPECForward.dtd*. The Jar file is the tool jar and can be used to analyze the models. *log4j.properties* file is required by the tool jar file for logging. 
 
 ## Running the Analyses
 
 There are two kinds of analysis that the tool supports - ForwardDFAS and BackwardDFAS. Therefore there are two kinds of run configurations possible in the tool. To run ForwardDFAS, following parameters are required :
 * -forward :  this tells the tool that you want to run ForwardDFAS
 * -inputFileName <path to model file>:  provides the path to the model file to be analyzed. The tool has been tested with relative paths. This is the reason why the *models* and *dat* folder are provided along with the jar file in the *target* folder.
 * -latticeType :  for forwardDFAS, the value of this parameter can only be "cp".
 * -cutoff <n> : this option takes a number `n` as input. As forwardDFAS is a conservative analysis, this parameter specifies the maximum number of instances of each kind of message in the system that is tracked precisely. Once the number of instances of messages in the channel goes above `n` then the analysis assumes that the channel contains infinite instances of that type of message.
 
 
 
 ## Understanding the Output
 broad output, data domain output format
 
 ## Editing the source
 
 In case of any queries, suggestions, or bugs, feel free to email me at snigdha@iisc.ac.in.
