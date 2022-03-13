package at.michaeladam.parser;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.AnnotationExpr;
import lombok.extern.log4j.Log4j2;

import java.util.Objects;

@Log4j2
public class JavaChangeHandlerHelper {

    private JavaChangeHandlerHelper(){

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

    private static boolean areAnnotationsEqual(NodeList<AnnotationExpr> annotations1, NodeList<AnnotationExpr> annotations2) {
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
}
