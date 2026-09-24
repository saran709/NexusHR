package com.nexushr.leave.dto;

import com.nexushr.leave.entity.Holiday;
import java.time.LocalDate;
import java.util.UUID;

public class HolidayResponse {

    private UUID id;
    private String name;
    private LocalDate holidayDate;
    private String description;

    public HolidayResponse() {}

    public HolidayResponse(Holiday holiday) {
        this.id = holiday.getId();
        this.name = holiday.getName();
        this.holidayDate = holiday.getHolidayDate();
        this.description = holiday.getDescription();
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public LocalDate getHolidayDate() { return holidayDate; }
    public String getDescription() { return description; }
}
