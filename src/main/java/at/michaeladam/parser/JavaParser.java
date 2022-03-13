package at.michaeladam.parser;

import at.michaeladam.data.*;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public class JavaParser implements Parser {


    @Override
    public String parseEnum(EnumData input) {
        StringBuilder returnal = new StringBuilder();
        String packageName = input.getPackageName();
        if (packageName != null) {
            String[] split = packageName.split("\\.", 3);
            packageName = split[0] + "." + split[1] + ".generated." + split[2];
        }
        returnal.append("package ").append(packageName).append(";\n\n");
        for (String importString : input.getImports()) {
            returnal.append("import ").append(importString).append(";\n");

        }
        returnal.append("\n\n");
        returnal.append(MessageFormat.format("public enum {0}{1}'{'\n",
                input.getName(), getImplements(input)));

        returnal.append(input.getEnumConstants().entrySet().stream()
                .map(stringEntry -> ("\t" + stringEntry.getKey() + "(" + String.join(", ", stringEntry.getValue()) + ")"))
                .collect(Collectors.joining(",\n"))).append(";\n");

        returnal.append("\n");
        returnal.append(Arrays.stream(input.getFields()).map(this::parseField).map(s -> "\t" + s).collect(Collectors.joining("\n")));
        returnal.append("\n\n");

        returnal.append(Arrays.stream(input.getConstructors())
                .map(methodData -> parseConstructor(input.getName(), methodData))
                .collect(Collectors.joining("\n\n")));


        return returnal.toString() + "}";
    }

    private String parseConstructor(String name, MethodData methodData) {

        String returnal = MessageFormat.format("\t{0} {1}({2})'{'",
                String.join(" ", methodData.getModifier()), name,
                methodData
                        .getParameter()
                        .entrySet()
                        .stream()
                        .map(stringEntry -> String.join(" ", parseType(stringEntry.getValue()), stringEntry.getKey()))
                        .collect(Collectors.joining(", "))) +
                "\n\n" +
                methodData
                        .getParameter()
                        .keySet()
                        .stream()
                        .map(fieldName -> "\t\tthis." + fieldName + " = " + fieldName + ";")
                        .collect(Collectors.joining("\n"));

        return returnal + "\n\n\t}\n";
    }

    public String parseClass(ClassData input) {
        StringBuilder returnal = new StringBuilder();
        String packageName = input.getPackageName();
        //insert .generated. after the second dot
        if (packageName != null) {
            String[] split = packageName.split("\\.", 3);
            packageName = split[0] + "." + split[1] + ".generated." + split[2];
        }

        returnal.append("package ").append(packageName).append(";\n\n");
        for (String importString : input.getImports()) {
            returnal.append("import ").append(importString).append(";\n");

        }
        returnal.append("\n\n");
        returnal.append(MessageFormat.format("public abstract {0} {1}{2}{3}'{'\n", input.getType(), input.getName(), getExtends(input), getImplements(input)));
        for (FieldData field : input.getFields()) {
            returnal.append("\t").append(parseField(field)).append("\n");

        }
        for (MethodData method : input.getMethods()) {
            String methodString = (parseMethod(method));
            //pad every line with a tab
            returnal.append("\t").append(methodString.replace("\n", "\n\t")).append("\n\n");
        }


        return returnal.toString() + "}";
    }

    @Override
    public String parseField(FieldData input) {
        return String.join(" ", String.join(" ", input.getModifier()), parseType(input.getType()), input.getName()) + ";";
    }

    private String parseType(TypeData type) {
        String returnal = "";
        if (type == null) {
            return returnal;
        }
        if (type.getType() != null) {
            returnal = type.getType();

            if (type.getGeneric() != null) {
                returnal += "<" + Arrays.stream(type.getGeneric()).filter(Objects::nonNull).map(this::parseType).collect(Collectors.joining(", ")) + ">";
            }
        }
        return returnal;
    }

    @Override
    public String parseMethod(MethodData input) {
        String returnal = "/** Auto generated code, any changes will be lost.";
        String[] modifier = input.getModifier();
        if (Arrays.asList(modifier).contains("private")) {
            returnal += "\n\t* converted private to protected consider changing the modifier";
            modifier = Arrays.stream(input.getModifier()).map(s -> Objects.equals(s, "private") ? "protected" : s).toArray(String[]::new);
        }
        if (Arrays.asList(modifier).contains("abstract")) {
            returnal += "\n\t* removed abstract from modifiers, all generated methods are abstract";
            modifier = Arrays.stream(input.getModifier()).filter(s -> !Objects.equals(s, "abstract")).toArray(String[]::new);
        }
        return returnal + "*/\n" +
                MessageFormat.format("{0} abstract {1} {2} ({3});",
                        String.join(" ", modifier),
                        parseType(input.getReturnType()),
                        input.getName(),
                        input.getParameter().entrySet().stream().map(s ->
                                parseType(s.getValue()) + " " + s.getKey()
                        ).collect(Collectors.joining(", ")));


    }

    private String getExtends(ClassData input) {
        if (input.getExtendType() != null) {
            return "extends " + input.getExtendType() + " ";
        }
        return "";
    }

    private String getImplements(ClassData input) {
        if (input.getInterfaces() != null && input.getInterfaces().length > 0) {
            return " implements " + String.join(", ", input.getInterfaces());

        }
        return "";
    }

    private String getImplements(EnumData input) {
        if (input.getImplements().length > 0)
            return " implements " + String.join(", ", input.getImplements());
        return "";
    }
}
