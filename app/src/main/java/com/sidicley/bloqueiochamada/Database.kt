
package com.sidicley.bloqueiochamada

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName="blocked_calls")
data class BlockedCall(@PrimaryKey(autoGenerate=true) val id:Long=0, val number:String, val displayName:String, val reason:String, val timestamp:Long=System.currentTimeMillis())

@Entity(tableName="blacklist")
data class BlackContact(@PrimaryKey val number:String)

@Entity(tableName="whitelist")
data class WhiteContact(@PrimaryKey val number:String)

@Dao interface BlockedDao{
 @Query("SELECT * FROM blocked_calls ORDER BY timestamp DESC") fun observeAll(): Flow<List<BlockedCall>>
 @Insert(onConflict=OnConflictStrategy.REPLACE) suspend fun insert(c:BlockedCall)
 @Delete suspend fun delete(c:BlockedCall)
}
@Dao interface BlacklistDao{
 @Query("SELECT EXISTS(SELECT 1 FROM blacklist WHERE :num LIKE '%' || number || '%' OR number LIKE '%' || :num || '%')") suspend fun exists(num:String): Boolean
 @Insert(onConflict=OnConflictStrategy.REPLACE) suspend fun insert(c:BlackContact)
}
@Dao interface WhitelistDao{
 @Query("SELECT EXISTS(SELECT 1 FROM whitelist WHERE :num LIKE '%' || number || '%' OR number LIKE '%' || :num || '%')") suspend fun exists(num:String): Boolean
 @Insert(onConflict=OnConflictStrategy.REPLACE) suspend fun insert(c:WhiteContact)
}

@Database(entities=[BlockedCall::class, BlackContact::class, WhiteContact::class], version=1)
abstract class AppDatabase: RoomDatabase(){
 abstract fun blockedDao(): BlockedDao
 abstract fun blacklistDao(): BlacklistDao
 abstract fun whitelistDao(): WhitelistDao
 companion object{
   @Volatile private var INSTANCE: AppDatabase? = null
   fun get(ctx:Context): AppDatabase = INSTANCE ?: synchronized(this){
     Room.databaseBuilder(ctx.applicationContext, AppDatabase::class.java, "sidicley.db").build().also{ INSTANCE = it }
   }
 }
}
