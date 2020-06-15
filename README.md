# Data Flow Analysis for Asynchronous Systems (DFAS)

This is a prototype implementation of the intra-procedural data flow analysis algorithms for asynchronous message-passing systems proposed as part of my PhD.  Asynchronous message-passing systems are employed frequently to implement distributed mechanisms, protocols, and processes. My work addressed the problem of precise data flow analysis for such systems. To obtain good precision, data flow analysis of message-passing systems needs to somehow skip  execution paths that read more messages than the number of messages sent so far in the path, as such
 paths are infeasible at run time. Current data flow analysis techniques for such systems either compromise on precision by traversing infeasible paths in addition to feasible paths, or traverse only feasible paths but admit only finite abstract analysis domains. 
 
 We propose two approaches, which elide a large class of infeasible paths, and generalize upon the state of art by admitting infinite abstract analysis domains. Our first approach, ForwardDFAS, is a conservative algorithm that admits any infinite abstract data domain. The second approach, BackwardDFAS is a precise algorithm and admits a more restricted class of data domains. More details can be found in the [thesis](https://tinyurl.com/y8zglqpe).
 
 
 We have implemented our approaches, and have analyzed their performance on a set of 14 benchmarks and 3 abstract domains. We are making the benchmarks and their models available along with the tool source. Four of these benchmarks -- bartlett, leader, lynch, and peterson, are Promela models for the [Spin model-checker](www.imm.dtu.dk/~albl/promela.html). Three benchmarks -- boundedAsync, receive1, and replicatingStorage, are taken from the [P language repository](www.github.com/p-org), while two -- server and chameneos, are taken from the [Basset repository](www.github.com/SoftwareEngineeringToolDemos/FSE-2010-Basset), which contains actor-based examples for Java
PathFinder. Four benchmarks -- event\_bus\_test, jobqueue\_test, nursery\_test and bookCollectionStore are  real world [Go programs](www.github.com/avelino/awesome-go). There is one toy example "mutualExclusionUnbounded", for ensuring mutual exclusion, via blocking receive messages, that we have made ourselves. Some of the benchmarks use procedures, but none of them use recursion, so we have manually inlined the procedure calls. 

 
 
 ## Input to the Tool
 Our DFAS implementations expect the asynchronous system to be specified as an XML file. We have developed a custom XML schema for this, closely based on the Promela modeling language used in Spin~\cite{holzmann2004spin}. The schema *SPECForward.dtd* is available in the *dat*  folder. The XML format allowed us to evaluate our approach on examples from different languages. 
 
 We have manually translated each benchmark into an XML file, which we call a **model**. Details about the modeling decisions are present in the thesis.
 

 ## Downloading and Building the Tool
 
 The tool is a Maven Project. It can be built using the command-line or can be imported as an Eclipse project, and them built using the Maven tool in Eclipse. For both the methods, Maven should be installed on your system. 
 
 ### Building the Tool Using Command-Line
 
 To build the tool using command-line, you for
 ## Running the Analyses
 
 
 
 ## Understanding the Output
 broad output, data domain output format
 
 ## Editing the source
 
 In case of any queries, suggestions, or bugs, feel free to email me at snigdha@iisc.ac.in.
