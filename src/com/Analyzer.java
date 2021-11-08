package com;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

//Анализатор цикла while в языке с++
public class Analyzer implements Serializable{
    public static final long serialVersionUID = 10000;
    private final int maxLogicOperators;
    private final int maxIdLength;
    private int logicOperatorsCheck;
    private int idLengthCheck;

    private Error error = Error.NO_ERROR;
    private final List<Character> constants = new ArrayList<>();
    private final List<Character> idNames = new ArrayList<>();
    private int pos;

    private static final char SPACE = ' ';
    private static final char RND_BRACKET_OPEN = '(';
    private static final char RND_BRACKET_CLOSE = ')';
    private static final char SQ_BRACKET_OPEN  = '[';
    private static final char SQ_BRACKET_CLOSE = ']';
    private static final char BRACE_OPEN = '{';
    private static final char BRACE_CLOSE = '}';
    private static final char UNDERLINE = '_';
    private static final char EQUALS = '=';
    private static final char EXCL_POINT = '!';
    private static final char LESS = '<';
    private static final char MORE = '>';
    private static final char DOT = '.';
    private static final char AND = '&';
    private static final char OR = '|';
    private static final char PLUS = '+';
    private static final char MINUS = '-';
    private static final char MULTIPLICATION = '*';
    private static final char SLASH = '/';
    private static final char PERCENT = '%';
    private static final char SEMICOLON = ';';

    private static final String FOR_WORD = "for";
    private static final String WHILE_WORD = "while";
    private static final String BREAK_WORD = "break";
    private static final String SWITCH_WORD = "switch";
    private static final String CASE_WORD = "case";
    private static final String CONST_WORD = "const";

    public Analyzer(final int maxLogicOperators,final int maxIdLength) {
        this.maxLogicOperators = maxLogicOperators;
        this.maxIdLength = maxIdLength;
    }

    enum Error {
        NO_ERROR,                                       //отсутствие ошибки
        LETTER_KEY_EXPECTED,                            //ожидалась буква ключевого слова
        PARENTHESES_EXPECTED,                           //ожидалась круглая скобки
        SQUARE_BRACKET_EXPECTED,                        //ожидалась квадратная скобки
        SQUARE_BRACKET_OR_EQUALS_EXPECTED,              //ожидалась квадратная скобка или равно
        TERM_CHAR_EXPECTED,                             //ожидался символ терминала: -|0|..|9|_|a|..|z
        AFTER_TERM_CHAR_EXPECTED,                       //ожидался символ после терминала: =|!|<|>|&|||)
        DIGIT_EXPECTED,                                 //ожидалась цифра: 0|..|9
        EQUALS_EXPECTED,                                //ожидался символ: =
        TERM_CHAR_OR_EQUALS_EXPECTED,                   //ожидался символ терминала или равно
        TERM_CHAR_OR_LOGIC_OR_PARENTHESES_EXPECTED,     //ожидались круглая скобка, логический или терминальный символ
        DIGIT_OR_LOGIC_OR_PARENTHESES_EXPECTED,         //ожидались круглая скобка, логический символ или цифра
        LOGIC_OR_BRACKET_EXPECTED,                      //ожидались круглая скобка или логический символ
        LOGIC_EXPECTED,                                 //ожидался логический символ
        BRACES_EXPECTED,                                //ожидался символ фигурной скобки: {|}
        MATH_OR_END_EXPECTED,                           //ожидался математический символ или символ точки с запятой
        LETTER_EXPECTED,                                //ожидалась буква
        EXCESS_MAX_LOGIC_OPERATORS,                     //превышено число логических операций в условии цикла
        EXCESS_MAX_ID_LENGTH,                           //превышена максимальная длина идентификатора
        DISCOVERED_FAKE_KEY_WORD,                       //идентификатор является ключевым словом
        SUPERIOR_INT;                                   //целое число не вошло в диапазон -32768 – 32767

        public String message() {
            String result;
            switch (this) {
                case LETTER_KEY_EXPECTED ->
                        result = "Ожидалась буква ключевого слова";
                case PARENTHESES_EXPECTED ->
                        result = "Ожидалась круглая скобки";
                case SQUARE_BRACKET_EXPECTED ->
                        result = "Ожидалась квадратная скобки";
                case SQUARE_BRACKET_OR_EQUALS_EXPECTED ->
                        result = "Ожидалась квадратная скобка или равно";
                case TERM_CHAR_EXPECTED ->
                        result = "Ожидался символ терминала: -,0,...,9,_,a,...,z";
                case AFTER_TERM_CHAR_EXPECTED ->
                        result = "Ожидался символ после терминала: =,!,<,>,&,|,)";
                case DIGIT_EXPECTED ->
                        result = "Ожидалась цифра: 0,...,9";
                case EQUALS_EXPECTED ->
                        result = "Ожидался символ: =";
                case TERM_CHAR_OR_EQUALS_EXPECTED ->
                        result = "Ожидался символ терминала или равно";
                case TERM_CHAR_OR_LOGIC_OR_PARENTHESES_EXPECTED ->
                        result = "Ожидались круглая скобка, логический или терминальный символ";
                case DIGIT_OR_LOGIC_OR_PARENTHESES_EXPECTED ->
                        result = "Ожидались круглая скобка, логический символ или цифра";
                case LOGIC_OR_BRACKET_EXPECTED ->
                        result = "Ожидались круглая скобка или логический символ";
                case LOGIC_EXPECTED ->
                        result = "Ожидался логический символ";
                case BRACES_EXPECTED ->
                        result = "Ожидался символ фигурной скобки: {,}";
                case MATH_OR_END_EXPECTED ->
                        result = "Ожидался символ математической операции или символ точки с запятой";
                case LETTER_EXPECTED ->
                        result = "Ожидалась буква";
                case EXCESS_MAX_LOGIC_OPERATORS ->
                        result = "Превышено число логических операций";
                case EXCESS_MAX_ID_LENGTH ->
                        result = "Превышена максимальная длина идентификатора";
                case DISCOVERED_FAKE_KEY_WORD ->
                        result = "Идентификатор является ключевым словом";
                case SUPERIOR_INT ->
                        result = "Целое число не вошло в диапазон -32768 – 32767";
                case NO_ERROR ->
                        result = "Ошибок нет";
                default ->
                        result = "Непредвиденная ошибка";
            }
            return result;
        }
    }
    enum State {
        S1,  S2,  S3,  S4,  S5,  S6,  S7,  S8,  S9,  S10,
        S11, S12, S13, S14, S15, S16, S17, S18, S19, S20,
        S21, S22, S23, S24, S25, S26, S27, S28, S29, S30,
        S31, S32, S33, S34, S35, S36, S37, S38, S39, S40,
        S41, S42, S43, S44, S45, S46, S47, S48, S49, S50,
        S51, S52, S53, S54, S55, S56, S57, S58, S59, S60,
        S61, S62, S63, S64, S65, S66, S67, S68, S69, S70,
        START, ERROR, FINAL
    }

    public void analyze(final String input) {
        if (input.isEmpty()) {return;}
        State state = State.START;
        final char[] str = input.toCharArray();

        for (; state != State.ERROR && state != State.FINAL; pos++) {
            char chr;
            if (pos == str.length) {chr = '\0';}
            else {chr = str[pos];}
            switch (state) {
                case START -> {
                    if (chr == 'w' || chr == 'W') {state = State.S1;}
                    else if (chr != SPACE) {error = Error.LETTER_KEY_EXPECTED;}
                }

                case S1 -> {
                    if (chr == 'h' || chr == 'H') {state = State.S2;}
                    else {error = Error.LETTER_KEY_EXPECTED;}
                }

                case S2 -> {
                    if (chr == 'i' || chr == 'I') {state = State.S3;}
                    else {error = Error.LETTER_KEY_EXPECTED;}
                }

                case S3 -> {
                    if (chr == 'l' || chr == 'L') {state = State.S4;}
                    else {error = Error.LETTER_KEY_EXPECTED;}
                }

                case S4 -> {
                    if (chr == 'e' || chr == 'E') {state = State.S5;}
                    else {error = Error.LETTER_KEY_EXPECTED;}
                }

                case S5 -> {
                    if (chr == RND_BRACKET_OPEN) {state = State.S6;}
                    else if (chr != SPACE) {error = Error.PARENTHESES_EXPECTED;}
                }

                case S6 -> {
                    if (Character.isLetter(chr) || chr == UNDERLINE) {
                        if (idLengthCheck < maxIdLength) {idLengthCheck++;}
                        else {error = Error.EXCESS_MAX_ID_LENGTH;}
                        idNames.add(chr);
                        state = State.S7;
                    }
                    else if (chr == MINUS) {
                        constants.add(chr);
                        state = State.S8;
                    }
                    else if (Character.isDigit(chr)) {
                        constants.add(chr);
                        state = State.S9;
                    }
                    else if (chr != SPACE) {error = Error.TERM_CHAR_EXPECTED;}
                }

                case S7 -> {
                    if (chr == SPACE) {
                        idNames.add(SPACE);
                        idLengthCheck = 0;
                        state = State.S10;
                    }
                    else if (chr == SQ_BRACKET_OPEN) {
                        idNames.add(SPACE);
                        idLengthCheck = 0;
                        state = State.S11;
                    }
                    else if (isNotCharOrDigit(chr)) {
                        idNames.add(SPACE);
                        idLengthCheck = 0;
                        state = getAfterTermState(chr);
                    } else {
                        if (idLengthCheck < maxIdLength) {idLengthCheck++;}
                        else {error = Error.EXCESS_MAX_ID_LENGTH;}
                        idNames.add(chr);
                    }
                }

                case S8 -> {
                    if (Character.isDigit(chr)) {
                        constants.add(chr);
                        state = State.S9;
                    } else {error = Error.DIGIT_EXPECTED;}
                }

                case S9 -> {
                    if (chr == SPACE) {
                        constants.add(SPACE);
                        state = State.S17;
                    }
                    else if (chr == DOT) {
                        constants.add(chr);
                        state = State.S18;
                    }
                    else if (Character.isDigit(chr)) {
                        constants.add(chr);
                    } else {
                        constants.add(SPACE);
                        state = getAfterTermState(chr);
                    }

                }

                case S10 -> {
                    if (chr == SQ_BRACKET_OPEN) {state = State.S11;}
                    else if (chr != SPACE) {
                        state = getAfterTermState(chr);
                    }
                }

                case S11 -> {
                    if (Character.isLetter(chr) || chr == UNDERLINE) {
                        if (idLengthCheck < maxIdLength) {idLengthCheck++;}
                        else {error = Error.EXCESS_MAX_ID_LENGTH;}
                        idNames.add(chr);
                        state = State.S12;
                    }
                    else if (chr == MINUS) {
                        constants.add(chr);
                        state = State.S13;
                    }
                    else if (Character.isDigit(chr)) {
                        constants.add(chr);
                        state = State.S14;
                    }
                    else if (chr != SPACE) {error = Error.TERM_CHAR_EXPECTED;}
                }

                case S12 -> {
                    if (chr == SPACE) {
                        idNames.add(SPACE);
                        state = State.S15;
                    }
                    else if (chr == SQ_BRACKET_CLOSE) {
                        idNames.add(SPACE);
                        state = State.S16;
                    }
                    else if (isNotCharOrDigit(chr)) {
                        idNames.add(SPACE);
                        error = Error.SQUARE_BRACKET_EXPECTED;
                    } else {
                        if (idLengthCheck < maxIdLength) {idLengthCheck++;}
                        else {error = Error.EXCESS_MAX_ID_LENGTH;}
                        idNames.add(chr);
                    }
                }

                case S13 -> {
                    if (Character.isDigit(chr)) {
                        constants.add(chr);
                        state = State.S14;
                    }
                    else {error = Error.DIGIT_EXPECTED;}
                }

                case S14 -> {
                    if (chr == SPACE) {
                        constants.add(SPACE);
                        state = State.S15;
                    }
                    else if (chr == SQ_BRACKET_CLOSE) {
                        constants.add(SPACE);
                        state = State.S16;
                    }
                    else if (Character.isDigit(chr)) {constants.add(chr);}
                    else {
                        constants.add(SPACE);
                        error = Error.SQUARE_BRACKET_EXPECTED;
                    }
                }

                case S15 -> {
                    if (chr == SQ_BRACKET_CLOSE) {state = State.S16;}
                    else if (chr != SPACE) {error = Error.SQUARE_BRACKET_EXPECTED;}
                }

                case S16 -> {
                    if (chr != SPACE) {
                        state = getAfterTermState(chr);
                    }
                }

                case S17 -> state = getAfterTermState(chr);

                case S18 -> {
                    if (Character.isDigit(chr)) {
                        constants.add(chr);
                        state = State.S19;
                    }
                    else {error = Error.DIGIT_EXPECTED;}
                }

                case S19 -> {
                    if (chr == SPACE) {
                        constants.add(SPACE);
                        state = State.S17;
                    }
                    else if (chr == 'e' || chr == 'E') {
                        constants.add(chr);
                        state = State.S20;
                    }
                    else if (Character.isDigit(chr)) {constants.add(chr);}
                    else {
                        constants.add(SPACE);
                        state = getAfterTermState(chr);
                    }
                }

                case S20 -> {
                    if (chr == MINUS) {
                        constants.add(chr);
                        state = State.S21;
                    }
                    else if (Character.isDigit(chr)) {
                        constants.add(chr);
                        state = State.S22;
                    }
                    else {error = Error.DIGIT_EXPECTED;}
                }

                case S21 -> {
                    if (Character.isDigit(chr)) {
                        constants.add(chr);
                        state = State.S22;
                    }
                    else {error = Error.DIGIT_EXPECTED;}
                }

                case S22 -> {
                    if (chr == SPACE) {
                        constants.add(SPACE);
                        state = State.S17;
                    }
                    else if (Character.isDigit(chr)) {constants.add(chr);}
                    else {
                        constants.add(SPACE);
                        state = getAfterTermState(chr);
                    }
                }

                case S23 -> {
                    if (chr == EQUALS) {state = State.S25;}
                    else {error = Error.EQUALS_EXPECTED;}
                }

                case S24 -> {
                    if (chr == EQUALS || chr == SPACE) {state = State.S25;}
                    else if (Character.isLetter(chr) || chr == UNDERLINE) {
                        if (idLengthCheck < maxIdLength) {idLengthCheck++;}
                        else {error = Error.EXCESS_MAX_ID_LENGTH;}
                        idNames.add(chr);
                        state = State.S26;
                    }
                    else if (chr == MINUS) {
                        constants.add(chr);
                        state = State.S27;
                    }
                    else if (Character.isDigit(chr)) {
                        constants.add(chr);
                        state = State.S28;
                    }
                    else {error = Error.TERM_CHAR_OR_EQUALS_EXPECTED;}
                }

                case S25 -> {
                    if (Character.isLetter(chr) || chr == UNDERLINE) {
                        if (idLengthCheck < maxIdLength) {idLengthCheck++;}
                        else {error = Error.EXCESS_MAX_ID_LENGTH;}
                        idNames.add(chr);
                        state = State.S26;
                    }
                    else if (chr == MINUS) {
                        constants.add(chr);
                        state = State.S27;
                    }
                    else if (Character.isDigit(chr)) {
                        constants.add(chr);
                        state = State.S28;
                    }
                    else if (chr != SPACE) {error = Error.TERM_CHAR_EXPECTED;}
                }

                case S26 -> {
                    if (chr == SPACE) {
                        idNames.add(SPACE);
                        idLengthCheck = 0;
                        state = State.S29;
                    }
                    else if (chr == SQ_BRACKET_OPEN) {
                        idNames.add(SPACE);
                        idLengthCheck = 0;
                        state = State.S30;
                    }
                    else if (chr == AND) {
                        if (logicOperatorsCheck < maxLogicOperators) {logicOperatorsCheck++;}
                        else {error = Error.EXCESS_MAX_LOGIC_OPERATORS;}
                        idNames.add(SPACE);
                        idLengthCheck = 0;
                        state = State.S41;
                    }
                    else if (chr == OR) {
                        if (logicOperatorsCheck < maxLogicOperators) {logicOperatorsCheck++;}
                        else {error = Error.EXCESS_MAX_LOGIC_OPERATORS;}
                        idNames.add(SPACE);
                        idLengthCheck = 0;
                        state = State.S42;
                    }
                    else if (chr == RND_BRACKET_CLOSE) {
                        idNames.add(SPACE);
                        idLengthCheck = 0;
                        state = State.S43;
                    }
                    else if (isNotCharOrDigit(chr)) {
                        idNames.add(SPACE);
                        idLengthCheck = 0;
                        error = Error.TERM_CHAR_OR_LOGIC_OR_PARENTHESES_EXPECTED;
                    }
                    else {
                        if (idLengthCheck < maxIdLength) {idLengthCheck++;}
                        else {error = Error.EXCESS_MAX_ID_LENGTH;}
                        idNames.add(chr);
                    }
                }

                case S27 -> {
                    if (Character.isDigit(chr)) {
                        constants.add(chr);
                        state = State.S28;
                    }
                    else {error = Error.DIGIT_EXPECTED;}
                }

                case S28 -> {
                    if (chr == SPACE) {
                        constants.add(SPACE);
                        state = State.S35;
                    }
                    else if (chr == DOT) {
                        constants.add(chr);
                        state = State.S36;
                    } else {
                        state = getDigitLogicParentheses(chr, constants, state);
                        if (state == State.ERROR) {error = Error.DIGIT_OR_LOGIC_OR_PARENTHESES_EXPECTED;}
                    }
                }

                case S29 -> {
                    if (chr == SQ_BRACKET_OPEN) {state = State.S30;}
                    else {state = getLogicOrParentheses(chr, state);}
                }

                case S30 -> {
                    if (Character.isLetter(chr) || chr == UNDERLINE) {
                        if (idLengthCheck < maxIdLength) {idLengthCheck++;}
                        else {error = Error.EXCESS_MAX_ID_LENGTH;}
                        idNames.add(chr);
                        state = State.S31;
                    }
                    else if (chr == MINUS) {
                        constants.add(chr);
                        state = State.S32;
                    }
                    else if (Character.isDigit(chr)) {
                        constants.add(chr);
                        state = State.S33;
                    }
                    else if (chr != SPACE) {error = Error.TERM_CHAR_EXPECTED;}
                }

                case S31 -> {
                    if (chr == SPACE) {
                        idNames.add(SPACE);
                        idLengthCheck = 0;
                        state = State.S34;
                    }
                    else if (chr == SQ_BRACKET_CLOSE) {
                        idNames.add(SPACE);
                        idLengthCheck = 0;
                        state = State.S35;
                    }
                    else {checkCharOrDigitForId(chr);}
                }

                case S32 -> {
                    if (Character.isDigit(chr)) {
                        constants.add(chr);
                        state = State.S33;
                    }
                    else {error = Error.DIGIT_EXPECTED;}
                }

                case S33 -> {
                    if (chr == SPACE) {
                        constants.add(SPACE);
                        state = State.S34;
                    }
                    else if (chr == SQ_BRACKET_CLOSE) {
                        constants.add(SPACE);
                        state = State.S35;
                    }
                    else if (Character.isDigit(chr)) {constants.add(chr);}
                    else {
                        constants.add(SPACE);
                        error = Error.SQUARE_BRACKET_EXPECTED;
                    }
                }

                case S34 -> {
                    if (chr == SQ_BRACKET_CLOSE) {state = State.S35;}
                    else if (chr != SPACE) {error = Error.SQUARE_BRACKET_EXPECTED;}
                }

                case S35 -> state = getLogicOrParentheses(chr, state);

                case S36 -> {
                    if (Character.isDigit(chr)) {
                        constants.add(chr);
                        state = State.S37;
                    }
                    else {error = Error.DIGIT_EXPECTED;}
                }

                case S37 -> {
                    if (chr == SPACE) {
                        constants.add(SPACE);
                        state = State.S35;
                    }
                    else if (chr == 'e' || chr == 'E') {
                        constants.add(chr);
                        state = State.S38;
                    } else {
                        state = getDigitLogicParentheses(chr, constants, state);
                        if (state == State.ERROR) {error = Error.DIGIT_OR_LOGIC_OR_PARENTHESES_EXPECTED;}
                    }
                }

                case S38 -> {
                    if (chr == MINUS) {
                        constants.add(chr);
                        state = State.S39;
                    }
                    else if (Character.isDigit(chr)) {
                        constants.add(chr);
                        state = State.S40;
                    }
                    else {error = Error.DIGIT_EXPECTED;}
                }

                case S39 -> {
                    if (Character.isDigit(chr)) {
                        constants.add(chr);
                        state = State.S40;
                    }
                    else {error = Error.DIGIT_EXPECTED;}
                }

                case S40 -> {
                    if (chr == SPACE) {
                        constants.add(SPACE);
                        state = State.S35;
                    } else {
                        state = getDigitLogicParentheses(chr, constants, state);
                        if (state == State.ERROR) {error = Error.DIGIT_OR_LOGIC_OR_PARENTHESES_EXPECTED;}
                    }
                }

                case S41 -> {
                    if (chr == AND) {state = State.S6;}
                    else {error = Error.LOGIC_EXPECTED;}
                }

                case S42 -> {
                    if (chr == OR) {state = State.S6;}
                    else {error = Error.LOGIC_EXPECTED;}
                }

                case S43 -> {
                    if (chr == BRACE_OPEN) {state = State.S44;}
                    else if (chr != SPACE) {error = Error.BRACES_EXPECTED;}
                }

                case S44 -> {
                    if (Character.isLetter(chr) || chr == UNDERLINE) {
                        if (idLengthCheck < maxIdLength) {idLengthCheck++;}
                        else {error = Error.EXCESS_MAX_ID_LENGTH;}
                        idNames.add(chr);
                        state = State.S45;
                    }
                    else if (chr != SPACE) {error = Error.LETTER_KEY_EXPECTED;}
                }

                case S45 -> {
                    if (chr == SPACE) {
                        idNames.add(SPACE);
                        idLengthCheck = 0;
                        state = State.S46;
                    }
                    else if (chr == SQ_BRACKET_OPEN) {
                        idNames.add(SPACE);
                        idLengthCheck = 0;
                        state = State.S47;
                    }
                    else if (chr == EQUALS) {
                        idNames.add(SPACE);
                        idLengthCheck = 0;
                        state = State.S53;
                    }
                    else if (isNotCharOrDigit(chr)) {
                        idNames.add(SPACE);
                        idLengthCheck = 0;
                        error = Error.TERM_CHAR_OR_EQUALS_EXPECTED;
                    }
                    else {
                        if (idLengthCheck < maxIdLength) {idLengthCheck++;}
                        else {error = Error.EXCESS_MAX_ID_LENGTH;}
                        idNames.add(chr);
                    }
                }

                case S46 -> {
                    if (chr == SQ_BRACKET_OPEN) {state = State.S47;}
                    else if (chr == EQUALS) {state = State.S53;}
                    else if (chr != SPACE) {error = Error.SQUARE_BRACKET_OR_EQUALS_EXPECTED;}
                }

                case S47 -> {
                    if (Character.isLetter(chr) || chr == UNDERLINE) {
                        if (idLengthCheck < maxIdLength) {idLengthCheck++;}
                        else {error = Error.EXCESS_MAX_ID_LENGTH;}
                        idNames.add(chr);
                        state = State.S48;
                    }
                    else if (chr == MINUS) {
                        constants.add(chr);
                        state = State.S49;
                    }
                    else if (Character.isDigit(chr)) {
                        constants.add(chr);
                        state = State.S50;
                    }
                    else if (chr != SPACE) {error = Error.TERM_CHAR_EXPECTED;}
                }

                case S48 -> {
                    if (chr == SPACE) {
                        idNames.add(SPACE);
                        idLengthCheck = 0;
                        state = State.S51;
                    }
                    else if (chr == SQ_BRACKET_CLOSE) {
                        idNames.add(SPACE);
                        idLengthCheck = 0;
                        state = State.S52;
                    }
                    else {checkCharOrDigitForId(chr);}
                }

                case S49 -> {
                    if (Character.isDigit(chr)) {
                        constants.add(chr);
                        state = State.S50;
                    }
                    else {error = Error.DIGIT_EXPECTED;}
                }

                case S50 -> {
                    if (chr == SPACE) {
                        constants.add(SPACE);
                        state = State.S51;
                    }
                    else if (chr == SQ_BRACKET_CLOSE) {
                        constants.add(SPACE);
                        state = State.S52;
                    }
                    else if (Character.isDigit(chr)) {constants.add(chr);}
                    else {
                        constants.add(SPACE);
                        error = Error.SQUARE_BRACKET_EXPECTED;
                    }
                }

                case S51 -> {
                    if (chr == SQ_BRACKET_CLOSE) {state = State.S52;}
                    else if (chr != SPACE) {error = Error.SQUARE_BRACKET_EXPECTED;}
                }

                case S52 -> {
                    if (chr == EQUALS) {state = State.S53;}
                    else if (chr != SPACE) {error = Error.EQUALS_EXPECTED;}
                }

                case S53 -> {
                    if (Character.isLetter(chr) || chr == UNDERLINE) {
                        if (idLengthCheck < maxIdLength) {idLengthCheck++;}
                        else {error = Error.EXCESS_MAX_ID_LENGTH;}
                        idNames.add(chr);
                        state = State.S54;
                    }
                    else if (chr == MINUS) {
                        constants.add(chr);
                        state = State.S55;
                    }
                    else if (Character.isDigit(chr)) {
                        constants.add(chr);
                        state = State.S56;
                    }
                    else if (chr != SPACE) {error = Error.TERM_CHAR_EXPECTED;}
                }

                case S54 -> {
                    if (chr == SPACE) {
                        idNames.add(SPACE);
                        idLengthCheck = 0;
                        state = State.S57;
                    }
                    else if (chr == SQ_BRACKET_OPEN) {
                        idNames.add(SPACE);
                        idLengthCheck = 0;
                        state = State.S58;
                    }
                    else if (chr == PLUS || chr == MINUS || chr == MULTIPLICATION || chr == SLASH || chr == PERCENT) {
                        idNames.add(SPACE);
                        idLengthCheck = 0;
                        state = State.S53;
                    }
                    else if (chr == SEMICOLON) {
                        idNames.add(SPACE);
                        idLengthCheck = 0;
                        state = State.S70;
                    }
                    else if (isNotCharOrDigit(chr)) {
                        idNames.add(SPACE);
                        idLengthCheck = 0;
                        error = Error.MATH_OR_END_EXPECTED;
                    }
                    else {
                        if (idLengthCheck < maxIdLength) {idLengthCheck++;}
                        else {error = Error.EXCESS_MAX_ID_LENGTH;}
                        idNames.add(chr);
                    }
                }

                case S55 -> {
                    if (Character.isDigit(chr)) {
                        constants.add(chr);
                        state = State.S56;
                    }
                    else {error = Error.DIGIT_EXPECTED;}
                }

                case S56 -> {
                    if (chr == SPACE) {
                        constants.add(SPACE);
                        state = State.S64;
                    }
                    else if (chr == DOT) {
                        constants.add(chr);
                        state = State.S65;
                    } else {
                        state = getMathOrEndOrDigit(chr, constants, state);
                        if (state == State.ERROR) {error = Error.MATH_OR_END_EXPECTED;}
                    }
                }

                ///
                case S57 -> {
                    if (chr == SQ_BRACKET_OPEN) {state = State.S58;}
                    else if (chr == PLUS || chr == MINUS || chr == MULTIPLICATION || chr == SLASH || chr == PERCENT) {
                        state = State.S53;
                    }
                    else if (chr == SEMICOLON) {state = State.S70;}
                    else if (chr != SPACE) {error = Error.MATH_OR_END_EXPECTED;}
                }

                case S58 -> {
                    if (Character.isLetter(chr) || chr == UNDERLINE) {
                        if (idLengthCheck < maxIdLength) {idLengthCheck++;}
                        else {error = Error.EXCESS_MAX_ID_LENGTH;}
                        idNames.add(chr);
                        state = State.S59;
                    }
                    else if (chr == MINUS) {
                        constants.add(chr);
                        state = State.S60;
                    }
                    else if (Character.isDigit(chr)) {
                        constants.add(chr);
                        state = State.S56;
                    }
                    else if (chr != SPACE) {error = Error.TERM_CHAR_EXPECTED;}
                }

                case S59 -> {
                    if (chr == UNDERLINE) {
                        idNames.add(SPACE);
                        idLengthCheck = 0;
                        state = State.S62;
                    }
                    else if (chr == SQ_BRACKET_CLOSE) {
                        idNames.add(SPACE);
                        idLengthCheck = 0;
                        state = State.S63;
                    }
                    else {checkCharOrDigitForId(chr);}
                }

                case S60 -> {
                    if (Character.isDigit(chr)) {
                        constants.add(chr);
                        state = State.S61;
                    }
                    else {error = Error.DIGIT_EXPECTED;}
                }

                case S61 -> {
                    if (chr == UNDERLINE) {
                        constants.add(SPACE);
                        state = State.S62;
                    }
                    else if (chr == SQ_BRACKET_CLOSE) {
                        constants.add(SPACE);
                        state = State.S63;
                    }
                    else if (Character.isDigit(chr)) {constants.add(chr);}
                    else {
                        constants.add(SPACE);
                        error = Error.SQUARE_BRACKET_EXPECTED;
                    }
                }

                case S62 -> {
                    if (chr == SQ_BRACKET_CLOSE) {state = State.S63;}
                    else if (chr != UNDERLINE) {error = Error.SQUARE_BRACKET_EXPECTED;}
                }

                case S63 -> state = getMathOrEnd(chr, state);

                case S64 -> {
                    if (chr == PLUS || chr == MINUS || chr == MULTIPLICATION || chr == SLASH || chr == PERCENT) {
                        state = State.S53;
                    }
                    else if (chr == SEMICOLON) {state = State.S70;}
                    else {error = Error.MATH_OR_END_EXPECTED;}
                }

                case S65 -> {
                    if (Character.isDigit(chr)) {
                        constants.add(chr);
                        state = State.S66;
                    }
                    else {error = Error.DIGIT_EXPECTED;}
                }

                case S66 -> {
                    if (chr == SPACE) {
                        constants.add(SPACE);
                        state = State.S64;
                    }
                    else if (chr == 'e' || chr == 'E') {
                        constants.add(chr);
                        state = State.S67;
                    } else {
                        state = getMathOrEndOrDigit(chr, constants, state);
                        if (state == State.ERROR) {error = Error.MATH_OR_END_EXPECTED;}
                    }
                }

                case S67 -> {
                    if (chr == MINUS) {
                        constants.add(chr);
                        state = State.S68;
                    }
                    else if (Character.isDigit(chr)) {
                        constants.add(chr);
                        state = State.S69;
                    }
                    else {error = Error.DIGIT_EXPECTED;}
                }

                case S68 -> {
                    if (Character.isDigit(chr)) {
                        constants.add(chr);
                        state = State.S69;
                    }
                    else {error = Error.DIGIT_EXPECTED;}
                }

                case S69 -> {
                    if (chr == SPACE) {
                        constants.add(SPACE);
                        state = State.S64;
                    } else {
                        state = getMathOrEndOrDigit(chr, constants, state);
                        if (state == State.ERROR) {error = Error.MATH_OR_END_EXPECTED;}
                    }
                }

                case S70 -> {
                    if (Character.isLetter(chr) || chr == UNDERLINE) {
                        if (idLengthCheck < maxIdLength) {idLengthCheck++;}
                        else {error = Error.EXCESS_MAX_ID_LENGTH;}
                        idNames.add(chr);
                        state = State.S45;
                    }
                    else if (chr == BRACE_CLOSE) {state = State.FINAL;}
                    else if (chr != SPACE) {error = Error.LETTER_EXPECTED;}
                }
            }
            if (!error.equals(Error.NO_ERROR)) {state = State.ERROR;}
        }
        if (error.equals(Error.NO_ERROR)) {
            final int fakeWordPos = posFakeKeyWord(idNames, input.toCharArray());
            final int superiorIntPos = posSuperiorInt(constants, input.toCharArray());
            if (fakeWordPos >= 0 || superiorIntPos >= 0) {
                if (fakeWordPos < 0) {
                    error = Error.SUPERIOR_INT;
                    pos = superiorIntPos;
                }
                else if (superiorIntPos < 0) {
                    error = Error.DISCOVERED_FAKE_KEY_WORD;
                    pos = fakeWordPos;
                } else {
                    pos = Math.min(fakeWordPos, superiorIntPos);
                    if (pos == fakeWordPos) {error = Error.DISCOVERED_FAKE_KEY_WORD;}
                    else {error = Error.SUPERIOR_INT;}
                }
            }
        }
    }

    //Вывод
    public void outOnScreen() {
        if (error.equals(Error.NO_ERROR)){
            System.out.println(ConsoleColors.GREEN_BOLD_BRIGHT + error.message() + ConsoleColors.RESET);

            printLine(Math.max(idNames.size(), constants.size()));
            System.out.print("id\t\t|\t");
            for (final Character character : idNames) {
                System.out.print(character);
            }
            System.out.println();
            printLine(Math.max(idNames.size(), constants.size()));
            System.out.print("const\t|\t");
            for (final Character character : constants) {
                System.out.print(character);
            }
            System.out.println();
            printLine(Math.max(idNames.size(), constants.size()));
        } else {
            for (int i = 0; i < pos; i++) {
                if (i == pos - 1) {System.out.print("^");}
                else {System.out.print(" ");}
            }
            System.out.println("\n" +
                    ConsoleColors.RED_BOLD_BRIGHT +
                    "!!! \"" + error.message() + ":" +
                    ConsoleColors.RESET +
                    " [" + pos + "]" +
                    ConsoleColors.RED_BOLD_BRIGHT +
                    "\"" +
                    ConsoleColors.RESET);
        }
    }

    //Метод вывода линии
    private void printLine(final int count) {
        for (int i = 0; i < count + 11; i++) {
            System.out.print("-");
        }
        System.out.println();
    }

    //Методы для устранения дублирования
    private State getAfterTermState(final char verifiable) {
        State result;
        if (verifiable == EQUALS || verifiable == EXCL_POINT) {result = State.S23;}
        else if (verifiable == LESS || verifiable == MORE) {result = State.S24;}
        else if (verifiable == AND ) {
            if (logicOperatorsCheck < maxLogicOperators) {
                logicOperatorsCheck++;
                result = State.S41;
            }
            else {
                result = State.ERROR;
                error = Error.EXCESS_MAX_LOGIC_OPERATORS;
            }
        }
        else if (verifiable == OR ) {
            if (logicOperatorsCheck < maxLogicOperators) {
                logicOperatorsCheck++;
                result = State.S42;
            }
            else {
                result = State.ERROR;
                error = Error.EXCESS_MAX_LOGIC_OPERATORS;
            }
        }
        else if (verifiable == RND_BRACKET_CLOSE) {result = State.S43;}
        else {
            result = State.ERROR;
            error = Error.AFTER_TERM_CHAR_EXPECTED;
        }
        return result;
    }
    private boolean isNotCharOrDigit(final char verifiable) {
        return !Character.isLetter(verifiable) &&
                verifiable != UNDERLINE &&
                !Character.isDigit(verifiable);
    }
    private State getMathOrEndOrDigit(final char verifiable, final List<Character> constants, final State currentState) {
        State result;
        if (verifiable == PLUS
                || verifiable == MINUS
                || verifiable == MULTIPLICATION
                || verifiable == SLASH
                || verifiable == PERCENT) {
            constants.add(SPACE);
            result = State.S53;
        }
        else if (verifiable == SEMICOLON) {
            constants.add(SPACE);
            result = State.S70;
        }
        else if (Character.isDigit(verifiable)) {
            result = currentState;
            constants.add(verifiable);
        } else {
            constants.add(SPACE);
            result = State.ERROR;
        }
        return result;
    }
    private State getDigitLogicParentheses(final char verifiable, final List<Character> constants, final State currentState) {
        State result;
        if (verifiable == AND) {
            if (logicOperatorsCheck < maxLogicOperators) {logicOperatorsCheck++;}
            else {error = Error.EXCESS_MAX_LOGIC_OPERATORS;}
            constants.add(SPACE);
            result = State.S41;
        }
        else if (verifiable == OR) {
            if (logicOperatorsCheck < maxLogicOperators) {logicOperatorsCheck++;}
            else {error = Error.EXCESS_MAX_LOGIC_OPERATORS;}
            constants.add(SPACE);
            result = State.S42;
        }
        else if (verifiable == RND_BRACKET_CLOSE) {
            constants.add(SPACE);
            result = State.S43;
        }
        else if (Character.isDigit(verifiable)) {
            constants.add(verifiable);
            result = currentState;
        } else {
            constants.add(SPACE);
            result = State.ERROR;
        }
        return result;
    }
    private State getLogicOrParentheses(final char verifiable, final State currentState) {
        State result;
        if (verifiable == AND) {
            if (logicOperatorsCheck < maxLogicOperators) {logicOperatorsCheck++;}
            else {error = Error.EXCESS_MAX_LOGIC_OPERATORS;}
            result = State.S41;
        }
        else if (verifiable == OR) {
            if (logicOperatorsCheck < maxLogicOperators) {logicOperatorsCheck++;}
            else {error = Error.EXCESS_MAX_LOGIC_OPERATORS;}
            result = State.S42;
        }
        else if (verifiable == RND_BRACKET_CLOSE) {result = State.S43;}
        else if (verifiable == SPACE) {result = currentState;}
        else {
            error = Error.LOGIC_OR_BRACKET_EXPECTED;
            result = currentState;
        }
        return result;
    }
    private State getMathOrEnd(final char verifiable, final State currentState) {
        State result;
        if (verifiable == PLUS
                || verifiable == MINUS
                || verifiable == MULTIPLICATION
                || verifiable == SLASH
                || verifiable == PERCENT) {
            result = State.S53;
        }
        else if (verifiable == SEMICOLON) {result = State.S70;}
        else if (verifiable == UNDERLINE) {
            result = currentState;
        } else {
            result = State.ERROR;
            error = Error.MATH_OR_END_EXPECTED;
        }
        return result;
    }
    private void checkCharOrDigitForId(final char verifiable){
        if (isNotCharOrDigit(verifiable)) {
            idNames.add(SPACE);
            idLengthCheck = 0;
            error = Error.SQUARE_BRACKET_EXPECTED;
        }
        else {
            if (idLengthCheck < maxIdLength) {idLengthCheck++;}
            else {error = Error.EXCESS_MAX_ID_LENGTH;}
            idNames.add(verifiable);
        }
    }

    //Методы определения позиций
    private int posFakeKeyWord(final List<Character> idNames, final char... input) {
        int pos = 0;
        int innerIndex = 0;
        final ArrayList<Character> checkable = new ArrayList<>();
        final StringBuilder checkableStr = new StringBuilder();
        while (innerIndex < idNames.size()) {
            for (; idNames.get(innerIndex) != ' '; pos++) {
                if (input[pos] == idNames.get(innerIndex)) {
                    checkable.add(idNames.get(innerIndex));
                    innerIndex++;
                }
            }
            innerIndex++;

            for (final Character c : checkable) {
                checkableStr.append(c.toString());
            }
            checkable.clear();
            if (FOR_WORD.equalsIgnoreCase(checkableStr.toString())
                    || WHILE_WORD.equalsIgnoreCase(checkableStr.toString())
                    || BREAK_WORD.equalsIgnoreCase(checkableStr.toString())
                    || SWITCH_WORD.equalsIgnoreCase(checkableStr.toString())
                    || CASE_WORD.equalsIgnoreCase(checkableStr.toString())
                    || CONST_WORD.equalsIgnoreCase(checkableStr.toString())) {break;}
            else if (innerIndex == idNames.size()) {pos = -1;}
            checkableStr.delete(0, checkableStr.length());
        }
        return pos;
    }
    private int posSuperiorInt(final List<Character> constants, final char... input) {
        int pos = 0;
        int innerIndex = 0;
        final ArrayList<Character> checkable = new ArrayList<>();
        final StringBuilder checkableStr = new StringBuilder();
        while (innerIndex < constants.size()) {
            boolean isInt = true;
            for (; constants.get(innerIndex) != SPACE; pos++) {
                if (input[pos] == constants.get(innerIndex)) {
                    if (input[pos] == DOT || input[pos] == 'e' || input[pos] == 'E')
                    {isInt = false;}
                    checkable.add(constants.get(innerIndex));
                    innerIndex++;
                }
            }
            innerIndex++;

            for (final Character c : checkable) {
                checkableStr.append(c.toString());
            }
            checkable.clear();
            if (isInt) {
                final int checkableValue = Integer.parseInt(checkableStr.toString());
                if (checkableValue < -32_768 || checkableValue > 32_767) {break;}
            }
            if (innerIndex == constants.size()) {pos = -1;}
            checkableStr.delete(0, checkableStr.length());
        }
        return pos;
    }
}