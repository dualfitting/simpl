/* Generated By:JJTree: Do not edit this line. ASTWhileLoopExpression.java */

package javacc;

import utils.InterpretException;
import utils.TypeException;

public class ASTWhileLoopExpression extends SimpleNode {
  public ASTWhileLoopExpression(int id) {
    super(id);
  }

  public ASTWhileLoopExpression(SimPLParser p, int id) {
    super(p, id);
  }

  public static Node jjtCreate(int id) {
      return new ASTWhileLoopExpression(id);
  }

  public static Node jjtCreate(SimPLParser p, int id) {
      return new ASTWhileLoopExpression(p, id);
  }

  /** Accept the visitor. 
 * @throws TypeException 
 * @throws InterpretException **/
  public Object jjtAccept(SimPLParserVisitor visitor, Object data) throws TypeException, InterpretException {
    return visitor.visit(this, data);
  }
}
