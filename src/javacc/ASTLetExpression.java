/* Generated By:JJTree: Do not edit this line. ASTLetExpression.java */

package javacc;

import java.util.HashMap;

import utils.InterpretException;
import utils.TypeException;

public class ASTLetExpression extends SimpleNode {

	HashMap<String, SimpleNode> parentEnv;

	public HashMap<String, SimpleNode> getParentEnv() {
		return parentEnv;
	}

	public void setParentEnv(HashMap<String, SimpleNode> parentEnv) {
		this.parentEnv = parentEnv;
	}

	public ASTLetExpression(int id) {
		super(id);
	}

	public ASTLetExpression(SimPLParser p, int id) {
		super(p, id);
	}

	public static Node jjtCreate(int id) {
		return new ASTLetExpression(id);
	}

	public static Node jjtCreate(SimPLParser p, int id) {
		return new ASTLetExpression(p, id);
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
		return "let " + this.jjtGetChild(0).toString() + " = "
				+ this.jjtGetChild(1).toString() + " in "
				+ this.jjtGetChild(2).toString() + " end ";
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof ASTJoinNode)) {
			return false;
		}

		ASTLetExpression otherNode = (ASTLetExpression) obj;

		return this.jjtGetChild(0).equals(otherNode.jjtGetChild(0))
				&& this.jjtGetChild(1).equals(otherNode.jjtGetChild(1))
				&& this.jjtGetChild(2).equals(otherNode.jjtGetChild(2));
	}
}
