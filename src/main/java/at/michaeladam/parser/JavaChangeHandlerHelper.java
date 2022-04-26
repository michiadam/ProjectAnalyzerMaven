package at.michaeladam.parser;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.EnumConstantDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.Expression;
import lombok.extern.log4j.Log4j2;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Log4j2
public class JavaChangeHandlerHelper {

    private JavaChangeHandlerHelper() {

    }

    public static boolean areParametersEqual(NodeList<Parameter> parameter1, NodeList<Parameter> parameter2) {

        if (parameter1.size() != parameter2.size()) {
            return false;
        }
        for (int i = 0; i < parameter1.size(); i++) {
            if (!Objects.equals(parameter1.get(i).getType().asString(), parameter2.get(i).getType().asString())) {
                log.info(parameter1.get(i).getType() + " != " + parameter2.get(i).getType());
                return false;
            }
            if (!Objects.equals(parameter1.get(i).getNameAsString(), parameter2.get(i).getNameAsString())) {
                log.info(parameter1.get(i).getNameAsString() + " != " + parameter2.get(i).getNameAsString());
                return false;
            }
            if (!areAnnotationsEqual(parameter1.get(i).getAnnotations(), parameter2.get(i).getAnnotations())) {
                return false;
            }
        }
        return true;
    }

    public static boolean areAnnotationsEqual(NodeList<AnnotationExpr> annotations1, NodeList<AnnotationExpr> annotations2) {
        if (annotations1.size() != annotations2.size()) {
            return false;
        }
        for (int i = 0; i < annotations1.size(); i++) {
            if (!Objects.equals(annotations1.get(i).getNameAsString(), annotations2.get(i).getNameAsString())) {
                log.info(annotations1.get(i).getNameAsString() + " != " + annotations2.get(i).getNameAsString());
                return false;
            }
        }
        return true;
    }

    public static boolean areEnumConstantsEqual(NodeList<EnumConstantDeclaration> entries, NodeList<EnumConstantDeclaration> entries1) {
        if (entries.size() != entries1.size()) {
            return false;
        }
        for (int i = 0; i < entries.size(); i++) {
            if (!Objects.equals(entries.get(i).getNameAsString(), entries1.get(i).getNameAsString())) {
                log.info(entries.get(i).getNameAsString() + " != " + entries1.get(i).getNameAsString());
                return false;
            }
            if (!areAnnotationsEqual(entries.get(i).getAnnotations(), entries1.get(i).getAnnotations())) {
                return false;
            }
            if (!areArgumentsEqual(entries.get(i).getArguments(), entries1.get(i).getArguments())) {
                return false;
            }
            if(!areCommentsEqual(entries.get(i).getComment(), entries1.get(i).getComment())){
                return false;
            }
        }
        return true;
    }

    public static boolean areCommentsEqual(Optional<Comment> comment, Optional<Comment> comment1) {

        if(comment.isEmpty() || comment1.isEmpty()){
            return false;
        }
        boolean equals = Objects.equals(comment.get().toString(), comment1.get().toString());

        if(!equals){
            log.info(comment.get().toString() + " != " + comment1.get().toString());
        }
        return equals;
    }

    public static boolean areArgumentsEqual(NodeList<Expression> arguments, NodeList<Expression> arguments1) {
        if (arguments.size() != arguments1.size()) {
            return false;
        }
        for (int i = 0; i < arguments.size(); i++) {
            if (!Objects.equals(arguments.get(i).toString(), arguments1.get(i).toString())) {
                log.info(arguments.get(i).toString() + " != " + arguments1.get(i).toString());
                return false;
            }
        }
        return true;
    }

    public static boolean areConstructorsEqual(List<ConstructorDeclaration> constructors, List<ConstructorDeclaration> constructors1) {
        if(constructors.size() != constructors1.size()){
            return false;
        }
        for (int i = 0; i < constructors.size(); i++) {
            if (!Objects.equals(constructors.get(i).getNameAsString(), constructors1.get(i).getNameAsString())) {
                log.info(constructors.get(i).getNameAsString() + " != " + constructors1.get(i).getNameAsString());
                return false;
            }
            if (!areParametersEqual(constructors.get(i).getParameters(), constructors1.get(i).getParameters())) {
                return false;

            }
            if (!areAnnotationsEqual(constructors.get(i).getAnnotations(), constructors1.get(i).getAnnotations())) {
                return false;

            }
            if(!areModifiersEqual(constructors.get(i).getModifiers(), constructors1.get(i).getModifiers())){
                return false;
            }

        }
        return true;
    }

    private static boolean areModifiersEqual(NodeList<Modifier> modifiers, NodeList<Modifier> modifiers1) {
        if(modifiers.size() != modifiers1.size()){
            return false;
        }
        for (int i = 0; i < modifiers.size(); i++) {
            if (!Objects.equals(modifiers.get(i).toString(), modifiers1.get(i).toString())) {
                log.info(modifiers.get(i).toString() + " != " + modifiers1.get(i).toString());
                return false;
            }

        }

        return true;
    }
}