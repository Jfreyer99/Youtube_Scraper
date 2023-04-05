package org.example;

import org.json.JSONObject;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class App
{
    public static void main( String[] args ) throws IOException, InterruptedException {
        System.setProperty("webdriver.http.factory", "jdk-http-client");

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-notifications");
        options.addArguments("disable-infobars");

        String website = "https://www.youtube.com/@programmersarealsohuman5909/videos";
        String handle = website.split("/")[3];

        BufferedWriter bw = new BufferedWriter(new FileWriter(handle+".txt"));

        WebDriver driver = new ChromeDriver(options);
        WebDriverWait driverWait = new WebDriverWait(driver, Duration.ofSeconds(2000L));

            driver.get(website);
            try {
                driverWait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".VfPpkd-LgbsSe.VfPpkd-LgbsSe-OWXEXe-k8QpJ.VfPpkd-LgbsSe-OWXEXe-dgl2Hf.nCP5yc.AjY5Oe.DuMIQc.LQeN7.IIdkle")));
                WebElement consent = driver.findElement(By.cssSelector(".VfPpkd-LgbsSe.VfPpkd-LgbsSe-OWXEXe-k8QpJ.VfPpkd-LgbsSe-OWXEXe-dgl2Hf.nCP5yc.AjY5Oe.DuMIQc.LQeN7.IIdkle"));
                consent.click();

                driverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("contents")));
                WebElement pageManager = driverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("page-manager")));

                long lastHeight = Long.parseLong(pageManager.getAttribute("scrollHeight"));

                int c = 0;
                while(true){
                    new Actions(driver).scrollByAmount(0,(int) lastHeight).perform();
                    new Actions(driver).pause(Duration.ofMillis(500L)).perform();

                    long newHeight = Long.parseLong(pageManager.getAttribute("scrollHeight"));

                    if(newHeight == lastHeight){
                        break;
                    }
                    lastHeight = newHeight;
                    //c++;
                }

                driverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("contents")));

                List<WebElement> metaData = driver.findElements(By.cssSelector(".inline-metadata-item.style-scope.ytd-video-meta-block"));
                List<WebElement> url = driver.findElements(By.id("video-title-link"));
                List<WebElement> thumbNail = driver.findElements(By.cssSelector("img.yt-core-image"));

                String thumbUrl = null;
                String videoUrl = null;
                String uploadDate = null;
                String viewCount = null;
                String title = null;

                List<YoutubeVideo> ytList = new ArrayList<>();

                for(int i = 0; i < thumbNail.size(); i++){

                    title = url.get(i).getDomAttribute("title");
                    thumbUrl = thumbNail.get(i).getDomAttribute("src");
                    videoUrl = url.get(i).getDomAttribute("href");
                    viewCount = metaData.get(i*2).getText();
                    uploadDate = metaData.get((2*i)+1).getText();

                    YoutubeVideo v = new YoutubeVideo(title, uploadDate, viewCount, videoUrl, thumbUrl);
                    ytList.add(v);
                    bw.write(v.toString());
                    bw.flush();
                }

                JSONObject o = new JSONObject();
                o.put(handle, ytList);
                String output = o.toString();

                try {
                    // Create POST Request with JSON-Body
                    HttpClient client = HttpClient.newHttpClient();
                    HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8000/v1/youtubeVideoList")).header("Content-Type", "application/json; charset=UTF-8").PUT(HttpRequest.BodyPublishers.ofString(output)).build();
                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                    if(response.statusCode() == 200 || response.statusCode() == 201){
                        System.out.println(response.body());
                    }
                    else{
                        System.out.println(response.body());
                        throw new InvalidResponseException("Status Code is not 200 | 201, please try again");
                    }
                }
                catch(Exception e){
                  System.err.println(e);
                  bw.close();
                  driver.quit();
                }
            } catch (Exception e) {
                System.err.println(e);
                bw.close();
                driver.quit();
            }

            bw.close();
            driver.quit();
    }
}