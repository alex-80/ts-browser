package com.hinnka.tsbrowser.persist

import androidx.annotation.DrawableRes
import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import com.hinnka.tsbrowser.R

object Favorites {
    val default = listOf(
        Favorite(
            "https://www.google.com",
            "Google",
            R.drawable.ic_google,
            0xFF4285F4
        ),
        Favorite(
            "https://mail.google.com",
            "Gmail",
            R.drawable.ic_gmail,
            0xFFD93025
        ),
        Favorite(
            "https://www.youtube.com",
            "Youtube",
            R.drawable.ic_youtube,
            0xFFFF0200
        ),
        Favorite(
            "https://www.amazon.com",
            "Amazon",
            R.drawable.ic_amazon,
            0xFFFEBD68
        ),
        Favorite(
            "https://www.facebook.com",
            "Facebook",
            R.drawable.ic_facebook,
            0xFF1877F2
        ),
        Favorite(
            "https://www.instagram.com",
            "Instagram",
            R.drawable.ic_instagram,
            0xFFEA4E4D
        ),
        Favorite(
            "https://www.twitter.com",
            "Twitter",
            R.drawable.ic_twitter,
            0xFF129AFB
        ),
    )

    suspend fun init() {
        if (LocalStorage.isFavoriteInitialized) return
        AppDatabase.instance.favoriteDao().add(*default.toTypedArray())
        LocalStorage.isFavoriteInitialized = true
    }
}

@Entity
data class Favorite(
    @PrimaryKey val url: String,
    @ColumnInfo val title: String,
    @ColumnInfo @DrawableRes val iconRes: Int = 0,
    @ColumnInfo val color: Long = 0,
    @ColumnInfo val order: Int = 0,
)

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorite ORDER BY `order`")
    suspend fun getAll(): List<Favorite>

    @Insert(onConflict = REPLACE)
    suspend fun add(vararg favorite: Favorite)

    @Delete
    suspend fun delete(vararg favorite: Favorite)
}