package com.mpmt.backend.DTO;

import jakarta.validation.constraints.NotNull;
import com.mpmt.backend.entity.StatusType;

public record StatusUpdateRequest(@NotNull StatusType status) {}
