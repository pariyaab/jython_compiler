package symboltable;

public class MethodParam {
    String id;
    String type;
    public MethodParam(String id, String type) {
        this.id = id;
        this.type = type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public String getId() {
        return id;
    }
}
