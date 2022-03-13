package at.michaeladam.data.shared;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class GeneratorIDTest {


    private final String exampleComment = "asdasd\n" +
                                          "@GeneratorID 9d64c8cd-3c82-47f1-b0d3-79d734d62ef2\n" +
                                          "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.\n" +
                                          "Vitae turpis massa sed elementum tempus egestas sed sed. Gravida rutrum quisque non tellus orci ac.\n" +
                                          "Rhoncus est pellentesque elit ullamcorper dignissim cras tincidunt lobortis feugiat.\n" +
                                          "Tellus rutrum tellus pellentesque eu tincidunt tortor aliquam nulla facilisi.\n" +
                                          "\n" +
                                          "Dignissim sodales ut eu sem integer vitae justo eget. Aenean pharetra magna ac placerat vestibulum lectus mauris ultrices eros.\n" +
                                          "Fermentum dui faucibus in ornare. Nullam ac tortor vitae purus faucibus. Tortor at risus viverra adipiscing.\n" +
                                          "Arcu ac tortor dignissim convallis aenean et tortor at.\n" +
                                          "\n" +
                                          "Elementum nibh tellus molestie nunc non blandit. Sit amet nisl suscipit adipiscing bibendum est.\n" +
                                          "Curabitur vitae nunc sed velit. Consectetur adipiscing elit duis tristique. Enim eu turpis egestas pretium aenean pharetra.\n" +
                                          "Tincidunt augue interdum velit euismod in pellentesque. Nisl rhoncus mattis rhoncus urna neque viverra justo nec.\n" +
                                          "Consectetur adipiscing elit duis tristique sollicitudin nibh.\n" +
                                          "\n";

    @Test
    void testGenerateID() {
        UUID id = SharedData.extractID(exampleComment);
        assertNotNull(id);
    }
}