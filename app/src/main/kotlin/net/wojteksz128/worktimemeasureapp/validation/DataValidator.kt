package net.wojteksz128.worktimemeasureapp.validation

import net.wojteksz128.worktimemeasureapp.model.DomainModel

interface DataValidator<DM> where DM : DomainModel {
    fun validate(model: DM): ValidationResult<DM> = ValidationResult(model, true)
}

data class ValidationResult<DM>(
    val model: DM,
    val isValid: Boolean,
    val errors: List<String> = emptyList(),
)
