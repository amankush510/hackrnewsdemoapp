package com.example.hackernews.utils;

/**
 * Created by aman.kush on 9/15/2017.
 */
public class DateFormatter {
    public static String formatDate(Long time){
        Long duration = System.currentTimeMillis() - time;

        Long years = duration / 1000 / 60 / 60 / 24 / 365;
        Long months = duration / 1000 / 60 / 60 / 24 / 30;
        Long days = duration / 1000 / 60 / 60 / 24;
        Long hours = duration / 1000 / 60 / 60;
        Long minutes = duration / 1000 / 60 ;
        Long seconds = duration / 1000 / 60 / 60 ;

        if(years != 0){
            if(years == 1){
                return "" + years + " year ago";
            }
            return "" + years + " years ago";
        } else if(months != 0){
            if(months == 1){
                return "" + months + " month ago";
            }
            return "" + months + " months ago";
        } else if(days != 0){
            if(days == 1){
                return "" + days + " day ago";
            }
            return "" + days + " days ago";
        } else if(hours != 0){
            if(hours == 1){
                return "" + hours + " hour ago";
            }
            return "" + hours + " hours ago";
        } else if(minutes != 0){
            if(minutes == 1){
                return "" + minutes + " minute ago";
            }
            return "" + minutes + " minutes ago";
        } else {
            if(seconds == 1){
                return "" + seconds + " second ago";
            }
            return "" + seconds + " seconds ago";
        }

    }
}
