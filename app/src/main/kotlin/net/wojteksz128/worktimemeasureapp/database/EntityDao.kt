package net.wojteksz128.worktimemeasureapp.database

interface EntityDao<Entity> where Entity : EntityDto {

    suspend fun insert(entity: Entity)

    suspend fun update(entity: Entity)

    suspend fun delete(entity: Entity)
}
