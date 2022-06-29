package symboltable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SymbolTable {
    private HashMap<String, ArrayList<SymbolTableEntry>> hashtable = new HashMap<>();
    public void insert(String identifierName, SymbolTableEntry attributes) {
        ArrayList<SymbolTableEntry> temp = new ArrayList<SymbolTableEntry>();
        if (hashtable.get(identifierName) == null) {
            temp.add(attributes);
            hashtable.put(identifierName, temp);
        } else {
            temp = hashtable.get(identifierName);
            temp.add(attributes);
            hashtable.replace(identifierName, temp); /*is it needed?*/
        }
    }

    public ArrayList<SymbolTableEntry> lookUp(String idefName) {
        return hashtable.get(idefName);
    }
    public String printItems() {
        String itemsStr = "";
        for (Map.Entry<String,ArrayList<SymbolTableEntry>> entry : hashtable.entrySet()) {
            itemsStr += "Key = " + entry.getKey() + "  | Value = " + entry.getValue() + "\n";
        }
        return itemsStr;
    }
}
