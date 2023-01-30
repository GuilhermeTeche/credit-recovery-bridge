package br.com.itau.creditrecoverybridge.keygeneration.controller;

import br.com.itau.creditrecoverybridge.modules.keygeneration.controller.GenerationKeyController;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class GenerationKeyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GenerationKeyController generationKeyController;

    @Before
    public void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(generationKeyController).build();
    }

    @Test
    public void testGetPublicKeyIsHttpStatusCode200() throws Exception {
        this.mockMvc.perform(get("/keys/getPublicKey")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void getKeyReturningContenTypeJson() throws Exception {
        this.mockMvc.perform(get("/keys/getPublicKey")
                .contentType(MediaType.TEXT_PLAIN))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

}