package net.wojteksz128.worktimemeasureapp.repository.api

class ApiErrorResponse(errorMessage: String, @Suppress("UNUSED_PARAMETER") responseBody: String) :
    Throwable(errorMessage)