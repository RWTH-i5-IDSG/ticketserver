package de.rwth.idsg.barti.core.datatypes.composite;

import com.google.common.collect.ImmutableList;
import de.rwth.idsg.barti.core.MyOutputStream;
import de.rwth.idsg.barti.core.datatypes.Data;
import de.rwth.idsg.barti.core.datatypes.ReadDescription;
import de.rwth.idsg.barti.core.datatypes.basic.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static java8.util.stream.StreamSupport.stream;

/**
 * STB Spec 3-1
 *
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@lombok.Data
@RequiredArgsConstructor
@Log4j2
public class STB implements Data<STB> {

    public static final OctetStringThree KENNUNG = new OctetStringThree("VDV".getBytes());
    // version 1.4.0
    public static final OctetStringTwo VERSION = new OctetStringTwo(new byte[]{(byte) 0x14, (byte) 0x00});

    public static final int MIN_INNER_LENGTH = 106;

    public static final ReadDescription<STB> READ_DESCRIPTION = stream -> {
        final BerechtigungID berBerechtigungID = BerechtigungID.READ_DESCRIPTION.read(stream);
        final EFMProduktID prodProduktID = EFMProduktID.READ_DESCRIPTION.read(stream);
        final DateTimeCompact berGueltigkeitsbeginn = DateTimeCompact.READ_DESCRIPTION.read(stream);
        final DateTimeCompact berGueltigkeitsende = DateTimeCompact.READ_DESCRIPTION.read(stream);
        final SeparateDatenBerechtigungEFSStatischerProduktspezifischerTeil statischerProduktspezifischerTeil =
                SeparateDatenBerechtigungEFSStatischerProduktspezifischerTeil.READ_DESCRIPTION.read(stream);
        final ReferenceNumberTwo logTransaktionsOperatorID = ReferenceNumberTwo.READ_DESCRIPTION.read(stream);
        final TerminalID logTerminalID = TerminalID.READ_DESCRIPTION.read(stream);
        final DateTimeCompact logTransaktionsZeitpunkt = DateTimeCompact.READ_DESCRIPTION.read(stream);
        final OrtID logTransaktionsOrtID = OrtID.READ_DESCRIPTION.read(stream);
        final TransaktionProduktspezifischerTeil transaktionProduktspezifischerTeil =
                TransaktionProduktspezifischerTeil.READ_DESCRIPTION.read(stream);
        final ReferenceNumberFour berProdLogSAMSeqNummer = ReferenceNumberFour.READ_DESCRIPTION.read(stream);
        final OctetStringOne versionKPV = OctetStringOne.READ_DESCRIPTION.read(stream);
        final SequenceNumberFour samSequenznummer = SequenceNumberFour.READ_DESCRIPTION.read(stream);
        final ReferenceNumberThree samIDSamNummer = ReferenceNumberThree.READ_DESCRIPTION.read(stream);
        final STB stb = new STB(berBerechtigungID, prodProduktID, berGueltigkeitsbeginn, berGueltigkeitsende,
                statischerProduktspezifischerTeil, logTransaktionsOperatorID, logTerminalID,
                logTransaktionsZeitpunkt, logTransaktionsOrtID, transaktionProduktspezifischerTeil,
                berProdLogSAMSeqNummer, versionKPV, samSequenznummer, samIDSamNummer);
        final int innerLength = stb.getInnerLength();
        final int fillBytes = MIN_INNER_LENGTH - innerLength;
        if (fillBytes > 0) {
            stream.readBytes(fillBytes);
        }
        KENNUNG.getReadDescription().read(stream);
        VERSION.getReadDescription().read(stream);
        return stb;
    };

    final BerechtigungID berBerechtigungID;
    final EFMProduktID prodProduktID;
    final DateTimeCompact berGueltigkeitsbeginn;
    final DateTimeCompact berGueltigkeitsende;
    final SeparateDatenBerechtigungEFSStatischerProduktspezifischerTeil statischerProduktspezifischerTeil;
    final ReferenceNumberTwo logTransaktionsOperatorID;
    final TerminalID logTerminalID;
    final DateTimeCompact logTransaktionsZeitpunkt;
    final OrtID logTransaktionsOrtID;
    final TransaktionProduktspezifischerTeil transaktionProduktspezifischerTeil;
    final ReferenceNumberFour berProdLogSAMSeqNummer;
    final OctetStringOne versionKPV;
    final SequenceNumberFour samSequenznummer;
    final ReferenceNumberThree samIDSamNummer;

    @Override
    public ReadDescription<STB> getReadDescription() {
        return READ_DESCRIPTION;
    }

    public ImmutableList<Data<?>> getElements() {
        return ImmutableList.of(
                berBerechtigungID,
                prodProduktID,
                berGueltigkeitsbeginn,
                berGueltigkeitsende,
                statischerProduktspezifischerTeil,
                logTransaktionsOperatorID,
                logTerminalID,
                logTransaktionsZeitpunkt,
                logTransaktionsOrtID,
                transaktionProduktspezifischerTeil,
                berProdLogSAMSeqNummer,
                versionKPV,
                samSequenznummer,
                samIDSamNummer);
    }

    @Override
    public void write(final MyOutputStream stream) throws IOException {
        for (final Data<?> data : getElements()) {
            data.write(stream);
        }
        final int innerLength = getInnerLength();
        final int fillBytes = MIN_INNER_LENGTH - innerLength;
        if (fillBytes > 0) {
            stream.writeBytes(new byte[fillBytes]);
        }
        KENNUNG.write(stream);
        VERSION.write(stream);
    }

    @Override
    public int getLength() {
        return 5 + Math.max(MIN_INNER_LENGTH, getInnerLength());
    }

    public int getInnerLength() {
        return stream(getElements()).mapToInt(Data::getLength).sum();
    }

    public byte[] encode2Stream() {
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream(this.getLength());
        final MyOutputStream myOutputStream = new MyOutputStream(outputStream);
        try {
            this.write(myOutputStream);
            myOutputStream.flush();
        } catch (final IOException e) {
            log.error(e);
        }
        return outputStream.toByteArray();
    }
}
