package symboltable;

import java.util.ArrayList;
import java.util.List;

public class MethodDecl extends SymbolTableEntry{
    private String id;
    private String type;
    private String value;

    public List<MethodParam> getParams() {
        return params;
    }

    private List<MethodParam> params = new ArrayList<MethodParam>();

    public MethodDecl(String id, String type,String value) {
        this.value = value;
        this.id = id;
        this.type = type;
        super.setType("Method");
    }
    public void addParam(String id, String type) {
        params.add(new MethodParam(id, type));
    }
    public String setReturnType(){
        if(!type.equals("bool") && !type.equals("int") && !type.equals("String") && !type.equals("void")){
            type = "class type = " + type;
        }
        return type;
    }
    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String toString(){
        String method = "Method: " +  "(name: " + value + ") (returnType: [" + setReturnType() + "]";
        if (params.size() == 0){
            return method + ")";
        }
        String types = " [parameter List: ";
        String parameter = "";
        for (int i=0; i<params.size(); i++){
            parameter += "[ type: "  +params.get(i).getType() + " , index: " + (i+1) + "], ";
        }

        return  method + types + parameter + ")";
    }
}
