// $ANTLR 3.2 Sep 23, 2009 12:02:23 CQDDL.g {{TIMESTAMP}}

package org.conqat.lib.cqddl.parser;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

public class CQDDLLexer extends Lexer {
    public static final int INTEGER=9;
    public static final int PLAIN_STRING=11;
    public static final int LETTER=5;
    public static final int NULL=7;
    public static final int WHITESPACE=13;
    public static final int EOF=-1;
    public static final int T__19=19;
    public static final int BOOLEAN=8;
    public static final int T__16=16;
    public static final int T__15=15;
    public static final int T__18=18;
    public static final int T__17=17;
    public static final int ALPHANUMEXT=6;
    public static final int T__14=14;
    public static final int DOUBLE=10;
    public static final int QUOTED_STRING=12;
    public static final int DIGIT=4;

    // delegates
    // delegators

    public CQDDLLexer() {;} 
    public CQDDLLexer(CharStream input) {
        this(input, new RecognizerSharedState());
    }
    public CQDDLLexer(CharStream input, RecognizerSharedState state) {
        super(input,state);

    }
    public String getGrammarFileName() { return "CQDDL.g"; }

    // $ANTLR start "T__14"
    public final void mT__14() throws RecognitionException {
        try {
            int _type = T__14;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // CQDDL.g:7:7: ( '$' )
            // CQDDL.g:7:9: '$'
            {
            match('$'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__14"

    // $ANTLR start "T__15"
    public final void mT__15() throws RecognitionException {
        try {
            int _type = T__15;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // CQDDL.g:8:7: ( '(' )
            // CQDDL.g:8:9: '('
            {
            match('('); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__15"

    // $ANTLR start "T__16"
    public final void mT__16() throws RecognitionException {
        try {
            int _type = T__16;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // CQDDL.g:9:7: ( ',' )
            // CQDDL.g:9:9: ','
            {
            match(','); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__16"

    // $ANTLR start "T__17"
    public final void mT__17() throws RecognitionException {
        try {
            int _type = T__17;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // CQDDL.g:10:7: ( ')' )
            // CQDDL.g:10:9: ')'
            {
            match(')'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__17"

    // $ANTLR start "T__18"
    public final void mT__18() throws RecognitionException {
        try {
            int _type = T__18;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // CQDDL.g:11:7: ( '=' )
            // CQDDL.g:11:9: '='
            {
            match('='); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__18"

    // $ANTLR start "T__19"
    public final void mT__19() throws RecognitionException {
        try {
            int _type = T__19;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // CQDDL.g:12:7: ( '@' )
            // CQDDL.g:12:9: '@'
            {
            match('@'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__19"

    // $ANTLR start "DIGIT"
    public final void mDIGIT() throws RecognitionException {
        try {
            // CQDDL.g:30:17: ( '0' .. '9' )
            // CQDDL.g:30:19: '0' .. '9'
            {
            matchRange('0','9'); 

            }

        }
        finally {
        }
    }
    // $ANTLR end "DIGIT"

    // $ANTLR start "LETTER"
    public final void mLETTER() throws RecognitionException {
        try {
            // CQDDL.g:31:17: ( 'a' .. 'z' | 'A' .. 'Z' | '_' )
            // CQDDL.g:
            {
            if ( (input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z') ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

        }
        finally {
        }
    }
    // $ANTLR end "LETTER"

    // $ANTLR start "ALPHANUMEXT"
    public final void mALPHANUMEXT() throws RecognitionException {
        try {
            // CQDDL.g:32:22: ( LETTER | DIGIT | '.' )
            // CQDDL.g:
            {
            if ( input.LA(1)=='.'||(input.LA(1)>='0' && input.LA(1)<='9')||(input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z') ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

        }
        finally {
        }
    }
    // $ANTLR end "ALPHANUMEXT"

    // $ANTLR start "NULL"
    public final void mNULL() throws RecognitionException {
        try {
            int _type = NULL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // CQDDL.g:34:9: ( 'null' )
            // CQDDL.g:34:11: 'null'
            {
            match("null"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "NULL"

    // $ANTLR start "BOOLEAN"
    public final void mBOOLEAN() throws RecognitionException {
        try {
            int _type = BOOLEAN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // CQDDL.g:35:9: ( 'true' | 'false' )
            int alt1=2;
            int LA1_0 = input.LA(1);

            if ( (LA1_0=='t') ) {
                alt1=1;
            }
            else if ( (LA1_0=='f') ) {
                alt1=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 1, 0, input);

                throw nvae;
            }
            switch (alt1) {
                case 1 :
                    // CQDDL.g:35:11: 'true'
                    {
                    match("true"); 


                    }
                    break;
                case 2 :
                    // CQDDL.g:35:20: 'false'
                    {
                    match("false"); 


                    }
                    break;

            }
            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "BOOLEAN"

    // $ANTLR start "INTEGER"
    public final void mINTEGER() throws RecognitionException {
        try {
            int _type = INTEGER;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // CQDDL.g:36:9: ( ( '+' | '-' )? ( DIGIT )+ )
            // CQDDL.g:36:11: ( '+' | '-' )? ( DIGIT )+
            {
            // CQDDL.g:36:11: ( '+' | '-' )?
            int alt2=2;
            int LA2_0 = input.LA(1);

            if ( (LA2_0=='+'||LA2_0=='-') ) {
                alt2=1;
            }
            switch (alt2) {
                case 1 :
                    // CQDDL.g:
                    {
                    if ( input.LA(1)=='+'||input.LA(1)=='-' ) {
                        input.consume();

                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        recover(mse);
                        throw mse;}


                    }
                    break;

            }

            // CQDDL.g:36:26: ( DIGIT )+
            int cnt3=0;
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( ((LA3_0>='0' && LA3_0<='9')) ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // CQDDL.g:36:26: DIGIT
            	    {
            	    mDIGIT(); 

            	    }
            	    break;

            	default :
            	    if ( cnt3 >= 1 ) break loop3;
                        EarlyExitException eee =
                            new EarlyExitException(3, input);
                        throw eee;
                }
                cnt3++;
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "INTEGER"

    // $ANTLR start "DOUBLE"
    public final void mDOUBLE() throws RecognitionException {
        try {
            int _type = DOUBLE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // CQDDL.g:37:9: ( ( '+' | '-' )? ( ( DIGIT )+ '.' ( DIGIT )* | '.' ( DIGIT )+ ) ( ( 'e' | 'E' ) ( '-' | '+' )? ( DIGIT )+ )? )
            // CQDDL.g:37:11: ( '+' | '-' )? ( ( DIGIT )+ '.' ( DIGIT )* | '.' ( DIGIT )+ ) ( ( 'e' | 'E' ) ( '-' | '+' )? ( DIGIT )+ )?
            {
            // CQDDL.g:37:11: ( '+' | '-' )?
            int alt4=2;
            int LA4_0 = input.LA(1);

            if ( (LA4_0=='+'||LA4_0=='-') ) {
                alt4=1;
            }
            switch (alt4) {
                case 1 :
                    // CQDDL.g:
                    {
                    if ( input.LA(1)=='+'||input.LA(1)=='-' ) {
                        input.consume();

                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        recover(mse);
                        throw mse;}


                    }
                    break;

            }

            // CQDDL.g:37:26: ( ( DIGIT )+ '.' ( DIGIT )* | '.' ( DIGIT )+ )
            int alt8=2;
            int LA8_0 = input.LA(1);

            if ( ((LA8_0>='0' && LA8_0<='9')) ) {
                alt8=1;
            }
            else if ( (LA8_0=='.') ) {
                alt8=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 8, 0, input);

                throw nvae;
            }
            switch (alt8) {
                case 1 :
                    // CQDDL.g:37:28: ( DIGIT )+ '.' ( DIGIT )*
                    {
                    // CQDDL.g:37:28: ( DIGIT )+
                    int cnt5=0;
                    loop5:
                    do {
                        int alt5=2;
                        int LA5_0 = input.LA(1);

                        if ( ((LA5_0>='0' && LA5_0<='9')) ) {
                            alt5=1;
                        }


                        switch (alt5) {
                    	case 1 :
                    	    // CQDDL.g:37:28: DIGIT
                    	    {
                    	    mDIGIT(); 

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt5 >= 1 ) break loop5;
                                EarlyExitException eee =
                                    new EarlyExitException(5, input);
                                throw eee;
                        }
                        cnt5++;
                    } while (true);

                    match('.'); 
                    // CQDDL.g:37:39: ( DIGIT )*
                    loop6:
                    do {
                        int alt6=2;
                        int LA6_0 = input.LA(1);

                        if ( ((LA6_0>='0' && LA6_0<='9')) ) {
                            alt6=1;
                        }


                        switch (alt6) {
                    	case 1 :
                    	    // CQDDL.g:37:39: DIGIT
                    	    {
                    	    mDIGIT(); 

                    	    }
                    	    break;

                    	default :
                    	    break loop6;
                        }
                    } while (true);


                    }
                    break;
                case 2 :
                    // CQDDL.g:37:48: '.' ( DIGIT )+
                    {
                    match('.'); 
                    // CQDDL.g:37:52: ( DIGIT )+
                    int cnt7=0;
                    loop7:
                    do {
                        int alt7=2;
                        int LA7_0 = input.LA(1);

                        if ( ((LA7_0>='0' && LA7_0<='9')) ) {
                            alt7=1;
                        }


                        switch (alt7) {
                    	case 1 :
                    	    // CQDDL.g:37:52: DIGIT
                    	    {
                    	    mDIGIT(); 

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt7 >= 1 ) break loop7;
                                EarlyExitException eee =
                                    new EarlyExitException(7, input);
                                throw eee;
                        }
                        cnt7++;
                    } while (true);


                    }
                    break;

            }

            // CQDDL.g:37:61: ( ( 'e' | 'E' ) ( '-' | '+' )? ( DIGIT )+ )?
            int alt11=2;
            int LA11_0 = input.LA(1);

            if ( (LA11_0=='E'||LA11_0=='e') ) {
                alt11=1;
            }
            switch (alt11) {
                case 1 :
                    // CQDDL.g:37:63: ( 'e' | 'E' ) ( '-' | '+' )? ( DIGIT )+
                    {
                    if ( input.LA(1)=='E'||input.LA(1)=='e' ) {
                        input.consume();

                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        recover(mse);
                        throw mse;}

                    // CQDDL.g:37:77: ( '-' | '+' )?
                    int alt9=2;
                    int LA9_0 = input.LA(1);

                    if ( (LA9_0=='+'||LA9_0=='-') ) {
                        alt9=1;
                    }
                    switch (alt9) {
                        case 1 :
                            // CQDDL.g:
                            {
                            if ( input.LA(1)=='+'||input.LA(1)=='-' ) {
                                input.consume();

                            }
                            else {
                                MismatchedSetException mse = new MismatchedSetException(null,input);
                                recover(mse);
                                throw mse;}


                            }
                            break;

                    }

                    // CQDDL.g:37:93: ( DIGIT )+
                    int cnt10=0;
                    loop10:
                    do {
                        int alt10=2;
                        int LA10_0 = input.LA(1);

                        if ( ((LA10_0>='0' && LA10_0<='9')) ) {
                            alt10=1;
                        }


                        switch (alt10) {
                    	case 1 :
                    	    // CQDDL.g:37:93: DIGIT
                    	    {
                    	    mDIGIT(); 

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt10 >= 1 ) break loop10;
                                EarlyExitException eee =
                                    new EarlyExitException(10, input);
                                throw eee;
                        }
                        cnt10++;
                    } while (true);


                    }
                    break;

            }


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "DOUBLE"

    // $ANTLR start "PLAIN_STRING"
    public final void mPLAIN_STRING() throws RecognitionException {
        try {
            int _type = PLAIN_STRING;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // CQDDL.g:39:15: ( LETTER ( ALPHANUMEXT )* )
            // CQDDL.g:39:19: LETTER ( ALPHANUMEXT )*
            {
            mLETTER(); 
            // CQDDL.g:39:26: ( ALPHANUMEXT )*
            loop12:
            do {
                int alt12=2;
                int LA12_0 = input.LA(1);

                if ( (LA12_0=='.'||(LA12_0>='0' && LA12_0<='9')||(LA12_0>='A' && LA12_0<='Z')||LA12_0=='_'||(LA12_0>='a' && LA12_0<='z')) ) {
                    alt12=1;
                }


                switch (alt12) {
            	case 1 :
            	    // CQDDL.g:39:26: ALPHANUMEXT
            	    {
            	    mALPHANUMEXT(); 

            	    }
            	    break;

            	default :
            	    break loop12;
                }
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "PLAIN_STRING"

    // $ANTLR start "QUOTED_STRING"
    public final void mQUOTED_STRING() throws RecognitionException {
        try {
            int _type = QUOTED_STRING;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // CQDDL.g:40:15: ( ( '\\'' ( . )* '\\'' ) | ( '\\\"' ( . )* '\\\"' ) )
            int alt15=2;
            int LA15_0 = input.LA(1);

            if ( (LA15_0=='\'') ) {
                alt15=1;
            }
            else if ( (LA15_0=='\"') ) {
                alt15=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 15, 0, input);

                throw nvae;
            }
            switch (alt15) {
                case 1 :
                    // CQDDL.g:40:17: ( '\\'' ( . )* '\\'' )
                    {
                    // CQDDL.g:40:17: ( '\\'' ( . )* '\\'' )
                    // CQDDL.g:40:18: '\\'' ( . )* '\\''
                    {
                    match('\''); 
                    // CQDDL.g:40:23: ( . )*
                    loop13:
                    do {
                        int alt13=2;
                        int LA13_0 = input.LA(1);

                        if ( (LA13_0=='\'') ) {
                            alt13=2;
                        }
                        else if ( ((LA13_0>='\u0000' && LA13_0<='&')||(LA13_0>='(' && LA13_0<='\uFFFF')) ) {
                            alt13=1;
                        }


                        switch (alt13) {
                    	case 1 :
                    	    // CQDDL.g:40:23: .
                    	    {
                    	    matchAny(); 

                    	    }
                    	    break;

                    	default :
                    	    break loop13;
                        }
                    } while (true);

                    match('\''); 

                    }


                    }
                    break;
                case 2 :
                    // CQDDL.g:40:34: ( '\\\"' ( . )* '\\\"' )
                    {
                    // CQDDL.g:40:34: ( '\\\"' ( . )* '\\\"' )
                    // CQDDL.g:40:35: '\\\"' ( . )* '\\\"'
                    {
                    match('\"'); 
                    // CQDDL.g:40:40: ( . )*
                    loop14:
                    do {
                        int alt14=2;
                        int LA14_0 = input.LA(1);

                        if ( (LA14_0=='\"') ) {
                            alt14=2;
                        }
                        else if ( ((LA14_0>='\u0000' && LA14_0<='!')||(LA14_0>='#' && LA14_0<='\uFFFF')) ) {
                            alt14=1;
                        }


                        switch (alt14) {
                    	case 1 :
                    	    // CQDDL.g:40:40: .
                    	    {
                    	    matchAny(); 

                    	    }
                    	    break;

                    	default :
                    	    break loop14;
                        }
                    } while (true);

                    match('\"'); 

                    }


                    }
                    break;

            }
            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "QUOTED_STRING"

    // $ANTLR start "WHITESPACE"
    public final void mWHITESPACE() throws RecognitionException {
        try {
            int _type = WHITESPACE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // CQDDL.g:42:12: ( ( '\\t' | ' ' | '\\r' | '\\n' | '\\u000C' )+ )
            // CQDDL.g:42:14: ( '\\t' | ' ' | '\\r' | '\\n' | '\\u000C' )+
            {
            // CQDDL.g:42:14: ( '\\t' | ' ' | '\\r' | '\\n' | '\\u000C' )+
            int cnt16=0;
            loop16:
            do {
                int alt16=2;
                int LA16_0 = input.LA(1);

                if ( ((LA16_0>='\t' && LA16_0<='\n')||(LA16_0>='\f' && LA16_0<='\r')||LA16_0==' ') ) {
                    alt16=1;
                }


                switch (alt16) {
            	case 1 :
            	    // CQDDL.g:
            	    {
            	    if ( (input.LA(1)>='\t' && input.LA(1)<='\n')||(input.LA(1)>='\f' && input.LA(1)<='\r')||input.LA(1)==' ' ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


            	    }
            	    break;

            	default :
            	    if ( cnt16 >= 1 ) break loop16;
                        EarlyExitException eee =
                            new EarlyExitException(16, input);
                        throw eee;
                }
                cnt16++;
            } while (true);

             _channel = HIDDEN; 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "WHITESPACE"

    public void mTokens() throws RecognitionException {
        // CQDDL.g:1:8: ( T__14 | T__15 | T__16 | T__17 | T__18 | T__19 | NULL | BOOLEAN | INTEGER | DOUBLE | PLAIN_STRING | QUOTED_STRING | WHITESPACE )
        int alt17=13;
        alt17 = dfa17.predict(input);
        switch (alt17) {
            case 1 :
                // CQDDL.g:1:10: T__14
                {
                mT__14(); 

                }
                break;
            case 2 :
                // CQDDL.g:1:16: T__15
                {
                mT__15(); 

                }
                break;
            case 3 :
                // CQDDL.g:1:22: T__16
                {
                mT__16(); 

                }
                break;
            case 4 :
                // CQDDL.g:1:28: T__17
                {
                mT__17(); 

                }
                break;
            case 5 :
                // CQDDL.g:1:34: T__18
                {
                mT__18(); 

                }
                break;
            case 6 :
                // CQDDL.g:1:40: T__19
                {
                mT__19(); 

                }
                break;
            case 7 :
                // CQDDL.g:1:46: NULL
                {
                mNULL(); 

                }
                break;
            case 8 :
                // CQDDL.g:1:51: BOOLEAN
                {
                mBOOLEAN(); 

                }
                break;
            case 9 :
                // CQDDL.g:1:59: INTEGER
                {
                mINTEGER(); 

                }
                break;
            case 10 :
                // CQDDL.g:1:67: DOUBLE
                {
                mDOUBLE(); 

                }
                break;
            case 11 :
                // CQDDL.g:1:74: PLAIN_STRING
                {
                mPLAIN_STRING(); 

                }
                break;
            case 12 :
                // CQDDL.g:1:87: QUOTED_STRING
                {
                mQUOTED_STRING(); 

                }
                break;
            case 13 :
                // CQDDL.g:1:101: WHITESPACE
                {
                mWHITESPACE(); 

                }
                break;

        }

    }


    protected DFA17 dfa17 = new DFA17(this);
    static final String DFA17_eotS =
        "\7\uffff\3\15\1\uffff\1\23\4\uffff\3\15\1\uffff\3\15\1\32\1\33\1"+
        "\15\2\uffff\1\33";
    static final String DFA17_eofS =
        "\35\uffff";
    static final String DFA17_minS =
        "\1\11\6\uffff\1\165\1\162\1\141\2\56\4\uffff\1\154\1\165\1\154\1"+
        "\uffff\1\154\1\145\1\163\2\56\1\145\2\uffff\1\56";
    static final String DFA17_maxS =
        "\1\172\6\uffff\1\165\1\162\1\141\2\71\4\uffff\1\154\1\165\1\154"+
        "\1\uffff\1\154\1\145\1\163\2\172\1\145\2\uffff\1\172";
    static final String DFA17_acceptS =
        "\1\uffff\1\1\1\2\1\3\1\4\1\5\1\6\5\uffff\1\12\1\13\1\14\1\15\3\uffff"+
        "\1\11\6\uffff\1\7\1\10\1\uffff";
    static final String DFA17_specialS =
        "\35\uffff}>";
    static final String[] DFA17_transitionS = {
            "\2\17\1\uffff\2\17\22\uffff\1\17\1\uffff\1\16\1\uffff\1\1\2"+
            "\uffff\1\16\1\2\1\4\1\uffff\1\12\1\3\1\12\1\14\1\uffff\12\13"+
            "\3\uffff\1\5\2\uffff\1\6\32\15\4\uffff\1\15\1\uffff\5\15\1\11"+
            "\7\15\1\7\5\15\1\10\6\15",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\20",
            "\1\21",
            "\1\22",
            "\1\14\1\uffff\12\13",
            "\1\14\1\uffff\12\13",
            "",
            "",
            "",
            "",
            "\1\24",
            "\1\25",
            "\1\26",
            "",
            "\1\27",
            "\1\30",
            "\1\31",
            "\1\15\1\uffff\12\15\7\uffff\32\15\4\uffff\1\15\1\uffff\32\15",
            "\1\15\1\uffff\12\15\7\uffff\32\15\4\uffff\1\15\1\uffff\32\15",
            "\1\34",
            "",
            "",
            "\1\15\1\uffff\12\15\7\uffff\32\15\4\uffff\1\15\1\uffff\32\15"
    };

    static final short[] DFA17_eot = DFA.unpackEncodedString(DFA17_eotS);
    static final short[] DFA17_eof = DFA.unpackEncodedString(DFA17_eofS);
    static final char[] DFA17_min = DFA.unpackEncodedStringToUnsignedChars(DFA17_minS);
    static final char[] DFA17_max = DFA.unpackEncodedStringToUnsignedChars(DFA17_maxS);
    static final short[] DFA17_accept = DFA.unpackEncodedString(DFA17_acceptS);
    static final short[] DFA17_special = DFA.unpackEncodedString(DFA17_specialS);
    static final short[][] DFA17_transition;

    static {
        int numStates = DFA17_transitionS.length;
        DFA17_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA17_transition[i] = DFA.unpackEncodedString(DFA17_transitionS[i]);
        }
    }

    class DFA17 extends DFA {

        public DFA17(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 17;
            this.eot = DFA17_eot;
            this.eof = DFA17_eof;
            this.min = DFA17_min;
            this.max = DFA17_max;
            this.accept = DFA17_accept;
            this.special = DFA17_special;
            this.transition = DFA17_transition;
        }
        public String getDescription() {
            return "1:1: Tokens : ( T__14 | T__15 | T__16 | T__17 | T__18 | T__19 | NULL | BOOLEAN | INTEGER | DOUBLE | PLAIN_STRING | QUOTED_STRING | WHITESPACE );";
        }
    }
 

}
