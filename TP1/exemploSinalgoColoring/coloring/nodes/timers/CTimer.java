package projects.coloring.nodes.timers;

import projects.coloring.nodes.nodeImplementations.CNode;
import projects.coloring.nodes.messages.*;
import sinalgo.nodes.timers.Timer;

/* Description of Timer */
public class CTimer extends Timer {
  CNode sender;
  int interval;

  public CTimer(CNode sender, int interval) {
    this.sender = sender;
    this.interval = interval;
  }

  /* Function fire called upon timeout */
  public void fire() {
    // Node creates a message carying its color (sscolor)
    CMessage msg = new CMessage(sender.getID(), sender.getSSColor());
    // The message is sent to each neibor
    sender.broadcast(msg);
    // Timer reset
    this.startRelative(interval, sender);
  }
}