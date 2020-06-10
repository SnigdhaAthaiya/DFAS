//
// Copyright (C) 2010 United States Government as represented by the
// Administrator of the National Aeronautics and Space Administration
// (NASA).  All Rights Reserved.
//
// This software is distributed under the NASA Open Source Agreement
// (NOSA), version 1.3.  The NOSA has been approved by the Open Source
// Initiative.  See the file NOSA-1.3-JPF at the top of the distribution
// directory tree for the complete NOSA document.
//
// THE SUBJECT SOFTWARE IS PROVIDED "AS IS" WITHOUT ANY WARRANTY OF ANY
// KIND, EITHER EXPRESSED, IMPLIED, OR STATUTORY, INCLUDING, BUT NOT
// LIMITED TO, ANY WARRANTY THAT THE SUBJECT SOFTWARE WILL CONFORM TO
// SPECIFICATIONS, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR
// A PARTICULAR PURPOSE, OR FREEDOM FROM INFRINGEMENT, ANY WARRANTY THAT
// THE SUBJECT SOFTWARE WILL BE ERROR FREE, OR ANY WARRANTY THAT
// DOCUMENTATION, IF PROVIDED, WILL CONFORM TO THE SUBJECT SOFTWARE.
//
package server;

import osl.manager.Actor;
import osl.manager.annotations.message;

/**
 * This class is is the server for a simple client server actor example based on
 * a ScalaWiki example that contained an atomicity violation bug.
 * 
 * @author Mirco Dotta (mirco.dotta@gmail.com)
 * @author Steven Lauterburg (steven.lauterburg@gmail.com)
 * 
 */
public class CSServer extends Actor {

  private int value;

  // Default constructor is okay

  @message
  public void set(int v) {
    value = v;
  }

  @message
  public int get() {
    return value;
  }

  @message
  public void shutdown() {
    destroy("server is finished processing");
  }

}
