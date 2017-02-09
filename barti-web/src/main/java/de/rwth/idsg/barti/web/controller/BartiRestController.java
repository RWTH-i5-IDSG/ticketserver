package de.rwth.idsg.barti.web.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.intarsys.security.smartcard.card.CardException;
import de.rwth.idsg.barti.core.aggregate.STBParameters;
import de.rwth.idsg.barti.db.exception.PendingUpdateException;
import de.rwth.idsg.barti.sam.exception.SamException;
import de.rwth.idsg.barti.server.IncomingJob;
import de.rwth.idsg.barti.server.SignatureResultHandler;
import de.rwth.idsg.barti.server.exception.NoMatchingSamException;
import de.rwth.idsg.barti.server.exception.TooManyFailedAttemptsException;
import de.rwth.idsg.barti.web.config.PictureProxy;
import de.rwth.idsg.barti.web.controller.dto.TicketLogRequestDTO;
import de.rwth.idsg.barti.web.controller.dto.TicketRequestDTO;
import de.rwth.idsg.barti.web.service.BartiService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@RestController
@RequestMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
@Log4j2
public class BartiRestController {
    private final BartiService service;
    private final ScheduledExecutorService scheduledExecutorService;

    @Autowired public BartiRestController(BartiService service, ScheduledExecutorService scheduledExecutorService) {
        this.service = service;
        this.scheduledExecutorService = scheduledExecutorService;
    }

    @RequestMapping(
            value = "/ticket",
            method = RequestMethod.POST
    )
    public DeferredResult<ResponseEntity<PictureProxy>> createTicket(
            @RequestBody @Valid final TicketRequestDTO requestDTO
    ) throws IOException {

        final STBParameters parameters = new STBParameters(
                requestDTO.getName(),
                requestDTO.getBegin(),
                requestDTO.getEnd()
        );

        final DeferredResult<ResponseEntity<PictureProxy>> deferredResult = new DeferredResult<>();

        final SignatureResultHandler resultHandler = new SignatureResultHandler() {
            @Override
            public void onSuccess(final byte[] signature) {
                scheduledExecutorService.execute(() -> {
                    log.debug("creating response!");
                    final PictureProxy image = new PictureProxy(signature);
                    deferredResult.setResult(ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(image));
                });
            }

            @Override
            public void onFailure(@Nonnull final Throwable failure) {
                int errorCode;
                try {
                    throw failure;
                } catch (final TooManyFailedAttemptsException e) {
                    errorCode = 1;
                } catch (final NoMatchingSamException e) {
                    errorCode = 2;
                } catch (final CardException e) {
                    errorCode = 3;
                } catch (final IOException e) {
                    errorCode = 4;
                } catch (final SamException e) {
                    errorCode = 5;
                } catch (final Throwable e) {
                    errorCode = Integer.MAX_VALUE;
                }
                deferredResult.setErrorResult(
                        ResponseEntity
                                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .header("Error-Code", Integer.toString(errorCode))
                                .body(null)
                );
            }
        };
        final IncomingJob incomingJob = new IncomingJob(requestDTO.getApiToken(), parameters, resultHandler);
        service.process(incomingJob);
        return deferredResult;
    }

    // -------------------------------------------------------------------------
    // Controller advice stuff
    // -------------------------------------------------------------------------

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Void> handleUnrecognizedPropertyException(final HttpMessageNotReadableException exception) {
        if (exception.getCause() instanceof JsonProcessingException) {
            return handleUnrecognizedPropertyException((JsonProcessingException) exception.getCause());
        }
        return handleError(exception);
    }

    @ExceptionHandler(JsonProcessingException.class)
    public ResponseEntity<Void> handleUnrecognizedPropertyException(final JsonProcessingException exception) {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Void> handleMissingPropertyException(final MethodArgumentNotValidException exception) {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Void> handleError(final Exception exception) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @RequestMapping(
            value = "/log",
            method = RequestMethod.POST
    )
    public void getTicketLog(
            @RequestBody @Valid final TicketLogRequestDTO requestDTO,
            final HttpServletResponse response
    ) throws PendingUpdateException, IOException {
        final String fileName = "tickets-" + requestDTO.getYearMonth() + ".csv";
        final String headerKey = "Content-Disposition";
        final String headerValue = String.format("attachment; filename=\"%s\"", fileName);
        response.setContentType("text/csv");
        response.setHeader(headerKey, headerValue);
        service.getTicketLog(requestDTO.getApiToken(), requestDTO.getYearMonth(),
                response.getWriter());
    }
}
