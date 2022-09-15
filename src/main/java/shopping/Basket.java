package shopping;

import java.io.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Collectors;

public class Basket implements Serializable {

    private int[] basket;
    private int[] prices;
    private String[] products;

    public Basket() {
    }

    public Basket(String[] products, int[] prices) {
        this.prices = prices;
        this.products = products;
        this.basket = new int[products.length];
        addSpaces();
    }

    public void addToList(int productCode, int productAmount) {
        basket[productCode] = productAmount == 0 ? 0
                : Math.max((basket[productCode] + productAmount), 0);
    }

    /**
     * Добавить пробелы в наименование
     * до максимальной длины по названию.
     * Для ровного вывода на печать.
     */
    void addSpaces() {

        int max = Arrays.stream(products)
                .max(Comparator.comparingInt(String::length))
                .orElseGet(String::new)
                .length();

        if (max > 0) {
            for (int i = 0; i < products.length; i++) {
                if (products[i].length() < max) {
                    products[i] += " ".repeat(max - products[i].length());
                }
            }
        }
    }

    public void saveToTxtFile(File textFile) {
        String basketTxt = Arrays.stream(basket)
                .mapToObj(c -> c + " ")
                .collect(Collectors.joining());

        try (BufferedWriter buffer = new BufferedWriter(new FileWriter(textFile))) {
            buffer.write(basketTxt);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    static Basket loadFromTxtFile(File textFile, String[] products, int[] prices) {
        Basket basket;
        try (BufferedReader buffer = new BufferedReader(new FileReader(textFile))) {
            String fileData = "";
            while (buffer.ready()) {
                fileData = buffer.readLine();
            }
            buffer.close();
            basket = new Basket(products, prices);
            basket.basket = Arrays.stream(fileData.split(" "))
                    .mapToInt(Integer::parseInt)
                    .toArray();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        return basket;
    }

    public String[] getProducts() {
        return products;
    }

    public int[] getBasket() {
        return basket;
    }

    public int[] getPrices() {
        return prices;
    }

    public void printSummaryList() {
        int basketSum = 0;

        System.out.println("\nВаша потребительская корзина:");

        for (int i = 0, j = 1; i < products.length; i++) {
            if (basket[i] > 0) {
                System.out.printf("%d) код %d. %s Цена: %3d руб. Количество: %3d шт. Всего на: %4d руб.\n",
                        j, i + 1, products[i], prices[i], basket[i], basket[i] * prices[i]);
                basketSum += basket[i] * prices[i];
                j++;
            }
        }
        System.out.println("Итого " + basketSum + " руб.");
    }

    public void printPossibleList() {
        System.out.println("\nСписок возможных товаров для покупки:");

        for (int i = 0; i < basket.length; i++) {
            System.out.printf("%d. %s Цена: %3d руб/шт.\n",
                    i + 1, products[i], prices[i]);
        }
    }
}
