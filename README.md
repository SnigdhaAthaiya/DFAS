# Data Flow Analysis for Asynchronous Systems (DFAS)

This is a prototype implementation of the intra-procedural data flow analysis algorithms for asynchronous message-passing systems proposed as part of my PhD.  Asynchronous message-passing systems are employed frequently to implement distributed mechanisms, protocols, and processes. My work addressed the problem of precise data flow analysis for such systems. To obtain good precision, data flow analysis of message-passing systems needs to somehow skip execution paths that read more messages than the number of messages sent so far in the path, as such paths are infeasible at run time. Current data flow analysis techniques for such systems either compromise on precision by traversing infeasible paths in addition to feasible paths, or traverse only feasible paths but admit only finite abstract analysis domains. 
 
 We propose two approaches, which elide a large class of infeasible paths, and generalize upon the state of art by admitting infinite abstract analysis domains. Our first approach, ForwardDFAS, is a conservative algorithm that admits any infinite abstract data domain. The second approach, BackwardDFAS is a precise algorithm and admits a more restricted class of data domains. More details can be found in the [thesis](https://tinyurl.com/y8zglqpe).
 
 
 We have implemented our approaches, and have analyzed their performance on a set of 14 benchmarks and 3 abstract domains. We are making the benchmarks and their models available along with the tool source. Four of these benchmarks -- bartlett, leader, lynch, and peterson, are Promela models for the [Spin model-checker](www.imm.dtu.dk/~albl/promela.html). Three benchmarks -- boundedAsync, receive1, and replicatingStorage, are taken from the [P language repository](www.github.com/p-org), while two -- server and chameneos, are taken from the [Basset repository](www.github.com/SoftwareEngineeringToolDemos/FSE-2010-Basset), which contains actor-based examples for Java
PathFinder. Four benchmarks -- event\_bus\_test, jobqueue\_test, nursery\_test and bookCollectionStore are real world [Go programs](www.github.com/avelino/awesome-go). There is one toy example "mutualExclusionUnbounded", for ensuring mutual exclusion, via blocking receive messages that we have made ourselves. Some of the benchmarks use procedures, but none of them use recursion, so we have manually inlined the procedure calls. 



 
 
 ## Input to the Tool
 Our DFAS implementations expect the asynchronous system to be specified as an XML file. We have developed a custom XML schema for this, closely based on the Promela modelling language used in [Spin model-checker](www.imm.dtu.dk/~albl/promela.html). The schema *SPECForward.dtd* is available in the *dat*  folder. The XML format allowed us to evaluate our approach on examples from different languages. 
 
 We have manually translated each benchmark into an XML file, which we call a **model**. Details about the modelling decisions are present in the thesis. The folder *benchmark-sources* contains the original source code of the XML models. Users can compare the code of Promela models like *bartlett.pml* to the corresponding model *bartlett.xml* to understand the modelling language better.
 
 Each variable reference in any statement is called a "use". For instance, in statement `x = x+1`, there is just one use, of variable x.  In all our experiments, the objective is to find the uses that are definitely constants. This is a common objective in many research papers, as finding constants enables optimizations such as constant folding, and also checking assertions in the code. 
 
An assertion in the model is declared using the \<ASSERT\> tag. For instance, the element declared using the \<ASSERT\> tag in lines 169-181 in *mutualExclusionUnbounded.xml* model is an assertion with the expression `(NCRITICAL == 1)`.
 

 ## Downloading and Building the Tool
 
 The tool is a Maven Project. It can be built using the command-line or can be imported as an Eclipse project, and then built using the Maven tool in Eclipse. For both the methods, [Maven](https://maven.apache.org/install.html) should be installed on your system. To begin with, clone the tool or download the source. We will call the directory *DFAS/AsyncDataAnalyzer/AsyncDataAnayzer* as the root directory.
 
 ### Building the Tool Using Command-Line
 
 To build the tool using command-line, go to folder `DFAS -> AsyncDataAnalyzer -> AsyncDataAnalyzer` and run `mvn clean install`. This will download all the necessary libraries automatically and create a folder *target* in the root directory.
 
 ### Building the Tool Using Eclipse
 
 Import the tool in Eclipse using `Right-Click -> Import -> Maven -> Existing Maven Projects` and selecting the downloaded *DFAS* as the root directory. Eclipse will detect the project and the *pom.xml* file. Click on **Finish**. Then, right-click on the *pom.xml* file, select `Run As -> Maven build`. A new dialog box will open for a new run configuration. Enter **clean install** in the Goals field and click on **Run**. Maven will download the dependencies and create the *target* folder in the root directory.
 
  
 ### *target* Folder Structure
 
 Besides generated sources and other Maven artefacts, the *target* folder has four important items. The *models* directory, the *dat* directory, the runnable jar file `DFAS-1.0.RELEASE-jar-with-dependencies.jar`, and the *log4j.properties* file. The *models* directory contains all the models and is a copy of the *models* folder present in the root folder. The *dat* folder contains the DTD file *SPECForward.dtd*. The Jar file is the tool jar and can be used to analyze the models. *log4j.properties* file is required by the tool jar file for logging. 
 
 ## Running the Analyses
 
 There are two kinds of analysis that the tool supports - ForwardDFAS and BackwardDFAS. Therefore, there are two kinds of run configurations possible in the tool. To run ForwardDFAS, following parameters are required :
 * -forward :  this tells the tool that you want to run ForwardDFAS
 * -inputFileName `path to model file`:  provides the path to the model file to be analyzed. The tool has been tested with relative paths. This is the reason why the *models* and *dat* folder are provided along with the jar file in the *target* folder.
 * -latticeType `T` :  for forwardDFAS, the value of T can only be "cp", for constant propagation analysis.
 * -cutoff `n` : this option takes a number `n` as input. As forwardDFAS is a conservative analysis, this parameter specifies the maximum number of instances of each kind of message in the system that is tracked precisely. Once the number of instances of messages in the channel goes above `n` then the analysis assumes that the channel contains infinite instances of that type of message.
 
 For running backwardDFAS, you need to provide the following parameters :
 * -backward : this tells the tool that you want to run backwardDFAS
 * -inputFileName `path to model file`:  provides the path to the model file to be analyzed. The tool has been tested with relative paths. This is the reason why the *models* and *dat* folder are provided along with the jar file in the *target* folder.
 * -latticeType `T` : the possible values for T are "lcp" for linear constant propagation, "ara" for affine relations analysis, and "ccp" for copy constant propagation.
 * -mode `M` : the possible values for M are "demand" for precise analysis and "naive" for the analysis that ignores all channel operations.
 
 These arguments can be provided for the Eclipse runs as it is by modifying the run configuration associated with the `iisc.edu.pll.Main` class. Many models are large and require significant memory for running. Therefore, in case of out of memory errors, allocate more RAM to the tool while running. For instance, the model *chameneos.xml* runs out of 100GB of memory for cutoff value 2. Most of the models except *chameneos.xml* and *lynch.xml* should terminate within seconds and will not use more than 8GB of memory.
 
 There are two things to keep in mind while running the analysis. First, remove the *output/result.out* and *output/debug.out* files before running any analysis. The logger is configured to append to these files currently and not deleting these files will keep accumulating results due to different runs. Secondly, check the paths in the log4j.properties file according to the system you are using, more precisely the path separators.
 
 
 ## Understanding the Output
 The tool produces two files as output - *output/result.out* and *output/debug.out*. The main output of the tool is present in the *output/result.out* file. The *debug.out* file only prints some debugging statistics about the runs. The *result.out* file looks a little different for forwardDFAS and backwardDFAS. 
 
 ### Output of ForwardDFAS
 To explain the output of the forward analysis we take the output of the run configuration
 
 `-forward -inputFileName "models/mutualExlcusionUnbounded.xml" -latticeType "cp" -cutoff "2"`.
 
 The important features of the output file *result.out* are as follows : 

* The total time taken by the analysis is reported in the last (in milli seconds) and second from the last (rounded to seconds) line of the output file. See lines 53 and 55 in the file result.out.
	
* The total number of constants identified is reported in the third from the last line in the file. In result.out for CP, line 51 reports the total number of constants, i.e., 6.
	
* The file also reports the results for individual uses using LABEL and VARUSE information. For instance, for Label L1 that uses variable NCRITICAL, the result is shown in lines 11-13. The file first displays the "use" information  - label and variable, besides some other information. Then it displays the data flow fact computed for the use. In case of result.out, it is NCRITICAL $= 0$. If a variable is constant (like in this example), then the output file also contains the line "... is constant at use ..." (see line 7 in result.out) before displaying the result for the use.
	
	If the variable is not a constant, the data flow fact displayed is  "NCRITICAL = top", where top represents the non-constant value. 	
	
	In order to check assertions, one has to manually identify the variables that occur in the assertion, then look up the output file to see whether those variables have constant values at the location of the assertion, and then manually infer whether the assertion verifies or not.

* The file reports the value of the cutoff in the second line (See line 3 in result.out). 

 
 ### Output of BackwardDFAS
 To explain the output of the backward analysis we take the output of the run configuration
 
 `-backward -inputFileName "models/mutualExlcusionUnbounded.xml" -latticeType "lcp" -mode "demand"`.
 
 The important features of the output file *result.out* are as follows : 

* The total time taken by the analysis is reported in seconds in the last line of the output file. See line 65 in the file result.out.

* The total number of constants identified by the approach is reported in the second from the last line in the file. In result.out for LCP, line 63 reports the total number of constants, i.e., 6.

* The *result.out* output file also reports the total number of uses in the third from the last line. Line 61 reports the total number of "uses" for *mutualExclusionUnbounded.xml*.

* The file also reports the results for individual uses using LABEL and VARUSE information. For instance, for Label L1 that uses variable NCRITICAL, the LCP result is shown in lines 8-10 (in IDE format). The file first displays the "use" information  - label and variable, besides some other information. Then it displays the data flow fact computed for the use. In case of result.out, it is 
	
	`_lambda -->NCRITICAL : LCPRep [a=0, b=0, c=top]`	
	
	which can be interpreted as NCRITICAL== 0. The general interpretation of the data flow fact `V1 -->V2 : LCPRep [a=a1, b=b1, c=c1]`, where V1 and V2 are variables and a1 and b1 are integers, and c can be an integer, "bot" (representing non-constant value), or "top"  (representing unreachable value), is 
	
	`V2 = (a1 * V1 + b1) \meet (c1)`  
	
	If c1 is bot, then the variable is a non-constant. If V1 is \_lambda and c1 is top then the V2 is a constant and the value is equal to b1. If a variable is constant (like in this example), then the output file also contains the line "for node ... variable ... is constant" (see line 13 in result.out) after displaying the result for the use.
	
	For an example of a non-constant case, the reader is encouraged to analyze *server.xml* in "demand" mode and refer to lines 11-14 in the corresponding result.out file.
	
	Assertion checking is done as described in the context of Forward DFAS.
	
 
 
 
 In case of any queries, suggestions, or bugs, feel free to email me at snigdha@iisc.ac.in.
