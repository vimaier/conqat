package body PKG is

    function fkt1 is
       new Foo.Bar (Some_Thing => Some_Thing_Else);


    task body Foo_Task is separate;


    task body Bar_Task is separate;

end PKG;
