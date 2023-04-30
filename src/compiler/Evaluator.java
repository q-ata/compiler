package compiler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Evaluator {
	
//	public static final String ENTRY = "entry";
//	public static final String STMTS = "stmts";
//	public static final String STMT = "stmt";
//	public static final String VARDEF = "vardef";
//	public static final String VARASN = "varasn";
//	public static final String ADD = "add";
//	public static final String SUB = "sub";
//	public static final String MULT = "mult";
//	public static final String VARNAME = "varname";
//	public static final String LITERAL = "literal";
//	public static final String EQUALS = "equals";
//	public static final String PLUS = "plus";
//	public static final String MINUS = "minus";
//	public static final String TIMES = "times";
//	public static final String VAR = "var";
	
	private Map<String, Integer> vars = new HashMap<>();
	
	public void eval(Node root) throws RuntimeError {
		String type = root.symbol.type;
		List<Node> children = root.getChildren();
		switch (type) {
		case SymbolNames.ENTRY:
		case SymbolNames.STMT:
		case SymbolNames.STMTS:
			for (Node c : root.getChildren()) eval(c);
			break;
		case "vardef": {
			String varname = children.get(1).token.content;
			if (vars.containsKey(varname)) throw new RuntimeError("Redefinition of variable: " + varname);
			String value = children.get(3).token.content;
			int res;
			try {
				res = Integer.parseInt(value);
			}
			catch (Exception e) {
				throw new RuntimeError("Invalid integer value: " + value);
			}
			vars.put(varname, res);
			break;
		}
		case "varasn": {
			String varname = children.get(0).token.content;
			if (!vars.containsKey(varname)) throw new RuntimeError("Variable was not declared: " + varname);
			String value = children.get(2).token.content;
			int res;
			try {
				res = Integer.parseInt(value);
			}
			catch (Exception e) {
				throw new RuntimeError("Invalid integer value: " + value);
			}
			vars.put(varname, res);
			break;
		}
		case "add": {
			String op1 = children.get(0).token.content;
			if (!vars.containsKey(op1)) throw new RuntimeError("Operand was not defined: " + op1);
			String op2 = children.get(2).token.content;
			if (!vars.containsKey(op2)) throw new RuntimeError("Operand was not defined: " + op2);
			System.out.println(vars.get(op1) + vars.get(op2));
			break;
		}
		case "sub": {
			String op1 = children.get(0).token.content;
			if (!vars.containsKey(op1)) throw new RuntimeError("Operand was not defined: " + op1);
			String op2 = children.get(2).token.content;
			if (!vars.containsKey(op2)) throw new RuntimeError("Operand was not defined: " + op2);
			System.out.println(vars.get(op1) - vars.get(op2));
			break;
		}
		case "mult": {
			String op1 = children.get(0).token.content;
			if (!vars.containsKey(op1)) throw new RuntimeError("Operand was not defined: " + op1);
			String op2 = children.get(2).token.content;
			if (!vars.containsKey(op2)) throw new RuntimeError("Operand was not defined: " + op2);
			System.out.println(vars.get(op1) * vars.get(op2));
			break;
		}
		}
	}

}
