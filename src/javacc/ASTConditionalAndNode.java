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
		return this.jjtGetChild(0).toString() + " and "
				+ this.jjtGetChild(1).toString();
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof ASTConditionalAndNode)) {
			return false;
		}

		ASTConditionalAndNode otherNode = (ASTConditionalAndNode) obj;

		return this.jjtGetChild(0).equals(otherNode.jjtGetChild(0))
				&& this.jjtGetChild(1).equals(otherNode.jjtGetChild(1));
	}
}
