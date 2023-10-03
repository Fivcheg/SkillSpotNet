package ru.netology.skillspotnet.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.netology.skillspotnet.dao.EventDao
import ru.netology.skillspotnet.dao.EventRemoteKeyDao
import ru.netology.skillspotnet.dao.JobDao
import ru.netology.skillspotnet.dao.PostDao
import ru.netology.skillspotnet.dao.PostRemoteKeyDao
import ru.netology.skillspotnet.dao.UserDao
import ru.netology.skillspotnet.entity.EventEntity
import ru.netology.skillspotnet.entity.EventRemoteKeyEntity
import ru.netology.skillspotnet.entity.JobEntity
import ru.netology.skillspotnet.entity.PostEntity
import ru.netology.skillspotnet.entity.PostRemoteKeyEntity
import ru.netology.skillspotnet.entity.UserEntity
import ru.netology.skillspotnet.util.Converters

@Database(
    entities = [
        PostEntity::class,
        PostRemoteKeyEntity::class,
        JobEntity::class,
        EventEntity::class,
        EventRemoteKeyEntity::class,
        UserEntity::class,
    ],
    version = 49,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDb : RoomDatabase() {
    abstract fun postDao(): PostDao
    abstract fun postRemoteKeyDao(): PostRemoteKeyDao
    abstract fun jobDao(): JobDao
    abstract fun eventDao(): EventDao
    abstract fun userDao(): UserDao
    abstract fun eventRemoteKeyDao(): EventRemoteKeyDao
}