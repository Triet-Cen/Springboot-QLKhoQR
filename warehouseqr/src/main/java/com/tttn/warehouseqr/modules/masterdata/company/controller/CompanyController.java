package com.tttn.warehouseqr.modules.masterdata.company.controller;

import com.tttn.warehouseqr.modules.masterdata.company.dto.CompanyCreateRequest;
import com.tttn.warehouseqr.modules.masterdata.company.dto.CompanyUpdateRequest;
import com.tttn.warehouseqr.modules.masterdata.company.entity.Company;
import com.tttn.warehouseqr.modules.masterdata.company.service.CompanyService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/companies")
public class CompanyController {
	private final CompanyService companyService;

	public CompanyController(CompanyService companyService) {
		this.companyService = companyService;
	}

	@GetMapping
	public String list(@RequestParam(defaultValue = "") String keyword, Model model) {
		List<Company> companies = companyService.searchCompanies(keyword);
		model.addAttribute("companies", companies);
		model.addAttribute("keyword", keyword);
		return "masterdata/company-list";
	}

	@GetMapping("/add")
	public String showAddForm(Model model) {
		model.addAttribute("companyRequest", new CompanyCreateRequest());
		return "masterdata/company-form";
	}

	@PostMapping("/add")
	public String create(@Valid @ModelAttribute("companyRequest") CompanyCreateRequest req,
						 BindingResult result,
						 RedirectAttributes ra) {
		if (result.hasErrors()) {
			return "masterdata/company-form";
		}

		try {
			companyService.createCompany(req);
			ra.addFlashAttribute("successMessage", "Thêm công ty thành công!");
			return "redirect:/companies";
		} catch (RuntimeException e) {
			ra.addFlashAttribute("errorMessage", e.getMessage());
			return "redirect:/companies/add";
		}
	}

	@GetMapping("/edit/{id}")
	public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes ra) {
		try {
			model.addAttribute("companyRequest", companyService.getUpdateById(id));
			model.addAttribute("isEdit", true);
			return "masterdata/company-form";
		} catch (RuntimeException e) {
			ra.addFlashAttribute("errorMessage", e.getMessage());
			return "redirect:/companies";
		}
	}

	@PostMapping("/edit")
	public String update(@Valid @ModelAttribute("companyRequest") CompanyUpdateRequest req,
						 BindingResult result,
						 Model model,
						 RedirectAttributes ra) {
		if (result.hasErrors()) {
			model.addAttribute("isEdit", true);
			return "masterdata/company-form";
		}

		try {
			companyService.updateCompany(req);
			ra.addFlashAttribute("successMessage", "Cập nhật công ty thành công!");
			return "redirect:/companies";
		} catch (RuntimeException e) {
			ra.addFlashAttribute("errorMessage", e.getMessage());
			return "redirect:/companies/edit/" + req.getCompanyId();
		}
	}

	@PostMapping("/delete/{id}")
	public String delete(@PathVariable Long id, RedirectAttributes ra) {
		try {
			companyService.deleteCompany(id);
			ra.addFlashAttribute("successMessage", "Xóa công ty thành công!");
		} catch (RuntimeException e) {
			ra.addFlashAttribute("errorMessage", e.getMessage());
		}
		return "redirect:/companies";
	}
}

