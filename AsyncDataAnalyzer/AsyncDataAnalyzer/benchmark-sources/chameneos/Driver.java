package chameneos;

import osl.manager.Actor;
import osl.manager.ActorName;
import osl.manager.RemoteCodeException;
import osl.manager.annotations.message;

public class Driver extends Actor {

  public static int N = 1;
  ActorName broker;

  @message
  public void setUp(String[] args) throws RemoteCodeException {
    if (args.length > 0) {
      N = Integer.parseInt(args[0]);
    }
    // create root set actors
    broker = create(Broker.class);
  }

  @message
  public void test(String[] args) {
    // queue initial messages
    send(broker, "boot", N);
  }
}
