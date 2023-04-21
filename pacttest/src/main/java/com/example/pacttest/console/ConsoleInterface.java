package com.example.pacttest.console;

import com.example.pacttest.model.Product;
import com.example.pacttest.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Scanner;
import java.util.stream.IntStream;

@Component
public class ConsoleInterface implements CommandLineRunner {
    private List<Product> products;
    private ProductService productService;

    @Autowired
    ConsoleInterface(ProductService productService) {
        this.productService = productService;
    }

    @Override
    public void run(String... args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            printAllProducts();
            Integer choice = getUserChoice(scanner);
            if(choice == null || choice <= 0 || choice > products.size()) {
                System.out.printf("Wrong choice, exiting...");
                break;
            }
            printProduct(choice);
        }
    }

    private void printProduct(Integer choice) {
        String id = products.get(choice).getId();
        Product product = productService.getProduct(id);
        System.out.println("================Product Details================= \n\n");
        System.out.println(product);
    }

    private Integer getUserChoice(Scanner scanner) {
        System.out.println("Select a product to view: ");
        String choice = scanner.nextLine();
        return parseChoice(choice);
    }

    private Integer parseChoice(String input) {
        try {
            return Integer.parseInt(input);
        } catch (Exception e) {
            return null;
        }
    }

    private void printAllProducts() {
        products = productService.getAllProducts();
        System.out.printf("=================Products==================\n\n");
        IntStream.range(0, products.size()).forEach(index -> {
            System.out.println(String.format("%d) %s", index + 1, products.get(index).getName()));
        });
    }
}
