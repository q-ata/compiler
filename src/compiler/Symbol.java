package compiler;

public class Symbol {
	
	public final String type;
	public final boolean terminal;
	
	public Symbol(String type, boolean terminal) {
		this.type = type;
		this.terminal = terminal;
	}
	
	@Override
	public boolean equals(Object o) {
		return o != null && o instanceof Symbol && ((Symbol) o).type == this.type;
	}

}
