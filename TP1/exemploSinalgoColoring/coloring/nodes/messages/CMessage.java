package projects.coloring.nodes.messages;

import sinalgo.nodes.messages.Message;

/* Description of Message Type */
public class CMessage extends Message {
  public long id;
  public int sscolor;

  public CMessage(long id, int sscolor) {
    this.id = id;
    this.sscolor = sscolor;
  }

  public Message clone() {
    return new CMessage(id, sscolor);
  }
}