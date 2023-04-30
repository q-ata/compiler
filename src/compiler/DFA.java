package compiler;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class DFA {
	
	public final Alphabet alpha;
	
	public final Map<String, State> stateMap;
	public final Map<String, List<Transition>> transitionMap;
	
	public final State initial;
	public final Set<String> accepting;
	
	public DFA(List<State> states, List<Transition> transitions, Alphabet alpha) throws DFAError {
		this.alpha = alpha;
		
		// find and set the initial state
		State initial = null;
		boolean foundInitial = false;
		for (State s : states) {
			if (s.initial && !foundInitial) {
				initial = s;
				foundInitial = true;
			}
			else if (s.initial) {
				throw new DFAError("Multiple initial states: (" + initial.label + ") and (" + s.label + ").");
			}
		}
		this.initial = initial;
		
		// find and add accepting states
		Set<String> accepting = new HashSet<>();
		for (State s : states) {
			if (s.accepting) accepting.add(s.label);
		}
		this.accepting = Collections.unmodifiableSet(accepting);
		
		// populate stateMap
		Map<String, State> stateMap = new HashMap<>();
		for (State s : states) {
			if (stateMap.containsKey(s.label)) {
				throw new DFAError("Duplicate state label: " + s.label);
			}
			stateMap.put(s.label, s);
		}
		this.stateMap = Collections.unmodifiableMap(stateMap);
		
		// populate transitionMap
		Map<String, List<Transition>> transitionMap = new HashMap<>();
		for (Transition t : transitions) {
			if (!this.stateMap.containsKey(t.from)) {
				throw new DFAError("Invalid from state in transition: " + t.from);
			}
			if (!this.stateMap.containsKey(t.to)) {
				throw new DFAError("Invalid to state in transition: " + t.to);
			}
			for (char c : t.transitions) {
				if (!this.alpha.inAlphabet(c)) {
					throw new DFAError("Character in transition is not in alphabet: " + c);
				}
			}
			
			if (!transitionMap.containsKey(t.from)) {
				transitionMap.put(t.from, new ArrayList<>());
			}
			transitionMap.get(t.from).add(t);
		}
		
		for (String k : transitionMap.keySet()) {
			transitionMap.put(k, Collections.unmodifiableList(transitionMap.get(k)));
			
			Map<Character, Integer> occurrences = new HashMap<>();
			for (Transition t : transitionMap.get(k)) {
				for (char c : t.transitions) {
					if (!occurrences.containsKey(c)) occurrences.put(c, 0);
					occurrences.put(c, occurrences.get(c) + 1);
				}
			}
			
			for (char ke : occurrences.keySet()) {
				if (occurrences.get(ke) > 1) throw new DFAError("Duplicate edge label, automata is not deterministic for state: (" +
					k + ") label: " + ke);
			}
		}
		
		this.transitionMap = Collections.unmodifiableMap(transitionMap);
	}
	
	public String validTransition(String cur, char transition) throws DFAError {
		if (!stateMap.containsKey(cur)) throw new DFAError("Invalid state specified: " + cur);
		if (!transitionMap.containsKey(cur)) return null;
		
		List<Transition> transitions = transitionMap.get(cur);
		for (Transition t : transitions) {
			if (t.transitions.contains(transition)) return t.to;
		}
		return null;
	}

}
