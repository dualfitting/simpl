/* Generated By:JJTree: Do not edit this line. ASTPairExpression.java */

package javacc;

import utils.InterpretException;
import utils.TypeException;

public class ASTPairExpression extends SimpleNode {
  public ASTPairExpression(int id) {
    super(id);
  }

  public ASTPairExpression(SimPLParser p, int id) {
    super(p, id);
  }

  public static Node jjtCreate(int id) {
      return new ASTPairExpression(id);
  }

  public static Node jjtCreate(SimPLParser p, int id) {
      return new ASTPairExpression(p, id);
  }

  /** Accept the visitor. 
 * @throws TypeException 
 * @throws InterpretException **/
  public Object jjtAccept(SimPLParserVisitor visitor, Object data) throws TypeException, InterpretException {
    return visitor.visit(this, data);
  }
}
