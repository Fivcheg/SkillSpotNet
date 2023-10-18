package ru.netology.skillspotnet.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.skillspotnet.dto.Job

@Entity
data class JobEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val name: String,
    val position: String,
    val start: String,
    val finish: String? = null,
    val link: String? = null
) {
    fun toDto(): Job {
        return Job(
            id = id,
            name = name,
            position = position,
            start = start,
            finish = finish,
            link = link
        )
    }

    companion object {
        fun fromDto(dto: Job): JobEntity {
            return JobEntity(
                id = dto.id,
                name = dto.name,
                position = dto.position,
                start = dto.start,
                finish = dto.finish,
                link = dto.link
            )
        }
    }
}

fun List<JobEntity>.toDto(): List<Job> = map(JobEntity::toDto)
fun List<Job>.toEntity(): List<JobEntity> = map(JobEntity::fromDto)
