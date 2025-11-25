package com.vincent_luracelli.clickup_google_calendar_agenda.web.controller.click_up;

import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.ClickUpClient;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.ListsResponse;
import com.vincent_luracelli.clickup_google_calendar_agenda.security.service.JwtUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/click-up")
@RequiredArgsConstructor
public class ClickUpListController {

    private final ClickUpClient clickUpClient;

    private final JwtUserDetailsService jwtUserDetailsService;

    @GetMapping("/folder/{id}/lists")
    ResponseEntity<ListsResponse> findListsByFolder(@PathVariable String id) {
        String username = jwtUserDetailsService.getUserDetailsFromContextOrThrow().getUsername();
        return ResponseEntity.ok(clickUpClient.findListsByFolder(id, username));
    }

    @GetMapping("/space/{id}/lists")
    ResponseEntity<ListsResponse> findFolderlessListsBySpace(@PathVariable String id) {
        String username = jwtUserDetailsService.getUserDetailsFromContextOrThrow().getUsername();
        return ResponseEntity.ok(clickUpClient.findFolderlessListsBySpace(id, username));
    }

}
