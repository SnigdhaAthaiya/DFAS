# Data Flow Analysis for Asynchronous Systems (DFAS)

This is a prototype implementation of the data flow analysis algorithms for asynchronous message-passing systems proposed as part of my PhD.  Asynchronous message-passing systems are employed frequently to implement distributed mechanisms, protocols, and processes. My work addressed the problem of precise data flow analysis for such systems. To obtain good precision, data flow analysis of message-passing systems needs to somehow skip  execution paths that read more messages than the number of messages sent so far in the path, as such
 paths are infeasible at run time. Current data flow analysis techniques for such systems either compromise on precision by traversing infeasible paths in addition to feasible paths, or traverse only feasible paths but admit only finite abstract analysis domains. 
 
 We propose two approaches, which elide a large class of infeasible paths, and generalize upon the state of art by admitting infinite abstract analysis domains. Our first approach, ForwardDFAS, is a conservative algorithm that admits any infinite abstract data domain. The second approach, BackwardDFAS is a precise algorithm and admits a more restricted class of data domains. More details can be found in the [thesis](https://tinyurl.com/y8zglqpe).
 
 
 We have implemented our approaches with different lattices, and have analyzed their performance on a set of 14 benchmarks. We are making the benchmarks and their models avalaible along with the tool source.
 
