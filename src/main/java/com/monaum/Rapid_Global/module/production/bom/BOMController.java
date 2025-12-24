//package com.monaum.Rapid_Global.module.production.bom;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Page;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/bom")
//@CrossOrigin(origins = "*")
//public class BOMController {
//
//    @Autowired
//    private BOMService bomService;
//
//    @GetMapping
//    public ResponseEntity<ApiResponse<Page<BOMResDto>>> getAll(
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size,
//            @RequestParam(required = false) String search) {
//        return ResponseEntity.ok(new ApiResponse<>(true,
//            "BOMs retrieved successfully", bomService.getAll(page, size, search)));
//    }
//
//    @GetMapping("/all-active")
//    public ResponseEntity<ApiResponse<Page<BOMResDto>>> getAllActive(
//            @RequestParam Boolean active,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size) {
//        return ResponseEntity.ok(new ApiResponse<>(true,
//            "Active BOMs retrieved", bomService.getAllActive(active, page, size)));
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<ApiResponse<BOMResDto>> getById(@PathVariable Long id) {
//        return ResponseEntity.ok(new ApiResponse<>(true,
//            "BOM retrieved successfully", bomService.getById(id)));
//    }
//
//    @GetMapping("/product/{productId}")
//    public ResponseEntity<ApiResponse<List<BOMResDto>>> getByProduct(
//            @PathVariable Long productId) {
//        return ResponseEntity.ok(new ApiResponse<>(true,
//            "BOMs for product retrieved", bomService.getByFinishedProduct(productId)));
//    }
//
//    @GetMapping("/product/{productId}/default")
//    public ResponseEntity<ApiResponse<BOMResDto>> getDefaultBOM(
//            @PathVariable Long productId) {
//        return ResponseEntity.ok(new ApiResponse<>(true,
//            "Default BOM retrieved", bomService.getDefaultBOM(productId)));
//    }
//
//    @PostMapping
//    public ResponseEntity<ApiResponse<BOMResDto>> create(@RequestBody BOMReqDto dto) {
//        return ResponseEntity.ok(new ApiResponse<>(true,
//            "BOM created successfully", bomService.create(dto)));
//    }
//
//    @PutMapping("/{id}")
//    public ResponseEntity<ApiResponse<BOMResDto>> update(
//            @PathVariable Long id, @RequestBody BOMReqDto dto) {
//        return ResponseEntity.ok(new ApiResponse<>(true,
//            "BOM updated successfully", bomService.update(id, dto)));
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
//        bomService.delete(id);
//        return ResponseEntity.ok(new ApiResponse<>(true, "BOM deleted successfully", null));
//    }
//
//    @PutMapping("/{id}/approve")
//    public ResponseEntity<ApiResponse<BOMResDto>> approve(@PathVariable Long id) {
//        return ResponseEntity.ok(new ApiResponse<>(true,
//            "BOM approved successfully", bomService.approveBOM(id)));
//    }
//
//    @PutMapping("/{id}/set-default")
//    public ResponseEntity<ApiResponse<BOMResDto>> setDefault(@PathVariable Long id) {
//        return ResponseEntity.ok(new ApiResponse<>(true,
//            "BOM set as default", bomService.setAsDefault(id)));
//    }
//
//    @PostMapping("/{id}/clone")
//    public ResponseEntity<ApiResponse<BOMResDto>> clone(
//            @PathVariable Long id, @RequestParam String version) {
//        return ResponseEntity.ok(new ApiResponse<>(true,
//            "BOM cloned successfully", bomService.clone(id, version)));
//    }
//
//    @GetMapping("/{id}/check-availability")
//    public ResponseEntity<ApiResponse<Boolean>> checkAvailability(
//            @PathVariable Long id, @RequestParam Double batchQuantity) {
//        return ResponseEntity.ok(new ApiResponse<>(true,
//            "Material availability checked",
//            bomService.checkMaterialAvailability(id, batchQuantity)));
//    }
//
//    @GetMapping("/{id}/calculate-cost")
//    public ResponseEntity<ApiResponse<Map<String, Object>>> calculateCost(
//            @PathVariable Long id, @RequestParam Double batchQuantity) {
//        return ResponseEntity.ok(new ApiResponse<>(true,
//            "Production cost calculated",
//            bomService.calculateProductionCost(id, batchQuantity)));
//    }
//}