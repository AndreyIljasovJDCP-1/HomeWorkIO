package shopping;

import com.fasterxml.jackson.databind.ObjectMapper;
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
        int productCode;
        int productAmount;

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File("shop.xml"));
        doc.getDocumentElement().normalize();
        Node root = doc.getDocumentElement();

        List<String> dataXml = new ArrayList<>();
        readConfigXML(root, dataXml);

        Basket basket;
        ClientLog clientLog = new ClientLog();
        Config config = new Config();

        config.setLoadEnabled(dataXml.get(0));
        config.setLoadFileName(dataXml.get(1));
        config.setLoadFileExtension(dataXml.get(2));
        config.setSaveEnabled(dataXml.get(3));
        config.setSaveFileName(dataXml.get(4));
        config.setSaveFileExtension(dataXml.get(5));
        config.setLogEnabled(dataXml.get(6));
        config.setLogFileName(dataXml.get(7));

        File fileLoad = new File(config.getLoadFileName());
        File fileSave = new File(config.getSaveFileName());
        File fileCSV = new File(config.getLogFileName());

        if (config.isLoadEnabled()) {
            if (config.getLoadFileExtension().equals("json")) {
                if (fileLoad.exists()) {
                    try {
                        ObjectMapper mapper = new ObjectMapper();
                        basket = mapper.readValue(fileLoad, Basket.class);
                        System.out.println("\nКорзина восстановлена из файла-> "
                                + fileLoad.getAbsolutePath());
                        basket.printSummaryList();
                    } catch (Exception e) {
                        throw new RuntimeException(e.getMessage());
                    }

                } else {
                    basket = new Basket(products, prices);
                }
            } else {
                try {
                    if (fileLoad.createNewFile()) {
                        System.out.println("\nСоздан файл для хранения корзины-> "
                                + fileLoad.getAbsolutePath());
                        basket = new Basket(products, prices);
                    } else {

                        basket = Basket.loadFromTxtFile(fileLoad, products, prices);
                        System.out.println("\nКорзина восстановлена из файла-> "
                                + fileLoad.getAbsolutePath());
                        basket.printSummaryList();
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        } else {
            basket = new Basket(products, prices);
        }

        basket.printPossibleList();
        int basketSize = basket.getProducts().length;

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
            } catch (NumberFormatException e) {
                System.out.println("Ошибка! Введены некорректные данные!");
                continue;
            }

            try {
                productAmount = Integer.parseInt(parts[1]);
            } catch (NumberFormatException e) {
                System.out.println("Ошибка! Введены некорректные данные!");
                continue;
            }

            basket.addToList(productCode, productAmount);

            if (config.isSaveEnabled()) {
                if (config.getSaveFileExtension().equals("json")) {

                    try {
                        ObjectMapper mapper = new ObjectMapper();
                        mapper.writeValue(fileSave, basket);
                        messageSave(fileSave);
                    } catch (Exception e) {
                        throw new RuntimeException(e.getMessage());
                    }
                } else {
                    basket.saveToTxtFile(fileSave);
                    messageSave(fileSave);
                }
            }

            if (config.isLogEnabled()) {
                clientLog.log(productCode, productAmount);
            }
        }
        basket.printSummaryList();
        if (config.isLogEnabled()) {
            clientLog.exportAsCSV(fileCSV);
        }

    }

    private static void readConfigXML(Node node, List<String> dataXml) {
        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node currentNode = nodeList.item(i);

            if (Node.ELEMENT_NODE == currentNode.getNodeType()) {
                Element element = (Element) currentNode;
                if (currentNode.getChildNodes().getLength() == 1) {
                    dataXml.add(element.getTextContent());
                }
                readConfigXML(currentNode, dataXml);
            }
        }
    }

    public static void messageSave(File file) {
        System.out.println("\nИнформация сохранена в файл-> "
                + file.getAbsolutePath());
    }
    
}






