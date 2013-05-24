// $ANTLR 3.2 Sep 23, 2009 12:02:23 EsperEPL2Ast.g 2013-01-03 08:00:33

  package com.espertech.esper.epl.generated;
  import java.util.Stack;
  import org.apache.commons.logging.Log;
  import org.apache.commons.logging.LogFactory;


import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

public class EsperEPL2Ast extends TreeParser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "CREATE", "WINDOW", "IN_SET", "BETWEEN", "LIKE", "REGEXP", "ESCAPE", "OR_EXPR", "AND_EXPR", "NOT_EXPR", "EVERY_EXPR", "EVERY_DISTINCT_EXPR", "WHERE", "AS", "SUM", "AVG", "MAX", "MIN", "COALESCE", "MEDIAN", "STDDEV", "AVEDEV", "COUNT", "SELECT", "CASE", "CASE2", "ELSE", "WHEN", "THEN", "END", "FROM", "OUTER", "INNER", "JOIN", "LEFT", "RIGHT", "FULL", "ON", "IS", "BY", "GROUP", "HAVING", "DISTINCT", "ALL", "ANY", "SOME", "OUTPUT", "EVENTS", "FIRST", "LAST", "INSERT", "INTO", "ORDER", "ASC", "DESC", "RSTREAM", "ISTREAM", "IRSTREAM", "SCHEMA", "UNIDIRECTIONAL", "RETAINUNION", "RETAININTERSECTION", "PATTERN", "SQL", "METADATASQL", "PREVIOUS", "PREVIOUSTAIL", "PREVIOUSCOUNT", "PREVIOUSWINDOW", "PRIOR", "EXISTS", "WEEKDAY", "LW", "INSTANCEOF", "TYPEOF", "CAST", "CURRENT_TIMESTAMP", "DELETE", "SNAPSHOT", "SET", "VARIABLE", "UNTIL", "AT", "INDEX", "TIMEPERIOD_YEAR", "TIMEPERIOD_YEARS", "TIMEPERIOD_MONTH", "TIMEPERIOD_MONTHS", "TIMEPERIOD_WEEK", "TIMEPERIOD_WEEKS", "TIMEPERIOD_DAY", "TIMEPERIOD_DAYS", "TIMEPERIOD_HOUR", "TIMEPERIOD_HOURS", "TIMEPERIOD_MINUTE", "TIMEPERIOD_MINUTES", "TIMEPERIOD_SEC", "TIMEPERIOD_SECOND", "TIMEPERIOD_SECONDS", "TIMEPERIOD_MILLISEC", "TIMEPERIOD_MILLISECOND", "TIMEPERIOD_MILLISECONDS", "BOOLEAN_TRUE", "BOOLEAN_FALSE", "VALUE_NULL", "ROW_LIMIT_EXPR", "OFFSET", "UPDATE", "MATCH_RECOGNIZE", "MEASURES", "DEFINE", "PARTITION", "MATCHES", "AFTER", "FOR", "WHILE", "USING", "MERGE", "MATCHED", "EXPRESSIONDECL", "NEWKW", "START", "CONTEXT", "INITIATED", "TERMINATED", "DATAFLOW", "NUMERIC_PARAM_RANGE", "NUMERIC_PARAM_LIST", "NUMERIC_PARAM_FREQUENCY", "OBJECT_PARAM_ORDERED_EXPR", "FOLLOWED_BY_EXPR", "FOLLOWED_BY_ITEM", "PATTERN_FILTER_EXPR", "PATTERN_NOT_EXPR", "PATTERN_EVERY_DISTINCT_EXPR", "EVENT_FILTER_EXPR", "EVENT_FILTER_PROPERTY_EXPR", "EVENT_FILTER_PROPERTY_EXPR_ATOM", "PROPERTY_SELECTION_ELEMENT_EXPR", "PROPERTY_SELECTION_STREAM", "PROPERTY_WILDCARD_SELECT", "EVENT_FILTER_IDENT", "EVENT_FILTER_PARAM", "EVENT_FILTER_RANGE", "EVENT_FILTER_NOT_RANGE", "EVENT_FILTER_IN", "EVENT_FILTER_NOT_IN", "EVENT_FILTER_BETWEEN", "EVENT_FILTER_NOT_BETWEEN", "CLASS_IDENT", "GUARD_EXPR", "OBSERVER_EXPR", "VIEW_EXPR", "PATTERN_INCL_EXPR", "DATABASE_JOIN_EXPR", "WHERE_EXPR", "HAVING_EXPR", "EVAL_BITWISE_EXPR", "EVAL_AND_EXPR", "EVAL_OR_EXPR", "EVAL_EQUALS_EXPR", "EVAL_NOTEQUALS_EXPR", "EVAL_IS_EXPR", "EVAL_ISNOT_EXPR", "EVAL_EQUALS_GROUP_EXPR", "EVAL_NOTEQUALS_GROUP_EXPR", "EVAL_IDENT", "SELECTION_EXPR", "SELECTION_ELEMENT_EXPR", "SELECTION_STREAM", "STREAM_EXPR", "OUTERJOIN_EXPR", "INNERJOIN_EXPR", "LEFT_OUTERJOIN_EXPR", "RIGHT_OUTERJOIN_EXPR", "FULL_OUTERJOIN_EXPR", "GROUP_BY_EXPR", "ORDER_BY_EXPR", "ORDER_ELEMENT_EXPR", "EVENT_PROP_EXPR", "EVENT_PROP_SIMPLE", "EVENT_PROP_MAPPED", "EVENT_PROP_INDEXED", "EVENT_PROP_DYNAMIC_SIMPLE", "EVENT_PROP_DYNAMIC_INDEXED", "EVENT_PROP_DYNAMIC_MAPPED", "EVENT_LIMIT_EXPR", "TIMEPERIOD_LIMIT_EXPR", "AFTER_LIMIT_EXPR", "CRONTAB_LIMIT_EXPR", "CRONTAB_LIMIT_EXPR_PARAM", "WHEN_LIMIT_EXPR", "TERM_LIMIT_EXPR", "INSERTINTO_EXPR", "EXPRCOL", "INDEXCOL", "CONCAT", "LIB_FUNCTION", "LIB_FUNC_CHAIN", "DOT_EXPR", "UNARY_MINUS", "TIME_PERIOD", "ARRAY_EXPR", "YEAR_PART", "MONTH_PART", "WEEK_PART", "DAY_PART", "HOUR_PART", "MINUTE_PART", "SECOND_PART", "MILLISECOND_PART", "NOT_IN_SET", "NOT_BETWEEN", "NOT_LIKE", "NOT_REGEXP", "DBSELECT_EXPR", "DBFROM_CLAUSE", "DBWHERE_CLAUSE", "WILDCARD_SELECT", "INSERTINTO_STREAM_NAME", "IN_RANGE", "NOT_IN_RANGE", "SUBSELECT_EXPR", "SUBSELECT_GROUP_EXPR", "EXISTS_SUBSELECT_EXPR", "IN_SUBSELECT_EXPR", "NOT_IN_SUBSELECT_EXPR", "IN_SUBSELECT_QUERY_EXPR", "LAST_OPERATOR", "WEEKDAY_OPERATOR", "SUBSTITUTION", "CAST_EXPR", "CREATE_INDEX_EXPR", "CREATE_WINDOW_EXPR", "CREATE_WINDOW_SELECT_EXPR", "ON_EXPR", "ON_STREAM", "ON_DELETE_EXPR", "ON_SELECT_EXPR", "ON_UPDATE_EXPR", "ON_MERGE_EXPR", "ON_SELECT_INSERT_EXPR", "ON_SELECT_INSERT_OUTPUT", "ON_EXPR_FROM", "ON_SET_EXPR", "CREATE_VARIABLE_EXPR", "METHOD_JOIN_EXPR", "MATCH_UNTIL_EXPR", "MATCH_UNTIL_RANGE_HALFOPEN", "MATCH_UNTIL_RANGE_HALFCLOSED", "MATCH_UNTIL_RANGE_CLOSED", "MATCH_UNTIL_RANGE_BOUNDED", "CREATE_COL_TYPE_LIST", "CREATE_COL_TYPE", "NUMBERSETSTAR", "ANNOTATION", "ANNOTATION_ARRAY", "ANNOTATION_VALUE", "FIRST_AGGREG", "LAST_AGGREG", "WINDOW_AGGREG", "ACCESS_AGG", "UPDATE_EXPR", "ON_SET_EXPR_ITEM", "CREATE_SCHEMA_EXPR", "CREATE_SCHEMA_EXPR_QUAL", "CREATE_SCHEMA_DEF", "VARIANT_LIST", "MERGE_UNM", "MERGE_MAT", "MERGE_UPD", "MERGE_INS", "MERGE_DEL", "NEW_ITEM", "AGG_FILTER_EXPR", "CREATE_EXPR", "CREATE_CTX", "CREATE_CTX_FIXED", "CREATE_CTX_PART", "CREATE_CTX_COAL", "CREATE_CTX_CAT", "CREATE_CTX_INIT", "CREATE_CTX_CATITEM", "CREATE_CTX_NESTED", "CREATE_CTX_PATTERN", "CREATE_DATAFLOW", "GOP", "GOPPARAM", "GOPPARAMITM", "GOPOUT", "GOPOUTITM", "GOPOUTTYP", "GOPCFG", "GOPCFGITM", "GOPCFGEPL", "PARTITIONITEM", "INT_TYPE", "LONG_TYPE", "FLOAT_TYPE", "DOUBLE_TYPE", "STRING_TYPE", "BOOL_TYPE", "NULL_TYPE", "NUM_DOUBLE", "EPL_EXPR", "MATCHREC_PATTERN", "MATCHREC_PATTERN_ATOM", "MATCHREC_PATTERN_CONCAT", "MATCHREC_PATTERN_ALTER", "MATCHREC_PATTERN_NESTED", "MATCHREC_AFTER_SKIP", "MATCHREC_INTERVAL", "MATCHREC_DEFINE", "MATCHREC_DEFINE_ITEM", "MATCHREC_MEASURES", "MATCHREC_MEASURE_ITEM", "JSON_OBJECT", "JSON_ARRAY", "JSON_FIELD", "LBRACK", "RBRACK", "IDENT", "LPAREN", "RPAREN", "COLON", "LCURLY", "RCURLY", "GOES", "ATCHAR", "COMMA", "EQUALS", "DOT", "STAR", "FOLLOWED_BY", "LT", "GT", "QUESTION", "BOR", "PLUS", "STRING_LITERAL", "QUOTED_STRING_LITERAL", "BAND", "BXOR", "SQL_NE", "NOT_EQUAL", "LE", "GE", "LOR", "MINUS", "DIV", "MOD", "NUM_INT", "FOLLOWMAX_BEGIN", "FOLLOWMAX_END", "ESCAPECHAR", "TICKED_STRING_LITERAL", "NUM_LONG", "NUM_FLOAT", "EQUAL", "LNOT", "BNOT", "DIV_ASSIGN", "PLUS_ASSIGN", "INC", "MINUS_ASSIGN", "DEC", "STAR_ASSIGN", "MOD_ASSIGN", "BXOR_ASSIGN", "BOR_ASSIGN", "BAND_ASSIGN", "LAND", "SEMI", "WS", "SL_COMMENT", "ML_COMMENT", "EscapeSequence", "UnicodeEscape", "OctalEscape", "HexDigit", "EXPONENT", "FLOAT_SUFFIX", "GOPCFGEXP", "EVAL_IS_GROUP_EXPR", "EVAL_ISNOT_GROUP_EXPR"
    };
    public static final int FLOAT_SUFFIX=385;
    public static final int GOPCFGITM=297;
    public static final int NUMERIC_PARAM_LIST=131;
    public static final int OUTERJOIN_EXPR=175;
    public static final int CREATE_COL_TYPE_LIST=256;
    public static final int MERGE_INS=275;
    public static final int TIMEPERIOD_MILLISECONDS=105;
    public static final int CREATE_CTX_FIXED=281;
    public static final int RPAREN=327;
    public static final int LNOT=363;
    public static final int INC=367;
    public static final int CREATE=4;
    public static final int STRING_LITERAL=343;
    public static final int STREAM_EXPR=174;
    public static final int MATCHES=116;
    public static final int METADATASQL=68;
    public static final int EVENT_FILTER_PROPERTY_EXPR=140;
    public static final int GOES=331;
    public static final int REGEXP=9;
    public static final int MATCHED=122;
    public static final int INITIATED=127;
    public static final int FOLLOWED_BY_EXPR=134;
    public static final int RBRACK=324;
    public static final int MATCH_UNTIL_RANGE_CLOSED=254;
    public static final int GE=350;
    public static final int ASC=57;
    public static final int IN_SET=6;
    public static final int EVENT_FILTER_EXPR=139;
    public static final int EVENT_FILTER_NOT_IN=150;
    public static final int NUM_DOUBLE=307;
    public static final int TIMEPERIOD_MILLISEC=103;
    public static final int RETAINUNION=64;
    public static final int DBWHERE_CLAUSE=221;
    public static final int MEDIAN=23;
    public static final int GROUP=44;
    public static final int SUBSELECT_GROUP_EXPR=227;
    public static final int YEAR_PART=207;
    public static final int TYPEOF=78;
    public static final int ESCAPECHAR=358;
    public static final int EXPRCOL=198;
    public static final int SL_COMMENT=378;
    public static final int NULL_TYPE=306;
    public static final int MATCH_UNTIL_RANGE_HALFOPEN=252;
    public static final int GT=339;
    public static final int LAND=375;
    public static final int EVENT_PROP_EXPR=183;
    public static final int LBRACK=323;
    public static final int VIEW_EXPR=156;
    public static final int MERGE_UPD=274;
    public static final int CREATE_SCHEMA_DEF=270;
    public static final int EVENT_FILTER_PROPERTY_EXPR_ATOM=141;
    public static final int ON_MERGE_EXPR=244;
    public static final int TIMEPERIOD_SEC=100;
    public static final int ON_SELECT_EXPR=242;
    public static final int TICKED_STRING_LITERAL=359;
    public static final int SUM=18;
    public static final int JSON_ARRAY=321;
    public static final int HexDigit=383;
    public static final int AT=86;
    public static final int AS=17;
    public static final int TIMEPERIOD_MONTH=90;
    public static final int LEFT=38;
    public static final int AVG=19;
    public static final int PREVIOUS=69;
    public static final int PREVIOUSWINDOW=72;
    public static final int PARTITIONITEM=299;
    public static final int DATABASE_JOIN_EXPR=158;
    public static final int IDENT=325;
    public static final int PLUS=342;
    public static final int EVENT_PROP_INDEXED=186;
    public static final int CREATE_SCHEMA_EXPR=268;
    public static final int CREATE_INDEX_EXPR=236;
    public static final int ACCESS_AGG=265;
    public static final int LIKE=8;
    public static final int OUTER=35;
    public static final int RIGHT_OUTERJOIN_EXPR=178;
    public static final int BY=43;
    public static final int MATCHREC_DEFINE=316;
    public static final int MERGE=121;
    public static final int MERGE_UNM=272;
    public static final int FOLLOWMAX_END=357;
    public static final int LEFT_OUTERJOIN_EXPR=177;
    public static final int GROUP_BY_EXPR=180;
    public static final int EPL_EXPR=308;
    public static final int RIGHT=39;
    public static final int HAVING=45;
    public static final int GOPOUTITM=294;
    public static final int MINUS=352;
    public static final int SEMI=376;
    public static final int INDEXCOL=199;
    public static final int STAR_ASSIGN=370;
    public static final int FIRST_AGGREG=262;
    public static final int COLON=328;
    public static final int PREVIOUSTAIL=70;
    public static final int NOT_IN_SET=215;
    public static final int VALUE_NULL=108;
    public static final int EVENT_PROP_DYNAMIC_SIMPLE=187;
    public static final int NOT_IN_SUBSELECT_EXPR=230;
    public static final int GUARD_EXPR=154;
    public static final int RCURLY=330;
    public static final int EXISTS_SUBSELECT_EXPR=228;
    public static final int WEEK_PART=209;
    public static final int ROW_LIMIT_EXPR=109;
    public static final int SELECTION_EXPR=171;
    public static final int EVAL_IS_GROUP_EXPR=387;
    public static final int LW=76;
    public static final int LT=338;
    public static final int CREATE_CTX=280;
    public static final int ORDER_BY_EXPR=181;
    public static final int NEW_ITEM=277;
    public static final int MOD_ASSIGN=371;
    public static final int IN_SUBSELECT_QUERY_EXPR=231;
    public static final int JSON_FIELD=322;
    public static final int EQUALS=334;
    public static final int COUNT=26;
    public static final int RETAININTERSECTION=65;
    public static final int TERMINATED=128;
    public static final int TIMEPERIOD_WEEKS=93;
    public static final int PATTERN=66;
    public static final int MATCHREC_AFTER_SKIP=314;
    public static final int ESCAPE=10;
    public static final int EVAL_NOTEQUALS_GROUP_EXPR=169;
    public static final int SELECT=27;
    public static final int INTO=55;
    public static final int EVAL_ISNOT_EXPR=167;
    public static final int FLOAT_TYPE=302;
    public static final int COALESCE=22;
    public static final int EVENT_FILTER_BETWEEN=151;
    public static final int ANNOTATION_VALUE=261;
    public static final int CLASS_IDENT=153;
    public static final int MATCHREC_PATTERN_ALTER=312;
    public static final int CREATE_WINDOW_EXPR=237;
    public static final int PROPERTY_SELECTION_STREAM=143;
    public static final int ON_DELETE_EXPR=241;
    public static final int ON=41;
    public static final int DELETE=81;
    public static final int INT_TYPE=300;
    public static final int EVERY_EXPR=14;
    public static final int EVAL_BITWISE_EXPR=161;
    public static final int TIMEPERIOD_HOURS=97;
    public static final int STRING_TYPE=304;
    public static final int MATCHREC_DEFINE_ITEM=317;
    public static final int STDDEV=24;
    public static final int OUTPUT=50;
    public static final int WEEKDAY_OPERATOR=233;
    public static final int DEC=369;
    public static final int WHERE=16;
    public static final int GOPOUT=293;
    public static final int BXOR_ASSIGN=372;
    public static final int AFTER_LIMIT_EXPR=192;
    public static final int SNAPSHOT=82;
    public static final int MAX=20;
    public static final int DEFINE=114;
    public static final int TIMEPERIOD_YEARS=89;
    public static final int TIMEPERIOD_DAYS=95;
    public static final int CONTEXT=126;
    public static final int CREATE_CTX_CAT=284;
    public static final int EVENT_PROP_DYNAMIC_INDEXED=188;
    public static final int BOR_ASSIGN=373;
    public static final int COMMA=333;
    public static final int WHEN_LIMIT_EXPR=195;
    public static final int IS=42;
    public static final int PARTITION=115;
    public static final int SOME=49;
    public static final int EQUAL=362;
    public static final int MATCHREC_MEASURE_ITEM=319;
    public static final int EVENT_FILTER_NOT_BETWEEN=152;
    public static final int IN_RANGE=224;
    public static final int TIMEPERIOD_WEEK=92;
    public static final int PROPERTY_WILDCARD_SELECT=144;
    public static final int INSERTINTO_EXPR=197;
    public static final int UNIDIRECTIONAL=63;
    public static final int MATCH_UNTIL_RANGE_BOUNDED=255;
    public static final int TIMEPERIOD_MINUTES=99;
    public static final int RSTREAM=59;
    public static final int NOT_BETWEEN=216;
    public static final int TIMEPERIOD_MINUTE=98;
    public static final int EVAL_OR_EXPR=163;
    public static final int BAND=345;
    public static final int MATCHREC_PATTERN_ATOM=310;
    public static final int QUOTED_STRING_LITERAL=344;
    public static final int NOT_EXPR=13;
    public static final int QUESTION=340;
    public static final int EVENT_FILTER_IDENT=145;
    public static final int UnicodeEscape=381;
    public static final int DBSELECT_EXPR=219;
    public static final int FOLLOWMAX_BEGIN=356;
    public static final int WINDOW=5;
    public static final int ON_SET_EXPR_ITEM=267;
    public static final int DBFROM_CLAUSE=220;
    public static final int LE=349;
    public static final int EVAL_IDENT=170;
    public static final int CRONTAB_LIMIT_EXPR=193;
    public static final int STAR=336;
    public static final int DOT_EXPR=203;
    public static final int ISTREAM=60;
    public static final int MOD=354;
    public static final int LIB_FUNC_CHAIN=202;
    public static final int EVAL_ISNOT_GROUP_EXPR=388;
    public static final int MONTH_PART=208;
    public static final int EOF=-1;
    public static final int LIB_FUNCTION=201;
    public static final int FULL_OUTERJOIN_EXPR=179;
    public static final int CREATE_CTX_NESTED=287;
    public static final int MATCHREC_PATTERN_CONCAT=311;
    public static final int USING=120;
    public static final int CAST_EXPR=235;
    public static final int TIMEPERIOD_SECONDS=102;
    public static final int NOT_EQUAL=348;
    public static final int LAST_AGGREG=263;
    public static final int NEWKW=124;
    public static final int HOUR_PART=211;
    public static final int FOLLOWED_BY=337;
    public static final int MATCHREC_PATTERN_NESTED=313;
    public static final int GOPPARAMITM=292;
    public static final int METHOD_JOIN_EXPR=250;
    public static final int CREATE_CTX_PART=282;
    public static final int PATTERN_EVERY_DISTINCT_EXPR=138;
    public static final int CREATE_CTX_COAL=283;
    public static final int ELSE=30;
    public static final int MINUS_ASSIGN=368;
    public static final int INSERTINTO_STREAM_NAME=223;
    public static final int UNARY_MINUS=204;
    public static final int LCURLY=329;
    public static final int EVENTS=51;
    public static final int AND_EXPR=12;
    public static final int EVENT_FILTER_NOT_RANGE=148;
    public static final int WS=377;
    public static final int FOLLOWED_BY_ITEM=135;
    public static final int ON_SELECT_INSERT_EXPR=245;
    public static final int GOPPARAM=291;
    public static final int BNOT=364;
    public static final int EVAL_IS_EXPR=166;
    public static final int WHERE_EXPR=159;
    public static final int END=33;
    public static final int INNERJOIN_EXPR=176;
    public static final int TERM_LIMIT_EXPR=196;
    public static final int NOT_REGEXP=218;
    public static final int MATCH_UNTIL_EXPR=251;
    public static final int ANNOTATION=259;
    public static final int LONG_TYPE=301;
    public static final int MATCHREC_PATTERN=309;
    public static final int ATCHAR=332;
    public static final int MINUTE_PART=212;
    public static final int PATTERN_NOT_EXPR=137;
    public static final int SQL_NE=347;
    public static final int UPDATE_EXPR=266;
    public static final int LPAREN=326;
    public static final int IN_SUBSELECT_EXPR=229;
    public static final int BOOLEAN_TRUE=106;
    public static final int OR_EXPR=11;
    public static final int JSON_OBJECT=320;
    public static final int THEN=32;
    public static final int NOT_IN_RANGE=225;
    public static final int MATCHREC_INTERVAL=315;
    public static final int OFFSET=110;
    public static final int SECOND_PART=213;
    public static final int MATCH_RECOGNIZE=112;
    public static final int CASE2=29;
    public static final int BXOR=346;
    public static final int TIMEPERIOD_DAY=94;
    public static final int MERGE_MAT=273;
    public static final int EXISTS=74;
    public static final int TIMEPERIOD_MILLISECOND=104;
    public static final int EVAL_NOTEQUALS_EXPR=165;
    public static final int CREATE_CTX_CATITEM=286;
    public static final int CREATE_VARIABLE_EXPR=249;
    public static final int MATCH_UNTIL_RANGE_HALFCLOSED=253;
    public static final int PATTERN_FILTER_EXPR=136;
    public static final int LAST_OPERATOR=232;
    public static final int NUMBERSETSTAR=258;
    public static final int EVAL_AND_EXPR=162;
    public static final int SET=83;
    public static final int INSTANCEOF=77;
    public static final int EVENT_PROP_SIMPLE=184;
    public static final int MIN=21;
    public static final int PREVIOUSCOUNT=71;
    public static final int VARIANT_LIST=271;
    public static final int EVAL_EQUALS_GROUP_EXPR=168;
    public static final int SCHEMA=62;
    public static final int BAND_ASSIGN=374;
    public static final int CRONTAB_LIMIT_EXPR_PARAM=194;
    public static final int WHEN=31;
    public static final int PLUS_ASSIGN=366;
    public static final int DAY_PART=210;
    public static final int START=125;
    public static final int EVENT_FILTER_IN=149;
    public static final int DIV=353;
    public static final int GOPCFGEXP=386;
    public static final int OBJECT_PARAM_ORDERED_EXPR=133;
    public static final int EXPRESSIONDECL=123;
    public static final int OctalEscape=382;
    public static final int BETWEEN=7;
    public static final int MILLISECOND_PART=214;
    public static final int FIRST=52;
    public static final int PRIOR=73;
    public static final int CAST=79;
    public static final int LOR=351;
    public static final int WILDCARD_SELECT=222;
    public static final int EXPONENT=384;
    public static final int PATTERN_INCL_EXPR=157;
    public static final int WHILE=119;
    public static final int BOOL_TYPE=305;
    public static final int GOPCFG=296;
    public static final int ANNOTATION_ARRAY=260;
    public static final int CASE=28;
    public static final int CREATE_EXPR=279;
    public static final int GOP=290;
    public static final int WINDOW_AGGREG=264;
    public static final int DIV_ASSIGN=365;
    public static final int CREATE_CTX_INIT=285;
    public static final int SQL=67;
    public static final int FULL=40;
    public static final int WEEKDAY=75;
    public static final int INSERT=54;
    public static final int ON_UPDATE_EXPR=243;
    public static final int ARRAY_EXPR=206;
    public static final int CREATE_COL_TYPE=257;
    public static final int LAST=53;
    public static final int BOOLEAN_FALSE=107;
    public static final int TIMEPERIOD_SECOND=101;
    public static final int SUBSELECT_EXPR=226;
    public static final int NUMERIC_PARAM_RANGE=130;
    public static final int CONCAT=200;
    public static final int ON_EXPR=239;
    public static final int NUM_LONG=360;
    public static final int TIME_PERIOD=205;
    public static final int DOUBLE_TYPE=303;
    public static final int ORDER_ELEMENT_EXPR=182;
    public static final int VARIABLE=84;
    public static final int SUBSTITUTION=234;
    public static final int UNTIL=85;
    public static final int ON_SET_EXPR=248;
    public static final int NUM_INT=355;
    public static final int ON_EXPR_FROM=247;
    public static final int NUM_FLOAT=361;
    public static final int FROM=34;
    public static final int DISTINCT=46;
    public static final int EscapeSequence=380;
    public static final int PROPERTY_SELECTION_ELEMENT_EXPR=142;
    public static final int INNER=36;
    public static final int NUMERIC_PARAM_FREQUENCY=132;
    public static final int ORDER=56;
    public static final int EVENT_FILTER_PARAM=146;
    public static final int EVENT_PROP_DYNAMIC_MAPPED=189;
    public static final int IRSTREAM=61;
    public static final int UPDATE=111;
    public static final int FOR=118;
    public static final int ON_STREAM=240;
    public static final int EVENT_FILTER_RANGE=147;
    public static final int INDEX=87;
    public static final int ML_COMMENT=379;
    public static final int TIMEPERIOD_LIMIT_EXPR=191;
    public static final int TIMEPERIOD_HOUR=96;
    public static final int ALL=47;
    public static final int BOR=341;
    public static final int DOT=335;
    public static final int CURRENT_TIMESTAMP=80;
    public static final int MATCHREC_MEASURES=318;
    public static final int EVERY_DISTINCT_EXPR=15;
    public static final int HAVING_EXPR=160;
    public static final int MERGE_DEL=276;
    public static final int EVAL_EQUALS_EXPR=164;
    public static final int NOT_LIKE=217;
    public static final int EVENT_LIMIT_EXPR=190;
    public static final int ON_SELECT_INSERT_OUTPUT=246;
    public static final int CREATE_DATAFLOW=289;
    public static final int AFTER=117;
    public static final int MEASURES=113;
    public static final int AGG_FILTER_EXPR=278;
    public static final int CREATE_CTX_PATTERN=288;
    public static final int JOIN=37;
    public static final int GOPOUTTYP=295;
    public static final int ANY=48;
    public static final int OBSERVER_EXPR=155;
    public static final int CREATE_SCHEMA_EXPR_QUAL=269;
    public static final int EVENT_PROP_MAPPED=185;
    public static final int TIMEPERIOD_YEAR=88;
    public static final int AVEDEV=25;
    public static final int GOPCFGEPL=298;
    public static final int TIMEPERIOD_MONTHS=91;
    public static final int SELECTION_ELEMENT_EXPR=172;
    public static final int CREATE_WINDOW_SELECT_EXPR=238;
    public static final int DESC=58;
    public static final int DATAFLOW=129;
    public static final int SELECTION_STREAM=173;

    // delegates
    // delegators


        public EsperEPL2Ast(TreeNodeStream input) {
            this(input, new RecognizerSharedState());
        }
        public EsperEPL2Ast(TreeNodeStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        

    public String[] getTokenNames() { return EsperEPL2Ast.tokenNames; }
    public String getGrammarFileName() { return "EsperEPL2Ast.g"; }


      private static Log log = LogFactory.getLog(EsperEPL2Ast.class);

      // For pattern processing within EPL
      protected void endPattern() {};

      protected void pushStmtContext() {};
      protected void leaveNode(Tree node) {};
      protected void end() {};

      protected void mismatch(IntStream input, int ttype, BitSet follow) throws RecognitionException {
        throw new MismatchedTokenException(ttype, input);  
      }

      public void recoverFromMismatchedToken(IntStream intStream, RecognitionException recognitionException, int i, BitSet bitSet) throws RecognitionException {
        throw recognitionException;
      }

      public Object recoverFromMismatchedSet(IntStream intStream, RecognitionException recognitionException, BitSet bitSet) throws RecognitionException {
        throw recognitionException;
      }

      protected boolean recoverFromMismatchedElement(IntStream intStream, RecognitionException recognitionException, BitSet bitSet) {
        throw new RuntimeException("Error recovering from mismatched element", recognitionException);
      }
      
      public void recover(org.antlr.runtime.IntStream intStream, org.antlr.runtime.RecognitionException recognitionException) {
        throw new RuntimeException("Error recovering from recognition exception", recognitionException);
      }



    // $ANTLR start "annotation"
    // EsperEPL2Ast.g:57:1: annotation[boolean isLeaveNode] : ^(a= ANNOTATION CLASS_IDENT ( elementValuePair )* ( elementValue )? ) ;
    public final void annotation(boolean isLeaveNode) throws RecognitionException {
        CommonTree a=null;

        try {
            // EsperEPL2Ast.g:58:2: ( ^(a= ANNOTATION CLASS_IDENT ( elementValuePair )* ( elementValue )? ) )
            // EsperEPL2Ast.g:58:4: ^(a= ANNOTATION CLASS_IDENT ( elementValuePair )* ( elementValue )? )
            {
            a=(CommonTree)match(input,ANNOTATION,FOLLOW_ANNOTATION_in_annotation92); 

            match(input, Token.DOWN, null); 
            match(input,CLASS_IDENT,FOLLOW_CLASS_IDENT_in_annotation94); 
            // EsperEPL2Ast.g:58:31: ( elementValuePair )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0==ANNOTATION_VALUE) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // EsperEPL2Ast.g:58:31: elementValuePair
            	    {
            	    pushFollow(FOLLOW_elementValuePair_in_annotation96);
            	    elementValuePair();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop1;
                }
            } while (true);

            // EsperEPL2Ast.g:58:49: ( elementValue )?
            int alt2=2;
            int LA2_0 = input.LA(1);

            if ( (LA2_0==CLASS_IDENT||(LA2_0>=ANNOTATION && LA2_0<=ANNOTATION_ARRAY)||(LA2_0>=INT_TYPE && LA2_0<=NULL_TYPE)) ) {
                alt2=1;
            }
            switch (alt2) {
                case 1 :
                    // EsperEPL2Ast.g:58:49: elementValue
                    {
                    pushFollow(FOLLOW_elementValue_in_annotation99);
                    elementValue();

                    state._fsp--;


                    }
                    break;

            }


            match(input, Token.UP, null); 
             if (isLeaveNode) leaveNode(a); 

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "annotation"


    // $ANTLR start "elementValuePair"
    // EsperEPL2Ast.g:61:1: elementValuePair : ^(a= ANNOTATION_VALUE IDENT elementValue ) ;
    public final void elementValuePair() throws RecognitionException {
        CommonTree a=null;

        try {
            // EsperEPL2Ast.g:62:2: ( ^(a= ANNOTATION_VALUE IDENT elementValue ) )
            // EsperEPL2Ast.g:62:4: ^(a= ANNOTATION_VALUE IDENT elementValue )
            {
            a=(CommonTree)match(input,ANNOTATION_VALUE,FOLLOW_ANNOTATION_VALUE_in_elementValuePair117); 

            match(input, Token.DOWN, null); 
            match(input,IDENT,FOLLOW_IDENT_in_elementValuePair119); 
            pushFollow(FOLLOW_elementValue_in_elementValuePair121);
            elementValue();

            state._fsp--;


            match(input, Token.UP, null); 

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "elementValuePair"


    // $ANTLR start "elementValue"
    // EsperEPL2Ast.g:65:1: elementValue : ( annotation[false] | ^( ANNOTATION_ARRAY ( elementValue )* ) | constant[false] | CLASS_IDENT );
    public final void elementValue() throws RecognitionException {
        try {
            // EsperEPL2Ast.g:66:6: ( annotation[false] | ^( ANNOTATION_ARRAY ( elementValue )* ) | constant[false] | CLASS_IDENT )
            int alt4=4;
            switch ( input.LA(1) ) {
            case ANNOTATION:
                {
                alt4=1;
                }
                break;
            case ANNOTATION_ARRAY:
                {
                alt4=2;
                }
                break;
            case INT_TYPE:
            case LONG_TYPE:
            case FLOAT_TYPE:
            case DOUBLE_TYPE:
            case STRING_TYPE:
            case BOOL_TYPE:
            case NULL_TYPE:
                {
                alt4=3;
                }
                break;
            case CLASS_IDENT:
                {
                alt4=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 4, 0, input);

                throw nvae;
            }

            switch (alt4) {
                case 1 :
                    // EsperEPL2Ast.g:66:11: annotation[false]
                    {
                    pushFollow(FOLLOW_annotation_in_elementValue148);
                    annotation(false);

                    state._fsp--;


                    }
                    break;
                case 2 :
                    // EsperEPL2Ast.g:67:5: ^( ANNOTATION_ARRAY ( elementValue )* )
                    {
                    match(input,ANNOTATION_ARRAY,FOLLOW_ANNOTATION_ARRAY_in_elementValue156); 

                    if ( input.LA(1)==Token.DOWN ) {
                        match(input, Token.DOWN, null); 
                        // EsperEPL2Ast.g:67:24: ( elementValue )*
                        loop3:
                        do {
                            int alt3=2;
                            int LA3_0 = input.LA(1);

                            if ( (LA3_0==CLASS_IDENT||(LA3_0>=ANNOTATION && LA3_0<=ANNOTATION_ARRAY)||(LA3_0>=INT_TYPE && LA3_0<=NULL_TYPE)) ) {
                                alt3=1;
                            }


                            switch (alt3) {
                        	case 1 :
                        	    // EsperEPL2Ast.g:67:24: elementValue
                        	    {
                        	    pushFollow(FOLLOW_elementValue_in_elementValue158);
                        	    elementValue();

                        	    state._fsp--;


                        	    }
                        	    break;

                        	default :
                        	    break loop3;
                            }
                        } while (true);


                        match(input, Token.UP, null); 
                    }

                    }
                    break;
                case 3 :
                    // EsperEPL2Ast.g:68:8: constant[false]
                    {
                    pushFollow(FOLLOW_constant_in_elementValue169);
                    constant(false);

                    state._fsp--;


                    }
                    break;
                case 4 :
                    // EsperEPL2Ast.g:69:8: CLASS_IDENT
                    {
                    match(input,CLASS_IDENT,FOLLOW_CLASS_IDENT_in_elementValue179); 

                    }
                    break;

            }
        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "elementValue"


    // $ANTLR start "expressionDecl"
    // EsperEPL2Ast.g:75:1: expressionDecl[boolean isLeaveNode] : ^(e= EXPRESSIONDECL IDENT expressionDef ( exprCol )? ( CLASS_IDENT )? ( ^( COLON IDENT ) )? ( LBRACK )? ) ;
    public final void expressionDecl(boolean isLeaveNode) throws RecognitionException {
        CommonTree e=null;

        try {
            // EsperEPL2Ast.g:76:2: ( ^(e= EXPRESSIONDECL IDENT expressionDef ( exprCol )? ( CLASS_IDENT )? ( ^( COLON IDENT ) )? ( LBRACK )? ) )
            // EsperEPL2Ast.g:76:4: ^(e= EXPRESSIONDECL IDENT expressionDef ( exprCol )? ( CLASS_IDENT )? ( ^( COLON IDENT ) )? ( LBRACK )? )
            {
            e=(CommonTree)match(input,EXPRESSIONDECL,FOLLOW_EXPRESSIONDECL_in_expressionDecl205); 

            match(input, Token.DOWN, null); 
            match(input,IDENT,FOLLOW_IDENT_in_expressionDecl207); 
            pushFollow(FOLLOW_expressionDef_in_expressionDecl209);
            expressionDef();

            state._fsp--;

            // EsperEPL2Ast.g:76:43: ( exprCol )?
            int alt5=2;
            int LA5_0 = input.LA(1);

            if ( (LA5_0==EXPRCOL) ) {
                alt5=1;
            }
            switch (alt5) {
                case 1 :
                    // EsperEPL2Ast.g:76:43: exprCol
                    {
                    pushFollow(FOLLOW_exprCol_in_expressionDecl211);
                    exprCol();

                    state._fsp--;


                    }
                    break;

            }

            // EsperEPL2Ast.g:76:52: ( CLASS_IDENT )?
            int alt6=2;
            int LA6_0 = input.LA(1);

            if ( (LA6_0==CLASS_IDENT) ) {
                alt6=1;
            }
            switch (alt6) {
                case 1 :
                    // EsperEPL2Ast.g:76:52: CLASS_IDENT
                    {
                    match(input,CLASS_IDENT,FOLLOW_CLASS_IDENT_in_expressionDecl214); 

                    }
                    break;

            }

            // EsperEPL2Ast.g:76:65: ( ^( COLON IDENT ) )?
            int alt7=2;
            int LA7_0 = input.LA(1);

            if ( (LA7_0==COLON) ) {
                alt7=1;
            }
            switch (alt7) {
                case 1 :
                    // EsperEPL2Ast.g:76:66: ^( COLON IDENT )
                    {
                    match(input,COLON,FOLLOW_COLON_in_expressionDecl219); 

                    match(input, Token.DOWN, null); 
                    match(input,IDENT,FOLLOW_IDENT_in_expressionDecl221); 

                    match(input, Token.UP, null); 

                    }
                    break;

            }

            // EsperEPL2Ast.g:76:83: ( LBRACK )?
            int alt8=2;
            int LA8_0 = input.LA(1);

            if ( (LA8_0==LBRACK) ) {
                alt8=1;
            }
            switch (alt8) {
                case 1 :
                    // EsperEPL2Ast.g:76:83: LBRACK
                    {
                    match(input,LBRACK,FOLLOW_LBRACK_in_expressionDecl226); 

                    }
                    break;

            }


            match(input, Token.UP, null); 
             if (isLeaveNode) leaveNode(e); 

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "expressionDecl"


    // $ANTLR start "expressionDef"
    // EsperEPL2Ast.g:79:1: expressionDef : ( ^( GOES valueExpr ( expressionLambdaDecl )? ) | ^( EXPRESSIONDECL constant[false] ) );
    public final void expressionDef() throws RecognitionException {
        try {
            // EsperEPL2Ast.g:80:2: ( ^( GOES valueExpr ( expressionLambdaDecl )? ) | ^( EXPRESSIONDECL constant[false] ) )
            int alt10=2;
            int LA10_0 = input.LA(1);

            if ( (LA10_0==GOES) ) {
                alt10=1;
            }
            else if ( (LA10_0==EXPRESSIONDECL) ) {
                alt10=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 10, 0, input);

                throw nvae;
            }
            switch (alt10) {
                case 1 :
                    // EsperEPL2Ast.g:80:4: ^( GOES valueExpr ( expressionLambdaDecl )? )
                    {
                    match(input,GOES,FOLLOW_GOES_in_expressionDef243); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_valueExpr_in_expressionDef245);
                    valueExpr();

                    state._fsp--;

                    // EsperEPL2Ast.g:80:21: ( expressionLambdaDecl )?
                    int alt9=2;
                    int LA9_0 = input.LA(1);

                    if ( (LA9_0==GOES) ) {
                        alt9=1;
                    }
                    switch (alt9) {
                        case 1 :
                            // EsperEPL2Ast.g:80:21: expressionLambdaDecl
                            {
                            pushFollow(FOLLOW_expressionLambdaDecl_in_expressionDef247);
                            expressionLambdaDecl();

                            state._fsp--;


                            }
                            break;

                    }


                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    // EsperEPL2Ast.g:81:4: ^( EXPRESSIONDECL constant[false] )
                    {
                    match(input,EXPRESSIONDECL,FOLLOW_EXPRESSIONDECL_in_expressionDef255); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_constant_in_expressionDef257);
                    constant(false);

                    state._fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;

            }
        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "expressionDef"


    // $ANTLR start "expressionLambdaDecl"
    // EsperEPL2Ast.g:84:1: expressionLambdaDecl : ^( GOES ( IDENT | exprCol ) ) ;
    public final void expressionLambdaDecl() throws RecognitionException {
        try {
            // EsperEPL2Ast.g:85:2: ( ^( GOES ( IDENT | exprCol ) ) )
            // EsperEPL2Ast.g:85:4: ^( GOES ( IDENT | exprCol ) )
            {
            match(input,GOES,FOLLOW_GOES_in_expressionLambdaDecl271); 

            match(input, Token.DOWN, null); 
            // EsperEPL2Ast.g:85:11: ( IDENT | exprCol )
            int alt11=2;
            int LA11_0 = input.LA(1);

            if ( (LA11_0==IDENT) ) {
                alt11=1;
            }
            else if ( (LA11_0==EXPRCOL) ) {
                alt11=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 11, 0, input);

                throw nvae;
            }
            switch (alt11) {
                case 1 :
                    // EsperEPL2Ast.g:85:12: IDENT
                    {
                    match(input,IDENT,FOLLOW_IDENT_in_expressionLambdaDecl274); 

                    }
                    break;
                case 2 :
                    // EsperEPL2Ast.g:85:20: exprCol
                    {
                    pushFollow(FOLLOW_exprCol_in_expressionLambdaDecl278);
                    exprCol();

                    state._fsp--;


                    }
                    break;

            }


            match(input, Token.UP, null); 

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "expressionLambdaDecl"


    // $ANTLR start "startEPLExpressionRule"
    // EsperEPL2Ast.g:91:1: startEPLExpressionRule : ^( EPL_EXPR ( annotation[true] | expressionDecl[true] )* eplExpressionRule ) ;
    public final void startEPLExpressionRule() throws RecognitionException {
        try {
            // EsperEPL2Ast.g:92:2: ( ^( EPL_EXPR ( annotation[true] | expressionDecl[true] )* eplExpressionRule ) )
            // EsperEPL2Ast.g:92:4: ^( EPL_EXPR ( annotation[true] | expressionDecl[true] )* eplExpressionRule )
            {
            match(input,EPL_EXPR,FOLLOW_EPL_EXPR_in_startEPLExpressionRule295); 

            match(input, Token.DOWN, null); 
            // EsperEPL2Ast.g:92:15: ( annotation[true] | expressionDecl[true] )*
            loop12:
            do {
                int alt12=3;
                int LA12_0 = input.LA(1);

                if ( (LA12_0==ANNOTATION) ) {
                    alt12=1;
                }
                else if ( (LA12_0==EXPRESSIONDECL) ) {
                    alt12=2;
                }


                switch (alt12) {
            	case 1 :
            	    // EsperEPL2Ast.g:92:16: annotation[true]
            	    {
            	    pushFollow(FOLLOW_annotation_in_startEPLExpressionRule298);
            	    annotation(true);

            	    state._fsp--;


            	    }
            	    break;
            	case 2 :
            	    // EsperEPL2Ast.g:92:35: expressionDecl[true]
            	    {
            	    pushFollow(FOLLOW_expressionDecl_in_startEPLExpressionRule303);
            	    expressionDecl(true);

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop12;
                }
            } while (true);

            pushFollow(FOLLOW_eplExpressionRule_in_startEPLExpressionRule308);
            eplExpressionRule();

            state._fsp--;


            match(input, Token.UP, null); 
             end(); 

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "startEPLExpressionRule"


    // $ANTLR start "eplExpressionRule"
    // EsperEPL2Ast.g:95:1: eplExpressionRule : ( ( ( contextExpr )? ( selectExpr | createWindowExpr | createIndexExpr | createVariableExpr | createSchemaExpr[true] | onExpr | updateExpr | createDataflow ) ( forExpr )? ) | createContextExpr | createExpr );
    public final void eplExpressionRule() throws RecognitionException {
        try {
            // EsperEPL2Ast.g:96:2: ( ( ( contextExpr )? ( selectExpr | createWindowExpr | createIndexExpr | createVariableExpr | createSchemaExpr[true] | onExpr | updateExpr | createDataflow ) ( forExpr )? ) | createContextExpr | createExpr )
            int alt16=3;
            switch ( input.LA(1) ) {
            case CONTEXT:
            case SELECTION_EXPR:
            case INSERTINTO_EXPR:
            case CREATE_INDEX_EXPR:
            case CREATE_WINDOW_EXPR:
            case ON_EXPR:
            case CREATE_VARIABLE_EXPR:
            case UPDATE_EXPR:
            case CREATE_SCHEMA_EXPR:
            case CREATE_DATAFLOW:
                {
                alt16=1;
                }
                break;
            case CREATE_CTX:
                {
                alt16=2;
                }
                break;
            case CREATE_EXPR:
                {
                alt16=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 16, 0, input);

                throw nvae;
            }

            switch (alt16) {
                case 1 :
                    // EsperEPL2Ast.g:96:4: ( ( contextExpr )? ( selectExpr | createWindowExpr | createIndexExpr | createVariableExpr | createSchemaExpr[true] | onExpr | updateExpr | createDataflow ) ( forExpr )? )
                    {
                    // EsperEPL2Ast.g:96:4: ( ( contextExpr )? ( selectExpr | createWindowExpr | createIndexExpr | createVariableExpr | createSchemaExpr[true] | onExpr | updateExpr | createDataflow ) ( forExpr )? )
                    // EsperEPL2Ast.g:96:5: ( contextExpr )? ( selectExpr | createWindowExpr | createIndexExpr | createVariableExpr | createSchemaExpr[true] | onExpr | updateExpr | createDataflow ) ( forExpr )?
                    {
                    // EsperEPL2Ast.g:96:5: ( contextExpr )?
                    int alt13=2;
                    int LA13_0 = input.LA(1);

                    if ( (LA13_0==CONTEXT) ) {
                        alt13=1;
                    }
                    switch (alt13) {
                        case 1 :
                            // EsperEPL2Ast.g:96:5: contextExpr
                            {
                            pushFollow(FOLLOW_contextExpr_in_eplExpressionRule325);
                            contextExpr();

                            state._fsp--;


                            }
                            break;

                    }

                    // EsperEPL2Ast.g:96:18: ( selectExpr | createWindowExpr | createIndexExpr | createVariableExpr | createSchemaExpr[true] | onExpr | updateExpr | createDataflow )
                    int alt14=8;
                    switch ( input.LA(1) ) {
                    case SELECTION_EXPR:
                    case INSERTINTO_EXPR:
                        {
                        alt14=1;
                        }
                        break;
                    case CREATE_WINDOW_EXPR:
                        {
                        alt14=2;
                        }
                        break;
                    case CREATE_INDEX_EXPR:
                        {
                        alt14=3;
                        }
                        break;
                    case CREATE_VARIABLE_EXPR:
                        {
                        alt14=4;
                        }
                        break;
                    case CREATE_SCHEMA_EXPR:
                        {
                        alt14=5;
                        }
                        break;
                    case ON_EXPR:
                        {
                        alt14=6;
                        }
                        break;
                    case UPDATE_EXPR:
                        {
                        alt14=7;
                        }
                        break;
                    case CREATE_DATAFLOW:
                        {
                        alt14=8;
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("", 14, 0, input);

                        throw nvae;
                    }

                    switch (alt14) {
                        case 1 :
                            // EsperEPL2Ast.g:96:19: selectExpr
                            {
                            pushFollow(FOLLOW_selectExpr_in_eplExpressionRule329);
                            selectExpr();

                            state._fsp--;


                            }
                            break;
                        case 2 :
                            // EsperEPL2Ast.g:96:32: createWindowExpr
                            {
                            pushFollow(FOLLOW_createWindowExpr_in_eplExpressionRule333);
                            createWindowExpr();

                            state._fsp--;


                            }
                            break;
                        case 3 :
                            // EsperEPL2Ast.g:96:51: createIndexExpr
                            {
                            pushFollow(FOLLOW_createIndexExpr_in_eplExpressionRule337);
                            createIndexExpr();

                            state._fsp--;


                            }
                            break;
                        case 4 :
                            // EsperEPL2Ast.g:96:69: createVariableExpr
                            {
                            pushFollow(FOLLOW_createVariableExpr_in_eplExpressionRule341);
                            createVariableExpr();

                            state._fsp--;


                            }
                            break;
                        case 5 :
                            // EsperEPL2Ast.g:96:90: createSchemaExpr[true]
                            {
                            pushFollow(FOLLOW_createSchemaExpr_in_eplExpressionRule345);
                            createSchemaExpr(true);

                            state._fsp--;


                            }
                            break;
                        case 6 :
                            // EsperEPL2Ast.g:96:115: onExpr
                            {
                            pushFollow(FOLLOW_onExpr_in_eplExpressionRule350);
                            onExpr();

                            state._fsp--;


                            }
                            break;
                        case 7 :
                            // EsperEPL2Ast.g:96:124: updateExpr
                            {
                            pushFollow(FOLLOW_updateExpr_in_eplExpressionRule354);
                            updateExpr();

                            state._fsp--;


                            }
                            break;
                        case 8 :
                            // EsperEPL2Ast.g:96:137: createDataflow
                            {
                            pushFollow(FOLLOW_createDataflow_in_eplExpressionRule358);
                            createDataflow();

                            state._fsp--;


                            }
                            break;

                    }

                    // EsperEPL2Ast.g:96:153: ( forExpr )?
                    int alt15=2;
                    int LA15_0 = input.LA(1);

                    if ( (LA15_0==FOR) ) {
                        alt15=1;
                    }
                    switch (alt15) {
                        case 1 :
                            // EsperEPL2Ast.g:96:153: forExpr
                            {
                            pushFollow(FOLLOW_forExpr_in_eplExpressionRule361);
                            forExpr();

                            state._fsp--;


                            }
                            break;

                    }


                    }


                    }
                    break;
                case 2 :
                    // EsperEPL2Ast.g:97:4: createContextExpr
                    {
                    pushFollow(FOLLOW_createContextExpr_in_eplExpressionRule368);
                    createContextExpr();

                    state._fsp--;


                    }
                    break;
                case 3 :
                    // EsperEPL2Ast.g:97:24: createExpr
                    {
                    pushFollow(FOLLOW_createExpr_in_eplExpressionRule372);
                    createExpr();

                    state._fsp--;


                    }
                    break;

            }
        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "eplExpressionRule"


    // $ANTLR start "contextExpr"
    // EsperEPL2Ast.g:100:1: contextExpr : ^(i= CONTEXT IDENT ) ;
    public final void contextExpr() throws RecognitionException {
        CommonTree i=null;

        try {
            // EsperEPL2Ast.g:101:2: ( ^(i= CONTEXT IDENT ) )
            // EsperEPL2Ast.g:101:4: ^(i= CONTEXT IDENT )
            {
            i=(CommonTree)match(input,CONTEXT,FOLLOW_CONTEXT_in_contextExpr387); 

            match(input, Token.DOWN, null); 
            match(input,IDENT,FOLLOW_IDENT_in_contextExpr389); 
             leaveNode(i); 

            match(input, Token.UP, null); 

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "contextExpr"


    // $ANTLR start "onExpr"
    // EsperEPL2Ast.g:104:1: onExpr : ^(i= ON_EXPR onStreamExpr ( onDeleteExpr | onUpdateExpr | onSelectExpr ( ( onSelectInsertExpr )+ ( onSelectInsertOutput )? )? | onSetExpr | onMergeExpr ) ) ;
    public final void onExpr() throws RecognitionException {
        CommonTree i=null;

        try {
            // EsperEPL2Ast.g:105:2: ( ^(i= ON_EXPR onStreamExpr ( onDeleteExpr | onUpdateExpr | onSelectExpr ( ( onSelectInsertExpr )+ ( onSelectInsertOutput )? )? | onSetExpr | onMergeExpr ) ) )
            // EsperEPL2Ast.g:105:4: ^(i= ON_EXPR onStreamExpr ( onDeleteExpr | onUpdateExpr | onSelectExpr ( ( onSelectInsertExpr )+ ( onSelectInsertOutput )? )? | onSetExpr | onMergeExpr ) )
            {
            i=(CommonTree)match(input,ON_EXPR,FOLLOW_ON_EXPR_in_onExpr408); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_onStreamExpr_in_onExpr410);
            onStreamExpr();

            state._fsp--;

            // EsperEPL2Ast.g:106:3: ( onDeleteExpr | onUpdateExpr | onSelectExpr ( ( onSelectInsertExpr )+ ( onSelectInsertOutput )? )? | onSetExpr | onMergeExpr )
            int alt20=5;
            switch ( input.LA(1) ) {
            case ON_DELETE_EXPR:
                {
                alt20=1;
                }
                break;
            case ON_UPDATE_EXPR:
                {
                alt20=2;
                }
                break;
            case ON_SELECT_EXPR:
                {
                alt20=3;
                }
                break;
            case ON_SET_EXPR:
                {
                alt20=4;
                }
                break;
            case ON_MERGE_EXPR:
                {
                alt20=5;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 20, 0, input);

                throw nvae;
            }

            switch (alt20) {
                case 1 :
                    // EsperEPL2Ast.g:106:4: onDeleteExpr
                    {
                    pushFollow(FOLLOW_onDeleteExpr_in_onExpr415);
                    onDeleteExpr();

                    state._fsp--;


                    }
                    break;
                case 2 :
                    // EsperEPL2Ast.g:106:19: onUpdateExpr
                    {
                    pushFollow(FOLLOW_onUpdateExpr_in_onExpr419);
                    onUpdateExpr();

                    state._fsp--;


                    }
                    break;
                case 3 :
                    // EsperEPL2Ast.g:106:34: onSelectExpr ( ( onSelectInsertExpr )+ ( onSelectInsertOutput )? )?
                    {
                    pushFollow(FOLLOW_onSelectExpr_in_onExpr423);
                    onSelectExpr();

                    state._fsp--;

                    // EsperEPL2Ast.g:106:47: ( ( onSelectInsertExpr )+ ( onSelectInsertOutput )? )?
                    int alt19=2;
                    int LA19_0 = input.LA(1);

                    if ( (LA19_0==ON_SELECT_INSERT_EXPR) ) {
                        alt19=1;
                    }
                    switch (alt19) {
                        case 1 :
                            // EsperEPL2Ast.g:106:48: ( onSelectInsertExpr )+ ( onSelectInsertOutput )?
                            {
                            // EsperEPL2Ast.g:106:48: ( onSelectInsertExpr )+
                            int cnt17=0;
                            loop17:
                            do {
                                int alt17=2;
                                int LA17_0 = input.LA(1);

                                if ( (LA17_0==ON_SELECT_INSERT_EXPR) ) {
                                    alt17=1;
                                }


                                switch (alt17) {
                            	case 1 :
                            	    // EsperEPL2Ast.g:106:48: onSelectInsertExpr
                            	    {
                            	    pushFollow(FOLLOW_onSelectInsertExpr_in_onExpr426);
                            	    onSelectInsertExpr();

                            	    state._fsp--;


                            	    }
                            	    break;

                            	default :
                            	    if ( cnt17 >= 1 ) break loop17;
                                        EarlyExitException eee =
                                            new EarlyExitException(17, input);
                                        throw eee;
                                }
                                cnt17++;
                            } while (true);

                            // EsperEPL2Ast.g:106:68: ( onSelectInsertOutput )?
                            int alt18=2;
                            int LA18_0 = input.LA(1);

                            if ( (LA18_0==ON_SELECT_INSERT_OUTPUT) ) {
                                alt18=1;
                            }
                            switch (alt18) {
                                case 1 :
                                    // EsperEPL2Ast.g:106:68: onSelectInsertOutput
                                    {
                                    pushFollow(FOLLOW_onSelectInsertOutput_in_onExpr429);
                                    onSelectInsertOutput();

                                    state._fsp--;


                                    }
                                    break;

                            }


                            }
                            break;

                    }


                    }
                    break;
                case 4 :
                    // EsperEPL2Ast.g:106:94: onSetExpr
                    {
                    pushFollow(FOLLOW_onSetExpr_in_onExpr436);
                    onSetExpr();

                    state._fsp--;


                    }
                    break;
                case 5 :
                    // EsperEPL2Ast.g:106:106: onMergeExpr
                    {
                    pushFollow(FOLLOW_onMergeExpr_in_onExpr440);
                    onMergeExpr();

                    state._fsp--;


                    }
                    break;

            }

             leaveNode(i); 

            match(input, Token.UP, null); 

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "onExpr"


    // $ANTLR start "onStreamExpr"
    // EsperEPL2Ast.g:110:1: onStreamExpr : ^(s= ON_STREAM ( eventFilterExpr[true] | patternInclusionExpression ) ( IDENT )? ) ;
    public final void onStreamExpr() throws RecognitionException {
        CommonTree s=null;

        try {
            // EsperEPL2Ast.g:111:2: ( ^(s= ON_STREAM ( eventFilterExpr[true] | patternInclusionExpression ) ( IDENT )? ) )
            // EsperEPL2Ast.g:111:4: ^(s= ON_STREAM ( eventFilterExpr[true] | patternInclusionExpression ) ( IDENT )? )
            {
            s=(CommonTree)match(input,ON_STREAM,FOLLOW_ON_STREAM_in_onStreamExpr462); 

            match(input, Token.DOWN, null); 
            // EsperEPL2Ast.g:111:18: ( eventFilterExpr[true] | patternInclusionExpression )
            int alt21=2;
            int LA21_0 = input.LA(1);

            if ( (LA21_0==EVENT_FILTER_EXPR) ) {
                alt21=1;
            }
            else if ( (LA21_0==PATTERN_INCL_EXPR) ) {
                alt21=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 21, 0, input);

                throw nvae;
            }
            switch (alt21) {
                case 1 :
                    // EsperEPL2Ast.g:111:19: eventFilterExpr[true]
                    {
                    pushFollow(FOLLOW_eventFilterExpr_in_onStreamExpr465);
                    eventFilterExpr(true);

                    state._fsp--;


                    }
                    break;
                case 2 :
                    // EsperEPL2Ast.g:111:43: patternInclusionExpression
                    {
                    pushFollow(FOLLOW_patternInclusionExpression_in_onStreamExpr470);
                    patternInclusionExpression();

                    state._fsp--;


                    }
                    break;

            }

            // EsperEPL2Ast.g:111:71: ( IDENT )?
            int alt22=2;
            int LA22_0 = input.LA(1);

            if ( (LA22_0==IDENT) ) {
                alt22=1;
            }
            switch (alt22) {
                case 1 :
                    // EsperEPL2Ast.g:111:71: IDENT
                    {
                    match(input,IDENT,FOLLOW_IDENT_in_onStreamExpr473); 

                    }
                    break;

            }

             leaveNode(s); 

            match(input, Token.UP, null); 

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "onStreamExpr"


    // $ANTLR start "onMergeExpr"
    // EsperEPL2Ast.g:114:1: onMergeExpr : ^(m= ON_MERGE_EXPR IDENT ( IDENT )? ( mergeItem )+ ( whereClause[true] )? ) ;
    public final void onMergeExpr() throws RecognitionException {
        CommonTree m=null;

        try {
            // EsperEPL2Ast.g:115:2: ( ^(m= ON_MERGE_EXPR IDENT ( IDENT )? ( mergeItem )+ ( whereClause[true] )? ) )
            // EsperEPL2Ast.g:115:4: ^(m= ON_MERGE_EXPR IDENT ( IDENT )? ( mergeItem )+ ( whereClause[true] )? )
            {
            m=(CommonTree)match(input,ON_MERGE_EXPR,FOLLOW_ON_MERGE_EXPR_in_onMergeExpr491); 

            match(input, Token.DOWN, null); 
            match(input,IDENT,FOLLOW_IDENT_in_onMergeExpr493); 
            // EsperEPL2Ast.g:115:28: ( IDENT )?
            int alt23=2;
            int LA23_0 = input.LA(1);

            if ( (LA23_0==IDENT) ) {
                alt23=1;
            }
            switch (alt23) {
                case 1 :
                    // EsperEPL2Ast.g:115:28: IDENT
                    {
                    match(input,IDENT,FOLLOW_IDENT_in_onMergeExpr495); 

                    }
                    break;

            }

            // EsperEPL2Ast.g:115:35: ( mergeItem )+
            int cnt24=0;
            loop24:
            do {
                int alt24=2;
                int LA24_0 = input.LA(1);

                if ( ((LA24_0>=MERGE_UNM && LA24_0<=MERGE_MAT)) ) {
                    alt24=1;
                }


                switch (alt24) {
            	case 1 :
            	    // EsperEPL2Ast.g:115:35: mergeItem
            	    {
            	    pushFollow(FOLLOW_mergeItem_in_onMergeExpr498);
            	    mergeItem();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    if ( cnt24 >= 1 ) break loop24;
                        EarlyExitException eee =
                            new EarlyExitException(24, input);
                        throw eee;
                }
                cnt24++;
            } while (true);

            // EsperEPL2Ast.g:115:46: ( whereClause[true] )?
            int alt25=2;
            int LA25_0 = input.LA(1);

            if ( (LA25_0==WHERE_EXPR) ) {
                alt25=1;
            }
            switch (alt25) {
                case 1 :
                    // EsperEPL2Ast.g:115:46: whereClause[true]
                    {
                    pushFollow(FOLLOW_whereClause_in_onMergeExpr501);
                    whereClause(true);

                    state._fsp--;


                    }
                    break;

            }


            match(input, Token.UP, null); 

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "onMergeExpr"


    // $ANTLR start "mergeItem"
    // EsperEPL2Ast.g:118:1: mergeItem : ( mergeMatched | mergeUnmatched ) ;
    public final void mergeItem() throws RecognitionException {
        try {
            // EsperEPL2Ast.g:119:2: ( ( mergeMatched | mergeUnmatched ) )
            // EsperEPL2Ast.g:119:4: ( mergeMatched | mergeUnmatched )
            {
            // EsperEPL2Ast.g:119:4: ( mergeMatched | mergeUnmatched )
            int alt26=2;
            int LA26_0 = input.LA(1);

            if ( (LA26_0==MERGE_MAT) ) {
                alt26=1;
            }
            else if ( (LA26_0==MERGE_UNM) ) {
                alt26=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 26, 0, input);

                throw nvae;
            }
            switch (alt26) {
                case 1 :
                    // EsperEPL2Ast.g:119:5: mergeMatched
                    {
                    pushFollow(FOLLOW_mergeMatched_in_mergeItem517);
                    mergeMatched();

                    state._fsp--;


                    }
                    break;
                case 2 :
                    // EsperEPL2Ast.g:119:20: mergeUnmatched
                    {
                    pushFollow(FOLLOW_mergeUnmatched_in_mergeItem521);
                    mergeUnmatched();

                    state._fsp--;


                    }
                    break;

            }


            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "mergeItem"


    // $ANTLR start "mergeMatched"
    // EsperEPL2Ast.g:122:1: mergeMatched : ^(m= MERGE_MAT ( mergeMatchedItem )+ ( valueExpr )? ) ;
    public final void mergeMatched() throws RecognitionException {
        CommonTree m=null;

        try {
            // EsperEPL2Ast.g:123:2: ( ^(m= MERGE_MAT ( mergeMatchedItem )+ ( valueExpr )? ) )
            // EsperEPL2Ast.g:123:4: ^(m= MERGE_MAT ( mergeMatchedItem )+ ( valueExpr )? )
            {
            m=(CommonTree)match(input,MERGE_MAT,FOLLOW_MERGE_MAT_in_mergeMatched536); 

            match(input, Token.DOWN, null); 
            // EsperEPL2Ast.g:123:18: ( mergeMatchedItem )+
            int cnt27=0;
            loop27:
            do {
                int alt27=2;
                int LA27_0 = input.LA(1);

                if ( ((LA27_0>=MERGE_UPD && LA27_0<=MERGE_DEL)) ) {
                    alt27=1;
                }


                switch (alt27) {
            	case 1 :
            	    // EsperEPL2Ast.g:123:18: mergeMatchedItem
            	    {
            	    pushFollow(FOLLOW_mergeMatchedItem_in_mergeMatched538);
            	    mergeMatchedItem();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    if ( cnt27 >= 1 ) break loop27;
                        EarlyExitException eee =
                            new EarlyExitException(27, input);
                        throw eee;
                }
                cnt27++;
            } while (true);

            // EsperEPL2Ast.g:123:36: ( valueExpr )?
            int alt28=2;
            int LA28_0 = input.LA(1);

            if ( ((LA28_0>=IN_SET && LA28_0<=REGEXP)||LA28_0==NOT_EXPR||(LA28_0>=SUM && LA28_0<=AVG)||(LA28_0>=COALESCE && LA28_0<=COUNT)||(LA28_0>=CASE && LA28_0<=CASE2)||LA28_0==ISTREAM||(LA28_0>=PREVIOUS && LA28_0<=EXISTS)||(LA28_0>=INSTANCEOF && LA28_0<=CURRENT_TIMESTAMP)||LA28_0==NEWKW||(LA28_0>=EVAL_AND_EXPR && LA28_0<=EVAL_NOTEQUALS_GROUP_EXPR)||LA28_0==EVENT_PROP_EXPR||LA28_0==CONCAT||(LA28_0>=LIB_FUNC_CHAIN && LA28_0<=DOT_EXPR)||LA28_0==ARRAY_EXPR||(LA28_0>=NOT_IN_SET && LA28_0<=NOT_REGEXP)||(LA28_0>=IN_RANGE && LA28_0<=SUBSELECT_EXPR)||(LA28_0>=EXISTS_SUBSELECT_EXPR && LA28_0<=NOT_IN_SUBSELECT_EXPR)||LA28_0==SUBSTITUTION||(LA28_0>=FIRST_AGGREG && LA28_0<=WINDOW_AGGREG)||(LA28_0>=INT_TYPE && LA28_0<=NULL_TYPE)||(LA28_0>=JSON_OBJECT && LA28_0<=JSON_ARRAY)||LA28_0==STAR||(LA28_0>=LT && LA28_0<=GT)||(LA28_0>=BOR && LA28_0<=PLUS)||(LA28_0>=BAND && LA28_0<=BXOR)||(LA28_0>=LE && LA28_0<=GE)||(LA28_0>=MINUS && LA28_0<=MOD)||(LA28_0>=EVAL_IS_GROUP_EXPR && LA28_0<=EVAL_ISNOT_GROUP_EXPR)) ) {
                alt28=1;
            }
            switch (alt28) {
                case 1 :
                    // EsperEPL2Ast.g:123:36: valueExpr
                    {
                    pushFollow(FOLLOW_valueExpr_in_mergeMatched541);
                    valueExpr();

                    state._fsp--;


                    }
                    break;

            }

             leaveNode(m); 

            match(input, Token.UP, null); 

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "mergeMatched"


    // $ANTLR start "mergeMatchedItem"
    // EsperEPL2Ast.g:126:1: mergeMatchedItem : ( ^(m= MERGE_UPD ( onSetAssignment )* ( whereClause[false] )? ) | ^(d= MERGE_DEL ( whereClause[false] )? INT_TYPE ) | mergeInsert );
    public final void mergeMatchedItem() throws RecognitionException {
        CommonTree m=null;
        CommonTree d=null;

        try {
            // EsperEPL2Ast.g:127:2: ( ^(m= MERGE_UPD ( onSetAssignment )* ( whereClause[false] )? ) | ^(d= MERGE_DEL ( whereClause[false] )? INT_TYPE ) | mergeInsert )
            int alt32=3;
            switch ( input.LA(1) ) {
            case MERGE_UPD:
                {
                alt32=1;
                }
                break;
            case MERGE_DEL:
                {
                alt32=2;
                }
                break;
            case MERGE_INS:
                {
                alt32=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 32, 0, input);

                throw nvae;
            }

            switch (alt32) {
                case 1 :
                    // EsperEPL2Ast.g:127:4: ^(m= MERGE_UPD ( onSetAssignment )* ( whereClause[false] )? )
                    {
                    m=(CommonTree)match(input,MERGE_UPD,FOLLOW_MERGE_UPD_in_mergeMatchedItem559); 

                    if ( input.LA(1)==Token.DOWN ) {
                        match(input, Token.DOWN, null); 
                        // EsperEPL2Ast.g:127:18: ( onSetAssignment )*
                        loop29:
                        do {
                            int alt29=2;
                            int LA29_0 = input.LA(1);

                            if ( (LA29_0==ON_SET_EXPR_ITEM) ) {
                                alt29=1;
                            }


                            switch (alt29) {
                        	case 1 :
                        	    // EsperEPL2Ast.g:127:18: onSetAssignment
                        	    {
                        	    pushFollow(FOLLOW_onSetAssignment_in_mergeMatchedItem561);
                        	    onSetAssignment();

                        	    state._fsp--;


                        	    }
                        	    break;

                        	default :
                        	    break loop29;
                            }
                        } while (true);

                        // EsperEPL2Ast.g:127:35: ( whereClause[false] )?
                        int alt30=2;
                        int LA30_0 = input.LA(1);

                        if ( (LA30_0==WHERE_EXPR) ) {
                            alt30=1;
                        }
                        switch (alt30) {
                            case 1 :
                                // EsperEPL2Ast.g:127:35: whereClause[false]
                                {
                                pushFollow(FOLLOW_whereClause_in_mergeMatchedItem564);
                                whereClause(false);

                                state._fsp--;


                                }
                                break;

                        }

                         leaveNode(m); 

                        match(input, Token.UP, null); 
                    }

                    }
                    break;
                case 2 :
                    // EsperEPL2Ast.g:128:4: ^(d= MERGE_DEL ( whereClause[false] )? INT_TYPE )
                    {
                    d=(CommonTree)match(input,MERGE_DEL,FOLLOW_MERGE_DEL_in_mergeMatchedItem577); 

                    match(input, Token.DOWN, null); 
                    // EsperEPL2Ast.g:128:18: ( whereClause[false] )?
                    int alt31=2;
                    int LA31_0 = input.LA(1);

                    if ( (LA31_0==WHERE_EXPR) ) {
                        alt31=1;
                    }
                    switch (alt31) {
                        case 1 :
                            // EsperEPL2Ast.g:128:18: whereClause[false]
                            {
                            pushFollow(FOLLOW_whereClause_in_mergeMatchedItem579);
                            whereClause(false);

                            state._fsp--;


                            }
                            break;

                    }

                    match(input,INT_TYPE,FOLLOW_INT_TYPE_in_mergeMatchedItem583); 
                     leaveNode(d); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 3 :
                    // EsperEPL2Ast.g:129:4: mergeInsert
                    {
                    pushFollow(FOLLOW_mergeInsert_in_mergeMatchedItem591);
                    mergeInsert();

                    state._fsp--;


                    }
                    break;

            }
        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "mergeMatchedItem"


    // $ANTLR start "mergeUnmatched"
    // EsperEPL2Ast.g:132:1: mergeUnmatched : ^(m= MERGE_UNM ( mergeInsert )+ ( valueExpr )? ) ;
    public final void mergeUnmatched() throws RecognitionException {
        CommonTree m=null;

        try {
            // EsperEPL2Ast.g:133:2: ( ^(m= MERGE_UNM ( mergeInsert )+ ( valueExpr )? ) )
            // EsperEPL2Ast.g:133:4: ^(m= MERGE_UNM ( mergeInsert )+ ( valueExpr )? )
            {
            m=(CommonTree)match(input,MERGE_UNM,FOLLOW_MERGE_UNM_in_mergeUnmatched605); 

            match(input, Token.DOWN, null); 
            // EsperEPL2Ast.g:133:18: ( mergeInsert )+
            int cnt33=0;
            loop33:
            do {
                int alt33=2;
                int LA33_0 = input.LA(1);

                if ( (LA33_0==MERGE_INS) ) {
                    alt33=1;
                }


                switch (alt33) {
            	case 1 :
            	    // EsperEPL2Ast.g:133:18: mergeInsert
            	    {
            	    pushFollow(FOLLOW_mergeInsert_in_mergeUnmatched607);
            	    mergeInsert();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    if ( cnt33 >= 1 ) break loop33;
                        EarlyExitException eee =
                            new EarlyExitException(33, input);
                        throw eee;
                }
                cnt33++;
            } while (true);

            // EsperEPL2Ast.g:133:31: ( valueExpr )?
            int alt34=2;
            int LA34_0 = input.LA(1);

            if ( ((LA34_0>=IN_SET && LA34_0<=REGEXP)||LA34_0==NOT_EXPR||(LA34_0>=SUM && LA34_0<=AVG)||(LA34_0>=COALESCE && LA34_0<=COUNT)||(LA34_0>=CASE && LA34_0<=CASE2)||LA34_0==ISTREAM||(LA34_0>=PREVIOUS && LA34_0<=EXISTS)||(LA34_0>=INSTANCEOF && LA34_0<=CURRENT_TIMESTAMP)||LA34_0==NEWKW||(LA34_0>=EVAL_AND_EXPR && LA34_0<=EVAL_NOTEQUALS_GROUP_EXPR)||LA34_0==EVENT_PROP_EXPR||LA34_0==CONCAT||(LA34_0>=LIB_FUNC_CHAIN && LA34_0<=DOT_EXPR)||LA34_0==ARRAY_EXPR||(LA34_0>=NOT_IN_SET && LA34_0<=NOT_REGEXP)||(LA34_0>=IN_RANGE && LA34_0<=SUBSELECT_EXPR)||(LA34_0>=EXISTS_SUBSELECT_EXPR && LA34_0<=NOT_IN_SUBSELECT_EXPR)||LA34_0==SUBSTITUTION||(LA34_0>=FIRST_AGGREG && LA34_0<=WINDOW_AGGREG)||(LA34_0>=INT_TYPE && LA34_0<=NULL_TYPE)||(LA34_0>=JSON_OBJECT && LA34_0<=JSON_ARRAY)||LA34_0==STAR||(LA34_0>=LT && LA34_0<=GT)||(LA34_0>=BOR && LA34_0<=PLUS)||(LA34_0>=BAND && LA34_0<=BXOR)||(LA34_0>=LE && LA34_0<=GE)||(LA34_0>=MINUS && LA34_0<=MOD)||(LA34_0>=EVAL_IS_GROUP_EXPR && LA34_0<=EVAL_ISNOT_GROUP_EXPR)) ) {
                alt34=1;
            }
            switch (alt34) {
                case 1 :
                    // EsperEPL2Ast.g:133:31: valueExpr
                    {
                    pushFollow(FOLLOW_valueExpr_in_mergeUnmatched610);
                    valueExpr();

                    state._fsp--;


                    }
                    break;

            }

             leaveNode(m); 

            match(input, Token.UP, null); 

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "mergeUnmatched"


    // $ANTLR start "mergeInsert"
    // EsperEPL2Ast.g:136:1: mergeInsert : ^(um= MERGE_INS selectionList ( CLASS_IDENT )? ( exprCol )? ( whereClause[false] )? ) ;
    public final void mergeInsert() throws RecognitionException {
        CommonTree um=null;

        try {
            // EsperEPL2Ast.g:137:2: ( ^(um= MERGE_INS selectionList ( CLASS_IDENT )? ( exprCol )? ( whereClause[false] )? ) )
            // EsperEPL2Ast.g:137:4: ^(um= MERGE_INS selectionList ( CLASS_IDENT )? ( exprCol )? ( whereClause[false] )? )
            {
            um=(CommonTree)match(input,MERGE_INS,FOLLOW_MERGE_INS_in_mergeInsert629); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_selectionList_in_mergeInsert631);
            selectionList();

            state._fsp--;

            // EsperEPL2Ast.g:137:33: ( CLASS_IDENT )?
            int alt35=2;
            int LA35_0 = input.LA(1);

            if ( (LA35_0==CLASS_IDENT) ) {
                alt35=1;
            }
            switch (alt35) {
                case 1 :
                    // EsperEPL2Ast.g:137:33: CLASS_IDENT
                    {
                    match(input,CLASS_IDENT,FOLLOW_CLASS_IDENT_in_mergeInsert633); 

                    }
                    break;

            }

            // EsperEPL2Ast.g:137:46: ( exprCol )?
            int alt36=2;
            int LA36_0 = input.LA(1);

            if ( (LA36_0==EXPRCOL) ) {
                alt36=1;
            }
            switch (alt36) {
                case 1 :
                    // EsperEPL2Ast.g:137:46: exprCol
                    {
                    pushFollow(FOLLOW_exprCol_in_mergeInsert636);
                    exprCol();

                    state._fsp--;


                    }
                    break;

            }

            // EsperEPL2Ast.g:137:55: ( whereClause[false] )?
            int alt37=2;
            int LA37_0 = input.LA(1);

            if ( (LA37_0==WHERE_EXPR) ) {
                alt37=1;
            }
            switch (alt37) {
                case 1 :
                    // EsperEPL2Ast.g:137:55: whereClause[false]
                    {
                    pushFollow(FOLLOW_whereClause_in_mergeInsert639);
                    whereClause(false);

                    state._fsp--;


                    }
                    break;

            }

             leaveNode(um); 

            match(input, Token.UP, null); 

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "mergeInsert"


    // $ANTLR start "updateExpr"
    // EsperEPL2Ast.g:140:1: updateExpr : ^(u= UPDATE_EXPR CLASS_IDENT ( IDENT )? ( onSetAssignment )+ ( whereClause[false] )? ) ;
    public final void updateExpr() throws RecognitionException {
        CommonTree u=null;

        try {
            // EsperEPL2Ast.g:141:2: ( ^(u= UPDATE_EXPR CLASS_IDENT ( IDENT )? ( onSetAssignment )+ ( whereClause[false] )? ) )
            // EsperEPL2Ast.g:141:4: ^(u= UPDATE_EXPR CLASS_IDENT ( IDENT )? ( onSetAssignment )+ ( whereClause[false] )? )
            {
            u=(CommonTree)match(input,UPDATE_EXPR,FOLLOW_UPDATE_EXPR_in_updateExpr659); 

            match(input, Token.DOWN, null); 
            match(input,CLASS_IDENT,FOLLOW_CLASS_IDENT_in_updateExpr661); 
            // EsperEPL2Ast.g:141:32: ( IDENT )?
            int alt38=2;
            int LA38_0 = input.LA(1);

            if ( (LA38_0==IDENT) ) {
                alt38=1;
            }
            switch (alt38) {
                case 1 :
                    // EsperEPL2Ast.g:141:32: IDENT
                    {
                    match(input,IDENT,FOLLOW_IDENT_in_updateExpr663); 

                    }
                    break;

            }

            // EsperEPL2Ast.g:141:39: ( onSetAssignment )+
            int cnt39=0;
            loop39:
            do {
                int alt39=2;
                int LA39_0 = input.LA(1);

                if ( (LA39_0==ON_SET_EXPR_ITEM) ) {
                    alt39=1;
                }


                switch (alt39) {
            	case 1 :
            	    // EsperEPL2Ast.g:141:39: onSetAssignment
            	    {
            	    pushFollow(FOLLOW_onSetAssignment_in_updateExpr666);
            	    onSetAssignment();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    if ( cnt39 >= 1 ) break loop39;
                        EarlyExitException eee =
                            new EarlyExitException(39, input);
                        throw eee;
                }
                cnt39++;
            } while (true);

            // EsperEPL2Ast.g:141:56: ( whereClause[false] )?
            int alt40=2;
            int LA40_0 = input.LA(1);

            if ( (LA40_0==WHERE_EXPR) ) {
                alt40=1;
            }
            switch (alt40) {
                case 1 :
                    // EsperEPL2Ast.g:141:56: whereClause[false]
                    {
                    pushFollow(FOLLOW_whereClause_in_updateExpr669);
                    whereClause(false);

                    state._fsp--;


                    }
                    break;

            }

             leaveNode(u); 

            match(input, Token.UP, null); 

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "updateExpr"


    // $ANTLR start "onDeleteExpr"
    // EsperEPL2Ast.g:144:1: onDeleteExpr : ^( ON_DELETE_EXPR onExprFrom ( whereClause[true] )? ) ;
    public final void onDeleteExpr() throws RecognitionException {
        try {
            // EsperEPL2Ast.g:145:2: ( ^( ON_DELETE_EXPR onExprFrom ( whereClause[true] )? ) )
            // EsperEPL2Ast.g:145:4: ^( ON_DELETE_EXPR onExprFrom ( whereClause[true] )? )
            {
            match(input,ON_DELETE_EXPR,FOLLOW_ON_DELETE_EXPR_in_onDeleteExpr686); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_onExprFrom_in_onDeleteExpr688);
            onExprFrom();

            state._fsp--;

            // EsperEPL2Ast.g:145:32: ( whereClause[true] )?
            int alt41=2;
            int LA41_0 = input.LA(1);

            if ( (LA41_0==WHERE_EXPR) ) {
                alt41=1;
            }
            switch (alt41) {
                case 1 :
                    // EsperEPL2Ast.g:145:33: whereClause[true]
                    {
                    pushFollow(FOLLOW_whereClause_in_onDeleteExpr691);
                    whereClause(true);

                    state._fsp--;


                    }
                    break;

            }


            match(input, Token.UP, null); 

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "onDeleteExpr"


    // $ANTLR start "onSelectExpr"
    // EsperEPL2Ast.g:148:1: onSelectExpr : ^(s= ON_SELECT_EXPR ( insertIntoExpr )? ( DELETE )? ( DISTINCT )? selectionList ( onExprFrom )? ( whereClause[true] )? ( groupByClause )? ( havingClause )? ( orderByClause )? ( rowLimitClause )? ) ;
    public final void onSelectExpr() throws RecognitionException {
        CommonTree s=null;

        try {
            // EsperEPL2Ast.g:149:2: ( ^(s= ON_SELECT_EXPR ( insertIntoExpr )? ( DELETE )? ( DISTINCT )? selectionList ( onExprFrom )? ( whereClause[true] )? ( groupByClause )? ( havingClause )? ( orderByClause )? ( rowLimitClause )? ) )
            // EsperEPL2Ast.g:149:4: ^(s= ON_SELECT_EXPR ( insertIntoExpr )? ( DELETE )? ( DISTINCT )? selectionList ( onExprFrom )? ( whereClause[true] )? ( groupByClause )? ( havingClause )? ( orderByClause )? ( rowLimitClause )? )
            {
            s=(CommonTree)match(input,ON_SELECT_EXPR,FOLLOW_ON_SELECT_EXPR_in_onSelectExpr711); 

            match(input, Token.DOWN, null); 
            // EsperEPL2Ast.g:149:23: ( insertIntoExpr )?
            int alt42=2;
            int LA42_0 = input.LA(1);

            if ( (LA42_0==INSERTINTO_EXPR) ) {
                alt42=1;
            }
            switch (alt42) {
                case 1 :
                    // EsperEPL2Ast.g:149:23: insertIntoExpr
                    {
                    pushFollow(FOLLOW_insertIntoExpr_in_onSelectExpr713);
                    insertIntoExpr();

                    state._fsp--;


                    }
                    break;

            }

            // EsperEPL2Ast.g:149:39: ( DELETE )?
            int alt43=2;
            int LA43_0 = input.LA(1);

            if ( (LA43_0==DELETE) ) {
                alt43=1;
            }
            switch (alt43) {
                case 1 :
                    // EsperEPL2Ast.g:149:39: DELETE
                    {
                    match(input,DELETE,FOLLOW_DELETE_in_onSelectExpr716); 

                    }
                    break;

            }

            // EsperEPL2Ast.g:149:47: ( DISTINCT )?
            int alt44=2;
            int LA44_0 = input.LA(1);

            if ( (LA44_0==DISTINCT) ) {
                alt44=1;
            }
            switch (alt44) {
                case 1 :
                    // EsperEPL2Ast.g:149:47: DISTINCT
                    {
                    match(input,DISTINCT,FOLLOW_DISTINCT_in_onSelectExpr719); 

                    }
                    break;

            }

            pushFollow(FOLLOW_selectionList_in_onSelectExpr722);
            selectionList();

            state._fsp--;

            // EsperEPL2Ast.g:149:71: ( onExprFrom )?
            int alt45=2;
            int LA45_0 = input.LA(1);

            if ( (LA45_0==ON_EXPR_FROM) ) {
                alt45=1;
            }
            switch (alt45) {
                case 1 :
                    // EsperEPL2Ast.g:149:71: onExprFrom
                    {
                    pushFollow(FOLLOW_onExprFrom_in_onSelectExpr724);
                    onExprFrom();

                    state._fsp--;


                    }
                    break;

            }

            // EsperEPL2Ast.g:149:83: ( whereClause[true] )?
            int alt46=2;
            int LA46_0 = input.LA(1);

            if ( (LA46_0==WHERE_EXPR) ) {
                alt46=1;
            }
            switch (alt46) {
                case 1 :
                    // EsperEPL2Ast.g:149:83: whereClause[true]
                    {
                    pushFollow(FOLLOW_whereClause_in_onSelectExpr727);
                    whereClause(true);

                    state._fsp--;


                    }
                    break;

            }

            // EsperEPL2Ast.g:149:102: ( groupByClause )?
            int alt47=2;
            int LA47_0 = input.LA(1);

            if ( (LA47_0==GROUP_BY_EXPR) ) {
                alt47=1;
            }
            switch (alt47) {
                case 1 :
                    // EsperEPL2Ast.g:149:102: groupByClause
                    {
                    pushFollow(FOLLOW_groupByClause_in_onSelectExpr731);
                    groupByClause();

                    state._fsp--;


                    }
                    break;

            }

            // EsperEPL2Ast.g:149:117: ( havingClause )?
            int alt48=2;
            int LA48_0 = input.LA(1);

            if ( (LA48_0==HAVING_EXPR) ) {
                alt48=1;
            }
            switch (alt48) {
                case 1 :
                    // EsperEPL2Ast.g:149:117: havingClause
                    {
                    pushFollow(FOLLOW_havingClause_in_onSelectExpr734);
                    havingClause();

                    state._fsp--;


                    }
                    break;

            }

            // EsperEPL2Ast.g:149:131: ( orderByClause )?
            int alt49=2;
            int LA49_0 = input.LA(1);

            if ( (LA49_0==ORDER_BY_EXPR) ) {
                alt49=1;
            }
            switch (alt49) {
                case 1 :
                    // EsperEPL2Ast.g:149:131: orderByClause
                    {
                    pushFollow(FOLLOW_orderByClause_in_onSelectExpr737);
                    orderByClause();

                    state._fsp--;


                    }
                    break;

            }

            // EsperEPL2Ast.g:149:146: ( rowLimitClause )?
            int alt50=2;
            int LA50_0 = input.LA(1);

            if ( (LA50_0==ROW_LIMIT_EXPR) ) {
                alt50=1;
            }
            switch (alt50) {
                case 1 :
                    // EsperEPL2Ast.g:149:146: rowLimitClause
                    {
                    pushFollow(FOLLOW_rowLimitClause_in_onSelectExpr740);
                    rowLimitClause();

                    state._fsp--;


                    }
                    break;

            }

             leaveNode(s); 

            match(input, Token.UP, null); 

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "onSelectExpr"


    // $ANTLR start "onSelectInsertExpr"
    // EsperEPL2Ast.g:152:1: onSelectInsertExpr : ^( ON_SELECT_INSERT_EXPR insertIntoExpr selectionList ( whereClause[true] )? ) ;
    public final void onSelectInsertExpr() throws RecognitionException {
        try {
            // EsperEPL2Ast.g:153:2: ( ^( ON_SELECT_INSERT_EXPR insertIntoExpr selectionList ( whereClause[true] )? ) )
            // EsperEPL2Ast.g:153:4: ^( ON_SELECT_INSERT_EXPR insertIntoExpr selectionList ( whereClause[true] )? )
            {
            pushStmtContext();
            match(input,ON_SELECT_INSERT_EXPR,FOLLOW_ON_SELECT_INSERT_EXPR_in_onSelectInsertExpr760); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_insertIntoExpr_in_onSelectInsertExpr762);
            insertIntoExpr();

            state._fsp--;

            pushFollow(FOLLOW_selectionList_in_onSelectInsertExpr764);
            selectionList();

            state._fsp--;

            // EsperEPL2Ast.g:153:78: ( whereClause[true] )?
            int alt51=2;
            int LA51_0 = input.LA(1);

            if ( (LA51_0==WHERE_EXPR) ) {
                alt51=1;
            }
            switch (alt51) {
                case 1 :
                    // EsperEPL2Ast.g:153:78: whereClause[true]
                    {
                    pushFollow(FOLLOW_whereClause_in_onSelectInsertExpr766);
                    whereClause(true);

                    state._fsp--;


                    }
                    break;

            }


            match(input, Token.UP, null); 

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "onSelectInsertExpr"


    // $ANTLR start "onSelectInsertOutput"
    // EsperEPL2Ast.g:156:1: onSelectInsertOutput : ^( ON_SELECT_INSERT_OUTPUT ( ALL | FIRST ) ) ;
    public final void onSelectInsertOutput() throws RecognitionException {
        try {
            // EsperEPL2Ast.g:157:2: ( ^( ON_SELECT_INSERT_OUTPUT ( ALL | FIRST ) ) )
            // EsperEPL2Ast.g:157:4: ^( ON_SELECT_INSERT_OUTPUT ( ALL | FIRST ) )
            {
            match(input,ON_SELECT_INSERT_OUTPUT,FOLLOW_ON_SELECT_INSERT_OUTPUT_in_onSelectInsertOutput783); 

            match(input, Token.DOWN, null); 
            if ( input.LA(1)==ALL||input.LA(1)==FIRST ) {
                input.consume();
                state.errorRecovery=false;
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }


            match(input, Token.UP, null); 

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "onSelectInsertOutput"


    // $ANTLR start "onSetExpr"
    // EsperEPL2Ast.g:160:1: onSetExpr : ^( ON_SET_EXPR onSetAssignment ( onSetAssignment )* ( whereClause[false] )? ) ;
    public final void onSetExpr() throws RecognitionException {
        try {
            // EsperEPL2Ast.g:161:2: ( ^( ON_SET_EXPR onSetAssignment ( onSetAssignment )* ( whereClause[false] )? ) )
            // EsperEPL2Ast.g:161:4: ^( ON_SET_EXPR onSetAssignment ( onSetAssignment )* ( whereClause[false] )? )
            {
            match(input,ON_SET_EXPR,FOLLOW_ON_SET_EXPR_in_onSetExpr803); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_onSetAssignment_in_onSetExpr805);
            onSetAssignment();

            state._fsp--;

            // EsperEPL2Ast.g:161:34: ( onSetAssignment )*
            loop52:
            do {
                int alt52=2;
                int LA52_0 = input.LA(1);

                if ( (LA52_0==ON_SET_EXPR_ITEM) ) {
                    alt52=1;
                }


                switch (alt52) {
            	case 1 :
            	    // EsperEPL2Ast.g:161:35: onSetAssignment
            	    {
            	    pushFollow(FOLLOW_onSetAssignment_in_onSetExpr808);
            	    onSetAssignment();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop52;
                }
            } while (true);

            // EsperEPL2Ast.g:161:53: ( whereClause[false] )?
            int alt53=2;
            int LA53_0 = input.LA(1);

            if ( (LA53_0==WHERE_EXPR) ) {
                alt53=1;
            }
            switch (alt53) {
                case 1 :
                    // EsperEPL2Ast.g:161:53: whereClause[false]
                    {
                    pushFollow(FOLLOW_whereClause_in_onSetExpr812);
                    whereClause(false);

                    state._fsp--;


                    }
                    break;

            }


            match(input, Token.UP, null); 

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "onSetExpr"


    // $ANTLR start "onUpdateExpr"
    // EsperEPL2Ast.g:164:1: onUpdateExpr : ^( ON_UPDATE_EXPR onExprFrom ( onSetAssignment )+ ( whereClause[false] )? ) ;
    public final void onUpdateExpr() throws RecognitionException {
        try {
            // EsperEPL2Ast.g:165:2: ( ^( ON_UPDATE_EXPR onExprFrom ( onSetAssignment )+ ( whereClause[false] )? ) )
            // EsperEPL2Ast.g:165:4: ^( ON_UPDATE_EXPR onExprFrom ( onSetAssignment )+ ( whereClause[false] )? )
            {
            match(input,ON_UPDATE_EXPR,FOLLOW_ON_UPDATE_EXPR_in_onUpdateExpr827); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_onExprFrom_in_onUpdateExpr829);
            onExprFrom();

            state._fsp--;

            // EsperEPL2Ast.g:165:32: ( onSetAssignment )+
            int cnt54=0;
            loop54:
            do {
                int alt54=2;
                int LA54_0 = input.LA(1);

                if ( (LA54_0==ON_SET_EXPR_ITEM) ) {
                    alt54=1;
                }


                switch (alt54) {
            	case 1 :
            	    // EsperEPL2Ast.g:165:32: onSetAssignment
            	    {
            	    pushFollow(FOLLOW_onSetAssignment_in_onUpdateExpr831);
            	    onSetAssignment();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    if ( cnt54 >= 1 ) break loop54;
                        EarlyExitException eee =
                            new EarlyExitException(54, input);
                        throw eee;
                }
                cnt54++;
            } while (true);

            // EsperEPL2Ast.g:165:49: ( whereClause[false] )?
            int alt55=2;
            int LA55_0 = input.LA(1);

            if ( (LA55_0==WHERE_EXPR) ) {
                alt55=1;
            }
            switch (alt55) {
                case 1 :
                    // EsperEPL2Ast.g:165:49: whereClause[false]
                    {
                    pushFollow(FOLLOW_whereClause_in_onUpdateExpr834);
                    whereClause(false);

                    state._fsp--;


                    }
                    break;

            }


            match(input, Token.UP, null); 

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "onUpdateExpr"


    // $ANTLR start "onSetAssignment"
    // EsperEPL2Ast.g:168:1: onSetAssignment : ^( ON_SET_EXPR_ITEM eventPropertyExpr[false] valueExpr ) ;
    public final void onSetAssignment() throws RecognitionException {
        try {
            // EsperEPL2Ast.g:169:2: ( ^( ON_SET_EXPR_ITEM eventPropertyExpr[false] valueExpr ) )
            // EsperEPL2Ast.g:169:4: ^( ON_SET_EXPR_ITEM eventPropertyExpr[false] valueExpr )
            {
            match(input,ON_SET_EXPR_ITEM,FOLLOW_ON_SET_EXPR_ITEM_in_onSetAssignment849); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_eventPropertyExpr_in_onSetAssignment851);
            eventPropertyExpr(false);

            state._fsp--;

            pushFollow(FOLLOW_valueExpr_in_onSetAssignment854);
            valueExpr();

            state._fsp--;


            match(input, Token.UP, null); 

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "onSetAssignment"


    // $ANTLR start "onExprFrom"
    // EsperEPL2Ast.g:172:1: onExprFrom : ^( ON_EXPR_FROM IDENT ( IDENT )? ) ;
    public final void onExprFrom() throws RecognitionException {
        try {
            // EsperEPL2Ast.g:173:2: ( ^( ON_EXPR_FROM IDENT ( IDENT )? ) )
            // EsperEPL2Ast.g:173:4: ^( ON_EXPR_FROM IDENT ( IDENT )? )
            {
            match(input,ON_EXPR_FROM,FOLLOW_ON_EXPR_FROM_in_onExprFrom868); 

            match(input, Token.DOWN, null); 
            match(input,IDENT,FOLLOW_IDENT_in_onExprFrom870); 
            // EsperEPL2Ast.g:173:25: ( IDENT )?
            int alt56=2;
            int LA56_0 = input.LA(1);

            if ( (LA56_0==IDENT) ) {
                alt56=1;
            }
            switch (alt56) {
                case 1 :
                    // EsperEPL2Ast.g:173:26: IDENT
                    {
                    match(input,IDENT,FOLLOW_IDENT_in_onExprFrom873); 

                    }
                    break;

            }


            match(input, Token.UP, null); 

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "onExprFrom"


    // $ANTLR start "createWindowExpr"
    // EsperEPL2Ast.g:176:1: createWindowExpr : ^(i= CREATE_WINDOW_EXPR IDENT ( viewListExpr )? ( RETAINUNION )? ( RETAININTERSECTION )? ( ( ( createSelectionList )? CLASS_IDENT ) | ( createColTypeList ) ) ( createWindowExprInsert )? ) ;
    public final void createWindowExpr() throws RecognitionException {
        CommonTree i=null;

        try {
            // EsperEPL2Ast.g:177:2: ( ^(i= CREATE_WINDOW_EXPR IDENT ( viewListExpr )? ( RETAINUNION )? ( RETAININTERSECTION )? ( ( ( createSelectionList )? CLASS_IDENT ) | ( createColTypeList ) ) ( createWindowExprInsert )? ) )
            // EsperEPL2Ast.g:177:4: ^(i= CREATE_WINDOW_EXPR IDENT ( viewListExpr )? ( RETAINUNION )? ( RETAININTERSECTION )? ( ( ( createSelectionList )? CLASS_IDENT ) | ( createColTypeList ) ) ( createWindowExprInsert )? )
            {
            i=(CommonTree)match(input,CREATE_WINDOW_EXPR,FOLLOW_CREATE_WINDOW_EXPR_in_createWindowExpr891); 

            match(input, Token.DOWN, null); 
            match(input,IDENT,FOLLOW_IDENT_in_createWindowExpr893); 
            // EsperEPL2Ast.g:177:33: ( viewListExpr )?
            int alt57=2;
            int LA57_0 = input.LA(1);

            if ( (LA57_0==VIEW_EXPR) ) {
                alt57=1;
            }
            switch (alt57) {
                case 1 :
                    // EsperEPL2Ast.g:177:34: viewListExpr
                    {
                    pushFollow(FOLLOW_viewListExpr_in_createWindowExpr896);
                    viewListExpr();

                    state._fsp--;


                    }
                    break;

            }

            // EsperEPL2Ast.g:177:49: ( RETAINUNION )?
            int alt58=2;
            int LA58_0 = input.LA(1);

            if ( (LA58_0==RETAINUNION) ) {
                alt58=1;
            }
            switch (alt58) {
                case 1 :
                    // EsperEPL2Ast.g:177:49: RETAINUNION
                    {
                    match(input,RETAINUNION,FOLLOW_RETAINUNION_in_createWindowExpr900); 

                    }
                    break;

            }

            // EsperEPL2Ast.g:177:62: ( RETAININTERSECTION )?
            int alt59=2;
            int LA59_0 = input.LA(1);

            if ( (LA59_0==RETAININTERSECTION) ) {
                alt59=1;
            }
            switch (alt59) {
                case 1 :
                    // EsperEPL2Ast.g:177:62: RETAININTERSECTION
                    {
                    match(input,RETAININTERSECTION,FOLLOW_RETAININTERSECTION_in_createWindowExpr903); 

                    }
                    break;

            }

            // EsperEPL2Ast.g:178:4: ( ( ( createSelectionList )? CLASS_IDENT ) | ( createColTypeList ) )
            int alt61=2;
            int LA61_0 = input.LA(1);

            if ( (LA61_0==CLASS_IDENT||LA61_0==CREATE_WINDOW_SELECT_EXPR) ) {
                alt61=1;
            }
            else if ( (LA61_0==CREATE_COL_TYPE_LIST) ) {
                alt61=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 61, 0, input);

                throw nvae;
            }
            switch (alt61) {
                case 1 :
                    // EsperEPL2Ast.g:179:5: ( ( createSelectionList )? CLASS_IDENT )
                    {
                    // EsperEPL2Ast.g:179:5: ( ( createSelectionList )? CLASS_IDENT )
                    // EsperEPL2Ast.g:179:6: ( createSelectionList )? CLASS_IDENT
                    {
                    // EsperEPL2Ast.g:179:6: ( createSelectionList )?
                    int alt60=2;
                    int LA60_0 = input.LA(1);

                    if ( (LA60_0==CREATE_WINDOW_SELECT_EXPR) ) {
                        alt60=1;
                    }
                    switch (alt60) {
                        case 1 :
                            // EsperEPL2Ast.g:179:6: createSelectionList
                            {
                            pushFollow(FOLLOW_createSelectionList_in_createWindowExpr917);
                            createSelectionList();

                            state._fsp--;


                            }
                            break;

                    }

                    match(input,CLASS_IDENT,FOLLOW_CLASS_IDENT_in_createWindowExpr920); 

                    }


                    }
                    break;
                case 2 :
                    // EsperEPL2Ast.g:181:12: ( createColTypeList )
                    {
                    // EsperEPL2Ast.g:181:12: ( createColTypeList )
                    // EsperEPL2Ast.g:181:13: createColTypeList
                    {
                    pushFollow(FOLLOW_createColTypeList_in_createWindowExpr949);
                    createColTypeList();

                    state._fsp--;


                    }


                    }
                    break;

            }

            // EsperEPL2Ast.g:183:4: ( createWindowExprInsert )?
            int alt62=2;
            int LA62_0 = input.LA(1);

            if ( (LA62_0==INSERT) ) {
                alt62=1;
            }
            switch (alt62) {
                case 1 :
                    // EsperEPL2Ast.g:183:4: createWindowExprInsert
                    {
                    pushFollow(FOLLOW_createWindowExprInsert_in_createWindowExpr960);
                    createWindowExprInsert();

                    state._fsp--;


                    }
                    break;

            }

             leaveNode(i); 

            match(input, Token.UP, null); 

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "createWindowExpr"


    // $ANTLR start "createIndexExpr"
    // EsperEPL2Ast.g:187:1: createIndexExpr : ^(i= CREATE_INDEX_EXPR IDENT IDENT indexColList ( IDENT )? ) ;
    public final void createIndexExpr() throws RecognitionException {
        CommonTree i=null;

        try {
            // EsperEPL2Ast.g:188:2: ( ^(i= CREATE_INDEX_EXPR IDENT IDENT indexColList ( IDENT )? ) )
            // EsperEPL2Ast.g:188:4: ^(i= CREATE_INDEX_EXPR IDENT IDENT indexColList ( IDENT )? )
            {
            i=(CommonTree)match(input,CREATE_INDEX_EXPR,FOLLOW_CREATE_INDEX_EXPR_in_createIndexExpr980); 

            match(input, Token.DOWN, null); 
            match(input,IDENT,FOLLOW_IDENT_in_createIndexExpr982); 
            match(input,IDENT,FOLLOW_IDENT_in_createIndexExpr984); 
            pushFollow(FOLLOW_indexColList_in_createIndexExpr986);
            indexColList();

            state._fsp--;

            // EsperEPL2Ast.g:188:51: ( IDENT )?
            int alt63=2;
            int LA63_0 = input.LA(1);

            if ( (LA63_0==IDENT) ) {
                alt63=1;
            }
            switch (alt63) {
                case 1 :
                    // EsperEPL2Ast.g:188:51: IDENT
                    {
                    match(input,IDENT,FOLLOW_IDENT_in_createIndexExpr988); 

                    }
                    break;

            }

             leaveNode(i); 

            match(input, Token.UP, null); 

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "createIndexExpr"


    // $ANTLR start "indexColList"
    // EsperEPL2Ast.g:191:1: indexColList : ^( INDEXCOL ( indexCol )+ ) ;
    public final void indexColList() throws RecognitionException {
        try {
            // EsperEPL2Ast.g:192:2: ( ^( INDEXCOL ( indexCol )+ ) )
            // EsperEPL2Ast.g:192:4: ^( INDEXCOL ( indexCol )+ )
            {
            match(input,INDEXCOL,FOLLOW_INDEXCOL_in_indexColList1004); 

            match(input, Token.DOWN, null); 
            // EsperEPL2Ast.g:192:15: ( indexCol )+
            int cnt64=0;
            loop64:
            do {
                int alt64=2;
                int LA64_0 = input.LA(1);

                if ( (LA64_0==INDEXCOL) ) {
                    alt64=1;
                }


                switch (alt64) {
            	case 1 :
            	    // EsperEPL2Ast.g:192:15: indexCol
            	    {
            	    pushFollow(FOLLOW_indexCol_in_indexColList1006);
            	    indexCol();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    if ( cnt64 >= 1 ) break loop64;
                        EarlyExitException eee =
                            new EarlyExitException(64, input);
                        throw eee;
                }
                cnt64++;
            } while (true);


            match(input, Token.UP, null); 

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "indexColList"


    // $ANTLR start "indexCol"
    // EsperEPL2Ast.g:195:1: indexCol : ^( INDEXCOL IDENT ( IDENT )? ) ;
    public final void indexCol() throws RecognitionException {
        try {
            // EsperEPL2Ast.g:196:2: ( ^( INDEXCOL IDENT ( IDENT )? ) )
            // EsperEPL2Ast.g:196:4: ^( INDEXCOL IDENT ( IDENT )? )
            {
            match(input,INDEXCOL,FOLLOW_INDEXCOL_in_indexCol1021); 

            match(input, Token.DOWN, null); 
            match(input,IDENT,FOLLOW_IDENT_in_indexCol1023); 
            // EsperEPL2Ast.g:196:21: ( IDENT )?
            int alt65=2;
            int LA65_0 = input.LA(1);

            if ( (LA65_0==IDENT) ) {
                alt65=1;
            }
            switch (alt65) {
                case 1 :
                    // EsperEPL2Ast.g:196:21: IDENT
                    {
                    match(input,IDENT,FOLLOW_IDENT_in_indexCol1025); 

                    }
                    break;

            }


            match(input, Token.UP, null); 

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "indexCol"


    // $ANTLR start "createWindowExprInsert"
    // EsperEPL2Ast.g:199:1: createWindowExprInsert : ^( INSERT ( valueExpr )? ) ;
    public final void createWindowExprInsert() throws RecognitionException {
        try {
            // EsperEPL2Ast.g:200:2: ( ^( INSERT ( valueExpr )? ) )
            // EsperEPL2Ast.g:200:4: ^( INSERT ( valueExpr )? )
            {
            match(input,INSERT,FOLLOW_INSERT_in_createWindowExprInsert1039); 

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // EsperEPL2Ast.g:200:13: ( valueExpr )?
                int alt66=2;
                int LA66_0 = input.LA(1);

                if ( ((LA66_0>=IN_SET && LA66_0<=REGEXP)||LA66_0==NOT_EXPR||(LA66_0>=SUM && LA66_0<=AVG)||(LA66_0>=COALESCE && LA66_0<=COUNT)||(LA66_0>=CASE && LA66_0<=CASE2)||LA66_0==ISTREAM||(LA66_0>=PREVIOUS && LA66_0<=EXISTS)||(LA66_0>=INSTANCEOF && LA66_0<=CURRENT_TIMESTAMP)||LA66_0==NEWKW||(LA66_0>=EVAL_AND_EXPR && LA66_0<=EVAL_NOTEQUALS_GROUP_EXPR)||LA66_0==EVENT_PROP_EXPR||LA66_0==CONCAT||(LA66_0>=LIB_FUNC_CHAIN && LA66_0<=DOT_EXPR)||LA66_0==ARRAY_EXPR||(LA66_0>=NOT_IN_SET && LA66_0<=NOT_REGEXP)||(LA66_0>=IN_RANGE && LA66_0<=SUBSELECT_EXPR)||(LA66_0>=EXISTS_SUBSELECT_EXPR && LA66_0<=NOT_IN_SUBSELECT_EXPR)||LA66_0==SUBSTITUTION||(LA66_0>=FIRST_AGGREG && LA66_0<=WINDOW_AGGREG)||(LA66_0>=INT_TYPE && LA66_0<=NULL_TYPE)||(LA66_0>=JSON_OBJECT && LA66_0<=JSON_ARRAY)||LA66_0==STAR||(LA66_0>=LT && LA66_0<=GT)||(LA66_0>=BOR && LA66_0<=PLUS)||(LA66_0>=BAND && LA66_0<=BXOR)||(LA66_0>=LE && LA66_0<=GE)||(LA66_0>=MINUS && LA66_0<=MOD)||(LA66_0>=EVAL_IS_GROUP_EXPR && LA66_0<=EVAL_ISNOT_GROUP_EXPR)) ) {
                    alt66=1;
                }
                switch (alt66) {
                    case 1 :
                        // EsperEPL2Ast.g:200:13: valueExpr
                        {
                        pushFollow(FOLLOW_valueExpr_in_createWindowExprInsert1041);
                        valueExpr();

                        state._fsp--;


                        }
                        break;

                }


                match(input, Token.UP, null); 
            }

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "createWindowExprInsert"


    // $ANTLR start "createSelectionList"
    // EsperEPL2Ast.g:203:1: createSelectionList : ^(s= CREATE_WINDOW_SELECT_EXPR createSelectionListElement ( createSelectionListElement )* ) ;
    public final void createSelectionList() throws RecognitionException {
        CommonTree s=null;

        try {
            // EsperEPL2Ast.g:204:2: ( ^(s= CREATE_WINDOW_SELECT_EXPR createSelectionListElement ( createSelectionListElement )* ) )
            // EsperEPL2Ast.g:204:4: ^(s= CREATE_WINDOW_SELECT_EXPR createSelectionListElement ( createSelectionListElement )* )
            {
            s=(CommonTree)match(input,CREATE_WINDOW_SELECT_EXPR,FOLLOW_CREATE_WINDOW_SELECT_EXPR_in_createSelectionList1058); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_createSelectionListElement_in_createSelectionList1060);
            createSelectionListElement();

            state._fsp--;

            // EsperEPL2Ast.g:204:61: ( createSelectionListElement )*
            loop67:
            do {
                int alt67=2;
                int LA67_0 = input.LA(1);

                if ( (LA67_0==SELECTION_ELEMENT_EXPR||LA67_0==WILDCARD_SELECT) ) {
                    alt67=1;
                }


                switch (alt67) {
            	case 1 :
            	    // EsperEPL2Ast.g:204:62: createSelectionListElement
            	    {
            	    pushFollow(FOLLOW_createSelectionListElement_in_createSelectionList1063);
            	    createSelectionListElement();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop67;
                }
            } while (true);

             leaveNode(s); 

            match(input, Token.UP, null); 

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "createSelectionList"


    // $ANTLR start "createColTypeList"
    // EsperEPL2Ast.g:207:1: createColTypeList : ^( CREATE_COL_TYPE_LIST createColTypeListElement ( createColTypeListElement )* ) ;
    public final void createColTypeList() throws RecognitionException {
        try {
            // EsperEPL2Ast.g:208:2: ( ^( CREATE_COL_TYPE_LIST createColTypeListElement ( createColTypeListElement )* ) )
            // EsperEPL2Ast.g:208:4: ^( CREATE_COL_TYPE_LIST createColTypeListElement ( createColTypeListElement )* )
            {
            match(input,CREATE_COL_TYPE_LIST,FOLLOW_CREATE_COL_TYPE_LIST_in_createColTypeList1082); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_createColTypeListElement_in_createColTypeList1084);
            createColTypeListElement();

            state._fsp--;

            // EsperEPL2Ast.g:208:52: ( createColTypeListElement )*
            loop68:
            do {
                int alt68=2;
                int LA68_0 = input.LA(1);

                if ( (LA68_0==CREATE_COL_TYPE) ) {
                    alt68=1;
                }


                switch (alt68) {
            	case 1 :
            	    // EsperEPL2Ast.g:208:53: createColTypeListElement
            	    {
            	    pushFollow(FOLLOW_createColTypeListElement_in_createColTypeList1087);
            	    createColTypeListElement();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop68;
                }
            } while (true);


            match(input, Token.UP, null); 

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "createColTypeList"


    // $ANTLR start "createColTypeListElement"
    // EsperEPL2Ast.g:211:1: createColTypeListElement : ^( CREATE_COL_TYPE CLASS_IDENT CLASS_IDENT ( LBRACK )? ) ;
    public final void createColTypeListElement() throws RecognitionException {
        try {
            // EsperEPL2Ast.g:212:2: ( ^( CREATE_COL_TYPE CLASS_IDENT CLASS_IDENT ( LBRACK )? ) )
            // EsperEPL2Ast.g:212:4: ^( CREATE_COL_TYPE CLASS_IDENT CLASS_IDENT ( LBRACK )? )
            {
            match(input,CREATE_COL_TYPE,FOLLOW_CREATE_COL_TYPE_in_createColTypeListElement1102); 

            match(input, Token.DOWN, null); 
            match(input,CLASS_IDENT,FOLLOW_CLASS_IDENT_in_createColTypeListElement1104); 
            match(input,CLASS_IDENT,FOLLOW_CLASS_IDENT_in_createColTypeListElement1106); 
            // EsperEPL2Ast.g:212:46: ( LBRACK )?
            int alt69=2;
            int LA69_0 = input.LA(1);

            if ( (LA69_0==LBRACK) ) {
                alt69=1;
            }
            switch (alt69) {
                case 1 :
                    // EsperEPL2Ast.g:212:46: LBRACK
                    {
                    match(input,LBRACK,FOLLOW_LBRACK_in_createColTypeListElement1108); 

                    }
                    break;

            }


            match(input, Token.UP, null); 

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "createColTypeListElement"


    // $ANTLR start "createSelectionListElement"
    // EsperEPL2Ast.g:215:1: createSelectionListElement : (w= WILDCARD_SELECT | ^(s= SELECTION_ELEMENT_EXPR ( ( eventPropertyExpr[true] ( IDENT )? ) | ( constant[true] IDENT ) ) ) );
    public final void createSelectionListElement() throws RecognitionException {
        CommonTree w=null;
        CommonTree s=null;

        try {
            // EsperEPL2Ast.g:216:2: (w= WILDCARD_SELECT | ^(s= SELECTION_ELEMENT_EXPR ( ( eventPropertyExpr[true] ( IDENT )? ) | ( constant[true] IDENT ) ) ) )
            int alt72=2;
            int LA72_0 = input.LA(1);

            if ( (LA72_0==WILDCARD_SELECT) ) {
                alt72=1;
            }
            else if ( (LA72_0==SELECTION_ELEMENT_EXPR) ) {
                alt72=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 72, 0, input);

                throw nvae;
            }
            switch (alt72) {
                case 1 :
                    // EsperEPL2Ast.g:216:4: w= WILDCARD_SELECT
                    {
                    w=(CommonTree)match(input,WILDCARD_SELECT,FOLLOW_WILDCARD_SELECT_in_createSelectionListElement1123); 
                     leaveNode(w); 

                    }
                    break;
                case 2 :
                    // EsperEPL2Ast.g:217:4: ^(s= SELECTION_ELEMENT_EXPR ( ( eventPropertyExpr[true] ( IDENT )? ) | ( constant[true] IDENT ) ) )
                    {
                    s=(CommonTree)match(input,SELECTION_ELEMENT_EXPR,FOLLOW_SELECTION_ELEMENT_EXPR_in_createSelectionListElement1133); 

                    match(input, Token.DOWN, null); 
                    // EsperEPL2Ast.g:217:31: ( ( eventPropertyExpr[true] ( IDENT )? ) | ( constant[true] IDENT ) )
                    int alt71=2;
                    int LA71_0 = input.LA(1);

                    if ( (LA71_0==EVENT_PROP_EXPR) ) {
                        alt71=1;
                    }
                    else if ( ((LA71_0>=INT_TYPE && LA71_0<=NULL_TYPE)) ) {
                        alt71=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 71, 0, input);

                        throw nvae;
                    }
                    switch (alt71) {
                        case 1 :
                            // EsperEPL2Ast.g:218:16: ( eventPropertyExpr[true] ( IDENT )? )
                            {
                            // EsperEPL2Ast.g:218:16: ( eventPropertyExpr[true] ( IDENT )? )
                            // EsperEPL2Ast.g:218:17: eventPropertyExpr[true] ( IDENT )?
                            {
                            pushFollow(FOLLOW_eventPropertyExpr_in_createSelectionListElement1153);
                            eventPropertyExpr(true);

                            state._fsp--;

                            // EsperEPL2Ast.g:218:41: ( IDENT )?
                            int alt70=2;
                            int LA70_0 = input.LA(1);

                            if ( (LA70_0==IDENT) ) {
                                alt70=1;
                            }
                            switch (alt70) {
                                case 1 :
                                    // EsperEPL2Ast.g:218:42: IDENT
                                    {
                                    match(input,IDENT,FOLLOW_IDENT_in_createSelectionListElement1157); 

                                    }
                                    break;

                            }


                            }


                            }
                            break;
                        case 2 :
                            // EsperEPL2Ast.g:219:16: ( constant[true] IDENT )
                            {
                            // EsperEPL2Ast.g:219:16: ( constant[true] IDENT )
                            // EsperEPL2Ast.g:219:17: constant[true] IDENT
                            {
                            pushFollow(FOLLOW_constant_in_createSelectionListElement1179);
                            constant(true);

                            state._fsp--;

                            match(input,IDENT,FOLLOW_IDENT_in_createSelectionListElement1182); 

                            }


                            }
                            break;

                    }

                     leaveNode(s); 

                    match(input, Token.UP, null); 

                    }
                    break;

            }
        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "createSelectionListElement"


    // $ANTLR start "createVariableExpr"
    // EsperEPL2Ast.g:223:1: createVariableExpr : ^(i= CREATE_VARIABLE_EXPR CLASS_IDENT IDENT ( IDENT )? ( LBRACK )? ( valueExpr )? ) ;
    public final void createVariableExpr() throws RecognitionException {
        CommonTree i=null;

        try {
            // EsperEPL2Ast.g:224:2: ( ^(i= CREATE_VARIABLE_EXPR CLASS_IDENT IDENT ( IDENT )? ( LBRACK )? ( valueExpr )? ) )
            // EsperEPL2Ast.g:224:4: ^(i= CREATE_VARIABLE_EXPR CLASS_IDENT IDENT ( IDENT )? ( LBRACK )? ( valueExpr )? )
            {
            i=(CommonTree)match(input,CREATE_VARIABLE_EXPR,FOLLOW_CREATE_VARIABLE_EXPR_in_createVariableExpr1218); 

            match(input, Token.DOWN, null); 
            match(input,CLASS_IDENT,FOLLOW_CLASS_IDENT_in_createVariableExpr1220); 
            match(input,IDENT,FOLLOW_IDENT_in_createVariableExpr1222); 
            // EsperEPL2Ast.g:224:47: ( IDENT )?
            int alt73=2;
            int LA73_0 = input.LA(1);

            if ( (LA73_0==IDENT) ) {
                alt73=1;
            }
            switch (alt73) {
                case 1 :
                    // EsperEPL2Ast.g:224:47: IDENT
                    {
                    match(input,IDENT,FOLLOW_IDENT_in_createVariableExpr1224); 

                    }
                    break;

            }

            // EsperEPL2Ast.g:224:54: ( LBRACK )?
            int alt74=2;
            int LA74_0 = input.LA(1);

            if ( (LA74_0==LBRACK) ) {
                alt74=1;
            }
            switch (alt74) {
                case 1 :
                    // EsperEPL2Ast.g:224:54: LBRACK
                    {
                    match(input,LBRACK,FOLLOW_LBRACK_in_createVariableExpr1227); 

                    }
                    break;

            }

            // EsperEPL2Ast.g:224:62: ( valueExpr )?
            int alt75=2;
            int LA75_0 = input.LA(1);

            if ( ((LA75_0>=IN_SET && LA75_0<=REGEXP)||LA75_0==NOT_EXPR||(LA75_0>=SUM && LA75_0<=AVG)||(LA75_0>=COALESCE && LA75_0<=COUNT)||(LA75_0>=CASE && LA75_0<=CASE2)||LA75_0==ISTREAM||(LA75_0>=PREVIOUS && LA75_0<=EXISTS)||(LA75_0>=INSTANCEOF && LA75_0<=CURRENT_TIMESTAMP)||LA75_0==NEWKW||(LA75_0>=EVAL_AND_EXPR && LA75_0<=EVAL_NOTEQUALS_GROUP_EXPR)||LA75_0==EVENT_PROP_EXPR||LA75_0==CONCAT||(LA75_0>=LIB_FUNC_CHAIN && LA75_0<=DOT_EXPR)||LA75_0==ARRAY_EXPR||(LA75_0>=NOT_IN_SET && LA75_0<=NOT_REGEXP)||(LA75_0>=IN_RANGE && LA75_0<=SUBSELECT_EXPR)||(LA75_0>=EXISTS_SUBSELECT_EXPR && LA75_0<=NOT_IN_SUBSELECT_EXPR)||LA75_0==SUBSTITUTION||(LA75_0>=FIRST_AGGREG && LA75_0<=WINDOW_AGGREG)||(LA75_0>=INT_TYPE && LA75_0<=NULL_TYPE)||(LA75_0>=JSON_OBJECT && LA75_0<=JSON_ARRAY)||LA75_0==STAR||(LA75_0>=LT && LA75_0<=GT)||(LA75_0>=BOR && LA75_0<=PLUS)||(LA75_0>=BAND && LA75_0<=BXOR)||(LA75_0>=LE && LA75_0<=GE)||(LA75_0>=MINUS && LA75_0<=MOD)||(LA75_0>=EVAL_IS_GROUP_EXPR && LA75_0<=EVAL_ISNOT_GROUP_EXPR)) ) {
                alt75=1;
            }
            switch (alt75) {
                case 1 :
                    // EsperEPL2Ast.g:224:62: valueExpr
                    {
                    pushFollow(FOLLOW_valueExpr_in_createVariableExpr1230);
                    valueExpr();

                    state._fsp--;


                    }
                    break;

            }

             leaveNode(i); 

            match(input, Token.UP, null); 

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "createVariableExpr"


    // $ANTLR start "createDataflow"
    // EsperEPL2Ast.g:230:1: createDataflow : ^(s= CREATE_DATAFLOW IDENT ( gop )+ ) ;
    public final void createDataflow() throws RecognitionException {
        CommonTree s=null;

        try {
            // EsperEPL2Ast.g:231:2: ( ^(s= CREATE_DATAFLOW IDENT ( gop )+ ) )
            // EsperEPL2Ast.g:231:4: ^(s= CREATE_DATAFLOW IDENT ( gop )+ )
            {
            s=(CommonTree)match(input,CREATE_DATAFLOW,FOLLOW_CREATE_DATAFLOW_in_createDataflow1255); 

            match(input, Token.DOWN, null); 
            match(input,IDENT,FOLLOW_IDENT_in_createDataflow1257); 
            // EsperEPL2Ast.g:231:30: ( gop )+
            int cnt76=0;
            loop76:
            do {
                int alt76=2;
                int LA76_0 = input.LA(1);

                if ( (LA76_0==CREATE_SCHEMA_EXPR||LA76_0==GOP) ) {
                    alt76=1;
                }


                switch (alt76) {
            	case 1 :
            	    // EsperEPL2Ast.g:231:30: gop
            	    {
            	    pushFollow(FOLLOW_gop_in_createDataflow1259);
            	    gop();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    if ( cnt76 >= 1 ) break loop76;
                        EarlyExitException eee =
                            new EarlyExitException(76, input);
                        throw eee;
                }
                cnt76++;
            } while (true);

             leaveNode(s); 

            match(input, Token.UP, null); 

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "createDataflow"


    // $ANTLR start "gop"
    // EsperEPL2Ast.g:234:1: gop : ( ^( GOP ( IDENT | SELECT ) ( gopParam )? ( gopOut )? ( gopDetail )? ( annotation[false] )* ) | createSchemaExpr[false] );
    public final void gop() throws RecognitionException {
        try {
            // EsperEPL2Ast.g:235:2: ( ^( GOP ( IDENT | SELECT ) ( gopParam )? ( gopOut )? ( gopDetail )? ( annotation[false] )* ) | createSchemaExpr[false] )
            int alt81=2;
            int LA81_0 = input.LA(1);

            if ( (LA81_0==GOP) ) {
                alt81=1;
            }
            else if ( (LA81_0==CREATE_SCHEMA_EXPR) ) {
                alt81=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 81, 0, input);

                throw nvae;
            }
            switch (alt81) {
                case 1 :
                    // EsperEPL2Ast.g:235:4: ^( GOP ( IDENT | SELECT ) ( gopParam )? ( gopOut )? ( gopDetail )? ( annotation[false] )* )
                    {
                    match(input,GOP,FOLLOW_GOP_in_gop1276); 

                    match(input, Token.DOWN, null); 
                    if ( input.LA(1)==SELECT||input.LA(1)==IDENT ) {
                        input.consume();
                        state.errorRecovery=false;
                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        throw mse;
                    }

                    // EsperEPL2Ast.g:235:25: ( gopParam )?
                    int alt77=2;
                    int LA77_0 = input.LA(1);

                    if ( (LA77_0==GOPPARAM) ) {
                        alt77=1;
                    }
                    switch (alt77) {
                        case 1 :
                            // EsperEPL2Ast.g:235:25: gopParam
                            {
                            pushFollow(FOLLOW_gopParam_in_gop1284);
                            gopParam();

                            state._fsp--;


                            }
                            break;

                    }

                    // EsperEPL2Ast.g:235:35: ( gopOut )?
                    int alt78=2;
                    int LA78_0 = input.LA(1);

                    if ( (LA78_0==GOPOUT) ) {
                        alt78=1;
                    }
                    switch (alt78) {
                        case 1 :
                            // EsperEPL2Ast.g:235:35: gopOut
                            {
                            pushFollow(FOLLOW_gopOut_in_gop1287);
                            gopOut();

                            state._fsp--;


                            }
                            break;

                    }

                    // EsperEPL2Ast.g:235:43: ( gopDetail )?
                    int alt79=2;
                    int LA79_0 = input.LA(1);

                    if ( (LA79_0==GOPCFG) ) {
                        alt79=1;
                    }
                    switch (alt79) {
                        case 1 :
                            // EsperEPL2Ast.g:235:43: gopDetail
                            {
                            pushFollow(FOLLOW_gopDetail_in_gop1290);
                            gopDetail();

                            state._fsp--;


                            }
                            break;

                    }

                    // EsperEPL2Ast.g:235:54: ( annotation[false] )*
                    loop80:
                    do {
                        int alt80=2;
                        int LA80_0 = input.LA(1);

                        if ( (LA80_0==ANNOTATION) ) {
                            alt80=1;
                        }


                        switch (alt80) {
                    	case 1 :
                    	    // EsperEPL2Ast.g:235:55: annotation[false]
                    	    {
                    	    pushFollow(FOLLOW_annotation_in_gop1294);
                    	    annotation(false);

                    	    state._fsp--;


                    	    }
                    	    break;

                    	default :
                    	    break loop80;
                        }
                    } while (true);


                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    // EsperEPL2Ast.g:236:4: createSchemaExpr[false]
                    {
                    pushFollow(FOLLOW_createSchemaExpr_in_gop1303);
                    createSchemaExpr(false);

                    state._fsp--;


                    }
                    break;

            }
        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "gop"


    // $ANTLR start "gopParam"
    // EsperEPL2Ast.g:239:1: gopParam : ^( GOPPARAM ( gopParamItem )* ) ;
    public final void gopParam() throws RecognitionException {
        try {
            // EsperEPL2Ast.g:240:2: ( ^( GOPPARAM ( gopParamItem )* ) )
            // EsperEPL2Ast.g:240:4: ^( GOPPARAM ( gopParamItem )* )
            {
            match(input,GOPPARAM,FOLLOW_GOPPARAM_in_gopParam1318); 

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // EsperEPL2Ast.g:240:15: ( gopParamItem )*
                loop82:
                do {
                    int alt82=2;
                    int LA82_0 = input.LA(1);

                    if ( (LA82_0==GOPPARAMITM) ) {
                        alt82=1;
                    }


                    switch (alt82) {
                	case 1 :
                	    // EsperEPL2Ast.g:240:15: gopParamItem
                	    {
                	    pushFollow(FOLLOW_gopParamItem_in_gopParam1320);
                	    gopParamItem();

                	    state._fsp--;


                	    }
                	    break;

                	default :
                	    break loop82;
                    }
                } while (true);


                match(input, Token.UP, null); 
            }

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "gopParam"


    // $ANTLR start "gopParamItem"
    // EsperEPL2Ast.g:243:1: gopParamItem : ^( GOPPARAMITM ( CLASS_IDENT )+ ( ^( AS IDENT ) )? ) ;
    public final void gopParamItem() throws RecognitionException {
        try {
            // EsperEPL2Ast.g:244:2: ( ^( GOPPARAMITM ( CLASS_IDENT )+ ( ^( AS IDENT ) )? ) )
            // EsperEPL2Ast.g:244:4: ^( GOPPARAMITM ( CLASS_IDENT )+ ( ^( AS IDENT ) )? )
            {
            match(input,GOPPARAMITM,FOLLOW_GOPPARAMITM_in_gopParamItem1334); 

            match(input, Token.DOWN, null); 
            // EsperEPL2Ast.g:244:18: ( CLASS_IDENT )+
            int cnt83=0;
            loop83:
            do {
                int alt83=2;
                int LA83_0 = input.LA(1);

                if ( (LA83_0==CLASS_IDENT) ) {
                    alt83=1;
                }


                switch (alt83) {
            	case 1 :
            	    // EsperEPL2Ast.g:244:18: CLASS_IDENT
            	    {
            	    match(input,CLASS_IDENT,FOLLOW_CLASS_IDENT_in_gopParamItem1336); 

            	    }
            	    break;

            	default :
            	    if ( cnt83 >= 1 ) break loop83;
                        EarlyExitException eee =
                            new EarlyExitException(83, input);
                        throw eee;
                }
                cnt83++;
            } while (true);

            // EsperEPL2Ast.g:244:31: ( ^( AS IDENT ) )?
            int alt84=2;
            int LA84_0 = input.LA(1);

            if ( (LA84_0==AS) ) {
                alt84=1;
            }
            switch (alt84) {
                case 1 :
                    // EsperEPL2Ast.g:244:32: ^( AS IDENT )
                    {
                    match(input,AS,FOLLOW_AS_in_gopParamItem1341); 

                    match(input, Token.DOWN, null); 
                    match(input,IDENT,FOLLOW_IDENT_in_gopParamItem1343); 

                    match(input, Token.UP, null); 

                    }
                    break;

            }


            match(input, Token.UP, null); 

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "gopParamItem"


    // $ANTLR start "gopOut"
    // EsperEPL2Ast.g:247:1: gopOut : ^( GOPOUT ( gopOutItem )* ) ;
    public final void gopOut() throws RecognitionException {
        try {
            // EsperEPL2Ast.g:248:2: ( ^( GOPOUT ( gopOutItem )* ) )
            // EsperEPL2Ast.g:248:4: ^( GOPOUT ( gopOutItem )* )
            {
            match(input,GOPOUT,FOLLOW_GOPOUT_in_gopOut1359); 

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // EsperEPL2Ast.g:248:13: ( gopOutItem )*
                loop85:
                do {
                    int alt85=2;
                    int LA85_0 = input.LA(1);

                    if ( (LA85_0==GOPOUTITM) ) {
                        alt85=1;
                    }


                    switch (alt85) {
                	case 1 :
                	    // EsperEPL2Ast.g:248:13: gopOutItem
                	    {
                	    pushFollow(FOLLOW_gopOutItem_in_gopOut1361);
                	    gopOutItem();

                	    state._fsp--;


                	    }
                	    break;

                	default :
                	    break loop85;
                    }
                } while (true);


                match(input, Token.UP, null); 
            }

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "gopOut"


    // $ANTLR start "gopOutItem"
    // EsperEPL2Ast.g:251:1: gopOutItem : ^( GOPOUTITM CLASS_IDENT ( gopOutTypeParam )* ) ;
    public final void gopOutItem() throws RecognitionException {
        try {
            // EsperEPL2Ast.g:252:2: ( ^( GOPOUTITM CLASS_IDENT ( gopOutTypeParam )* ) )
            // EsperEPL2Ast.g:252:4: ^( GOPOUTITM CLASS_IDENT ( gopOutTypeParam )* )
            {
            match(input,GOPOUTITM,FOLLOW_GOPOUTITM_in_gopOutItem1375); 

            match(input, Token.DOWN, null); 
            match(input,CLASS_IDENT,FOLLOW_CLASS_IDENT_in_gopOutItem1377); 
            // EsperEPL2Ast.g:252:28: ( gopOutTypeParam )*
            loop86:
            do {
                int alt86=2;
                int LA86_0 = input.LA(1);

                if ( (LA86_0==GOPOUTTYP) ) {
                    alt86=1;
                }


                switch (alt86) {
            	case 1 :
            	    // EsperEPL2Ast.g:252:28: gopOutTypeParam
            	    {
            	    pushFollow(FOLLOW_gopOutTypeParam_in_gopOutItem1379);
            	    gopOutTypeParam();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop86;
                }
            } while (true);


            match(input, Token.UP, null); 

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "gopOutItem"


    // $ANTLR start "gopOutTypeParam"
    // EsperEPL2Ast.g:255:1: gopOutTypeParam : ^( GOPOUTTYP ( ( CLASS_IDENT ( gopOutTypeParam )* ) | QUESTION ) ) ;
    public final void gopOutTypeParam() throws RecognitionException {
        try {
            // EsperEPL2Ast.g:256:2: ( ^( GOPOUTTYP ( ( CLASS_IDENT ( gopOutTypeParam )* ) | QUESTION ) ) )
            // EsperEPL2Ast.g:256:4: ^( GOPOUTTYP ( ( CLASS_IDENT ( gopOutTypeParam )* ) | QUESTION ) )
            {
            match(input,GOPOUTTYP,FOLLOW_GOPOUTTYP_in_gopOutTypeParam1394); 

            match(input, Token.DOWN, null); 
            // EsperEPL2Ast.g:256:16: ( ( CLASS_IDENT ( gopOutTypeParam )* ) | QUESTION )
            int alt88=2;
            int LA88_0 = input.LA(1);

            if ( (LA88_0==CLASS_IDENT) ) {
                alt88=1;
            }
            else if ( (LA88_0==QUESTION) ) {
                alt88=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 88, 0, input);

                throw nvae;
            }
            switch (alt88) {
                case 1 :
                    // EsperEPL2Ast.g:256:17: ( CLASS_IDENT ( gopOutTypeParam )* )
                    {
                    // EsperEPL2Ast.g:256:17: ( CLASS_IDENT ( gopOutTypeParam )* )
                    // EsperEPL2Ast.g:256:18: CLASS_IDENT ( gopOutTypeParam )*
                    {
                    match(input,CLASS_IDENT,FOLLOW_CLASS_IDENT_in_gopOutTypeParam1398); 
                    // EsperEPL2Ast.g:256:30: ( gopOutTypeParam )*
                    loop87:
                    do {
                        int alt87=2;
                        int LA87_0 = input.LA(1);

                        if ( (LA87_0==GOPOUTTYP) ) {
                            alt87=1;
                        }


                        switch (alt87) {
                    	case 1 :
                    	    // EsperEPL2Ast.g:256:30: gopOutTypeParam
                    	    {
                    	    pushFollow(FOLLOW_gopOutTypeParam_in_gopOutTypeParam1400);
                    	    gopOutTypeParam();

                    	    state._fsp--;


                    	    }
                    	    break;

                    	default :
                    	    break loop87;
                        }
                    } while (true);


                    }


                    }
                    break;
                case 2 :
                    // EsperEPL2Ast.g:256:50: QUESTION
                    {
                    match(input,QUESTION,FOLLOW_QUESTION_in_gopOutTypeParam1406); 

                    }
                    break;

            }


            match(input, Token.UP, null); 

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "gopOutTypeParam"


    // $ANTLR start "gopDetail"
    // EsperEPL2Ast.g:259:1: gopDetail : ^( GOPCFG ( gopConfig )+ ) ;
    public final void gopDetail() throws RecognitionException {
        try {
            // EsperEPL2Ast.g:260:2: ( ^( GOPCFG ( gopConfig )+ ) )
            // EsperEPL2Ast.g:260:4: ^( GOPCFG ( gopConfig )+ )
            {
            match(input,GOPCFG,FOLLOW_GOPCFG_in_gopDetail1420); 

            match(input, Token.DOWN, null); 
            // EsperEPL2Ast.g:260:13: ( gopConfig )+
            int cnt89=0;
            loop89:
            do {
                int alt89=2;
                int LA89_0 = input.LA(1);

                if ( ((LA89_0>=GOPCFGITM && LA89_0<=GOPCFGEPL)||LA89_0==GOPCFGEXP) ) {
                    alt89=1;
                }


                switch (alt89) {
            	case 1 :
            	    // EsperEPL2Ast.g:260:13: gopConfig
            	    {
            	    pushFollow(FOLLOW_gopConfig_in_gopDetail1422);
            	    gopConfig();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    if ( cnt89 >= 1 ) break loop89;
                        EarlyExitException eee =
                            new EarlyExitException(89, input);
                        throw eee;
                }
                cnt89++;
            } while (true);


            match(input, Token.UP, null); 

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "gopDetail"


    // $ANTLR start "gopConfig"
    // EsperEPL2Ast.g:263:1: gopConfig : ( ^(a= GOPCFGITM IDENT valueExpr ) | ^(b= GOPCFGEXP valueExpr ) | ^(c= GOPCFGEPL selectExpr ) );
    public final void gopConfig() throws RecognitionException {
        CommonTree a=null;
        CommonTree b=null;
        CommonTree c=null;

        try {
            // EsperEPL2Ast.g:264:2: ( ^(a= GOPCFGITM IDENT valueExpr ) | ^(b= GOPCFGEXP valueExpr ) | ^(c= GOPCFGEPL selectExpr ) )
            int alt90=3;
            switch ( input.LA(1) ) {
            case GOPCFGITM:
                {
                alt90=1;
                }
                break;
            case GOPCFGEXP:
                {
                alt90=2;
                }
                break;
            case GOPCFGEPL:
                {
                alt90=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 90, 0, input);

                throw nvae;
            }

            switch (alt90) {
                case 1 :
                    // EsperEPL2Ast.g:264:4: ^(a= GOPCFGITM IDENT valueExpr )
                    {
                    a=(CommonTree)match(input,GOPCFGITM,FOLLOW_GOPCFGITM_in_gopConfig1438); 

                    match(input, Token.DOWN, null); 
                    match(input,IDENT,FOLLOW_IDENT_in_gopConfig1440); 
                    pushFollow(FOLLOW_valueExpr_in_gopConfig1442);
                    valueExpr();

                    state._fsp--;

                     leaveNode(a); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    // EsperEPL2Ast.g:265:4: ^(b= GOPCFGEXP valueExpr )
                    {
                    b=(CommonTree)match(input,GOPCFGEXP,FOLLOW_GOPCFGEXP_in_gopConfig1453); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_valueExpr_in_gopConfig1455);
                    valueExpr();

                    state._fsp--;

                     leaveNode(b); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 3 :
                    // EsperEPL2Ast.g:266:4: ^(c= GOPCFGEPL selectExpr )
                    {
                    c=(CommonTree)match(input,GOPCFGEPL,FOLLOW_GOPCFGEPL_in_gopConfig1466); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_selectExpr_in_gopConfig1468);
                    selectExpr();

                    state._fsp--;

                     leaveNode(c); 

                    match(input, Token.UP, null); 

                    }
                    break;

            }
        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "gopConfig"


    // $ANTLR start "jsonvalue"
    // EsperEPL2Ast.g:269:1: jsonvalue : ( constant[false] | jsonobject[false] | jsonarray[false] );
    public final void jsonvalue() throws RecognitionException {
        try {
            // EsperEPL2Ast.g:270:2: ( constant[false] | jsonobject[false] | jsonarray[false] )
            int alt91=3;
            switch ( input.LA(1) ) {
            case INT_TYPE:
            case LONG_TYPE:
            case FLOAT_TYPE:
            case DOUBLE_TYPE:
            case STRING_TYPE:
            case BOOL_TYPE:
            case NULL_TYPE:
                {
                alt91=1;
                }
                break;
            case JSON_OBJECT:
                {
                alt91=2;
                }
                break;
            case JSON_ARRAY:
                {
                alt91=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 91, 0, input);

                throw nvae;
            }

            switch (alt91) {
                case 1 :
                    // EsperEPL2Ast.g:270:5: constant[false]
                    {
                    pushFollow(FOLLOW_constant_in_jsonvalue1484);
                    constant(false);

                    state._fsp--;


                    }
                    break;
                case 2 :
                    // EsperEPL2Ast.g:271:5: jsonobject[false]
                    {
                    pushFollow(FOLLOW_jsonobject_in_jsonvalue1491);
                    jsonobject(false);

                    state._fsp--;


                    }
                    break;
                case 3 :
                    // EsperEPL2Ast.g:272:5: jsonarray[false]
                    {
                    pushFollow(FOLLOW_jsonarray_in_jsonvalue1498);
                    jsonarray(false);

                    state._fsp--;


                    }
                    break;

            }
        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "jsonvalue"


    // $ANTLR start "jsonobject"
    // EsperEPL2Ast.g:275:1: jsonobject[boolean isLeaveNode] : ^(o= JSON_OBJECT ( jsonpair )* ) ;
    public final void jsonobject(boolean isLeaveNode) throws RecognitionException {
        CommonTree o=null;

        try {
            // EsperEPL2Ast.g:276:2: ( ^(o= JSON_OBJECT ( jsonpair )* ) )
            // EsperEPL2Ast.g:276:5: ^(o= JSON_OBJECT ( jsonpair )* )
            {
            o=(CommonTree)match(input,JSON_OBJECT,FOLLOW_JSON_OBJECT_in_jsonobject1515); 

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // EsperEPL2Ast.g:276:21: ( jsonpair )*
                loop92:
                do {
                    int alt92=2;
                    int LA92_0 = input.LA(1);

                    if ( (LA92_0==JSON_FIELD) ) {
                        alt92=1;
                    }


                    switch (alt92) {
                	case 1 :
                	    // EsperEPL2Ast.g:276:21: jsonpair
                	    {
                	    pushFollow(FOLLOW_jsonpair_in_jsonobject1517);
                	    jsonpair();

                	    state._fsp--;


                	    }
                	    break;

                	default :
                	    break loop92;
                    }
                } while (true);

                 if (isLeaveNode) leaveNode(o); 

                match(input, Token.UP, null); 
            }

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "jsonobject"


    // $ANTLR start "jsonarray"
    // EsperEPL2Ast.g:279:1: jsonarray[boolean isLeaveNode] : ^(a= JSON_ARRAY ( jsonvalue )* ) ;
    public final void jsonarray(boolean isLeaveNode) throws RecognitionException {
        CommonTree a=null;

        try {
            // EsperEPL2Ast.g:280:9: ( ^(a= JSON_ARRAY ( jsonvalue )* ) )
            // EsperEPL2Ast.g:280:12: ^(a= JSON_ARRAY ( jsonvalue )* )
            {
            a=(CommonTree)match(input,JSON_ARRAY,FOLLOW_JSON_ARRAY_in_jsonarray1546); 

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // EsperEPL2Ast.g:280:27: ( jsonvalue )*
                loop93:
                do {
                    int alt93=2;
                    int LA93_0 = input.LA(1);

                    if ( ((LA93_0>=INT_TYPE && LA93_0<=NULL_TYPE)||(LA93_0>=JSON_OBJECT && LA93_0<=JSON_ARRAY)) ) {
                        alt93=1;
                    }


                    switch (alt93) {
                	case 1 :
                	    // EsperEPL2Ast.g:280:27: jsonvalue
                	    {
                	    pushFollow(FOLLOW_jsonvalue_in_jsonarray1548);
                	    jsonvalue();

                	    state._fsp--;


                	    }
                	    break;

                	default :
                	    break loop93;
                    }
                } while (true);

                 if (isLeaveNode) leaveNode(a); 

                match(input, Token.UP, null); 
            }

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "jsonarray"


    // $ANTLR start "jsonpair"
    // EsperEPL2Ast.g:283:1: jsonpair : ^( JSON_FIELD ( constant[false] | IDENT ) jsonvalue ) ;
    public final void jsonpair() throws RecognitionException {
        try {
            // EsperEPL2Ast.g:284:2: ( ^( JSON_FIELD ( constant[false] | IDENT ) jsonvalue ) )
            // EsperEPL2Ast.g:284:5: ^( JSON_FIELD ( constant[false] | IDENT ) jsonvalue )
            {
            match(input,JSON_FIELD,FOLLOW_JSON_FIELD_in_jsonpair1566); 

            match(input, Token.DOWN, null); 
            // EsperEPL2Ast.g:284:18: ( constant[false] | IDENT )
            int alt94=2;
            int LA94_0 = input.LA(1);

            if ( ((LA94_0>=INT_TYPE && LA94_0<=NULL_TYPE)) ) {
                alt94=1;
            }
            else if ( (LA94_0==IDENT) ) {
                alt94=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 94, 0, input);

                throw nvae;
            }
            switch (alt94) {
                case 1 :
                    // EsperEPL2Ast.g:284:19: constant[false]
                    {
                    pushFollow(FOLLOW_constant_in_jsonpair1569);
                    constant(false);

                    state._fsp--;


                    }
                    break;
                case 2 :
                    // EsperEPL2Ast.g:284:37: IDENT
                    {
                    match(input,IDENT,FOLLOW_IDENT_in_jsonpair1574); 

                    }
                    break;

            }

            pushFollow(FOLLOW_jsonvalue_in_jsonpair1577);
            jsonvalue();

            state._fsp--;


            match(input, Token.UP, null); 

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "jsonpair"


    // $ANTLR start "createContextExpr"
    // EsperEPL2Ast.g:290:1: createContextExpr : ^(s= CREATE_CTX IDENT createContextDetail ) ;
    public final void createContextExpr() throws RecognitionException {
        CommonTree s=null;

        try {
            // EsperEPL2Ast.g:291:2: ( ^(s= CREATE_CTX IDENT createContextDetail ) )
            // EsperEPL2Ast.g:291:4: ^(s= CREATE_CTX IDENT createContextDetail )
            {
            s=(CommonTree)match(input,CREATE_CTX,FOLLOW_CREATE_CTX_in_createContextExpr1597); 

            match(input, Token.DOWN, null); 
            match(input,IDENT,FOLLOW_IDENT_in_createContextExpr1599); 
            pushFollow(FOLLOW_createContextDetail_in_createContextExpr1601);
            createContextDetail();

            state._fsp--;

             leaveNode(s); 

            match(input, Token.UP, null); 

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "createContextExpr"


    // $ANTLR start "createContextDetail"
    // EsperEPL2Ast.g:294:1: createContextDetail : ( ^( CREATE_CTX_FIXED createContextRangePoint createContextRangePoint ) | ^( CREATE_CTX_INIT createContextRangePoint createContextRangePoint ) | ^( CREATE_CTX_PART ( createContextPartitionItem )+ ) | ^( CREATE_CTX_CAT ( createContextCategoryItem )+ eventFilterExpr[false] ) | ^( CREATE_CTX_COAL ( createContextCoalesceItem )+ IDENT number ( IDENT )? ) | ^( CREATE_CTX_NESTED createContextNested ( createContextNested )+ ) );
    public final void createContextDetail() throws RecognitionException {
        try {
            // EsperEPL2Ast.g:295:2: ( ^( CREATE_CTX_FIXED createContextRangePoint createContextRangePoint ) | ^( CREATE_CTX_INIT createContextRangePoint createContextRangePoint ) | ^( CREATE_CTX_PART ( createContextPartitionItem )+ ) | ^( CREATE_CTX_CAT ( createContextCategoryItem )+ eventFilterExpr[false] ) | ^( CREATE_CTX_COAL ( createContextCoalesceItem )+ IDENT number ( IDENT )? ) | ^( CREATE_CTX_NESTED createContextNested ( createContextNested )+ ) )
            int alt100=6;
            switch ( input.LA(1) ) {
            case CREATE_CTX_FIXED:
                {
                alt100=1;
                }
                break;
            case CREATE_CTX_INIT:
                {
                alt100=2;
                }
                break;
            case CREATE_CTX_PART:
                {
                alt100=3;
                }
                break;
            case CREATE_CTX_CAT:
                {
                alt100=4;
                }
                break;
            case CREATE_CTX_COAL:
                {
                alt100=5;
                }
                break;
            case CREATE_CTX_NESTED:
                {
                alt100=6;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 100, 0, input);

                throw nvae;
            }

            switch (alt100) {
                case 1 :
                    // EsperEPL2Ast.g:295:4: ^( CREATE_CTX_FIXED createContextRangePoint createContextRangePoint )
                    {
                    match(input,CREATE_CTX_FIXED,FOLLOW_CREATE_CTX_FIXED_in_createContextDetail1617); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_createContextRangePoint_in_createContextDetail1619);
                    createContextRangePoint();

                    state._fsp--;

                    pushFollow(FOLLOW_createContextRangePoint_in_createContextDetail1621);
                    createContextRangePoint();

                    state._fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    // EsperEPL2Ast.g:296:4: ^( CREATE_CTX_INIT createContextRangePoint createContextRangePoint )
                    {
                    match(input,CREATE_CTX_INIT,FOLLOW_CREATE_CTX_INIT_in_createContextDetail1628); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_createContextRangePoint_in_createContextDetail1630);
                    createContextRangePoint();

                    state._fsp--;

                    pushFollow(FOLLOW_createContextRangePoint_in_createContextDetail1632);
                    createContextRangePoint();

                    state._fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 3 :
                    // EsperEPL2Ast.g:297:4: ^( CREATE_CTX_PART ( createContextPartitionItem )+ )
                    {
                    match(input,CREATE_CTX_PART,FOLLOW_CREATE_CTX_PART_in_createContextDetail1639); 

                    match(input, Token.DOWN, null); 
                    // EsperEPL2Ast.g:297:22: ( createContextPartitionItem )+
                    int cnt95=0;
                    loop95:
                    do {
                        int alt95=2;
                        int LA95_0 = input.LA(1);

                        if ( (LA95_0==PARTITIONITEM) ) {
                            alt95=1;
                        }


                        switch (alt95) {
                    	case 1 :
                    	    // EsperEPL2Ast.g:297:22: createContextPartitionItem
                    	    {
                    	    pushFollow(FOLLOW_createContextPartitionItem_in_createContextDetail1641);
                    	    createContextPartitionItem();

                    	    state._fsp--;


                    	    }
                    	    break;

                    	default :
                    	    if ( cnt95 >= 1 ) break loop95;
                                EarlyExitException eee =
                                    new EarlyExitException(95, input);
                                throw eee;
                        }
                        cnt95++;
                    } while (true);


                    match(input, Token.UP, null); 

                    }
                    break;
                case 4 :
                    // EsperEPL2Ast.g:298:4: ^( CREATE_CTX_CAT ( createContextCategoryItem )+ eventFilterExpr[false] )
                    {
                    match(input,CREATE_CTX_CAT,FOLLOW_CREATE_CTX_CAT_in_createContextDetail1649); 

                    match(input, Token.DOWN, null); 
                    // EsperEPL2Ast.g:298:21: ( createContextCategoryItem )+
                    int cnt96=0;
                    loop96:
                    do {
                        int alt96=2;
                        int LA96_0 = input.LA(1);

                        if ( (LA96_0==CREATE_CTX_CATITEM) ) {
                            alt96=1;
                        }


                        switch (alt96) {
                    	case 1 :
                    	    // EsperEPL2Ast.g:298:21: createContextCategoryItem
                    	    {
                    	    pushFollow(FOLLOW_createContextCategoryItem_in_createContextDetail1651);
                    	    createContextCategoryItem();

                    	    state._fsp--;


                    	    }
                    	    break;

                    	default :
                    	    if ( cnt96 >= 1 ) break loop96;
                                EarlyExitException eee =
                                    new EarlyExitException(96, input);
                                throw eee;
                        }
                        cnt96++;
                    } while (true);

                    pushFollow(FOLLOW_eventFilterExpr_in_createContextDetail1654);
                    eventFilterExpr(false);

                    state._fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 5 :
                    // EsperEPL2Ast.g:299:4: ^( CREATE_CTX_COAL ( createContextCoalesceItem )+ IDENT number ( IDENT )? )
                    {
                    match(input,CREATE_CTX_COAL,FOLLOW_CREATE_CTX_COAL_in_createContextDetail1662); 

                    match(input, Token.DOWN, null); 
                    // EsperEPL2Ast.g:299:22: ( createContextCoalesceItem )+
                    int cnt97=0;
                    loop97:
                    do {
                        int alt97=2;
                        int LA97_0 = input.LA(1);

                        if ( (LA97_0==COALESCE) ) {
                            alt97=1;
                        }


                        switch (alt97) {
                    	case 1 :
                    	    // EsperEPL2Ast.g:299:22: createContextCoalesceItem
                    	    {
                    	    pushFollow(FOLLOW_createContextCoalesceItem_in_createContextDetail1664);
                    	    createContextCoalesceItem();

                    	    state._fsp--;


                    	    }
                    	    break;

                    	default :
                    	    if ( cnt97 >= 1 ) break loop97;
                                EarlyExitException eee =
                                    new EarlyExitException(97, input);
                                throw eee;
                        }
                        cnt97++;
                    } while (true);

                    match(input,IDENT,FOLLOW_IDENT_in_createContextDetail1667); 
                    pushFollow(FOLLOW_number_in_createContextDetail1669);
                    number();

                    state._fsp--;

                    // EsperEPL2Ast.g:299:62: ( IDENT )?
                    int alt98=2;
                    int LA98_0 = input.LA(1);

                    if ( (LA98_0==IDENT) ) {
                        alt98=1;
                    }
                    switch (alt98) {
                        case 1 :
                            // EsperEPL2Ast.g:299:62: IDENT
                            {
                            match(input,IDENT,FOLLOW_IDENT_in_createContextDetail1671); 

                            }
                            break;

                    }


                    match(input, Token.UP, null); 

                    }
                    break;
                case 6 :
                    // EsperEPL2Ast.g:300:4: ^( CREATE_CTX_NESTED createContextNested ( createContextNested )+ )
                    {
                    match(input,CREATE_CTX_NESTED,FOLLOW_CREATE_CTX_NESTED_in_createContextDetail1679); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_createContextNested_in_createContextDetail1681);
                    createContextNested();

                    state._fsp--;

                    // EsperEPL2Ast.g:300:44: ( createContextNested )+
                    int cnt99=0;
                    loop99:
                    do {
                        int alt99=2;
                        int LA99_0 = input.LA(1);

                        if ( (LA99_0==CREATE_CTX) ) {
                            alt99=1;
                        }


                        switch (alt99) {
                    	case 1 :
                    	    // EsperEPL2Ast.g:300:44: createContextNested
                    	    {
                    	    pushFollow(FOLLOW_createContextNested_in_createContextDetail1683);
                    	    createContextNested();

                    	    state._fsp--;


                    	    }
                    	    break;

                    	default :
                    	    if ( cnt99 >= 1 ) break loop99;
                                EarlyExitException eee =
                                    new EarlyExitException(99, input);
                                throw eee;
                        }
                        cnt99++;
                    } while (true);


                    match(input, Token.UP, null); 

                    }
                    break;

            }
        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "createContextDetail"


    // $ANTLR start "createContextRangePoint"
    // EsperEPL2Ast.g:303:1: createContextRangePoint : ( createContextFilter | ^( CREATE_CTX_PATTERN patternInclusionExpression ( IDENT )? ) | crontabLimitParameterSet | ^( AFTER timePeriod ) );
    public final void createContextRangePoint() throws RecognitionException {
        try {
            // EsperEPL2Ast.g:304:2: ( createContextFilter | ^( CREATE_CTX_PATTERN patternInclusionExpression ( IDENT )? ) | crontabLimitParameterSet | ^( AFTER timePeriod ) )
            int alt102=4;
            switch ( input.LA(1) ) {
            case STREAM_EXPR:
                {
                alt102=1;
                }
                break;
            case CREATE_CTX_PATTERN:
                {
                alt102=2;
                }
                break;
            case CRONTAB_LIMIT_EXPR_PARAM:
                {
                alt102=3;
                }
                break;
            case AFTER:
                {
                alt102=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 102, 0, input);

                throw nvae;
            }

            switch (alt102) {
                case 1 :
                    // EsperEPL2Ast.g:304:4: createContextFilter
                    {
                    pushFollow(FOLLOW_createContextFilter_in_createContextRangePoint1696);
                    createContextFilter();

                    state._fsp--;


                    }
                    break;
                case 2 :
                    // EsperEPL2Ast.g:305:5: ^( CREATE_CTX_PATTERN patternInclusionExpression ( IDENT )? )
                    {
                    match(input,CREATE_CTX_PATTERN,FOLLOW_CREATE_CTX_PATTERN_in_createContextRangePoint1704); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_patternInclusionExpression_in_createContextRangePoint1706);
                    patternInclusionExpression();

                    state._fsp--;

                    // EsperEPL2Ast.g:305:53: ( IDENT )?
                    int alt101=2;
                    int LA101_0 = input.LA(1);

                    if ( (LA101_0==IDENT) ) {
                        alt101=1;
                    }
                    switch (alt101) {
                        case 1 :
                            // EsperEPL2Ast.g:305:53: IDENT
                            {
                            match(input,IDENT,FOLLOW_IDENT_in_createContextRangePoint1708); 

                            }
                            break;

                    }


                    match(input, Token.UP, null); 

                    }
                    break;
                case 3 :
                    // EsperEPL2Ast.g:306:4: crontabLimitParameterSet
                    {
                    pushFollow(FOLLOW_crontabLimitParameterSet_in_createContextRangePoint1715);
                    crontabLimitParameterSet();

                    state._fsp--;


                    }
                    break;
                case 4 :
                    // EsperEPL2Ast.g:307:4: ^( AFTER timePeriod )
                    {
                    match(input,AFTER,FOLLOW_AFTER_in_createContextRangePoint1721); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_timePeriod_in_createContextRangePoint1723);
                    timePeriod();

                    state._fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;

            }
        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "createContextRangePoint"


    // $ANTLR start "createContextNested"
    // EsperEPL2Ast.g:310:1: createContextNested : ^(s= CREATE_CTX IDENT createContextDetail ) ;
    public final void createContextNested() throws RecognitionException {
        CommonTree s=null;

        try {
            // EsperEPL2Ast.g:311:2: ( ^(s= CREATE_CTX IDENT createContextDetail ) )
            // EsperEPL2Ast.g:311:4: ^(s= CREATE_CTX IDENT createContextDetail )
            {
            s=(CommonTree)match(input,CREATE_CTX,FOLLOW_CREATE_CTX_in_createContextNested1739); 

            match(input, Token.DOWN, null); 
            match(input,IDENT,FOLLOW_IDENT_in_createContextNested1741); 
            pushFollow(FOLLOW_createContextDetail_in_createContextNested1743);
            createContextDetail();

            state._fsp--;


            match(input, Token.UP, null); 

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "createContextNested"


    // $ANTLR start "createContextFilter"
    // EsperEPL2Ast.g:314:1: createContextFilter : ^( STREAM_EXPR eventFilterExpr[false] ( IDENT )? ) ;
    public final void createContextFilter() throws RecognitionException {
        try {
            // EsperEPL2Ast.g:315:2: ( ^( STREAM_EXPR eventFilterExpr[false] ( IDENT )? ) )
            // EsperEPL2Ast.g:315:4: ^( STREAM_EXPR eventFilterExpr[false] ( IDENT )? )
            {
            match(input,STREAM_EXPR,FOLLOW_STREAM_EXPR_in_createContextFilter1756); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_eventFilterExpr_in_createContextFilter1758);
            eventFilterExpr(false);

            state._fsp--;

            // EsperEPL2Ast.g:315:41: ( IDENT )?
            int alt103=2;
            int LA103_0 = input.LA(1);

            if ( (LA103_0==IDENT) ) {
                alt103=1;
            }
            switch (alt103) {
                case 1 :
                    // EsperEPL2Ast.g:315:41: IDENT
                    {
                    match(input,IDENT,FOLLOW_IDENT_in_createContextFilter1761); 

                    }
                    break;

            }


            match(input, Token.UP, null); 

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "createContextFilter"


    // $ANTLR start "createContextPartitionItem"
    // EsperEPL2Ast.g:318:1: createContextPartitionItem : ^( PARTITIONITEM eventFilterExpr[false] ( eventPropertyExpr[false] )+ ) ;
    public final void createContextPartitionItem() throws RecognitionException {
        try {
            // EsperEPL2Ast.g:319:2: ( ^( PARTITIONITEM eventFilterExpr[false] ( eventPropertyExpr[false] )+ ) )
            // EsperEPL2Ast.g:319:4: ^( PARTITIONITEM eventFilterExpr[false] ( eventPropertyExpr[false] )+ )
            {
            match(input,PARTITIONITEM,FOLLOW_PARTITIONITEM_in_createContextPartitionItem1777); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_eventFilterExpr_in_createContextPartitionItem1779);
            eventFilterExpr(false);

            state._fsp--;

            // EsperEPL2Ast.g:319:43: ( eventPropertyExpr[false] )+
            int cnt104=0;
            loop104:
            do {
                int alt104=2;
                int LA104_0 = input.LA(1);

                if ( (LA104_0==EVENT_PROP_EXPR) ) {
                    alt104=1;
                }


                switch (alt104) {
            	case 1 :
            	    // EsperEPL2Ast.g:319:43: eventPropertyExpr[false]
            	    {
            	    pushFollow(FOLLOW_eventPropertyExpr_in_createContextPartitionItem1782);
            	    eventPropertyExpr(false);

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    if ( cnt104 >= 1 ) break loop104;
                        EarlyExitException eee =
                            new EarlyExitException(104, input);
                        throw eee;
                }
                cnt104++;
            } while (true);


            match(input, Token.UP, null); 

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "createContextPartitionItem"


    // $ANTLR start "createContextCoalesceItem"
    // EsperEPL2Ast.g:322:1: createContextCoalesceItem : ^( COALESCE libFunctionWithClass eventFilterExpr[false] ) ;
    public final void createContextCoalesceItem() throws RecognitionException {
        try {
            // EsperEPL2Ast.g:323:2: ( ^( COALESCE libFunctionWithClass eventFilterExpr[false] ) )
            // EsperEPL2Ast.g:323:4: ^( COALESCE libFunctionWithClass eventFilterExpr[false] )
            {
            match(input,COALESCE,FOLLOW_COALESCE_in_createContextCoalesceItem1799); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_libFunctionWithClass_in_createContextCoalesceItem1801);
            libFunctionWithClass();

            state._fsp--;

            pushFollow(FOLLOW_eventFilterExpr_in_createContextCoalesceItem1803);
            eventFilterExpr(false);

            state._fsp--;


            match(input, Token.UP, null); 

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "createContextCoalesceItem"


    // $ANTLR start "createContextCategoryItem"
    // EsperEPL2Ast.g:326:1: createContextCategoryItem : ^( CREATE_CTX_CATITEM valueExpr IDENT ) ;
    public final void createContextCategoryItem() throws RecognitionException {
        try {
            // EsperEPL2Ast.g:327:2: ( ^( CREATE_CTX_CATITEM valueExpr IDENT ) )
            // EsperEPL2Ast.g:327:4: ^( CREATE_CTX_CATITEM valueExpr IDENT )
            {
            match(input,CREATE_CTX_CATITEM,FOLLOW_CREATE_CTX_CATITEM_in_createContextCategoryItem1819); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_valueExpr_in_createContextCategoryItem1821);
            valueExpr();

            state._fsp--;

            match(input,IDENT,FOLLOW_IDENT_in_createContextCategoryItem1823); 

            match(input, Token.UP, null); 

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "createContextCategoryItem"


    // $ANTLR start "createExpr"
    // EsperEPL2Ast.g:330:1: createExpr : ^(s= CREATE_EXPR expressionDecl[false] ) ;
    public final void createExpr() throws RecognitionException {
        CommonTree s=null;

        try {
            // EsperEPL2Ast.g:331:2: ( ^(s= CREATE_EXPR expressionDecl[false] ) )
            // EsperEPL2Ast.g:331:4: ^(s= CREATE_EXPR expressionDecl[false] )
            {
            s=(CommonTree)match(input,CREATE_EXPR,FOLLOW_CREATE_EXPR_in_createExpr1840); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_expressionDecl_in_createExpr1842);
            expressionDecl(false);

            state._fsp--;

             leaveNode(s); 

            match(input, Token.UP, null); 

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "createExpr"


    // $ANTLR start "createSchemaExpr"
    // EsperEPL2Ast.g:334:1: createSchemaExpr[boolean isLeaveNode] : ^(s= CREATE_SCHEMA_EXPR createSchemaDef ( IDENT )? ) ;
    public final void createSchemaExpr(boolean isLeaveNode) throws RecognitionException {
        CommonTree s=null;

        try {
            // EsperEPL2Ast.g:335:2: ( ^(s= CREATE_SCHEMA_EXPR createSchemaDef ( IDENT )? ) )
            // EsperEPL2Ast.g:335:4: ^(s= CREATE_SCHEMA_EXPR createSchemaDef ( IDENT )? )
            {
            s=(CommonTree)match(input,CREATE_SCHEMA_EXPR,FOLLOW_CREATE_SCHEMA_EXPR_in_createSchemaExpr1861); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_createSchemaDef_in_createSchemaExpr1863);
            createSchemaDef();

            state._fsp--;

            // EsperEPL2Ast.g:335:43: ( IDENT )?
            int alt105=2;
            int LA105_0 = input.LA(1);

            if ( (LA105_0==IDENT) ) {
                alt105=1;
            }
            switch (alt105) {
                case 1 :
                    // EsperEPL2Ast.g:335:43: IDENT
                    {
                    match(input,IDENT,FOLLOW_IDENT_in_createSchemaExpr1865); 

                    }
                    break;

            }

             if (isLeaveNode) leaveNode(s); 

            match(input, Token.UP, null); 

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "createSchemaExpr"


    // $ANTLR start "createSchemaDef"
    // EsperEPL2Ast.g:338:1: createSchemaDef : ^( CREATE_SCHEMA_DEF IDENT ( variantList | ( createColTypeList )? ) ( createSchemaQual )* ) ;
    public final void createSchemaDef() throws RecognitionException {
        try {
            // EsperEPL2Ast.g:339:2: ( ^( CREATE_SCHEMA_DEF IDENT ( variantList | ( createColTypeList )? ) ( createSchemaQual )* ) )
            // EsperEPL2Ast.g:339:4: ^( CREATE_SCHEMA_DEF IDENT ( variantList | ( createColTypeList )? ) ( createSchemaQual )* )
            {
            match(input,CREATE_SCHEMA_DEF,FOLLOW_CREATE_SCHEMA_DEF_in_createSchemaDef1882); 

            match(input, Token.DOWN, null); 
            match(input,IDENT,FOLLOW_IDENT_in_createSchemaDef1884); 
            // EsperEPL2Ast.g:339:30: ( variantList | ( createColTypeList )? )
            int alt107=2;
            int LA107_0 = input.LA(1);

            if ( (LA107_0==VARIANT_LIST) ) {
                alt107=1;
            }
            else if ( (LA107_0==UP||LA107_0==CREATE_COL_TYPE_LIST||LA107_0==CREATE_SCHEMA_EXPR_QUAL) ) {
                alt107=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 107, 0, input);

                throw nvae;
            }
            switch (alt107) {
                case 1 :
                    // EsperEPL2Ast.g:339:31: variantList
                    {
                    pushFollow(FOLLOW_variantList_in_createSchemaDef1887);
                    variantList();

                    state._fsp--;


                    }
                    break;
                case 2 :
                    // EsperEPL2Ast.g:339:43: ( createColTypeList )?
                    {
                    // EsperEPL2Ast.g:339:43: ( createColTypeList )?
                    int alt106=2;
                    int LA106_0 = input.LA(1);

                    if ( (LA106_0==CREATE_COL_TYPE_LIST) ) {
                        alt106=1;
                    }
                    switch (alt106) {
                        case 1 :
                            // EsperEPL2Ast.g:339:43: createColTypeList
                            {
                            pushFollow(FOLLOW_createColTypeList_in_createSchemaDef1889);
                            createColTypeList();

                            state._fsp--;


                            }
                            break;

                    }


                    }
                    break;

            }

            // EsperEPL2Ast.g:339:63: ( createSchemaQual )*
            loop108:
            do {
                int alt108=2;
                int LA108_0 = input.LA(1);

                if ( (LA108_0==CREATE_SCHEMA_EXPR_QUAL) ) {
                    alt108=1;
                }


                switch (alt108) {
            	case 1 :
            	    // EsperEPL2Ast.g:339:63: createSchemaQual
            	    {
            	    pushFollow(FOLLOW_createSchemaQual_in_createSchemaDef1893);
            	    createSchemaQual();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop108;
                }
            } while (true);


            match(input, Token.UP, null); 

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "createSchemaDef"


    // $ANTLR start "createSchemaQual"
    // EsperEPL2Ast.g:342:1: createSchemaQual : ^( CREATE_SCHEMA_EXPR_QUAL IDENT exprCol ) ;
    public final void createSchemaQual() throws RecognitionException {
        try {
            // EsperEPL2Ast.g:343:2: ( ^( CREATE_SCHEMA_EXPR_QUAL IDENT exprCol ) )
            // EsperEPL2Ast.g:343:4: ^( CREATE_SCHEMA_EXPR_QUAL IDENT exprCol )
            {
            match(input,CREATE_SCHEMA_EXPR_QUAL,FOLLOW_CREATE_SCHEMA_EXPR_QUAL_in_createSchemaQual1908); 

            match(input, Token.DOWN, null); 
            match(input,IDENT,FOLLOW_IDENT_in_createSchemaQual1910); 
            pushFollow(FOLLOW_exprCol_in_createSchemaQual1912);
            exprCol();

            state._fsp--;


            match(input, Token.UP, null); 

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "createSchemaQual"


    // $ANTLR start "variantList"
    // EsperEPL2Ast.g:346:1: variantList : ^( VARIANT_LIST ( STAR | CLASS_IDENT )+ ) ;
    public final void variantList() throws RecognitionException {
        try {
            // EsperEPL2Ast.g:347:2: ( ^( VARIANT_LIST ( STAR | CLASS_IDENT )+ ) )
            // EsperEPL2Ast.g:347:4: ^( VARIANT_LIST ( STAR | CLASS_IDENT )+ )
            {
            match(input,VARIANT_LIST,FOLLOW_VARIANT_LIST_in_variantList1928); 

            match(input, Token.DOWN, null); 
            // EsperEPL2Ast.g:347:19: ( STAR | CLASS_IDENT )+
            int cnt109=0;
            loop109:
            do {
                int alt109=2;
                int LA109_0 = input.LA(1);

                if ( (LA109_0==CLASS_IDENT||LA109_0==STAR) ) {
                    alt109=1;
                }


                switch (alt109) {
            	case 1 :
            	    // EsperEPL2Ast.g:
            	    {
            	    if ( input.LA(1)==CLASS_IDENT||input.LA(1)==STAR ) {
            	        input.consume();
            	        state.errorRecovery=false;
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    if ( cnt109 >= 1 ) break loop109;
                        EarlyExitException eee =
                            new EarlyExitException(109, input);
                        throw eee;
                }
                cnt109++;
            } while (true);


            match(input, Token.UP, null); 

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "variantList"


    // $ANTLR start "selectExpr"
    // EsperEPL2Ast.g:350:1: selectExpr : ( insertIntoExpr )? selectClause fromClause ( matchRecogClause )? ( whereClause[true] )? ( groupByClause )? ( havingClause )? ( outputLimitExpr )? ( orderByClause )? ( rowLimitClause )? ;
    public final void selectExpr() throws RecognitionException {
        try {
            // EsperEPL2Ast.g:351:2: ( ( insertIntoExpr )? selectClause fromClause ( matchRecogClause )? ( whereClause[true] )? ( groupByClause )? ( havingClause )? ( outputLimitExpr )? ( orderByClause )? ( rowLimitClause )? )
            // EsperEPL2Ast.g:351:4: ( insertIntoExpr )? selectClause fromClause ( matchRecogClause )? ( whereClause[true] )? ( groupByClause )? ( havingClause )? ( outputLimitExpr )? ( orderByClause )? ( rowLimitClause )?
            {
            // EsperEPL2Ast.g:351:4: ( insertIntoExpr )?
            int alt110=2;
            int LA110_0 = input.LA(1);

            if ( (LA110_0==INSERTINTO_EXPR) ) {
                alt110=1;
            }
            switch (alt110) {
                case 1 :
                    // EsperEPL2Ast.g:351:5: insertIntoExpr
                    {
                    pushFollow(FOLLOW_insertIntoExpr_in_selectExpr1948);
                    insertIntoExpr();

                    state._fsp--;


                    }
                    break;

            }

            pushFollow(FOLLOW_selectClause_in_selectExpr1954);
            selectClause();

            state._fsp--;

            pushFollow(FOLLOW_fromClause_in_selectExpr1959);
            fromClause();

            state._fsp--;

            // EsperEPL2Ast.g:354:3: ( matchRecogClause )?
            int alt111=2;
            int LA111_0 = input.LA(1);

            if ( (LA111_0==MATCH_RECOGNIZE) ) {
                alt111=1;
            }
            switch (alt111) {
                case 1 :
                    // EsperEPL2Ast.g:354:4: matchRecogClause
                    {
                    pushFollow(FOLLOW_matchRecogClause_in_selectExpr1964);
                    matchRecogClause();

                    state._fsp--;


                    }
                    break;

            }

            // EsperEPL2Ast.g:355:3: ( whereClause[true] )?
            int alt112=2;
            int LA112_0 = input.LA(1);

            if ( (LA112_0==WHERE_EXPR) ) {
                alt112=1;
            }
            switch (alt112) {
                case 1 :
                    // EsperEPL2Ast.g:355:4: whereClause[true]
                    {
                    pushFollow(FOLLOW_whereClause_in_selectExpr1971);
                    whereClause(true);

                    state._fsp--;


                    }
                    break;

            }

            // EsperEPL2Ast.g:356:3: ( groupByClause )?
            int alt113=2;
            int LA113_0 = input.LA(1);

            if ( (LA113_0==GROUP_BY_EXPR) ) {
                alt113=1;
            }
            switch (alt113) {
                case 1 :
                    // EsperEPL2Ast.g:356:4: groupByClause
                    {
                    pushFollow(FOLLOW_groupByClause_in_selectExpr1979);
                    groupByClause();

                    state._fsp--;


                    }
                    break;

            }

            // EsperEPL2Ast.g:357:3: ( havingClause )?
            int alt114=2;
            int LA114_0 = input.LA(1);

            if ( (LA114_0==HAVING_EXPR) ) {
                alt114=1;
            }
            switch (alt114) {
                case 1 :
                    // EsperEPL2Ast.g:357:4: havingClause
                    {
                    pushFollow(FOLLOW_havingClause_in_selectExpr1986);
                    havingClause();

                    state._fsp--;


                    }
                    break;

            }

            // EsperEPL2Ast.g:358:3: ( outputLimitExpr )?
            int alt115=2;
            int LA115_0 = input.LA(1);

            if ( ((LA115_0>=EVENT_LIMIT_EXPR && LA115_0<=CRONTAB_LIMIT_EXPR)||(LA115_0>=WHEN_LIMIT_EXPR && LA115_0<=TERM_LIMIT_EXPR)) ) {
                alt115=1;
            }
            switch (alt115) {
                case 1 :
                    // EsperEPL2Ast.g:358:4: outputLimitExpr
                    {
                    pushFollow(FOLLOW_outputLimitExpr_in_selectExpr1993);
                    outputLimitExpr();

                    state._fsp--;


                    }
                    break;

            }

            // EsperEPL2Ast.g:359:3: ( orderByClause )?
            int alt116=2;
            int LA116_0 = input.LA(1);

            if ( (LA116_0==ORDER_BY_EXPR) ) {
                alt116=1;
            }
            switch (alt116) {
                case 1 :
                    // EsperEPL2Ast.g:359:4: orderByClause
                    {
                    pushFollow(FOLLOW_orderByClause_in_selectExpr2000);
                    orderByClause();

                    state._fsp--;


                    }
                    break;

            }

            // EsperEPL2Ast.g:360:3: ( rowLimitClause )?
            int alt117=2;
            int LA117_0 = input.LA(1);

            if ( (LA117_0==ROW_LIMIT_EXPR) ) {
                alt117=1;
            }
            switch (alt117) {
                case 1 :
                    // EsperEPL2Ast.g:360:4: rowLimitClause
                    {
                    pushFollow(FOLLOW_rowLimitClause_in_selectExpr2007);
                    rowLimitClause();

                    state._fsp--;


                    }
                    break;

            }


            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "selectExpr"


    // $ANTLR start "insertIntoExpr"
    // EsperEPL2Ast.g:363:1: insertIntoExpr : ^(i= INSERTINTO_EXPR ( ISTREAM | RSTREAM | IRSTREAM )? CLASS_IDENT ( exprCol )? ) ;
    public final void insertIntoExpr() throws RecognitionException {
        CommonTree i=null;

        try {
            // EsperEPL2Ast.g:364:2: ( ^(i= INSERTINTO_EXPR ( ISTREAM | RSTREAM | IRSTREAM )? CLASS_IDENT ( exprCol )? ) )
            // EsperEPL2Ast.g:364:4: ^(i= INSERTINTO_EXPR ( ISTREAM | RSTREAM | IRSTREAM )? CLASS_IDENT ( exprCol )? )
            {
            i=(CommonTree)match(input,INSERTINTO_EXPR,FOLLOW_INSERTINTO_EXPR_in_insertIntoExpr2024); 

            match(input, Token.DOWN, null); 
            // EsperEPL2Ast.g:364:24: ( ISTREAM | RSTREAM | IRSTREAM )?
            int alt118=2;
            int LA118_0 = input.LA(1);

            if ( ((LA118_0>=RSTREAM && LA118_0<=IRSTREAM)) ) {
                alt118=1;
            }
            switch (alt118) {
                case 1 :
                    // EsperEPL2Ast.g:
                    {
                    if ( (input.LA(1)>=RSTREAM && input.LA(1)<=IRSTREAM) ) {
                        input.consume();
                        state.errorRecovery=false;
                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        throw mse;
                    }


                    }
                    break;

            }

            match(input,CLASS_IDENT,FOLLOW_CLASS_IDENT_in_insertIntoExpr2039); 
            // EsperEPL2Ast.g:364:68: ( exprCol )?
            int alt119=2;
            int LA119_0 = input.LA(1);

            if ( (LA119_0==EXPRCOL) ) {
                alt119=1;
            }
            switch (alt119) {
                case 1 :
                    // EsperEPL2Ast.g:364:69: exprCol
                    {
                    pushFollow(FOLLOW_exprCol_in_insertIntoExpr2042);
                    exprCol();

                    state._fsp--;


                    }
                    break;

            }

             leaveNode(i); 

            match(input, Token.UP, null); 

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "insertIntoExpr"


    // $ANTLR start "exprCol"
    // EsperEPL2Ast.g:367:1: exprCol : ^( EXPRCOL IDENT ( IDENT )* ) ;
    public final void exprCol() throws RecognitionException {
        try {
            // EsperEPL2Ast.g:368:2: ( ^( EXPRCOL IDENT ( IDENT )* ) )
            // EsperEPL2Ast.g:368:4: ^( EXPRCOL IDENT ( IDENT )* )
            {
            match(input,EXPRCOL,FOLLOW_EXPRCOL_in_exprCol2061); 

            match(input, Token.DOWN, null); 
            match(input,IDENT,FOLLOW_IDENT_in_exprCol2063); 
            // EsperEPL2Ast.g:368:20: ( IDENT )*
            loop120:
            do {
                int alt120=2;
                int LA120_0 = input.LA(1);

                if ( (LA120_0==IDENT) ) {
                    alt120=1;
                }


                switch (alt120) {
            	case 1 :
            	    // EsperEPL2Ast.g:368:21: IDENT
            	    {
            	    match(input,IDENT,FOLLOW_IDENT_in_exprCol2066); 

            	    }
            	    break;

            	default :
            	    break loop120;
                }
            } while (true);


            match(input, Token.UP, null); 

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "exprCol"


    // $ANTLR start "selectClause"
    // EsperEPL2Ast.g:371:1: selectClause : ^(s= SELECTION_EXPR ( RSTREAM | ISTREAM | IRSTREAM )? ( DISTINCT )? selectionList ) ;
    public final void selectClause() throws RecognitionException {
        CommonTree s=null;

        try {
            // EsperEPL2Ast.g:372:2: ( ^(s= SELECTION_EXPR ( RSTREAM | ISTREAM | IRSTREAM )? ( DISTINCT )? selectionList ) )
            // EsperEPL2Ast.g:372:4: ^(s= SELECTION_EXPR ( RSTREAM | ISTREAM | IRSTREAM )? ( DISTINCT )? selectionList )
            {
            s=(CommonTree)match(input,SELECTION_EXPR,FOLLOW_SELECTION_EXPR_in_selectClause2084); 

            match(input, Token.DOWN, null); 
            // EsperEPL2Ast.g:372:23: ( RSTREAM | ISTREAM | IRSTREAM )?
            int alt121=2;
            int LA121_0 = input.LA(1);

            if ( ((LA121_0>=RSTREAM && LA121_0<=IRSTREAM)) ) {
                alt121=1;
            }
            switch (alt121) {
                case 1 :
                    // EsperEPL2Ast.g:
                    {
                    if ( (input.LA(1)>=RSTREAM && input.LA(1)<=IRSTREAM) ) {
                        input.consume();
                        state.errorRecovery=false;
                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        throw mse;
                    }


                    }
                    break;

            }

            // EsperEPL2Ast.g:372:55: ( DISTINCT )?
            int alt122=2;
            int LA122_0 = input.LA(1);

            if ( (LA122_0==DISTINCT) ) {
                alt122=1;
            }
            switch (alt122) {
                case 1 :
                    // EsperEPL2Ast.g:372:55: DISTINCT
                    {
                    match(input,DISTINCT,FOLLOW_DISTINCT_in_selectClause2099); 

                    }
                    break;

            }

            pushFollow(FOLLOW_selectionList_in_selectClause2102);
            selectionList();

            state._fsp--;

             leaveNode(s); 

            match(input, Token.UP, null); 

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "selectClause"


    // $ANTLR start "fromClause"
    // EsperEPL2Ast.g:375:1: fromClause : streamExpression ( streamExpression ( outerJoin )* )* ;
    public final void fromClause() throws RecognitionException {
        try {
            // EsperEPL2Ast.g:376:2: ( streamExpression ( streamExpression ( outerJoin )* )* )
            // EsperEPL2Ast.g:376:4: streamExpression ( streamExpression ( outerJoin )* )*
            {
            pushFollow(FOLLOW_streamExpression_in_fromClause2116);
            streamExpression();

            state._fsp--;

            // EsperEPL2Ast.g:376:21: ( streamExpression ( outerJoin )* )*
            loop124:
            do {
                int alt124=2;
                int LA124_0 = input.LA(1);

                if ( (LA124_0==STREAM_EXPR) ) {
                    alt124=1;
                }


                switch (alt124) {
            	case 1 :
            	    // EsperEPL2Ast.g:376:22: streamExpression ( outerJoin )*
            	    {
            	    pushFollow(FOLLOW_streamExpression_in_fromClause2119);
            	    streamExpression();

            	    state._fsp--;

            	    // EsperEPL2Ast.g:376:39: ( outerJoin )*
            	    loop123:
            	    do {
            	        int alt123=2;
            	        int LA123_0 = input.LA(1);

            	        if ( ((LA123_0>=INNERJOIN_EXPR && LA123_0<=FULL_OUTERJOIN_EXPR)) ) {
            	            alt123=1;
            	        }


            	        switch (alt123) {
            	    	case 1 :
            	    	    // EsperEPL2Ast.g:376:40: outerJoin
            	    	    {
            	    	    pushFollow(FOLLOW_outerJoin_in_fromClause2122);
            	    	    outerJoin();

            	    	    state._fsp--;


            	    	    }
            	    	    break;

            	    	default :
            	    	    break loop123;
            	        }
            	    } while (true);


            	    }
            	    break;

            	default :
            	    break loop124;
                }
            } while (true);


            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "fromClause"


    // $ANTLR start "forExpr"
    // EsperEPL2Ast.g:379:1: forExpr : ^(f= FOR IDENT ( valueExpr )* ) ;
    public final void forExpr() throws RecognitionException {
        CommonTree f=null;

        try {
            // EsperEPL2Ast.g:380:2: ( ^(f= FOR IDENT ( valueExpr )* ) )
            // EsperEPL2Ast.g:380:4: ^(f= FOR IDENT ( valueExpr )* )
            {
            f=(CommonTree)match(input,FOR,FOLLOW_FOR_in_forExpr2142); 

            match(input, Token.DOWN, null); 
            match(input,IDENT,FOLLOW_IDENT_in_forExpr2144); 
            // EsperEPL2Ast.g:380:18: ( valueExpr )*
            loop125:
            do {
                int alt125=2;
                int LA125_0 = input.LA(1);

                if ( ((LA125_0>=IN_SET && LA125_0<=REGEXP)||LA125_0==NOT_EXPR||(LA125_0>=SUM && LA125_0<=AVG)||(LA125_0>=COALESCE && LA125_0<=COUNT)||(LA125_0>=CASE && LA125_0<=CASE2)||LA125_0==ISTREAM||(LA125_0>=PREVIOUS && LA125_0<=EXISTS)||(LA125_0>=INSTANCEOF && LA125_0<=CURRENT_TIMESTAMP)||LA125_0==NEWKW||(LA125_0>=EVAL_AND_EXPR && LA125_0<=EVAL_NOTEQUALS_GROUP_EXPR)||LA125_0==EVENT_PROP_EXPR||LA125_0==CONCAT||(LA125_0>=LIB_FUNC_CHAIN && LA125_0<=DOT_EXPR)||LA125_0==ARRAY_EXPR||(LA125_0>=NOT_IN_SET && LA125_0<=NOT_REGEXP)||(LA125_0>=IN_RANGE && LA125_0<=SUBSELECT_EXPR)||(LA125_0>=EXISTS_SUBSELECT_EXPR && LA125_0<=NOT_IN_SUBSELECT_EXPR)||LA125_0==SUBSTITUTION||(LA125_0>=FIRST_AGGREG && LA125_0<=WINDOW_AGGREG)||(LA125_0>=INT_TYPE && LA125_0<=NULL_TYPE)||(LA125_0>=JSON_OBJECT && LA125_0<=JSON_ARRAY)||LA125_0==STAR||(LA125_0>=LT && LA125_0<=GT)||(LA125_0>=BOR && LA125_0<=PLUS)||(LA125_0>=BAND && LA125_0<=BXOR)||(LA125_0>=LE && LA125_0<=GE)||(LA125_0>=MINUS && LA125_0<=MOD)||(LA125_0>=EVAL_IS_GROUP_EXPR && LA125_0<=EVAL_ISNOT_GROUP_EXPR)) ) {
                    alt125=1;
                }


                switch (alt125) {
            	case 1 :
            	    // EsperEPL2Ast.g:380:18: valueExpr
            	    {
            	    pushFollow(FOLLOW_valueExpr_in_forExpr2146);
            	    valueExpr();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop125;
                }
            } while (true);

             leaveNode(f); 

            match(input, Token.UP, null); 

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "forExpr"


    // $ANTLR start "matchRecogClause"
    // EsperEPL2Ast.g:383:1: matchRecogClause : ^(m= MATCH_RECOGNIZE ( matchRecogPartitionBy )? matchRecogMeasures ( ALL )? ( matchRecogMatchesAfterSkip )? matchRecogPattern ( matchRecogMatchesInterval )? matchRecogDefine ) ;
    public final void matchRecogClause() throws RecognitionException {
        CommonTree m=null;

        try {
            // EsperEPL2Ast.g:384:2: ( ^(m= MATCH_RECOGNIZE ( matchRecogPartitionBy )? matchRecogMeasures ( ALL )? ( matchRecogMatchesAfterSkip )? matchRecogPattern ( matchRecogMatchesInterval )? matchRecogDefine ) )
            // EsperEPL2Ast.g:384:4: ^(m= MATCH_RECOGNIZE ( matchRecogPartitionBy )? matchRecogMeasures ( ALL )? ( matchRecogMatchesAfterSkip )? matchRecogPattern ( matchRecogMatchesInterval )? matchRecogDefine )
            {
            m=(CommonTree)match(input,MATCH_RECOGNIZE,FOLLOW_MATCH_RECOGNIZE_in_matchRecogClause2165); 

            match(input, Token.DOWN, null); 
            // EsperEPL2Ast.g:384:24: ( matchRecogPartitionBy )?
            int alt126=2;
            int LA126_0 = input.LA(1);

            if ( (LA126_0==PARTITIONITEM) ) {
                alt126=1;
            }
            switch (alt126) {
                case 1 :
                    // EsperEPL2Ast.g:384:24: matchRecogPartitionBy
                    {
                    pushFollow(FOLLOW_matchRecogPartitionBy_in_matchRecogClause2167);
                    matchRecogPartitionBy();

                    state._fsp--;


                    }
                    break;

            }

            pushFollow(FOLLOW_matchRecogMeasures_in_matchRecogClause2174);
            matchRecogMeasures();

            state._fsp--;

            // EsperEPL2Ast.g:386:4: ( ALL )?
            int alt127=2;
            int LA127_0 = input.LA(1);

            if ( (LA127_0==ALL) ) {
                alt127=1;
            }
            switch (alt127) {
                case 1 :
                    // EsperEPL2Ast.g:386:4: ALL
                    {
                    match(input,ALL,FOLLOW_ALL_in_matchRecogClause2180); 

                    }
                    break;

            }

            // EsperEPL2Ast.g:387:4: ( matchRecogMatchesAfterSkip )?
            int alt128=2;
            int LA128_0 = input.LA(1);

            if ( (LA128_0==MATCHREC_AFTER_SKIP) ) {
                alt128=1;
            }
            switch (alt128) {
                case 1 :
                    // EsperEPL2Ast.g:387:4: matchRecogMatchesAfterSkip
                    {
                    pushFollow(FOLLOW_matchRecogMatchesAfterSkip_in_matchRecogClause2186);
                    matchRecogMatchesAfterSkip();

                    state._fsp--;


                    }
                    break;

            }

            pushFollow(FOLLOW_matchRecogPattern_in_matchRecogClause2192);
            matchRecogPattern();

            state._fsp--;

            // EsperEPL2Ast.g:389:4: ( matchRecogMatchesInterval )?
            int alt129=2;
            int LA129_0 = input.LA(1);

            if ( (LA129_0==MATCHREC_INTERVAL) ) {
                alt129=1;
            }
            switch (alt129) {
                case 1 :
                    // EsperEPL2Ast.g:389:4: matchRecogMatchesInterval
                    {
                    pushFollow(FOLLOW_matchRecogMatchesInterval_in_matchRecogClause2198);
                    matchRecogMatchesInterval();

                    state._fsp--;


                    }
                    break;

            }

            pushFollow(FOLLOW_matchRecogDefine_in_matchRecogClause2204);
            matchRecogDefine();

            state._fsp--;

             leaveNode(m); 

            match(input, Token.UP, null); 

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "matchRecogClause"


    // $ANTLR start "matchRecogPartitionBy"
    // EsperEPL2Ast.g:393:1: matchRecogPartitionBy : ^(p= PARTITIONITEM ( valueExpr )+ ) ;
    public final void matchRecogPartitionBy() throws RecognitionException {
        CommonTree p=null;

        try {
            // EsperEPL2Ast.g:394:2: ( ^(p= PARTITIONITEM ( valueExpr )+ ) )
            // EsperEPL2Ast.g:394:4: ^(p= PARTITIONITEM ( valueExpr )+ )
            {
            p=(CommonTree)match(input,PARTITIONITEM,FOLLOW_PARTITIONITEM_in_matchRecogPartitionBy2222); 

            match(input, Token.DOWN, null); 
            // EsperEPL2Ast.g:394:22: ( valueExpr )+
            int cnt130=0;
            loop130:
            do {
                int alt130=2;
                int LA130_0 = input.LA(1);

                if ( ((LA130_0>=IN_SET && LA130_0<=REGEXP)||LA130_0==NOT_EXPR||(LA130_0>=SUM && LA130_0<=AVG)||(LA130_0>=COALESCE && LA130_0<=COUNT)||(LA130_0>=CASE && LA130_0<=CASE2)||LA130_0==ISTREAM||(LA130_0>=PREVIOUS && LA130_0<=EXISTS)||(LA130_0>=INSTANCEOF && LA130_0<=CURRENT_TIMESTAMP)||LA130_0==NEWKW||(LA130_0>=EVAL_AND_EXPR && LA130_0<=EVAL_NOTEQUALS_GROUP_EXPR)||LA130_0==EVENT_PROP_EXPR||LA130_0==CONCAT||(LA130_0>=LIB_FUNC_CHAIN && LA130_0<=DOT_EXPR)||LA130_0==ARRAY_EXPR||(LA130_0>=NOT_IN_SET && LA130_0<=NOT_REGEXP)||(LA130_0>=IN_RANGE && LA130_0<=SUBSELECT_EXPR)||(LA130_0>=EXISTS_SUBSELECT_EXPR && LA130_0<=NOT_IN_SUBSELECT_EXPR)||LA130_0==SUBSTITUTION||(LA130_0>=FIRST_AGGREG && LA130_0<=WINDOW_AGGREG)||(LA130_0>=INT_TYPE && LA130_0<=NULL_TYPE)||(LA130_0>=JSON_OBJECT && LA130_0<=JSON_ARRAY)||LA130_0==STAR||(LA130_0>=LT && LA130_0<=GT)||(LA130_0>=BOR && LA130_0<=PLUS)||(LA130_0>=BAND && LA130_0<=BXOR)||(LA130_0>=LE && LA130_0<=GE)||(LA130_0>=MINUS && LA130_0<=MOD)||(LA130_0>=EVAL_IS_GROUP_EXPR && LA130_0<=EVAL_ISNOT_GROUP_EXPR)) ) {
                    alt130=1;
                }


                switch (alt130) {
            	case 1 :
            	    // EsperEPL2Ast.g:394:22: valueExpr
            	    {
            	    pushFollow(FOLLOW_valueExpr_in_matchRecogPartitionBy2224);
            	    valueExpr();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    if ( cnt130 >= 1 ) break loop130;
                        EarlyExitException eee =
                            new EarlyExitException(130, input);
                        throw eee;
                }
                cnt130++;
            } while (true);

             leaveNode(p); 

            match(input, Token.UP, null); 

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "matchRecogPartitionBy"


    // $ANTLR start "matchRecogMatchesAfterSkip"
    // EsperEPL2Ast.g:397:1: matchRecogMatchesAfterSkip : ^( MATCHREC_AFTER_SKIP IDENT IDENT IDENT ( IDENT | LAST ) IDENT ) ;
    public final void matchRecogMatchesAfterSkip() throws RecognitionException {
        try {
            // EsperEPL2Ast.g:398:2: ( ^( MATCHREC_AFTER_SKIP IDENT IDENT IDENT ( IDENT | LAST ) IDENT ) )
            // EsperEPL2Ast.g:398:4: ^( MATCHREC_AFTER_SKIP IDENT IDENT IDENT ( IDENT | LAST ) IDENT )
            {
            match(input,MATCHREC_AFTER_SKIP,FOLLOW_MATCHREC_AFTER_SKIP_in_matchRecogMatchesAfterSkip2241); 

            match(input, Token.DOWN, null); 
            match(input,IDENT,FOLLOW_IDENT_in_matchRecogMatchesAfterSkip2243); 
            match(input,IDENT,FOLLOW_IDENT_in_matchRecogMatchesAfterSkip2245); 
            match(input,IDENT,FOLLOW_IDENT_in_matchRecogMatchesAfterSkip2247); 
            if ( input.LA(1)==LAST||input.LA(1)==IDENT ) {
                input.consume();
                state.errorRecovery=false;
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }

            match(input,IDENT,FOLLOW_IDENT_in_matchRecogMatchesAfterSkip2255); 

            match(input, Token.UP, null); 

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "matchRecogMatchesAfterSkip"


    // $ANTLR start "matchRecogMatchesInterval"
    // EsperEPL2Ast.g:401:1: matchRecogMatchesInterval : ^( MATCHREC_INTERVAL IDENT timePeriod ) ;
    public final void matchRecogMatchesInterval() throws RecognitionException {
        try {
            // EsperEPL2Ast.g:402:2: ( ^( MATCHREC_INTERVAL IDENT timePeriod ) )
            // EsperEPL2Ast.g:402:4: ^( MATCHREC_INTERVAL IDENT timePeriod )
            {
            match(input,MATCHREC_INTERVAL,FOLLOW_MATCHREC_INTERVAL_in_matchRecogMatchesInterval2270); 

            match(input, Token.DOWN, null); 
            match(input,IDENT,FOLLOW_IDENT_in_matchRecogMatchesInterval2272); 
            pushFollow(FOLLOW_timePeriod_in_matchRecogMatchesInterval2274);
            timePeriod();

            state._fsp--;


            match(input, Token.UP, null); 

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "matchRecogMatchesInterval"


    // $ANTLR start "matchRecogMeasures"
    // EsperEPL2Ast.g:405:1: matchRecogMeasures : ^(m= MATCHREC_MEASURES ( matchRecogMeasureListElement )* ) ;
    public final void matchRecogMeasures() throws RecognitionException {
        CommonTree m=null;

        try {
            // EsperEPL2Ast.g:406:2: ( ^(m= MATCHREC_MEASURES ( matchRecogMeasureListElement )* ) )
            // EsperEPL2Ast.g:406:4: ^(m= MATCHREC_MEASURES ( matchRecogMeasureListElement )* )
            {
            m=(CommonTree)match(input,MATCHREC_MEASURES,FOLLOW_MATCHREC_MEASURES_in_matchRecogMeasures2290); 

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // EsperEPL2Ast.g:406:26: ( matchRecogMeasureListElement )*
                loop131:
                do {
                    int alt131=2;
                    int LA131_0 = input.LA(1);

                    if ( (LA131_0==MATCHREC_MEASURE_ITEM) ) {
                        alt131=1;
                    }


                    switch (alt131) {
                	case 1 :
                	    // EsperEPL2Ast.g:406:26: matchRecogMeasureListElement
                	    {
                	    pushFollow(FOLLOW_matchRecogMeasureListElement_in_matchRecogMeasures2292);
                	    matchRecogMeasureListElement();

                	    state._fsp--;


                	    }
                	    break;

                	default :
                	    break loop131;
                    }
                } while (true);


                match(input, Token.UP, null); 
            }

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "matchRecogMeasures"


    // $ANTLR start "matchRecogMeasureListElement"
    // EsperEPL2Ast.g:409:1: matchRecogMeasureListElement : ^(m= MATCHREC_MEASURE_ITEM valueExpr ( IDENT )? ) ;
    public final void matchRecogMeasureListElement() throws RecognitionException {
        CommonTree m=null;

        try {
            // EsperEPL2Ast.g:410:2: ( ^(m= MATCHREC_MEASURE_ITEM valueExpr ( IDENT )? ) )
            // EsperEPL2Ast.g:410:4: ^(m= MATCHREC_MEASURE_ITEM valueExpr ( IDENT )? )
            {
            m=(CommonTree)match(input,MATCHREC_MEASURE_ITEM,FOLLOW_MATCHREC_MEASURE_ITEM_in_matchRecogMeasureListElement2309); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_valueExpr_in_matchRecogMeasureListElement2311);
            valueExpr();

            state._fsp--;

            // EsperEPL2Ast.g:410:40: ( IDENT )?
            int alt132=2;
            int LA132_0 = input.LA(1);

            if ( (LA132_0==IDENT) ) {
                alt132=1;
            }
            switch (alt132) {
                case 1 :
                    // EsperEPL2Ast.g:410:40: IDENT
                    {
                    match(input,IDENT,FOLLOW_IDENT_in_matchRecogMeasureListElement2313); 

                    }
                    break;

            }

             leaveNode(m); 

            match(input, Token.UP, null); 

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "matchRecogMeasureListElement"


    // $ANTLR start "matchRecogPattern"
    // EsperEPL2Ast.g:413:1: matchRecogPattern : ^(p= MATCHREC_PATTERN ( matchRecogPatternAlteration )+ ) ;
    public final void matchRecogPattern() throws RecognitionException {
        CommonTree p=null;

        try {
            // EsperEPL2Ast.g:414:2: ( ^(p= MATCHREC_PATTERN ( matchRecogPatternAlteration )+ ) )
            // EsperEPL2Ast.g:414:4: ^(p= MATCHREC_PATTERN ( matchRecogPatternAlteration )+ )
            {
            p=(CommonTree)match(input,MATCHREC_PATTERN,FOLLOW_MATCHREC_PATTERN_in_matchRecogPattern2333); 

            match(input, Token.DOWN, null); 
            // EsperEPL2Ast.g:414:25: ( matchRecogPatternAlteration )+
            int cnt133=0;
            loop133:
            do {
                int alt133=2;
                int LA133_0 = input.LA(1);

                if ( ((LA133_0>=MATCHREC_PATTERN_CONCAT && LA133_0<=MATCHREC_PATTERN_ALTER)) ) {
                    alt133=1;
                }


                switch (alt133) {
            	case 1 :
            	    // EsperEPL2Ast.g:414:25: matchRecogPatternAlteration
            	    {
            	    pushFollow(FOLLOW_matchRecogPatternAlteration_in_matchRecogPattern2335);
            	    matchRecogPatternAlteration();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    if ( cnt133 >= 1 ) break loop133;
                        EarlyExitException eee =
                            new EarlyExitException(133, input);
                        throw eee;
                }
                cnt133++;
            } while (true);

             leaveNode(p); 

            match(input, Token.UP, null); 

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "matchRecogPattern"


    // $ANTLR start "matchRecogPatternAlteration"
    // EsperEPL2Ast.g:417:1: matchRecogPatternAlteration : ( matchRecogPatternConcat | ^(o= MATCHREC_PATTERN_ALTER matchRecogPatternConcat ( matchRecogPatternConcat )+ ) );
    public final void matchRecogPatternAlteration() throws RecognitionException {
        CommonTree o=null;

        try {
            // EsperEPL2Ast.g:418:2: ( matchRecogPatternConcat | ^(o= MATCHREC_PATTERN_ALTER matchRecogPatternConcat ( matchRecogPatternConcat )+ ) )
            int alt135=2;
            int LA135_0 = input.LA(1);

            if ( (LA135_0==MATCHREC_PATTERN_CONCAT) ) {
                alt135=1;
            }
            else if ( (LA135_0==MATCHREC_PATTERN_ALTER) ) {
                alt135=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 135, 0, input);

                throw nvae;
            }
            switch (alt135) {
                case 1 :
                    // EsperEPL2Ast.g:418:4: matchRecogPatternConcat
                    {
                    pushFollow(FOLLOW_matchRecogPatternConcat_in_matchRecogPatternAlteration2350);
                    matchRecogPatternConcat();

                    state._fsp--;


                    }
                    break;
                case 2 :
                    // EsperEPL2Ast.g:419:4: ^(o= MATCHREC_PATTERN_ALTER matchRecogPatternConcat ( matchRecogPatternConcat )+ )
                    {
                    o=(CommonTree)match(input,MATCHREC_PATTERN_ALTER,FOLLOW_MATCHREC_PATTERN_ALTER_in_matchRecogPatternAlteration2358); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_matchRecogPatternConcat_in_matchRecogPatternAlteration2360);
                    matchRecogPatternConcat();

                    state._fsp--;

                    // EsperEPL2Ast.g:419:55: ( matchRecogPatternConcat )+
                    int cnt134=0;
                    loop134:
                    do {
                        int alt134=2;
                        int LA134_0 = input.LA(1);

                        if ( (LA134_0==MATCHREC_PATTERN_CONCAT) ) {
                            alt134=1;
                        }


                        switch (alt134) {
                    	case 1 :
                    	    // EsperEPL2Ast.g:419:55: matchRecogPatternConcat
                    	    {
                    	    pushFollow(FOLLOW_matchRecogPatternConcat_in_matchRecogPatternAlteration2362);
                    	    matchRecogPatternConcat();

                    	    state._fsp--;


                    	    }
                    	    break;

                    	default :
                    	    if ( cnt134 >= 1 ) break loop134;
                                EarlyExitException eee =
                                    new EarlyExitException(134, input);
                                throw eee;
                        }
                        cnt134++;
                    } while (true);

                     leaveNode(o); 

                    match(input, Token.UP, null); 

                    }
                    break;

            }
        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "matchRecogPatternAlteration"


    // $ANTLR start "matchRecogPatternConcat"
    // EsperEPL2Ast.g:422:1: matchRecogPatternConcat : ^(p= MATCHREC_PATTERN_CONCAT ( matchRecogPatternUnary )+ ) ;
    public final void matchRecogPatternConcat() throws RecognitionException {
        CommonTree p=null;

        try {
            // EsperEPL2Ast.g:423:2: ( ^(p= MATCHREC_PATTERN_CONCAT ( matchRecogPatternUnary )+ ) )
            // EsperEPL2Ast.g:423:4: ^(p= MATCHREC_PATTERN_CONCAT ( matchRecogPatternUnary )+ )
            {
            p=(CommonTree)match(input,MATCHREC_PATTERN_CONCAT,FOLLOW_MATCHREC_PATTERN_CONCAT_in_matchRecogPatternConcat2380); 

            match(input, Token.DOWN, null); 
            // EsperEPL2Ast.g:423:32: ( matchRecogPatternUnary )+
            int cnt136=0;
            loop136:
            do {
                int alt136=2;
                int LA136_0 = input.LA(1);

                if ( (LA136_0==MATCHREC_PATTERN_ATOM||LA136_0==MATCHREC_PATTERN_NESTED) ) {
                    alt136=1;
                }


                switch (alt136) {
            	case 1 :
            	    // EsperEPL2Ast.g:423:32: matchRecogPatternUnary
            	    {
            	    pushFollow(FOLLOW_matchRecogPatternUnary_in_matchRecogPatternConcat2382);
            	    matchRecogPatternUnary();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    if ( cnt136 >= 1 ) break loop136;
                        EarlyExitException eee =
                            new EarlyExitException(136, input);
                        throw eee;
                }
                cnt136++;
            } while (true);

             leaveNode(p); 

            match(input, Token.UP, null); 

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "matchRecogPatternConcat"


    // $ANTLR start "matchRecogPatternUnary"
    // EsperEPL2Ast.g:426:1: matchRecogPatternUnary : ( matchRecogPatternNested | matchRecogPatternAtom );
    public final void matchRecogPatternUnary() throws RecognitionException {
        try {
            // EsperEPL2Ast.g:427:2: ( matchRecogPatternNested | matchRecogPatternAtom )
            int alt137=2;
            int LA137_0 = input.LA(1);

            if ( (LA137_0==MATCHREC_PATTERN_NESTED) ) {
                alt137=1;
            }
            else if ( (LA137_0==MATCHREC_PATTERN_ATOM) ) {
                alt137=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 137, 0, input);

                throw nvae;
            }
            switch (alt137) {
                case 1 :
                    // EsperEPL2Ast.g:427:4: matchRecogPatternNested
                    {
                    pushFollow(FOLLOW_matchRecogPatternNested_in_matchRecogPatternUnary2397);
                    matchRecogPatternNested();

                    state._fsp--;


                    }
                    break;
                case 2 :
                    // EsperEPL2Ast.g:428:4: matchRecogPatternAtom
                    {
                    pushFollow(FOLLOW_matchRecogPatternAtom_in_matchRecogPatternUnary2402);
                    matchRecogPatternAtom();

                    state._fsp--;


                    }
                    break;

            }
        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "matchRecogPatternUnary"


    // $ANTLR start "matchRecogPatternNested"
    // EsperEPL2Ast.g:431:1: matchRecogPatternNested : ^(p= MATCHREC_PATTERN_NESTED matchRecogPatternAlteration ( PLUS | STAR | QUESTION )? ) ;
    public final void matchRecogPatternNested() throws RecognitionException {
        CommonTree p=null;

        try {
            // EsperEPL2Ast.g:432:2: ( ^(p= MATCHREC_PATTERN_NESTED matchRecogPatternAlteration ( PLUS | STAR | QUESTION )? ) )
            // EsperEPL2Ast.g:432:4: ^(p= MATCHREC_PATTERN_NESTED matchRecogPatternAlteration ( PLUS | STAR | QUESTION )? )
            {
            p=(CommonTree)match(input,MATCHREC_PATTERN_NESTED,FOLLOW_MATCHREC_PATTERN_NESTED_in_matchRecogPatternNested2417); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_matchRecogPatternAlteration_in_matchRecogPatternNested2419);
            matchRecogPatternAlteration();

            state._fsp--;

            // EsperEPL2Ast.g:432:60: ( PLUS | STAR | QUESTION )?
            int alt138=2;
            int LA138_0 = input.LA(1);

            if ( (LA138_0==STAR||LA138_0==QUESTION||LA138_0==PLUS) ) {
                alt138=1;
            }
            switch (alt138) {
                case 1 :
                    // EsperEPL2Ast.g:
                    {
                    if ( input.LA(1)==STAR||input.LA(1)==QUESTION||input.LA(1)==PLUS ) {
                        input.consume();
                        state.errorRecovery=false;
                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        throw mse;
                    }


                    }
                    break;

            }

             leaveNode(p); 

            match(input, Token.UP, null); 

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "matchRecogPatternNested"


    // $ANTLR start "matchRecogPatternAtom"
    // EsperEPL2Ast.g:435:1: matchRecogPatternAtom : ^(p= MATCHREC_PATTERN_ATOM IDENT ( ( PLUS | STAR | QUESTION ) ( QUESTION )? )? ) ;
    public final void matchRecogPatternAtom() throws RecognitionException {
        CommonTree p=null;

        try {
            // EsperEPL2Ast.g:436:2: ( ^(p= MATCHREC_PATTERN_ATOM IDENT ( ( PLUS | STAR | QUESTION ) ( QUESTION )? )? ) )
            // EsperEPL2Ast.g:436:4: ^(p= MATCHREC_PATTERN_ATOM IDENT ( ( PLUS | STAR | QUESTION ) ( QUESTION )? )? )
            {
            p=(CommonTree)match(input,MATCHREC_PATTERN_ATOM,FOLLOW_MATCHREC_PATTERN_ATOM_in_matchRecogPatternAtom2450); 

            match(input, Token.DOWN, null); 
            match(input,IDENT,FOLLOW_IDENT_in_matchRecogPatternAtom2452); 
            // EsperEPL2Ast.g:436:36: ( ( PLUS | STAR | QUESTION ) ( QUESTION )? )?
            int alt140=2;
            int LA140_0 = input.LA(1);

            if ( (LA140_0==STAR||LA140_0==QUESTION||LA140_0==PLUS) ) {
                alt140=1;
            }
            switch (alt140) {
                case 1 :
                    // EsperEPL2Ast.g:436:38: ( PLUS | STAR | QUESTION ) ( QUESTION )?
                    {
                    if ( input.LA(1)==STAR||input.LA(1)==QUESTION||input.LA(1)==PLUS ) {
                        input.consume();
                        state.errorRecovery=false;
                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        throw mse;
                    }

                    // EsperEPL2Ast.g:436:63: ( QUESTION )?
                    int alt139=2;
                    int LA139_0 = input.LA(1);

                    if ( (LA139_0==QUESTION) ) {
                        alt139=1;
                    }
                    switch (alt139) {
                        case 1 :
                            // EsperEPL2Ast.g:436:63: QUESTION
                            {
                            match(input,QUESTION,FOLLOW_QUESTION_in_matchRecogPatternAtom2468); 

                            }
                            break;

                    }


                    }
                    break;

            }

             leaveNode(p); 

            match(input, Token.UP, null); 

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "matchRecogPatternAtom"


    // $ANTLR start "matchRecogDefine"
    // EsperEPL2Ast.g:439:1: matchRecogDefine : ^(p= MATCHREC_DEFINE ( matchRecogDefineItem )+ ) ;
    public final void matchRecogDefine() throws RecognitionException {
        CommonTree p=null;

        try {
            // EsperEPL2Ast.g:440:2: ( ^(p= MATCHREC_DEFINE ( matchRecogDefineItem )+ ) )
            // EsperEPL2Ast.g:440:4: ^(p= MATCHREC_DEFINE ( matchRecogDefineItem )+ )
            {
            p=(CommonTree)match(input,MATCHREC_DEFINE,FOLLOW_MATCHREC_DEFINE_in_matchRecogDefine2490); 

            match(input, Token.DOWN, null); 
            // EsperEPL2Ast.g:440:24: ( matchRecogDefineItem )+
            int cnt141=0;
            loop141:
            do {
                int alt141=2;
                int LA141_0 = input.LA(1);

                if ( (LA141_0==MATCHREC_DEFINE_ITEM) ) {
                    alt141=1;
                }


                switch (alt141) {
            	case 1 :
            	    // EsperEPL2Ast.g:440:24: matchRecogDefineItem
            	    {
            	    pushFollow(FOLLOW_matchRecogDefineItem_in_matchRecogDefine2492);
            	    matchRecogDefineItem();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    if ( cnt141 >= 1 ) break loop141;
                        EarlyExitException eee =
                            new EarlyExitException(141, input);
                        throw eee;
                }
                cnt141++;
            } while (true);


            match(input, Token.UP, null); 

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "matchRecogDefine"


    // $ANTLR start "matchRecogDefineItem"
    // EsperEPL2Ast.g:443:1: matchRecogDefineItem : ^(d= MATCHREC_DEFINE_ITEM IDENT valueExpr ) ;
    public final void matchRecogDefineItem() throws RecognitionException {
        CommonTree d=null;

        try {
            // EsperEPL2Ast.g:444:2: ( ^(d= MATCHREC_DEFINE_ITEM IDENT valueExpr ) )
            // EsperEPL2Ast.g:444:4: ^(d= MATCHREC_DEFINE_ITEM IDENT valueExpr )
            {
            d=(CommonTree)match(input,MATCHREC_DEFINE_ITEM,FOLLOW_MATCHREC_DEFINE_ITEM_in_matchRecogDefineItem2509); 

            match(input, Token.DOWN, null); 
            match(input,IDENT,FOLLOW_IDENT_in_matchRecogDefineItem2511); 
            pushFollow(FOLLOW_valueExpr_in_matchRecogDefineItem2513);
            valueExpr();

            state._fsp--;

             leaveNode(d); 

            match(input, Token.UP, null); 

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "matchRecogDefineItem"


    // $ANTLR start "selectionList"
    // EsperEPL2Ast.g:448:1: selectionList : selectionListElement ( selectionListElement )* ;
    public final void selectionList() throws RecognitionException {
        try {
            // EsperEPL2Ast.g:449:2: ( selectionListElement ( selectionListElement )* )
            // EsperEPL2Ast.g:449:4: selectionListElement ( selectionListElement )*
            {
            pushFollow(FOLLOW_selectionListElement_in_selectionList2530);
            selectionListElement();

            state._fsp--;

            // EsperEPL2Ast.g:449:25: ( selectionListElement )*
            loop142:
            do {
                int alt142=2;
                int LA142_0 = input.LA(1);

                if ( ((LA142_0>=SELECTION_ELEMENT_EXPR && LA142_0<=SELECTION_STREAM)||LA142_0==WILDCARD_SELECT) ) {
                    alt142=1;
                }


                switch (alt142) {
            	case 1 :
            	    // EsperEPL2Ast.g:449:26: selectionListElement
            	    {
            	    pushFollow(FOLLOW_selectionListElement_in_selectionList2533);
            	    selectionListElement();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop142;
                }
            } while (true);


            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "selectionList"


    // $ANTLR start "selectionListElement"
    // EsperEPL2Ast.g:452:1: selectionListElement : (w= WILDCARD_SELECT | ^(e= SELECTION_ELEMENT_EXPR valueExpr ( IDENT )? ) | ^(s= SELECTION_STREAM IDENT ( IDENT )? ) );
    public final void selectionListElement() throws RecognitionException {
        CommonTree w=null;
        CommonTree e=null;
        CommonTree s=null;

        try {
            // EsperEPL2Ast.g:453:2: (w= WILDCARD_SELECT | ^(e= SELECTION_ELEMENT_EXPR valueExpr ( IDENT )? ) | ^(s= SELECTION_STREAM IDENT ( IDENT )? ) )
            int alt145=3;
            switch ( input.LA(1) ) {
            case WILDCARD_SELECT:
                {
                alt145=1;
                }
                break;
            case SELECTION_ELEMENT_EXPR:
                {
                alt145=2;
                }
                break;
            case SELECTION_STREAM:
                {
                alt145=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 145, 0, input);

                throw nvae;
            }

            switch (alt145) {
                case 1 :
                    // EsperEPL2Ast.g:453:4: w= WILDCARD_SELECT
                    {
                    w=(CommonTree)match(input,WILDCARD_SELECT,FOLLOW_WILDCARD_SELECT_in_selectionListElement2549); 
                     leaveNode(w); 

                    }
                    break;
                case 2 :
                    // EsperEPL2Ast.g:454:4: ^(e= SELECTION_ELEMENT_EXPR valueExpr ( IDENT )? )
                    {
                    e=(CommonTree)match(input,SELECTION_ELEMENT_EXPR,FOLLOW_SELECTION_ELEMENT_EXPR_in_selectionListElement2559); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_valueExpr_in_selectionListElement2561);
                    valueExpr();

                    state._fsp--;

                    // EsperEPL2Ast.g:454:41: ( IDENT )?
                    int alt143=2;
                    int LA143_0 = input.LA(1);

                    if ( (LA143_0==IDENT) ) {
                        alt143=1;
                    }
                    switch (alt143) {
                        case 1 :
                            // EsperEPL2Ast.g:454:42: IDENT
                            {
                            match(input,IDENT,FOLLOW_IDENT_in_selectionListElement2564); 

                            }
                            break;

                    }

                     leaveNode(e); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 3 :
                    // EsperEPL2Ast.g:455:4: ^(s= SELECTION_STREAM IDENT ( IDENT )? )
                    {
                    s=(CommonTree)match(input,SELECTION_STREAM,FOLLOW_SELECTION_STREAM_in_selectionListElement2578); 

                    match(input, Token.DOWN, null); 
                    match(input,IDENT,FOLLOW_IDENT_in_selectionListElement2580); 
                    // EsperEPL2Ast.g:455:31: ( IDENT )?
                    int alt144=2;
                    int LA144_0 = input.LA(1);

                    if ( (LA144_0==IDENT) ) {
                        alt144=1;
                    }
                    switch (alt144) {
                        case 1 :
                            // EsperEPL2Ast.g:455:32: IDENT
                            {
                            match(input,IDENT,FOLLOW_IDENT_in_selectionListElement2583); 

                            }
                            break;

                    }

                     leaveNode(s); 

                    match(input, Token.UP, null); 

                    }
                    break;

            }
        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "selectionListElement"


    // $ANTLR start "outerJoin"
    // EsperEPL2Ast.g:458:1: outerJoin : ( ^(tl= LEFT_OUTERJOIN_EXPR ON ( outerJoinIdent )? ) | ^(tr= RIGHT_OUTERJOIN_EXPR ON ( outerJoinIdent )? ) | ^(tf= FULL_OUTERJOIN_EXPR ON ( outerJoinIdent )? ) | ^(i= INNERJOIN_EXPR ON ( outerJoinIdent )? ) );
    public final void outerJoin() throws RecognitionException {
        CommonTree tl=null;
        CommonTree tr=null;
        CommonTree tf=null;
        CommonTree i=null;

        try {
            // EsperEPL2Ast.g:459:2: ( ^(tl= LEFT_OUTERJOIN_EXPR ON ( outerJoinIdent )? ) | ^(tr= RIGHT_OUTERJOIN_EXPR ON ( outerJoinIdent )? ) | ^(tf= FULL_OUTERJOIN_EXPR ON ( outerJoinIdent )? ) | ^(i= INNERJOIN_EXPR ON ( outerJoinIdent )? ) )
            int alt150=4;
            switch ( input.LA(1) ) {
            case LEFT_OUTERJOIN_EXPR:
                {
                alt150=1;
                }
                break;
            case RIGHT_OUTERJOIN_EXPR:
                {
                alt150=2;
                }
                break;
            case FULL_OUTERJOIN_EXPR:
                {
                alt150=3;
                }
                break;
            case INNERJOIN_EXPR:
                {
                alt150=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 150, 0, input);

                throw nvae;
            }

            switch (alt150) {
                case 1 :
                    // EsperEPL2Ast.g:459:4: ^(tl= LEFT_OUTERJOIN_EXPR ON ( outerJoinIdent )? )
                    {
                    tl=(CommonTree)match(input,LEFT_OUTERJOIN_EXPR,FOLLOW_LEFT_OUTERJOIN_EXPR_in_outerJoin2605); 

                    match(input, Token.DOWN, null); 
                    match(input,ON,FOLLOW_ON_in_outerJoin2607); 
                    // EsperEPL2Ast.g:459:32: ( outerJoinIdent )?
                    int alt146=2;
                    int LA146_0 = input.LA(1);

                    if ( (LA146_0==EVENT_PROP_EXPR) ) {
                        alt146=1;
                    }
                    switch (alt146) {
                        case 1 :
                            // EsperEPL2Ast.g:459:32: outerJoinIdent
                            {
                            pushFollow(FOLLOW_outerJoinIdent_in_outerJoin2609);
                            outerJoinIdent();

                            state._fsp--;


                            }
                            break;

                    }

                     leaveNode(tl); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    // EsperEPL2Ast.g:460:4: ^(tr= RIGHT_OUTERJOIN_EXPR ON ( outerJoinIdent )? )
                    {
                    tr=(CommonTree)match(input,RIGHT_OUTERJOIN_EXPR,FOLLOW_RIGHT_OUTERJOIN_EXPR_in_outerJoin2622); 

                    match(input, Token.DOWN, null); 
                    match(input,ON,FOLLOW_ON_in_outerJoin2624); 
                    // EsperEPL2Ast.g:460:33: ( outerJoinIdent )?
                    int alt147=2;
                    int LA147_0 = input.LA(1);

                    if ( (LA147_0==EVENT_PROP_EXPR) ) {
                        alt147=1;
                    }
                    switch (alt147) {
                        case 1 :
                            // EsperEPL2Ast.g:460:33: outerJoinIdent
                            {
                            pushFollow(FOLLOW_outerJoinIdent_in_outerJoin2626);
                            outerJoinIdent();

                            state._fsp--;


                            }
                            break;

                    }

                     leaveNode(tr); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 3 :
                    // EsperEPL2Ast.g:461:4: ^(tf= FULL_OUTERJOIN_EXPR ON ( outerJoinIdent )? )
                    {
                    tf=(CommonTree)match(input,FULL_OUTERJOIN_EXPR,FOLLOW_FULL_OUTERJOIN_EXPR_in_outerJoin2639); 

                    match(input, Token.DOWN, null); 
                    match(input,ON,FOLLOW_ON_in_outerJoin2641); 
                    // EsperEPL2Ast.g:461:32: ( outerJoinIdent )?
                    int alt148=2;
                    int LA148_0 = input.LA(1);

                    if ( (LA148_0==EVENT_PROP_EXPR) ) {
                        alt148=1;
                    }
                    switch (alt148) {
                        case 1 :
                            // EsperEPL2Ast.g:461:32: outerJoinIdent
                            {
                            pushFollow(FOLLOW_outerJoinIdent_in_outerJoin2643);
                            outerJoinIdent();

                            state._fsp--;


                            }
                            break;

                    }

                     leaveNode(tf); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 4 :
                    // EsperEPL2Ast.g:462:4: ^(i= INNERJOIN_EXPR ON ( outerJoinIdent )? )
                    {
                    i=(CommonTree)match(input,INNERJOIN_EXPR,FOLLOW_INNERJOIN_EXPR_in_outerJoin2656); 

                    match(input, Token.DOWN, null); 
                    match(input,ON,FOLLOW_ON_in_outerJoin2658); 
                    // EsperEPL2Ast.g:462:26: ( outerJoinIdent )?
                    int alt149=2;
                    int LA149_0 = input.LA(1);

                    if ( (LA149_0==EVENT_PROP_EXPR) ) {
                        alt149=1;
                    }
                    switch (alt149) {
                        case 1 :
                            // EsperEPL2Ast.g:462:26: outerJoinIdent
                            {
                            pushFollow(FOLLOW_outerJoinIdent_in_outerJoin2660);
                            outerJoinIdent();

                            state._fsp--;


                            }
                            break;

                    }

                     leaveNode(i); 

                    match(input, Token.UP, null); 

                    }
                    break;

            }
        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "outerJoin"


    // $ANTLR start "outerJoinIdent"
    // EsperEPL2Ast.g:465:1: outerJoinIdent : eventPropertyExpr[true] eventPropertyExpr[true] ( eventPropertyExpr[true] eventPropertyExpr[true] )* ;
    public final void outerJoinIdent() throws RecognitionException {
        try {
            // EsperEPL2Ast.g:466:2: ( eventPropertyExpr[true] eventPropertyExpr[true] ( eventPropertyExpr[true] eventPropertyExpr[true] )* )
            // EsperEPL2Ast.g:466:4: eventPropertyExpr[true] eventPropertyExpr[true] ( eventPropertyExpr[true] eventPropertyExpr[true] )*
            {
            pushFollow(FOLLOW_eventPropertyExpr_in_outerJoinIdent2676);
            eventPropertyExpr(true);

            state._fsp--;

            pushFollow(FOLLOW_eventPropertyExpr_in_outerJoinIdent2679);
            eventPropertyExpr(true);

            state._fsp--;

            // EsperEPL2Ast.g:466:52: ( eventPropertyExpr[true] eventPropertyExpr[true] )*
            loop151:
            do {
                int alt151=2;
                int LA151_0 = input.LA(1);

                if ( (LA151_0==EVENT_PROP_EXPR) ) {
                    alt151=1;
                }


                switch (alt151) {
            	case 1 :
            	    // EsperEPL2Ast.g:466:53: eventPropertyExpr[true] eventPropertyExpr[true]
            	    {
            	    pushFollow(FOLLOW_eventPropertyExpr_in_outerJoinIdent2683);
            	    eventPropertyExpr(true);

            	    state._fsp--;

            	    pushFollow(FOLLOW_eventPropertyExpr_in_outerJoinIdent2686);
            	    eventPropertyExpr(true);

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop151;
                }
            } while (true);


            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "outerJoinIdent"


    // $ANTLR start "streamExpression"
    // EsperEPL2Ast.g:469:1: streamExpression : ^(v= STREAM_EXPR ( eventFilterExpr[true] | patternInclusionExpression | databaseJoinExpression | methodJoinExpression ) ( viewListExpr )? ( IDENT )? ( UNIDIRECTIONAL )? ( RETAINUNION | RETAININTERSECTION )? ) ;
    public final void streamExpression() throws RecognitionException {
        CommonTree v=null;

        try {
            // EsperEPL2Ast.g:470:2: ( ^(v= STREAM_EXPR ( eventFilterExpr[true] | patternInclusionExpression | databaseJoinExpression | methodJoinExpression ) ( viewListExpr )? ( IDENT )? ( UNIDIRECTIONAL )? ( RETAINUNION | RETAININTERSECTION )? ) )
            // EsperEPL2Ast.g:470:4: ^(v= STREAM_EXPR ( eventFilterExpr[true] | patternInclusionExpression | databaseJoinExpression | methodJoinExpression ) ( viewListExpr )? ( IDENT )? ( UNIDIRECTIONAL )? ( RETAINUNION | RETAININTERSECTION )? )
            {
            v=(CommonTree)match(input,STREAM_EXPR,FOLLOW_STREAM_EXPR_in_streamExpression2703); 

            match(input, Token.DOWN, null); 
            // EsperEPL2Ast.g:470:20: ( eventFilterExpr[true] | patternInclusionExpression | databaseJoinExpression | methodJoinExpression )
            int alt152=4;
            switch ( input.LA(1) ) {
            case EVENT_FILTER_EXPR:
                {
                alt152=1;
                }
                break;
            case PATTERN_INCL_EXPR:
                {
                alt152=2;
                }
                break;
            case DATABASE_JOIN_EXPR:
                {
                alt152=3;
                }
                break;
            case METHOD_JOIN_EXPR:
                {
                alt152=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 152, 0, input);

                throw nvae;
            }

            switch (alt152) {
                case 1 :
                    // EsperEPL2Ast.g:470:21: eventFilterExpr[true]
                    {
                    pushFollow(FOLLOW_eventFilterExpr_in_streamExpression2706);
                    eventFilterExpr(true);

                    state._fsp--;


                    }
                    break;
                case 2 :
                    // EsperEPL2Ast.g:470:45: patternInclusionExpression
                    {
                    pushFollow(FOLLOW_patternInclusionExpression_in_streamExpression2711);
                    patternInclusionExpression();

                    state._fsp--;


                    }
                    break;
                case 3 :
                    // EsperEPL2Ast.g:470:74: databaseJoinExpression
                    {
                    pushFollow(FOLLOW_databaseJoinExpression_in_streamExpression2715);
                    databaseJoinExpression();

                    state._fsp--;


                    }
                    break;
                case 4 :
                    // EsperEPL2Ast.g:470:99: methodJoinExpression
                    {
                    pushFollow(FOLLOW_methodJoinExpression_in_streamExpression2719);
                    methodJoinExpression();

                    state._fsp--;


                    }
                    break;

            }

            // EsperEPL2Ast.g:470:121: ( viewListExpr )?
            int alt153=2;
            int LA153_0 = input.LA(1);

            if ( (LA153_0==VIEW_EXPR) ) {
                alt153=1;
            }
            switch (alt153) {
                case 1 :
                    // EsperEPL2Ast.g:470:122: viewListExpr
                    {
                    pushFollow(FOLLOW_viewListExpr_in_streamExpression2723);
                    viewListExpr();

                    state._fsp--;


                    }
                    break;

            }

            // EsperEPL2Ast.g:470:137: ( IDENT )?
            int alt154=2;
            int LA154_0 = input.LA(1);

            if ( (LA154_0==IDENT) ) {
                alt154=1;
            }
            switch (alt154) {
                case 1 :
                    // EsperEPL2Ast.g:470:138: IDENT
                    {
                    match(input,IDENT,FOLLOW_IDENT_in_streamExpression2728); 

                    }
                    break;

            }

            // EsperEPL2Ast.g:470:146: ( UNIDIRECTIONAL )?
            int alt155=2;
            int LA155_0 = input.LA(1);

            if ( (LA155_0==UNIDIRECTIONAL) ) {
                alt155=1;
            }
            switch (alt155) {
                case 1 :
                    // EsperEPL2Ast.g:470:147: UNIDIRECTIONAL
                    {
                    match(input,UNIDIRECTIONAL,FOLLOW_UNIDIRECTIONAL_in_streamExpression2733); 

                    }
                    break;

            }

            // EsperEPL2Ast.g:470:164: ( RETAINUNION | RETAININTERSECTION )?
            int alt156=2;
            int LA156_0 = input.LA(1);

            if ( ((LA156_0>=RETAINUNION && LA156_0<=RETAININTERSECTION)) ) {
                alt156=1;
            }
            switch (alt156) {
                case 1 :
                    // EsperEPL2Ast.g:
                    {
                    if ( (input.LA(1)>=RETAINUNION && input.LA(1)<=RETAININTERSECTION) ) {
                        input.consume();
                        state.errorRecovery=false;
                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        throw mse;
                    }


                    }
                    break;

            }

             leaveNode(v); 

            match(input, Token.UP, null); 

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "streamExpression"


    // $ANTLR start "eventFilterExpr"
    // EsperEPL2Ast.g:473:1: eventFilterExpr[boolean isLeaveNode] : ^(f= EVENT_FILTER_EXPR ( IDENT )? CLASS_IDENT ( propertyExpression )? ( valueExpr )* ) ;
    public final void eventFilterExpr(boolean isLeaveNode) throws RecognitionException {
        CommonTree f=null;

        try {
            // EsperEPL2Ast.g:474:2: ( ^(f= EVENT_FILTER_EXPR ( IDENT )? CLASS_IDENT ( propertyExpression )? ( valueExpr )* ) )
            // EsperEPL2Ast.g:474:4: ^(f= EVENT_FILTER_EXPR ( IDENT )? CLASS_IDENT ( propertyExpression )? ( valueExpr )* )
            {
            f=(CommonTree)match(input,EVENT_FILTER_EXPR,FOLLOW_EVENT_FILTER_EXPR_in_eventFilterExpr2762); 

            match(input, Token.DOWN, null); 
            // EsperEPL2Ast.g:474:27: ( IDENT )?
            int alt157=2;
            int LA157_0 = input.LA(1);

            if ( (LA157_0==IDENT) ) {
                alt157=1;
            }
            switch (alt157) {
                case 1 :
                    // EsperEPL2Ast.g:474:27: IDENT
                    {
                    match(input,IDENT,FOLLOW_IDENT_in_eventFilterExpr2764); 

                    }
                    break;

            }

            match(input,CLASS_IDENT,FOLLOW_CLASS_IDENT_in_eventFilterExpr2767); 
            // EsperEPL2Ast.g:474:46: ( propertyExpression )?
            int alt158=2;
            int LA158_0 = input.LA(1);

            if ( (LA158_0==EVENT_FILTER_PROPERTY_EXPR) ) {
                alt158=1;
            }
            switch (alt158) {
                case 1 :
                    // EsperEPL2Ast.g:474:46: propertyExpression
                    {
                    pushFollow(FOLLOW_propertyExpression_in_eventFilterExpr2769);
                    propertyExpression();

                    state._fsp--;


                    }
                    break;

            }

            // EsperEPL2Ast.g:474:66: ( valueExpr )*
            loop159:
            do {
                int alt159=2;
                int LA159_0 = input.LA(1);

                if ( ((LA159_0>=IN_SET && LA159_0<=REGEXP)||LA159_0==NOT_EXPR||(LA159_0>=SUM && LA159_0<=AVG)||(LA159_0>=COALESCE && LA159_0<=COUNT)||(LA159_0>=CASE && LA159_0<=CASE2)||LA159_0==ISTREAM||(LA159_0>=PREVIOUS && LA159_0<=EXISTS)||(LA159_0>=INSTANCEOF && LA159_0<=CURRENT_TIMESTAMP)||LA159_0==NEWKW||(LA159_0>=EVAL_AND_EXPR && LA159_0<=EVAL_NOTEQUALS_GROUP_EXPR)||LA159_0==EVENT_PROP_EXPR||LA159_0==CONCAT||(LA159_0>=LIB_FUNC_CHAIN && LA159_0<=DOT_EXPR)||LA159_0==ARRAY_EXPR||(LA159_0>=NOT_IN_SET && LA159_0<=NOT_REGEXP)||(LA159_0>=IN_RANGE && LA159_0<=SUBSELECT_EXPR)||(LA159_0>=EXISTS_SUBSELECT_EXPR && LA159_0<=NOT_IN_SUBSELECT_EXPR)||LA159_0==SUBSTITUTION||(LA159_0>=FIRST_AGGREG && LA159_0<=WINDOW_AGGREG)||(LA159_0>=INT_TYPE && LA159_0<=NULL_TYPE)||(LA159_0>=JSON_OBJECT && LA159_0<=JSON_ARRAY)||LA159_0==STAR||(LA159_0>=LT && LA159_0<=GT)||(LA159_0>=BOR && LA159_0<=PLUS)||(LA159_0>=BAND && LA159_0<=BXOR)||(LA159_0>=LE && LA159_0<=GE)||(LA159_0>=MINUS && LA159_0<=MOD)||(LA159_0>=EVAL_IS_GROUP_EXPR && LA159_0<=EVAL_ISNOT_GROUP_EXPR)) ) {
                    alt159=1;
                }


                switch (alt159) {
            	case 1 :
            	    // EsperEPL2Ast.g:474:67: valueExpr
            	    {
            	    pushFollow(FOLLOW_valueExpr_in_eventFilterExpr2773);
            	    valueExpr();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop159;
                }
            } while (true);

             if (isLeaveNode) leaveNode(f); 

            match(input, Token.UP, null); 

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "eventFilterExpr"


    // $ANTLR start "propertyExpression"
    // EsperEPL2Ast.g:477:1: propertyExpression : ^( EVENT_FILTER_PROPERTY_EXPR ( propertyExpressionAtom )* ) ;
    public final void propertyExpression() throws RecognitionException {
        try {
            // EsperEPL2Ast.g:478:2: ( ^( EVENT_FILTER_PROPERTY_EXPR ( propertyExpressionAtom )* ) )
            // EsperEPL2Ast.g:478:4: ^( EVENT_FILTER_PROPERTY_EXPR ( propertyExpressionAtom )* )
            {
            match(input,EVENT_FILTER_PROPERTY_EXPR,FOLLOW_EVENT_FILTER_PROPERTY_EXPR_in_propertyExpression2793); 

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // EsperEPL2Ast.g:478:34: ( propertyExpressionAtom )*
                loop160:
                do {
                    int alt160=2;
                    int LA160_0 = input.LA(1);

                    if ( (LA160_0==EVENT_FILTER_PROPERTY_EXPR_ATOM) ) {
                        alt160=1;
                    }


                    switch (alt160) {
                	case 1 :
                	    // EsperEPL2Ast.g:478:34: propertyExpressionAtom
                	    {
                	    pushFollow(FOLLOW_propertyExpressionAtom_in_propertyExpression2795);
                	    propertyExpressionAtom();

                	    state._fsp--;


                	    }
                	    break;

                	default :
                	    break loop160;
                    }
                } while (true);


                match(input, Token.UP, null); 
            }

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "propertyExpression"


    // $ANTLR start "propertyExpressionAtom"
    // EsperEPL2Ast.g:481:1: propertyExpressionAtom : ^(a= EVENT_FILTER_PROPERTY_EXPR_ATOM ( ^( SELECT ( propertySelectionListElement )+ ) )? valueExpr ( ^( ATCHAR IDENT IDENT ) )? ( IDENT )? ^( WHERE_EXPR ( valueExpr )? ) ) ;
    public final void propertyExpressionAtom() throws RecognitionException {
        CommonTree a=null;

        try {
            // EsperEPL2Ast.g:482:2: ( ^(a= EVENT_FILTER_PROPERTY_EXPR_ATOM ( ^( SELECT ( propertySelectionListElement )+ ) )? valueExpr ( ^( ATCHAR IDENT IDENT ) )? ( IDENT )? ^( WHERE_EXPR ( valueExpr )? ) ) )
            // EsperEPL2Ast.g:482:4: ^(a= EVENT_FILTER_PROPERTY_EXPR_ATOM ( ^( SELECT ( propertySelectionListElement )+ ) )? valueExpr ( ^( ATCHAR IDENT IDENT ) )? ( IDENT )? ^( WHERE_EXPR ( valueExpr )? ) )
            {
            a=(CommonTree)match(input,EVENT_FILTER_PROPERTY_EXPR_ATOM,FOLLOW_EVENT_FILTER_PROPERTY_EXPR_ATOM_in_propertyExpressionAtom2814); 

            match(input, Token.DOWN, null); 
            // EsperEPL2Ast.g:482:41: ( ^( SELECT ( propertySelectionListElement )+ ) )?
            int alt162=2;
            int LA162_0 = input.LA(1);

            if ( (LA162_0==SELECT) ) {
                alt162=1;
            }
            switch (alt162) {
                case 1 :
                    // EsperEPL2Ast.g:482:42: ^( SELECT ( propertySelectionListElement )+ )
                    {
                    match(input,SELECT,FOLLOW_SELECT_in_propertyExpressionAtom2818); 

                    match(input, Token.DOWN, null); 
                    // EsperEPL2Ast.g:482:51: ( propertySelectionListElement )+
                    int cnt161=0;
                    loop161:
                    do {
                        int alt161=2;
                        int LA161_0 = input.LA(1);

                        if ( ((LA161_0>=PROPERTY_SELECTION_ELEMENT_EXPR && LA161_0<=PROPERTY_WILDCARD_SELECT)) ) {
                            alt161=1;
                        }


                        switch (alt161) {
                    	case 1 :
                    	    // EsperEPL2Ast.g:482:51: propertySelectionListElement
                    	    {
                    	    pushFollow(FOLLOW_propertySelectionListElement_in_propertyExpressionAtom2820);
                    	    propertySelectionListElement();

                    	    state._fsp--;


                    	    }
                    	    break;

                    	default :
                    	    if ( cnt161 >= 1 ) break loop161;
                                EarlyExitException eee =
                                    new EarlyExitException(161, input);
                                throw eee;
                        }
                        cnt161++;
                    } while (true);


                    match(input, Token.UP, null); 

                    }
                    break;

            }

            pushFollow(FOLLOW_valueExpr_in_propertyExpressionAtom2826);
            valueExpr();

            state._fsp--;

            // EsperEPL2Ast.g:482:94: ( ^( ATCHAR IDENT IDENT ) )?
            int alt163=2;
            int LA163_0 = input.LA(1);

            if ( (LA163_0==ATCHAR) ) {
                alt163=1;
            }
            switch (alt163) {
                case 1 :
                    // EsperEPL2Ast.g:482:95: ^( ATCHAR IDENT IDENT )
                    {
                    match(input,ATCHAR,FOLLOW_ATCHAR_in_propertyExpressionAtom2830); 

                    match(input, Token.DOWN, null); 
                    match(input,IDENT,FOLLOW_IDENT_in_propertyExpressionAtom2832); 
                    match(input,IDENT,FOLLOW_IDENT_in_propertyExpressionAtom2834); 

                    match(input, Token.UP, null); 

                    }
                    break;

            }

            // EsperEPL2Ast.g:482:119: ( IDENT )?
            int alt164=2;
            int LA164_0 = input.LA(1);

            if ( (LA164_0==IDENT) ) {
                alt164=1;
            }
            switch (alt164) {
                case 1 :
                    // EsperEPL2Ast.g:482:119: IDENT
                    {
                    match(input,IDENT,FOLLOW_IDENT_in_propertyExpressionAtom2839); 

                    }
                    break;

            }

            match(input,WHERE_EXPR,FOLLOW_WHERE_EXPR_in_propertyExpressionAtom2843); 

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // EsperEPL2Ast.g:482:139: ( valueExpr )?
                int alt165=2;
                int LA165_0 = input.LA(1);

                if ( ((LA165_0>=IN_SET && LA165_0<=REGEXP)||LA165_0==NOT_EXPR||(LA165_0>=SUM && LA165_0<=AVG)||(LA165_0>=COALESCE && LA165_0<=COUNT)||(LA165_0>=CASE && LA165_0<=CASE2)||LA165_0==ISTREAM||(LA165_0>=PREVIOUS && LA165_0<=EXISTS)||(LA165_0>=INSTANCEOF && LA165_0<=CURRENT_TIMESTAMP)||LA165_0==NEWKW||(LA165_0>=EVAL_AND_EXPR && LA165_0<=EVAL_NOTEQUALS_GROUP_EXPR)||LA165_0==EVENT_PROP_EXPR||LA165_0==CONCAT||(LA165_0>=LIB_FUNC_CHAIN && LA165_0<=DOT_EXPR)||LA165_0==ARRAY_EXPR||(LA165_0>=NOT_IN_SET && LA165_0<=NOT_REGEXP)||(LA165_0>=IN_RANGE && LA165_0<=SUBSELECT_EXPR)||(LA165_0>=EXISTS_SUBSELECT_EXPR && LA165_0<=NOT_IN_SUBSELECT_EXPR)||LA165_0==SUBSTITUTION||(LA165_0>=FIRST_AGGREG && LA165_0<=WINDOW_AGGREG)||(LA165_0>=INT_TYPE && LA165_0<=NULL_TYPE)||(LA165_0>=JSON_OBJECT && LA165_0<=JSON_ARRAY)||LA165_0==STAR||(LA165_0>=LT && LA165_0<=GT)||(LA165_0>=BOR && LA165_0<=PLUS)||(LA165_0>=BAND && LA165_0<=BXOR)||(LA165_0>=LE && LA165_0<=GE)||(LA165_0>=MINUS && LA165_0<=MOD)||(LA165_0>=EVAL_IS_GROUP_EXPR && LA165_0<=EVAL_ISNOT_GROUP_EXPR)) ) {
                    alt165=1;
                }
                switch (alt165) {
                    case 1 :
                        // EsperEPL2Ast.g:482:139: valueExpr
                        {
                        pushFollow(FOLLOW_valueExpr_in_propertyExpressionAtom2845);
                        valueExpr();

                        state._fsp--;


                        }
                        break;

                }


                match(input, Token.UP, null); 
            }
             leaveNode(a); 

            match(input, Token.UP, null); 

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "propertyExpressionAtom"


    // $ANTLR start "propertySelectionListElement"
    // EsperEPL2Ast.g:485:1: propertySelectionListElement : (w= PROPERTY_WILDCARD_SELECT | ^(e= PROPERTY_SELECTION_ELEMENT_EXPR valueExpr ( IDENT )? ) | ^(s= PROPERTY_SELECTION_STREAM IDENT ( IDENT )? ) );
    public final void propertySelectionListElement() throws RecognitionException {
        CommonTree w=null;
        CommonTree e=null;
        CommonTree s=null;

        try {
            // EsperEPL2Ast.g:486:2: (w= PROPERTY_WILDCARD_SELECT | ^(e= PROPERTY_SELECTION_ELEMENT_EXPR valueExpr ( IDENT )? ) | ^(s= PROPERTY_SELECTION_STREAM IDENT ( IDENT )? ) )
            int alt168=3;
            switch ( input.LA(1) ) {
            case PROPERTY_WILDCARD_SELECT:
                {
                alt168=1;
                }
                break;
            case PROPERTY_SELECTION_ELEMENT_EXPR:
                {
                alt168=2;
                }
                break;
            case PROPERTY_SELECTION_STREAM:
                {
                alt168=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 168, 0, input);

                throw nvae;
            }

            switch (alt168) {
                case 1 :
                    // EsperEPL2Ast.g:486:4: w= PROPERTY_WILDCARD_SELECT
                    {
                    w=(CommonTree)match(input,PROPERTY_WILDCARD_SELECT,FOLLOW_PROPERTY_WILDCARD_SELECT_in_propertySelectionListElement2865); 
                     leaveNode(w); 

                    }
                    break;
                case 2 :
                    // EsperEPL2Ast.g:487:4: ^(e= PROPERTY_SELECTION_ELEMENT_EXPR valueExpr ( IDENT )? )
                    {
                    e=(CommonTree)match(input,PROPERTY_SELECTION_ELEMENT_EXPR,FOLLOW_PROPERTY_SELECTION_ELEMENT_EXPR_in_propertySelectionListElement2875); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_valueExpr_in_propertySelectionListElement2877);
                    valueExpr();

                    state._fsp--;

                    // EsperEPL2Ast.g:487:50: ( IDENT )?
                    int alt166=2;
                    int LA166_0 = input.LA(1);

                    if ( (LA166_0==IDENT) ) {
                        alt166=1;
                    }
                    switch (alt166) {
                        case 1 :
                            // EsperEPL2Ast.g:487:51: IDENT
                            {
                            match(input,IDENT,FOLLOW_IDENT_in_propertySelectionListElement2880); 

                            }
                            break;

                    }

                     leaveNode(e); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 3 :
                    // EsperEPL2Ast.g:488:4: ^(s= PROPERTY_SELECTION_STREAM IDENT ( IDENT )? )
                    {
                    s=(CommonTree)match(input,PROPERTY_SELECTION_STREAM,FOLLOW_PROPERTY_SELECTION_STREAM_in_propertySelectionListElement2894); 

                    match(input, Token.DOWN, null); 
                    match(input,IDENT,FOLLOW_IDENT_in_propertySelectionListElement2896); 
                    // EsperEPL2Ast.g:488:40: ( IDENT )?
                    int alt167=2;
                    int LA167_0 = input.LA(1);

                    if ( (LA167_0==IDENT) ) {
                        alt167=1;
                    }
                    switch (alt167) {
                        case 1 :
                            // EsperEPL2Ast.g:488:41: IDENT
                            {
                            match(input,IDENT,FOLLOW_IDENT_in_propertySelectionListElement2899); 

                            }
                            break;

                    }

                     leaveNode(s); 

                    match(input, Token.UP, null); 

                    }
                    break;

            }
        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "propertySelectionListElement"


    // $ANTLR start "patternInclusionExpression"
    // EsperEPL2Ast.g:491:1: patternInclusionExpression : ^(p= PATTERN_INCL_EXPR exprChoice ) ;
    public final void patternInclusionExpression() throws RecognitionException {
        CommonTree p=null;

        try {
            // EsperEPL2Ast.g:492:2: ( ^(p= PATTERN_INCL_EXPR exprChoice ) )
            // EsperEPL2Ast.g:492:4: ^(p= PATTERN_INCL_EXPR exprChoice )
            {
            p=(CommonTree)match(input,PATTERN_INCL_EXPR,FOLLOW_PATTERN_INCL_EXPR_in_patternInclusionExpression2920); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_exprChoice_in_patternInclusionExpression2922);
            exprChoice();

            state._fsp--;

             leaveNode(p); 

            match(input, Token.UP, null); 

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "patternInclusionExpression"


    // $ANTLR start "databaseJoinExpression"
    // EsperEPL2Ast.g:495:1: databaseJoinExpression : ^( DATABASE_JOIN_EXPR IDENT ( STRING_LITERAL | QUOTED_STRING_LITERAL ) ( STRING_LITERAL | QUOTED_STRING_LITERAL )? ) ;
    public final void databaseJoinExpression() throws RecognitionException {
        try {
            // EsperEPL2Ast.g:496:2: ( ^( DATABASE_JOIN_EXPR IDENT ( STRING_LITERAL | QUOTED_STRING_LITERAL ) ( STRING_LITERAL | QUOTED_STRING_LITERAL )? ) )
            // EsperEPL2Ast.g:496:4: ^( DATABASE_JOIN_EXPR IDENT ( STRING_LITERAL | QUOTED_STRING_LITERAL ) ( STRING_LITERAL | QUOTED_STRING_LITERAL )? )
            {
            match(input,DATABASE_JOIN_EXPR,FOLLOW_DATABASE_JOIN_EXPR_in_databaseJoinExpression2939); 

            match(input, Token.DOWN, null); 
            match(input,IDENT,FOLLOW_IDENT_in_databaseJoinExpression2941); 
            if ( (input.LA(1)>=STRING_LITERAL && input.LA(1)<=QUOTED_STRING_LITERAL) ) {
                input.consume();
                state.errorRecovery=false;
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }

            // EsperEPL2Ast.g:496:72: ( STRING_LITERAL | QUOTED_STRING_LITERAL )?
            int alt169=2;
            int LA169_0 = input.LA(1);

            if ( ((LA169_0>=STRING_LITERAL && LA169_0<=QUOTED_STRING_LITERAL)) ) {
                alt169=1;
            }
            switch (alt169) {
                case 1 :
                    // EsperEPL2Ast.g:
                    {
                    if ( (input.LA(1)>=STRING_LITERAL && input.LA(1)<=QUOTED_STRING_LITERAL) ) {
                        input.consume();
                        state.errorRecovery=false;
                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        throw mse;
                    }


                    }
                    break;

            }


            match(input, Token.UP, null); 

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "databaseJoinExpression"


    // $ANTLR start "methodJoinExpression"
    // EsperEPL2Ast.g:499:1: methodJoinExpression : ^( METHOD_JOIN_EXPR IDENT CLASS_IDENT ( valueExpr )* ) ;
    public final void methodJoinExpression() throws RecognitionException {
        try {
            // EsperEPL2Ast.g:500:2: ( ^( METHOD_JOIN_EXPR IDENT CLASS_IDENT ( valueExpr )* ) )
            // EsperEPL2Ast.g:500:4: ^( METHOD_JOIN_EXPR IDENT CLASS_IDENT ( valueExpr )* )
            {
            match(input,METHOD_JOIN_EXPR,FOLLOW_METHOD_JOIN_EXPR_in_methodJoinExpression2972); 

            match(input, Token.DOWN, null); 
            match(input,IDENT,FOLLOW_IDENT_in_methodJoinExpression2974); 
            match(input,CLASS_IDENT,FOLLOW_CLASS_IDENT_in_methodJoinExpression2976); 
            // EsperEPL2Ast.g:500:41: ( valueExpr )*
            loop170:
            do {
                int alt170=2;
                int LA170_0 = input.LA(1);

                if ( ((LA170_0>=IN_SET && LA170_0<=REGEXP)||LA170_0==NOT_EXPR||(LA170_0>=SUM && LA170_0<=AVG)||(LA170_0>=COALESCE && LA170_0<=COUNT)||(LA170_0>=CASE && LA170_0<=CASE2)||LA170_0==ISTREAM||(LA170_0>=PREVIOUS && LA170_0<=EXISTS)||(LA170_0>=INSTANCEOF && LA170_0<=CURRENT_TIMESTAMP)||LA170_0==NEWKW||(LA170_0>=EVAL_AND_EXPR && LA170_0<=EVAL_NOTEQUALS_GROUP_EXPR)||LA170_0==EVENT_PROP_EXPR||LA170_0==CONCAT||(LA170_0>=LIB_FUNC_CHAIN && LA170_0<=DOT_EXPR)||LA170_0==ARRAY_EXPR||(LA170_0>=NOT_IN_SET && LA170_0<=NOT_REGEXP)||(LA170_0>=IN_RANGE && LA170_0<=SUBSELECT_EXPR)||(LA170_0>=EXISTS_SUBSELECT_EXPR && LA170_0<=NOT_IN_SUBSELECT_EXPR)||LA170_0==SUBSTITUTION||(LA170_0>=FIRST_AGGREG && LA170_0<=WINDOW_AGGREG)||(LA170_0>=INT_TYPE && LA170_0<=NULL_TYPE)||(LA170_0>=JSON_OBJECT && LA170_0<=JSON_ARRAY)||LA170_0==STAR||(LA170_0>=LT && LA170_0<=GT)||(LA170_0>=BOR && LA170_0<=PLUS)||(LA170_0>=BAND && LA170_0<=BXOR)||(LA170_0>=LE && LA170_0<=GE)||(LA170_0>=MINUS && LA170_0<=MOD)||(LA170_0>=EVAL_IS_GROUP_EXPR && LA170_0<=EVAL_ISNOT_GROUP_EXPR)) ) {
                    alt170=1;
                }


                switch (alt170) {
            	case 1 :
            	    // EsperEPL2Ast.g:500:42: valueExpr
            	    {
            	    pushFollow(FOLLOW_valueExpr_in_methodJoinExpression2979);
            	    valueExpr();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop170;
                }
            } while (true);


            match(input, Token.UP, null); 

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "methodJoinExpression"


    // $ANTLR start "viewListExpr"
    // EsperEPL2Ast.g:503:1: viewListExpr : viewExpr ( viewExpr )* ;
    public final void viewListExpr() throws RecognitionException {
        try {
            // EsperEPL2Ast.g:504:2: ( viewExpr ( viewExpr )* )
            // EsperEPL2Ast.g:504:4: viewExpr ( viewExpr )*
            {
            pushFollow(FOLLOW_viewExpr_in_viewListExpr2993);
            viewExpr();

            state._fsp--;

            // EsperEPL2Ast.g:504:13: ( viewExpr )*
            loop171:
            do {
                int alt171=2;
                int LA171_0 = input.LA(1);

                if ( (LA171_0==VIEW_EXPR) ) {
                    alt171=1;
                }


                switch (alt171) {
            	case 1 :
            	    // EsperEPL2Ast.g:504:14: viewExpr
            	    {
            	    pushFollow(FOLLOW_viewExpr_in_viewListExpr2996);
            	    viewExpr();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop171;
                }
            } while (true);


            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "viewListExpr"


    // $ANTLR start "viewExpr"
    // EsperEPL2Ast.g:507:1: viewExpr : ^(n= VIEW_EXPR IDENT IDENT ( valueExprWithTime )* ) ;
    public final void viewExpr() throws RecognitionException {
        CommonTree n=null;

        try {
            // EsperEPL2Ast.g:508:2: ( ^(n= VIEW_EXPR IDENT IDENT ( valueExprWithTime )* ) )
            // EsperEPL2Ast.g:508:4: ^(n= VIEW_EXPR IDENT IDENT ( valueExprWithTime )* )
            {
            n=(CommonTree)match(input,VIEW_EXPR,FOLLOW_VIEW_EXPR_in_viewExpr3013); 

            match(input, Token.DOWN, null); 
            match(input,IDENT,FOLLOW_IDENT_in_viewExpr3015); 
            match(input,IDENT,FOLLOW_IDENT_in_viewExpr3017); 
            // EsperEPL2Ast.g:508:30: ( valueExprWithTime )*
            loop172:
            do {
                int alt172=2;
                int LA172_0 = input.LA(1);

                if ( ((LA172_0>=IN_SET && LA172_0<=REGEXP)||LA172_0==NOT_EXPR||(LA172_0>=SUM && LA172_0<=AVG)||(LA172_0>=COALESCE && LA172_0<=COUNT)||(LA172_0>=CASE && LA172_0<=CASE2)||LA172_0==LAST||LA172_0==ISTREAM||(LA172_0>=PREVIOUS && LA172_0<=EXISTS)||(LA172_0>=LW && LA172_0<=CURRENT_TIMESTAMP)||LA172_0==NEWKW||(LA172_0>=NUMERIC_PARAM_RANGE && LA172_0<=OBJECT_PARAM_ORDERED_EXPR)||(LA172_0>=EVAL_AND_EXPR && LA172_0<=EVAL_NOTEQUALS_GROUP_EXPR)||LA172_0==EVENT_PROP_EXPR||LA172_0==CONCAT||(LA172_0>=LIB_FUNC_CHAIN && LA172_0<=DOT_EXPR)||(LA172_0>=TIME_PERIOD && LA172_0<=ARRAY_EXPR)||(LA172_0>=NOT_IN_SET && LA172_0<=NOT_REGEXP)||(LA172_0>=IN_RANGE && LA172_0<=SUBSELECT_EXPR)||(LA172_0>=EXISTS_SUBSELECT_EXPR && LA172_0<=NOT_IN_SUBSELECT_EXPR)||(LA172_0>=LAST_OPERATOR && LA172_0<=SUBSTITUTION)||LA172_0==NUMBERSETSTAR||(LA172_0>=FIRST_AGGREG && LA172_0<=WINDOW_AGGREG)||(LA172_0>=INT_TYPE && LA172_0<=NULL_TYPE)||(LA172_0>=JSON_OBJECT && LA172_0<=JSON_ARRAY)||LA172_0==STAR||(LA172_0>=LT && LA172_0<=GT)||(LA172_0>=BOR && LA172_0<=PLUS)||(LA172_0>=BAND && LA172_0<=BXOR)||(LA172_0>=LE && LA172_0<=GE)||(LA172_0>=MINUS && LA172_0<=MOD)||(LA172_0>=EVAL_IS_GROUP_EXPR && LA172_0<=EVAL_ISNOT_GROUP_EXPR)) ) {
                    alt172=1;
                }


                switch (alt172) {
            	case 1 :
            	    // EsperEPL2Ast.g:508:31: valueExprWithTime
            	    {
            	    pushFollow(FOLLOW_valueExprWithTime_in_viewExpr3020);
            	    valueExprWithTime();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop172;
                }
            } while (true);

             leaveNode(n); 

            match(input, Token.UP, null); 

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "viewExpr"


    // $ANTLR start "whereClause"
    // EsperEPL2Ast.g:511:1: whereClause[boolean isLeaveNode] : ^(n= WHERE_EXPR valueExpr ) ;
    public final void whereClause(boolean isLeaveNode) throws RecognitionException {
        CommonTree n=null;

        try {
            // EsperEPL2Ast.g:512:2: ( ^(n= WHERE_EXPR valueExpr ) )
            // EsperEPL2Ast.g:512:4: ^(n= WHERE_EXPR valueExpr )
            {
            n=(CommonTree)match(input,WHERE_EXPR,FOLLOW_WHERE_EXPR_in_whereClause3042); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_valueExpr_in_whereClause3044);
            valueExpr();

            state._fsp--;

             if (isLeaveNode) leaveNode(n); 

            match(input, Token.UP, null); 

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "whereClause"


    // $ANTLR start "groupByClause"
    // EsperEPL2Ast.g:515:1: groupByClause : ^(g= GROUP_BY_EXPR valueExpr ( valueExpr )* ) ;
    public final void groupByClause() throws RecognitionException {
        CommonTree g=null;

        try {
            // EsperEPL2Ast.g:516:2: ( ^(g= GROUP_BY_EXPR valueExpr ( valueExpr )* ) )
            // EsperEPL2Ast.g:516:4: ^(g= GROUP_BY_EXPR valueExpr ( valueExpr )* )
            {
            g=(CommonTree)match(input,GROUP_BY_EXPR,FOLLOW_GROUP_BY_EXPR_in_groupByClause3062); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_valueExpr_in_groupByClause3064);
            valueExpr();

            state._fsp--;

            // EsperEPL2Ast.g:516:32: ( valueExpr )*
            loop173:
            do {
                int alt173=2;
                int LA173_0 = input.LA(1);

                if ( ((LA173_0>=IN_SET && LA173_0<=REGEXP)||LA173_0==NOT_EXPR||(LA173_0>=SUM && LA173_0<=AVG)||(LA173_0>=COALESCE && LA173_0<=COUNT)||(LA173_0>=CASE && LA173_0<=CASE2)||LA173_0==ISTREAM||(LA173_0>=PREVIOUS && LA173_0<=EXISTS)||(LA173_0>=INSTANCEOF && LA173_0<=CURRENT_TIMESTAMP)||LA173_0==NEWKW||(LA173_0>=EVAL_AND_EXPR && LA173_0<=EVAL_NOTEQUALS_GROUP_EXPR)||LA173_0==EVENT_PROP_EXPR||LA173_0==CONCAT||(LA173_0>=LIB_FUNC_CHAIN && LA173_0<=DOT_EXPR)||LA173_0==ARRAY_EXPR||(LA173_0>=NOT_IN_SET && LA173_0<=NOT_REGEXP)||(LA173_0>=IN_RANGE && LA173_0<=SUBSELECT_EXPR)||(LA173_0>=EXISTS_SUBSELECT_EXPR && LA173_0<=NOT_IN_SUBSELECT_EXPR)||LA173_0==SUBSTITUTION||(LA173_0>=FIRST_AGGREG && LA173_0<=WINDOW_AGGREG)||(LA173_0>=INT_TYPE && LA173_0<=NULL_TYPE)||(LA173_0>=JSON_OBJECT && LA173_0<=JSON_ARRAY)||LA173_0==STAR||(LA173_0>=LT && LA173_0<=GT)||(LA173_0>=BOR && LA173_0<=PLUS)||(LA173_0>=BAND && LA173_0<=BXOR)||(LA173_0>=LE && LA173_0<=GE)||(LA173_0>=MINUS && LA173_0<=MOD)||(LA173_0>=EVAL_IS_GROUP_EXPR && LA173_0<=EVAL_ISNOT_GROUP_EXPR)) ) {
                    alt173=1;
                }


                switch (alt173) {
            	case 1 :
            	    // EsperEPL2Ast.g:516:33: valueExpr
            	    {
            	    pushFollow(FOLLOW_valueExpr_in_groupByClause3067);
            	    valueExpr();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop173;
                }
            } while (true);


            match(input, Token.UP, null); 
             leaveNode(g); 

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "groupByClause"


    // $ANTLR start "orderByClause"
    // EsperEPL2Ast.g:519:1: orderByClause : ^( ORDER_BY_EXPR orderByElement ( orderByElement )* ) ;
    public final void orderByClause() throws RecognitionException {
        try {
            // EsperEPL2Ast.g:520:2: ( ^( ORDER_BY_EXPR orderByElement ( orderByElement )* ) )
            // EsperEPL2Ast.g:520:4: ^( ORDER_BY_EXPR orderByElement ( orderByElement )* )
            {
            match(input,ORDER_BY_EXPR,FOLLOW_ORDER_BY_EXPR_in_orderByClause3085); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_orderByElement_in_orderByClause3087);
            orderByElement();

            state._fsp--;

            // EsperEPL2Ast.g:520:35: ( orderByElement )*
            loop174:
            do {
                int alt174=2;
                int LA174_0 = input.LA(1);

                if ( (LA174_0==ORDER_ELEMENT_EXPR) ) {
                    alt174=1;
                }


                switch (alt174) {
            	case 1 :
            	    // EsperEPL2Ast.g:520:36: orderByElement
            	    {
            	    pushFollow(FOLLOW_orderByElement_in_orderByClause3090);
            	    orderByElement();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop174;
                }
            } while (true);


            match(input, Token.UP, null); 

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "orderByClause"


    // $ANTLR start "orderByElement"
    // EsperEPL2Ast.g:523:1: orderByElement : ^(e= ORDER_ELEMENT_EXPR valueExpr ( ASC | DESC )? ) ;
    public final void orderByElement() throws RecognitionException {
        CommonTree e=null;

        try {
            // EsperEPL2Ast.g:524:2: ( ^(e= ORDER_ELEMENT_EXPR valueExpr ( ASC | DESC )? ) )
            // EsperEPL2Ast.g:524:5: ^(e= ORDER_ELEMENT_EXPR valueExpr ( ASC | DESC )? )
            {
            e=(CommonTree)match(input,ORDER_ELEMENT_EXPR,FOLLOW_ORDER_ELEMENT_EXPR_in_orderByElement3110); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_valueExpr_in_orderByElement3112);
            valueExpr();

            state._fsp--;

            // EsperEPL2Ast.g:524:38: ( ASC | DESC )?
            int alt175=2;
            int LA175_0 = input.LA(1);

            if ( ((LA175_0>=ASC && LA175_0<=DESC)) ) {
                alt175=1;
            }
            switch (alt175) {
                case 1 :
                    // EsperEPL2Ast.g:
                    {
                    if ( (input.LA(1)>=ASC && input.LA(1)<=DESC) ) {
                        input.consume();
                        state.errorRecovery=false;
                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        throw mse;
                    }


                    }
                    break;

            }

             leaveNode(e); 

            match(input, Token.UP, null); 

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "orderByElement"


    // $ANTLR start "havingClause"
    // EsperEPL2Ast.g:527:1: havingClause : ^(n= HAVING_EXPR valueExpr ) ;
    public final void havingClause() throws RecognitionException {
        CommonTree n=null;

        try {
            // EsperEPL2Ast.g:528:2: ( ^(n= HAVING_EXPR valueExpr ) )
            // EsperEPL2Ast.g:528:4: ^(n= HAVING_EXPR valueExpr )
            {
            n=(CommonTree)match(input,HAVING_EXPR,FOLLOW_HAVING_EXPR_in_havingClause3137); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_valueExpr_in_havingClause3139);
            valueExpr();

            state._fsp--;

             leaveNode(n); 

            match(input, Token.UP, null); 

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "havingClause"


    // $ANTLR start "outputLimitExpr"
    // EsperEPL2Ast.g:531:1: outputLimitExpr : ( ^(e= EVENT_LIMIT_EXPR ( ALL | FIRST | LAST | SNAPSHOT )? ( number | IDENT ) ( outputLimitAfter )? ( outputLimitAndTerm )? ) | ^(tp= TIMEPERIOD_LIMIT_EXPR ( ALL | FIRST | LAST | SNAPSHOT )? timePeriod ( outputLimitAfter )? ( outputLimitAndTerm )? ) | ^(cron= CRONTAB_LIMIT_EXPR ( ALL | FIRST | LAST | SNAPSHOT )? crontabLimitParameterSet ( outputLimitAfter )? ( outputLimitAndTerm )? ) | ^(when= WHEN_LIMIT_EXPR ( ALL | FIRST | LAST | SNAPSHOT )? valueExpr ( onSetExpr )? ( outputLimitAfter )? ( outputLimitAndTerm )? ) | ^(term= TERM_LIMIT_EXPR ( ALL | FIRST | LAST | SNAPSHOT )? outputLimitAndTerm ( onSetExpr )? ( outputLimitAfter )? ( outputLimitAndTerm )? ) | ^(after= AFTER_LIMIT_EXPR outputLimitAfter ( outputLimitAndTerm )? ) );
    public final void outputLimitExpr() throws RecognitionException {
        CommonTree e=null;
        CommonTree tp=null;
        CommonTree cron=null;
        CommonTree when=null;
        CommonTree term=null;
        CommonTree after=null;

        try {
            // EsperEPL2Ast.g:532:2: ( ^(e= EVENT_LIMIT_EXPR ( ALL | FIRST | LAST | SNAPSHOT )? ( number | IDENT ) ( outputLimitAfter )? ( outputLimitAndTerm )? ) | ^(tp= TIMEPERIOD_LIMIT_EXPR ( ALL | FIRST | LAST | SNAPSHOT )? timePeriod ( outputLimitAfter )? ( outputLimitAndTerm )? ) | ^(cron= CRONTAB_LIMIT_EXPR ( ALL | FIRST | LAST | SNAPSHOT )? crontabLimitParameterSet ( outputLimitAfter )? ( outputLimitAndTerm )? ) | ^(when= WHEN_LIMIT_EXPR ( ALL | FIRST | LAST | SNAPSHOT )? valueExpr ( onSetExpr )? ( outputLimitAfter )? ( outputLimitAndTerm )? ) | ^(term= TERM_LIMIT_EXPR ( ALL | FIRST | LAST | SNAPSHOT )? outputLimitAndTerm ( onSetExpr )? ( outputLimitAfter )? ( outputLimitAndTerm )? ) | ^(after= AFTER_LIMIT_EXPR outputLimitAfter ( outputLimitAndTerm )? ) )
            int alt195=6;
            switch ( input.LA(1) ) {
            case EVENT_LIMIT_EXPR:
                {
                alt195=1;
                }
                break;
            case TIMEPERIOD_LIMIT_EXPR:
                {
                alt195=2;
                }
                break;
            case CRONTAB_LIMIT_EXPR:
                {
                alt195=3;
                }
                break;
            case WHEN_LIMIT_EXPR:
                {
                alt195=4;
                }
                break;
            case TERM_LIMIT_EXPR:
                {
                alt195=5;
                }
                break;
            case AFTER_LIMIT_EXPR:
                {
                alt195=6;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 195, 0, input);

                throw nvae;
            }

            switch (alt195) {
                case 1 :
                    // EsperEPL2Ast.g:532:4: ^(e= EVENT_LIMIT_EXPR ( ALL | FIRST | LAST | SNAPSHOT )? ( number | IDENT ) ( outputLimitAfter )? ( outputLimitAndTerm )? )
                    {
                    e=(CommonTree)match(input,EVENT_LIMIT_EXPR,FOLLOW_EVENT_LIMIT_EXPR_in_outputLimitExpr3157); 

                    match(input, Token.DOWN, null); 
                    // EsperEPL2Ast.g:532:25: ( ALL | FIRST | LAST | SNAPSHOT )?
                    int alt176=2;
                    int LA176_0 = input.LA(1);

                    if ( (LA176_0==ALL||(LA176_0>=FIRST && LA176_0<=LAST)||LA176_0==SNAPSHOT) ) {
                        alt176=1;
                    }
                    switch (alt176) {
                        case 1 :
                            // EsperEPL2Ast.g:
                            {
                            if ( input.LA(1)==ALL||(input.LA(1)>=FIRST && input.LA(1)<=LAST)||input.LA(1)==SNAPSHOT ) {
                                input.consume();
                                state.errorRecovery=false;
                            }
                            else {
                                MismatchedSetException mse = new MismatchedSetException(null,input);
                                throw mse;
                            }


                            }
                            break;

                    }

                    // EsperEPL2Ast.g:532:52: ( number | IDENT )
                    int alt177=2;
                    int LA177_0 = input.LA(1);

                    if ( ((LA177_0>=INT_TYPE && LA177_0<=DOUBLE_TYPE)) ) {
                        alt177=1;
                    }
                    else if ( (LA177_0==IDENT) ) {
                        alt177=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 177, 0, input);

                        throw nvae;
                    }
                    switch (alt177) {
                        case 1 :
                            // EsperEPL2Ast.g:532:53: number
                            {
                            pushFollow(FOLLOW_number_in_outputLimitExpr3171);
                            number();

                            state._fsp--;


                            }
                            break;
                        case 2 :
                            // EsperEPL2Ast.g:532:60: IDENT
                            {
                            match(input,IDENT,FOLLOW_IDENT_in_outputLimitExpr3173); 

                            }
                            break;

                    }

                    // EsperEPL2Ast.g:532:67: ( outputLimitAfter )?
                    int alt178=2;
                    int LA178_0 = input.LA(1);

                    if ( (LA178_0==AFTER) ) {
                        alt178=1;
                    }
                    switch (alt178) {
                        case 1 :
                            // EsperEPL2Ast.g:532:67: outputLimitAfter
                            {
                            pushFollow(FOLLOW_outputLimitAfter_in_outputLimitExpr3176);
                            outputLimitAfter();

                            state._fsp--;


                            }
                            break;

                    }

                    // EsperEPL2Ast.g:532:85: ( outputLimitAndTerm )?
                    int alt179=2;
                    int LA179_0 = input.LA(1);

                    if ( (LA179_0==TERMINATED) ) {
                        alt179=1;
                    }
                    switch (alt179) {
                        case 1 :
                            // EsperEPL2Ast.g:532:85: outputLimitAndTerm
                            {
                            pushFollow(FOLLOW_outputLimitAndTerm_in_outputLimitExpr3179);
                            outputLimitAndTerm();

                            state._fsp--;


                            }
                            break;

                    }

                     leaveNode(e); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    // EsperEPL2Ast.g:533:7: ^(tp= TIMEPERIOD_LIMIT_EXPR ( ALL | FIRST | LAST | SNAPSHOT )? timePeriod ( outputLimitAfter )? ( outputLimitAndTerm )? )
                    {
                    tp=(CommonTree)match(input,TIMEPERIOD_LIMIT_EXPR,FOLLOW_TIMEPERIOD_LIMIT_EXPR_in_outputLimitExpr3196); 

                    match(input, Token.DOWN, null); 
                    // EsperEPL2Ast.g:533:34: ( ALL | FIRST | LAST | SNAPSHOT )?
                    int alt180=2;
                    int LA180_0 = input.LA(1);

                    if ( (LA180_0==ALL||(LA180_0>=FIRST && LA180_0<=LAST)||LA180_0==SNAPSHOT) ) {
                        alt180=1;
                    }
                    switch (alt180) {
                        case 1 :
                            // EsperEPL2Ast.g:
                            {
                            if ( input.LA(1)==ALL||(input.LA(1)>=FIRST && input.LA(1)<=LAST)||input.LA(1)==SNAPSHOT ) {
                                input.consume();
                                state.errorRecovery=false;
                            }
                            else {
                                MismatchedSetException mse = new MismatchedSetException(null,input);
                                throw mse;
                            }


                            }
                            break;

                    }

                    pushFollow(FOLLOW_timePeriod_in_outputLimitExpr3209);
                    timePeriod();

                    state._fsp--;

                    // EsperEPL2Ast.g:533:72: ( outputLimitAfter )?
                    int alt181=2;
                    int LA181_0 = input.LA(1);

                    if ( (LA181_0==AFTER) ) {
                        alt181=1;
                    }
                    switch (alt181) {
                        case 1 :
                            // EsperEPL2Ast.g:533:72: outputLimitAfter
                            {
                            pushFollow(FOLLOW_outputLimitAfter_in_outputLimitExpr3211);
                            outputLimitAfter();

                            state._fsp--;


                            }
                            break;

                    }

                    // EsperEPL2Ast.g:533:90: ( outputLimitAndTerm )?
                    int alt182=2;
                    int LA182_0 = input.LA(1);

                    if ( (LA182_0==TERMINATED) ) {
                        alt182=1;
                    }
                    switch (alt182) {
                        case 1 :
                            // EsperEPL2Ast.g:533:90: outputLimitAndTerm
                            {
                            pushFollow(FOLLOW_outputLimitAndTerm_in_outputLimitExpr3214);
                            outputLimitAndTerm();

                            state._fsp--;


                            }
                            break;

                    }

                     leaveNode(tp); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 3 :
                    // EsperEPL2Ast.g:534:7: ^(cron= CRONTAB_LIMIT_EXPR ( ALL | FIRST | LAST | SNAPSHOT )? crontabLimitParameterSet ( outputLimitAfter )? ( outputLimitAndTerm )? )
                    {
                    cron=(CommonTree)match(input,CRONTAB_LIMIT_EXPR,FOLLOW_CRONTAB_LIMIT_EXPR_in_outputLimitExpr3230); 

                    match(input, Token.DOWN, null); 
                    // EsperEPL2Ast.g:534:33: ( ALL | FIRST | LAST | SNAPSHOT )?
                    int alt183=2;
                    int LA183_0 = input.LA(1);

                    if ( (LA183_0==ALL||(LA183_0>=FIRST && LA183_0<=LAST)||LA183_0==SNAPSHOT) ) {
                        alt183=1;
                    }
                    switch (alt183) {
                        case 1 :
                            // EsperEPL2Ast.g:
                            {
                            if ( input.LA(1)==ALL||(input.LA(1)>=FIRST && input.LA(1)<=LAST)||input.LA(1)==SNAPSHOT ) {
                                input.consume();
                                state.errorRecovery=false;
                            }
                            else {
                                MismatchedSetException mse = new MismatchedSetException(null,input);
                                throw mse;
                            }


                            }
                            break;

                    }

                    pushFollow(FOLLOW_crontabLimitParameterSet_in_outputLimitExpr3243);
                    crontabLimitParameterSet();

                    state._fsp--;

                    // EsperEPL2Ast.g:534:85: ( outputLimitAfter )?
                    int alt184=2;
                    int LA184_0 = input.LA(1);

                    if ( (LA184_0==AFTER) ) {
                        alt184=1;
                    }
                    switch (alt184) {
                        case 1 :
                            // EsperEPL2Ast.g:534:85: outputLimitAfter
                            {
                            pushFollow(FOLLOW_outputLimitAfter_in_outputLimitExpr3245);
                            outputLimitAfter();

                            state._fsp--;


                            }
                            break;

                    }

                    // EsperEPL2Ast.g:534:103: ( outputLimitAndTerm )?
                    int alt185=2;
                    int LA185_0 = input.LA(1);

                    if ( (LA185_0==TERMINATED) ) {
                        alt185=1;
                    }
                    switch (alt185) {
                        case 1 :
                            // EsperEPL2Ast.g:534:103: outputLimitAndTerm
                            {
                            pushFollow(FOLLOW_outputLimitAndTerm_in_outputLimitExpr3248);
                            outputLimitAndTerm();

                            state._fsp--;


                            }
                            break;

                    }

                     leaveNode(cron); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 4 :
                    // EsperEPL2Ast.g:535:7: ^(when= WHEN_LIMIT_EXPR ( ALL | FIRST | LAST | SNAPSHOT )? valueExpr ( onSetExpr )? ( outputLimitAfter )? ( outputLimitAndTerm )? )
                    {
                    when=(CommonTree)match(input,WHEN_LIMIT_EXPR,FOLLOW_WHEN_LIMIT_EXPR_in_outputLimitExpr3264); 

                    match(input, Token.DOWN, null); 
                    // EsperEPL2Ast.g:535:30: ( ALL | FIRST | LAST | SNAPSHOT )?
                    int alt186=2;
                    int LA186_0 = input.LA(1);

                    if ( (LA186_0==ALL||(LA186_0>=FIRST && LA186_0<=LAST)||LA186_0==SNAPSHOT) ) {
                        alt186=1;
                    }
                    switch (alt186) {
                        case 1 :
                            // EsperEPL2Ast.g:
                            {
                            if ( input.LA(1)==ALL||(input.LA(1)>=FIRST && input.LA(1)<=LAST)||input.LA(1)==SNAPSHOT ) {
                                input.consume();
                                state.errorRecovery=false;
                            }
                            else {
                                MismatchedSetException mse = new MismatchedSetException(null,input);
                                throw mse;
                            }


                            }
                            break;

                    }

                    pushFollow(FOLLOW_valueExpr_in_outputLimitExpr3277);
                    valueExpr();

                    state._fsp--;

                    // EsperEPL2Ast.g:535:67: ( onSetExpr )?
                    int alt187=2;
                    int LA187_0 = input.LA(1);

                    if ( (LA187_0==ON_SET_EXPR) ) {
                        alt187=1;
                    }
                    switch (alt187) {
                        case 1 :
                            // EsperEPL2Ast.g:535:67: onSetExpr
                            {
                            pushFollow(FOLLOW_onSetExpr_in_outputLimitExpr3279);
                            onSetExpr();

                            state._fsp--;


                            }
                            break;

                    }

                    // EsperEPL2Ast.g:535:78: ( outputLimitAfter )?
                    int alt188=2;
                    int LA188_0 = input.LA(1);

                    if ( (LA188_0==AFTER) ) {
                        alt188=1;
                    }
                    switch (alt188) {
                        case 1 :
                            // EsperEPL2Ast.g:535:78: outputLimitAfter
                            {
                            pushFollow(FOLLOW_outputLimitAfter_in_outputLimitExpr3282);
                            outputLimitAfter();

                            state._fsp--;


                            }
                            break;

                    }

                    // EsperEPL2Ast.g:535:96: ( outputLimitAndTerm )?
                    int alt189=2;
                    int LA189_0 = input.LA(1);

                    if ( (LA189_0==TERMINATED) ) {
                        alt189=1;
                    }
                    switch (alt189) {
                        case 1 :
                            // EsperEPL2Ast.g:535:96: outputLimitAndTerm
                            {
                            pushFollow(FOLLOW_outputLimitAndTerm_in_outputLimitExpr3285);
                            outputLimitAndTerm();

                            state._fsp--;


                            }
                            break;

                    }

                     leaveNode(when); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 5 :
                    // EsperEPL2Ast.g:536:7: ^(term= TERM_LIMIT_EXPR ( ALL | FIRST | LAST | SNAPSHOT )? outputLimitAndTerm ( onSetExpr )? ( outputLimitAfter )? ( outputLimitAndTerm )? )
                    {
                    term=(CommonTree)match(input,TERM_LIMIT_EXPR,FOLLOW_TERM_LIMIT_EXPR_in_outputLimitExpr3301); 

                    match(input, Token.DOWN, null); 
                    // EsperEPL2Ast.g:536:30: ( ALL | FIRST | LAST | SNAPSHOT )?
                    int alt190=2;
                    int LA190_0 = input.LA(1);

                    if ( (LA190_0==ALL||(LA190_0>=FIRST && LA190_0<=LAST)||LA190_0==SNAPSHOT) ) {
                        alt190=1;
                    }
                    switch (alt190) {
                        case 1 :
                            // EsperEPL2Ast.g:
                            {
                            if ( input.LA(1)==ALL||(input.LA(1)>=FIRST && input.LA(1)<=LAST)||input.LA(1)==SNAPSHOT ) {
                                input.consume();
                                state.errorRecovery=false;
                            }
                            else {
                                MismatchedSetException mse = new MismatchedSetException(null,input);
                                throw mse;
                            }


                            }
                            break;

                    }

                    pushFollow(FOLLOW_outputLimitAndTerm_in_outputLimitExpr3314);
                    outputLimitAndTerm();

                    state._fsp--;

                    // EsperEPL2Ast.g:536:76: ( onSetExpr )?
                    int alt191=2;
                    int LA191_0 = input.LA(1);

                    if ( (LA191_0==ON_SET_EXPR) ) {
                        alt191=1;
                    }
                    switch (alt191) {
                        case 1 :
                            // EsperEPL2Ast.g:536:76: onSetExpr
                            {
                            pushFollow(FOLLOW_onSetExpr_in_outputLimitExpr3316);
                            onSetExpr();

                            state._fsp--;


                            }
                            break;

                    }

                    // EsperEPL2Ast.g:536:87: ( outputLimitAfter )?
                    int alt192=2;
                    int LA192_0 = input.LA(1);

                    if ( (LA192_0==AFTER) ) {
                        alt192=1;
                    }
                    switch (alt192) {
                        case 1 :
                            // EsperEPL2Ast.g:536:87: outputLimitAfter
                            {
                            pushFollow(FOLLOW_outputLimitAfter_in_outputLimitExpr3319);
                            outputLimitAfter();

                            state._fsp--;


                            }
                            break;

                    }

                    // EsperEPL2Ast.g:536:105: ( outputLimitAndTerm )?
                    int alt193=2;
                    int LA193_0 = input.LA(1);

                    if ( (LA193_0==TERMINATED) ) {
                        alt193=1;
                    }
                    switch (alt193) {
                        case 1 :
                            // EsperEPL2Ast.g:536:105: outputLimitAndTerm
                            {
                            pushFollow(FOLLOW_outputLimitAndTerm_in_outputLimitExpr3322);
                            outputLimitAndTerm();

                            state._fsp--;


                            }
                            break;

                    }

                     leaveNode(term); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 6 :
                    // EsperEPL2Ast.g:537:4: ^(after= AFTER_LIMIT_EXPR outputLimitAfter ( outputLimitAndTerm )? )
                    {
                    after=(CommonTree)match(input,AFTER_LIMIT_EXPR,FOLLOW_AFTER_LIMIT_EXPR_in_outputLimitExpr3335); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_outputLimitAfter_in_outputLimitExpr3337);
                    outputLimitAfter();

                    state._fsp--;

                    // EsperEPL2Ast.g:537:46: ( outputLimitAndTerm )?
                    int alt194=2;
                    int LA194_0 = input.LA(1);

                    if ( (LA194_0==TERMINATED) ) {
                        alt194=1;
                    }
                    switch (alt194) {
                        case 1 :
                            // EsperEPL2Ast.g:537:46: outputLimitAndTerm
                            {
                            pushFollow(FOLLOW_outputLimitAndTerm_in_outputLimitExpr3339);
                            outputLimitAndTerm();

                            state._fsp--;


                            }
                            break;

                    }

                     leaveNode(after); 

                    match(input, Token.UP, null); 

                    }
                    break;

            }
        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "outputLimitExpr"


    // $ANTLR start "outputLimitAndTerm"
    // EsperEPL2Ast.g:540:1: outputLimitAndTerm : ^( TERMINATED ( valueExpr )? ( onSetExpr )? ) ;
    public final void outputLimitAndTerm() throws RecognitionException {
        try {
            // EsperEPL2Ast.g:541:2: ( ^( TERMINATED ( valueExpr )? ( onSetExpr )? ) )
            // EsperEPL2Ast.g:541:5: ^( TERMINATED ( valueExpr )? ( onSetExpr )? )
            {
            match(input,TERMINATED,FOLLOW_TERMINATED_in_outputLimitAndTerm3356); 

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // EsperEPL2Ast.g:541:18: ( valueExpr )?
                int alt196=2;
                int LA196_0 = input.LA(1);

                if ( ((LA196_0>=IN_SET && LA196_0<=REGEXP)||LA196_0==NOT_EXPR||(LA196_0>=SUM && LA196_0<=AVG)||(LA196_0>=COALESCE && LA196_0<=COUNT)||(LA196_0>=CASE && LA196_0<=CASE2)||LA196_0==ISTREAM||(LA196_0>=PREVIOUS && LA196_0<=EXISTS)||(LA196_0>=INSTANCEOF && LA196_0<=CURRENT_TIMESTAMP)||LA196_0==NEWKW||(LA196_0>=EVAL_AND_EXPR && LA196_0<=EVAL_NOTEQUALS_GROUP_EXPR)||LA196_0==EVENT_PROP_EXPR||LA196_0==CONCAT||(LA196_0>=LIB_FUNC_CHAIN && LA196_0<=DOT_EXPR)||LA196_0==ARRAY_EXPR||(LA196_0>=NOT_IN_SET && LA196_0<=NOT_REGEXP)||(LA196_0>=IN_RANGE && LA196_0<=SUBSELECT_EXPR)||(LA196_0>=EXISTS_SUBSELECT_EXPR && LA196_0<=NOT_IN_SUBSELECT_EXPR)||LA196_0==SUBSTITUTION||(LA196_0>=FIRST_AGGREG && LA196_0<=WINDOW_AGGREG)||(LA196_0>=INT_TYPE && LA196_0<=NULL_TYPE)||(LA196_0>=JSON_OBJECT && LA196_0<=JSON_ARRAY)||LA196_0==STAR||(LA196_0>=LT && LA196_0<=GT)||(LA196_0>=BOR && LA196_0<=PLUS)||(LA196_0>=BAND && LA196_0<=BXOR)||(LA196_0>=LE && LA196_0<=GE)||(LA196_0>=MINUS && LA196_0<=MOD)||(LA196_0>=EVAL_IS_GROUP_EXPR && LA196_0<=EVAL_ISNOT_GROUP_EXPR)) ) {
                    alt196=1;
                }
                switch (alt196) {
                    case 1 :
                        // EsperEPL2Ast.g:541:18: valueExpr
                        {
                        pushFollow(FOLLOW_valueExpr_in_outputLimitAndTerm3358);
                        valueExpr();

                        state._fsp--;


                        }
                        break;

                }

                // EsperEPL2Ast.g:541:29: ( onSetExpr )?
                int alt197=2;
                int LA197_0 = input.LA(1);

                if ( (LA197_0==ON_SET_EXPR) ) {
                    alt197=1;
                }
                switch (alt197) {
                    case 1 :
                        // EsperEPL2Ast.g:541:29: onSetExpr
                        {
                        pushFollow(FOLLOW_onSetExpr_in_outputLimitAndTerm3361);
                        onSetExpr();

                        state._fsp--;


                        }
                        break;

                }


                match(input, Token.UP, null); 
            }

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "outputLimitAndTerm"


    // $ANTLR start "outputLimitAfter"
    // EsperEPL2Ast.g:544:1: outputLimitAfter : ^( AFTER ( timePeriod )? ( number )? ) ;
    public final void outputLimitAfter() throws RecognitionException {
        try {
            // EsperEPL2Ast.g:545:2: ( ^( AFTER ( timePeriod )? ( number )? ) )
            // EsperEPL2Ast.g:545:4: ^( AFTER ( timePeriod )? ( number )? )
            {
            match(input,AFTER,FOLLOW_AFTER_in_outputLimitAfter3375); 

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // EsperEPL2Ast.g:545:12: ( timePeriod )?
                int alt198=2;
                int LA198_0 = input.LA(1);

                if ( (LA198_0==TIME_PERIOD) ) {
                    alt198=1;
                }
                switch (alt198) {
                    case 1 :
                        // EsperEPL2Ast.g:545:12: timePeriod
                        {
                        pushFollow(FOLLOW_timePeriod_in_outputLimitAfter3377);
                        timePeriod();

                        state._fsp--;


                        }
                        break;

                }

                // EsperEPL2Ast.g:545:24: ( number )?
                int alt199=2;
                int LA199_0 = input.LA(1);

                if ( ((LA199_0>=INT_TYPE && LA199_0<=DOUBLE_TYPE)) ) {
                    alt199=1;
                }
                switch (alt199) {
                    case 1 :
                        // EsperEPL2Ast.g:545:24: number
                        {
                        pushFollow(FOLLOW_number_in_outputLimitAfter3380);
                        number();

                        state._fsp--;


                        }
                        break;

                }


                match(input, Token.UP, null); 
            }

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "outputLimitAfter"


    // $ANTLR start "rowLimitClause"
    // EsperEPL2Ast.g:548:1: rowLimitClause : ^(e= ROW_LIMIT_EXPR ( number | IDENT ) ( number | IDENT )? ( COMMA )? ( OFFSET )? ) ;
    public final void rowLimitClause() throws RecognitionException {
        CommonTree e=null;

        try {
            // EsperEPL2Ast.g:549:2: ( ^(e= ROW_LIMIT_EXPR ( number | IDENT ) ( number | IDENT )? ( COMMA )? ( OFFSET )? ) )
            // EsperEPL2Ast.g:549:4: ^(e= ROW_LIMIT_EXPR ( number | IDENT ) ( number | IDENT )? ( COMMA )? ( OFFSET )? )
            {
            e=(CommonTree)match(input,ROW_LIMIT_EXPR,FOLLOW_ROW_LIMIT_EXPR_in_rowLimitClause3396); 

            match(input, Token.DOWN, null); 
            // EsperEPL2Ast.g:549:23: ( number | IDENT )
            int alt200=2;
            int LA200_0 = input.LA(1);

            if ( ((LA200_0>=INT_TYPE && LA200_0<=DOUBLE_TYPE)) ) {
                alt200=1;
            }
            else if ( (LA200_0==IDENT) ) {
                alt200=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 200, 0, input);

                throw nvae;
            }
            switch (alt200) {
                case 1 :
                    // EsperEPL2Ast.g:549:24: number
                    {
                    pushFollow(FOLLOW_number_in_rowLimitClause3399);
                    number();

                    state._fsp--;


                    }
                    break;
                case 2 :
                    // EsperEPL2Ast.g:549:31: IDENT
                    {
                    match(input,IDENT,FOLLOW_IDENT_in_rowLimitClause3401); 

                    }
                    break;

            }

            // EsperEPL2Ast.g:549:38: ( number | IDENT )?
            int alt201=3;
            int LA201_0 = input.LA(1);

            if ( ((LA201_0>=INT_TYPE && LA201_0<=DOUBLE_TYPE)) ) {
                alt201=1;
            }
            else if ( (LA201_0==IDENT) ) {
                alt201=2;
            }
            switch (alt201) {
                case 1 :
                    // EsperEPL2Ast.g:549:39: number
                    {
                    pushFollow(FOLLOW_number_in_rowLimitClause3405);
                    number();

                    state._fsp--;


                    }
                    break;
                case 2 :
                    // EsperEPL2Ast.g:549:46: IDENT
                    {
                    match(input,IDENT,FOLLOW_IDENT_in_rowLimitClause3407); 

                    }
                    break;

            }

            // EsperEPL2Ast.g:549:54: ( COMMA )?
            int alt202=2;
            int LA202_0 = input.LA(1);

            if ( (LA202_0==COMMA) ) {
                alt202=1;
            }
            switch (alt202) {
                case 1 :
                    // EsperEPL2Ast.g:549:54: COMMA
                    {
                    match(input,COMMA,FOLLOW_COMMA_in_rowLimitClause3411); 

                    }
                    break;

            }

            // EsperEPL2Ast.g:549:61: ( OFFSET )?
            int alt203=2;
            int LA203_0 = input.LA(1);

            if ( (LA203_0==OFFSET) ) {
                alt203=1;
            }
            switch (alt203) {
                case 1 :
                    // EsperEPL2Ast.g:549:61: OFFSET
                    {
                    match(input,OFFSET,FOLLOW_OFFSET_in_rowLimitClause3414); 

                    }
                    break;

            }

             leaveNode(e); 

            match(input, Token.UP, null); 

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "rowLimitClause"


    // $ANTLR start "crontabLimitParameterSet"
    // EsperEPL2Ast.g:552:1: crontabLimitParameterSet : ^( CRONTAB_LIMIT_EXPR_PARAM valueExprWithTime valueExprWithTime valueExprWithTime valueExprWithTime valueExprWithTime ( valueExprWithTime )? ) ;
    public final void crontabLimitParameterSet() throws RecognitionException {
        try {
            // EsperEPL2Ast.g:553:2: ( ^( CRONTAB_LIMIT_EXPR_PARAM valueExprWithTime valueExprWithTime valueExprWithTime valueExprWithTime valueExprWithTime ( valueExprWithTime )? ) )
            // EsperEPL2Ast.g:553:4: ^( CRONTAB_LIMIT_EXPR_PARAM valueExprWithTime valueExprWithTime valueExprWithTime valueExprWithTime valueExprWithTime ( valueExprWithTime )? )
            {
            match(input,CRONTAB_LIMIT_EXPR_PARAM,FOLLOW_CRONTAB_LIMIT_EXPR_PARAM_in_crontabLimitParameterSet3432); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_valueExprWithTime_in_crontabLimitParameterSet3434);
            valueExprWithTime();

            state._fsp--;

            pushFollow(FOLLOW_valueExprWithTime_in_crontabLimitParameterSet3436);
            valueExprWithTime();

            state._fsp--;

            pushFollow(FOLLOW_valueExprWithTime_in_crontabLimitParameterSet3438);
            valueExprWithTime();

            state._fsp--;

            pushFollow(FOLLOW_valueExprWithTime_in_crontabLimitParameterSet3440);
            valueExprWithTime();

            state._fsp--;

            pushFollow(FOLLOW_valueExprWithTime_in_crontabLimitParameterSet3442);
            valueExprWithTime();

            state._fsp--;

            // EsperEPL2Ast.g:553:121: ( valueExprWithTime )?
            int alt204=2;
            int LA204_0 = input.LA(1);

            if ( ((LA204_0>=IN_SET && LA204_0<=REGEXP)||LA204_0==NOT_EXPR||(LA204_0>=SUM && LA204_0<=AVG)||(LA204_0>=COALESCE && LA204_0<=COUNT)||(LA204_0>=CASE && LA204_0<=CASE2)||LA204_0==LAST||LA204_0==ISTREAM||(LA204_0>=PREVIOUS && LA204_0<=EXISTS)||(LA204_0>=LW && LA204_0<=CURRENT_TIMESTAMP)||LA204_0==NEWKW||(LA204_0>=NUMERIC_PARAM_RANGE && LA204_0<=OBJECT_PARAM_ORDERED_EXPR)||(LA204_0>=EVAL_AND_EXPR && LA204_0<=EVAL_NOTEQUALS_GROUP_EXPR)||LA204_0==EVENT_PROP_EXPR||LA204_0==CONCAT||(LA204_0>=LIB_FUNC_CHAIN && LA204_0<=DOT_EXPR)||(LA204_0>=TIME_PERIOD && LA204_0<=ARRAY_EXPR)||(LA204_0>=NOT_IN_SET && LA204_0<=NOT_REGEXP)||(LA204_0>=IN_RANGE && LA204_0<=SUBSELECT_EXPR)||(LA204_0>=EXISTS_SUBSELECT_EXPR && LA204_0<=NOT_IN_SUBSELECT_EXPR)||(LA204_0>=LAST_OPERATOR && LA204_0<=SUBSTITUTION)||LA204_0==NUMBERSETSTAR||(LA204_0>=FIRST_AGGREG && LA204_0<=WINDOW_AGGREG)||(LA204_0>=INT_TYPE && LA204_0<=NULL_TYPE)||(LA204_0>=JSON_OBJECT && LA204_0<=JSON_ARRAY)||LA204_0==STAR||(LA204_0>=LT && LA204_0<=GT)||(LA204_0>=BOR && LA204_0<=PLUS)||(LA204_0>=BAND && LA204_0<=BXOR)||(LA204_0>=LE && LA204_0<=GE)||(LA204_0>=MINUS && LA204_0<=MOD)||(LA204_0>=EVAL_IS_GROUP_EXPR && LA204_0<=EVAL_ISNOT_GROUP_EXPR)) ) {
                alt204=1;
            }
            switch (alt204) {
                case 1 :
                    // EsperEPL2Ast.g:553:121: valueExprWithTime
                    {
                    pushFollow(FOLLOW_valueExprWithTime_in_crontabLimitParameterSet3444);
                    valueExprWithTime();

                    state._fsp--;


                    }
                    break;

            }


            match(input, Token.UP, null); 

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "crontabLimitParameterSet"


    // $ANTLR start "relationalExpr"
    // EsperEPL2Ast.g:556:1: relationalExpr : ( ^(n= LT relationalExprValue ) | ^(n= GT relationalExprValue ) | ^(n= LE relationalExprValue ) | ^(n= GE relationalExprValue ) );
    public final void relationalExpr() throws RecognitionException {
        CommonTree n=null;

        try {
            // EsperEPL2Ast.g:557:2: ( ^(n= LT relationalExprValue ) | ^(n= GT relationalExprValue ) | ^(n= LE relationalExprValue ) | ^(n= GE relationalExprValue ) )
            int alt205=4;
            switch ( input.LA(1) ) {
            case LT:
                {
                alt205=1;
                }
                break;
            case GT:
                {
                alt205=2;
                }
                break;
            case LE:
                {
                alt205=3;
                }
                break;
            case GE:
                {
                alt205=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 205, 0, input);

                throw nvae;
            }

            switch (alt205) {
                case 1 :
                    // EsperEPL2Ast.g:557:5: ^(n= LT relationalExprValue )
                    {
                    n=(CommonTree)match(input,LT,FOLLOW_LT_in_relationalExpr3461); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_relationalExprValue_in_relationalExpr3463);
                    relationalExprValue();

                    state._fsp--;

                     leaveNode(n); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    // EsperEPL2Ast.g:558:5: ^(n= GT relationalExprValue )
                    {
                    n=(CommonTree)match(input,GT,FOLLOW_GT_in_relationalExpr3476); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_relationalExprValue_in_relationalExpr3478);
                    relationalExprValue();

                    state._fsp--;

                     leaveNode(n); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 3 :
                    // EsperEPL2Ast.g:559:5: ^(n= LE relationalExprValue )
                    {
                    n=(CommonTree)match(input,LE,FOLLOW_LE_in_relationalExpr3491); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_relationalExprValue_in_relationalExpr3493);
                    relationalExprValue();

                    state._fsp--;

                     leaveNode(n); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 4 :
                    // EsperEPL2Ast.g:560:4: ^(n= GE relationalExprValue )
                    {
                    n=(CommonTree)match(input,GE,FOLLOW_GE_in_relationalExpr3505); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_relationalExprValue_in_relationalExpr3507);
                    relationalExprValue();

                    state._fsp--;

                     leaveNode(n); 

                    match(input, Token.UP, null); 

                    }
                    break;

            }
        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "relationalExpr"


    // $ANTLR start "relationalExprValue"
    // EsperEPL2Ast.g:563:1: relationalExprValue : ( valueExpr ( valueExpr | ( ANY | SOME | ALL ) ( ( valueExpr )* | subSelectGroupExpr ) ) ) ;
    public final void relationalExprValue() throws RecognitionException {
        try {
            // EsperEPL2Ast.g:564:2: ( ( valueExpr ( valueExpr | ( ANY | SOME | ALL ) ( ( valueExpr )* | subSelectGroupExpr ) ) ) )
            // EsperEPL2Ast.g:564:4: ( valueExpr ( valueExpr | ( ANY | SOME | ALL ) ( ( valueExpr )* | subSelectGroupExpr ) ) )
            {
            // EsperEPL2Ast.g:564:4: ( valueExpr ( valueExpr | ( ANY | SOME | ALL ) ( ( valueExpr )* | subSelectGroupExpr ) ) )
            // EsperEPL2Ast.g:565:5: valueExpr ( valueExpr | ( ANY | SOME | ALL ) ( ( valueExpr )* | subSelectGroupExpr ) )
            {
            pushFollow(FOLLOW_valueExpr_in_relationalExprValue3529);
            valueExpr();

            state._fsp--;

            // EsperEPL2Ast.g:566:6: ( valueExpr | ( ANY | SOME | ALL ) ( ( valueExpr )* | subSelectGroupExpr ) )
            int alt208=2;
            int LA208_0 = input.LA(1);

            if ( ((LA208_0>=IN_SET && LA208_0<=REGEXP)||LA208_0==NOT_EXPR||(LA208_0>=SUM && LA208_0<=AVG)||(LA208_0>=COALESCE && LA208_0<=COUNT)||(LA208_0>=CASE && LA208_0<=CASE2)||LA208_0==ISTREAM||(LA208_0>=PREVIOUS && LA208_0<=EXISTS)||(LA208_0>=INSTANCEOF && LA208_0<=CURRENT_TIMESTAMP)||LA208_0==NEWKW||(LA208_0>=EVAL_AND_EXPR && LA208_0<=EVAL_NOTEQUALS_GROUP_EXPR)||LA208_0==EVENT_PROP_EXPR||LA208_0==CONCAT||(LA208_0>=LIB_FUNC_CHAIN && LA208_0<=DOT_EXPR)||LA208_0==ARRAY_EXPR||(LA208_0>=NOT_IN_SET && LA208_0<=NOT_REGEXP)||(LA208_0>=IN_RANGE && LA208_0<=SUBSELECT_EXPR)||(LA208_0>=EXISTS_SUBSELECT_EXPR && LA208_0<=NOT_IN_SUBSELECT_EXPR)||LA208_0==SUBSTITUTION||(LA208_0>=FIRST_AGGREG && LA208_0<=WINDOW_AGGREG)||(LA208_0>=INT_TYPE && LA208_0<=NULL_TYPE)||(LA208_0>=JSON_OBJECT && LA208_0<=JSON_ARRAY)||LA208_0==STAR||(LA208_0>=LT && LA208_0<=GT)||(LA208_0>=BOR && LA208_0<=PLUS)||(LA208_0>=BAND && LA208_0<=BXOR)||(LA208_0>=LE && LA208_0<=GE)||(LA208_0>=MINUS && LA208_0<=MOD)||(LA208_0>=EVAL_IS_GROUP_EXPR && LA208_0<=EVAL_ISNOT_GROUP_EXPR)) ) {
                alt208=1;
            }
            else if ( ((LA208_0>=ALL && LA208_0<=SOME)) ) {
                alt208=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 208, 0, input);

                throw nvae;
            }
            switch (alt208) {
                case 1 :
                    // EsperEPL2Ast.g:566:8: valueExpr
                    {
                    pushFollow(FOLLOW_valueExpr_in_relationalExprValue3539);
                    valueExpr();

                    state._fsp--;


                    }
                    break;
                case 2 :
                    // EsperEPL2Ast.g:568:6: ( ANY | SOME | ALL ) ( ( valueExpr )* | subSelectGroupExpr )
                    {
                    if ( (input.LA(1)>=ALL && input.LA(1)<=SOME) ) {
                        input.consume();
                        state.errorRecovery=false;
                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        throw mse;
                    }

                    // EsperEPL2Ast.g:568:21: ( ( valueExpr )* | subSelectGroupExpr )
                    int alt207=2;
                    int LA207_0 = input.LA(1);

                    if ( (LA207_0==UP||(LA207_0>=IN_SET && LA207_0<=REGEXP)||LA207_0==NOT_EXPR||(LA207_0>=SUM && LA207_0<=AVG)||(LA207_0>=COALESCE && LA207_0<=COUNT)||(LA207_0>=CASE && LA207_0<=CASE2)||LA207_0==ISTREAM||(LA207_0>=PREVIOUS && LA207_0<=EXISTS)||(LA207_0>=INSTANCEOF && LA207_0<=CURRENT_TIMESTAMP)||LA207_0==NEWKW||(LA207_0>=EVAL_AND_EXPR && LA207_0<=EVAL_NOTEQUALS_GROUP_EXPR)||LA207_0==EVENT_PROP_EXPR||LA207_0==CONCAT||(LA207_0>=LIB_FUNC_CHAIN && LA207_0<=DOT_EXPR)||LA207_0==ARRAY_EXPR||(LA207_0>=NOT_IN_SET && LA207_0<=NOT_REGEXP)||(LA207_0>=IN_RANGE && LA207_0<=SUBSELECT_EXPR)||(LA207_0>=EXISTS_SUBSELECT_EXPR && LA207_0<=NOT_IN_SUBSELECT_EXPR)||LA207_0==SUBSTITUTION||(LA207_0>=FIRST_AGGREG && LA207_0<=WINDOW_AGGREG)||(LA207_0>=INT_TYPE && LA207_0<=NULL_TYPE)||(LA207_0>=JSON_OBJECT && LA207_0<=JSON_ARRAY)||LA207_0==STAR||(LA207_0>=LT && LA207_0<=GT)||(LA207_0>=BOR && LA207_0<=PLUS)||(LA207_0>=BAND && LA207_0<=BXOR)||(LA207_0>=LE && LA207_0<=GE)||(LA207_0>=MINUS && LA207_0<=MOD)||(LA207_0>=EVAL_IS_GROUP_EXPR && LA207_0<=EVAL_ISNOT_GROUP_EXPR)) ) {
                        alt207=1;
                    }
                    else if ( (LA207_0==SUBSELECT_GROUP_EXPR) ) {
                        alt207=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 207, 0, input);

                        throw nvae;
                    }
                    switch (alt207) {
                        case 1 :
                            // EsperEPL2Ast.g:568:22: ( valueExpr )*
                            {
                            // EsperEPL2Ast.g:568:22: ( valueExpr )*
                            loop206:
                            do {
                                int alt206=2;
                                int LA206_0 = input.LA(1);

                                if ( ((LA206_0>=IN_SET && LA206_0<=REGEXP)||LA206_0==NOT_EXPR||(LA206_0>=SUM && LA206_0<=AVG)||(LA206_0>=COALESCE && LA206_0<=COUNT)||(LA206_0>=CASE && LA206_0<=CASE2)||LA206_0==ISTREAM||(LA206_0>=PREVIOUS && LA206_0<=EXISTS)||(LA206_0>=INSTANCEOF && LA206_0<=CURRENT_TIMESTAMP)||LA206_0==NEWKW||(LA206_0>=EVAL_AND_EXPR && LA206_0<=EVAL_NOTEQUALS_GROUP_EXPR)||LA206_0==EVENT_PROP_EXPR||LA206_0==CONCAT||(LA206_0>=LIB_FUNC_CHAIN && LA206_0<=DOT_EXPR)||LA206_0==ARRAY_EXPR||(LA206_0>=NOT_IN_SET && LA206_0<=NOT_REGEXP)||(LA206_0>=IN_RANGE && LA206_0<=SUBSELECT_EXPR)||(LA206_0>=EXISTS_SUBSELECT_EXPR && LA206_0<=NOT_IN_SUBSELECT_EXPR)||LA206_0==SUBSTITUTION||(LA206_0>=FIRST_AGGREG && LA206_0<=WINDOW_AGGREG)||(LA206_0>=INT_TYPE && LA206_0<=NULL_TYPE)||(LA206_0>=JSON_OBJECT && LA206_0<=JSON_ARRAY)||LA206_0==STAR||(LA206_0>=LT && LA206_0<=GT)||(LA206_0>=BOR && LA206_0<=PLUS)||(LA206_0>=BAND && LA206_0<=BXOR)||(LA206_0>=LE && LA206_0<=GE)||(LA206_0>=MINUS && LA206_0<=MOD)||(LA206_0>=EVAL_IS_GROUP_EXPR && LA206_0<=EVAL_ISNOT_GROUP_EXPR)) ) {
                                    alt206=1;
                                }


                                switch (alt206) {
                            	case 1 :
                            	    // EsperEPL2Ast.g:568:22: valueExpr
                            	    {
                            	    pushFollow(FOLLOW_valueExpr_in_relationalExprValue3563);
                            	    valueExpr();

                            	    state._fsp--;


                            	    }
                            	    break;

                            	default :
                            	    break loop206;
                                }
                            } while (true);


                            }
                            break;
                        case 2 :
                            // EsperEPL2Ast.g:568:35: subSelectGroupExpr
                            {
                            pushFollow(FOLLOW_subSelectGroupExpr_in_relationalExprValue3568);
                            subSelectGroupExpr();

                            state._fsp--;


                            }
                            break;

                    }


                    }
                    break;

            }


            }


            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "relationalExprValue"


    // $ANTLR start "evalExprChoice"
    // EsperEPL2Ast.g:573:1: evalExprChoice : ( ^(jo= EVAL_OR_EXPR valueExpr valueExpr ( valueExpr )* ) | ^(ja= EVAL_AND_EXPR valueExpr valueExpr ( valueExpr )* ) | ^(je= EVAL_EQUALS_EXPR valueExpr valueExpr ) | ^(ji= EVAL_IS_EXPR valueExpr valueExpr ) | ^(jne= EVAL_NOTEQUALS_EXPR valueExpr valueExpr ) | ^(jis= EVAL_ISNOT_EXPR valueExpr valueExpr ) | ^(jge= EVAL_EQUALS_GROUP_EXPR equalsSubquery ) | ^(jgi= EVAL_IS_GROUP_EXPR equalsSubquery ) | ^(jgne= EVAL_NOTEQUALS_GROUP_EXPR equalsSubquery ) | ^(jgni= EVAL_ISNOT_GROUP_EXPR equalsSubquery ) | ^(n= NOT_EXPR valueExpr ) | r= relationalExpr );
    public final void evalExprChoice() throws RecognitionException {
        CommonTree jo=null;
        CommonTree ja=null;
        CommonTree je=null;
        CommonTree ji=null;
        CommonTree jne=null;
        CommonTree jis=null;
        CommonTree jge=null;
        CommonTree jgi=null;
        CommonTree jgne=null;
        CommonTree jgni=null;
        CommonTree n=null;

        try {
            // EsperEPL2Ast.g:574:2: ( ^(jo= EVAL_OR_EXPR valueExpr valueExpr ( valueExpr )* ) | ^(ja= EVAL_AND_EXPR valueExpr valueExpr ( valueExpr )* ) | ^(je= EVAL_EQUALS_EXPR valueExpr valueExpr ) | ^(ji= EVAL_IS_EXPR valueExpr valueExpr ) | ^(jne= EVAL_NOTEQUALS_EXPR valueExpr valueExpr ) | ^(jis= EVAL_ISNOT_EXPR valueExpr valueExpr ) | ^(jge= EVAL_EQUALS_GROUP_EXPR equalsSubquery ) | ^(jgi= EVAL_IS_GROUP_EXPR equalsSubquery ) | ^(jgne= EVAL_NOTEQUALS_GROUP_EXPR equalsSubquery ) | ^(jgni= EVAL_ISNOT_GROUP_EXPR equalsSubquery ) | ^(n= NOT_EXPR valueExpr ) | r= relationalExpr )
            int alt211=12;
            switch ( input.LA(1) ) {
            case EVAL_OR_EXPR:
                {
                alt211=1;
                }
                break;
            case EVAL_AND_EXPR:
                {
                alt211=2;
                }
                break;
            case EVAL_EQUALS_EXPR:
                {
                alt211=3;
                }
                break;
            case EVAL_IS_EXPR:
                {
                alt211=4;
                }
                break;
            case EVAL_NOTEQUALS_EXPR:
                {
                alt211=5;
                }
                break;
            case EVAL_ISNOT_EXPR:
                {
                alt211=6;
                }
                break;
            case EVAL_EQUALS_GROUP_EXPR:
                {
                alt211=7;
                }
                break;
            case EVAL_IS_GROUP_EXPR:
                {
                alt211=8;
                }
                break;
            case EVAL_NOTEQUALS_GROUP_EXPR:
                {
                alt211=9;
                }
                break;
            case EVAL_ISNOT_GROUP_EXPR:
                {
                alt211=10;
                }
                break;
            case NOT_EXPR:
                {
                alt211=11;
                }
                break;
            case LT:
            case GT:
            case LE:
            case GE:
                {
                alt211=12;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 211, 0, input);

                throw nvae;
            }

            switch (alt211) {
                case 1 :
                    // EsperEPL2Ast.g:574:4: ^(jo= EVAL_OR_EXPR valueExpr valueExpr ( valueExpr )* )
                    {
                    jo=(CommonTree)match(input,EVAL_OR_EXPR,FOLLOW_EVAL_OR_EXPR_in_evalExprChoice3594); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_valueExpr_in_evalExprChoice3596);
                    valueExpr();

                    state._fsp--;

                    pushFollow(FOLLOW_valueExpr_in_evalExprChoice3598);
                    valueExpr();

                    state._fsp--;

                    // EsperEPL2Ast.g:574:42: ( valueExpr )*
                    loop209:
                    do {
                        int alt209=2;
                        int LA209_0 = input.LA(1);

                        if ( ((LA209_0>=IN_SET && LA209_0<=REGEXP)||LA209_0==NOT_EXPR||(LA209_0>=SUM && LA209_0<=AVG)||(LA209_0>=COALESCE && LA209_0<=COUNT)||(LA209_0>=CASE && LA209_0<=CASE2)||LA209_0==ISTREAM||(LA209_0>=PREVIOUS && LA209_0<=EXISTS)||(LA209_0>=INSTANCEOF && LA209_0<=CURRENT_TIMESTAMP)||LA209_0==NEWKW||(LA209_0>=EVAL_AND_EXPR && LA209_0<=EVAL_NOTEQUALS_GROUP_EXPR)||LA209_0==EVENT_PROP_EXPR||LA209_0==CONCAT||(LA209_0>=LIB_FUNC_CHAIN && LA209_0<=DOT_EXPR)||LA209_0==ARRAY_EXPR||(LA209_0>=NOT_IN_SET && LA209_0<=NOT_REGEXP)||(LA209_0>=IN_RANGE && LA209_0<=SUBSELECT_EXPR)||(LA209_0>=EXISTS_SUBSELECT_EXPR && LA209_0<=NOT_IN_SUBSELECT_EXPR)||LA209_0==SUBSTITUTION||(LA209_0>=FIRST_AGGREG && LA209_0<=WINDOW_AGGREG)||(LA209_0>=INT_TYPE && LA209_0<=NULL_TYPE)||(LA209_0>=JSON_OBJECT && LA209_0<=JSON_ARRAY)||LA209_0==STAR||(LA209_0>=LT && LA209_0<=GT)||(LA209_0>=BOR && LA209_0<=PLUS)||(LA209_0>=BAND && LA209_0<=BXOR)||(LA209_0>=LE && LA209_0<=GE)||(LA209_0>=MINUS && LA209_0<=MOD)||(LA209_0>=EVAL_IS_GROUP_EXPR && LA209_0<=EVAL_ISNOT_GROUP_EXPR)) ) {
                            alt209=1;
                        }


                        switch (alt209) {
                    	case 1 :
                    	    // EsperEPL2Ast.g:574:43: valueExpr
                    	    {
                    	    pushFollow(FOLLOW_valueExpr_in_evalExprChoice3601);
                    	    valueExpr();

                    	    state._fsp--;


                    	    }
                    	    break;

                    	default :
                    	    break loop209;
                        }
                    } while (true);

                     leaveNode(jo); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    // EsperEPL2Ast.g:575:4: ^(ja= EVAL_AND_EXPR valueExpr valueExpr ( valueExpr )* )
                    {
                    ja=(CommonTree)match(input,EVAL_AND_EXPR,FOLLOW_EVAL_AND_EXPR_in_evalExprChoice3615); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_valueExpr_in_evalExprChoice3617);
                    valueExpr();

                    state._fsp--;

                    pushFollow(FOLLOW_valueExpr_in_evalExprChoice3619);
                    valueExpr();

                    state._fsp--;

                    // EsperEPL2Ast.g:575:43: ( valueExpr )*
                    loop210:
                    do {
                        int alt210=2;
                        int LA210_0 = input.LA(1);

                        if ( ((LA210_0>=IN_SET && LA210_0<=REGEXP)||LA210_0==NOT_EXPR||(LA210_0>=SUM && LA210_0<=AVG)||(LA210_0>=COALESCE && LA210_0<=COUNT)||(LA210_0>=CASE && LA210_0<=CASE2)||LA210_0==ISTREAM||(LA210_0>=PREVIOUS && LA210_0<=EXISTS)||(LA210_0>=INSTANCEOF && LA210_0<=CURRENT_TIMESTAMP)||LA210_0==NEWKW||(LA210_0>=EVAL_AND_EXPR && LA210_0<=EVAL_NOTEQUALS_GROUP_EXPR)||LA210_0==EVENT_PROP_EXPR||LA210_0==CONCAT||(LA210_0>=LIB_FUNC_CHAIN && LA210_0<=DOT_EXPR)||LA210_0==ARRAY_EXPR||(LA210_0>=NOT_IN_SET && LA210_0<=NOT_REGEXP)||(LA210_0>=IN_RANGE && LA210_0<=SUBSELECT_EXPR)||(LA210_0>=EXISTS_SUBSELECT_EXPR && LA210_0<=NOT_IN_SUBSELECT_EXPR)||LA210_0==SUBSTITUTION||(LA210_0>=FIRST_AGGREG && LA210_0<=WINDOW_AGGREG)||(LA210_0>=INT_TYPE && LA210_0<=NULL_TYPE)||(LA210_0>=JSON_OBJECT && LA210_0<=JSON_ARRAY)||LA210_0==STAR||(LA210_0>=LT && LA210_0<=GT)||(LA210_0>=BOR && LA210_0<=PLUS)||(LA210_0>=BAND && LA210_0<=BXOR)||(LA210_0>=LE && LA210_0<=GE)||(LA210_0>=MINUS && LA210_0<=MOD)||(LA210_0>=EVAL_IS_GROUP_EXPR && LA210_0<=EVAL_ISNOT_GROUP_EXPR)) ) {
                            alt210=1;
                        }


                        switch (alt210) {
                    	case 1 :
                    	    // EsperEPL2Ast.g:575:44: valueExpr
                    	    {
                    	    pushFollow(FOLLOW_valueExpr_in_evalExprChoice3622);
                    	    valueExpr();

                    	    state._fsp--;


                    	    }
                    	    break;

                    	default :
                    	    break loop210;
                        }
                    } while (true);

                     leaveNode(ja); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 3 :
                    // EsperEPL2Ast.g:576:4: ^(je= EVAL_EQUALS_EXPR valueExpr valueExpr )
                    {
                    je=(CommonTree)match(input,EVAL_EQUALS_EXPR,FOLLOW_EVAL_EQUALS_EXPR_in_evalExprChoice3636); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_valueExpr_in_evalExprChoice3638);
                    valueExpr();

                    state._fsp--;

                    pushFollow(FOLLOW_valueExpr_in_evalExprChoice3640);
                    valueExpr();

                    state._fsp--;

                     leaveNode(je); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 4 :
                    // EsperEPL2Ast.g:577:4: ^(ji= EVAL_IS_EXPR valueExpr valueExpr )
                    {
                    ji=(CommonTree)match(input,EVAL_IS_EXPR,FOLLOW_EVAL_IS_EXPR_in_evalExprChoice3652); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_valueExpr_in_evalExprChoice3654);
                    valueExpr();

                    state._fsp--;

                    pushFollow(FOLLOW_valueExpr_in_evalExprChoice3656);
                    valueExpr();

                    state._fsp--;

                     leaveNode(ji); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 5 :
                    // EsperEPL2Ast.g:578:4: ^(jne= EVAL_NOTEQUALS_EXPR valueExpr valueExpr )
                    {
                    jne=(CommonTree)match(input,EVAL_NOTEQUALS_EXPR,FOLLOW_EVAL_NOTEQUALS_EXPR_in_evalExprChoice3668); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_valueExpr_in_evalExprChoice3670);
                    valueExpr();

                    state._fsp--;

                    pushFollow(FOLLOW_valueExpr_in_evalExprChoice3672);
                    valueExpr();

                    state._fsp--;

                     leaveNode(jne); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 6 :
                    // EsperEPL2Ast.g:579:4: ^(jis= EVAL_ISNOT_EXPR valueExpr valueExpr )
                    {
                    jis=(CommonTree)match(input,EVAL_ISNOT_EXPR,FOLLOW_EVAL_ISNOT_EXPR_in_evalExprChoice3684); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_valueExpr_in_evalExprChoice3686);
                    valueExpr();

                    state._fsp--;

                    pushFollow(FOLLOW_valueExpr_in_evalExprChoice3688);
                    valueExpr();

                    state._fsp--;

                     leaveNode(jis); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 7 :
                    // EsperEPL2Ast.g:580:4: ^(jge= EVAL_EQUALS_GROUP_EXPR equalsSubquery )
                    {
                    jge=(CommonTree)match(input,EVAL_EQUALS_GROUP_EXPR,FOLLOW_EVAL_EQUALS_GROUP_EXPR_in_evalExprChoice3700); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_equalsSubquery_in_evalExprChoice3702);
                    equalsSubquery();

                    state._fsp--;

                     leaveNode(jge); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 8 :
                    // EsperEPL2Ast.g:581:4: ^(jgi= EVAL_IS_GROUP_EXPR equalsSubquery )
                    {
                    jgi=(CommonTree)match(input,EVAL_IS_GROUP_EXPR,FOLLOW_EVAL_IS_GROUP_EXPR_in_evalExprChoice3714); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_equalsSubquery_in_evalExprChoice3716);
                    equalsSubquery();

                    state._fsp--;

                     leaveNode(jgi); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 9 :
                    // EsperEPL2Ast.g:582:4: ^(jgne= EVAL_NOTEQUALS_GROUP_EXPR equalsSubquery )
                    {
                    jgne=(CommonTree)match(input,EVAL_NOTEQUALS_GROUP_EXPR,FOLLOW_EVAL_NOTEQUALS_GROUP_EXPR_in_evalExprChoice3728); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_equalsSubquery_in_evalExprChoice3730);
                    equalsSubquery();

                    state._fsp--;

                     leaveNode(jgne); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 10 :
                    // EsperEPL2Ast.g:583:4: ^(jgni= EVAL_ISNOT_GROUP_EXPR equalsSubquery )
                    {
                    jgni=(CommonTree)match(input,EVAL_ISNOT_GROUP_EXPR,FOLLOW_EVAL_ISNOT_GROUP_EXPR_in_evalExprChoice3742); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_equalsSubquery_in_evalExprChoice3744);
                    equalsSubquery();

                    state._fsp--;

                     leaveNode(jgni); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 11 :
                    // EsperEPL2Ast.g:584:4: ^(n= NOT_EXPR valueExpr )
                    {
                    n=(CommonTree)match(input,NOT_EXPR,FOLLOW_NOT_EXPR_in_evalExprChoice3756); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_valueExpr_in_evalExprChoice3758);
                    valueExpr();

                    state._fsp--;

                     leaveNode(n); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 12 :
                    // EsperEPL2Ast.g:585:4: r= relationalExpr
                    {
                    pushFollow(FOLLOW_relationalExpr_in_evalExprChoice3769);
                    relationalExpr();

                    state._fsp--;


                    }
                    break;

            }
        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "evalExprChoice"


    // $ANTLR start "equalsSubquery"
    // EsperEPL2Ast.g:588:1: equalsSubquery : valueExpr ( ANY | SOME | ALL ) ( ( valueExpr )* | subSelectGroupExpr ) ;
    public final void equalsSubquery() throws RecognitionException {
        try {
            // EsperEPL2Ast.g:589:2: ( valueExpr ( ANY | SOME | ALL ) ( ( valueExpr )* | subSelectGroupExpr ) )
            // EsperEPL2Ast.g:589:4: valueExpr ( ANY | SOME | ALL ) ( ( valueExpr )* | subSelectGroupExpr )
            {
            pushFollow(FOLLOW_valueExpr_in_equalsSubquery3780);
            valueExpr();

            state._fsp--;

            if ( (input.LA(1)>=ALL && input.LA(1)<=SOME) ) {
                input.consume();
                state.errorRecovery=false;
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }

            // EsperEPL2Ast.g:589:29: ( ( valueExpr )* | subSelectGroupExpr )
            int alt213=2;
            int LA213_0 = input.LA(1);

            if ( (LA213_0==UP||(LA213_0>=IN_SET && LA213_0<=REGEXP)||LA213_0==NOT_EXPR||(LA213_0>=SUM && LA213_0<=AVG)||(LA213_0>=COALESCE && LA213_0<=COUNT)||(LA213_0>=CASE && LA213_0<=CASE2)||LA213_0==ISTREAM||(LA213_0>=PREVIOUS && LA213_0<=EXISTS)||(LA213_0>=INSTANCEOF && LA213_0<=CURRENT_TIMESTAMP)||LA213_0==NEWKW||(LA213_0>=EVAL_AND_EXPR && LA213_0<=EVAL_NOTEQUALS_GROUP_EXPR)||LA213_0==EVENT_PROP_EXPR||LA213_0==CONCAT||(LA213_0>=LIB_FUNC_CHAIN && LA213_0<=DOT_EXPR)||LA213_0==ARRAY_EXPR||(LA213_0>=NOT_IN_SET && LA213_0<=NOT_REGEXP)||(LA213_0>=IN_RANGE && LA213_0<=SUBSELECT_EXPR)||(LA213_0>=EXISTS_SUBSELECT_EXPR && LA213_0<=NOT_IN_SUBSELECT_EXPR)||LA213_0==SUBSTITUTION||(LA213_0>=FIRST_AGGREG && LA213_0<=WINDOW_AGGREG)||(LA213_0>=INT_TYPE && LA213_0<=NULL_TYPE)||(LA213_0>=JSON_OBJECT && LA213_0<=JSON_ARRAY)||LA213_0==STAR||(LA213_0>=LT && LA213_0<=GT)||(LA213_0>=BOR && LA213_0<=PLUS)||(LA213_0>=BAND && LA213_0<=BXOR)||(LA213_0>=LE && LA213_0<=GE)||(LA213_0>=MINUS && LA213_0<=MOD)||(LA213_0>=EVAL_IS_GROUP_EXPR && LA213_0<=EVAL_ISNOT_GROUP_EXPR)) ) {
                alt213=1;
            }
            else if ( (LA213_0==SUBSELECT_GROUP_EXPR) ) {
                alt213=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 213, 0, input);

                throw nvae;
            }
            switch (alt213) {
                case 1 :
                    // EsperEPL2Ast.g:589:30: ( valueExpr )*
                    {
                    // EsperEPL2Ast.g:589:30: ( valueExpr )*
                    loop212:
                    do {
                        int alt212=2;
                        int LA212_0 = input.LA(1);

                        if ( ((LA212_0>=IN_SET && LA212_0<=REGEXP)||LA212_0==NOT_EXPR||(LA212_0>=SUM && LA212_0<=AVG)||(LA212_0>=COALESCE && LA212_0<=COUNT)||(LA212_0>=CASE && LA212_0<=CASE2)||LA212_0==ISTREAM||(LA212_0>=PREVIOUS && LA212_0<=EXISTS)||(LA212_0>=INSTANCEOF && LA212_0<=CURRENT_TIMESTAMP)||LA212_0==NEWKW||(LA212_0>=EVAL_AND_EXPR && LA212_0<=EVAL_NOTEQUALS_GROUP_EXPR)||LA212_0==EVENT_PROP_EXPR||LA212_0==CONCAT||(LA212_0>=LIB_FUNC_CHAIN && LA212_0<=DOT_EXPR)||LA212_0==ARRAY_EXPR||(LA212_0>=NOT_IN_SET && LA212_0<=NOT_REGEXP)||(LA212_0>=IN_RANGE && LA212_0<=SUBSELECT_EXPR)||(LA212_0>=EXISTS_SUBSELECT_EXPR && LA212_0<=NOT_IN_SUBSELECT_EXPR)||LA212_0==SUBSTITUTION||(LA212_0>=FIRST_AGGREG && LA212_0<=WINDOW_AGGREG)||(LA212_0>=INT_TYPE && LA212_0<=NULL_TYPE)||(LA212_0>=JSON_OBJECT && LA212_0<=JSON_ARRAY)||LA212_0==STAR||(LA212_0>=LT && LA212_0<=GT)||(LA212_0>=BOR && LA212_0<=PLUS)||(LA212_0>=BAND && LA212_0<=BXOR)||(LA212_0>=LE && LA212_0<=GE)||(LA212_0>=MINUS && LA212_0<=MOD)||(LA212_0>=EVAL_IS_GROUP_EXPR && LA212_0<=EVAL_ISNOT_GROUP_EXPR)) ) {
                            alt212=1;
                        }


                        switch (alt212) {
                    	case 1 :
                    	    // EsperEPL2Ast.g:589:30: valueExpr
                    	    {
                    	    pushFollow(FOLLOW_valueExpr_in_equalsSubquery3791);
                    	    valueExpr();

                    	    state._fsp--;


                    	    }
                    	    break;

                    	default :
                    	    break loop212;
                        }
                    } while (true);


                    }
                    break;
                case 2 :
                    // EsperEPL2Ast.g:589:43: subSelectGroupExpr
                    {
                    pushFollow(FOLLOW_subSelectGroupExpr_in_equalsSubquery3796);
                    subSelectGroupExpr();

                    state._fsp--;


                    }
                    break;

            }


            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "equalsSubquery"


    // $ANTLR start "valueExpr"
    // EsperEPL2Ast.g:592:1: valueExpr : ( constant[true] | substitution | arithmeticExpr | eventPropertyExpr[true] | evalExprChoice | builtinFunc | libFuncChain | caseExpr | inExpr | betweenExpr | likeExpr | regExpExpr | arrayExpr | subSelectInExpr | subSelectRowExpr | subSelectExistsExpr | dotExpr | newExpr | jsonarray[true] | jsonobject[true] );
    public final void valueExpr() throws RecognitionException {
        try {
            // EsperEPL2Ast.g:593:2: ( constant[true] | substitution | arithmeticExpr | eventPropertyExpr[true] | evalExprChoice | builtinFunc | libFuncChain | caseExpr | inExpr | betweenExpr | likeExpr | regExpExpr | arrayExpr | subSelectInExpr | subSelectRowExpr | subSelectExistsExpr | dotExpr | newExpr | jsonarray[true] | jsonobject[true] )
            int alt214=20;
            switch ( input.LA(1) ) {
            case INT_TYPE:
            case LONG_TYPE:
            case FLOAT_TYPE:
            case DOUBLE_TYPE:
            case STRING_TYPE:
            case BOOL_TYPE:
            case NULL_TYPE:
                {
                alt214=1;
                }
                break;
            case SUBSTITUTION:
                {
                alt214=2;
                }
                break;
            case CONCAT:
            case STAR:
            case BOR:
            case PLUS:
            case BAND:
            case BXOR:
            case MINUS:
            case DIV:
            case MOD:
                {
                alt214=3;
                }
                break;
            case EVENT_PROP_EXPR:
                {
                alt214=4;
                }
                break;
            case NOT_EXPR:
            case EVAL_AND_EXPR:
            case EVAL_OR_EXPR:
            case EVAL_EQUALS_EXPR:
            case EVAL_NOTEQUALS_EXPR:
            case EVAL_IS_EXPR:
            case EVAL_ISNOT_EXPR:
            case EVAL_EQUALS_GROUP_EXPR:
            case EVAL_NOTEQUALS_GROUP_EXPR:
            case LT:
            case GT:
            case LE:
            case GE:
            case EVAL_IS_GROUP_EXPR:
            case EVAL_ISNOT_GROUP_EXPR:
                {
                alt214=5;
                }
                break;
            case SUM:
            case AVG:
            case COALESCE:
            case MEDIAN:
            case STDDEV:
            case AVEDEV:
            case COUNT:
            case ISTREAM:
            case PREVIOUS:
            case PREVIOUSTAIL:
            case PREVIOUSCOUNT:
            case PREVIOUSWINDOW:
            case PRIOR:
            case EXISTS:
            case INSTANCEOF:
            case TYPEOF:
            case CAST:
            case CURRENT_TIMESTAMP:
            case FIRST_AGGREG:
            case LAST_AGGREG:
            case WINDOW_AGGREG:
                {
                alt214=6;
                }
                break;
            case LIB_FUNC_CHAIN:
                {
                alt214=7;
                }
                break;
            case CASE:
            case CASE2:
                {
                alt214=8;
                }
                break;
            case IN_SET:
            case NOT_IN_SET:
            case IN_RANGE:
            case NOT_IN_RANGE:
                {
                alt214=9;
                }
                break;
            case BETWEEN:
            case NOT_BETWEEN:
                {
                alt214=10;
                }
                break;
            case LIKE:
            case NOT_LIKE:
                {
                alt214=11;
                }
                break;
            case REGEXP:
            case NOT_REGEXP:
                {
                alt214=12;
                }
                break;
            case ARRAY_EXPR:
                {
                alt214=13;
                }
                break;
            case IN_SUBSELECT_EXPR:
            case NOT_IN_SUBSELECT_EXPR:
                {
                alt214=14;
                }
                break;
            case SUBSELECT_EXPR:
                {
                alt214=15;
                }
                break;
            case EXISTS_SUBSELECT_EXPR:
                {
                alt214=16;
                }
                break;
            case DOT_EXPR:
                {
                alt214=17;
                }
                break;
            case NEWKW:
                {
                alt214=18;
                }
                break;
            case JSON_ARRAY:
                {
                alt214=19;
                }
                break;
            case JSON_OBJECT:
                {
                alt214=20;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 214, 0, input);

                throw nvae;
            }

            switch (alt214) {
                case 1 :
                    // EsperEPL2Ast.g:593:5: constant[true]
                    {
                    pushFollow(FOLLOW_constant_in_valueExpr3810);
                    constant(true);

                    state._fsp--;


                    }
                    break;
                case 2 :
                    // EsperEPL2Ast.g:594:4: substitution
                    {
                    pushFollow(FOLLOW_substitution_in_valueExpr3816);
                    substitution();

                    state._fsp--;


                    }
                    break;
                case 3 :
                    // EsperEPL2Ast.g:595:5: arithmeticExpr
                    {
                    pushFollow(FOLLOW_arithmeticExpr_in_valueExpr3822);
                    arithmeticExpr();

                    state._fsp--;


                    }
                    break;
                case 4 :
                    // EsperEPL2Ast.g:596:5: eventPropertyExpr[true]
                    {
                    pushFollow(FOLLOW_eventPropertyExpr_in_valueExpr3829);
                    eventPropertyExpr(true);

                    state._fsp--;


                    }
                    break;
                case 5 :
                    // EsperEPL2Ast.g:597:7: evalExprChoice
                    {
                    pushFollow(FOLLOW_evalExprChoice_in_valueExpr3838);
                    evalExprChoice();

                    state._fsp--;


                    }
                    break;
                case 6 :
                    // EsperEPL2Ast.g:598:4: builtinFunc
                    {
                    pushFollow(FOLLOW_builtinFunc_in_valueExpr3843);
                    builtinFunc();

                    state._fsp--;


                    }
                    break;
                case 7 :
                    // EsperEPL2Ast.g:599:7: libFuncChain
                    {
                    pushFollow(FOLLOW_libFuncChain_in_valueExpr3851);
                    libFuncChain();

                    state._fsp--;


                    }
                    break;
                case 8 :
                    // EsperEPL2Ast.g:600:4: caseExpr
                    {
                    pushFollow(FOLLOW_caseExpr_in_valueExpr3856);
                    caseExpr();

                    state._fsp--;


                    }
                    break;
                case 9 :
                    // EsperEPL2Ast.g:601:4: inExpr
                    {
                    pushFollow(FOLLOW_inExpr_in_valueExpr3861);
                    inExpr();

                    state._fsp--;


                    }
                    break;
                case 10 :
                    // EsperEPL2Ast.g:602:4: betweenExpr
                    {
                    pushFollow(FOLLOW_betweenExpr_in_valueExpr3867);
                    betweenExpr();

                    state._fsp--;


                    }
                    break;
                case 11 :
                    // EsperEPL2Ast.g:603:4: likeExpr
                    {
                    pushFollow(FOLLOW_likeExpr_in_valueExpr3872);
                    likeExpr();

                    state._fsp--;


                    }
                    break;
                case 12 :
                    // EsperEPL2Ast.g:604:4: regExpExpr
                    {
                    pushFollow(FOLLOW_regExpExpr_in_valueExpr3877);
                    regExpExpr();

                    state._fsp--;


                    }
                    break;
                case 13 :
                    // EsperEPL2Ast.g:605:4: arrayExpr
                    {
                    pushFollow(FOLLOW_arrayExpr_in_valueExpr3882);
                    arrayExpr();

                    state._fsp--;


                    }
                    break;
                case 14 :
                    // EsperEPL2Ast.g:606:4: subSelectInExpr
                    {
                    pushFollow(FOLLOW_subSelectInExpr_in_valueExpr3887);
                    subSelectInExpr();

                    state._fsp--;


                    }
                    break;
                case 15 :
                    // EsperEPL2Ast.g:607:5: subSelectRowExpr
                    {
                    pushFollow(FOLLOW_subSelectRowExpr_in_valueExpr3893);
                    subSelectRowExpr();

                    state._fsp--;


                    }
                    break;
                case 16 :
                    // EsperEPL2Ast.g:608:5: subSelectExistsExpr
                    {
                    pushFollow(FOLLOW_subSelectExistsExpr_in_valueExpr3900);
                    subSelectExistsExpr();

                    state._fsp--;


                    }
                    break;
                case 17 :
                    // EsperEPL2Ast.g:609:4: dotExpr
                    {
                    pushFollow(FOLLOW_dotExpr_in_valueExpr3905);
                    dotExpr();

                    state._fsp--;


                    }
                    break;
                case 18 :
                    // EsperEPL2Ast.g:610:4: newExpr
                    {
                    pushFollow(FOLLOW_newExpr_in_valueExpr3910);
                    newExpr();

                    state._fsp--;


                    }
                    break;
                case 19 :
                    // EsperEPL2Ast.g:611:4: jsonarray[true]
                    {
                    pushFollow(FOLLOW_jsonarray_in_valueExpr3915);
                    jsonarray(true);

                    state._fsp--;


                    }
                    break;
                case 20 :
                    // EsperEPL2Ast.g:612:4: jsonobject[true]
                    {
                    pushFollow(FOLLOW_jsonobject_in_valueExpr3921);
                    jsonobject(true);

                    state._fsp--;


                    }
                    break;

            }
        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "valueExpr"


    // $ANTLR start "valueExprWithTime"
    // EsperEPL2Ast.g:615:1: valueExprWithTime : (l= LAST | lw= LW | valueExpr | ^(ordered= OBJECT_PARAM_ORDERED_EXPR valueExpr ( DESC | ASC ) ) | rangeOperator | frequencyOperator | lastOperator | weekDayOperator | ^(l= NUMERIC_PARAM_LIST ( numericParameterList )+ ) | s= NUMBERSETSTAR | timePeriod );
    public final void valueExprWithTime() throws RecognitionException {
        CommonTree l=null;
        CommonTree lw=null;
        CommonTree ordered=null;
        CommonTree s=null;

        try {
            // EsperEPL2Ast.g:616:2: (l= LAST | lw= LW | valueExpr | ^(ordered= OBJECT_PARAM_ORDERED_EXPR valueExpr ( DESC | ASC ) ) | rangeOperator | frequencyOperator | lastOperator | weekDayOperator | ^(l= NUMERIC_PARAM_LIST ( numericParameterList )+ ) | s= NUMBERSETSTAR | timePeriod )
            int alt216=11;
            switch ( input.LA(1) ) {
            case LAST:
                {
                alt216=1;
                }
                break;
            case LW:
                {
                alt216=2;
                }
                break;
            case IN_SET:
            case BETWEEN:
            case LIKE:
            case REGEXP:
            case NOT_EXPR:
            case SUM:
            case AVG:
            case COALESCE:
            case MEDIAN:
            case STDDEV:
            case AVEDEV:
            case COUNT:
            case CASE:
            case CASE2:
            case ISTREAM:
            case PREVIOUS:
            case PREVIOUSTAIL:
            case PREVIOUSCOUNT:
            case PREVIOUSWINDOW:
            case PRIOR:
            case EXISTS:
            case INSTANCEOF:
            case TYPEOF:
            case CAST:
            case CURRENT_TIMESTAMP:
            case NEWKW:
            case EVAL_AND_EXPR:
            case EVAL_OR_EXPR:
            case EVAL_EQUALS_EXPR:
            case EVAL_NOTEQUALS_EXPR:
            case EVAL_IS_EXPR:
            case EVAL_ISNOT_EXPR:
            case EVAL_EQUALS_GROUP_EXPR:
            case EVAL_NOTEQUALS_GROUP_EXPR:
            case EVENT_PROP_EXPR:
            case CONCAT:
            case LIB_FUNC_CHAIN:
            case DOT_EXPR:
            case ARRAY_EXPR:
            case NOT_IN_SET:
            case NOT_BETWEEN:
            case NOT_LIKE:
            case NOT_REGEXP:
            case IN_RANGE:
            case NOT_IN_RANGE:
            case SUBSELECT_EXPR:
            case EXISTS_SUBSELECT_EXPR:
            case IN_SUBSELECT_EXPR:
            case NOT_IN_SUBSELECT_EXPR:
            case SUBSTITUTION:
            case FIRST_AGGREG:
            case LAST_AGGREG:
            case WINDOW_AGGREG:
            case INT_TYPE:
            case LONG_TYPE:
            case FLOAT_TYPE:
            case DOUBLE_TYPE:
            case STRING_TYPE:
            case BOOL_TYPE:
            case NULL_TYPE:
            case JSON_OBJECT:
            case JSON_ARRAY:
            case STAR:
            case LT:
            case GT:
            case BOR:
            case PLUS:
            case BAND:
            case BXOR:
            case LE:
            case GE:
            case MINUS:
            case DIV:
            case MOD:
            case EVAL_IS_GROUP_EXPR:
            case EVAL_ISNOT_GROUP_EXPR:
                {
                alt216=3;
                }
                break;
            case OBJECT_PARAM_ORDERED_EXPR:
                {
                alt216=4;
                }
                break;
            case NUMERIC_PARAM_RANGE:
                {
                alt216=5;
                }
                break;
            case NUMERIC_PARAM_FREQUENCY:
                {
                alt216=6;
                }
                break;
            case LAST_OPERATOR:
                {
                alt216=7;
                }
                break;
            case WEEKDAY_OPERATOR:
                {
                alt216=8;
                }
                break;
            case NUMERIC_PARAM_LIST:
                {
                alt216=9;
                }
                break;
            case NUMBERSETSTAR:
                {
                alt216=10;
                }
                break;
            case TIME_PERIOD:
                {
                alt216=11;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 216, 0, input);

                throw nvae;
            }

            switch (alt216) {
                case 1 :
                    // EsperEPL2Ast.g:616:4: l= LAST
                    {
                    l=(CommonTree)match(input,LAST,FOLLOW_LAST_in_valueExprWithTime3935); 
                     leaveNode(l); 

                    }
                    break;
                case 2 :
                    // EsperEPL2Ast.g:617:4: lw= LW
                    {
                    lw=(CommonTree)match(input,LW,FOLLOW_LW_in_valueExprWithTime3944); 
                     leaveNode(lw); 

                    }
                    break;
                case 3 :
                    // EsperEPL2Ast.g:618:4: valueExpr
                    {
                    pushFollow(FOLLOW_valueExpr_in_valueExprWithTime3951);
                    valueExpr();

                    state._fsp--;


                    }
                    break;
                case 4 :
                    // EsperEPL2Ast.g:619:4: ^(ordered= OBJECT_PARAM_ORDERED_EXPR valueExpr ( DESC | ASC ) )
                    {
                    ordered=(CommonTree)match(input,OBJECT_PARAM_ORDERED_EXPR,FOLLOW_OBJECT_PARAM_ORDERED_EXPR_in_valueExprWithTime3959); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_valueExpr_in_valueExprWithTime3961);
                    valueExpr();

                    state._fsp--;

                    if ( (input.LA(1)>=ASC && input.LA(1)<=DESC) ) {
                        input.consume();
                        state.errorRecovery=false;
                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        throw mse;
                    }

                     leaveNode(ordered); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 5 :
                    // EsperEPL2Ast.g:620:5: rangeOperator
                    {
                    pushFollow(FOLLOW_rangeOperator_in_valueExprWithTime3976);
                    rangeOperator();

                    state._fsp--;


                    }
                    break;
                case 6 :
                    // EsperEPL2Ast.g:621:5: frequencyOperator
                    {
                    pushFollow(FOLLOW_frequencyOperator_in_valueExprWithTime3982);
                    frequencyOperator();

                    state._fsp--;


                    }
                    break;
                case 7 :
                    // EsperEPL2Ast.g:622:4: lastOperator
                    {
                    pushFollow(FOLLOW_lastOperator_in_valueExprWithTime3987);
                    lastOperator();

                    state._fsp--;


                    }
                    break;
                case 8 :
                    // EsperEPL2Ast.g:623:4: weekDayOperator
                    {
                    pushFollow(FOLLOW_weekDayOperator_in_valueExprWithTime3992);
                    weekDayOperator();

                    state._fsp--;


                    }
                    break;
                case 9 :
                    // EsperEPL2Ast.g:624:5: ^(l= NUMERIC_PARAM_LIST ( numericParameterList )+ )
                    {
                    l=(CommonTree)match(input,NUMERIC_PARAM_LIST,FOLLOW_NUMERIC_PARAM_LIST_in_valueExprWithTime4002); 

                    match(input, Token.DOWN, null); 
                    // EsperEPL2Ast.g:624:29: ( numericParameterList )+
                    int cnt215=0;
                    loop215:
                    do {
                        int alt215=2;
                        int LA215_0 = input.LA(1);

                        if ( (LA215_0==NUMERIC_PARAM_RANGE||LA215_0==NUMERIC_PARAM_FREQUENCY||(LA215_0>=INT_TYPE && LA215_0<=NULL_TYPE)) ) {
                            alt215=1;
                        }


                        switch (alt215) {
                    	case 1 :
                    	    // EsperEPL2Ast.g:624:29: numericParameterList
                    	    {
                    	    pushFollow(FOLLOW_numericParameterList_in_valueExprWithTime4004);
                    	    numericParameterList();

                    	    state._fsp--;


                    	    }
                    	    break;

                    	default :
                    	    if ( cnt215 >= 1 ) break loop215;
                                EarlyExitException eee =
                                    new EarlyExitException(215, input);
                                throw eee;
                        }
                        cnt215++;
                    } while (true);

                     leaveNode(l); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 10 :
                    // EsperEPL2Ast.g:625:4: s= NUMBERSETSTAR
                    {
                    s=(CommonTree)match(input,NUMBERSETSTAR,FOLLOW_NUMBERSETSTAR_in_valueExprWithTime4015); 
                     leaveNode(s); 

                    }
                    break;
                case 11 :
                    // EsperEPL2Ast.g:626:4: timePeriod
                    {
                    pushFollow(FOLLOW_timePeriod_in_valueExprWithTime4022);
                    timePeriod();

                    state._fsp--;


                    }
                    break;

            }
        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "valueExprWithTime"


    // $ANTLR start "numericParameterList"
    // EsperEPL2Ast.g:629:1: numericParameterList : ( constant[true] | rangeOperator | frequencyOperator );
    public final void numericParameterList() throws RecognitionException {
        try {
            // EsperEPL2Ast.g:630:2: ( constant[true] | rangeOperator | frequencyOperator )
            int alt217=3;
            switch ( input.LA(1) ) {
            case INT_TYPE:
            case LONG_TYPE:
            case FLOAT_TYPE:
            case DOUBLE_TYPE:
            case STRING_TYPE:
            case BOOL_TYPE:
            case NULL_TYPE:
                {
                alt217=1;
                }
                break;
            case NUMERIC_PARAM_RANGE:
                {
                alt217=2;
                }
                break;
            case NUMERIC_PARAM_FREQUENCY:
                {
                alt217=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 217, 0, input);

                throw nvae;
            }

            switch (alt217) {
                case 1 :
                    // EsperEPL2Ast.g:630:5: constant[true]
                    {
                    pushFollow(FOLLOW_constant_in_numericParameterList4035);
                    constant(true);

                    state._fsp--;


                    }
                    break;
                case 2 :
                    // EsperEPL2Ast.g:631:5: rangeOperator
                    {
                    pushFollow(FOLLOW_rangeOperator_in_numericParameterList4042);
                    rangeOperator();

                    state._fsp--;


                    }
                    break;
                case 3 :
                    // EsperEPL2Ast.g:632:5: frequencyOperator
                    {
                    pushFollow(FOLLOW_frequencyOperator_in_numericParameterList4048);
                    frequencyOperator();

                    state._fsp--;


                    }
                    break;

            }
        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "numericParameterList"


    // $ANTLR start "rangeOperator"
    // EsperEPL2Ast.g:635:1: rangeOperator : ^(r= NUMERIC_PARAM_RANGE ( constant[true] | eventPropertyExpr[true] | substitution ) ( constant[true] | eventPropertyExpr[true] | substitution ) ) ;
    public final void rangeOperator() throws RecognitionException {
        CommonTree r=null;

        try {
            // EsperEPL2Ast.g:636:2: ( ^(r= NUMERIC_PARAM_RANGE ( constant[true] | eventPropertyExpr[true] | substitution ) ( constant[true] | eventPropertyExpr[true] | substitution ) ) )
            // EsperEPL2Ast.g:636:4: ^(r= NUMERIC_PARAM_RANGE ( constant[true] | eventPropertyExpr[true] | substitution ) ( constant[true] | eventPropertyExpr[true] | substitution ) )
            {
            r=(CommonTree)match(input,NUMERIC_PARAM_RANGE,FOLLOW_NUMERIC_PARAM_RANGE_in_rangeOperator4064); 

            match(input, Token.DOWN, null); 
            // EsperEPL2Ast.g:636:29: ( constant[true] | eventPropertyExpr[true] | substitution )
            int alt218=3;
            switch ( input.LA(1) ) {
            case INT_TYPE:
            case LONG_TYPE:
            case FLOAT_TYPE:
            case DOUBLE_TYPE:
            case STRING_TYPE:
            case BOOL_TYPE:
            case NULL_TYPE:
                {
                alt218=1;
                }
                break;
            case EVENT_PROP_EXPR:
                {
                alt218=2;
                }
                break;
            case SUBSTITUTION:
                {
                alt218=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 218, 0, input);

                throw nvae;
            }

            switch (alt218) {
                case 1 :
                    // EsperEPL2Ast.g:636:30: constant[true]
                    {
                    pushFollow(FOLLOW_constant_in_rangeOperator4067);
                    constant(true);

                    state._fsp--;


                    }
                    break;
                case 2 :
                    // EsperEPL2Ast.g:636:45: eventPropertyExpr[true]
                    {
                    pushFollow(FOLLOW_eventPropertyExpr_in_rangeOperator4070);
                    eventPropertyExpr(true);

                    state._fsp--;


                    }
                    break;
                case 3 :
                    // EsperEPL2Ast.g:636:69: substitution
                    {
                    pushFollow(FOLLOW_substitution_in_rangeOperator4073);
                    substitution();

                    state._fsp--;


                    }
                    break;

            }

            // EsperEPL2Ast.g:636:83: ( constant[true] | eventPropertyExpr[true] | substitution )
            int alt219=3;
            switch ( input.LA(1) ) {
            case INT_TYPE:
            case LONG_TYPE:
            case FLOAT_TYPE:
            case DOUBLE_TYPE:
            case STRING_TYPE:
            case BOOL_TYPE:
            case NULL_TYPE:
                {
                alt219=1;
                }
                break;
            case EVENT_PROP_EXPR:
                {
                alt219=2;
                }
                break;
            case SUBSTITUTION:
                {
                alt219=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 219, 0, input);

                throw nvae;
            }

            switch (alt219) {
                case 1 :
                    // EsperEPL2Ast.g:636:84: constant[true]
                    {
                    pushFollow(FOLLOW_constant_in_rangeOperator4077);
                    constant(true);

                    state._fsp--;


                    }
                    break;
                case 2 :
                    // EsperEPL2Ast.g:636:99: eventPropertyExpr[true]
                    {
                    pushFollow(FOLLOW_eventPropertyExpr_in_rangeOperator4080);
                    eventPropertyExpr(true);

                    state._fsp--;


                    }
                    break;
                case 3 :
                    // EsperEPL2Ast.g:636:123: substitution
                    {
                    pushFollow(FOLLOW_substitution_in_rangeOperator4083);
                    substitution();

                    state._fsp--;


                    }
                    break;

            }

             leaveNode(r); 

            match(input, Token.UP, null); 

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "rangeOperator"


    // $ANTLR start "frequencyOperator"
    // EsperEPL2Ast.g:639:1: frequencyOperator : ^(f= NUMERIC_PARAM_FREQUENCY ( constant[true] | eventPropertyExpr[true] | substitution ) ) ;
    public final void frequencyOperator() throws RecognitionException {
        CommonTree f=null;

        try {
            // EsperEPL2Ast.g:640:2: ( ^(f= NUMERIC_PARAM_FREQUENCY ( constant[true] | eventPropertyExpr[true] | substitution ) ) )
            // EsperEPL2Ast.g:640:4: ^(f= NUMERIC_PARAM_FREQUENCY ( constant[true] | eventPropertyExpr[true] | substitution ) )
            {
            f=(CommonTree)match(input,NUMERIC_PARAM_FREQUENCY,FOLLOW_NUMERIC_PARAM_FREQUENCY_in_frequencyOperator4104); 

            match(input, Token.DOWN, null); 
            // EsperEPL2Ast.g:640:33: ( constant[true] | eventPropertyExpr[true] | substitution )
            int alt220=3;
            switch ( input.LA(1) ) {
            case INT_TYPE:
            case LONG_TYPE:
            case FLOAT_TYPE:
            case DOUBLE_TYPE:
            case STRING_TYPE:
            case BOOL_TYPE:
            case NULL_TYPE:
                {
                alt220=1;
                }
                break;
            case EVENT_PROP_EXPR:
                {
                alt220=2;
                }
                break;
            case SUBSTITUTION:
                {
                alt220=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 220, 0, input);

                throw nvae;
            }

            switch (alt220) {
                case 1 :
                    // EsperEPL2Ast.g:640:34: constant[true]
                    {
                    pushFollow(FOLLOW_constant_in_frequencyOperator4107);
                    constant(true);

                    state._fsp--;


                    }
                    break;
                case 2 :
                    // EsperEPL2Ast.g:640:49: eventPropertyExpr[true]
                    {
                    pushFollow(FOLLOW_eventPropertyExpr_in_frequencyOperator4110);
                    eventPropertyExpr(true);

                    state._fsp--;


                    }
                    break;
                case 3 :
                    // EsperEPL2Ast.g:640:73: substitution
                    {
                    pushFollow(FOLLOW_substitution_in_frequencyOperator4113);
                    substitution();

                    state._fsp--;


                    }
                    break;

            }

             leaveNode(f); 

            match(input, Token.UP, null); 

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "frequencyOperator"


    // $ANTLR start "lastOperator"
    // EsperEPL2Ast.g:643:1: lastOperator : ^(l= LAST_OPERATOR ( constant[true] | eventPropertyExpr[true] | substitution ) ) ;
    public final void lastOperator() throws RecognitionException {
        CommonTree l=null;

        try {
            // EsperEPL2Ast.g:644:2: ( ^(l= LAST_OPERATOR ( constant[true] | eventPropertyExpr[true] | substitution ) ) )
            // EsperEPL2Ast.g:644:4: ^(l= LAST_OPERATOR ( constant[true] | eventPropertyExpr[true] | substitution ) )
            {
            l=(CommonTree)match(input,LAST_OPERATOR,FOLLOW_LAST_OPERATOR_in_lastOperator4132); 

            match(input, Token.DOWN, null); 
            // EsperEPL2Ast.g:644:23: ( constant[true] | eventPropertyExpr[true] | substitution )
            int alt221=3;
            switch ( input.LA(1) ) {
            case INT_TYPE:
            case LONG_TYPE:
            case FLOAT_TYPE:
            case DOUBLE_TYPE:
            case STRING_TYPE:
            case BOOL_TYPE:
            case NULL_TYPE:
                {
                alt221=1;
                }
                break;
            case EVENT_PROP_EXPR:
                {
                alt221=2;
                }
                break;
            case SUBSTITUTION:
                {
                alt221=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 221, 0, input);

                throw nvae;
            }

            switch (alt221) {
                case 1 :
                    // EsperEPL2Ast.g:644:24: constant[true]
                    {
                    pushFollow(FOLLOW_constant_in_lastOperator4135);
                    constant(true);

                    state._fsp--;


                    }
                    break;
                case 2 :
                    // EsperEPL2Ast.g:644:39: eventPropertyExpr[true]
                    {
                    pushFollow(FOLLOW_eventPropertyExpr_in_lastOperator4138);
                    eventPropertyExpr(true);

                    state._fsp--;


                    }
                    break;
                case 3 :
                    // EsperEPL2Ast.g:644:63: substitution
                    {
                    pushFollow(FOLLOW_substitution_in_lastOperator4141);
                    substitution();

                    state._fsp--;


                    }
                    break;

            }

             leaveNode(l); 

            match(input, Token.UP, null); 

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "lastOperator"


    // $ANTLR start "weekDayOperator"
    // EsperEPL2Ast.g:647:1: weekDayOperator : ^(w= WEEKDAY_OPERATOR ( constant[true] | eventPropertyExpr[true] | substitution ) ) ;
    public final void weekDayOperator() throws RecognitionException {
        CommonTree w=null;

        try {
            // EsperEPL2Ast.g:648:2: ( ^(w= WEEKDAY_OPERATOR ( constant[true] | eventPropertyExpr[true] | substitution ) ) )
            // EsperEPL2Ast.g:648:4: ^(w= WEEKDAY_OPERATOR ( constant[true] | eventPropertyExpr[true] | substitution ) )
            {
            w=(CommonTree)match(input,WEEKDAY_OPERATOR,FOLLOW_WEEKDAY_OPERATOR_in_weekDayOperator4160); 

            match(input, Token.DOWN, null); 
            // EsperEPL2Ast.g:648:26: ( constant[true] | eventPropertyExpr[true] | substitution )
            int alt222=3;
            switch ( input.LA(1) ) {
            case INT_TYPE:
            case LONG_TYPE:
            case FLOAT_TYPE:
            case DOUBLE_TYPE:
            case STRING_TYPE:
            case BOOL_TYPE:
            case NULL_TYPE:
                {
                alt222=1;
                }
                break;
            case EVENT_PROP_EXPR:
                {
                alt222=2;
                }
                break;
            case SUBSTITUTION:
                {
                alt222=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 222, 0, input);

                throw nvae;
            }

            switch (alt222) {
                case 1 :
                    // EsperEPL2Ast.g:648:27: constant[true]
                    {
                    pushFollow(FOLLOW_constant_in_weekDayOperator4163);
                    constant(true);

                    state._fsp--;


                    }
                    break;
                case 2 :
                    // EsperEPL2Ast.g:648:42: eventPropertyExpr[true]
                    {
                    pushFollow(FOLLOW_eventPropertyExpr_in_weekDayOperator4166);
                    eventPropertyExpr(true);

                    state._fsp--;


                    }
                    break;
                case 3 :
                    // EsperEPL2Ast.g:648:66: substitution
                    {
                    pushFollow(FOLLOW_substitution_in_weekDayOperator4169);
                    substitution();

                    state._fsp--;


                    }
                    break;

            }

             leaveNode(w); 

            match(input, Token.UP, null); 

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "weekDayOperator"


    // $ANTLR start "subSelectGroupExpr"
    // EsperEPL2Ast.g:651:1: subSelectGroupExpr : ^(s= SUBSELECT_GROUP_EXPR subQueryExpr ) ;
    public final void subSelectGroupExpr() throws RecognitionException {
        CommonTree s=null;

        try {
            // EsperEPL2Ast.g:652:2: ( ^(s= SUBSELECT_GROUP_EXPR subQueryExpr ) )
            // EsperEPL2Ast.g:652:4: ^(s= SUBSELECT_GROUP_EXPR subQueryExpr )
            {
            pushStmtContext();
            s=(CommonTree)match(input,SUBSELECT_GROUP_EXPR,FOLLOW_SUBSELECT_GROUP_EXPR_in_subSelectGroupExpr4190); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_subQueryExpr_in_subSelectGroupExpr4192);
            subQueryExpr();

            state._fsp--;


            match(input, Token.UP, null); 

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "subSelectGroupExpr"


    // $ANTLR start "subSelectRowExpr"
    // EsperEPL2Ast.g:655:1: subSelectRowExpr : ^(s= SUBSELECT_EXPR subQueryExpr ) ;
    public final void subSelectRowExpr() throws RecognitionException {
        CommonTree s=null;

        try {
            // EsperEPL2Ast.g:656:2: ( ^(s= SUBSELECT_EXPR subQueryExpr ) )
            // EsperEPL2Ast.g:656:4: ^(s= SUBSELECT_EXPR subQueryExpr )
            {
            pushStmtContext();
            s=(CommonTree)match(input,SUBSELECT_EXPR,FOLLOW_SUBSELECT_EXPR_in_subSelectRowExpr4211); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_subQueryExpr_in_subSelectRowExpr4213);
            subQueryExpr();

            state._fsp--;


            match(input, Token.UP, null); 
            leaveNode(s);

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "subSelectRowExpr"


    // $ANTLR start "subSelectExistsExpr"
    // EsperEPL2Ast.g:659:1: subSelectExistsExpr : ^(e= EXISTS_SUBSELECT_EXPR subQueryExpr ) ;
    public final void subSelectExistsExpr() throws RecognitionException {
        CommonTree e=null;

        try {
            // EsperEPL2Ast.g:660:2: ( ^(e= EXISTS_SUBSELECT_EXPR subQueryExpr ) )
            // EsperEPL2Ast.g:660:4: ^(e= EXISTS_SUBSELECT_EXPR subQueryExpr )
            {
            pushStmtContext();
            e=(CommonTree)match(input,EXISTS_SUBSELECT_EXPR,FOLLOW_EXISTS_SUBSELECT_EXPR_in_subSelectExistsExpr4232); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_subQueryExpr_in_subSelectExistsExpr4234);
            subQueryExpr();

            state._fsp--;


            match(input, Token.UP, null); 
            leaveNode(e);

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "subSelectExistsExpr"


    // $ANTLR start "subSelectInExpr"
    // EsperEPL2Ast.g:663:1: subSelectInExpr : ( ^(s= IN_SUBSELECT_EXPR valueExpr subSelectInQueryExpr ) | ^(s= NOT_IN_SUBSELECT_EXPR valueExpr subSelectInQueryExpr ) );
    public final void subSelectInExpr() throws RecognitionException {
        CommonTree s=null;

        try {
            // EsperEPL2Ast.g:664:2: ( ^(s= IN_SUBSELECT_EXPR valueExpr subSelectInQueryExpr ) | ^(s= NOT_IN_SUBSELECT_EXPR valueExpr subSelectInQueryExpr ) )
            int alt223=2;
            int LA223_0 = input.LA(1);

            if ( (LA223_0==IN_SUBSELECT_EXPR) ) {
                alt223=1;
            }
            else if ( (LA223_0==NOT_IN_SUBSELECT_EXPR) ) {
                alt223=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 223, 0, input);

                throw nvae;
            }
            switch (alt223) {
                case 1 :
                    // EsperEPL2Ast.g:664:5: ^(s= IN_SUBSELECT_EXPR valueExpr subSelectInQueryExpr )
                    {
                    s=(CommonTree)match(input,IN_SUBSELECT_EXPR,FOLLOW_IN_SUBSELECT_EXPR_in_subSelectInExpr4253); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_valueExpr_in_subSelectInExpr4255);
                    valueExpr();

                    state._fsp--;

                    pushFollow(FOLLOW_subSelectInQueryExpr_in_subSelectInExpr4257);
                    subSelectInQueryExpr();

                    state._fsp--;


                    match(input, Token.UP, null); 
                     leaveNode(s); 

                    }
                    break;
                case 2 :
                    // EsperEPL2Ast.g:665:5: ^(s= NOT_IN_SUBSELECT_EXPR valueExpr subSelectInQueryExpr )
                    {
                    s=(CommonTree)match(input,NOT_IN_SUBSELECT_EXPR,FOLLOW_NOT_IN_SUBSELECT_EXPR_in_subSelectInExpr4269); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_valueExpr_in_subSelectInExpr4271);
                    valueExpr();

                    state._fsp--;

                    pushFollow(FOLLOW_subSelectInQueryExpr_in_subSelectInExpr4273);
                    subSelectInQueryExpr();

                    state._fsp--;


                    match(input, Token.UP, null); 
                     leaveNode(s); 

                    }
                    break;

            }
        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "subSelectInExpr"


    // $ANTLR start "subSelectInQueryExpr"
    // EsperEPL2Ast.g:668:1: subSelectInQueryExpr : ^(i= IN_SUBSELECT_QUERY_EXPR subQueryExpr ) ;
    public final void subSelectInQueryExpr() throws RecognitionException {
        CommonTree i=null;

        try {
            // EsperEPL2Ast.g:669:2: ( ^(i= IN_SUBSELECT_QUERY_EXPR subQueryExpr ) )
            // EsperEPL2Ast.g:669:4: ^(i= IN_SUBSELECT_QUERY_EXPR subQueryExpr )
            {
            pushStmtContext();
            i=(CommonTree)match(input,IN_SUBSELECT_QUERY_EXPR,FOLLOW_IN_SUBSELECT_QUERY_EXPR_in_subSelectInQueryExpr4292); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_subQueryExpr_in_subSelectInQueryExpr4294);
            subQueryExpr();

            state._fsp--;


            match(input, Token.UP, null); 
            leaveNode(i);

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "subSelectInQueryExpr"


    // $ANTLR start "subQueryExpr"
    // EsperEPL2Ast.g:672:1: subQueryExpr : ( DISTINCT )? selectionList subSelectFilterExpr ( whereClause[true] )? ;
    public final void subQueryExpr() throws RecognitionException {
        try {
            // EsperEPL2Ast.g:673:2: ( ( DISTINCT )? selectionList subSelectFilterExpr ( whereClause[true] )? )
            // EsperEPL2Ast.g:673:4: ( DISTINCT )? selectionList subSelectFilterExpr ( whereClause[true] )?
            {
            // EsperEPL2Ast.g:673:4: ( DISTINCT )?
            int alt224=2;
            int LA224_0 = input.LA(1);

            if ( (LA224_0==DISTINCT) ) {
                alt224=1;
            }
            switch (alt224) {
                case 1 :
                    // EsperEPL2Ast.g:673:4: DISTINCT
                    {
                    match(input,DISTINCT,FOLLOW_DISTINCT_in_subQueryExpr4310); 

                    }
                    break;

            }

            pushFollow(FOLLOW_selectionList_in_subQueryExpr4313);
            selectionList();

            state._fsp--;

            pushFollow(FOLLOW_subSelectFilterExpr_in_subQueryExpr4315);
            subSelectFilterExpr();

            state._fsp--;

            // EsperEPL2Ast.g:673:48: ( whereClause[true] )?
            int alt225=2;
            int LA225_0 = input.LA(1);

            if ( (LA225_0==WHERE_EXPR) ) {
                alt225=1;
            }
            switch (alt225) {
                case 1 :
                    // EsperEPL2Ast.g:673:49: whereClause[true]
                    {
                    pushFollow(FOLLOW_whereClause_in_subQueryExpr4318);
                    whereClause(true);

                    state._fsp--;


                    }
                    break;

            }


            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "subQueryExpr"


    // $ANTLR start "subSelectFilterExpr"
    // EsperEPL2Ast.g:676:1: subSelectFilterExpr : ^(v= STREAM_EXPR eventFilterExpr[true] ( viewListExpr )? ( IDENT )? ( RETAINUNION )? ( RETAININTERSECTION )? ) ;
    public final void subSelectFilterExpr() throws RecognitionException {
        CommonTree v=null;

        try {
            // EsperEPL2Ast.g:677:2: ( ^(v= STREAM_EXPR eventFilterExpr[true] ( viewListExpr )? ( IDENT )? ( RETAINUNION )? ( RETAININTERSECTION )? ) )
            // EsperEPL2Ast.g:677:4: ^(v= STREAM_EXPR eventFilterExpr[true] ( viewListExpr )? ( IDENT )? ( RETAINUNION )? ( RETAININTERSECTION )? )
            {
            v=(CommonTree)match(input,STREAM_EXPR,FOLLOW_STREAM_EXPR_in_subSelectFilterExpr4336); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_eventFilterExpr_in_subSelectFilterExpr4338);
            eventFilterExpr(true);

            state._fsp--;

            // EsperEPL2Ast.g:677:42: ( viewListExpr )?
            int alt226=2;
            int LA226_0 = input.LA(1);

            if ( (LA226_0==VIEW_EXPR) ) {
                alt226=1;
            }
            switch (alt226) {
                case 1 :
                    // EsperEPL2Ast.g:677:43: viewListExpr
                    {
                    pushFollow(FOLLOW_viewListExpr_in_subSelectFilterExpr4342);
                    viewListExpr();

                    state._fsp--;


                    }
                    break;

            }

            // EsperEPL2Ast.g:677:58: ( IDENT )?
            int alt227=2;
            int LA227_0 = input.LA(1);

            if ( (LA227_0==IDENT) ) {
                alt227=1;
            }
            switch (alt227) {
                case 1 :
                    // EsperEPL2Ast.g:677:59: IDENT
                    {
                    match(input,IDENT,FOLLOW_IDENT_in_subSelectFilterExpr4347); 

                    }
                    break;

            }

            // EsperEPL2Ast.g:677:67: ( RETAINUNION )?
            int alt228=2;
            int LA228_0 = input.LA(1);

            if ( (LA228_0==RETAINUNION) ) {
                alt228=1;
            }
            switch (alt228) {
                case 1 :
                    // EsperEPL2Ast.g:677:67: RETAINUNION
                    {
                    match(input,RETAINUNION,FOLLOW_RETAINUNION_in_subSelectFilterExpr4351); 

                    }
                    break;

            }

            // EsperEPL2Ast.g:677:80: ( RETAININTERSECTION )?
            int alt229=2;
            int LA229_0 = input.LA(1);

            if ( (LA229_0==RETAININTERSECTION) ) {
                alt229=1;
            }
            switch (alt229) {
                case 1 :
                    // EsperEPL2Ast.g:677:80: RETAININTERSECTION
                    {
                    match(input,RETAININTERSECTION,FOLLOW_RETAININTERSECTION_in_subSelectFilterExpr4354); 

                    }
                    break;

            }

             leaveNode(v); 

            match(input, Token.UP, null); 

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "subSelectFilterExpr"


    // $ANTLR start "caseExpr"
    // EsperEPL2Ast.g:680:1: caseExpr : ( ^(c= CASE ( valueExpr )* ) | ^(c= CASE2 ( valueExpr )* ) );
    public final void caseExpr() throws RecognitionException {
        CommonTree c=null;

        try {
            // EsperEPL2Ast.g:681:2: ( ^(c= CASE ( valueExpr )* ) | ^(c= CASE2 ( valueExpr )* ) )
            int alt232=2;
            int LA232_0 = input.LA(1);

            if ( (LA232_0==CASE) ) {
                alt232=1;
            }
            else if ( (LA232_0==CASE2) ) {
                alt232=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 232, 0, input);

                throw nvae;
            }
            switch (alt232) {
                case 1 :
                    // EsperEPL2Ast.g:681:4: ^(c= CASE ( valueExpr )* )
                    {
                    c=(CommonTree)match(input,CASE,FOLLOW_CASE_in_caseExpr4374); 

                    if ( input.LA(1)==Token.DOWN ) {
                        match(input, Token.DOWN, null); 
                        // EsperEPL2Ast.g:681:13: ( valueExpr )*
                        loop230:
                        do {
                            int alt230=2;
                            int LA230_0 = input.LA(1);

                            if ( ((LA230_0>=IN_SET && LA230_0<=REGEXP)||LA230_0==NOT_EXPR||(LA230_0>=SUM && LA230_0<=AVG)||(LA230_0>=COALESCE && LA230_0<=COUNT)||(LA230_0>=CASE && LA230_0<=CASE2)||LA230_0==ISTREAM||(LA230_0>=PREVIOUS && LA230_0<=EXISTS)||(LA230_0>=INSTANCEOF && LA230_0<=CURRENT_TIMESTAMP)||LA230_0==NEWKW||(LA230_0>=EVAL_AND_EXPR && LA230_0<=EVAL_NOTEQUALS_GROUP_EXPR)||LA230_0==EVENT_PROP_EXPR||LA230_0==CONCAT||(LA230_0>=LIB_FUNC_CHAIN && LA230_0<=DOT_EXPR)||LA230_0==ARRAY_EXPR||(LA230_0>=NOT_IN_SET && LA230_0<=NOT_REGEXP)||(LA230_0>=IN_RANGE && LA230_0<=SUBSELECT_EXPR)||(LA230_0>=EXISTS_SUBSELECT_EXPR && LA230_0<=NOT_IN_SUBSELECT_EXPR)||LA230_0==SUBSTITUTION||(LA230_0>=FIRST_AGGREG && LA230_0<=WINDOW_AGGREG)||(LA230_0>=INT_TYPE && LA230_0<=NULL_TYPE)||(LA230_0>=JSON_OBJECT && LA230_0<=JSON_ARRAY)||LA230_0==STAR||(LA230_0>=LT && LA230_0<=GT)||(LA230_0>=BOR && LA230_0<=PLUS)||(LA230_0>=BAND && LA230_0<=BXOR)||(LA230_0>=LE && LA230_0<=GE)||(LA230_0>=MINUS && LA230_0<=MOD)||(LA230_0>=EVAL_IS_GROUP_EXPR && LA230_0<=EVAL_ISNOT_GROUP_EXPR)) ) {
                                alt230=1;
                            }


                            switch (alt230) {
                        	case 1 :
                        	    // EsperEPL2Ast.g:681:14: valueExpr
                        	    {
                        	    pushFollow(FOLLOW_valueExpr_in_caseExpr4377);
                        	    valueExpr();

                        	    state._fsp--;


                        	    }
                        	    break;

                        	default :
                        	    break loop230;
                            }
                        } while (true);


                        match(input, Token.UP, null); 
                    }
                     leaveNode(c); 

                    }
                    break;
                case 2 :
                    // EsperEPL2Ast.g:682:4: ^(c= CASE2 ( valueExpr )* )
                    {
                    c=(CommonTree)match(input,CASE2,FOLLOW_CASE2_in_caseExpr4390); 

                    if ( input.LA(1)==Token.DOWN ) {
                        match(input, Token.DOWN, null); 
                        // EsperEPL2Ast.g:682:14: ( valueExpr )*
                        loop231:
                        do {
                            int alt231=2;
                            int LA231_0 = input.LA(1);

                            if ( ((LA231_0>=IN_SET && LA231_0<=REGEXP)||LA231_0==NOT_EXPR||(LA231_0>=SUM && LA231_0<=AVG)||(LA231_0>=COALESCE && LA231_0<=COUNT)||(LA231_0>=CASE && LA231_0<=CASE2)||LA231_0==ISTREAM||(LA231_0>=PREVIOUS && LA231_0<=EXISTS)||(LA231_0>=INSTANCEOF && LA231_0<=CURRENT_TIMESTAMP)||LA231_0==NEWKW||(LA231_0>=EVAL_AND_EXPR && LA231_0<=EVAL_NOTEQUALS_GROUP_EXPR)||LA231_0==EVENT_PROP_EXPR||LA231_0==CONCAT||(LA231_0>=LIB_FUNC_CHAIN && LA231_0<=DOT_EXPR)||LA231_0==ARRAY_EXPR||(LA231_0>=NOT_IN_SET && LA231_0<=NOT_REGEXP)||(LA231_0>=IN_RANGE && LA231_0<=SUBSELECT_EXPR)||(LA231_0>=EXISTS_SUBSELECT_EXPR && LA231_0<=NOT_IN_SUBSELECT_EXPR)||LA231_0==SUBSTITUTION||(LA231_0>=FIRST_AGGREG && LA231_0<=WINDOW_AGGREG)||(LA231_0>=INT_TYPE && LA231_0<=NULL_TYPE)||(LA231_0>=JSON_OBJECT && LA231_0<=JSON_ARRAY)||LA231_0==STAR||(LA231_0>=LT && LA231_0<=GT)||(LA231_0>=BOR && LA231_0<=PLUS)||(LA231_0>=BAND && LA231_0<=BXOR)||(LA231_0>=LE && LA231_0<=GE)||(LA231_0>=MINUS && LA231_0<=MOD)||(LA231_0>=EVAL_IS_GROUP_EXPR && LA231_0<=EVAL_ISNOT_GROUP_EXPR)) ) {
                                alt231=1;
                            }


                            switch (alt231) {
                        	case 1 :
                        	    // EsperEPL2Ast.g:682:15: valueExpr
                        	    {
                        	    pushFollow(FOLLOW_valueExpr_in_caseExpr4393);
                        	    valueExpr();

                        	    state._fsp--;


                        	    }
                        	    break;

                        	default :
                        	    break loop231;
                            }
                        } while (true);


                        match(input, Token.UP, null); 
                    }
                     leaveNode(c); 

                    }
                    break;

            }
        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "caseExpr"


    // $ANTLR start "inExpr"
    // EsperEPL2Ast.g:685:1: inExpr : ( ^(i= IN_SET valueExpr ( LPAREN | LBRACK ) valueExpr ( valueExpr )* ( RPAREN | RBRACK ) ) | ^(i= NOT_IN_SET valueExpr ( LPAREN | LBRACK ) valueExpr ( valueExpr )* ( RPAREN | RBRACK ) ) | ^(i= IN_RANGE valueExpr ( LPAREN | LBRACK ) valueExpr valueExpr ( RPAREN | RBRACK ) ) | ^(i= NOT_IN_RANGE valueExpr ( LPAREN | LBRACK ) valueExpr valueExpr ( RPAREN | RBRACK ) ) );
    public final void inExpr() throws RecognitionException {
        CommonTree i=null;

        try {
            // EsperEPL2Ast.g:686:2: ( ^(i= IN_SET valueExpr ( LPAREN | LBRACK ) valueExpr ( valueExpr )* ( RPAREN | RBRACK ) ) | ^(i= NOT_IN_SET valueExpr ( LPAREN | LBRACK ) valueExpr ( valueExpr )* ( RPAREN | RBRACK ) ) | ^(i= IN_RANGE valueExpr ( LPAREN | LBRACK ) valueExpr valueExpr ( RPAREN | RBRACK ) ) | ^(i= NOT_IN_RANGE valueExpr ( LPAREN | LBRACK ) valueExpr valueExpr ( RPAREN | RBRACK ) ) )
            int alt235=4;
            switch ( input.LA(1) ) {
            case IN_SET:
                {
                alt235=1;
                }
                break;
            case NOT_IN_SET:
                {
                alt235=2;
                }
                break;
            case IN_RANGE:
                {
                alt235=3;
                }
                break;
            case NOT_IN_RANGE:
                {
                alt235=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 235, 0, input);

                throw nvae;
            }

            switch (alt235) {
                case 1 :
                    // EsperEPL2Ast.g:686:4: ^(i= IN_SET valueExpr ( LPAREN | LBRACK ) valueExpr ( valueExpr )* ( RPAREN | RBRACK ) )
                    {
                    i=(CommonTree)match(input,IN_SET,FOLLOW_IN_SET_in_inExpr4413); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_valueExpr_in_inExpr4415);
                    valueExpr();

                    state._fsp--;

                    if ( input.LA(1)==LBRACK||input.LA(1)==LPAREN ) {
                        input.consume();
                        state.errorRecovery=false;
                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        throw mse;
                    }

                    pushFollow(FOLLOW_valueExpr_in_inExpr4423);
                    valueExpr();

                    state._fsp--;

                    // EsperEPL2Ast.g:686:51: ( valueExpr )*
                    loop233:
                    do {
                        int alt233=2;
                        int LA233_0 = input.LA(1);

                        if ( ((LA233_0>=IN_SET && LA233_0<=REGEXP)||LA233_0==NOT_EXPR||(LA233_0>=SUM && LA233_0<=AVG)||(LA233_0>=COALESCE && LA233_0<=COUNT)||(LA233_0>=CASE && LA233_0<=CASE2)||LA233_0==ISTREAM||(LA233_0>=PREVIOUS && LA233_0<=EXISTS)||(LA233_0>=INSTANCEOF && LA233_0<=CURRENT_TIMESTAMP)||LA233_0==NEWKW||(LA233_0>=EVAL_AND_EXPR && LA233_0<=EVAL_NOTEQUALS_GROUP_EXPR)||LA233_0==EVENT_PROP_EXPR||LA233_0==CONCAT||(LA233_0>=LIB_FUNC_CHAIN && LA233_0<=DOT_EXPR)||LA233_0==ARRAY_EXPR||(LA233_0>=NOT_IN_SET && LA233_0<=NOT_REGEXP)||(LA233_0>=IN_RANGE && LA233_0<=SUBSELECT_EXPR)||(LA233_0>=EXISTS_SUBSELECT_EXPR && LA233_0<=NOT_IN_SUBSELECT_EXPR)||LA233_0==SUBSTITUTION||(LA233_0>=FIRST_AGGREG && LA233_0<=WINDOW_AGGREG)||(LA233_0>=INT_TYPE && LA233_0<=NULL_TYPE)||(LA233_0>=JSON_OBJECT && LA233_0<=JSON_ARRAY)||LA233_0==STAR||(LA233_0>=LT && LA233_0<=GT)||(LA233_0>=BOR && LA233_0<=PLUS)||(LA233_0>=BAND && LA233_0<=BXOR)||(LA233_0>=LE && LA233_0<=GE)||(LA233_0>=MINUS && LA233_0<=MOD)||(LA233_0>=EVAL_IS_GROUP_EXPR && LA233_0<=EVAL_ISNOT_GROUP_EXPR)) ) {
                            alt233=1;
                        }


                        switch (alt233) {
                    	case 1 :
                    	    // EsperEPL2Ast.g:686:52: valueExpr
                    	    {
                    	    pushFollow(FOLLOW_valueExpr_in_inExpr4426);
                    	    valueExpr();

                    	    state._fsp--;


                    	    }
                    	    break;

                    	default :
                    	    break loop233;
                        }
                    } while (true);

                    if ( input.LA(1)==RBRACK||input.LA(1)==RPAREN ) {
                        input.consume();
                        state.errorRecovery=false;
                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        throw mse;
                    }


                    match(input, Token.UP, null); 
                     leaveNode(i); 

                    }
                    break;
                case 2 :
                    // EsperEPL2Ast.g:687:4: ^(i= NOT_IN_SET valueExpr ( LPAREN | LBRACK ) valueExpr ( valueExpr )* ( RPAREN | RBRACK ) )
                    {
                    i=(CommonTree)match(input,NOT_IN_SET,FOLLOW_NOT_IN_SET_in_inExpr4445); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_valueExpr_in_inExpr4447);
                    valueExpr();

                    state._fsp--;

                    if ( input.LA(1)==LBRACK||input.LA(1)==LPAREN ) {
                        input.consume();
                        state.errorRecovery=false;
                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        throw mse;
                    }

                    pushFollow(FOLLOW_valueExpr_in_inExpr4455);
                    valueExpr();

                    state._fsp--;

                    // EsperEPL2Ast.g:687:55: ( valueExpr )*
                    loop234:
                    do {
                        int alt234=2;
                        int LA234_0 = input.LA(1);

                        if ( ((LA234_0>=IN_SET && LA234_0<=REGEXP)||LA234_0==NOT_EXPR||(LA234_0>=SUM && LA234_0<=AVG)||(LA234_0>=COALESCE && LA234_0<=COUNT)||(LA234_0>=CASE && LA234_0<=CASE2)||LA234_0==ISTREAM||(LA234_0>=PREVIOUS && LA234_0<=EXISTS)||(LA234_0>=INSTANCEOF && LA234_0<=CURRENT_TIMESTAMP)||LA234_0==NEWKW||(LA234_0>=EVAL_AND_EXPR && LA234_0<=EVAL_NOTEQUALS_GROUP_EXPR)||LA234_0==EVENT_PROP_EXPR||LA234_0==CONCAT||(LA234_0>=LIB_FUNC_CHAIN && LA234_0<=DOT_EXPR)||LA234_0==ARRAY_EXPR||(LA234_0>=NOT_IN_SET && LA234_0<=NOT_REGEXP)||(LA234_0>=IN_RANGE && LA234_0<=SUBSELECT_EXPR)||(LA234_0>=EXISTS_SUBSELECT_EXPR && LA234_0<=NOT_IN_SUBSELECT_EXPR)||LA234_0==SUBSTITUTION||(LA234_0>=FIRST_AGGREG && LA234_0<=WINDOW_AGGREG)||(LA234_0>=INT_TYPE && LA234_0<=NULL_TYPE)||(LA234_0>=JSON_OBJECT && LA234_0<=JSON_ARRAY)||LA234_0==STAR||(LA234_0>=LT && LA234_0<=GT)||(LA234_0>=BOR && LA234_0<=PLUS)||(LA234_0>=BAND && LA234_0<=BXOR)||(LA234_0>=LE && LA234_0<=GE)||(LA234_0>=MINUS && LA234_0<=MOD)||(LA234_0>=EVAL_IS_GROUP_EXPR && LA234_0<=EVAL_ISNOT_GROUP_EXPR)) ) {
                            alt234=1;
                        }


                        switch (alt234) {
                    	case 1 :
                    	    // EsperEPL2Ast.g:687:56: valueExpr
                    	    {
                    	    pushFollow(FOLLOW_valueExpr_in_inExpr4458);
                    	    valueExpr();

                    	    state._fsp--;


                    	    }
                    	    break;

                    	default :
                    	    break loop234;
                        }
                    } while (true);

                    if ( input.LA(1)==RBRACK||input.LA(1)==RPAREN ) {
                        input.consume();
                        state.errorRecovery=false;
                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        throw mse;
                    }


                    match(input, Token.UP, null); 
                     leaveNode(i); 

                    }
                    break;
                case 3 :
                    // EsperEPL2Ast.g:688:4: ^(i= IN_RANGE valueExpr ( LPAREN | LBRACK ) valueExpr valueExpr ( RPAREN | RBRACK ) )
                    {
                    i=(CommonTree)match(input,IN_RANGE,FOLLOW_IN_RANGE_in_inExpr4477); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_valueExpr_in_inExpr4479);
                    valueExpr();

                    state._fsp--;

                    if ( input.LA(1)==LBRACK||input.LA(1)==LPAREN ) {
                        input.consume();
                        state.errorRecovery=false;
                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        throw mse;
                    }

                    pushFollow(FOLLOW_valueExpr_in_inExpr4487);
                    valueExpr();

                    state._fsp--;

                    pushFollow(FOLLOW_valueExpr_in_inExpr4489);
                    valueExpr();

                    state._fsp--;

                    if ( input.LA(1)==RBRACK||input.LA(1)==RPAREN ) {
                        input.consume();
                        state.errorRecovery=false;
                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        throw mse;
                    }


                    match(input, Token.UP, null); 
                     leaveNode(i); 

                    }
                    break;
                case 4 :
                    // EsperEPL2Ast.g:689:4: ^(i= NOT_IN_RANGE valueExpr ( LPAREN | LBRACK ) valueExpr valueExpr ( RPAREN | RBRACK ) )
                    {
                    i=(CommonTree)match(input,NOT_IN_RANGE,FOLLOW_NOT_IN_RANGE_in_inExpr4506); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_valueExpr_in_inExpr4508);
                    valueExpr();

                    state._fsp--;

                    if ( input.LA(1)==LBRACK||input.LA(1)==LPAREN ) {
                        input.consume();
                        state.errorRecovery=false;
                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        throw mse;
                    }

                    pushFollow(FOLLOW_valueExpr_in_inExpr4516);
                    valueExpr();

                    state._fsp--;

                    pushFollow(FOLLOW_valueExpr_in_inExpr4518);
                    valueExpr();

                    state._fsp--;

                    if ( input.LA(1)==RBRACK||input.LA(1)==RPAREN ) {
                        input.consume();
                        state.errorRecovery=false;
                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        throw mse;
                    }


                    match(input, Token.UP, null); 
                     leaveNode(i); 

                    }
                    break;

            }
        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "inExpr"


    // $ANTLR start "betweenExpr"
    // EsperEPL2Ast.g:692:1: betweenExpr : ( ^(b= BETWEEN valueExpr valueExpr valueExpr ) | ^(b= NOT_BETWEEN valueExpr valueExpr ( valueExpr )* ) );
    public final void betweenExpr() throws RecognitionException {
        CommonTree b=null;

        try {
            // EsperEPL2Ast.g:693:2: ( ^(b= BETWEEN valueExpr valueExpr valueExpr ) | ^(b= NOT_BETWEEN valueExpr valueExpr ( valueExpr )* ) )
            int alt237=2;
            int LA237_0 = input.LA(1);

            if ( (LA237_0==BETWEEN) ) {
                alt237=1;
            }
            else if ( (LA237_0==NOT_BETWEEN) ) {
                alt237=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 237, 0, input);

                throw nvae;
            }
            switch (alt237) {
                case 1 :
                    // EsperEPL2Ast.g:693:4: ^(b= BETWEEN valueExpr valueExpr valueExpr )
                    {
                    b=(CommonTree)match(input,BETWEEN,FOLLOW_BETWEEN_in_betweenExpr4543); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_valueExpr_in_betweenExpr4545);
                    valueExpr();

                    state._fsp--;

                    pushFollow(FOLLOW_valueExpr_in_betweenExpr4547);
                    valueExpr();

                    state._fsp--;

                    pushFollow(FOLLOW_valueExpr_in_betweenExpr4549);
                    valueExpr();

                    state._fsp--;


                    match(input, Token.UP, null); 
                     leaveNode(b); 

                    }
                    break;
                case 2 :
                    // EsperEPL2Ast.g:694:4: ^(b= NOT_BETWEEN valueExpr valueExpr ( valueExpr )* )
                    {
                    b=(CommonTree)match(input,NOT_BETWEEN,FOLLOW_NOT_BETWEEN_in_betweenExpr4560); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_valueExpr_in_betweenExpr4562);
                    valueExpr();

                    state._fsp--;

                    pushFollow(FOLLOW_valueExpr_in_betweenExpr4564);
                    valueExpr();

                    state._fsp--;

                    // EsperEPL2Ast.g:694:40: ( valueExpr )*
                    loop236:
                    do {
                        int alt236=2;
                        int LA236_0 = input.LA(1);

                        if ( ((LA236_0>=IN_SET && LA236_0<=REGEXP)||LA236_0==NOT_EXPR||(LA236_0>=SUM && LA236_0<=AVG)||(LA236_0>=COALESCE && LA236_0<=COUNT)||(LA236_0>=CASE && LA236_0<=CASE2)||LA236_0==ISTREAM||(LA236_0>=PREVIOUS && LA236_0<=EXISTS)||(LA236_0>=INSTANCEOF && LA236_0<=CURRENT_TIMESTAMP)||LA236_0==NEWKW||(LA236_0>=EVAL_AND_EXPR && LA236_0<=EVAL_NOTEQUALS_GROUP_EXPR)||LA236_0==EVENT_PROP_EXPR||LA236_0==CONCAT||(LA236_0>=LIB_FUNC_CHAIN && LA236_0<=DOT_EXPR)||LA236_0==ARRAY_EXPR||(LA236_0>=NOT_IN_SET && LA236_0<=NOT_REGEXP)||(LA236_0>=IN_RANGE && LA236_0<=SUBSELECT_EXPR)||(LA236_0>=EXISTS_SUBSELECT_EXPR && LA236_0<=NOT_IN_SUBSELECT_EXPR)||LA236_0==SUBSTITUTION||(LA236_0>=FIRST_AGGREG && LA236_0<=WINDOW_AGGREG)||(LA236_0>=INT_TYPE && LA236_0<=NULL_TYPE)||(LA236_0>=JSON_OBJECT && LA236_0<=JSON_ARRAY)||LA236_0==STAR||(LA236_0>=LT && LA236_0<=GT)||(LA236_0>=BOR && LA236_0<=PLUS)||(LA236_0>=BAND && LA236_0<=BXOR)||(LA236_0>=LE && LA236_0<=GE)||(LA236_0>=MINUS && LA236_0<=MOD)||(LA236_0>=EVAL_IS_GROUP_EXPR && LA236_0<=EVAL_ISNOT_GROUP_EXPR)) ) {
                            alt236=1;
                        }


                        switch (alt236) {
                    	case 1 :
                    	    // EsperEPL2Ast.g:694:41: valueExpr
                    	    {
                    	    pushFollow(FOLLOW_valueExpr_in_betweenExpr4567);
                    	    valueExpr();

                    	    state._fsp--;


                    	    }
                    	    break;

                    	default :
                    	    break loop236;
                        }
                    } while (true);


                    match(input, Token.UP, null); 
                     leaveNode(b); 

                    }
                    break;

            }
        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "betweenExpr"


    // $ANTLR start "likeExpr"
    // EsperEPL2Ast.g:697:1: likeExpr : ( ^(l= LIKE valueExpr valueExpr ( valueExpr )? ) | ^(l= NOT_LIKE valueExpr valueExpr ( valueExpr )? ) );
    public final void likeExpr() throws RecognitionException {
        CommonTree l=null;

        try {
            // EsperEPL2Ast.g:698:2: ( ^(l= LIKE valueExpr valueExpr ( valueExpr )? ) | ^(l= NOT_LIKE valueExpr valueExpr ( valueExpr )? ) )
            int alt240=2;
            int LA240_0 = input.LA(1);

            if ( (LA240_0==LIKE) ) {
                alt240=1;
            }
            else if ( (LA240_0==NOT_LIKE) ) {
                alt240=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 240, 0, input);

                throw nvae;
            }
            switch (alt240) {
                case 1 :
                    // EsperEPL2Ast.g:698:4: ^(l= LIKE valueExpr valueExpr ( valueExpr )? )
                    {
                    l=(CommonTree)match(input,LIKE,FOLLOW_LIKE_in_likeExpr4587); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_valueExpr_in_likeExpr4589);
                    valueExpr();

                    state._fsp--;

                    pushFollow(FOLLOW_valueExpr_in_likeExpr4591);
                    valueExpr();

                    state._fsp--;

                    // EsperEPL2Ast.g:698:33: ( valueExpr )?
                    int alt238=2;
                    int LA238_0 = input.LA(1);

                    if ( ((LA238_0>=IN_SET && LA238_0<=REGEXP)||LA238_0==NOT_EXPR||(LA238_0>=SUM && LA238_0<=AVG)||(LA238_0>=COALESCE && LA238_0<=COUNT)||(LA238_0>=CASE && LA238_0<=CASE2)||LA238_0==ISTREAM||(LA238_0>=PREVIOUS && LA238_0<=EXISTS)||(LA238_0>=INSTANCEOF && LA238_0<=CURRENT_TIMESTAMP)||LA238_0==NEWKW||(LA238_0>=EVAL_AND_EXPR && LA238_0<=EVAL_NOTEQUALS_GROUP_EXPR)||LA238_0==EVENT_PROP_EXPR||LA238_0==CONCAT||(LA238_0>=LIB_FUNC_CHAIN && LA238_0<=DOT_EXPR)||LA238_0==ARRAY_EXPR||(LA238_0>=NOT_IN_SET && LA238_0<=NOT_REGEXP)||(LA238_0>=IN_RANGE && LA238_0<=SUBSELECT_EXPR)||(LA238_0>=EXISTS_SUBSELECT_EXPR && LA238_0<=NOT_IN_SUBSELECT_EXPR)||LA238_0==SUBSTITUTION||(LA238_0>=FIRST_AGGREG && LA238_0<=WINDOW_AGGREG)||(LA238_0>=INT_TYPE && LA238_0<=NULL_TYPE)||(LA238_0>=JSON_OBJECT && LA238_0<=JSON_ARRAY)||LA238_0==STAR||(LA238_0>=LT && LA238_0<=GT)||(LA238_0>=BOR && LA238_0<=PLUS)||(LA238_0>=BAND && LA238_0<=BXOR)||(LA238_0>=LE && LA238_0<=GE)||(LA238_0>=MINUS && LA238_0<=MOD)||(LA238_0>=EVAL_IS_GROUP_EXPR && LA238_0<=EVAL_ISNOT_GROUP_EXPR)) ) {
                        alt238=1;
                    }
                    switch (alt238) {
                        case 1 :
                            // EsperEPL2Ast.g:698:34: valueExpr
                            {
                            pushFollow(FOLLOW_valueExpr_in_likeExpr4594);
                            valueExpr();

                            state._fsp--;


                            }
                            break;

                    }


                    match(input, Token.UP, null); 
                     leaveNode(l); 

                    }
                    break;
                case 2 :
                    // EsperEPL2Ast.g:699:4: ^(l= NOT_LIKE valueExpr valueExpr ( valueExpr )? )
                    {
                    l=(CommonTree)match(input,NOT_LIKE,FOLLOW_NOT_LIKE_in_likeExpr4607); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_valueExpr_in_likeExpr4609);
                    valueExpr();

                    state._fsp--;

                    pushFollow(FOLLOW_valueExpr_in_likeExpr4611);
                    valueExpr();

                    state._fsp--;

                    // EsperEPL2Ast.g:699:37: ( valueExpr )?
                    int alt239=2;
                    int LA239_0 = input.LA(1);

                    if ( ((LA239_0>=IN_SET && LA239_0<=REGEXP)||LA239_0==NOT_EXPR||(LA239_0>=SUM && LA239_0<=AVG)||(LA239_0>=COALESCE && LA239_0<=COUNT)||(LA239_0>=CASE && LA239_0<=CASE2)||LA239_0==ISTREAM||(LA239_0>=PREVIOUS && LA239_0<=EXISTS)||(LA239_0>=INSTANCEOF && LA239_0<=CURRENT_TIMESTAMP)||LA239_0==NEWKW||(LA239_0>=EVAL_AND_EXPR && LA239_0<=EVAL_NOTEQUALS_GROUP_EXPR)||LA239_0==EVENT_PROP_EXPR||LA239_0==CONCAT||(LA239_0>=LIB_FUNC_CHAIN && LA239_0<=DOT_EXPR)||LA239_0==ARRAY_EXPR||(LA239_0>=NOT_IN_SET && LA239_0<=NOT_REGEXP)||(LA239_0>=IN_RANGE && LA239_0<=SUBSELECT_EXPR)||(LA239_0>=EXISTS_SUBSELECT_EXPR && LA239_0<=NOT_IN_SUBSELECT_EXPR)||LA239_0==SUBSTITUTION||(LA239_0>=FIRST_AGGREG && LA239_0<=WINDOW_AGGREG)||(LA239_0>=INT_TYPE && LA239_0<=NULL_TYPE)||(LA239_0>=JSON_OBJECT && LA239_0<=JSON_ARRAY)||LA239_0==STAR||(LA239_0>=LT && LA239_0<=GT)||(LA239_0>=BOR && LA239_0<=PLUS)||(LA239_0>=BAND && LA239_0<=BXOR)||(LA239_0>=LE && LA239_0<=GE)||(LA239_0>=MINUS && LA239_0<=MOD)||(LA239_0>=EVAL_IS_GROUP_EXPR && LA239_0<=EVAL_ISNOT_GROUP_EXPR)) ) {
                        alt239=1;
                    }
                    switch (alt239) {
                        case 1 :
                            // EsperEPL2Ast.g:699:38: valueExpr
                            {
                            pushFollow(FOLLOW_valueExpr_in_likeExpr4614);
                            valueExpr();

                            state._fsp--;


                            }
                            break;

                    }


                    match(input, Token.UP, null); 
                     leaveNode(l); 

                    }
                    break;

            }
        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "likeExpr"


    // $ANTLR start "regExpExpr"
    // EsperEPL2Ast.g:702:1: regExpExpr : ( ^(r= REGEXP valueExpr valueExpr ) | ^(r= NOT_REGEXP valueExpr valueExpr ) );
    public final void regExpExpr() throws RecognitionException {
        CommonTree r=null;

        try {
            // EsperEPL2Ast.g:703:2: ( ^(r= REGEXP valueExpr valueExpr ) | ^(r= NOT_REGEXP valueExpr valueExpr ) )
            int alt241=2;
            int LA241_0 = input.LA(1);

            if ( (LA241_0==REGEXP) ) {
                alt241=1;
            }
            else if ( (LA241_0==NOT_REGEXP) ) {
                alt241=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 241, 0, input);

                throw nvae;
            }
            switch (alt241) {
                case 1 :
                    // EsperEPL2Ast.g:703:4: ^(r= REGEXP valueExpr valueExpr )
                    {
                    r=(CommonTree)match(input,REGEXP,FOLLOW_REGEXP_in_regExpExpr4633); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_valueExpr_in_regExpExpr4635);
                    valueExpr();

                    state._fsp--;

                    pushFollow(FOLLOW_valueExpr_in_regExpExpr4637);
                    valueExpr();

                    state._fsp--;


                    match(input, Token.UP, null); 
                     leaveNode(r); 

                    }
                    break;
                case 2 :
                    // EsperEPL2Ast.g:704:4: ^(r= NOT_REGEXP valueExpr valueExpr )
                    {
                    r=(CommonTree)match(input,NOT_REGEXP,FOLLOW_NOT_REGEXP_in_regExpExpr4648); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_valueExpr_in_regExpExpr4650);
                    valueExpr();

                    state._fsp--;

                    pushFollow(FOLLOW_valueExpr_in_regExpExpr4652);
                    valueExpr();

                    state._fsp--;


                    match(input, Token.UP, null); 
                     leaveNode(r); 

                    }
                    break;

            }
        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "regExpExpr"


    // $ANTLR start "builtinFunc"
    // EsperEPL2Ast.g:707:1: builtinFunc : ( ^(f= SUM ( DISTINCT )? valueExpr ( aggregationFilterExpr )? ) | ^(f= AVG ( DISTINCT )? valueExpr ( aggregationFilterExpr )? ) | ^(f= COUNT ( ( DISTINCT )? valueExpr )? ( aggregationFilterExpr )? ) | ^(f= MEDIAN ( DISTINCT )? valueExpr ( aggregationFilterExpr )? ) | ^(f= STDDEV ( DISTINCT )? valueExpr ( aggregationFilterExpr )? ) | ^(f= AVEDEV ( DISTINCT )? valueExpr ( aggregationFilterExpr )? ) | ^(f= LAST_AGGREG ( DISTINCT )? ( accessValueExpr )? ( valueExpr )? ) | ^(f= FIRST_AGGREG ( DISTINCT )? ( accessValueExpr )? ( valueExpr )? ) | ^(f= WINDOW_AGGREG ( DISTINCT )? ( accessValueExpr )? ) | ^(f= COALESCE valueExpr valueExpr ( valueExpr )* ) | ^(f= PREVIOUS valueExpr ( valueExpr )? ) | ^(f= PREVIOUSTAIL valueExpr ( valueExpr )? ) | ^(f= PREVIOUSCOUNT valueExpr ) | ^(f= PREVIOUSWINDOW valueExpr ) | ^(f= PRIOR c= NUM_INT eventPropertyExpr[true] ) | ^(f= INSTANCEOF valueExpr CLASS_IDENT ( CLASS_IDENT )* ) | ^(f= TYPEOF valueExpr ) | ^(f= CAST valueExpr CLASS_IDENT ) | ^(f= EXISTS eventPropertyExpr[true] ) | ^(f= CURRENT_TIMESTAMP ) | ^(f= ISTREAM ) );
    public final void builtinFunc() throws RecognitionException {
        CommonTree f=null;
        CommonTree c=null;

        try {
            // EsperEPL2Ast.g:708:2: ( ^(f= SUM ( DISTINCT )? valueExpr ( aggregationFilterExpr )? ) | ^(f= AVG ( DISTINCT )? valueExpr ( aggregationFilterExpr )? ) | ^(f= COUNT ( ( DISTINCT )? valueExpr )? ( aggregationFilterExpr )? ) | ^(f= MEDIAN ( DISTINCT )? valueExpr ( aggregationFilterExpr )? ) | ^(f= STDDEV ( DISTINCT )? valueExpr ( aggregationFilterExpr )? ) | ^(f= AVEDEV ( DISTINCT )? valueExpr ( aggregationFilterExpr )? ) | ^(f= LAST_AGGREG ( DISTINCT )? ( accessValueExpr )? ( valueExpr )? ) | ^(f= FIRST_AGGREG ( DISTINCT )? ( accessValueExpr )? ( valueExpr )? ) | ^(f= WINDOW_AGGREG ( DISTINCT )? ( accessValueExpr )? ) | ^(f= COALESCE valueExpr valueExpr ( valueExpr )* ) | ^(f= PREVIOUS valueExpr ( valueExpr )? ) | ^(f= PREVIOUSTAIL valueExpr ( valueExpr )? ) | ^(f= PREVIOUSCOUNT valueExpr ) | ^(f= PREVIOUSWINDOW valueExpr ) | ^(f= PRIOR c= NUM_INT eventPropertyExpr[true] ) | ^(f= INSTANCEOF valueExpr CLASS_IDENT ( CLASS_IDENT )* ) | ^(f= TYPEOF valueExpr ) | ^(f= CAST valueExpr CLASS_IDENT ) | ^(f= EXISTS eventPropertyExpr[true] ) | ^(f= CURRENT_TIMESTAMP ) | ^(f= ISTREAM ) )
            int alt267=21;
            switch ( input.LA(1) ) {
            case SUM:
                {
                alt267=1;
                }
                break;
            case AVG:
                {
                alt267=2;
                }
                break;
            case COUNT:
                {
                alt267=3;
                }
                break;
            case MEDIAN:
                {
                alt267=4;
                }
                break;
            case STDDEV:
                {
                alt267=5;
                }
                break;
            case AVEDEV:
                {
                alt267=6;
                }
                break;
            case LAST_AGGREG:
                {
                alt267=7;
                }
                break;
            case FIRST_AGGREG:
                {
                alt267=8;
                }
                break;
            case WINDOW_AGGREG:
                {
                alt267=9;
                }
                break;
            case COALESCE:
                {
                alt267=10;
                }
                break;
            case PREVIOUS:
                {
                alt267=11;
                }
                break;
            case PREVIOUSTAIL:
                {
                alt267=12;
                }
                break;
            case PREVIOUSCOUNT:
                {
                alt267=13;
                }
                break;
            case PREVIOUSWINDOW:
                {
                alt267=14;
                }
                break;
            case PRIOR:
                {
                alt267=15;
                }
                break;
            case INSTANCEOF:
                {
                alt267=16;
                }
                break;
            case TYPEOF:
                {
                alt267=17;
                }
                break;
            case CAST:
                {
                alt267=18;
                }
                break;
            case EXISTS:
                {
                alt267=19;
                }
                break;
            case CURRENT_TIMESTAMP:
                {
                alt267=20;
                }
                break;
            case ISTREAM:
                {
                alt267=21;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 267, 0, input);

                throw nvae;
            }

            switch (alt267) {
                case 1 :
                    // EsperEPL2Ast.g:708:5: ^(f= SUM ( DISTINCT )? valueExpr ( aggregationFilterExpr )? )
                    {
                    f=(CommonTree)match(input,SUM,FOLLOW_SUM_in_builtinFunc4671); 

                    match(input, Token.DOWN, null); 
                    // EsperEPL2Ast.g:708:13: ( DISTINCT )?
                    int alt242=2;
                    int LA242_0 = input.LA(1);

                    if ( (LA242_0==DISTINCT) ) {
                        alt242=1;
                    }
                    switch (alt242) {
                        case 1 :
                            // EsperEPL2Ast.g:708:14: DISTINCT
                            {
                            match(input,DISTINCT,FOLLOW_DISTINCT_in_builtinFunc4674); 

                            }
                            break;

                    }

                    pushFollow(FOLLOW_valueExpr_in_builtinFunc4678);
                    valueExpr();

                    state._fsp--;

                    // EsperEPL2Ast.g:708:35: ( aggregationFilterExpr )?
                    int alt243=2;
                    int LA243_0 = input.LA(1);

                    if ( (LA243_0==AGG_FILTER_EXPR) ) {
                        alt243=1;
                    }
                    switch (alt243) {
                        case 1 :
                            // EsperEPL2Ast.g:708:35: aggregationFilterExpr
                            {
                            pushFollow(FOLLOW_aggregationFilterExpr_in_builtinFunc4680);
                            aggregationFilterExpr();

                            state._fsp--;


                            }
                            break;

                    }


                    match(input, Token.UP, null); 
                     leaveNode(f); 

                    }
                    break;
                case 2 :
                    // EsperEPL2Ast.g:709:4: ^(f= AVG ( DISTINCT )? valueExpr ( aggregationFilterExpr )? )
                    {
                    f=(CommonTree)match(input,AVG,FOLLOW_AVG_in_builtinFunc4692); 

                    match(input, Token.DOWN, null); 
                    // EsperEPL2Ast.g:709:12: ( DISTINCT )?
                    int alt244=2;
                    int LA244_0 = input.LA(1);

                    if ( (LA244_0==DISTINCT) ) {
                        alt244=1;
                    }
                    switch (alt244) {
                        case 1 :
                            // EsperEPL2Ast.g:709:13: DISTINCT
                            {
                            match(input,DISTINCT,FOLLOW_DISTINCT_in_builtinFunc4695); 

                            }
                            break;

                    }

                    pushFollow(FOLLOW_valueExpr_in_builtinFunc4699);
                    valueExpr();

                    state._fsp--;

                    // EsperEPL2Ast.g:709:34: ( aggregationFilterExpr )?
                    int alt245=2;
                    int LA245_0 = input.LA(1);

                    if ( (LA245_0==AGG_FILTER_EXPR) ) {
                        alt245=1;
                    }
                    switch (alt245) {
                        case 1 :
                            // EsperEPL2Ast.g:709:34: aggregationFilterExpr
                            {
                            pushFollow(FOLLOW_aggregationFilterExpr_in_builtinFunc4701);
                            aggregationFilterExpr();

                            state._fsp--;


                            }
                            break;

                    }


                    match(input, Token.UP, null); 
                     leaveNode(f); 

                    }
                    break;
                case 3 :
                    // EsperEPL2Ast.g:710:4: ^(f= COUNT ( ( DISTINCT )? valueExpr )? ( aggregationFilterExpr )? )
                    {
                    f=(CommonTree)match(input,COUNT,FOLLOW_COUNT_in_builtinFunc4713); 

                    if ( input.LA(1)==Token.DOWN ) {
                        match(input, Token.DOWN, null); 
                        // EsperEPL2Ast.g:710:14: ( ( DISTINCT )? valueExpr )?
                        int alt247=2;
                        int LA247_0 = input.LA(1);

                        if ( ((LA247_0>=IN_SET && LA247_0<=REGEXP)||LA247_0==NOT_EXPR||(LA247_0>=SUM && LA247_0<=AVG)||(LA247_0>=COALESCE && LA247_0<=COUNT)||(LA247_0>=CASE && LA247_0<=CASE2)||LA247_0==DISTINCT||LA247_0==ISTREAM||(LA247_0>=PREVIOUS && LA247_0<=EXISTS)||(LA247_0>=INSTANCEOF && LA247_0<=CURRENT_TIMESTAMP)||LA247_0==NEWKW||(LA247_0>=EVAL_AND_EXPR && LA247_0<=EVAL_NOTEQUALS_GROUP_EXPR)||LA247_0==EVENT_PROP_EXPR||LA247_0==CONCAT||(LA247_0>=LIB_FUNC_CHAIN && LA247_0<=DOT_EXPR)||LA247_0==ARRAY_EXPR||(LA247_0>=NOT_IN_SET && LA247_0<=NOT_REGEXP)||(LA247_0>=IN_RANGE && LA247_0<=SUBSELECT_EXPR)||(LA247_0>=EXISTS_SUBSELECT_EXPR && LA247_0<=NOT_IN_SUBSELECT_EXPR)||LA247_0==SUBSTITUTION||(LA247_0>=FIRST_AGGREG && LA247_0<=WINDOW_AGGREG)||(LA247_0>=INT_TYPE && LA247_0<=NULL_TYPE)||(LA247_0>=JSON_OBJECT && LA247_0<=JSON_ARRAY)||LA247_0==STAR||(LA247_0>=LT && LA247_0<=GT)||(LA247_0>=BOR && LA247_0<=PLUS)||(LA247_0>=BAND && LA247_0<=BXOR)||(LA247_0>=LE && LA247_0<=GE)||(LA247_0>=MINUS && LA247_0<=MOD)||(LA247_0>=EVAL_IS_GROUP_EXPR && LA247_0<=EVAL_ISNOT_GROUP_EXPR)) ) {
                            alt247=1;
                        }
                        switch (alt247) {
                            case 1 :
                                // EsperEPL2Ast.g:710:15: ( DISTINCT )? valueExpr
                                {
                                // EsperEPL2Ast.g:710:15: ( DISTINCT )?
                                int alt246=2;
                                int LA246_0 = input.LA(1);

                                if ( (LA246_0==DISTINCT) ) {
                                    alt246=1;
                                }
                                switch (alt246) {
                                    case 1 :
                                        // EsperEPL2Ast.g:710:16: DISTINCT
                                        {
                                        match(input,DISTINCT,FOLLOW_DISTINCT_in_builtinFunc4717); 

                                        }
                                        break;

                                }

                                pushFollow(FOLLOW_valueExpr_in_builtinFunc4721);
                                valueExpr();

                                state._fsp--;


                                }
                                break;

                        }

                        // EsperEPL2Ast.g:710:39: ( aggregationFilterExpr )?
                        int alt248=2;
                        int LA248_0 = input.LA(1);

                        if ( (LA248_0==AGG_FILTER_EXPR) ) {
                            alt248=1;
                        }
                        switch (alt248) {
                            case 1 :
                                // EsperEPL2Ast.g:710:39: aggregationFilterExpr
                                {
                                pushFollow(FOLLOW_aggregationFilterExpr_in_builtinFunc4725);
                                aggregationFilterExpr();

                                state._fsp--;


                                }
                                break;

                        }


                        match(input, Token.UP, null); 
                    }
                     leaveNode(f); 

                    }
                    break;
                case 4 :
                    // EsperEPL2Ast.g:711:4: ^(f= MEDIAN ( DISTINCT )? valueExpr ( aggregationFilterExpr )? )
                    {
                    f=(CommonTree)match(input,MEDIAN,FOLLOW_MEDIAN_in_builtinFunc4737); 

                    match(input, Token.DOWN, null); 
                    // EsperEPL2Ast.g:711:15: ( DISTINCT )?
                    int alt249=2;
                    int LA249_0 = input.LA(1);

                    if ( (LA249_0==DISTINCT) ) {
                        alt249=1;
                    }
                    switch (alt249) {
                        case 1 :
                            // EsperEPL2Ast.g:711:16: DISTINCT
                            {
                            match(input,DISTINCT,FOLLOW_DISTINCT_in_builtinFunc4740); 

                            }
                            break;

                    }

                    pushFollow(FOLLOW_valueExpr_in_builtinFunc4744);
                    valueExpr();

                    state._fsp--;

                    // EsperEPL2Ast.g:711:37: ( aggregationFilterExpr )?
                    int alt250=2;
                    int LA250_0 = input.LA(1);

                    if ( (LA250_0==AGG_FILTER_EXPR) ) {
                        alt250=1;
                    }
                    switch (alt250) {
                        case 1 :
                            // EsperEPL2Ast.g:711:37: aggregationFilterExpr
                            {
                            pushFollow(FOLLOW_aggregationFilterExpr_in_builtinFunc4746);
                            aggregationFilterExpr();

                            state._fsp--;


                            }
                            break;

                    }


                    match(input, Token.UP, null); 
                     leaveNode(f); 

                    }
                    break;
                case 5 :
                    // EsperEPL2Ast.g:712:4: ^(f= STDDEV ( DISTINCT )? valueExpr ( aggregationFilterExpr )? )
                    {
                    f=(CommonTree)match(input,STDDEV,FOLLOW_STDDEV_in_builtinFunc4758); 

                    match(input, Token.DOWN, null); 
                    // EsperEPL2Ast.g:712:15: ( DISTINCT )?
                    int alt251=2;
                    int LA251_0 = input.LA(1);

                    if ( (LA251_0==DISTINCT) ) {
                        alt251=1;
                    }
                    switch (alt251) {
                        case 1 :
                            // EsperEPL2Ast.g:712:16: DISTINCT
                            {
                            match(input,DISTINCT,FOLLOW_DISTINCT_in_builtinFunc4761); 

                            }
                            break;

                    }

                    pushFollow(FOLLOW_valueExpr_in_builtinFunc4765);
                    valueExpr();

                    state._fsp--;

                    // EsperEPL2Ast.g:712:37: ( aggregationFilterExpr )?
                    int alt252=2;
                    int LA252_0 = input.LA(1);

                    if ( (LA252_0==AGG_FILTER_EXPR) ) {
                        alt252=1;
                    }
                    switch (alt252) {
                        case 1 :
                            // EsperEPL2Ast.g:712:37: aggregationFilterExpr
                            {
                            pushFollow(FOLLOW_aggregationFilterExpr_in_builtinFunc4767);
                            aggregationFilterExpr();

                            state._fsp--;


                            }
                            break;

                    }


                    match(input, Token.UP, null); 
                     leaveNode(f); 

                    }
                    break;
                case 6 :
                    // EsperEPL2Ast.g:713:4: ^(f= AVEDEV ( DISTINCT )? valueExpr ( aggregationFilterExpr )? )
                    {
                    f=(CommonTree)match(input,AVEDEV,FOLLOW_AVEDEV_in_builtinFunc4779); 

                    match(input, Token.DOWN, null); 
                    // EsperEPL2Ast.g:713:15: ( DISTINCT )?
                    int alt253=2;
                    int LA253_0 = input.LA(1);

                    if ( (LA253_0==DISTINCT) ) {
                        alt253=1;
                    }
                    switch (alt253) {
                        case 1 :
                            // EsperEPL2Ast.g:713:16: DISTINCT
                            {
                            match(input,DISTINCT,FOLLOW_DISTINCT_in_builtinFunc4782); 

                            }
                            break;

                    }

                    pushFollow(FOLLOW_valueExpr_in_builtinFunc4786);
                    valueExpr();

                    state._fsp--;

                    // EsperEPL2Ast.g:713:37: ( aggregationFilterExpr )?
                    int alt254=2;
                    int LA254_0 = input.LA(1);

                    if ( (LA254_0==AGG_FILTER_EXPR) ) {
                        alt254=1;
                    }
                    switch (alt254) {
                        case 1 :
                            // EsperEPL2Ast.g:713:37: aggregationFilterExpr
                            {
                            pushFollow(FOLLOW_aggregationFilterExpr_in_builtinFunc4788);
                            aggregationFilterExpr();

                            state._fsp--;


                            }
                            break;

                    }


                    match(input, Token.UP, null); 
                     leaveNode(f); 

                    }
                    break;
                case 7 :
                    // EsperEPL2Ast.g:714:4: ^(f= LAST_AGGREG ( DISTINCT )? ( accessValueExpr )? ( valueExpr )? )
                    {
                    f=(CommonTree)match(input,LAST_AGGREG,FOLLOW_LAST_AGGREG_in_builtinFunc4800); 

                    if ( input.LA(1)==Token.DOWN ) {
                        match(input, Token.DOWN, null); 
                        // EsperEPL2Ast.g:714:20: ( DISTINCT )?
                        int alt255=2;
                        int LA255_0 = input.LA(1);

                        if ( (LA255_0==DISTINCT) ) {
                            alt255=1;
                        }
                        switch (alt255) {
                            case 1 :
                                // EsperEPL2Ast.g:714:21: DISTINCT
                                {
                                match(input,DISTINCT,FOLLOW_DISTINCT_in_builtinFunc4803); 

                                }
                                break;

                        }

                        // EsperEPL2Ast.g:714:32: ( accessValueExpr )?
                        int alt256=2;
                        int LA256_0 = input.LA(1);

                        if ( (LA256_0==ACCESS_AGG) ) {
                            alt256=1;
                        }
                        switch (alt256) {
                            case 1 :
                                // EsperEPL2Ast.g:714:32: accessValueExpr
                                {
                                pushFollow(FOLLOW_accessValueExpr_in_builtinFunc4807);
                                accessValueExpr();

                                state._fsp--;


                                }
                                break;

                        }

                        // EsperEPL2Ast.g:714:49: ( valueExpr )?
                        int alt257=2;
                        int LA257_0 = input.LA(1);

                        if ( ((LA257_0>=IN_SET && LA257_0<=REGEXP)||LA257_0==NOT_EXPR||(LA257_0>=SUM && LA257_0<=AVG)||(LA257_0>=COALESCE && LA257_0<=COUNT)||(LA257_0>=CASE && LA257_0<=CASE2)||LA257_0==ISTREAM||(LA257_0>=PREVIOUS && LA257_0<=EXISTS)||(LA257_0>=INSTANCEOF && LA257_0<=CURRENT_TIMESTAMP)||LA257_0==NEWKW||(LA257_0>=EVAL_AND_EXPR && LA257_0<=EVAL_NOTEQUALS_GROUP_EXPR)||LA257_0==EVENT_PROP_EXPR||LA257_0==CONCAT||(LA257_0>=LIB_FUNC_CHAIN && LA257_0<=DOT_EXPR)||LA257_0==ARRAY_EXPR||(LA257_0>=NOT_IN_SET && LA257_0<=NOT_REGEXP)||(LA257_0>=IN_RANGE && LA257_0<=SUBSELECT_EXPR)||(LA257_0>=EXISTS_SUBSELECT_EXPR && LA257_0<=NOT_IN_SUBSELECT_EXPR)||LA257_0==SUBSTITUTION||(LA257_0>=FIRST_AGGREG && LA257_0<=WINDOW_AGGREG)||(LA257_0>=INT_TYPE && LA257_0<=NULL_TYPE)||(LA257_0>=JSON_OBJECT && LA257_0<=JSON_ARRAY)||LA257_0==STAR||(LA257_0>=LT && LA257_0<=GT)||(LA257_0>=BOR && LA257_0<=PLUS)||(LA257_0>=BAND && LA257_0<=BXOR)||(LA257_0>=LE && LA257_0<=GE)||(LA257_0>=MINUS && LA257_0<=MOD)||(LA257_0>=EVAL_IS_GROUP_EXPR && LA257_0<=EVAL_ISNOT_GROUP_EXPR)) ) {
                            alt257=1;
                        }
                        switch (alt257) {
                            case 1 :
                                // EsperEPL2Ast.g:714:49: valueExpr
                                {
                                pushFollow(FOLLOW_valueExpr_in_builtinFunc4810);
                                valueExpr();

                                state._fsp--;


                                }
                                break;

                        }


                        match(input, Token.UP, null); 
                    }
                     leaveNode(f); 

                    }
                    break;
                case 8 :
                    // EsperEPL2Ast.g:715:4: ^(f= FIRST_AGGREG ( DISTINCT )? ( accessValueExpr )? ( valueExpr )? )
                    {
                    f=(CommonTree)match(input,FIRST_AGGREG,FOLLOW_FIRST_AGGREG_in_builtinFunc4822); 

                    if ( input.LA(1)==Token.DOWN ) {
                        match(input, Token.DOWN, null); 
                        // EsperEPL2Ast.g:715:21: ( DISTINCT )?
                        int alt258=2;
                        int LA258_0 = input.LA(1);

                        if ( (LA258_0==DISTINCT) ) {
                            alt258=1;
                        }
                        switch (alt258) {
                            case 1 :
                                // EsperEPL2Ast.g:715:22: DISTINCT
                                {
                                match(input,DISTINCT,FOLLOW_DISTINCT_in_builtinFunc4825); 

                                }
                                break;

                        }

                        // EsperEPL2Ast.g:715:33: ( accessValueExpr )?
                        int alt259=2;
                        int LA259_0 = input.LA(1);

                        if ( (LA259_0==ACCESS_AGG) ) {
                            alt259=1;
                        }
                        switch (alt259) {
                            case 1 :
                                // EsperEPL2Ast.g:715:33: accessValueExpr
                                {
                                pushFollow(FOLLOW_accessValueExpr_in_builtinFunc4829);
                                accessValueExpr();

                                state._fsp--;


                                }
                                break;

                        }

                        // EsperEPL2Ast.g:715:50: ( valueExpr )?
                        int alt260=2;
                        int LA260_0 = input.LA(1);

                        if ( ((LA260_0>=IN_SET && LA260_0<=REGEXP)||LA260_0==NOT_EXPR||(LA260_0>=SUM && LA260_0<=AVG)||(LA260_0>=COALESCE && LA260_0<=COUNT)||(LA260_0>=CASE && LA260_0<=CASE2)||LA260_0==ISTREAM||(LA260_0>=PREVIOUS && LA260_0<=EXISTS)||(LA260_0>=INSTANCEOF && LA260_0<=CURRENT_TIMESTAMP)||LA260_0==NEWKW||(LA260_0>=EVAL_AND_EXPR && LA260_0<=EVAL_NOTEQUALS_GROUP_EXPR)||LA260_0==EVENT_PROP_EXPR||LA260_0==CONCAT||(LA260_0>=LIB_FUNC_CHAIN && LA260_0<=DOT_EXPR)||LA260_0==ARRAY_EXPR||(LA260_0>=NOT_IN_SET && LA260_0<=NOT_REGEXP)||(LA260_0>=IN_RANGE && LA260_0<=SUBSELECT_EXPR)||(LA260_0>=EXISTS_SUBSELECT_EXPR && LA260_0<=NOT_IN_SUBSELECT_EXPR)||LA260_0==SUBSTITUTION||(LA260_0>=FIRST_AGGREG && LA260_0<=WINDOW_AGGREG)||(LA260_0>=INT_TYPE && LA260_0<=NULL_TYPE)||(LA260_0>=JSON_OBJECT && LA260_0<=JSON_ARRAY)||LA260_0==STAR||(LA260_0>=LT && LA260_0<=GT)||(LA260_0>=BOR && LA260_0<=PLUS)||(LA260_0>=BAND && LA260_0<=BXOR)||(LA260_0>=LE && LA260_0<=GE)||(LA260_0>=MINUS && LA260_0<=MOD)||(LA260_0>=EVAL_IS_GROUP_EXPR && LA260_0<=EVAL_ISNOT_GROUP_EXPR)) ) {
                            alt260=1;
                        }
                        switch (alt260) {
                            case 1 :
                                // EsperEPL2Ast.g:715:50: valueExpr
                                {
                                pushFollow(FOLLOW_valueExpr_in_builtinFunc4832);
                                valueExpr();

                                state._fsp--;


                                }
                                break;

                        }


                        match(input, Token.UP, null); 
                    }
                     leaveNode(f); 

                    }
                    break;
                case 9 :
                    // EsperEPL2Ast.g:716:4: ^(f= WINDOW_AGGREG ( DISTINCT )? ( accessValueExpr )? )
                    {
                    f=(CommonTree)match(input,WINDOW_AGGREG,FOLLOW_WINDOW_AGGREG_in_builtinFunc4844); 

                    if ( input.LA(1)==Token.DOWN ) {
                        match(input, Token.DOWN, null); 
                        // EsperEPL2Ast.g:716:22: ( DISTINCT )?
                        int alt261=2;
                        int LA261_0 = input.LA(1);

                        if ( (LA261_0==DISTINCT) ) {
                            alt261=1;
                        }
                        switch (alt261) {
                            case 1 :
                                // EsperEPL2Ast.g:716:23: DISTINCT
                                {
                                match(input,DISTINCT,FOLLOW_DISTINCT_in_builtinFunc4847); 

                                }
                                break;

                        }

                        // EsperEPL2Ast.g:716:34: ( accessValueExpr )?
                        int alt262=2;
                        int LA262_0 = input.LA(1);

                        if ( (LA262_0==ACCESS_AGG) ) {
                            alt262=1;
                        }
                        switch (alt262) {
                            case 1 :
                                // EsperEPL2Ast.g:716:34: accessValueExpr
                                {
                                pushFollow(FOLLOW_accessValueExpr_in_builtinFunc4851);
                                accessValueExpr();

                                state._fsp--;


                                }
                                break;

                        }


                        match(input, Token.UP, null); 
                    }
                     leaveNode(f); 

                    }
                    break;
                case 10 :
                    // EsperEPL2Ast.g:717:5: ^(f= COALESCE valueExpr valueExpr ( valueExpr )* )
                    {
                    f=(CommonTree)match(input,COALESCE,FOLLOW_COALESCE_in_builtinFunc4864); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_valueExpr_in_builtinFunc4866);
                    valueExpr();

                    state._fsp--;

                    pushFollow(FOLLOW_valueExpr_in_builtinFunc4868);
                    valueExpr();

                    state._fsp--;

                    // EsperEPL2Ast.g:717:38: ( valueExpr )*
                    loop263:
                    do {
                        int alt263=2;
                        int LA263_0 = input.LA(1);

                        if ( ((LA263_0>=IN_SET && LA263_0<=REGEXP)||LA263_0==NOT_EXPR||(LA263_0>=SUM && LA263_0<=AVG)||(LA263_0>=COALESCE && LA263_0<=COUNT)||(LA263_0>=CASE && LA263_0<=CASE2)||LA263_0==ISTREAM||(LA263_0>=PREVIOUS && LA263_0<=EXISTS)||(LA263_0>=INSTANCEOF && LA263_0<=CURRENT_TIMESTAMP)||LA263_0==NEWKW||(LA263_0>=EVAL_AND_EXPR && LA263_0<=EVAL_NOTEQUALS_GROUP_EXPR)||LA263_0==EVENT_PROP_EXPR||LA263_0==CONCAT||(LA263_0>=LIB_FUNC_CHAIN && LA263_0<=DOT_EXPR)||LA263_0==ARRAY_EXPR||(LA263_0>=NOT_IN_SET && LA263_0<=NOT_REGEXP)||(LA263_0>=IN_RANGE && LA263_0<=SUBSELECT_EXPR)||(LA263_0>=EXISTS_SUBSELECT_EXPR && LA263_0<=NOT_IN_SUBSELECT_EXPR)||LA263_0==SUBSTITUTION||(LA263_0>=FIRST_AGGREG && LA263_0<=WINDOW_AGGREG)||(LA263_0>=INT_TYPE && LA263_0<=NULL_TYPE)||(LA263_0>=JSON_OBJECT && LA263_0<=JSON_ARRAY)||LA263_0==STAR||(LA263_0>=LT && LA263_0<=GT)||(LA263_0>=BOR && LA263_0<=PLUS)||(LA263_0>=BAND && LA263_0<=BXOR)||(LA263_0>=LE && LA263_0<=GE)||(LA263_0>=MINUS && LA263_0<=MOD)||(LA263_0>=EVAL_IS_GROUP_EXPR && LA263_0<=EVAL_ISNOT_GROUP_EXPR)) ) {
                            alt263=1;
                        }


                        switch (alt263) {
                    	case 1 :
                    	    // EsperEPL2Ast.g:717:39: valueExpr
                    	    {
                    	    pushFollow(FOLLOW_valueExpr_in_builtinFunc4871);
                    	    valueExpr();

                    	    state._fsp--;


                    	    }
                    	    break;

                    	default :
                    	    break loop263;
                        }
                    } while (true);


                    match(input, Token.UP, null); 
                     leaveNode(f); 

                    }
                    break;
                case 11 :
                    // EsperEPL2Ast.g:718:5: ^(f= PREVIOUS valueExpr ( valueExpr )? )
                    {
                    f=(CommonTree)match(input,PREVIOUS,FOLLOW_PREVIOUS_in_builtinFunc4886); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_valueExpr_in_builtinFunc4888);
                    valueExpr();

                    state._fsp--;

                    // EsperEPL2Ast.g:718:28: ( valueExpr )?
                    int alt264=2;
                    int LA264_0 = input.LA(1);

                    if ( ((LA264_0>=IN_SET && LA264_0<=REGEXP)||LA264_0==NOT_EXPR||(LA264_0>=SUM && LA264_0<=AVG)||(LA264_0>=COALESCE && LA264_0<=COUNT)||(LA264_0>=CASE && LA264_0<=CASE2)||LA264_0==ISTREAM||(LA264_0>=PREVIOUS && LA264_0<=EXISTS)||(LA264_0>=INSTANCEOF && LA264_0<=CURRENT_TIMESTAMP)||LA264_0==NEWKW||(LA264_0>=EVAL_AND_EXPR && LA264_0<=EVAL_NOTEQUALS_GROUP_EXPR)||LA264_0==EVENT_PROP_EXPR||LA264_0==CONCAT||(LA264_0>=LIB_FUNC_CHAIN && LA264_0<=DOT_EXPR)||LA264_0==ARRAY_EXPR||(LA264_0>=NOT_IN_SET && LA264_0<=NOT_REGEXP)||(LA264_0>=IN_RANGE && LA264_0<=SUBSELECT_EXPR)||(LA264_0>=EXISTS_SUBSELECT_EXPR && LA264_0<=NOT_IN_SUBSELECT_EXPR)||LA264_0==SUBSTITUTION||(LA264_0>=FIRST_AGGREG && LA264_0<=WINDOW_AGGREG)||(LA264_0>=INT_TYPE && LA264_0<=NULL_TYPE)||(LA264_0>=JSON_OBJECT && LA264_0<=JSON_ARRAY)||LA264_0==STAR||(LA264_0>=LT && LA264_0<=GT)||(LA264_0>=BOR && LA264_0<=PLUS)||(LA264_0>=BAND && LA264_0<=BXOR)||(LA264_0>=LE && LA264_0<=GE)||(LA264_0>=MINUS && LA264_0<=MOD)||(LA264_0>=EVAL_IS_GROUP_EXPR && LA264_0<=EVAL_ISNOT_GROUP_EXPR)) ) {
                        alt264=1;
                    }
                    switch (alt264) {
                        case 1 :
                            // EsperEPL2Ast.g:718:28: valueExpr
                            {
                            pushFollow(FOLLOW_valueExpr_in_builtinFunc4890);
                            valueExpr();

                            state._fsp--;


                            }
                            break;

                    }


                    match(input, Token.UP, null); 
                     leaveNode(f); 

                    }
                    break;
                case 12 :
                    // EsperEPL2Ast.g:719:5: ^(f= PREVIOUSTAIL valueExpr ( valueExpr )? )
                    {
                    f=(CommonTree)match(input,PREVIOUSTAIL,FOLLOW_PREVIOUSTAIL_in_builtinFunc4903); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_valueExpr_in_builtinFunc4905);
                    valueExpr();

                    state._fsp--;

                    // EsperEPL2Ast.g:719:32: ( valueExpr )?
                    int alt265=2;
                    int LA265_0 = input.LA(1);

                    if ( ((LA265_0>=IN_SET && LA265_0<=REGEXP)||LA265_0==NOT_EXPR||(LA265_0>=SUM && LA265_0<=AVG)||(LA265_0>=COALESCE && LA265_0<=COUNT)||(LA265_0>=CASE && LA265_0<=CASE2)||LA265_0==ISTREAM||(LA265_0>=PREVIOUS && LA265_0<=EXISTS)||(LA265_0>=INSTANCEOF && LA265_0<=CURRENT_TIMESTAMP)||LA265_0==NEWKW||(LA265_0>=EVAL_AND_EXPR && LA265_0<=EVAL_NOTEQUALS_GROUP_EXPR)||LA265_0==EVENT_PROP_EXPR||LA265_0==CONCAT||(LA265_0>=LIB_FUNC_CHAIN && LA265_0<=DOT_EXPR)||LA265_0==ARRAY_EXPR||(LA265_0>=NOT_IN_SET && LA265_0<=NOT_REGEXP)||(LA265_0>=IN_RANGE && LA265_0<=SUBSELECT_EXPR)||(LA265_0>=EXISTS_SUBSELECT_EXPR && LA265_0<=NOT_IN_SUBSELECT_EXPR)||LA265_0==SUBSTITUTION||(LA265_0>=FIRST_AGGREG && LA265_0<=WINDOW_AGGREG)||(LA265_0>=INT_TYPE && LA265_0<=NULL_TYPE)||(LA265_0>=JSON_OBJECT && LA265_0<=JSON_ARRAY)||LA265_0==STAR||(LA265_0>=LT && LA265_0<=GT)||(LA265_0>=BOR && LA265_0<=PLUS)||(LA265_0>=BAND && LA265_0<=BXOR)||(LA265_0>=LE && LA265_0<=GE)||(LA265_0>=MINUS && LA265_0<=MOD)||(LA265_0>=EVAL_IS_GROUP_EXPR && LA265_0<=EVAL_ISNOT_GROUP_EXPR)) ) {
                        alt265=1;
                    }
                    switch (alt265) {
                        case 1 :
                            // EsperEPL2Ast.g:719:32: valueExpr
                            {
                            pushFollow(FOLLOW_valueExpr_in_builtinFunc4907);
                            valueExpr();

                            state._fsp--;


                            }
                            break;

                    }


                    match(input, Token.UP, null); 
                     leaveNode(f); 

                    }
                    break;
                case 13 :
                    // EsperEPL2Ast.g:720:5: ^(f= PREVIOUSCOUNT valueExpr )
                    {
                    f=(CommonTree)match(input,PREVIOUSCOUNT,FOLLOW_PREVIOUSCOUNT_in_builtinFunc4920); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_valueExpr_in_builtinFunc4922);
                    valueExpr();

                    state._fsp--;


                    match(input, Token.UP, null); 
                     leaveNode(f); 

                    }
                    break;
                case 14 :
                    // EsperEPL2Ast.g:721:5: ^(f= PREVIOUSWINDOW valueExpr )
                    {
                    f=(CommonTree)match(input,PREVIOUSWINDOW,FOLLOW_PREVIOUSWINDOW_in_builtinFunc4934); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_valueExpr_in_builtinFunc4936);
                    valueExpr();

                    state._fsp--;


                    match(input, Token.UP, null); 
                     leaveNode(f); 

                    }
                    break;
                case 15 :
                    // EsperEPL2Ast.g:722:5: ^(f= PRIOR c= NUM_INT eventPropertyExpr[true] )
                    {
                    f=(CommonTree)match(input,PRIOR,FOLLOW_PRIOR_in_builtinFunc4948); 

                    match(input, Token.DOWN, null); 
                    c=(CommonTree)match(input,NUM_INT,FOLLOW_NUM_INT_in_builtinFunc4952); 
                    pushFollow(FOLLOW_eventPropertyExpr_in_builtinFunc4954);
                    eventPropertyExpr(true);

                    state._fsp--;


                    match(input, Token.UP, null); 
                    leaveNode(c); leaveNode(f);

                    }
                    break;
                case 16 :
                    // EsperEPL2Ast.g:723:5: ^(f= INSTANCEOF valueExpr CLASS_IDENT ( CLASS_IDENT )* )
                    {
                    f=(CommonTree)match(input,INSTANCEOF,FOLLOW_INSTANCEOF_in_builtinFunc4967); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_valueExpr_in_builtinFunc4969);
                    valueExpr();

                    state._fsp--;

                    match(input,CLASS_IDENT,FOLLOW_CLASS_IDENT_in_builtinFunc4971); 
                    // EsperEPL2Ast.g:723:42: ( CLASS_IDENT )*
                    loop266:
                    do {
                        int alt266=2;
                        int LA266_0 = input.LA(1);

                        if ( (LA266_0==CLASS_IDENT) ) {
                            alt266=1;
                        }


                        switch (alt266) {
                    	case 1 :
                    	    // EsperEPL2Ast.g:723:43: CLASS_IDENT
                    	    {
                    	    match(input,CLASS_IDENT,FOLLOW_CLASS_IDENT_in_builtinFunc4974); 

                    	    }
                    	    break;

                    	default :
                    	    break loop266;
                        }
                    } while (true);


                    match(input, Token.UP, null); 
                     leaveNode(f); 

                    }
                    break;
                case 17 :
                    // EsperEPL2Ast.g:724:5: ^(f= TYPEOF valueExpr )
                    {
                    f=(CommonTree)match(input,TYPEOF,FOLLOW_TYPEOF_in_builtinFunc4988); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_valueExpr_in_builtinFunc4990);
                    valueExpr();

                    state._fsp--;


                    match(input, Token.UP, null); 
                     leaveNode(f); 

                    }
                    break;
                case 18 :
                    // EsperEPL2Ast.g:725:5: ^(f= CAST valueExpr CLASS_IDENT )
                    {
                    f=(CommonTree)match(input,CAST,FOLLOW_CAST_in_builtinFunc5002); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_valueExpr_in_builtinFunc5004);
                    valueExpr();

                    state._fsp--;

                    match(input,CLASS_IDENT,FOLLOW_CLASS_IDENT_in_builtinFunc5006); 

                    match(input, Token.UP, null); 
                     leaveNode(f); 

                    }
                    break;
                case 19 :
                    // EsperEPL2Ast.g:726:5: ^(f= EXISTS eventPropertyExpr[true] )
                    {
                    f=(CommonTree)match(input,EXISTS,FOLLOW_EXISTS_in_builtinFunc5018); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_eventPropertyExpr_in_builtinFunc5020);
                    eventPropertyExpr(true);

                    state._fsp--;


                    match(input, Token.UP, null); 
                     leaveNode(f); 

                    }
                    break;
                case 20 :
                    // EsperEPL2Ast.g:727:4: ^(f= CURRENT_TIMESTAMP )
                    {
                    f=(CommonTree)match(input,CURRENT_TIMESTAMP,FOLLOW_CURRENT_TIMESTAMP_in_builtinFunc5032); 



                    if ( input.LA(1)==Token.DOWN ) {
                        match(input, Token.DOWN, null); 
                        match(input, Token.UP, null); 
                    }
                     leaveNode(f); 

                    }
                    break;
                case 21 :
                    // EsperEPL2Ast.g:728:4: ^(f= ISTREAM )
                    {
                    f=(CommonTree)match(input,ISTREAM,FOLLOW_ISTREAM_in_builtinFunc5045); 



                    if ( input.LA(1)==Token.DOWN ) {
                        match(input, Token.DOWN, null); 
                        match(input, Token.UP, null); 
                    }
                     leaveNode(f); 

                    }
                    break;

            }
        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "builtinFunc"


    // $ANTLR start "aggregationFilterExpr"
    // EsperEPL2Ast.g:731:1: aggregationFilterExpr : ^( AGG_FILTER_EXPR valueExpr ) ;
    public final void aggregationFilterExpr() throws RecognitionException {
        try {
            // EsperEPL2Ast.g:732:2: ( ^( AGG_FILTER_EXPR valueExpr ) )
            // EsperEPL2Ast.g:732:4: ^( AGG_FILTER_EXPR valueExpr )
            {
            match(input,AGG_FILTER_EXPR,FOLLOW_AGG_FILTER_EXPR_in_aggregationFilterExpr5062); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_valueExpr_in_aggregationFilterExpr5064);
            valueExpr();

            state._fsp--;


            match(input, Token.UP, null); 

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "aggregationFilterExpr"


    // $ANTLR start "accessValueExpr"
    // EsperEPL2Ast.g:735:1: accessValueExpr : ^( ACCESS_AGG accessValueExprChoice ) ;
    public final void accessValueExpr() throws RecognitionException {
        try {
            // EsperEPL2Ast.g:736:2: ( ^( ACCESS_AGG accessValueExprChoice ) )
            // EsperEPL2Ast.g:736:5: ^( ACCESS_AGG accessValueExprChoice )
            {
            match(input,ACCESS_AGG,FOLLOW_ACCESS_AGG_in_accessValueExpr5078); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_accessValueExprChoice_in_accessValueExpr5080);
            accessValueExprChoice();

            state._fsp--;


            match(input, Token.UP, null); 

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "accessValueExpr"


    // $ANTLR start "accessValueExprChoice"
    // EsperEPL2Ast.g:739:1: accessValueExprChoice : ( PROPERTY_WILDCARD_SELECT | ^(s= PROPERTY_SELECTION_STREAM IDENT ( IDENT )? ) | valueExpr );
    public final void accessValueExprChoice() throws RecognitionException {
        CommonTree s=null;

        try {
            // EsperEPL2Ast.g:740:2: ( PROPERTY_WILDCARD_SELECT | ^(s= PROPERTY_SELECTION_STREAM IDENT ( IDENT )? ) | valueExpr )
            int alt269=3;
            switch ( input.LA(1) ) {
            case PROPERTY_WILDCARD_SELECT:
                {
                alt269=1;
                }
                break;
            case PROPERTY_SELECTION_STREAM:
                {
                alt269=2;
                }
                break;
            case IN_SET:
            case BETWEEN:
            case LIKE:
            case REGEXP:
            case NOT_EXPR:
            case SUM:
            case AVG:
            case COALESCE:
            case MEDIAN:
            case STDDEV:
            case AVEDEV:
            case COUNT:
            case CASE:
            case CASE2:
            case ISTREAM:
            case PREVIOUS:
            case PREVIOUSTAIL:
            case PREVIOUSCOUNT:
            case PREVIOUSWINDOW:
            case PRIOR:
            case EXISTS:
            case INSTANCEOF:
            case TYPEOF:
            case CAST:
            case CURRENT_TIMESTAMP:
            case NEWKW:
            case EVAL_AND_EXPR:
            case EVAL_OR_EXPR:
            case EVAL_EQUALS_EXPR:
            case EVAL_NOTEQUALS_EXPR:
            case EVAL_IS_EXPR:
            case EVAL_ISNOT_EXPR:
            case EVAL_EQUALS_GROUP_EXPR:
            case EVAL_NOTEQUALS_GROUP_EXPR:
            case EVENT_PROP_EXPR:
            case CONCAT:
            case LIB_FUNC_CHAIN:
            case DOT_EXPR:
            case ARRAY_EXPR:
            case NOT_IN_SET:
            case NOT_BETWEEN:
            case NOT_LIKE:
            case NOT_REGEXP:
            case IN_RANGE:
            case NOT_IN_RANGE:
            case SUBSELECT_EXPR:
            case EXISTS_SUBSELECT_EXPR:
            case IN_SUBSELECT_EXPR:
            case NOT_IN_SUBSELECT_EXPR:
            case SUBSTITUTION:
            case FIRST_AGGREG:
            case LAST_AGGREG:
            case WINDOW_AGGREG:
            case INT_TYPE:
            case LONG_TYPE:
            case FLOAT_TYPE:
            case DOUBLE_TYPE:
            case STRING_TYPE:
            case BOOL_TYPE:
            case NULL_TYPE:
            case JSON_OBJECT:
            case JSON_ARRAY:
            case STAR:
            case LT:
            case GT:
            case BOR:
            case PLUS:
            case BAND:
            case BXOR:
            case LE:
            case GE:
            case MINUS:
            case DIV:
            case MOD:
            case EVAL_IS_GROUP_EXPR:
            case EVAL_ISNOT_GROUP_EXPR:
                {
                alt269=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 269, 0, input);

                throw nvae;
            }

            switch (alt269) {
                case 1 :
                    // EsperEPL2Ast.g:740:4: PROPERTY_WILDCARD_SELECT
                    {
                    match(input,PROPERTY_WILDCARD_SELECT,FOLLOW_PROPERTY_WILDCARD_SELECT_in_accessValueExprChoice5095); 

                    }
                    break;
                case 2 :
                    // EsperEPL2Ast.g:740:31: ^(s= PROPERTY_SELECTION_STREAM IDENT ( IDENT )? )
                    {
                    s=(CommonTree)match(input,PROPERTY_SELECTION_STREAM,FOLLOW_PROPERTY_SELECTION_STREAM_in_accessValueExprChoice5102); 

                    match(input, Token.DOWN, null); 
                    match(input,IDENT,FOLLOW_IDENT_in_accessValueExprChoice5104); 
                    // EsperEPL2Ast.g:740:67: ( IDENT )?
                    int alt268=2;
                    int LA268_0 = input.LA(1);

                    if ( (LA268_0==IDENT) ) {
                        alt268=1;
                    }
                    switch (alt268) {
                        case 1 :
                            // EsperEPL2Ast.g:740:67: IDENT
                            {
                            match(input,IDENT,FOLLOW_IDENT_in_accessValueExprChoice5106); 

                            }
                            break;

                    }


                    match(input, Token.UP, null); 

                    }
                    break;
                case 3 :
                    // EsperEPL2Ast.g:740:77: valueExpr
                    {
                    pushFollow(FOLLOW_valueExpr_in_accessValueExprChoice5112);
                    valueExpr();

                    state._fsp--;


                    }
                    break;

            }
        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "accessValueExprChoice"


    // $ANTLR start "arrayExpr"
    // EsperEPL2Ast.g:743:1: arrayExpr : ^(a= ARRAY_EXPR ( valueExpr )* ) ;
    public final void arrayExpr() throws RecognitionException {
        CommonTree a=null;

        try {
            // EsperEPL2Ast.g:744:2: ( ^(a= ARRAY_EXPR ( valueExpr )* ) )
            // EsperEPL2Ast.g:744:4: ^(a= ARRAY_EXPR ( valueExpr )* )
            {
            a=(CommonTree)match(input,ARRAY_EXPR,FOLLOW_ARRAY_EXPR_in_arrayExpr5128); 

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // EsperEPL2Ast.g:744:19: ( valueExpr )*
                loop270:
                do {
                    int alt270=2;
                    int LA270_0 = input.LA(1);

                    if ( ((LA270_0>=IN_SET && LA270_0<=REGEXP)||LA270_0==NOT_EXPR||(LA270_0>=SUM && LA270_0<=AVG)||(LA270_0>=COALESCE && LA270_0<=COUNT)||(LA270_0>=CASE && LA270_0<=CASE2)||LA270_0==ISTREAM||(LA270_0>=PREVIOUS && LA270_0<=EXISTS)||(LA270_0>=INSTANCEOF && LA270_0<=CURRENT_TIMESTAMP)||LA270_0==NEWKW||(LA270_0>=EVAL_AND_EXPR && LA270_0<=EVAL_NOTEQUALS_GROUP_EXPR)||LA270_0==EVENT_PROP_EXPR||LA270_0==CONCAT||(LA270_0>=LIB_FUNC_CHAIN && LA270_0<=DOT_EXPR)||LA270_0==ARRAY_EXPR||(LA270_0>=NOT_IN_SET && LA270_0<=NOT_REGEXP)||(LA270_0>=IN_RANGE && LA270_0<=SUBSELECT_EXPR)||(LA270_0>=EXISTS_SUBSELECT_EXPR && LA270_0<=NOT_IN_SUBSELECT_EXPR)||LA270_0==SUBSTITUTION||(LA270_0>=FIRST_AGGREG && LA270_0<=WINDOW_AGGREG)||(LA270_0>=INT_TYPE && LA270_0<=NULL_TYPE)||(LA270_0>=JSON_OBJECT && LA270_0<=JSON_ARRAY)||LA270_0==STAR||(LA270_0>=LT && LA270_0<=GT)||(LA270_0>=BOR && LA270_0<=PLUS)||(LA270_0>=BAND && LA270_0<=BXOR)||(LA270_0>=LE && LA270_0<=GE)||(LA270_0>=MINUS && LA270_0<=MOD)||(LA270_0>=EVAL_IS_GROUP_EXPR && LA270_0<=EVAL_ISNOT_GROUP_EXPR)) ) {
                        alt270=1;
                    }


                    switch (alt270) {
                	case 1 :
                	    // EsperEPL2Ast.g:744:20: valueExpr
                	    {
                	    pushFollow(FOLLOW_valueExpr_in_arrayExpr5131);
                	    valueExpr();

                	    state._fsp--;


                	    }
                	    break;

                	default :
                	    break loop270;
                    }
                } while (true);


                match(input, Token.UP, null); 
            }
             leaveNode(a); 

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "arrayExpr"


    // $ANTLR start "arithmeticExpr"
    // EsperEPL2Ast.g:747:1: arithmeticExpr : ( ^(a= PLUS valueExpr valueExpr ) | ^(a= MINUS valueExpr valueExpr ) | ^(a= DIV valueExpr valueExpr ) | ^(a= STAR valueExpr valueExpr ) | ^(a= MOD valueExpr valueExpr ) | ^(a= BAND valueExpr valueExpr ) | ^(a= BOR valueExpr valueExpr ) | ^(a= BXOR valueExpr valueExpr ) | ^(a= CONCAT valueExpr valueExpr ( valueExpr )* ) );
    public final void arithmeticExpr() throws RecognitionException {
        CommonTree a=null;

        try {
            // EsperEPL2Ast.g:748:2: ( ^(a= PLUS valueExpr valueExpr ) | ^(a= MINUS valueExpr valueExpr ) | ^(a= DIV valueExpr valueExpr ) | ^(a= STAR valueExpr valueExpr ) | ^(a= MOD valueExpr valueExpr ) | ^(a= BAND valueExpr valueExpr ) | ^(a= BOR valueExpr valueExpr ) | ^(a= BXOR valueExpr valueExpr ) | ^(a= CONCAT valueExpr valueExpr ( valueExpr )* ) )
            int alt272=9;
            switch ( input.LA(1) ) {
            case PLUS:
                {
                alt272=1;
                }
                break;
            case MINUS:
                {
                alt272=2;
                }
                break;
            case DIV:
                {
                alt272=3;
                }
                break;
            case STAR:
                {
                alt272=4;
                }
                break;
            case MOD:
                {
                alt272=5;
                }
                break;
            case BAND:
                {
                alt272=6;
                }
                break;
            case BOR:
                {
                alt272=7;
                }
                break;
            case BXOR:
                {
                alt272=8;
                }
                break;
            case CONCAT:
                {
                alt272=9;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 272, 0, input);

                throw nvae;
            }

            switch (alt272) {
                case 1 :
                    // EsperEPL2Ast.g:748:5: ^(a= PLUS valueExpr valueExpr )
                    {
                    a=(CommonTree)match(input,PLUS,FOLLOW_PLUS_in_arithmeticExpr5152); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_valueExpr_in_arithmeticExpr5154);
                    valueExpr();

                    state._fsp--;

                    pushFollow(FOLLOW_valueExpr_in_arithmeticExpr5156);
                    valueExpr();

                    state._fsp--;


                    match(input, Token.UP, null); 
                     leaveNode(a); 

                    }
                    break;
                case 2 :
                    // EsperEPL2Ast.g:749:5: ^(a= MINUS valueExpr valueExpr )
                    {
                    a=(CommonTree)match(input,MINUS,FOLLOW_MINUS_in_arithmeticExpr5168); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_valueExpr_in_arithmeticExpr5170);
                    valueExpr();

                    state._fsp--;

                    pushFollow(FOLLOW_valueExpr_in_arithmeticExpr5172);
                    valueExpr();

                    state._fsp--;


                    match(input, Token.UP, null); 
                     leaveNode(a); 

                    }
                    break;
                case 3 :
                    // EsperEPL2Ast.g:750:5: ^(a= DIV valueExpr valueExpr )
                    {
                    a=(CommonTree)match(input,DIV,FOLLOW_DIV_in_arithmeticExpr5184); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_valueExpr_in_arithmeticExpr5186);
                    valueExpr();

                    state._fsp--;

                    pushFollow(FOLLOW_valueExpr_in_arithmeticExpr5188);
                    valueExpr();

                    state._fsp--;


                    match(input, Token.UP, null); 
                     leaveNode(a); 

                    }
                    break;
                case 4 :
                    // EsperEPL2Ast.g:751:4: ^(a= STAR valueExpr valueExpr )
                    {
                    a=(CommonTree)match(input,STAR,FOLLOW_STAR_in_arithmeticExpr5199); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_valueExpr_in_arithmeticExpr5201);
                    valueExpr();

                    state._fsp--;

                    pushFollow(FOLLOW_valueExpr_in_arithmeticExpr5203);
                    valueExpr();

                    state._fsp--;


                    match(input, Token.UP, null); 
                     leaveNode(a); 

                    }
                    break;
                case 5 :
                    // EsperEPL2Ast.g:752:5: ^(a= MOD valueExpr valueExpr )
                    {
                    a=(CommonTree)match(input,MOD,FOLLOW_MOD_in_arithmeticExpr5215); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_valueExpr_in_arithmeticExpr5217);
                    valueExpr();

                    state._fsp--;

                    pushFollow(FOLLOW_valueExpr_in_arithmeticExpr5219);
                    valueExpr();

                    state._fsp--;


                    match(input, Token.UP, null); 
                     leaveNode(a); 

                    }
                    break;
                case 6 :
                    // EsperEPL2Ast.g:753:4: ^(a= BAND valueExpr valueExpr )
                    {
                    a=(CommonTree)match(input,BAND,FOLLOW_BAND_in_arithmeticExpr5230); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_valueExpr_in_arithmeticExpr5232);
                    valueExpr();

                    state._fsp--;

                    pushFollow(FOLLOW_valueExpr_in_arithmeticExpr5234);
                    valueExpr();

                    state._fsp--;


                    match(input, Token.UP, null); 
                     leaveNode(a); 

                    }
                    break;
                case 7 :
                    // EsperEPL2Ast.g:754:4: ^(a= BOR valueExpr valueExpr )
                    {
                    a=(CommonTree)match(input,BOR,FOLLOW_BOR_in_arithmeticExpr5245); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_valueExpr_in_arithmeticExpr5247);
                    valueExpr();

                    state._fsp--;

                    pushFollow(FOLLOW_valueExpr_in_arithmeticExpr5249);
                    valueExpr();

                    state._fsp--;


                    match(input, Token.UP, null); 
                     leaveNode(a); 

                    }
                    break;
                case 8 :
                    // EsperEPL2Ast.g:755:4: ^(a= BXOR valueExpr valueExpr )
                    {
                    a=(CommonTree)match(input,BXOR,FOLLOW_BXOR_in_arithmeticExpr5260); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_valueExpr_in_arithmeticExpr5262);
                    valueExpr();

                    state._fsp--;

                    pushFollow(FOLLOW_valueExpr_in_arithmeticExpr5264);
                    valueExpr();

                    state._fsp--;


                    match(input, Token.UP, null); 
                     leaveNode(a); 

                    }
                    break;
                case 9 :
                    // EsperEPL2Ast.g:756:5: ^(a= CONCAT valueExpr valueExpr ( valueExpr )* )
                    {
                    a=(CommonTree)match(input,CONCAT,FOLLOW_CONCAT_in_arithmeticExpr5276); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_valueExpr_in_arithmeticExpr5278);
                    valueExpr();

                    state._fsp--;

                    pushFollow(FOLLOW_valueExpr_in_arithmeticExpr5280);
                    valueExpr();

                    state._fsp--;

                    // EsperEPL2Ast.g:756:36: ( valueExpr )*
                    loop271:
                    do {
                        int alt271=2;
                        int LA271_0 = input.LA(1);

                        if ( ((LA271_0>=IN_SET && LA271_0<=REGEXP)||LA271_0==NOT_EXPR||(LA271_0>=SUM && LA271_0<=AVG)||(LA271_0>=COALESCE && LA271_0<=COUNT)||(LA271_0>=CASE && LA271_0<=CASE2)||LA271_0==ISTREAM||(LA271_0>=PREVIOUS && LA271_0<=EXISTS)||(LA271_0>=INSTANCEOF && LA271_0<=CURRENT_TIMESTAMP)||LA271_0==NEWKW||(LA271_0>=EVAL_AND_EXPR && LA271_0<=EVAL_NOTEQUALS_GROUP_EXPR)||LA271_0==EVENT_PROP_EXPR||LA271_0==CONCAT||(LA271_0>=LIB_FUNC_CHAIN && LA271_0<=DOT_EXPR)||LA271_0==ARRAY_EXPR||(LA271_0>=NOT_IN_SET && LA271_0<=NOT_REGEXP)||(LA271_0>=IN_RANGE && LA271_0<=SUBSELECT_EXPR)||(LA271_0>=EXISTS_SUBSELECT_EXPR && LA271_0<=NOT_IN_SUBSELECT_EXPR)||LA271_0==SUBSTITUTION||(LA271_0>=FIRST_AGGREG && LA271_0<=WINDOW_AGGREG)||(LA271_0>=INT_TYPE && LA271_0<=NULL_TYPE)||(LA271_0>=JSON_OBJECT && LA271_0<=JSON_ARRAY)||LA271_0==STAR||(LA271_0>=LT && LA271_0<=GT)||(LA271_0>=BOR && LA271_0<=PLUS)||(LA271_0>=BAND && LA271_0<=BXOR)||(LA271_0>=LE && LA271_0<=GE)||(LA271_0>=MINUS && LA271_0<=MOD)||(LA271_0>=EVAL_IS_GROUP_EXPR && LA271_0<=EVAL_ISNOT_GROUP_EXPR)) ) {
                            alt271=1;
                        }


                        switch (alt271) {
                    	case 1 :
                    	    // EsperEPL2Ast.g:756:37: valueExpr
                    	    {
                    	    pushFollow(FOLLOW_valueExpr_in_arithmeticExpr5283);
                    	    valueExpr();

                    	    state._fsp--;


                    	    }
                    	    break;

                    	default :
                    	    break loop271;
                        }
                    } while (true);


                    match(input, Token.UP, null); 
                     leaveNode(a); 

                    }
                    break;

            }
        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "arithmeticExpr"


    // $ANTLR start "dotExpr"
    // EsperEPL2Ast.g:759:1: dotExpr : ^(d= DOT_EXPR valueExpr ( libFunctionWithClass )* ) ;
    public final void dotExpr() throws RecognitionException {
        CommonTree d=null;

        try {
            // EsperEPL2Ast.g:760:2: ( ^(d= DOT_EXPR valueExpr ( libFunctionWithClass )* ) )
            // EsperEPL2Ast.g:760:4: ^(d= DOT_EXPR valueExpr ( libFunctionWithClass )* )
            {
            d=(CommonTree)match(input,DOT_EXPR,FOLLOW_DOT_EXPR_in_dotExpr5303); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_valueExpr_in_dotExpr5305);
            valueExpr();

            state._fsp--;

            // EsperEPL2Ast.g:760:27: ( libFunctionWithClass )*
            loop273:
            do {
                int alt273=2;
                int LA273_0 = input.LA(1);

                if ( (LA273_0==LIB_FUNCTION) ) {
                    alt273=1;
                }


                switch (alt273) {
            	case 1 :
            	    // EsperEPL2Ast.g:760:27: libFunctionWithClass
            	    {
            	    pushFollow(FOLLOW_libFunctionWithClass_in_dotExpr5307);
            	    libFunctionWithClass();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop273;
                }
            } while (true);


            match(input, Token.UP, null); 
             leaveNode(d); 

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "dotExpr"


    // $ANTLR start "newExpr"
    // EsperEPL2Ast.g:763:1: newExpr : ^(n= NEWKW ( newAssign )* ) ;
    public final void newExpr() throws RecognitionException {
        CommonTree n=null;

        try {
            // EsperEPL2Ast.g:763:9: ( ^(n= NEWKW ( newAssign )* ) )
            // EsperEPL2Ast.g:763:11: ^(n= NEWKW ( newAssign )* )
            {
            n=(CommonTree)match(input,NEWKW,FOLLOW_NEWKW_in_newExpr5325); 

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // EsperEPL2Ast.g:763:21: ( newAssign )*
                loop274:
                do {
                    int alt274=2;
                    int LA274_0 = input.LA(1);

                    if ( (LA274_0==NEW_ITEM) ) {
                        alt274=1;
                    }


                    switch (alt274) {
                	case 1 :
                	    // EsperEPL2Ast.g:763:21: newAssign
                	    {
                	    pushFollow(FOLLOW_newAssign_in_newExpr5327);
                	    newAssign();

                	    state._fsp--;


                	    }
                	    break;

                	default :
                	    break loop274;
                    }
                } while (true);


                match(input, Token.UP, null); 
            }
             leaveNode(n); 

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "newExpr"


    // $ANTLR start "newAssign"
    // EsperEPL2Ast.g:766:1: newAssign : ^( NEW_ITEM eventPropertyExpr[false] ( valueExpr )? ) ;
    public final void newAssign() throws RecognitionException {
        try {
            // EsperEPL2Ast.g:767:2: ( ^( NEW_ITEM eventPropertyExpr[false] ( valueExpr )? ) )
            // EsperEPL2Ast.g:767:4: ^( NEW_ITEM eventPropertyExpr[false] ( valueExpr )? )
            {
            match(input,NEW_ITEM,FOLLOW_NEW_ITEM_in_newAssign5343); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_eventPropertyExpr_in_newAssign5345);
            eventPropertyExpr(false);

            state._fsp--;

            // EsperEPL2Ast.g:767:40: ( valueExpr )?
            int alt275=2;
            int LA275_0 = input.LA(1);

            if ( ((LA275_0>=IN_SET && LA275_0<=REGEXP)||LA275_0==NOT_EXPR||(LA275_0>=SUM && LA275_0<=AVG)||(LA275_0>=COALESCE && LA275_0<=COUNT)||(LA275_0>=CASE && LA275_0<=CASE2)||LA275_0==ISTREAM||(LA275_0>=PREVIOUS && LA275_0<=EXISTS)||(LA275_0>=INSTANCEOF && LA275_0<=CURRENT_TIMESTAMP)||LA275_0==NEWKW||(LA275_0>=EVAL_AND_EXPR && LA275_0<=EVAL_NOTEQUALS_GROUP_EXPR)||LA275_0==EVENT_PROP_EXPR||LA275_0==CONCAT||(LA275_0>=LIB_FUNC_CHAIN && LA275_0<=DOT_EXPR)||LA275_0==ARRAY_EXPR||(LA275_0>=NOT_IN_SET && LA275_0<=NOT_REGEXP)||(LA275_0>=IN_RANGE && LA275_0<=SUBSELECT_EXPR)||(LA275_0>=EXISTS_SUBSELECT_EXPR && LA275_0<=NOT_IN_SUBSELECT_EXPR)||LA275_0==SUBSTITUTION||(LA275_0>=FIRST_AGGREG && LA275_0<=WINDOW_AGGREG)||(LA275_0>=INT_TYPE && LA275_0<=NULL_TYPE)||(LA275_0>=JSON_OBJECT && LA275_0<=JSON_ARRAY)||LA275_0==STAR||(LA275_0>=LT && LA275_0<=GT)||(LA275_0>=BOR && LA275_0<=PLUS)||(LA275_0>=BAND && LA275_0<=BXOR)||(LA275_0>=LE && LA275_0<=GE)||(LA275_0>=MINUS && LA275_0<=MOD)||(LA275_0>=EVAL_IS_GROUP_EXPR && LA275_0<=EVAL_ISNOT_GROUP_EXPR)) ) {
                alt275=1;
            }
            switch (alt275) {
                case 1 :
                    // EsperEPL2Ast.g:767:40: valueExpr
                    {
                    pushFollow(FOLLOW_valueExpr_in_newAssign5348);
                    valueExpr();

                    state._fsp--;


                    }
                    break;

            }


            match(input, Token.UP, null); 

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "newAssign"


    // $ANTLR start "libFuncChain"
    // EsperEPL2Ast.g:770:1: libFuncChain : ^(l= LIB_FUNC_CHAIN libFunctionWithClass ( libOrPropFunction )* ) ;
    public final void libFuncChain() throws RecognitionException {
        CommonTree l=null;

        try {
            // EsperEPL2Ast.g:771:2: ( ^(l= LIB_FUNC_CHAIN libFunctionWithClass ( libOrPropFunction )* ) )
            // EsperEPL2Ast.g:771:6: ^(l= LIB_FUNC_CHAIN libFunctionWithClass ( libOrPropFunction )* )
            {
            l=(CommonTree)match(input,LIB_FUNC_CHAIN,FOLLOW_LIB_FUNC_CHAIN_in_libFuncChain5366); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_libFunctionWithClass_in_libFuncChain5368);
            libFunctionWithClass();

            state._fsp--;

            // EsperEPL2Ast.g:771:46: ( libOrPropFunction )*
            loop276:
            do {
                int alt276=2;
                int LA276_0 = input.LA(1);

                if ( (LA276_0==EVENT_PROP_EXPR||LA276_0==LIB_FUNCTION) ) {
                    alt276=1;
                }


                switch (alt276) {
            	case 1 :
            	    // EsperEPL2Ast.g:771:46: libOrPropFunction
            	    {
            	    pushFollow(FOLLOW_libOrPropFunction_in_libFuncChain5370);
            	    libOrPropFunction();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop276;
                }
            } while (true);


            match(input, Token.UP, null); 
             leaveNode(l); 

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "libFuncChain"


    // $ANTLR start "libFunctionWithClass"
    // EsperEPL2Ast.g:774:1: libFunctionWithClass : ^(l= LIB_FUNCTION ( CLASS_IDENT )? IDENT ( DISTINCT )? ( libFunctionArgItem )* ( LPAREN )? ) ;
    public final void libFunctionWithClass() throws RecognitionException {
        CommonTree l=null;

        try {
            // EsperEPL2Ast.g:775:2: ( ^(l= LIB_FUNCTION ( CLASS_IDENT )? IDENT ( DISTINCT )? ( libFunctionArgItem )* ( LPAREN )? ) )
            // EsperEPL2Ast.g:775:6: ^(l= LIB_FUNCTION ( CLASS_IDENT )? IDENT ( DISTINCT )? ( libFunctionArgItem )* ( LPAREN )? )
            {
            l=(CommonTree)match(input,LIB_FUNCTION,FOLLOW_LIB_FUNCTION_in_libFunctionWithClass5390); 

            match(input, Token.DOWN, null); 
            // EsperEPL2Ast.g:775:23: ( CLASS_IDENT )?
            int alt277=2;
            int LA277_0 = input.LA(1);

            if ( (LA277_0==CLASS_IDENT) ) {
                alt277=1;
            }
            switch (alt277) {
                case 1 :
                    // EsperEPL2Ast.g:775:24: CLASS_IDENT
                    {
                    match(input,CLASS_IDENT,FOLLOW_CLASS_IDENT_in_libFunctionWithClass5393); 

                    }
                    break;

            }

            match(input,IDENT,FOLLOW_IDENT_in_libFunctionWithClass5397); 
            // EsperEPL2Ast.g:775:44: ( DISTINCT )?
            int alt278=2;
            int LA278_0 = input.LA(1);

            if ( (LA278_0==DISTINCT) ) {
                alt278=1;
            }
            switch (alt278) {
                case 1 :
                    // EsperEPL2Ast.g:775:45: DISTINCT
                    {
                    match(input,DISTINCT,FOLLOW_DISTINCT_in_libFunctionWithClass5400); 

                    }
                    break;

            }

            // EsperEPL2Ast.g:775:56: ( libFunctionArgItem )*
            loop279:
            do {
                int alt279=2;
                int LA279_0 = input.LA(1);

                if ( ((LA279_0>=IN_SET && LA279_0<=REGEXP)||LA279_0==NOT_EXPR||(LA279_0>=SUM && LA279_0<=AVG)||(LA279_0>=COALESCE && LA279_0<=COUNT)||(LA279_0>=CASE && LA279_0<=CASE2)||LA279_0==LAST||LA279_0==ISTREAM||(LA279_0>=PREVIOUS && LA279_0<=EXISTS)||(LA279_0>=LW && LA279_0<=CURRENT_TIMESTAMP)||LA279_0==NEWKW||(LA279_0>=NUMERIC_PARAM_RANGE && LA279_0<=OBJECT_PARAM_ORDERED_EXPR)||(LA279_0>=EVAL_AND_EXPR && LA279_0<=EVAL_NOTEQUALS_GROUP_EXPR)||LA279_0==EVENT_PROP_EXPR||LA279_0==CONCAT||(LA279_0>=LIB_FUNC_CHAIN && LA279_0<=DOT_EXPR)||(LA279_0>=TIME_PERIOD && LA279_0<=ARRAY_EXPR)||(LA279_0>=NOT_IN_SET && LA279_0<=NOT_REGEXP)||(LA279_0>=IN_RANGE && LA279_0<=SUBSELECT_EXPR)||(LA279_0>=EXISTS_SUBSELECT_EXPR && LA279_0<=NOT_IN_SUBSELECT_EXPR)||(LA279_0>=LAST_OPERATOR && LA279_0<=SUBSTITUTION)||LA279_0==NUMBERSETSTAR||(LA279_0>=FIRST_AGGREG && LA279_0<=WINDOW_AGGREG)||(LA279_0>=INT_TYPE && LA279_0<=NULL_TYPE)||(LA279_0>=JSON_OBJECT && LA279_0<=JSON_ARRAY)||LA279_0==GOES||LA279_0==STAR||(LA279_0>=LT && LA279_0<=GT)||(LA279_0>=BOR && LA279_0<=PLUS)||(LA279_0>=BAND && LA279_0<=BXOR)||(LA279_0>=LE && LA279_0<=GE)||(LA279_0>=MINUS && LA279_0<=MOD)||(LA279_0>=EVAL_IS_GROUP_EXPR && LA279_0<=EVAL_ISNOT_GROUP_EXPR)) ) {
                    alt279=1;
                }


                switch (alt279) {
            	case 1 :
            	    // EsperEPL2Ast.g:775:56: libFunctionArgItem
            	    {
            	    pushFollow(FOLLOW_libFunctionArgItem_in_libFunctionWithClass5404);
            	    libFunctionArgItem();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop279;
                }
            } while (true);

            // EsperEPL2Ast.g:775:76: ( LPAREN )?
            int alt280=2;
            int LA280_0 = input.LA(1);

            if ( (LA280_0==LPAREN) ) {
                alt280=1;
            }
            switch (alt280) {
                case 1 :
                    // EsperEPL2Ast.g:775:76: LPAREN
                    {
                    match(input,LPAREN,FOLLOW_LPAREN_in_libFunctionWithClass5407); 

                    }
                    break;

            }


            match(input, Token.UP, null); 

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "libFunctionWithClass"


    // $ANTLR start "libFunctionArgItem"
    // EsperEPL2Ast.g:778:1: libFunctionArgItem : ( expressionLambdaDecl | valueExprWithTime );
    public final void libFunctionArgItem() throws RecognitionException {
        try {
            // EsperEPL2Ast.g:779:2: ( expressionLambdaDecl | valueExprWithTime )
            int alt281=2;
            int LA281_0 = input.LA(1);

            if ( (LA281_0==GOES) ) {
                alt281=1;
            }
            else if ( ((LA281_0>=IN_SET && LA281_0<=REGEXP)||LA281_0==NOT_EXPR||(LA281_0>=SUM && LA281_0<=AVG)||(LA281_0>=COALESCE && LA281_0<=COUNT)||(LA281_0>=CASE && LA281_0<=CASE2)||LA281_0==LAST||LA281_0==ISTREAM||(LA281_0>=PREVIOUS && LA281_0<=EXISTS)||(LA281_0>=LW && LA281_0<=CURRENT_TIMESTAMP)||LA281_0==NEWKW||(LA281_0>=NUMERIC_PARAM_RANGE && LA281_0<=OBJECT_PARAM_ORDERED_EXPR)||(LA281_0>=EVAL_AND_EXPR && LA281_0<=EVAL_NOTEQUALS_GROUP_EXPR)||LA281_0==EVENT_PROP_EXPR||LA281_0==CONCAT||(LA281_0>=LIB_FUNC_CHAIN && LA281_0<=DOT_EXPR)||(LA281_0>=TIME_PERIOD && LA281_0<=ARRAY_EXPR)||(LA281_0>=NOT_IN_SET && LA281_0<=NOT_REGEXP)||(LA281_0>=IN_RANGE && LA281_0<=SUBSELECT_EXPR)||(LA281_0>=EXISTS_SUBSELECT_EXPR && LA281_0<=NOT_IN_SUBSELECT_EXPR)||(LA281_0>=LAST_OPERATOR && LA281_0<=SUBSTITUTION)||LA281_0==NUMBERSETSTAR||(LA281_0>=FIRST_AGGREG && LA281_0<=WINDOW_AGGREG)||(LA281_0>=INT_TYPE && LA281_0<=NULL_TYPE)||(LA281_0>=JSON_OBJECT && LA281_0<=JSON_ARRAY)||LA281_0==STAR||(LA281_0>=LT && LA281_0<=GT)||(LA281_0>=BOR && LA281_0<=PLUS)||(LA281_0>=BAND && LA281_0<=BXOR)||(LA281_0>=LE && LA281_0<=GE)||(LA281_0>=MINUS && LA281_0<=MOD)||(LA281_0>=EVAL_IS_GROUP_EXPR && LA281_0<=EVAL_ISNOT_GROUP_EXPR)) ) {
                alt281=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 281, 0, input);

                throw nvae;
            }
            switch (alt281) {
                case 1 :
                    // EsperEPL2Ast.g:779:4: expressionLambdaDecl
                    {
                    pushFollow(FOLLOW_expressionLambdaDecl_in_libFunctionArgItem5421);
                    expressionLambdaDecl();

                    state._fsp--;


                    }
                    break;
                case 2 :
                    // EsperEPL2Ast.g:779:27: valueExprWithTime
                    {
                    pushFollow(FOLLOW_valueExprWithTime_in_libFunctionArgItem5425);
                    valueExprWithTime();

                    state._fsp--;


                    }
                    break;

            }
        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "libFunctionArgItem"


    // $ANTLR start "libOrPropFunction"
    // EsperEPL2Ast.g:782:1: libOrPropFunction : ( eventPropertyExpr[false] | libFunctionWithClass );
    public final void libOrPropFunction() throws RecognitionException {
        try {
            // EsperEPL2Ast.g:783:2: ( eventPropertyExpr[false] | libFunctionWithClass )
            int alt282=2;
            int LA282_0 = input.LA(1);

            if ( (LA282_0==EVENT_PROP_EXPR) ) {
                alt282=1;
            }
            else if ( (LA282_0==LIB_FUNCTION) ) {
                alt282=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 282, 0, input);

                throw nvae;
            }
            switch (alt282) {
                case 1 :
                    // EsperEPL2Ast.g:783:7: eventPropertyExpr[false]
                    {
                    pushFollow(FOLLOW_eventPropertyExpr_in_libOrPropFunction5440);
                    eventPropertyExpr(false);

                    state._fsp--;


                    }
                    break;
                case 2 :
                    // EsperEPL2Ast.g:784:7: libFunctionWithClass
                    {
                    pushFollow(FOLLOW_libFunctionWithClass_in_libOrPropFunction5450);
                    libFunctionWithClass();

                    state._fsp--;


                    }
                    break;

            }
        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "libOrPropFunction"


    // $ANTLR start "startPatternExpressionRule"
    // EsperEPL2Ast.g:790:1: startPatternExpressionRule : ( annotation[true] )* exprChoice ;
    public final void startPatternExpressionRule() throws RecognitionException {
        try {
            // EsperEPL2Ast.g:791:2: ( ( annotation[true] )* exprChoice )
            // EsperEPL2Ast.g:791:4: ( annotation[true] )* exprChoice
            {
            // EsperEPL2Ast.g:791:4: ( annotation[true] )*
            loop283:
            do {
                int alt283=2;
                int LA283_0 = input.LA(1);

                if ( (LA283_0==ANNOTATION) ) {
                    alt283=1;
                }


                switch (alt283) {
            	case 1 :
            	    // EsperEPL2Ast.g:791:4: annotation[true]
            	    {
            	    pushFollow(FOLLOW_annotation_in_startPatternExpressionRule5465);
            	    annotation(true);

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop283;
                }
            } while (true);

            pushFollow(FOLLOW_exprChoice_in_startPatternExpressionRule5469);
            exprChoice();

            state._fsp--;

             endPattern(); end(); 

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "startPatternExpressionRule"


    // $ANTLR start "exprChoice"
    // EsperEPL2Ast.g:794:1: exprChoice : ( atomicExpr | patternOp | ^(a= EVERY_EXPR exprChoice ) | ^(a= EVERY_DISTINCT_EXPR distinctExpressions exprChoice ) | ^(n= PATTERN_NOT_EXPR exprChoice ) | ^(g= GUARD_EXPR exprChoice ( IDENT IDENT ( valueExprWithTime )* | valueExpr ) ) | ^(m= MATCH_UNTIL_EXPR ( matchUntilRange )? exprChoice ( exprChoice )? ) );
    public final void exprChoice() throws RecognitionException {
        CommonTree a=null;
        CommonTree n=null;
        CommonTree g=null;
        CommonTree m=null;

        try {
            // EsperEPL2Ast.g:795:2: ( atomicExpr | patternOp | ^(a= EVERY_EXPR exprChoice ) | ^(a= EVERY_DISTINCT_EXPR distinctExpressions exprChoice ) | ^(n= PATTERN_NOT_EXPR exprChoice ) | ^(g= GUARD_EXPR exprChoice ( IDENT IDENT ( valueExprWithTime )* | valueExpr ) ) | ^(m= MATCH_UNTIL_EXPR ( matchUntilRange )? exprChoice ( exprChoice )? ) )
            int alt288=7;
            switch ( input.LA(1) ) {
            case PATTERN_FILTER_EXPR:
            case OBSERVER_EXPR:
                {
                alt288=1;
                }
                break;
            case OR_EXPR:
            case AND_EXPR:
            case FOLLOWED_BY_EXPR:
                {
                alt288=2;
                }
                break;
            case EVERY_EXPR:
                {
                alt288=3;
                }
                break;
            case EVERY_DISTINCT_EXPR:
                {
                alt288=4;
                }
                break;
            case PATTERN_NOT_EXPR:
                {
                alt288=5;
                }
                break;
            case GUARD_EXPR:
                {
                alt288=6;
                }
                break;
            case MATCH_UNTIL_EXPR:
                {
                alt288=7;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 288, 0, input);

                throw nvae;
            }

            switch (alt288) {
                case 1 :
                    // EsperEPL2Ast.g:795:5: atomicExpr
                    {
                    pushFollow(FOLLOW_atomicExpr_in_exprChoice5483);
                    atomicExpr();

                    state._fsp--;


                    }
                    break;
                case 2 :
                    // EsperEPL2Ast.g:796:4: patternOp
                    {
                    pushFollow(FOLLOW_patternOp_in_exprChoice5488);
                    patternOp();

                    state._fsp--;


                    }
                    break;
                case 3 :
                    // EsperEPL2Ast.g:797:5: ^(a= EVERY_EXPR exprChoice )
                    {
                    a=(CommonTree)match(input,EVERY_EXPR,FOLLOW_EVERY_EXPR_in_exprChoice5498); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_exprChoice_in_exprChoice5500);
                    exprChoice();

                    state._fsp--;

                     leaveNode(a); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 4 :
                    // EsperEPL2Ast.g:798:5: ^(a= EVERY_DISTINCT_EXPR distinctExpressions exprChoice )
                    {
                    a=(CommonTree)match(input,EVERY_DISTINCT_EXPR,FOLLOW_EVERY_DISTINCT_EXPR_in_exprChoice5514); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_distinctExpressions_in_exprChoice5516);
                    distinctExpressions();

                    state._fsp--;

                    pushFollow(FOLLOW_exprChoice_in_exprChoice5518);
                    exprChoice();

                    state._fsp--;

                     leaveNode(a); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 5 :
                    // EsperEPL2Ast.g:799:5: ^(n= PATTERN_NOT_EXPR exprChoice )
                    {
                    n=(CommonTree)match(input,PATTERN_NOT_EXPR,FOLLOW_PATTERN_NOT_EXPR_in_exprChoice5532); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_exprChoice_in_exprChoice5534);
                    exprChoice();

                    state._fsp--;

                     leaveNode(n); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 6 :
                    // EsperEPL2Ast.g:800:5: ^(g= GUARD_EXPR exprChoice ( IDENT IDENT ( valueExprWithTime )* | valueExpr ) )
                    {
                    g=(CommonTree)match(input,GUARD_EXPR,FOLLOW_GUARD_EXPR_in_exprChoice5548); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_exprChoice_in_exprChoice5550);
                    exprChoice();

                    state._fsp--;

                    // EsperEPL2Ast.g:800:32: ( IDENT IDENT ( valueExprWithTime )* | valueExpr )
                    int alt285=2;
                    int LA285_0 = input.LA(1);

                    if ( (LA285_0==IDENT) ) {
                        alt285=1;
                    }
                    else if ( ((LA285_0>=IN_SET && LA285_0<=REGEXP)||LA285_0==NOT_EXPR||(LA285_0>=SUM && LA285_0<=AVG)||(LA285_0>=COALESCE && LA285_0<=COUNT)||(LA285_0>=CASE && LA285_0<=CASE2)||LA285_0==ISTREAM||(LA285_0>=PREVIOUS && LA285_0<=EXISTS)||(LA285_0>=INSTANCEOF && LA285_0<=CURRENT_TIMESTAMP)||LA285_0==NEWKW||(LA285_0>=EVAL_AND_EXPR && LA285_0<=EVAL_NOTEQUALS_GROUP_EXPR)||LA285_0==EVENT_PROP_EXPR||LA285_0==CONCAT||(LA285_0>=LIB_FUNC_CHAIN && LA285_0<=DOT_EXPR)||LA285_0==ARRAY_EXPR||(LA285_0>=NOT_IN_SET && LA285_0<=NOT_REGEXP)||(LA285_0>=IN_RANGE && LA285_0<=SUBSELECT_EXPR)||(LA285_0>=EXISTS_SUBSELECT_EXPR && LA285_0<=NOT_IN_SUBSELECT_EXPR)||LA285_0==SUBSTITUTION||(LA285_0>=FIRST_AGGREG && LA285_0<=WINDOW_AGGREG)||(LA285_0>=INT_TYPE && LA285_0<=NULL_TYPE)||(LA285_0>=JSON_OBJECT && LA285_0<=JSON_ARRAY)||LA285_0==STAR||(LA285_0>=LT && LA285_0<=GT)||(LA285_0>=BOR && LA285_0<=PLUS)||(LA285_0>=BAND && LA285_0<=BXOR)||(LA285_0>=LE && LA285_0<=GE)||(LA285_0>=MINUS && LA285_0<=MOD)||(LA285_0>=EVAL_IS_GROUP_EXPR && LA285_0<=EVAL_ISNOT_GROUP_EXPR)) ) {
                        alt285=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 285, 0, input);

                        throw nvae;
                    }
                    switch (alt285) {
                        case 1 :
                            // EsperEPL2Ast.g:800:33: IDENT IDENT ( valueExprWithTime )*
                            {
                            match(input,IDENT,FOLLOW_IDENT_in_exprChoice5553); 
                            match(input,IDENT,FOLLOW_IDENT_in_exprChoice5555); 
                            // EsperEPL2Ast.g:800:45: ( valueExprWithTime )*
                            loop284:
                            do {
                                int alt284=2;
                                int LA284_0 = input.LA(1);

                                if ( ((LA284_0>=IN_SET && LA284_0<=REGEXP)||LA284_0==NOT_EXPR||(LA284_0>=SUM && LA284_0<=AVG)||(LA284_0>=COALESCE && LA284_0<=COUNT)||(LA284_0>=CASE && LA284_0<=CASE2)||LA284_0==LAST||LA284_0==ISTREAM||(LA284_0>=PREVIOUS && LA284_0<=EXISTS)||(LA284_0>=LW && LA284_0<=CURRENT_TIMESTAMP)||LA284_0==NEWKW||(LA284_0>=NUMERIC_PARAM_RANGE && LA284_0<=OBJECT_PARAM_ORDERED_EXPR)||(LA284_0>=EVAL_AND_EXPR && LA284_0<=EVAL_NOTEQUALS_GROUP_EXPR)||LA284_0==EVENT_PROP_EXPR||LA284_0==CONCAT||(LA284_0>=LIB_FUNC_CHAIN && LA284_0<=DOT_EXPR)||(LA284_0>=TIME_PERIOD && LA284_0<=ARRAY_EXPR)||(LA284_0>=NOT_IN_SET && LA284_0<=NOT_REGEXP)||(LA284_0>=IN_RANGE && LA284_0<=SUBSELECT_EXPR)||(LA284_0>=EXISTS_SUBSELECT_EXPR && LA284_0<=NOT_IN_SUBSELECT_EXPR)||(LA284_0>=LAST_OPERATOR && LA284_0<=SUBSTITUTION)||LA284_0==NUMBERSETSTAR||(LA284_0>=FIRST_AGGREG && LA284_0<=WINDOW_AGGREG)||(LA284_0>=INT_TYPE && LA284_0<=NULL_TYPE)||(LA284_0>=JSON_OBJECT && LA284_0<=JSON_ARRAY)||LA284_0==STAR||(LA284_0>=LT && LA284_0<=GT)||(LA284_0>=BOR && LA284_0<=PLUS)||(LA284_0>=BAND && LA284_0<=BXOR)||(LA284_0>=LE && LA284_0<=GE)||(LA284_0>=MINUS && LA284_0<=MOD)||(LA284_0>=EVAL_IS_GROUP_EXPR && LA284_0<=EVAL_ISNOT_GROUP_EXPR)) ) {
                                    alt284=1;
                                }


                                switch (alt284) {
                            	case 1 :
                            	    // EsperEPL2Ast.g:800:45: valueExprWithTime
                            	    {
                            	    pushFollow(FOLLOW_valueExprWithTime_in_exprChoice5557);
                            	    valueExprWithTime();

                            	    state._fsp--;


                            	    }
                            	    break;

                            	default :
                            	    break loop284;
                                }
                            } while (true);


                            }
                            break;
                        case 2 :
                            // EsperEPL2Ast.g:800:66: valueExpr
                            {
                            pushFollow(FOLLOW_valueExpr_in_exprChoice5562);
                            valueExpr();

                            state._fsp--;


                            }
                            break;

                    }

                     leaveNode(g); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 7 :
                    // EsperEPL2Ast.g:801:4: ^(m= MATCH_UNTIL_EXPR ( matchUntilRange )? exprChoice ( exprChoice )? )
                    {
                    m=(CommonTree)match(input,MATCH_UNTIL_EXPR,FOLLOW_MATCH_UNTIL_EXPR_in_exprChoice5576); 

                    match(input, Token.DOWN, null); 
                    // EsperEPL2Ast.g:801:26: ( matchUntilRange )?
                    int alt286=2;
                    int LA286_0 = input.LA(1);

                    if ( ((LA286_0>=MATCH_UNTIL_RANGE_HALFOPEN && LA286_0<=MATCH_UNTIL_RANGE_BOUNDED)) ) {
                        alt286=1;
                    }
                    switch (alt286) {
                        case 1 :
                            // EsperEPL2Ast.g:801:26: matchUntilRange
                            {
                            pushFollow(FOLLOW_matchUntilRange_in_exprChoice5578);
                            matchUntilRange();

                            state._fsp--;


                            }
                            break;

                    }

                    pushFollow(FOLLOW_exprChoice_in_exprChoice5581);
                    exprChoice();

                    state._fsp--;

                    // EsperEPL2Ast.g:801:54: ( exprChoice )?
                    int alt287=2;
                    int LA287_0 = input.LA(1);

                    if ( ((LA287_0>=OR_EXPR && LA287_0<=AND_EXPR)||(LA287_0>=EVERY_EXPR && LA287_0<=EVERY_DISTINCT_EXPR)||LA287_0==FOLLOWED_BY_EXPR||(LA287_0>=PATTERN_FILTER_EXPR && LA287_0<=PATTERN_NOT_EXPR)||(LA287_0>=GUARD_EXPR && LA287_0<=OBSERVER_EXPR)||LA287_0==MATCH_UNTIL_EXPR) ) {
                        alt287=1;
                    }
                    switch (alt287) {
                        case 1 :
                            // EsperEPL2Ast.g:801:54: exprChoice
                            {
                            pushFollow(FOLLOW_exprChoice_in_exprChoice5583);
                            exprChoice();

                            state._fsp--;


                            }
                            break;

                    }

                     leaveNode(m); 

                    match(input, Token.UP, null); 

                    }
                    break;

            }
        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "exprChoice"


    // $ANTLR start "distinctExpressions"
    // EsperEPL2Ast.g:805:1: distinctExpressions : ^( PATTERN_EVERY_DISTINCT_EXPR ( valueExprWithTime )+ ) ;
    public final void distinctExpressions() throws RecognitionException {
        try {
            // EsperEPL2Ast.g:806:2: ( ^( PATTERN_EVERY_DISTINCT_EXPR ( valueExprWithTime )+ ) )
            // EsperEPL2Ast.g:806:4: ^( PATTERN_EVERY_DISTINCT_EXPR ( valueExprWithTime )+ )
            {
            match(input,PATTERN_EVERY_DISTINCT_EXPR,FOLLOW_PATTERN_EVERY_DISTINCT_EXPR_in_distinctExpressions5604); 

            match(input, Token.DOWN, null); 
            // EsperEPL2Ast.g:806:35: ( valueExprWithTime )+
            int cnt289=0;
            loop289:
            do {
                int alt289=2;
                int LA289_0 = input.LA(1);

                if ( ((LA289_0>=IN_SET && LA289_0<=REGEXP)||LA289_0==NOT_EXPR||(LA289_0>=SUM && LA289_0<=AVG)||(LA289_0>=COALESCE && LA289_0<=COUNT)||(LA289_0>=CASE && LA289_0<=CASE2)||LA289_0==LAST||LA289_0==ISTREAM||(LA289_0>=PREVIOUS && LA289_0<=EXISTS)||(LA289_0>=LW && LA289_0<=CURRENT_TIMESTAMP)||LA289_0==NEWKW||(LA289_0>=NUMERIC_PARAM_RANGE && LA289_0<=OBJECT_PARAM_ORDERED_EXPR)||(LA289_0>=EVAL_AND_EXPR && LA289_0<=EVAL_NOTEQUALS_GROUP_EXPR)||LA289_0==EVENT_PROP_EXPR||LA289_0==CONCAT||(LA289_0>=LIB_FUNC_CHAIN && LA289_0<=DOT_EXPR)||(LA289_0>=TIME_PERIOD && LA289_0<=ARRAY_EXPR)||(LA289_0>=NOT_IN_SET && LA289_0<=NOT_REGEXP)||(LA289_0>=IN_RANGE && LA289_0<=SUBSELECT_EXPR)||(LA289_0>=EXISTS_SUBSELECT_EXPR && LA289_0<=NOT_IN_SUBSELECT_EXPR)||(LA289_0>=LAST_OPERATOR && LA289_0<=SUBSTITUTION)||LA289_0==NUMBERSETSTAR||(LA289_0>=FIRST_AGGREG && LA289_0<=WINDOW_AGGREG)||(LA289_0>=INT_TYPE && LA289_0<=NULL_TYPE)||(LA289_0>=JSON_OBJECT && LA289_0<=JSON_ARRAY)||LA289_0==STAR||(LA289_0>=LT && LA289_0<=GT)||(LA289_0>=BOR && LA289_0<=PLUS)||(LA289_0>=BAND && LA289_0<=BXOR)||(LA289_0>=LE && LA289_0<=GE)||(LA289_0>=MINUS && LA289_0<=MOD)||(LA289_0>=EVAL_IS_GROUP_EXPR && LA289_0<=EVAL_ISNOT_GROUP_EXPR)) ) {
                    alt289=1;
                }


                switch (alt289) {
            	case 1 :
            	    // EsperEPL2Ast.g:806:35: valueExprWithTime
            	    {
            	    pushFollow(FOLLOW_valueExprWithTime_in_distinctExpressions5606);
            	    valueExprWithTime();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    if ( cnt289 >= 1 ) break loop289;
                        EarlyExitException eee =
                            new EarlyExitException(289, input);
                        throw eee;
                }
                cnt289++;
            } while (true);


            match(input, Token.UP, null); 

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "distinctExpressions"


    // $ANTLR start "patternOp"
    // EsperEPL2Ast.g:809:1: patternOp : ( ^(f= FOLLOWED_BY_EXPR followedByItem followedByItem ( followedByItem )* ) | ^(o= OR_EXPR exprChoice exprChoice ( exprChoice )* ) | ^(a= AND_EXPR exprChoice exprChoice ( exprChoice )* ) );
    public final void patternOp() throws RecognitionException {
        CommonTree f=null;
        CommonTree o=null;
        CommonTree a=null;

        try {
            // EsperEPL2Ast.g:810:2: ( ^(f= FOLLOWED_BY_EXPR followedByItem followedByItem ( followedByItem )* ) | ^(o= OR_EXPR exprChoice exprChoice ( exprChoice )* ) | ^(a= AND_EXPR exprChoice exprChoice ( exprChoice )* ) )
            int alt293=3;
            switch ( input.LA(1) ) {
            case FOLLOWED_BY_EXPR:
                {
                alt293=1;
                }
                break;
            case OR_EXPR:
                {
                alt293=2;
                }
                break;
            case AND_EXPR:
                {
                alt293=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 293, 0, input);

                throw nvae;
            }

            switch (alt293) {
                case 1 :
                    // EsperEPL2Ast.g:810:4: ^(f= FOLLOWED_BY_EXPR followedByItem followedByItem ( followedByItem )* )
                    {
                    f=(CommonTree)match(input,FOLLOWED_BY_EXPR,FOLLOW_FOLLOWED_BY_EXPR_in_patternOp5625); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_followedByItem_in_patternOp5627);
                    followedByItem();

                    state._fsp--;

                    pushFollow(FOLLOW_followedByItem_in_patternOp5629);
                    followedByItem();

                    state._fsp--;

                    // EsperEPL2Ast.g:810:56: ( followedByItem )*
                    loop290:
                    do {
                        int alt290=2;
                        int LA290_0 = input.LA(1);

                        if ( (LA290_0==FOLLOWED_BY_ITEM) ) {
                            alt290=1;
                        }


                        switch (alt290) {
                    	case 1 :
                    	    // EsperEPL2Ast.g:810:57: followedByItem
                    	    {
                    	    pushFollow(FOLLOW_followedByItem_in_patternOp5632);
                    	    followedByItem();

                    	    state._fsp--;


                    	    }
                    	    break;

                    	default :
                    	    break loop290;
                        }
                    } while (true);

                     leaveNode(f); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    // EsperEPL2Ast.g:811:5: ^(o= OR_EXPR exprChoice exprChoice ( exprChoice )* )
                    {
                    o=(CommonTree)match(input,OR_EXPR,FOLLOW_OR_EXPR_in_patternOp5648); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_exprChoice_in_patternOp5650);
                    exprChoice();

                    state._fsp--;

                    pushFollow(FOLLOW_exprChoice_in_patternOp5652);
                    exprChoice();

                    state._fsp--;

                    // EsperEPL2Ast.g:811:40: ( exprChoice )*
                    loop291:
                    do {
                        int alt291=2;
                        int LA291_0 = input.LA(1);

                        if ( ((LA291_0>=OR_EXPR && LA291_0<=AND_EXPR)||(LA291_0>=EVERY_EXPR && LA291_0<=EVERY_DISTINCT_EXPR)||LA291_0==FOLLOWED_BY_EXPR||(LA291_0>=PATTERN_FILTER_EXPR && LA291_0<=PATTERN_NOT_EXPR)||(LA291_0>=GUARD_EXPR && LA291_0<=OBSERVER_EXPR)||LA291_0==MATCH_UNTIL_EXPR) ) {
                            alt291=1;
                        }


                        switch (alt291) {
                    	case 1 :
                    	    // EsperEPL2Ast.g:811:41: exprChoice
                    	    {
                    	    pushFollow(FOLLOW_exprChoice_in_patternOp5655);
                    	    exprChoice();

                    	    state._fsp--;


                    	    }
                    	    break;

                    	default :
                    	    break loop291;
                        }
                    } while (true);

                     leaveNode(o); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 3 :
                    // EsperEPL2Ast.g:812:5: ^(a= AND_EXPR exprChoice exprChoice ( exprChoice )* )
                    {
                    a=(CommonTree)match(input,AND_EXPR,FOLLOW_AND_EXPR_in_patternOp5671); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_exprChoice_in_patternOp5673);
                    exprChoice();

                    state._fsp--;

                    pushFollow(FOLLOW_exprChoice_in_patternOp5675);
                    exprChoice();

                    state._fsp--;

                    // EsperEPL2Ast.g:812:41: ( exprChoice )*
                    loop292:
                    do {
                        int alt292=2;
                        int LA292_0 = input.LA(1);

                        if ( ((LA292_0>=OR_EXPR && LA292_0<=AND_EXPR)||(LA292_0>=EVERY_EXPR && LA292_0<=EVERY_DISTINCT_EXPR)||LA292_0==FOLLOWED_BY_EXPR||(LA292_0>=PATTERN_FILTER_EXPR && LA292_0<=PATTERN_NOT_EXPR)||(LA292_0>=GUARD_EXPR && LA292_0<=OBSERVER_EXPR)||LA292_0==MATCH_UNTIL_EXPR) ) {
                            alt292=1;
                        }


                        switch (alt292) {
                    	case 1 :
                    	    // EsperEPL2Ast.g:812:42: exprChoice
                    	    {
                    	    pushFollow(FOLLOW_exprChoice_in_patternOp5678);
                    	    exprChoice();

                    	    state._fsp--;


                    	    }
                    	    break;

                    	default :
                    	    break loop292;
                        }
                    } while (true);

                     leaveNode(a); 

                    match(input, Token.UP, null); 

                    }
                    break;

            }
        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "patternOp"


    // $ANTLR start "followedByItem"
    // EsperEPL2Ast.g:815:1: followedByItem : ^( FOLLOWED_BY_ITEM ( valueExpr )? exprChoice ) ;
    public final void followedByItem() throws RecognitionException {
        try {
            // EsperEPL2Ast.g:816:2: ( ^( FOLLOWED_BY_ITEM ( valueExpr )? exprChoice ) )
            // EsperEPL2Ast.g:816:4: ^( FOLLOWED_BY_ITEM ( valueExpr )? exprChoice )
            {
            match(input,FOLLOWED_BY_ITEM,FOLLOW_FOLLOWED_BY_ITEM_in_followedByItem5699); 

            match(input, Token.DOWN, null); 
            // EsperEPL2Ast.g:816:24: ( valueExpr )?
            int alt294=2;
            int LA294_0 = input.LA(1);

            if ( ((LA294_0>=IN_SET && LA294_0<=REGEXP)||LA294_0==NOT_EXPR||(LA294_0>=SUM && LA294_0<=AVG)||(LA294_0>=COALESCE && LA294_0<=COUNT)||(LA294_0>=CASE && LA294_0<=CASE2)||LA294_0==ISTREAM||(LA294_0>=PREVIOUS && LA294_0<=EXISTS)||(LA294_0>=INSTANCEOF && LA294_0<=CURRENT_TIMESTAMP)||LA294_0==NEWKW||(LA294_0>=EVAL_AND_EXPR && LA294_0<=EVAL_NOTEQUALS_GROUP_EXPR)||LA294_0==EVENT_PROP_EXPR||LA294_0==CONCAT||(LA294_0>=LIB_FUNC_CHAIN && LA294_0<=DOT_EXPR)||LA294_0==ARRAY_EXPR||(LA294_0>=NOT_IN_SET && LA294_0<=NOT_REGEXP)||(LA294_0>=IN_RANGE && LA294_0<=SUBSELECT_EXPR)||(LA294_0>=EXISTS_SUBSELECT_EXPR && LA294_0<=NOT_IN_SUBSELECT_EXPR)||LA294_0==SUBSTITUTION||(LA294_0>=FIRST_AGGREG && LA294_0<=WINDOW_AGGREG)||(LA294_0>=INT_TYPE && LA294_0<=NULL_TYPE)||(LA294_0>=JSON_OBJECT && LA294_0<=JSON_ARRAY)||LA294_0==STAR||(LA294_0>=LT && LA294_0<=GT)||(LA294_0>=BOR && LA294_0<=PLUS)||(LA294_0>=BAND && LA294_0<=BXOR)||(LA294_0>=LE && LA294_0<=GE)||(LA294_0>=MINUS && LA294_0<=MOD)||(LA294_0>=EVAL_IS_GROUP_EXPR && LA294_0<=EVAL_ISNOT_GROUP_EXPR)) ) {
                alt294=1;
            }
            switch (alt294) {
                case 1 :
                    // EsperEPL2Ast.g:816:24: valueExpr
                    {
                    pushFollow(FOLLOW_valueExpr_in_followedByItem5701);
                    valueExpr();

                    state._fsp--;


                    }
                    break;

            }

            pushFollow(FOLLOW_exprChoice_in_followedByItem5704);
            exprChoice();

            state._fsp--;


            match(input, Token.UP, null); 

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "followedByItem"


    // $ANTLR start "atomicExpr"
    // EsperEPL2Ast.g:819:1: atomicExpr : ( patternFilterExpr | ^(ac= OBSERVER_EXPR IDENT IDENT ( valueExprWithTime )* ) );
    public final void atomicExpr() throws RecognitionException {
        CommonTree ac=null;

        try {
            // EsperEPL2Ast.g:820:2: ( patternFilterExpr | ^(ac= OBSERVER_EXPR IDENT IDENT ( valueExprWithTime )* ) )
            int alt296=2;
            int LA296_0 = input.LA(1);

            if ( (LA296_0==PATTERN_FILTER_EXPR) ) {
                alt296=1;
            }
            else if ( (LA296_0==OBSERVER_EXPR) ) {
                alt296=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 296, 0, input);

                throw nvae;
            }
            switch (alt296) {
                case 1 :
                    // EsperEPL2Ast.g:820:4: patternFilterExpr
                    {
                    pushFollow(FOLLOW_patternFilterExpr_in_atomicExpr5718);
                    patternFilterExpr();

                    state._fsp--;


                    }
                    break;
                case 2 :
                    // EsperEPL2Ast.g:821:7: ^(ac= OBSERVER_EXPR IDENT IDENT ( valueExprWithTime )* )
                    {
                    ac=(CommonTree)match(input,OBSERVER_EXPR,FOLLOW_OBSERVER_EXPR_in_atomicExpr5730); 

                    match(input, Token.DOWN, null); 
                    match(input,IDENT,FOLLOW_IDENT_in_atomicExpr5732); 
                    match(input,IDENT,FOLLOW_IDENT_in_atomicExpr5734); 
                    // EsperEPL2Ast.g:821:39: ( valueExprWithTime )*
                    loop295:
                    do {
                        int alt295=2;
                        int LA295_0 = input.LA(1);

                        if ( ((LA295_0>=IN_SET && LA295_0<=REGEXP)||LA295_0==NOT_EXPR||(LA295_0>=SUM && LA295_0<=AVG)||(LA295_0>=COALESCE && LA295_0<=COUNT)||(LA295_0>=CASE && LA295_0<=CASE2)||LA295_0==LAST||LA295_0==ISTREAM||(LA295_0>=PREVIOUS && LA295_0<=EXISTS)||(LA295_0>=LW && LA295_0<=CURRENT_TIMESTAMP)||LA295_0==NEWKW||(LA295_0>=NUMERIC_PARAM_RANGE && LA295_0<=OBJECT_PARAM_ORDERED_EXPR)||(LA295_0>=EVAL_AND_EXPR && LA295_0<=EVAL_NOTEQUALS_GROUP_EXPR)||LA295_0==EVENT_PROP_EXPR||LA295_0==CONCAT||(LA295_0>=LIB_FUNC_CHAIN && LA295_0<=DOT_EXPR)||(LA295_0>=TIME_PERIOD && LA295_0<=ARRAY_EXPR)||(LA295_0>=NOT_IN_SET && LA295_0<=NOT_REGEXP)||(LA295_0>=IN_RANGE && LA295_0<=SUBSELECT_EXPR)||(LA295_0>=EXISTS_SUBSELECT_EXPR && LA295_0<=NOT_IN_SUBSELECT_EXPR)||(LA295_0>=LAST_OPERATOR && LA295_0<=SUBSTITUTION)||LA295_0==NUMBERSETSTAR||(LA295_0>=FIRST_AGGREG && LA295_0<=WINDOW_AGGREG)||(LA295_0>=INT_TYPE && LA295_0<=NULL_TYPE)||(LA295_0>=JSON_OBJECT && LA295_0<=JSON_ARRAY)||LA295_0==STAR||(LA295_0>=LT && LA295_0<=GT)||(LA295_0>=BOR && LA295_0<=PLUS)||(LA295_0>=BAND && LA295_0<=BXOR)||(LA295_0>=LE && LA295_0<=GE)||(LA295_0>=MINUS && LA295_0<=MOD)||(LA295_0>=EVAL_IS_GROUP_EXPR && LA295_0<=EVAL_ISNOT_GROUP_EXPR)) ) {
                            alt295=1;
                        }


                        switch (alt295) {
                    	case 1 :
                    	    // EsperEPL2Ast.g:821:39: valueExprWithTime
                    	    {
                    	    pushFollow(FOLLOW_valueExprWithTime_in_atomicExpr5736);
                    	    valueExprWithTime();

                    	    state._fsp--;


                    	    }
                    	    break;

                    	default :
                    	    break loop295;
                        }
                    } while (true);

                     leaveNode(ac); 

                    match(input, Token.UP, null); 

                    }
                    break;

            }
        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "atomicExpr"


    // $ANTLR start "patternFilterExpr"
    // EsperEPL2Ast.g:824:1: patternFilterExpr : ^(f= PATTERN_FILTER_EXPR ( IDENT )? CLASS_IDENT ( propertyExpression )? ( patternFilterAnno )? ( valueExpr )* ) ;
    public final void patternFilterExpr() throws RecognitionException {
        CommonTree f=null;

        try {
            // EsperEPL2Ast.g:825:2: ( ^(f= PATTERN_FILTER_EXPR ( IDENT )? CLASS_IDENT ( propertyExpression )? ( patternFilterAnno )? ( valueExpr )* ) )
            // EsperEPL2Ast.g:825:4: ^(f= PATTERN_FILTER_EXPR ( IDENT )? CLASS_IDENT ( propertyExpression )? ( patternFilterAnno )? ( valueExpr )* )
            {
            f=(CommonTree)match(input,PATTERN_FILTER_EXPR,FOLLOW_PATTERN_FILTER_EXPR_in_patternFilterExpr5756); 

            match(input, Token.DOWN, null); 
            // EsperEPL2Ast.g:825:29: ( IDENT )?
            int alt297=2;
            int LA297_0 = input.LA(1);

            if ( (LA297_0==IDENT) ) {
                alt297=1;
            }
            switch (alt297) {
                case 1 :
                    // EsperEPL2Ast.g:825:29: IDENT
                    {
                    match(input,IDENT,FOLLOW_IDENT_in_patternFilterExpr5758); 

                    }
                    break;

            }

            match(input,CLASS_IDENT,FOLLOW_CLASS_IDENT_in_patternFilterExpr5761); 
            // EsperEPL2Ast.g:825:48: ( propertyExpression )?
            int alt298=2;
            int LA298_0 = input.LA(1);

            if ( (LA298_0==EVENT_FILTER_PROPERTY_EXPR) ) {
                alt298=1;
            }
            switch (alt298) {
                case 1 :
                    // EsperEPL2Ast.g:825:48: propertyExpression
                    {
                    pushFollow(FOLLOW_propertyExpression_in_patternFilterExpr5763);
                    propertyExpression();

                    state._fsp--;


                    }
                    break;

            }

            // EsperEPL2Ast.g:825:68: ( patternFilterAnno )?
            int alt299=2;
            int LA299_0 = input.LA(1);

            if ( (LA299_0==ATCHAR) ) {
                alt299=1;
            }
            switch (alt299) {
                case 1 :
                    // EsperEPL2Ast.g:825:68: patternFilterAnno
                    {
                    pushFollow(FOLLOW_patternFilterAnno_in_patternFilterExpr5766);
                    patternFilterAnno();

                    state._fsp--;


                    }
                    break;

            }

            // EsperEPL2Ast.g:825:87: ( valueExpr )*
            loop300:
            do {
                int alt300=2;
                int LA300_0 = input.LA(1);

                if ( ((LA300_0>=IN_SET && LA300_0<=REGEXP)||LA300_0==NOT_EXPR||(LA300_0>=SUM && LA300_0<=AVG)||(LA300_0>=COALESCE && LA300_0<=COUNT)||(LA300_0>=CASE && LA300_0<=CASE2)||LA300_0==ISTREAM||(LA300_0>=PREVIOUS && LA300_0<=EXISTS)||(LA300_0>=INSTANCEOF && LA300_0<=CURRENT_TIMESTAMP)||LA300_0==NEWKW||(LA300_0>=EVAL_AND_EXPR && LA300_0<=EVAL_NOTEQUALS_GROUP_EXPR)||LA300_0==EVENT_PROP_EXPR||LA300_0==CONCAT||(LA300_0>=LIB_FUNC_CHAIN && LA300_0<=DOT_EXPR)||LA300_0==ARRAY_EXPR||(LA300_0>=NOT_IN_SET && LA300_0<=NOT_REGEXP)||(LA300_0>=IN_RANGE && LA300_0<=SUBSELECT_EXPR)||(LA300_0>=EXISTS_SUBSELECT_EXPR && LA300_0<=NOT_IN_SUBSELECT_EXPR)||LA300_0==SUBSTITUTION||(LA300_0>=FIRST_AGGREG && LA300_0<=WINDOW_AGGREG)||(LA300_0>=INT_TYPE && LA300_0<=NULL_TYPE)||(LA300_0>=JSON_OBJECT && LA300_0<=JSON_ARRAY)||LA300_0==STAR||(LA300_0>=LT && LA300_0<=GT)||(LA300_0>=BOR && LA300_0<=PLUS)||(LA300_0>=BAND && LA300_0<=BXOR)||(LA300_0>=LE && LA300_0<=GE)||(LA300_0>=MINUS && LA300_0<=MOD)||(LA300_0>=EVAL_IS_GROUP_EXPR && LA300_0<=EVAL_ISNOT_GROUP_EXPR)) ) {
                    alt300=1;
                }


                switch (alt300) {
            	case 1 :
            	    // EsperEPL2Ast.g:825:88: valueExpr
            	    {
            	    pushFollow(FOLLOW_valueExpr_in_patternFilterExpr5770);
            	    valueExpr();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop300;
                }
            } while (true);

             leaveNode(f); 

            match(input, Token.UP, null); 

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "patternFilterExpr"


    // $ANTLR start "patternFilterAnno"
    // EsperEPL2Ast.g:828:1: patternFilterAnno : ^( ATCHAR IDENT ( number )? ) ;
    public final void patternFilterAnno() throws RecognitionException {
        try {
            // EsperEPL2Ast.g:829:2: ( ^( ATCHAR IDENT ( number )? ) )
            // EsperEPL2Ast.g:829:4: ^( ATCHAR IDENT ( number )? )
            {
            match(input,ATCHAR,FOLLOW_ATCHAR_in_patternFilterAnno5790); 

            match(input, Token.DOWN, null); 
            match(input,IDENT,FOLLOW_IDENT_in_patternFilterAnno5792); 
            // EsperEPL2Ast.g:829:20: ( number )?
            int alt301=2;
            int LA301_0 = input.LA(1);

            if ( ((LA301_0>=INT_TYPE && LA301_0<=DOUBLE_TYPE)) ) {
                alt301=1;
            }
            switch (alt301) {
                case 1 :
                    // EsperEPL2Ast.g:829:20: number
                    {
                    pushFollow(FOLLOW_number_in_patternFilterAnno5794);
                    number();

                    state._fsp--;


                    }
                    break;

            }


            match(input, Token.UP, null); 

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "patternFilterAnno"


    // $ANTLR start "matchUntilRange"
    // EsperEPL2Ast.g:832:1: matchUntilRange : ( ^( MATCH_UNTIL_RANGE_CLOSED valueExpr valueExpr ) | ^( MATCH_UNTIL_RANGE_BOUNDED valueExpr ) | ^( MATCH_UNTIL_RANGE_HALFCLOSED valueExpr ) | ^( MATCH_UNTIL_RANGE_HALFOPEN valueExpr ) );
    public final void matchUntilRange() throws RecognitionException {
        try {
            // EsperEPL2Ast.g:833:2: ( ^( MATCH_UNTIL_RANGE_CLOSED valueExpr valueExpr ) | ^( MATCH_UNTIL_RANGE_BOUNDED valueExpr ) | ^( MATCH_UNTIL_RANGE_HALFCLOSED valueExpr ) | ^( MATCH_UNTIL_RANGE_HALFOPEN valueExpr ) )
            int alt302=4;
            switch ( input.LA(1) ) {
            case MATCH_UNTIL_RANGE_CLOSED:
                {
                alt302=1;
                }
                break;
            case MATCH_UNTIL_RANGE_BOUNDED:
                {
                alt302=2;
                }
                break;
            case MATCH_UNTIL_RANGE_HALFCLOSED:
                {
                alt302=3;
                }
                break;
            case MATCH_UNTIL_RANGE_HALFOPEN:
                {
                alt302=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 302, 0, input);

                throw nvae;
            }

            switch (alt302) {
                case 1 :
                    // EsperEPL2Ast.g:833:4: ^( MATCH_UNTIL_RANGE_CLOSED valueExpr valueExpr )
                    {
                    match(input,MATCH_UNTIL_RANGE_CLOSED,FOLLOW_MATCH_UNTIL_RANGE_CLOSED_in_matchUntilRange5809); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_valueExpr_in_matchUntilRange5811);
                    valueExpr();

                    state._fsp--;

                    pushFollow(FOLLOW_valueExpr_in_matchUntilRange5813);
                    valueExpr();

                    state._fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    // EsperEPL2Ast.g:834:5: ^( MATCH_UNTIL_RANGE_BOUNDED valueExpr )
                    {
                    match(input,MATCH_UNTIL_RANGE_BOUNDED,FOLLOW_MATCH_UNTIL_RANGE_BOUNDED_in_matchUntilRange5821); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_valueExpr_in_matchUntilRange5823);
                    valueExpr();

                    state._fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 3 :
                    // EsperEPL2Ast.g:835:5: ^( MATCH_UNTIL_RANGE_HALFCLOSED valueExpr )
                    {
                    match(input,MATCH_UNTIL_RANGE_HALFCLOSED,FOLLOW_MATCH_UNTIL_RANGE_HALFCLOSED_in_matchUntilRange5831); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_valueExpr_in_matchUntilRange5833);
                    valueExpr();

                    state._fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 4 :
                    // EsperEPL2Ast.g:836:4: ^( MATCH_UNTIL_RANGE_HALFOPEN valueExpr )
                    {
                    match(input,MATCH_UNTIL_RANGE_HALFOPEN,FOLLOW_MATCH_UNTIL_RANGE_HALFOPEN_in_matchUntilRange5840); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_valueExpr_in_matchUntilRange5842);
                    valueExpr();

                    state._fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;

            }
        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "matchUntilRange"


    // $ANTLR start "filterParam"
    // EsperEPL2Ast.g:839:1: filterParam : ^( EVENT_FILTER_PARAM valueExpr ( valueExpr )* ) ;
    public final void filterParam() throws RecognitionException {
        try {
            // EsperEPL2Ast.g:840:2: ( ^( EVENT_FILTER_PARAM valueExpr ( valueExpr )* ) )
            // EsperEPL2Ast.g:840:4: ^( EVENT_FILTER_PARAM valueExpr ( valueExpr )* )
            {
            match(input,EVENT_FILTER_PARAM,FOLLOW_EVENT_FILTER_PARAM_in_filterParam5855); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_valueExpr_in_filterParam5857);
            valueExpr();

            state._fsp--;

            // EsperEPL2Ast.g:840:35: ( valueExpr )*
            loop303:
            do {
                int alt303=2;
                int LA303_0 = input.LA(1);

                if ( ((LA303_0>=IN_SET && LA303_0<=REGEXP)||LA303_0==NOT_EXPR||(LA303_0>=SUM && LA303_0<=AVG)||(LA303_0>=COALESCE && LA303_0<=COUNT)||(LA303_0>=CASE && LA303_0<=CASE2)||LA303_0==ISTREAM||(LA303_0>=PREVIOUS && LA303_0<=EXISTS)||(LA303_0>=INSTANCEOF && LA303_0<=CURRENT_TIMESTAMP)||LA303_0==NEWKW||(LA303_0>=EVAL_AND_EXPR && LA303_0<=EVAL_NOTEQUALS_GROUP_EXPR)||LA303_0==EVENT_PROP_EXPR||LA303_0==CONCAT||(LA303_0>=LIB_FUNC_CHAIN && LA303_0<=DOT_EXPR)||LA303_0==ARRAY_EXPR||(LA303_0>=NOT_IN_SET && LA303_0<=NOT_REGEXP)||(LA303_0>=IN_RANGE && LA303_0<=SUBSELECT_EXPR)||(LA303_0>=EXISTS_SUBSELECT_EXPR && LA303_0<=NOT_IN_SUBSELECT_EXPR)||LA303_0==SUBSTITUTION||(LA303_0>=FIRST_AGGREG && LA303_0<=WINDOW_AGGREG)||(LA303_0>=INT_TYPE && LA303_0<=NULL_TYPE)||(LA303_0>=JSON_OBJECT && LA303_0<=JSON_ARRAY)||LA303_0==STAR||(LA303_0>=LT && LA303_0<=GT)||(LA303_0>=BOR && LA303_0<=PLUS)||(LA303_0>=BAND && LA303_0<=BXOR)||(LA303_0>=LE && LA303_0<=GE)||(LA303_0>=MINUS && LA303_0<=MOD)||(LA303_0>=EVAL_IS_GROUP_EXPR && LA303_0<=EVAL_ISNOT_GROUP_EXPR)) ) {
                    alt303=1;
                }


                switch (alt303) {
            	case 1 :
            	    // EsperEPL2Ast.g:840:36: valueExpr
            	    {
            	    pushFollow(FOLLOW_valueExpr_in_filterParam5860);
            	    valueExpr();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop303;
                }
            } while (true);


            match(input, Token.UP, null); 

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "filterParam"


    // $ANTLR start "filterParamComparator"
    // EsperEPL2Ast.g:843:1: filterParamComparator : ( ^( EQUALS filterAtom ) | ^( NOT_EQUAL filterAtom ) | ^( LT filterAtom ) | ^( LE filterAtom ) | ^( GT filterAtom ) | ^( GE filterAtom ) | ^( EVENT_FILTER_RANGE ( LPAREN | LBRACK ) ( constant[false] | filterIdentifier ) ( constant[false] | filterIdentifier ) ( RPAREN | RBRACK ) ) | ^( EVENT_FILTER_NOT_RANGE ( LPAREN | LBRACK ) ( constant[false] | filterIdentifier ) ( constant[false] | filterIdentifier ) ( RPAREN | RBRACK ) ) | ^( EVENT_FILTER_IN ( LPAREN | LBRACK ) ( constant[false] | filterIdentifier ) ( constant[false] | filterIdentifier )* ( RPAREN | RBRACK ) ) | ^( EVENT_FILTER_NOT_IN ( LPAREN | LBRACK ) ( constant[false] | filterIdentifier ) ( constant[false] | filterIdentifier )* ( RPAREN | RBRACK ) ) | ^( EVENT_FILTER_BETWEEN ( constant[false] | filterIdentifier ) ( constant[false] | filterIdentifier ) ) | ^( EVENT_FILTER_NOT_BETWEEN ( constant[false] | filterIdentifier ) ( constant[false] | filterIdentifier ) ) );
    public final void filterParamComparator() throws RecognitionException {
        try {
            // EsperEPL2Ast.g:844:2: ( ^( EQUALS filterAtom ) | ^( NOT_EQUAL filterAtom ) | ^( LT filterAtom ) | ^( LE filterAtom ) | ^( GT filterAtom ) | ^( GE filterAtom ) | ^( EVENT_FILTER_RANGE ( LPAREN | LBRACK ) ( constant[false] | filterIdentifier ) ( constant[false] | filterIdentifier ) ( RPAREN | RBRACK ) ) | ^( EVENT_FILTER_NOT_RANGE ( LPAREN | LBRACK ) ( constant[false] | filterIdentifier ) ( constant[false] | filterIdentifier ) ( RPAREN | RBRACK ) ) | ^( EVENT_FILTER_IN ( LPAREN | LBRACK ) ( constant[false] | filterIdentifier ) ( constant[false] | filterIdentifier )* ( RPAREN | RBRACK ) ) | ^( EVENT_FILTER_NOT_IN ( LPAREN | LBRACK ) ( constant[false] | filterIdentifier ) ( constant[false] | filterIdentifier )* ( RPAREN | RBRACK ) ) | ^( EVENT_FILTER_BETWEEN ( constant[false] | filterIdentifier ) ( constant[false] | filterIdentifier ) ) | ^( EVENT_FILTER_NOT_BETWEEN ( constant[false] | filterIdentifier ) ( constant[false] | filterIdentifier ) ) )
            int alt316=12;
            switch ( input.LA(1) ) {
            case EQUALS:
                {
                alt316=1;
                }
                break;
            case NOT_EQUAL:
                {
                alt316=2;
                }
                break;
            case LT:
                {
                alt316=3;
                }
                break;
            case LE:
                {
                alt316=4;
                }
                break;
            case GT:
                {
                alt316=5;
                }
                break;
            case GE:
                {
                alt316=6;
                }
                break;
            case EVENT_FILTER_RANGE:
                {
                alt316=7;
                }
                break;
            case EVENT_FILTER_NOT_RANGE:
                {
                alt316=8;
                }
                break;
            case EVENT_FILTER_IN:
                {
                alt316=9;
                }
                break;
            case EVENT_FILTER_NOT_IN:
                {
                alt316=10;
                }
                break;
            case EVENT_FILTER_BETWEEN:
                {
                alt316=11;
                }
                break;
            case EVENT_FILTER_NOT_BETWEEN:
                {
                alt316=12;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 316, 0, input);

                throw nvae;
            }

            switch (alt316) {
                case 1 :
                    // EsperEPL2Ast.g:844:4: ^( EQUALS filterAtom )
                    {
                    match(input,EQUALS,FOLLOW_EQUALS_in_filterParamComparator5876); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_filterAtom_in_filterParamComparator5878);
                    filterAtom();

                    state._fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    // EsperEPL2Ast.g:845:4: ^( NOT_EQUAL filterAtom )
                    {
                    match(input,NOT_EQUAL,FOLLOW_NOT_EQUAL_in_filterParamComparator5885); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_filterAtom_in_filterParamComparator5887);
                    filterAtom();

                    state._fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 3 :
                    // EsperEPL2Ast.g:846:4: ^( LT filterAtom )
                    {
                    match(input,LT,FOLLOW_LT_in_filterParamComparator5894); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_filterAtom_in_filterParamComparator5896);
                    filterAtom();

                    state._fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 4 :
                    // EsperEPL2Ast.g:847:4: ^( LE filterAtom )
                    {
                    match(input,LE,FOLLOW_LE_in_filterParamComparator5903); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_filterAtom_in_filterParamComparator5905);
                    filterAtom();

                    state._fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 5 :
                    // EsperEPL2Ast.g:848:4: ^( GT filterAtom )
                    {
                    match(input,GT,FOLLOW_GT_in_filterParamComparator5912); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_filterAtom_in_filterParamComparator5914);
                    filterAtom();

                    state._fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 6 :
                    // EsperEPL2Ast.g:849:4: ^( GE filterAtom )
                    {
                    match(input,GE,FOLLOW_GE_in_filterParamComparator5921); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_filterAtom_in_filterParamComparator5923);
                    filterAtom();

                    state._fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 7 :
                    // EsperEPL2Ast.g:850:4: ^( EVENT_FILTER_RANGE ( LPAREN | LBRACK ) ( constant[false] | filterIdentifier ) ( constant[false] | filterIdentifier ) ( RPAREN | RBRACK ) )
                    {
                    match(input,EVENT_FILTER_RANGE,FOLLOW_EVENT_FILTER_RANGE_in_filterParamComparator5930); 

                    match(input, Token.DOWN, null); 
                    if ( input.LA(1)==LBRACK||input.LA(1)==LPAREN ) {
                        input.consume();
                        state.errorRecovery=false;
                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        throw mse;
                    }

                    // EsperEPL2Ast.g:850:41: ( constant[false] | filterIdentifier )
                    int alt304=2;
                    int LA304_0 = input.LA(1);

                    if ( ((LA304_0>=INT_TYPE && LA304_0<=NULL_TYPE)) ) {
                        alt304=1;
                    }
                    else if ( (LA304_0==EVENT_FILTER_IDENT) ) {
                        alt304=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 304, 0, input);

                        throw nvae;
                    }
                    switch (alt304) {
                        case 1 :
                            // EsperEPL2Ast.g:850:42: constant[false]
                            {
                            pushFollow(FOLLOW_constant_in_filterParamComparator5939);
                            constant(false);

                            state._fsp--;


                            }
                            break;
                        case 2 :
                            // EsperEPL2Ast.g:850:58: filterIdentifier
                            {
                            pushFollow(FOLLOW_filterIdentifier_in_filterParamComparator5942);
                            filterIdentifier();

                            state._fsp--;


                            }
                            break;

                    }

                    // EsperEPL2Ast.g:850:76: ( constant[false] | filterIdentifier )
                    int alt305=2;
                    int LA305_0 = input.LA(1);

                    if ( ((LA305_0>=INT_TYPE && LA305_0<=NULL_TYPE)) ) {
                        alt305=1;
                    }
                    else if ( (LA305_0==EVENT_FILTER_IDENT) ) {
                        alt305=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 305, 0, input);

                        throw nvae;
                    }
                    switch (alt305) {
                        case 1 :
                            // EsperEPL2Ast.g:850:77: constant[false]
                            {
                            pushFollow(FOLLOW_constant_in_filterParamComparator5946);
                            constant(false);

                            state._fsp--;


                            }
                            break;
                        case 2 :
                            // EsperEPL2Ast.g:850:93: filterIdentifier
                            {
                            pushFollow(FOLLOW_filterIdentifier_in_filterParamComparator5949);
                            filterIdentifier();

                            state._fsp--;


                            }
                            break;

                    }

                    if ( input.LA(1)==RBRACK||input.LA(1)==RPAREN ) {
                        input.consume();
                        state.errorRecovery=false;
                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        throw mse;
                    }


                    match(input, Token.UP, null); 

                    }
                    break;
                case 8 :
                    // EsperEPL2Ast.g:851:4: ^( EVENT_FILTER_NOT_RANGE ( LPAREN | LBRACK ) ( constant[false] | filterIdentifier ) ( constant[false] | filterIdentifier ) ( RPAREN | RBRACK ) )
                    {
                    match(input,EVENT_FILTER_NOT_RANGE,FOLLOW_EVENT_FILTER_NOT_RANGE_in_filterParamComparator5963); 

                    match(input, Token.DOWN, null); 
                    if ( input.LA(1)==LBRACK||input.LA(1)==LPAREN ) {
                        input.consume();
                        state.errorRecovery=false;
                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        throw mse;
                    }

                    // EsperEPL2Ast.g:851:45: ( constant[false] | filterIdentifier )
                    int alt306=2;
                    int LA306_0 = input.LA(1);

                    if ( ((LA306_0>=INT_TYPE && LA306_0<=NULL_TYPE)) ) {
                        alt306=1;
                    }
                    else if ( (LA306_0==EVENT_FILTER_IDENT) ) {
                        alt306=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 306, 0, input);

                        throw nvae;
                    }
                    switch (alt306) {
                        case 1 :
                            // EsperEPL2Ast.g:851:46: constant[false]
                            {
                            pushFollow(FOLLOW_constant_in_filterParamComparator5972);
                            constant(false);

                            state._fsp--;


                            }
                            break;
                        case 2 :
                            // EsperEPL2Ast.g:851:62: filterIdentifier
                            {
                            pushFollow(FOLLOW_filterIdentifier_in_filterParamComparator5975);
                            filterIdentifier();

                            state._fsp--;


                            }
                            break;

                    }

                    // EsperEPL2Ast.g:851:80: ( constant[false] | filterIdentifier )
                    int alt307=2;
                    int LA307_0 = input.LA(1);

                    if ( ((LA307_0>=INT_TYPE && LA307_0<=NULL_TYPE)) ) {
                        alt307=1;
                    }
                    else if ( (LA307_0==EVENT_FILTER_IDENT) ) {
                        alt307=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 307, 0, input);

                        throw nvae;
                    }
                    switch (alt307) {
                        case 1 :
                            // EsperEPL2Ast.g:851:81: constant[false]
                            {
                            pushFollow(FOLLOW_constant_in_filterParamComparator5979);
                            constant(false);

                            state._fsp--;


                            }
                            break;
                        case 2 :
                            // EsperEPL2Ast.g:851:97: filterIdentifier
                            {
                            pushFollow(FOLLOW_filterIdentifier_in_filterParamComparator5982);
                            filterIdentifier();

                            state._fsp--;


                            }
                            break;

                    }

                    if ( input.LA(1)==RBRACK||input.LA(1)==RPAREN ) {
                        input.consume();
                        state.errorRecovery=false;
                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        throw mse;
                    }


                    match(input, Token.UP, null); 

                    }
                    break;
                case 9 :
                    // EsperEPL2Ast.g:852:4: ^( EVENT_FILTER_IN ( LPAREN | LBRACK ) ( constant[false] | filterIdentifier ) ( constant[false] | filterIdentifier )* ( RPAREN | RBRACK ) )
                    {
                    match(input,EVENT_FILTER_IN,FOLLOW_EVENT_FILTER_IN_in_filterParamComparator5996); 

                    match(input, Token.DOWN, null); 
                    if ( input.LA(1)==LBRACK||input.LA(1)==LPAREN ) {
                        input.consume();
                        state.errorRecovery=false;
                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        throw mse;
                    }

                    // EsperEPL2Ast.g:852:38: ( constant[false] | filterIdentifier )
                    int alt308=2;
                    int LA308_0 = input.LA(1);

                    if ( ((LA308_0>=INT_TYPE && LA308_0<=NULL_TYPE)) ) {
                        alt308=1;
                    }
                    else if ( (LA308_0==EVENT_FILTER_IDENT) ) {
                        alt308=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 308, 0, input);

                        throw nvae;
                    }
                    switch (alt308) {
                        case 1 :
                            // EsperEPL2Ast.g:852:39: constant[false]
                            {
                            pushFollow(FOLLOW_constant_in_filterParamComparator6005);
                            constant(false);

                            state._fsp--;


                            }
                            break;
                        case 2 :
                            // EsperEPL2Ast.g:852:55: filterIdentifier
                            {
                            pushFollow(FOLLOW_filterIdentifier_in_filterParamComparator6008);
                            filterIdentifier();

                            state._fsp--;


                            }
                            break;

                    }

                    // EsperEPL2Ast.g:852:73: ( constant[false] | filterIdentifier )*
                    loop309:
                    do {
                        int alt309=3;
                        int LA309_0 = input.LA(1);

                        if ( ((LA309_0>=INT_TYPE && LA309_0<=NULL_TYPE)) ) {
                            alt309=1;
                        }
                        else if ( (LA309_0==EVENT_FILTER_IDENT) ) {
                            alt309=2;
                        }


                        switch (alt309) {
                    	case 1 :
                    	    // EsperEPL2Ast.g:852:74: constant[false]
                    	    {
                    	    pushFollow(FOLLOW_constant_in_filterParamComparator6012);
                    	    constant(false);

                    	    state._fsp--;


                    	    }
                    	    break;
                    	case 2 :
                    	    // EsperEPL2Ast.g:852:90: filterIdentifier
                    	    {
                    	    pushFollow(FOLLOW_filterIdentifier_in_filterParamComparator6015);
                    	    filterIdentifier();

                    	    state._fsp--;


                    	    }
                    	    break;

                    	default :
                    	    break loop309;
                        }
                    } while (true);

                    if ( input.LA(1)==RBRACK||input.LA(1)==RPAREN ) {
                        input.consume();
                        state.errorRecovery=false;
                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        throw mse;
                    }


                    match(input, Token.UP, null); 

                    }
                    break;
                case 10 :
                    // EsperEPL2Ast.g:853:4: ^( EVENT_FILTER_NOT_IN ( LPAREN | LBRACK ) ( constant[false] | filterIdentifier ) ( constant[false] | filterIdentifier )* ( RPAREN | RBRACK ) )
                    {
                    match(input,EVENT_FILTER_NOT_IN,FOLLOW_EVENT_FILTER_NOT_IN_in_filterParamComparator6030); 

                    match(input, Token.DOWN, null); 
                    if ( input.LA(1)==LBRACK||input.LA(1)==LPAREN ) {
                        input.consume();
                        state.errorRecovery=false;
                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        throw mse;
                    }

                    // EsperEPL2Ast.g:853:42: ( constant[false] | filterIdentifier )
                    int alt310=2;
                    int LA310_0 = input.LA(1);

                    if ( ((LA310_0>=INT_TYPE && LA310_0<=NULL_TYPE)) ) {
                        alt310=1;
                    }
                    else if ( (LA310_0==EVENT_FILTER_IDENT) ) {
                        alt310=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 310, 0, input);

                        throw nvae;
                    }
                    switch (alt310) {
                        case 1 :
                            // EsperEPL2Ast.g:853:43: constant[false]
                            {
                            pushFollow(FOLLOW_constant_in_filterParamComparator6039);
                            constant(false);

                            state._fsp--;


                            }
                            break;
                        case 2 :
                            // EsperEPL2Ast.g:853:59: filterIdentifier
                            {
                            pushFollow(FOLLOW_filterIdentifier_in_filterParamComparator6042);
                            filterIdentifier();

                            state._fsp--;


                            }
                            break;

                    }

                    // EsperEPL2Ast.g:853:77: ( constant[false] | filterIdentifier )*
                    loop311:
                    do {
                        int alt311=3;
                        int LA311_0 = input.LA(1);

                        if ( ((LA311_0>=INT_TYPE && LA311_0<=NULL_TYPE)) ) {
                            alt311=1;
                        }
                        else if ( (LA311_0==EVENT_FILTER_IDENT) ) {
                            alt311=2;
                        }


                        switch (alt311) {
                    	case 1 :
                    	    // EsperEPL2Ast.g:853:78: constant[false]
                    	    {
                    	    pushFollow(FOLLOW_constant_in_filterParamComparator6046);
                    	    constant(false);

                    	    state._fsp--;


                    	    }
                    	    break;
                    	case 2 :
                    	    // EsperEPL2Ast.g:853:94: filterIdentifier
                    	    {
                    	    pushFollow(FOLLOW_filterIdentifier_in_filterParamComparator6049);
                    	    filterIdentifier();

                    	    state._fsp--;


                    	    }
                    	    break;

                    	default :
                    	    break loop311;
                        }
                    } while (true);

                    if ( input.LA(1)==RBRACK||input.LA(1)==RPAREN ) {
                        input.consume();
                        state.errorRecovery=false;
                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        throw mse;
                    }


                    match(input, Token.UP, null); 

                    }
                    break;
                case 11 :
                    // EsperEPL2Ast.g:854:4: ^( EVENT_FILTER_BETWEEN ( constant[false] | filterIdentifier ) ( constant[false] | filterIdentifier ) )
                    {
                    match(input,EVENT_FILTER_BETWEEN,FOLLOW_EVENT_FILTER_BETWEEN_in_filterParamComparator6064); 

                    match(input, Token.DOWN, null); 
                    // EsperEPL2Ast.g:854:27: ( constant[false] | filterIdentifier )
                    int alt312=2;
                    int LA312_0 = input.LA(1);

                    if ( ((LA312_0>=INT_TYPE && LA312_0<=NULL_TYPE)) ) {
                        alt312=1;
                    }
                    else if ( (LA312_0==EVENT_FILTER_IDENT) ) {
                        alt312=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 312, 0, input);

                        throw nvae;
                    }
                    switch (alt312) {
                        case 1 :
                            // EsperEPL2Ast.g:854:28: constant[false]
                            {
                            pushFollow(FOLLOW_constant_in_filterParamComparator6067);
                            constant(false);

                            state._fsp--;


                            }
                            break;
                        case 2 :
                            // EsperEPL2Ast.g:854:44: filterIdentifier
                            {
                            pushFollow(FOLLOW_filterIdentifier_in_filterParamComparator6070);
                            filterIdentifier();

                            state._fsp--;


                            }
                            break;

                    }

                    // EsperEPL2Ast.g:854:62: ( constant[false] | filterIdentifier )
                    int alt313=2;
                    int LA313_0 = input.LA(1);

                    if ( ((LA313_0>=INT_TYPE && LA313_0<=NULL_TYPE)) ) {
                        alt313=1;
                    }
                    else if ( (LA313_0==EVENT_FILTER_IDENT) ) {
                        alt313=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 313, 0, input);

                        throw nvae;
                    }
                    switch (alt313) {
                        case 1 :
                            // EsperEPL2Ast.g:854:63: constant[false]
                            {
                            pushFollow(FOLLOW_constant_in_filterParamComparator6074);
                            constant(false);

                            state._fsp--;


                            }
                            break;
                        case 2 :
                            // EsperEPL2Ast.g:854:79: filterIdentifier
                            {
                            pushFollow(FOLLOW_filterIdentifier_in_filterParamComparator6077);
                            filterIdentifier();

                            state._fsp--;


                            }
                            break;

                    }


                    match(input, Token.UP, null); 

                    }
                    break;
                case 12 :
                    // EsperEPL2Ast.g:855:4: ^( EVENT_FILTER_NOT_BETWEEN ( constant[false] | filterIdentifier ) ( constant[false] | filterIdentifier ) )
                    {
                    match(input,EVENT_FILTER_NOT_BETWEEN,FOLLOW_EVENT_FILTER_NOT_BETWEEN_in_filterParamComparator6085); 

                    match(input, Token.DOWN, null); 
                    // EsperEPL2Ast.g:855:31: ( constant[false] | filterIdentifier )
                    int alt314=2;
                    int LA314_0 = input.LA(1);

                    if ( ((LA314_0>=INT_TYPE && LA314_0<=NULL_TYPE)) ) {
                        alt314=1;
                    }
                    else if ( (LA314_0==EVENT_FILTER_IDENT) ) {
                        alt314=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 314, 0, input);

                        throw nvae;
                    }
                    switch (alt314) {
                        case 1 :
                            // EsperEPL2Ast.g:855:32: constant[false]
                            {
                            pushFollow(FOLLOW_constant_in_filterParamComparator6088);
                            constant(false);

                            state._fsp--;


                            }
                            break;
                        case 2 :
                            // EsperEPL2Ast.g:855:48: filterIdentifier
                            {
                            pushFollow(FOLLOW_filterIdentifier_in_filterParamComparator6091);
                            filterIdentifier();

                            state._fsp--;


                            }
                            break;

                    }

                    // EsperEPL2Ast.g:855:66: ( constant[false] | filterIdentifier )
                    int alt315=2;
                    int LA315_0 = input.LA(1);

                    if ( ((LA315_0>=INT_TYPE && LA315_0<=NULL_TYPE)) ) {
                        alt315=1;
                    }
                    else if ( (LA315_0==EVENT_FILTER_IDENT) ) {
                        alt315=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 315, 0, input);

                        throw nvae;
                    }
                    switch (alt315) {
                        case 1 :
                            // EsperEPL2Ast.g:855:67: constant[false]
                            {
                            pushFollow(FOLLOW_constant_in_filterParamComparator6095);
                            constant(false);

                            state._fsp--;


                            }
                            break;
                        case 2 :
                            // EsperEPL2Ast.g:855:83: filterIdentifier
                            {
                            pushFollow(FOLLOW_filterIdentifier_in_filterParamComparator6098);
                            filterIdentifier();

                            state._fsp--;


                            }
                            break;

                    }


                    match(input, Token.UP, null); 

                    }
                    break;

            }
        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "filterParamComparator"


    // $ANTLR start "filterAtom"
    // EsperEPL2Ast.g:858:1: filterAtom : ( constant[false] | filterIdentifier );
    public final void filterAtom() throws RecognitionException {
        try {
            // EsperEPL2Ast.g:859:2: ( constant[false] | filterIdentifier )
            int alt317=2;
            int LA317_0 = input.LA(1);

            if ( ((LA317_0>=INT_TYPE && LA317_0<=NULL_TYPE)) ) {
                alt317=1;
            }
            else if ( (LA317_0==EVENT_FILTER_IDENT) ) {
                alt317=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 317, 0, input);

                throw nvae;
            }
            switch (alt317) {
                case 1 :
                    // EsperEPL2Ast.g:859:4: constant[false]
                    {
                    pushFollow(FOLLOW_constant_in_filterAtom6112);
                    constant(false);

                    state._fsp--;


                    }
                    break;
                case 2 :
                    // EsperEPL2Ast.g:860:4: filterIdentifier
                    {
                    pushFollow(FOLLOW_filterIdentifier_in_filterAtom6118);
                    filterIdentifier();

                    state._fsp--;


                    }
                    break;

            }
        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "filterAtom"


    // $ANTLR start "filterIdentifier"
    // EsperEPL2Ast.g:862:1: filterIdentifier : ^( EVENT_FILTER_IDENT IDENT eventPropertyExpr[true] ) ;
    public final void filterIdentifier() throws RecognitionException {
        try {
            // EsperEPL2Ast.g:863:2: ( ^( EVENT_FILTER_IDENT IDENT eventPropertyExpr[true] ) )
            // EsperEPL2Ast.g:863:4: ^( EVENT_FILTER_IDENT IDENT eventPropertyExpr[true] )
            {
            match(input,EVENT_FILTER_IDENT,FOLLOW_EVENT_FILTER_IDENT_in_filterIdentifier6129); 

            match(input, Token.DOWN, null); 
            match(input,IDENT,FOLLOW_IDENT_in_filterIdentifier6131); 
            pushFollow(FOLLOW_eventPropertyExpr_in_filterIdentifier6133);
            eventPropertyExpr(true);

            state._fsp--;


            match(input, Token.UP, null); 

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "filterIdentifier"


    // $ANTLR start "eventPropertyExpr"
    // EsperEPL2Ast.g:866:1: eventPropertyExpr[boolean isLeaveNode] : ^(p= EVENT_PROP_EXPR eventPropertyAtomic ( eventPropertyAtomic )* ) ;
    public final void eventPropertyExpr(boolean isLeaveNode) throws RecognitionException {
        CommonTree p=null;

        try {
            // EsperEPL2Ast.g:867:2: ( ^(p= EVENT_PROP_EXPR eventPropertyAtomic ( eventPropertyAtomic )* ) )
            // EsperEPL2Ast.g:867:4: ^(p= EVENT_PROP_EXPR eventPropertyAtomic ( eventPropertyAtomic )* )
            {
            p=(CommonTree)match(input,EVENT_PROP_EXPR,FOLLOW_EVENT_PROP_EXPR_in_eventPropertyExpr6152); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_eventPropertyAtomic_in_eventPropertyExpr6154);
            eventPropertyAtomic();

            state._fsp--;

            // EsperEPL2Ast.g:867:44: ( eventPropertyAtomic )*
            loop318:
            do {
                int alt318=2;
                int LA318_0 = input.LA(1);

                if ( ((LA318_0>=EVENT_PROP_SIMPLE && LA318_0<=EVENT_PROP_DYNAMIC_MAPPED)) ) {
                    alt318=1;
                }


                switch (alt318) {
            	case 1 :
            	    // EsperEPL2Ast.g:867:45: eventPropertyAtomic
            	    {
            	    pushFollow(FOLLOW_eventPropertyAtomic_in_eventPropertyExpr6157);
            	    eventPropertyAtomic();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop318;
                }
            } while (true);


            match(input, Token.UP, null); 
             if (isLeaveNode) leaveNode(p); 

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "eventPropertyExpr"


    // $ANTLR start "eventPropertyAtomic"
    // EsperEPL2Ast.g:870:1: eventPropertyAtomic : ( ^( EVENT_PROP_SIMPLE IDENT ) | ^( EVENT_PROP_INDEXED IDENT NUM_INT ) | ^( EVENT_PROP_MAPPED IDENT ( STRING_LITERAL | QUOTED_STRING_LITERAL ) ) | ^( EVENT_PROP_DYNAMIC_SIMPLE IDENT ) | ^( EVENT_PROP_DYNAMIC_INDEXED IDENT NUM_INT ) | ^( EVENT_PROP_DYNAMIC_MAPPED IDENT ( STRING_LITERAL | QUOTED_STRING_LITERAL ) ) );
    public final void eventPropertyAtomic() throws RecognitionException {
        try {
            // EsperEPL2Ast.g:871:2: ( ^( EVENT_PROP_SIMPLE IDENT ) | ^( EVENT_PROP_INDEXED IDENT NUM_INT ) | ^( EVENT_PROP_MAPPED IDENT ( STRING_LITERAL | QUOTED_STRING_LITERAL ) ) | ^( EVENT_PROP_DYNAMIC_SIMPLE IDENT ) | ^( EVENT_PROP_DYNAMIC_INDEXED IDENT NUM_INT ) | ^( EVENT_PROP_DYNAMIC_MAPPED IDENT ( STRING_LITERAL | QUOTED_STRING_LITERAL ) ) )
            int alt319=6;
            switch ( input.LA(1) ) {
            case EVENT_PROP_SIMPLE:
                {
                alt319=1;
                }
                break;
            case EVENT_PROP_INDEXED:
                {
                alt319=2;
                }
                break;
            case EVENT_PROP_MAPPED:
                {
                alt319=3;
                }
                break;
            case EVENT_PROP_DYNAMIC_SIMPLE:
                {
                alt319=4;
                }
                break;
            case EVENT_PROP_DYNAMIC_INDEXED:
                {
                alt319=5;
                }
                break;
            case EVENT_PROP_DYNAMIC_MAPPED:
                {
                alt319=6;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 319, 0, input);

                throw nvae;
            }

            switch (alt319) {
                case 1 :
                    // EsperEPL2Ast.g:871:4: ^( EVENT_PROP_SIMPLE IDENT )
                    {
                    match(input,EVENT_PROP_SIMPLE,FOLLOW_EVENT_PROP_SIMPLE_in_eventPropertyAtomic6176); 

                    match(input, Token.DOWN, null); 
                    match(input,IDENT,FOLLOW_IDENT_in_eventPropertyAtomic6178); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    // EsperEPL2Ast.g:872:4: ^( EVENT_PROP_INDEXED IDENT NUM_INT )
                    {
                    match(input,EVENT_PROP_INDEXED,FOLLOW_EVENT_PROP_INDEXED_in_eventPropertyAtomic6185); 

                    match(input, Token.DOWN, null); 
                    match(input,IDENT,FOLLOW_IDENT_in_eventPropertyAtomic6187); 
                    match(input,NUM_INT,FOLLOW_NUM_INT_in_eventPropertyAtomic6189); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 3 :
                    // EsperEPL2Ast.g:873:4: ^( EVENT_PROP_MAPPED IDENT ( STRING_LITERAL | QUOTED_STRING_LITERAL ) )
                    {
                    match(input,EVENT_PROP_MAPPED,FOLLOW_EVENT_PROP_MAPPED_in_eventPropertyAtomic6196); 

                    match(input, Token.DOWN, null); 
                    match(input,IDENT,FOLLOW_IDENT_in_eventPropertyAtomic6198); 
                    if ( (input.LA(1)>=STRING_LITERAL && input.LA(1)<=QUOTED_STRING_LITERAL) ) {
                        input.consume();
                        state.errorRecovery=false;
                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        throw mse;
                    }


                    match(input, Token.UP, null); 

                    }
                    break;
                case 4 :
                    // EsperEPL2Ast.g:874:4: ^( EVENT_PROP_DYNAMIC_SIMPLE IDENT )
                    {
                    match(input,EVENT_PROP_DYNAMIC_SIMPLE,FOLLOW_EVENT_PROP_DYNAMIC_SIMPLE_in_eventPropertyAtomic6213); 

                    match(input, Token.DOWN, null); 
                    match(input,IDENT,FOLLOW_IDENT_in_eventPropertyAtomic6215); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 5 :
                    // EsperEPL2Ast.g:875:4: ^( EVENT_PROP_DYNAMIC_INDEXED IDENT NUM_INT )
                    {
                    match(input,EVENT_PROP_DYNAMIC_INDEXED,FOLLOW_EVENT_PROP_DYNAMIC_INDEXED_in_eventPropertyAtomic6222); 

                    match(input, Token.DOWN, null); 
                    match(input,IDENT,FOLLOW_IDENT_in_eventPropertyAtomic6224); 
                    match(input,NUM_INT,FOLLOW_NUM_INT_in_eventPropertyAtomic6226); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 6 :
                    // EsperEPL2Ast.g:876:4: ^( EVENT_PROP_DYNAMIC_MAPPED IDENT ( STRING_LITERAL | QUOTED_STRING_LITERAL ) )
                    {
                    match(input,EVENT_PROP_DYNAMIC_MAPPED,FOLLOW_EVENT_PROP_DYNAMIC_MAPPED_in_eventPropertyAtomic6233); 

                    match(input, Token.DOWN, null); 
                    match(input,IDENT,FOLLOW_IDENT_in_eventPropertyAtomic6235); 
                    if ( (input.LA(1)>=STRING_LITERAL && input.LA(1)<=QUOTED_STRING_LITERAL) ) {
                        input.consume();
                        state.errorRecovery=false;
                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        throw mse;
                    }


                    match(input, Token.UP, null); 

                    }
                    break;

            }
        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "eventPropertyAtomic"


    // $ANTLR start "timePeriod"
    // EsperEPL2Ast.g:879:1: timePeriod : ^(t= TIME_PERIOD timePeriodDef ) ;
    public final void timePeriod() throws RecognitionException {
        CommonTree t=null;

        try {
            // EsperEPL2Ast.g:880:2: ( ^(t= TIME_PERIOD timePeriodDef ) )
            // EsperEPL2Ast.g:880:5: ^(t= TIME_PERIOD timePeriodDef )
            {
            t=(CommonTree)match(input,TIME_PERIOD,FOLLOW_TIME_PERIOD_in_timePeriod6262); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_timePeriodDef_in_timePeriod6264);
            timePeriodDef();

            state._fsp--;

             leaveNode(t); 

            match(input, Token.UP, null); 

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "timePeriod"


    // $ANTLR start "timePeriodDef"
    // EsperEPL2Ast.g:883:1: timePeriodDef : ( yearPart ( monthPart )? ( weekPart )? ( dayPart )? ( hourPart )? ( minutePart )? ( secondPart )? ( millisecondPart )? | monthPart ( weekPart )? ( dayPart )? ( hourPart )? ( minutePart )? ( secondPart )? ( millisecondPart )? | weekPart ( dayPart )? ( hourPart )? ( minutePart )? ( secondPart )? ( millisecondPart )? | dayPart ( hourPart )? ( minutePart )? ( secondPart )? ( millisecondPart )? | hourPart ( minutePart )? ( secondPart )? ( millisecondPart )? | minutePart ( secondPart )? ( millisecondPart )? | secondPart ( millisecondPart )? | millisecondPart );
    public final void timePeriodDef() throws RecognitionException {
        try {
            // EsperEPL2Ast.g:884:2: ( yearPart ( monthPart )? ( weekPart )? ( dayPart )? ( hourPart )? ( minutePart )? ( secondPart )? ( millisecondPart )? | monthPart ( weekPart )? ( dayPart )? ( hourPart )? ( minutePart )? ( secondPart )? ( millisecondPart )? | weekPart ( dayPart )? ( hourPart )? ( minutePart )? ( secondPart )? ( millisecondPart )? | dayPart ( hourPart )? ( minutePart )? ( secondPart )? ( millisecondPart )? | hourPart ( minutePart )? ( secondPart )? ( millisecondPart )? | minutePart ( secondPart )? ( millisecondPart )? | secondPart ( millisecondPart )? | millisecondPart )
            int alt348=8;
            switch ( input.LA(1) ) {
            case YEAR_PART:
                {
                alt348=1;
                }
                break;
            case MONTH_PART:
                {
                alt348=2;
                }
                break;
            case WEEK_PART:
                {
                alt348=3;
                }
                break;
            case DAY_PART:
                {
                alt348=4;
                }
                break;
            case HOUR_PART:
                {
                alt348=5;
                }
                break;
            case MINUTE_PART:
                {
                alt348=6;
                }
                break;
            case SECOND_PART:
                {
                alt348=7;
                }
                break;
            case MILLISECOND_PART:
                {
                alt348=8;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 348, 0, input);

                throw nvae;
            }

            switch (alt348) {
                case 1 :
                    // EsperEPL2Ast.g:884:5: yearPart ( monthPart )? ( weekPart )? ( dayPart )? ( hourPart )? ( minutePart )? ( secondPart )? ( millisecondPart )?
                    {
                    pushFollow(FOLLOW_yearPart_in_timePeriodDef6280);
                    yearPart();

                    state._fsp--;

                    // EsperEPL2Ast.g:884:14: ( monthPart )?
                    int alt320=2;
                    int LA320_0 = input.LA(1);

                    if ( (LA320_0==MONTH_PART) ) {
                        alt320=1;
                    }
                    switch (alt320) {
                        case 1 :
                            // EsperEPL2Ast.g:884:15: monthPart
                            {
                            pushFollow(FOLLOW_monthPart_in_timePeriodDef6283);
                            monthPart();

                            state._fsp--;


                            }
                            break;

                    }

                    // EsperEPL2Ast.g:884:27: ( weekPart )?
                    int alt321=2;
                    int LA321_0 = input.LA(1);

                    if ( (LA321_0==WEEK_PART) ) {
                        alt321=1;
                    }
                    switch (alt321) {
                        case 1 :
                            // EsperEPL2Ast.g:884:28: weekPart
                            {
                            pushFollow(FOLLOW_weekPart_in_timePeriodDef6288);
                            weekPart();

                            state._fsp--;


                            }
                            break;

                    }

                    // EsperEPL2Ast.g:884:39: ( dayPart )?
                    int alt322=2;
                    int LA322_0 = input.LA(1);

                    if ( (LA322_0==DAY_PART) ) {
                        alt322=1;
                    }
                    switch (alt322) {
                        case 1 :
                            // EsperEPL2Ast.g:884:40: dayPart
                            {
                            pushFollow(FOLLOW_dayPart_in_timePeriodDef6293);
                            dayPart();

                            state._fsp--;


                            }
                            break;

                    }

                    // EsperEPL2Ast.g:884:50: ( hourPart )?
                    int alt323=2;
                    int LA323_0 = input.LA(1);

                    if ( (LA323_0==HOUR_PART) ) {
                        alt323=1;
                    }
                    switch (alt323) {
                        case 1 :
                            // EsperEPL2Ast.g:884:51: hourPart
                            {
                            pushFollow(FOLLOW_hourPart_in_timePeriodDef6298);
                            hourPart();

                            state._fsp--;


                            }
                            break;

                    }

                    // EsperEPL2Ast.g:884:62: ( minutePart )?
                    int alt324=2;
                    int LA324_0 = input.LA(1);

                    if ( (LA324_0==MINUTE_PART) ) {
                        alt324=1;
                    }
                    switch (alt324) {
                        case 1 :
                            // EsperEPL2Ast.g:884:63: minutePart
                            {
                            pushFollow(FOLLOW_minutePart_in_timePeriodDef6303);
                            minutePart();

                            state._fsp--;


                            }
                            break;

                    }

                    // EsperEPL2Ast.g:884:76: ( secondPart )?
                    int alt325=2;
                    int LA325_0 = input.LA(1);

                    if ( (LA325_0==SECOND_PART) ) {
                        alt325=1;
                    }
                    switch (alt325) {
                        case 1 :
                            // EsperEPL2Ast.g:884:77: secondPart
                            {
                            pushFollow(FOLLOW_secondPart_in_timePeriodDef6308);
                            secondPart();

                            state._fsp--;


                            }
                            break;

                    }

                    // EsperEPL2Ast.g:884:90: ( millisecondPart )?
                    int alt326=2;
                    int LA326_0 = input.LA(1);

                    if ( (LA326_0==MILLISECOND_PART) ) {
                        alt326=1;
                    }
                    switch (alt326) {
                        case 1 :
                            // EsperEPL2Ast.g:884:91: millisecondPart
                            {
                            pushFollow(FOLLOW_millisecondPart_in_timePeriodDef6313);
                            millisecondPart();

                            state._fsp--;


                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // EsperEPL2Ast.g:885:5: monthPart ( weekPart )? ( dayPart )? ( hourPart )? ( minutePart )? ( secondPart )? ( millisecondPart )?
                    {
                    pushFollow(FOLLOW_monthPart_in_timePeriodDef6321);
                    monthPart();

                    state._fsp--;

                    // EsperEPL2Ast.g:885:15: ( weekPart )?
                    int alt327=2;
                    int LA327_0 = input.LA(1);

                    if ( (LA327_0==WEEK_PART) ) {
                        alt327=1;
                    }
                    switch (alt327) {
                        case 1 :
                            // EsperEPL2Ast.g:885:16: weekPart
                            {
                            pushFollow(FOLLOW_weekPart_in_timePeriodDef6324);
                            weekPart();

                            state._fsp--;


                            }
                            break;

                    }

                    // EsperEPL2Ast.g:885:27: ( dayPart )?
                    int alt328=2;
                    int LA328_0 = input.LA(1);

                    if ( (LA328_0==DAY_PART) ) {
                        alt328=1;
                    }
                    switch (alt328) {
                        case 1 :
                            // EsperEPL2Ast.g:885:28: dayPart
                            {
                            pushFollow(FOLLOW_dayPart_in_timePeriodDef6329);
                            dayPart();

                            state._fsp--;


                            }
                            break;

                    }

                    // EsperEPL2Ast.g:885:38: ( hourPart )?
                    int alt329=2;
                    int LA329_0 = input.LA(1);

                    if ( (LA329_0==HOUR_PART) ) {
                        alt329=1;
                    }
                    switch (alt329) {
                        case 1 :
                            // EsperEPL2Ast.g:885:39: hourPart
                            {
                            pushFollow(FOLLOW_hourPart_in_timePeriodDef6334);
                            hourPart();

                            state._fsp--;


                            }
                            break;

                    }

                    // EsperEPL2Ast.g:885:50: ( minutePart )?
                    int alt330=2;
                    int LA330_0 = input.LA(1);

                    if ( (LA330_0==MINUTE_PART) ) {
                        alt330=1;
                    }
                    switch (alt330) {
                        case 1 :
                            // EsperEPL2Ast.g:885:51: minutePart
                            {
                            pushFollow(FOLLOW_minutePart_in_timePeriodDef6339);
                            minutePart();

                            state._fsp--;


                            }
                            break;

                    }

                    // EsperEPL2Ast.g:885:64: ( secondPart )?
                    int alt331=2;
                    int LA331_0 = input.LA(1);

                    if ( (LA331_0==SECOND_PART) ) {
                        alt331=1;
                    }
                    switch (alt331) {
                        case 1 :
                            // EsperEPL2Ast.g:885:65: secondPart
                            {
                            pushFollow(FOLLOW_secondPart_in_timePeriodDef6344);
                            secondPart();

                            state._fsp--;


                            }
                            break;

                    }

                    // EsperEPL2Ast.g:885:78: ( millisecondPart )?
                    int alt332=2;
                    int LA332_0 = input.LA(1);

                    if ( (LA332_0==MILLISECOND_PART) ) {
                        alt332=1;
                    }
                    switch (alt332) {
                        case 1 :
                            // EsperEPL2Ast.g:885:79: millisecondPart
                            {
                            pushFollow(FOLLOW_millisecondPart_in_timePeriodDef6349);
                            millisecondPart();

                            state._fsp--;


                            }
                            break;

                    }


                    }
                    break;
                case 3 :
                    // EsperEPL2Ast.g:886:5: weekPart ( dayPart )? ( hourPart )? ( minutePart )? ( secondPart )? ( millisecondPart )?
                    {
                    pushFollow(FOLLOW_weekPart_in_timePeriodDef6357);
                    weekPart();

                    state._fsp--;

                    // EsperEPL2Ast.g:886:14: ( dayPart )?
                    int alt333=2;
                    int LA333_0 = input.LA(1);

                    if ( (LA333_0==DAY_PART) ) {
                        alt333=1;
                    }
                    switch (alt333) {
                        case 1 :
                            // EsperEPL2Ast.g:886:15: dayPart
                            {
                            pushFollow(FOLLOW_dayPart_in_timePeriodDef6360);
                            dayPart();

                            state._fsp--;


                            }
                            break;

                    }

                    // EsperEPL2Ast.g:886:25: ( hourPart )?
                    int alt334=2;
                    int LA334_0 = input.LA(1);

                    if ( (LA334_0==HOUR_PART) ) {
                        alt334=1;
                    }
                    switch (alt334) {
                        case 1 :
                            // EsperEPL2Ast.g:886:26: hourPart
                            {
                            pushFollow(FOLLOW_hourPart_in_timePeriodDef6365);
                            hourPart();

                            state._fsp--;


                            }
                            break;

                    }

                    // EsperEPL2Ast.g:886:37: ( minutePart )?
                    int alt335=2;
                    int LA335_0 = input.LA(1);

                    if ( (LA335_0==MINUTE_PART) ) {
                        alt335=1;
                    }
                    switch (alt335) {
                        case 1 :
                            // EsperEPL2Ast.g:886:38: minutePart
                            {
                            pushFollow(FOLLOW_minutePart_in_timePeriodDef6370);
                            minutePart();

                            state._fsp--;


                            }
                            break;

                    }

                    // EsperEPL2Ast.g:886:51: ( secondPart )?
                    int alt336=2;
                    int LA336_0 = input.LA(1);

                    if ( (LA336_0==SECOND_PART) ) {
                        alt336=1;
                    }
                    switch (alt336) {
                        case 1 :
                            // EsperEPL2Ast.g:886:52: secondPart
                            {
                            pushFollow(FOLLOW_secondPart_in_timePeriodDef6375);
                            secondPart();

                            state._fsp--;


                            }
                            break;

                    }

                    // EsperEPL2Ast.g:886:65: ( millisecondPart )?
                    int alt337=2;
                    int LA337_0 = input.LA(1);

                    if ( (LA337_0==MILLISECOND_PART) ) {
                        alt337=1;
                    }
                    switch (alt337) {
                        case 1 :
                            // EsperEPL2Ast.g:886:66: millisecondPart
                            {
                            pushFollow(FOLLOW_millisecondPart_in_timePeriodDef6380);
                            millisecondPart();

                            state._fsp--;


                            }
                            break;

                    }


                    }
                    break;
                case 4 :
                    // EsperEPL2Ast.g:887:5: dayPart ( hourPart )? ( minutePart )? ( secondPart )? ( millisecondPart )?
                    {
                    pushFollow(FOLLOW_dayPart_in_timePeriodDef6388);
                    dayPart();

                    state._fsp--;

                    // EsperEPL2Ast.g:887:13: ( hourPart )?
                    int alt338=2;
                    int LA338_0 = input.LA(1);

                    if ( (LA338_0==HOUR_PART) ) {
                        alt338=1;
                    }
                    switch (alt338) {
                        case 1 :
                            // EsperEPL2Ast.g:887:14: hourPart
                            {
                            pushFollow(FOLLOW_hourPart_in_timePeriodDef6391);
                            hourPart();

                            state._fsp--;


                            }
                            break;

                    }

                    // EsperEPL2Ast.g:887:25: ( minutePart )?
                    int alt339=2;
                    int LA339_0 = input.LA(1);

                    if ( (LA339_0==MINUTE_PART) ) {
                        alt339=1;
                    }
                    switch (alt339) {
                        case 1 :
                            // EsperEPL2Ast.g:887:26: minutePart
                            {
                            pushFollow(FOLLOW_minutePart_in_timePeriodDef6396);
                            minutePart();

                            state._fsp--;


                            }
                            break;

                    }

                    // EsperEPL2Ast.g:887:39: ( secondPart )?
                    int alt340=2;
                    int LA340_0 = input.LA(1);

                    if ( (LA340_0==SECOND_PART) ) {
                        alt340=1;
                    }
                    switch (alt340) {
                        case 1 :
                            // EsperEPL2Ast.g:887:40: secondPart
                            {
                            pushFollow(FOLLOW_secondPart_in_timePeriodDef6401);
                            secondPart();

                            state._fsp--;


                            }
                            break;

                    }

                    // EsperEPL2Ast.g:887:53: ( millisecondPart )?
                    int alt341=2;
                    int LA341_0 = input.LA(1);

                    if ( (LA341_0==MILLISECOND_PART) ) {
                        alt341=1;
                    }
                    switch (alt341) {
                        case 1 :
                            // EsperEPL2Ast.g:887:54: millisecondPart
                            {
                            pushFollow(FOLLOW_millisecondPart_in_timePeriodDef6406);
                            millisecondPart();

                            state._fsp--;


                            }
                            break;

                    }


                    }
                    break;
                case 5 :
                    // EsperEPL2Ast.g:888:4: hourPart ( minutePart )? ( secondPart )? ( millisecondPart )?
                    {
                    pushFollow(FOLLOW_hourPart_in_timePeriodDef6413);
                    hourPart();

                    state._fsp--;

                    // EsperEPL2Ast.g:888:13: ( minutePart )?
                    int alt342=2;
                    int LA342_0 = input.LA(1);

                    if ( (LA342_0==MINUTE_PART) ) {
                        alt342=1;
                    }
                    switch (alt342) {
                        case 1 :
                            // EsperEPL2Ast.g:888:14: minutePart
                            {
                            pushFollow(FOLLOW_minutePart_in_timePeriodDef6416);
                            minutePart();

                            state._fsp--;


                            }
                            break;

                    }

                    // EsperEPL2Ast.g:888:27: ( secondPart )?
                    int alt343=2;
                    int LA343_0 = input.LA(1);

                    if ( (LA343_0==SECOND_PART) ) {
                        alt343=1;
                    }
                    switch (alt343) {
                        case 1 :
                            // EsperEPL2Ast.g:888:28: secondPart
                            {
                            pushFollow(FOLLOW_secondPart_in_timePeriodDef6421);
                            secondPart();

                            state._fsp--;


                            }
                            break;

                    }

                    // EsperEPL2Ast.g:888:41: ( millisecondPart )?
                    int alt344=2;
                    int LA344_0 = input.LA(1);

                    if ( (LA344_0==MILLISECOND_PART) ) {
                        alt344=1;
                    }
                    switch (alt344) {
                        case 1 :
                            // EsperEPL2Ast.g:888:42: millisecondPart
                            {
                            pushFollow(FOLLOW_millisecondPart_in_timePeriodDef6426);
                            millisecondPart();

                            state._fsp--;


                            }
                            break;

                    }


                    }
                    break;
                case 6 :
                    // EsperEPL2Ast.g:889:4: minutePart ( secondPart )? ( millisecondPart )?
                    {
                    pushFollow(FOLLOW_minutePart_in_timePeriodDef6433);
                    minutePart();

                    state._fsp--;

                    // EsperEPL2Ast.g:889:15: ( secondPart )?
                    int alt345=2;
                    int LA345_0 = input.LA(1);

                    if ( (LA345_0==SECOND_PART) ) {
                        alt345=1;
                    }
                    switch (alt345) {
                        case 1 :
                            // EsperEPL2Ast.g:889:16: secondPart
                            {
                            pushFollow(FOLLOW_secondPart_in_timePeriodDef6436);
                            secondPart();

                            state._fsp--;


                            }
                            break;

                    }

                    // EsperEPL2Ast.g:889:29: ( millisecondPart )?
                    int alt346=2;
                    int LA346_0 = input.LA(1);

                    if ( (LA346_0==MILLISECOND_PART) ) {
                        alt346=1;
                    }
                    switch (alt346) {
                        case 1 :
                            // EsperEPL2Ast.g:889:30: millisecondPart
                            {
                            pushFollow(FOLLOW_millisecondPart_in_timePeriodDef6441);
                            millisecondPart();

                            state._fsp--;


                            }
                            break;

                    }


                    }
                    break;
                case 7 :
                    // EsperEPL2Ast.g:890:4: secondPart ( millisecondPart )?
                    {
                    pushFollow(FOLLOW_secondPart_in_timePeriodDef6448);
                    secondPart();

                    state._fsp--;

                    // EsperEPL2Ast.g:890:15: ( millisecondPart )?
                    int alt347=2;
                    int LA347_0 = input.LA(1);

                    if ( (LA347_0==MILLISECOND_PART) ) {
                        alt347=1;
                    }
                    switch (alt347) {
                        case 1 :
                            // EsperEPL2Ast.g:890:16: millisecondPart
                            {
                            pushFollow(FOLLOW_millisecondPart_in_timePeriodDef6451);
                            millisecondPart();

                            state._fsp--;


                            }
                            break;

                    }


                    }
                    break;
                case 8 :
                    // EsperEPL2Ast.g:891:4: millisecondPart
                    {
                    pushFollow(FOLLOW_millisecondPart_in_timePeriodDef6458);
                    millisecondPart();

                    state._fsp--;


                    }
                    break;

            }
        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "timePeriodDef"


    // $ANTLR start "yearPart"
    // EsperEPL2Ast.g:894:1: yearPart : ^( YEAR_PART valueExpr ) ;
    public final void yearPart() throws RecognitionException {
        try {
            // EsperEPL2Ast.g:895:2: ( ^( YEAR_PART valueExpr ) )
            // EsperEPL2Ast.g:895:4: ^( YEAR_PART valueExpr )
            {
            match(input,YEAR_PART,FOLLOW_YEAR_PART_in_yearPart6472); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_valueExpr_in_yearPart6474);
            valueExpr();

            state._fsp--;


            match(input, Token.UP, null); 

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "yearPart"


    // $ANTLR start "monthPart"
    // EsperEPL2Ast.g:898:1: monthPart : ^( MONTH_PART valueExpr ) ;
    public final void monthPart() throws RecognitionException {
        try {
            // EsperEPL2Ast.g:899:2: ( ^( MONTH_PART valueExpr ) )
            // EsperEPL2Ast.g:899:4: ^( MONTH_PART valueExpr )
            {
            match(input,MONTH_PART,FOLLOW_MONTH_PART_in_monthPart6489); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_valueExpr_in_monthPart6491);
            valueExpr();

            state._fsp--;


            match(input, Token.UP, null); 

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "monthPart"


    // $ANTLR start "weekPart"
    // EsperEPL2Ast.g:902:1: weekPart : ^( WEEK_PART valueExpr ) ;
    public final void weekPart() throws RecognitionException {
        try {
            // EsperEPL2Ast.g:903:2: ( ^( WEEK_PART valueExpr ) )
            // EsperEPL2Ast.g:903:4: ^( WEEK_PART valueExpr )
            {
            match(input,WEEK_PART,FOLLOW_WEEK_PART_in_weekPart6506); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_valueExpr_in_weekPart6508);
            valueExpr();

            state._fsp--;


            match(input, Token.UP, null); 

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "weekPart"


    // $ANTLR start "dayPart"
    // EsperEPL2Ast.g:906:1: dayPart : ^( DAY_PART valueExpr ) ;
    public final void dayPart() throws RecognitionException {
        try {
            // EsperEPL2Ast.g:907:2: ( ^( DAY_PART valueExpr ) )
            // EsperEPL2Ast.g:907:4: ^( DAY_PART valueExpr )
            {
            match(input,DAY_PART,FOLLOW_DAY_PART_in_dayPart6523); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_valueExpr_in_dayPart6525);
            valueExpr();

            state._fsp--;


            match(input, Token.UP, null); 

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "dayPart"


    // $ANTLR start "hourPart"
    // EsperEPL2Ast.g:910:1: hourPart : ^( HOUR_PART valueExpr ) ;
    public final void hourPart() throws RecognitionException {
        try {
            // EsperEPL2Ast.g:911:2: ( ^( HOUR_PART valueExpr ) )
            // EsperEPL2Ast.g:911:4: ^( HOUR_PART valueExpr )
            {
            match(input,HOUR_PART,FOLLOW_HOUR_PART_in_hourPart6540); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_valueExpr_in_hourPart6542);
            valueExpr();

            state._fsp--;


            match(input, Token.UP, null); 

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "hourPart"


    // $ANTLR start "minutePart"
    // EsperEPL2Ast.g:914:1: minutePart : ^( MINUTE_PART valueExpr ) ;
    public final void minutePart() throws RecognitionException {
        try {
            // EsperEPL2Ast.g:915:2: ( ^( MINUTE_PART valueExpr ) )
            // EsperEPL2Ast.g:915:4: ^( MINUTE_PART valueExpr )
            {
            match(input,MINUTE_PART,FOLLOW_MINUTE_PART_in_minutePart6557); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_valueExpr_in_minutePart6559);
            valueExpr();

            state._fsp--;


            match(input, Token.UP, null); 

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "minutePart"


    // $ANTLR start "secondPart"
    // EsperEPL2Ast.g:918:1: secondPart : ^( SECOND_PART valueExpr ) ;
    public final void secondPart() throws RecognitionException {
        try {
            // EsperEPL2Ast.g:919:2: ( ^( SECOND_PART valueExpr ) )
            // EsperEPL2Ast.g:919:4: ^( SECOND_PART valueExpr )
            {
            match(input,SECOND_PART,FOLLOW_SECOND_PART_in_secondPart6574); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_valueExpr_in_secondPart6576);
            valueExpr();

            state._fsp--;


            match(input, Token.UP, null); 

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "secondPart"


    // $ANTLR start "millisecondPart"
    // EsperEPL2Ast.g:922:1: millisecondPart : ^( MILLISECOND_PART valueExpr ) ;
    public final void millisecondPart() throws RecognitionException {
        try {
            // EsperEPL2Ast.g:923:2: ( ^( MILLISECOND_PART valueExpr ) )
            // EsperEPL2Ast.g:923:4: ^( MILLISECOND_PART valueExpr )
            {
            match(input,MILLISECOND_PART,FOLLOW_MILLISECOND_PART_in_millisecondPart6591); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_valueExpr_in_millisecondPart6593);
            valueExpr();

            state._fsp--;


            match(input, Token.UP, null); 

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "millisecondPart"


    // $ANTLR start "substitution"
    // EsperEPL2Ast.g:926:1: substitution : s= SUBSTITUTION ;
    public final void substitution() throws RecognitionException {
        CommonTree s=null;

        try {
            // EsperEPL2Ast.g:927:2: (s= SUBSTITUTION )
            // EsperEPL2Ast.g:927:4: s= SUBSTITUTION
            {
            s=(CommonTree)match(input,SUBSTITUTION,FOLLOW_SUBSTITUTION_in_substitution6608); 
             leaveNode(s); 

            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "substitution"


    // $ANTLR start "constant"
    // EsperEPL2Ast.g:930:1: constant[boolean isLeaveNode] : (c= INT_TYPE | c= LONG_TYPE | c= FLOAT_TYPE | c= DOUBLE_TYPE | c= STRING_TYPE | c= BOOL_TYPE | c= NULL_TYPE );
    public final void constant(boolean isLeaveNode) throws RecognitionException {
        CommonTree c=null;

        try {
            // EsperEPL2Ast.g:931:2: (c= INT_TYPE | c= LONG_TYPE | c= FLOAT_TYPE | c= DOUBLE_TYPE | c= STRING_TYPE | c= BOOL_TYPE | c= NULL_TYPE )
            int alt349=7;
            switch ( input.LA(1) ) {
            case INT_TYPE:
                {
                alt349=1;
                }
                break;
            case LONG_TYPE:
                {
                alt349=2;
                }
                break;
            case FLOAT_TYPE:
                {
                alt349=3;
                }
                break;
            case DOUBLE_TYPE:
                {
                alt349=4;
                }
                break;
            case STRING_TYPE:
                {
                alt349=5;
                }
                break;
            case BOOL_TYPE:
                {
                alt349=6;
                }
                break;
            case NULL_TYPE:
                {
                alt349=7;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 349, 0, input);

                throw nvae;
            }

            switch (alt349) {
                case 1 :
                    // EsperEPL2Ast.g:931:4: c= INT_TYPE
                    {
                    c=(CommonTree)match(input,INT_TYPE,FOLLOW_INT_TYPE_in_constant6624); 
                     if (isLeaveNode) leaveNode(c); 

                    }
                    break;
                case 2 :
                    // EsperEPL2Ast.g:932:4: c= LONG_TYPE
                    {
                    c=(CommonTree)match(input,LONG_TYPE,FOLLOW_LONG_TYPE_in_constant6633); 
                     if (isLeaveNode) leaveNode(c); 

                    }
                    break;
                case 3 :
                    // EsperEPL2Ast.g:933:4: c= FLOAT_TYPE
                    {
                    c=(CommonTree)match(input,FLOAT_TYPE,FOLLOW_FLOAT_TYPE_in_constant6642); 
                     if (isLeaveNode) leaveNode(c); 

                    }
                    break;
                case 4 :
                    // EsperEPL2Ast.g:934:4: c= DOUBLE_TYPE
                    {
                    c=(CommonTree)match(input,DOUBLE_TYPE,FOLLOW_DOUBLE_TYPE_in_constant6651); 
                     if (isLeaveNode) leaveNode(c); 

                    }
                    break;
                case 5 :
                    // EsperEPL2Ast.g:935:11: c= STRING_TYPE
                    {
                    c=(CommonTree)match(input,STRING_TYPE,FOLLOW_STRING_TYPE_in_constant6667); 
                     if (isLeaveNode) leaveNode(c); 

                    }
                    break;
                case 6 :
                    // EsperEPL2Ast.g:936:11: c= BOOL_TYPE
                    {
                    c=(CommonTree)match(input,BOOL_TYPE,FOLLOW_BOOL_TYPE_in_constant6683); 
                     if (isLeaveNode) leaveNode(c); 

                    }
                    break;
                case 7 :
                    // EsperEPL2Ast.g:937:8: c= NULL_TYPE
                    {
                    c=(CommonTree)match(input,NULL_TYPE,FOLLOW_NULL_TYPE_in_constant6696); 
                     if (isLeaveNode) leaveNode(c); 

                    }
                    break;

            }
        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "constant"


    // $ANTLR start "number"
    // EsperEPL2Ast.g:940:1: number : ( INT_TYPE | LONG_TYPE | FLOAT_TYPE | DOUBLE_TYPE );
    public final void number() throws RecognitionException {
        try {
            // EsperEPL2Ast.g:941:2: ( INT_TYPE | LONG_TYPE | FLOAT_TYPE | DOUBLE_TYPE )
            // EsperEPL2Ast.g:
            {
            if ( (input.LA(1)>=INT_TYPE && input.LA(1)<=DOUBLE_TYPE) ) {
                input.consume();
                state.errorRecovery=false;
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }


            }

        }

          catch (RecognitionException rex) {
            throw rex;
          }
        finally {
        }
        return ;
    }
    // $ANTLR end "number"

    // Delegated rules


 

    public static final BitSet FOLLOW_ANNOTATION_in_annotation92 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_CLASS_IDENT_in_annotation94 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000002000000L,0x0000000000000000L,0x0007F00000000038L});
    public static final BitSet FOLLOW_elementValuePair_in_annotation96 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000002000000L,0x0000000000000000L,0x0007F00000000038L});
    public static final BitSet FOLLOW_elementValue_in_annotation99 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ANNOTATION_VALUE_in_elementValuePair117 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_IDENT_in_elementValuePair119 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000002000000L,0x0000000000000000L,0x0007F00000000018L});
    public static final BitSet FOLLOW_elementValue_in_elementValuePair121 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_annotation_in_elementValue148 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ANNOTATION_ARRAY_in_elementValue156 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_elementValue_in_elementValue158 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000002000000L,0x0000000000000000L,0x0007F00000000018L});
    public static final BitSet FOLLOW_constant_in_elementValue169 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CLASS_IDENT_in_elementValue179 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EXPRESSIONDECL_in_expressionDecl205 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_IDENT_in_expressionDecl207 = new BitSet(new long[]{0x0000000000000000L,0x0800000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000800L});
    public static final BitSet FOLLOW_expressionDef_in_expressionDecl209 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000002000000L,0x0000000000000040L,0x0000000000000000L,0x0000000000000108L});
    public static final BitSet FOLLOW_exprCol_in_expressionDecl211 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000002000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000108L});
    public static final BitSet FOLLOW_CLASS_IDENT_in_expressionDecl214 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000108L});
    public static final BitSet FOLLOW_COLON_in_expressionDecl219 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_IDENT_in_expressionDecl221 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_LBRACK_in_expressionDecl226 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_GOES_in_expressionDef243 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_valueExpr_in_expressionDef245 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000800L});
    public static final BitSet FOLLOW_expressionLambdaDecl_in_expressionDef247 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_EXPRESSIONDECL_in_expressionDef255 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_constant_in_expressionDef257 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_GOES_in_expressionLambdaDecl271 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_IDENT_in_expressionLambdaDecl274 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_exprCol_in_expressionLambdaDecl278 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_EPL_EXPR_in_startEPLExpressionRule295 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_annotation_in_startEPLExpressionRule298 = new BitSet(new long[]{0x0000000000000000L,0x4800000000000000L,0x0000080000000000L,0x0200B00000000020L,0x0000000201801408L});
    public static final BitSet FOLLOW_expressionDecl_in_startEPLExpressionRule303 = new BitSet(new long[]{0x0000000000000000L,0x4800000000000000L,0x0000080000000000L,0x0200B00000000020L,0x0000000201801408L});
    public static final BitSet FOLLOW_eplExpressionRule_in_startEPLExpressionRule308 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_contextExpr_in_eplExpressionRule325 = new BitSet(new long[]{0x0000000000000000L,0x4000000000000000L,0x0000080000000000L,0x0200B00000000020L,0x0000000200001400L});
    public static final BitSet FOLLOW_selectExpr_in_eplExpressionRule329 = new BitSet(new long[]{0x0000000000000002L,0x0040000000000000L});
    public static final BitSet FOLLOW_createWindowExpr_in_eplExpressionRule333 = new BitSet(new long[]{0x0000000000000002L,0x0040000000000000L});
    public static final BitSet FOLLOW_createIndexExpr_in_eplExpressionRule337 = new BitSet(new long[]{0x0000000000000002L,0x0040000000000000L});
    public static final BitSet FOLLOW_createVariableExpr_in_eplExpressionRule341 = new BitSet(new long[]{0x0000000000000002L,0x0040000000000000L});
    public static final BitSet FOLLOW_createSchemaExpr_in_eplExpressionRule345 = new BitSet(new long[]{0x0000000000000002L,0x0040000000000000L});
    public static final BitSet FOLLOW_onExpr_in_eplExpressionRule350 = new BitSet(new long[]{0x0000000000000002L,0x0040000000000000L});
    public static final BitSet FOLLOW_updateExpr_in_eplExpressionRule354 = new BitSet(new long[]{0x0000000000000002L,0x0040000000000000L});
    public static final BitSet FOLLOW_createDataflow_in_eplExpressionRule358 = new BitSet(new long[]{0x0000000000000002L,0x0040000000000000L});
    public static final BitSet FOLLOW_forExpr_in_eplExpressionRule361 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_createContextExpr_in_eplExpressionRule368 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_createExpr_in_eplExpressionRule372 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CONTEXT_in_contextExpr387 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_IDENT_in_contextExpr389 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ON_EXPR_in_onExpr408 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_onStreamExpr_in_onExpr410 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x011E000000000000L});
    public static final BitSet FOLLOW_onDeleteExpr_in_onExpr415 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_onUpdateExpr_in_onExpr419 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_onSelectExpr_in_onExpr423 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000000000000L,0x0020000000000000L});
    public static final BitSet FOLLOW_onSelectInsertExpr_in_onExpr426 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000000000000L,0x0060000000000000L});
    public static final BitSet FOLLOW_onSelectInsertOutput_in_onExpr429 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_onSetExpr_in_onExpr436 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_onMergeExpr_in_onExpr440 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ON_STREAM_in_onStreamExpr462 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_eventFilterExpr_in_onStreamExpr465 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_patternInclusionExpression_in_onStreamExpr470 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_IDENT_in_onStreamExpr473 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ON_MERGE_EXPR_in_onMergeExpr491 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_IDENT_in_onMergeExpr493 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000030000L,0x0000000000000020L});
    public static final BitSet FOLLOW_IDENT_in_onMergeExpr495 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000030000L,0x0000000000000020L});
    public static final BitSet FOLLOW_mergeItem_in_onMergeExpr498 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000080000000L,0x0000000000000000L,0x0000000000030000L,0x0000000000000020L});
    public static final BitSet FOLLOW_whereClause_in_onMergeExpr501 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_mergeMatched_in_mergeItem517 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_mergeUnmatched_in_mergeItem521 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MERGE_MAT_in_mergeMatched536 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_mergeMatchedItem_in_mergeMatched538 = new BitSet(new long[]{0x1000000037CC23C8L,0x100000000001E7E0L,0x008003FC00000000L,0x0000047707804D00L,0x0007F000001C01C0L,0x00000007666D0003L,0x0000000000000018L});
    public static final BitSet FOLLOW_valueExpr_in_mergeMatched541 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_MERGE_UPD_in_mergeMatchedItem559 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_onSetAssignment_in_mergeMatchedItem561 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000080000000L,0x0000000000000000L,0x0000000000000800L});
    public static final BitSet FOLLOW_whereClause_in_mergeMatchedItem564 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_MERGE_DEL_in_mergeMatchedItem577 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_whereClause_in_mergeMatchedItem579 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000100000000000L});
    public static final BitSet FOLLOW_INT_TYPE_in_mergeMatchedItem583 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_mergeInsert_in_mergeMatchedItem591 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MERGE_UNM_in_mergeUnmatched605 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_mergeInsert_in_mergeUnmatched607 = new BitSet(new long[]{0x1000000037CC23C8L,0x100000000001E7E0L,0x008003FC00000000L,0x0000047707804D00L,0x0007F000001C01C0L,0x00000007666D0003L,0x0000000000000018L});
    public static final BitSet FOLLOW_valueExpr_in_mergeUnmatched610 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_MERGE_INS_in_mergeInsert629 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_selectionList_in_mergeInsert631 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000082000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_CLASS_IDENT_in_mergeInsert633 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000080000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_exprCol_in_mergeInsert636 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000080000000L});
    public static final BitSet FOLLOW_whereClause_in_mergeInsert639 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_UPDATE_EXPR_in_updateExpr659 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_CLASS_IDENT_in_updateExpr661 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000080000000L,0x0000000000000000L,0x0000000000000800L,0x0000000000000020L});
    public static final BitSet FOLLOW_IDENT_in_updateExpr663 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000080000000L,0x0000000000000000L,0x0000000000000800L});
    public static final BitSet FOLLOW_onSetAssignment_in_updateExpr666 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000080000000L,0x0000000000000000L,0x0000000000000800L});
    public static final BitSet FOLLOW_whereClause_in_updateExpr669 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ON_DELETE_EXPR_in_onDeleteExpr686 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_onExprFrom_in_onDeleteExpr688 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000080000000L});
    public static final BitSet FOLLOW_whereClause_in_onDeleteExpr691 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ON_SELECT_EXPR_in_onSelectExpr711 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_insertIntoExpr_in_onSelectExpr713 = new BitSet(new long[]{0x0000400000000000L,0x0000000000020000L,0x0000300000000000L,0x0000000040000000L});
    public static final BitSet FOLLOW_DELETE_in_onSelectExpr716 = new BitSet(new long[]{0x0000400000000000L,0x0000000000020000L,0x0000300000000000L,0x0000000040000000L});
    public static final BitSet FOLLOW_DISTINCT_in_onSelectExpr719 = new BitSet(new long[]{0x0000400000000000L,0x0000000000020000L,0x0000300000000000L,0x0000000040000000L});
    public static final BitSet FOLLOW_selectionList_in_onSelectExpr722 = new BitSet(new long[]{0x0000000000000008L,0x0000200000000000L,0x0030000180000000L,0x0080000000000000L});
    public static final BitSet FOLLOW_onExprFrom_in_onSelectExpr724 = new BitSet(new long[]{0x0000000000000008L,0x0000200000000000L,0x0030000180000000L});
    public static final BitSet FOLLOW_whereClause_in_onSelectExpr727 = new BitSet(new long[]{0x0000000000000008L,0x0000200000000000L,0x0030000100000000L});
    public static final BitSet FOLLOW_groupByClause_in_onSelectExpr731 = new BitSet(new long[]{0x0000000000000008L,0x0000200000000000L,0x0020000100000000L});
    public static final BitSet FOLLOW_havingClause_in_onSelectExpr734 = new BitSet(new long[]{0x0000000000000008L,0x0000200000000000L,0x0020000000000000L});
    public static final BitSet FOLLOW_orderByClause_in_onSelectExpr737 = new BitSet(new long[]{0x0000000000000008L,0x0000200000000000L});
    public static final BitSet FOLLOW_rowLimitClause_in_onSelectExpr740 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ON_SELECT_INSERT_EXPR_in_onSelectInsertExpr760 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_insertIntoExpr_in_onSelectInsertExpr762 = new BitSet(new long[]{0x0000400000000000L,0x0000000000020000L,0x0000300000000000L,0x0000000040000000L});
    public static final BitSet FOLLOW_selectionList_in_onSelectInsertExpr764 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000080000000L});
    public static final BitSet FOLLOW_whereClause_in_onSelectInsertExpr766 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ON_SELECT_INSERT_OUTPUT_in_onSelectInsertOutput783 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_set_in_onSelectInsertOutput785 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ON_SET_EXPR_in_onSetExpr803 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_onSetAssignment_in_onSetExpr805 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000080000000L,0x0000000000000000L,0x0000000000000800L});
    public static final BitSet FOLLOW_onSetAssignment_in_onSetExpr808 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000080000000L,0x0000000000000000L,0x0000000000000800L});
    public static final BitSet FOLLOW_whereClause_in_onSetExpr812 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ON_UPDATE_EXPR_in_onUpdateExpr827 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_onExprFrom_in_onUpdateExpr829 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000080000000L,0x0000000000000000L,0x0000000000000800L});
    public static final BitSet FOLLOW_onSetAssignment_in_onUpdateExpr831 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000080000000L,0x0000000000000000L,0x0000000000000800L});
    public static final BitSet FOLLOW_whereClause_in_onUpdateExpr834 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ON_SET_EXPR_ITEM_in_onSetAssignment849 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_eventPropertyExpr_in_onSetAssignment851 = new BitSet(new long[]{0x1000000037CC23C0L,0x100000000001E7E0L,0x008003FC00000000L,0x0000047707804D00L,0x0007F000000001C0L,0x00000007666D0003L,0x0000000000000018L});
    public static final BitSet FOLLOW_valueExpr_in_onSetAssignment854 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ON_EXPR_FROM_in_onExprFrom868 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_IDENT_in_onExprFrom870 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_IDENT_in_onExprFrom873 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_CREATE_WINDOW_EXPR_in_createWindowExpr891 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_IDENT_in_createWindowExpr893 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000003L,0x0000000012000000L,0x0000400000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_viewListExpr_in_createWindowExpr896 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000003L,0x0000000012000000L,0x0000400000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_RETAINUNION_in_createWindowExpr900 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000003L,0x0000000012000000L,0x0000400000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_RETAININTERSECTION_in_createWindowExpr903 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000003L,0x0000000012000000L,0x0000400000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_createSelectionList_in_createWindowExpr917 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000002000000L});
    public static final BitSet FOLLOW_CLASS_IDENT_in_createWindowExpr920 = new BitSet(new long[]{0x0040000000000008L});
    public static final BitSet FOLLOW_createColTypeList_in_createWindowExpr949 = new BitSet(new long[]{0x0040000000000008L});
    public static final BitSet FOLLOW_createWindowExprInsert_in_createWindowExpr960 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_CREATE_INDEX_EXPR_in_createIndexExpr980 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_IDENT_in_createIndexExpr982 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_IDENT_in_createIndexExpr984 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000080L});
    public static final BitSet FOLLOW_indexColList_in_createIndexExpr986 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_IDENT_in_createIndexExpr988 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_INDEXCOL_in_indexColList1004 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_indexCol_in_indexColList1006 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000000000000L,0x0000000000000080L});
    public static final BitSet FOLLOW_INDEXCOL_in_indexCol1021 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_IDENT_in_indexCol1023 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_IDENT_in_indexCol1025 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_INSERT_in_createWindowExprInsert1039 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_valueExpr_in_createWindowExprInsert1041 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_CREATE_WINDOW_SELECT_EXPR_in_createSelectionList1058 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_createSelectionListElement_in_createSelectionList1060 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000100000000000L,0x0000000040000000L});
    public static final BitSet FOLLOW_createSelectionListElement_in_createSelectionList1063 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000100000000000L,0x0000000040000000L});
    public static final BitSet FOLLOW_CREATE_COL_TYPE_LIST_in_createColTypeList1082 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_createColTypeListElement_in_createColTypeList1084 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_createColTypeListElement_in_createColTypeList1087 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_CREATE_COL_TYPE_in_createColTypeListElement1102 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_CLASS_IDENT_in_createColTypeListElement1104 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000002000000L});
    public static final BitSet FOLLOW_CLASS_IDENT_in_createColTypeListElement1106 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_LBRACK_in_createColTypeListElement1108 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_WILDCARD_SELECT_in_createSelectionListElement1123 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SELECTION_ELEMENT_EXPR_in_createSelectionListElement1133 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_eventPropertyExpr_in_createSelectionListElement1153 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_IDENT_in_createSelectionListElement1157 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_constant_in_createSelectionListElement1179 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_IDENT_in_createSelectionListElement1182 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_CREATE_VARIABLE_EXPR_in_createVariableExpr1218 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_CLASS_IDENT_in_createVariableExpr1220 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_IDENT_in_createVariableExpr1222 = new BitSet(new long[]{0x1000000037CC23C8L,0x100000000001E7E0L,0x008003FC00000000L,0x0000047707804D00L,0x0007F000000001C0L,0x00000007666D002BL,0x0000000000000018L});
    public static final BitSet FOLLOW_IDENT_in_createVariableExpr1224 = new BitSet(new long[]{0x1000000037CC23C8L,0x100000000001E7E0L,0x008003FC00000000L,0x0000047707804D00L,0x0007F000000001C0L,0x00000007666D000BL,0x0000000000000018L});
    public static final BitSet FOLLOW_LBRACK_in_createVariableExpr1227 = new BitSet(new long[]{0x1000000037CC23C8L,0x100000000001E7E0L,0x008003FC00000000L,0x0000047707804D00L,0x0007F000000001C0L,0x00000007666D0003L,0x0000000000000018L});
    public static final BitSet FOLLOW_valueExpr_in_createVariableExpr1230 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_CREATE_DATAFLOW_in_createDataflow1255 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_IDENT_in_createDataflow1257 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000400001000L});
    public static final BitSet FOLLOW_gop_in_createDataflow1259 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000400001000L});
    public static final BitSet FOLLOW_GOP_in_gop1276 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_set_in_gop1278 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000012800000008L});
    public static final BitSet FOLLOW_gopParam_in_gop1284 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000012000000008L});
    public static final BitSet FOLLOW_gopOut_in_gop1287 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000010000000008L});
    public static final BitSet FOLLOW_gopDetail_in_gop1290 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_annotation_in_gop1294 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_createSchemaExpr_in_gop1303 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GOPPARAM_in_gopParam1318 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_gopParamItem_in_gopParam1320 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000001000000000L});
    public static final BitSet FOLLOW_GOPPARAMITM_in_gopParamItem1334 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_CLASS_IDENT_in_gopParamItem1336 = new BitSet(new long[]{0x0000000000020008L,0x0000000000000000L,0x0000000002000000L});
    public static final BitSet FOLLOW_AS_in_gopParamItem1341 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_IDENT_in_gopParamItem1343 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_GOPOUT_in_gopOut1359 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_gopOutItem_in_gopOut1361 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000004000000000L});
    public static final BitSet FOLLOW_GOPOUTITM_in_gopOutItem1375 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_CLASS_IDENT_in_gopOutItem1377 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000008000000000L});
    public static final BitSet FOLLOW_gopOutTypeParam_in_gopOutItem1379 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000008000000000L});
    public static final BitSet FOLLOW_GOPOUTTYP_in_gopOutTypeParam1394 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_CLASS_IDENT_in_gopOutTypeParam1398 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000008000000000L});
    public static final BitSet FOLLOW_gopOutTypeParam_in_gopOutTypeParam1400 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000008000000000L});
    public static final BitSet FOLLOW_QUESTION_in_gopOutTypeParam1406 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_GOPCFG_in_gopDetail1420 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_gopConfig_in_gopDetail1422 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000060000000000L,0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_GOPCFGITM_in_gopConfig1438 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_IDENT_in_gopConfig1440 = new BitSet(new long[]{0x1000000037CC23C0L,0x100000000001E7E0L,0x008003FC00000000L,0x0000047707804D00L,0x0007F000000001C0L,0x00000007666D0003L,0x0000000000000018L});
    public static final BitSet FOLLOW_valueExpr_in_gopConfig1442 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_GOPCFGEXP_in_gopConfig1453 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_valueExpr_in_gopConfig1455 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_GOPCFGEPL_in_gopConfig1466 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_selectExpr_in_gopConfig1468 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_constant_in_jsonvalue1484 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_jsonobject_in_jsonvalue1491 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_jsonarray_in_jsonvalue1498 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_JSON_OBJECT_in_jsonobject1515 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_jsonpair_in_jsonobject1517 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_JSON_ARRAY_in_jsonarray1546 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_jsonvalue_in_jsonarray1548 = new BitSet(new long[]{0x1000000037CC23C8L,0x100000000001E7E0L,0x008003FC00000000L,0x0000047707804D00L,0x0007F000000001C0L,0x00000007666D0003L,0x0000000000000018L});
    public static final BitSet FOLLOW_JSON_FIELD_in_jsonpair1566 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_constant_in_jsonpair1569 = new BitSet(new long[]{0x1000000037CC23C8L,0x100000000001E7E0L,0x008003FC00000000L,0x0000047707804D00L,0x0007F000000001C0L,0x00000007666D0003L,0x0000000000000018L});
    public static final BitSet FOLLOW_IDENT_in_jsonpair1574 = new BitSet(new long[]{0x1000000037CC23C8L,0x100000000001E7E0L,0x008003FC00000000L,0x0000047707804D00L,0x0007F000000001C0L,0x00000007666D0003L,0x0000000000000018L});
    public static final BitSet FOLLOW_jsonvalue_in_jsonpair1577 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_CREATE_CTX_in_createContextExpr1597 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_IDENT_in_createContextExpr1599 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x00000000BE000000L});
    public static final BitSet FOLLOW_createContextDetail_in_createContextExpr1601 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_CREATE_CTX_FIXED_in_createContextDetail1617 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_createContextRangePoint_in_createContextDetail1619 = new BitSet(new long[]{0x0000000000000000L,0x0020000000000000L,0x0000400000000000L,0x0000000000000004L,0x0000000100000000L});
    public static final BitSet FOLLOW_createContextRangePoint_in_createContextDetail1621 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_CREATE_CTX_INIT_in_createContextDetail1628 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_createContextRangePoint_in_createContextDetail1630 = new BitSet(new long[]{0x0000000000000000L,0x0020000000000000L,0x0000400000000000L,0x0000000000000004L,0x0000000100000000L});
    public static final BitSet FOLLOW_createContextRangePoint_in_createContextDetail1632 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_CREATE_CTX_PART_in_createContextDetail1639 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_createContextPartitionItem_in_createContextDetail1641 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000080000000000L});
    public static final BitSet FOLLOW_CREATE_CTX_CAT_in_createContextDetail1649 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_createContextCategoryItem_in_createContextDetail1651 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000800L,0x0000000000000000L,0x0000000040000000L});
    public static final BitSet FOLLOW_eventFilterExpr_in_createContextDetail1654 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_CREATE_CTX_COAL_in_createContextDetail1662 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_createContextCoalesceItem_in_createContextDetail1664 = new BitSet(new long[]{0x0000000000400000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_IDENT_in_createContextDetail1667 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000F00000000000L});
    public static final BitSet FOLLOW_number_in_createContextDetail1669 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_IDENT_in_createContextDetail1671 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_CREATE_CTX_NESTED_in_createContextDetail1679 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_createContextNested_in_createContextDetail1681 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_createContextNested_in_createContextDetail1683 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_createContextFilter_in_createContextRangePoint1696 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CREATE_CTX_PATTERN_in_createContextRangePoint1704 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_patternInclusionExpression_in_createContextRangePoint1706 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_IDENT_in_createContextRangePoint1708 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_crontabLimitParameterSet_in_createContextRangePoint1715 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_AFTER_in_createContextRangePoint1721 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_timePeriod_in_createContextRangePoint1723 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_CREATE_CTX_in_createContextNested1739 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_IDENT_in_createContextNested1741 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x00000000BE000000L});
    public static final BitSet FOLLOW_createContextDetail_in_createContextNested1743 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_STREAM_EXPR_in_createContextFilter1756 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_eventFilterExpr_in_createContextFilter1758 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_IDENT_in_createContextFilter1761 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_PARTITIONITEM_in_createContextPartitionItem1777 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_eventFilterExpr_in_createContextPartitionItem1779 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0080000000000000L});
    public static final BitSet FOLLOW_eventPropertyExpr_in_createContextPartitionItem1782 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0080000000000000L});
    public static final BitSet FOLLOW_COALESCE_in_createContextCoalesceItem1799 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_libFunctionWithClass_in_createContextCoalesceItem1801 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000800L});
    public static final BitSet FOLLOW_eventFilterExpr_in_createContextCoalesceItem1803 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_CREATE_CTX_CATITEM_in_createContextCategoryItem1819 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_valueExpr_in_createContextCategoryItem1821 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_IDENT_in_createContextCategoryItem1823 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_CREATE_EXPR_in_createExpr1840 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expressionDecl_in_createExpr1842 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_CREATE_SCHEMA_EXPR_in_createSchemaExpr1861 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_createSchemaDef_in_createSchemaExpr1863 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_IDENT_in_createSchemaExpr1865 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_CREATE_SCHEMA_DEF_in_createSchemaDef1882 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_IDENT_in_createSchemaDef1884 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000003L,0x0000000012000000L,0x0000400000000000L,0x000000000000A001L});
    public static final BitSet FOLLOW_variantList_in_createSchemaDef1887 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000002000L});
    public static final BitSet FOLLOW_createColTypeList_in_createSchemaDef1889 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000002000L});
    public static final BitSet FOLLOW_createSchemaQual_in_createSchemaDef1893 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000002000L});
    public static final BitSet FOLLOW_CREATE_SCHEMA_EXPR_QUAL_in_createSchemaQual1908 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_IDENT_in_createSchemaQual1910 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_exprCol_in_createSchemaQual1912 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VARIANT_LIST_in_variantList1928 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_set_in_variantList1930 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000002000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000010000L});
    public static final BitSet FOLLOW_insertIntoExpr_in_selectExpr1948 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000080000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_selectClause_in_selectExpr1954 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000400000000000L});
    public static final BitSet FOLLOW_fromClause_in_selectExpr1959 = new BitSet(new long[]{0x0000000000000002L,0x0001200000000000L,0xC030000180000000L,0x000000000000001BL});
    public static final BitSet FOLLOW_matchRecogClause_in_selectExpr1964 = new BitSet(new long[]{0x0000000000000002L,0x0000200000000000L,0xC030000180000000L,0x000000000000001BL});
    public static final BitSet FOLLOW_whereClause_in_selectExpr1971 = new BitSet(new long[]{0x0000000000000002L,0x0000200000000000L,0xC030000100000000L,0x000000000000001BL});
    public static final BitSet FOLLOW_groupByClause_in_selectExpr1979 = new BitSet(new long[]{0x0000000000000002L,0x0000200000000000L,0xC020000100000000L,0x000000000000001BL});
    public static final BitSet FOLLOW_havingClause_in_selectExpr1986 = new BitSet(new long[]{0x0000000000000002L,0x0000200000000000L,0xC020000000000000L,0x000000000000001BL});
    public static final BitSet FOLLOW_outputLimitExpr_in_selectExpr1993 = new BitSet(new long[]{0x0000000000000002L,0x0000200000000000L,0x0020000000000000L});
    public static final BitSet FOLLOW_orderByClause_in_selectExpr2000 = new BitSet(new long[]{0x0000000000000002L,0x0000200000000000L});
    public static final BitSet FOLLOW_rowLimitClause_in_selectExpr2007 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INSERTINTO_EXPR_in_insertIntoExpr2024 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_set_in_insertIntoExpr2026 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000002000000L});
    public static final BitSet FOLLOW_CLASS_IDENT_in_insertIntoExpr2039 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000000000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_exprCol_in_insertIntoExpr2042 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_EXPRCOL_in_exprCol2061 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_IDENT_in_exprCol2063 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_IDENT_in_exprCol2066 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_SELECTION_EXPR_in_selectClause2084 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_set_in_selectClause2086 = new BitSet(new long[]{0x0000400000000000L,0x0000000000020000L,0x0000300000000000L,0x0000000040000000L});
    public static final BitSet FOLLOW_DISTINCT_in_selectClause2099 = new BitSet(new long[]{0x0000400000000000L,0x0000000000020000L,0x0000300000000000L,0x0000000040000000L});
    public static final BitSet FOLLOW_selectionList_in_selectClause2102 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_streamExpression_in_fromClause2116 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000400000000000L});
    public static final BitSet FOLLOW_streamExpression_in_fromClause2119 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x000F400000000000L});
    public static final BitSet FOLLOW_outerJoin_in_fromClause2122 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x000F400000000000L});
    public static final BitSet FOLLOW_FOR_in_forExpr2142 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_IDENT_in_forExpr2144 = new BitSet(new long[]{0x1000000037CC23C8L,0x100000000001E7E0L,0x008003FC00000000L,0x0000047707804D00L,0x0007F000000001C0L,0x00000007666D0003L,0x0000000000000018L});
    public static final BitSet FOLLOW_valueExpr_in_forExpr2146 = new BitSet(new long[]{0x1000000037CC23C8L,0x100000000001E7E0L,0x008003FC00000000L,0x0000047707804D00L,0x0007F000000001C0L,0x00000007666D0003L,0x0000000000000018L});
    public static final BitSet FOLLOW_MATCH_RECOGNIZE_in_matchRecogClause2165 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_matchRecogPartitionBy_in_matchRecogClause2167 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x4000000000000000L});
    public static final BitSet FOLLOW_matchRecogMeasures_in_matchRecogClause2174 = new BitSet(new long[]{0x0000800000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0420000000000000L});
    public static final BitSet FOLLOW_ALL_in_matchRecogClause2180 = new BitSet(new long[]{0x0000800000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0420000000000000L});
    public static final BitSet FOLLOW_matchRecogMatchesAfterSkip_in_matchRecogClause2186 = new BitSet(new long[]{0x0000800000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0420000000000000L});
    public static final BitSet FOLLOW_matchRecogPattern_in_matchRecogClause2192 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x1800000000000000L});
    public static final BitSet FOLLOW_matchRecogMatchesInterval_in_matchRecogClause2198 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x1800000000000000L});
    public static final BitSet FOLLOW_matchRecogDefine_in_matchRecogClause2204 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_PARTITIONITEM_in_matchRecogPartitionBy2222 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_valueExpr_in_matchRecogPartitionBy2224 = new BitSet(new long[]{0x1000000037CC23C8L,0x100000000001E7E0L,0x008003FC00000000L,0x0000047707804D00L,0x0007F000000001C0L,0x00000007666D0003L,0x0000000000000018L});
    public static final BitSet FOLLOW_MATCHREC_AFTER_SKIP_in_matchRecogMatchesAfterSkip2241 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_IDENT_in_matchRecogMatchesAfterSkip2243 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_IDENT_in_matchRecogMatchesAfterSkip2245 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_IDENT_in_matchRecogMatchesAfterSkip2247 = new BitSet(new long[]{0x0020000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_set_in_matchRecogMatchesAfterSkip2249 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_IDENT_in_matchRecogMatchesAfterSkip2255 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_MATCHREC_INTERVAL_in_matchRecogMatchesInterval2270 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_IDENT_in_matchRecogMatchesInterval2272 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000002000L});
    public static final BitSet FOLLOW_timePeriod_in_matchRecogMatchesInterval2274 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_MATCHREC_MEASURES_in_matchRecogMeasures2290 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_matchRecogMeasureListElement_in_matchRecogMeasures2292 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x8000000000000000L});
    public static final BitSet FOLLOW_MATCHREC_MEASURE_ITEM_in_matchRecogMeasureListElement2309 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_valueExpr_in_matchRecogMeasureListElement2311 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_IDENT_in_matchRecogMeasureListElement2313 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_MATCHREC_PATTERN_in_matchRecogPattern2333 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_matchRecogPatternAlteration_in_matchRecogPattern2335 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0180000000000000L});
    public static final BitSet FOLLOW_matchRecogPatternConcat_in_matchRecogPatternAlteration2350 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MATCHREC_PATTERN_ALTER_in_matchRecogPatternAlteration2358 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_matchRecogPatternConcat_in_matchRecogPatternAlteration2360 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0080000000000000L});
    public static final BitSet FOLLOW_matchRecogPatternConcat_in_matchRecogPatternAlteration2362 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0080000000000000L});
    public static final BitSet FOLLOW_MATCHREC_PATTERN_CONCAT_in_matchRecogPatternConcat2380 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_matchRecogPatternUnary_in_matchRecogPatternConcat2382 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0240000000000000L});
    public static final BitSet FOLLOW_matchRecogPatternNested_in_matchRecogPatternUnary2397 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_matchRecogPatternAtom_in_matchRecogPatternUnary2402 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MATCHREC_PATTERN_NESTED_in_matchRecogPatternNested2417 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_matchRecogPatternAlteration_in_matchRecogPatternNested2419 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000510000L});
    public static final BitSet FOLLOW_set_in_matchRecogPatternNested2421 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_MATCHREC_PATTERN_ATOM_in_matchRecogPatternAtom2450 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_IDENT_in_matchRecogPatternAtom2452 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000510000L});
    public static final BitSet FOLLOW_set_in_matchRecogPatternAtom2456 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_QUESTION_in_matchRecogPatternAtom2468 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_MATCHREC_DEFINE_in_matchRecogDefine2490 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_matchRecogDefineItem_in_matchRecogDefine2492 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x2000000000000000L});
    public static final BitSet FOLLOW_MATCHREC_DEFINE_ITEM_in_matchRecogDefineItem2509 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_IDENT_in_matchRecogDefineItem2511 = new BitSet(new long[]{0x1000000037CC23C0L,0x100000000001E7E0L,0x008003FC00000000L,0x0000047707804D00L,0x0007F000000001C0L,0x00000007666D0003L,0x0000000000000018L});
    public static final BitSet FOLLOW_valueExpr_in_matchRecogDefineItem2513 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_selectionListElement_in_selectionList2530 = new BitSet(new long[]{0x0000400000000002L,0x0000000000020000L,0x0000300000000000L,0x0000000040000000L});
    public static final BitSet FOLLOW_selectionListElement_in_selectionList2533 = new BitSet(new long[]{0x0000400000000002L,0x0000000000020000L,0x0000300000000000L,0x0000000040000000L});
    public static final BitSet FOLLOW_WILDCARD_SELECT_in_selectionListElement2549 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SELECTION_ELEMENT_EXPR_in_selectionListElement2559 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_valueExpr_in_selectionListElement2561 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_IDENT_in_selectionListElement2564 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_SELECTION_STREAM_in_selectionListElement2578 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_IDENT_in_selectionListElement2580 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_IDENT_in_selectionListElement2583 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_LEFT_OUTERJOIN_EXPR_in_outerJoin2605 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ON_in_outerJoin2607 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0080000000000000L});
    public static final BitSet FOLLOW_outerJoinIdent_in_outerJoin2609 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_RIGHT_OUTERJOIN_EXPR_in_outerJoin2622 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ON_in_outerJoin2624 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0080000000000000L});
    public static final BitSet FOLLOW_outerJoinIdent_in_outerJoin2626 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_FULL_OUTERJOIN_EXPR_in_outerJoin2639 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ON_in_outerJoin2641 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0080000000000000L});
    public static final BitSet FOLLOW_outerJoinIdent_in_outerJoin2643 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_INNERJOIN_EXPR_in_outerJoin2656 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ON_in_outerJoin2658 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0080000000000000L});
    public static final BitSet FOLLOW_outerJoinIdent_in_outerJoin2660 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_eventPropertyExpr_in_outerJoinIdent2676 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0080000000000000L});
    public static final BitSet FOLLOW_eventPropertyExpr_in_outerJoinIdent2679 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0080000000000000L});
    public static final BitSet FOLLOW_eventPropertyExpr_in_outerJoinIdent2683 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0080000000000000L});
    public static final BitSet FOLLOW_eventPropertyExpr_in_outerJoinIdent2686 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0080000000000000L});
    public static final BitSet FOLLOW_STREAM_EXPR_in_streamExpression2703 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_eventFilterExpr_in_streamExpression2706 = new BitSet(new long[]{0x8000000000000008L,0x0000000000000003L,0x0000000010000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_patternInclusionExpression_in_streamExpression2711 = new BitSet(new long[]{0x8000000000000008L,0x0000000000000003L,0x0000000010000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_databaseJoinExpression_in_streamExpression2715 = new BitSet(new long[]{0x8000000000000008L,0x0000000000000003L,0x0000000010000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_methodJoinExpression_in_streamExpression2719 = new BitSet(new long[]{0x8000000000000008L,0x0000000000000003L,0x0000000010000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_viewListExpr_in_streamExpression2723 = new BitSet(new long[]{0x8000000000000008L,0x0000000000000003L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_IDENT_in_streamExpression2728 = new BitSet(new long[]{0x8000000000000008L,0x0000000000000003L});
    public static final BitSet FOLLOW_UNIDIRECTIONAL_in_streamExpression2733 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000003L});
    public static final BitSet FOLLOW_set_in_streamExpression2737 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_EVENT_FILTER_EXPR_in_eventFilterExpr2762 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_IDENT_in_eventFilterExpr2764 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000002000000L});
    public static final BitSet FOLLOW_CLASS_IDENT_in_eventFilterExpr2767 = new BitSet(new long[]{0x1000000037CC23C8L,0x100000000001E7E0L,0x008003FC00001000L,0x0000047707804D00L,0x0007F000000001C0L,0x00000007666D0003L,0x0000000000000018L});
    public static final BitSet FOLLOW_propertyExpression_in_eventFilterExpr2769 = new BitSet(new long[]{0x1000000037CC23C8L,0x100000000001E7E0L,0x008003FC00000000L,0x0000047707804D00L,0x0007F000000001C0L,0x00000007666D0003L,0x0000000000000018L});
    public static final BitSet FOLLOW_valueExpr_in_eventFilterExpr2773 = new BitSet(new long[]{0x1000000037CC23C8L,0x100000000001E7E0L,0x008003FC00000000L,0x0000047707804D00L,0x0007F000000001C0L,0x00000007666D0003L,0x0000000000000018L});
    public static final BitSet FOLLOW_EVENT_FILTER_PROPERTY_EXPR_in_propertyExpression2793 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_propertyExpressionAtom_in_propertyExpression2795 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000000002000L});
    public static final BitSet FOLLOW_EVENT_FILTER_PROPERTY_EXPR_ATOM_in_propertyExpressionAtom2814 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_SELECT_in_propertyExpressionAtom2818 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_propertySelectionListElement_in_propertyExpressionAtom2820 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x000000000001C000L});
    public static final BitSet FOLLOW_valueExpr_in_propertyExpressionAtom2826 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000080000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000001020L});
    public static final BitSet FOLLOW_ATCHAR_in_propertyExpressionAtom2830 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_IDENT_in_propertyExpressionAtom2832 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_IDENT_in_propertyExpressionAtom2834 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_IDENT_in_propertyExpressionAtom2839 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000080000000L});
    public static final BitSet FOLLOW_WHERE_EXPR_in_propertyExpressionAtom2843 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_valueExpr_in_propertyExpressionAtom2845 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_PROPERTY_WILDCARD_SELECT_in_propertySelectionListElement2865 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PROPERTY_SELECTION_ELEMENT_EXPR_in_propertySelectionListElement2875 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_valueExpr_in_propertySelectionListElement2877 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_IDENT_in_propertySelectionListElement2880 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_PROPERTY_SELECTION_STREAM_in_propertySelectionListElement2894 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_IDENT_in_propertySelectionListElement2896 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_IDENT_in_propertySelectionListElement2899 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_PATTERN_INCL_EXPR_in_patternInclusionExpression2920 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_exprChoice_in_patternInclusionExpression2922 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_DATABASE_JOIN_EXPR_in_databaseJoinExpression2939 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_IDENT_in_databaseJoinExpression2941 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000001800000L});
    public static final BitSet FOLLOW_set_in_databaseJoinExpression2943 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000001800000L});
    public static final BitSet FOLLOW_set_in_databaseJoinExpression2951 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_METHOD_JOIN_EXPR_in_methodJoinExpression2972 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_IDENT_in_methodJoinExpression2974 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000002000000L});
    public static final BitSet FOLLOW_CLASS_IDENT_in_methodJoinExpression2976 = new BitSet(new long[]{0x1000000037CC23C8L,0x100000000001E7E0L,0x008003FC00000000L,0x0000047707804D00L,0x0007F000000001C0L,0x00000007666D0003L,0x0000000000000018L});
    public static final BitSet FOLLOW_valueExpr_in_methodJoinExpression2979 = new BitSet(new long[]{0x1000000037CC23C8L,0x100000000001E7E0L,0x008003FC00000000L,0x0000047707804D00L,0x0007F000000001C0L,0x00000007666D0003L,0x0000000000000018L});
    public static final BitSet FOLLOW_viewExpr_in_viewListExpr2993 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000010000000L});
    public static final BitSet FOLLOW_viewExpr_in_viewListExpr2996 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000010000000L});
    public static final BitSet FOLLOW_VIEW_EXPR_in_viewExpr3013 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_IDENT_in_viewExpr3015 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_IDENT_in_viewExpr3017 = new BitSet(new long[]{0x1020000037CC23C8L,0x100000000001F7E0L,0x008003FC0000003CL,0x0000077707806D00L,0x0007F000000001C4L,0x00000007666D0003L,0x0000000000000018L});
    public static final BitSet FOLLOW_valueExprWithTime_in_viewExpr3020 = new BitSet(new long[]{0x1020000037CC23C8L,0x100000000001F7E0L,0x008003FC0000003CL,0x0000077707806D00L,0x0007F000000001C4L,0x00000007666D0003L,0x0000000000000018L});
    public static final BitSet FOLLOW_WHERE_EXPR_in_whereClause3042 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_valueExpr_in_whereClause3044 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_GROUP_BY_EXPR_in_groupByClause3062 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_valueExpr_in_groupByClause3064 = new BitSet(new long[]{0x1000000037CC23C8L,0x100000000001E7E0L,0x008003FC00000000L,0x0000047707804D00L,0x0007F000000001C0L,0x00000007666D0003L,0x0000000000000018L});
    public static final BitSet FOLLOW_valueExpr_in_groupByClause3067 = new BitSet(new long[]{0x1000000037CC23C8L,0x100000000001E7E0L,0x008003FC00000000L,0x0000047707804D00L,0x0007F000000001C0L,0x00000007666D0003L,0x0000000000000018L});
    public static final BitSet FOLLOW_ORDER_BY_EXPR_in_orderByClause3085 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_orderByElement_in_orderByClause3087 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0040000000000000L});
    public static final BitSet FOLLOW_orderByElement_in_orderByClause3090 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0040000000000000L});
    public static final BitSet FOLLOW_ORDER_ELEMENT_EXPR_in_orderByElement3110 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_valueExpr_in_orderByElement3112 = new BitSet(new long[]{0x0600000000000008L});
    public static final BitSet FOLLOW_set_in_orderByElement3114 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_HAVING_EXPR_in_havingClause3137 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_valueExpr_in_havingClause3139 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_EVENT_LIMIT_EXPR_in_outputLimitExpr3157 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_set_in_outputLimitExpr3159 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000F00000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_number_in_outputLimitExpr3171 = new BitSet(new long[]{0x0000000000000008L,0x0020000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_IDENT_in_outputLimitExpr3173 = new BitSet(new long[]{0x0000000000000008L,0x0020000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_outputLimitAfter_in_outputLimitExpr3176 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_outputLimitAndTerm_in_outputLimitExpr3179 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_TIMEPERIOD_LIMIT_EXPR_in_outputLimitExpr3196 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_set_in_outputLimitExpr3198 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000002000L});
    public static final BitSet FOLLOW_timePeriod_in_outputLimitExpr3209 = new BitSet(new long[]{0x0000000000000008L,0x0020000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_outputLimitAfter_in_outputLimitExpr3211 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_outputLimitAndTerm_in_outputLimitExpr3214 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_CRONTAB_LIMIT_EXPR_in_outputLimitExpr3230 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_set_in_outputLimitExpr3232 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_crontabLimitParameterSet_in_outputLimitExpr3243 = new BitSet(new long[]{0x0000000000000008L,0x0020000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_outputLimitAfter_in_outputLimitExpr3245 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_outputLimitAndTerm_in_outputLimitExpr3248 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_WHEN_LIMIT_EXPR_in_outputLimitExpr3264 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_set_in_outputLimitExpr3266 = new BitSet(new long[]{0x1000000037CC23C0L,0x100000000001E7E0L,0x008003FC00000000L,0x0000047707804D00L,0x0007F000000001C0L,0x00000007666D0003L,0x0000000000000018L});
    public static final BitSet FOLLOW_valueExpr_in_outputLimitExpr3277 = new BitSet(new long[]{0x0000000000000008L,0x0020000000000000L,0x0000000000000001L,0x0100000000000000L});
    public static final BitSet FOLLOW_onSetExpr_in_outputLimitExpr3279 = new BitSet(new long[]{0x0000000000000008L,0x0020000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_outputLimitAfter_in_outputLimitExpr3282 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_outputLimitAndTerm_in_outputLimitExpr3285 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_TERM_LIMIT_EXPR_in_outputLimitExpr3301 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_set_in_outputLimitExpr3303 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_outputLimitAndTerm_in_outputLimitExpr3314 = new BitSet(new long[]{0x0000000000000008L,0x0020000000000000L,0x0000000000000001L,0x0100000000000000L});
    public static final BitSet FOLLOW_onSetExpr_in_outputLimitExpr3316 = new BitSet(new long[]{0x0000000000000008L,0x0020000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_outputLimitAfter_in_outputLimitExpr3319 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_outputLimitAndTerm_in_outputLimitExpr3322 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_AFTER_LIMIT_EXPR_in_outputLimitExpr3335 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_outputLimitAfter_in_outputLimitExpr3337 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_outputLimitAndTerm_in_outputLimitExpr3339 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_TERMINATED_in_outputLimitAndTerm3356 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_valueExpr_in_outputLimitAndTerm3358 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000000000000L,0x0100000000000000L});
    public static final BitSet FOLLOW_onSetExpr_in_outputLimitAndTerm3361 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_AFTER_in_outputLimitAfter3375 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_timePeriod_in_outputLimitAfter3377 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000F00000000000L});
    public static final BitSet FOLLOW_number_in_outputLimitAfter3380 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ROW_LIMIT_EXPR_in_rowLimitClause3396 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_number_in_rowLimitClause3399 = new BitSet(new long[]{0x0000000000000008L,0x0000400000000000L,0x0000000000000000L,0x0000000000000000L,0x0000F00000000000L,0x0000000000002020L});
    public static final BitSet FOLLOW_IDENT_in_rowLimitClause3401 = new BitSet(new long[]{0x0000000000000008L,0x0000400000000000L,0x0000000000000000L,0x0000000000000000L,0x0000F00000000000L,0x0000000000002020L});
    public static final BitSet FOLLOW_number_in_rowLimitClause3405 = new BitSet(new long[]{0x0000000000000008L,0x0000400000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000002000L});
    public static final BitSet FOLLOW_IDENT_in_rowLimitClause3407 = new BitSet(new long[]{0x0000000000000008L,0x0000400000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000002000L});
    public static final BitSet FOLLOW_COMMA_in_rowLimitClause3411 = new BitSet(new long[]{0x0000000000000008L,0x0000400000000000L});
    public static final BitSet FOLLOW_OFFSET_in_rowLimitClause3414 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_CRONTAB_LIMIT_EXPR_PARAM_in_crontabLimitParameterSet3432 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_valueExprWithTime_in_crontabLimitParameterSet3434 = new BitSet(new long[]{0x1020000037CC23C0L,0x100000000001F7E0L,0x008003FC0000003CL,0x0000077707806D00L,0x0007F000000001C4L,0x00000007666D0003L,0x0000000000000018L});
    public static final BitSet FOLLOW_valueExprWithTime_in_crontabLimitParameterSet3436 = new BitSet(new long[]{0x1020000037CC23C0L,0x100000000001F7E0L,0x008003FC0000003CL,0x0000077707806D00L,0x0007F000000001C4L,0x00000007666D0003L,0x0000000000000018L});
    public static final BitSet FOLLOW_valueExprWithTime_in_crontabLimitParameterSet3438 = new BitSet(new long[]{0x1020000037CC23C0L,0x100000000001F7E0L,0x008003FC0000003CL,0x0000077707806D00L,0x0007F000000001C4L,0x00000007666D0003L,0x0000000000000018L});
    public static final BitSet FOLLOW_valueExprWithTime_in_crontabLimitParameterSet3440 = new BitSet(new long[]{0x1020000037CC23C0L,0x100000000001F7E0L,0x008003FC0000003CL,0x0000077707806D00L,0x0007F000000001C4L,0x00000007666D0003L,0x0000000000000018L});
    public static final BitSet FOLLOW_valueExprWithTime_in_crontabLimitParameterSet3442 = new BitSet(new long[]{0x1020000037CC23C8L,0x100000000001F7E0L,0x008003FC0000003CL,0x0000077707806D00L,0x0007F000000001C4L,0x00000007666D0003L,0x0000000000000018L});
    public static final BitSet FOLLOW_valueExprWithTime_in_crontabLimitParameterSet3444 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_LT_in_relationalExpr3461 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_relationalExprValue_in_relationalExpr3463 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_GT_in_relationalExpr3476 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_relationalExprValue_in_relationalExpr3478 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_LE_in_relationalExpr3491 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_relationalExprValue_in_relationalExpr3493 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_GE_in_relationalExpr3505 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_relationalExprValue_in_relationalExpr3507 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_valueExpr_in_relationalExprValue3529 = new BitSet(new long[]{0x1003800037CC23C0L,0x100000000001E7E0L,0x008003FC00000000L,0x0000047707804D00L,0x0007F000000001C0L,0x00000007666D0003L,0x0000000000000018L});
    public static final BitSet FOLLOW_valueExpr_in_relationalExprValue3539 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_relationalExprValue3554 = new BitSet(new long[]{0x1000000037CC23C2L,0x100000000001E7E0L,0x008003FC00000000L,0x0000047F07804D00L,0x0007F000000001C0L,0x00000007666D0003L,0x0000000000000018L});
    public static final BitSet FOLLOW_valueExpr_in_relationalExprValue3563 = new BitSet(new long[]{0x1000000037CC23C2L,0x100000000001E7E0L,0x008003FC00000000L,0x0000047707804D00L,0x0007F000000001C0L,0x00000007666D0003L,0x0000000000000018L});
    public static final BitSet FOLLOW_subSelectGroupExpr_in_relationalExprValue3568 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EVAL_OR_EXPR_in_evalExprChoice3594 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_valueExpr_in_evalExprChoice3596 = new BitSet(new long[]{0x1000000037CC23C0L,0x100000000001E7E0L,0x008003FC00000000L,0x0000047707804D00L,0x0007F000000001C0L,0x00000007666D0003L,0x0000000000000018L});
    public static final BitSet FOLLOW_valueExpr_in_evalExprChoice3598 = new BitSet(new long[]{0x1000000037CC23C8L,0x100000000001E7E0L,0x008003FC00000000L,0x0000047707804D00L,0x0007F000000001C0L,0x00000007666D0003L,0x0000000000000018L});
    public static final BitSet FOLLOW_valueExpr_in_evalExprChoice3601 = new BitSet(new long[]{0x1000000037CC23C8L,0x100000000001E7E0L,0x008003FC00000000L,0x0000047707804D00L,0x0007F000000001C0L,0x00000007666D0003L,0x0000000000000018L});
    public static final BitSet FOLLOW_EVAL_AND_EXPR_in_evalExprChoice3615 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_valueExpr_in_evalExprChoice3617 = new BitSet(new long[]{0x1000000037CC23C0L,0x100000000001E7E0L,0x008003FC00000000L,0x0000047707804D00L,0x0007F000000001C0L,0x00000007666D0003L,0x0000000000000018L});
    public static final BitSet FOLLOW_valueExpr_in_evalExprChoice3619 = new BitSet(new long[]{0x1000000037CC23C8L,0x100000000001E7E0L,0x008003FC00000000L,0x0000047707804D00L,0x0007F000000001C0L,0x00000007666D0003L,0x0000000000000018L});
    public static final BitSet FOLLOW_valueExpr_in_evalExprChoice3622 = new BitSet(new long[]{0x1000000037CC23C8L,0x100000000001E7E0L,0x008003FC00000000L,0x0000047707804D00L,0x0007F000000001C0L,0x00000007666D0003L,0x0000000000000018L});
    public static final BitSet FOLLOW_EVAL_EQUALS_EXPR_in_evalExprChoice3636 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_valueExpr_in_evalExprChoice3638 = new BitSet(new long[]{0x1000000037CC23C0L,0x100000000001E7E0L,0x008003FC00000000L,0x0000047707804D00L,0x0007F000000001C0L,0x00000007666D0003L,0x0000000000000018L});
    public static final BitSet FOLLOW_valueExpr_in_evalExprChoice3640 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_EVAL_IS_EXPR_in_evalExprChoice3652 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_valueExpr_in_evalExprChoice3654 = new BitSet(new long[]{0x1000000037CC23C0L,0x100000000001E7E0L,0x008003FC00000000L,0x0000047707804D00L,0x0007F000000001C0L,0x00000007666D0003L,0x0000000000000018L});
    public static final BitSet FOLLOW_valueExpr_in_evalExprChoice3656 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_EVAL_NOTEQUALS_EXPR_in_evalExprChoice3668 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_valueExpr_in_evalExprChoice3670 = new BitSet(new long[]{0x1000000037CC23C0L,0x100000000001E7E0L,0x008003FC00000000L,0x0000047707804D00L,0x0007F000000001C0L,0x00000007666D0003L,0x0000000000000018L});
    public static final BitSet FOLLOW_valueExpr_in_evalExprChoice3672 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_EVAL_ISNOT_EXPR_in_evalExprChoice3684 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_valueExpr_in_evalExprChoice3686 = new BitSet(new long[]{0x1000000037CC23C0L,0x100000000001E7E0L,0x008003FC00000000L,0x0000047707804D00L,0x0007F000000001C0L,0x00000007666D0003L,0x0000000000000018L});
    public static final BitSet FOLLOW_valueExpr_in_evalExprChoice3688 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_EVAL_EQUALS_GROUP_EXPR_in_evalExprChoice3700 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_equalsSubquery_in_evalExprChoice3702 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_EVAL_IS_GROUP_EXPR_in_evalExprChoice3714 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_equalsSubquery_in_evalExprChoice3716 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_EVAL_NOTEQUALS_GROUP_EXPR_in_evalExprChoice3728 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_equalsSubquery_in_evalExprChoice3730 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_EVAL_ISNOT_GROUP_EXPR_in_evalExprChoice3742 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_equalsSubquery_in_evalExprChoice3744 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_NOT_EXPR_in_evalExprChoice3756 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_valueExpr_in_evalExprChoice3758 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_relationalExpr_in_evalExprChoice3769 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_valueExpr_in_equalsSubquery3780 = new BitSet(new long[]{0x0003800000000000L});
    public static final BitSet FOLLOW_set_in_equalsSubquery3782 = new BitSet(new long[]{0x1000000037CC23C2L,0x100000000001E7E0L,0x008003FC00000000L,0x0000047F07804D00L,0x0007F000000001C0L,0x00000007666D0003L,0x0000000000000018L});
    public static final BitSet FOLLOW_valueExpr_in_equalsSubquery3791 = new BitSet(new long[]{0x1000000037CC23C2L,0x100000000001E7E0L,0x008003FC00000000L,0x0000047707804D00L,0x0007F000000001C0L,0x00000007666D0003L,0x0000000000000018L});
    public static final BitSet FOLLOW_subSelectGroupExpr_in_equalsSubquery3796 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_constant_in_valueExpr3810 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_substitution_in_valueExpr3816 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_arithmeticExpr_in_valueExpr3822 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_eventPropertyExpr_in_valueExpr3829 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_evalExprChoice_in_valueExpr3838 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_builtinFunc_in_valueExpr3843 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_libFuncChain_in_valueExpr3851 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_caseExpr_in_valueExpr3856 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_inExpr_in_valueExpr3861 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_betweenExpr_in_valueExpr3867 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_likeExpr_in_valueExpr3872 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_regExpExpr_in_valueExpr3877 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_arrayExpr_in_valueExpr3882 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_subSelectInExpr_in_valueExpr3887 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_subSelectRowExpr_in_valueExpr3893 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_subSelectExistsExpr_in_valueExpr3900 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_dotExpr_in_valueExpr3905 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_newExpr_in_valueExpr3910 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_jsonarray_in_valueExpr3915 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_jsonobject_in_valueExpr3921 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LAST_in_valueExprWithTime3935 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LW_in_valueExprWithTime3944 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_valueExpr_in_valueExprWithTime3951 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OBJECT_PARAM_ORDERED_EXPR_in_valueExprWithTime3959 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_valueExpr_in_valueExprWithTime3961 = new BitSet(new long[]{0x0600000000000000L});
    public static final BitSet FOLLOW_set_in_valueExprWithTime3963 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_rangeOperator_in_valueExprWithTime3976 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_frequencyOperator_in_valueExprWithTime3982 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lastOperator_in_valueExprWithTime3987 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_weekDayOperator_in_valueExprWithTime3992 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NUMERIC_PARAM_LIST_in_valueExprWithTime4002 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_numericParameterList_in_valueExprWithTime4004 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000000000014L,0x0000000000000000L,0x0007F00000000000L});
    public static final BitSet FOLLOW_NUMBERSETSTAR_in_valueExprWithTime4015 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_timePeriod_in_valueExprWithTime4022 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_constant_in_numericParameterList4035 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rangeOperator_in_numericParameterList4042 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_frequencyOperator_in_numericParameterList4048 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NUMERIC_PARAM_RANGE_in_rangeOperator4064 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_constant_in_rangeOperator4067 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0080000000000000L,0x0000040000000000L,0x0007F00000000000L});
    public static final BitSet FOLLOW_eventPropertyExpr_in_rangeOperator4070 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0080000000000000L,0x0000040000000000L,0x0007F00000000000L});
    public static final BitSet FOLLOW_substitution_in_rangeOperator4073 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0080000000000000L,0x0000040000000000L,0x0007F00000000000L});
    public static final BitSet FOLLOW_constant_in_rangeOperator4077 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_eventPropertyExpr_in_rangeOperator4080 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_substitution_in_rangeOperator4083 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_NUMERIC_PARAM_FREQUENCY_in_frequencyOperator4104 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_constant_in_frequencyOperator4107 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_eventPropertyExpr_in_frequencyOperator4110 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_substitution_in_frequencyOperator4113 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_LAST_OPERATOR_in_lastOperator4132 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_constant_in_lastOperator4135 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_eventPropertyExpr_in_lastOperator4138 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_substitution_in_lastOperator4141 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_WEEKDAY_OPERATOR_in_weekDayOperator4160 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_constant_in_weekDayOperator4163 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_eventPropertyExpr_in_weekDayOperator4166 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_substitution_in_weekDayOperator4169 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_SUBSELECT_GROUP_EXPR_in_subSelectGroupExpr4190 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_subQueryExpr_in_subSelectGroupExpr4192 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_SUBSELECT_EXPR_in_subSelectRowExpr4211 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_subQueryExpr_in_subSelectRowExpr4213 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_EXISTS_SUBSELECT_EXPR_in_subSelectExistsExpr4232 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_subQueryExpr_in_subSelectExistsExpr4234 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_IN_SUBSELECT_EXPR_in_subSelectInExpr4253 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_valueExpr_in_subSelectInExpr4255 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000008000000000L});
    public static final BitSet FOLLOW_subSelectInQueryExpr_in_subSelectInExpr4257 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_NOT_IN_SUBSELECT_EXPR_in_subSelectInExpr4269 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_valueExpr_in_subSelectInExpr4271 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000008000000000L});
    public static final BitSet FOLLOW_subSelectInQueryExpr_in_subSelectInExpr4273 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_IN_SUBSELECT_QUERY_EXPR_in_subSelectInQueryExpr4292 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_subQueryExpr_in_subSelectInQueryExpr4294 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_DISTINCT_in_subQueryExpr4310 = new BitSet(new long[]{0x0000400000000000L,0x0000000000020000L,0x0000300000000000L,0x0000000040000000L});
    public static final BitSet FOLLOW_selectionList_in_subQueryExpr4313 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000400000000000L});
    public static final BitSet FOLLOW_subSelectFilterExpr_in_subQueryExpr4315 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000080000000L});
    public static final BitSet FOLLOW_whereClause_in_subQueryExpr4318 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STREAM_EXPR_in_subSelectFilterExpr4336 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_eventFilterExpr_in_subSelectFilterExpr4338 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000003L,0x0000000010000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_viewListExpr_in_subSelectFilterExpr4342 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000003L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_IDENT_in_subSelectFilterExpr4347 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000003L});
    public static final BitSet FOLLOW_RETAINUNION_in_subSelectFilterExpr4351 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000002L});
    public static final BitSet FOLLOW_RETAININTERSECTION_in_subSelectFilterExpr4354 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_CASE_in_caseExpr4374 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_valueExpr_in_caseExpr4377 = new BitSet(new long[]{0x1000000037CC23C8L,0x100000000001E7E0L,0x008003FC00000000L,0x0000047707804D00L,0x0007F000000001C0L,0x00000007666D0003L,0x0000000000000018L});
    public static final BitSet FOLLOW_CASE2_in_caseExpr4390 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_valueExpr_in_caseExpr4393 = new BitSet(new long[]{0x1000000037CC23C8L,0x100000000001E7E0L,0x008003FC00000000L,0x0000047707804D00L,0x0007F000000001C0L,0x00000007666D0003L,0x0000000000000018L});
    public static final BitSet FOLLOW_IN_SET_in_inExpr4413 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_valueExpr_in_inExpr4415 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000048L});
    public static final BitSet FOLLOW_set_in_inExpr4417 = new BitSet(new long[]{0x1000000037CC23C0L,0x100000000001E7E0L,0x008003FC00000000L,0x0000047707804D00L,0x0007F000000001C0L,0x00000007666D0003L,0x0000000000000018L});
    public static final BitSet FOLLOW_valueExpr_in_inExpr4423 = new BitSet(new long[]{0x1000000037CC23C0L,0x100000000001E7E0L,0x008003FC00000000L,0x0000047707804D00L,0x0007F000000001C0L,0x00000007666D0093L,0x0000000000000018L});
    public static final BitSet FOLLOW_valueExpr_in_inExpr4426 = new BitSet(new long[]{0x1000000037CC23C0L,0x100000000001E7E0L,0x008003FC00000000L,0x0000047707804D00L,0x0007F000000001C0L,0x00000007666D0093L,0x0000000000000018L});
    public static final BitSet FOLLOW_set_in_inExpr4430 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_NOT_IN_SET_in_inExpr4445 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_valueExpr_in_inExpr4447 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000048L});
    public static final BitSet FOLLOW_set_in_inExpr4449 = new BitSet(new long[]{0x1000000037CC23C0L,0x100000000001E7E0L,0x008003FC00000000L,0x0000047707804D00L,0x0007F000000001C0L,0x00000007666D0003L,0x0000000000000018L});
    public static final BitSet FOLLOW_valueExpr_in_inExpr4455 = new BitSet(new long[]{0x1000000037CC23C0L,0x100000000001E7E0L,0x008003FC00000000L,0x0000047707804D00L,0x0007F000000001C0L,0x00000007666D0093L,0x0000000000000018L});
    public static final BitSet FOLLOW_valueExpr_in_inExpr4458 = new BitSet(new long[]{0x1000000037CC23C0L,0x100000000001E7E0L,0x008003FC00000000L,0x0000047707804D00L,0x0007F000000001C0L,0x00000007666D0093L,0x0000000000000018L});
    public static final BitSet FOLLOW_set_in_inExpr4462 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_IN_RANGE_in_inExpr4477 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_valueExpr_in_inExpr4479 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000048L});
    public static final BitSet FOLLOW_set_in_inExpr4481 = new BitSet(new long[]{0x1000000037CC23C0L,0x100000000001E7E0L,0x008003FC00000000L,0x0000047707804D00L,0x0007F000000001C0L,0x00000007666D0003L,0x0000000000000018L});
    public static final BitSet FOLLOW_valueExpr_in_inExpr4487 = new BitSet(new long[]{0x1000000037CC23C0L,0x100000000001E7E0L,0x008003FC00000000L,0x0000047707804D00L,0x0007F000000001C0L,0x00000007666D0003L,0x0000000000000018L});
    public static final BitSet FOLLOW_valueExpr_in_inExpr4489 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000090L});
    public static final BitSet FOLLOW_set_in_inExpr4491 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_NOT_IN_RANGE_in_inExpr4506 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_valueExpr_in_inExpr4508 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000048L});
    public static final BitSet FOLLOW_set_in_inExpr4510 = new BitSet(new long[]{0x1000000037CC23C0L,0x100000000001E7E0L,0x008003FC00000000L,0x0000047707804D00L,0x0007F000000001C0L,0x00000007666D0003L,0x0000000000000018L});
    public static final BitSet FOLLOW_valueExpr_in_inExpr4516 = new BitSet(new long[]{0x1000000037CC23C0L,0x100000000001E7E0L,0x008003FC00000000L,0x0000047707804D00L,0x0007F000000001C0L,0x00000007666D0003L,0x0000000000000018L});
    public static final BitSet FOLLOW_valueExpr_in_inExpr4518 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000090L});
    public static final BitSet FOLLOW_set_in_inExpr4520 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BETWEEN_in_betweenExpr4543 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_valueExpr_in_betweenExpr4545 = new BitSet(new long[]{0x1000000037CC23C0L,0x100000000001E7E0L,0x008003FC00000000L,0x0000047707804D00L,0x0007F000000001C0L,0x00000007666D0003L,0x0000000000000018L});
    public static final BitSet FOLLOW_valueExpr_in_betweenExpr4547 = new BitSet(new long[]{0x1000000037CC23C0L,0x100000000001E7E0L,0x008003FC00000000L,0x0000047707804D00L,0x0007F000000001C0L,0x00000007666D0003L,0x0000000000000018L});
    public static final BitSet FOLLOW_valueExpr_in_betweenExpr4549 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_NOT_BETWEEN_in_betweenExpr4560 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_valueExpr_in_betweenExpr4562 = new BitSet(new long[]{0x1000000037CC23C0L,0x100000000001E7E0L,0x008003FC00000000L,0x0000047707804D00L,0x0007F000000001C0L,0x00000007666D0003L,0x0000000000000018L});
    public static final BitSet FOLLOW_valueExpr_in_betweenExpr4564 = new BitSet(new long[]{0x1000000037CC23C8L,0x100000000001E7E0L,0x008003FC00000000L,0x0000047707804D00L,0x0007F000000001C0L,0x00000007666D0003L,0x0000000000000018L});
    public static final BitSet FOLLOW_valueExpr_in_betweenExpr4567 = new BitSet(new long[]{0x1000000037CC23C8L,0x100000000001E7E0L,0x008003FC00000000L,0x0000047707804D00L,0x0007F000000001C0L,0x00000007666D0003L,0x0000000000000018L});
    public static final BitSet FOLLOW_LIKE_in_likeExpr4587 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_valueExpr_in_likeExpr4589 = new BitSet(new long[]{0x1000000037CC23C0L,0x100000000001E7E0L,0x008003FC00000000L,0x0000047707804D00L,0x0007F000000001C0L,0x00000007666D0003L,0x0000000000000018L});
    public static final BitSet FOLLOW_valueExpr_in_likeExpr4591 = new BitSet(new long[]{0x1000000037CC23C8L,0x100000000001E7E0L,0x008003FC00000000L,0x0000047707804D00L,0x0007F000000001C0L,0x00000007666D0003L,0x0000000000000018L});
    public static final BitSet FOLLOW_valueExpr_in_likeExpr4594 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_NOT_LIKE_in_likeExpr4607 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_valueExpr_in_likeExpr4609 = new BitSet(new long[]{0x1000000037CC23C0L,0x100000000001E7E0L,0x008003FC00000000L,0x0000047707804D00L,0x0007F000000001C0L,0x00000007666D0003L,0x0000000000000018L});
    public static final BitSet FOLLOW_valueExpr_in_likeExpr4611 = new BitSet(new long[]{0x1000000037CC23C8L,0x100000000001E7E0L,0x008003FC00000000L,0x0000047707804D00L,0x0007F000000001C0L,0x00000007666D0003L,0x0000000000000018L});
    public static final BitSet FOLLOW_valueExpr_in_likeExpr4614 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_REGEXP_in_regExpExpr4633 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_valueExpr_in_regExpExpr4635 = new BitSet(new long[]{0x1000000037CC23C0L,0x100000000001E7E0L,0x008003FC00000000L,0x0000047707804D00L,0x0007F000000001C0L,0x00000007666D0003L,0x0000000000000018L});
    public static final BitSet FOLLOW_valueExpr_in_regExpExpr4637 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_NOT_REGEXP_in_regExpExpr4648 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_valueExpr_in_regExpExpr4650 = new BitSet(new long[]{0x1000000037CC23C0L,0x100000000001E7E0L,0x008003FC00000000L,0x0000047707804D00L,0x0007F000000001C0L,0x00000007666D0003L,0x0000000000000018L});
    public static final BitSet FOLLOW_valueExpr_in_regExpExpr4652 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_SUM_in_builtinFunc4671 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_DISTINCT_in_builtinFunc4674 = new BitSet(new long[]{0x1000000037CC23C0L,0x100000000001E7E0L,0x008003FC00000000L,0x0000047707804D00L,0x0007F000000001C0L,0x00000007666D0003L,0x0000000000000018L});
    public static final BitSet FOLLOW_valueExpr_in_builtinFunc4678 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_aggregationFilterExpr_in_builtinFunc4680 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_AVG_in_builtinFunc4692 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_DISTINCT_in_builtinFunc4695 = new BitSet(new long[]{0x1000000037CC23C0L,0x100000000001E7E0L,0x008003FC00000000L,0x0000047707804D00L,0x0007F000000001C0L,0x00000007666D0003L,0x0000000000000018L});
    public static final BitSet FOLLOW_valueExpr_in_builtinFunc4699 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_aggregationFilterExpr_in_builtinFunc4701 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_COUNT_in_builtinFunc4713 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_DISTINCT_in_builtinFunc4717 = new BitSet(new long[]{0x1000000037CC23C0L,0x100000000001E7E0L,0x008003FC00000000L,0x0000047707804D00L,0x0007F000000001C0L,0x00000007666D0003L,0x0000000000000018L});
    public static final BitSet FOLLOW_valueExpr_in_builtinFunc4721 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_aggregationFilterExpr_in_builtinFunc4725 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_MEDIAN_in_builtinFunc4737 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_DISTINCT_in_builtinFunc4740 = new BitSet(new long[]{0x1000000037CC23C0L,0x100000000001E7E0L,0x008003FC00000000L,0x0000047707804D00L,0x0007F000000001C0L,0x00000007666D0003L,0x0000000000000018L});
    public static final BitSet FOLLOW_valueExpr_in_builtinFunc4744 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_aggregationFilterExpr_in_builtinFunc4746 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_STDDEV_in_builtinFunc4758 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_DISTINCT_in_builtinFunc4761 = new BitSet(new long[]{0x1000000037CC23C0L,0x100000000001E7E0L,0x008003FC00000000L,0x0000047707804D00L,0x0007F000000001C0L,0x00000007666D0003L,0x0000000000000018L});
    public static final BitSet FOLLOW_valueExpr_in_builtinFunc4765 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_aggregationFilterExpr_in_builtinFunc4767 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_AVEDEV_in_builtinFunc4779 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_DISTINCT_in_builtinFunc4782 = new BitSet(new long[]{0x1000000037CC23C0L,0x100000000001E7E0L,0x008003FC00000000L,0x0000047707804D00L,0x0007F000000001C0L,0x00000007666D0003L,0x0000000000000018L});
    public static final BitSet FOLLOW_valueExpr_in_builtinFunc4786 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_aggregationFilterExpr_in_builtinFunc4788 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_LAST_AGGREG_in_builtinFunc4800 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_DISTINCT_in_builtinFunc4803 = new BitSet(new long[]{0x1000000037CC23C8L,0x100000000001E7E0L,0x008003FC00000000L,0x0000047707804D00L,0x0007F000000003C0L,0x00000007666D0003L,0x0000000000000018L});
    public static final BitSet FOLLOW_accessValueExpr_in_builtinFunc4807 = new BitSet(new long[]{0x1000000037CC23C8L,0x100000000001E7E0L,0x008003FC00000000L,0x0000047707804D00L,0x0007F000000001C0L,0x00000007666D0003L,0x0000000000000018L});
    public static final BitSet FOLLOW_valueExpr_in_builtinFunc4810 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_FIRST_AGGREG_in_builtinFunc4822 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_DISTINCT_in_builtinFunc4825 = new BitSet(new long[]{0x1000000037CC23C8L,0x100000000001E7E0L,0x008003FC00000000L,0x0000047707804D00L,0x0007F000000003C0L,0x00000007666D0003L,0x0000000000000018L});
    public static final BitSet FOLLOW_accessValueExpr_in_builtinFunc4829 = new BitSet(new long[]{0x1000000037CC23C8L,0x100000000001E7E0L,0x008003FC00000000L,0x0000047707804D00L,0x0007F000000001C0L,0x00000007666D0003L,0x0000000000000018L});
    public static final BitSet FOLLOW_valueExpr_in_builtinFunc4832 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_WINDOW_AGGREG_in_builtinFunc4844 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_DISTINCT_in_builtinFunc4847 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_accessValueExpr_in_builtinFunc4851 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_COALESCE_in_builtinFunc4864 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_valueExpr_in_builtinFunc4866 = new BitSet(new long[]{0x1000000037CC23C0L,0x100000000001E7E0L,0x008003FC00000000L,0x0000047707804D00L,0x0007F000000001C0L,0x00000007666D0003L,0x0000000000000018L});
    public static final BitSet FOLLOW_valueExpr_in_builtinFunc4868 = new BitSet(new long[]{0x1000000037CC23C8L,0x100000000001E7E0L,0x008003FC00000000L,0x0000047707804D00L,0x0007F000000001C0L,0x00000007666D0003L,0x0000000000000018L});
    public static final BitSet FOLLOW_valueExpr_in_builtinFunc4871 = new BitSet(new long[]{0x1000000037CC23C8L,0x100000000001E7E0L,0x008003FC00000000L,0x0000047707804D00L,0x0007F000000001C0L,0x00000007666D0003L,0x0000000000000018L});
    public static final BitSet FOLLOW_PREVIOUS_in_builtinFunc4886 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_valueExpr_in_builtinFunc4888 = new BitSet(new long[]{0x1000000037CC23C8L,0x100000000001E7E0L,0x008003FC00000000L,0x0000047707804D00L,0x0007F000000001C0L,0x00000007666D0003L,0x0000000000000018L});
    public static final BitSet FOLLOW_valueExpr_in_builtinFunc4890 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_PREVIOUSTAIL_in_builtinFunc4903 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_valueExpr_in_builtinFunc4905 = new BitSet(new long[]{0x1000000037CC23C8L,0x100000000001E7E0L,0x008003FC00000000L,0x0000047707804D00L,0x0007F000000001C0L,0x00000007666D0003L,0x0000000000000018L});
    public static final BitSet FOLLOW_valueExpr_in_builtinFunc4907 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_PREVIOUSCOUNT_in_builtinFunc4920 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_valueExpr_in_builtinFunc4922 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_PREVIOUSWINDOW_in_builtinFunc4934 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_valueExpr_in_builtinFunc4936 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_PRIOR_in_builtinFunc4948 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_NUM_INT_in_builtinFunc4952 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0080000000000000L});
    public static final BitSet FOLLOW_eventPropertyExpr_in_builtinFunc4954 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_INSTANCEOF_in_builtinFunc4967 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_valueExpr_in_builtinFunc4969 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000002000000L});
    public static final BitSet FOLLOW_CLASS_IDENT_in_builtinFunc4971 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000002000000L});
    public static final BitSet FOLLOW_CLASS_IDENT_in_builtinFunc4974 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000002000000L});
    public static final BitSet FOLLOW_TYPEOF_in_builtinFunc4988 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_valueExpr_in_builtinFunc4990 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_CAST_in_builtinFunc5002 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_valueExpr_in_builtinFunc5004 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000002000000L});
    public static final BitSet FOLLOW_CLASS_IDENT_in_builtinFunc5006 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_EXISTS_in_builtinFunc5018 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_eventPropertyExpr_in_builtinFunc5020 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_CURRENT_TIMESTAMP_in_builtinFunc5032 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ISTREAM_in_builtinFunc5045 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_AGG_FILTER_EXPR_in_aggregationFilterExpr5062 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_valueExpr_in_aggregationFilterExpr5064 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ACCESS_AGG_in_accessValueExpr5078 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_accessValueExprChoice_in_accessValueExpr5080 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_PROPERTY_WILDCARD_SELECT_in_accessValueExprChoice5095 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PROPERTY_SELECTION_STREAM_in_accessValueExprChoice5102 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_IDENT_in_accessValueExprChoice5104 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_IDENT_in_accessValueExprChoice5106 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_valueExpr_in_accessValueExprChoice5112 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ARRAY_EXPR_in_arrayExpr5128 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_valueExpr_in_arrayExpr5131 = new BitSet(new long[]{0x1000000037CC23C8L,0x100000000001E7E0L,0x008003FC00000000L,0x0000047707804D00L,0x0007F000000001C0L,0x00000007666D0003L,0x0000000000000018L});
    public static final BitSet FOLLOW_PLUS_in_arithmeticExpr5152 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_valueExpr_in_arithmeticExpr5154 = new BitSet(new long[]{0x1000000037CC23C0L,0x100000000001E7E0L,0x008003FC00000000L,0x0000047707804D00L,0x0007F000000001C0L,0x00000007666D0003L,0x0000000000000018L});
    public static final BitSet FOLLOW_valueExpr_in_arithmeticExpr5156 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_MINUS_in_arithmeticExpr5168 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_valueExpr_in_arithmeticExpr5170 = new BitSet(new long[]{0x1000000037CC23C0L,0x100000000001E7E0L,0x008003FC00000000L,0x0000047707804D00L,0x0007F000000001C0L,0x00000007666D0003L,0x0000000000000018L});
    public static final BitSet FOLLOW_valueExpr_in_arithmeticExpr5172 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_DIV_in_arithmeticExpr5184 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_valueExpr_in_arithmeticExpr5186 = new BitSet(new long[]{0x1000000037CC23C0L,0x100000000001E7E0L,0x008003FC00000000L,0x0000047707804D00L,0x0007F000000001C0L,0x00000007666D0003L,0x0000000000000018L});
    public static final BitSet FOLLOW_valueExpr_in_arithmeticExpr5188 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_STAR_in_arithmeticExpr5199 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_valueExpr_in_arithmeticExpr5201 = new BitSet(new long[]{0x1000000037CC23C0L,0x100000000001E7E0L,0x008003FC00000000L,0x0000047707804D00L,0x0007F000000001C0L,0x00000007666D0003L,0x0000000000000018L});
    public static final BitSet FOLLOW_valueExpr_in_arithmeticExpr5203 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_MOD_in_arithmeticExpr5215 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_valueExpr_in_arithmeticExpr5217 = new BitSet(new long[]{0x1000000037CC23C0L,0x100000000001E7E0L,0x008003FC00000000L,0x0000047707804D00L,0x0007F000000001C0L,0x00000007666D0003L,0x0000000000000018L});
    public static final BitSet FOLLOW_valueExpr_in_arithmeticExpr5219 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BAND_in_arithmeticExpr5230 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_valueExpr_in_arithmeticExpr5232 = new BitSet(new long[]{0x1000000037CC23C0L,0x100000000001E7E0L,0x008003FC00000000L,0x0000047707804D00L,0x0007F000000001C0L,0x00000007666D0003L,0x0000000000000018L});
    public static final BitSet FOLLOW_valueExpr_in_arithmeticExpr5234 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BOR_in_arithmeticExpr5245 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_valueExpr_in_arithmeticExpr5247 = new BitSet(new long[]{0x1000000037CC23C0L,0x100000000001E7E0L,0x008003FC00000000L,0x0000047707804D00L,0x0007F000000001C0L,0x00000007666D0003L,0x0000000000000018L});
    public static final BitSet FOLLOW_valueExpr_in_arithmeticExpr5249 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BXOR_in_arithmeticExpr5260 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_valueExpr_in_arithmeticExpr5262 = new BitSet(new long[]{0x1000000037CC23C0L,0x100000000001E7E0L,0x008003FC00000000L,0x0000047707804D00L,0x0007F000000001C0L,0x00000007666D0003L,0x0000000000000018L});
    public static final BitSet FOLLOW_valueExpr_in_arithmeticExpr5264 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_CONCAT_in_arithmeticExpr5276 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_valueExpr_in_arithmeticExpr5278 = new BitSet(new long[]{0x1000000037CC23C0L,0x100000000001E7E0L,0x008003FC00000000L,0x0000047707804D00L,0x0007F000000001C0L,0x00000007666D0003L,0x0000000000000018L});
    public static final BitSet FOLLOW_valueExpr_in_arithmeticExpr5280 = new BitSet(new long[]{0x1000000037CC23C8L,0x100000000001E7E0L,0x008003FC00000000L,0x0000047707804D00L,0x0007F000000001C0L,0x00000007666D0003L,0x0000000000000018L});
    public static final BitSet FOLLOW_valueExpr_in_arithmeticExpr5283 = new BitSet(new long[]{0x1000000037CC23C8L,0x100000000001E7E0L,0x008003FC00000000L,0x0000047707804D00L,0x0007F000000001C0L,0x00000007666D0003L,0x0000000000000018L});
    public static final BitSet FOLLOW_DOT_EXPR_in_dotExpr5303 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_valueExpr_in_dotExpr5305 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000000000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_libFunctionWithClass_in_dotExpr5307 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000000000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_NEWKW_in_newExpr5325 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_newAssign_in_newExpr5327 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000200000L});
    public static final BitSet FOLLOW_NEW_ITEM_in_newAssign5343 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_eventPropertyExpr_in_newAssign5345 = new BitSet(new long[]{0x1000000037CC23C8L,0x100000000001E7E0L,0x008003FC00000000L,0x0000047707804D00L,0x0007F000000001C0L,0x00000007666D0003L,0x0000000000000018L});
    public static final BitSet FOLLOW_valueExpr_in_newAssign5348 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_LIB_FUNC_CHAIN_in_libFuncChain5366 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_libFunctionWithClass_in_libFuncChain5368 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0080000000000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_libOrPropFunction_in_libFuncChain5370 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0080000000000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_LIB_FUNCTION_in_libFunctionWithClass5390 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_CLASS_IDENT_in_libFunctionWithClass5393 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_IDENT_in_libFunctionWithClass5397 = new BitSet(new long[]{0x1020400037CC23C8L,0x100000000001F7E0L,0x008003FC0000003CL,0x0000077707806D00L,0x0007F000000001C4L,0x00000007666D0843L,0x0000000000000018L});
    public static final BitSet FOLLOW_DISTINCT_in_libFunctionWithClass5400 = new BitSet(new long[]{0x1020000037CC23C8L,0x100000000001F7E0L,0x008003FC0000003CL,0x0000077707806D00L,0x0007F000000001C4L,0x00000007666D0843L,0x0000000000000018L});
    public static final BitSet FOLLOW_libFunctionArgItem_in_libFunctionWithClass5404 = new BitSet(new long[]{0x1020000037CC23C8L,0x100000000001F7E0L,0x008003FC0000003CL,0x0000077707806D00L,0x0007F000000001C4L,0x00000007666D0843L,0x0000000000000018L});
    public static final BitSet FOLLOW_LPAREN_in_libFunctionWithClass5407 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_expressionLambdaDecl_in_libFunctionArgItem5421 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_valueExprWithTime_in_libFunctionArgItem5425 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_eventPropertyExpr_in_libOrPropFunction5440 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_libFunctionWithClass_in_libOrPropFunction5450 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotation_in_startPatternExpressionRule5465 = new BitSet(new long[]{0x000000000000D800L,0x0000000000000000L,0x000000000C000340L,0x0800000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_exprChoice_in_startPatternExpressionRule5469 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_atomicExpr_in_exprChoice5483 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_patternOp_in_exprChoice5488 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EVERY_EXPR_in_exprChoice5498 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_exprChoice_in_exprChoice5500 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_EVERY_DISTINCT_EXPR_in_exprChoice5514 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_distinctExpressions_in_exprChoice5516 = new BitSet(new long[]{0x000000000000D800L,0x0000000000000000L,0x000000000C000340L,0x0800000000000000L});
    public static final BitSet FOLLOW_exprChoice_in_exprChoice5518 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_PATTERN_NOT_EXPR_in_exprChoice5532 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_exprChoice_in_exprChoice5534 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_GUARD_EXPR_in_exprChoice5548 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_exprChoice_in_exprChoice5550 = new BitSet(new long[]{0x1000000037CC23C0L,0x100000000001E7E0L,0x008003FC00000000L,0x0000047707804D00L,0x0007F000000001C0L,0x00000007666D0023L,0x0000000000000018L});
    public static final BitSet FOLLOW_IDENT_in_exprChoice5553 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_IDENT_in_exprChoice5555 = new BitSet(new long[]{0x1020000037CC23C8L,0x100000000001F7E0L,0x008003FC0000003CL,0x0000077707806D00L,0x0007F000000001C4L,0x00000007666D0003L,0x0000000000000018L});
    public static final BitSet FOLLOW_valueExprWithTime_in_exprChoice5557 = new BitSet(new long[]{0x1020000037CC23C8L,0x100000000001F7E0L,0x008003FC0000003CL,0x0000077707806D00L,0x0007F000000001C4L,0x00000007666D0003L,0x0000000000000018L});
    public static final BitSet FOLLOW_valueExpr_in_exprChoice5562 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_MATCH_UNTIL_EXPR_in_exprChoice5576 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_matchUntilRange_in_exprChoice5578 = new BitSet(new long[]{0x000000000000D800L,0x0000000000000000L,0x000000000C000340L,0x0800000000000000L});
    public static final BitSet FOLLOW_exprChoice_in_exprChoice5581 = new BitSet(new long[]{0x000000000000D808L,0x0000000000000000L,0x000000000C000340L,0x0800000000000000L});
    public static final BitSet FOLLOW_exprChoice_in_exprChoice5583 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_PATTERN_EVERY_DISTINCT_EXPR_in_distinctExpressions5604 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_valueExprWithTime_in_distinctExpressions5606 = new BitSet(new long[]{0x1020000037CC23C8L,0x100000000001F7E0L,0x008003FC0000003CL,0x0000077707806D00L,0x0007F000000001C4L,0x00000007666D0003L,0x0000000000000018L});
    public static final BitSet FOLLOW_FOLLOWED_BY_EXPR_in_patternOp5625 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_followedByItem_in_patternOp5627 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000080L});
    public static final BitSet FOLLOW_followedByItem_in_patternOp5629 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000000000080L});
    public static final BitSet FOLLOW_followedByItem_in_patternOp5632 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000000000080L});
    public static final BitSet FOLLOW_OR_EXPR_in_patternOp5648 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_exprChoice_in_patternOp5650 = new BitSet(new long[]{0x000000000000D800L,0x0000000000000000L,0x000000000C000340L,0x0800000000000000L});
    public static final BitSet FOLLOW_exprChoice_in_patternOp5652 = new BitSet(new long[]{0x000000000000D808L,0x0000000000000000L,0x000000000C000340L,0x0800000000000000L});
    public static final BitSet FOLLOW_exprChoice_in_patternOp5655 = new BitSet(new long[]{0x000000000000D808L,0x0000000000000000L,0x000000000C000340L,0x0800000000000000L});
    public static final BitSet FOLLOW_AND_EXPR_in_patternOp5671 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_exprChoice_in_patternOp5673 = new BitSet(new long[]{0x000000000000D800L,0x0000000000000000L,0x000000000C000340L,0x0800000000000000L});
    public static final BitSet FOLLOW_exprChoice_in_patternOp5675 = new BitSet(new long[]{0x000000000000D808L,0x0000000000000000L,0x000000000C000340L,0x0800000000000000L});
    public static final BitSet FOLLOW_exprChoice_in_patternOp5678 = new BitSet(new long[]{0x000000000000D808L,0x0000000000000000L,0x000000000C000340L,0x0800000000000000L});
    public static final BitSet FOLLOW_FOLLOWED_BY_ITEM_in_followedByItem5699 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_valueExpr_in_followedByItem5701 = new BitSet(new long[]{0x000000000000D800L,0x0000000000000000L,0x000000000C000340L,0x0800000000000000L});
    public static final BitSet FOLLOW_exprChoice_in_followedByItem5704 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_patternFilterExpr_in_atomicExpr5718 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OBSERVER_EXPR_in_atomicExpr5730 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_IDENT_in_atomicExpr5732 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_IDENT_in_atomicExpr5734 = new BitSet(new long[]{0x1020000037CC23C8L,0x100000000001F7E0L,0x008003FC0000003CL,0x0000077707806D00L,0x0007F000000001C4L,0x00000007666D0003L,0x0000000000000018L});
    public static final BitSet FOLLOW_valueExprWithTime_in_atomicExpr5736 = new BitSet(new long[]{0x1020000037CC23C8L,0x100000000001F7E0L,0x008003FC0000003CL,0x0000077707806D00L,0x0007F000000001C4L,0x00000007666D0003L,0x0000000000000018L});
    public static final BitSet FOLLOW_PATTERN_FILTER_EXPR_in_patternFilterExpr5756 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_IDENT_in_patternFilterExpr5758 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000002000000L});
    public static final BitSet FOLLOW_CLASS_IDENT_in_patternFilterExpr5761 = new BitSet(new long[]{0x1000000037CC23C8L,0x100000000001E7E0L,0x008003FC00001000L,0x0000047707804D00L,0x0007F000000001C0L,0x00000007666D1003L,0x0000000000000018L});
    public static final BitSet FOLLOW_propertyExpression_in_patternFilterExpr5763 = new BitSet(new long[]{0x1000000037CC23C8L,0x100000000001E7E0L,0x008003FC00000000L,0x0000047707804D00L,0x0007F000000001C0L,0x00000007666D1003L,0x0000000000000018L});
    public static final BitSet FOLLOW_patternFilterAnno_in_patternFilterExpr5766 = new BitSet(new long[]{0x1000000037CC23C8L,0x100000000001E7E0L,0x008003FC00000000L,0x0000047707804D00L,0x0007F000000001C0L,0x00000007666D0003L,0x0000000000000018L});
    public static final BitSet FOLLOW_valueExpr_in_patternFilterExpr5770 = new BitSet(new long[]{0x1000000037CC23C8L,0x100000000001E7E0L,0x008003FC00000000L,0x0000047707804D00L,0x0007F000000001C0L,0x00000007666D0003L,0x0000000000000018L});
    public static final BitSet FOLLOW_ATCHAR_in_patternFilterAnno5790 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_IDENT_in_patternFilterAnno5792 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000F00000000000L});
    public static final BitSet FOLLOW_number_in_patternFilterAnno5794 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_MATCH_UNTIL_RANGE_CLOSED_in_matchUntilRange5809 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_valueExpr_in_matchUntilRange5811 = new BitSet(new long[]{0x1000000037CC23C0L,0x100000000001E7E0L,0x008003FC00000000L,0x0000047707804D00L,0x0007F000000001C0L,0x00000007666D0003L,0x0000000000000018L});
    public static final BitSet FOLLOW_valueExpr_in_matchUntilRange5813 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_MATCH_UNTIL_RANGE_BOUNDED_in_matchUntilRange5821 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_valueExpr_in_matchUntilRange5823 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_MATCH_UNTIL_RANGE_HALFCLOSED_in_matchUntilRange5831 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_valueExpr_in_matchUntilRange5833 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_MATCH_UNTIL_RANGE_HALFOPEN_in_matchUntilRange5840 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_valueExpr_in_matchUntilRange5842 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_EVENT_FILTER_PARAM_in_filterParam5855 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_valueExpr_in_filterParam5857 = new BitSet(new long[]{0x1000000037CC23C8L,0x100000000001E7E0L,0x008003FC00000000L,0x0000047707804D00L,0x0007F000000001C0L,0x00000007666D0003L,0x0000000000000018L});
    public static final BitSet FOLLOW_valueExpr_in_filterParam5860 = new BitSet(new long[]{0x1000000037CC23C8L,0x100000000001E7E0L,0x008003FC00000000L,0x0000047707804D00L,0x0007F000000001C0L,0x00000007666D0003L,0x0000000000000018L});
    public static final BitSet FOLLOW_EQUALS_in_filterParamComparator5876 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_filterAtom_in_filterParamComparator5878 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_NOT_EQUAL_in_filterParamComparator5885 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_filterAtom_in_filterParamComparator5887 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_LT_in_filterParamComparator5894 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_filterAtom_in_filterParamComparator5896 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_LE_in_filterParamComparator5903 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_filterAtom_in_filterParamComparator5905 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_GT_in_filterParamComparator5912 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_filterAtom_in_filterParamComparator5914 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_GE_in_filterParamComparator5921 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_filterAtom_in_filterParamComparator5923 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_EVENT_FILTER_RANGE_in_filterParamComparator5930 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_set_in_filterParamComparator5932 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000020000L,0x0000000000000000L,0x0007F00000000000L});
    public static final BitSet FOLLOW_constant_in_filterParamComparator5939 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000020000L,0x0000000000000000L,0x0007F00000000000L});
    public static final BitSet FOLLOW_filterIdentifier_in_filterParamComparator5942 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000020000L,0x0000000000000000L,0x0007F00000000000L});
    public static final BitSet FOLLOW_constant_in_filterParamComparator5946 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000090L});
    public static final BitSet FOLLOW_filterIdentifier_in_filterParamComparator5949 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000090L});
    public static final BitSet FOLLOW_set_in_filterParamComparator5952 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_EVENT_FILTER_NOT_RANGE_in_filterParamComparator5963 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_set_in_filterParamComparator5965 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000020000L,0x0000000000000000L,0x0007F00000000000L});
    public static final BitSet FOLLOW_constant_in_filterParamComparator5972 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000020000L,0x0000000000000000L,0x0007F00000000000L});
    public static final BitSet FOLLOW_filterIdentifier_in_filterParamComparator5975 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000020000L,0x0000000000000000L,0x0007F00000000000L});
    public static final BitSet FOLLOW_constant_in_filterParamComparator5979 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000090L});
    public static final BitSet FOLLOW_filterIdentifier_in_filterParamComparator5982 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000090L});
    public static final BitSet FOLLOW_set_in_filterParamComparator5985 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_EVENT_FILTER_IN_in_filterParamComparator5996 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_set_in_filterParamComparator5998 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000020000L,0x0000000000000000L,0x0007F00000000000L});
    public static final BitSet FOLLOW_constant_in_filterParamComparator6005 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000020000L,0x0000000000000000L,0x0007F00000000000L,0x0000000000000090L});
    public static final BitSet FOLLOW_filterIdentifier_in_filterParamComparator6008 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000020000L,0x0000000000000000L,0x0007F00000000000L,0x0000000000000090L});
    public static final BitSet FOLLOW_constant_in_filterParamComparator6012 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000020000L,0x0000000000000000L,0x0007F00000000000L,0x0000000000000090L});
    public static final BitSet FOLLOW_filterIdentifier_in_filterParamComparator6015 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000020000L,0x0000000000000000L,0x0007F00000000000L,0x0000000000000090L});
    public static final BitSet FOLLOW_set_in_filterParamComparator6019 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_EVENT_FILTER_NOT_IN_in_filterParamComparator6030 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_set_in_filterParamComparator6032 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000020000L,0x0000000000000000L,0x0007F00000000000L});
    public static final BitSet FOLLOW_constant_in_filterParamComparator6039 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000020000L,0x0000000000000000L,0x0007F00000000000L,0x0000000000000090L});
    public static final BitSet FOLLOW_filterIdentifier_in_filterParamComparator6042 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000020000L,0x0000000000000000L,0x0007F00000000000L,0x0000000000000090L});
    public static final BitSet FOLLOW_constant_in_filterParamComparator6046 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000020000L,0x0000000000000000L,0x0007F00000000000L,0x0000000000000090L});
    public static final BitSet FOLLOW_filterIdentifier_in_filterParamComparator6049 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000020000L,0x0000000000000000L,0x0007F00000000000L,0x0000000000000090L});
    public static final BitSet FOLLOW_set_in_filterParamComparator6053 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_EVENT_FILTER_BETWEEN_in_filterParamComparator6064 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_constant_in_filterParamComparator6067 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000020000L,0x0000000000000000L,0x0007F00000000000L});
    public static final BitSet FOLLOW_filterIdentifier_in_filterParamComparator6070 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000020000L,0x0000000000000000L,0x0007F00000000000L});
    public static final BitSet FOLLOW_constant_in_filterParamComparator6074 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_filterIdentifier_in_filterParamComparator6077 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_EVENT_FILTER_NOT_BETWEEN_in_filterParamComparator6085 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_constant_in_filterParamComparator6088 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000020000L,0x0000000000000000L,0x0007F00000000000L});
    public static final BitSet FOLLOW_filterIdentifier_in_filterParamComparator6091 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000020000L,0x0000000000000000L,0x0007F00000000000L});
    public static final BitSet FOLLOW_constant_in_filterParamComparator6095 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_filterIdentifier_in_filterParamComparator6098 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_constant_in_filterAtom6112 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_filterIdentifier_in_filterAtom6118 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EVENT_FILTER_IDENT_in_filterIdentifier6129 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_IDENT_in_filterIdentifier6131 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0080000000000000L});
    public static final BitSet FOLLOW_eventPropertyExpr_in_filterIdentifier6133 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_EVENT_PROP_EXPR_in_eventPropertyExpr6152 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_eventPropertyAtomic_in_eventPropertyExpr6154 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x3F00000000000000L});
    public static final BitSet FOLLOW_eventPropertyAtomic_in_eventPropertyExpr6157 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x3F00000000000000L});
    public static final BitSet FOLLOW_EVENT_PROP_SIMPLE_in_eventPropertyAtomic6176 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_IDENT_in_eventPropertyAtomic6178 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_EVENT_PROP_INDEXED_in_eventPropertyAtomic6185 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_IDENT_in_eventPropertyAtomic6187 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000800000000L});
    public static final BitSet FOLLOW_NUM_INT_in_eventPropertyAtomic6189 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_EVENT_PROP_MAPPED_in_eventPropertyAtomic6196 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_IDENT_in_eventPropertyAtomic6198 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000001800000L});
    public static final BitSet FOLLOW_set_in_eventPropertyAtomic6200 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_EVENT_PROP_DYNAMIC_SIMPLE_in_eventPropertyAtomic6213 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_IDENT_in_eventPropertyAtomic6215 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_EVENT_PROP_DYNAMIC_INDEXED_in_eventPropertyAtomic6222 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_IDENT_in_eventPropertyAtomic6224 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000800000000L});
    public static final BitSet FOLLOW_NUM_INT_in_eventPropertyAtomic6226 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_EVENT_PROP_DYNAMIC_MAPPED_in_eventPropertyAtomic6233 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_IDENT_in_eventPropertyAtomic6235 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000001800000L});
    public static final BitSet FOLLOW_set_in_eventPropertyAtomic6237 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_TIME_PERIOD_in_timePeriod6262 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_timePeriodDef_in_timePeriod6264 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_yearPart_in_timePeriodDef6280 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000000000L,0x00000000007F0000L});
    public static final BitSet FOLLOW_monthPart_in_timePeriodDef6283 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000000000L,0x00000000007E0000L});
    public static final BitSet FOLLOW_weekPart_in_timePeriodDef6288 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000000000L,0x00000000007C0000L});
    public static final BitSet FOLLOW_dayPart_in_timePeriodDef6293 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000000000L,0x0000000000780000L});
    public static final BitSet FOLLOW_hourPart_in_timePeriodDef6298 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000000000L,0x0000000000700000L});
    public static final BitSet FOLLOW_minutePart_in_timePeriodDef6303 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000000000L,0x0000000000600000L});
    public static final BitSet FOLLOW_secondPart_in_timePeriodDef6308 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_millisecondPart_in_timePeriodDef6313 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_monthPart_in_timePeriodDef6321 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000000000L,0x00000000007E0000L});
    public static final BitSet FOLLOW_weekPart_in_timePeriodDef6324 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000000000L,0x00000000007C0000L});
    public static final BitSet FOLLOW_dayPart_in_timePeriodDef6329 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000000000L,0x0000000000780000L});
    public static final BitSet FOLLOW_hourPart_in_timePeriodDef6334 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000000000L,0x0000000000700000L});
    public static final BitSet FOLLOW_minutePart_in_timePeriodDef6339 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000000000L,0x0000000000600000L});
    public static final BitSet FOLLOW_secondPart_in_timePeriodDef6344 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_millisecondPart_in_timePeriodDef6349 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_weekPart_in_timePeriodDef6357 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000000000L,0x00000000007C0000L});
    public static final BitSet FOLLOW_dayPart_in_timePeriodDef6360 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000000000L,0x0000000000780000L});
    public static final BitSet FOLLOW_hourPart_in_timePeriodDef6365 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000000000L,0x0000000000700000L});
    public static final BitSet FOLLOW_minutePart_in_timePeriodDef6370 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000000000L,0x0000000000600000L});
    public static final BitSet FOLLOW_secondPart_in_timePeriodDef6375 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_millisecondPart_in_timePeriodDef6380 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_dayPart_in_timePeriodDef6388 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000000000L,0x0000000000780000L});
    public static final BitSet FOLLOW_hourPart_in_timePeriodDef6391 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000000000L,0x0000000000700000L});
    public static final BitSet FOLLOW_minutePart_in_timePeriodDef6396 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000000000L,0x0000000000600000L});
    public static final BitSet FOLLOW_secondPart_in_timePeriodDef6401 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_millisecondPart_in_timePeriodDef6406 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_hourPart_in_timePeriodDef6413 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000000000L,0x0000000000700000L});
    public static final BitSet FOLLOW_minutePart_in_timePeriodDef6416 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000000000L,0x0000000000600000L});
    public static final BitSet FOLLOW_secondPart_in_timePeriodDef6421 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_millisecondPart_in_timePeriodDef6426 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_minutePart_in_timePeriodDef6433 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000000000L,0x0000000000600000L});
    public static final BitSet FOLLOW_secondPart_in_timePeriodDef6436 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_millisecondPart_in_timePeriodDef6441 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_secondPart_in_timePeriodDef6448 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_millisecondPart_in_timePeriodDef6451 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_millisecondPart_in_timePeriodDef6458 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_YEAR_PART_in_yearPart6472 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_valueExpr_in_yearPart6474 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_MONTH_PART_in_monthPart6489 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_valueExpr_in_monthPart6491 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_WEEK_PART_in_weekPart6506 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_valueExpr_in_weekPart6508 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_DAY_PART_in_dayPart6523 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_valueExpr_in_dayPart6525 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_HOUR_PART_in_hourPart6540 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_valueExpr_in_hourPart6542 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_MINUTE_PART_in_minutePart6557 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_valueExpr_in_minutePart6559 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_SECOND_PART_in_secondPart6574 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_valueExpr_in_secondPart6576 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_MILLISECOND_PART_in_millisecondPart6591 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_valueExpr_in_millisecondPart6593 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_SUBSTITUTION_in_substitution6608 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INT_TYPE_in_constant6624 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LONG_TYPE_in_constant6633 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FLOAT_TYPE_in_constant6642 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOUBLE_TYPE_in_constant6651 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_TYPE_in_constant6667 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BOOL_TYPE_in_constant6683 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NULL_TYPE_in_constant6696 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_number0 = new BitSet(new long[]{0x0000000000000002L});

}