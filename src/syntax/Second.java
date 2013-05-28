package syntax;

public class Second extends Expression{
	Expression e;
	
	public String toString(){
		return "snd " + e.toString();
	}
}