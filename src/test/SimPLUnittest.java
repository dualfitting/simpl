package test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import javacc.ASTAnonymousFunctionNode;
import javacc.ASTValue;
import javacc.ParseException;
import javacc.SimPLParser;
import javacc.SimpleNode;
import semantics.InterpretVisitor;
import semantics.SimPLTypes;
import utils.InterpretException;
import utils.TypeException;

public class SimPLUnittest {

	public static boolean testUnit(String filename, Object expectedResult) {
		SimPLParser parser = new SimPLParser(System.in);
		String simplProgram = "";
		try {

			BufferedReader br = new BufferedReader(new FileReader(filename));
			while (true) {
				String line = br.readLine();
				if (null == line) {
					break;
				}

				simplProgram += line + " \n";
			}

			simplProgram = simplProgram.replace('$', ' ');

			parser.ReInit(new ByteArrayInputStream(simplProgram.getBytes()));

		} catch (FileNotFoundException e) {
			System.out.println("File " + filename + " not found.");
			return false;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.out.println("Error reading " + filename);
			return false;
		} catch (Error e) {
			System.out.print(e.getMessage());
		}

		try {
			parser.ReInit(new ByteArrayInputStream(simplProgram.getBytes()));
			SimpleNode astTree = parser.Start();

			SimpleNode sn = (SimpleNode) astTree.jjtAccept(
					new InterpretVisitor(), null);
			ASTValue v;
			ASTAnonymousFunctionNode f;
			if (sn instanceof ASTValue) {
				v = (ASTValue) sn;
				switch (v.getType()) {
				case SimPLTypes.TYPE_BOOLEAN:
					if (expectedResult instanceof Boolean
							&& v.getBoolValue() == ((Boolean) expectedResult)
									.booleanValue()) {
						return true;
					} else {
						return false;
					}
				case SimPLTypes.TYPE_INTEGER:
					if (expectedResult instanceof Integer
							&& v.getIntValue() == ((Integer) expectedResult)
									.intValue()) {
						return true;
					} else {
						return false;
					}
				case SimPLTypes.TYPE_LIST:
					List<Object> vlist = v.getListValue();
					List<Object> elist = (List) expectedResult;
					if (vlist.size() == 0 && elist.size() == 0)
						return true;
					else if (vlist.get(0) instanceof ASTValue) {

						for (int i = 0; i < vlist.size(); ++i) {
							switch (((ASTValue) vlist.get(i)).getType()) {
							case SimPLTypes.TYPE_INTEGER:
								if (((ASTValue) vlist.get(i)).getIntValue() != ((Integer) elist
										.get(i)).intValue()) {
									return false;
								}
								break;
							case SimPLTypes.TYPE_BOOLEAN:
								if (((ASTValue) vlist.get(i)).getBoolValue() != ((Boolean) elist
										.get(i)).booleanValue()) {
									return false;
								}
								return true;
							default:
								// FIXME: Other type(eg. list of
								// pair/list/function is not supported
								return true;

							}
						}
					} else {
						// FIXME: function comparsion is not supported
						return true;
					}

					// System.out.println(v.getListValue());
					break;
				case SimPLTypes.TYPE_PAIR:
					return true;
				}
			} else if (sn instanceof ASTAnonymousFunctionNode) {
				// FIXME: function comparison is not supported
				return true;
			}

			// System.out.println("SimPL Program interpreted successfully.");

		} catch (ParseException e) {

			System.out.println(e.getMessage());
			// System.out.println("Encountered errors during parse.");
			return false;
		} catch (TypeException e) {
			System.out.println(e.getMessage());
			// System.out.println("Encountered errors during type checking.");
			return false;
		} catch (InterpretException e) {
			System.out.println(e.getMessage());
			// System.out.println("Encountered errors during interpreting.");
			return false;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return false;
		} catch (Error e) {
			System.out.println(e.getMessage());
			return false;
		}
		return true;
	}

	public static void correctTestBatch() {
		HashMap<String, Object> testSet = new HashMap<String, Object>();
		
		testSet.put("testcase/correctcases/testcase09.spl", new Integer(24));
		testSet.put("testcase/correctcases/testcase10.spl", new Integer(1029));
		testSet.put("testcase/correctcases/testcase12.spl", new Integer(9));
		testSet.put("testcase/correctcases/application.spl", new Integer(4));
		testSet.put("testcase/correctcases/binary_operation.spl", new Boolean(false));
		testSet.put("testcase/correctcases/fst.spl", new Integer(1));
		testSet.put("testcase/correctcases/if.spl", new Integer(4));
		testSet.put("testcase/correctcases/let.spl", null);
		testSet.put("testcase/correctcases/let3.spl", new Integer(15));
		testSet.put("testcase/correctcases/length.spl", new Integer(3));
		testSet.put("testcase/correctcases/fun_fun.spl", new Integer(211));
		List<Integer> takeList = new LinkedList<Integer>();
		takeList.add(234);
		takeList.add(352);
		testSet.put("testcase/correctcases/take.spl", takeList);
		List<Integer> concatList = new LinkedList<Integer>();
		concatList.add(1);
		concatList.add(5);
		concatList.add(10);
		testSet.put("testcase/correctcases/concat.spl", concatList);
		
		LinkedList<Integer> list = new LinkedList<Integer>();
		list.add(new Integer(4));
		list.add(new Integer(5));
		testSet.put("testcase/correctcases/list.spl", list);
		testSet.put("testcase/correctcases/pair.spl", null);
		testSet.put("testcase/correctcases/unary_operation.spl", new Boolean(false));
		testSet.put("testcase/correctcases/fst.spl", new Integer(1));
		testSet.put("testcase/correctcases/value.spl", new Integer(567));
		LinkedList<Integer> whilelist = new LinkedList<Integer>();
		whilelist.add(new Integer(0));
		whilelist.add(new Integer(1));
		whilelist.add(new Integer(2));
		whilelist.add(new Integer(3));
		whilelist.add(new Integer(4));
		testSet.put("testcase/correctcases/while.spl", whilelist);
		
		LinkedList<Integer> reverseList = new LinkedList<Integer>();
		reverseList.add(new Integer(3));
		reverseList.add(new Integer(2));
		reverseList.add(new Integer(1));
		testSet.put("testcase/correctcases/reverse.spl", reverseList);
		int sum = testSet.size();
		int passed = 0;

		int index = 0;
		Iterator<Entry<String, Object>> iter = testSet.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<String, Object> entry = iter.next();
			String key = entry.getKey();
			Object value = entry.getValue();

			boolean match = testUnit(key, value);
			++index;
			if(match){
				++passed;
				System.out.println("Test " + key 
						+ ": passed. (" + index + "/" + sum + ")");
			}
			else
			{

				System.out.println("Test " + key  
						+ ": failed. (" + index + "/" + sum + ")");
			}
		}
		
		System.out.println("Test finished, passed " + passed + " of " + sum);
	}
	
	public static void wrongTestBatch() {
		HashMap<String, Object> testSet = new HashMap<String, Object>();
		
		testSet.put("testcase/wrongcases/testcase01.spl", null);
		testSet.put("testcase/wrongcases/testcase02.spl", null);
		testSet.put("testcase/wrongcases/testcase03.spl", null);
		testSet.put("testcase/wrongcases/testcase04.spl", null);
		testSet.put("testcase/wrongcases/testcase05.spl", null);
		testSet.put("testcase/wrongcases/testcase06.spl", null);
		testSet.put("testcase/wrongcases/testcase07.spl", null);
		testSet.put("testcase/wrongcases/testcase08.spl", null);
		int sum = testSet.size();
		int passed = 0;

		int index = 0;
		Iterator<Entry<String, Object>> iter = testSet.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<String, Object> entry = iter.next();
			String key = entry.getKey();
			Object value = entry.getValue();

			boolean match = testUnit(key, value);
			++index;
			if(match){
				++passed;
				System.out.println("Test " + key 
						+ ": passed. (" + index + "/" + sum + ")");
			}
			else
			{

				System.out.println("Test " + key  
						+ ": failed. (" + index + "/" + sum + ")");
			}
		}
		
		System.out.println("Wrong Test finished, passed " + passed + " of " + sum);
	}
	
	public static void main(String args[]){
		correctTestBatch();
		//wrongTestBatch();
	}
}
