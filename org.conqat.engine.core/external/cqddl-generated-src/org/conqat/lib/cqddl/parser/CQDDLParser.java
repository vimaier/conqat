// $ANTLR 3.2 Sep 23, 2009 12:02:23 CQDDL.g {{TIMESTAMP}}

package org.conqat.lib.cqddl.parser;

import org.conqat.lib.commons.collections.PairList;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

public class CQDDLParser extends CQDDLParserBase {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "DIGIT", "LETTER", "ALPHANUMEXT", "NULL", "BOOLEAN", "INTEGER", "DOUBLE", "PLAIN_STRING", "QUOTED_STRING", "WHITESPACE", "'$'", "'('", "','", "')'", "'='", "'@'"
    };
    public static final int INTEGER=9;
    public static final int PLAIN_STRING=11;
    public static final int T__19=19;
    public static final int BOOLEAN=8;
    public static final int T__16=16;
    public static final int T__15=15;
    public static final int T__18=18;
    public static final int T__17=17;
    public static final int ALPHANUMEXT=6;
    public static final int LETTER=5;
    public static final int T__14=14;
    public static final int NULL=7;
    public static final int DOUBLE=10;
    public static final int WHITESPACE=13;
    public static final int QUOTED_STRING=12;
    public static final int DIGIT=4;
    public static final int EOF=-1;

    // delegates
    // delegators


        public CQDDLParser(TokenStream input) {
            this(input, new RecognizerSharedState());
        }
        public CQDDLParser(TokenStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        

    public String[] getTokenNames() { return CQDDLParser.tokenNames; }
    public String getGrammarFileName() { return "CQDDL.g"; }



    // $ANTLR start "objectEOF"
    // CQDDL.g:48:1: objectEOF returns [Object result] : object EOF ;
    public final Object objectEOF() throws RecognitionException {
        Object result = null;

        Object object1 = null;


        try {
            // CQDDL.g:48:35: ( object EOF )
            // CQDDL.g:49:2: object EOF
            {
            pushFollow(FOLLOW_object_in_objectEOF294);
            object1=object();

            state._fsp--;

            match(input,EOF,FOLLOW_EOF_in_objectEOF296); 
             result = object1; 

            }

        }

        	catch ( RecognitionException ex ) {
        		reportError(ex);
        		throw ex;
        	}
        finally {
        }
        return result;
    }
    // $ANTLR end "objectEOF"


    // $ANTLR start "object"
    // CQDDL.g:52:1: object returns [Object result] : (p= pair_list | NULL | b= BOOLEAN | i= INTEGER | d= DOUBLE | s= string | '$' n= string | f= string p= pair_list );
    public final Object object() throws RecognitionException {
        Object result = null;

        Token b=null;
        Token i=null;
        Token d=null;
        PairList p = null;

        String s = null;

        String n = null;

        String f = null;


        try {
            // CQDDL.g:52:32: (p= pair_list | NULL | b= BOOLEAN | i= INTEGER | d= DOUBLE | s= string | '$' n= string | f= string p= pair_list )
            int alt1=8;
            alt1 = dfa1.predict(input);
            switch (alt1) {
                case 1 :
                    // CQDDL.g:53:4: p= pair_list
                    {
                    pushFollow(FOLLOW_pair_list_in_object320);
                    p=pair_list();

                    state._fsp--;

                     result = p; 

                    }
                    break;
                case 2 :
                    // CQDDL.g:54:4: NULL
                    {
                    match(input,NULL,FOLLOW_NULL_in_object337); 
                     result = null; 

                    }
                    break;
                case 3 :
                    // CQDDL.g:55:4: b= BOOLEAN
                    {
                    b=(Token)match(input,BOOLEAN,FOLLOW_BOOLEAN_in_object363); 
                     result = Boolean.parseBoolean ((b!=null?b.getText():null)); 

                    }
                    break;
                case 4 :
                    // CQDDL.g:56:4: i= INTEGER
                    {
                    i=(Token)match(input,INTEGER,FOLLOW_INTEGER_in_object384); 
                     result = Integer.parseInt ((i!=null?i.getText():null)); 

                    }
                    break;
                case 5 :
                    // CQDDL.g:57:4: d= DOUBLE
                    {
                    d=(Token)match(input,DOUBLE,FOLLOW_DOUBLE_in_object405); 
                     result = Double.parseDouble ((d!=null?d.getText():null)); 

                    }
                    break;
                case 6 :
                    // CQDDL.g:58:4: s= string
                    {
                    pushFollow(FOLLOW_string_in_object427);
                    s=string();

                    state._fsp--;

                     result = s; 

                    }
                    break;
                case 7 :
                    // CQDDL.g:59:4: '$' n= string
                    {
                    match(input,14,FOLLOW_14_in_object447); 
                    pushFollow(FOLLOW_string_in_object451);
                    n=string();

                    state._fsp--;

                     result = retrieveObject (n); 

                    }
                    break;
                case 8 :
                    // CQDDL.g:60:4: f= string p= pair_list
                    {
                    pushFollow(FOLLOW_string_in_object469);
                    f=string();

                    state._fsp--;

                    pushFollow(FOLLOW_pair_list_in_object473);
                    p=pair_list();

                    state._fsp--;

                     result = eval (f, p); 

                    }
                    break;

            }
        }

        	catch ( RecognitionException ex ) {
        		reportError(ex);
        		throw ex;
        	}
        finally {
        }
        return result;
    }
    // $ANTLR end "object"


    // $ANTLR start "pair_list"
    // CQDDL.g:63:1: pair_list returns [PairList result] : '(' (e1= list_entry ( ',' e2= list_entry )* )? ')' ;
    public final PairList pair_list() throws RecognitionException {
        PairList result = null;

        CQDDLParser.list_entry_return e1 = null;

        CQDDLParser.list_entry_return e2 = null;


        try {
            // CQDDL.g:63:37: ( '(' (e1= list_entry ( ',' e2= list_entry )* )? ')' )
            // CQDDL.g:64:2: '(' (e1= list_entry ( ',' e2= list_entry )* )? ')'
            {
            match(input,15,FOLLOW_15_in_pair_list491); 
             result = new PairList<String, Object> (); 
            // CQDDL.g:65:6: (e1= list_entry ( ',' e2= list_entry )* )?
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( ((LA3_0>=NULL && LA3_0<=QUOTED_STRING)||(LA3_0>=14 && LA3_0<=15)) ) {
                alt3=1;
            }
            switch (alt3) {
                case 1 :
                    // CQDDL.g:66:5: e1= list_entry ( ',' e2= list_entry )*
                    {
                    pushFollow(FOLLOW_list_entry_in_pair_list531);
                    e1=list_entry();

                    state._fsp--;

                     result.add ((e1!=null?e1.key:null), (e1!=null?e1.value:null)); 
                    // CQDDL.g:67:5: ( ',' e2= list_entry )*
                    loop2:
                    do {
                        int alt2=2;
                        int LA2_0 = input.LA(1);

                        if ( (LA2_0==16) ) {
                            alt2=1;
                        }


                        switch (alt2) {
                    	case 1 :
                    	    // CQDDL.g:68:6: ',' e2= list_entry
                    	    {
                    	    match(input,16,FOLLOW_16_in_pair_list554); 
                    	    pushFollow(FOLLOW_list_entry_in_pair_list558);
                    	    e2=list_entry();

                    	    state._fsp--;

                    	     result.add ((e2!=null?e2.key:null), (e2!=null?e2.value:null)); 

                    	    }
                    	    break;

                    	default :
                    	    break loop2;
                        }
                    } while (true);


                    }
                    break;

            }

            match(input,17,FOLLOW_17_in_pair_list577); 

            }

        }

        	catch ( RecognitionException ex ) {
        		reportError(ex);
        		throw ex;
        	}
        finally {
        }
        return result;
    }
    // $ANTLR end "pair_list"

    public static class list_entry_return extends ParserRuleReturnScope {
        public String key;
        public Object value;
    };

    // $ANTLR start "list_entry"
    // CQDDL.g:74:1: list_entry returns [String key, Object value] : (k= string '=' )? v= object ( '@' n= string )? ;
    public final CQDDLParser.list_entry_return list_entry() throws RecognitionException {
        CQDDLParser.list_entry_return retval = new CQDDLParser.list_entry_return();
        retval.start = input.LT(1);

        String k = null;

        Object v = null;

        String n = null;


        try {
            // CQDDL.g:74:47: ( (k= string '=' )? v= object ( '@' n= string )? )
            // CQDDL.g:75:29: (k= string '=' )? v= object ( '@' n= string )?
            {
             retval.key = null; 
            // CQDDL.g:76:5: (k= string '=' )?
            int alt4=2;
            int LA4_0 = input.LA(1);

            if ( (LA4_0==PLAIN_STRING) ) {
                int LA4_1 = input.LA(2);

                if ( (LA4_1==18) ) {
                    alt4=1;
                }
            }
            else if ( (LA4_0==QUOTED_STRING) ) {
                int LA4_2 = input.LA(2);

                if ( (LA4_2==18) ) {
                    alt4=1;
                }
            }
            switch (alt4) {
                case 1 :
                    // CQDDL.g:77:6: k= string '='
                    {
                    pushFollow(FOLLOW_string_in_list_entry634);
                    k=string();

                    state._fsp--;

                    match(input,18,FOLLOW_18_in_list_entry636); 
                     retval.key = resolveKey(k); 

                    }
                    break;

            }

            pushFollow(FOLLOW_object_in_list_entry664);
            v=object();

            state._fsp--;

             retval.value = v; 
            // CQDDL.g:80:5: ( '@' n= string )?
            int alt5=2;
            int LA5_0 = input.LA(1);

            if ( (LA5_0==19) ) {
                alt5=1;
            }
            switch (alt5) {
                case 1 :
                    // CQDDL.g:81:6: '@' n= string
                    {
                    match(input,19,FOLLOW_19_in_list_entry698); 
                    pushFollow(FOLLOW_string_in_list_entry702);
                    n=string();

                    state._fsp--;

                     storeObject (n, retval.value); 

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

        }

        	catch ( RecognitionException ex ) {
        		reportError(ex);
        		throw ex;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end "list_entry"


    // $ANTLR start "string"
    // CQDDL.g:85:1: string returns [String result] : (p= PLAIN_STRING | q= QUOTED_STRING );
    public final String string() throws RecognitionException {
        String result = null;

        Token p=null;
        Token q=null;

        try {
            // CQDDL.g:85:32: (p= PLAIN_STRING | q= QUOTED_STRING )
            int alt6=2;
            int LA6_0 = input.LA(1);

            if ( (LA6_0==PLAIN_STRING) ) {
                alt6=1;
            }
            else if ( (LA6_0==QUOTED_STRING) ) {
                alt6=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 6, 0, input);

                throw nvae;
            }
            switch (alt6) {
                case 1 :
                    // CQDDL.g:86:4: p= PLAIN_STRING
                    {
                    p=(Token)match(input,PLAIN_STRING,FOLLOW_PLAIN_STRING_in_string740); 
                     result = (p!=null?p.getText():null); 

                    }
                    break;
                case 2 :
                    // CQDDL.g:87:4: q= QUOTED_STRING
                    {
                    q=(Token)match(input,QUOTED_STRING,FOLLOW_QUOTED_STRING_in_string750); 
                     result = unquote ((q!=null?q.getText():null)); 

                    }
                    break;

            }
        }

        	catch ( RecognitionException ex ) {
        		reportError(ex);
        		throw ex;
        	}
        finally {
        }
        return result;
    }
    // $ANTLR end "string"

    // Delegated rules


    protected DFA1 dfa1 = new DFA1(this);
    static final String DFA1_eotS =
        "\13\uffff";
    static final String DFA1_eofS =
        "\6\uffff\2\12\3\uffff";
    static final String DFA1_minS =
        "\1\7\5\uffff\2\17\3\uffff";
    static final String DFA1_maxS =
        "\1\17\5\uffff\2\23\3\uffff";
    static final String DFA1_acceptS =
        "\1\uffff\1\1\1\2\1\3\1\4\1\5\2\uffff\1\7\1\10\1\6";
    static final String DFA1_specialS =
        "\13\uffff}>";
    static final String[] DFA1_transitionS = {
            "\1\2\1\3\1\4\1\5\1\6\1\7\1\uffff\1\10\1\1",
            "",
            "",
            "",
            "",
            "",
            "\1\11\2\12\1\uffff\1\12",
            "\1\11\2\12\1\uffff\1\12",
            "",
            "",
            ""
    };

    static final short[] DFA1_eot = DFA.unpackEncodedString(DFA1_eotS);
    static final short[] DFA1_eof = DFA.unpackEncodedString(DFA1_eofS);
    static final char[] DFA1_min = DFA.unpackEncodedStringToUnsignedChars(DFA1_minS);
    static final char[] DFA1_max = DFA.unpackEncodedStringToUnsignedChars(DFA1_maxS);
    static final short[] DFA1_accept = DFA.unpackEncodedString(DFA1_acceptS);
    static final short[] DFA1_special = DFA.unpackEncodedString(DFA1_specialS);
    static final short[][] DFA1_transition;

    static {
        int numStates = DFA1_transitionS.length;
        DFA1_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA1_transition[i] = DFA.unpackEncodedString(DFA1_transitionS[i]);
        }
    }

    class DFA1 extends DFA {

        public DFA1(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 1;
            this.eot = DFA1_eot;
            this.eof = DFA1_eof;
            this.min = DFA1_min;
            this.max = DFA1_max;
            this.accept = DFA1_accept;
            this.special = DFA1_special;
            this.transition = DFA1_transition;
        }
        public String getDescription() {
            return "52:1: object returns [Object result] : (p= pair_list | NULL | b= BOOLEAN | i= INTEGER | d= DOUBLE | s= string | '$' n= string | f= string p= pair_list );";
        }
    }
 

    public static final BitSet FOLLOW_object_in_objectEOF294 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_objectEOF296 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_pair_list_in_object320 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NULL_in_object337 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BOOLEAN_in_object363 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INTEGER_in_object384 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOUBLE_in_object405 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_string_in_object427 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_14_in_object447 = new BitSet(new long[]{0x0000000000001800L});
    public static final BitSet FOLLOW_string_in_object451 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_string_in_object469 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_pair_list_in_object473 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_15_in_pair_list491 = new BitSet(new long[]{0x000000000002DF80L});
    public static final BitSet FOLLOW_list_entry_in_pair_list531 = new BitSet(new long[]{0x0000000000030000L});
    public static final BitSet FOLLOW_16_in_pair_list554 = new BitSet(new long[]{0x000000000000DF80L});
    public static final BitSet FOLLOW_list_entry_in_pair_list558 = new BitSet(new long[]{0x0000000000030000L});
    public static final BitSet FOLLOW_17_in_pair_list577 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_string_in_list_entry634 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_18_in_list_entry636 = new BitSet(new long[]{0x000000000000DF80L});
    public static final BitSet FOLLOW_object_in_list_entry664 = new BitSet(new long[]{0x0000000000080002L});
    public static final BitSet FOLLOW_19_in_list_entry698 = new BitSet(new long[]{0x0000000000001800L});
    public static final BitSet FOLLOW_string_in_list_entry702 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PLAIN_STRING_in_string740 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_QUOTED_STRING_in_string750 = new BitSet(new long[]{0x0000000000000002L});

}
