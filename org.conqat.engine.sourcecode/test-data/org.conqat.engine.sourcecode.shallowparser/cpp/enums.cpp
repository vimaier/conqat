
enum Farbe {ROT, GELB, GRUEN, BLAU};

enum Farbe2 {ROT=2, GELB, GRUEN=7, BLAU};

// anonymous enum
enum  {ROT=2, GELB, GRUEN=7};

typedef const struct {
uint8 i;
} MyCoolStruct;

typedef enum  {ROT=2, GELB, GRUEN=7} MyColors;

enum E : unsigned long
{
    A = 1,
    B = 2,
    C = 3,
};

struct s
{
    int id;
    LPCTSTR err;
} errors[] = {
    { A, _T( "foo" ) },
    { B, _T( "bar" ) },
    { C, _T( "ooh" ) }
};



