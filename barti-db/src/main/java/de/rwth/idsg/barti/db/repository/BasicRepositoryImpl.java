package de.rwth.idsg.barti.db.repository;

import de.rwth.idsg.barti.core.aggregate.*;
import de.rwth.idsg.barti.core.datatypes.basic.INT1;
import de.rwth.idsg.barti.core.datatypes.basic.ReferenceNumberThree;
import de.rwth.idsg.barti.core.datatypes.basic.ReferenceNumberTwo;
import de.rwth.idsg.barti.core.datatypes.enums.TransportmittelKategorieCode;
import de.rwth.idsg.barti.db.PseudoSequence;
import de.rwth.idsg.barti.db.exception.PendingUpdateException;
import jooq.barti.db.tables.records.TicketsCreatedRecord;
import lombok.extern.log4j.Log4j2;
import org.joda.time.DateTimeZone;
import org.joda.time.Interval;
import org.joda.time.LocalDateTime;
import org.joda.time.YearMonth;
import org.jooq.DSLContext;
import org.jooq.Record2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.Nullable;
import java.io.Writer;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import static de.rwth.idsg.barti.db.Util.convertToUTC;
import static jooq.barti.db.tables.BetreiberKey.BETREIBER_KEY;
import static jooq.barti.db.tables.DeploymentProductToTransactionData.DEPLOYMENT_PRODUCT_TO_TRANSACTION_DATA;
import static jooq.barti.db.tables.LastUpdate.LAST_UPDATE;
import static jooq.barti.db.tables.LogApiToken.LOG_API_TOKEN;
import static jooq.barti.db.tables.Organisation.ORGANISATION;
import static jooq.barti.db.tables.Partner.PARTNER;
import static jooq.barti.db.tables.Product.PRODUCT;
import static jooq.barti.db.tables.SequenceInformation.SEQUENCE_INFORMATION;
import static jooq.barti.db.tables.TicketApiToken.TICKET_API_TOKEN;
import static jooq.barti.db.tables.TicketsCreated.TICKETS_CREATED;
import static jooq.barti.db.tables.TransactionData.TRANSACTION_DATA;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@Log4j2
@Repository
public class BasicRepositoryImpl implements BasicRepository {

    @Autowired private DSLContext ctx;

    @Override
    public List<PseudoSequence> getAllTicketSequenceCountersForDeployment(final int deployment) {
        return ctx
                .selectFrom(SEQUENCE_INFORMATION)
                .where(SEQUENCE_INFORMATION.DEPLOYMENT_ID.equal(deployment))
                .fetch().map(record ->
                        PseudoSequence.builder()
                                .deploymentId(record.getDeploymentId())
                                .kvpOrgId(record.getKvpOrgId())
                                .currentValue(new AtomicLong(record.getCurrentValue()))
                                .minValue(record.getMinValue())
                                .maxValue(record.getMaxValue())
                                .build()
                );
    }

    @Override
    public long getNextTicketNumber(final PseudoSequence sequence) {
        final long currentValue = sequence.getCurrentValue().getAndIncrement();
        if (currentValue < sequence.getMinValue() || sequence.getMaxValue() < currentValue) {
            throw new IllegalStateException("current sequence value out of range!");
        }
        final int numUpdatedRecords = ctx
                .update(SEQUENCE_INFORMATION)
                .set(SEQUENCE_INFORMATION.CURRENT_VALUE, currentValue + 1)
                .where(
                        SEQUENCE_INFORMATION.CURRENT_VALUE.equal(currentValue)
                                .and(SEQUENCE_INFORMATION.DEPLOYMENT_ID.equal(sequence.getDeploymentId())
                                        .and(SEQUENCE_INFORMATION.KVP_ORG_ID.equal(sequence.getKvpOrgId())))
                ).execute();
        if (numUpdatedRecords != 1) {
            throw new IllegalStateException("no line was updated where one update was expected");
        }
        return currentValue;
    }

    @Override
    public List<ProductConfiguration> getAllConfigurationsForDeployment(final int deployment) {
        final jooq.barti.db.tables.Organisation pv = ORGANISATION.as("pv");
        final jooq.barti.db.tables.Organisation kvp = ORGANISATION.as("kvp");
        final jooq.barti.db.tables.Organisation transactionOp = ORGANISATION.as("transactionOp");
        final jooq.barti.db.tables.Organisation terminalOrg = ORGANISATION.as("terminalOrg");
        final jooq.barti.db.tables.Organisation locationOrg = ORGANISATION.as("locationOrg");
        return ctx
                .select(
                        DEPLOYMENT_PRODUCT_TO_TRANSACTION_DATA.DEPLOYMENT_ID,
                        DEPLOYMENT_PRODUCT_TO_TRANSACTION_DATA.PRODUCT_ID,
                        pv.ORG_ID,
                        pv.NAME,
                        kvp.ORG_ID,
                        kvp.NAME,
                        transactionOp.ORG_ID,
                        transactionOp.NAME,
                        terminalOrg.ORG_ID,
                        terminalOrg.NAME,
                        locationOrg.ORG_ID,
                        locationOrg.NAME,
                        TRANSACTION_DATA.TERMINAL_NUMBER,
                        TRANSACTION_DATA.LOCATION_NUMBER,
                        PARTNER.PARTNER_ID,
                        PARTNER.NAME,
                        TICKET_API_TOKEN.API_TOKEN,
                        PRODUCT.MEANS_OF_TRANSPORT_CATEGORY_CODE)
                .from(DEPLOYMENT_PRODUCT_TO_TRANSACTION_DATA)
                .join(PRODUCT).onKey()
                .join(TICKET_API_TOKEN).on(PRODUCT.PRODUCT_ID.equal(TICKET_API_TOKEN.PRODUCT_ID)
                        .and(PRODUCT.PV_ORG_ID.equal(TICKET_API_TOKEN.PV_ORG_ID)).and(TICKET_API_TOKEN.ACTIVE))
                .join(PARTNER).onKey()
                .join(TRANSACTION_DATA).onKey()
                .join(pv).on(PRODUCT.PV_ORG_ID.equal(pv.ORG_ID))
                .join(kvp).on(TICKET_API_TOKEN.KVP_ORG_ID.equal(kvp.ORG_ID))
                .join(transactionOp).on(TRANSACTION_DATA.TRANSACTION_OPERATOR_ID.equal(transactionOp.ORG_ID))
                .join(terminalOrg).on(TRANSACTION_DATA.TERMINAL_ORG_ID.equal(terminalOrg.ORG_ID))
                .join(locationOrg).on(TRANSACTION_DATA.LOCATION_ORG_ID.equal(locationOrg.ORG_ID))
                .where(DEPLOYMENT_PRODUCT_TO_TRANSACTION_DATA.DEPLOYMENT_ID.eq(deployment))
                .fetch()
                .map(r -> ProductConfiguration.builder()
                        .deploymentId(r.get(DEPLOYMENT_PRODUCT_TO_TRANSACTION_DATA.DEPLOYMENT_ID))
                        .productId(new ReferenceNumberTwo(r.get(DEPLOYMENT_PRODUCT_TO_TRANSACTION_DATA.PRODUCT_ID)))
                        .pv(new Organisation(r.get(pv.ORG_ID), r.get(pv.NAME)))
                        .kvp(new Organisation(r.get(kvp.ORG_ID), r.get(kvp.NAME)))
                        .transactionOp(new Organisation(r.get(transactionOp.ORG_ID), r.get(transactionOp.NAME)))
                        .terminalOrg(new Organisation(r.get(terminalOrg.ORG_ID), r.get(terminalOrg.NAME)))
                        .locationOrg(new Organisation(r.get(locationOrg.ORG_ID), r.get(locationOrg.NAME)))
                        .terminalNumber(new ReferenceNumberTwo(r.get(TRANSACTION_DATA.TERMINAL_NUMBER)))
                        .locationNumber(new ReferenceNumberThree(r.get(TRANSACTION_DATA.LOCATION_NUMBER)))
                        .partner(new Partner(r.get(PARTNER.PARTNER_ID), r.get(PARTNER.NAME)))
                        .apiToken(r.get(TICKET_API_TOKEN.API_TOKEN))
                        .meansOfTransportCategory(TransportmittelKategorieCode.of(new INT1(
                                r.get(PRODUCT.MEANS_OF_TRANSPORT_CATEGORY_CODE).shortValue())))
                        .build());
    }

    @Override
    public List<BetreiberKey> getBetreiberKeys() {
        return ctx.fetch(BETREIBER_KEY).stream().map(r -> {
            try {
                return new BetreiberKey(r.getChr(), r.getPrivateExponent(), r.getModulus());
            } catch (final Throwable e) {
                log.error("Betreiber Key could not be parsed!", e);
                return null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    //    @Async
    @Override
    public void insertLogLine(final LogLine logLine) {
        final TicketsCreatedRecord logRecord = ctx.newRecord(TICKETS_CREATED);
        logRecord.setLogtime(convertToUTC(logLine.getLogTime()));
        final STBParameters parameters = logLine.getParameters();
        logRecord.setBov(convertToUTC(parameters.getBerGueltigkeitsbeginn().getValue()));
        logRecord.setEov(convertToUTC(parameters.getBerGueltigkeitsende().getValue()));
        logRecord.setFreitext(parameters.getFreitext());
        final ProductConfiguration productConfiguration = logLine.getProductConfiguration();
        logRecord.setDeploymentId(productConfiguration.getDeploymentId());
        logRecord.setProductId(productConfiguration.getProductId().getValue().getValue());
        logRecord.setPvOrgId(productConfiguration.getPv().getOrgIdAsInt());
        logRecord.setKvpOrgId(productConfiguration.getKvp().getOrgIdAsInt());
        logRecord.setApiToken(productConfiguration.getApiToken());
        logRecord.setPartnerId(productConfiguration.getPartner().getId());
        logRecord.setPartnerName(productConfiguration.getPartner().getName());
        logRecord.setTransactionOpOrgId(productConfiguration.getTransactionOp().getOrgIdAsInt());
        logRecord.setTerminalOrgId(productConfiguration.getTerminalOrg().getOrgIdAsInt());
        logRecord.setTerminalNumber(productConfiguration.getTerminalNumber().getValue().getValue());
        logRecord.setLocationOrgId(productConfiguration.getLocationOrg().getOrgIdAsInt());
        logRecord.setLocationNumber(productConfiguration.getLocationNumber().getValue().getValue());
        logRecord.setMeansOfTransportCategoryCode(
                (int) productConfiguration.getMeansOfTransportCategory().getValue().getValue());
        logRecord.setTicketNumber(logLine.getTicketNumber());
        logRecord.setPvMkVersion(logLine.getPvMkVersion());
        logRecord.setSamId(logLine.getSamId());
        logRecord.setSignKeyChr(logLine.getSignKeyChr());
        logRecord.setTicket(logLine.getTicket());
        logRecord.insert();
    }

    @Override
    public void getLogLinesAsCSV(final String logApiToken, final YearMonth month, final Writer writer)
            throws PendingUpdateException {
        final Interval interval = month.toInterval(DateTimeZone.UTC);
        final Timestamp endTimestamp = new Timestamp(interval.getEndMillis());
        @Nullable
        final Record2<Timestamp, Integer> lastRecentUpdateAndDeployment = ctx
                .select(LAST_UPDATE.LOGTIME, LAST_UPDATE.DEPLOYMENT)
                .from(LAST_UPDATE)
                .orderBy(LAST_UPDATE.LOGTIME)
                .limit(1)
                .fetchOne();
        if (null == lastRecentUpdateAndDeployment) {
            throw new PendingUpdateException("The heartbeat table is empty!");
        }
        final Timestamp leastRecentUpdate = lastRecentUpdateAndDeployment.get(LAST_UPDATE.LOGTIME);
        final Integer deployment = lastRecentUpdateAndDeployment.get(LAST_UPDATE.DEPLOYMENT);
        if (leastRecentUpdate.before(endTimestamp)) {
            throw new PendingUpdateException("It can not be assured that all tickets would be presented, since the "
                    + "last confirmed update from deployment " + deployment + " was at " + leastRecentUpdate + " !");
        }
        final Timestamp startTimestamp = new Timestamp(interval.getStartMillis());
        final Record2<Integer, Boolean> possiblePvOrgId = ctx
                .select(LOG_API_TOKEN.PV_ORG_ID, LOG_API_TOKEN.ACTIVE)
                .from(LOG_API_TOKEN)
                .where(LOG_API_TOKEN.API_TOKEN.equal(logApiToken))
                .fetchOne();
        if (null == possiblePvOrgId) {
            throw new IllegalArgumentException("Unknown API Token!");
        }
        if (!possiblePvOrgId.get(LOG_API_TOKEN.ACTIVE)) {
            throw new IllegalArgumentException("Inactive API Token!");
        }
        final int pvOrgId = possiblePvOrgId.get(LOG_API_TOKEN.PV_ORG_ID);
        ctx
                .selectFrom(TICKETS_CREATED)
                .where(TICKETS_CREATED.LOGTIME.between(startTimestamp, endTimestamp))
                .and(TICKETS_CREATED.PV_ORG_ID.equal(pvOrgId))
                .fetch()
                .formatCSV(writer, true);
    }

    @Override
    public void sendHeartbeat(final int deployment) {
        final int linesAffected = ctx
                .update(LAST_UPDATE)
                .set(LAST_UPDATE.LOGTIME, convertToUTC(LocalDateTime.now(DateTimeZone.UTC)))
                .where(LAST_UPDATE.DEPLOYMENT.equal(deployment))
                .execute();
        if (1 != linesAffected) {
            throw new IllegalStateException("Heartbeat update changed " + linesAffected + " instead of exactly 1!");
        }
    }
}
