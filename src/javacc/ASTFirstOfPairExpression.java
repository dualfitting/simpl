/* Generated By:JJTree: Do not edit this line. ASTFirstOfPairExpression.java */

package javacc;

import utils.InterpretException;
import utils.TypeException;

public class ASTFirstOfPairExpression extends SimpleNode {
	public ASTFirstOfPairExpression(int id) {
		super(id);
	}

	public ASTFirstOfPairExpression(SimPLParser p, int id) {
		super(p, id);
	}

	public static Node jjtCreate(int id) {
		return new ASTFirstOfPairExpression(id);
	}

	public static Node jjtCreate(SimPLParser p, int id) {
		return new ASTFirstOfPairExpression(p, id);
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
		return "fst " + this.jjtGetChild(0).toString();
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof ASTFirstOfPairExpression)) {
			return false;
		}

		ASTFirstOfPairExpression otherNode = (ASTFirstOfPairExpression) obj;

		return this.jjtGetChild(0).equals(otherNode.jjtGetChild(0));
	}
}
