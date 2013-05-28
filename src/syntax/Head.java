package syntax;

public class Head extends Expression{
	Expression e;
	
	public String toString(){
		return "head " + e.toString();
	}
}