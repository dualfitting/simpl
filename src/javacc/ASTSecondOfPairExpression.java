/* Generated By:JJTree: Do not edit this line. ASTSecondOfPairExpression.java */

package javacc;

import utils.InterpretException;
import utils.TypeException;

public class ASTSecondOfPairExpression extends SimpleNode {
	public ASTSecondOfPairExpression(int id) {
		super(id);
	}

	public ASTSecondOfPairExpression(SimPLParser p, int id) {
		super(p, id);
	}

	public static Node jjtCreate(int id) {
		return new ASTSecondOfPairExpression(id);
	}

	public static Node jjtCreate(SimPLParser p, int id) {
		return new ASTSecondOfPairExpression(p, id);
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
		return "snd " + this.jjtGetChild(0).toString();
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof ASTSecondOfPairExpression)) {
			return false;
		}

		ASTSecondOfPairExpression otherNode = (ASTSecondOfPairExpression) obj;

		return this.jjtGetChild(0).equals(otherNode.jjtGetChild(0));
	}
}
