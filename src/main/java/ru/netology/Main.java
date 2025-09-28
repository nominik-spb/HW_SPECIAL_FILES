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
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";
        String saveFileName = "data.json";
        List<Employee> list = parseCSV(columnMapping, fileName);
        String json = listToJson(list);
        writeString(json, list, saveFileName);

        String fileName2 = "data.xml";
        String saveFileName2 = "data2.json";
        List<Employee> list2 = parseXML(fileName2);
        String json2 = listToJson(list2);
        writeString(json2, list2, saveFileName2);
    }

    private static List<Employee> parseCSV(String[] columnMapping, String fileName) {

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

    private static List<Employee> parseXML(String fileName) throws ParserConfigurationException, IOException, SAXException {
        List<Employee> list = new ArrayList<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File(fileName));

        NodeList nodeList = doc.getElementsByTagName("employee");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (Node.ELEMENT_NODE == node.getNodeType()) {
                //System.out.println("Текущий узел: " + node.getNodeName());
                Element element = (Element) node;

                long id = Integer.parseInt(element.getAttribute("id"));
                String firstName = element.getAttribute("firstName");
                String lastName = element.getAttribute("lastName");
                String country = element.getAttribute("country");
                int age = Integer.parseInt(element.getAttribute("age"));

                Employee employee = new Employee(id, firstName, lastName, country, age);
                list.add(employee);
            }
        }
        return list;
    }

    private static void writeString(String json, List<Employee> list, String saveFileName) {
        try (Writer writer = new FileWriter(saveFileName)) {
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
        Type listType = new TypeToken<List<Employee>>() {
        }.getType();
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(list, listType);
        return json;
    }


}