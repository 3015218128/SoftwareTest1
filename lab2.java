import jxl.Sheet;
import jxl.Workbook;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.*;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;

import static junit.framework.Assert.assertTrue;

class User {
    public String username;
    public String url;

    public User(String username, String url) {
        this.username=username;
        this.url=url;
    }
}

@RunWith(Parameterized.class)
public class Test {
    private static List<User> information;
    private static WebDriver driver;

    public static List<User> readFile(String path) {
        List<User> re=new ArrayList<>();
        try {
        	System.out.println(System.getProperty("user.dir"));
            Workbook workbook=Workbook.getWorkbook(new File(path));
            Sheet sheet=workbook.getSheet(0);

            for (int i=0; i<sheet.getRows(); ++i) {
                String id=sheet.getCell(0,i).getContents();
                String url=sheet.getCell(1,i).getContents();
                re.add(new User(id,url));
            }

            workbook.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return re;
    }

    private String username;
    private String password;
    private String url;

    public Test(String username, String password, String expected) {
        this.username=username;
        this.password=password;
        this.url=expected;
    }

    @BeforeClass
    public static void init() {
        String driver_path = System.getProperty("user.dir")+"/IEDriverServer.exe";
        System.setProperty("webdriver.ie.driver", driver_path);

        DesiredCapabilities ieCapabilities = DesiredCapabilities.internetExplorer();
        ieCapabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);

        driver = new InternetExplorerDriver(ieCapabilities);
    }

    @org.junit.Test
    public void setUp() {
        driver.get("https://psych.liebes.top/st");

        WebElement username_element=driver.findElement(By.id("username"));
        WebElement password_element=driver.findElement(By.id("password"));
        WebElement submit=driver.findElement(By.id("submitButton"));

        username_element.sendKeys(username);
        password_element.sendKeys(password);
        submit.click();

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {}

        assertTrue(driver.findElement(By.xpath("//p[@class='login-box-msg']")).getText().equals(url));
    }

    @AfterClass
    public static void cleanup() {
        driver.close();
    }

    @Parameters
    public static Collection<Object[]> provide() {
    	information=readFile(System.getProperty("user.dir")+"/input.xls");
        Collection<Object[]> re=new ArrayList<>();
        for (User x : information) {
            re.add(new Object[]{x.username,x.username.substring(4),x.url});
        }
        return re;
    }
}
