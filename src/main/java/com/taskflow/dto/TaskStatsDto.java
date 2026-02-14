package com.taskflow.dto;

public record TaskStatsDto(long total, long inProgress, long completed, long overdue) {
}