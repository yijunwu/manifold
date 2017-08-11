package manifold.js;


import jdk.nashorn.api.scripting.JSObject;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import manifold.api.gen.*;
import manifold.util.ManClassUtil;
import manifold.api.type.SourcePosition;
import manifold.js.parser.Parser;
import manifold.js.parser.Tokenizer;
import manifold.js.parser.tree.*;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.lang.reflect.Modifier;

import static manifold.js.JavascriptProgram.generateArgList;
import static manifold.js.JavascriptProgram.loadSrcForName;
import static manifold.js.Util.safe;

public class JavascriptClass {

    /* codegen */
    static SrcClass genClass(String fqn, ProgramNode programNode) {

        ClassNode classNode = programNode.getFirstChild(ClassNode.class);

        SrcClass clazz = new SrcClass(fqn, SrcClass.Kind.Class);

        String superClass = classNode.getSuperClass();
        if (superClass != null) {
            clazz.superClass(superClass);
        }
        clazz.imports(JavascriptClass.class)
          .imports( SourcePosition.class );

        clazz.addField(new SrcField("ENGINE", ScriptEngine.class)
                .modifiers(Modifier.STATIC)
                .initializer(new SrcRawExpression(("JavascriptClass.init(\"" + fqn + "\")"))));


        clazz.addField(new SrcField("_context", ScriptObjectMirror.class));

        addConstructor(clazz, classNode);
        addMethods(fqn, clazz, classNode);
        addProperties(fqn, clazz, classNode);

        return clazz;
    }

    private static void addConstructor(SrcClass clazz, ClassNode classNode) {
        ConstructorNode constructor = classNode.getFirstChild(ConstructorNode.class);

        SrcConstructor ctor = new SrcConstructor()
          .name(classNode.getName())
          .modifiers(Modifier.PUBLIC);

        SrcParameter[] srcParameters = new SrcParameter[0];
        if (constructor != null) {
            // params
            ParameterNode parameters = constructor.getFirstChild(ParameterNode.class);
            for (SrcParameter srcParameter : parameters.toParamList()) {
                ctor.addParam(srcParameter);
            }
            srcParameters = parameters.toParamList();
        }

        //impl
        ctor.body(new SrcStatementBlock()
          .addStatement(
            new SrcRawStatement()
              .rawText("_context = JavascriptClass.initInstance(ENGINE, \"" + classNode.getName() + "\"" + generateArgList(srcParameters) + ");")));

        clazz.addConstructor(ctor);
    }

    private static void addMethods(String fqn, SrcClass clazz, ClassNode classNode) {
        for (ClassFunctionNode node : classNode.getChildren(ClassFunctionNode.class)) {
            AbstractSrcMethod<SrcMethod> srcMethod = new SrcMethod()
                    .name(node.getName())
                    .modifiers(Modifier.PUBLIC | (node.isStatic() ? Modifier.STATIC : 0))
                    .returns(node.getReturnType());

            // params
            ParameterNode parameters = node.getFirstChild(ParameterNode.class);
            for (SrcParameter srcParameter : parameters.toParamList()) {
                srcMethod.addParam(srcParameter);
            }

            //impl
            if (node.isStatic()) srcMethod.body(new SrcStatementBlock()
              .addStatement(
                new SrcRawStatement()
                  .rawText( "return JavascriptClass.invokeStatic(ENGINE, \"" + ManClassUtil.getShortClassName( fqn ) + "\", \"" + node.getName() + "\"" + generateArgList( parameters.toParamList()) + ");")));
            else srcMethod.body(new SrcStatementBlock()
              .addStatement(
                new SrcRawStatement()
                  .rawText("return JavascriptClass.invoke(_context, \"" + node.getName() + "\"" + generateArgList(parameters.toParamList()) + ");")));
            clazz.addMethod(srcMethod);
        }
    }

    private static void addProperties(String fqn, SrcClass clazz, ClassNode classNode) {

        for (PropertyNode node : classNode.getChildren(PropertyNode.class)) {
            final String name = node.getName();
            final String capitalizedName = name.substring(0, 1).toUpperCase() + name.substring(1);

            AbstractSrcMethod<SrcMethod> getter = new SrcMethod()
              .name("get" + capitalizedName)
              .modifiers(Modifier.PUBLIC | (node.isStatic() ? Modifier.STATIC : 0))
              .returns(node.getReturnType());

            //impl
            if (node.isStatic()) getter.body(new SrcStatementBlock()
              .addStatement(
                new SrcRawStatement()
                  .rawText( "return JavascriptClass.getStaticProp(ENGINE, \"" + ManClassUtil.getShortClassName( fqn ) + "\", \"" + name + "\");")));
            else getter.body(new SrcStatementBlock()
              .addStatement(
                new SrcRawStatement()
                  .rawText("return JavascriptClass.getProp(_context, \"" + name + "\");")));
            clazz.addMethod(getter);

            AbstractSrcMethod<SrcMethod> setter = new SrcMethod()
              .name("set" + capitalizedName)
              .modifiers(Modifier.PUBLIC | (node.isStatic() ? Modifier.STATIC : 0))
              .addParam("val", Object.class)
              .returns("void");

            //impl
            if (node.isStatic()) setter.body(new SrcStatementBlock()
              .addStatement(
                new SrcRawStatement()
                  .rawText( "JavascriptClass.setStaticProp(ENGINE, \"" + ManClassUtil.getShortClassName( fqn ) + "\", \"" + name + "\", val);")));
            else setter.body(new SrcStatementBlock()
              .addStatement(
                new SrcRawStatement()
                  .rawText("JavascriptClass.setProp(_context, \"" + name + "\", val);")));
            clazz.addMethod(setter);
        }
    }

    /* implementation */
    public static Object invoke(ScriptObjectMirror context, String func, Object... args) {
        return context.callMember(func, args);
    }

    public static Object invokeStatic(ScriptEngine engine, String className, String func, Object... args) {
        ScriptObjectMirror classObject = (ScriptObjectMirror) engine.get(className);
        return classObject.callMember(func, args);
    }

    public static Object getProp(ScriptObjectMirror context, String prop) {
        return context.get(prop);
    }

    public static Object getStaticProp(ScriptEngine engine, String className, String property) {
        ScriptObjectMirror classObject = (ScriptObjectMirror) engine.get(className);
        return classObject.get(property);
    }

    public static void setProp(ScriptObjectMirror context, String prop, Object value) {
        context.put("_" + prop, value);
    }

    public static void setStaticProp(ScriptEngine engine, String className, String property, Object value) {
        ScriptObjectMirror classObject = (ScriptObjectMirror) engine.get(className);
        classObject.put("_" + property, value); //TODO why the underscore?
    }

    public static ScriptEngine init(String programName) {
        ScriptEngine nashorn = new ScriptEngineManager().getEngineByName("nashorn");
        Parser parser = new Parser(new Tokenizer(loadSrcForName(programName)));
        Node programNode =  parser.parse();
        ClassNode classNode = programNode.getFirstChild(ClassNode.class);
        safe(() -> nashorn.eval(classNode.genCode()));
        return nashorn;
    }

    public static ScriptObjectMirror initInstance(ScriptEngine engine, String name, Object... args){
        JSObject classObject =  (ScriptObjectMirror) engine.get(name);
        return (ScriptObjectMirror) classObject.newObject(args);
    }

}
