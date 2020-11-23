package com.jaliansystems.javadriver.examples.swing.ut;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import net.sourceforge.marathon.javadriver.JavaDriver;
import net.sourceforge.marathon.javadriver.JavaProfile;
import net.sourceforge.marathon.javadriver.JavaProfile.LaunchMode;
import net.sourceforge.marathon.javadriver.JavaProfile.LaunchType;

@TestInstance(Lifecycle.PER_CLASS)
public class LoginDialogTest {

	private LoginDialog login;
	private WebDriver driver;
	private static Users user = new Users();

	@BeforeAll
	public void init() {
		user.setUserName("bob");
		user.setPassword("secret");
		user.setWrongPassword("wrong");
	}

	@BeforeEach
	public void setUpBeforeClass() throws Exception {
		login = new LoginDialog() {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSuccess() {
			}

			@Override
			protected void onCancel() {
			}
		};
		SwingUtilities.invokeLater(() -> login.setVisible(true));
		JavaProfile profile = new JavaProfile(LaunchMode.EMBEDDED);
		profile.setLaunchType(LaunchType.SWING_APPLICATION);
		driver = new JavaDriver(profile);
	}

	@AfterEach
	public void tearDown() throws Exception {
		if (login != null)
			SwingUtilities.invokeAndWait(() -> login.dispose());
		if (driver != null)
			driver.quit();
	}

	@Test
	public void testLoginSuccess() {
		String userName = user.getUserName();
		String password = user.getPassword();
		WebElement user = driver.findElement(By.cssSelector("text-field"));
		user.sendKeys(userName);
		WebElement pass = driver.findElement(By.cssSelector("password-field"));
		pass.sendKeys(password);
		WebElement loginBtn = driver.findElement(By.cssSelector("button[text='Login']"));
		WebDriverWait wait = new WebDriverWait(driver, 10);
		wait.until(ExpectedConditions.elementToBeClickable(loginBtn));
		loginBtn.click();
		assertTrue(login.authenticate(userName, password));
		assertNotNull(login.getSize());
	}

	@Test
	public void testLoginCancel() {
		String userName = user.getUserName();
		String password = user.getWrongPassword();
		WebElement user = driver.findElement(By.cssSelector("text-field"));
		user.sendKeys(userName);
		WebElement pass = driver.findElement(By.cssSelector("password-field"));
		pass.sendKeys(password);
		WebElement cancelBtn = driver.findElement(By.cssSelector("button[text='Cancel']"));
		cancelBtn.click();
		assertFalse(login.authenticate(userName, password));
	}

	@Test
	public void testLoginInvalid() throws InterruptedException {
		String userName = user.getUserName();
		String password = user.getWrongPassword();
		WebElement user = driver.findElement(By.cssSelector("text-field"));
		user.sendKeys(userName);
		WebElement pass = driver.findElement(By.cssSelector("password-field"));
		pass.sendKeys(password);
		WebElement loginBtn = driver.findElement(By.cssSelector("button[text='Login']"));
		WebDriverWait wait = new WebDriverWait(driver, 10);
		wait.until(ExpectedConditions.elementToBeClickable(loginBtn));
		loginBtn.click();
		driver.switchTo().window("Invalid Login");
		driver.findElement(By.cssSelector("button[text='OK']")).click();
		driver.switchTo().window("Login");
		user = driver.findElement(By.cssSelector("text-field"));
		pass = driver.findElement(By.cssSelector("password-field"));
		assertNotNull(user.getText());
		assertNotNull(pass.getText());
	}
	@Test 
	public void testTextFieldsAreEmpty() {
		String userName = "";
		String password = "";
		WebElement user = driver.findElement(By.cssSelector("text-field"));
		user.sendKeys(userName);
		WebElement pass = driver.findElement(By.cssSelector("password-field"));
		pass.sendKeys(password);
		WebElement clearBtn = driver.findElement(By.cssSelector("button[text='Clear']"));
		WebDriverWait wait = new WebDriverWait(driver, 10);
		wait.until(ExpectedConditions.elementToBeClickable(clearBtn));
		clearBtn.click();
		assertTrue(user.getText().isEmpty());
		assertTrue(pass.getText().isEmpty());
		
	}

	@Test
	public void checkTooltipText() {
		// Check that all the text components (like text fields, password
		// fields, text areas) are associated
		// with a tooltip
		List<WebElement> textComponents = driver.findElements(By.className(JTextComponent.class.getName()));
		for (WebElement tc : textComponents) {
			assertNotEquals(null, tc.getAttribute("toolTipText"));
		}
	}
}
