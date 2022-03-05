package at.michaeladam;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.AnnotationExpr;
import lombok.Data;

@Data
public class AnnotationData {

    private String name;

    private AnnotationData(){
        name ="";
    }

    public static AnnotationData[] of(NodeList<AnnotationExpr> annotations) {
        AnnotationData[] result = new AnnotationData[annotations.size()];
        for (int i = 0; i < annotations.size(); i++) {
            AnnotationData data = new AnnotationData();
            data.name = annotations.get(i).getName().toString();
            result[i] = data;
        }
        return result;
    }
}
