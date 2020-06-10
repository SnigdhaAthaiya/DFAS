package chameneos;

import osl.manager.Actor;
import osl.manager.ActorName;
import osl.manager.RemoteCodeException;
import osl.manager.annotations.message;

public class Broker extends Actor {

  private static final long serialVersionUID = 5021339002556170557L;

  public static final Chameneos.Colour[][] groups = new Chameneos.Colour[][] {
      { Chameneos.Colour.blue, Chameneos.Colour.red, Chameneos.Colour.yellow },
      { Chameneos.Colour.blue, Chameneos.Colour.red, Chameneos.Colour.yellow,
          Chameneos.Colour.red, Chameneos.Colour.yellow, Chameneos.Colour.blue,
          Chameneos.Colour.red, Chameneos.Colour.yellow, Chameneos.Colour.red,
          Chameneos.Colour.blue } };

  int totalRendezvous;
  ActorName firstHooker;
  Chameneos.Colour firstColor;

  @message
  public void boot(Integer total) throws RemoteCodeException {
    totalRendezvous = total;
    ActorName[] actors = new ActorName[groups[0].length];

    for (int i = 0; i < groups[0].length; i++) {
      actors[i] = create(Chameneos.class, self(), groups[0][i]);
      send(actors[i], "start");
    }
  }

  @message
  public void hook(ActorName other, Chameneos.Colour c)
      throws RemoteCodeException {
    if (totalRendezvous == 0) {
      send(other, "stop");
      return;
    }

    if (firstHooker == null) {
      firstHooker = other;
      firstColor = c;
    } else {
      send(firstHooker, "hook", other, c);
      send(other, "hook", firstHooker, firstColor);
      firstHooker = null;
      totalRendezvous--;
    }
  }
}
