package com.epam.esm.conttoller;

import com.epam.esm.model.Certificate;
import com.epam.esm.model.Tag;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("dev")
@Sql(scripts = "classpath:data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class CertificateControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void showCertificates_shouldReturnFoundCertificates() throws Exception {
        mockMvc.perform(get("/certificates"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("free music listen certificate")))
                .andExpect(jsonPath("$[0].description", is("spotify free music listening")))
                .andExpect(jsonPath("$[0].price", is(200.50)))
                .andExpect(jsonPath("$[0].duration", is(20)))
                .andExpect(jsonPath("$[0].tags", hasSize(3)))
                .andExpect(jsonPath("$[0].tags[0].id", is(1)))
                .andExpect(jsonPath("$[0].tags[0].name", is("spotify")))
                .andExpect(jsonPath("$[0].tags[1].id", is(2)))
                .andExpect(jsonPath("$[0].tags[1].name", is("music")))
                .andExpect(jsonPath("$[0].tags[2].id", is(3)))
                .andExpect(jsonPath("$[0].tags[2].name", is("art")));
    }

    @Test
    public void showCerificateById_shouldReturnHttpStatusCode404() throws Exception {
        mockMvc.perform(get("/certificates/{id}", 100L))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorCode", is("40401")))
                .andExpect(jsonPath("$.message", is("Certificate with id 100 not found")));
    }

    @Test
    public void showCertificateById_shouldReturnFoundCertificate() throws Exception {
        mockMvc.perform(get("/certificates/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("free music listen certificate")))
                .andExpect(jsonPath("$.description", is("spotify free music listening")))
                .andExpect(jsonPath("$.price", is(200.50)))
                .andExpect(jsonPath("$.duration", is(20)))
                .andExpect(jsonPath("$.tags", hasSize(3)))
                .andExpect(jsonPath("$.tags[0].id", is(1)))
                .andExpect(jsonPath("$.tags[0].name", is("spotify")))
                .andExpect(jsonPath("$.tags[1].id", is(2)))
                .andExpect(jsonPath("$.tags[1].name", is("music")))
                .andExpect(jsonPath("$.tags[2].id", is(3)))
                .andExpect(jsonPath("$.tags[2].name", is("art")));
    }

    @Test
    public void saveCertificate_shouldReturnBadRequestForInvalidCertificate() throws Exception {
        Certificate invalidCertificate = new Certificate("", "", -10.0, -10);
        mockMvc.perform(post("/certificates")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(invalidCertificate)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorCode", is("40003")))
                .andExpect(jsonPath("$.message", is("Passed certificate fields are invalid." +
                        " All fields must be not empty. Price and duration must be positive")));
    }

    @Test
    public void saveCertificate_shouldAddNewCertificate() throws Exception {
        Certificate certificate = new Certificate("certificate", "test certificate", 10.0, 100);
        mockMvc.perform(post("/certificates")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(certificate)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.name", is("certificate")))
                .andExpect(jsonPath("$.description", is("test certificate")))
                .andExpect(jsonPath("$.price", is(10.0)))
                .andExpect(jsonPath("$.duration", is(100)));
    }

    @Test
    public void updateCertificate_shouldUpdateCertificateWithPassedId() throws Exception {
        Certificate certificateForUpdate = new Certificate("spotify", "spotify", 10.0, 10);
        mockMvc.perform(post("/certificates/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(certificateForUpdate)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("spotify")))
                .andExpect(jsonPath("$.description", is("spotify")))
                .andExpect(jsonPath("$.price", is(10.0)))
                .andExpect(jsonPath("$.duration", is(10)))
                .andExpect(jsonPath("$.tags", hasSize(3)))
                .andExpect(jsonPath("$.tags[0].id", is(1)))
                .andExpect(jsonPath("$.tags[0].name", is("spotify")))
                .andExpect(jsonPath("$.tags[1].id", is(2)))
                .andExpect(jsonPath("$.tags[1].name", is("music")))
                .andExpect(jsonPath("$.tags[2].id", is(3)))
                .andExpect(jsonPath("$.tags[2].name", is("art")));
    }

    @Test
    public void deleteCertificate_shouldDeleteCertificate() throws Exception {
        mockMvc.perform(delete("/certificates/{id}", 1L))
                .andExpect(status().isNoContent());
    }

    @Test
    public void showCertificateTags_shouldReturnCertificateTags() throws Exception {
        mockMvc.perform(get("/certificates/{id}/tags", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("spotify")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("music")))
                .andExpect(jsonPath("$[2].id", is(3)))
                .andExpect(jsonPath("$[2].name", is("art")));
    }

    @Test
    public void showCertificateTag_shouldReturnCertificateTag() throws Exception {
        mockMvc.perform(get("/certificates/{id}/tags/{tagId}", 1L, 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("spotify")));
    }

    @Test
    public void showCertificateTag_shouldReturn404WhenThereIsNotCertificateTag() throws Exception {
        mockMvc.perform(get("/certificates/{id}/tags/{tagId}", 1L, 100L))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorCode", is("40402")))
                .andExpect(jsonPath("$.message", is("Tag with id 100 not found")));
    }

    @Test
    public void addTagToCertificate_shouldReturnCertificateWithNewTag() throws Exception {
        List<Tag> tags = Collections.singletonList(new Tag(4L, "new tag"));
        mockMvc.perform(post("/certificates/{id}/tags", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(tags)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("free music listen certificate")))
                .andExpect(jsonPath("$.description", is("spotify free music listening")))
                .andExpect(jsonPath("$.price", is(200.5)))
                .andExpect(jsonPath("$.duration", is(20)))
                .andExpect(jsonPath("$.tags", hasSize(4)))
                .andExpect(jsonPath("$.tags[0].id", is(1)))
                .andExpect(jsonPath("$.tags[0].name", is("spotify")))
                .andExpect(jsonPath("$.tags[1].id", is(2)))
                .andExpect(jsonPath("$.tags[1].name", is("music")))
                .andExpect(jsonPath("$.tags[2].id", is(3)))
                .andExpect(jsonPath("$.tags[2].name", is("art")))
                .andExpect(jsonPath("$.tags[3].id", is(4)))
                .andExpect(jsonPath("$.tags[3].name", is("new tag")))
                .andDo(print());
    }

    @Test
    public void addTagToCertificate_shouldReturnBadRequestCode() throws Exception {
        List<Tag> tags = Collections.singletonList(new Tag(""));
        mockMvc.perform(post("/certificates/{id}/tags", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(tags)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode", is("40004")))
                .andExpect(jsonPath("$.message", is("Passed tag fields are invalid. All fields must be not empty")));
    }

    @Test
    public void deleteCertificateTag_shouldDeleteCertificateIfThereIsCertificate() throws Exception {
        mockMvc.perform(delete("/certificates/{id}/tags/{tagId}", 1L, 1L))
                .andExpect(status().isNoContent());
    }

    @Test
    public void deleteCertificateTag_shouldReturn404ErrorCodeIfThereIsNoCertificateTag() throws Exception {
        mockMvc.perform(delete("/certificates/{id}/tags/{tagId}", 1L, 100L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode", is("40402")))
                .andExpect(jsonPath("$.message", is("Tag with id 100 not found")));
    }
}