package compiler;

import java.util.Collections;
import java.util.ArrayList;
import java.util.List;

public class Node {
	
	public final Symbol symbol;
	private List<Node> children = new ArrayList<>();
	public final Token token;
	
	public Node(Symbol symbol, Token token) {
		this.symbol = symbol;
		this.token = token;
	}
	
	public Node(Symbol symbol) {
		this.symbol = symbol;
		this.token = null;
	}
	
	public List<Node> getChildren() {
		return Collections.unmodifiableList(children);
	}
	
	public void addChild(Node child) {
		children.add(child);
	}
	
	public void addChildren(List<Node> children) {
		this.children.addAll(children);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(symbol.type);
		if (this.token != null) sb.append(" | " + token.content);
		sb.append("\n");
		for (Node c : children) sb.append(c.print(2));
		return sb.toString();
	}
	
	public String print(int indent) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < indent; ++i) sb.append(' ');
		sb.append(symbol.type);
		if (this.token != null) sb.append(" | " + token.content);
		sb.append("\n");
		for (Node c : children) sb.append(c.print(indent + 2));
		return sb.toString();
	}

}
