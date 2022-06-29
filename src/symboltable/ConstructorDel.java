package symboltable;

import java.util.ArrayList;
import java.util.List;

public class ConstructorDel extends SymbolTableEntry{
    private String id;
    private String value;

    public List<MethodParam> getParams() {
        return params;
    }

    private List<MethodParam> params = new ArrayList<MethodParam>();

    public ConstructorDel(String id,String value) {
        this.value = value;
        this.id = id;
        super.setType("Constructor");
    }
    public void addParam(String id, String type) {
        params.add(new MethodParam(id, type));
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }



    public String toString(){
        String method = "Method: " +  "(name: " + value ;
        if (params.size() == 0){
            return method + ")";
        }
        String types = " [parameter List: ";
        String parameter = "";
        for (int i=0; i<params.size(); i++){
            parameter += "[ type: "  +params.get(i).getType() + " , index: " + (i+1) + "], ";
        }

        return  method + ")" + types + parameter + ")";
    }
}
