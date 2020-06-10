package chameneos;

import osl.manager.Actor;
import osl.manager.ActorName;
import osl.manager.RemoteCodeException;
import osl.manager.annotations.message;

public class Chameneos extends Actor {

  private static final long serialVersionUID = 5021339002556170556L;

  public enum Colour {
    blue, red, yellow
  }

  private static Colour doCompliment(Colour c1, Colour c2) {
    switch (c1) {
    case blue:
      switch (c2) {
      case blue:
        return Colour.blue;
      case red:
        return Colour.yellow;
      case yellow:
        return Colour.red;
      }
    case red:
      switch (c2) {
      case blue:
        return Colour.yellow;
      case red:
        return Colour.red;
      case yellow:
        return Colour.blue;
      }
    case yellow:
      switch (c2) {
      case blue:
        return Colour.red;
      case red:
        return Colour.blue;
      case yellow:
        return Colour.yellow;
      }
    }

    throw new RuntimeException("Error");
  }

  int myHooks, selfHooks;
  Colour myColor;

  ActorName broker;

  public Chameneos(ActorName b, Colour c) {
    broker = b;
    myColor = c;
  }

  @message
  public void start() {
    send(broker, "hook", self(), myColor);
  }

  @message
  public void hook(ActorName other, Colour c) throws RemoteCodeException {
    myColor = doCompliment(myColor, c);

    if (self() == other)
      selfHooks++;
    myHooks++;
    this.start();
    // send(self(), "start");
  }

  @message
  public void stop() {
    // send(stdout, "println", myHooks+":"+selfHooks);
    System.out.println(myHooks + ":" + selfHooks);
  }
}
