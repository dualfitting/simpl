package syntax;

public class IntValue extends Value{
	boolean isUndef;
	int value;

	public String toString(){
		if(isUndef)
			return "undef";
		else
			return String.valueOf(value);
	}
}