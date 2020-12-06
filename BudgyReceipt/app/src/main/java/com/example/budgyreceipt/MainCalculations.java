package com.example.budgyreceipt;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainCalculations {
    private final ArrayList<String> str = new ArrayList<String>();

    public static int getArrayIndex(String[] resource_tags, String tag) {
        int index = -1;
        for (int i = 0; i < resource_tags.length; i++) {
            if (resource_tags[i].equals(tag)) {
                index = i;
                break;
            }
        }
        return index;
    }

    public static String[] stringParse(StringBuilder string) { //parse through string generated by the google vision frame
        ArrayList<String> prices = new ArrayList<String>();
        String sb = string.toString();
        String[] sArray = sb.split("\n");
        String total = null, subtotal = null, date = null, payment = null;

        ArrayList<String[]> temp = new ArrayList<String[]>();

        for (String item: sArray) { // split string a second time to grab each individual word
            String[] item_list = item.split(" ");
            temp.add(item_list);
        }

        for (String[] list: temp) {
            for (String item: list) {
                if (item.matches("(\\d)+\\.\\d{2}")){ // matches for typical price point ex. 500.43
                    prices.add(item);
                }
                if (item.matches("(\\d{1,2})/(\\d{1,2})/(\\d{4})")){ // need to add way of formatting date format of mm/dd/yy --> mm/dd/yyyy
                    date = item;
                } else if (item.matches("(\\d{1,2})/(\\d{1,2})/(\\d{2})")){
                    String[] date_arr = item.split("/");
                    int year_val = Integer.parseInt(date_arr[2]);
                    if (year_val > 20 && year_val <= 99){ //check if the year is smaller than 99 for 1999 but bigger than 20 for 2020
                        date_arr[2] = "19" + date_arr[2];
                    } else {
                        date_arr[2] = "20" + date_arr[2];
                    }
                    date = date_arr[0] + "/" + date_arr[1] + "/" + date_arr[2];
                }
            }
        }

        ArrayList<Float> pricesTemp = new ArrayList<Float>(); // temp array to hold actual float values of prices
        for (String item: prices) {
            float price = Float.parseFloat(item);
            pricesTemp.add(price);
        }
        int totalIndex = 0;
        int subtotalIndex = 0;
        for (String a: sArray) {
            if (a.toLowerCase().contains("subtotal")){ // look for both subtotal and total prices
                float total_amount = (float) 00.01;
                float subtotal_amount = (float) 00.01;

                for (float item: pricesTemp) {
                    if (item > total_amount){
                        total_amount = item;
                        totalIndex = pricesTemp.indexOf(item);
                    }
                }
                for (float item: pricesTemp) {
                    if (item > subtotal_amount && item < total_amount){
                        subtotal_amount = item;
                        subtotalIndex = pricesTemp.indexOf(item);
                    }
                }
                total = prices.get(totalIndex);
                subtotal = prices.get(subtotalIndex);
                break;
            }
            if (a.toLowerCase().contains("total")  || a.toLowerCase().contains("balance due")){ // look for just the total
                float total_amount = (float) 00.01;
                for (float item: pricesTemp) {
                    if (item > total_amount){
                        total_amount = item;
                        totalIndex = pricesTemp.indexOf(item);
                    }
                }
                total = prices.get(totalIndex);
                break;
            }
        }

        for (String[] list: temp) { //match for type of payment
            for (String name: list) {
                switch (name.toLowerCase()){
                    case "cash":
                        payment = "cash";
                        break;
                    case "visa":
                    case "master card":
                    case "american express":
                    case "discover":
                        payment = "credit";
                        break;
                    case "debit":
                        payment = "debit";
                        break;
                }
            }
        }
        String[] returns = {date, payment, total, subtotal};
        return returns;
    }

    public static List<String> dateCalc(String pastDate, String futureDate, List<String> dates) throws ParseException { // calculate all the numbers the
        List<Date> datesFormatted = new ArrayList<>();
        List<String> desiredDates = new ArrayList<>(); // final array to be sent back with the dates in specified range
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);

        Date pDate = sdf.parse(pastDate);
        Date fDate = sdf.parse(futureDate);

        for (String date:dates) { // format all the dates to be checked later
            Date dFormatted = sdf.parse(date);
            datesFormatted.add(dFormatted);
        }

        for (int i = 0; i < datesFormatted.size(); i++){ // get the desired dates between the start and end date
            Date date = datesFormatted.get(i);
            if (date.getTime() > pDate.getTime() && date.getTime() < fDate.getTime()){
                desiredDates.add(dates.get(i));
            }
        }

        return desiredDates;
    }

    public static List<Integer> getUniqueIds(List<Integer> ids) {
        List<Integer> ids_temp = ids;
        for (int i = 0; i < ids_temp.size(); i++){
            for (int j = i+1; j < ids_temp.size(); j++){
                if (ids_temp.get(i) == ids_temp.get(j)){ ids_temp.remove(j); } // remove any duplicate ids
            }
        }
        return ids_temp;
    }

    public static List<List<String>> getDatesSorted(List<List<String>> datesNtotals) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);
        List<List<String>> temp = datesNtotals;
        for (int i = 0; i < temp.size(); i++){
            List<String> i_inner_temp = temp.get(i); // temp array for array at i pos
            for (int j = i+1; j < temp.size(); j++){
                List<String> j_inner_temp = temp.get(j); // temp array for array at j pos
                Date i_temp_date = sdf.parse(i_inner_temp.get(0)); // grab the formatted date for each of the dates in the i and j pos
                Date j_temp_date = sdf.parse(j_inner_temp.get(0));
                if (i_temp_date.getTime() > j_temp_date.getTime()){ // compare the two to see which came before the other
                    List<String> swapper = temp.get(i);
                    temp.set(i, temp.get(j));
                    temp.set(j, swapper);
                    i_inner_temp = temp.get(i);
                }
                if (i_temp_date.getTime() == j_temp_date.getTime()){
                    //  merge the two equal date's totals and add it to the i tuple
                    Double i_total = Double.parseDouble(i_inner_temp.get(1));
                    Double j_total = Double.parseDouble(j_inner_temp.get(1));
                    Double combined_total = i_total + j_total;
                    String new_total = Double.toString(combined_total);
                    i_inner_temp.set(1, new_total);
                    temp.set(i, i_inner_temp);
                    temp.remove(j);
                    j--; // make sure to decrement to not miss the next value that took the removed item's pos
                }
            }
        }
        return temp;
    }
    /*This method will be used to add all similar tags and then return the resulting list with only one
    * occurrence of each type of tag available */
    public static List<List<String>> getTagsSorted(List<List<String>> dtt) {
        List<List<String>> temp = dtt;

        for (int i = 0; i < temp.size(); i++) {
            List<String> i_inner_temp = temp.get(i); // temp array for array at i pos
            for (int j = i + 1; j < temp.size(); j++) {
                List<String> j_inner_temp = temp.get(j); // temp array for array at j pos
                String i_temp_tag = i_inner_temp.get(0);
                String j_temp_tag = j_inner_temp.get(0);
                if (i_temp_tag.equals(j_temp_tag)){
                    Double i_total = Double.parseDouble(i_inner_temp.get(1));
                    Double j_total = Double.parseDouble(j_inner_temp.get(1));
                    Double combined_total = i_total + j_total;
                    String new_total = Double.toString(combined_total);
                    i_inner_temp.set(1, new_total);
                    temp.set(i, i_inner_temp);
                    temp.remove(j);
                    j--; // make sure to decrement to not miss the next value that took the removed item's pos
                }
            }
        }

        return temp;
    }

    public static List<String> getSortedDates(List<String> dates) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);
        for (int i = 0; i < dates.size(); i++) {
            for (int j = i + 1; j < dates.size(); j++) {
                Date i_temp_date = sdf.parse(dates.get(i)); // grab the formatted date for each of the dates in the i and j pos
                Date j_temp_date = sdf.parse(dates.get(j));
                if (i_temp_date.getTime() > j_temp_date.getTime()) { // compare the two to see which came before the other
                    String swapper = dates.get(i);
                    dates.set(i, dates.get(j));
                    dates.set(j, swapper);
                }
                if (i_temp_date.getTime() == j_temp_date.getTime()){ // remove dup dates
                    dates.remove(j);
                    j--;
                }
            }
        }
        return dates;
    }

    public static long getDateFormatted(String date) throws ParseException { // simply grabs the date and reformats it.
        SimpleDateFormat sdf = new SimpleDateFormat("MM/yy", Locale.ENGLISH);
        Date formatted = sdf.parse(date);
        return formatted.getTime();
    }

}


