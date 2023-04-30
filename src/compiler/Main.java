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
		states.add(new State(true, false, SymbolNames.EQUALS));
		states.add(new State(true, false, SymbolNames.TIMES));
		states.add(new State(true, false, SymbolNames.PLUS));
		states.add(new State(true, false, SymbolNames.MINUS));
		states.add(new State(true, false, SymbolNames.LITERAL));
		states.add(new State(true, false, SymbolNames.VARNAME));
		states.add(new State(false, false, "v"));
		states.add(new State(false, false, "va"));
		states.add(new State(true, false, SymbolNames.VAR));
		states.add(new State(true, false, "whitespace"));
		
		List<Transition> transitions =  new ArrayList<>();
		transitions.add(new Transition("initial", SymbolNames.EQUALS, "="));
		transitions.add(new Transition("initial", SymbolNames.TIMES, "*"));
		transitions.add(new Transition("initial", SymbolNames.PLUS, "+"));
		transitions.add(new Transition("initial", SymbolNames.MINUS, "-"));
		transitions.add(new Transition("initial", SymbolNames.LITERAL, "123456789"));
		transitions.add(new Transition(SymbolNames.LITERAL, SymbolNames.LITERAL, "0123456789"));
		transitions.add(new Transition("initial", "v", "v"));
		transitions.add(new Transition("v", "va", "a"));
		transitions.add(new Transition("va", SymbolNames.VAR, "r"));
		transitions.add(new Transition("initial", SymbolNames.VARNAME, "abcdefghijklmnopqrstuwxyz"));
		transitions.add(new Transition("v", SymbolNames.VARNAME, "bcdefghijklmnopqrstuvwxyz0123456789"));
		transitions.add(new Transition("va", SymbolNames.VARNAME, "abcdefghijklmnopqstuvwxyz0123456789"));
		transitions.add(new Transition(SymbolNames.VAR, SymbolNames.VARNAME, "abcdefghijklmnopqrstuvwxyz0123456789"));
		transitions.add(new Transition("initial", "whitespace", " \r\n\t"));
		transitions.add(new Transition("whitespace", "whitespace", " \r\n\t"));
		
		// symbols should use shared constants to be consistent with the evaluator
		Symbol entry = new Symbol(SymbolNames.ENTRY, false);
		Symbol stmt = new Symbol(SymbolNames.STMT, false);
		Symbol stmts = new Symbol(SymbolNames.STMTS, false);
		Symbol vardef = new Symbol(SymbolNames.VARDEF, false);
		Symbol varasn = new Symbol(SymbolNames.VARASN, false);
		Symbol add = new Symbol(SymbolNames.ADD, false);
		Symbol sub = new Symbol(SymbolNames.SUB, false);
		Symbol mult = new Symbol(SymbolNames.MULT, false);
		Symbol varname = new Symbol(SymbolNames.VARNAME, true);
		Symbol literal = new Symbol(SymbolNames.LITERAL, true);
		Symbol equals = new Symbol(SymbolNames.EQUALS, true);
		Symbol plus = new Symbol(SymbolNames.PLUS, true);
		Symbol minus = new Symbol(SymbolNames.MINUS, true);
		Symbol times = new Symbol(SymbolNames.TIMES, true);
		Symbol var = new Symbol(SymbolNames.VAR, true);
		
		List<Rule> rules = new ArrayList<>();
		rules.add(new Rule("entry", entry, stmts));
		rules.add(new Rule("stmt1", stmt, vardef));
		rules.add(new Rule("stmt2", stmt, varasn));
		rules.add(new Rule("stmt3", stmt, add));
		rules.add(new Rule("stmt4", stmt, sub));
		rules.add(new Rule("stmt5", stmt, mult));
		rules.add(new Rule("stmts1", stmts, stmt, stmts));
		rules.add(new Rule("stmts2", stmts, stmt));
		rules.add(new Rule("vardef", vardef, var, varname, equals, literal));
		rules.add(new Rule("varasn", varasn, varname, equals, literal));
		rules.add(new Rule("add", add, varname, plus, varname));
		rules.add(new Rule("sub", sub, varname, minus, varname));
		rules.add(new Rule("mult", mult, varname, times, varname));
		
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
