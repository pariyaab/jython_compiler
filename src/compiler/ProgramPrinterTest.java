package compiler;

import gen.jythonListener;
import gen.jythonParser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.ArrayList;
import java.util.List;


public class ProgramPrinterTest implements jythonListener {

    String tabString = "    ";

    @Override
    public void enterProgram(jythonParser.ProgramContext ctx) {
        System.out.println("program start {");
    }

    @Override
    public void exitProgram(jythonParser.ProgramContext ctx) {
        System.out.println("}");
    }

    @Override
    public void enterImportclass(jythonParser.ImportclassContext ctx) {

        String[] arrOfStr = ctx.getText().split("import");
        System.out.println(tabString + "import class: " + arrOfStr[1]);
    }

    @Override
    public void exitImportclass(jythonParser.ImportclassContext ctx) {

    }

    @Override
    public void enterClassDef(jythonParser.ClassDefContext ctx) {

        List<TerminalNode> classNames = new ArrayList<>();
        classNames = ctx.CLASSNAME();
        TerminalNode childClass = classNames.get(0);
        String output = "    class: " + childClass + "/ class parents: ";
        if (classNames.size() > 1) {
            String parentsName = "";
            classNames.remove(0);
            for (TerminalNode parents : classNames) {
                parentsName += parents + ", ";
            }
            output += parentsName + "{";
            System.out.println(output);
        } else {
            System.out.println(output + "object, {");
        }


    }

    @Override
    public void exitClassDef(jythonParser.ClassDefContext ctx) {
        System.out.println("    }");
    }

    @Override
    public void enterClass_body(jythonParser.Class_bodyContext ctx) {
//        System.out.println(ctx.getText());
    }

    @Override
    public void exitClass_body(jythonParser.Class_bodyContext ctx) {
        jythonParser.VarDecContext varDec = ctx.varDec();
        jythonParser.ArrayDecContext arrayDec = ctx.arrayDec();
        if (varDec != null) {
            TerminalNode def = ((varDec.CLASSNAME() == null) ? varDec.TYPE() : varDec.CLASSNAME());
            TerminalNode variableName = varDec.ID();
            System.out.println(tabString + tabString + "filed: " + variableName + "/ " + "type= " + def);
        }
        if (arrayDec != null) {
            TerminalNode def = ((arrayDec.CLASSNAME() == null) ? arrayDec.TYPE() : arrayDec.CLASSNAME());
            TerminalNode variableName = arrayDec.ID();
            System.out.println(tabString + tabString + "filed: " + variableName + "/ " + "type= " + def);
        }
    }

    @Override
    public void enterVarDec(jythonParser.VarDecContext ctx) {

//        if(ctx.getParent().getParent().getRuleContext().getText().startsWith("class")){
//            TerminalNode def = ((ctx.CLASSNAME() == null) ? ctx.TYPE() : ctx.CLASSNAME());
//            TerminalNode variableName = ctx.ID();
//            System.out.println(tabString + tabString + "filed: " + variableName + "/ " + "type= " + def);
//        }
//        if(ctx.getParent().getParent().getRuleContext().getText().startsWith("def")){
//            TerminalNode def = ((ctx.CLASSNAME() == null) ? ctx.TYPE() : ctx.CLASSNAME());
//            TerminalNode variableName = ctx.ID();
//            System.out.println(tabString + tabString + tabString + "filed: " + variableName + "/ " + "type= " + def);
//        }

    }

    @Override
    public void exitVarDec(jythonParser.VarDecContext ctx) {

    }

    @Override
    public void enterArrayDec(jythonParser.ArrayDecContext ctx) {
//        TerminalNode def = ((ctx.CLASSNAME() == null) ? ctx.TYPE() : ctx.CLASSNAME());
//        TerminalNode variableName = ctx.ID();
//        System.out.println( tabString + tabString +"filed: " + variableName + "/ " + "type= " + def);
    }

    @Override
    public void exitArrayDec(jythonParser.ArrayDecContext ctx) {

    }

    @Override
    public void enterMethodDec(jythonParser.MethodDecContext ctx) {
        String output = tabString + tabString + "class method: ";
        TerminalNode methodName = ctx.ID();
        if(methodName.getText().equals("main")){
            output = tabString + tabString + "main method {";
        }
        TerminalNode returnType = ((ctx.CLASSNAME() == null) ? ctx.TYPE() : ctx.CLASSNAME());
        if (returnType == null && !methodName.getText().equals("main")) {
            output += methodName + "{";
        } else if(!methodName.getText().equals("main")) {
            output += methodName + "/ return type: " + returnType + "{";
        }
        List<jythonParser.ParameterContext> parameters = ctx.parameter();
        String parameterOutput = "";
        for (jythonParser.ParameterContext parameter : parameters) {
            List<jythonParser.VarDecContext> variableDecs = parameter.varDec();
            if (variableDecs.size() > 1) {
                for (jythonParser.VarDecContext varDec : variableDecs) {
                    TerminalNode def = ((varDec.CLASSNAME() == null) ? varDec.TYPE() : varDec.CLASSNAME());
                    TerminalNode variable_name = varDec.ID();
                    parameterOutput += def + " " + variable_name + ", ";
                }
            } else {
                for (jythonParser.VarDecContext varDec : variableDecs) {
                    TerminalNode def = ((varDec.CLASSNAME() == null) ? varDec.TYPE() : varDec.CLASSNAME());
                    TerminalNode variable_name = varDec.ID();
                    parameterOutput += def + " " + variable_name;
                }
            }
        }
        String statmentOutput = "";
        List<jythonParser.StatementContext> statements = ctx.statement();
        for (jythonParser.StatementContext statement : statements) {
            jythonParser.VarDecContext varDec = statement.varDec();
            if (varDec != null) {
                TerminalNode def = ((varDec.CLASSNAME() == null) ? varDec.TYPE() : varDec.CLASSNAME());
                TerminalNode variableName = varDec.ID();
                statmentOutput = tabString + tabString + tabString + "filed: " + variableName + "/ " + "type= " + def;
            }

        }
        System.out.println(output);
        if (parameters.size() > 0)
            System.out.println(tabString + tabString + tabString + "parameter list: [" + parameterOutput + "]");
        if (statements.size() > 0) System.out.println(statmentOutput);
    }

    @Override
    public void exitMethodDec(jythonParser.MethodDecContext ctx) {
        System.out.println(tabString + tabString + "}");
    }

    @Override
    public void enterConstructor(jythonParser.ConstructorContext ctx) {
        System.out.println(tabString + tabString + "class constructor: " + ctx.CLASSNAME().getText() + "{");
        String output = "";
        List<jythonParser.ParameterContext> parameters = ctx.parameter();
        for (jythonParser.ParameterContext parameter : parameters) {
            List<jythonParser.VarDecContext> variableDecs = parameter.varDec();
            if (variableDecs.size() > 1) {
                for (jythonParser.VarDecContext varDec : variableDecs) {
                    TerminalNode def = ((varDec.CLASSNAME() == null) ? varDec.TYPE() : varDec.CLASSNAME());
                    TerminalNode variable_name = varDec.ID();
                    output += def + " " + variable_name + ", ";
                }
            } else {
                for (jythonParser.VarDecContext varDec : variableDecs) {
                    TerminalNode def = ((varDec.CLASSNAME() == null) ? varDec.TYPE() : varDec.CLASSNAME());
                    TerminalNode variable_name = varDec.ID();
                    output += def + " " + variable_name;
                }
            }
        }
        System.out.println(tabString + tabString + tabString + "parameter list: [" + output + "]");
    }

    @Override
    public void exitConstructor(jythonParser.ConstructorContext ctx) {
        System.out.println(tabString + tabString + "}");
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
