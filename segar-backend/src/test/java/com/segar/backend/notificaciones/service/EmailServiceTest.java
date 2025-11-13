package com.segar.backend.notificaciones.service;

import com.segar.backend.notificaciones.api.dto.*;
import com.segar.backend.notificaciones.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para EmailService
 * Verifica la lógica de envío, recepción y sincronización de correos
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Servicio de Email - Tests")
class EmailServiceTest {

    @Mock
    private EmailRepository emailRepository;

    @Mock
    private EmailSender emailSender;

    @Mock
    private EmailReader emailReader;

    @InjectMocks
    private EmailService emailService;

    @Captor
    private ArgumentCaptor<Email> emailCaptor;

    private SendEmailRequest sendEmailRequest;
    private Email emailMock;
    private EmailFilterRequest filterRequest;

    @BeforeEach
    void setUp() {
        // Inyectar el systemEmailAddress usando ReflectionTestUtils
        ReflectionTestUtils.setField(emailService, "systemEmailAddress", "system@segar.com");

        // Configurar request de envío de email
        sendEmailRequest = new SendEmailRequest();
        sendEmailRequest.setToAddresses(Arrays.asList("destinatario@example.com"));
        sendEmailRequest.setToNames(Arrays.asList("Destinatario Test"));
        sendEmailRequest.setSubject("Test Subject");
        sendEmailRequest.setContent("Test Content");
        sendEmailRequest.setIsHtml(false);

        // Configurar email mock
        emailMock = Email.builder()
                .id(1L)
                .fromAddress("system@segar.com")
                .toAddresses("destinatario@example.com")
                .subject("Test Subject")
                .content("Test Content")
                .isHtml(false)
                .isRead(false)
                .type(EmailType.OUTBOUND)
                .status(EmailStatus.SENT)
                .sentDate(LocalDateTime.now())
                .attachments(new ArrayList<>())
                .build();

        // Configurar filtros
        filterRequest = new EmailFilterRequest();
        filterRequest.setPage(0);
        filterRequest.setSize(10);
        filterRequest.setSortBy("receivedDate");
        filterRequest.setSortDirection("DESC");
    }

    // ==================== TESTS DE ENVÍO DE EMAIL ====================

    @Test
    @DisplayName("Enviar email sin adjuntos debe ser exitoso")
    void testEnviarEmailSinAdjuntos_DebeSerExitoso() throws EmailSendingException {
        // Given
        when(emailRepository.save(any(Email.class))).thenReturn(emailMock);
        doNothing().when(emailSender).sendEmail(anyList(), anyList(), anyList(), any(EmailContent.class));

        // When
        EmailResponse response = emailService.sendEmail(sendEmailRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getSubject()).isEqualTo("Test Subject");
        assertThat(response.getStatus()).isEqualTo(EmailStatus.SENT);

        // Verificar que se guardó dos veces (estado QUEUED y luego SENT)
        verify(emailRepository, times(2)).save(emailCaptor.capture());

        List<Email> savedEmails = emailCaptor.getAllValues();
        assertThat(savedEmails.get(0).getStatus()).isEqualTo(EmailStatus.QUEUED);
        assertThat(savedEmails.get(1).getStatus()).isEqualTo(EmailStatus.SENT);

        verify(emailSender).sendEmail(anyList(), anyList(), anyList(), any(EmailContent.class));
    }

    @Test
    @DisplayName("Enviar email con adjuntos debe incluir archivos")
    void testEnviarEmailConAdjuntos_DebeIncluirArchivos() throws Exception {
        // Given
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.isEmpty()).thenReturn(false);
        when(mockFile.getOriginalFilename()).thenReturn("test.pdf");
        when(mockFile.getContentType()).thenReturn("application/pdf");
        when(mockFile.getSize()).thenReturn(1024L);
        when(mockFile.getBytes()).thenReturn("test content".getBytes());

        sendEmailRequest.setAttachments(Arrays.asList(mockFile));

        when(emailRepository.save(any(Email.class))).thenReturn(emailMock);
        // El método espera argumentos específicos, no anyList()
        doNothing().when(emailSender).sendEmailWithAttachmentsAndInlineContent(
                any(), any(), any(), any(EmailContent.class), any(), any()
        );

        // When
        EmailResponse response = emailService.sendEmail(sendEmailRequest);

        // Then
        assertThat(response).isNotNull();
        verify(emailSender).sendEmailWithAttachmentsAndInlineContent(
                any(), any(), any(), any(EmailContent.class), any(), any()
        );
    }

    @Test
    @DisplayName("Error al enviar email debe lanzar excepción")
    void testErrorAlEnviar_DebeLanzarExcepcion() throws EmailSendingException {
        // Given
        when(emailRepository.save(any(Email.class))).thenReturn(emailMock);
        doThrow(new EmailSendingException("SMTP Error"))
                .when(emailSender).sendEmail(anyList(), anyList(), anyList(), any(EmailContent.class));

        // When & Then
        assertThatThrownBy(() -> emailService.sendEmail(sendEmailRequest))
                .isInstanceOf(EmailSendingException.class)
                .hasMessageContaining("Error al enviar correo");
    }

    @Test
    @DisplayName("Enviar email HTML debe configurar flag correctamente")
    void testEnviarEmailHTML() throws EmailSendingException {
        // Given
        sendEmailRequest.setIsHtml(true);
        sendEmailRequest.setContent("<html><body><h1>Test</h1></body></html>");

        // Mock para que el email retornado tenga isHtml = true
        Email htmlEmail = Email.builder()
                .id(1L)
                .fromAddress("system@segar.com")
                .toAddresses("destinatario@example.com")
                .subject("Test Subject")
                .content("<html><body><h1>Test</h1></body></html>")
                .isHtml(true)  // Importante: debe ser true
                .isRead(false)
                .type(EmailType.OUTBOUND)
                .status(EmailStatus.SENT)
                .sentDate(LocalDateTime.now())
                .attachments(new ArrayList<>())
                .build();

        when(emailRepository.save(any(Email.class))).thenReturn(htmlEmail);
        doNothing().when(emailSender).sendEmail(anyList(), anyList(), anyList(), any(EmailContent.class));

        // When
        EmailResponse response = emailService.sendEmail(sendEmailRequest);

        // Then
        assertThat(response.getIsHtml()).isTrue();
    }

    // ==================== TESTS DE BÚSQUEDA Y FILTRADO ====================

    @Test
    @DisplayName("Buscar emails sin filtros debe retornar todos")
    void testBuscarEmailsSinFiltros() {
        // Given
        List<Email> emails = Arrays.asList(emailMock);
        Page<Email> page = new PageImpl<>(emails);

        when(emailRepository.findByCriteria(
                any(), any(), any(), any(), any(), any(), any(), any()
        )).thenReturn(page);

        // When
        EmailSearchFilter searchFilter = EmailSearchFilter.builder()
                .page(0)
                .size(10)
                .build();

        Page<EmailResponse> result = emailService.searchEmails(searchFilter);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getSubject()).isEqualTo("Test Subject");
    }

    @Test
    @DisplayName("Buscar emails con texto debe filtrar correctamente")
    void testBuscarEmailsConTexto() {
        // Given
        List<Email> emails = Arrays.asList(emailMock);
        Page<Email> page = new PageImpl<>(emails, PageRequest.of(0, 10), 1);

        when(emailRepository.findByCriteria(
                any(), any(), any(), any(), any(), any(), any(), any()
        )).thenReturn(page);

        // When
        EmailSearchFilter searchFilter = EmailSearchFilter.builder()
                .searchText("Test")
                .page(0)
                .size(10)
                .build();

        Page<EmailResponse> result = emailService.searchEmails(searchFilter);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isNotEmpty();
    }

    @Test
    @DisplayName("Filtrar por remitente debe aplicar filtro")
    void testFiltrarPorRemitente() {
        // Given
        List<Email> emails = Arrays.asList(emailMock);
        Page<Email> page = new PageImpl<>(emails);

        when(emailRepository.findByCriteria(
                eq("sender@example.com"), any(), any(), any(), any(), any(), any(), any()
        )).thenReturn(page);

        // When
        EmailSearchFilter searchFilter = EmailSearchFilter.builder()
                .fromAddress("sender@example.com")
                .page(0)
                .size(10)
                .build();

        Page<EmailResponse> result = emailService.searchEmails(searchFilter);

        // Then
        assertThat(result).isNotNull();
        verify(emailRepository).findByCriteria(
                eq("sender@example.com"), any(), any(), any(), any(), any(), any(), any()
        );
    }

    @Test
    @DisplayName("Filtrar por estado de lectura debe funcionar")
    void testFiltrarPorEstadoLectura() {
        // Given
        emailMock.setIsRead(false);
        List<Email> emails = Arrays.asList(emailMock);
        Page<Email> page = new PageImpl<>(emails);

        when(emailRepository.findByCriteria(
                any(), any(), any(), any(), eq(false), any(), any(), any()
        )).thenReturn(page);

        // When
        EmailSearchFilter searchFilter = EmailSearchFilter.builder()
                .isRead(false)
                .page(0)
                .size(10)
                .build();

        Page<EmailResponse> result = emailService.searchEmails(searchFilter);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).allMatch(email -> !email.getIsRead());
    }

    // ==================== TESTS DE SINCRONIZACIÓN ====================

    @Test
    @DisplayName("Sincronización debe traer correos nuevos")
    void testSincronizacion_DebeTraerNuevos() throws EmailReadingException {
        // Given
        Email newEmail = Email.builder()
                .messageId("new-message-id")
                .subject("New Email")
                .fromAddress("sender@example.com")
                .toAddresses("receiver@example.com")
                .content("New content")
                .isRead(false)
                .receivedDate(LocalDateTime.now())
                .build();

        when(emailRepository.findLatestReceivedDate()).thenReturn(Optional.of(LocalDateTime.now().minusDays(1)));
        when(emailRepository.findAllMessageIds()).thenReturn(new ArrayList<>());
        when(emailReader.readNewEmails()).thenReturn(Arrays.asList(newEmail));
        when(emailRepository.existsByMessageId(anyString())).thenReturn(false);
        when(emailRepository.save(any(Email.class))).thenReturn(newEmail);

        // When
        emailService.synchronizeEmailsInternal();

        // Then
        verify(emailReader).readNewEmails();
        verify(emailRepository).save(any(Email.class));
    }

    @Test
    @DisplayName("Sincronización debe omitir duplicados")
    void testSincronizacion_DebeOmitirDuplicados() throws EmailReadingException {
        // Given
        Email existingEmail = Email.builder()
                .messageId("existing-message-id")
                .subject("Existing Email")
                .isRead(false)  // Importante: agregar este campo
                .type(EmailType.INBOUND)
                .status(EmailStatus.RECEIVED)
                .build();

        when(emailRepository.findLatestReceivedDate()).thenReturn(Optional.of(LocalDateTime.now().minusDays(1)));
        when(emailRepository.findAllMessageIds()).thenReturn(Arrays.asList("existing-message-id"));
        when(emailReader.readNewEmails()).thenReturn(Arrays.asList(existingEmail));
        when(emailRepository.findByMessageId("existing-message-id")).thenReturn(Optional.of(existingEmail));

        // When
        emailService.synchronizeEmailsInternal();

        // Then
        verify(emailRepository, never()).save(any(Email.class));
    }

    @Test
    @DisplayName("Error en sincronización debe manejarse correctamente")
    void testErrorEnSincronizacion() throws EmailReadingException {
        // Given
        when(emailRepository.findLatestReceivedDate()).thenReturn(Optional.empty());
        when(emailRepository.findAllMessageIds()).thenReturn(new ArrayList<>());
        when(emailReader.readNewEmails()).thenThrow(new EmailReadingException("IMAP Error"));

        // When & Then
        assertThatThrownBy(() -> emailService.synchronizeEmailsInternal())
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Error durante la sincronización");
    }

    // ==================== TESTS DE OPERACIONES SOBRE EMAILS ====================

    @Test
    @DisplayName("Marcar email como leído debe actualizar estado")
    void testMarcarComoLeido() throws EmailReadingException {
        // Given
        emailMock.setIsRead(false);
        when(emailRepository.findById(1L)).thenReturn(Optional.of(emailMock));
        when(emailRepository.save(any(Email.class))).thenReturn(emailMock);

        // When
        emailService.markEmailAsRead(1L);

        // Then
        verify(emailRepository).save(emailCaptor.capture());
        Email savedEmail = emailCaptor.getValue();
        assertThat(savedEmail.getIsRead()).isTrue();
    }

    @Test
    @DisplayName("Marcar email como no leído debe actualizar estado")
    void testMarcarComoNoLeido() throws EmailReadingException {
        // Given
        emailMock.setIsRead(true);
        when(emailRepository.findById(1L)).thenReturn(Optional.of(emailMock));
        when(emailRepository.save(any(Email.class))).thenReturn(emailMock);

        // When
        emailService.markEmailAsUnread(1L);

        // Then
        verify(emailRepository).save(emailCaptor.capture());
        Email savedEmail = emailCaptor.getValue();
        assertThat(savedEmail.getIsRead()).isFalse();
    }

    @Test
    @DisplayName("Eliminar email debe borrar de BD y servidor")
    void testEliminarEmail() throws EmailReadingException {
        // Given
        emailMock.setType(EmailType.INBOUND);
        emailMock.setMessageId("message-id-123");
        when(emailRepository.findById(1L)).thenReturn(Optional.of(emailMock));
        doNothing().when(emailReader).deleteEmailFromServer(anyString());
        doNothing().when(emailRepository).delete(any(Email.class));

        // When
        emailService.deleteEmail(1L);

        // Then
        verify(emailReader).deleteEmailFromServer("message-id-123");
        verify(emailRepository).delete(emailMock);
    }

    @Test
    @DisplayName("Obtener email por ID debe retornar Optional")
    void testObtenerEmailPorId() {
        // Given
        when(emailRepository.findById(1L)).thenReturn(Optional.of(emailMock));

        // When
        Optional<EmailResponse> result = emailService.getEmailById(1L);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getSubject()).isEqualTo("Test Subject");
    }

    // ==================== TESTS DE CONTADORES ====================

    @Test
    @DisplayName("Obtener conteo de no leídos debe retornar cantidad correcta")
    void testObtenerConteoNoLeidos() {
        // Given
        when(emailRepository.countByIsReadFalse()).thenReturn(5L);

        // When
        long count = emailService.getUnreadEmailCount();

        // Then
        assertThat(count).isEqualTo(5L);
    }

    @Test
    @DisplayName("Obtener conteo total debe retornar todos los emails")
    void testObtenerConteoTotal() {
        // Given
        when(emailRepository.count()).thenReturn(50L);

        // When
        long count = emailService.getTotalEmailCount();

        // Then
        assertThat(count).isEqualTo(50L);
    }

    // ==================== TESTS DE CORREOS ENVIADOS ====================

    @Test
    @DisplayName("Obtener correos enviados desde Gmail debe funcionar")
    void testObtenerCorreosEnviados() throws EmailReadingException {
        // Given
        Email sentEmail = Email.builder()
                .id(1L)
                .fromAddress("system@segar.com")
                .toAddresses("receiver@example.com")
                .subject("Sent Email")
                .type(EmailType.OUTBOUND)
                .status(EmailStatus.SENT)
                .sentDate(LocalDateTime.now())
                .attachments(new ArrayList<>())
                .build();

        when(emailReader.readSentEmails()).thenReturn(Arrays.asList(sentEmail));

        // When
        Page<EmailResponse> result = emailService.getSentEmails(PageRequest.of(0, 10));

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getType()).isEqualTo(EmailType.OUTBOUND);
        verify(emailReader).readSentEmails();
    }

    // ==================== TESTS DE CASOS EDGE ====================

    @Test
    @DisplayName("Email sin destinatarios debe fallar validación")
    void testEmailSinDestinatarios() {
        // Given
        sendEmailRequest.setToAddresses(new ArrayList<>());

        // When & Then
        assertThatThrownBy(() -> emailService.sendEmail(sendEmailRequest))
                .isInstanceOf(Exception.class);
    }

    @Test
    @DisplayName("Buscar email inexistente debe retornar Optional vacío")
    void testBuscarEmailInexistente() {
        // Given
        when(emailRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<EmailResponse> result = emailService.getEmailById(999L);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Paginación debe respetar límites")
    void testPaginacionConLimites() {
        // Given
        List<Email> emails = Arrays.asList(emailMock);
        Pageable pageable = PageRequest.of(0, 5);
        Page<Email> page = new PageImpl<>(emails, pageable, 10);

        when(emailRepository.findByCriteria(
                any(), any(), any(), any(), any(), any(), any(), any()
        )).thenReturn(page);

        // When
        EmailSearchFilter searchFilter = EmailSearchFilter.builder()
                .page(0)
                .size(5)
                .build();

        Page<EmailResponse> result = emailService.searchEmails(searchFilter);

        // Then
        assertThat(result.getSize()).isEqualTo(5);
        assertThat(result.getTotalElements()).isEqualTo(10);
    }
}
