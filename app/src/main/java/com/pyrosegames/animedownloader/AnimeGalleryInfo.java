package com.pyrosegames.animedownloader;

public class AnimeGalleryInfo {
    private String animeName;
    private String animeEps;
    private String downloadDate;

    public static int day = 0;
    public static int month = 1;
    public static int year = 2;
    public static int hour = 3;
    public static int minute = 4;
    public static int second = 5;

    public AnimeGalleryInfo(String animeName, String animeEps, String downloadDate){
        this.animeName = animeName;
        this.animeEps = animeEps;
        this.downloadDate = downloadDate;
    }

    public String getAnimeName(){ return  animeName; }
    public String getAnimeEps(){ return  animeEps; }
    public String getDownloadDate(){ return  downloadDate; }
    //Mon Aug 23 13:21:39 GMT+02:00 2021
    public String getDate(){
        String[] parts = downloadDate.split(" ");
        String strictedMonth = parts[1];
        String day = parts[2];
        String year = parts[parts.length - 1];
        String month = "";
        String compactHour = parts[3];
        String[] hourParts = compactHour.split(":");
        String hour = hourParts[0];
        String minute = hourParts[1];
        String second = hourParts[2];
        switch (strictedMonth){
            case "Jan":
                month = "01";
                break;
            case "Feb":
                month = "02";
                break;
            case "Mar":
                month = "03";
                break;
            case "Apr":
                month = "04";
                break;
            case "May":
                month = "05";
                break;
            case "June":
                month = "06";
                break;
            case "July":
                month = "07";
                break;
            case "Aug":
                month = "08";
                break;
            case "Sept":
                month = "09";
                break;
            case "Oct":
                month = "10";
                break;
            case "Nov":
                month = "11";
                break;
            case "Dec":
                month = "12";
                break;
        }
        String date = day + "/" + month + "/" + year + "/" + hour + "/" + minute + "/" + second;
        return date;
    }
}
