package org.example;


public class YoutubeVideo {

    private String videoURL;
    private String thumbnailURL;
    private String uploadDate;
    private String viewCount;
    private String title;

    private String handle;
    private long unixTimeStamp;
    public YoutubeVideo(String handle, String title, String uploadDate, String viewCount, String videoURL, String thumbnailURL, long unixTimeStamp){
        this.videoURL = videoURL;
        this.thumbnailURL = thumbnailURL;
        this.uploadDate = uploadDate;
        this.viewCount = viewCount;
        this.title = title;
        this.unixTimeStamp = unixTimeStamp;
        this.handle = handle;
    }

    public String getVideoURL() {
        return videoURL;
    }

    public void setVideoURL(String videoURL) {
        this.videoURL = videoURL;
    }

    public String getThumbnailURL() {
        return thumbnailURL;
    }

    public void setThumbnailURL(String thumbnailURL) {
        this.thumbnailURL = thumbnailURL;
    }

    public String getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(String uploadDate) {
        this.uploadDate = uploadDate;
    }

    public String getViewCount() {
        return viewCount;
    }

    public void setViewCount(String viewCount) {
        this.viewCount = viewCount;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getUnixTimeStamp() {
        return unixTimeStamp;
    }

    public void setUnixTimeStamp(long unixTimeStamp) {
        this.unixTimeStamp = unixTimeStamp;
    }

    public String toStringDelimiterTab(){
        return this.getTitle()+"\t"+this.getUploadDate()+"\t"+this.getViewCount()+"\t"+this.getVideoURL()+"\t"+this.getThumbnailURL()+"\n";
    }

    public String getHandle() {
        return handle;
    }

    public void setHandle(String handle) {
        this.handle = handle;
    }

    @Override
    public String toString(){
        return this.getTitle()+" "+this.getUploadDate()+" "+this.getViewCount()+" "+this.getVideoURL()+" "+this.getThumbnailURL()+" "+ this.unixTimeStamp+"\n";
    }
}
