package projects.election.nodes.timers;

import projects.election.nodes.nodeImplementations.ENode;
import projects.election.nodes.messages.EMessage;
import projects.election.enums.MessageType;
import sinalgo.nodes.timers.Timer;

/* Description of Timer */
public class ETimer extends Timer {
  ENode sender;
  int interval;

  public ETimer(ENode sender, int interval) {
    this.sender = sender;
    this.interval = interval;
  }

  /* Function fire called upon timeout */
  public void fire() {
    // Node creates a message carying its color (sscolor)
    EMessage msg = new EMessage(sender.getID(), MessageType.ELECTION);
    // The message is sent to each neibor
    sender.broadcast(msg);
    // Timer reset
    this.startRelative(interval, sender);
  }
}