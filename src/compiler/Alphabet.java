package compiler;

import java.util.HashSet;
import java.util.Set;

public class Alphabet {
	
	private final Set<Character> alpha;
	
	public Alphabet(String alpha) {
		this.alpha = new HashSet<>();
		for (char c : alpha.toCharArray()) this.alpha.add(c);
	}
	
	public boolean inAlphabet(char c) {
		return alpha.contains(c);
	}
	
	public boolean inAlphabet(String s) {
		for (char c : s.toCharArray()) {
			if (!inAlphabet(c)) return false;
		}
		return true;
	}

}
