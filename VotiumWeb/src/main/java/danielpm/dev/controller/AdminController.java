package danielpm.dev.controller;

import danielpm.dev.dto.response.user.PaginatedUser;
import danielpm.dev.model.Category;
import danielpm.dev.model.Role;
import danielpm.dev.model.User;
import danielpm.dev.service.CategoryService;
import danielpm.dev.service.PermissionService;
import danielpm.dev.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * @author danielpm.dev
 */
@Controller
public class AdminController {

    private final PermissionService permissionService;
    private final CategoryService categoryService;
    private final UserService userService;

    public AdminController(PermissionService permissionService, CategoryService categoryService, UserService userService) {
        this.permissionService = permissionService;
        this.categoryService = categoryService;
        this.userService = userService;
    }

    @GetMapping("/admin")
    public String adminPanel(Model model, HttpSession session) {
        String token = (String) session.getAttribute("jwtToken");
        Boolean isAuthenticated = token != null && !token.isEmpty();

        if (!isAuthenticated || !permissionService.isAdmin(token)) {
            return "redirect:/";
        }

        model.addAttribute("authenticated", isAuthenticated);
        model.addAttribute("admin", true); // Explicitly set for view consistency
        return "admin/panel";
    }

    /* Categories */
    @GetMapping("/admin/categories")
    public String showCategories(Model model, HttpSession session) {
        String token = (String) session.getAttribute("jwtToken");
        Boolean isAuthenticated = token != null && !token.isEmpty();

        if (!isAuthenticated || !permissionService.isAdmin(token)) {
            return "redirect:/";
        }

        model.addAttribute("categories", categoryService.getAllCategories(token));
        model.addAttribute("authenticated", isAuthenticated);
        model.addAttribute("admin", true);
        return "admin/category/categories";
    }

    @PostMapping("/admin/categories/new")
    public String addCategory(@RequestParam("name") String name, HttpSession session, RedirectAttributes redirectAttributes) {
        String token = (String) session.getAttribute("jwtToken");
        if (!permissionService.isAdmin(token)) {
            return "redirect:/";
        }

        try {
            Category category = new Category();
            category.setName(name);
            categoryService.saveCategory(token, category);
            redirectAttributes.addFlashAttribute("success", "Categoría añadida correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al añadir la categoría: " + e.getMessage());
        }
        return "redirect:/admin/categories";
    }

    @PostMapping("/admin/categories/edit/{id}")
    public String editCategory(@PathVariable("id") Long id, @RequestParam("name") String name,
                               HttpSession session, RedirectAttributes redirectAttributes) {
        String token = (String) session.getAttribute("jwtToken");
        if (!permissionService.isAdmin(token)) {
            return "redirect:/";
        }

        try {
            Category category = categoryService.getCategoryById(token, id);
            if (category != null) {
                category.setName(name);
                categoryService.updateCategory(token, category);
                redirectAttributes.addFlashAttribute("success", "Categoría actualizada correctamente.");
            } else {
                redirectAttributes.addFlashAttribute("error", "Categoría no encontrada.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar la categoría: " + e.getMessage());
        }
        return "redirect:/admin/categories";
    }

    @PostMapping("/admin/categories/delete/{id}")
    public String deleteCategory(@PathVariable("id") Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        String token = (String) session.getAttribute("jwtToken");
        if (!permissionService.isAdmin(token)) {
            return "redirect:/";
        }

        try {
            categoryService.deleteCategoryById(token, id);
            redirectAttributes.addFlashAttribute("success", "Categoría eliminada correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar la categoría: " + e.getMessage());
        }
        return "redirect:/admin/categories";
    }

    /* Users */
    @GetMapping("/admin/users")
    public String showUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            Model model, HttpSession session
    ) {
        String token = (String) session.getAttribute("jwtToken");
        boolean isAuthenticated = token != null && !token.isEmpty();

        if (!isAuthenticated || !permissionService.isAdmin(token)) {
            return "redirect:/";
        }

        try {
            PaginatedUser paginated = userService.getAllUsers(token, page, size);
            model.addAttribute("users", paginated.getUsers());
            model.addAttribute("totalPages", paginated.getTotalPages());
            model.addAttribute("currentPage", paginated.getCurrentPage());
            model.addAttribute("size", size); // para mantener el tamaño entre páginas
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar los usuarios: " + e.getMessage());
        }

        model.addAttribute("authenticated", isAuthenticated);
        model.addAttribute("admin", true);
        return "admin/user/users";
    }

    @PostMapping("/admin/users/edit/{id}")
    public String editRoleUser(@PathVariable("id") Long id, @RequestParam("role") String role,
                               HttpSession session, RedirectAttributes redirectAttributes) {
        String token = (String) session.getAttribute("jwtToken");
        if (!permissionService.isAdmin(token)) {
            return "redirect:/";
        }

        try {
            User user = userService.getUserById(token, id);
            if (user != null) {
                user.setRole(Role.valueOf(role.toUpperCase()));
                userService.updateRoleUser(token, user);
                redirectAttributes.addFlashAttribute("success", "Rol del usuario actualizado correctamente.");
            } else {
                redirectAttributes.addFlashAttribute("error", "Usuario no encontrado.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar el rol del usuario: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/admin/users/delete/{id}")
    public String deleteUser(@PathVariable("id") Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        String token = (String) session.getAttribute("jwtToken");

        if (!permissionService.isAdmin(token)) {
            return "redirect:/";
        }

        try {
            userService.deleteUserById(token, id);
            redirectAttributes.addFlashAttribute("success", "Usuario eliminado correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar el usuario: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }
}
