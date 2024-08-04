package com.fitpro;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.bonigarcia.wdm.WebDriverManager;

public class FitProAssessment {

	WebDriver driver;

	@SuppressWarnings("deprecation")
	@BeforeClass

	public void preCondition() {
		ChromeOptions options = new ChromeOptions();
		WebDriverManager.chromedriver().setup();

		driver = new ChromeDriver(options);
		options.addArguments("--incognito");
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		driver.manage().window().maximize();
		//Open the web browser and navigate to FitPeo Homepage.
		driver.get("https://www.fitpeo.com/");
	}

	@Test
	public void startup() throws InterruptedException, JsonProcessingException, IOException

	{

		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
		ObjectMapper mapper = new ObjectMapper();
		InputStream inputStream = FitProAssessment.class.getClassLoader().getResourceAsStream("fitprodata.json");

		System.out.println(inputStream);
		JsonNode rootNode = mapper.readTree(inputStream);
		JsonNode xpathsNode = rootNode.path("fitpro_xpaths");

		// Read XPath values from json
		String Revenue_Calculator_Page = xpathsNode.path("Revenue_Calculator_Page").asText();
		String slider_value = xpathsNode.path("slider_value").asText();
		String sliderThumbCss = xpathsNode.path("sliderThumbCss").asText();
		String sliderTrackCss = xpathsNode.path("sliderTrackCss").asText();
		String inputField = xpathsNode.path("inputField").asText();
		String checkboxes_CPT_99091 = xpathsNode.path("checkboxes_CPT_99091").asText();
		String checkboxes_CPT_99453 = xpathsNode.path("checkboxes_CPT_99453").asText();
		String checkboxes_CPT_99454 = xpathsNode.path("checkboxes_CPT_99454").asText();
		String checkboxes_CPT_99474 = xpathsNode.path("checkboxes_CPT_99474").asText();

		// Action Perform

		try {
			
			//From the homepage, navigate to the Revenue Calculator Page.
			driver.findElement(By.xpath(Revenue_Calculator_Page)).click();
			Thread.sleep(1000);
			Actions actions = new Actions(driver);
			actions.sendKeys(Keys.PAGE_DOWN).perform();

			/*
			 * Adjust the slider to set its value to 820. we’ve highlighted the slider in
			 * red color, Once the slider is moved the bottom text field value should be
			 * updated to 820
			 */
			WebElement sliderThumb = wait
					.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(sliderThumbCss)));
			WebElement sliderTrack = driver.findElement(By.cssSelector(sliderTrackCss));
			int trackWidth = sliderTrack.getSize().width;
			int thumbWidth = sliderThumb.getSize().width;
			int desiredValue = 820;
			int minValue = 0;
			int maxValue = 244;
			double valuePercentage = (double) (desiredValue - minValue) / (maxValue - minValue);
			int offset = (int) (trackWidth * valuePercentage) - (thumbWidth / 2);

			// Use Actions class to click and drag the slider thumb to the desired value
			actions.clickAndHold(sliderThumb).moveByOffset(offset, 0).release().perform();
			Thread.sleep(3000);

			int numberInputFields = 820;

			driver.findElement(By.xpath(inputField)).sendKeys(String.valueOf(numberInputFields));

			// Enter the value 560 in the text field. Now the slider also should change
			// accordingly
			WebElement numberInputField = wait
					.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(slider_value)));

			JavascriptExecutor js = (JavascriptExecutor) driver;

			js.executeScript("arguments[0].value = '560';", numberInputField);
			Thread.sleep(1000);
			js.executeScript(
					"var event = new Event('input', { bubbles: true, cancelable: true }); arguments[0].dispatchEvent(event);",
					numberInputField);
			js.executeScript(
					"var event = new Event('change', { bubbles: true, cancelable: true }); arguments[0].dispatchEvent(event);",
					numberInputField);
			js.executeScript(
					"var event = new Event('blur', { bubbles: true, cancelable: true }); arguments[0].dispatchEvent(event);",
					numberInputField);
			// Optionally, add an additional wait to ensure the page updates
			wait.until(ExpectedConditions.textToBePresentInElementValue(numberInputField, "560"));
			// updated values checking
			String newValue = numberInputField.getAttribute("value");
			System.out.println("New value set: " + newValue);

			actions.sendKeys(Keys.PAGE_DOWN).perform();

			Thread.sleep(1000);
			//Scroll down further and select the checkboxes for CPT-99091, CPT-99453, CPT-99454, and CPT-99474
			driver.findElement(By.xpath(checkboxes_CPT_99091)).click();
			driver.findElement(By.xpath(checkboxes_CPT_99453)).click();
			driver.findElement(By.xpath(checkboxes_CPT_99454)).click();
			driver.findElement(By.xpath(checkboxes_CPT_99474)).click();

			Thread.sleep(10000);

		} catch (Exception e) {
			System.out.println("Exception occurred while setting value with JavaScript.");
			e.printStackTrace();
		}

	}

	@AfterClass
	public void finish() throws InterruptedException

	{
		Thread.sleep(1000);

		if (driver != null) {
			driver.close();
		}
	}

	
}
