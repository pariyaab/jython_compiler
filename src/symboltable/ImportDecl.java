package symboltable;

public class ImportDecl extends SymbolTableEntry{
    private String id;
    private String value;
    public ImportDecl(String id,String value){
        this.value = value;
        this.id = id;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    @Override
    public String toString() {
        String out = "import: " + "(name: " + this.value + ")";

        return out;
    }
}
