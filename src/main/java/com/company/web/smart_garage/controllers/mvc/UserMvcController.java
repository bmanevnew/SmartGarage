package com.company.web.smart_garage.controllers.mvc;

import com.company.web.smart_garage.data_transfer_objects.UserDtoIn;
import com.company.web.smart_garage.exceptions.InvalidParamException;
import com.company.web.smart_garage.models.User;
import com.company.web.smart_garage.models.filters.UserFilterOptionsDto;
import com.company.web.smart_garage.services.EmailSenderService;
import com.company.web.smart_garage.services.UserService;
import com.company.web.smart_garage.utils.mappers.UserMapper;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.company.web.smart_garage.utils.AuthorizationUtils.userIsAdminOrEmployee;

@RequiredArgsConstructor
@Controller
@RequestMapping("/users")
public class UserMvcController {

    public static final int DEFAULT_PAGE_SIZE = 5;
    private final UserService userService;
    private final UserMapper userMapper;
    private final EmailSenderService emailSenderService;

    @GetMapping("/{id}")
    public String getById(@PathVariable long id, Authentication authentication, Model model) {
        User user;

        user = userService.getById(id);
        if (!userIsAdminOrEmployee(authentication) &&
                !user.getId().equals(userService.getByUsername(authentication.getName()).getId())) {
            //Todo change view to show user was unauthorized to access resource
            return "notFound";
        }
        model.addAttribute("user", user);
        return "user";
    }

    @GetMapping
    public String getAll(@ModelAttribute("filterOptionsDto") UserFilterOptionsDto filterOptionsDto,
                         @PageableDefault(size = DEFAULT_PAGE_SIZE) Pageable pageable,
                         Authentication authentication, Model viewModel) {

        if (!userIsAdminOrEmployee(authentication)) {
            throw new AccessDeniedException("Access denied");
        }

        viewModel.addAttribute("pageSize", pageable.getPageSize());
        Page<User> users;
        LocalDate dateFrom = null;
        LocalDate dateTo = null;

        try {

            if (filterOptionsDto.getDateFrom() != null && !filterOptionsDto.getDateFrom().isBlank())
                dateFrom = LocalDate.parse(filterOptionsDto.getDateFrom(), DateTimeFormatter.ofPattern("MM/dd/yyyy"));
            if (filterOptionsDto.getDateTo() != null && !filterOptionsDto.getDateTo().isBlank())
                dateTo = LocalDate.parse(filterOptionsDto.getDateTo(), DateTimeFormatter.ofPattern("MM/dd/yyyy"));

            users = userService.getFilteredUsers(filterOptionsDto.getFirstName(), filterOptionsDto.getModel(),
                    filterOptionsDto.getBrand(), dateFrom, dateTo, pageable);
        } catch (InvalidParamException e) {
            viewModel.addAttribute("paramError", e.getMessage());
            return "allUsers";
        } catch (NumberFormatException e) {
            viewModel.addAttribute("paramError", "Invalid year input.");
            return "allUsers";
        }

        viewModel.addAttribute("users", users);
        return "allUsers";
    }

}
