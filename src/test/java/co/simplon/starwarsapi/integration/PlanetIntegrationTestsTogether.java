package co.simplon.starwarsapi.integration;

import co.simplon.starwarsapi.model.planet.Planet;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PlanetIntegrationTestsTogether {

    @Autowired
    private TestRestTemplate restTemplate;

    private Long lastCreatedPlanetId;

    // Get planet list
    @Test
    public void getPlanetList() {
        // When getting on /api/planets
        ResponseEntity<List> responseEntity = this.restTemplate.exchange("/api/planets", HttpMethod.GET, null, List.class);
        List<?> planets = responseEntity.getBody();

        // Then we should have a list and a 200 response code
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(planets).isNotNull();
    }

    // Get existing planet
    @Test
    public void getExistingPlanet() {
        // When getting on /api/planets/1
        ResponseEntity<Planet> responseEntity = this.restTemplate.exchange("/api/planets/{planetId}", HttpMethod.GET, null, Planet.class, 1);
        Planet existingPlanet = responseEntity.getBody();

        // Then we should have the existing planet and a 200 response code
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(existingPlanet).isNotNull();
        assertThat(existingPlanet.getId()).isEqualTo(1L);
        // ...
    }

    // Get non existing planet
    @Test
    public void getNonExistingPlanet() {
        // When getting on /api/planets/2000
        ResponseEntity<Planet> responseEntity = this.restTemplate.exchange("/api/planets/{planetId}", HttpMethod.GET, null, Planet.class, 2000);

        // Then we should have a list and a 200 response code
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    // Create planet
    @Order(1)
    @Test
    public void createPlanet() {
        // Given a new valid Planet
        String planetName = "Terre";
        Planet newPlanet = new Planet(planetName);

        HttpEntity<Planet> planetHttpEntity = new HttpEntity<>(newPlanet, null);

        // When posting this new planet to /api/planet
        ResponseEntity<Planet> responseEntity = this.restTemplate.exchange("/api/planets", HttpMethod.POST, planetHttpEntity, Planet.class);
        Planet createdPlanet = responseEntity.getBody();

        // Then OK status code should be sent back and
        // the created planet should be returned and should be have an ID and name should be set.
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(createdPlanet.getId()).isPositive();
        assertThat(createdPlanet.getName()).isEqualTo(planetName);

        lastCreatedPlanetId = createdPlanet.getId();
    }

    // Update existing planet
    @Test
    public void updateExistingPlanet() {
        // Given an existing valid Planet (with name modified)
        String newName = "Tatooine";
        Planet existingPlanet = new Planet(1L, newName);

        HttpEntity<Planet> planetHttpEntity = new HttpEntity<>(existingPlanet, null);

        // When putting this existing planet to /api/planet/1
        ResponseEntity<Planet> responseEntity = this.restTemplate.exchange("/api/planets/{planetId}", HttpMethod.PUT, planetHttpEntity, Planet.class, 1);
        Planet updatedPlanet = responseEntity.getBody();

        // Then OK status code should be sent back and
        // the updated planet should be returned and should have the same ID and an updated name.
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(updatedPlanet.getId()).isEqualTo(1L);
        assertThat(updatedPlanet.getName()).isEqualTo(newName);
    }

    // Update non existing planet
    @Test
    public void updateNonExistingPlanet() {
        // Given a non existing Planet
        String planetName = "NonExistingPlanet";
        Planet nonExistingPlanet = new Planet(2000L, planetName);

        HttpEntity<Planet> planetHttpEntity = new HttpEntity<>(nonExistingPlanet, null);

        // When putting this planet to /api/planet/2000
        ResponseEntity<Planet> responseEntity = this.restTemplate.exchange("/api/planets/{planetId}", HttpMethod.PUT, planetHttpEntity, Planet.class, 2000L);

        // Then NOT_FOUND status code should be sent back.
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    // Delete planet
    @Order(2)
    @Test
    public void deletePlanet() {
        // When deleting to /api/planet
        ResponseEntity<Planet> responseEntity = this.restTemplate.exchange("/api/planets/{planetId}", HttpMethod.DELETE, null, Planet.class, lastCreatedPlanetId);

        // Then NO_CONTENT status code should be sent back.
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
}
