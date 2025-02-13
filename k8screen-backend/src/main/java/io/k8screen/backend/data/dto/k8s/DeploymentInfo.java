package io.k8screen.backend.data.dto.k8s;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import org.jetbrains.annotations.NotNull;

@Builder
public record DeploymentInfo(
    @NotNull String name,
    @JsonProperty("total_replicas") int totalReplicas,
    @JsonProperty("ready_replicas") int readyReplicas,
    @JsonProperty("up_to_date") int upToDate,
    int available,
    @NotNull String age) {}
