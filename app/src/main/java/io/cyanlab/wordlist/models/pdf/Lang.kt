package io.cyanlab.wordlist.models.pdf

enum class Lang {
    ENG{
        override val langName: String? = "English"
    }, RUS{

        override val langName: String? = "Russian"
    }, NUM{

        override val langName: String? = null
    }, UNDEFINED{

        override val langName: String? = null
    }, BRACE{

        override val langName: String? = null
    };

    abstract val langName: String?
}