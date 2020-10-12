package com.example.budgyreceipt;

import java.util.ArrayList;

public class MainCalculations {
    private ArrayList<String> str = new ArrayList<String>();

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
                if (item.matches("(\\d{1,2})/(\\d{1,2})/(\\d{2,4})")){
                    date = item;
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
            if (a.toLowerCase().contains("total")){ // look for just the total
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

    public void monthlyCalc () { // calculate all the numbers the

    }
}
