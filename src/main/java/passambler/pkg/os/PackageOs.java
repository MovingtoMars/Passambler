package passambler.pkg.os;

import java.util.Map;
import passambler.parser.Scope;
import passambler.pkg.Package;
import passambler.pkg.os.function.*;
import passambler.value.Value;
import passambler.value.ValueStr;

public class PackageOs implements Package {
    @Override
    public void addSymbols(Scope scope, Map<String, Value> symbols) {
        symbols.put("Exit", new FunctionExit());
        symbols.put("Time", new FunctionTime());
        symbols.put("Exec", new FunctionExec());
        
        symbols.put("LineSeparator", new ValueStr(System.getProperty("line.separator")));
        symbols.put("FileSeparator", new ValueStr(System.getProperty("file.separator")));
        symbols.put("PathSeparator", new ValueStr(System.getProperty("path.separator"))); 
        
        symbols.put("UserDir", new ValueStr(System.getProperty("user.dir")));    
        symbols.put("UserHome", new ValueStr(System.getProperty("user.home")));    
        symbols.put("UserName", new ValueStr(System.getProperty("user.name"))); 
        
        symbols.put("Name", new ValueStr(System.getProperty("os.name")));
        symbols.put("Version", new ValueStr(System.getProperty("os.version")));
        symbols.put("Arch", new ValueStr(System.getProperty("os.arch")));
    }
}