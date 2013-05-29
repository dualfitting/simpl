/* Generated By:JJTree: Do not edit this line. ASTUnaryNotNode.java */

package javacc;

import utils.InterpretException;
import utils.TypeException;

public class ASTUnaryNotNode extends SimpleNode {
	public ASTUnaryNotNode(int id) {
		super(id);
	}

	public ASTUnaryNotNode(SimPLParser p, int id) {
		super(p, id);
	}

	public static Node jjtCreate(int id) {
		return new ASTUnaryNotNode(id);
	}

	public static Node jjtCreate(SimPLParser p, int id) {
		return new ASTUnaryNotNode(p, id);
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
		return "not " + this.jjtGetChild(0).toString();
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof ASTUnaryNotNode)) {
			return false;
		}

		ASTUnaryNotNode otherNode = (ASTUnaryNotNode) obj;

		return this.jjtGetChild(0).equals(otherNode.jjtGetChild(0));
	}
}
