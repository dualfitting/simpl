/* Generated By:JJTree: Do not edit this line. ASTTailOfListExpression.java */

package javacc;

import utils.InterpretException;
import utils.TypeException;

public class ASTTailOfListExpression extends SimpleNode {
	public ASTTailOfListExpression(int id) {
		super(id);
	}

	public ASTTailOfListExpression(SimPLParser p, int id) {
		super(p, id);
	}

	public static Node jjtCreate(int id) {
		return new ASTTailOfListExpression(id);
	}

	public static Node jjtCreate(SimPLParser p, int id) {
		return new ASTTailOfListExpression(p, id);
	}

	/**
	 * Accept the visitor.
	 * 
	 * @throws TypeException
	 * @throws InterpretException
	 **/
	public Object jjtAccept(SimPLParserVisitor visitor, Object data)
			throws TypeException, InterpretException {
		return visitor.visit(this, data);
	}

	public String toString() {
		return "tail " + this.jjtGetChild(0).toString();
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof ASTTailOfListExpression)) {
			return false;
		}

		ASTTailOfListExpression otherNode = (ASTTailOfListExpression) obj;

		return this.jjtGetChild(0).equals(otherNode.jjtGetChild(0));
	}
}
