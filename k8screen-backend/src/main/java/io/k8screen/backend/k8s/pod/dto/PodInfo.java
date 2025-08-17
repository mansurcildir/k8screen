package io.k8screen.backend.k8s.pod.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import org.jetbrains.annotations.NotNull;

@Builder
public record PodInfo(
    @NotNull String name,
    @JsonProperty("total_containers") int totalContainers,
    @JsonProperty("ready_containers") int readyContainers,
    @NotNull String status,
    int restarts,
    @NotNull String age) {}
