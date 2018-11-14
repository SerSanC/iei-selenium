package iei.entrega2;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 *
 * @author sergisanz
 */
public class FXMLDocumentController implements Initializable {
    
    private Label label;
    @FXML
    private JFXCheckBox checkAmazon;
    @FXML
    private JFXCheckBox checkElCorteIngles;
    @FXML
    private JFXCheckBox checkFnac;
    @FXML
    private JFXTextField t_libro;
    @FXML
    private JFXTextField autor;
    @FXML
    private GridPane grid;
    private static WebDriver driver= null;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        grid.setVisible(false);
    }    

    @FXML
    private void Buscar(ActionEvent event) {
        Fnac(t_libro.getText());
    }
       public static void Fnac(String libro){
        String exePath = "lib/chromedriver";
        System.setProperty("webdriver.chrome.driver", exePath); 
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        driver = new ChromeDriver(options); 
        driver.get("http://www.fnac.es"); 
        driver.findElement(By.className("Header__aisle")).click();
        driver.findElements(By.cssSelector("ul.select-options>li.select-option")).get(1).click();
        WebElement element = driver.findElement(By.id("Fnac_Search"));
        element.sendKeys(libro);
        element.submit();
        do {
            WebDriverWait waiting = new WebDriverWait(driver, 10);
            waiting.until(ExpectedConditions.presenceOfElementLocated(By.className("Article-desc")));
            waiting.until(ExpectedConditions.invisibilityOf(driver.findElement(By.id("ajaxLoader"))));
            for (WebElement el : driver.findElements(By.className("Article-desc"))) {
                List<WebElement> title = el.findElements(By.className("js-minifa-title"));
                if(title.size() >= 1) {
                    System.out.println(title.get(0).getText());
                }
            }
            try {
                driver.findElement(By.className("nextLevel1")).click();
            } catch (Exception e) {
                break;
            }
        } while(driver.findElement(By.className("nextLevel1")) != null);
        System.out.println("END");
    }
}