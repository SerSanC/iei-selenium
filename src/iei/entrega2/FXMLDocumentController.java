package iei.entrega2;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
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

    private static WebDriver driver = null;
    int tamaño;
    
    @FXML
    private VBox vbox_identificador;
    private GridPane grid_identificador;
    int i = 1;
    @FXML
    private ScrollPane scroll;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        grid_identificador = new GridPane();
        grid_identificador.add(new Label(" Sitio Web "), 0, 0);
        grid_identificador.add(new Label(" Título "), 1, 0);
        grid_identificador.add(new Label(" Precio "), 2, 0);
        grid_identificador.add(new Label(" Descuento "), 3, 0);
    }

    @FXML
    private void Buscar(ActionEvent event) {
        if (checkFnac.isSelected()) {
            Fnac(t_libro.getText(), autor.getText());
        }
        if (checkAmazon.isSelected()) {
            Amazon(t_libro.getText(), autor.getText());
        }

    }

    public void Fnac(String libro, String autor) {
        grid_identificador.setVisible(false);

        String exePath = "lib/chromedriver";
        System.setProperty("webdriver.chrome.driver", exePath);
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        driver = new ChromeDriver(options);
        driver.get("http://www.fnac.es");
        driver.findElement(By.className("Header__aisle")).click();
        driver.findElements(By.cssSelector("ul.select-options>li.select-option")).get(1).click();
        WebElement element = driver.findElement(By.id("Fnac_Search"));
        
        if(!autor.isEmpty()){element.sendKeys(autor);System.out.println("Entras a autor");}
        else{
            element.sendKeys(libro);
        }
        element.submit();

        tamaño = driver.findElements(By.className("Article-desc")).size();
   

        do {
            WebDriverWait waiting = new WebDriverWait(driver, 10);
            waiting.until(ExpectedConditions.presenceOfElementLocated(By.className("Article-desc")));
            waiting.until(ExpectedConditions.invisibilityOf(driver.findElement(By.id("ajaxLoader"))));
            for (WebElement el : driver.findElements(By.className("Article-desc"))) {
                List<WebElement> title = el.findElements(By.className("js-minifa-title"));

                if (title.size() >= 1) {
                    grid_identificador.add(new Label(" "+title.get(0).getText()+" "), 1, i);
                    grid_identificador.addColumn(0, new Label(" Fnac "));
                    i++;
                }
            }
            try {
                driver.findElement(By.className("nextLevel1")).click();
            } catch (Exception e) {
                break;
            }
        } while (driver.findElement(By.className("nextLevel1")) != null);
        grid_identificador.setAlignment(Pos.CENTER);
        grid_identificador.setPadding(new Insets(50, 20, 20, 20));
  
        vbox_identificador.getChildren().addAll(grid_identificador);
        vbox_identificador.setAlignment(Pos.CENTER);  
        System.out.println("END");
        grid_identificador.setGridLinesVisible(true);
        grid_identificador.setVisible(true);

    }

    private void Amazon(String libro, String autor) {
        String exePath = "lib/chromedriver";
        System.setProperty("webdriver.chrome.driver", exePath);
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        driver = new ChromeDriver(options);
        driver.get("http://www.elcorteingles.es");
        driver.findElement(By.className("menu-container")).click();
        driver.findElement(By.className("md-10")).click();

        //driver.findElements(By.cssSelector("ul.select-options>li.select-option")).get(1).click();
        WebElement element = driver.findElement(By.id("search-box"));
        element.sendKeys(libro);
        element.submit();

    }

}
