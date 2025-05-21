package com.example.packyourbag.Dao;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.packyourbag.Models.Items;

import java.util.List;

@Dao
public interface ItemsDao {

    // Insert or replace an item in the database
    @Insert(onConflict = REPLACE)
    void saveItem(Items item);

    // Retrieve all items from the database that belong to a specific category, ordered by ID in ascending order
    @Query(value="select * from items where category=:category order by id asc")
    List<Items> getAll(String category);

    // Delete a specific item from the database
    @Delete
    void delete(Items items);

    // Update the checked status of an item based on its ID
    @Query(value="update items set checked=:checked where ID=:id")
    void checkUncheck(int id, boolean checked);

    // Retrieve the count of all items in the database
    @Query(value= "select count(*) from items")
    Integer getItemsCount();

    // Delete all items added by a specific user
    @Query(value = "delete from items where addedby=:addedby")
    void deleteAllSystemItems(String addedby);

    // Delete all items in a specific category
    @Query(value="delete from items where category=:category")
    Integer deleteAllByCategory(String category);

    // Delete all items in a specific category and added by a specific user
    @Query(value="delete from items where category=:category and addedby=:addedby")
    Integer deleteAllByCategoryAndAddedBy(String category, String addedby);

    // Retrieve all items with a specified checked status, ordered by ID in ascending order
    @Query(value = "select * from items where checked=:checked order by id asc")
    List<Items> getallSellected(Boolean checked);

    // Retrieve the count of items with a specified checked status
    @Query(value="select count (*) from items where checked=:checked")
    Integer getSelectedCount(Boolean checked);
}
