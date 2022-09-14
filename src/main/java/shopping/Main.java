package shopping;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
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

        File fileTxt = new File("basket_repo/basket.txt");
        File fileCSV = new File("basket_repo/log.csv");
        File fileJson = new File("basket_repo/basket.json");

        ClientLog clientLog = new ClientLog();
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        String stringJson = "";
        Basket basket;

        if (fileJson.exists()) {

            try (BufferedReader reader = new BufferedReader(new FileReader(fileJson))) {
                while (reader.ready()) {
                    stringJson = reader.readLine();
                }
                basket = gson.fromJson(stringJson, Basket.class);
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
            System.out.println("Введите код товара и кол-во через пробел, end - выход.");
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

        stringJson = gson.toJson(basket);
        try (FileWriter writer = new FileWriter(fileJson)) {
            writer.write(stringJson);
            System.out.println("\nИнформация записана в файл -> "
                    + fileJson.getAbsolutePath());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}






