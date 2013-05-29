/* Generated By:JJTree: Do not edit this line. ASTValue.java */

package javacc;

import java.util.LinkedList;
import java.util.List;

import semantics.SimPLTypes;
import utils.InterpretException;
import utils.TypeException;

public class ASTValue extends SimpleNode {

	private String name;


	private int intValue;
	private boolean boolValue;
	private List<Object> listValue;
	private List<Object> pairValue;
	private int type;
	
	public ASTValue deepCopy()
	{
		return (ASTValue)copyDump(this);
	}
	
	public SimpleNode copyDump(SimpleNode v)
	{
		if(v instanceof ASTValue)
		{
			ASTValue oldCopy = (ASTValue)v;
			ASTValue newCopy = new ASTValue(oldCopy.id);
			newCopy.setType(oldCopy.getType());
			switch(newCopy.getType())
			{
				case SimPLTypes.TYPE_BOOLEAN:
					newCopy.setBoolValue(oldCopy.getBoolValue());
					return newCopy;
				case SimPLTypes.TYPE_INTEGER:
					newCopy.setIntValue(oldCopy.getIntValue());
					return newCopy;
				case SimPLTypes.TYPE_LIST:
					List<Object> oldCopyList = oldCopy.getListValue();
					List<Object> newCopyList = new LinkedList<Object>();

					for(int i = 0; i < oldCopyList.size(); ++i)
					{
						newCopyList.add(this.copyDump((SimpleNode)oldCopyList.get(i)));
					}
					newCopy.setListValue(newCopyList);
					return newCopy;
				case SimPLTypes.TYPE_PAIR:
					List<Object> oldCopyPiar = oldCopy.getPairValue();
					List<Object> newCopyPair = new LinkedList<Object>();

					for(int i = 0; i < oldCopyPiar.size(); ++i)
					{
						newCopyPair.add(this.copyDump((SimpleNode)oldCopyPiar.get(i)));
					}
					newCopy.setPairValue(newCopyPair);
					return newCopy;
				default:
					return v;
			}
		}
		else
		{
			return v;
		}
	}
	

	public ASTValue(int id) {
		super(id);
	}

	public ASTValue(SimPLParser p, int id) {
		super(p, id);
	}

	public static Node jjtCreate(int id) {
		return new ASTValue(id);
	}

	public static Node jjtCreate(SimPLParser p, int id) {
		return new ASTValue(p, id);
	}

	/** Accept the visitor. 
	 * @throws TypeException 
	 * @throws InterpretException **/
	public Object jjtAccept(SimPLParserVisitor visitor, Object data) throws TypeException, InterpretException {
		return visitor.visit(this, data);
	}
	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getIntValue() {
		return intValue;
	}

	public void setIntValue(int intValue) {
		this.intValue = intValue;
	}

	public boolean getBoolValue() {
		return boolValue;
	}

	public void setBoolValue(boolean boolValue) {
		this.boolValue = boolValue;
	}

	public List<Object> getListValue() {
		return listValue;
	}

	public void setListValue(List<Object> listValue) {
		this.listValue = listValue;
	}

	public List<Object> getPairValue() {
		return pairValue;
	}

	public void setPairValue(List<Object> pairValue) {
		this.pairValue = pairValue;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	
	public String toString()
	{
		return this.dump(this);
	}
	
	public String dump(SimpleNode v)
	{
		if(v instanceof ASTValue)
		{
			ASTValue astValue = (ASTValue)v;
			switch(astValue.getType())
			{
				case SimPLTypes.TYPE_BOOLEAN:
					return new Boolean(astValue.getBoolValue()).toString();
				case SimPLTypes.TYPE_INTEGER:
					return new Integer(astValue.getIntValue()).toString();
				case SimPLTypes.TYPE_LIST:
					List<Object> astValueList = astValue.getListValue();
					String listStr = "list(";
					if(astValueList.size() > 0)
					{
						for(int i = 0; i < astValueList.size() - 1; ++i)
						{
							listStr += dump((SimpleNode)astValueList.get(i)) + "::";
						}
						
						listStr += dump((SimpleNode)astValueList.get(astValueList.size() - 1)) + ")";
					}
					else listStr += "nil)";
					
					return listStr;
				case SimPLTypes.TYPE_PAIR:
					List<Object> astValuePair = astValue.getPairValue();
					return "(" + dump((SimpleNode)astValuePair.get(0)) +
							"," + dump((SimpleNode)astValuePair.get(1)) + ")";
				case SimPLTypes.TYPE_UNIT:
					return "()";
				default:
					return "";
			}
		}
		else
		{
			ASTAnonymousFunctionNode func = (ASTAnonymousFunctionNode)v;
			
			return func.toString();
		}
	}
	
	public boolean equals(Object obj)
	{
		if(!(obj instanceof ASTValue))
		{
			return false;
			
		}
		
		ASTValue astValue = (ASTValue)obj;
		if(astValue.getType() != this.getType())
		{
			return false;
		}
		switch(astValue.getType())
		{
			case SimPLTypes.TYPE_BOOLEAN:
				return astValue.getBoolValue() == this.getBoolValue();
			case SimPLTypes.TYPE_INTEGER:
				return astValue.getIntValue() == this.getIntValue();
			case SimPLTypes.TYPE_LIST:
				List<Object> astValueList = astValue.getListValue();
				if(this.getListValue().size() != astValue.getListValue().size())
				{
					return false;
				}
				boolean listBool = true;
				for(int i = 0; i < astValueList.size(); ++i)
				{
					listBool = listBool && this.getListValue().get(i).equals(astValueList.get(i));
					if(!listBool)
					{
						return false;
					}
				}
				
				return listBool;
			case SimPLTypes.TYPE_PAIR:
				List<Object> astValuePair = astValue.getPairValue();
				return this.getPairValue().get(0).equals(astValuePair.get(0))
						&& this.getPairValue().get(1).equals(astValuePair.get(1));
			case SimPLTypes.TYPE_UNIT:
				return true;
			default:
				return false;
		}
		
	}
	

}
