import graph.Graph;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.ParseException;

/**
 * Created by Martin Šícho on 3.10.14.
 */
public class Main {

    public static void main(String[] args) {
        Reader reader = new StringReader("6;1-2,2-3,3-4,4-5,5-6,6-1");
        StringWriter string_writer = new StringWriter();
        try {
            Graph graph = Graph.readGraph(reader);
            graph.writeGraph(string_writer);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        System.out.println(String.format("Writing representation: %s", string_writer.toString()));
    }
}
