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
import java.util.*;

public class App
{
    public static void main( String[] args ) throws IOException, InterruptedException {
        System.setProperty("webdriver.http.factory", "jdk-http-client");

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-notifications");
        options.addArguments("disable-infobars");

        String website = "https://www.youtube.com/@rossmanngroup/videos";
        String handle = website.split("/")[3];

        Map<String, Long> relativeTimetoUnixTime = new HashMap<>();
        relativeTimetoUnixTime.put("Sekunden",1000L);
        relativeTimetoUnixTime.put("Minuten",60000L);
        relativeTimetoUnixTime.put("Stunden",3600000L);
        relativeTimetoUnixTime.put("Tagen",86400000L);
        relativeTimetoUnixTime.put("Wochen",604800000L);
        relativeTimetoUnixTime.put("Monaten",2629743000L);
        relativeTimetoUnixTime.put("Jahren",31556926000L);

       // BufferedWriter bw = new BufferedWriter(new FileWriter(handle+".txt"));

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

                //int c = 0;
                while(true){
                    new Actions(driver).scrollByAmount(0,(int) lastHeight).perform();
                    new Actions(driver).pause(Duration.ofMillis(500L)).perform();

                    long newHeight = Long.parseLong(pageManager.getAttribute("scrollHeight"));

                    if(newHeight == lastHeight){
                        break;
                    }
                    lastHeight = newHeight;
                  //  c++;
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

                List<JSONObject> ytVideo = new ArrayList<>();

                for(int i = 0; i < thumbNail.size(); i++) {

                    title = url.get(i).getDomAttribute("title");
                    thumbUrl = thumbNail.get(i).getDomAttribute("src");
                    videoUrl = url.get(i).getDomAttribute("href");
                    viewCount = metaData.get(i * 2).getText();
                    uploadDate = metaData.get((2 * i) + 1).getText();

                    long time = System.currentTimeMillis() - getUnixTimeFromRelativeTime(uploadDate, relativeTimetoUnixTime);

                    YoutubeVideo video = new YoutubeVideo(handle, title, uploadDate, viewCount, videoUrl, thumbUrl, time);

                    JSONObject object = new JSONObject(video);

                    ytVideo.add(object);
                   // bw.write(video.toString());
                   // bw.flush();
                }

                try {
                    // Create POST Request with JSON-Body
                    HttpClient client = HttpClient.newHttpClient();
                    HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8000/v1/youtubeVideoList")).header("Content-Type", "application/json; charset=UTF-8").PUT(HttpRequest.BodyPublishers.ofString(ytVideo.toString())).build();
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
                  //bw.close();
                  driver.quit();
                }
            } catch (Exception e) {
                System.err.println(e);
                //bw.close();
                driver.quit();
            }
            finally {
                //bw.close();
                driver.quit();
            }

            //bw.close();
            driver.quit();
    }

    public static long getUnixTimeFromRelativeTime(String time, Map<String, Long> relativeTimeToUnix){

        String[] a = time.split(" ");
        long relativeTime = 0L;
        long multiply = Integer.parseInt(a[1]);

        for(String s : relativeTimeToUnix.keySet()){
            if(s.contains(a[2])){
                relativeTime = relativeTimeToUnix.get(s) * multiply;
                return relativeTime;
            }
        }

        return relativeTime;
    }
}