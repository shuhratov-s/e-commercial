package com.example.ecommercial.controller;

import com.example.ecommercial.ECommercialApplication;
import com.example.ecommercial.controller.dto.request.UserCreateAndUpdateRequest;
import com.example.ecommercial.controller.dto.response.BaseResponse;
import com.example.ecommercial.controller.dto.response.UserGetResponse;
import com.example.ecommercial.domain.enums.UserRole;
import com.example.ecommercial.service.user.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = ECommercialApplication.class)
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserService userService;


    private UserCreateAndUpdateRequest user;

    @BeforeEach
    void setUp() {
        user = UserCreateAndUpdateRequest.builder()
                .name("Asadbek")
                .username("freeman")
                .password("Asadbek1234")
                .userRoles(List.of(UserRole.SUPER_ADMIN))
                .build();

        objectMapper = new ObjectMapper();
    }

    @SneakyThrows
    @Test
    void addUserWithBindingResultError() {
        user.setPassword("1234");
        ModelAndView modelAndView = mockMvc.perform(
                        post("/user/add")
                                .with(SecurityMockMvcRequestPostProcessors
                                        .user("nobleman")
                                        .password("1234"))
                                .flashAttr("user", user))
                .andReturn().getModelAndView();

        Map<String, Object> model = modelAndView.getModel();

        String message = (String) model.get("message");

        assertNotEquals("saved", message);
    }

    @SneakyThrows
    @Test
    void addUserWithSuccess(){
        ModelAndView modelAndView = mockMvc.perform(
                        post("/user/add")
                                .with(SecurityMockMvcRequestPostProcessors
                                        .user("nobleman")
                                        .password("1234"))
                                .flashAttr("user", user))
                .andReturn().getModelAndView();

        Map<String, Object> model = modelAndView.getModel();

        String message = (String) model.get("message");

        assertEquals("saved", message);
    }

    @Test
    @SneakyThrows
    void addUserWithExistingUsername(){
        userService.save(user);
        ModelAndView modelAndView = mockMvc.perform(
                        post("/user/add")
                                .with(SecurityMockMvcRequestPostProcessors
                                        .user("nobleman")
                                        .password("1234"))
                                .flashAttr("user", user))
                .andReturn().getModelAndView();

        Map<String, Object> model = modelAndView.getModel();

        String message = (String) model.get("message");

        assertEquals(user.getUsername() + " already exists", message);
    }

    @SneakyThrows
    @Test
    void updateUserWithSuccess() {
        userService.save(user);
        user = UserCreateAndUpdateRequest.builder()
                .id(1L)
                .username("freeman")
                .password("Freeman1234")
                .build();
        ModelAndView modelAndView = mockMvc.perform(post("/user/update")
                        .with(SecurityMockMvcRequestPostProcessors
                                .user("nobleman")
                                .password("1234"))
                        .flashAttr("user", user))
                .andReturn().getModelAndView();

        Map<String, Object> modelAndView1 = modelAndView.getModel();

        String message = (String) modelAndView1.get("message");

        assertEquals("updated", message);
    }

    @SneakyThrows
    @Test
    void updateUserWithUsernameExists() {
        userService.save(user);
        user = UserCreateAndUpdateRequest.builder()
                .name("Asadbek")
                .username("nobleman")
                .password("Freeman1234")
                .userRoles(List.of(UserRole.ADMIN))
                .build();
        userService.save(user);
        user.setUsername("freeman");
        user.setId(2L);
        ModelAndView modelAndView = mockMvc.perform(post("/user/update")
                        .with(SecurityMockMvcRequestPostProcessors
                                .user("nobleman")
                                .password("1234"))
                        .flashAttr("user", user))
                .andReturn().getModelAndView();

        Map<String, Object> modelAndView1 = modelAndView.getModel();

        String message = (String) modelAndView1.get("message");

        assertEquals(user.getUsername() + " already exists", message);
    }

    @SneakyThrows
    @Test
    void delete() {
        userService.save(user);
        mockMvc.perform(
                        get("/user/delete/{id}", 1L)
                                .with(SecurityMockMvcRequestPostProcessors
                                        .user("nobleman")
                                        .password("1234")))
                .andReturn().getModelAndView();

        BaseResponse<UserGetResponse> response = userService.getById(1L);

        assertEquals(404, response.getStatus());
    }

    @Test
    void getAllUsers() {

    }


}