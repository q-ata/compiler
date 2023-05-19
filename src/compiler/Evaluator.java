package compiler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Evaluator {
	
	private Map<String, Integer> vars = new HashMap<>();
	
	public void eval(Node root) throws RuntimeError {
		String type = root.symbol.type;
		List<Node> children = root.getChildren();
	}

}
