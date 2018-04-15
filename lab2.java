import jxl.Sheet;
import jxl.Workbook;

import org.openqa.selenium.*;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.*;

import java.io.File;
import java.util.*;

import static junit.framework.Assert.assertTrue;

@RunWith(Parameterized.class)
public class Test {
    private static WebDriver driver;

    private String username;
    private String password;
    private String url;

    public Test(String username,String url) {
        this.username=username;
        this.password=username.substring(4);
        this.url=url;
    }

    @BeforeClass
    public static void init() {
        System.setProperty("webdriver.ie.driver", System.getProperty("user.dir")+"/IEDriverServer.exe");
        DesiredCapabilities ieCapabilities = DesiredCapabilities.internetExplorer();
        ieCapabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
        driver = new InternetExplorerDriver(ieCapabilities);
    }

    @org.junit.Test
    public void setUp() {
        driver.get("https://psych.liebes.top/st");

        driver.findElement(By.id("username")).sendKeys(username);
        driver.findElement(By.id("password")).sendKeys(password);
        driver.findElement(By.id("submitButton")).click();

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
        Collection<Object[]> re=new ArrayList<>();

        try {
            Workbook workbook=Workbook.getWorkbook(new File(System.getProperty("user.dir")+"/input.xls"));
            Sheet sheet=workbook.getSheet(0);
            for (int i=0; i<sheet.getRows(); ++i) {
                String id=sheet.getCell(0,i).getContents();
                String url=sheet.getCell(1,i).getContents();
                re.add(new Object[]{id,url});
            }
            workbook.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return re;
    }
}
