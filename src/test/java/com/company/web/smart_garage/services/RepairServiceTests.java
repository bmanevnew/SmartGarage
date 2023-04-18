package com.company.web.smart_garage.services;

import com.company.web.smart_garage.exceptions.EntityDuplicationException;
import com.company.web.smart_garage.exceptions.EntityNotFoundException;
import com.company.web.smart_garage.exceptions.InvalidParamException;
import com.company.web.smart_garage.models.Repair;
import com.company.web.smart_garage.repositories.RepairRepository;
import com.company.web.smart_garage.services.impl.RepairServiceImpl;
import jakarta.persistence.EntityManager;
import org.hibernate.Filter;
import org.hibernate.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static com.company.web.smart_garage.Helpers.createMockRepair;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RepairServiceTests {

    @Mock
    private RepairRepository repairRepository;
    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private RepairServiceImpl repairService;

    private Repair repair;

    @BeforeEach
    public void beforeEach() {
        repair = createMockRepair();

    }

    @Test
    public void getById_Should_ReturnRepair_When_RepairExists() {
        Session session = mock(Session.class);
        when(entityManager.unwrap(Session.class)).thenReturn(session);
        when(session.enableFilter(anyString())).thenReturn(mock(Filter.class));

        when(repairRepository.findById(anyLong())).thenReturn(Optional.of(repair));

        Repair actualRepair = repairService.getById(1L);

        verify(repairRepository).findById(1L);
        assertEquals(repair, actualRepair);
    }

    @Test
    public void getById_Should_ThrowException_When_IdIsNegativeOrZero() {
        Session session = mock(Session.class);
        when(entityManager.unwrap(Session.class)).thenReturn(session);
        when(session.enableFilter(anyString())).thenReturn(mock(Filter.class));

        assertThrows(InvalidParamException.class, () -> repairService.getById(-5));
    }

    @Test
    public void getById_Should_ThrowException_When_RepairDoesNotExist() {
        Session session = mock(Session.class);
        when(entityManager.unwrap(Session.class)).thenReturn(session);
        when(session.enableFilter(anyString())).thenReturn(mock(Filter.class));

        when(repairRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> repairService.getById(1));
    }

    @Test
    public void getByName_Should_ReturnRepair_When_RepairExists() {
        Session session = mock(Session.class);
        when(entityManager.unwrap(Session.class)).thenReturn(session);
        when(session.enableFilter(anyString())).thenReturn(mock(Filter.class));

        when(repairRepository.findFirstByName(anyString())).thenReturn(Optional.of(repair));

        Repair actualRepair = repairService.getByName("Oil change");

        verify(repairRepository).findFirstByName("Oil change");
        assertEquals(repair, actualRepair);
    }

    @Test
    public void getByName_Should_ThrowException_When_RepairDoesNotExist() {
        Session session = mock(Session.class);
        when(entityManager.unwrap(Session.class)).thenReturn(session);
        when(session.enableFilter(anyString())).thenReturn(mock(Filter.class));

        when(repairRepository.findFirstByName(anyString())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> repairService.getByName("Oil change"));
    }

    @Test
    public void getAll_Should_ReturnAllVehicles_When_ParametersAreNull() {
        Session session = mock(Session.class);
        when(entityManager.unwrap(Session.class)).thenReturn(session);
        when(session.enableFilter(anyString())).thenReturn(mock(Filter.class));

        Pageable pageable = PageRequest.of(0, 10);

        Page<Repair> page = new PageImpl<>(List.of(repair));
        when(repairRepository.findByParameters(null, null, null, pageable)).thenReturn(page);

        Page<Repair> result = repairService.getAll(null, null, null, pageable);

        verify(repairRepository).findByParameters(null, null, null, pageable);
        assertEquals(page, result);
    }

    @Test
    public void getAll_Should_ReturnVehicles_When_ParametersAreValid() {
        Session session = mock(Session.class);
        when(entityManager.unwrap(Session.class)).thenReturn(session);
        when(session.enableFilter(anyString())).thenReturn(mock(Filter.class));

        String name = "oil";
        Double priceFrom = 20.99;
        Double PriceTo = 30.99;
        Pageable pageable = PageRequest.of(0, 10);

        Page<Repair> page = new PageImpl<>(List.of(repair));
        when(repairRepository.findByParameters(name, priceFrom, PriceTo, pageable)).thenReturn(page);

        Page<Repair> result = repairService.getAll(name, priceFrom, PriceTo, pageable);

        verify(repairRepository).findByParameters(name, priceFrom, PriceTo, pageable);
        assertEquals(page, result);
    }

    @Test
    public void getAll_Should_ThrowException_When_PriceIsInvalid() {
        Session session = mock(Session.class);
        when(entityManager.unwrap(Session.class)).thenReturn(session);
        when(session.enableFilter(anyString())).thenReturn(mock(Filter.class));

        Pageable pageable = PageRequest.of(0, 10);
        assertThrows(InvalidParamException.class, () ->
                repairService.getAll(null, -12d, null, pageable));
    }

    @Test
    public void getAll_Should_ThrowException_When_PriceIntervalIsInvalid() {
        Session session = mock(Session.class);
        when(entityManager.unwrap(Session.class)).thenReturn(session);
        when(session.enableFilter(anyString())).thenReturn(mock(Filter.class));

        Pageable pageable = PageRequest.of(0, 10);
        assertThrows(InvalidParamException.class, () -> repairService.getAll(
                null, 21.99, 12.99, pageable));
    }

    //requested page 3 is empty thus is invalid
    @Test
    public void getAll_Should_ThrowException_When_PageIsInvalid() {
        Session session = mock(Session.class);
        when(entityManager.unwrap(Session.class)).thenReturn(session);
        when(session.enableFilter(anyString())).thenReturn(mock(Filter.class));

        Pageable pageable = PageRequest.of(3, 10);

        Page<Repair> page = Page.empty();
        when(repairRepository.findByParameters(null, null, null, pageable)).thenReturn(page);

        assertThrows(InvalidParamException.class, () ->
                repairService.getAll(null, null, null, pageable));
    }

    @Test
    public void getAll_Should_ThrowException_When_SortingPropertyInvalid() {
        Session session = mock(Session.class);
        when(entityManager.unwrap(Session.class)).thenReturn(session);
        when(session.enableFilter(anyString())).thenReturn(mock(Filter.class));

        Pageable pageable = PageRequest.of(0, 10, Sort.by("InvalidProperty"));
        assertThrows(InvalidParamException.class, () ->
                repairService.getAll(null, null, null, pageable));
    }

    @Test
    public void create_Should_CreateRepair_When_PropertiesValid() {
        Session session = mock(Session.class);
        when(entityManager.unwrap(Session.class)).thenReturn(session);
        when(session.enableFilter(anyString())).thenReturn(mock(Filter.class));

        when(repairRepository.findFirstByName(anyString())).thenReturn(Optional.empty());
        when(repairRepository.save(repair)).thenReturn(repair);

        Repair returnedRepair = repairService.create(repair);

        verify(repairRepository).save(repair);
        assertEquals(returnedRepair, repair);
    }

    @Test
    public void create_Should_ThrowException_When_PriceIsInvalid() {
        repair.setPrice(-21.59);
        assertThrows(InvalidParamException.class, () -> repairService.create(repair));
    }

    @Test
    public void create_Should_ThrowException_When_NameAlreadyExists() {
        Session session = mock(Session.class);
        when(entityManager.unwrap(Session.class)).thenReturn(session);
        when(session.enableFilter(anyString())).thenReturn(mock(Filter.class));

        when(repairRepository.findFirstByName(anyString())).thenReturn(Optional.of(repair));
        assertThrows(EntityDuplicationException.class, () -> repairService.create(repair));
    }

    @Test
    public void update_Should_UpdateRepair_When_RepairIsValid() {
        Session session = mock(Session.class);
        when(entityManager.unwrap(Session.class)).thenReturn(session);
        when(session.enableFilter(anyString())).thenReturn(mock(Filter.class));

        when(repairRepository.existsById(anyLong())).thenReturn(true);
        when(repairRepository.findFirstByName(anyString())).thenReturn(Optional.empty());
        when(repairRepository.save(repair)).thenReturn(repair);

        Repair updatedRepair = repairService.update(repair);

        verify(repairRepository).save(repair);
        assertEquals(repair, updatedRepair);
    }

    @Test
    public void update_Should_ThrowException_When_RepairWithIdDoesNotExist() {
        when(repairRepository.existsById(anyLong())).thenReturn(false);
        assertThrows(EntityNotFoundException.class, () -> repairService.update(repair));
    }

    @Test
    public void update_Should_ThrowException_When_IdIsNegativeOrZero() {
        repair.setId(-5L);
        assertThrows(InvalidParamException.class, () -> repairService.update(repair));
    }

    @Test
    public void update_Should_ThrowException_When_PriceIsInvalid() {
        repair.setPrice(-20.99);
        assertThrows(InvalidParamException.class, () -> repairService.update(repair));
    }

    @Test
    public void update_Should_ThrowException_When_NameAlreadyExists() {
        Session session = mock(Session.class);
        when(entityManager.unwrap(Session.class)).thenReturn(session);
        when(session.enableFilter(anyString())).thenReturn(mock(Filter.class));

        when(repairRepository.existsById(anyLong())).thenReturn(true);
        Repair differentRepair = createMockRepair();
        differentRepair.setId(22L);
        when(repairRepository.findFirstByName(anyString())).thenReturn(Optional.of(differentRepair));

        assertThrows(EntityDuplicationException.class, () -> repairService.update(repair));
    }

    @Test
    public void delete_Should_DeleteRepairAndReturnIt_When_RepairExists() {
        Session session = mock(Session.class);
        when(entityManager.unwrap(Session.class)).thenReturn(session);
        when(session.enableFilter(anyString())).thenReturn(mock(Filter.class));

        when(repairRepository.findById(anyLong())).thenReturn(Optional.of(repair));

        Repair deletedRepair = repairService.delete(repair.getId());

        verify(repairRepository).deleteById(repair.getId());
        assertEquals(repair, deletedRepair);
    }

    @Test
    public void delete_Should_ThrowException_When_IdIsNegativeOrZero() {
        assertThrows(InvalidParamException.class, () -> repairService.delete(-5));
    }

    @Test
    public void delete_Should_ThrowException_When_RepairDoesNotExist() {
        Session session = mock(Session.class);
        when(entityManager.unwrap(Session.class)).thenReturn(session);
        when(session.enableFilter(anyString())).thenReturn(mock(Filter.class));

        when(repairRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> repairService.delete(1L));
        verify(repairRepository, never()).deleteById(1L);
    }

}
