package com.example.packyourbag.Data;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import com.example.packyourbag.Constants.MyConstants;
import com.example.packyourbag.Database.RoomDb;
import com.example.packyourbag.Models.Items;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// Application class for managing data throughout the app
public class AppData extends Application {

    RoomDb database;
    String category;
    Context context;
    public static final String LAST_VERSION = "LAST_VERSION";
    public static final int NEW_VERSION = 1;

    // Constructor with Room database
    public AppData(RoomDb database) {
        this.database = database;
    }

    // Constructor with Room database and context
    public AppData(RoomDb database, Context context) {
        this.database = database;
        this.context = context;
    }

    // Method to get basic needs data
    public List<Items> getBasicDate() {
        category = "Basic Needs";
        List<Items> basicItem = new ArrayList<>();
        String[] data = {"Visa", "Passport", "Tickets", "Wallet", "Driving License", "Aadhaar Card", "Money", "House key", "EyeWear", "Water Bottle"};
        return prepareItemList(MyConstants.BASIC_NEEDS, data);
    }



    // Helper method to prepare item list
    public List<Items> prepareItemList(String category, String[] data) {
        List<String> list = Arrays.asList(data);
        List<Items> dataList = new ArrayList<>();
        dataList.clear();
        for (int i = 0; i < list.size(); i++) {
            dataList.add(new Items(list.get(i), category, false));
        }
        return dataList;
    }

    // Method to get all data for different categories
    public List<List<Items>> getAllData() {
        List<List<Items>> listOfAllItems = new ArrayList<>();
        listOfAllItems.add(getBasicDate());
        // Add other categories here
        return listOfAllItems;
    }

    // Method to persist all data into the database
    public void persistAllData() {
        List<List<Items>> listOfAllItem = getAllData();
        for (List<Items> list : listOfAllItem) {
            for (Items item : list) {
                database.mainDao().saveItem(item);
            }
        }
        System.out.println("Data Added");
    }

    //reset or delete items in a specific category
    public void persistDataByCategory(String category, Boolean onlyDelete) {
        try {
            List<Items> list = deleteAndGetListByCategory(category, onlyDelete);
            if (!onlyDelete) {
                for (Items item : list) {
                    database.mainDao().saveItem(item);
                }
                Toast.makeText(context, category + " Reset Successfully.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, category + " Reset Successfully.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
        }
    }

    // method to delete and get list by category
    private List<Items> deleteAndGetListByCategory(String category, Boolean onlyDelete) {
        if (onlyDelete) {
            database.mainDao().deleteAllByCategoryAndAddedBy(category, MyConstants.SYSTEM_SMALL);
        } else {
            database.mainDao().deleteAllByCategory(category);
        }
        switch (category) {
            //return data based on the category
            case MyConstants.BASIC_NEEDS:
                return getBasicDate();
            default:
                return new ArrayList<>();
        }
    }
}
