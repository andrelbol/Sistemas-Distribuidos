package projects.election.nodes.nodeImplementations;

import java.awt.Graphics;
import java.util.*;
import sinalgo.gui.transformation.PositionTransformation;
import sinalgo.nodes.Node;
import sinalgo.nodes.messages.Inbox;
import projects.election.nodes.timers.ETimer;
import projects.election.nodes.messages.EMessage;
import projects.election.enums.MessageType;
import sinalgo.nodes.messages.Message;

public class ENode extends Node {

  private Node successor;
  private long elected;
  private boolean isParticipant;

  public ENode() {
    super();
    this.elected = this.getID();
    this.isParticipant = false;

    // TODO(@andre): Encontrar uma forma de definir o sucessor.
  }

  public void compute() {
  }

  public void handleMessages(Inbox inbox) {
    while(inbox.hasNext()) {
      Message msg = inbox.next();
      if(msg instanceof EMessage){
        switch(((EMessage) msg).getType()){
          case ELECTION: 
            treatElectionMessage((EMessage) msg);
            break;
          case OK:
            break;
  
          case COORDENATOR:
            this.isParticipant = false;
            this.elected = ((EMessage) msg).getId();
            break;
  
          default:
        }
      }
    }
  }

  public void preStep() {
  }

  public void init() {
  }

  public void neighborhoodChange() {
  }

  public void postStep() {
  }

  public String toString() {
    return null;
  }

  public void checkRequirements() {
  }

  /**
   * Regras:
   *  - Msgid >Pi => repassa a mensagem
   *  - Msgid < Pi e Pi não é participante, substitui o id na mensagem e repassa, marca como participante.
   *  - Pi participante => não reenvia
   *  - Msgid = Pi => Pi tem o maior id, marca como não participante e envia mensagem coordenador(pi) par todo mundo.

   */
  public void treatElectionMessage(EMessage msg) {
    if (msg.getId() > this.getID()) {
      send(msg, successor);
    } else if (msg.getId() < this.getID()) {
      if (!this.isParticipant) {
        this.isParticipant = true;
        msg.setId(this.getID());
        send(msg, successor);
      }
    } else { // Id igual
      EMessage coordMessage = new EMessage(this.getID(), MessageType.COORDENATOR);
      broadcast(coordMessage);
    }
  }
}