package compiler;

public class State {
	
	public final boolean accepting;
	public final boolean initial;
	public final String label;
	
	public State(boolean accepting, boolean initial) {
		this.accepting = accepting;
		this.initial = initial;
		this.label = super.toString();
	}
	
	public State(boolean accepting, boolean initial, String label) {
		this.accepting = accepting;
		this.initial = initial;
		this.label = label;
	}
	
	@Override
	public String toString() {
		return this.label;
	}

}
