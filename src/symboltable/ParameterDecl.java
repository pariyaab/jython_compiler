package symboltable;

public class ParameterDecl extends SymbolTableEntry{
    private String id;
    private String value;
    private boolean isDefined = false;
    private String classType;
    private int index ;
    public ParameterDecl(String id,String value,int index,String classType) {
        this.classType = classType;
        this.value = value;
        this.id = id;
        this.index = index;
        super.setType("Parameter");
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    private String setClassType(){
        if(!classType.equals("bool") && !classType.equals("int") && !classType.equals("string") ){
            classType = "class type = " + classType;
        }
        return classType;
    }
    private boolean checkIsDefined(){
        if(classType.equals("bool") || classType.equals("int") || classType.equals("string")){
            isDefined = true;
        }
        return isDefined;
    }
    @Override
    public String toString() {
        String out = super.getType() + "(name: " + this.value + ") " +"(type: [ " +setClassType() +", isDefined: " +checkIsDefined() +")" + "(index: " +index;
        return out +")";
    }
}
