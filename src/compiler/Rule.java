package compiler;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Rule {
	
	public final Symbol source;
	public final List<Symbol> dest;
	
	public final String name;
	
	public Rule(String name, Symbol source, List<Symbol> dest) {
		this.source = source;
		this.dest = Collections.unmodifiableList(dest);
		this.name = name;
	}
	
	public Rule(String name, Symbol source, Symbol... dest) {
		this(name, source, Arrays.asList(dest));
	}

}
