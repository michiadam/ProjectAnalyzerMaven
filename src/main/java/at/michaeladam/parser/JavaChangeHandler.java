package at.michaeladam.parser;

import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.comments.Comment;
import lombok.extern.log4j.Log4j2;

import java.util.Optional;

@Log4j2
public class JavaChangeHandler {

    private JavaChangeHandler(){

    }

    public static void handleClassOrInterfaceDeclarationChange(ClassOrInterfaceDeclaration oldClass, ClassOrInterfaceDeclaration newClass) {
        //handle name change
        if (!oldClass.getNameAsString().equals(newClass.getNameAsString())) {
            log.info("Class name changed from " + oldClass.getNameAsString() + " to " + newClass.getNameAsString());
            oldClass.setName(newClass.getNameAsString());
        }
        if (!oldClass.getExtendedTypes().equals(newClass.getExtendedTypes())) {
            log.info("Class extends changed from " + oldClass.getExtendedTypes() + " to " + newClass.getExtendedTypes());
            oldClass.setExtendedTypes(newClass.getExtendedTypes());
        }
        if (!oldClass.getImplementedTypes().equals(newClass.getImplementedTypes())) {
            log.info("Class implements changed from " + oldClass.getImplementedTypes() + " to " + newClass.getImplementedTypes());
            oldClass.setImplementedTypes(newClass.getImplementedTypes());
        }

        Optional<Comment> oldClassComment = oldClass.getComment();
        Optional<Comment> newClassComment = newClass.getComment();
        if (oldClassComment.isEmpty() || newClassComment.isEmpty()) {
            throw new IllegalArgumentException("Class comment is empty");
        }
        String oldComment = oldClassComment.get().getContent();
        String newComment = newClassComment.get().getContent();

        if (!oldComment.replaceAll("\\s+","").equals(newComment.replaceAll("\\s+",""))) {
            log.info("Class comment changed from {" + oldComment + "} to {" + newComment + "}");
            oldClass.setComment(newClassComment.get());
        }
        if (!oldClass.getAnnotations().equals(newClass.getAnnotations())) {
            log.info("Class annotations changed from " + oldClass.getAnnotations() + " to " + newClass.getAnnotations());
            oldClass.setAnnotations(newClass.getAnnotations());
        }


    }

    public static void handleEnumDeclarationChange(EnumDeclaration oldEnum, EnumDeclaration newEnum) {
        if (!oldEnum.getNameAsString().equals(newEnum.getNameAsString())) {
            log.info("Enum name changed from " + oldEnum.getNameAsString() + " to " + newEnum.getNameAsString());
            oldEnum.setName(newEnum.getNameAsString());

        }
        if (!oldEnum.getImplementedTypes().equals(newEnum.getImplementedTypes())) {
            log.info("Enum implements changed from " + oldEnum.getImplementedTypes() + " to " + newEnum.getImplementedTypes());
            oldEnum.setImplementedTypes(newEnum.getImplementedTypes());
        }
        if (!oldEnum.getAnnotations().equals(newEnum.getAnnotations())) {
            log.info("Enum annotations changed from " + oldEnum.getAnnotations() + " to " + newEnum.getAnnotations());
            oldEnum.setAnnotations(newEnum.getAnnotations());

        }
        if(!JavaChangeHandlerHelper.areConstructorsEqual(oldEnum.getConstructors(), newEnum.getConstructors())) {
            log.info("Enum constructors changed from " + oldEnum.getConstructors() + " to " + newEnum.getConstructors());
            oldEnum.getConstructors().clear();
            oldEnum.getConstructors().addAll(newEnum.getConstructors());
        }
        if(!JavaChangeHandlerHelper.areEnumConstantsEqual(oldEnum.getEntries(), newEnum.getEntries())) {
            log.info("Enum entries changed from " + oldEnum.getEntries() + " to " + newEnum.getEntries());
            oldEnum.setEntries(newEnum.getEntries());
        }
    }

    public static void handleMethodDeclarationChange(MethodDeclaration oldMethod, MethodDeclaration newMethod) {
        if(!oldMethod.getNameAsString().equals(newMethod.getNameAsString())) {
            log.info("Method name changed from " + oldMethod.getNameAsString() + " to " + newMethod.getNameAsString());
            oldMethod.setName(newMethod.getNameAsString());
        }
        if(!oldMethod.getType().equals(newMethod.getType())) {
            log.info("Method type changed from " + oldMethod.getType() + " to " + newMethod.getType());
            oldMethod.setType(newMethod.getType());
        }
        if(!oldMethod.getAnnotations().equals(newMethod.getAnnotations())) {
            log.info("Method annotations changed from " + oldMethod.getAnnotations() + " to " + newMethod.getAnnotations());
            oldMethod.setAnnotations(newMethod.getAnnotations());
        }

        if(!JavaChangeHandlerHelper.areParametersEqual(oldMethod.getParameters(), newMethod.getParameters())) {
            log.info("Method parameters changed from " + oldMethod.getParameters() + " to " + newMethod.getParameters());
            oldMethod.setParameters(newMethod.getParameters());
        }
    }

    public static void handleConstructorDeclarationChange(ConstructorDeclaration oldConstructor, ConstructorDeclaration newConstructor) {
        if(!oldConstructor.getNameAsString().equals(newConstructor.getNameAsString())) {
            log.info("Constructor name changed from " + oldConstructor.getNameAsString() + " to " + newConstructor.getNameAsString());
            oldConstructor.setName(newConstructor.getNameAsString());
        }
        if(!oldConstructor.getAnnotations().equals(newConstructor.getAnnotations())) {
            log.info("Constructor annotations changed from " + oldConstructor.getAnnotations() + " to " + newConstructor.getAnnotations());
            oldConstructor.setAnnotations(newConstructor.getAnnotations());
        }
        if(!JavaChangeHandlerHelper.areParametersEqual(oldConstructor.getParameters(), newConstructor.getParameters())){
            log.info("Constructor parameters changed from " + oldConstructor.getParameters() + " to " + newConstructor.getParameters());
            oldConstructor.setParameters(newConstructor.getParameters());
        }
        if(!oldConstructor.getThrownExceptions().equals(newConstructor.getThrownExceptions())) {
            log.info("Constructor thrown exceptions changed from " + oldConstructor.getThrownExceptions() + " to " + newConstructor.getThrownExceptions());
            oldConstructor.setThrownExceptions(newConstructor.getThrownExceptions());
        }
    }

    public static void handleFieldDeclarationChange(FieldDeclaration oldField, FieldDeclaration newField) {
        if(!oldField.getModifiers().equals(newField.getModifiers())) {
            log.info("Field modifiers changed from " + oldField.getModifiers() + " to " + newField.getModifiers());
            oldField.setModifiers(newField.getModifiers());
        }
        if(!oldField.getVariables().equals(newField.getVariables())) {
            log.info("Field variables changed from " + oldField.getVariables() + " to " + newField.getVariables());
            oldField.setVariables(newField.getVariables());
        }
        if(!oldField.getAnnotations().equals(newField.getAnnotations())) {
            log.info("Field annotations changed from " + oldField.getAnnotations() + " to " + newField.getAnnotations());
            oldField.setAnnotations(newField.getAnnotations());

        }

    }

    public static void handleAnnotationDeclarationChange(AnnotationDeclaration oldAnnotation, AnnotationDeclaration newAnnotation) {
        // TODO implement me
    }

    public static void handleEnumConstantDeclarationChange(EnumConstantDeclaration oldEnumConstant, EnumConstantDeclaration newEnumConstant) {

        if(!oldEnumConstant.getNameAsString().equals(newEnumConstant.getNameAsString())) {
            log.info("Enum constant name changed from " + oldEnumConstant.getNameAsString() + " to " + newEnumConstant.getNameAsString());
        }
        if(!JavaChangeHandlerHelper.areAnnotationsEqual(oldEnumConstant.getAnnotations(), newEnumConstant.getAnnotations())) {
            log.info("Enum constant annotations changed from " + oldEnumConstant.getAnnotations() + " to " + newEnumConstant.getAnnotations());
        }
        if(!JavaChangeHandlerHelper.areArgumentsEqual(oldEnumConstant.getArguments(), newEnumConstant.getArguments())) {
            log.info("Enum constant arguments changed from " + oldEnumConstant.getArguments() + " to " + newEnumConstant.getArguments());
        }
    }
}
