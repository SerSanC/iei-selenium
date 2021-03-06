package iei.entrega2;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXTextField;
import static com.jfoenix.effects.JFXDepthManager.pop;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 *
 * @author sergisanz
 */
public class FXMLDocumentController implements Initializable {

    private Text label;
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
    String precioAnt;
    private static WebDriver driver = null;
    int tamaño;

    @FXML
    private VBox vbox_identificador;
    private GridPane grid_identificador;
    private GridPane grid_identificador_ECI;

    int i = 1;
    @FXML
    private ScrollPane scroll;
    private String dato;
    private int cont = 0;
    private int contador_busqueda;
    private int contador_busqueda_ECI;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        grid_identificador = new GridPane();
        grid_identificador_ECI = new GridPane();

        InicializarGridPane();
        checkAmazon.setDisable(true);

        checkFnac.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                checkFnac.setSelected(newValue);
                checkElCorteIngles.setSelected(!newValue);
            }
        });

        checkElCorteIngles.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                checkElCorteIngles.setSelected(newValue);
                checkFnac.setSelected(!newValue);
            }
        });
    }

    @FXML
    private void Buscar(ActionEvent event) {
        LimpiarGridPane();
        InicializarGridPane();
        i = 1;

        if (checkFnac.isSelected()) {
            Fnac(t_libro.getText(), autor.getText());
        }

        if (checkElCorteIngles.isSelected()) {
            ECI(t_libro.getText(), autor.getText());
        }
        
        grid_identificador.setVisible(true);
    }

    public void Fnac(String titulo, String autor) {
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

        if (!autor.isEmpty()) {
            element.sendKeys(autor);
            element.submit();
            Fnac_Autor(autor);
        } else {
            element.sendKeys(titulo);
            element.submit();
            Fnac_Titulo(titulo);
        }

        grid_identificador.setAlignment(Pos.CENTER);
        grid_identificador.setPadding(new Insets(50, 20, 20, 20));
        vbox_identificador.getChildren().addAll(grid_identificador);
        vbox_identificador.setAlignment(Pos.CENTER);
        grid_identificador.setGridLinesVisible(true);
        grid_identificador.setVisible(true);

        driver.close();
        driver.quit();
    }

    private void Fnac_Titulo(String titulo) {
        tamaño = driver.findElements(By.className("Article-item")).size();

        do {
            WebDriverWait waiting = new WebDriverWait(driver, 10);
            try {
                waiting.until(ExpectedConditions.presenceOfElementLocated(By.className("Article-item")));
            } catch (Exception e) {
                LimpiarGridPane();
                label = new Text("Libro no encontrado");
                vbox_identificador.getChildren().add(label);
                break;
            }

            waiting.until(ExpectedConditions.invisibilityOf(driver.findElement(By.id("ajaxLoader"))));
            for (WebElement el : driver.findElements(By.className("Article-item"))) {
                List<WebElement> titles = el.findElements(By.className("js-minifa-title"));
                grid_identificador.addColumn(0, new Label(" Fnac "));
                if (titles.size() >= 1) {
                    dato = titles.get(0).getText();
                    if (dato.length() >= 80) {
                        dato = dato.substring(0, 80);
                    }
                    System.out.println(dato);
                    String oldPrice, price;
                    try {
                        oldPrice = el.findElement(By.className("oldPrice")).getText();
                    } catch (Exception e) {
                        oldPrice = "";
                    }
                    try {
                        price = el.findElement(By.className("userPrice")).getText();
                    } catch (Exception e) {
                        price = "";
                    }
                    if (oldPrice.equals("")) {
                        oldPrice = price;
                        price = "";
                    }
                    grid_identificador.add(new Label(" " + dato + " "), 1, i);
                    grid_identificador.addColumn(2, new Label(oldPrice));
                    grid_identificador.addColumn(3, new Label(price));
                    i++;
                }
            }
            try {
                driver.findElement(By.className("nextLevel1")).click();
            } catch (Exception e) {
                break;
            }
        } while (driver.findElement(By.className("nextLevel1")) != null);
        contador_busqueda++;
    }

    private void Fnac_Autor(String autor) {
        WebDriverWait waiting = new WebDriverWait(driver, 10);
        waiting.until(ExpectedConditions.presenceOfElementLocated(By.className("Action-btn")));
        WebElement fichaBtn = driver.findElement(By.className("Action-btn"));
        if (fichaBtn.getText().equals("Ver ficha")) {
            System.out.println("Ficha de autor encontrada.");
            fichaBtn.click();
            waiting.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("univers-text")));
            List<WebElement> librosBtns = driver.findElements(By.className("univers-text"));
            for (WebElement librosBtn : librosBtns) {
                if (librosBtn.getText().equals("Libros")) {
                    System.out.println("Click");
                    librosBtn.click();
                    do {
                        waiting.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("Article-item")));
                        waiting.until(ExpectedConditions.invisibilityOf(driver.findElement(By.id("ajaxLoader"))));
                        for (WebElement el : driver.findElements(By.className("Article-item"))) {
                            List<WebElement> titles = el.findElements(By.className("js-minifa-title"));
                            grid_identificador.addColumn(0, new Label(" Fnac "));
                            if (titles.size() >= 1) {
                                dato = titles.get(0).getText();
                                if (dato.length() >= 80) {
                                    dato = dato.substring(0, 80);
                                }
                                System.out.println(dato);
                                String oldPrice, price;
                                try {
                                    oldPrice = el.findElement(By.className("oldPrice")).getText();
                                } catch (Exception e) {
                                    oldPrice = "";
                                }
                                try {
                                    price = el.findElement(By.className("userPrice")).getText();
                                } catch (Exception e) {
                                    price = "";
                                }
                                if (oldPrice.equals("")) {
                                    oldPrice = price;
                                    price = "";
                                }
                                grid_identificador.add(new Label(" " + titles.get(0).getText() + " "), 1, i);
                                grid_identificador.addColumn(2, new Label(oldPrice));
                                grid_identificador.addColumn(3, new Label(price));
                                i++;
                            }

                        }
                        try {
                            driver.findElement(By.className("nextLevel1")).click();
                        } catch (Exception e) {
                            break;
                        }
                    } while (driver.findElement(By.className("nextLevel1")) != null);
                }
            }
        } else {
            //label.setFont(new Font("Arial",25));
            Fnac_Titulo(autor);
            LimpiarGridPane();
            label = new Text("Ficha de autor no encontrada");

            vbox_identificador.getChildren().add(label);
            System.out.println("Ficha de autor no encontrada");

        }
    }

    private void ECI(String titulo, String autor) {
        String exePath = "lib/chromedriver";
        System.setProperty("webdriver.chrome.driver", exePath);
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        driver = new ChromeDriver(options);
        driver.get("http://www.elcorteingles.es");

        if (!titulo.isEmpty()) {
            ECI_Titulo(titulo);
        } else if (!autor.isEmpty()) {
            ECI_Autor(autor);
        }

        grid_identificador.setAlignment(Pos.CENTER);
        grid_identificador.setPadding(new Insets(50, 20, 20, 20));
        vbox_identificador.getChildren().addAll(grid_identificador);
        vbox_identificador.setAlignment(Pos.CENTER);
        grid_identificador.setGridLinesVisible(true);
        grid_identificador.setVisible(true);

        driver.quit();
    }

    private void InicializarGridPane() {
        grid_identificador.add(new Label(" Sitio Web "), 0, 0);
        grid_identificador.add(new Label(" Título "), 1, 0);
        grid_identificador.add(new Label(" Precio "), 2, 0);
        grid_identificador.add(new Label(" Descuento "), 3, 0);
    }

    private void LimpiarGridPane() {
        dato = null;
        vbox_identificador.getChildren().clear();
        grid_identificador = null;
        grid_identificador = new GridPane();
    }

    private void ECI_Titulo(String titulo) {
        WebElement element = driver.findElement(By.id("search-box"));
        element.sendKeys(titulo);
        element.submit();

        WebDriverWait waiting = new WebDriverWait(driver, 10);

        driver.findElement(By.id("cookies-agree")).click();

        waiting.until(ExpectedConditions.presenceOfElementLocated(By.className("facet-popup")));
        driver.findElements(By.className("facet-popup")).get(1).click();

        try {
            waiting.until(ExpectedConditions.presenceOfElementLocated(By.className("js-product-click")));
        } catch (Exception e) {
            LimpiarGridPane();
            label = new Text("Libro no encontrado");
            vbox_identificador.getChildren().add(label);
            //     break;
        }
        boolean last_page = false;
        do {
            waiting.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("product-preview")));
            for (WebElement el : driver.findElements(By.className("product-preview"))) {
                String titles = el.findElement(By.className("js-product-click")).getText();
                System.out.println(titles);
                String precioAct, precioAnt;
                try {
                    precioAct = el.findElement(By.className("current")).getText();
                } catch (Exception e) {
                    precioAct = "";
                }
                try {
                    precioAnt = el.findElement(By.className("former")).getText();
                } catch (Exception e) {
                    precioAnt = "";
                }
                if(precioAnt.equals("")) {
                    precioAnt = precioAct;
                    precioAct = "";
                }
                grid_identificador.addColumn(0, new Label(" El Corte Ingles "));

                if (titles.length() >= 80) {
                    titles = titles.substring(0, 80);
                }
                grid_identificador.add(new Label(" " + titles + " "), 1, i);
                grid_identificador.addColumn(2, new Label(precioAnt));
                grid_identificador.addColumn(3, new Label(precioAct));
                i++;
            }
            try {
                driver.findElements(By.cssSelector("div.pagination>ul li:last-child>a")).get(0).click();
                System.out.println("Next");
            } catch (Exception e) {
                last_page = true;
                System.out.println("Última página");
            }
        } while (!last_page);
    }

    private void ECI_Autor(String autor) {
        WebElement element = driver.findElement(By.id("search-box"));
        element.sendKeys(autor);
        element.submit();

        WebDriverWait waiting = new WebDriverWait(driver, 10);

        driver.findElement(By.id("cookies-agree")).click();

        try {
            waiting.until(ExpectedConditions.presenceOfElementLocated(By.className("facet-popup")));
            driver.findElements(By.className("facet-popup")).get(0).click();
        } catch (Exception e) {
            LimpiarGridPane();
            label = new Text("Autor no encontrado");
            vbox_identificador.getChildren().add(label);
            driver.close();
            return;
        }

        try {
            waiting.until(ExpectedConditions.presenceOfElementLocated(By.className("js-product-click")));
        } catch (Exception e) {
            LimpiarGridPane();
            label = new Text("Libros no encontrados");
            vbox_identificador.getChildren().add(label);
            driver.close();
            //     break;
        }
            int cont = 0;
            //  do {
            int k = 0;
            for (WebElement el : driver.findElements(By.className("js-product-click"))) {
                if (k == 1) {
                    el.click();
                    System.out.println("Llegamos al break");

                    break;
                }
                k++;
            }
         
            waiting.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div.product-features>dl.cb>dd>a")));
            WebElement al = driver.findElements(By.cssSelector("div.product-features>dl.cb>dd>a")).get(0);
            if(autor.contains(al.getText()) || al.getText().contains(autor)) {
                al.click();
            } else {
                LimpiarGridPane();
                label = new Text("Autor no encontrado");
                vbox_identificador.getChildren().add(label);
                driver.close();
                return;
            }
            waiting.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("tabbed-ctrl")));
            List<WebElement> tabs = driver.findElements(By.className("tabbed-ctrl"));
            WebElement oktab = null;
            for (WebElement tab : tabs) {
                if(tab.getText().equals("VER TODOS")) {
                    oktab = tab;
                    break;
                }
            }
            try {
                oktab.click();
            } catch (Exception e) {
                System.out.println("Tab no encontrada");
            }
            boolean last_page = false;
            do {
                waiting.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("product-preview")));
                for (WebElement el : driver.findElements(By.className("product-preview"))) {
                    String titles = el.findElement(By.className("js-product-click")).getText();
                    System.out.println(titles);
                    String precioAct, precioAnt;
                    try {
                        precioAct = el.findElement(By.className("current")).getText();
                    } catch (Exception e) {
                        precioAct = "";
                    }
                    try {
                        precioAnt = el.findElement(By.className("former")).getText();
                    } catch (Exception e) {
                        precioAnt = "";
                    }
                    if(precioAnt.equals("")) {
                        precioAnt = precioAct;
                        precioAct = "";
                    }
                    grid_identificador.addColumn(0, new Label(" El Corte Ingles "));

                    if (titles.length() >= 80) {
                        titles = titles.substring(0, 80);
                    }
                    grid_identificador.add(new Label(" " + titles + " "), 1, i);
                    grid_identificador.addColumn(2, new Label(precioAnt));
                    grid_identificador.addColumn(3, new Label(precioAct));
                    i++;
                }
                try {
                    driver.findElements(By.cssSelector("div.pagination>ul li:last-child>a")).get(0).click();
                    System.out.println("Next");
                } catch (Exception e) {
                    last_page = true;
                    System.out.println("Última página");
                }
            } while (!last_page);

        }

    }


