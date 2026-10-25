package com.abhishek.studentms.model;

import java.util.Map;

public record StudentStats(long totalStudents, long onBudget, long selfFinanced, Map<String, Long> byDepartment) {
}
