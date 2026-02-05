package com.monaum.Rapid_Global.module.master.approval;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
/**
 * Monaum Hossain
 * monaum.202@gmail.com
 *
 * @since 31-Jan-26 12:13 AM
 */

@RestController
@RequestMapping("/api/admin/approval-levels")
@RequiredArgsConstructor
public class ApprovalLevelController {

    private final ApprovalLevelRepository approvalLevelRepository;
    private final UserApprovalAuthorityRepository authorityRepository;

    @GetMapping
    public ResponseEntity<List<ApprovalLevel>> getAllLevels() {
        return ResponseEntity.ok(approvalLevelRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<ApprovalLevel> createLevel(@RequestBody ApprovalLevel level) {
        return ResponseEntity.ok(approvalLevelRepository.save(level));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApprovalLevel> updateLevel(
            @PathVariable Long id,
            @RequestBody ApprovalLevel level) {
        level.setId(id);
        return ResponseEntity.ok(approvalLevelRepository.save(level));
    }

    @PostMapping("/assign-authority")
    public ResponseEntity<UserApprovalAuthority> assignAuthority(
            @RequestBody UserApprovalAuthority authority) {
        return ResponseEntity.ok(authorityRepository.save(authority));
    }
}