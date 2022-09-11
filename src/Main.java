import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
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
        Basket basket;
        String fileName = "basket.bin";

        File file = new File(fileName);

        try {
            if (file.createNewFile()) {
                System.out.println("\nСоздан файл для хранения корзины-> "
                        + file.getAbsolutePath());
                basket = new Basket(products, prices);
            } else {
                basket = Basket.loadFromBinFile(file);
                System.out.println("\nКорзина восстановлена из файла-> "
                        + file.getAbsolutePath());
                basket.printSummaryList();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        basket.printPossibleList();

        while (true) {
            int basketSize = basket.getProducts().length;
            System.out.println("\nВведите код товара(1-5) и кол-во через пробел, end -> выход.");
            String input = scanner.nextLine();

            if ("end".equals(input)) {
                break;
            }

            String[] parts = input.split(" ");

            if (parts.length != 2) {
                errorMessage();
                continue;
            }

            try {
                productCode = Integer.parseInt(parts[0]) - 1;

                if (productCode < 0 || productCode > basketSize - 1) {
                   errorMessage();
                    continue;
                }
            } catch (NumberFormatException e) {
                errorMessage();
                continue;
            }

            try {
                productAmount = Integer.parseInt(parts[1]);
            } catch (NumberFormatException e) {
                errorMessage();
                continue;
            }

            basket.addToList(productCode, productAmount);
            basket.saveToBinFile(file);
        }
        basket.printSummaryList();
    }
    public static void errorMessage(){
        System.out.println("Ошибка! Введены некорректные данные!");
    }
}






