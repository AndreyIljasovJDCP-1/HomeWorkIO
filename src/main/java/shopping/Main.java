package shopping;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {
        Scanner scanner = new Scanner(System.in);
        String[] products = new String[]{
                "Нарзан 0.5 л.",
                "Шоколад 100 гр.",
                "Йогурт",
                "Сырок глазированный",
                "Пломбир"
        };
        int[] prices = new int[]{80, 100, 50, 30, 70};
        int basketSize = products.length;
        int productCode;
        int productAmount;
        ClientLog clientLog = new ClientLog();
        Basket basket;
        Config config;
        File fileConfig=new File("basket_repo/config.sys");
        // todo загрузить из файла config.sys
        if (fileConfig.exists()) {
            config = loadConfigGson(fileConfig);
        } else {
            System.out.printf("Файла %s конфигурации не существует: \n"
                            + " Настройки будут загружены из shop.xml",
                    fileConfig.getAbsolutePath());

            //считываем данные из shop.xml в config последовательно
            config = getConfigFromXML();
            //считываем данные из shop.xml в config через рекурсию
            //Config config = getConfigFromXMLRecursion();
        }


        basket = config.isLoadEnabled()
                ? Basket.loadFromFile(config.getLoadFileName(), config.getLoadFileFormat(), products, prices)
                : new Basket(products, prices);
        basket.printPossibleList();

        while (true) {
            System.out.println("\nВведите код товара и кол-во через пробел, end - выход.");
            String input = scanner.nextLine();
            if ("end".equals(input)) {
                break;
            }

            String[] parts = input.split(" ");
            if (parts.length != 2) {
                System.out.println("Ошибка! Нужно ввести 2 числа через пробел.");
                continue;
            }

            try {
                productCode = Integer.parseInt(parts[0]) - 1;
                if (productCode < 0 || productCode > basketSize - 1) {
                    System.out.println("Ошибка! Код товара д.б. от 1 до " + basketSize);
                    continue;
                }

                productAmount = Integer.parseInt(parts[1]);
            } catch (NumberFormatException e) {
                System.out.println("Ошибка! Введены некорректные данные!");
                continue;
            }

            basket.addToList(productCode, productAmount);
            basket.saveToFile(config.getSaveFileName(), config.getSaveFileFormat(), config.isSaveEnabled());
            clientLog.log(productCode, productAmount, config.isLogEnabled());
        }

        basket.printSummaryList();
        basket.messageSave(config.getSaveFileName(), config.isSaveEnabled());
        clientLog.exportAsCSV(config.getLogFileName(), config.isLogEnabled());
        //todo сохранить настройки в config.sys
        saveConfigGson(config,fileConfig);
    }
     /**
             * Десериализация через Gson
     */
    private static Config loadConfigGson(File fileConfig) throws IOException {
        Config config;
        String stringJson="";
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        Gson gson = builder.create();

        /*stringJson= Files.readAllLines(
                Path.of(fileConfig.getPath())).stream().collect(Collectors.joining());*/
        try (BufferedReader reader = new BufferedReader(new FileReader(fileConfig))) {
            while (reader.ready()) {
                stringJson = reader.lines().collect(Collectors.joining(System.lineSeparator()));
            }
            config = gson.fromJson(stringJson, Config.class);
            System.out.println("\nИнформация восстановлена из файла-> "
                    + fileConfig.getAbsolutePath());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        return config;
    }

    /**
     * Сериализация через Gson
     */
    private static void saveConfigGson(Config config,File fileConfig) {
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        Gson gson = builder.create();
        String stringJson = gson.toJson(config);
        try (FileWriter writer = new FileWriter(fileConfig)) {
            writer.write(stringJson);
            System.out.println("\nКонфигурация записана в файл -> "
                    + fileConfig.getAbsolutePath());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

    }


    /**
     * Сериализация через writeObject
     *//*
    private static void saveConfig(Config config) {
        try (ObjectOutputStream out = new ObjectOutputStream(
                new FileOutputStream("basket_repo/config.sys"))) {
            out.writeObject(config);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    *//**
     * Десериализация через readObject
     *//*
    private static Config loadConfig() {
        Config config;
        try (ObjectInputStream in = new ObjectInputStream(
                new FileInputStream("basket_repo/config.sys"))) {
            config = (Config) in.readObject();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        return config;
    }*/

    /**
     * считать данные из shop.xml в лист dataXml последовательно
     *
     * @return Config config
     */
    private static Config getConfigFromXML() throws ParserConfigurationException, IOException, SAXException {
        Config config = new Config();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File("shop.xml"));
        doc.getDocumentElement().normalize();

        NodeList nodeList = doc.getElementsByTagName("load");
        Node node = nodeList.item(0);
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element) node;
            config.setLoadEnabled(element.getElementsByTagName("enabled").item(0).getTextContent());
            config.setLoadFileName(element.getElementsByTagName("fileName").item(0).getTextContent());
            config.setLoadFileFormat(element.getElementsByTagName("format").item(0).getTextContent());
        }
        nodeList = doc.getElementsByTagName("save");
        node = nodeList.item(0);
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element) node;
            config.setSaveEnabled(element.getElementsByTagName("enabled").item(0).getTextContent());
            config.setSaveFileName(element.getElementsByTagName("fileName").item(0).getTextContent());
            config.setSaveFileFormat(element.getElementsByTagName("format").item(0).getTextContent());
        }
        nodeList = doc.getElementsByTagName("log");
        node = nodeList.item(0);
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element) node;
            config.setLogEnabled(element.getElementsByTagName("enabled").item(0).getTextContent());
            config.setLogFileName(element.getElementsByTagName("fileName").item(0).getTextContent());
        }
        return config;
    }

    /**
     * считать данные из shop.xml в лист dataXml через рекурсию
     *
     * @return Config config
     */
    private static Config getConfigFromXMLRecursion() throws
            ParserConfigurationException, IOException, SAXException {
        Config config = new Config();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File("shop.xml"));
        Node root = doc.getDocumentElement();
        List<String> dataXml = new ArrayList<>();
        readXML(root, dataXml);

        config.setLoadEnabled(dataXml.get(0));
        config.setLoadFileName(dataXml.get(1));
        config.setLoadFileFormat(dataXml.get(2));
        config.setSaveEnabled(dataXml.get(3));
        config.setSaveFileName(dataXml.get(4));
        config.setSaveFileFormat(dataXml.get(5));
        config.setLogEnabled(dataXml.get(6));
        config.setLogFileName(dataXml.get(7));
        return config;
    }

    private static void readXML(Node node, List<String> dataXml) {
        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node currentNode = nodeList.item(i);

            if (Node.ELEMENT_NODE == currentNode.getNodeType()) {
                Element element = (Element) currentNode;
                if (element.getChildNodes().getLength() == 1) {
                    dataXml.add(element.getTextContent());
                }
                readXML(currentNode, dataXml);
            }
        }
    }

    /**
     * Это будет работать, если несколько повторяющихся
     * узлов <>load</>, к каждому создается объект.
     */
    private static Config getConfig(Node node) {
        Config config = new Config();
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element) node;
            config.setLoadEnabled(getTagValue("enabled", element));
            config.setLoadFileName(getTagValue("fileName", element));
            config.setLoadFileFormat(getTagValue("format", element));
        }
        return config;
    }

    /**
     * Получаем значение последнего элемента в списке
     * т.е. спускаемся на самый нижний уровень этого узла, деток нет.
     */
    private static String getTagValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
        Node node = (Node) nodeList.item(0);
        return node.getNodeValue();
    }

}






