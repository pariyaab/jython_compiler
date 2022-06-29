package symboltable;

import java.util.ArrayList;

public class Scope {
    private Scope parent;
    private String id;
    private SymbolTable symbolTable = new SymbolTable();
    int scopeNumber;
    public Scope(){
        this.scopeNumber = 1;
    }
    public Scope(int scopeNumber){
        this.scopeNumber = scopeNumber;
    }

    public Scope(int scopeNumber, String id, Scope parent){
        this.scopeNumber = scopeNumber;
        this.parent = parent;
        this.id = id;
    }

    public void insert(String idefName, SymbolTableEntry attributes) {
        symbolTable.insert(idefName, attributes);
    }

    public ArrayList<SymbolTableEntry> lookUp(String idefName) {
        return symbolTable.lookUp(idefName);
    }

    public int getScopeNumber() {
        return scopeNumber;
    }

    private ArrayList<SymbolTableEntry> recursiveLoopUpHelper (String idefName, Scope scopeToLookUp) {
        ArrayList<SymbolTableEntry> temp = scopeToLookUp.lookUp(idefName);
        if (temp == null) {
            if (scopeToLookUp.getParent() == null) {
                return null;
            }
            temp = recursiveLoopUpHelper(idefName, scopeToLookUp.getParent());
        }
        return temp;
    }

    public ArrayList<SymbolTableEntry> recursiveLoopUp(String idefName) {
        ArrayList<SymbolTableEntry> temp = symbolTable.lookUp(idefName);
        if (parent == null && temp == null) {
            return null;
        }
        if (temp == null) {
            return recursiveLoopUpHelper(idefName, parent);
        } else {
            return temp;
        }
    }

    public Scope getParent() {
        return parent;
    }

    public void setParent(Scope parent) {
        this.parent = parent;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String toString(){
        return "-------------  " + id + " : " + scopeNumber + "  -------------\n" +
                symbolTable.printItems() +
                "================================================================================\n";
    }
}
