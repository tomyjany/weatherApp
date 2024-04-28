package com.inn.weatherApp.JWT;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ViewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void forwardToIndex_ReturnsIndexHtml() throws Exception {
        mockMvc.perform(get("/anyPath"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("<title>Pocasko</title>")));
    }

    @Test
    public void redirectRoot_RedirectsToIndexHtml() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("<title>Pocasko</title>")));
    }
    @Test
    public void testForwardToIndex() throws Exception {
        // Arrange
        ViewController viewController = new ViewController();
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setRequestURI("/anyPath");

        // Act
        String viewName = viewController.forwardToIndex();

        // Assert
        assertEquals("forward:/index.html", viewName);
    }

    @Test
    public void testRedirectRoot() throws Exception {
        // Arrange
        ViewController viewController = new ViewController();
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setRequestURI("/");

        // Act
        String viewName = viewController.redirectRoot();

        // Assert
        assertEquals("redirect:/index.html", viewName);
    }
}
