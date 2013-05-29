/* Generated By:JJTree: Do not edit this line. ASTBracketExpression.java */

package javacc;

import utils.InterpretException;
import utils.TypeException;

public class ASTBracketExpression extends SimpleNode {
	public ASTBracketExpression(int id) {
		super(id);
	}

	public ASTBracketExpression(SimPLParser p, int id) {
		super(p, id);
	}

	public static Node jjtCreate(int id) {
		return new ASTBracketExpression(id);
	}

	public static Node jjtCreate(SimPLParser p, int id) {
		return new ASTBracketExpression(p, id);
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
		return ")";
	}

	public boolean equals(Object obj) {
		return true;
	}
}
