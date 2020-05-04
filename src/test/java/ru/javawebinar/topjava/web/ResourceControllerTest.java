package ru.javawebinar.topjava.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;

public class ResourceControllerTest extends AbstractControllerTest{

    @Test
    void css() throws Exception {
        perform(get("/resources/css/style.css"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith("text/css"));
    }
}
