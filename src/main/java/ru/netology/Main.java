package ru.netology;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import com.opencsv.CSVReader;
import com.opencsv.bean.*;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;

import java.lang.reflect.Type;
import java.util.List;

public class Main {
    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";
        List<Employee> list = parseCSV(columnMapping, fileName);
        String json = listToJson(list);
        writeString(json, list);

        List<Employee> list2 = parseXML("data.xml");
    }

    private static List<Employee> parseXML(String fileName) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File("data2.xml"));
        Node root = doc.getDocumentElement();

        System.out.println( "Корневой элемент: " + root.getNodeName());
        read(root);
        Element root = document.createElement("root");
        document.appendChild(root);
    }

    private static void read(Node node) {
        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node_ = nodeList.item(i);
            if (Node.ELEMENT_NODE == node_.getNodeType()) {
                System. out.println( "Текущий узел: " + node_.getNodeName());
                Element element = (Element) node_;
                NamedNodeMap map = element.getAttributes();
                for (int a = 0; a < map.getLength(); a++) {
                    String attrName = map.item(a).getNodeName();
                    String attrValue = map.item(a).getNodeValue();
                    System. out.println( "Атрибут: " + attrName + "; значение: " + attrValue);
                }
                read(node_);
            }
        }
    }

    private static void writeString(String json, List<Employee> list) {
        try(Writer writer = new FileWriter("data2.csv")) {
            StatefulBeanToCsv<Employee> sbc =
                    new StatefulBeanToCsvBuilder<Employee>(writer)
                            .build();
            sbc.write(list);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CsvRequiredFieldEmptyException | CsvDataTypeMismatchException e) {
            e.printStackTrace();
        }
    }

    private static String listToJson(List<Employee> list) {
        Type listType = new TypeToken<List<Employee>>() {}.getType();
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(list, listType);
        return json;
    }

    private static List<Employee> parseCSV (String[] columnMapping, String fileName) {

        try (CSVReader reader = new CSVReader(new FileReader(fileName))) {

            ColumnPositionMappingStrategy<Employee> mappingStrategy = new ColumnPositionMappingStrategy<>();
            mappingStrategy.setType(Employee.class);
            mappingStrategy.setColumnMapping(columnMapping);

            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(reader)
                    .withMappingStrategy(mappingStrategy)
                    .build();
            List<Employee> list = csv.parse();
            //list.forEach(System.out::println);
            return list;

        } catch (IOException e) {
            e.printStackTrace();
            return null;

        }
    }
}