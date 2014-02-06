// some constructs typically found in C++ programs written for the windows platform


int __fastcall myFastCallFunction () {
}

BOOL APIENTRY DllMain( HANDLE hModule, 
                       DWORD  ul_reason_for_call, 
                       LPVOID lpReserved
					 )
{
	// DLL entry method
}

DWORD WINAPI myThreadRun( LPVOID lpUserData )
{
	// WINAPI stuff
}
	
	
static BOOL CALLBACK EnumWindowsProc(
  HWND hwnd,      // handle to parent window
  LPARAM lParam   // application-defined value
)
{
	// callback
}
	