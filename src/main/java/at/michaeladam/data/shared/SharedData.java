package at.michaeladam.data.shared;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.javaparser.ast.comments.Comment;
import lombok.Data;
import lombok.extern.log4j.Log4j2;

import java.text.MessageFormat;
import java.util.UUID;

@Data
@Log4j2
public abstract class SharedData {

    private static final String ID_PREFIX = "@GeneratorID";
    private UUID generatorId;

    public void extractID(Comment comment) {
        if (comment != null) {
            String commentText = comment.getContent();
            this.generatorId = extractID(commentText);
        }
        if (this.generatorId == null) {
            this.generatorId = UUID.randomUUID();
        }
    }

    public static UUID extractID(String commentText) {
        if (commentText.contains(ID_PREFIX)) {
            //if the comment contains the @GeneratorID tag, extract the UUID which is right after the tag and a space and before the next line
            int beginIndex = commentText.indexOf(ID_PREFIX) + ID_PREFIX.length() + 1;
            String id = commentText.substring(beginIndex, commentText.indexOf("\n",beginIndex)-1);

            //if the id is a valid UUID, return it

            try {

                return UUID.fromString(id);
            } catch (Exception e) {

                UUID uuid = UUID.randomUUID();
                log.warn("Invalid UUID in comment: " + id);
                log.warn("Invalid UUID in comment: " + id + " - using random UUID: " + uuid);
                return UUID.randomUUID();
            }


        }
        return null;
    }

    @JsonIgnore
    public String getComment(){
        return MessageFormat.format("\n{0} {1}\nGenerated code, any changes to header or parameters will be lost!\n", ID_PREFIX, this.generatorId);
    }

}
