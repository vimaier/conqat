begin
	foo ();
exception when others then
    bar ();
end;

-- also deal with exit

exit 0
