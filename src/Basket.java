import java.io.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Collectors;

public class Basket {
    private int[] basket;
    private final int[] prices;
    private final String[] products;


    public Basket() {
        this.prices = new int[]{80, 100, 50, 30, 70};
        this.products = new String[]{
                "Нарзан 0.5 л.",
                "Шоколад 100 гр.",
                "Йогурт",
                "Сырок глазированный",
                "Пломбир"
        };
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

    public void saveToTxtFile(File textFile) throws IOException {

        String basketTxt = Arrays.stream(basket)
                .mapToObj(c -> c + " ")
                .collect(Collectors.joining());

        BufferedWriter buffer = new BufferedWriter(new FileWriter(textFile));
        buffer.write(basketTxt);
        buffer.close();
    }

    static Basket loadFromTxtFile(File textFile) throws IOException {

        BufferedReader buffer = new BufferedReader(new FileReader(textFile));
        String fileData = "";
        while (buffer.ready()) {
            fileData = buffer.readLine();
        }
        buffer.close();
        Basket basket = new Basket();
        basket.basket = Arrays.stream(fileData.split(" "))
                .mapToInt(Integer::parseInt)
                .toArray();
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
