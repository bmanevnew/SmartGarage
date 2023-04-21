package com.company.web.smart_garage.services;

import com.company.web.smart_garage.exceptions.EntityNotFoundException;
import com.company.web.smart_garage.exceptions.InvalidParamException;
import com.company.web.smart_garage.models.Vehicle;
import com.company.web.smart_garage.models.Visit;
import com.company.web.smart_garage.repositories.VisitRepository;
import com.company.web.smart_garage.services.impl.VisitServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static com.company.web.smart_garage.Helpers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VisitServiceTests {
    @Mock
    private VisitRepository visitRepository;

    @InjectMocks
    private VisitServiceImpl visitService;

    private Visit visit;
    private Long visitorId;
    private Long vehicleId;
    private LocalDate dateFrom;
    private LocalDate dateTo;
    private Pageable pageable;

    @BeforeEach
    public void beforeEach() {
        visit = createMockVisit();
        visitorId = 1L;
        vehicleId = 1L;
        dateFrom = dateTime.toLocalDate().minusDays(7);
        dateTo = dateTime.toLocalDate();
        pageable = PageRequest.of(0, 20);
    }

    @Test
    public void getById_Should_ReturnVisit_When_VisitExists() {
        when(visitRepository.findById(anyLong())).thenReturn(Optional.of(visit));
        Visit actualVisit = visitService.getById(1L);

        verify(visitRepository).findById(1L);
        assertEquals(visit, actualVisit);
    }

    @Test
    public void getById_Should_ThrowException_When_IdIsNegativeOrZero() {
        assertThrows(InvalidParamException.class, () -> visitService.getById(-5));
    }

    @Test
    public void getById_Should_ThrowException_When_VisitDoesNotExist() {
        when(visitRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> visitService.getById(1));
    }

    @Test
    public void getAll_Should_ReturnPage_When_ParametersAreValid() {
        Page<Visit> expectedPage = new PageImpl<>(Collections.emptyList());
        when(visitRepository.findByParameters(visitorId, vehicleId, Date.valueOf(dateFrom), Date.valueOf(dateTo),
                pageable)).thenReturn(expectedPage);

        Page<Visit> actualPage = visitService.getAll(visitorId, vehicleId, dateFrom, dateTo, pageable);

        verify(visitRepository).findByParameters(visitorId, vehicleId, Date.valueOf(dateFrom), Date.valueOf(dateTo),
                pageable);
        assertEquals(expectedPage, actualPage);
    }

    @Test
    public void getAll_Should_ThrowException_When_VisitorIdIsInvalid() {
        assertThrows(InvalidParamException.class, () ->
                visitService.getAll(-1L, vehicleId, dateFrom, dateTo, pageable));
    }

    @Test
    public void getAll_Should_ThrowException_When_VehicleIdIsInvalid() {
        assertThrows(InvalidParamException.class, () ->
                visitService.getAll(visitorId, -1L, dateFrom, dateTo, pageable));
    }

    @Test
    public void getAll_Should_ThrowException_When_DateIntervalIsInvalid() {
        assertThrows(InvalidParamException.class, () ->
                visitService.getAll(visitorId, vehicleId, dateTo, dateFrom, pageable));
    }

    @Test
    public void getAll_Should_ThrowException_When_DateIsInvalid() {
        assertThrows(InvalidParamException.class, () ->
                visitService.getAll(visitorId, vehicleId, dateFrom, LocalDate.now().plusDays(1), pageable));
    }

    @Test
    public void getAll_Should_ThrowException_When_SortPropertiesAreInvalid() {
        pageable = PageRequest.of(0, 10, Sort.by("invalidProperty"));

        assertThrows(InvalidParamException.class, () ->
                visitService.getAll(visitorId, vehicleId, dateFrom, dateTo, pageable));
    }

    @Test
    public void create_Should_SetDateAndSaveVisit_When_VisitIsValid() {
        when(visitRepository.save(visit)).thenReturn(visit);

        Visit createdVisit = visitService.create(visit);

        assertNotNull(createdVisit.getDate());
        verify(visitRepository).save(visit);
        assertEquals(visit, createdVisit);
    }

    @Test
    public void update_Should_UpdateVisit_When_VisitIsValid() {
        when(visitRepository.findById(visit.getId())).thenReturn(Optional.of(visit));
        when(visitRepository.save(visit)).thenReturn(visit);

        Visit updatedVisit = visitService.update(visit);

        verify(visitRepository).findById(visit.getId());
        verify(visitRepository).save(visit);
        assertEquals(visit, updatedVisit);
    }

    @Test
    public void update_Should_IgnoreVisitor_When_VisitIsUpdated() {
        when(visitRepository.findById(visit.getId())).thenReturn(Optional.of(visit));
        Visit visitChanged = visit;
        visitChanged.setVisitor(createMockEmployee());
        when(visitRepository.save(visit)).thenReturn(visit);

        Visit updatedVisit = visitService.update(visitChanged);

        verify(visitRepository).findById(visit.getId());
        verify(visitRepository).save(visit);
        assertEquals(visit, updatedVisit);
    }

    @Test
    public void update_Should_IgnoreVehicle_When_VisitIsUpdated() {
        when(visitRepository.findById(visit.getId())).thenReturn(Optional.of(visit));
        Visit visitChanged = visit;
        Vehicle vehicle = createMockVehicle();
        vehicle.setLicensePlate("BT 1231 PP");
        visitChanged.setVehicle(vehicle);
        when(visitRepository.save(visit)).thenReturn(visit);

        Visit updatedVisit = visitService.update(visitChanged);

        verify(visitRepository).findById(visit.getId());
        verify(visitRepository).save(visit);
        assertEquals(visit, updatedVisit);
    }

    @Test
    public void update_Should_ThrowException_When_VisitIdIsInvalid() {
        when(visitRepository.findById(visit.getId())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> visitService.update(visit));
    }

    @Test
    public void update_Should_ThrowException_When_VisitIdIsNegativeOrZero() {
        visit.setId(-5L);
        assertThrows(InvalidParamException.class, () -> visitService.update(visit));
    }

    @Test
    public void delete_Should_DeleteVisitAndReturnIt_When_VisitExists() {
        when(visitRepository.findByIdFetchRepairs(visit.getId())).thenReturn(Optional.of(visit));

        Visit deletedVisit = visitService.delete(visit.getId());

        verify(visitRepository).deleteById(visit.getId());
        assertEquals(visit, deletedVisit);
    }

    @Test
    public void delete_Should_ThrowException_When_IdIsNegativeOrZero() {
        assertThrows(InvalidParamException.class, () -> visitService.delete(-5));
    }

    @Test
    public void delete_Should_ThrowException_When_VisitDoesNotExist() {
        when(visitRepository.findByIdFetchRepairs(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> visitService.delete(1L));
        verify(visitRepository, never()).deleteById(1L);
    }
}
