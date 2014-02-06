package my_test is

	procedure foo (var1 string, var2 string) as
	begin
		if case var1 
			when 'a' then 1
			when 'b' then 2
			when 'c' then 3
			else 4 end
			<
		   case var2 
			when 'a' then 1
			when 'b' then 2
			when 'c' then 3
			else 4 end
		then
			null;
		else
		    var1 := var2;
		end if;	
	end;
end;
 
 