/* Generated By:JJTree: Do not edit this line. ASTUnaryNegativeNode.java */

package javacc;

import utils.InterpretException;
import utils.TypeException;

public class ASTUnaryNegativeNode extends SimpleNode {
  public ASTUnaryNegativeNode(int id) {
    super(id);
  }

  public ASTUnaryNegativeNode(SimPLParser p, int id) {
    super(p, id);
  }

  public static Node jjtCreate(int id) {
      return new ASTUnaryNegativeNode(id);
  }

  public static Node jjtCreate(SimPLParser p, int id) {
      return new ASTUnaryNegativeNode(p, id);
  }

  /** Accept the visitor. 
 * @throws TypeException 
 * @throws InterpretException **/
  public Object jjtAccept(SimPLParserVisitor visitor, Object data) throws TypeException, InterpretException {
    return visitor.visit(this, data);
  }
}
