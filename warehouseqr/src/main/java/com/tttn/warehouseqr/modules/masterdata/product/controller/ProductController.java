    package com.tttn.warehouseqr.modules.masterdata.product.controller;
    
    import com.tttn.warehouseqr.modules.auth.entity.User;
    import com.tttn.warehouseqr.modules.auth.repository.UserRepository;
    import com.tttn.warehouseqr.modules.masterdata.category.entity.ProductCategory;
    import com.tttn.warehouseqr.modules.masterdata.category.service.impl.CategoryService;
    import com.tttn.warehouseqr.modules.masterdata.product.dto.ProductDTO;
    import com.tttn.warehouseqr.modules.masterdata.product.dto.ProductPageResponse;
    import com.tttn.warehouseqr.modules.masterdata.product.dto.ProductQrDTO;
    import com.tttn.warehouseqr.modules.masterdata.product.entity.Product;
    import com.tttn.warehouseqr.modules.masterdata.product.entity.TempImportData;
    import com.tttn.warehouseqr.modules.masterdata.product.repository.TempImportDataRepository;
    import com.tttn.warehouseqr.modules.masterdata.product.service.impl.ImportQrService;
    import com.tttn.warehouseqr.modules.masterdata.product.service.impl.ProductBatchService;
    import com.tttn.warehouseqr.modules.masterdata.product.service.impl.ProductService;
    import com.tttn.warehouseqr.modules.masterdata.supplier.service.SupplierService;
    import com.tttn.warehouseqr.modules.masterdata.unit.entity.Unit;
    import com.tttn.warehouseqr.modules.masterdata.unit.service.UnitService;
    import com.tttn.warehouseqr.modules.masterdata.warehouse.services.WarehouseService;
    import org.springframework.data.domain.Page;
    import org.springframework.http.ResponseEntity;
    import org.springframework.stereotype.Controller;
    import org.springframework.ui.Model;
    import org.springframework.web.bind.annotation.*;
    import org.springframework.web.multipart.MultipartFile;
    import org.springframework.web.servlet.mvc.support.RedirectAttributes;
    
    import java.security.Principal;
    import java.util.List;
    
    @Controller
    @RequestMapping("/products")
    public class ProductController {
        private final ProductService productService;
        private final CategoryService categoryService;
        private final UnitService unitService;
        private final ImportQrService importQrService;
        private final ProductBatchService productBatchService;
        private final SupplierService supplierService;
        private final WarehouseService warehouseService;
        private final TempImportDataRepository tempImportDataRepository;
        private final UserRepository userRepository;
        public ProductController(ProductService productService,
                                 CategoryService categoryService,
                                 UnitService unitService,
                                 ImportQrService importQrService,
                                 ProductBatchService productBatchService,
                                 SupplierService supplierService,
                                 WarehouseService warehouseService,
                                 TempImportDataRepository tempImportDataRepository,
                                 UserRepository userRepository) {
            this.productService = productService;
            this.categoryService = categoryService;
            this.unitService = unitService;
            this.importQrService = importQrService;
            this.productBatchService = productBatchService;
            this.supplierService = supplierService;
            this.warehouseService=warehouseService;
            this.tempImportDataRepository = tempImportDataRepository;
            this.userRepository = userRepository;
        }
    
        // Bước 1: Nhận file, lưu bảng tạm và Validate
        @PostMapping("/import-csv")
        public String importCsv(@RequestParam("files") MultipartFile file, RedirectAttributes redirectAttributes){
            if(file.isEmpty()){
                redirectAttributes.addFlashAttribute("error", "Vui lòng chọn file CSV!");
                return "redirect:/products";
            }
            try {
                String sessionId = importQrService.importCsvToTemp(file);
    
                importQrService.ValidateTempData(sessionId);
    
                return "redirect:/products/import-preview?sessionId=" + sessionId;
    
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("error","Lỗi khi đọc file: " + e.getMessage());
                return "redirect:/products";
            }
        }
    
        @GetMapping("/import-preview")
        public String previewImportData(@RequestParam("sessionId") String sessionId, Model model) {
            List<TempImportData> tempList = tempImportDataRepository.findByImportSessionId(sessionId);
    
            model.addAttribute("tempList", tempList);
            model.addAttribute("sessionId", sessionId);
    
            return "productAndQr/products/product-list/preview_import";
        }
    
    
        @PostMapping("/import-confirm")
        public String confirmImport(@RequestParam("sessionId") String sessionId, Principal principal, RedirectAttributes redirectAttributes) {
            try {
                Long currentUserId;
                if (principal != null){
                    String username = principal.getName();
                    User currentUser = userRepository.findByUsername(username)
                            .orElseThrow(() -> new RuntimeException("Không xác định được người dùng hiện tại"));
                    currentUserId = currentUser.getUserId();
                } else {
                    throw new RuntimeException("Phiên đăng nhập đã hết hạn, vui lòng đăng nhập lại");
                }
    
                String resultMessage = importQrService.confirmImport(sessionId, currentUserId);
    
                redirectAttributes.addFlashAttribute("success", resultMessage);
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("error", e.getMessage());
            }
            return "redirect:/products";
        }
    
        @GetMapping("/import-cancel")
        public String cancelImport(@RequestParam("sessiomId") String sessionId, RedirectAttributes redirectAttributes){
            List<TempImportData> temps = tempImportDataRepository.findByImportSessionId(sessionId);
            if(!temps.isEmpty()){
                tempImportDataRepository.deleteAll();
            }
            redirectAttributes.addFlashAttribute("success", "Đã hủy quá trình Import và xử lý dữ liệu tạm.");
            return  "redirect:/products";
        }
    
        @GetMapping
        public String listProduct(@RequestParam(defaultValue = "1") int page,
                                  @RequestParam(defaultValue = "10") int limit,
                                  @RequestParam(defaultValue = "") String keyw,
                                  @RequestParam(defaultValue = "0") long categoryId,
                                  Model model){
            Page<ProductQrDTO> batchPage = productBatchService.getBatchesWithQrCustom(page,limit,keyw,categoryId);
            ProductPageResponse response = new ProductPageResponse();
            response.setContent(batchPage);
            response.setTotalPage(batchPage.getTotalPages());
            response.setCurrentPage(page);
            response.setTotalElements(batchPage.getTotalElements());
    
            model.addAttribute("productPage", response);
            model.addAttribute("keyword", keyw);
            model.addAttribute("categoryId", categoryId);
            model.addAttribute("categories",categoryService.getAllCategory());
            model.addAttribute("suppliers", supplierService.getAllSuppliers());
            model.addAttribute("units", unitService.getAllUnit());
            model.addAttribute("warehouses", warehouseService.getAllWarehouse());
    
            return "productAndQr/products/product-list/list";
        }
    
        @GetMapping("/create")
        public String showCreateForm(Model model){
            List<ProductCategory> categories = categoryService.getAllCategory();
            List<Unit> units = unitService.getAllUnit();
            model.addAttribute("categories",categories);
            model.addAttribute("units",units);
            model.addAttribute("productDTO", new ProductDTO());
    
            return "productAndQr/products/product-form/create-form";
        }
        @PostMapping("/create")
        public String createProduct(@ModelAttribute("productDTO") ProductDTO productDTO){
            productService.createProduct(productDTO);
    
            return "redirect:/products";
        }
    
        @GetMapping("/edit/{id}")
        public String showEditFrom(@PathVariable("id") long productId, Model model){
            Product product = productService.getProductById(productId);
            List<ProductCategory> categories = categoryService.getAllCategory();
            List<Unit> units = unitService.getAllUnit();
    
            ProductDTO dto = new ProductDTO();
            dto.setSku(product.getSku());
            dto.setProductName(product.getProductName());
            dto.setDescription(product.getDescription());
            dto.setMinStock(product.getMinStock());
    
            if(product.getCategory() != null){
                dto.setCategoryId(product.getCategory().getCategoryId());
            }
            if(product.getUnit() != null){
                dto.setUnitId(product.getUnit().getUnitId());
            }
    
            model.addAttribute("categories",categories);
            model.addAttribute("units",units);
            model.addAttribute("productDTO" , dto);
            model.addAttribute("product_id",productId);
            return "productAndQr/products/product-form/edit-form";
        }
        @PostMapping("/edit/{id}")
        public String updateProduct(@PathVariable("id") long productId, @ModelAttribute("productDTO") ProductDTO productDTO){
            productService.updateProdcut(productId, productDTO);
            return "redirect:/products";
        }
    
        @GetMapping("/delete/{id}")
        public String deleteProduct(@PathVariable("id") long productId){
            productService.deleteProduct(productId);
    
            return "redirect:/products";
        }
    
        @PostMapping("/generate-qr")
        @ResponseBody
        public ResponseEntity<String> generateManualQr(@RequestBody List<Long> batchId){
            importQrService.generateManualQr(batchId);
            return ResponseEntity.ok("Success");
        }



    }
