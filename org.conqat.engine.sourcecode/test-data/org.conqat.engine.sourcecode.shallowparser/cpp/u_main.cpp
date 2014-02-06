//---------------------------------------------------------------------------
#include <vcl.h>
#pragma hdrstop

#include "U_Main.h"
//---------------------------------------------------------------------------
#pragma package(smart_init)
#pragma link "cspin"
#pragma link "cspin"
#pragma resource "*.dfm"
TForm1 *Form1;
//---------------------------------------------------------------------------
__fastcall TForm1::TForm1(TComponent* Owner)
    : TForm(Owner)
{
}
//---------------------------------------------------------------------------
void __fastcall TForm1::FormShow(TObject *Sender)
{
  ComboBox1->ItemIndex = 1;    
}
//---------------------------------------------------------------------------
void __fastcall TForm1::FormCreate(TObject *Sender)
{
  SoundBuffer = new TMemoryStream();    
}
//---------------------------------------------------------------------------
void __fastcall TForm1::FormDestroy(TObject *Sender)
{
  delete SoundBuffer;    
}
//---------------------------------------------------------------------------

#define OUTPUT(x) Output->Caption = (x);

void WaveHeader ( TMemoryStream *tmp, int SamRate, int Count ) {
  char RIFF[5] = "RIFF";
  unsigned long FileLen = Count*2 + 44 -8;
  char WAVE[9] = "WAVEfmt ";
  unsigned long BlockLen = 0x10;
  unsigned short FormatType = 1;
  unsigned short NumChannels = 1;
  unsigned long SimpleSC = SamRate;
  unsigned long AllSC = SamRate * 2;
  unsigned short DataBytes = 2;
  unsigned short Res = 16;
  char Data[5] = "data";
  unsigned long AllDataBytes = Count * 2;

  tmp->Write ( RIFF, 4 );
  tmp->Write ( &FileLen, 4 );
  tmp->Write ( WAVE, 8 );
  tmp->Write ( &BlockLen, 4 );
  tmp->Write ( &FormatType, 2 );
  tmp->Write ( &NumChannels, 2 );
  tmp->Write ( &SimpleSC, 4 );
  tmp->Write ( &AllSC, 4 );
  tmp->Write ( &DataBytes, 2 );
  tmp->Write ( &Res, 2 );
  tmp->Write ( Data, 4 );
  tmp->Write ( &AllDataBytes, 4 );
}

void __fastcall TForm1::SpeedButton1Click(TObject *Sender)
{
  TMemoryStream *d = new TMemoryStream();
  FormelIntr *fi = new FormelIntr();

  long double res;
  long double max = 0;
#define VARCOUNT 3
  FormIntrVar vars[VARCOUNT] = {
    { "rand" , 0 } ,
    { "t" , 0 } ,
    { "pi" , 3.141592654 }
  };
  FormIntrError error;

  int SampRate = StrToInt ( ComboBox1->Text.SubString ( 1, 5 ) );

  randomize();

  for ( long double i = 0;
        i < CSpinEdit1->Value * SampRate; i++ ) {
    vars[0].Val =
      (long double)(rand()*rand()) / ((long double)rand()+1);
    vars[1].Val = (long double)i / (long double)SampRate;
    fi->Calc ( res, Edit1->Text.c_str(), VARCOUNT, vars, error );
    if ( error.ErrNum != 0 ) res = 0.0;
    d->Write ( &res, sizeof ( res ) );
    if ( res > max ) max = res;
    else if ( res < (-max) ) max = -res;
  }

  if ( max == 0 ) {
    delete d;
    delete fi;
    OUTPUT ( "Error" );
    return;
  }

  long double scal = (long double)30000 / max;

  delete SoundBuffer;
  SoundBuffer = new TMemoryStream();
  Chart1->Series[0]->Clear();
  Chart2->Series[0]->Clear();

  WaveHeader ( SoundBuffer, SampRate, d->Size / sizeof(res) );
  d->Position = 0;

  short signed end = -1;
  short unsigned x1, x2;
  x2 = CSpinEdit2->Value;
  x1 = 0;

  while ( d->Read ( &res, sizeof(res) ) == sizeof( res ) ) {
    end = ( res * scal );
    SoundBuffer->Write ( &end, 2 );
    if ( x1 <= 0 ) {
      Chart1->Series[0]->AddY ( end, "", clTeeColor );
      Chart2->Series[0]->AddY ( end, "", clTeeColor );
      x1 = x2;
    }
    x1--;
  }

  delete d;
  delete fi;

  OUTPUT ( "Created" );
}
//---------------------------------------------------------------------------

void __fastcall TForm1::SpeedButton5Click(TObject *Sender)
{
  PlaySound ( NULL, NULL, 0 );
  OUTPUT ( "Stopped" );    
}
//---------------------------------------------------------------------------

void __fastcall TForm1::SpeedButton3Click(TObject *Sender)
{
  SoundBuffer->Position = 0;
  if ( PlaySound ( (char *)SoundBuffer->Memory, NULL,
              SND_ASYNC | SND_MEMORY ) )
    OUTPUT ( "Playing..." );
}
//---------------------------------------------------------------------------

void __fastcall TForm1::SpeedButton4Click(TObject *Sender)
{
  SoundBuffer->Position = 0;
  if ( PlaySound ( (char *)SoundBuffer->Memory, NULL,
              SND_ASYNC | SND_MEMORY | SND_LOOP ) )
    OUTPUT ( "Looping..." );
}
//---------------------------------------------------------------------------

void __fastcall TForm1::SpeedButton2Click(TObject *Sender)
{
  if ( SaveDialog1->Execute() )
    SoundBuffer->SaveToFile ( SaveDialog1->FileName );
}
//---------------------------------------------------------------------------

