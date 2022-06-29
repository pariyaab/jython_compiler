package symboltable;

public class ClassDecl extends SymbolTableEntry{
    private String id;
    private String value;
    private String parent;

    public ClassDecl(String id,String value) {
        this.value = value;
        this.id = id;
        this.parent = "";
        super.setType("class");
    }
    public String getId() {
        return id;
    }

    public String getParent() {
        return parent;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    @Override
    public String toString() {
        String out = "Class: " + "(name: " + this.value + ") " +"(parents : " + parent;

        return out +")";
    }
}
