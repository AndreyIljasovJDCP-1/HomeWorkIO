import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        int productCode;
        int productAmount;
        Basket basket;
        String fileName = "basket.txt";

        File file = new File(fileName);

        try {
            if (file.createNewFile()) {
                System.out.println("\nСоздан файл для хранения корзины-> "
                        + file.getAbsolutePath());
                basket = new Basket();
            } else {
                basket = Basket.loadFromTxtFile(file);
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
            basket.saveToTxtFile(file);
        }
        basket.printSummaryList();
    }
}






