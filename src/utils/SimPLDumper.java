package utils;

import java.util.List;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.util.Stack;

import semantics.SimPLTypes;

import javacc.ASTAnonymousFunctionNode;
import javacc.ASTValue;
import javacc.SimpleNode;

public class SimPLDumper {

	
	public static void dumpASTValue(SimpleNode v, OutputStream out) throws IOException
	{
		if(v instanceof ASTValue)
		{
			ASTValue astValue = (ASTValue)v;
			switch(astValue.getType())
			{
				case SimPLTypes.TYPE_BOOLEAN:
					
					out.write(new Boolean(astValue.getBoolValue()).toString().getBytes());
					out.write(new String(" ").getBytes());
					break;
				case SimPLTypes.TYPE_INTEGER:
					out.write(new Integer(astValue.getIntValue()).toString().getBytes());
					out.write(new String(" ").getBytes());
					break;
				case SimPLTypes.TYPE_LIST:
					List<Object> astValueList = astValue.getListValue();
					
					for(int i = 0; i < astValueList.size() - 1; ++i)
					{
						dumpASTValue((SimpleNode)astValueList.get(i), out);
						out.write(new String("::").getBytes());
					}
					
					dumpASTValue((SimpleNode)astValueList.
							get(astValueList.size() - 1), out);
					out.write(new String(" ").getBytes());
					
					//out.println(astValue.getListValue());
					break;
				case SimPLTypes.TYPE_PAIR:
					List<Object> astValuePair = astValue.getPairValue();
					out.write(new String("(").getBytes());
					dumpASTValue((SimpleNode)astValuePair.get(0), out);
					out.write(new String(",").getBytes());
					
					dumpASTValue((SimpleNode)astValuePair.get(1), out);
					out.write(new String(") ").getBytes());
					//out.println(astValue.getPairValue());
					break;
			}
		}
		else
		{
			ASTAnonymousFunctionNode func = (ASTAnonymousFunctionNode)v;
			
			out.write(new String("fun " + func.getIdentifier().getName()
					+ "->" + func.getAnonymousFunction().toString()).getBytes());
		}
		
		
	}
}
