package org.tkopec;

import org.jgrapht.VertexFactory;
import org.jgrapht.generate.GridGraphGenerator;
import org.jgrapht.generate.ScaleFreeGraphGenerator;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by tkopec on 23/05/16.
 */
class GraphGenerator {
    void generateAndSave(int rows, int columns,
                         double weightMin, double weightMax) {
        saveToXml(generate(rows, columns),
                weightMin, weightMax);
    }

    private DefaultDirectedGraph<String, DefaultEdge> generate(
            int rows, int columns){
        VertexFactory<String> vertexFactory = new VertexFactory<String>() {
            int n = 0;

            public String createVertex() {
                n++;
                return String.valueOf(n);
            }
        };

        DefaultDirectedGraph<String, DefaultEdge> target =
                new DefaultDirectedGraph<String, DefaultEdge>(DefaultEdge.class);
        Map<String, String> results = new HashMap<String, String>();

        GridGraphGenerator<String, DefaultEdge> graphGenerator =
                new GridGraphGenerator<String, DefaultEdge>(rows, columns);
        graphGenerator.generateGraph(target, vertexFactory, results);

        System.out.println(target);

        return target;
    }

    private double getRandomWeight(double weightMin, double weightMax) {
        Random random = new Random();
        return weightMin + (weightMax - weightMin) * random.nextDouble();
    }

    private void saveToXml(DefaultDirectedGraph<String, DefaultEdge> target,
                           double weightMin, double weightMax) {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();

        DocumentBuilder dBuilder;
        Document document;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
            document = dBuilder.newDocument();

            // root element
            Element graph = document.createElement("graph");
            document.appendChild(graph);

            Element vertices = document.createElement("vertices");
            graph.appendChild(vertices);

            for (String v : target.vertexSet()) {
                Element vertex = document.createElement("vertex");
                vertex.setAttribute("name", v);

                Element neighbours = document.createElement("neighbours");

                for (DefaultEdge edge : target.outgoingEdgesOf(v)) {
                    Element neighbour = document.createElement("neighbour");
                    neighbour.setAttribute("name", target.getEdgeTarget(edge));

                    Element weights = document.createElement("weights");
                    for (int i = 0; i < 4; i++) {
                        Element weight = document.createElement("weight");
                        weight.appendChild(document.createTextNode(
                                String.format("%.2f",
                                        getRandomWeight(weightMin, weightMax))
                        ));
                        weights.appendChild(weight);
                    }

                    neighbour.appendChild(weights);
                    neighbours.appendChild(neighbour);
                }

                vertex.appendChild(neighbours);
                vertices.appendChild(vertex);
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(new File("graph.xml"));

            // for pretty print
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

            // write to result
            transformer.transform(source, result);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }

    }
}
