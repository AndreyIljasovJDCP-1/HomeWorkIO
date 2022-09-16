package shopping;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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
        Basket basket;
        ClientLog clientLog = new ClientLog();
        //считываем данные из shop.xml в config последовательно
        Config config = getConfigFromXML();
        //считываем данные из shop.xml в config через рекурсию
        //Config config = getConfigFromXMLRecursion();

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
    }

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
    private static Config getConfigFromXMLRecursion() throws ParserConfigurationException, IOException, SAXException {
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
     * т.е. спускаемся на самый нижний уровень этого узла.
     */
    private static String getTagValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
        Node node = (Node) nodeList.item(0);
        return node.getNodeValue();
    }

}






