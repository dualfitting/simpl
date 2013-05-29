package semantics;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import semantics.SimPLTypes;
import utils.*;
import javacc.*;

public class InterpretVisitor implements SimPLParserVisitor {

	private Stack<SimpleNode> executeStack = new Stack<SimpleNode>();
	private EnvironmentStack envStack = new EnvironmentStack();

	@Override
	public Object visit(SimpleNode node, Object data) throws TypeException, InterpretException {
		// Should not go here
		throw new InterpretException("Wrong call to visit() method. Probably a bug.");
	}

	@Override
	public Object visit(ASTStart node, Object data) throws TypeException, InterpretException {
		// clear previous stack
		executeStack.clear();
		envStack.clear();
		// Initial global environment stack
		envStack.push(new HashMap<String, SimpleNode>());
		
		// interpret root node
		node.jjtGetChild(0).jjtAccept(this, data);
		
		// pop the result
		SimpleNode returnNode = executeStack.pop();
		
		// return type is not a value
		if (!(returnNode instanceof ASTValue)
				&& !(returnNode instanceof ASTAnonymousFunctionNode)) {
			throw new TypeException("Return type should be a value or function.");
		}
		return returnNode;
	}

	@Override
	public Object visit(ASTExpression node, Object data) throws TypeException, InterpretException {
		// visit only child node : sequence expression
		return node.jjtGetChild(0).jjtAccept(this, data);
	}

	@Override
	public Object visit(ASTVariable node, Object data) throws TypeException, InterpretException {
		
		executeStack.push(node);
		
		// Have this variable been declared in current scope ?
		if (envStack.containsKey(node.getName())) {
			
			SimpleNode sn = envStack.get(node.getName());

			
			if (sn instanceof ASTValue) {
				// value type
				return ((ASTValue) sn).getType();
			} else if (sn instanceof ASTAnonymousFunctionNode) {
				// function type
				return SimPLTypes.TYPE_FUNCTION;
			} else {
				throw new InterpretException("Undefined variable in current scope. Probably a bug.");
			}

		} else {
			return null;
		}

	}

	@Override
	public Object visit(ASTValue node, Object data) throws TypeException, InterpretException {
		if (SimPLTypes.TYPE_FUNCTION != node.getType()) {
			// not a function
			// push it to the stack
			executeStack.push(node);
			return node.getType();
		} else {
			// a function
			// pass it to ASTAnonymousFunctionNode visit()
			node.jjtGetChild(0).jjtAccept(this, data);
			return SimPLTypes.TYPE_FUNCTION;
		}

	}

	@Override
	public Object visit(ASTAnonymousFunctionNode node, Object data)
			throws TypeException {
		if (!(node.jjtGetChild(0) instanceof ASTVariable)) {
			throw new TypeException("Function argument type error. Check your function definations.");
		}
		// remember args and function body
		// will interpret it when called
		node.setIdentifier((ASTVariable) node.jjtGetChild(0));
		node.setAnonymousFunction((SimpleNode) node.jjtGetChild(1));
		
		HashMap<String, SimpleNode> funcEnv = envStack.peek();

		if(null != node.getParentEnv())
		{
			node.getParentEnv().putAll(funcEnv);
			//funcEnv.putAll(node.getParentEnv());
		} 
		else
	        node.setParentEnv(funcEnv);

		// push it to the stack
		executeStack.push(node);

		return SimPLTypes.TYPE_FUNCTION;
	}

	@Override
	public Object visit(ASTCommonLeftBracketExpression node, Object data)
			throws TypeException, InterpretException {
		// check expression type
		// can be of the following 3:
		// 1) Bracket expression
		// 2) Application expression
		// 3) Pair expression
		Integer type1 = (Integer) node.jjtGetChild(0).jjtAccept(this, data);

		if (node.jjtGetChild(1) instanceof ASTBracketExpression) {
			// bracket expression
			// nothing to do
			return type1;
		} else if (node.jjtGetChild(1) instanceof ASTApplicationExpression) {
			// application expression
			// get function name
			SimpleNode appliedFunc = executeStack.pop();
			ASTVariable funcName = null;
			SimpleNode funcNode = null;
			
			if (appliedFunc instanceof ASTVariable) {
				// a function variable
				funcName = (ASTVariable) appliedFunc;
				// find defination
				if (!envStack.containsKey(funcName.getName())) {
					throw new InterpretException("Function " + funcName.getName()
							+ " is not defined");
				}

				funcNode = envStack.get(funcName.getName());
			} else if (appliedFunc instanceof ASTAnonymousFunctionNode) {
				// anonymous function
				funcNode = appliedFunc;
			} else {
				
				throw new TypeException("No function defined in application expression. " +
						"Check left side of your application expression.");
			}

			// check type of function node
			if (!(funcNode instanceof ASTAnonymousFunctionNode)) {
				throw new TypeException("Not a valid function type.");
			}

			ASTAnonymousFunctionNode func = (ASTAnonymousFunctionNode) funcNode;
			// interpret right side of application expression
			node.jjtGetChild(1).jjtAccept(this, data);
			
			SimpleNode valueNode = executeStack.pop();

			// Right side of application expression must be 1 of the following 3,
			if (!(valueNode instanceof ASTValue)
					&& !(valueNode instanceof ASTAnonymousFunctionNode)
					&& !(valueNode instanceof ASTVariable)) {
				throw new TypeException("Invalid expression of " +
						"right side of application expression." +
						"Check right side of your application expression");
			}
			


			if (valueNode instanceof ASTVariable
					&& envStack.containsKey(((ASTVariable) valueNode).getName())) {
				// if the application value is a variable, find it
				valueNode = envStack.get(((ASTVariable) valueNode).getName());
			} else if (valueNode instanceof ASTVariable) {
				
				throw new InterpretException("Undefined identifier " 
				    + ((ASTVariable) valueNode).getName()+ ".");
			}
			
			// sub function gets parent's environment
			HashMap<String, SimpleNode> parentEnv =
			     new HashMap<String, SimpleNode>();

			if (null != func.getParentEnv()) {

				parentEnv.putAll(func.getParentEnv());
			}
			if(valueNode instanceof ASTValue)
			{
				valueNode = ((ASTValue)valueNode).deepCopy();
			}
			// prepare environment for function interpret
			envStack.push(parentEnv);
			// pass args to the function environment
				

			envStack.add(func.getIdentifier().getName(), valueNode);
			
			// interpret function body
			func.getAnonymousFunction().jjtAccept(this, data);
			

			// get return value
			SimpleNode returnValue = executeStack.peek();
			
			// if it's a variable, change it to a value
			if (returnValue instanceof ASTVariable) {
				executeStack.pop();
				if(envStack.containsKey(((ASTVariable) returnValue).getName()))
				{
					returnValue = (SimpleNode) envStack
							.get(((ASTVariable) returnValue).getName());
				}
				else
				{
					throw new InterpretException("Undefined identifier " 
						    + ((ASTVariable) returnValue).getName()+ ".");
				}
				
				
				
				executeStack.push(returnValue);
			}

			// restore environment after function call
			parentEnv = envStack.pop();

			
			if (returnValue instanceof ASTValue) {
				// if a value is returned then end this call

				
				return ((ASTValue) returnValue).getType();
			} else if (returnValue instanceof ASTAnonymousFunctionNode) {
				
				return SimPLTypes.TYPE_FUNCTION;
			} else {
				// not a valid return type
				throw new TypeException("Error return type of a function.");
			}

		} else if (node.jjtGetChild(1) instanceof ASTPairExpression) {
			// pair expression
			// get first value and second value then return it
			SimpleNode firstNode = executeStack.pop();
			SimpleNode firstValue = null;
			if (firstNode instanceof ASTVariable) {

				String varName = ((ASTVariable) firstNode).getName();
				if (envStack.containsKey(varName)
						&& (envStack.get(varName) instanceof ASTValue)) {
					firstValue = ((ASTValue) envStack.get(varName)).deepCopy();
				} 
				else if (envStack.containsKey(varName)){
					throw new TypeException("Error type of " 
						    + varName + ". Expecting value instead of function");
				}else {
					firstValue = new ASTValue(0);
					((ASTValue) firstValue).setType(SimPLTypes.TYPE_UNDEFINED);
				}
			} else if (firstNode instanceof ASTValue) {
				firstValue = ((ASTValue) firstNode).deepCopy();
			} else if (firstNode instanceof ASTAnonymousFunctionNode) {
				firstValue = (ASTAnonymousFunctionNode) firstNode;
			} else {
				throw new TypeException("Error value type for pair expression");
			}
			node.jjtGetChild(1).jjtAccept(this, data);

			SimpleNode secondNode = executeStack.pop();
			SimpleNode secondValue = null;
			if (secondNode instanceof ASTVariable) {

				String varName = ((ASTVariable) secondNode).getName();
				if (envStack.containsKey(varName)
						&& (envStack.get(varName) instanceof ASTValue)) {
					secondValue = ((ASTValue) envStack.get(varName)).deepCopy();
				} else if (envStack.containsKey(varName)){
					throw new TypeException("Error type of " 
						    + varName + ". Expecting value instead of function");
				}else {
					secondValue = new ASTValue(0);
					((ASTValue) secondValue).setType(SimPLTypes.TYPE_UNDEFINED);
				}
			} else if (secondNode instanceof ASTValue) {
				secondValue = ((ASTValue) secondNode).deepCopy();
			} else if (secondNode instanceof ASTAnonymousFunctionNode) {
				secondValue = (ASTAnonymousFunctionNode) secondNode;
			} else {
				throw new TypeException("Error value type for pair expression");
			}

			ASTValue pairValue = new ASTValue(0);
			pairValue.setType(SimPLTypes.TYPE_PAIR);
			List<Object> pairList = new LinkedList<Object>();
			pairList.add(firstValue);
			pairList.add(secondValue);

			pairValue.setPairValue(pairList);

			executeStack.push(pairValue);

			return SimPLTypes.TYPE_PAIR;
		} else {
			throw new TypeException("Error expression type." +
					" Check your bracket expression.");
		}

	}

	@Override
	public Object visit(ASTPairExpression node, Object data)
			throws TypeException, InterpretException {
		return (Integer) node.jjtGetChild(0).jjtAccept(this, data);
	}

	@Override
	public Object visit(ASTApplicationExpression node, Object data)
			throws TypeException, InterpretException {
		return node.jjtGetChild(0).jjtAccept(this, data);
	}

	@Override
	public Object visit(ASTBracketExpression node, Object data) {
		return null;
	}

	@Override
	public Object visit(ASTLetExpression node, Object data)
			throws TypeException, InterpretException {
		// let x = e1 in e2
		// interpret x
		node.jjtGetChild(0).jjtAccept(this, data);
		// should be a variable type
		SimpleNode varNode = executeStack.pop();
		if (!(varNode instanceof ASTVariable)) {
			throw new TypeException("Error variable type in let expression.");
		}

		// interpret e1
		node.jjtGetChild(1).jjtAccept(this, data);

		// should be a value node
		SimpleNode valNode = executeStack.pop();
		
		if(!(valNode instanceof ASTValue)
				&& !(valNode instanceof ASTAnonymousFunctionNode))
		{
			throw new TypeException("Error value type in let expression");
		}

		// Create new environment for interpret let expression
		HashMap<String, SimpleNode> allocEnv = new HashMap<String, SimpleNode>();

		// prepare environment for interpret let expression e2
		envStack.push(allocEnv);
		// pass args
		envStack.add(((ASTVariable) varNode).getName(), valNode);

		
		// interpret e2
		node.jjtGetChild(2).jjtAccept(this, data);
		
		
		// get return value
		SimpleNode returnValue = executeStack.peek();

		// if it's a variable, change it to a value
		if (returnValue instanceof ASTVariable) {
			executeStack.pop();
			if(envStack.containsKey(((ASTVariable) returnValue).getName()))
			{
				returnValue = (ASTValue) envStack
					.get(((ASTVariable) returnValue).getName());
			}
			else
			{
				throw new InterpretException("Undefined identifier " 
				    + ((ASTVariable) returnValue).getName()+ ".");
			}
						
			executeStack.push(returnValue);
		}
		
		
		// clear environment
		envStack.pop();

		// check result type
		if (returnValue instanceof ASTValue) {
			return ((ASTValue) returnValue).getType();
		} 
		else if (returnValue instanceof ASTAnonymousFunctionNode) {
			return SimPLTypes.TYPE_FUNCTION;
		} else {
			throw new TypeException("Error return type in let expression.");
		}
	}

	@Override
	public Object visit(ASTIfThenElseExpression node, Object data)
			throws TypeException, InterpretException {
		Node ifExpression = node.jjtGetChild(0);
		Node thenExpression = node.jjtGetChild(1);
		Node elseExpression = node.jjtGetChild(2);

		// interpret if expression
		ifExpression.jjtAccept(this, data);

		SimpleNode sn = executeStack.pop();
		ASTValue snValue = null;


		if (sn instanceof ASTVariable) {
			// if result is a variable then get its value
			String snName = ((ASTVariable) sn).getName();
			if (envStack.containsKey(snName)) {
				snValue = (ASTValue) envStack.get(snName);
			} else {
				throw new InterpretException("Undefined identifier " 
					    + snName + ".");
			}
		} else if (sn instanceof ASTValue) {
			snValue = (ASTValue) sn;
		} else {
			throw new TypeException("if-condition needs a value of boolean type.");
		}

		if (SimPLTypes.TYPE_BOOLEAN != snValue.getType()) {
			throw new TypeException("if-condition needs a value of boolean type.");
		}

		if (snValue.getBoolValue()) {
			// then branch
			Integer thenType = (Integer) thenExpression.jjtAccept(this, data);
			return thenType;
		} else {
			// else branch
			Integer elseType = (Integer) elseExpression.jjtAccept(this, data);

			return elseType;
		}
	}

	@Override
	public Object visit(ASTSequenceExpression node, Object data)
			throws TypeException, InterpretException {

		int numberOfChildren = node.jjtGetNumChildren();
		for (int i = 0; i < numberOfChildren - 1; ++i) {
			node.jjtGetChild(i).jjtAccept(this, data);
			executeStack.pop();
		}
		// last expression type as sequence expression type
		return node.jjtGetChild(numberOfChildren - 1).jjtAccept(this, data);
	}

	@Override
	public Object visit(ASTJoinNode node, Object data) throws TypeException, InterpretException {
		// first expression requires a value or variable type
		node.jjtGetChild(0).jjtAccept(this, data);
		SimpleNode firstNode = executeStack.pop();
		SimpleNode firstValue = null;
		if (firstNode instanceof ASTVariable) {

			String varName = ((ASTVariable) firstNode).getName();
			if (envStack.containsKey(varName) && (envStack.get(varName) instanceof ASTValue)) {
				firstValue = ((ASTValue) envStack.get(varName)).deepCopy();
			} else if (envStack.containsKey(varName)){
				throw new TypeException("Error type of " 
					    + varName + ". Expecting value instead of function");
			}else {
				throw new TypeException("Error type ");
			}
		} else if (firstNode instanceof ASTValue) {
			firstValue = ((ASTValue)firstNode).deepCopy();
		} else if (firstNode instanceof ASTAnonymousFunctionNode){
			firstValue = firstNode;
		}
		else {
			throw new TypeException("Error value type for list expression");
		}
		node.jjtGetChild(1).jjtAccept(this, data);

		SimpleNode secondNode = executeStack.pop();
		SimpleNode secondValue = null;
		if (secondNode instanceof ASTVariable) {

			String varName = ((ASTVariable) secondNode).getName();
			if (envStack.containsKey(varName) && (envStack.get(varName) instanceof ASTValue)) {
				secondValue = ((ASTValue) envStack.get(varName)).deepCopy();
			} else if (envStack.containsKey(varName)){
				throw new TypeException("Error type of " 
					    + varName + ". Expecting value instead of function");
			}else {
				throw new InterpretException("Undefined identifier " 
					    + varName + ".");
			}
		} else if (secondNode instanceof ASTValue) {
			secondValue = ((ASTValue)secondNode).deepCopy();
		} else if (secondNode instanceof ASTAnonymousFunctionNode) {
			secondValue = secondNode;
		}else {
		
			throw new TypeException("Error value type for list expression.");
		}

		if (secondValue instanceof ASTValue
				&& SimPLTypes.TYPE_LIST == ((ASTValue)secondValue).getType()) {
			secondValue = ((ASTValue)secondValue).deepCopy();
			
			if(firstValue instanceof ASTValue
					&& ((ASTValue)firstValue).getType() == SimPLTypes.TYPE_LIST
					&& ((ASTValue)firstValue).getListValue().size() == 0){
				// nil::list
				// do nothing about secondvalue
			}else if(firstValue instanceof ASTValue
					&& ((ASTValue)secondValue).getListValue().size() > 0
					&& ((ASTValue)secondValue).getListValue().get(0) instanceof ASTValue
					&& (((ASTValue)((ASTValue)secondValue).getListValue().get(0)).getType()
					== ((ASTValue)firstValue).getType()))
			{
				((ASTValue)secondValue).getListValue().add(0, firstValue);
			}else if (firstValue instanceof ASTAnonymousFunctionNode
					&& ((ASTValue)secondValue).getListValue().size() > 0
					&& ((ASTValue)secondValue).getListValue().get(0) instanceof ASTAnonymousFunctionNode)
			{
				((ASTValue)secondValue).getListValue().add(0, firstValue);
			}else if(((ASTValue)secondValue).getListValue().size() == 0)
			{
				// e::nil
				((ASTValue)secondValue).getListValue().add(0, firstValue);
			}
			else
			{
				throw new TypeException("List value type incompatible.");
			}
			
			executeStack.push(secondValue);
		} else if (firstValue instanceof ASTValue
				&& secondValue instanceof ASTValue
				&& SimPLTypes.TYPE_LIST != ((ASTValue)secondValue).getType()
				&& ((ASTValue)firstValue).getType() == ((ASTValue)secondValue).getType()) {
			ASTValue newListValue = new ASTValue(0);

			newListValue.setType(SimPLTypes.TYPE_LIST);

			List<Object> joinedList = new LinkedList<Object>();

			joinedList.add(firstValue);
			joinedList.add(secondValue);

			newListValue.setListValue(joinedList);
			executeStack.push(newListValue);
		} else if (firstValue instanceof ASTAnonymousFunctionNode
				&& secondValue instanceof ASTAnonymousFunctionNode)
		{
			ASTValue newListValue = new ASTValue(0);

			newListValue.setType(SimPLTypes.TYPE_LIST);

			List<Object> joinedList = new LinkedList<Object>();

			joinedList.add(firstValue);
			joinedList.add(secondValue);

			newListValue.setListValue(joinedList);
			executeStack.push(newListValue);
		}
		else {
			throw new TypeException("Error value type for list expression.");
		}
		


		return SimPLTypes.TYPE_LIST;
	}

	@Override
	public Object visit(ASTConditionalAndNode node, Object data)
			throws TypeException, InterpretException {
		node.jjtGetChild(0).jjtAccept(this, data);
		SimpleNode firstNode = executeStack.pop();
		ASTValue firstValue = null;
		if (firstNode instanceof ASTVariable) {

			String varName = ((ASTVariable) firstNode).getName();
			if (envStack.containsKey(varName) && (envStack.get(varName) instanceof ASTValue)) {
				firstValue = ((ASTValue) envStack.get(varName)).deepCopy();
			} else if (envStack.containsKey(varName)){
				throw new TypeException("Error type of " 
					    + varName + ". Expecting value instead of function");
			}else {
				throw new InterpretException("Undefined identifier " 
					    + varName + ".");
			}
		} else if (firstNode instanceof ASTValue) {
			firstValue = ((ASTValue) firstNode).deepCopy();
		} else {
			throw new TypeException("First expression in and-expression requires" +
					" a value or identifier.");
		}
		node.jjtGetChild(1).jjtAccept(this, data);

		SimpleNode secondNode = executeStack.pop();
		ASTValue secondValue = null;
		if (secondNode instanceof ASTVariable) {

			String varName = ((ASTVariable) secondNode).getName();
			if (envStack.containsKey(varName) && (envStack.get(varName) instanceof ASTValue)) {
				secondValue = ((ASTValue) envStack.get(varName)).deepCopy();
			} else if (envStack.containsKey(varName)){
				throw new TypeException("Error type of " 
					    + varName + ". Expecting value instead of function");
			}else {
				throw new InterpretException("Undefined identifier " 
					    + varName + ".");
			}
		} else if (secondNode instanceof ASTValue) {
			secondValue = ((ASTValue) secondNode).deepCopy();
		} else {
			throw new TypeException("Second expression in and-expression requires" +
					" a value or identifer.");
		}

		if (SimPLTypes.TYPE_BOOLEAN != firstValue.getType()
				|| SimPLTypes.TYPE_BOOLEAN != secondValue.getType()) {
			throw new TypeException("And-expression requires boolean types on two sides.");
		}

		firstValue.setBoolValue(firstValue.getBoolValue()
				&& secondValue.getBoolValue());

		executeStack.push(firstValue);

		return SimPLTypes.TYPE_BOOLEAN;
	}

	@Override
	public Object visit(ASTConditionalOrNode node, Object data)
			throws TypeException, InterpretException {
		node.jjtGetChild(0).jjtAccept(this, data);
		SimpleNode firstNode = executeStack.pop();
		ASTValue firstValue = null;
		if (firstNode instanceof ASTVariable) {

			String varName = ((ASTVariable) firstNode).getName();
			if (envStack.containsKey(varName) && (envStack.get(varName) instanceof ASTValue)) {
				firstValue = ((ASTValue) envStack.get(varName)).deepCopy();
			} else if (envStack.containsKey(varName)){
				throw new TypeException("Error type of " 
					    + varName + ". Expecting value instead of function");
			} else {
				throw new InterpretException("Undefined identifier " 
					    + varName + ".");
			}
		} else if (firstNode instanceof ASTValue) {
			firstValue = ((ASTValue) firstNode).deepCopy();
		} else {
			throw new TypeException("First expression in or-expression requires" +
					" a value or identifier.");
		}
		node.jjtGetChild(1).jjtAccept(this, data);

		SimpleNode secondNode = executeStack.pop();
		ASTValue secondValue = null;
		if (secondNode instanceof ASTVariable) {

			String varName = ((ASTVariable) secondNode).getName();
			if (envStack.containsKey(varName) && (envStack.get(varName) instanceof ASTValue)) {
				secondValue = ((ASTValue) envStack.get(varName)).deepCopy();
			} else if (envStack.containsKey(varName)){
				throw new TypeException("Error type of " 
					    + varName + ". Expecting value instead of function");
			}else {
				throw new InterpretException("Undefined identifier " 
					    + varName + ".");
			}
		} else if (secondNode instanceof ASTValue) {
			secondValue = ((ASTValue) secondNode).deepCopy();
		} else {
			throw new TypeException("Second expression in or-expression requires" +
					" a value or identifier.");
		}

		if (SimPLTypes.TYPE_BOOLEAN != firstValue.getType()
				|| SimPLTypes.TYPE_BOOLEAN != secondValue.getType()) {
			throw new TypeException("And-expression requires boolean types on two sides.");
		}

		firstValue.setBoolValue(firstValue.getBoolValue()
				|| secondValue.getBoolValue());

		executeStack.push(firstValue);
		return SimPLTypes.TYPE_BOOLEAN;
	}

	@Override
	public Object visit(ASTEqualityEqualNode node, Object data)
			throws TypeException, InterpretException {
		node.jjtGetChild(0).jjtAccept(this, data);
		SimpleNode firstNode = executeStack.pop();
		ASTValue firstValue = null;
		if (firstNode instanceof ASTVariable) {

			String varName = ((ASTVariable) firstNode).getName();
			if (envStack.containsKey(varName) && (envStack.get(varName) instanceof ASTValue)) {
				firstValue = ((ASTValue) envStack.get(varName)).deepCopy();
			} else if (envStack.containsKey(varName)){
				throw new TypeException("Error type of " 
					    + varName + ". Expecting value instead of function");
			}else {
				throw new InterpretException("Undefined identifier " 
					    + varName + ".");
			}
		} else if (firstNode instanceof ASTValue) {
			firstValue = ((ASTValue) firstNode).deepCopy();
		} else {
			throw new TypeException("First expression in equal-expression requires" +
					" a value or identifier.");
		}
		node.jjtGetChild(1).jjtAccept(this, data);

		SimpleNode secondNode = executeStack.pop();
		ASTValue secondValue = null;
		if (secondNode instanceof ASTVariable) {

			String varName = ((ASTVariable) secondNode).getName();
			if (envStack.containsKey(varName) && (envStack.get(varName) instanceof ASTValue)) {
				secondValue = ((ASTValue) envStack.get(varName)).deepCopy();
			} else if (envStack.containsKey(varName)){
				throw new TypeException("Error type of " 
					    + varName + ". Expecting value instead of function");
			}else {
				throw new InterpretException("Undefined identifier " 
					    + varName + ".");
			}
		} else if (secondNode instanceof ASTValue) {
			secondValue = ((ASTValue) secondNode).deepCopy();
		} else {
			throw new TypeException("Second expression in equal-expression requires" +
					" a value or identifier.");
		}

		if (firstValue.getType() != secondValue.getType()) {
			throw new TypeException("Equal-expression requires the same types on two sides.");
		}
		boolean result = firstValue.equals(secondValue);

		ASTValue newBoolValue = new ASTValue(0);

		newBoolValue.setType(SimPLTypes.TYPE_BOOLEAN);
		newBoolValue.setBoolValue(result);
		executeStack.push(newBoolValue);
		return SimPLTypes.TYPE_BOOLEAN;
	}

	@Override
	public Object visit(ASTEqualityBiggerThanNode node, Object data)
			throws TypeException, InterpretException {
		node.jjtGetChild(0).jjtAccept(this, data);
		SimpleNode firstNode = executeStack.pop();
		ASTValue firstValue = null;
		if (firstNode instanceof ASTVariable) {

			String varName = ((ASTVariable) firstNode).getName();
			if (envStack.containsKey(varName) && (envStack.get(varName) instanceof ASTValue)) {
				firstValue = ((ASTValue) envStack.get(varName)).deepCopy();
			} else if (envStack.containsKey(varName)){
				throw new TypeException("Error type of " 
					    + varName + ". Expecting value instead of function");
			}else {
				throw new InterpretException("Undefined identifier " 
					    + varName + ".");
			}
		} else if (firstNode instanceof ASTValue) {
			firstValue = ((ASTValue) firstNode).deepCopy();
		} else {
			throw new TypeException("First expression in bigger-than-expression requires" +
					" a value or identifier.");
		}
		node.jjtGetChild(1).jjtAccept(this, data);

		SimpleNode secondNode = executeStack.pop();
		ASTValue secondValue = null;
		if (secondNode instanceof ASTVariable) {

			String varName = ((ASTVariable) secondNode).getName();
			if (envStack.containsKey(varName) && (envStack.get(varName) instanceof ASTValue)) {
				secondValue = ((ASTValue) envStack.get(varName)).deepCopy();
			} else if (envStack.containsKey(varName)){
				throw new TypeException("Error type of " 
					    + varName + ". Expecting value instead of function");
			}else {
				throw new InterpretException("Undefined identifier " 
					    + varName + ".");
			}
		} else if (secondNode instanceof ASTValue) {
			secondValue = ((ASTValue) secondNode).deepCopy();
		} else {
			throw new TypeException("Second expression in bigger-than-expression requires" +
					" a value or identifier.");
		}

		if (SimPLTypes.TYPE_INTEGER != firstValue.getType()
				|| SimPLTypes.TYPE_INTEGER != secondValue.getType()) {
			throw new TypeException("Bigger-than-expression requires " +
					"the same types of integer on two sides.");
		}

		ASTValue newBoolValue = new ASTValue(0);

		newBoolValue.setType(SimPLTypes.TYPE_BOOLEAN);
		newBoolValue.setBoolValue(firstValue.getIntValue() > secondValue
				.getIntValue());

		executeStack.push(newBoolValue);
		return SimPLTypes.TYPE_BOOLEAN;
	}

	@Override
	public Object visit(ASTEqualityLessThanNode node, Object data)
			throws TypeException, InterpretException {
		node.jjtGetChild(0).jjtAccept(this, data);
		SimpleNode firstNode = executeStack.pop();
		ASTValue firstValue = null;
		if (firstNode instanceof ASTVariable) {

			String varName = ((ASTVariable) firstNode).getName();
			if (envStack.containsKey(varName) && (envStack.get(varName) instanceof ASTValue)) {
				firstValue = ((ASTValue) envStack.get(varName)).deepCopy();
			} else if (envStack.containsKey(varName)){
				throw new TypeException("Error type of " 
					    + varName + ". Expecting value instead of function");
			}else {
				throw new InterpretException("Undefined identifier " 
					    + varName + ".");
			}
		} else if (firstNode instanceof ASTValue) {
			firstValue = ((ASTValue) firstNode).deepCopy();
		} else {
			throw new TypeException("First expression in less-than-expression requires" +
					" a value or identifier.");
		}
		node.jjtGetChild(1).jjtAccept(this, data);

		SimpleNode secondNode = executeStack.pop();
		ASTValue secondValue = null;
		if (secondNode instanceof ASTVariable) {

			String varName = ((ASTVariable) secondNode).getName();
			if (envStack.containsKey(varName) && (envStack.get(varName) instanceof ASTValue)) {
				secondValue = ((ASTValue) envStack.get(varName)).deepCopy();
			} else if (envStack.containsKey(varName)){
				throw new TypeException("Error type of " 
					    + varName + ". Expecting value instead of function");
			}else {
				throw new InterpretException("Undefined identifier " 
					    + varName + ".");
			}
		} else if (secondNode instanceof ASTValue) {
			secondValue = ((ASTValue) secondNode).deepCopy();
		} else {
			throw new TypeException("Second expression in less-than-expression requires" +
					" a value or identifier.");
		}

		if (SimPLTypes.TYPE_INTEGER != firstValue.getType()
				|| SimPLTypes.TYPE_INTEGER != secondValue.getType()) {
			throw new TypeException("Less-than-expression requires " +
					"the same types of integer on two sides.");
		}

		ASTValue newBoolValue = new ASTValue(0);

		newBoolValue.setType(SimPLTypes.TYPE_BOOLEAN);
		newBoolValue.setBoolValue(firstValue.getIntValue() < secondValue
				.getIntValue());

		executeStack.push(newBoolValue);
		return SimPLTypes.TYPE_BOOLEAN;
	}

	@Override
	public Object visit(ASTAdditivePlusNode node, Object data)
			throws TypeException, InterpretException {
		node.jjtGetChild(0).jjtAccept(this, data);
		SimpleNode firstNode = executeStack.pop();
		ASTValue firstValue = null;
		if (firstNode instanceof ASTVariable) {

			String varName = ((ASTVariable) firstNode).getName();
			if (envStack.containsKey(varName) && (envStack.get(varName) instanceof ASTValue)) {
				firstValue = ((ASTValue) envStack.get(varName)).deepCopy();
			} else if (envStack.containsKey(varName)){
				throw new TypeException("Error type of " 
					    + varName + ". Expecting value instead of function");
			}
			else {
				throw new InterpretException("Undefined identifier " 
					    + varName + ".");
			}
		} else if (firstNode instanceof ASTValue) {
			firstValue = ((ASTValue) firstNode).deepCopy();
		} else {
			throw new TypeException("First expression in plus-expression requires" +
					" a value or identifier.");
		}
		node.jjtGetChild(1).jjtAccept(this, data);

		SimpleNode secondNode = executeStack.pop();
		ASTValue secondValue = null;
		if (secondNode instanceof ASTVariable) {

			String varName = ((ASTVariable) secondNode).getName();
			if (envStack.containsKey(varName) && (envStack.get(varName) instanceof ASTValue)) {
				secondValue = ((ASTValue) envStack.get(varName)).deepCopy();
			} else if (envStack.containsKey(varName)){
				throw new TypeException("Error type of " 
					    + varName + ". Expecting value instead of function");
			}else {
				throw new InterpretException("Undefined identifier " 
					    + varName + ".");
			}
		} else if (secondNode instanceof ASTValue) {
			secondValue = ((ASTValue) secondNode).deepCopy();
		} else {
			throw new TypeException("Second expression in plus-expression requires" +
					" a value or identifier.");
		}

		if (SimPLTypes.TYPE_INTEGER != firstValue.getType()
				|| SimPLTypes.TYPE_INTEGER != secondValue.getType()) {
			throw new TypeException("Plus-expression requires " +
					"the same types of integer on two sides.");
		}

		ASTValue newIntegerValue = new ASTValue(0);

		newIntegerValue.setType(SimPLTypes.TYPE_INTEGER);
		newIntegerValue.setIntValue(firstValue.getIntValue()
				+ secondValue.getIntValue());

		executeStack.push(newIntegerValue);

		return SimPLTypes.TYPE_INTEGER;
	}

	@Override
	public Object visit(ASTAdditiveMinusNode node, Object data)
			throws TypeException, InterpretException {
		node.jjtGetChild(0).jjtAccept(this, data);
		SimpleNode firstNode = executeStack.pop();
		ASTValue firstValue = null;
		if (firstNode instanceof ASTVariable) {

			String varName = ((ASTVariable) firstNode).getName();

			if (envStack.containsKey(varName) && (envStack.get(varName) instanceof ASTValue)) {
				firstValue = ((ASTValue) envStack.get(varName)).deepCopy();
			} else if (envStack.containsKey(varName)){
				throw new TypeException("Error type of " 
					    + varName + ". Expecting value instead of function");
			} else {
				throw new InterpretException("Undefined identifier " 
					    + varName + ".");
			}
		} else if (firstNode instanceof ASTValue) {
			firstValue = ((ASTValue) firstNode).deepCopy();
		} else {
			throw new TypeException("First expression in minus-expression requires" +
					" a value or identifier.");
		}
		node.jjtGetChild(1).jjtAccept(this, data);

		SimpleNode secondNode = executeStack.pop();
		ASTValue secondValue = null;
		if (secondNode instanceof ASTVariable) {

			String varName = ((ASTVariable) secondNode).getName();
			if (envStack.containsKey(varName) && (envStack.get(varName) instanceof ASTValue)) {
				secondValue = ((ASTValue) envStack.get(varName)).deepCopy();
			} else if (envStack.containsKey(varName)){
				throw new TypeException("Error type of " 
					    + varName + ". Expecting value instead of function");
			}else {
				throw new InterpretException("Undefined identifier " 
					    + varName + ".");
			}
		} else if (secondNode instanceof ASTValue) {
			secondValue = ((ASTValue) secondNode).deepCopy();
		} else {
			throw new TypeException("Second expression in minus-expression requires" +
					" a value or identifier.");
		}

		if (SimPLTypes.TYPE_INTEGER != firstValue.getType()
				|| SimPLTypes.TYPE_INTEGER != secondValue.getType()) {
			throw new TypeException("Minus-expression requires " +
					"the same types of integer on two sides.");
		}

		ASTValue newIntegerValue = new ASTValue(0);

		newIntegerValue.setType(SimPLTypes.TYPE_INTEGER);
		newIntegerValue.setIntValue(firstValue.getIntValue()
				- secondValue.getIntValue());
		executeStack.push(newIntegerValue);

		return SimPLTypes.TYPE_INTEGER;
	}

	@Override
	public Object visit(ASTMultiplicativeMultiplyNode node, Object data)
			throws TypeException, InterpretException {
		node.jjtGetChild(0).jjtAccept(this, data);
		SimpleNode firstNode = executeStack.pop();
		ASTValue firstValue = null;
		if (firstNode instanceof ASTVariable) {

			String varName = ((ASTVariable) firstNode).getName();
			if (envStack.containsKey(varName) && (envStack.get(varName) instanceof ASTValue)) {
				firstValue = ((ASTValue) envStack.get(varName)).deepCopy();
			} else if (envStack.containsKey(varName)){
				throw new TypeException("Error type of " 
					    + varName + ". Expecting value instead of function");
			}else {
				throw new InterpretException("Undefined identifier " 
					    + varName + ".");
			}
		} else if (firstNode instanceof ASTValue) {
			firstValue = ((ASTValue) firstNode).deepCopy();
		} else {
			throw new TypeException("First expression in multiply-expression requires" +
					" a value or identifier.");
		}
		node.jjtGetChild(1).jjtAccept(this, data);

		SimpleNode secondNode = executeStack.pop();
		ASTValue secondValue = null;
		if (secondNode instanceof ASTVariable) {

			String varName = ((ASTVariable) secondNode).getName();
			if (envStack.containsKey(varName) && (envStack.get(varName) instanceof ASTValue)) {
				secondValue = ((ASTValue) envStack.get(varName)).deepCopy();
			} else if (envStack.containsKey(varName)){
				throw new TypeException("Error type of " 
					    + varName + ". Expecting value instead of function");
			}else {
				throw new InterpretException("Undefined identifier " 
					    + varName + ".");
			}
		} else if (secondNode instanceof ASTValue) {
			secondValue = ((ASTValue) secondNode).deepCopy();
		} else {
			throw new TypeException("Second expression multiply-expression requires" +
					" a value or identifier.");
		}

		if (SimPLTypes.TYPE_INTEGER != firstValue.getType()
				|| SimPLTypes.TYPE_INTEGER != secondValue.getType()) {
			throw new TypeException("Multiply-expression requires " +
					"the same types of integer on two sides.");
		}

		ASTValue newIntegerValue = new ASTValue(0);

		newIntegerValue.setType(SimPLTypes.TYPE_INTEGER);
		newIntegerValue.setIntValue(firstValue.getIntValue()
				* secondValue.getIntValue());

		executeStack.push(newIntegerValue);

		return SimPLTypes.TYPE_INTEGER;
	}

	@Override
	public Object visit(ASTMultiplicativeDivideNode node, Object data)
			throws TypeException, InterpretException {
		node.jjtGetChild(0).jjtAccept(this, data);
		SimpleNode firstNode = executeStack.pop();
		ASTValue firstValue = null;
		if (firstNode instanceof ASTVariable) {

			String varName = ((ASTVariable) firstNode).getName();
			if (envStack.containsKey(varName) && (envStack.get(varName) instanceof ASTValue)) {
				firstValue = ((ASTValue) envStack.get(varName)).deepCopy();
			} else if (envStack.containsKey(varName)){
				throw new TypeException("Error type of " 
					    + varName + ". Expecting value instead of function");
			} else {
				throw new InterpretException("Undefined identifier " 
					    + varName + ".");
			}
		} else if (firstNode instanceof ASTValue) {
			firstValue = ((ASTValue) firstNode).deepCopy();
		} else {
			throw new TypeException("First expression in divide-expression requires" +
					" a value or identifier.");
		}
		node.jjtGetChild(1).jjtAccept(this, data);

		SimpleNode secondNode = executeStack.pop();
		ASTValue secondValue = null;
		if (secondNode instanceof ASTVariable) {

			String varName = ((ASTVariable) secondNode).getName();
			if (envStack.containsKey(varName) && (envStack.get(varName) instanceof ASTValue)) {
				secondValue = ((ASTValue) envStack.get(varName)).deepCopy();
			} else if (envStack.containsKey(varName)){
				throw new TypeException("Error type of " 
					    + varName + ". Expecting value instead of function");
			}else {
				throw new InterpretException("Undefined identifier " 
					    + varName + ".");
			}
		} else if (secondNode instanceof ASTValue) {
			secondValue = ((ASTValue) secondNode).deepCopy();
		} else {
			throw new TypeException("Second expression in divide-expression requires" +
					" a value or identifier.");
		}

		if (SimPLTypes.TYPE_INTEGER != firstValue.getType()
				|| SimPLTypes.TYPE_INTEGER != secondValue.getType()) {
			throw new TypeException("Divide-expression requires " +
					"the same types of integer on two sides.");
		}

		ASTValue newIntegerValue = new ASTValue(0);

		newIntegerValue.setType(SimPLTypes.TYPE_INTEGER);
		if(secondValue.getIntValue() == 0)
		{
			throw new InterpretException("Divided by zero.\n");
		}
		newIntegerValue.setIntValue(firstValue.getIntValue()
				/ secondValue.getIntValue());

		executeStack.push(newIntegerValue);

		return SimPLTypes.TYPE_INTEGER;
	}

	@Override
	public Object visit(ASTUnaryNotNode node, Object data) throws TypeException, InterpretException {
		node.jjtGetChild(0).jjtAccept(this, data);
		SimpleNode sn = executeStack.pop();
		ASTValue snValue = null;

		if (sn instanceof ASTVariable) {

			String snName = ((ASTVariable) sn).getName();
			if (envStack.containsKey(snName) && (envStack.get(snName) instanceof ASTValue)) {
				snValue = ((ASTValue) envStack.get(snName)).deepCopy();
			} else if (envStack.containsKey(snName)){
				throw new TypeException("Error type of " 
					    + snName + ". Expecting value instead of function");
			}else {
				throw new InterpretException("Undefined identifier " 
					    + snName + ".");
			}
		} else if (sn instanceof ASTValue) {
			snValue = ((ASTValue) sn).deepCopy();
		} else {
			throw new TypeException("Not-expression requires" +
					" a value or identifier.");
		}

		if (SimPLTypes.TYPE_BOOLEAN != snValue.getType()) {
			throw new TypeException("Not-expression requires" +
					" boolean type.");
		}

		snValue.setBoolValue(!snValue.getBoolValue());

		executeStack.push(snValue);

		return SimPLTypes.TYPE_BOOLEAN;
	}

	@Override
	public Object visit(ASTUnaryNegativeNode node, Object data)
			throws TypeException, InterpretException {
		node.jjtGetChild(0).jjtAccept(this, data);
		SimpleNode sn = executeStack.pop();
		ASTValue snValue = null;

		if (sn instanceof ASTVariable) {

			String snName = ((ASTVariable) sn).getName();
			if (envStack.containsKey(snName) && (envStack.get(snName) instanceof ASTValue)) {
				snValue = ((ASTValue) envStack.get(snName)).deepCopy();
			} else if (envStack.containsKey(snName)){
				throw new TypeException("Error type of " 
					    + snName + ". Expecting value instead of function");
			}else {
				throw new InterpretException("Undefined identifier " 
					    + snName + ".");
			}
		} else if (sn instanceof ASTValue) {
			snValue = ((ASTValue) sn).deepCopy();
		} else {
			throw new TypeException("Negative-expression requires" +
					" a value or identifier.");
		}

		if (SimPLTypes.TYPE_INTEGER != snValue.getType()) {
			throw new TypeException("Negative-expression requires" +
					" interger type.");
		}

		snValue.setIntValue(snValue.getIntValue() * -1);

		executeStack.push(snValue);
		return SimPLTypes.TYPE_INTEGER;
	}

	@Override
	public Object visit(ASTWhileLoopExpression node, Object data)
			throws TypeException, InterpretException {

		while (true) {
			// interpret condition expression
			node.jjtGetChild(0).jjtAccept(this, data);
			SimpleNode condNode = executeStack.pop();
			ASTValue condValue = null;

			if (condNode instanceof ASTVariable) {

				String snName = ((ASTVariable) condNode).getName();
				if (envStack.containsKey(snName) && (envStack.get(snName) instanceof ASTValue)) {
					condValue = ((ASTValue) envStack.get(snName)).deepCopy();
				} else if (envStack.containsKey(snName)){
					throw new TypeException("Error type of " 
						    + snName + ". Expecting value instead of function");
				}else {
					throw new TypeException("Undefined identifier " 
						    + snName + ".");
				}
			} else if (condNode instanceof ASTValue) {
				condValue = ((ASTValue) condNode).deepCopy();
			} else {
				throw new TypeException("While-loop conditon requires" +
						" a value or variable.");
			}

			if (SimPLTypes.TYPE_BOOLEAN != condValue.getType()) {
				throw new TypeException("While-loop condition requires " +
						"boolean type ");
			}

			if (!condValue.getBoolValue()) {
				break;
			}

			Integer type = (Integer) node.jjtGetChild(1).jjtAccept(this, data);

			if (!type.equals(SimPLTypes.TYPE_UNIT)) {
				throw new TypeException("Invalid type of body expression in while loop. UNIT type required.");
			}

			executeStack.pop();

		}

		ASTValue unitValue = new ASTValue(0);
		unitValue.setType(SimPLTypes.TYPE_UNIT);
		executeStack.push(unitValue);
		return SimPLTypes.TYPE_UNIT;
	}

	@Override
	public Object visit(ASTFirstOfPairExpression node, Object data)
			throws TypeException, InterpretException {
		node.jjtGetChild(0).jjtAccept(this, data);
		SimpleNode sn = executeStack.pop();
		ASTValue snValue = null;

		if (sn instanceof ASTVariable) {

			String snName = ((ASTVariable) sn).getName();
			if (envStack.containsKey(snName) && (envStack.get(snName) instanceof ASTValue)) {
				snValue = ((ASTValue) envStack.get(snName)).deepCopy();
			} else if (envStack.containsKey(snName)){
				throw new TypeException("Error type of " 
					    + snName + ". Expecting value instead of function");
			}else {
				throw new InterpretException("Undefined identifier " 
					    + snName + ".");
			}
		} else if (sn instanceof ASTValue) {
			snValue = ((ASTValue) sn).deepCopy();
		} else {
			throw new TypeException("fst-expression requires a value or variable.");
		}

		if (SimPLTypes.TYPE_PAIR != snValue.getType()) {
			throw new TypeException("fst-expression requires pair type.");
		}

		SimpleNode firstValue =  (SimpleNode)snValue.getPairValue().get(0);

		executeStack.push(firstValue);
		
		if(firstValue instanceof ASTValue)
		{
			return ((ASTValue) firstValue).getType();
		}else if(firstValue instanceof ASTAnonymousFunctionNode)
		{
			return SimPLTypes.TYPE_FUNCTION;
		}else{
			throw new TypeException("Invalid pair value.");
		}

	}

	@Override
	public Object visit(ASTSecondOfPairExpression node, Object data)
			throws TypeException, InterpretException {
		node.jjtGetChild(0).jjtAccept(this, data);
		SimpleNode sn = executeStack.pop();
		ASTValue snValue = null;

		if (sn instanceof ASTVariable) {

			String snName = ((ASTVariable) sn).getName();
			if (envStack.containsKey(snName) && (envStack.get(snName) instanceof ASTValue)) {
				snValue = ((ASTValue) envStack.get(snName)).deepCopy();
			} else if (envStack.containsKey(snName)){
				throw new TypeException("Error type of " 
					    + snName + ". Expecting value instead of function");
			} else {
				throw new InterpretException("Undefined identifier " 
					    + snName + ".");
			}
		} else if (sn instanceof ASTValue) {
			snValue = ((ASTValue) sn).deepCopy();
		} else {
			throw new TypeException("snd-expression requires a value or variable.");
		}

		if (SimPLTypes.TYPE_PAIR != snValue.getType()) {
			throw new TypeException("snd-expression requires pair type.");
		}

		SimpleNode secondValue =  (SimpleNode)snValue.getPairValue().get(1);

		executeStack.push(secondValue);
		
		if(secondValue instanceof ASTValue)
		{
			return ((ASTValue) secondValue).getType();
		}else if(secondValue instanceof ASTAnonymousFunctionNode)
		{
			return SimPLTypes.TYPE_FUNCTION;
		}else{
			throw new TypeException("Invalid pair value.");
		}
	}

	@Override
	public Object visit(ASTHeadOfListExpression node, Object data)
			throws TypeException, InterpretException {
		node.jjtGetChild(0).jjtAccept(this, data);
		SimpleNode sn = executeStack.pop();
		ASTValue snValue = null;

		if (sn instanceof ASTVariable) {

			String snName = ((ASTVariable) sn).getName();
			
			if (envStack.containsKey(snName) && (envStack.get(snName) instanceof ASTValue)) {
				snValue = ((ASTValue) envStack.get(snName)).deepCopy();
			} else if (envStack.containsKey(snName)){
				throw new TypeException("Error type of " 
					    + snName + ". Expecting value instead of function");
			}else {
				throw new InterpretException("Undefined identifier " 
					    + snName + ".");
			}

		} else if (sn instanceof ASTValue) {
			snValue = ((ASTValue) sn).deepCopy();
		} else {
			throw new TypeException("head-expression requires a value or variable.");
		}

		if (SimPLTypes.TYPE_LIST != snValue.getType()) {
			throw new TypeException("head-expression requires list type.");
		}
		SimpleNode value = null;
		if (snValue.getListValue().size() > 0) {
			value = (SimpleNode)snValue.getListValue().get(0);
		} else {
			value = new ASTValue(0);
			
			((ASTValue)value).setType(SimPLTypes.TYPE_LIST);
			((ASTValue)value).setListValue(snValue.getListValue());
		}

		executeStack.push(value);
		
		
		if(value instanceof ASTValue)
		{
			return ((ASTValue)value).getType();
		}
		else
		{
			return SimPLTypes.TYPE_FUNCTION;
		}
	}

	@Override
	public Object visit(ASTTailOfListExpression node, Object data)
			throws TypeException, InterpretException {
		node.jjtGetChild(0).jjtAccept(this, data);
		SimpleNode sn = executeStack.pop();
		ASTValue snValue = null;

		if (sn instanceof ASTVariable) {

			String snName = ((ASTVariable) sn).getName();
			if (envStack.containsKey(snName) && (envStack.get(snName) instanceof ASTValue)) {
				snValue = ((ASTValue) envStack.get(snName)).deepCopy();
			} else if (envStack.containsKey(snName)){
				throw new TypeException("Error type of " 
					    + snName + ". Expecting value instead of function");
			}else {
				throw new InterpretException("Undefined identifier " 
					    + snName + ".");
			}
		} else if (sn instanceof ASTValue) {
			snValue = ((ASTValue) sn).deepCopy();
		} else {
			throw new TypeException("Tail-expression requires a value or variable.");
		}

		if (SimPLTypes.TYPE_LIST != snValue.getType()) {
			throw new TypeException("Tail-expression requires list type.");
		}

//		List<Object> listValue = snValue.getListValue();
		
//		List<Object> subValue = new LinkedList<Object>();
//		for(int i = 1; i < listValue.size(); ++i)
//		{
//			subValue.add(listValue.get(i));
//		}
		
//		ASTValue newListValue = new ASTValue(0);
//		newListValue.setType(SimPLTypes.TYPE_LIST);
//		newListValue.setListValue(subValue);
		ASTValue newListValue = snValue.deepCopy();
		newListValue.getListValue().remove(0);
		executeStack.push(newListValue);

		return SimPLTypes.TYPE_LIST;
	}

	@Override
	public Object visit(ASTAssignNode node, Object data) throws TypeException, InterpretException {
		
		// interpret identifier
		node.jjtGetChild(0).jjtAccept(this, new Boolean(true));

		
		SimpleNode varNode = executeStack.pop();
		if(!(varNode instanceof ASTVariable))
		{
			throw new TypeException("Invalid identifier type" +
					" of left side in assign-expression.");
		}
		else if(envStack.get(((ASTVariable)varNode).getName()) == null)
		{
			throw new InterpretException("Undefined identifier " + ((ASTVariable)varNode).getName() + ".");
		}
		ASTVariable var = (ASTVariable) varNode;

		// interpret assignment
		node.jjtGetChild(1).jjtAccept(this, data);

		SimpleNode sn = executeStack.pop();

		if (sn instanceof ASTValue) {
			
			if (envStack.containsKey(var.getName())
					&& (!(envStack.get(var.getName()) instanceof ASTValue))) {
				throw new TypeException("Type incompatible in assign expression.");
			}

			envStack.put(var.getName(), sn);
		} else if (sn instanceof ASTVariable) {
			ASTVariable definedVar = (ASTVariable) sn;

			if (!envStack.containsKey(definedVar.getName())) {
				throw new InterpretException("Undefined identifier " 
					    + definedVar.getName() + ".");
			}

			envStack.put(var.getName(), envStack.get(definedVar.getName()));
		} else if (sn instanceof ASTAnonymousFunctionNode) {

			envStack.put(var.getName(), sn);
		} else {
			throw new TypeException("Invalid expression type" +
					" of right side in assign-expression.");
		}
		ASTValue unitValue = new ASTValue(0);
		unitValue.setType(SimPLTypes.TYPE_UNIT);
		executeStack.push(unitValue);

		return SimPLTypes.TYPE_UNIT;
	}

}
