import java.io.*;
import java.util.Arrays;
import java.util.Comparator;

public class Basket implements Serializable {
    private int[] basket;
    private int[] prices = new int[]{};
    private String[] products = new String[]{};

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

    public void saveToBinFile(File textFile) {

        try (FileOutputStream fos = new FileOutputStream(textFile);
             ObjectOutputStream basketWriter = new ObjectOutputStream(fos)) {

            basketWriter.writeObject(this);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    static Basket loadFromBinFile(File textFile) {

        Basket basket;

        try (FileInputStream fis = new FileInputStream(textFile);
             ObjectInputStream basketReader = new ObjectInputStream(fis)) {
            basket = (Basket) basketReader.readObject();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        return basket;
    }

    public String[] getProducts() {
        return products;
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
