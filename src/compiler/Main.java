package compiler;

import java.util.ArrayList;
import java.util.List;

public class Main {

	public static void main(String[] args) {
		
		Alphabet alpha = new Alphabet("abcdefghijklmnopqrstuvwxyz0123456789 \r\n\t+-=*");
		
		// all accepting states with the exception of whitespace should use a shared constant
		// as its name since it has to be consistent with the corresponding symbol defined later.
		List<State> states = new ArrayList<>();
		states.add(new State(false, true, "initial"));
		
		List<Transition> transitions =  new ArrayList<>();
		transitions.add(new Transition("whitespace", "whitespace", " \r\n\t"));
		
		// symbols should use shared constants to be consistent with the evaluator
		Symbol entry = null;
		
		List<Rule> rules = new ArrayList<>();
		
		// expected out: 323 120
		String input = "var x = 123 var y = 200 x + y y = 12 var z = 10 y * z";
		
		DFA dfa;
		try {
			dfa = new DFA(states, transitions, alpha);
		}
		catch (DFAError e) {
			e.printStackTrace();
			return;
		}
		
		MaximalMunch tokenizer;
		List<Token> tokens;
		try {
			tokenizer = new MaximalMunch(dfa);
			tokenizer.initialize(input);
			tokens = tokenizer.tokenizeAll();
		}
		catch (ParseError e) {
			e.printStackTrace();
			return;
		}
		
		List<Token> out = new ArrayList<>();
		for (Token t : tokens) {
			if (t.type != "whitespace") out.add(t);
		}
		tokens = out;
		
		CYKParser parser = new CYKParser(rules, rules.get(0), false);
		Node root;
		try {
			List<Node> trees = parser.parse(tokens, input);
			root = new Node(entry, null);
			root.addChildren(trees);
		}
		catch (ParseError e) {
			e.printStackTrace();
			return;
		}
		
		Evaluator evaler = new Evaluator();
		try {
			evaler.eval(root);
		}
		catch (RuntimeError e) {
			e.printStackTrace();
		}
	}

}
