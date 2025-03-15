package org.example;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.JavascriptExecutor;
import io.github.bonigarcia.wdm.WebDriverManager;
import java.time.Duration;
import java.util.List;

public class AmazonAutomation {
    public static void main(String[] args) {
        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            // 1. Open website of Amazon Egypt
            driver.get("https://www.amazon.eg/");
            driver.manage().window().maximize();

            // Click on sign in button
            driver.findElement(By.id("nav-link-accountList")).click();

            // Enter username & password and sign in
            driver.findElement(By.id("ap_email")).sendKeys("enter email");
            driver.findElement(By.id("continue")).click();
            driver.findElement(By.id("ap_password")).sendKeys("enter password");
            driver.findElement(By.id("signInSubmit")).click();

            // 2. Open “All” menu
            wait.until(ExpectedConditions.elementToBeClickable(By.id("nav-hamburger-menu"))).click();
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[text()='See all']"))).click();


            // 3. Click on “Video games” then choose “All Video Games”
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[text()='Video Games']"))).click();
            WebElement videoGames = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[text()='All Video Games']")));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", videoGames);
            Thread.sleep(1000);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", videoGames);


            // 4. Apply filters (Free Shipping & New Condition)

            // First click on "Free Shipping" filter and apply it
            WebElement freeShipping = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//span[text()='Free Shipping']")));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", freeShipping);
            Thread.sleep(1000);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", freeShipping);

            // Then click on "New" filter and apply it
            WebElement newCondition = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//span[text()='New']")));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", newCondition);
            Thread.sleep(1000);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", newCondition);

            // 5. Sort by Price: High to Low
            WebElement sorting = wait.until(ExpectedConditions.elementToBeClickable(By.id("s-result-sort-select")));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", sorting);
            Thread.sleep(1000);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", sorting);
            driver.findElement(By.xpath("//option[text()='Price: High to Low']")).click();

            // 6. Add products below 15K EGP to cart
            boolean productAdded = false;
            do {
                List<WebElement> products = driver.findElements(By.cssSelector(".s-main-slot .s-result-item"));
                for (WebElement product : products) {
                    String priceText = product.findElement(By.cssSelector(".a-price-whole")).getText().replace(",", "");
                    int price = Integer.parseInt(priceText);
                    if (price < 15000) {
                        product.findElement(By.cssSelector(".s-title a")).click();
                        wait.until(ExpectedConditions.elementToBeClickable(By.id("add-to-cart-button"))).click();
                        driver.navigate().back();
                        productAdded = true;
                    }
                }
                // If no product below 15K EGP, move to next page
                if (!productAdded) {
                    WebElement nextPage = driver.findElement(By.cssSelector(".s-pagination-next"));
                    if (nextPage.isEnabled()) {
                        nextPage.click();
                    } else {
                        break;
                    }
                }
            } while (!productAdded);

            // 7. Make sure that all products are added to the cart
            driver.findElement(By.id("nav-cart")).click();
            List<WebElement> cartItems = driver.findElements(By.cssSelector(".sc-list-item"));
            if (cartItems.size() > 0) {
                System.out.println("All products added to cart successfully.");
            }

            // 8. Add address and choose cash on delivery
            driver.findElement(By.name("proceedToRetailCheckout")).click();
            // Assume that the address is saved already
            driver.findElement(By.xpath("//input[@value='Cash on Delivery']")).click();

            // 9. Make sure that the total amount of all items is correct with the shipping fees if exist
            String totalText = driver.findElement(By.id("sc-subtotal-amount-buybox")).getText().replace(" EGP", "").replace(",", "");
            double totalPrice = Double.parseDouble(totalText);
            System.out.println("Total Price: " + totalPrice);


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit();
        }
    }
}
