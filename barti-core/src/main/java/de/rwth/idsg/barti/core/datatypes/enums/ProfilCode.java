package de.rwth.idsg.barti.core.datatypes.enums;

import de.rwth.idsg.barti.core.MyOutputStream;
import de.rwth.idsg.barti.core.datatypes.Data;
import de.rwth.idsg.barti.core.datatypes.ReadDescription;
import de.rwth.idsg.barti.core.datatypes.basic.INT1;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

/**
 * 6-56
 *
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@Getter
@RequiredArgsConstructor
public enum ProfilCode implements Data<ProfilCode> {
    NICHT_SPEZIFIZIERT_UNBESTIMMT(0, "nicht spezifiziert/unbestimmt"),
    ERWACHSENER(1, "Erwachsener"),
    KIND(2, "Kind"),
    STUDENT(3, "Student"),
    BEHINDERTER_NICHT_WEITER_SPEZIFIZIERT(5, "Behinderter nicht weiter spezifiziert"),
    SEHBEHINDERTER(6, "Sehbehinderter"),
    HOERGESCHAEDIGETER(7, "Hörgeschädigter"),
    ARBEITSLOSER_SOZIALHILFEEMPFAENGER(8, "Arbeitsloser/Sozialhilfeempfänger"),
    PERSONAL(9, "Personal"),
    MILITAERANGEHOERIGER(10, "Militärangehöriger"),
    SCHUELER(19, "Schüler"),
    AZUBI(20, "Azubi"),
    SENIOR(25, "Senior"),
    ERMAESSIGT(64, "ermäßigt"),
    FAHRRAD(65, "Fahrrad"),
    HUND(66, "Hund"),
    KA_SPEZIFISCH_67(67, "KA-spezifisch 67"),
    KA_SPEZIFISCH_68(68, "KA-spezifisch 68"),
    KA_SPEZIFISCH_69(69, "KA-spezifisch 69"),
    KA_SPEZIFISCH_70(70, "KA-spezifisch 70"),
    KA_SPEZIFISCH_71(71, "KA-spezifisch 71"),
    KA_SPEZIFISCH_72(72, "KA-spezifisch 72"),
    KA_SPEZIFISCH_73(73, "KA-spezifisch 73"),
    KA_SPEZIFISCH_74(74, "KA-spezifisch 74"),
    KA_SPEZIFISCH_75(75, "KA-spezifisch 75"),
    KA_SPEZIFISCH_76(76, "KA-spezifisch 76"),
    KA_SPEZIFISCH_77(77, "KA-spezifisch 77"),
    KA_SPEZIFISCH_78(78, "KA-spezifisch 78"),
    KA_SPEZIFISCH_79(79, "KA-spezifisch 79"),
    KA_SPEZIFISCH_80(80, "KA-spezifisch 80"),
    KA_SPEZIFISCH_81(81, "KA-spezifisch 81"),
    KA_SPEZIFISCH_82(82, "KA-spezifisch 82"),
    KA_SPEZIFISCH_83(83, "KA-spezifisch 83"),
    KA_SPEZIFISCH_84(84, "KA-spezifisch 84"),
    KA_SPEZIFISCH_85(85, "KA-spezifisch 85"),
    KA_SPEZIFISCH_86(86, "KA-spezifisch 86"),
    KA_SPEZIFISCH_87(87, "KA-spezifisch 87"),
    KA_SPEZIFISCH_88(88, "KA-spezifisch 88"),
    KA_SPEZIFISCH_89(89, "KA-spezifisch 89"),
    KA_SPEZIFISCH_90(90, "KA-spezifisch 90"),
    KA_SPEZIFISCH_91(91, "KA-spezifisch 91"),
    KA_SPEZIFISCH_92(92, "KA-spezifisch 92"),
    KA_SPEZIFISCH_93(93, "KA-spezifisch 93"),
    KA_SPEZIFISCH_94(94, "KA-spezifisch 94"),
    KA_SPEZIFISCH_95(95, "KA-spezifisch 95"),
    KA_SPEZIFISCH_96(96, "KA-spezifisch 96"),
    KA_SPEZIFISCH_97(97, "KA-spezifisch 97"),
    KA_SPEZIFISCH_98(98, "KA-spezifisch 98"),
    KA_SPEZIFISCH_99(99, "KA-spezifisch 99"),
    KA_SPEZIFISCH_100(100, "KA-spezifisch 100"),
    KA_SPEZIFISCH_101(101, "KA-spezifisch 101"),
    KA_SPEZIFISCH_102(102, "KA-spezifisch 102"),
    KA_SPEZIFISCH_103(103, "KA-spezifisch 103"),
    KA_SPEZIFISCH_104(104, "KA-spezifisch 104"),
    KA_SPEZIFISCH_105(105, "KA-spezifisch 105"),
    KA_SPEZIFISCH_106(106, "KA-spezifisch 106"),
    KA_SPEZIFISCH_107(107, "KA-spezifisch 107"),
    KA_SPEZIFISCH_108(108, "KA-spezifisch 108"),
    KA_SPEZIFISCH_109(109, "KA-spezifisch 109"),
    KA_SPEZIFISCH_110(110, "KA-spezifisch 110"),
    KA_SPEZIFISCH_111(111, "KA-spezifisch 111"),
    KA_SPEZIFISCH_112(112, "KA-spezifisch 112"),
    KA_SPEZIFISCH_113(113, "KA-spezifisch 113"),
    KA_SPEZIFISCH_114(114, "KA-spezifisch 114"),
    KA_SPEZIFISCH_115(115, "KA-spezifisch 115"),
    KA_SPEZIFISCH_116(116, "KA-spezifisch 116"),
    KA_SPEZIFISCH_117(117, "KA-spezifisch 117"),
    KA_SPEZIFISCH_118(118, "KA-spezifisch 118"),
    KA_SPEZIFISCH_119(119, "KA-spezifisch 119"),
    KA_SPEZIFISCH_120(120, "KA-spezifisch 120"),
    KA_SPEZIFISCH_121(121, "KA-spezifisch 121"),
    KA_SPEZIFISCH_122(122, "KA-spezifisch 122"),
    KA_SPEZIFISCH_123(123, "KA-spezifisch 123"),
    KA_SPEZIFISCH_124(124, "KA-spezifisch 124"),
    KA_SPEZIFISCH_125(125, "KA-spezifisch 125"),
    KA_SPEZIFISCH_126(126, "KA-spezifisch 126"),
    KA_SPEZIFISCH_127(127, "KA-spezifisch 127"),
    KA_SPEZIFISCH_128(128, "KA-spezifisch 128"),
    KA_SPEZIFISCH_129(129, "KA-spezifisch 129"),
    KA_SPEZIFISCH_130(130, "KA-spezifisch 130"),
    KA_SPEZIFISCH_131(131, "KA-spezifisch 131"),
    KA_SPEZIFISCH_132(132, "KA-spezifisch 132"),
    KA_SPEZIFISCH_133(133, "KA-spezifisch 133"),
    KA_SPEZIFISCH_134(134, "KA-spezifisch 134"),
    KA_SPEZIFISCH_135(135, "KA-spezifisch 135"),
    KA_SPEZIFISCH_136(136, "KA-spezifisch 136"),
    KA_SPEZIFISCH_137(137, "KA-spezifisch 137"),
    KA_SPEZIFISCH_138(138, "KA-spezifisch 138"),
    KA_SPEZIFISCH_139(139, "KA-spezifisch 139"),
    KA_SPEZIFISCH_140(140, "KA-spezifisch 140"),
    KA_SPEZIFISCH_141(141, "KA-spezifisch 141"),
    KA_SPEZIFISCH_142(142, "KA-spezifisch 142"),
    KA_SPEZIFISCH_143(143, "KA-spezifisch 143"),
    KA_SPEZIFISCH_144(144, "KA-spezifisch 144"),
    KA_SPEZIFISCH_145(145, "KA-spezifisch 145"),
    KA_SPEZIFISCH_146(146, "KA-spezifisch 146"),
    KA_SPEZIFISCH_147(147, "KA-spezifisch 147"),
    KA_SPEZIFISCH_148(148, "KA-spezifisch 148"),
    KA_SPEZIFISCH_149(149, "KA-spezifisch 149"),
    KA_SPEZIFISCH_150(150, "KA-spezifisch 150"),
    KA_SPEZIFISCH_151(151, "KA-spezifisch 151"),
    KA_SPEZIFISCH_152(152, "KA-spezifisch 152"),
    KA_SPEZIFISCH_153(153, "KA-spezifisch 153"),
    KA_SPEZIFISCH_154(154, "KA-spezifisch 154"),
    KA_SPEZIFISCH_155(155, "KA-spezifisch 155"),
    KA_SPEZIFISCH_156(156, "KA-spezifisch 156"),
    KA_SPEZIFISCH_157(157, "KA-spezifisch 157"),
    KA_SPEZIFISCH_158(158, "KA-spezifisch 158"),
    KA_SPEZIFISCH_159(159, "KA-spezifisch 159"),
    KA_SPEZIFISCH_160(160, "KA-spezifisch 160"),
    KA_SPEZIFISCH_161(161, "KA-spezifisch 161"),
    KA_SPEZIFISCH_162(162, "KA-spezifisch 162"),
    KA_SPEZIFISCH_163(163, "KA-spezifisch 163"),
    KA_SPEZIFISCH_164(164, "KA-spezifisch 164"),
    KA_SPEZIFISCH_165(165, "KA-spezifisch 165"),
    KA_SPEZIFISCH_166(166, "KA-spezifisch 166"),
    KA_SPEZIFISCH_167(167, "KA-spezifisch 167"),
    KA_SPEZIFISCH_168(168, "KA-spezifisch 168"),
    KA_SPEZIFISCH_169(169, "KA-spezifisch 169"),
    KA_SPEZIFISCH_170(170, "KA-spezifisch 170"),
    KA_SPEZIFISCH_171(171, "KA-spezifisch 171"),
    KA_SPEZIFISCH_172(172, "KA-spezifisch 172"),
    KA_SPEZIFISCH_173(173, "KA-spezifisch 173"),
    KA_SPEZIFISCH_174(174, "KA-spezifisch 174"),
    KA_SPEZIFISCH_175(175, "KA-spezifisch 175"),
    KA_SPEZIFISCH_176(176, "KA-spezifisch 176"),
    KA_SPEZIFISCH_177(177, "KA-spezifisch 177"),
    KA_SPEZIFISCH_178(178, "KA-spezifisch 178"),
    KA_SPEZIFISCH_179(179, "KA-spezifisch 179"),
    KA_SPEZIFISCH_180(180, "KA-spezifisch 180"),
    KA_SPEZIFISCH_181(181, "KA-spezifisch 181"),
    KA_SPEZIFISCH_182(182, "KA-spezifisch 182"),
    KA_SPEZIFISCH_183(183, "KA-spezifisch 183"),
    KA_SPEZIFISCH_184(184, "KA-spezifisch 184"),
    KA_SPEZIFISCH_185(185, "KA-spezifisch 185"),
    KA_SPEZIFISCH_186(186, "KA-spezifisch 186"),
    KA_SPEZIFISCH_187(187, "KA-spezifisch 187"),
    KA_SPEZIFISCH_188(188, "KA-spezifisch 188"),
    KA_SPEZIFISCH_189(189, "KA-spezifisch 189"),
    KA_SPEZIFISCH_190(190, "KA-spezifisch 190"),
    KA_SPEZIFISCH_191(191, "KA-spezifisch 191"),
    KA_SPEZIFISCH_192(192, "KA-spezifisch 192"),
    KA_SPEZIFISCH_193(193, "KA-spezifisch 193"),
    KA_SPEZIFISCH_194(194, "KA-spezifisch 194"),
    KA_SPEZIFISCH_195(195, "KA-spezifisch 195"),
    KA_SPEZIFISCH_196(196, "KA-spezifisch 196"),
    KA_SPEZIFISCH_197(197, "KA-spezifisch 197"),
    KA_SPEZIFISCH_198(198, "KA-spezifisch 198"),
    KA_SPEZIFISCH_199(199, "KA-spezifisch 199"),
    KA_SPEZIFISCH_200(200, "KA-spezifisch 200"),
    KA_SPEZIFISCH_201(201, "KA-spezifisch 201"),
    KA_SPEZIFISCH_202(202, "KA-spezifisch 202"),
    KA_SPEZIFISCH_203(203, "KA-spezifisch 203"),
    KA_SPEZIFISCH_204(204, "KA-spezifisch 204"),
    KA_SPEZIFISCH_205(205, "KA-spezifisch 205"),
    KA_SPEZIFISCH_206(206, "KA-spezifisch 206"),
    KA_SPEZIFISCH_207(207, "KA-spezifisch 207"),
    KA_SPEZIFISCH_208(208, "KA-spezifisch 208"),
    KA_SPEZIFISCH_209(209, "KA-spezifisch 209"),
    KA_SPEZIFISCH_210(210, "KA-spezifisch 210"),
    KA_SPEZIFISCH_211(211, "KA-spezifisch 211"),
    KA_SPEZIFISCH_212(212, "KA-spezifisch 212"),
    KA_SPEZIFISCH_213(213, "KA-spezifisch 213"),
    KA_SPEZIFISCH_214(214, "KA-spezifisch 214"),
    KA_SPEZIFISCH_215(215, "KA-spezifisch 215"),
    KA_SPEZIFISCH_216(216, "KA-spezifisch 216"),
    KA_SPEZIFISCH_217(217, "KA-spezifisch 217"),
    KA_SPEZIFISCH_218(218, "KA-spezifisch 218"),
    KA_SPEZIFISCH_219(219, "KA-spezifisch 219"),
    KA_SPEZIFISCH_220(220, "KA-spezifisch 220"),
    KA_SPEZIFISCH_221(221, "KA-spezifisch 221"),
    KA_SPEZIFISCH_222(222, "KA-spezifisch 222"),
    KA_SPEZIFISCH_223(223, "KA-spezifisch 223"),
    KA_SPEZIFISCH_224(224, "KA-spezifisch 224"),
    KA_SPEZIFISCH_225(225, "KA-spezifisch 225"),
    KA_SPEZIFISCH_226(226, "KA-spezifisch 226"),
    KA_SPEZIFISCH_227(227, "KA-spezifisch 227"),
    KA_SPEZIFISCH_228(228, "KA-spezifisch 228"),
    KA_SPEZIFISCH_229(229, "KA-spezifisch 229"),
    KA_SPEZIFISCH_230(230, "KA-spezifisch 230"),
    KA_SPEZIFISCH_231(231, "KA-spezifisch 231"),
    KA_SPEZIFISCH_232(232, "KA-spezifisch 232"),
    KA_SPEZIFISCH_233(233, "KA-spezifisch 233"),
    KA_SPEZIFISCH_234(234, "KA-spezifisch 234"),
    KA_SPEZIFISCH_235(235, "KA-spezifisch 235"),
    KA_SPEZIFISCH_236(236, "KA-spezifisch 236"),
    KA_SPEZIFISCH_237(237, "KA-spezifisch 237"),
    KA_SPEZIFISCH_238(238, "KA-spezifisch 238"),
    KA_SPEZIFISCH_239(239, "KA-spezifisch 239"),
    KA_SPEZIFISCH_240(240, "KA-spezifisch 240"),
    KA_SPEZIFISCH_241(241, "KA-spezifisch 241"),
    KA_SPEZIFISCH_242(242, "KA-spezifisch 242"),
    KA_SPEZIFISCH_243(243, "KA-spezifisch 243"),
    KA_SPEZIFISCH_244(244, "KA-spezifisch 244"),
    KA_SPEZIFISCH_245(245, "KA-spezifisch 245"),
    KA_SPEZIFISCH_246(246, "KA-spezifisch 246"),
    KA_SPEZIFISCH_247(247, "KA-spezifisch 247"),
    KA_SPEZIFISCH_248(248, "KA-spezifisch 248"),
    KA_SPEZIFISCH_249(249, "KA-spezifisch 249"),
    KA_SPEZIFISCH_250(250, "KA-spezifisch 250"),
    FREI_FUER_NICHT_INTEROPERABLE_VERWENDUNG_251(251, "frei für nicht interoperable Verwendung 251"),
    FREI_FUER_NICHT_INTEROPERABLE_VERWENDUNG_252(252, "frei für nicht interoperable Verwendung 252"),
    FREI_FUER_NICHT_INTEROPERABLE_VERWENDUNG_253(253, "frei für nicht interoperable Verwendung 253"),
    FREI_FUER_NICHT_INTEROPERABLE_VERWENDUNG_254(254, "frei für nicht interoperable Verwendung 254"),
    FREI_FUER_NICHT_INTEROPERABLE_VERWENDUNG_255(255, "frei für nicht interoperable Verwendung 255");

    final INT1 value;
    final String stringRepresentation;

    ProfilCode(final int value, final String stringRepresentation) {
        this(new INT1((short) value), stringRepresentation);
    }

    public static ProfilCode of(final INT1 value) {
        for (final ProfilCode terminalTypCode : values()) {
            if (terminalTypCode.value.equals(value)) {
                return terminalTypCode;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return stringRepresentation;
    }

    public static final ReadDescription<ProfilCode> READ_DESCRIPTION = stream -> of(INT1
            .READ_DESCRIPTION.read(stream));

    @Override
    public ReadDescription<ProfilCode> getReadDescription() {
        return READ_DESCRIPTION;
    }

    @Override
    public void write(final MyOutputStream stream) throws IOException {
        value.write(stream);
    }

    @Override
    public int getLength() {
        return value.getLength();
    }
}
