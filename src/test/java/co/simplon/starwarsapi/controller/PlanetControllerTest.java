package co.simplon.starwarsapi.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import co.simplon.starwarsapi.model.planet.Planet;
import co.simplon.starwarsapi.repository.PlanetRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Optional;

@WebMvcTest
public class PlanetControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    PlanetRepository planetRepository;

    @Test
    public void getPlanets() throws Exception {
        when(this.planetRepository.findAll()).thenReturn(new ArrayList<>());

        this.mockMvc.perform(get("/api/planets")).andExpect(status().isOk());
    }

    @Test
    public void getExistingPlanet() throws Exception {
        when(this.planetRepository.findById(anyLong())).thenReturn(Optional.of(new Planet(1L, "Alderaan")));

        this.mockMvc.perform(get("/api/planets/1")).andExpect(status().isOk());
    }

    @Test
    public void getNonExistingPlanet() throws Exception {
        when(this.planetRepository.findById(anyLong())).thenReturn(Optional.empty());

        this.mockMvc.perform(get("/api/planets/1")).andExpect(status().isNotFound());
    }

    @Test
    public void createPlanet() throws Exception {
        when(this.planetRepository.save(any())).thenReturn(new Planet(1L, "Terre"));

        this.mockMvc.perform(post("/api/planets")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"Terre\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value("Terre"))
                .andExpect(jsonPath("id").value(1));
    }

}
