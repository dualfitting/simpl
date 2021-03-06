/* Generated By:JJTree: Do not edit this line. ASTConditionalOrNode.java */

package javacc;

import utils.InterpretException;
import utils.TypeException;

public class ASTConditionalOrNode extends SimpleNode {
	public ASTConditionalOrNode(int id) {
		super(id);
	}

	public ASTConditionalOrNode(SimPLParser p, int id) {
		super(p, id);
	}

	public static Node jjtCreate(int id) {
		return new ASTConditionalOrNode(id);
	}

	public static Node jjtCreate(SimPLParser p, int id) {
		return new ASTConditionalOrNode(p, id);
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
		return this.jjtGetChild(0).toString() + " or "
				+ this.jjtGetChild(1).toString();
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof ASTConditionalOrNode)) {
			return false;
		}

		ASTConditionalOrNode otherNode = (ASTConditionalOrNode) obj;

		return this.jjtGetChild(0).equals(otherNode.jjtGetChild(0))
				&& this.jjtGetChild(1).equals(otherNode.jjtGetChild(1));
	}
}
