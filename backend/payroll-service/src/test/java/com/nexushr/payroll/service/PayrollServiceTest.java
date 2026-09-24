package com.nexushr.payroll.service;

import com.nexushr.payroll.dto.PayrollResponseDto;
import com.nexushr.payroll.dto.PayrollRunDto;
import com.nexushr.payroll.entity.PayrollRun;
import com.nexushr.payroll.entity.SalaryStructure;
import com.nexushr.payroll.entity.TaxConfiguration;
import com.nexushr.payroll.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PayrollServiceTest {

    @Mock
    private PayrollRunRepository payrollRunRepository;

    @Mock
    private PayrollRecordRepository payrollRecordRepository;

    @Mock
    private SalaryStructureRepository salaryStructureRepository;

    @Mock
    private TaxConfigurationRepository taxConfigurationRepository;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private PayrollService payrollService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreatePayrollRunSuccess() {
        PayrollRunDto dto = new PayrollRunDto();
        dto.setPeriodStart(LocalDate.of(2026, 9, 1));
        dto.setPeriodEnd(LocalDate.of(2026, 9, 30));

        when(payrollRunRepository.findByPeriodStartAndPeriodEnd(any(), any())).thenReturn(Optional.empty());

        PayrollRun saved = new PayrollRun();
        saved.setId(UUID.randomUUID());
        saved.setPeriodStart(dto.getPeriodStart());
        saved.setPeriodEnd(dto.getPeriodEnd());
        saved.setStatus("DRAFT");

        when(payrollRunRepository.save(any())).thenReturn(saved);

        PayrollResponseDto response = payrollService.createPayrollRun(dto, "admin@nexushr.com", "127.0.0.1");

        assertNotNull(response);
        assertEquals("DRAFT", response.getStatus());
        verify(payrollRunRepository, times(1)).save(any());
        verify(auditService, times(1)).log(any(), eq("CREATE_PAYROLL_RUN"), any(), any(), any(), any());
    }

    @Test
    void testCreateDuplicatePayrollRunThrowsException() {
        PayrollRunDto dto = new PayrollRunDto();
        dto.setPeriodStart(LocalDate.of(2026, 9, 1));
        dto.setPeriodEnd(LocalDate.of(2026, 9, 30));

        when(payrollRunRepository.findByPeriodStartAndPeriodEnd(any(), any()))
                .thenReturn(Optional.of(new PayrollRun()));

        assertThrows(IllegalStateException.class, () -> {
            payrollService.createPayrollRun(dto, "admin@nexushr.com", "127.0.0.1");
        });
    }

    @Test
    void testProcessPayrollRunSuccess() {
        UUID runId = UUID.randomUUID();
        PayrollRun run = new PayrollRun();
        run.setId(runId);
        run.setStatus("DRAFT");

        when(payrollRunRepository.findById(runId)).thenReturn(Optional.of(run));

        SalaryStructure structure = new SalaryStructure();
        structure.setEmployeeId(UUID.randomUUID());
        structure.setBasicSalary(5000.0);
        structure.setAllowances(1000.0);
        structure.setDeductions(200.0);

        when(salaryStructureRepository.findAll()).thenReturn(Collections.singletonList(structure));

        TaxConfiguration taxConfig = new TaxConfiguration();
        taxConfig.setTaxBracketName("Medium");
        taxConfig.setMinIncome(0.0);
        taxConfig.setMaxIncome(100000.0);
        taxConfig.setTaxPercentage(15.0);

        when(taxConfigurationRepository.findAll()).thenReturn(Collections.singletonList(taxConfig));
        when(payrollRunRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        PayrollResponseDto response = payrollService.processPayrollRun(runId, "admin@nexushr.com", "127.0.0.1");

        assertNotNull(response);
        assertEquals("COMPLETED", response.getStatus());
        assertEquals(1, response.getTotalEmployees());
        verify(payrollRecordRepository, times(1)).saveAll(any());
    }

    @Test
    void testLockLockedOrIncompletePayrollThrowsException() {
        UUID runId = UUID.randomUUID();
        PayrollRun run = new PayrollRun();
        run.setId(runId);
        run.setStatus("DRAFT");

        when(payrollRunRepository.findById(runId)).thenReturn(Optional.of(run));

        assertThrows(IllegalStateException.class, () -> {
            payrollService.lockPayrollRun(runId, "admin@nexushr.com", "127.0.0.1");
        });
    }
}
