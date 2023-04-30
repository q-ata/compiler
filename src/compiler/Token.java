package compiler;

public class Token {
	
	public final String type;
	public final String content;
	public final int pos;
	
	public Token(String type, String content, int pos) {
		this.type = type;
		this.content = content;
		this.pos = pos;
	}
	
	@Override
	public boolean equals(Object o) {
		return o != null && o instanceof Token && ((Token) o).type == this.type && ((Token) o).content == this.content;
	}

}
