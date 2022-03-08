package at.michaeladam.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static at.michaeladam.data.TypeData.extractGeneric;

public class ExtractionHelper {

    private ExtractionHelper(){}

    public static List<String> extractTypesOfGenericField(String field) {
        String generic = field.substring(field.indexOf("<") + 1, field.lastIndexOf(">"));

        List<String> types = new ArrayList<>();
        if (generic.contains("<")) {
            extractGeneric(generic, types);

        } else {
            if (generic.contains(",")) {
                types.addAll(Arrays.asList(generic.split(",")));
            } else {
                types.add(generic);
            }
        }
        return types;
    }
}
