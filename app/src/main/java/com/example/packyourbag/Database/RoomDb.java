package com.example.packyourbag.Database;

import android.content.Context;
import android.widget.Toast;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.packyourbag.Dao.ItemsDao;
import com.example.packyourbag.Models.Items;

@Database(entities = Items.class, version = 1, exportSchema = false)
public abstract class RoomDb extends RoomDatabase {

    // Singleton instance of the Room database
    private static RoomDb database;

    // Name of the database
    private static String DATABASE_NAME = "MyDb";

    // Get or create a singleton instance of the Room database
    public synchronized static RoomDb getInstance(Context context) {
        // Check if the database instance is null
        if (database == null) {
            // If null, create a new Room database instance using Room.databaseBuilder
            database = Room.databaseBuilder(context.getApplicationContext(), RoomDb.class, DATABASE_NAME)
                    .allowMainThreadQueries()  // Allow database queries on the main thread
                    .fallbackToDestructiveMigration()  // Recreates the database if migrations are needed
                    .build();
        }
        // Return the database instance
        return database;
    }

    // Abstract method that returns the DAO (Data Access Object) for the Items entity
    public abstract ItemsDao mainDao();
}
