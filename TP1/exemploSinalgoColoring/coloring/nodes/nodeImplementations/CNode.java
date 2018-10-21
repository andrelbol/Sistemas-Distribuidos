package projects.coloring.nodes.nodeImplementations;

import java.awt.Color;
import java.awt.Graphics;
import java.util.*;
// import sinalgo.configuration.WrongConfigurationException;
import sinalgo.gui.transformation.PositionTransformation;
import sinalgo.nodes.Node;
import sinalgo.nodes.edges.Edge;
import sinalgo.nodes.messages.Inbox;
import projects.coloring.nodes.timers.*;
import projects.coloring.nodes.messages.*;
import sinalgo.nodes.messages.Message;
/* Class ssdata given to store neighbor state (its sscolor).
 * Neighbor states are stored in a hashing table.
 */

class ssdata {
  int sscolor;

  ssdata(int sscolor) {
    this.sscolor = sscolor;
  }
}

/* Class Cnode implements a node. */
public class CNode extends Node {
  private int sscolor; // Node color
  /* Nb contains the total number of colors. Tab contains color codes. */
  private final int nb = 10;
  private final Color tab[] = { Color.BLUE, Color.CYAN, Color.GREEN, Color.LIGHT_GRAY, Color.MAGENTA, Color.ORANGE,
      Color.PINK, Color.RED, Color.WHITE, Color.YELLOW };
  /* Hashing table "neighstate" contains the last known state
   *of each neighbor.
  */
  private Hashtable<Integer, ssdata> neighstate;

  public int getSSColor() {
    return sscolor;
  }

  public Color RGBColor() {
    return tab[getSSColor()];
  }

  public void setSSColor(int c) {
    this.sscolor = c;
  }

  /* Function initSSColor chooses a new color randomly */
  public void initSSColor(int range) {
    setSSColor((int) (Math.random() * range) % range);
  }

  /* Function Compute est executed upon each message receipt. If necessary,
  it updates the color. */
  public void compute() {
    boolean same = false;
    Iterator<Edge> it = this.getOutgoingConnections().iterator();
    boolean SC[] = new boolean[nb];
    for (int i = 0; i < SC.length; i++)
      SC[i] = false;
    while (it.hasNext()) {
      Edge e = it.next();
      ssdata tmp = neighstate.get(new Integer((int)   e.getEndNode().getID()));
      if (tmp != null) {
        if (tmp.sscolor == this.getSSColor()) {
          same = true;
        }
        SC[tmp.sscolor] = true;
      }
    }
    if (same) {
      int available = 0;
      for (int i = 0; i < SC.length; i++)
        if (SC[i] == false)
          available++;
      if (available == 0)
        return;
      int choice = ((int) (Math.random() * 10000)) % available + 1;
      int i = 0;
      while (choice > 0) {
        if (SC[i] == false)
          choice--;
        if (choice > 0)
          i++;
      }
      this.setSSColor(i);
    }
  }

  /* Function handleMessages is executed upon each message receipt. */
  public void handleMessages(Inbox inbox) {
    if (inbox.hasNext() == false)
      return;
    while (inbox.hasNext()) {
      Message msg = inbox.next();
      if (msg instanceof CMessage) {
        /* Each message contains the state of a neighbor.
         * The hashing table is updated accordingly.
         */
        ssdata tmp = new ssdata(((CMessage) msg).sscolor);
        neighstate.put(new Integer((int) ((CMessage) msg).id), tmp);
        compute();
      }
    }
  }

  public void preStep() {
  }

  /* Function init is called only once. The color and the timer is
  initialized.
   * Tha hashing table is created. */
  public void init() {
    initSSColor(nb);
    (new CTimer(this, 50)).startRelative(50, this);
    this.neighstate = new Hashtable<Integer, ssdata>(this.getOutgoingConnections().size());
  }

  public void neighborhoodChange() {
  }

  public void postStep() {
  }

  public String toString() {
    String s = "Node(" + this.getID() + ") [";
    Iterator<Edge> edgeIter = this.getOutgoingConnections().iterator();
    while (edgeIter.hasNext()) {
      Edge e = edgeIter.next();
      Node n = e.getEndNode();
      s += n.getID() + " ";
    }
    return s + "]";
  }

  public void checkRequirements() {}

  /* Function draw is used to display the node. */
  public void draw(Graphics g, PositionTransformation pt, boolean highlight) {
    Color c;
    this.setColor(this.RGBColor());
    String text = "" + this.getID();
    c = Color.BLACK;
    super.drawNodeAsDiskWithText(g, pt, highlight, text, 20, c);
  }
}