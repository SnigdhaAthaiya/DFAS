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
import osl.manager.ActorName;
import osl.manager.RemoteCodeException;
import osl.manager.annotations.message;

/**
 * This class is is the client for a simple client server actor example based on
 * a ScalaWiki example that contained an atomicity violation.
 * 
 * @author Mirco Dotta (mirco.dotta@gmail.com)
 * @author Steven Lauterburg (steven.lauterburg@gmail.com)
 * 
 */
public class CSClient extends Actor {

  private ActorName server;
  private int v1;
  private int v2;

  // Constructor
  public CSClient(ActorName s) {
    this.server = s;
  }

  @message
  public boolean start() throws RemoteCodeException {
    send(server, "set", 5);

    v1 = (Integer) call(server, "get");
    System.out.println("value v1 = " + v1);

    v2 = (Integer) call(server, "get");
    System.out.println("value v2 = " + v2);

    //assert(v1 == v2);

    send(server, "shutdown");

    return (v1 == v2);
  }

}
