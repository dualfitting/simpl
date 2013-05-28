package syntax;

public class Tail extends Expression{
	Expression e;	
	
	public String toString(){
		return "tail " + e.toString();
	}
}