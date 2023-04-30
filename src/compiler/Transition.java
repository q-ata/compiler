package compiler;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Transition {
	
	public final String from;
	public final String to;
	public final Set<Character> transitions;
	
	public Transition(String from, String to, String transitions) {
		this.from = from;
		this.to = to;
		Set<Character> theTransitions = new HashSet<>();
		for (char c : transitions.toCharArray()) theTransitions.add(c);
		this.transitions = Collections.unmodifiableSet(theTransitions);
	}

}
