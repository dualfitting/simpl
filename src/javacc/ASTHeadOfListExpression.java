/* Generated By:JJTree: Do not edit this line. ASTHeadOfListExpression.java */

package javacc;

import utils.InterpretException;
import utils.TypeException;

public class ASTHeadOfListExpression extends SimpleNode {
	public ASTHeadOfListExpression(int id) {
		super(id);
	}

	public ASTHeadOfListExpression(SimPLParser p, int id) {
		super(p, id);
	}

	public static Node jjtCreate(int id) {
		return new ASTHeadOfListExpression(id);
	}

	public static Node jjtCreate(SimPLParser p, int id) {
		return new ASTHeadOfListExpression(p, id);
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
		return "head " + this.jjtGetChild(0).toString();
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof ASTFirstOfPairExpression)) {
			return false;
		}

		ASTHeadOfListExpression otherNode = (ASTHeadOfListExpression) obj;

		return this.jjtGetChild(0).equals(otherNode.jjtGetChild(0));
	}
}
