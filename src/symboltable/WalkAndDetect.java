package symboltable;

import gen.jythonListener;
import gen.jythonParser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class WalkAndDetect implements jythonListener {

    Scope scope;
    private Stack<Boolean> isNested = new Stack<Boolean>();

    private boolean isNested(RuleContext child) {
        boolean isNested = false;
        if (child instanceof jythonParser.While_statmentContext || child instanceof jythonParser.If_statmentContext || child instanceof jythonParser.For_statmentContext)
            isNested = true;
        return isNested;
    }

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

    }

    @Override
    public void exitClass_body(jythonParser.Class_bodyContext ctx) {

    }

    @Override
    public void enterVarDec(jythonParser.VarDecContext ctx) {
        String id = ctx.ID().getText();
        ArrayList<SymbolTableEntry> check = scope.recursiveLoopUp("Field_"+id);
        if (check != null) {
            System.out.printf("ERROR[%d:%d]: chosen ID:[%s] for field method had defined already.\n", ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine(), id);
            System.exit(1);
        }
        int type =3;
        TerminalNode def = ((ctx.CLASSNAME() == null) ? ctx.TYPE() : ctx.CLASSNAME());
        TerminalNode variableName = ctx.ID();
        if (!def.getText().equals("int") && !def.getText().equals("bool") && !def.getText().equals("string")) {
            type = 1;
        }
        SymbolTableEntry fieldDef = new FieldDecl("Field_" + variableName.getText(), variableName.getText(), type, def.toString());
        scope.insert(((FieldDecl) fieldDef).getId(), fieldDef);
    }

    @Override
    public void exitVarDec(jythonParser.VarDecContext ctx) {

    }

    @Override
    public void enterArrayDec(jythonParser.ArrayDecContext ctx) {
        TerminalNode def = ((ctx.CLASSNAME() == null) ? ctx.TYPE() : ctx.CLASSNAME());
        TerminalNode variableName = ctx.ID();
        int type = 3;
        if (!def.getText().equals("int") && !def.getText().equals("bool") && !def.getText().equals("string")) {
            type = 2;
        }
        SymbolTableEntry fieldDef = new FieldDecl("Field_" + variableName.getText(), variableName.getText(), type, def.toString());
        scope.insert(((FieldDecl) fieldDef).getId(), fieldDef);
    }

    @Override
    public void exitArrayDec(jythonParser.ArrayDecContext ctx) {

    }

    @Override
    public void enterMethodDec(jythonParser.MethodDecContext ctx) {

        String id = ctx.ID().getText();
        ArrayList<SymbolTableEntry> check = scope.recursiveLoopUp("Method_"+id);
        if (check != null) {
            System.out.printf("ERROR[%d:%d]: chosen ID:[%s] for class method has been defined already.\n", ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine(), id);
            System.exit(1);
      }
        String returnType = "";
        if (ctx.CLASSNAME() == null && ctx.TYPE() == null) {
            returnType = "void";
        } else {
            returnType = ((ctx.CLASSNAME() == null) ? ctx.TYPE().getText() : ctx.CLASSNAME().getText());
        }
        SymbolTableEntry methodDef = new MethodDecl("Method_" + id, returnType, id);
        List<jythonParser.ParameterContext> parameters = ctx.parameter();
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
        scope.insert(((MethodDecl) methodDef).getId(), methodDef);
        Scope methodDeclScope = new Scope(ctx.getStart().getLine(), id, scope);
        scope = methodDeclScope;
        List<jythonParser.StatementContext> statements = ctx.statement();
        if (statements.size() > 0) {
            for (jythonParser.StatementContext statement : statements) {
                jythonParser.VarDecContext varDec = statement.varDec();
                if (varDec != null) {
                    TerminalNode def = ((varDec.CLASSNAME() == null) ? varDec.TYPE() : varDec.CLASSNAME());
                    TerminalNode variableName = varDec.ID();
                    SymbolTableEntry fieldDef = new FieldDecl("Field_" + variableName.getText(), variableName.getText(), 5, def.toString());
                    scope.insert(((FieldDecl) fieldDef).getId(), fieldDef);
                }
            }
        }
        if (parameters.size() > 0) {
            int i = 1;
            for (jythonParser.ParameterContext parameter : parameters) {
                List<jythonParser.VarDecContext> variableDecs = parameter.varDec();
                if (variableDecs.size() > 1) {
                    for (jythonParser.VarDecContext varDec : variableDecs) {
                        TerminalNode def = ((varDec.CLASSNAME() == null) ? varDec.TYPE() : varDec.CLASSNAME());
                        TerminalNode variableName = varDec.ID();
                        SymbolTableEntry parameterDef = new ParameterDecl("Field_" + variableName.getText(), variableName.getText(), i, def.getText());
                        i++;
                        scope.insert(((ParameterDecl) parameterDef).getId(), parameterDef);
                    }
                } else {
                    for (jythonParser.VarDecContext varDec : variableDecs) {
                        TerminalNode def = ((varDec.CLASSNAME() == null) ? varDec.TYPE() : varDec.CLASSNAME());
                        TerminalNode variableName = varDec.ID();
                        SymbolTableEntry parameterDef = new ParameterDecl("Field_" + variableName.getText(), variableName.getText(), i, def.getText());
                        scope.insert(((ParameterDecl) parameterDef).getId(), parameterDef);
                    }
                }
            }
        }
    }

    @Override
    public void exitMethodDec(jythonParser.MethodDecContext ctx) {
        System.out.println(scope.toString());
        scope = scope.getParent();
    }

    @Override
    public void enterConstructor(jythonParser.ConstructorContext ctx) {
        String id = ctx.CLASSNAME().getText();
        SymbolTableEntry constructorDef = new ConstructorDel("Constructor_" + id, id);
        List<jythonParser.ParameterContext> parameters = ctx.parameter();
        if (parameters.size() > 0) {
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

        scope.insert(((ConstructorDel) constructorDef).getId(), constructorDef);
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
        System.out.println(ctx.getRuleIndex());
    }

    @Override
    public void exitIf_statment(jythonParser.If_statmentContext ctx) {
    }

    @Override
    public void enterWhile_statment(jythonParser.While_statmentContext ctx) {
        Scope whileStatementScope = new Scope(ctx.getStart().getLine(), "while", scope);
        scope = whileStatementScope;
        List<jythonParser.StatementContext> statements = ctx.statement();
        boolean isNested;
        if (statements.size() > 0) {
            for (jythonParser.StatementContext statement : statements) {

                RuleContext ifChild = statement.getRuleContext();
                isNested = isNested(ifChild);
                if (isNested) {
                    System.out.println("hello");
                }
                jythonParser.VarDecContext varDec = statement.varDec();
                if (varDec != null) {
                    TerminalNode def = ((varDec.CLASSNAME() == null) ? varDec.TYPE() : varDec.CLASSNAME());
                    TerminalNode variableName = varDec.ID();
                    SymbolTableEntry fieldDef = new FieldDecl("Field_" + variableName.getText(), variableName.getText(), 5, def.toString());
                    scope.insert(((FieldDecl) fieldDef).getId(), fieldDef);
                }
            }
        }
    }

    @Override
    public void exitWhile_statment(jythonParser.While_statmentContext ctx) {
        System.out.println(scope.toString());
        scope = scope.getParent();
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
