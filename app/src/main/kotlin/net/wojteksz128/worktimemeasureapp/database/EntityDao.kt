package net.wojteksz128.worktimemeasureapp.database

interface EntityDao<Entity> where Entity : EntityDto {

    fun insert(entity: Entity)

    fun update(entity: Entity)

    fun delete(entity: Entity)
}
