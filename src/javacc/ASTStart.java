/* Generated By:JJTree: Do not edit this line. ASTStart.java */

package javacc;

import utils.InterpretException;
import utils.TypeException;

public class ASTStart extends SimpleNode {
  public ASTStart(int id) {
    super(id);
  }

  public ASTStart(SimPLParser p, int id) {
    super(p, id);
  }

  public static Node jjtCreate(int id) {
      return new ASTStart(id);
  }

  public static Node jjtCreate(SimPLParser p, int id) {
      return new ASTStart(p, id);
  }

  /** Accept the visitor. 
 * @throws TypeException 
 * @throws InterpretException **/
  public Object jjtAccept(SimPLParserVisitor visitor, Object data) throws TypeException, InterpretException {
    return visitor.visit(this, data);
  }
}