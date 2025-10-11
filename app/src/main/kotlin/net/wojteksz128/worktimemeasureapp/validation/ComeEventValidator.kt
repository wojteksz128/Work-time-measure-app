package net.wojteksz128.worktimemeasureapp.validation

import net.wojteksz128.worktimemeasureapp.model.ComeEvent
import javax.inject.Inject

class ComeEventValidator @Inject constructor() : DataValidator<ComeEvent> {

    override fun validate(model: ComeEvent): ValidationResult<ComeEvent> {
        val errors = mutableListOf<String>()

        if (model.endDate?.isBefore(model.startDate) == true)
            errors.add("End date cannot be before start date")

        return if (errors.isEmpty()) {
            ValidationResult(model, true)
        } else {
            ValidationResult(model, false, errors)
        }
    }
}