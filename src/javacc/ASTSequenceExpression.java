/* Generated By:JJTree: Do not edit this line. ASTSequenceExpression.java */

package javacc;

import utils.InterpretException;
import utils.TypeException;

public class ASTSequenceExpression extends SimpleNode {
  public ASTSequenceExpression(int id) {
    super(id);
  }

  public ASTSequenceExpression(SimPLParser p, int id) {
    super(p, id);
  }

  public static Node jjtCreate(int id) {
      return new ASTSequenceExpression(id);
  }

  public static Node jjtCreate(SimPLParser p, int id) {
      return new ASTSequenceExpression(p, id);
  }

  /** Accept the visitor. 
 * @throws TypeException 
 * @throws InterpretException **/
  public Object jjtAccept(SimPLParserVisitor visitor, Object data) throws TypeException, InterpretException {
    return visitor.visit(this, data);
  }
}
