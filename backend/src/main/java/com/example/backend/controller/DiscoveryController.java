package com.example.backend.controller;

import com.example.backend.dto.UserDiscoveryDTO;
import com.example.backend.service.DiscoveryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users/discover")
public class DiscoveryController {

    private final DiscoveryService discoveryService;

    public DiscoveryController(DiscoveryService discoveryService) {
        this.discoveryService = discoveryService;
    }

    @Operation(summary = "Discover users", description = "Retrieves registered users with their teaching and learning skills. Optionally excludes a user by ID and/or filters by name.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "List of users with their skills"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public ResponseEntity<List<UserDiscoveryDTO>> discoverUsers(
            @Parameter(description = "Optional partial name to search users by (case-insensitive)")
            @RequestParam(required = false) String name,
            @Parameter(description = "ID of the user to exclude from discovery results (e.g., the logged-in user)")
            @RequestParam(required = false) Long excludeUserId) {
        List<UserDiscoveryDTO> users = discoveryService.discoverUsers(excludeUserId, name);
        return ResponseEntity.ok(users);
    }
}
