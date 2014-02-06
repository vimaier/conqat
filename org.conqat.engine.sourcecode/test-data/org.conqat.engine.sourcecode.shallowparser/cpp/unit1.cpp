//---------------------------------------------------------------------------
#include <vcl.h>
#pragma hdrstop

#include "Unit1.h"
#include "math.h"
//---------------------------------------------------------------------------
#pragma package(smart_init)
#pragma resource "*.dfm"
TForm1 *Form1;
//---------------------------------------------------------------------------
__fastcall TForm1::TForm1(TComponent* Owner)
    : TForm(Owner)
{
}
//---------------------------------------------------------------------------

// some helpful makros
#define GETRED(x) ( ( (x) & 0x00FF0000 ) >> 16 )
#define GETGREEN(x) ( ( (x) & 0x0000FF00 ) >> 8 )
#define GETBLUE(x) ( ( (x) & 0x000000FF ) )
#define SETRED(x) ( ( (x) &0xFF) << 16 )
#define SETGREEN(x) ( ( (x) &0xFF) << 8 )
#define SETBLUE(x) ( ( (x) &0xFF) )

#define ADDCOLOR(c,w) Count+=(w); red+=(w)*(float)GETRED(c); \
                      green+=(w)*(float)GETGREEN(c); \
                      blue+= (w)*(float)GETBLUE(c);

#define ADDCOLORONE(c) Count+=1.0; red+=GETRED(c); green+=GETGREEN(c); blue+=GETBLUE(c);

void RenderNewBitmap ( Graphics::TBitmap *src, Graphics::TBitmap *dest )
{
  float xf = (float)src->Width / (float)dest->Width;
  float yf = (float)src->Height / (float)dest->Height;
  for ( int x = 0 ; x < dest->Width ; x++ )
  for ( int y = 0 ; y < dest->Height ; y++ )
  {
    float xs = (float)x*xf;
    float ys = (float)y*yf;
    float xe = xs + xf;
    float ye = ys + yf;
    unsigned long red = 0;
    unsigned long green = 0;
    unsigned long blue = 0;
    float Count = 0;

    for ( int tx = floor(xs) ; tx <= ceil(xe) ; tx++ )
      for ( int ty = floor(ys) ; ty <= ceil(ye) ; ty++ ) {
      float wei = 1.0;
      if ( tx == floor(xs) ) wei *= (1.0 - fmod(xs, 1.0));
      else if ( tx == ceil(xe) ) wei *= fmod(xe, 1.0);
      if ( ty == floor(ys) ) wei *= (1.0 - fmod(ys, 1.0));
      else if ( ty == ceil(ye) ) wei *= fmod(ye, 1.0);
      if ( wei == 0 ) {
        int x = 2*3;
      }
      ADDCOLOR(src->Canvas->Pixels[tx][ty], wei );
    }

    if ( Count == 0.0 ) Count = 0.001;

    red = (float)red / Count;
    green = (float)green / Count;
    blue = (float)blue / Count;
    dest->Canvas->Pixels[x][y] = SETRED(red) | SETGREEN(green) | SETBLUE(blue);
  }
}

void ResamplePrec ( Graphics::TBitmap *src, Graphics::TBitmap *dest )
{
  float xf = (float)src->Width / (float)dest->Width;
  float yf = (float)src->Height / (float)dest->Height;
for ( int x = 0 ; x < dest->Width ; x++ ) {
  float xs = (float)x*xf;
  float xe = xs + xf;
  for ( int y = 0 ; y < dest->Height ; y++ )
  {
    float ys = (float)y*yf;
    float ye = ys + yf;

    unsigned long red = 0;
    unsigned long green = 0;
    unsigned long blue = 0;
    float Count = 0;

    int tx = floor(xs);
    float wei = (1.0 - fmod(xs, 1.0));
    int ty = floor(ys);
    float wei_t = wei* (1.0 - fmod(ys, 1.0));
    ADDCOLOR(src->Canvas->Pixels[tx][ty], wei_t );
    for ( int ty = ceil(ys) ; ty < ceil(ye) ; ty++ ) {
        ADDCOLORONE(src->Canvas->Pixels[tx][ty] );
    }
    ty = ceil(ye);
    wei_t = wei * fmod(ye, 1.0);
    ADDCOLOR(src->Canvas->Pixels[tx][ty], wei_t );

    for ( tx = ceil(xs) ; tx < ceil(xe) ; tx++ ) {
      ty = floor(ys);
      wei = (1.0 - fmod(ys, 1.0));
      ADDCOLOR(src->Canvas->Pixels[tx][ty], wei );

      for ( ty = ceil(ys) ; ty < ceil(ye) ; ty++ ) {
        ADDCOLORONE(src->Canvas->Pixels[tx][ty] );
      }

      ty = ceil(ye);
      wei = fmod(ye, 1.0);
      ADDCOLOR(src->Canvas->Pixels[tx][ty], wei );
    }

    tx = ceil(xe);
    wei = fmod(xe, 1.0);
    ty = floor(ys);
    wei_t = wei* (1.0 - fmod(ys, 1.0));
    ADDCOLOR(src->Canvas->Pixels[tx][ty], wei_t );
    for ( int ty = ceil(ys) ; ty < ceil(ye) ; ty++ ) {
        ADDCOLORONE(src->Canvas->Pixels[tx][ty] );
    }
    ty = ceil(ye);
    wei_t = wei * fmod(ye, 1.0);
    ADDCOLOR(src->Canvas->Pixels[tx][ty], wei_t );

    if ( Count == 0.0 ) Count = 0.001;

    red = (float)red / Count;
    green = (float)green / Count;
    blue = (float)blue / Count;
    dest->Canvas->Pixels[x][y] = SETRED(red) | SETGREEN(green) | SETBLUE(blue);
  }
}
}

void RenderNewBitmap2 ( Graphics::TBitmap *src, Graphics::TBitmap *dest )
{
  float xf = (float)src->Width / (float)dest->Width;
  float yf = (float)src->Height / (float)dest->Height;
  for ( int x = 0 ; x < dest->Width ; x++ )
  for ( int y = 0 ; y < dest->Height ; y++ )
  {
    float xs = (float)x*xf;
    float ys = (float)y*yf;
    unsigned long red = 0;
    unsigned long green = 0;
    unsigned long blue = 0;
    float Count = 0;

    for ( int tx = floor(xs) ; tx <= ceil(xs) ; tx++ )
      for ( int ty = floor(ys) ; ty <= ceil(ys) ; ty++ ) {
      float wei = 1.0;
      if ( tx == floor(xs) ) wei *= (1.0 - fmod(xs, 1.0));
      else if ( tx == ceil(xs) ) wei *= fmod(xs, 1.0);
      if ( ty == floor(ys) ) wei *= (1.0 - fmod(ys, 1.0));
      else if ( ty == ceil(ys) ) wei *= fmod(ys, 1.0);
      if ( wei == 0 ) {
        int x = 2*3;
      }
      ADDCOLOR(src->Canvas->Pixels[tx][ty], wei );
    }

    if ( Count == 0.0 ) Count = 0.001;

    red = (float)red / Count;
    green = (float)green / Count;
    blue = (float)blue / Count;
    dest->Canvas->Pixels[x][y] = SETRED(red) | SETGREEN(green) | SETBLUE(blue);
  }
}


// This function starts here
union ResNearColor {
  unsigned long c;
  struct {
    unsigned char b;
    unsigned char g;
    unsigned char r;
    unsigned char a;
  };
};

#define ADDCOLOR_RNC(col,w) rnc_t.c = (col); \
                          rnc.r += (w)*(float)(rnc_t.r); \
                          rnc.g += (w)*(float)(rnc_t.g); \
                          rnc.b += (w)*(float)(rnc_t.b);

void ResampleNear ( Graphics::TBitmap *src, Graphics::TBitmap *dest )
{
  src->PixelFormat = pf32bit;
  dest->PixelFormat = pf32bit;

  float xf = (float)src->Width / (float)dest->Width;
  float yf = (float)src->Height / (float)dest->Height;

  unsigned long *scan_fl;
  unsigned long *scan_ce;
  unsigned long *dest_line;
  ResNearColor rnc;
  ResNearColor rnc_t;
  float wei;

  for ( int y = 0 ; y < dest->Height ; y++ ) {
    float ys = (float)y*yf;
    scan_fl = (unsigned long *)(src->ScanLine[floor(ys)]);
    scan_ce = (unsigned long *)(src->ScanLine[ceil(ys)]);
    dest_line = (unsigned long *)(dest->ScanLine[y]);
    for ( int x = 0 ; x < dest->Width ; x++ )
    {
      float xs = (float)x*xf;
      rnc.c = 0;

      float fm_xs = fmod(xs, 1.0);
      float fm_ys = fmod(ys, 1.0);
      float f_pro = fm_xs*fm_ys;

      // wei = (1.0 - fmod(xs, 1.0))*(1.0 - fmod(ys, 1.0));
      wei = 1.0 + f_pro - fm_xs - fm_ys;
      ADDCOLOR_RNC(scan_fl[(int)floor(xs)], wei );
      // wei = fmod(xs, 1.0)*(1.0 - fmod(ys, 1.0));
      wei = fm_xs - f_pro;
      ADDCOLOR_RNC(scan_fl[(int)ceil(xs)], wei );
      // wei = (1.0 - fmod(xs, 1.0))*fmod(ys, 1.0);
      wei = fm_ys - f_pro;
      ADDCOLOR_RNC(scan_ce[(int)floor(xs)], wei );
      // wei = fmod(xs, 1.0)*fmod(ys, 1.0);
      wei = f_pro;
      ADDCOLOR_RNC(scan_ce[(int)ceil(xs)], wei );

      dest_line[x] = rnc.c;
    }
  }
}

#define FIXI_PREC  0x00010000
#define FIXI_MASK  0x0000ffff
#define FIXI_SHIFT 16

#define ADDCOLOR_RNCI(col,w) rnc_t.c = (col); \
                          rnc.r += (((w)*(rnc_t.r))>>FIXI_SHIFT); \
                          rnc.g += (((w)*(rnc_t.g))>>FIXI_SHIFT); \
                          rnc.b += (((w)*(rnc_t.b))>>FIXI_SHIFT);

void ResampleNear_INT ( Graphics::TBitmap *src, Graphics::TBitmap *dest )
{
  src->PixelFormat = pf32bit;
  dest->PixelFormat = pf32bit;

  unsigned long xf = (src->Width  << FIXI_SHIFT) / dest->Width;
  unsigned long yf = (src->Height << FIXI_SHIFT) / dest->Height;

  unsigned long *scan_fl;
  unsigned long *scan_ce;
  unsigned long *dest_line;
  ResNearColor rnc;
  ResNearColor rnc_t;
  unsigned long wei;

  for ( int y = 0 ; y < dest->Height ; y++ ) {
    unsigned long ys = y*yf;
    scan_fl = (unsigned long *)(src->ScanLine[(ys>>FIXI_SHIFT)]);
    scan_ce = (unsigned long *)(src->ScanLine[(ys>>FIXI_SHIFT)+1]);
    dest_line = (unsigned long *)(dest->ScanLine[y]);
    for ( int x = 0 ; x < dest->Width ; x++ )
    {
      unsigned long xs = x*xf;
      rnc.c = 0;

      unsigned long fm_xs = xs & FIXI_MASK;
      unsigned long fm_ys = ys & FIXI_MASK;
      unsigned long f_pro = (fm_xs*fm_ys) >> FIXI_SHIFT;

      wei = FIXI_PREC + f_pro - fm_xs - fm_ys;
      ADDCOLOR_RNCI(scan_fl[(xs>>FIXI_SHIFT)], wei );
      wei = fm_xs - f_pro;
      ADDCOLOR_RNCI(scan_fl[(xs>>FIXI_SHIFT)+1], wei );
      wei = fm_ys - f_pro;
      ADDCOLOR_RNCI(scan_ce[(xs>>FIXI_SHIFT)], wei );
      wei = f_pro;
      ADDCOLOR_RNCI(scan_ce[(xs>>FIXI_SHIFT)+1], wei );

      dest_line[x] = rnc.c;
    }
  }
}


// uncomment the version you prefer:
// #define SCALE_SIMPLE
// #define SCALE_RENDER1
// #define SCALE_RENDER2
#define SCALE_OPT1
// #define SCALE_OPT2
// #define SCALE_ULTRA

void SmoothScale ( Graphics::TBitmap *src, Graphics::TBitmap *dest )
{
  // Trivial case:
  if ( src->Width == dest->Width && src->Height == dest->Height )
    { dest->Canvas->Draw ( 0, 0, src ); return; }

#ifdef SCALE_SIMPLE
  dest->Canvas->CopyRect ( Rect ( 0, 0, dest->Width, dest->Height ),
    src->Canvas, Rect ( 0, 0, src->Width, src->Height ) );
  return;
#endif
#ifdef SCALE_RENDER1
  RenderNewBitmap ( src, dest ); return;
#endif
#ifdef SCALE_RENDER2
  RenderNewBitmap2 ( src, dest ); return;
#endif
#ifdef SCALE_OPT1
  ResamplePrec ( src, dest ); return;
#endif
#ifdef SCALE_OPT2
  ResampleNear ( src, dest ); return;
#endif
#ifdef SCALE_ULTRA
  ResampleNear_INT ( src, dest ); return;
#endif
}

void __fastcall TForm1::Button1Click(TObject *Sender)
{
  Graphics::TBitmap *b1 = new Graphics::TBitmap();
  Graphics::TBitmap *b2 = new Graphics::TBitmap();

  b1->LoadFromFile ( Edit1->Text );
  b2->Width = Edit3->Text.ToInt();
  b2->Height = Edit4->Text.ToInt();

  unsigned long ticks = GetTickCount();
  SmoothScale ( b1, b2 );
  ticks = GetTickCount() - ticks;

  b2->SaveToFile ( Edit2->Text );

  ShowMessage ( "Needed " + IntToStr ( ticks ) + " ticks!" );

  delete b1; delete b2;
}
//---------------------------------------------------------------------------
 