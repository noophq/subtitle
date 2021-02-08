package fr.noop.subtitle.stl;


public class LanguageCode {
    public enum Lc {
        UNKNOWN(0x3030),
        ALBANIAN(0x3031),
        BRETON(0x3032),
        CATALAN(0x3033),
        CROATIAN(0x3034),
        WELSH(0x3035),
        CZECH(0x3036),
        DANISH(0x3037),
        GERMAN(0x3038),
        ENGLISH(0x3039),
        SPANISH(0x3041),
        ESPERANTO(0x3042),
        ESTONIAN(0x3043),
        BASQUE(0x3044),
        FAROESE(0x3045),
        FRENCH(0x3046),
        FRISIAN(0x3130),
        IRISH(0x3131),
        GAELIC(0x3132),
        GALICIAN(0x3133),
        ICELANDIC(0x3134),
        ITALIAN(0x3135),
        LAPPISH(0x3136),
        LATIN(0x3137),
        LATVIAN(0x3138),
        LUXEMBOURGIAN(0x3139),
        LITHUANIAN(0x3141),
        HUNGARIAN(0x3142),
        MALTESE(0x3143),
        DUTCH(0x3144),
        NORWEGIAN(0x3145),
        OCCITAN(0x3146),
        POLISH(0x3230),
        PORTUGESE(0x3231),
        ROMANIAN(0x3232),
        ROMANSH(0x3233),
        SERBIAN(0x3234),
        SLOVAK(0x3235),
        SLOVENIAN(0x3236),
        FINNISH(0x3237),
        SWEDISH(0x3238),
        TURKISH(0x3239),
        FLEMISH(0x3241),
        WALLON(0x3242),
        AMHARIC(0x3746),
        ARABIC(0x3745),
        ARMENIAN(0x3744),
        ASSAMESE(0x3743),
        AZERBAIJANI(0x3742),
        BAMBORA(0x3741),
        BIELORUSSIAN(0x3739),
        BENGALI(0x3738),
        BULGARIAN(0x3737),
        BURMESE(0x3736),
        CHINESE(0x3735),
        CHURASH(0x3734),
        DARI(0x3733),
        FULANI(0x3732),
        GEORGIAN(0x3731),
        GREEK(0x3730),
        GUJURATI(0x3646),
        GURANI(0x3645),
        HAUSA(0x3644),
        HEBREW(0x3643),
        HINDI(0x3642),
        INDONESIAN(0x3641),
        JAPANESE(0x3639),
        KANNADA(0x3638),
        KAZAKH(0x3637),
        KHMER(0x3636),
        KOREAN(0x3635),
        LAOTIAN(0x3634),
        MACEDONIAN(0x3633),
        MALAGASAY(0x3632),
        MALAYSIAN(0x3631),
        MOLDAVIAN(0x3630),
        MARATHI(0x3546),
        NDEBELE(0x3545),
        NEPALI(0x3544),
        ORIYA(0x3543),
        PAPAMIENTO(0x3542),
        PERSIAN(0x3541),
        PUNJABI(0x3539),
        PUSHTU(0x3538),
        QUECHUA(0x3537),
        RUSSIAN(0x3536),
        RUTHENIAN(0x3535),
        SERBO_CROAT(0x3534),
        SHONA(0x3533),
        SINHALESE(0x3532),
        SOMALI(0x3531),
        SRANAN_TONGO(0x3530),
        SWAHILI(0x3446),
        TADZHIK(0x3445),
        TAMIL(0x3444),
        TATAR(0x3443),
        TELUGU(0x3442),
        THAI(0x3441),
        UKRAINIAN(0x3439),
        URDU(0x3438),
        UZBEK(0x3437),
        VIETNAMESE(0x3436),
        ZULU(0x3435),
        RESERVED(0x3246);

        private int value;

        Lc(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }

        public static Lc getEnum(int value) {
            switch (value) {
                case 0x3246:
                case 0x3330:
                case 0x3331:
                case 0x3332:
                case 0x3333:
                case 0x3334:
                case 0x3335:
                case 0x3336:
                case 0x3337:
                case 0x3338:
                case 0x3339:
                case 0x3341:
                case 0x3342:
                case 0x3343:
                case 0x3344:
                case 0x3345:
                case 0x3346:
                    return RESERVED;
            }
            for(Lc v : values())
                if(v.getValue() == value) return v;
            throw new IllegalArgumentException();
        }
    }
}
