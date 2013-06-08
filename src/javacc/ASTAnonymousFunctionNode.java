/* Generated By:JJTree: Do not edit this line. ASTAnonymousFunctionNode.java */

package javacc;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import utils.InterpretException;
import utils.TypeException;

public class ASTAnonymousFunctionNode extends SimpleNode {

	private ASTVariable identifier;
	private SimpleNode anonymousFunction;
	private HashMap<String, SimpleNode> parentEnv;

	public HashMap<String, SimpleNode> getParentEnv() {
		return parentEnv;
	}

	public void setParentEnv(HashMap<String, SimpleNode> parentEnv) {
		this.parentEnv = parentEnv;
	}

	public ASTVariable getIdentifier() {
		return identifier;
	}

	public void setIdentifier(ASTVariable identifier) {
		this.identifier = identifier;
	}

	public SimpleNode getAnonymousFunction() {
		return anonymousFunction;
	}

	public void setAnonymousFunction(SimpleNode anonymousFunction) {
		this.anonymousFunction = anonymousFunction;
	}

	public ASTAnonymousFunctionNode(int id) {
		super(id);
	}

	public ASTAnonymousFunctionNode(SimPLParser p, int id) {
		super(p, id);
	}

	public static Node jjtCreate(int id) {
		return new ASTAnonymousFunctionNode(id);
	}

	public static Node jjtCreate(SimPLParser p, int id) {
		return new ASTAnonymousFunctionNode(p, id);
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
		return "fun " + this.getIdentifier() + " -> "
				+ this.jjtGetChild(1).toString();
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof ASTAnonymousFunctionNode)) {
			return false;
		}

		ASTAnonymousFunctionNode otherNode = (ASTAnonymousFunctionNode) obj;

		return this.jjtGetChild(1).equals(otherNode.jjtGetChild(1))
				&& this.getIdentifier().equals(otherNode.getIdentifier());
	}
	
	public ASTAnonymousFunctionNode deepCopy()
	{
		ASTAnonymousFunctionNode copy = new ASTAnonymousFunctionNode(0);
		copy.setIdentifier(this.getIdentifier());
		copy.setAnonymousFunction(this.getAnonymousFunction());
		for(int i = 0; i < this.jjtGetNumChildren(); ++i)
			copy.jjtAddChild(this.jjtGetChild(i), i);
		if(this.getParentEnv() != null)
		{
			HashMap<String, SimpleNode> copiedEnv = new HashMap<String, SimpleNode>();
			Iterator<Entry<String, SimpleNode>> iter =
					this.getParentEnv().entrySet().iterator();
			while(iter.hasNext())
			{
				Entry<String, SimpleNode> symtabItem = iter.next();
				if(symtabItem.getValue() instanceof ASTValue)
				{
					copiedEnv.put(symtabItem.getKey(), 
							((ASTValue)symtabItem.getValue()).deepCopy());
				}
				else
				{
					copiedEnv.put(symtabItem.getKey(), 
							((ASTAnonymousFunctionNode)symtabItem.getValue()).deepCopy());
				}
				
			}
			
			copy.setParentEnv(copiedEnv);
		}
		return copy;
	}
}
