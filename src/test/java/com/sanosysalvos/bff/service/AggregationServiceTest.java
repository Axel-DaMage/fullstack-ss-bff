package com.sanosysalvos.bff.service;

import com.sanosysalvos.bff.client.LocationServiceClient;
import com.sanosysalvos.bff.client.MatchServiceClient;
import com.sanosysalvos.bff.client.PetServiceClient;
import com.sanosysalvos.bff.model.LocationDto;
import com.sanosysalvos.bff.model.MatchDto;
import com.sanosysalvos.bff.model.PetDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AggregationServiceTest {

    @Mock
    private PetServiceClient petClient;
    @Mock
    private LocationServiceClient locationClient;
    @Mock
    private MatchServiceClient matchClient;

    private AggregationService service;

    @BeforeEach
    void setUp() {
        service = new AggregationService(petClient, locationClient, matchClient);
    }

    @Test
    void obtenerTodasMascotas_DeberiaRetornarLista() {
        when(petClient.getAllPets()).thenReturn(List.of(new PetDto()));
        assertEquals(1, service.getAllPets().size());
    }

    @Test
    void obtenerMascotaPorId_CuandoExiste_DeberiaRetornar() {
        PetDto p = new PetDto();
        p.setId(1L);
        when(petClient.getPetById(1L)).thenReturn(p);
        assertEquals(1L, service.getPetById(1L).getId());
    }

    @Test
    void obtenerMascotaPorId_CuandoNoExiste_DeberiaRetornarNulo() {
        when(petClient.getPetById(99L)).thenReturn(null);
        assertNull(service.getPetById(99L));
    }

    @Test
    void crearMascota_DeberiaDelegarACliente() {
        PetDto p = new PetDto();
        p.setName("Nueva");
        when(petClient.createPet(p)).thenReturn(p);
        assertEquals("Nueva", service.createPet(p).getName());
    }

    @Test
    void actualizarMascota_DeberiaDelegarACliente() {
        PetDto p = new PetDto();
        p.setName("Actualizada");
        when(petClient.updatePet(1L, p)).thenReturn(p);
        assertEquals("Actualizada", service.updatePet(1L, p).getName());
    }

    @Test
    void eliminarMascota_DeberiaDelegarACliente() {
        service.deletePet(1L);
        verify(petClient).deletePet(1L);
    }

    @Test
    void obtenerTodasUbicaciones_DeberiaRetornarLista() {
        when(locationClient.getAllLocations()).thenReturn(List.of(new LocationDto()));
        assertEquals(1, service.getAllLocations().size());
    }

    @Test
    void obtenerUbicacionPorId_DeberiaRetornar() {
        LocationDto l = new LocationDto();
        l.setId(1L);
        when(locationClient.getLocationById(1L)).thenReturn(l);
        assertEquals(1L, service.getLocationById(1L).getId());
    }

    @Test
    void obtenerUbicacionesPorZona_DeberiaRetornarFiltradas() {
        when(locationClient.getLocationsByZone("Las Condes")).thenReturn(List.of(new LocationDto()));
        assertEquals(1, service.getLocationsByZone("Las Condes").size());
    }

    @Test
    void actualizarUbicacion_DeberiaDelegar() {
        LocationDto l = new LocationDto();
        when(locationClient.updateLocation(eq(1L), any())).thenReturn(l);
        assertNotNull(service.updateLocation(1L, l));
    }

    @Test
    void enriqueceMatchesConNombresDeMascotas() {
        PetDto perdido = new PetDto();
        perdido.setId(1L);
        perdido.setName("Perdido");
        PetDto encontrado = new PetDto();
        encontrado.setId(2L);
        encontrado.setName("Encontrado");

        MatchDto match = new MatchDto();
        match.setId(1L);
        match.setPetLostId(1L);
        match.setPetFoundId(2L);

        when(matchClient.getAllMatches()).thenReturn(List.of(match));
        when(petClient.getPetById(1L)).thenReturn(perdido);
        when(petClient.getPetById(2L)).thenReturn(encontrado);

        List<MatchDto> resultado = service.getAllMatches();
        assertEquals("Perdido", resultado.get(0).getPetLostName());
        assertEquals("Encontrado", resultado.get(0).getPetFoundName());
    }

    @Test
    void noEnriqueceSiMatchNoTieneIds() {
        MatchDto match = new MatchDto();
        match.setId(1L);
        when(matchClient.getAllMatches()).thenReturn(List.of(match));

        List<MatchDto> resultado = service.getAllMatches();
        assertNull(resultado.get(0).getPetLostName());
        verify(petClient, never()).getPetById(any());
    }

    @Test
    void obtenerMatchPorId_DeberiaEnriquecer() {
        MatchDto match = new MatchDto();
        match.setId(1L);
        match.setPetLostId(1L);
        PetDto p = new PetDto();
        p.setId(1L);
        p.setName("Perdido");

        when(matchClient.getMatchById(1L)).thenReturn(match);
        when(petClient.getPetById(1L)).thenReturn(p);

        assertEquals("Perdido", service.getMatchById(1L).getPetLostName());
    }

    @Test
    void obtenerMatchPorId_CuandoNoExiste_DeberiaRetornarNulo() {
        when(matchClient.getMatchById(99L)).thenReturn(null);
        assertNull(service.getMatchById(99L));
    }

    @Test
    void crearMatch_DeberiaCrearYEnriquecer() {
        MatchDto match = new MatchDto();
        match.setPetLostId(1L);
        when(matchClient.createMatch(1L, 2L)).thenReturn(match);
        when(petClient.getPetById(1L)).thenReturn(new PetDto());

        assertNotNull(service.createMatch(1L, 2L));
    }

    @Test
    void confirmarMatch_DeberiaActualizarEstado() {
        MatchDto match = new MatchDto();
        when(matchClient.updateMatchStatus(1L, "CONFIRMED")).thenReturn(match);
        assertNotNull(service.confirmMatch(1L));
    }

    @Test
    void rechazarMatch_DeberiaActualizarEstado() {
        MatchDto match = new MatchDto();
        when(matchClient.updateMatchStatus(1L, "REJECTED")).thenReturn(match);
        assertNotNull(service.rejectMatch(1L));
    }

    @Test
    void eliminarMatch_DeberiaDelegar() {
        service.deleteMatch(1L);
        verify(matchClient).deleteMatch(1L);
    }

    @Test
    void dashboardRetornaEstadisticasAgregadas() {
        PetDto perdido = new PetDto();
        PetDto encontrado = new PetDto();
        LocationDto locCentro = new LocationDto();
        locCentro.setLatitude(-33.45);
        locCentro.setLongitude(-70.65);
        LocationDto locCondes = new LocationDto();
        locCondes.setLatitude(-33.43);
        locCondes.setLongitude(-70.55);

        when(petClient.getPetsByStatus("PERDIDO")).thenReturn(List.of(perdido));
        when(petClient.getPetsByStatus("ENCONTRADO")).thenReturn(List.of(encontrado));
        when(matchClient.getMatchesByStatus("PENDIENTE")).thenReturn(List.of());
        when(locationClient.getAllLocations()).thenReturn(List.of(locCentro, locCondes));

        Map<String, Object> dash = service.getDashboard();
        assertEquals(1, dash.get("lostPets"));
        assertEquals(1, dash.get("foundPets"));
        assertEquals(2, dash.get("totalLocations"));
    }

    @Test
    void dashboardAgrupaUbicacionesPorZona() {
        LocationDto l1 = new LocationDto();
        l1.setLatitude(-33.45);
        l1.setLongitude(-70.65);
        LocationDto l2 = new LocationDto();
        l2.setLatitude(-33.45);
        l2.setLongitude(-70.65);

        when(petClient.getPetsByStatus("PERDIDO")).thenReturn(List.of());
        when(petClient.getPetsByStatus("ENCONTRADO")).thenReturn(List.of());
        when(matchClient.getMatchesByStatus("PENDIENTE")).thenReturn(List.of());
        when(locationClient.getAllLocations()).thenReturn(List.of(l1, l2));

        Map<String, Object> dash = service.getDashboard();
        Map<String, Long> zonas = (Map<String, Long>) dash.get("locationsByZone");
        assertEquals(2, zonas.get("Santiago Centro"));
    }

    @Test
    void determinaZona_CuandoTieneZonaExistente_DeberiaUsarla() {
        LocationDto loc = new LocationDto();
        loc.setLatitude(-33.45);
        loc.setLongitude(-70.65);
        loc.setZone("Las Condes");

        when(petClient.getPetsByStatus("PERDIDO")).thenReturn(List.of());
        when(petClient.getPetsByStatus("ENCONTRADO")).thenReturn(List.of());
        when(matchClient.getMatchesByStatus("PENDIENTE")).thenReturn(List.of());
        when(locationClient.getAllLocations()).thenReturn(List.of(loc));

        Map<String, Object> dash = service.getDashboard();
        Map<String, Long> zonas = (Map<String, Long>) dash.get("locationsByZone");
        assertEquals(1, zonas.get("Las Condes"));
    }

    @Test
    void obtieneMascotaConUbicacion() {
        PetDto pet = new PetDto();
        pet.setId(1L);
        LocationDto loc = new LocationDto();
        loc.setPetId(1L);

        when(petClient.getPetById(1L)).thenReturn(pet);
        when(locationClient.getAllLocations()).thenReturn(List.of(loc));

        Map<String, Object> resultado = service.getPetWithLocation(1L);
        assertEquals(pet, resultado.get("pet"));
        assertEquals(loc, resultado.get("location"));
    }

    @Test
    void obtieneMascotaConUbicacion_SinUbicacion_DeberiaRetornarNulo() {
        PetDto pet = new PetDto();
        pet.setId(1L);

        when(petClient.getPetById(1L)).thenReturn(pet);
        when(locationClient.getAllLocations()).thenReturn(List.of());

        Map<String, Object> resultado = service.getPetWithLocation(1L);
        assertNull(resultado.get("location"));
    }

    @Test
    void ejecutarMatchingAutomatico_DeberiaDelegar() {
        service.runAutomaticMatching();
        verify(matchClient).runAutomaticMatching();
    }
}
