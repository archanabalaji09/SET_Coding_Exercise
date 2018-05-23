package com.org.realtor;

import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import org.testng.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class RealtorTest {
	static WebDriver driver;

	public static void main(String[] args) throws InterruptedException {
		
		try {
			//Call function to launch Browser
			launchBrowser();
			
			//Initialize wait to 30 seconds
			WebDriverWait wait = new WebDriverWait(driver, 30);
			
			//Load Realtor.com website
			driver.get("https://www.realtor.com");
			Thread.sleep(5000);
			//Set cookie
			Cookie c = new Cookie("clstr","c");
			driver.manage().addCookie(c);

			driver.findElement(By.xpath("//*[@id=\'img_buy\']/a")).click();
			Thread.sleep(3000);

			//wait until searchBox is located and type Morgantown, WV
			wait.until(ExpectedConditions.presenceOfElementLocated(By.id("searchBox")));
			driver.findElement(By.id("searchBox")).click();
			driver.findElement(By.id("searchBox")).sendKeys("Morgantown, WV");

			//Click on search button
			driver.findElement(By.xpath("//button[contains(@class,'btn btn-primary js-searchButton')]")).click();
			
			//wait until HomeCount is visible and get its value
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id='srp-sort-count-wrap']/span")));
	        WebElement resultValue = driver.findElement(By.xpath("//*[@id='srp-sort-count-wrap']/span"));
	      
	        //Convert value to string and process string to get integer count
	        String searchResult = resultValue.getText().replaceAll("[a-zA-Z,]", "").trim();
	        System.out.println("homeCount = "+ searchResult);
	        if (Integer.parseInt(searchResult) > 0) {
	        	System.out.println("Number of Homes available is greater than zero");
		        Assert.assertTrue(true);
	        } else {
	        	Assert.fail();
	        }
	       
			if(driver.findElements(By.xpath("//*[@id='acsMainInvite']/a")).size() != 0) {
				driver.findElement(By.xpath("//*[@id='acsMainInvite']/a")).click();
			}
			
			
			//Get the second listing price and compare it with price in the listing's detailed page
			if (driver.findElements(By.xpath("//*[@id='srp-list']/div[1]/ul/li")).size() != 0) {
				List<WebElement> vertListOfValues = driver.findElements(By.xpath("//*[@id='srp-list']/div[1]/ul/li"));
				
				String priceInListPage = driver.findElement(By.xpath("//*[@id='srp-list']/div[1]/ul/li[2]/div[2]/div[2]/div/span")).getText();
				System.out.println("Thumbnail Listing Price: "+priceInListPage);
				driver.findElement(By.xpath("//*[@id='srp-list']/div[1]/ul/li[2]/div[2]/div[2]/div/span")).click();
				Thread.sleep(3000);
				String detailPagePrice = driver.findElement(By.xpath("//*[@id='ldp-pricewrap']/div/div/span")).getText();
				System.out.println("detailPagePrice: "+detailPagePrice);

				//Remove special characters and compare absolute number value
				if (Float.parseFloat(priceInListPage.replaceAll("[-+.^:,$]","")) == Float.parseFloat(detailPagePrice.replaceAll("[-+.^:,$]",""))) {
					System.out.println("Price in Listing page is equal to Price in detail page");
		            Assert.assertTrue(true);
		        } else {
		            Assert.fail();
		        }
				
			}
			closeBrowser();
			
		} catch (NoSuchElementException e) {
			System.out.println("Exception received: "+ e.getMessage());
			closeBrowser();
		}
	}

	public static void launchBrowser() {

		System.out.println("Select a browser to execute : 1.Chrome (Default) 2.Firefox");
		Scanner scanner = new Scanner(System.in);
		int input = scanner.nextInt();
		scanner.close();
		switch (input)
        {
            case 1:
            	System.setProperty("webdriver.chrome.driver", ".\\lib\\chromedriver.exe");
        		driver = new ChromeDriver();
                break;
            case 2:
        		System.setProperty("webdriver.gecko.driver",".\\lib\\geckodriver.exe");
                driver = new FirefoxDriver();
                break;
            default:
            	System.setProperty("webdriver.chrome.driver", ".\\lib\\chromedriver.exe");
            	driver = new ChromeDriver();
            	break;
        }

		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		driver.manage().window().maximize();
	}
	
	public static void closeBrowser() {
		System.out.println("Closing browser!");
		driver.close();
	}

}