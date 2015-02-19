package passambler.extension;

import passambler.parser.Scope;

public interface Extension {
    public String getId();
    
    public void applySymbols(Scope scope);
}
