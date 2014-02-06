package body PKG is

	procedure a () is
	begin
		for Character'('0') .. Character'('9') loop
			x ();
		end loop;
    end a;

    procedure b (a: int; b: int) is
    begin
		y ();
    end b;
end PKG;
