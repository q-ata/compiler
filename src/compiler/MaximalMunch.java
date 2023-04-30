package compiler;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class MaximalMunch {
	
	private final DFA dfa;
	private String input;
	private int pos;
	private String state;
	private boolean init = false;
	
	public MaximalMunch(DFA dfa) {
		this.dfa = dfa;
	}
	
	public void initialize(String input) {
		this.input = input;
		this.pos = 0;
		this.state = dfa.initial.label;
		this.init = true;
	}
	
	public Token nextToken() throws ParseError {
		if (!init) {
			throw new ParseError("Maximal Muncher is not initialized.");
		}
		if (pos == input.length()) return null;
		Stack<String> states = new Stack<>();
		
		int oldPos = pos;
		String nextState;
		try {
			while (pos < input.length() && (nextState = dfa.validTransition(state, input.charAt(pos))) != null) {
				states.push(nextState);
				state = nextState;
				++pos;
			}
		}
		catch (DFAError e) {
			e.printStackTrace();
			return null;
		}
		
		while (!states.empty() && !dfa.stateMap.get(states.peek()).accepting) {
			states.pop();
			--pos;
		}
		
		state = dfa.initial.label;
		
		if (states.empty()) throw new ParseError(constructError(oldPos));
		return new Token(states.peek(), input.substring(oldPos, pos), oldPos);
	}
	
	public int getPos() {
		if (!init) {
			return -1;
		}
		return pos;
	}
	
	public List<Token> tokenizeAll() throws ParseError {
		if (!init) {
			throw new ParseError("Maximal Muncher is not initialized.");
		}
		List<Token> tokens = new ArrayList<>();
		Token t;
		while ((t = nextToken()) != null) {
			tokens.add(t);
		}
		if (pos < input.length()) {
			throw new ParseError(constructError(pos));
		}
		
		return tokens;
	}
	
	public String constructError(int pos) {
		StringBuilder sb = new StringBuilder("Failed to parse token at position: " + pos + "\n");
		sb.append(input);
		sb.append("\n");
		for (int i = 0; i < pos; ++i) sb.append(" ");
		sb.append("^\n");
		return sb.toString();
	}

}
