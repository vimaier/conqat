*&---------------------------------------------------------------------*
*& Report  ZCQSE_PARSER_SELECT_TEST
*&
*&---------------------------------------------------------------------*
*&
*&
*&---------------------------------------------------------------------*

report  zcqse_parser_select_test.

types:
  begin of pricest,
    carrid type s_carr_id,
    sum type s_price,
    avg type s_price,
  end of pricest.

data:
      result_line type sflight,
      result_tab type table of sflight,
      sum_price type s_price,
      prices type pricest,
      prices_table type table of pricest,
      ps type i.


select sum( price ) from sflight into sum_price.
write: / sum_price.


select sum( price ) as sum avg( price ) as avg from sflight into corresponding fields of prices.
write: / prices-sum, prices-avg.

select sum( price ) as sum avg( price ) as avg from sflight into corresponding fields of prices
  group by carrid.
  write: / prices-sum, prices-avg.
endselect.

select carrid sum( price ) as sum avg( price ) as avg from sflight into corresponding fields of prices
  group by carrid.
  write: / prices-carrid, prices-sum, prices-avg.
endselect.

select carrid sum( price ) avg( price )  from sflight into table prices_table
  group by carrid.

select carrid sum( price ) as sum avg( price ) as avg from sflight into corresponding fields of table prices_table
  group by carrid.

select carrid sum( price ) as sum avg( price ) as avg from sflight into corresponding fields of table prices_table
  where carrid like 'A%'
  group by carrid.

select carrid sum( price ) as sum avg( price ) as avg from sflight into corresponding fields of table prices_table
  package size ps
  where carrid like 'A%'
  group by carrid.
  write / ps.
endselect.

select carrid sum( price ) as sum avg( price ) as avg from sflight appending corresponding fields of table prices_table
  package size ps
  where carrid like 'A%'
  group by carrid.
  write / ps.
endselect.

select * from sflight into result_line
  where carrid = 'LH'.
  write: / result_line-fldate, result_line-carrid.
endselect.

select * from sflight appending table result_tab
  where carrid = 'AA'.

select * appending table result_tab from sflight
  where carrid = 'BB'.
  
SELECT COUNT( * ) INTO lv_count
				FROM xtable
				FOR ALL ENTRIES IN it_p
				WHERE name = it_p-name.

SELECT COUNT( * ) INTO lv_count
				  FROM xtable
				  WHERE name = is_p-name.
					
SELECT COUNT(*) INTO lv_count
				FROM xtable WHERE name IS NULL.
				
				
					
					