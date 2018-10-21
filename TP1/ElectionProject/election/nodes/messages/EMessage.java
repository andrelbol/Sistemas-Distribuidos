package projects.election.nodes.messages;

import sinalgo.nodes.messages.Message;
import projects.election.enums.MessageType;

/* Description of Message Type */
public class EMessage extends Message {
  public long id;
  public MessageType type;

  public EMessage(long id, MessageType type) {
    this.id = id;
    this.type = type;
  }

  public Message clone() {
    return new EMessage(id, type);
  }

  public long getId() {
    return this.id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public MessageType getType() {
    return this.type;
  }

  public void setType(MessageType type) {
    this.type = type;
  }
}