package shopping;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        String[] products = new String[]{
                "Нарзан 0.5 л.",
                "Шоколад 100 гр.",
                "Йогурт",
                "Сырок глазированный",
                "Пломбир"
        };
        int[] prices = new int[]{80, 100, 50, 30, 70};
        int basketSize = prices.length;
        int productCode;
        int productAmount;
        Basket basket;
        File fileTxt = new File("basket_repo/basket.txt");
        File fileCSV = new File("basket_repo/log.csv");
        File fileJson = new File("basket_repo/basket.json");
        ClientLog clientLog = new ClientLog();

        if (fileJson.exists()) {
            try {

                JSONParser parser = new JSONParser();
                JSONObject jsonObject = (JSONObject) parser.parse(new FileReader(fileJson));
                JSONArray bask = (JSONArray) jsonObject.get("basket");
                basket = new Basket(products, prices);

                int[] temp = new int[basketSize];

                for (int i = 0; i < basketSize; i++) {
                    temp[i] = ((Long) bask.get(i)).intValue();
                }
                basket.setBasket(temp);

                System.out.println("\nИнформация восстановлена из файла-> "
                        + fileJson.getAbsolutePath());
                basket.printSummaryList();
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        } else {
            basket = new Basket(products, prices);
        }
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
            clientLog.log(productCode, productAmount);
        }

        basket.printSummaryList();
        clientLog.exportAsCSV(fileCSV);

        JSONObject obj = new JSONObject();

        JSONArray listPrices = new JSONArray();
        JSONArray listProduct = new JSONArray();
        JSONArray listBasket = new JSONArray();

        Collections.addAll(listProduct, basket.getProducts());

        for (int i = 0; i < basketSize; i++) {
            listBasket.add(basket.getBasket()[i]);
            listPrices.add(basket.getPrices()[i]);

        }
        obj.put("products", listProduct);
        obj.put("prices", listPrices);
        obj.put("basket", listBasket);

        try (FileWriter fileWriter = new FileWriter(fileJson)) {
            fileWriter.write(obj.toJSONString());
            System.out.println("\nИнформация сохранена в файл-> "
                    + fileJson.getAbsolutePath());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}






