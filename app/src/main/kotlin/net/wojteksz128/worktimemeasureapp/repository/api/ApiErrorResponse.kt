package net.wojteksz128.worktimemeasureapp.repository.api

class ApiErrorResponse(errorMessage: String) : Throwable(errorMessage)