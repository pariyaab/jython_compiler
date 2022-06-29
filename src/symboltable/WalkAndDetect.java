package symboltable;

import gen.jythonListener;
import gen.jythonParser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class WalkAndDetect implements jythonListener {

    Scope scope;
    private Stack<Boolean> isNested = new Stack<Boolean>();

    @Override
    public void enterProgram(jythonParser.ProgramContext ctx) {
        Scope enterProgramScope = new Scope(ctx.getStart().getLine(), "program", null);
        scope = enterProgramScope;
        isNested.push(false);
    }

    @Override
    public void exitProgram(jythonParser.ProgramContext ctx) {
        System.out.println(scope.toString());
        isNested.pop();
    }

    @Override
    public void enterImportclass(jythonParser.ImportclassContext ctx) {
        String[] arrOfStr = ctx.getText().split("import");
        String id = arrOfStr[1];
        SymbolTableEntry importDef = new ImportDecl("import_" + id, id);
        scope.insert(((ImportDecl) importDef).getId(), importDef);
    }

    @Override
    public void exitImportclass(jythonParser.ImportclassContext ctx) {
    }

    @Override
    public void enterClassDef(jythonParser.ClassDefContext ctx) {
        String id = ctx.CLASSNAME(0).getText();
        List<TerminalNode> classNames = ctx.CLASSNAME();
        ArrayList<SymbolTableEntry> check = scope.recursiveLoopUp(id);
        if (check != null) {
            for (SymbolTableEntry s :
                    check) {
                if (s.getType().equals("class")) {
                    System.out.printf("ERROR[%d:%d]: chosen ID:[%s] for class had already been used.\n", ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine(), id);
                }
            }
        }
        Scope classDelScope = new Scope(ctx.getStart().getLine(), id, scope);
        String parentsName = "";
        if (classNames.size() > 1) {
            classNames.remove(0);
            for (TerminalNode parents : classNames) {
                parentsName += parents + ", ";
            }
        } else {
            parentsName = "object";
        }
        SymbolTableEntry classDef = new ClassDecl("class_" + id, id);
        if (ctx.parent != null) {
            ((ClassDecl) classDef).setParent(parentsName);
        } else {
            ((ClassDecl) classDef).setParent("Object");
        }
        scope.insert(((ClassDecl) classDef).getId(), classDef);
        scope = classDelScope;
    }

    @Override
    public void exitClassDef(jythonParser.ClassDefContext ctx) {
        System.out.println(scope.toString());
        scope = scope.getParent();
    }

    @Override
    public void enterClass_body(jythonParser.Class_bodyContext ctx) {
        jythonParser.VarDecContext varDec = ctx.varDec();
        jythonParser.ArrayDecContext arrayDec = ctx.arrayDec();
        jythonParser.MethodDecContext methodDec = ctx.methodDec();
        jythonParser.ConstructorContext constructorDec = ctx.constructor();
        int type = 3;
        if (varDec != null) {
            TerminalNode def = ((varDec.CLASSNAME() == null) ? varDec.TYPE() : varDec.CLASSNAME());
            TerminalNode variableName = varDec.ID();
            if (!def.getText().equals("int") && !def.getText().equals("bool") && !def.getText().equals("string")) {
                type = 1;
            }
            SymbolTableEntry fieldDef = new FieldDecl("Field_" + variableName.getText(), variableName.getText(), type, def.toString());
            scope.insert(((FieldDecl) fieldDef).getId(), fieldDef);

        }
        if (arrayDec != null) {
            TerminalNode def = ((arrayDec.CLASSNAME() == null) ? arrayDec.TYPE() : arrayDec.CLASSNAME());
            TerminalNode variableName = arrayDec.ID();
            if (!def.getText().equals("int") && !def.getText().equals("bool") && !def.getText().equals("string")) {
                type = 2;
            }
            SymbolTableEntry fieldDef = new FieldDecl("Field_" + variableName.getText(), variableName.getText(), type, def.toString());
            scope.insert(((FieldDecl) fieldDef).getId(), fieldDef);
        }
        if (methodDec != null) {

            String id = methodDec.ID().getText();
            String returnType = "";
            if (methodDec.CLASSNAME() == null && methodDec.TYPE() == null) {
                returnType = "void";
            } else {
                returnType = ((methodDec.CLASSNAME() == null) ? methodDec.TYPE().getText() : methodDec.CLASSNAME().getText());
            }

            ArrayList<SymbolTableEntry> check = scope.recursiveLoopUp(id);
            if (check != null) {
                for (SymbolTableEntry s :
                        check) {
                    if (s.getType().equals("Method")) {
                        System.out.printf("ERROR[%d:%d]: chosen ID:[%s] for class method had already been used.\n", ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine(), id);
                    }
                }
            }
            SymbolTableEntry methodDef = new MethodDecl("Method_"+id, returnType, id);

            List<jythonParser.ParameterContext> parameters = methodDec.parameter();
            for (jythonParser.ParameterContext parameter : parameters) {
                List<jythonParser.VarDecContext> variableDecs = parameter.varDec();
                if (variableDecs.size() > 1) {
                    for (jythonParser.VarDecContext varDec1 : variableDecs) {
                        TerminalNode def = ((varDec1.CLASSNAME() == null) ? varDec1.TYPE() : varDec1.CLASSNAME());
                        TerminalNode variableName = varDec1.ID();
                        ((MethodDecl) methodDef).addParam(variableName.getText(), def.getText());
                    }
                } else {
                    for (jythonParser.VarDecContext varDec1 : variableDecs) {
                        TerminalNode def = ((varDec1.CLASSNAME() == null) ? varDec1.TYPE() : varDec1.CLASSNAME());
                        TerminalNode variableName = varDec1.ID();
                        ((MethodDecl) methodDef).addParam(variableName.getText(), def.getText());
                    }
                }
            }
            scope.insert(((MethodDecl) methodDef).getId(),methodDef);
        }
        if(constructorDec != null){
            String id = constructorDec.CLASSNAME().getText();
            SymbolTableEntry constructorDef = new ConstructorDel("Constructor_"+id,id);
            List<jythonParser.ParameterContext> parameters = constructorDec.parameter();
            if(parameters.size() > 0){
                for (jythonParser.ParameterContext parameter : parameters) {
                    List<jythonParser.VarDecContext> variableDecs = parameter.varDec();
                    if (variableDecs.size() > 1) {
                        for (jythonParser.VarDecContext varDec1 : variableDecs) {
                            TerminalNode def = ((varDec1.CLASSNAME() == null) ? varDec1.TYPE() : varDec1.CLASSNAME());
                            TerminalNode variableName = varDec1.ID();
                            ((ConstructorDel) constructorDef).addParam(variableName.getText(), def.getText());
                        }
                    } else {
                        for (jythonParser.VarDecContext varDec1 : variableDecs) {
                            TerminalNode def = ((varDec1.CLASSNAME() == null) ? varDec1.TYPE() : varDec1.CLASSNAME());
                            TerminalNode variableName = varDec1.ID();
                            ((ConstructorDel) constructorDef).addParam(variableName.getText(), def.getText());
                        }
                    }
                }
            }
            scope.insert(((ConstructorDel) constructorDef).getId(),constructorDef);
        }
    }

    @Override
    public void exitClass_body(jythonParser.Class_bodyContext ctx) {

    }

    @Override
    public void enterVarDec(jythonParser.VarDecContext ctx) {
        String id = ctx.ID().getText();
        ArrayList<SymbolTableEntry> check = scope.recursiveLoopUp(id);
        if (check != null) {
            for (SymbolTableEntry s :
                    check) {
                if (s.getType().equals("Field")) {
                    System.out.printf("ERROR[%d:%d]: chosen ID:[%s] for class variable had already been used.\n", ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine(), id);
                }
            }
        }
    }

    @Override
    public void exitVarDec(jythonParser.VarDecContext ctx) {

    }

    @Override
    public void enterArrayDec(jythonParser.ArrayDecContext ctx) {

    }

    @Override
    public void exitArrayDec(jythonParser.ArrayDecContext ctx) {

    }

    @Override
    public void enterMethodDec(jythonParser.MethodDecContext ctx) {

    }

    @Override
    public void exitMethodDec(jythonParser.MethodDecContext ctx) {
    }

    @Override
    public void enterConstructor(jythonParser.ConstructorContext ctx) {
        String id = ctx.CLASSNAME().getText();
        Scope classDelScope = new Scope(ctx.getStart().getLine(), id, scope);
        scope = classDelScope;
    }

    @Override
    public void exitConstructor(jythonParser.ConstructorContext ctx) {
        System.out.println(scope.toString());
        scope = scope.getParent();
    }

    @Override
    public void enterParameter(jythonParser.ParameterContext ctx) {

    }

    @Override
    public void exitParameter(jythonParser.ParameterContext ctx) {

    }

    @Override
    public void enterStatement(jythonParser.StatementContext ctx) {

    }

    @Override
    public void exitStatement(jythonParser.StatementContext ctx) {

    }

    @Override
    public void enterReturn_statment(jythonParser.Return_statmentContext ctx) {

    }

    @Override
    public void exitReturn_statment(jythonParser.Return_statmentContext ctx) {

    }

    @Override
    public void enterCondition_list(jythonParser.Condition_listContext ctx) {

    }

    @Override
    public void exitCondition_list(jythonParser.Condition_listContext ctx) {

    }

    @Override
    public void enterCondition(jythonParser.ConditionContext ctx) {

    }

    @Override
    public void exitCondition(jythonParser.ConditionContext ctx) {

    }

    @Override
    public void enterIf_statment(jythonParser.If_statmentContext ctx) {

    }

    @Override
    public void exitIf_statment(jythonParser.If_statmentContext ctx) {

    }

    @Override
    public void enterWhile_statment(jythonParser.While_statmentContext ctx) {

    }

    @Override
    public void exitWhile_statment(jythonParser.While_statmentContext ctx) {

    }

    @Override
    public void enterIf_else_statment(jythonParser.If_else_statmentContext ctx) {

    }

    @Override
    public void exitIf_else_statment(jythonParser.If_else_statmentContext ctx) {

    }

    @Override
    public void enterPrint_statment(jythonParser.Print_statmentContext ctx) {

    }

    @Override
    public void exitPrint_statment(jythonParser.Print_statmentContext ctx) {

    }

    @Override
    public void enterFor_statment(jythonParser.For_statmentContext ctx) {

    }

    @Override
    public void exitFor_statment(jythonParser.For_statmentContext ctx) {

    }

    @Override
    public void enterMethod_call(jythonParser.Method_callContext ctx) {

    }

    @Override
    public void exitMethod_call(jythonParser.Method_callContext ctx) {

    }

    @Override
    public void enterAssignment(jythonParser.AssignmentContext ctx) {

    }

    @Override
    public void exitAssignment(jythonParser.AssignmentContext ctx) {

    }

    @Override
    public void enterExp(jythonParser.ExpContext ctx) {

    }

    @Override
    public void exitExp(jythonParser.ExpContext ctx) {

    }

    @Override
    public void enterPrefixexp(jythonParser.PrefixexpContext ctx) {

    }

    @Override
    public void exitPrefixexp(jythonParser.PrefixexpContext ctx) {

    }

    @Override
    public void enterArgs(jythonParser.ArgsContext ctx) {

    }

    @Override
    public void exitArgs(jythonParser.ArgsContext ctx) {

    }

    @Override
    public void enterExplist(jythonParser.ExplistContext ctx) {

    }

    @Override
    public void exitExplist(jythonParser.ExplistContext ctx) {

    }

    @Override
    public void enterArithmetic_operator(jythonParser.Arithmetic_operatorContext ctx) {

    }

    @Override
    public void exitArithmetic_operator(jythonParser.Arithmetic_operatorContext ctx) {

    }

    @Override
    public void enterRelational_operators(jythonParser.Relational_operatorsContext ctx) {

    }

    @Override
    public void exitRelational_operators(jythonParser.Relational_operatorsContext ctx) {

    }

    @Override
    public void enterAssignment_operators(jythonParser.Assignment_operatorsContext ctx) {

    }

    @Override
    public void exitAssignment_operators(jythonParser.Assignment_operatorsContext ctx) {

    }

    @Override
    public void visitTerminal(TerminalNode terminalNode) {

    }

    @Override
    public void visitErrorNode(ErrorNode errorNode) {

    }

    @Override
    public void enterEveryRule(ParserRuleContext parserRuleContext) {

    }

    @Override
    public void exitEveryRule(ParserRuleContext parserRuleContext) {

    }
}
