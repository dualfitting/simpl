/* Generated By:JJTree: Do not edit this line. ASTConditionalAndNode.java */

package javacc;

import utils.InterpretException;
import utils.TypeException;

public class ASTConditionalAndNode extends SimpleNode {
  public ASTConditionalAndNode(int id) {
    super(id);
  }

  public ASTConditionalAndNode(SimPLParser p, int id) {
    super(p, id);
  }

  public static Node jjtCreate(int id) {
      return new ASTConditionalAndNode(id);
  }

  public static Node jjtCreate(SimPLParser p, int id) {
      return new ASTConditionalAndNode(p, id);
  }

  /** Accept the visitor. 
 * @throws TypeException 
 * @throws InterpretException **/
  public Object jjtAccept(SimPLParserVisitor visitor, Object data) throws TypeException, InterpretException {
    return visitor.visit(this, data);
  }
}