package compiler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class CYKParser {
	
	private final Map<Symbol, List<Rule>> rules;
	private final Rule source;
	private final boolean debug;
	
	public CYKParser(List<Rule> rulesList, Rule source, boolean debug) {
		this.source = source;
		this.debug = debug;
		Map<Symbol, List<Rule>> rules = new HashMap<>();
		for (Rule r : rulesList) {
			if (!rules.containsKey(r.source)) rules.put(r.source, new ArrayList<>());
			rules.get(r.source).add(r);
		}
		for (Symbol nts : rules.keySet()) {
			rules.put(nts, Collections.unmodifiableList(rules.get(nts)));
		}
		this.rules = Collections.unmodifiableMap(rules);
	}
	
	public CYKParser(List<Rule> rulesList, Rule source) {
		this(rulesList, source, false);
	}
	
	private class CacheEntry {
		private final List<Symbol> syms;
		private final List<Token> tokens;
		
		public CacheEntry(List<Symbol> syms, List<Token> tokens) {
			this.syms = syms;
			this.tokens = tokens;
		}
		
		@Override
		public boolean equals(Object o) {
			if (o == null || !(o instanceof CacheEntry)) return false;
			CacheEntry other = (CacheEntry) o;
			if (this.syms.size() != other.syms.size() || this.tokens.size() != other.tokens.size()) return false;
			for (int i = 0; i < this.syms.size(); ++i) {
				if (!this.syms.get(i).equals(other)) return false;
			}
			for (int i = 0; i < this.tokens.size(); ++i) {
				if (!this.tokens.get(i).equals(other)) return false;
			}
			
			return true;
		}
	}
	
	private Map<CacheEntry, List<Node>> cache;
	private Token errorToken;
	
	private List<Node> parse(List<Symbol> syms, List<Token> tokens, int idt) throws ParseError {
		CacheEntry entry = new CacheEntry(syms, tokens);
		if (debug) pr(syms, tokens, idt);
		if (cache.containsKey(entry)) return cache.get(entry);
		// case 1: tokens empty
		if (tokens.isEmpty()) {
			if (syms.isEmpty()) {
				List<Node> ret = new ArrayList<>();
				cache.put(entry, ret);
				return ret;
			}
			return null;
		}
		// case 2: symbols starts with a terminal
		else if (!syms.isEmpty() && syms.get(0).terminal) {
			Symbol first = syms.get(0);
			if (first.type == tokens.get(0).type) {
				List<Node> children = parse(syms.subList(1, syms.size()), tokens.subList(1, tokens.size()), idt + 2);
				if (children == null) return null;
				List<Node> ret = new ArrayList<>();
				ret.add(new Node(first, tokens.get(0)));
				ret.addAll(children);
				cache.put(entry, ret);
				return ret;
			}
			if (errorToken == null || tokens.get(0).pos > errorToken.pos) {
				errorToken = tokens.get(0);
			}
			return null;
		}
		// case 3: symbols is a single nonterminal
		else if (syms.size() == 1) {
			if (!this.rules.containsKey(syms.get(0))) return null;
			List<Rule> rules = this.rules.get(syms.get(0));
			for (Rule r : rules) {
				List<Node> children = parse(r.dest, tokens, idt + 2);
				if (children == null) continue;
				List<Node> ret = new ArrayList<>();
				ret.add(new Node(syms.get(0)));
				ret.get(0).addChildren(children);
				cache.put(entry, ret);
				return ret;
			}
			if (errorToken == null || tokens.get(0).pos > errorToken.pos) {
				errorToken = tokens.get(0);
			}
			return null;
		}
		// case 4: symbols is a nonterminal followed by some symbols
		else if (!syms.isEmpty() && !syms.get(0).terminal) {
			for (int i = 0; i < tokens.size() + 1; ++i) {
				List<Token> l1 = tokens.subList(0, i);
				List<Token> l2 = tokens.subList(i, tokens.size());
				List<Node> c1 = parse(syms.subList(0, 1), l1, idt + 2);
				if (c1 == null) continue;
				List<Node> c2 = parse(syms.subList(1, syms.size()), l2, idt + 2);
				if (c2 == null) continue;
				
				List<Node> ret = new ArrayList<>();
				ret.add(c1.get(0));
				ret.addAll(c2);
				cache.put(entry, ret);
				return ret;
			}
			if (errorToken == null || tokens.get(0).pos > errorToken.pos) {
				errorToken = tokens.get(0);
			}
			return null;
		}
		else {
			if (errorToken == null || tokens.get(0).pos > errorToken.pos) {
				errorToken = tokens.get(0);
			}
			throw new ParseError("");
		}
	}
	
	public List<Node> parse(List<Token> tokens, String input) throws ParseError {
		cache = new HashMap<>();
		errorToken = null;
		List<Symbol> src = new ArrayList<>();
		src.addAll(source.dest);
		try {
			List<Node> res = parse(src, tokens, 0);
			if (res == null) throw new ParseError("");
			return res;
		}
		catch (ParseError e) {
			StringBuilder sb = new StringBuilder("Unexpected token: (" + errorToken.content + ") at position: " + errorToken.pos + "\n");
			sb.append(input.substring(0, errorToken.pos + errorToken.content.length()));
			sb.append("\n");
			for (int i = 0; i < errorToken.pos; ++i) sb.append(" ");
			sb.append("^");
			throw new ParseError(sb.toString());
		}
	}
	
	private void pr(List<Symbol> syms, List<Token> toks, int idt) {
		for (int i = 0; i < idt; ++i) System.out.print(" ");
		for (Symbol s : syms) System.out.print(s.type + ", ");
		System.out.print("| ");
		for (Token t : toks) System.out.print(t.content + ", ");
		System.out.println();
	}

}
